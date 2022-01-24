package org.preesm.algorithm.schedule.fpga;

import java.io.IOException;
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
import org.preesm.commons.files.PreesmResourcesHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.UserSpecialActor;
import org.preesm.model.pisdf.check.RefinementChecker;
import org.preesm.model.pisdf.util.CHeaderUsedLocator;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.FPGA;

/**
 * This class generates code for Xilinx FPGA with OpenCL HLS flow.
 * 
 * @author ahonorat
 */
public class FpgaCodeGenerator {

  public static final String VELOCITY_PACKAGE_NAME = "org.apache.velocity";

  public static final String TEMPLATE_DEFINE_HEADER_NAME = "PreesmAutoDefinedSizes.h";

  public static final String TEMPLATE_MAKEFILE_RES_LOCATION =

      "templates/xilinxCodegen/template_Makefile";

  public static final String TEMPLATE_SCRIPT_VIVADO_RES_LOCATION =

      "templates/xilinxCodegen/template_script_vivado.tcl";

  public static final String TEMPLATE_PYNQ_HOST_NOTEBOOK_RES_LOCATION =

      "templates/xilinxCodegen/template_PYNQ_host_notebook.ipynb";

  public static final String TEMPLATE_PYNQ_HOST_RES_LOCATION =

      "templates/xilinxCodegen/template_PYNQ_host_fpga.py";

  public static final String TEMPLATE_OPENCL_HOST_RES_LOCATION =

      "templates/xilinxCodegen/template_OpenCL_host_fpga.cpp";

  public static final String TEMPLATE_C_HOST_RES_LOCATION =

      "templates/xilinxCodegen/template_C_host_fpga.c";

  public static final String TEMPLATE_TOP_KERNEL_RES_LOCATION =

      "templates/xilinxCodegen/template_top_kernel_fpga.cpp";

  public static final String TEMPLATE_READ_KERNEL_RES_LOCATION =

      "templates/xilinxCodegen/template_read_kernel_fpga.cpp";

  public static final String TEMPLATE_WRITE_KERNEL_RES_LOCATION =

      "templates/xilinxCodegen/template_write_kernel_fpga.cpp";

  public static final String TEMPLATE_READ_KERNEL_MULTI_RES_LOCATION =

      "templates/xilinxCodegen/template_read_kernel_fpga_multi_interfaces.cpp";

  public static final String TEMPLATE_WRITE_KERNEL_MULTI_RES_LOCATION =

      "templates/xilinxCodegen/template_write_kernel_fpga_multi_interfaces.cpp";

  public static final String TEMPLATE_SCRIPT_PYNQ_HLS =

      "templates/xilinxCodegen/template_script_hls.tcl";

  public static final String STDFILE_SCRIPT_SUBDIR = "scripts/";
  // xcl2 files have been written by Xilinx under Apache 2.0 license
  // see https://github.com/Xilinx/Vitis_Accel_Examples/tree/master/common/includes/xcl2
  // see also https://github.com/Xilinx/Vitis_Accel_Examples/issues/46
  public static final String STDFILE_LIB_XOCL_SUBDIR = "libs/common/includes/xcl2/";
  public static final String STDFILE_LIB_XOCL_CPP    = "stdfiles/xilinxCodegen/xcl2.cpp";
  public static final String STDFILE_LIB_XOCL_HPP    = "stdfiles/xilinxCodegen/xcl2.hpp";
  public static final String SCRIPT_VIVADO_TCL       = "stdfiles/xilinxCodegen/script_vivado_xsa.tcl";

  public static final String NAME_WRAPPER_INITPROTO   = "preesmInitWrapper";
  public static final String PREFIX_WRAPPER_DELAYPROD = "wrapperProdDelay_";
  public static final String SUFFIX_INTERFACE_ARRAY   = "_mem";             // suffix name for read/write kernels
  public static final String SUFFIX_INTERFACE_STREAM  = "_stream";          // suffix name for other kernels
  public static final String SUFFIX_INTERFACE_VECTOR  = "_vect";            // suffix name for host vector
  public static final String SUFFIX_INTERFACE_BUFFER  = "_buff";            // suffix name for host buffer

  protected static final String PRAGMA_AXILITE_CTRL  = "#pragma HLS INTERFACE s_axilite port=return\n";
  protected static final int    PYNQ_INTERFACE_DEPTH = 64;

  private final FPGA                                  fpga;
  private final PiGraph                               graph;
  private final String                                graphName;
  private final Map<InterfaceActor, Pair<Long, Long>> interfaceRates;
  private final Map<Fifo, Long>                       allFifoDepths;

