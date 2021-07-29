package org.preesm.algorithm.schedule.fpga;

import java.io.File;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.model.pisdf.AbstractVertex;
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

  public static final String TEMPLATE_KERNEL_RES_LOCATION = "templates/xilinxCodegen/template_host_fpga.cpp";
  public static final String TEMPLATE_HOST_RES_LOCATION   = "templates/xilinxCodegen/template_kernel_fpga.cpp";

  private final Scenario                              scenario;
  private final PiGraph                               graph;
  private final Map<AbstractVertex, Long>             brv;
  private final Map<InterfaceActor, Pair<Long, Long>> interfaceRates;
  private final Map<Fifo, Long>                       allFifoSizes;

  private FpgaCodeGenerator(final Scenario scenario, final PiGraph graph, final Map<AbstractVertex, Long> brv,
      final Map<InterfaceActor, Pair<Long, Long>> interfaceRates, final Map<Fifo, Long> allFifoSizes) {
    this.scenario = scenario;
    this.graph = graph;
    this.brv = brv;
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
   * @param brv
   *          Repetition vector of the flat graph.
   * @param interfaceRates
   *          Interface rates of the flat graph.
   * @param allFifoSizes
   *          All sizes of the fifo.
   */
  public static void generateFiles(final Scenario scenario, final PiGraph graph, Map<AbstractVertex, Long> brv,
      Map<InterfaceActor, Pair<Long, Long>> interfaceRates, Map<Fifo, Long> allFifoSizes) {
    final FpgaCodeGenerator fcg = new FpgaCodeGenerator(scenario, graph, brv, interfaceRates, allFifoSizes);

    // 0- without the following class loader initialization, I get the following exception when running as Eclipse
    // plugin:
    // org.apache.velocity.exception.VelocityException: The specified class for ResourceManager
    // (org.apache.velocity.runtime.resource.ResourceManagerImpl) does not implement
    // org.apache.velocity.runtime.resource.ResourceManager; Velocity is not initialized correctly.
    final ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(fcg.getClass().getClassLoader());

    final String headerFileContent = fcg.writeDefineHeaderFile();
    final String hostFileContent = fcg.writeHostFile();
    final String kernelFileContent = fcg.writeKernelFile();

    // 99- set back default class loader
    Thread.currentThread().setContextClassLoader(oldContextClassLoader);

    final String codegenPath = scenario.getCodegenDirectory() + File.separator;
    final String graphName = scenario.getAlgorithm().getName();

    PreesmIOHelper.getInstance().print(codegenPath, "define_" + graphName + ".h", headerFileContent);
    PreesmIOHelper.getInstance().print(codegenPath, "host_" + graphName + ".cpp", hostFileContent);
    PreesmIOHelper.getInstance().print(codegenPath, "kernel_" + graphName + ".cpp", kernelFileContent);

  }

  public String writeDefineHeaderFile() {

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

  public String writeHostFile() {
    // 1- init engine
    final VelocityEngine engine = new VelocityEngine();
    engine.init();

    // 2- init context
    final VelocityContext context = new VelocityContext();
    final List<String> findAllCHeaderFileNamesUsed = CHeaderUsedLocator.findAllCHeaderFileNamesUsed(graph);

    context.put("PREESM_INCLUDES", "#include \"" + TEMPLATE_DEFINE_HEADER_NAME + "\"\n");

    // TODO generate ther kernel args

    // 3- init template reader
    final InputStreamReader reader = PreesmIOHelper.getInstance().getFileReader(TEMPLATE_KERNEL_RES_LOCATION,
        this.getClass());

    // 4- init output writer
    final StringWriter writer = new StringWriter();

    engine.evaluate(context, writer, "org.apache.velocity", reader);

    return writer.toString();
  }

  public String writeKernelFile() {
    // 1- init engine
    final VelocityEngine engine = new VelocityEngine();
    engine.init();

    // 2- init context
    final VelocityContext context = new VelocityContext();
    final List<String> findAllCHeaderFileNamesUsed = CHeaderUsedLocator.findAllCHeaderFileNamesUsed(graph);

    context.put("PREESM_INCLUDES", "#include \"" + TEMPLATE_DEFINE_HEADER_NAME + "\"\n");

    context.put("USER_INCLUDES",
        findAllCHeaderFileNamesUsed.stream().map(x -> "#include \"" + x + "\"").collect(Collectors.joining("\n")));

    // 3- init template reader
    final InputStreamReader reader = PreesmIOHelper.getInstance().getFileReader(TEMPLATE_KERNEL_RES_LOCATION,
        this.getClass());

    // 4- init output writer
    final StringWriter writer = new StringWriter();

    engine.evaluate(context, writer, "org.apache.velocity", reader);

    return writer.toString();
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
