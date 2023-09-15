package org.preesm.algorithm.node.partitioner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EMap;
import org.preesm.commons.graph.Vertex;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.generator.ScenariosGenerator;
import org.preesm.model.scenario.util.ScenarioUserFactory;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.TimingType;
import org.preesm.model.slam.generator.ArchitecturesGenerator;

public class NodePartitioner {
  /**
   * Input graph.
   */
  private final PiGraph  graph;
  /**
   * Workflow scenario.
   */
  private final Scenario scenario;
  /**
   * Architecture design.
   */
  private final Design   archi;

  private final String                         archicsvpath;
  private final String                         workloadpath;
  private final Map<Long, Map<Long, Long>>     archiH;       // id node/id core/freq
  private final Map<Long, Long>                archiEq;      // id node/nb core
  private Long                                 totArchiEq;
  private final Map<Long, Long>                timeEq;       // id node/cumulative time
  private final Map<Long, Long>                load;         // id node/exceed
  private Map<AbstractVertex, Long>            brv;          // actor/brv
  private final Map<Long, List<AbstractActor>> topoOrderASAP;// id rank/actor
  private final Map<Long, String>              nodeNames;

  private List<PiGraph>      subs      = null;
  private final List<Design> archiList = new ArrayList<>();

  static String                       fileError         = "Error occurred during file generation: ";
  private String                      graphPath         = "";
  private String                      archiPath         = "";
  private String                      scenariiPath      = "";
  private String                      includePath       = "";
  private final String                workspaceLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation()
      .toString();
  Map<Long, Map<AbstractActor, Long>> subsCopy          = new HashMap<>();
  private final PiGraph               topGraph          = null;

  public NodePartitioner(PiGraph graph, Scenario scenario, Design archi, String archicsvpath, String workloadpath) {
    this.graph = graph;
    this.scenario = scenario;
    this.archi = archi;
    this.archicsvpath = archicsvpath;
    this.workloadpath = workloadpath;
    this.archiH = new HashMap<>();
    this.archiEq = new HashMap<>();
    this.totArchiEq = 0L;
    this.timeEq = new HashMap<>();
    this.load = new HashMap<>();
    this.brv = new HashMap<>();
    this.topoOrderASAP = new HashMap<>();
    this.nodeNames = new HashMap<>();

  }

  public PiGraph execute() {
    final String[] uriString = graph.getUrl().split("/");
    graphPath = File.separator + uriString[1] + File.separator + uriString[2] + "/generated/";
    scenariiPath = File.separator + uriString[1] + "/Scenarios/generated/";
    archiPath = File.separator + uriString[1] + "/Archi/";
    includePath = uriString[1] + "/Code/include/";
    // 0. check level
    if (!graph.getAllChildrenGraphs().isEmpty()) {
      PreesmLogger.getLogger().log(Level.INFO, "Hierarchical graphs are not handle yet, please feed a flat version");
    }

    // 1. compute the number of equivalent core
    computeEqCore();
    if (graph.getActorIndex() < totArchiEq) {
      final String issue = "O(G_app)<O(G_archi) SimSDP 1.0 isn't appropriated (reduce archi or change method)";
      PreesmLogger.getLogger().log(Level.INFO, issue);
    }
    // 2. compute cumulative equivalent time
    brv = PiBRV.compute(graph, BRVMethod.LCM);// test
    computeWorkload();
    computeEqTime();
    // 3. sort actor in topological as soon as possible order
    computeTopoASAP();
    // homogeneous transform (if delay create sub of cycle path)
    // 4. identifies the actors who will form the sub
    this.subs = new IntranodeBuilder(scenario, brv, timeEq, archiList).execute();
    // 5. construct subs
    // constructSubs();
    // 7. construct top
    constructTop();
    // 9. generate main file
    new CodegenSimSDP(scenario, topGraph, nodeNames);

    return null;

  }

  /**
   * Read a CSV file containing implementation length on each node compute the average implementation length for each
   * node compute the excess/!excess file a structure load
   */
  private void computeWorkload() {
    // 1. read file
    if (!workloadpath.isEmpty()) {
      final File file = new File(workloadpath);
      final Map<Long, Long> wl = new HashMap<>();
      try {
        final FileReader read = new FileReader(file);
        final BufferedReader buffer = new BufferedReader(read);
        try {
          String line;
          while ((line = buffer.readLine()) != null) {
            final String[] split = line.split(";");
            final Long node = Long.valueOf(split[0]);
            final Long workload = Long.valueOf(split[1]);
            wl.put(node, workload);
          }
        } finally {
          buffer.close();
        }
      } catch (final IOException e) {
        final String errorMessage = fileError + workloadpath;
        PreesmLogger.getLogger().log(Level.INFO, errorMessage);
      }
      // compute average workload
      Long average = 0L;
      for (Long i = 0L; i < wl.size(); i++) {
        average = average + wl.get(i) / wl.size();
      }
      for (Long i = 0L; i < wl.size(); i++) {
        load.put(i, wl.get(i) - average);
      }
    }

  }

