package org.preesm.algorithm.refining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.statictools.PiSDFFlattener;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.generator.ScenariosGenerator;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.TimingType;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

@PreesmTask(id = "AutoRefiningTask.identifier", name = "Auto-Refining Task",
    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class) },
    outputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class) },
    parameters = {
        @Parameter(name = AutoRefiningTask.MODE_PARAM, description = "Select part of the graph to be refine",
            values = { @Value(name = "Fixed:=n", effect = "refine actor according the strategy selected") }),
        @Parameter(name = AutoRefiningTask.MD5_PARAM, description = "Compare MD5 hash between actor and actor refine",
            values = { @Value(name = "Boolean", effect = "check MD5 hash") }) })
public class AutoRefiningTask extends AbstractTaskImplementation {

  private static final String                   MODE_PARAM      = "mode";
  private static final String                   MODE_DEFAULT    = "0";
  private static final String                   MD5_PARAM       = "MD5 check";
  private static final String                   MD5_DEFAULT     = "true";
  private RefineMode                            refineMode;
  String                                        filePath        = "";
  String                                        functionContent = "";
  StringBuilder                                 splitfunc       = new StringBuilder();
  StringBuilder                                 splitHeader     = new StringBuilder();
  int                                           iteratorLoop    = 0;
  static List<org.preesm.model.pisdf.Parameter> graphParameters = new ArrayList<>();
  String                                        directory       = "";
  List<AbstractActor>                           splitActors     = new ArrayList<>();
  List<Long>                                    timings         = new ArrayList<>();
  static String                                 bufferIterator  = "";
  String                                        forLoop         = "";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {
    // Get the root of the workspace
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

    // Task inputs
    final Scenario scenario = (Scenario) inputs.get("scenario");
    PiGraph graph = scenario.getAlgorithm();
    graphParameters = graph.getParameters();
    final String[] split = scenario.getScenarioURL().split("/");
    final String projectName = split[1].intern();

    // Get the project from the workspace
    final IProject project = root.getProject(projectName);
    final String projectRootPath = (project.getLocationURI().getPath()).replace(projectName, "");
    String projectFullPath = scenario.getScenarioURL();

    projectFullPath = projectFullPath.substring(0, projectFullPath.lastIndexOf("/") + 1) + "generated/";

    final String modeStr = parameters.get(MODE_PARAM);

    final String md5Str = parameters.get(MD5_PARAM);
    final Boolean md5 = Boolean.valueOf(md5Str);

    List<AbstractActor> iterActor = new ArrayList<>();

    this.refineMode = switch (modeStr) {
      case "0" -> RefineMode.FULL;
      case "1" -> RefineMode.CPN;
      case "2" -> RefineMode.TIME;
      case "3" -> RefineMode.HYBRID;
      default -> RefineMode.FULL;
    };

    switch (refineMode) {
      case FULL -> iterActor = graph.getAllExecutableActors();
      case CPN -> iterActor = computeCPN();
      case TIME -> iterActor = graph.getAllExecutableActors();
      case HYBRID -> iterActor = computeCPN();
      default -> throw new PreesmRuntimeException("Unrecognized Refine mode.");

    }

    for (final AbstractActor actor : iterActor) {
      splitfunc = new StringBuilder();
      splitHeader = new StringBuilder();
      forLoop = "";
      iteratorLoop = 0;
      splitActors = new ArrayList<>();
      timings = new ArrayList<>();
      bufferIterator = "";
      parseActorRefinement(actor);
      if (iteratorLoop > 1) {

        if (Boolean.TRUE.equals(!(md5)) || new ExecutionCheck(directory, projectRootPath, functionContent, actor,
            splitfunc, splitHeader, timings, bufferIterator, forLoop).execute()) {
          // PreesmIOHelper.getInstance().deleteFile(directory + "main_testMD5.c");
          PreesmIOHelper.getInstance().print(directory, "code_fine.c", splitfunc);
          PreesmIOHelper.getInstance().print(directory.replace("src", "include"), "code_fine.h", splitHeader);
          graphTransform(actor, graph, iteratorLoop, scenario);
          updateTiming(actor, scenario, graph, refineMode.equals(RefineMode.TIME));
          // break;
        }
      }
    }

    // Check consistency
    final Map<AbstractVertex, Long> rv = PiBRV.compute(graph, BRVMethod.LCM);
    PiBRV.printRV(rv);
    final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ANALYSIS,
        CheckerErrorLevel.NONE);
    pgcc.check(graph);
    if (!graph.getAllChildrenGraphs().isEmpty()) {
      graph = PiSDFFlattener.flatten(graph, false);
    }
    // scenarioExporter(scenario, projectFullPath);

