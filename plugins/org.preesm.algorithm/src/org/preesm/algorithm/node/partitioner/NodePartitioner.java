package org.preesm.algorithm.node.partitioner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
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
  List<NodeMapping>    hierarchicalArchitecture;

  private final Map<Integer, Long>             timeEq;       // id node/cumulative time
  private final Map<Integer, Double>           load;         // id node/exceed
  private Map<AbstractVertex, Long>            brv;          // actor/brv
  private final Map<Long, List<AbstractActor>> topoOrderASAP;// id rank/actor
  private final Map<Long, String>              nodeNames;

  private final List<Design> archiList = new ArrayList<>();

  static String                       fileError    = "No such File find in folder: ";
  private String                      archiPath    = "";
  private String                      scenariiPath = "";
  Map<Long, Map<AbstractActor, Long>> subsCopy     = new HashMap<>();

  public NodePartitioner(Scenario scenario, String archicsvpath) {
    this.graph = scenario.getAlgorithm();
    this.scenario = scenario;
    this.archicsvpath = archicsvpath;

    this.timeEq = new HashMap<>();
    this.load = new HashMap<>();
    this.brv = new HashMap<>();
    this.topoOrderASAP = new HashMap<>();
    this.nodeNames = new HashMap<>();
    this.hierarchicalArchitecture = new ArrayList<>();

  }

  public PiGraph execute() {
    final String[] uriString = graph.getUrl().split("/");
    scenariiPath = "/" + uriString[1] + "/Scenarios/generated/";
    archiPath = "/" + uriString[1] + "/Archi/";
    // 0. check level
    if (!graph.getAllChildrenGraphs().isEmpty()) {
      PreesmLogger.getLogger().log(Level.INFO, "Hierarchical graphs are not handle yet, please feed a flat version");
    }

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
    final List<
        PiGraph> subs = new IntranodeBuilder(scenario, brv, timeEq, archiList, topoOrderASAP, hierarchicalArchitecture)
            .execute();
    // 7. construct top
    final PiGraph topGraph = new InternodeBuilder(scenario, subs, hierarchicalArchitecture).execute();
    // 9. generate main file
    new CodegenSimSDP(scenario, topGraph, nodeNames);

    return null;

  }

  private void exportArchitecture() {
    for (final NodeMapping node : hierarchicalArchitecture) {
      final IPath fromPortableString = Path.fromPortableString(scenariiPath);
      final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(fromPortableString);
      final IProject iProject = file.getProject();
      final ArchitecturesGenerator a = new ArchitecturesGenerator(iProject);
      final Map<String, Integer> type2nb = new HashMap<>();
      for (final CoreMapping core : node.getCores()) {
        if (type2nb.containsKey(core.getCoreType())) {
          type2nb.replace(core.getCoreType(), type2nb.get(core.getCoreType()), type2nb.get(core.getCoreType()) + 1);
        } else {
          type2nb.put(core.getCoreType(), 1);
        }
      }
      final Design subArchi = ArchitecturesGenerator.generateArchitecture(type2nb, "Node" + node.getID());

      subArchi.setUrl(archiPath + "Node" + node.getID() + ".slam");
      archiList.add(subArchi);
      a.saveArchitecture(subArchi);
      a.generateAndSaveArchitecture(type2nb, "Node" + node.getID());

    }
  }

  private int fillNodeMapping() {
    final String content = PreesmIOHelper.getInstance().read(archiPath, archicsvpath);
    final String[] line = content.split("[\n]");
    int coreID = 0;
    for (final String l : line) {
      final String[] element = l.split("[,]");
      final int lastIndex = hierarchicalArchitecture.size() - 1;

      if ((lastIndex == -1) || !element[0].equals(hierarchicalArchitecture.get(lastIndex).getNodeName())) {
        final NodeMapping newNode = MappingFactory.eINSTANCE.createNodeMapping();
        newNode.setNodeName(element[0]);
        newNode.setNodeMemcpySpeed(Double.valueOf(element[4]));
        final CoreMapping newCore = MappingFactory.eINSTANCE.createCoreMapping();
        newCore.setID(Integer.valueOf(element[1]));
        newCore.setCoreFrequency(Double.valueOf(element[2]));
        newCore.setCoreMemcpySpeed(Double.valueOf(element[3]));
        newCore.setID(coreID);
        newNode.getCores().add(newCore);
        hierarchicalArchitecture.add(newNode);
      } else {
        final CoreMapping newCore = MappingFactory.eINSTANCE.createCoreMapping();
        newCore.setID(Integer.valueOf(element[1]));
        newCore.setCoreFrequency(Double.valueOf(element[2]));
        newCore.setCoreMemcpySpeed(Double.valueOf(element[3]));
        newCore.setID(coreID);
        hierarchicalArchitecture.get(lastIndex).getCores().add(newCore);
      }
      coreID++;
    }
    // new NodeCompararator(coreID, hierarchicalArchitecture, graph);

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
    Collections.sort(hierarchicalArchitecture,
        (node1, node2) -> Integer.compare(node2.getNbCoreEquivalent(), node1.getNbCoreEquivalent()));
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
    // 1. read file
    final IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(scenariiPath + "workload.csv"));
    if (iFile.isAccessible()) {
      final String content = PreesmIOHelper.getInstance().read(scenariiPath, "workload.csv");
      final String[] line = content.split("\\n");
      for (final String element : line) {
        final String[] split = element.split(";");
        if (!split[0].equals("Nodes") && !split[0].equals("Latency") && !split[0].equals("SigmaW")) {
          final Integer node = Integer.valueOf(split[0].replace("node", ""));
          final Double workload = Double.valueOf(split[1]);
          load.put(node, workload);
        }
      }
    }

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

  private void computeEqTime(int sumNodeEquivalent) {
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
