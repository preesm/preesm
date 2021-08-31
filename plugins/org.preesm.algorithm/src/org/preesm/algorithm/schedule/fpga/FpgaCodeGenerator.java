package org.preesm.algorithm.schedule.fpga;

import java.io.File;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.UserSpecialActor;
import org.preesm.model.pisdf.util.CHeaderUsedLocator;
import org.preesm.model.scenario.Scenario;

/**
 * This class generates code for Xilinx FPGA with OpenCL HLS flow.
 * 
 * @author ahonorat
 */
public class FpgaCodeGenerator {

  public static final String VELOCITY_PACKAGE_NAME = "org.apache.velocity";

  public static final String TEMPLATE_DEFINE_HEADER_NAME = "PreesmAutoDefinedSizes.h";

  public static final String TEMPLATE_HOST_RES_LOCATION =

      "templates/xilinxCodegen/template_host_fpga.cpp";

  public static final String TEMPLATE_TOP_KERNEL_RES_LOCATION =

      "templates/xilinxCodegen/template_top_kernel_fpga.cpp";

  public static final String TEMPLATE_READ_KERNEL_RES_LOCATION =

      "templates/xilinxCodegen/template_read_kernel_fpga.cpp";

  public static final String TEMPLATE_WRITE_KERNEL_RES_LOCATION =

      "templates/xilinxCodegen/template_write_kernel_fpga.cpp";

  public static final String KERNEL_NAME_READ        = "mem_read";
  public static final String KERNEL_NAME_WRITE       = "mem_write";
  public static final String SUFFIX_INTERFACE_ARRAY  = "_mem";     // suffix name for read/write kernels
  public static final String SUFFIX_INTERFACE_STREAM = "_stream";  // suffix name for other kernels
  public static final String SUFFIX_INTERFACE_VECTOR = "_vect";    // suffix name for host vector
  public static final String SUFFIX_INTERFACE_BUFFER = "_buff";    // suffix name for host buffer

  private final Scenario                              scenario;
  private final PiGraph                               graph;
  private final String                                graphName;
  private final Map<InterfaceActor, Pair<Long, Long>> interfaceRates;
  private final Map<Fifo, Long>                       allFifoSizes;

  private FpgaCodeGenerator(final Scenario scenario, final PiGraph graph,
      final Map<InterfaceActor, Pair<Long, Long>> interfaceRates, final Map<Fifo, Long> allFifoSizes) {
    this.scenario = scenario;
    this.graph = graph;
    this.graphName = scenario.getAlgorithm().getName();
    this.interfaceRates = interfaceRates;
    this.allFifoSizes = allFifoSizes;

    // check broadcast being more than broadcasts ...
    if (!checkPureBroadcasts(graph) || !checkOtherSpecialActorAbsence(graph)) {
      throw new PreesmRuntimeException("The codegen does not support special actors (fork, join, roundbuffer) yet, "
          + "but only broadcasts having all equal port rates.");
    }

  }

  private static boolean checkPureBroadcasts(final PiGraph flatGraph) {
    boolean valid = true;
    for (final AbstractActor aa : flatGraph.getActors()) {
      if (aa instanceof BroadcastActor) {
        final long inputRate = aa.getDataInputPorts().get(0).getExpression().evaluate();
        for (final DataPort dop : aa.getDataOutputPorts()) {
          final long outputRate = dop.getExpression().evaluate();
          if (outputRate != inputRate) {
            PreesmLogger.getLogger()
                .warning(() -> String.format("Broadcast output port [%s:%s] has different rate than its input.",
                    PreesmCopyTracker.getOriginalSource(aa).getVertexPath(), dop.getName()));
            valid = false;
          }
        }
      }
    }
    return valid;
  }

  private static boolean checkOtherSpecialActorAbsence(final PiGraph flatGraph) {
    boolean valid = true;
    for (final AbstractActor aa : flatGraph.getActors()) {
      if (aa instanceof UserSpecialActor && !(aa instanceof BroadcastActor)) {
        valid = false;
      }
    }
    return valid;
  }