  private void constructTop() {
    // 1. replace by an empty actor
    topGraph.setName("top");
    final int nodeIndex = emptyTop();
    // 2. insert delay
    pipelineTop();

    // remove extra parameter
    for (final AbstractActor a : topGraph.getExecutableActors()) {
      final List<String> cfgOccur = new ArrayList<>();
      for (int i = 0; i < a.getConfigInputPorts().size(); i++) {
        a.getConfigInputPorts().get(i)
            .setName(((AbstractVertex) a.getConfigInputPorts().get(i).getIncomingDependency().getSetter()).getName());
        final String name = a.getConfigInputPorts().get(i).getName();

        if (cfgOccur.contains(a.getConfigInputPorts().get(i).getName())) {
          topGraph.removeDependency(a.getConfigInputPorts().get(i).getIncomingDependency());
          a.getConfigInputPorts().remove(a.getConfigInputPorts().get(i));
          i--;
        }
        cfgOccur.add(name);
      }

    }
    topGraph.getAllParameters().stream().filter(x -> !x.getContainingPiGraph().equals(topGraph))
        .forEach(x -> topGraph.removeParameter(x));
    for (final Parameter param : topGraph.getAllParameters()) {
      for (final Dependency element : param.getOutgoingDependencies()) {
        if (element.getContainingGraph() != topGraph) {
          element.setContainingGraph(topGraph);
        }
      }
    }
    for (final Dependency i : topGraph.getAllDependencies()) {

      final boolean getterContained = i.getGetter().getConfigurable() != null;
      if (!getterContained) {
        i.getSetter().getOutgoingDependencies().remove(i);
        topGraph.removeDependency(i);
      }
    }

    // 3. export graph
    PiBRV.compute(topGraph, BRVMethod.LCM);
    graphExporter(topGraph);
    final IPath fromPortableString = Path.fromPortableString(scenariiPath);
    final IFile file2 = ResourcesPlugin.getWorkspace().getRoot().getFile(fromPortableString);
    final IProject iProject = file2.getProject();
    final ArchitecturesGenerator a = new ArchitecturesGenerator(iProject);
    final Design topArchi = ArchitecturesGenerator.generateArchitecture(nodeIndex, "top");
    a.saveArchitecture(topArchi);
    topArchi.setUrl(archiPath + "top.slam");
    // 4. generate scenario
    final Scenario subScenario = ScenarioUserFactory.createScenario();
    subScenario.setAlgorithm(topGraph);
    subScenario.setDesign(topArchi);
    final String codegenpath = scenario.getCodegenDirectory();
    subScenario.setCodegenDirectory(codegenpath + "/top");
    scenarioExporter(subScenario);

  }

  private void pipelineTop() {
    int index = 0;
    for (final Fifo f : topGraph.getFifos()) {
      final Delay d = PiMMUserFactory.instance.createDelay();
      d.setName(((AbstractActor) f.getSource()).getName() + "_out_" + ((AbstractActor) f.getTarget()).getName() + "_in_"
          + index);
      d.setLevel(PersistenceLevel.PERMANENT);
      d.setExpression(f.getSourcePort().getExpression().evaluate());
      d.setContainingGraph(f.getContainingGraph());
      f.assignDelay(d);
      d.getActor().setContainingGraph(f.getContainingGraph());
      index++;
    }
  }

  private int emptyTop() {
    int nodeIndex = 0;
    topGraph.setUrl(graphPath + topGraph.getName() + ".pi");
    for (final AbstractActor pi : topGraph.getActors()) {
      if (pi instanceof PiGraph) {
        final Actor aEmpty = PiMMUserFactory.instance.createActor();
        aEmpty.setName(pi.getName());
        for (int i = 0; i < pi.getDataInputPorts().size(); i++) {
          final DataInputPort inputPort = PiMMUserFactory.instance.copy(pi.getDataInputPorts().get(i));
          aEmpty.getDataInputPorts().add(inputPort);
        }
        for (int i = 0; i < pi.getDataOutputPorts().size(); i++) {
          final DataOutputPort outputPort = PiMMUserFactory.instance.copy(pi.getDataOutputPorts().get(i));
          aEmpty.getDataOutputPorts().add(outputPort);
        }
        for (int i = 0; i < pi.getConfigInputPorts().size(); i++) {
          final ConfigInputPort cfgInputPort = PiMMUserFactory.instance.copy(pi.getConfigInputPorts().get(i));
          aEmpty.getConfigInputPorts().add(cfgInputPort);
        }
        topGraph.replaceActor(pi, aEmpty);
        nodeIndex++;
      }
    }
    return nodeIndex;
  }

