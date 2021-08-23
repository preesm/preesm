package org.preesm.algorithm.schedule.fpga;

import java.io.File;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.CHeaderUsedLocator;
import org.preesm.model.scenario.Scenario;

/**
 * This class generates code for Xilinx FPGA with OpenCL HLS flow.
 * 
 * @author ahonorat
 */
public class FpgaCodeGenerator {

  public static final String TEMPLATE_DEFINE_HEADER_NAME = "PreesmAutoDefinedSizes.h";

  public static final String TEMPLATE_HOST_RES_LOCATION =

      "templates/xilinxCodegen/template_host_fpga.cpp";

  public static final String TEMPLATE_TOP_KERNEL_RES_LOCATION =

      "templates/xilinxCodegen/template_top_kernel_fpga.cpp";

  public static final String TEMPLATE_READ_KERNEL_RES_LOCATION =

      "templates/xilinxCodegen/template_read_kernel_fpga.cpp";

  public static final String TEMPLATE_WRITE_KERNEL_RES_LOCATION =

      "templates/xilinxCodegen/template_write_kernel_fpga.cpp";

  public static final String KERNEL_NAME_READ  = "mem_read";
  public static final String KERNEL_NAME_WRITE = "mem_write";

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

    // TODO
    // // 2.1- generate vectors for interfaces
    // final StringBuilder interfaceBuffers = new StringBuilder();
    // interfaceRates.forEach((i, p) -> {
    // });

    // 3- init template reader
    final InputStreamReader reader = PreesmIOHelper.getInstance().getFileReader(TEMPLATE_HOST_RES_LOCATION,
        this.getClass());

    // 4- init output writer
    final StringWriter writer = new StringWriter();

    engine.evaluate(context, writer, "org.apache.velocity", reader);

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

    // TODO

    // 3- init template reader
    final InputStreamReader reader = PreesmIOHelper.getInstance().getFileReader(TEMPLATE_TOP_KERNEL_RES_LOCATION,
        this.getClass());

    // 4- init output writer
    final StringWriter writer = new StringWriter();

    engine.evaluate(context, writer, "org.apache.velocity", reader);

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
        args.add(f.getType() + "* " + ia.getName() + "_mem");
        args.add("hls::stream<" + f.getType() + ">" + " &" + ia.getName() + "_stream");
      }
    }
    sb.append(args.stream().collect(Collectors.joining(",\n  ")));
    sb.append(") {\n");
    // read kernel body
    for (final Entry<InterfaceActor, Pair<Long, Long>> e : interfaceRates.entrySet()) {
      final InterfaceActor ia = e.getKey();
      if (ia instanceof DataInputInterface) {
        final Fifo f = ia.getDataPort().getFifo();
        sb.append("    readInput<" + f.getType() + ">(" + ia.getName() + "_mem, " + ia.getName() + "_stream, "
            + getInterfaceRateName(ia) + ", " + getInterfaceFactorName(ia) + ");\n");
      }
    }
    sb.append("}\n");

    context.put("PREESM_READ_KERNEL", sb.toString());

    // 3- init template reader
    final InputStreamReader reader = PreesmIOHelper.getInstance().getFileReader(TEMPLATE_READ_KERNEL_RES_LOCATION,
        this.getClass());

    // 4- init output writer
    final StringWriter writer = new StringWriter();

    engine.evaluate(context, writer, "org.apache.velocity", reader);

    return writer.toString();
  }

  protected String writeWriteKernelFile() {
    // 1- init engine
    final VelocityEngine engine = new VelocityEngine();
    engine.init();

    // 2- init context
    final VelocityContext context = new VelocityContext();
    context.put("PREESM_INCLUDES", "#include \"" + TEMPLATE_DEFINE_HEADER_NAME + "\"\n");

    // read kernel prototype
    final StringBuilder sb = new StringBuilder("void mem_write(\n  ");
    final List<String> args = new ArrayList<>();
    for (final InterfaceActor ia : interfaceRates.keySet()) {
      if (ia instanceof DataOutputInterface) {
        final Fifo f = ia.getDataPort().getFifo();
        args.add(f.getType() + "* " + ia.getName() + "_mem");
        args.add("hls::stream<" + f.getType() + ">" + " &" + ia.getName() + "_stream");
      }
    }
    sb.append(args.stream().collect(Collectors.joining(",\n  ")));
    sb.append(") {\n");
    // write kernel body
    for (final Entry<InterfaceActor, Pair<Long, Long>> e : interfaceRates.entrySet()) {
      final InterfaceActor ia = e.getKey();
      if (ia instanceof DataOutputInterface) {
        final Fifo f = ia.getDataPort().getFifo();
        sb.append("    writeOutput<" + f.getType() + ">(" + ia.getName() + "_mem, " + ia.getName() + "_stream, "
            + getInterfaceRateName(ia) + ", " + getInterfaceFactorName(ia) + ");\n");
      }
    }
    sb.append("}\n");

    context.put("PREESM_WRITE_KERNEL", sb.toString());

    // 3- init template reader
    final InputStreamReader reader = PreesmIOHelper.getInstance().getFileReader(TEMPLATE_WRITE_KERNEL_RES_LOCATION,
        this.getClass());

    // 4- init output writer
    final StringWriter writer = new StringWriter();

    engine.evaluate(context, writer, "org.apache.velocity", reader);

    return writer.toString();
  }

  private String writeConnectivityFile() {
    final StringBuilder sb = new StringBuilder("[connectivity]\n");
    for (final InterfaceActor ia : interfaceRates.keySet()) {
      sb.append("stream_connect=");
      if (ia instanceof DataInputInterface) {
        sb.append(KERNEL_NAME_READ + "_1.");
        sb.append(ia.getName() + "_stream:");
        sb.append(graphName + "_1.");
        sb.append(ia.getName() + "_stream\n");
      } else if (ia instanceof DataOutputInterface) {
        sb.append(graphName + "_1.");
        sb.append(ia.getName() + "_stream:");
        sb.append(KERNEL_NAME_WRITE + "_1.");
        sb.append(ia.getName() + "_stream\n");
      }
    }
    return sb.toString();
  }

  public static String getFifoDataSizeName(final Fifo fifo) {
    return "DEPTH_OF_" + fifo.getId().replace('.', '_').replace("-", "__");
  }

  public static String getInterfaceRateName(final InterfaceActor ia) {
    return "RATE_OF_" + ia.getName();
  }

  public static String getInterfaceFactorName(final InterfaceActor ia) {
    return "FACTOR_OF_" + ia.getName();
  }

}
