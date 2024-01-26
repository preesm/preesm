package org.preesm.algorithm.node.partitioner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EMap;
import org.preesm.algorithm.mapping.model.CoreMapping;
import org.preesm.algorithm.mapping.model.MappingFactory;
import org.preesm.algorithm.mapping.model.NodeMapping;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.commons.graph.Vertex;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.scenario.Scenario;
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

  private final String archicsvpath;
  private final String partitioningMode;
  List<NodeMapping>    hierarchicalArchitecture;

  private final Map<Integer, Long>             timeEq;       // id node/cumulative time
  private final Map<Integer, Double>           load;         // id node/exceed
  private Map<AbstractVertex, Long>            brv;          // actor/brv
  private final Map<Long, List<AbstractActor>> topoOrderASAP;// id rank/actor
  private final Map<Long, String>              nodeNames;

  private final List<Design> archiList = new ArrayList<>();

  private String archiPath      = "";
  private String scenariiPath   = "";
  private String simulationPath = "";
  boolean        isHomogeneous  = true;

  public NodePartitioner(Scenario scenario, String archicsvpath, String partitioningMode) {
    this.graph = scenario.getAlgorithm();
    this.scenario = scenario;
    this.archicsvpath = archicsvpath;
    this.partitioningMode = partitioningMode;

    this.timeEq = new HashMap<>();
    this.load = new HashMap<>();
    this.brv = new HashMap<>();
    this.topoOrderASAP = new HashMap<>();
    this.nodeNames = new HashMap<>();
    this.hierarchicalArchitecture = new ArrayList<>();

  }

  public PiGraph execute() {

    // prevent wrong dependency assignment
    for (final Fifo fifo : graph.getAllFifos()) {
      final Long realExpressionIn = fifo.getSourcePort().getExpression().evaluate();
      fifo.getSourcePort().setExpression(realExpressionIn);
      final Long realExpressionOut = fifo.getTargetPort().getExpression().evaluate();
      fifo.getTargetPort().setExpression(realExpressionOut);
    }
    for (final Dependency dependencies : graph.getAllDependencies()) {

      if (!((Parameter) dependencies.getSetter()).getName().equals(dependencies.getGetter().getName())
          && (dependencies.getGetter() instanceof Parameter)) {

        final String message = "Parameter name:" + ((Parameter) dependencies.getSetter()).getName()
            + " should correspond to configuration port name" + dependencies.getGetter().getName()
            + ", and fit the function prototype";
        PreesmLogger.getLogger().log(Level.SEVERE, message);
      }
    }

    final String[] uriString = graph.getUrl().split("/");
    scenariiPath = "/" + uriString[1] + "/Scenarios/generated/";
    archiPath = "/" + uriString[1] + "/Archi/";
    simulationPath = "/" + uriString[1] + "/Simulation/";

    if (!scenario.getDesign().getProcessingElements().stream().allMatch(x -> x.getVlnv().getName().contains("_f"))) {
      PreesmLogger.getLogger().log(Level.SEVERE,
          "In order to handle heterogeneous core frequencies add _f[i] in the slam processing element definition");
    }
    // Filter hierarchy
    if (!graph.getAllChildrenGraphs().isEmpty()) {
      PreesmLogger.getLogger().log(Level.SEVERE, "Hierarchical graphs are not handle yet, please feed a flat version");
    }
    final PipelineCycleInfo pipelineCycleInfo = new PipelineCycleInfo(scenario);
    pipelineCycleInfo.execute();
    // filter pipeline
    if (!pipelineCycleInfo.getPipelineDelays().isEmpty()) {
      PreesmLogger.getLogger().log(Level.SEVERE,
          "SimSDP cannot compile if there are initial optimizations, please remove your pipelines");
    }

    pipelineCycleInfo.removeCycle();

    // 1. compute the number of equivalent core
    final int sumNodeEquivalent = fillNodeMapping();
    if (graph.getActorIndex() < sumNodeEquivalent) {
      final String issue = "O(G_app)<O(G_archi) SimSDP 1.0 isn't appropriated (reduce archi or change method)";
      PreesmLogger.getLogger().log(Level.INFO, issue);
    }
    exportArchitecture();
    // 2. compute cumulative equivalent time
    brv = PiBRV.compute(graph, BRVMethod.LCM);// test
    computeWorkload();
    computeEqTime(sumNodeEquivalent);
    // 3. sort actor in topological as soon as possible order

    computeTopoASAP();
    // 4. construct subGraphs
    final List<PiGraph> subs = new IntranodeBuilder(scenario, brv, timeEq, archiList, topoOrderASAP).execute();
    // 7. construct top
    final PiGraph topGraph = new InternodeBuilder(scenario, subs, hierarchicalArchitecture).execute();
    // 9. generate main file

    new CodegenSimSDP(scenario, topGraph, nodeNames, isHomogeneous);

    return topGraph;

  }

  private void exportArchitecture() {
    int coreIDStart = 0;
    for (final NodeMapping node : hierarchicalArchitecture) {

      final ArchitecturesGenerator a = new ArchitecturesGenerator(ScenarioBuilder.iproject(scenariiPath));
      final Map<String, Integer> type2nb = new HashMap<>();
      for (final CoreMapping core : node.getCores()) {
        if (type2nb.containsKey(core.getCoreType())) {
          type2nb.replace(core.getCoreType(), type2nb.get(core.getCoreType()), type2nb.get(core.getCoreType()) + 1);
        } else {
          type2nb.put(core.getCoreType(), 1);
        }
      }

      final Design subArchi = ArchitecturesGenerator.generateArchitecture(type2nb, "Node" + node.getID(),
          node.getCores().get(0).getCoreCommunicationRate(), coreIDStart);

      subArchi.setUrl(archiPath + "Node" + node.getID() + ".slam");
      archiList.add(subArchi);
      a.saveArchitecture(subArchi);
      a.generateAndSaveArchitecture(type2nb, "Node" + node.getID(), node.getCores().get(0).getCoreCommunicationRate(),
          coreIDStart);
      coreIDStart += node.getCores().size();
    }
  }

  private int fillNodeMapping() {
    final String content = PreesmIOHelper.getInstance().read(archiPath, archicsvpath);
    final String[] line = content.split("\n");
    if (!line[0].equals("Node name;Core ID;Core frequency;Intranode rate;Internode rate")) {
      PreesmLogger.getLogger().log(Level.SEVERE,
          "Missing the first line: Node name;Core ID;Core frequency;Intranode rate;Internode rate");
    }
    int coreID = 0;
    for (int i = 1; i < line.length; i++) {
      final String[] column = line[i].split(";");
      final int lastIndex = hierarchicalArchitecture.size() - 1;

      final CoreMapping newCore = MappingFactory.eINSTANCE.createCoreMapping();
      newCore.setID(Integer.valueOf(column[1]));
      newCore.setCoreFrequency(Double.valueOf(column[2]));
      newCore.setCoreCommunicationRate(Double.valueOf(column[3]));
      newCore.setID(coreID);

      if ((lastIndex == -1) || !column[0].equals(hierarchicalArchitecture.get(lastIndex).getNodeName())) {
        final NodeMapping newNode = MappingFactory.eINSTANCE.createNodeMapping();
        newNode.setNodeName(column[0]);
        newNode.setNodeCommunicationRate(Double.valueOf(column[4]));
        newNode.getCores().add(newCore);
        hierarchicalArchitecture.add(newNode);
      } else {
        hierarchicalArchitecture.get(lastIndex).getCores().add(newCore);
      }
      coreID++;
    }

    return computeEquivalentArchitecture();
  }

  private int computeEquivalentArchitecture() {
    Double minFreq = Double.MAX_VALUE;

    // compute Min frequency
    for (final NodeMapping node : hierarchicalArchitecture) {
      for (final CoreMapping core : node.getCores()) {
        minFreq = Math.min(core.getCoreFrequency(), minFreq);
      }
    }
    // compute ratio
    int sumNodeEquivalent = 0;
    for (final NodeMapping node : hierarchicalArchitecture) {
      int coreEquivalent = 0;
      for (final CoreMapping core : node.getCores()) {
        coreEquivalent = (int) (coreEquivalent + core.getCoreFrequency() / minFreq);
        core.setCoreType("x86_f" + Math.round(core.getCoreFrequency() / minFreq));
      }
      node.setNbCoreEquivalent(coreEquivalent);

      sumNodeEquivalent += coreEquivalent;
    }
    // Sort list by nbCoreEquivalent in descending order
    Collections.sort(hierarchicalArchitecture, (node1, node2) -> {
      final int comparisonResult = Integer.compare(node2.getNbCoreEquivalent(), node1.getNbCoreEquivalent());

      // If node1 is greater than node2, set isHomogeneous to false
      if (comparisonResult < 0) {
        isHomogeneous = false;
      }

      return comparisonResult;
    });
    // Assign ascending IDs based on sorting
    int rank = 0;
    for (final NodeMapping node : hierarchicalArchitecture) {
      node.setID(rank);
      rank++;
    }
    return sumNodeEquivalent;
  }

  /**
   * Read a CSV file containing implementation length on each node compute the average implementation length for each
   * node compute the excess/!excess file a structure load. Loads are in % format, current node load to maximum node
   * load.
   */
  private void computeWorkload() {
    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(simulationPath + "workload.csv"));
    if (iFile.isAccessible()) {
      final String content = PreesmIOHelper.getInstance().read(simulationPath, "workload.csv");
      final String[] lines = content.split("\n");
      for (final String line : lines) {
        final String[] columns = line.split(";");
        Double value;
        if (this.partitioningMode.equals("equivalentTimed")) {
          value = Double.valueOf(columns[1]);
        } else {
          final Random random = new Random();
          value = random.nextDouble();
        }

        load.put(Integer.valueOf(columns[0].replace("Node", "")), value);
      }
    }

  }

  private void computeTopoASAP() {
    // Utilisation d'une expression lambda pour filtrer les acteurs qui ne sont pas des instances de DelayActor
    final List<AbstractActor> fullList = graph.getActors().stream().filter(actor -> !(actor instanceof DelayActor))
        .collect(Collectors.toList());
    final List<AbstractActor> rankList = new ArrayList<>();
    Long rank = 0L;

    // feed the 1st rank
    for (final AbstractActor a : graph.getActors()) {
      if (!(a instanceof DelayActor) && (a.getDataInputPorts().isEmpty()
          || a.getDataInputPorts().stream().allMatch(x -> x.getFifo().isHasADelay()))) {
        rankList.add(a);
        fullList.remove(a);
      }
    }
    topoOrderASAP.put(rank, rankList);
    // feed the rest

    while (!fullList.isEmpty()) {
      final List<AbstractActor> list = new ArrayList<>();

      for (final AbstractActor a : topoOrderASAP.get(rank)) {
        processDirectSuccessors(a, rank, list, fullList);
      }

      rank++;
      topoOrderASAP.put(rank, orderRank(list));
    }
  }

  private void processDirectSuccessors(AbstractActor a, Long rank, List<AbstractActor> list,
      List<AbstractActor> fullList) {
    for (final Vertex aa : a.getDirectSuccessors()) {
      final Long rankMatch = rank + 1;

      if (isValidSuccessor(aa, rankMatch) && (!list.contains(aa))) {
        list.add((AbstractActor) aa);
        fullList.remove(aa);
      }
    }
  }

  private boolean isValidSuccessor(Vertex aa, Long rankMatch) {
    return aa.getDirectPredecessors().stream().filter(x -> x instanceof Actor || x instanceof SpecialActor)
        .allMatch(x -> isPredecessorInPreviousRanks(x, rankMatch));
  }

  private boolean isPredecessorInPreviousRanks(Vertex x, Long rankMatch) {
    return topoOrderASAP.entrySet().stream().filter(y -> y.getKey() < rankMatch)
        .anyMatch(y -> y.getValue().contains(x));
  }

  private List<AbstractActor> orderRank(List<AbstractActor> list) {
    // orders the list in descending order of the execution time of the actors in the rank
    final List<AbstractActor> sortedList = new ArrayList<>(list);
    Collections.sort(sortedList, (actor1, actor2) -> {
      final double time1 = slowestTime(actor1, scenario);
      final double time2 = slowestTime(actor2, scenario);
      return Double.compare(time2, time1);
    });
    return sortedList;
  }

  public static Long slowestTime(AbstractActor actor, Scenario scenario) {
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

  private void computeEqTime(int sumNodeEquivalent) {
    // total equivalent cumulative time
    Long totTCeq = 0L;
    for (final AbstractActor a : graph.getExecutableActors()) {
      if (a instanceof Actor) {
        totTCeq = slowestTime(a, scenario) * brv.get(a) + totTCeq;
      }
    }
    // construct structure

    for (final NodeMapping node : hierarchicalArchitecture) {

      final Long timeEqSeek = totTCeq * node.getNbCoreEquivalent() / sumNodeEquivalent;
      Long timEqSeekUpdate;
      if (!load.isEmpty()) {
        timEqSeekUpdate = Math.round(timeEqSeek * (1 - load.get(node.getID())));
      } else {
        timEqSeekUpdate = timeEqSeek;
      }
      timeEq.put(node.getID(), timEqSeekUpdate);
    }
  }

}