  private void computeTopoASAP() {
    final List<AbstractActor> temp = new ArrayList<>();
    final List<AbstractActor> entry = new ArrayList<>();
    Long rank = 0L;
    for (final AbstractActor a : graph.getActors()) {
      temp.add(a);
    }
    // feed the 1st rank
    for (final AbstractActor a : graph.getActors()) {
      if (a.getDataInputPorts().isEmpty()) {
        entry.add(a);
        temp.remove(a);
      }
    }
    topoOrderASAP.put(rank, entry);
    // feed the rest
    while (!temp.isEmpty()) {
      final List<AbstractActor> list = new ArrayList<>();
      for (final AbstractActor a : topoOrderASAP.get(rank)) {
        for (final Vertex aa : a.getDirectSuccessors()) {
          // this is piece of art, don't remove
          final Long rankMatch = rank + 1;
          if (aa.getDirectPredecessors().stream().filter(x -> x instanceof Actor || x instanceof SpecialActor)
              .allMatch(x -> topoOrderASAP.entrySet().stream().filter(y -> y.getKey() < rankMatch)
                  .anyMatch(y -> y.getValue().contains(x)))
              && (!list.contains(aa))) {
            list.add((AbstractActor) aa);
            temp.remove(aa);

          }
        }
      }
      // orders the list in descending order of the execution time of the actors in the rank
      final List<AbstractActor> sortedList = new ArrayList<>(list);
      Collections.sort(sortedList, (actor1, actor2) -> {
        final double time1 = slowestTime(actor1);
        final double time2 = slowestTime(actor2);
        return Double.compare(time2, time1);
      });
      rank++;
      topoOrderASAP.put(rank, sortedList);
    }
  }

  private Long slowestTime(AbstractActor actor) {
    Long slow;
    if (scenario.getTimings().getActorTimings().get(actor) != null) {
      slow = Long
          .valueOf(scenario.getTimings().getActorTimings().get(actor).get(0).getValue().get(TimingType.EXECUTION_TIME));
      for (final Entry<Component, EMap<TimingType, String>> element : scenario.getTimings().getActorTimings()
          .get(actor)) {
        final Long timeSeek = Long.valueOf(element.getValue().get(TimingType.EXECUTION_TIME));
        if (timeSeek < slow) {
          slow = timeSeek;
        }
      }
    } else {
      slow = 100L;
    }
    return slow;
  }

  private void computeEqTime() {
    // total equivalent cumulative time
    Long totTCeq = 0L;
    for (final AbstractActor a : graph.getExecutableActors()) {
      if (a instanceof Actor) {
        Long slow;
        if (scenario.getTimings().getActorTimings().get(a) != null) {
          slow = Long
              .valueOf(scenario.getTimings().getActorTimings().get(a).get(0).getValue().get(TimingType.EXECUTION_TIME));
          for (final Entry<Component, EMap<TimingType, String>> element : scenario.getTimings().getActorTimings()
              .get(a)) {
            final Long timeSeek = Long.valueOf(element.getValue().get(TimingType.EXECUTION_TIME));
            if (timeSeek < slow) {
              slow = timeSeek;
            }
          }
        } else {
          slow = 100L;
        }
        totTCeq = slow * brv.get(a) + totTCeq;
      }
    }
    // construct structure
    for (long i = 0; i < archiEq.keySet().size(); i++) {
      final Long timeEqSeek = totTCeq * archiEq.get(i) / totArchiEq;
      timeEq.put(i, timeEqSeek);
    }
  }