  /**
   * Perform the codegen.
   * 
   * @param scenario
   *          Scenario with codegen path.
   * @param graph
   *          Flat graph of the app.
   * @param interfaceRates
   *          Interface rates of the flat graph.
   * @param allFifoSizes
   *          All sizes of the fifo.
   */
  public static void generateFiles(final Scenario scenario, final PiGraph graph,
      Map<InterfaceActor, Pair<Long, Long>> interfaceRates, Map<Fifo, Long> allFifoSizes) {
    final FpgaCodeGenerator fcg = new FpgaCodeGenerator(scenario, graph, interfaceRates, allFifoSizes);

    // 0- without the following class loader initialization, I get the following exception when running as Eclipse
    // plugin:
    // org.apache.velocity.exception.VelocityException: The specified class for ResourceManager
    // (org.apache.velocity.runtime.resource.ResourceManagerImpl) does not implement
    // org.apache.velocity.runtime.resource.ResourceManager; Velocity is not initialized correctly.
    final ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(fcg.getClass().getClassLoader());

    final String headerFileContent = fcg.writeDefineHeaderFile();
    final String hostFileContent = fcg.writeHostFile();
    final String topKernelFileContent = fcg.writeTopKernelFile();
    final String readKernelFileContent = fcg.writeReadKernelFile();
    final String writeKernelFileContent = fcg.writeWriteKernelFile();
    final String connectivityFileContent = fcg.writeConnectivityFile();

    // 99- set back default class loader
    Thread.currentThread().setContextClassLoader(oldContextClassLoader);

    final String codegenPath = scenario.getCodegenDirectory() + File.separator;

    PreesmIOHelper.getInstance().print(codegenPath, TEMPLATE_DEFINE_HEADER_NAME, headerFileContent);
    PreesmIOHelper.getInstance().print(codegenPath, "host_" + fcg.graphName + ".cpp", hostFileContent);
    PreesmIOHelper.getInstance().print(codegenPath, "top_kernel_" + fcg.graphName + ".cpp", topKernelFileContent);
    PreesmIOHelper.getInstance().print(codegenPath, "read_kernel_" + fcg.graphName + ".cpp", readKernelFileContent);
    PreesmIOHelper.getInstance().print(codegenPath, "write_kernel_" + fcg.graphName + ".cpp", writeKernelFileContent);
    PreesmIOHelper.getInstance().print(codegenPath, "connectivity_" + fcg.graphName + ".cfg", connectivityFileContent);

  }

  protected String writeDefineHeaderFile() {

    final StringBuilder sb = new StringBuilder("// fifo sizes computed by PREESM\n");
    allFifoSizes.forEach((fifo, size) -> {
      sb.append(String.format("#define %s %d\n", getFifoDataSizeName(fifo), size));
    });
    sb.append("// interface sizes computed by PREESM\n");
    interfaceRates.forEach((ia, p) -> {
      final long rate = p.getKey();
      final long factor = p.getValue();
      sb.append(String.format("#define %s %d\n", getInterfaceRateName(ia), rate));
      sb.append(String.format("#define %s %d\n", getInterfaceFactorName(ia), factor));
    });

    return sb.toString();
  }