    final Map<String, Object> output = new HashMap<>();
    output.put("PiMM", graph);
    output.put("scenario", scenario);
    return output;

  }

  private void scenarioExporter(Scenario subScenario, String path) {

    final ScenariosGenerator s = new ScenariosGenerator(iproject(path));
    final IFolder scenarioDir = iproject(path).getFolder("Scenarios/generated");
    final Set<Scenario> scenarios = new HashSet<>();
    scenarios.add(subScenario);
    try {
      s.saveScenarios(scenarios, scenarioDir);
    } catch (final CoreException e) {
      PreesmLogger.getLogger().log(Level.SEVERE, () -> "Error occurred during file generation: " + e.getMessage());
    }
    PreesmLogger.getLogger().log(Level.INFO, () -> "scenario print in : " + path);

  }

  public static IProject iproject(String path) {
    final IPath fromPortableString = Path.fromPortableString(path);
    final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(fromPortableString);
    return file.getProject();
  }

  private void updateTiming(AbstractActor actor, Scenario scenario, PiGraph graph, boolean b) {
    final Design archi = scenario.getDesign();
    // final AbstractActor init_actor = graph.getAllActors().stream()
    // .filter(x -> x.getName().equals(actor.getName() + "_init")).findFirst().get();
    // final EMap<Component, EMap<TimingType, String>> componentMap = new BasicEMap<>();
    // for (final Component component : archi.getProcessingElements()) {
    // final EMap<TimingType, String> timingMap = new BasicEMap<>();
    // timingMap.put(TimingType.EXECUTION_TIME, String.valueOf(2));
    // componentMap.put(component, timingMap);
    // }
    // // Utiliser le constructeur fourni par BasicEMap pour créer une entrée
    // final BasicEMap<AbstractActor, EMap<Component, EMap<TimingType, String>>> actorTimingsMap = new BasicEMap<>();
    // actorTimingsMap.put(init_actor, componentMap);
    //
    // scenario.getTimings().getActorTimings().addAll(actorTimingsMap.entrySet());
    for (final Component opId : archi.getProcessingElements()) {
      final String tim = scenario.getTimings().getTiming(actor, opId, TimingType.EXECUTION_TIME);
      final AbstractActor init_actor = graph.getAllActors().stream()
          .filter(x -> x.getName().equals(actor.getName() + "_init")).findFirst().get();
      final double ratio1 = b == true ? timings.get(1) / timings.get(0) : 20.0 / 100.0;
      final Long tim_init = (long) (Long.decode(tim) * ratio1);
      // final EMap<TimingType, String> timingsMap = new BasicEMap<>();
      // timingsMap.put(TimingType.EXECUTION_TIME, String.valueOf(tim_init));

      scenario.getTimings().setTiming(init_actor, opId, TimingType.EXECUTION_TIME, String.valueOf(tim_init));
      // final EMap<Component, EMap<TimingType, String>> actorTimingsMap = new BasicEMap<>();
      // actorTimingsMap.put(opId, timingsMap);
      //
      // if (scenario.getTimings().getActorTimings().containsKey(init_actor)) {
      // scenario.getTimings().getActorTimings().get(init_actor).put(opId, timingsMap);
      // } else {
      // scenario.getTimings().getActorTimings().put(actor, actorTimingsMap);
      // }
      PreesmLogger.getLogger().log(Level.INFO, () -> "init actor timing : " + tim_init);

      final AbstractActor loop_actor = graph.getAllActors().stream()
          .filter(x -> x.getName().equals(actor.getName() + "_loop")).findFirst().get();
      final double ratio2 = b == true ? timings.get(2) / timings.get(0) : 70.0 / 100.0 / iteratorLoop;
      final Long tim_loop = (long) (Long.decode(tim) * ratio2);
      scenario.getTimings().setTiming(loop_actor, opId, TimingType.EXECUTION_TIME, String.valueOf(tim_loop));
      PreesmLogger.getLogger().log(Level.INFO, () -> "loop actor timing : " + tim_loop);

      final AbstractActor end_actor = graph.getAllActors().stream()
          .filter(x -> x.getName().equals(actor.getName() + "_end")).findFirst().get();
      final double ratio3 = b == true ? timings.get(3) / timings.get(0) : 10.0 / 100.0;
      final Long tim_end = (long) (Long.decode(tim) * ratio3);
      scenario.getTimings().setTiming(end_actor, opId, TimingType.EXECUTION_TIME, String.valueOf(tim_end));
      PreesmLogger.getLogger().log(Level.INFO, () -> "end actor timing : " + tim_end);

    }
    final int i = 0;

  }

  private void parseActorRefinement(AbstractActor actor) {
    final Refinement refinement = ((Actor) actor).getRefinement();
    filePath = refinement.getFilePath().replace("include", "src").replace(".h", ".c");
    final int lastSeparatorIndex = filePath.lastIndexOf('/');
    directory = filePath.substring(0, lastSeparatorIndex) + "/"; // Chemin du répertoire
    final String fileName = filePath.substring(lastSeparatorIndex + 1); // Nom du fichier

    // extract full source file with multiple func
    final String content = PreesmIOHelper.getInstance().read(directory, fileName);
    // prototype to extract
    final CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) (refinement);
    final FunctionPrototype loopprototype = cHeaderRefinement.getLoopPrototype();
    final String functionPattern = "void " + loopprototype.getName() + "\\s*\\([^)]*\\)\\s*\\{";
    functionContent = extractFunction(content, functionPattern);

    // Structures pour stocker les résultats
    final List<String> outBuffers = new ArrayList<>();
    final List<String> inBuffers = new ArrayList<>();
    final List<String> cfgParam = new ArrayList<>();

    for (final FunctionArgument arg : loopprototype.getInputConfigParameters()) {
      cfgParam.add(arg.getType() + " " + arg.getName());
    }
    for (final FunctionArgument arg : loopprototype.getInputArguments()) {
      inBuffers.add(arg.getType() + " *" + arg.getName());
    }
    for (final FunctionArgument arg : loopprototype.getOutputArguments()) {
      outBuffers.add(arg.getType() + " *" + arg.getName());
    }
    final String patternFor = "(?s)\\{(.*?)for\\s*\\(([^;]+);([^;]+);([^\\)]+)\\)\\s*\\{(.*?)\\}(.*)\\}";
    // Compiler le motif regex
    final Pattern regex = Pattern.compile(patternFor);
    final Matcher matcher2 = regex.matcher(functionContent);

    iteratorLoop = 0;
    // Vérifier si une correspondance est trouvée
    if (matcher2.find()) {
      // Initialisation, condition, mise à jour
      final String initialization = matcher2.group(2).trim();
      final String condition = matcher2.group(3).trim();
      final String update = matcher2.group(4).trim();
      System.out.println("Initialization: " + initialization);
      System.out.println("Condition: " + condition);
      System.out.println("Update: " + update);
      // Extraire et convertir les valeurs
      iteratorLoop = parseLoop(initialization, condition, update);
      forLoop = "for(" + initialization + ";" + condition + ";" + update + "){";
    }

    // Vérifier si une correspondance est trouvée
    if (iteratorLoop > 1) {
      // Regex pour capturer les parties avant, dans et après la boucle
      final String patternFor2 = "for\\s*\\(([^;]+);([^;]+);([^\\)]+)\\)\\s*\\{";
      //

      String loop = extractFunction(functionContent, patternFor2);
      System.out.println("Loop: " + loop);
      final String[] splitLoop = functionContent.split(Pattern.quote(loop));
      loop = loop.substring(loop.indexOf('\n') + 1);
      loop = loop.substring(0, loop.lastIndexOf('}'));
      String beforeLoop = splitLoop[0];
      beforeLoop = beforeLoop.substring(beforeLoop.indexOf('\n') + 1);

      String afterLoop = splitLoop[1];
      afterLoop = afterLoop.substring(0, afterLoop.lastIndexOf('}'));

      final String buffers = String.join(",", cfgParam) + (cfgParam.size() > 0 ? ", " : "")
          + String.join(",", inBuffers) + (inBuffers.size() > 0 && outBuffers.size() > 0 ? ", " : "")
          + String.join(",", outBuffers);

      // buffers.replaceFirst(",", "");
      splitfunc.append("void " + loopprototype.getName() + "_init(" + String.join(",", buffers) + "){\n");
      splitfunc.append(beforeLoop);
      splitfunc.append("\n}\n");

      final String buffers2 = buffers + "," + inBuffers.stream().map(s -> s + "_out").collect(Collectors.joining(","))
          + (inBuffers.size() > 0 && outBuffers.size() > 0 ? ", " : "")
          + outBuffers.stream().map(s -> s + "_out").collect(Collectors.joining(","));

      splitfunc.append("void " + loopprototype.getName() + "_loop(" + buffers2 + ",int " + bufferIterator + "){\n");
      splitfunc.append(loop);
      for (final String buff : inBuffers) {
        final String name = buff.split("\\*")[1];
        final String type = buff.split("\\*")[0];
        splitfunc.append("\nmemcpy(" + name + "_out," + name + ",sizeof(" + type + "));\n");
      }
      for (final String buff : outBuffers) {
        final String name = buff.split("\\*")[1];
        final String type = buff.split("\\*")[0];
        splitfunc.append("\nmemcpy(" + name + "_out," + name + ",sizeof(" + type + "));\n");
      }
      splitfunc.append("\n}\n");
      splitfunc.append("void " + loopprototype.getName() + "_end(" + buffers + "){\n");
      splitfunc.append(afterLoop);
      splitfunc.append("\n}\n");

      splitHeader.append("void " + loopprototype.getName() + "_init(" + String.join(",", buffers) + ");\n");
      splitHeader.append("void " + loopprototype.getName() + "_loop(" + buffers2 + ");\n");
      splitHeader.append("void " + loopprototype.getName() + "_end(" + buffers + ");\n");

    }
  }

  private String extractFunction(String content, String functionPattern) {

    final Pattern startPattern = Pattern.compile(functionPattern);
    final Matcher startMatcher = startPattern.matcher(content);

    if (startMatcher.find()) {
      final int startIndex = startMatcher.start(); // Début de la fonction
      int openBraces = 0;
      int i = startMatcher.end();

      // Parcourir le fichier pour gérer les accolades
      while (i < content.length()) {
        final char c = content.charAt(i);
        if (c == '{') {
          openBraces++;
        } else if (c == '}') {
          if (openBraces == 0) {
            return content.substring(startIndex, i + 1); // Fin de la fonction
          }
          openBraces--;
        }
        i++;
      }
      // Si aucune fin trouvée, renvoyer jusqu'à la fin du fichier
      return content.substring(startIndex);
    }
    return null; // Fonction non trouvée
  }

  private void graphTransform(AbstractActor actor, PiGraph graph, int iteratorLoop, Scenario scenario) {
    // create sub
    final PiGraph sub = PiMMUserFactory.instance.createPiGraph();
    sub.setName(actor.getName());

    sub.setUrl(graph.getUrl() + "/" + sub.getName() + ".pi");
    for (final DataInputPort din : actor.getDataInputPorts()) {
      final DataInputInterface inputInterface = PiMMUserFactory.instance.createDataInputInterface();
      inputInterface.setName(din.getName());
      inputInterface.getDataPort().setName(din.getName());
      sub.addActor(inputInterface);
      final DataInputPort inputPort = (DataInputPort) inputInterface.getGraphPort();
      inputPort.setName(din.getName());
      inputPort.setExpression(din.getExpression().evaluate());
      inputInterface.getDataPort().setExpression(din.getExpression().evaluate());

    }
    for (final DataOutputPort dout : actor.getDataOutputPorts()) {
      final DataOutputInterface outputInterface = PiMMUserFactory.instance.createDataOutputInterface();
      outputInterface.setName(dout.getName());
      outputInterface.getDataPort().setName(dout.getName());
      sub.addActor(outputInterface);
      final DataOutputPort outputPort = (DataOutputPort) outputInterface.getGraphPort();
      outputPort.setName(dout.getName());
      outputPort.setExpression(dout.getExpression().evaluate());
      outputInterface.getDataPort().setExpression(dout.getExpression().evaluate());
    }
    for (final ConfigInputPort cfg : actor.getConfigInputPorts()) {
      final ConfigInputInterface cfgInterface = PiMMUserFactory.instance.createConfigInputInterface();
      cfgInterface.setName(cfg.getName());
      sub.addConfigurable(cfgInterface);

    }

    graph.replaceActor(actor, sub);

    // add inner actors
    final AbstractActor init = PiMMUserFactory.instance.createActor();
    // init actor
    init.setName(actor.getName() + "_init");
    final Refinement refinement = PiMMUserFactory.instance.createCHeaderRefinement();
    refinement.setFilePath(directory + "code_fine.h");
    ((Actor) init).setRefinement(refinement);
    final FunctionPrototype functionPrototype = PiMMUserFactory.instance.createFunctionPrototype();
    ((CHeaderRefinement) refinement).setLoopPrototype(functionPrototype);
    functionPrototype.setName(init.getName());
    sub.addActor(init);
    for (final DataInputPort din : sub.getDataInputPorts()) {
      final DataInputPort pin = PiMMUserFactory.instance.createDataInputPort();
      pin.setName(din.getName());
      pin.setExpression(sub.getDataInputInterfaces().stream().filter(x -> x.getName().equals(pin.getName())).findFirst()
          .get().getGraphPort().getExpression().evaluate());
      init.getDataInputPorts().add(pin);
      final Fifo fifo = PiMMUserFactory.instance.createFifo(sub.getDataInputInterfaces().stream()
          .filter(x -> x.getName().equals(pin.getName())).findFirst().get().getDataOutputPorts().get(0), pin,
          din.getFifo().getType());
      fifo.setContainingGraph(sub);

    }
    for (final DataOutputPort dout : sub.getDataOutputPorts()) {
      final DataOutputPort pout = PiMMUserFactory.instance.createDataOutputPort();
      pout.setName(dout.getName());
      pout.setExpression(sub.getDataInterfaces().stream().filter(x -> x.getName().equals(pout.getName())).findFirst()
          .get().getGraphPort().getExpression().evaluate());
      init.getDataOutputPorts().add(pout);

    }
    // brd actor
    final List<AbstractActor> brds = new ArrayList<>();
    for (final DataOutputPort doutInit : init.getDataOutputPorts()) {
      final BroadcastActor brd = PiMMUserFactory.instance.createBroadcastActor();
      brd.setName("brd_" + actor.getName() + "_" + doutInit.getName());
      sub.addActor(brd);
      final DataInputPort pin = PiMMUserFactory.instance.createDataInputPort();
      pin.setName("in");
      pin.setExpression(sub.getDataInterfaces().stream().filter(x -> x.getName().equals(doutInit.getName())).findFirst()
          .get().getGraphPort().getExpression().evaluate());
      brd.getDataInputPorts().add(pin);
      final Fifo fifo = PiMMUserFactory.instance.createFifo(doutInit, pin, sub.getDataInterfaces().stream()
          .filter(x -> x.getName().equals(doutInit.getName())).findFirst().get().getGraphPort().getFifo().getType());
      fifo.setContainingGraph(sub);

      final DataOutputPort pout = PiMMUserFactory.instance.createDataOutputPort();
      pout.setName("out");
      pout.setExpression(pin.getExpression().evaluate() * iteratorLoop);
      brd.getDataOutputPorts().add(pout);
      brds.add(brd);
    }

    // loop actor
    final AbstractActor loop = PiMMUserFactory.instance.createActor();
    loop.setName(actor.getName() + "_loop");
    final Refinement refinement_loop = PiMMUserFactory.instance.createCHeaderRefinement();
    refinement_loop.setFilePath(directory + "code_fine.h");
    ((Actor) loop).setRefinement(refinement_loop);
    final FunctionPrototype functionPrototype_loop = PiMMUserFactory.instance.createFunctionPrototype();
    ((CHeaderRefinement) refinement_loop).setLoopPrototype(functionPrototype_loop);
    functionPrototype_loop.setName(loop.getName());
    sub.addActor(loop);
    for (final DataOutputPort doutInit : init.getDataOutputPorts()) {
      final DataInputPort pin = PiMMUserFactory.instance.createDataInputPort();
      pin.setName(doutInit.getName());
      pin.setExpression(doutInit.getExpression().evaluate());
      loop.getDataInputPorts().add(pin);
      final Fifo fifo = PiMMUserFactory.instance.createFifo(
          brds.stream().filter(x -> x.getDataInputPorts().get(0).getFifo().getSourcePort().equals(doutInit)).findFirst()
              .get().getDataOutputPorts().get(0),
          pin, sub.getDataOutputInterfaces().stream().filter(x -> x.getName().equals(doutInit.getName())).findFirst()
              .get().getGraphPort().getFifo().getType());
      fifo.setContainingGraph(sub);
    }
    for (final DataOutputPort dout : sub.getDataOutputPorts()) {
      final DataOutputPort pout = PiMMUserFactory.instance.createDataOutputPort();
      pout.setName(dout.getName() + "_out");
      pout.setExpression(dout.getExpression().evaluate());
      loop.getDataOutputPorts().add(pout);
    }
    // join actor
    final List<AbstractActor> jns = new ArrayList<>();
    for (final DataOutputPort doutLoop : loop.getDataOutputPorts()) {
      final JoinActor jn = PiMMUserFactory.instance.createJoinActor();
      jn.setName("jn_" + actor.getName() + "_" + doutLoop.getName());
      sub.addActor(jn);
      final DataInputPort pin = PiMMUserFactory.instance.createDataInputPort();
      pin.setName("in");
      pin.setExpression(doutLoop.getExpression().evaluate() * iteratorLoop);
      jn.getDataInputPorts().add(pin);
      final Fifo fifo = PiMMUserFactory.instance.createFifo(doutLoop, pin,
          sub.getDataOutputInterfaces().stream().filter(x -> x.getName().equals(doutLoop.getName().replace("_out", "")))
              .findFirst().get().getGraphPort().getFifo().getType());
      fifo.setContainingGraph(sub);

      final DataOutputPort pout = PiMMUserFactory.instance.createDataOutputPort();
      pout.setName("out");
      pout.setExpression(pin.getExpression().evaluate() / iteratorLoop);
      jn.getDataOutputPorts().add(pout);
      jns.add(jn);
    }
    // end actor
    final AbstractActor end = PiMMUserFactory.instance.createActor();
    end.setName(actor.getName() + "_end");
    final Refinement refinement_end = PiMMUserFactory.instance.createCHeaderRefinement();
    refinement_end.setFilePath(directory + "code_fine.h");
    ((Actor) end).setRefinement(refinement_end);
    final FunctionPrototype functionPrototype_end = PiMMUserFactory.instance.createFunctionPrototype();
    ((CHeaderRefinement) refinement_end).setLoopPrototype(functionPrototype_end);
    functionPrototype_end.setName(end.getName());
    sub.addActor(end);
    for (final DataOutputPort doutLoop : loop.getDataOutputPorts()) {
      final DataInputPort pin = PiMMUserFactory.instance.createDataInputPort();
      pin.setName(doutLoop.getName());
      pin.setExpression(doutLoop.getExpression().evaluate());
      end.getDataInputPorts().add(pin);
      final Fifo fifo = PiMMUserFactory.instance.createFifo(
          jns.stream().filter(x -> x.getDataInputPorts().get(0).getFifo().getSourcePort().equals(doutLoop)).findFirst()
              .get().getDataOutputPorts().get(0),
          pin,
          sub.getDataOutputInterfaces().stream().filter(x -> x.getName().equals(doutLoop.getName().replace("_out", "")))
              .findFirst().get().getGraphPort().getFifo().getType());
      fifo.setContainingGraph(sub);
    }
    for (final DataOutputPort dout : sub.getDataOutputPorts()) {
      final DataOutputPort pout = PiMMUserFactory.instance.createDataOutputPort();
      pout.setName(dout.getName());
      pout.setExpression(dout.getExpression().evaluate());
      end.getDataOutputPorts().add(pout);

      final Fifo fifo = PiMMUserFactory.instance.createFifo(pout,
          sub.getDataOutputInterfaces().stream().filter(x -> x.getName().equals(pout.getName())).findFirst().get()
              .getDataInputPorts().get(0),
          sub.getDataOutputInterfaces().stream().filter(x -> x.getName().equals(pout.getName())).findFirst().get()
              .getGraphPort().getFifo().getType());
      fifo.setContainingGraph(sub);
    }
    splitActors.add(init);
    splitActors.add(loop);
    splitActors.add(end);
    lastLevelScenario(sub, scenario);

    final Map<AbstractVertex, Long> rv = PiBRV.compute(sub, BRVMethod.LCM);
    // PiBRV.printRV(rv);
  }

  private static void lastLevelScenario(PiGraph subgraph, Scenario scenario) {
    final Design archi = scenario.getDesign();

    final List<ComponentInstance> coreIds = new ArrayList<>(archi.getOperatorComponentInstances());
    // for all different type of cores, allow mapping on it
    for (final ComponentInstance coreId : coreIds) {
      for (final AbstractActor actor : subgraph.getAllActors()) {
        // Add constraint
        scenario.getConstraints().addConstraint(coreId, actor);
      }
    }
  }

  private List<AbstractActor> computeCPN() {
    // TODO Auto-generated method stub
    return null;
  }

  private static int parseLoop(String initialization, String condition, String update) {
    // Extraction de l'itérateur et de ses bornes
    final String iterator = initialization.split("=")[0].trim();
    String startValue = initialization.split("=")[1].trim();
    for (final org.preesm.model.pisdf.Parameter param : graphParameters) {
      if (startValue.contains(param.getName())) {
        startValue = startValue.replace(param.getName(), String.valueOf(param.getValueExpression().evaluate()));
      }
    }
    final int startValue2 = Integer.parseInt(startValue);
    final String condition2 = "";
    for (final org.preesm.model.pisdf.Parameter param : graphParameters) {
      if (condition.contains(param.getName())) {
        condition = condition.replace(param.getName(), String.valueOf(param.getValueExpression().evaluate()));
      }

    }
    int endValue;

    endValue = evaluateAfterChevron(condition);

    final int step = update.contains("++") ? 1 : -1; // Gestion de i++, i--
    bufferIterator = iterator.replace("int ", "");
    System.out.println("Iterator: " + iterator);
    System.out.println("Start Value: " + startValue2);
    System.out.println("End Value: " + endValue);
    System.out.println("Step: " + step);
    return endValue;
  }

  private static int evaluateAfterChevron(String input) {
    // Trouver la position du chevron '<'
    final int index = input.indexOf('<');

    if (index != -1 && index + 1 < input.length()) {
      // Extraire la partie après le chevron
      final String expression = input.substring(index + 1).trim();
      // Évaluer l'expression
      return evaluateExpression(expression);
    }
    return 0;
  }

  private static int evaluateExpression(String expression) {
    // Supprimer les espaces inutiles
    expression = expression.replaceAll("\\s+", "");

    // Pile pour les opérandes et les opérateurs
    final Stack<Integer> operands = new Stack<>();
    final Stack<Character> operators = new Stack<>();

    int i = 0;
    while (i < expression.length()) {
      final char c = expression.charAt(i);

      // Si c'est un nombre, parse et empile
      if (Character.isDigit(c)) {
        int num = 0;
        while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
          num = num * 10 + (expression.charAt(i) - '0');
          i++;
        }
        operands.push(num);
        continue;
      }

      // Si c'est un opérateur (+, -, *, /), empile
      if (c == '+' || c == '-' || c == '*' || c == '/') {
        while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
          compute(operands, operators);
        }
        operators.push(c);
      }
      i++;
    }

    // Effectuer les calculs restants
    while (!operators.isEmpty()) {
      compute(operands, operators);
    }

    // Le résultat final
    return operands.pop();
  }

  private static void compute(Stack<Integer> operands, Stack<Character> operators) {
    final int b = operands.pop();
    final int a = operands.pop();
    final char operator = operators.pop();

    switch (operator) {
      case '+':
        operands.push(a + b);
        break;
      case '-':
        operands.push(a - b);
        break;
      case '*':
        operands.push(a * b);
        break;
      case '/':
        operands.push(a / b);
        break;
      default:
        break;
    }

  }

  // Priorité des opérateurs
  public static int precedence(char operator) {
    if (operator == '+' || operator == '-') {
      return 1;
    }
    if (operator == '*' || operator == '/') {
      return 2;
    }
    return 0;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(MODE_PARAM, MODE_DEFAULT);
    parameters.put(MD5_PARAM, MD5_DEFAULT);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of Auto-Refining Task ";
  }

}