  private void computeEqCore() {
    // Read temporary architecture file, extract composition and build structure
    // file -> |Node name|coreID|frequency|
    final File file = new File(archicsvpath);
    Long minFreq = Long.MAX_VALUE;// MHz
    try {
      final FileReader read = new FileReader(file);
      final BufferedReader buffer = new BufferedReader(read);
      long nodeID = 0L;
      String line;
      while ((line = buffer.readLine()) != null) {
        final String[] split = line.split(";");
        if (!archiH.isEmpty()) {

          if (!nodeNames.containsValue(split[0])) {
            nodeID++;
            final Long node = nodeID;
            final Long core = Long.valueOf(split[1]);
            final Long freq = Long.valueOf(split[2]);
            final Map<Long, Long> basis = new HashMap<>();
            basis.put(core, freq);
            archiH.put(node, basis);
            nodeNames.put(nodeID, split[0]);
            if (freq < minFreq) {
              minFreq = freq;
            }
          } else {
            final Long core = Long.valueOf(split[1]);
            final Long freq = Long.valueOf(split[2]);

            archiH.get(nodeID).put(core, freq);
            if (freq < minFreq) {
              minFreq = freq;
            }
          }
        } else {
          final Long node = nodeID;
          final Long core = Long.valueOf(split[1]);
          final Long freq = Long.valueOf(split[2]);
          final Map<Long, Long> basis = new HashMap<>();
          basis.put(core, freq);
          archiH.put(node, basis);
          nodeNames.put(nodeID, split[0]);
          if (freq < minFreq) {
            minFreq = freq;
          }
        }

      }
      buffer.close();
      read.close();
    } catch (final IOException e) {
      final String errorMessage = fileError + e.getMessage();
      PreesmLogger.getLogger().log(Level.INFO, errorMessage);
    }
    final Map<Long, Long> architemp = new HashMap<>();
    // construct equivalent archi structure
    for (Long i = 0L; i < archiH.keySet().size(); i++) {
      Long coreEq = 0L;
      for (final Entry<Long, Long> j : archiH.get(i).entrySet()) {
        final Long ratio = j.getValue() / minFreq;
        coreEq = coreEq + ratio;
      }

      architemp.put(i, coreEq);
      totArchiEq = totArchiEq + coreEq;
    }
    // sort in descending order of performance
    // first is the one with the max number of highest frequency

    // if homogeneous frequency first is the one with highest core

    // Ordonner les nœuds en fonction de archiEq, archiH et les critères donnés
    final List<Map.Entry<Long, Long>> nodeList = new ArrayList<>(architemp.entrySet());
    nodeList.sort((node1, node2) -> Long.compare(node2.getValue(), node1.getValue())); // Tri décroissant

    Long newIndex = 0L;
    for (final Map.Entry<Long, Long> entry : nodeList) {
      archiEq.put(newIndex++, entry.getValue());
      final IPath fromPortableString = Path.fromPortableString(scenariiPath);
      final IFile file2 = ResourcesPlugin.getWorkspace().getRoot().getFile(fromPortableString);
      final IProject iProject = file2.getProject();
      final ArchitecturesGenerator a = new ArchitecturesGenerator(iProject);
      final Design subArchi = ArchitecturesGenerator.generateArchitecture(entry.getValue().intValue(),
          "Node" + (newIndex - 1));
      a.saveArchitecture(subArchi);
      subArchi.setUrl(archiPath + "Node" + (newIndex - 1) + ".slam");
      archiList.add(subArchi);
      // a.generateAndSaveArchitecture(entry.getValue().intValue(),"Node"+(newIndex-1));

    }

  }

  private void graphExporter(PiGraph printgraph) {
    PiBRV.compute(printgraph, BRVMethod.LCM);
    final IPath fromPortableString = Path.fromPortableString(graphPath);
    final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(fromPortableString);
    final IProject iProject = file.getProject();
    // SavePiGraph.savePiGraphInFolder(iProject, fromPortableString, printgraph, "");

  }

  private void scenarioExporter(Scenario scenario) {
    final Set<Scenario> scenarios = new HashSet<>();
    scenarios.add(scenario);

    final IPath fromPortableString = Path.fromPortableString(scenariiPath);
    final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(fromPortableString);
    final IProject iProject = file.getProject();
    scenario.setScenarioURL(scenariiPath + scenario.getAlgorithm().getName() + ".scenario");
    final ScenariosGenerator s = new ScenariosGenerator(iProject);
    final IFolder scenarioDir = iProject.getFolder("Scenarios/generated");
    try {
      s.saveScenarios(scenarios, scenarioDir);
    } catch (final CoreException e) {
      final String errorMessage = fileError + e.getMessage();
      PreesmLogger.getLogger().log(Level.INFO, errorMessage);
    }
  }

  private void slamExporter(Design architecture) {
    final IPath fromPortableString = Path.fromPortableString(scenariiPath);
    final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(fromPortableString);
    final IProject iProject = file.getProject();
    final ArchitecturesGenerator a = new ArchitecturesGenerator(iProject);
    a.saveArchitecture(architecture);
    // a.generateAndSaveArchitecture(1);
  }

}