  protected String writeHostFile() {
    // 1- init engine
    final VelocityEngine engine = new VelocityEngine();
    engine.init();

    // 2- init context
    final VelocityContext context = new VelocityContext();
    final List<String> findAllCHeaderFileNamesUsed = CHeaderUsedLocator.findAllCHeaderFileNamesUsed(graph);

    context.put("PREESM_INCLUDES", "#include \"" + TEMPLATE_DEFINE_HEADER_NAME + "\"\n");

    // 2.1- generate vectors for interfaces
    final StringBuilder interfaceVectors = new StringBuilder("// vectors containing interface elements\n");
    interfaceRates.forEach((i, p) -> {
      final String type = i.getDataPort().getFifo().getType();
      interfaceVectors.append("  std::vector<" + type + ", aligned_allocator<" + type + ">> ");
      interfaceVectors.append(i.getName() + SUFFIX_INTERFACE_VECTOR + "(");
      interfaceVectors.append(getInterfaceFactorName(i) + "*" + getInterfaceRateName(i) + ");\n");
    });
    context.put("INTERFACE_VECTORS", interfaceVectors.toString());

    // 2.2- generate buffers for interfaces
    final StringBuilder interfaceBuffers = new StringBuilder("// buffers referencing interface elements\n");
    interfaceRates.forEach((i, p) -> {
      String bufferDecl = "cl::Buffer " + i.getName() + SUFFIX_INTERFACE_BUFFER + "(context, CL_MEM_USE_HOST_PTR";
      if (i instanceof DataInputInterface) {
        bufferDecl += " | CL_MEM_READ_ONLY";
      } else if (i instanceof DataOutputInterface) {
        bufferDecl += " | CL_MEM_WRITE_ONLY";
      }
      final String type = i.getDataPort().getFifo().getType();
      bufferDecl += ", sizeof(" + type + ")*" + getInterfaceFactorName(i) + "*" + getInterfaceRateName(i);
      bufferDecl += ", " + i.getName() + SUFFIX_INTERFACE_VECTOR + ".data(), &err)";
      interfaceBuffers.append("  " + surroundWithOCLcheck(bufferDecl) + "\n");
    });
    context.put("INTERFACE_BUFFERS", interfaceBuffers.toString());

    // 2.3- set kernel args
    final StringBuilder kernelLaunch = new StringBuilder("// set kernel arguments\n");
    int indexArg = 0;
    for (final InterfaceActor ia : interfaceRates.keySet()) {
      if (ia instanceof DataInputInterface) {
        String kernelArg = "err = krnl_mem_read.setArg(" + Integer.toString(indexArg) + ", " + ia.getName()
            + SUFFIX_INTERFACE_BUFFER + ")";
        kernelLaunch.append("  " + surroundWithOCLcheck(kernelArg) + "\n");
        indexArg += 2;
      }
    }
    indexArg = 0;
    for (final InterfaceActor ia : interfaceRates.keySet()) {
      if (ia instanceof DataOutputInterface) {
        String kernelArg = "err = krnl_mem_write.setArg(" + Integer.toString(indexArg) + ", " + ia.getName()
            + SUFFIX_INTERFACE_BUFFER + ")";
        kernelLaunch.append("  " + surroundWithOCLcheck(kernelArg) + "\n");
        indexArg += 2;
      }
    }

    // 2.4- launch kernels
    kernelLaunch.append("// launch the OpenCL tasks\n");
    kernelLaunch.append("  std::cout << \"Copying data...\" << std::endl;\n");
    final List<String> bufferArgs = new ArrayList<>();
    for (final InterfaceActor ia : interfaceRates.keySet()) {
      if (ia instanceof DataInputInterface) {
        bufferArgs.add(ia.getName() + SUFFIX_INTERFACE_BUFFER);
      }
    }
    final String migrateIn = "err = q.enqueueMigrateMemObjects({"
        + bufferArgs.stream().collect(Collectors.joining(", ")) + "}, 0)";
    kernelLaunch.append("  " + surroundWithOCLcheck(migrateIn) + "\n");
    kernelLaunch.append("  " + printFinishQueue() + "\n");

    kernelLaunch.append("  std::cout << \"Launching kernels...\" << std::endl;\n");
    kernelLaunch.append("  " + surroundWithOCLcheck("err = q.enqueueTask(krnl_mem_read)") + "\n");
    kernelLaunch.append("  " + surroundWithOCLcheck("err = q.enqueueTask(krnl_mem_write)") + "\n");
    kernelLaunch.append("  " + printFinishQueue() + "\n");

    kernelLaunch.append("  std::cout << \"Getting results...\" << std::endl;\n");
    bufferArgs.clear();
    for (final InterfaceActor ia : interfaceRates.keySet()) {
      if (ia instanceof DataOutputInterface) {
        bufferArgs.add(ia.getName() + SUFFIX_INTERFACE_BUFFER);
      }
    }
    final String migrateOut = "err = q.enqueueMigrateMemObjects({"
        + bufferArgs.stream().collect(Collectors.joining(", ")) + "}, CL_MIGRATE_MEM_OBJECT_HOST)";
    kernelLaunch.append("  " + surroundWithOCLcheck(migrateOut) + "\n");
    kernelLaunch.append("  " + printFinishQueue() + "\n");

    context.put("KERNEL_LAUNCH", kernelLaunch.toString());

    // 3- init template reader
    final InputStreamReader reader = PreesmIOHelper.getInstance().getFileReader(TEMPLATE_HOST_RES_LOCATION,
        this.getClass());

    // 4- init output writer
    final StringWriter writer = new StringWriter();

    engine.evaluate(context, writer, VELOCITY_PACKAGE_NAME, reader);

    return writer.toString();
  }

