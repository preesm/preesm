package org.preesm.algorithm.node.partitioner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

  private final Map<Long, Map<Long, Long>>     archiH;       // id node/id core/freq
  private final Map<Long, Long>                archiEq;      // id node/nb core
  private Long                                 totArchiEq;
  private final Map<Long, Long>                timeEq;       // id node/cumulative time
  private final Map<Long, Long>                load;         // id node/exceed
  private Map<AbstractVertex, Long>            brv;          // actor/brv
  private final Map<Long, List<AbstractActor>> topoOrderASAP;// id rank/actor
  private final Map<Long, String>              nodeNames;

  private final List<Design> archiList = new ArrayList<>();

  static String                       fileError    = "Error occurred during file generation: ";
  private String                      archiPath    = "";
  private String                      scenariiPath = "";
  private String                      workloadpath = "";
  Map<Long, Map<AbstractActor, Long>> subsCopy     = new HashMap<>();

  public NodePartitioner(Scenario scenario, String archicsvpath) {
    this.graph = scenario.getAlgorithm();
    this.scenario = scenario;
    this.archicsvpath = archicsvpath;
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
    scenariiPath = File.separator + uriString[1] + "/Scenarios/generated/";
    archiPath = File.separator + uriString[1] + "/Archi/";
    workloadpath = File.separator + uriString[1] + "/Scenarios/workload/";
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
    // 4. construct subGraphs
    final List<PiGraph> subs = new IntranodeBuilder(scenario, brv, timeEq, archiList, topoOrderASAP).execute();
    // 7. construct top
    final PiGraph topGraph = new InternodeBuilder(scenario, subs).execute();
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
      a.generateAndSaveArchitecture(entry.getValue().intValue(), "Node" + (newIndex - 1));

    }

  }

}