  private FpgaCodeGenerator(final Scenario scenario, final FPGA fpga, final PiGraph graph,
      final Map<InterfaceActor, Pair<Long, Long>> interfaceRates, final Map<Fifo, Long> allFifoSizes) {
    this.fpga = fpga;
    this.graph = graph;
    this.graphName = scenario.getAlgorithm().getName();
    this.interfaceRates = interfaceRates;
    this.allFifoDepths = new LinkedHashMap<>();

    // the fifo sizes are given in bits while we want the depth in number of elements
    allFifoSizes.forEach((k, v) -> {
      final long dataTypeSize = scenario.getSimulationInfo().getDataTypeSizeInBit(k.getType());
      final long depth = (v / dataTypeSize);
      // if a fifo depth is less than 2, we promote it to 2
      if (depth < 2L) {
        PreesmLogger.getLogger().info(() -> "Fifo " + k.getId() + " had depth " + depth + ", increasing it to 2.");
        allFifoDepths.put(k, 2L);
      } else {
        allFifoDepths.put(k, depth);
      }
    });

    // check that all actors have refinements
    final List<Actor> actorsWithoutCrefinement = graph.getActorsWithRefinement().stream()
        .filter(x -> !(x.getRefinement() instanceof CHeaderRefinement)).collect(Collectors.toList());
    if (!actorsWithoutCrefinement.isEmpty()) {
      throw new PreesmRuntimeException("Cannot perform codegen since some actors do not have refinements:\n"
          + actorsWithoutCrefinement.stream().map(Actor::getVertexPath).collect(Collectors.joining("\n")));
    }

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
   * @param fpga
   *          FPGA targeted by codegen.
   * @param graph
   *          Flat graph of the app.
   * @param interfaceRates
   *          Interface rates of the flat graph.
   * @param allFifoSizes
   *          All sizes of the fifo.
   */
  public static void generateFiles(final Scenario scenario, final FPGA fpga, final PiGraph graph,
      Map<InterfaceActor, Pair<Long, Long>> interfaceRates, Map<Fifo, Long> allFifoSizes) {
    final FpgaCodeGenerator fcg = new FpgaCodeGenerator(scenario, fpga, graph, interfaceRates, allFifoSizes);

    // 0- without the following class loader initialization, I get the following exception when running as Eclipse
    // plugin:
    // org.apache.velocity.exception.VelocityException: The specified class for ResourceManager
    // (org.apache.velocity.runtime.resource.ResourceManagerImpl) does not implement
    // org.apache.velocity.runtime.resource.ResourceManager; Velocity is not initialized correctly.
    final ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(fcg.getClass().getClassLoader());

    final String headerFileContent = fcg.writeDefineHeaderFile();
    final String topKernelFileContent = fcg.writeTopKernelFile();
    final String readKernelFileContent = fcg.writeReadKernelFile();
    final String writeKernelFileContent = fcg.writeWriteKernelFile();

    final String vivadoScriptContent = fcg.writeVivadoScriptFile();
    final String hlsScriptContent = fcg.writeHlsScriptFile();
    final String makefileContent = fcg.writeMakefile();

    // Xilinx OpenCL specific
    final String connectivityFileContent = fcg.writeConnectivityFile();
    final String xoclHostFileContent = fcg.writeXOCLHostFile();

    // Xilinx PYNQ specific
    final String pynqHostFileContent = fcg.writePYNQHostFile();
    final String pynqNotebookFileContent = fcg.writePYNQNotebookFile(pynqHostFileContent);

    // Xilinx C specific
    final String cHostFileContent = fcg.writeCHostFile();

    // 99- set back default class loader
    Thread.currentThread().setContextClassLoader(oldContextClassLoader);

    final String codegenPath = scenario.getCodegenDirectory() + "/";

    // copy generated files
    PreesmIOHelper.getInstance().print(codegenPath, TEMPLATE_DEFINE_HEADER_NAME, headerFileContent);
    PreesmIOHelper.getInstance().print(codegenPath, fcg.getTopKernelName() + ".cpp", topKernelFileContent);
    PreesmIOHelper.getInstance().print(codegenPath, fcg.getReadKernelName() + ".cpp", readKernelFileContent);
    PreesmIOHelper.getInstance().print(codegenPath, fcg.getWriteKernelName() + ".cpp", writeKernelFileContent);

    PreesmIOHelper.getInstance().print(codegenPath, "host_xocl_" + fcg.graphName + ".cpp", xoclHostFileContent);
    PreesmIOHelper.getInstance().print(codegenPath, "host_c_" + fcg.graphName + ".c", cHostFileContent);
    PreesmIOHelper.getInstance().print(codegenPath, "connectivity_" + fcg.graphName + ".cfg", connectivityFileContent);

    PreesmIOHelper.getInstance().print(codegenPath, "host_pynq_" + fcg.graphName + ".py", pynqHostFileContent);
    PreesmIOHelper.getInstance().print(codegenPath, "host_pynq_" + fcg.graphName + ".ipynb", pynqNotebookFileContent);
    PreesmIOHelper.getInstance().print(codegenPath + "/" + STDFILE_SCRIPT_SUBDIR, "script_vivado.tcl",
        vivadoScriptContent);
    PreesmIOHelper.getInstance().print(codegenPath + "/" + STDFILE_SCRIPT_SUBDIR, "script_hls.tcl", hlsScriptContent);
    PreesmIOHelper.getInstance().print(codegenPath, "Makefile", makefileContent);

    // copy stdfiles
    try {

      final String contentXOCLcpp = PreesmResourcesHelper.getInstance().read(STDFILE_LIB_XOCL_CPP, fcg.getClass());
      PreesmIOHelper.getInstance().print(codegenPath + STDFILE_LIB_XOCL_SUBDIR, "xcl2.cpp", contentXOCLcpp);

      final String contentXOCLhpp = PreesmResourcesHelper.getInstance().read(STDFILE_LIB_XOCL_HPP, fcg.getClass());
      PreesmIOHelper.getInstance().print(codegenPath + STDFILE_LIB_XOCL_SUBDIR, "xcl2.hpp", contentXOCLhpp);

      final String contentScriptXsa = PreesmResourcesHelper.getInstance().read(SCRIPT_VIVADO_TCL, fcg.getClass());
      PreesmIOHelper.getInstance().print(codegenPath + "/" + STDFILE_SCRIPT_SUBDIR, "script_vivado_xsa.tcl",
          contentScriptXsa);

    } catch (IOException e) {
      throw new PreesmRuntimeException("Could not copy all the stdfiles.", e);
    }

  }

  protected String writePYNQNotebookFile(final String rawPynqHostCode) {
    // 1- init engine
    final VelocityEngine engine = new VelocityEngine();
    engine.init();

    // 2- init context
    final VelocityContext context = new VelocityContext();
    final String[] rawCodeLines = rawPynqHostCode.split("\n");
    final List<String> fmtCodeLines = new ArrayList<>();
    for (final String line : rawCodeLines) {
      fmtCodeLines.add("\"" + line.replaceAll("\"", "\\\\\"") + "\\n\"");
    }

    context.put("PREESM_PYNQ_HOST_CODE_FMT", fmtCodeLines.stream().collect(Collectors.joining(",\n")));

    // 3- init template reader
    final InputStreamReader reader = PreesmIOHelper.getInstance()
        .getFileReader(TEMPLATE_PYNQ_HOST_NOTEBOOK_RES_LOCATION, this.getClass());

    // 4- init output writer
    final StringWriter writer = new StringWriter();

    engine.evaluate(context, writer, VELOCITY_PACKAGE_NAME, reader);

    return writer.toString();
  }

  protected String writePYNQHostFile() {
    // 1- init engine
    final VelocityEngine engine = new VelocityEngine();
    engine.init();

    // 2- init context
    final VelocityContext context = new VelocityContext();
    context.put("APPLI_NAME", graphName);
    context.put("KERNEL_NAME_READ", getReadKernelName());
    context.put("KERNEL_NAME_WRITE", getWriteKernelName());

    final StringBuilder sbConstants = new StringBuilder();
    interfaceRates.forEach((ia, p) -> {
      final long rate = p.getKey();
      sbConstants.append(String.format("%s = %d\n", getInterfaceRateName(ia), rate));
    });
    context.put("PREESM_CONSTANTS", sbConstants.toString());

    final StringBuilder sbBufferInit = new StringBuilder();
    final StringBuilder sbBufferMapping = new StringBuilder();
    for (final InterfaceActor ia : interfaceRates.keySet()) {
      final String type = ia.getDataPort().getFifo().getType();
      sbBufferInit.append(ia.getName() + SUFFIX_INTERFACE_BUFFER + " = allocate(shape=(" + getInterfaceRateName(ia)
          + ",), dtype=np.dtype('" + type + "'))\n");
      if (ia instanceof DataInputInterface) {
        sbBufferInit.append(ia.getName() + SUFFIX_INTERFACE_VECTOR + " = [" + type + "() for i in range("
            + getInterfaceRateName(ia) + ")]\n" + "np.copyto(" + ia.getName() + SUFFIX_INTERFACE_BUFFER + ", "
            + ia.getName() + SUFFIX_INTERFACE_VECTOR + ")\n\n");
        sbBufferMapping.append("mem_read.write(mem_read.register_map." + ia.getName() + SUFFIX_INTERFACE_ARRAY
            + "_1.address, " + ia.getName() + SUFFIX_INTERFACE_BUFFER + ".physical_address)\n");
      } else if (ia instanceof DataOutputInterface) {
        sbBufferMapping.append("mem_write.write(mem_write.register_map." + ia.getName() + SUFFIX_INTERFACE_ARRAY
            + "_1.address, " + ia.getName() + SUFFIX_INTERFACE_BUFFER + ".physical_address)\n");
      }
    }
    context.put("PREESM_BUFFER_INIT", sbBufferInit.toString());
    context.put("PREESM_BUFFER_MAPPING", sbBufferMapping.toString());

    // 3- init template reader
    final InputStreamReader reader = PreesmIOHelper.getInstance().getFileReader(TEMPLATE_PYNQ_HOST_RES_LOCATION,
        this.getClass());

    // 4- init output writer
    final StringWriter writer = new StringWriter();

    engine.evaluate(context, writer, VELOCITY_PACKAGE_NAME, reader);

    return writer.toString();
  }

  protected String writeVivadoScriptFile() {
    // 1- init engine
    final VelocityEngine engine = new VelocityEngine();
    engine.init();

    // 2- init context
    final VelocityContext context = new VelocityContext();

    context.put("KERNEL_NAME_TOP", getTopKernelName());
    context.put("KERNEL_NAME_READ", getReadKernelName());
    context.put("KERNEL_NAME_WRITE", getWriteKernelName());
    context.put("GLOBAL_CLOCK_MHZ", Integer.toString(fpga.getFrequency()));
    context.put("PART_NAME", fpga.getPart());
    context.put("BOARD_NAME", fpga.getBoard());

    final StringBuilder sb = new StringBuilder();
    for (final InterfaceActor ia : interfaceRates.keySet()) {
      final String fifoName = "axis_data_fifo_" + ia.getName() + SUFFIX_INTERFACE_STREAM;
      sb.append("create_bd_cell -type ip -vlnv xilinx.com:ip:axis_data_fifo:2.0 " + fifoName + "\n"
          + "set_property -dict [list CONFIG.FIFO_DEPTH {" + PYNQ_INTERFACE_DEPTH + "}] [get_bd_cells " + fifoName
          + "]\n");
      if (ia instanceof DataInputInterface) {
        sb.append("connect_bd_intf_net [get_bd_intf_pins " + getReadKernelName() + "_0/" + ia.getName()
            + SUFFIX_INTERFACE_STREAM + "] [get_bd_intf_pins " + fifoName + "/S_AXIS]\n"
            + "connect_bd_intf_net [get_bd_intf_pins " + fifoName + "/M_AXIS] [get_bd_intf_pins " + getTopKernelName()
            + "_0/" + ia.getName() + SUFFIX_INTERFACE_STREAM + "]\n");
      } else if (ia instanceof DataOutputInterface) {
        sb.append("connect_bd_intf_net [get_bd_intf_pins " + getTopKernelName() + "_0/" + ia.getName()
            + SUFFIX_INTERFACE_STREAM + "] [get_bd_intf_pins " + fifoName + "/S_AXIS]\n"
            + "connect_bd_intf_net [get_bd_intf_pins " + fifoName + "/M_AXIS] [get_bd_intf_pins " + getWriteKernelName()
            + "_0/" + ia.getName() + SUFFIX_INTERFACE_STREAM + "]\n");
      }
      sb.append("#apply_bd_automation -rule xilinx.com:bd_rule:clkrst -config { Clk {/processing_system7_0/FCLK_CLK0 ("
          + fpga.getFrequency() + " MHz)} Freq {" + fpga.getFrequency()
          + "} Ref_Clk0 {} Ref_Clk1 {} Ref_Clk2 {}}  [get_bd_pins " + fifoName + "/s_axis_aclk]\n\n");
    }

    context.put("PREESM_STREAM_CONNECTIVITY", sb.toString());

    // 3- init template reader
    final InputStreamReader reader = PreesmIOHelper.getInstance().getFileReader(TEMPLATE_SCRIPT_VIVADO_RES_LOCATION,
        this.getClass());

    // 4- init output writer
    final StringWriter writer = new StringWriter();

    engine.evaluate(context, writer, VELOCITY_PACKAGE_NAME, reader);

    return writer.toString();
  }

  protected String writeHlsScriptFile() {
    // 1- init engine
    final VelocityEngine engine = new VelocityEngine();
    engine.init();

    // 2- init context
    final VelocityContext context = new VelocityContext();

    context.put("GLOBAL_PERIOD_NS", Double.toString(1000.0 / fpga.getFrequency()));
    context.put("PART_NAME", fpga.getPart());

    // 3- init template reader
    final InputStreamReader reader = PreesmIOHelper.getInstance().getFileReader(TEMPLATE_SCRIPT_PYNQ_HLS,
        this.getClass());

    // 4- init output writer
    final StringWriter writer = new StringWriter();

    engine.evaluate(context, writer, VELOCITY_PACKAGE_NAME, reader);

    return writer.toString();
  }

  protected String writeMakefile() {
    // 1- init engine
    final VelocityEngine engine = new VelocityEngine();
    engine.init();

    // 2- init context
    final VelocityContext context = new VelocityContext();
    context.put("SCRIPTS_SUBDIR", STDFILE_SCRIPT_SUBDIR);
    context.put("TOP_KERNEL_SOURCE", getTopKernelName() + ".cpp");
    context.put("READ_KERNEL_SOURCE", getReadKernelName() + ".cpp");
    context.put("WRITE_KERNEL_SOURCE", getWriteKernelName() + ".cpp");
    context.put("KERNEL_NAME_TOP", getTopKernelName());
    context.put("KERNEL_NAME_READ", getReadKernelName());
    context.put("KERNEL_NAME_WRITE", getWriteKernelName());
    context.put("APPLI_NAME", graphName);

    // 3- init template reader
    final InputStreamReader reader = PreesmIOHelper.getInstance().getFileReader(TEMPLATE_MAKEFILE_RES_LOCATION,
        this.getClass());

    // 4- init output writer
    final StringWriter writer = new StringWriter();

    engine.evaluate(context, writer, VELOCITY_PACKAGE_NAME, reader);

    return writer.toString();
  }

  protected String writeDefineHeaderFile() {

    final StringBuilder sb = new StringBuilder("// interface sizes computed by PREESM\n");
    interfaceRates.forEach((ia, p) -> {
      final long rate = p.getKey();
      final long factor = p.getValue();
      sb.append(String.format("#define %s %d\n", getInterfaceRateName(ia), rate));
      sb.append(String.format("#define %s %d\n", getInterfaceFactorName(ia), factor));
    });

    return sb.toString();
  }

  protected String writeXOCLHostFile() {
    // 1- init engine
    final VelocityEngine engine = new VelocityEngine();
    engine.init();

    // 2- init context
    final VelocityContext context = new VelocityContext();

    context.put("PREESM_INCLUDES", includeCFile(TEMPLATE_DEFINE_HEADER_NAME));
    context.put("KERNEL_NAME_READ", getReadKernelName());
    context.put("KERNEL_NAME_WRITE", getWriteKernelName());

    // 2.1- generate vectors for interfaces
    final StringBuilder interfaceVectors = new StringBuilder("// vectors containing interface elements\n");
    interfaceRates.forEach((i, p) -> {
      final String type = i.getDataPort().getFifo().getType();
      interfaceVectors.append("  std::vector<" + type + ", aligned_allocator<" + type + ">> ");
      interfaceVectors.append(i.getName() + SUFFIX_INTERFACE_VECTOR + "(" + getInterfaceRateName(i) + ");\n");
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
      bufferDecl += ", sizeof(" + type + ")*" + getInterfaceRateName(i);
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
    final InputStreamReader reader = PreesmIOHelper.getInstance().getFileReader(TEMPLATE_OPENCL_HOST_RES_LOCATION,
        this.getClass());

    // 4- init output writer
    final StringWriter writer = new StringWriter();

    engine.evaluate(context, writer, VELOCITY_PACKAGE_NAME, reader);

    return writer.toString();
  }

  protected String writeCHostFile() {
    // 1- init engine
    final VelocityEngine engine = new VelocityEngine();
    engine.init();

    // 2- init context
    final VelocityContext context = new VelocityContext();

    String includes = includeCFile(TEMPLATE_DEFINE_HEADER_NAME);
    includes += includeCFile(getCReadKernelName().toLowerCase() + ".h");
    includes += includeCFile(getCWriteKernelName().toLowerCase() + ".h");
    context.put("PREESM_INCLUDES", includes);

    // Replace placeholders names in code
    context.put("XMEM_READ_KERNEL_NAME", getCReadKernelName());
    context.put("XMEM_WRITE_KERNEL_NAME", getCWriteKernelName());
    context.put("XMEM_READ_KERNEL_DEVICE_ID", "XPAR_MEM_READ_" + graphName.toUpperCase() + "_0_DEVICE_ID");
    context.put("XMEM_WRITE_KERNEL_DEVICE_ID", "XPAR_MEM_WRITE_" + graphName.toUpperCase() + "_0_DEVICE_ID");

    final StringBuilder bufferInit = new StringBuilder();
    final StringBuilder inBufferInterface = new StringBuilder();
    final StringBuilder outBufferInterface = new StringBuilder();
    final StringBuilder inBufferFlushing = new StringBuilder();
    final StringBuilder outBufferFlushing = new StringBuilder();

    interfaceRates.forEach((i, p) -> {
      final String type = i.getDataPort().getFifo().getType();
      final String name = i.getName();
      final String rate = getInterfaceRateName(i);

      // Initialize buffer for interface
      bufferInit.append("  " + type + "* " + name + " = malloc(sizeof(*" + name + ") * " + rate + ");\n");

      // Set buffer on interface
      if (i instanceof DataInputInterface) {
        inBufferInterface
            .append("  " + getCReadKernelName() + "_Set_" + name + "_mem(&mem_read, (u32) " + name + ");\n");
      } else {
        outBufferInterface
            .append("  " + getCWriteKernelName() + "_Set_" + name + "_mem(&mem_write, (u32) " + name + ");\n");
      }

      // Flush buffers
      final String flush = "  Xil_DCacheFlushRange((INTPTR)" + name + ", " + rate + " * sizeof(*" + name + "));\n";
      if (i instanceof DataInputInterface) {
        inBufferFlushing.append(flush);
      } else {
        outBufferFlushing.append(flush);
      }
    });
    context.put("BUFFER_INITIALIZATION", bufferInit);
    context.put("INPUT_BUFFER_INTERFACE", inBufferInterface);
    context.put("OUTPUT_BUFFER_INTERFACE", outBufferInterface);
    context.put("INPUT_BUFFER_FLUSHING", inBufferFlushing);
    context.put("OUTPUT_BUFFER_FLUSHING", outBufferFlushing);

    // 2.1- generate vectors for interfaces
    final StringBuilder interfaceVectors = new StringBuilder("// vectors containing interface elements\n");
    interfaceRates.forEach((i, p) -> {
      final String type = i.getDataPort().getFifo().getType();
      interfaceVectors.append("  std::vector<" + type + ", aligned_allocator<" + type + ">> ");
      interfaceVectors.append(i.getName() + SUFFIX_INTERFACE_VECTOR + "(" + getInterfaceRateName(i) + ");\n");
    });
    context.put("INTERFACE_VECTORS", interfaceVectors.toString());

    // 3- init template reader
    final InputStreamReader reader = PreesmIOHelper.getInstance().getFileReader(TEMPLATE_C_HOST_RES_LOCATION,
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

    context.put("PREESM_INCLUDES", includeCFile(TEMPLATE_DEFINE_HEADER_NAME));

    context.put("USER_INCLUDES",
        findAllCHeaderFileNamesUsed.stream().map(FpgaCodeGenerator::includeCFile).collect(Collectors.joining()));

    final Map<AbstractActor, String> initActorsCalls = new LinkedHashMap<>();
    final Map<AbstractActor, String> loopActorsCalls = new LinkedHashMap<>();
    final StringBuilder defs = new StringBuilder();
    // 2.1- first we add the definitions of special actors
    defs.append(FpgaSpecialActorsCodeGenerator.generateSpecialActorDefinitions(graph, loopActorsCalls));
    context.put("PREESM_SPECIAL_ACTORS", defs.toString());
    // 2.2- we add all other calls to the map
    final Map<Actor, Pair<String, String>> actorTemplateParts = new LinkedHashMap<>();
    graph.getActorsWithRefinement().forEach(x -> {
      // at this point, all actors should have a CHeaderRefinement
      actorTemplateParts.put(x, AutoFillHeaderTemplatedFunctions.getFilledTemplateFunctionPart(x));
    });
    generateRegularActorCalls(actorTemplateParts, initActorsCalls, true);
    generateRegularActorCalls(actorTemplateParts, loopActorsCalls, false);
    // 2.3- we wrap the actor init calls
    if (!initActorsCalls.isEmpty()) {
      context.put("PREESM_INIT_WRAPPER", generateInitWrapper(initActorsCalls));
    } else {
      context.put("PREESM_INIT_WRAPPER", "");
    }
    // 2.4- we wrap the actor calls producing delays
    context.put("PREESM_DELAY_WRAPPERS", generateDelayWrappers(loopActorsCalls));
    // 2.5- we generate the top kernel function with all calls in the start time order
    final StringBuilder topK = new StringBuilder("extern \"C\" {\nvoid " + getTopKernelName() + "(\n");
    // add interface names
    final List<String> args = new ArrayList<>();
    for (final InterfaceActor ia : interfaceRates.keySet()) {
      if (ia instanceof DataInputInterface) {
        final Fifo f = ia.getDataPort().getFifo();
        args.add("  hls::stream<" + f.getType() + ">" + " &" + getFifoStreamName(f));
      }
    }
    for (final InterfaceActor ia : interfaceRates.keySet()) {
      if (ia instanceof DataOutputInterface) {
        final Fifo f = ia.getDataPort().getFifo();
        args.add("  hls::stream<" + f.getType() + ">" + " &" + getFifoStreamName(f));
      }
    }
    topK.append(args.stream().collect(Collectors.joining(",\n")));
    topK.append(") {\n");

    // add interface protocols
    for (final InterfaceActor ia : interfaceRates.keySet()) {
      if (ia instanceof DataInputInterface || ia instanceof DataOutputInterface) {
        topK.append(getPragmaAXIStream(ia));
      }
    }
    topK.append("#pragma HLS interface ap_ctrl_none port=return\n#pragma HLS dataflow disable_start_propagation\n\n");

    // add fifo defs
    topK.append(generateAllFifoDefinitions(allFifoDepths));
    // add function calls
    topK.append("\n");
    if (!initActorsCalls.isEmpty()) {
      topK.append(NAME_WRAPPER_INITPROTO + "();\n\n");
    }
    loopActorsCalls.forEach((x, y) -> topK.append("  " + y));

    topK.append("}\n}\n");
    context.put("PREESM_TOP_KERNEL", topK.toString());

    // 3- init template reader
    final InputStreamReader reader = PreesmIOHelper.getInstance().getFileReader(TEMPLATE_TOP_KERNEL_RES_LOCATION,
        this.getClass());

    // 4- init output writer
    final StringWriter writer = new StringWriter();

    engine.evaluate(context, writer, VELOCITY_PACKAGE_NAME, reader);

    return writer.toString();
  }

  protected String generateAllFifoDefinitions(final Map<Fifo, Long> allFifoSizes) {
    final StringBuilder sb = new StringBuilder();
    allFifoSizes.forEach((x, y) -> {
      final String name = getFifoStreamName(x);
      sb.append("  static hls::stream<" + x.getType() + "> " + name + ";\n");
      // note sure if Xilinx compiler can handle macro in pragmas ... so we use the direct value here
      sb.append("#pragma HLS stream variable=" + name + " depth=" + y + "\n");
    });
    return sb.toString();
  }

  protected void generateRegularActorCalls(final Map<Actor, Pair<String, String>> actorTemplateParts,
      final Map<AbstractActor, String> actorCalls, final boolean init) {
    for (final Entry<Actor, Pair<String, String>> e : actorTemplateParts.entrySet()) {
      final Actor a = e.getKey();
      final Pair<String, String> templates = e.getValue();
      // templates cannot be null since we check in the constructor that all actors have a CHeaderRefinement
      final String call = generateRegularActorCall((CHeaderRefinement) a.getRefinement(), templates, init);
      if (call != null) {
        actorCalls.put(a, call);
      }
    }
  }

  protected String generateRegularActorCall(final CHeaderRefinement cref, final Pair<String, String> templates,
      final boolean init) {
    // this weird way of passing the refinement instead of the actor is needed to handle
    // both Actor and DelayActor whose closest common ancestor is RefinementContainer
    // (which can hold a PiGraph or a CHeaderRefinement)
    // refinement container always is an actor for now
    final AbstractActor containerActor = (AbstractActor) cref.getRefinementContainer();
    final FunctionPrototype proto = init ? cref.getInitPrototype() : cref.getLoopPrototype();
    if (proto == null) {
      return null;
    }
    final String templatePart = init ? templates.getKey() : templates.getValue();
    final String funcRawName = proto.getName();
    final int indexStartTemplate = funcRawName.indexOf('<');
    final String funcShortName = indexStartTemplate < 0 ? funcRawName : funcRawName.substring(0, indexStartTemplate);
    final String funcTemplatedName = funcShortName + templatePart;
    final String prefix = RefinementChecker.getActorNamePrefix(containerActor);

    // now manage the arguments
    final List<String> listArgNames = new ArrayList<>();
    for (final FunctionArgument arg : proto.getArguments()) {
      if (arg.isIsConfigurationParameter() && arg.getDirection() == Direction.OUT) {
        throw new PreesmRuntimeException(
            "FPGA codegen does not support dynamic parameters as in actor " + containerActor.getVertexPath());
      } else if (arg.isIsConfigurationParameter() && arg.getDirection() == Direction.IN) {
        if (containerActor instanceof DelayActor) {
          // the graph parameter name may have been prefixed during a flattening transformation
          for (final Parameter inputParam : ((DelayActor) containerActor).getInputParameters()) {
            if (inputParam.getName().equals(prefix + arg.getName())) {
              listArgNames.add(Long.toString(inputParam.getExpression().evaluate()));
              break;
            }
          }
        } else {
          // look for incoming parameter with same name
          // more efficient with a map?
          for (final ConfigInputPort cip : containerActor.getConfigInputPorts()) {
            if (cip.getName().equals(arg.getName())) {
              final ISetter setter = cip.getIncomingDependency().getSetter();
              if (setter instanceof Parameter) {
                listArgNames.add(Long.toString(((Parameter) setter).getExpression().evaluate()));
                break;
              }
            }
          }
        }
      } else if (!arg.isIsConfigurationParameter()) {
        if (containerActor instanceof DelayActor) {
          // there is only one fifo, we take it
          final Fifo f = ((DelayActor) containerActor).getLinkedDelay().getContainingFifo();
          listArgNames.add(getFifoStreamName(f));
        } else {
          // look for incoming/outgoing fifo with same port name
          // more efficient with a map?
          for (final DataPort dp : containerActor.getAllDataPorts()) {
            if (dp.getName().equals(arg.getName())) {
              listArgNames.add(getFifoStreamName(dp.getFifo()));
              break;
            }
          }
        }
      }
    }
    // check that we found as many objects as arguments:
    if (listArgNames.size() != proto.getArguments().size()) {
      throw new PreesmRuntimeException("FPGA codegen couldn't evaluate all the arguments of the prototype of actor "
          + containerActor.getVertexPath() + ".");
    }
    // and otherwise we merge everything
    return funcTemplatedName + "(" + listArgNames.stream().collect(Collectors.joining(",")) + ");\n";
  }

  protected String generateInitWrapper(final Map<AbstractActor, String> actorCalls) {
    final StringBuilder sb = new StringBuilder("static void " + NAME_WRAPPER_INITPROTO + "() {\n");
    sb.append("  static bool init = false;\n  if (!init) {\n");
    for (final String call : actorCalls.values()) {
      sb.append("    " + call);
    }
    sb.append("    ap_wait();\n    init = true;\n");
    sb.append("  }\n}\n");
    return sb.toString();
  }

  protected String generateDelayWrappers(final Map<AbstractActor, String> actorCalls) {
    final StringBuilder sb = new StringBuilder();
    for (final Entry<AbstractActor, String> e : actorCalls.entrySet()) {
      final AbstractActor aa = e.getKey();
      final List<Fifo> delayedFifos = new ArrayList<>();
      for (final DataOutputPort dop : aa.getDataOutputPorts()) {
        final Fifo fifo = dop.getFifo();
        if (fifo.getDelay() != null) {
          delayedFifos.add(fifo);
        }
      }
      if (delayedFifos.isEmpty()) {
        // the actor has no outgoing fifo with delays so we do not need to wrap it
        continue;
      }
      // first we get the original call and build the new one
      final String oriCall = e.getValue();
      final StringBuilder newCall = new StringBuilder(PREFIX_WRAPPER_DELAYPROD + aa.getName() + "(");
      final StringBuilder wrapperProto = new StringBuilder(
          "static void " + PREFIX_WRAPPER_DELAYPROD + aa.getName() + "(");
      final List<String> listArgCall = new ArrayList<>();
      final List<String> listArgProto = new ArrayList<>();
      for (final DataPort dp : aa.getAllDataPorts()) {
        final Fifo f = dp.getFifo();
        final String fifoName = getFifoStreamName(f);
        // in the case of self-loops, the fifo would appear twice as an argument
        // we need to avoid that
        if (!listArgCall.contains(fifoName)) {
          listArgCall.add(fifoName);
          // we do not use port name because the original call already uses the fifo names
          listArgProto.add("hls::stream<" + f.getType() + "> &" + fifoName);
        }
      }
      newCall.append(listArgCall.stream().collect(Collectors.joining(", ")) + ");\n");
      wrapperProto.append(listArgProto.stream().collect(Collectors.joining(", ")) + ") {\n");
      e.setValue(newCall.toString());
      // we fill the wrapper body
      wrapperProto.append("  static bool init = false;\n  if (!init) {\n");
      for (final Fifo f : delayedFifos) {
        // if the delay has an init function we call it, otherwise we fill with zero
        final Delay d = f.getDelay();
        final DelayActor da = d.getActor();
        if (da != null && da.hasValidRefinement()) {
          // then the delay actor has a prototype that we will call
          // (it may have multiple params and only one output)
          wrapperProto.append("    " + generateDelayActorInitCall(da));
        } else {
          final String size = Long.toString(d.getSizeExpression().evaluate());
          // the type is surrounded by parenthesis to handle the case of "unsigned char" for example
          // the constructor call is made by braces "{}" since it is safer (to avoid the famous *C++ most vexing parse*)
          wrapperProto.append("  for (int i = 0; i < " + size + "; i++) {\n    " + getFifoStreamName(f) + ".write(("
              + f.getType() + "){});\n  }\n");
        }
      }
      wrapperProto.append("    ap_wait();\n    init = true;\n");
      wrapperProto.append("  }\n  " + oriCall + "}\n");
      sb.append(wrapperProto.toString());
    }
    return sb.toString();
  }

  protected String generateDelayActorInitCall(final DelayActor da) {
    final CHeaderRefinement cref = (CHeaderRefinement) da.getRefinement();
    // only loop prototype is used here and has already been checked
    final FunctionPrototype fp = cref.getInitPrototype();
    final List<Pair<Port, FunctionArgument>> correspondingArguments = RefinementChecker
        .getCHeaderRefinementPrototypeCorrespondingArguments(da, fp);
    final String funcFilledTemplate = AutoFillHeaderTemplatedFunctions.getFilledTemplatePrototypePart(cref, fp,
        correspondingArguments);
    return generateRegularActorCall(cref, new Pair<>(funcFilledTemplate, null), true);

  }

  protected String writeReadKernelFile() {
    final long nbIa = interfaceRates.keySet().stream().filter(DataInputInterface.class::isInstance).count();
    final boolean isMulti = nbIa > 1L;

    // 1- init engine
    final VelocityEngine engine = new VelocityEngine();
    engine.init();

    // 2- init context
    final VelocityContext context = new VelocityContext();
    context.put("PREESM_INCLUDES", includeCFile(TEMPLATE_DEFINE_HEADER_NAME));

    // read kernel prototype
    final StringBuilder sb = new StringBuilder("void " + getReadKernelName() + "(\n  ");
    final List<String> args = new ArrayList<>();
    for (final InterfaceActor ia : interfaceRates.keySet()) {
      if (ia instanceof DataInputInterface) {
        final Fifo f = ia.getDataPort().getFifo();
        args.add(f.getType() + "* " + ia.getName() + SUFFIX_INTERFACE_ARRAY);
        args.add("hls::stream<" + f.getType() + ">" + " &" + getFifoStreamName(f));
      }
    }
    sb.append(args.stream().collect(Collectors.joining(",\n  ")));
    sb.append(") {\n");

    // add interface protocols
    for (final InterfaceActor ia : interfaceRates.keySet()) {
      if (ia instanceof DataInputInterface) {
        sb.append(getPragmaAXIMemory(ia));
        sb.append(getPragmaAXIStream(ia));
      }
    }
    sb.append(PRAGMA_AXILITE_CTRL + "\n");

    // read kernel body
    int idxIa = 0;
    if (isMulti) {
      sb.append("  bool shouldContinue = true;\n  while (shouldContinue) {\n    shouldContinue = false;\n");
    }

    for (final Entry<InterfaceActor, Pair<Long, Long>> e : interfaceRates.entrySet()) {
      final InterfaceActor ia = e.getKey();
      if (ia instanceof DataInputInterface) {
        final Fifo f = ia.getDataPort().getFifo();
        if (isMulti) {
          final String templateParams = String.format("%s, %d, %s, %s", f.getType(), idxIa, getInterfaceFactorName(ia),
              getInterfaceRateName(ia));
          sb.append("    shouldContinue |= readInput<" + templateParams + ">(" + ia.getName() + SUFFIX_INTERFACE_ARRAY
              + ", " + getFifoStreamName(f) + ");\n");
        } else {
          sb.append("    readInput<" + f.getType() + ">(" + ia.getName() + SUFFIX_INTERFACE_ARRAY + ", "
              + getFifoStreamName(f) + ", " + getInterfaceRateName(ia) + ", " + getInterfaceFactorName(ia) + ");\n");
        }
        idxIa++;
      }
    }

    if (isMulti) {
      sb.append("  }\n");
    }
    sb.append("}\n");

    context.put("PREESM_READ_KERNEL", sb.toString());

    // 3- init template reader
    final String templateName = isMulti ? TEMPLATE_READ_KERNEL_MULTI_RES_LOCATION : TEMPLATE_READ_KERNEL_RES_LOCATION;
    final InputStreamReader reader = PreesmIOHelper.getInstance().getFileReader(templateName, this.getClass());

    // 4- init output writer
    final StringWriter writer = new StringWriter();

    engine.evaluate(context, writer, VELOCITY_PACKAGE_NAME, reader);

    return writer.toString();
  }

  protected String writeWriteKernelFile() {
    final long nbIa = interfaceRates.keySet().stream().filter(DataOutputInterface.class::isInstance).count();
    final boolean isMulti = nbIa > 1L;

    // 1- init engine
    final VelocityEngine engine = new VelocityEngine();
    engine.init();

    // 2- init context
    final VelocityContext context = new VelocityContext();
    context.put("PREESM_INCLUDES", includeCFile(TEMPLATE_DEFINE_HEADER_NAME));

    // write kernel prototype
    final StringBuilder sb = new StringBuilder("void " + getWriteKernelName() + "(\n  ");
    final List<String> args = new ArrayList<>();
    for (final InterfaceActor ia : interfaceRates.keySet()) {
      if (ia instanceof DataOutputInterface) {
        final Fifo f = ia.getDataPort().getFifo();
        args.add(f.getType() + "* " + ia.getName() + SUFFIX_INTERFACE_ARRAY);
        args.add("hls::stream<" + f.getType() + ">" + " &" + getFifoStreamName(f));
      }
    }
    sb.append(args.stream().collect(Collectors.joining(",\n  ")));
    sb.append(") {\n");

    // add interface protocols
    for (final InterfaceActor ia : interfaceRates.keySet()) {
      if (ia instanceof DataOutputInterface) {
        sb.append(getPragmaAXIMemory(ia));
        sb.append(getPragmaAXIStream(ia));
      }
    }
    sb.append(PRAGMA_AXILITE_CTRL + "\n");

    // write kernel body
    int idxIa = 0;
    if (isMulti) {
      sb.append("  bool shouldContinue = true;\n  while (shouldContinue) {\n    shouldContinue = false;\n");
    }

    for (final Entry<InterfaceActor, Pair<Long, Long>> e : interfaceRates.entrySet()) {
      final InterfaceActor ia = e.getKey();
      if (ia instanceof DataOutputInterface) {
        final Fifo f = ia.getDataPort().getFifo();
        if (isMulti) {
          final String templateParams = String.format("%s, %d, %s, %s", f.getType(), idxIa, getInterfaceFactorName(ia),
              getInterfaceRateName(ia));
          sb.append("    shouldContinue |= writeOutput<" + templateParams + ">(" + ia.getName() + SUFFIX_INTERFACE_ARRAY
              + ", " + getFifoStreamName(f) + ");\n");
        } else {
          sb.append("    writeOutput<" + f.getType() + ">(" + ia.getName() + SUFFIX_INTERFACE_ARRAY + ", "
              + getFifoStreamName(f) + ", " + getInterfaceRateName(ia) + ", " + getInterfaceFactorName(ia) + ");\n");
        }
        idxIa++;
      }
    }

    if (isMulti) {
      sb.append("  }\n");
    }
    sb.append("}\n");

    context.put("PREESM_WRITE_KERNEL", sb.toString());

    // 3- init template reader
    final String templateName = isMulti ? TEMPLATE_WRITE_KERNEL_MULTI_RES_LOCATION : TEMPLATE_WRITE_KERNEL_RES_LOCATION;
    final InputStreamReader reader = PreesmIOHelper.getInstance().getFileReader(templateName, this.getClass());

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
        sb.append(getReadKernelName() + "_1.");
        sb.append(ia.getName() + SUFFIX_INTERFACE_STREAM + ":");
        sb.append(getTopKernelName() + "_1.");
        sb.append(ia.getName() + SUFFIX_INTERFACE_STREAM + "\n");
      } else if (ia instanceof DataOutputInterface) {
        sb.append(getTopKernelName() + "_1.");
        sb.append(ia.getName() + SUFFIX_INTERFACE_STREAM + ":");
        sb.append(getWriteKernelName() + "_1.");
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

  protected static final String includeCFile(final String file) {
    return "#include \"" + file + "\"\n";
  }

  protected static final String getPragmaAXIStream(InterfaceActor ia) {
    final String name = getFifoStreamName(ia.getDataPort().getFifo());
    return "#pragma HLS INTERFACE axis port=" + name + " name=" + name + "\n";
  }

  protected static final String getPragmaAXIMemory(InterfaceActor ia) {
    final String name = ia.getName() + SUFFIX_INTERFACE_ARRAY;
    return "#pragma HLS INTERFACE m_axi offset=slave port=" + name + " name=" + name + "\n";
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

  public static final String getFifoStreamName(final Fifo fifo) {
    if (fifo.getSource() instanceof InterfaceActor) {
      return ((InterfaceActor) fifo.getSource()).getName() + SUFFIX_INTERFACE_STREAM;
    }
    if (fifo.getTarget() instanceof InterfaceActor) {
      return ((InterfaceActor) fifo.getTarget()).getName() + SUFFIX_INTERFACE_STREAM;
    }
    return "stream__" + fifo.getId().replace('.', '_').replace("-", "__");
  }

  public final String getReadKernelName() {
    return "mem_read_" + graphName;
  }

  public final String getCReadKernelName() {
    return "XMem_read_" + graphName.toLowerCase();
  }

  public final String getWriteKernelName() {
    return "mem_write_" + graphName;
  }

  public final String getCWriteKernelName() {
    return "XMem_write_" + graphName.toLowerCase();
  }

  public final String getTopKernelName() {
    return "top_graph_" + graphName;
  }
}