  protected String writeTopKernelFile() {
    // 1- init engine
    final VelocityEngine engine = new VelocityEngine();
    engine.init();

    // 2- init context
    final VelocityContext context = new VelocityContext();
    final List<String> findAllCHeaderFileNamesUsed = CHeaderUsedLocator.findAllCHeaderFileNamesUsed(graph);

    context.put("PREESM_INCLUDES", "#include \"" + TEMPLATE_DEFINE_HEADER_NAME + "\"\n");

    context.put("USER_INCLUDES",
        findAllCHeaderFileNamesUsed.stream().map(x -> "#include \"" + x + "\"").collect(Collectors.joining("\n")));

    final Map<AbstractActor, String> actorsCalls = new LinkedHashMap<>();
    final StringBuilder defs = new StringBuilder();
    // 2.1- first we add the definitions of special actors
    defs.append(FpgaSpecialActorsCodeGenerator.generateSpecialActorDefinitions(graph, actorsCalls));
    context.put("PREESM_SPECIAL_ACTORS", defs.toString());
    // 2.2- we add all other calls to the map
    // TODO
    // 2.3- we wrap the actor calls producing delays
    final StringBuilder wrapperDelays = new StringBuilder();
    // TODO
    context.put("PREESM_DELAY_WRAPPERS", wrapperDelays.toString());
    // 2.4- we wrap the actor init calls
    final StringBuilder wrapperInits = new StringBuilder();
    // TODO
    context.put("PREESM_INIT_WRAPPER", wrapperInits.toString());
    // 2.5- we generate the top kernel function with all calls in the start time order
    final StringBuilder topK = new StringBuilder();
    // TODO
    context.put("PREESM_TOP_KERNEL", topK.toString());

    // 3- init template reader
    final InputStreamReader reader = PreesmIOHelper.getInstance().getFileReader(TEMPLATE_TOP_KERNEL_RES_LOCATION,
        this.getClass());

    // 4- init output writer
    final StringWriter writer = new StringWriter();

    engine.evaluate(context, writer, VELOCITY_PACKAGE_NAME, reader);

    return writer.toString();
  }

  protected String writeReadKernelFile() {
    // 1- init engine
    final VelocityEngine engine = new VelocityEngine();
    engine.init();

    // 2- init context
    final VelocityContext context = new VelocityContext();
    context.put("PREESM_INCLUDES", "#include \"" + TEMPLATE_DEFINE_HEADER_NAME + "\"\n");

    // read kernel prototype
    final StringBuilder sb = new StringBuilder("void mem_read(\n  ");
    final List<String> args = new ArrayList<>();
    for (final InterfaceActor ia : interfaceRates.keySet()) {
      if (ia instanceof DataInputInterface) {
        final Fifo f = ia.getDataPort().getFifo();
        args.add(f.getType() + "* " + ia.getName() + SUFFIX_INTERFACE_ARRAY);
        args.add("hls::stream<" + f.getType() + ">" + " &" + ia.getName() + SUFFIX_INTERFACE_STREAM);
      }
    }
    sb.append(args.stream().collect(Collectors.joining(",\n  ")));
    sb.append(") {\n");
    // read kernel body
    for (final Entry<InterfaceActor, Pair<Long, Long>> e : interfaceRates.entrySet()) {
      final InterfaceActor ia = e.getKey();
      if (ia instanceof DataInputInterface) {
        final Fifo f = ia.getDataPort().getFifo();
        sb.append("    readInput<" + f.getType() + ">(" + ia.getName() + SUFFIX_INTERFACE_ARRAY + ", " + ia.getName()
            + SUFFIX_INTERFACE_STREAM + ", " + getInterfaceRateName(ia) + ", " + getInterfaceFactorName(ia) + ");\n");
      }
    }
    sb.append("}\n");

    context.put("PREESM_READ_KERNEL", sb.toString());

    // 3- init template reader
    final InputStreamReader reader = PreesmIOHelper.getInstance().getFileReader(TEMPLATE_READ_KERNEL_RES_LOCATION,
        this.getClass());

    // 4- init output writer
    final StringWriter writer = new StringWriter();

    engine.evaluate(context, writer, VELOCITY_PACKAGE_NAME, reader);

    return writer.toString();
  }

