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

  public static final String KERNEL_NAME_READ         = "mem_read";
  public static final String KERNEL_NAME_WRITE        = "mem_write";
  public static final String KERNEL_NAME_TOP          = "top_graph";
  public static final String NAME_WRAPPER_INITPROTO   = "preesmInitWrapper";
  public static final String PREFIX_WRAPPER_DELAYPROD = "wrapperProdDelay_";
  public static final String SUFFIX_INTERFACE_ARRAY   = "_mem";             // suffix name for read/write kernels
  public static final String SUFFIX_INTERFACE_STREAM  = "_stream";          // suffix name for other kernels
  public static final String SUFFIX_INTERFACE_VECTOR  = "_vect";            // suffix name for host vector
  public static final String SUFFIX_INTERFACE_BUFFER  = "_buff";            // suffix name for host buffer

  protected static final String PRAGMA_AXILITE_CTRL = "#pragma HLS INTERFACE s_axilite port=return\n";

  private final Scenario                              scenario;
  private final PiGraph                               graph;
  private final String                                graphName;
  private final Map<InterfaceActor, Pair<Long, Long>> interfaceRates;
  private final Map<Fifo, Long>                       allFifoDepths;

  private FpgaCodeGenerator(final Scenario scenario, final PiGraph graph,
      final Map<InterfaceActor, Pair<Long, Long>> interfaceRates, final Map<Fifo, Long> allFifoSizes) {
    this.scenario = scenario;
    this.graph = graph;
    this.graphName = scenario.getAlgorithm().getName();
    this.interfaceRates = interfaceRates;
    this.allFifoDepths = new LinkedHashMap<>();

    // the fifo sizes are given in bytes while we want the depth in number of elements
    allFifoSizes.forEach((k, v) -> {
      final long dataTypeSize = scenario.getSimulationInfo().getDataTypeSizeOrDefault(k.getType());
      allFifoDepths.put(k, (v / dataTypeSize));
    });

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
    allFifoDepths.forEach((fifo, size) -> {
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

    context.put("PREESM_INCLUDES", "#include \"" + TEMPLATE_DEFINE_HEADER_NAME + "\"\n");

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

    final Map<AbstractActor, String> initActorsCalls = new LinkedHashMap<>();
    final Map<AbstractActor, String> loopActorsCalls = new LinkedHashMap<>();
    final StringBuilder defs = new StringBuilder();
    // 2.1- first we add the definitions of special actors
    defs.append(FpgaSpecialActorsCodeGenerator.generateSpecialActorDefinitions(graph, loopActorsCalls));
    context.put("PREESM_SPECIAL_ACTORS", defs.toString());
    // 2.2- we add all other calls to the map
    final Map<Actor, Pair<String, String>> actorTemplateParts = new LinkedHashMap<>();
    graph.getActorsWithRefinement().forEach(x -> {
      // TODO should not be check here but at the beginning of the codegen (all actors should have a loop proto)
      // in prod we could even throw an exception, but useful for tests when no refinement set yet
      if (x.getRefinement() instanceof CHeaderRefinement) {
        actorTemplateParts.put(x, AutoFillHeaderTemplatedFunctions.getFilledTemplateFunctionPart(x));
      }
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
    final StringBuilder topK = new StringBuilder("extern \"C\" {\nvoid " + KERNEL_NAME_TOP + "(\n");
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
      if (templates == null) {
        // TODO remove this test
        // in prod we could even throw an exception, but useful for tests when no refinement set yet
        continue;
      }
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
      throw new PreesmRuntimeException("FPGA codegen coudn't evaluate all the arguments of the prototype of actor "
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
        args.add("hls::stream<" + f.getType() + ">" + " &" + getFifoStreamName(f));
      }
    }
    sb.append(args.stream().collect(Collectors.joining(",\n  ")));
    sb.append(") {\n");

    // add interface protocols
    for (final InterfaceActor ia : interfaceRates.keySet()) {
      if (ia instanceof DataInputInterface) {
        final Fifo f = ia.getDataPort().getFifo();
        sb.append(getPragmaAXIMemory(ia));
        sb.append(getPragmaAXIStream(ia));
      }
    }
    sb.append(PRAGMA_AXILITE_CTRL + "\n");

    // read kernel body
    for (final Entry<InterfaceActor, Pair<Long, Long>> e : interfaceRates.entrySet()) {
      final InterfaceActor ia = e.getKey();
      if (ia instanceof DataInputInterface) {
        final Fifo f = ia.getDataPort().getFifo();
        sb.append("    readInput<" + f.getType() + ">(" + ia.getName() + SUFFIX_INTERFACE_ARRAY + ", "
            + getFifoStreamName(f) + ", " + getInterfaceRateName(ia) + ", " + getInterfaceFactorName(ia) + ");\n");
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
    for (final Entry<InterfaceActor, Pair<Long, Long>> e : interfaceRates.entrySet()) {
      final InterfaceActor ia = e.getKey();
      if (ia instanceof DataOutputInterface) {
        final Fifo f = ia.getDataPort().getFifo();
        sb.append("    writeOutput<" + f.getType() + ">(" + ia.getName() + SUFFIX_INTERFACE_ARRAY + ", "
            + getFifoStreamName(f) + ", " + getInterfaceRateName(ia) + ", " + getInterfaceFactorName(ia) + ");\n");
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
        sb.append(KERNEL_NAME_TOP + "_1.");
        sb.append(ia.getName() + SUFFIX_INTERFACE_STREAM + "\n");
      } else if (ia instanceof DataOutputInterface) {
        sb.append(KERNEL_NAME_TOP + "_1.");
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

  protected static final String getPragmaAXIStream(InterfaceActor ia) {
    return "#pragma HLS INTERFACE axis port=" + getFifoStreamName(ia.getDataPort().getFifo()) + "\n";
  }

  protected static final String getPragmaAXIMemory(InterfaceActor ia) {
    return "#pragma HLS INTERFACE m_axi offset=slave port=" + ia.getName() + SUFFIX_INTERFACE_ARRAY + "\n";
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

}