  protected String writeWriteKernelFile() {
    // 1- init engine
    final VelocityEngine engine = new VelocityEngine();
    engine.init();

    // 2- init context
    final VelocityContext context = new VelocityContext();
    context.put("PREESM_INCLUDES", "#include \"" + TEMPLATE_DEFINE_HEADER_NAME + "\"\n");

    // write kernel prototype
    final StringBuilder sb = new StringBuilder("void mem_write(\n  ");
    final List<String> args = new ArrayList<>();
    for (final InterfaceActor ia : interfaceRates.keySet()) {
      if (ia instanceof DataOutputInterface) {
        final Fifo f = ia.getDataPort().getFifo();
        args.add(f.getType() + "* " + ia.getName() + SUFFIX_INTERFACE_ARRAY);
        args.add("hls::stream<" + f.getType() + ">" + " &" + ia.getName() + SUFFIX_INTERFACE_STREAM);
      }
    }
    sb.append(args.stream().collect(Collectors.joining(",\n  ")));
    sb.append(") {\n");
    // write kernel body
    for (final Entry<InterfaceActor, Pair<Long, Long>> e : interfaceRates.entrySet()) {
      final InterfaceActor ia = e.getKey();
      if (ia instanceof DataOutputInterface) {
        final Fifo f = ia.getDataPort().getFifo();
        sb.append("    writeOutput<" + f.getType() + ">(" + ia.getName() + SUFFIX_INTERFACE_ARRAY + ", " + ia.getName()
            + SUFFIX_INTERFACE_STREAM + ", " + getInterfaceRateName(ia) + ", " + getInterfaceFactorName(ia) + ");\n");
      }
    }
    sb.append("}\n");

    context.put("PREESM_WRITE_KERNEL", sb.toString());

    // 3- init template reader
    final InputStreamReader reader = PreesmIOHelper.getInstance().getFileReader(TEMPLATE_WRITE_KERNEL_RES_LOCATION,
        this.getClass());

    // 4- init output writer
    final StringWriter writer = new StringWriter();

    engine.evaluate(context, writer, VELOCITY_PACKAGE_NAME, reader);

    return writer.toString();
  }

  protected String writeConnectivityFile() {
    final StringBuilder sb = new StringBuilder("[connectivity]\n");
    for (final InterfaceActor ia : interfaceRates.keySet()) {
      sb.append("stream_connect=");
      if (ia instanceof DataInputInterface) {
        sb.append(KERNEL_NAME_READ + "_1.");
        sb.append(ia.getName() + SUFFIX_INTERFACE_STREAM + ":");
        sb.append("top_graph_1.");
        sb.append(ia.getName() + SUFFIX_INTERFACE_STREAM + "\n");
      } else if (ia instanceof DataOutputInterface) {
        sb.append("top_graph_1.");
        sb.append(ia.getName() + SUFFIX_INTERFACE_STREAM + ":");
        sb.append(KERNEL_NAME_WRITE + "_1.");
        sb.append(ia.getName() + SUFFIX_INTERFACE_STREAM + "\n");
      }
    }
    return sb.toString();
  }

  protected static final String printFinishQueue() {
    return surroundWithOCLcheck("err = q.finish()");
  }

  protected static final String surroundWithOCLcheck(final String OCLcall) {
    return "OCL_CHECK(err, " + OCLcall + ");";
  }

  public static final String getFifoDataSizeName(final Fifo fifo) {
    return "DEPTH_OF_" + fifo.getId().replace('.', '_').replace("-", "__");
  }

  public static final String getInterfaceRateName(final InterfaceActor ia) {
    return "RATE_OF_" + ia.getName();
  }

  public static final String getInterfaceFactorName(final InterfaceActor ia) {
    return "FACTOR_OF_" + ia.getName();
  }

  public static final String getFifoName(final Fifo fifo) {
    return fifo.getId();
  }

}
