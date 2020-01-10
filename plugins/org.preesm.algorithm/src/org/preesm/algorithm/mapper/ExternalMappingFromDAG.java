/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.algorithm.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.abc.taskscheduling.AbstractTaskSched;
import org.preesm.algorithm.mapper.algo.InitialLists;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.mapper.params.AbcParameters;
import org.preesm.algorithm.mapper.schedule.Architecture;
import org.preesm.algorithm.mapper.schedule.Schedule;
import org.preesm.algorithm.mapper.schedule.ScheduleEntry;
import org.preesm.algorithm.mapper.schedule.ScheduleUtils;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.algorithm.model.iterators.TopologicalDAGIterator;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.URLResolver;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.Operator;
import org.preesm.model.slam.VLNV;
import org.preesm.model.slam.utils.LexicographicComponentInstanceComparator;

/**
 * This class imports schedule expressed in dedicated json format. It is experimental and limited to flat PiMM and a few
 * architectures (regular x86 and Odroid). See package org.preesm.algorithm.mapper.schedule for the json format.
 *
 * @author ahonorat
 * @author anmorvan
 *
 */
@PreesmTask(id = "org.ietr.preesm.plugin.mapper.external", name = "External Scheduling from DAG",
    category = "Schedulers",

    description = "This class imports schedule expressed in dedicated json format. "
        + "It is experimental and limited to flat PiMM and a few architectures (regular x86 and Odroid)."
        + " See package org.preesm.algorithm.mapper.schedule for the json format.",

    inputs = { @Port(name = "DAG", type = DirectedAcyclicGraph.class),
        @Port(name = "architecture", type = Design.class), @Port(name = "scenario", type = Scenario.class) },

    outputs = { @Port(name = "DAG", type = DirectedAcyclicGraph.class), @Port(name = "ABC", type = LatencyAbc.class) },

    parameters = {
        @Parameter(name = "SCHEDULE_FILE", values = { @Value(name = "/schedule.json", effect = "default value") })

    })
public class ExternalMappingFromDAG extends AbstractMappingFromDAG {

  public static final String SCHEDULE_FILE = "SCHEDULE_FILE";

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> params = new LinkedHashMap<>();
    params.put(ExternalMappingFromDAG.SCHEDULE_FILE, "/schedule.json");
    return params;
  }

  @Override
  protected LatencyAbc schedule(final Map<String, Object> outputs, final Map<String, String> parameters,
      final InitialLists initial, final Scenario scenario, final AbcParameters abcParams, final MapperDAG dag,
      final Design architecture, final AbstractTaskSched taskSched) {

    // 1- read schedule file
    final String jsonScheduleFilePath = parameters.get(ExternalMappingFromDAG.SCHEDULE_FILE);
    final Schedule schedule = readSchedule(jsonScheduleFilePath);

    // 2- check
    final Map<DAGVertex, ScheduleEntry> entries = checkScheduleCompatibility(schedule, dag, architecture);

    checkStartTimePrecedences(entries);
    PreesmLogger.getLogger().log(Level.INFO, "Successfully parsed " + entries.size() + " schedule entries.");

    // 3- sort components to have a relation from ID to component
    final List<ComponentInstance> componentInstances = new ArrayList<>(architecture.getComponentInstances());
    Collections.sort(componentInstances, new LexicographicComponentInstanceComparator());

    PreesmLogger.getLogger().log(Level.INFO, "Order schedule vertices.");

    // 4- retrieve topological order of firings and source actors
    long nbActors = 0;
    final Map<DAGVertex, Long> actorsRank = new HashMap<>();
    final Set<DAGVertex> sourceActors = new HashSet<>();
    TopologicalDAGIterator topologicalDAGIterator = new TopologicalDAGIterator(dag);
    while (topologicalDAGIterator.hasNext()) {
      DAGVertex vertex = topologicalDAGIterator.next();
      if (vertex.incomingEdges().isEmpty()) {
        sourceActors.add(vertex);
      }
      actorsRank.put(vertex, nbActors);
      nbActors++;
    }

    // 5.1 - build unordered lists of vertices per core ID
    final Map<Integer, List<DAGVertex>> orderedVertices = new HashMap<>();
    Map<Integer, ComponentInstance> ciI = new HashMap<>();
    // assumes hardwareId goes from 0 to ...
    for (int i = 0; i < componentInstances.size(); i++) {
      final ComponentInstance componentInstance = componentInstances.get(i);
      if (componentInstance.getComponent() instanceof Operator) {
        int coreId = componentInstance.getHardwareId();
        PreesmLogger.getLogger().log(Level.INFO, "Adding available core operator with id: " + coreId);
        orderedVertices.put(coreId, new ArrayList<>());
        ciI.put(coreId, componentInstance);
      }
    }
    final Map<DAGVertex, Integer> vertexToCore = new HashMap<>();
    for (final ScheduleEntry e : schedule.getScheduleEntries()) {
      final Integer coreID = e.getCore();
      final String srActorName = e.getFiringName();
      if (coreID == null || coreID < 0) {
        final String message = "Schedule does not specifycorrect core ID for firing " + srActorName;
        throw new PreesmRuntimeException(message);
      }
      int offset = 0;
      final String processorName = e.getProcessingUnitName();
      // ugly hack to handle case of ARM bib.LITTLE with A7 cores numbered from 0 to 3 and A15 cores from 4 to 7
      // it should be addressed by analyzing all processor names first
      if (processorName.startsWith("A15")) {
        offset += 4;
      }
      final DAGVertex vertex = dag.getVertex(srActorName);
      vertexToCore.put(vertex, coreID + offset);
      final List<DAGVertex> list = orderedVertices.get(coreID + offset);
      list.add(vertex);
    }

    // 5.2 - insert missing nodes in the lists
    // (for instance implode/explode nodes if schedule has been done on flatten graph)
    final Set<DAGVertex> unmappedVertices = new HashSet<>(dag.vertexSet());
    unmappedVertices.removeAll(entries.keySet());
    final Set<DAGVertex> initVertices = new HashSet<>();
    final Set<DAGVertex> endVertices = new HashSet<>();

    Set<DAGVertex> visitedNodes = new HashSet<>();
    removeEndOrInit(unmappedVertices, initVertices, false);

    // inits
    while (!initVertices.isEmpty()) {
      Iterator<DAGVertex> itv = initVertices.iterator();
      DAGVertex vtx = itv.next();

      visitedNodes.clear();
      visitedNodes.add(vtx);
      DAGVertex associateVtx = ExternalMappingFromDAG.getAssociateVertex(vtx, Direction.NONE, unmappedVertices);
      if (unmappedVertices.contains(associateVtx)) {
        associateVtx = vtx;
        do {
          associateVtx = ExternalMappingFromDAG.getAssociateVertex(associateVtx, Direction.FORWARD, unmappedVertices);
          visitedNodes.add(associateVtx);
        } while (associateVtx != null && unmappedVertices.contains(associateVtx));
      }

      int coreId = vertexToCore.get(associateVtx).intValue();
      for (DAGVertex vertex : visitedNodes) {
        vertexToCore.put(vertex, coreId);
        unmappedVertices.remove(vertex);
      }
      initVertices.remove(vtx);
    }

    removeEndOrInit(unmappedVertices, endVertices, true);

    // ends
    for (final DAGVertex vtx : endVertices) {
      final String nodeKind = vtx.getKind();
      if (!entries.containsKey(vtx) && MapperDAGVertex.DAG_END_VERTEX.equals(nodeKind)) {
        final String oppositeINIT = vtx.getPropertyStringValue(MapperDAGVertex.END_REFERENCE);
        DAGVertex associateVtx = dag.getVertex(oppositeINIT);
        int coreId = vertexToCore.get(associateVtx);
        vertexToCore.put(vtx, coreId);
      }
    }

    // fork and joins
    while (!unmappedVertices.isEmpty()) {
      Iterator<DAGVertex> itv = unmappedVertices.iterator();
      DAGVertex vtx = itv.next();

      visitedNodes.clear();
      visitedNodes.add(vtx);
      DAGVertex associateVtx = ExternalMappingFromDAG.getAssociateVertex(vtx, Direction.NONE, unmappedVertices);
      if (unmappedVertices.contains(associateVtx)) {
        associateVtx = vtx;
        while (associateVtx != null && unmappedVertices.contains(associateVtx)) {
          associateVtx = ExternalMappingFromDAG.getAssociateVertex(associateVtx, Direction.BACKWARD, unmappedVertices);
          visitedNodes.add(associateVtx);
        }
      }

      int coreId = vertexToCore.get(associateVtx).intValue();
      for (DAGVertex vertex : visitedNodes) {
        vertexToCore.put(vertex, coreId);
        unmappedVertices.remove(vertex);
      }
    }

    PreesmLogger.getLogger().log(Level.INFO, "Starting mapping from external schedule.");

    final LatencyAbc abc = LatencyAbc.getInstance(abcParams, dag, architecture, scenario);
    // 6- sort vertex lists following start dates from the schedule entries and apply schedule

    for (Entry<Integer, ComponentInstance> e : ciI.entrySet()) {
      int i = e.getKey();
      final List<DAGVertex> list = orderedVertices.get(i);
      list.sort(new ScheduleComparator(entries, actorsRank));
    }

    final Map<DAGVertex, Integer> visitedInputsActors = new HashMap<>();
    final List<DAGVertex> readyToSchedule = new LinkedList<>(sourceActors);

    while (!readyToSchedule.isEmpty()) {
      DAGVertex firingToMap = null;
      ComponentInstance ci = null;
      for (Entry<Integer, ComponentInstance> e : ciI.entrySet()) {
        int i = e.getKey();
        final ComponentInstance componentInstance = e.getValue();
        final List<DAGVertex> list = orderedVertices.get(i);
        if (list.isEmpty()) {
          continue;
        }
        final DAGVertex firstOnList = list.get(0);
        if (readyToSchedule.contains(firstOnList)) {
          firingToMap = firstOnList;
          ci = componentInstance;
          list.remove(firstOnList);
          break;
        }
      }

      if (firingToMap == null) {
        // This means that it is a special actor not scheduled by IBM
        firingToMap = readyToSchedule.get(0);
        int coreId = vertexToCore.get(firingToMap);
        ci = ciI.get(coreId);
      }

      mapVertex(abc, ci, firingToMap);

      readyToSchedule.remove(firingToMap);
      // we add firings that are now ready to execute, and map them directly if not managed by external schedule
      addReadyFirings(firingToMap, entries, readyToSchedule, visitedInputsActors, abc, vertexToCore, ciI);

    }

    return abc;

  }

  private static void checkStartTimePrecedences(Map<DAGVertex, ScheduleEntry> entries) {
    for (Entry<DAGVertex, ScheduleEntry> e : entries.entrySet()) {
      // get all previous direct edges
      DAGVertex vtx = e.getKey();
      Set<DAGVertex> predecessorsWS = new HashSet<>();
      Set<DAGVertex> predecessorsWOS = new HashSet<>();
      Set<DAGVertex> visited = new HashSet<>();
      predecessorsWOS.add(vtx);
      visited.addAll(predecessorsWOS);
      while (!predecessorsWOS.isEmpty()) {
        Set<DAGVertex> newNodes = new HashSet<>();
        for (DAGVertex currentV : predecessorsWOS) {
          visited.add(currentV);
          for (DAGEdge edge : currentV.incomingEdges()) {
            DAGVertex src = edge.getSource();
            if (entries.containsKey(src)) {
              predecessorsWS.add(src);
            } else if (!visited.contains(src)) {
              newNodes.add(src);
            }
          }
        }
        predecessorsWOS = newNodes;

      }

      ScheduleEntry se = e.getValue();

      Integer core = se.getCore();
      Long start = se.getStart();
      for (DAGVertex vertex : predecessorsWS) {
        ScheduleEntry predSE = entries.get(vertex);
        Long predEnd = predSE.getEnd();
        Integer predCore = predSE.getCore();
        if (start < predEnd && predCore.equals(core)) {
          throw new PreesmRuntimeException("Start time of firing " + vtx.getName() + " and end time of "
              + vertex.getName() + " do not resepct their precedence order.");
        }
      }

    }
  }

  private static void mapVertex(final LatencyAbc abc, final ComponentInstance componentInstance, final DAGVertex v) {
    final MapperDAGVertex vertex = (MapperDAGVertex) v;
    if (abc.isMapable(vertex, componentInstance, false)) {
      abc.map(vertex, componentInstance, true, false);
    } else {
      final String message = "The schedule is invalid: vertex [" + vertex.getName()
          + "] cannot be mapped on component [" + componentInstance.getInstanceName() + "].";
      throw new PreesmRuntimeException(message);
    }
  }

  private static void addReadyFirings(DAGVertex firingToMap, Map<DAGVertex, ScheduleEntry> entries,
      List<DAGVertex> readyToSchedule, Map<DAGVertex, Integer> visitedInputsActors, LatencyAbc abc,
      Map<DAGVertex, Integer> vertexToCore, Map<Integer, ComponentInstance> ciI) {

    for (DAGEdge outEdge : firingToMap.outgoingEdges()) {
      DAGVertex dst = outEdge.getTarget();
      int visitedEdges = visitedInputsActors.getOrDefault(dst, 0) + 1;
      visitedInputsActors.put(dst, visitedEdges);
      if (visitedEdges == dst.incomingEdges().size()) {
        visitedInputsActors.remove(dst);
        if (entries.containsKey(dst)) {
          readyToSchedule.add(dst);
        } else {
          // we map directly the node
          int coreId = vertexToCore.get(dst);
          ComponentInstance ci = ciI.get(coreId);
          mapVertex(abc, ci, dst);
          addReadyFirings(dst, entries, readyToSchedule, visitedInputsActors, abc, vertexToCore, ciI);
        }
      }
    }

  }

  private static void removeEndOrInit(Set<DAGVertex> unscheduledVertices, Set<DAGVertex> removedVertices,
      boolean EndOrInit) {
    Iterator<DAGVertex> itv = unscheduledVertices.iterator();
    while (itv.hasNext()) {
      DAGVertex vtx = itv.next();
      final String nodeKind = vtx.getKind();

      if ((MapperDAGVertex.DAG_END_VERTEX.equals(nodeKind) && EndOrInit)
          || (MapperDAGVertex.DAG_INIT_VERTEX.equals(nodeKind) && !EndOrInit)) {
        removedVertices.add(vtx);
        itv.remove();
      }
    }

  }

  /**
   * Indicates research direction for associate vertex.
   * 
   * @author ahonorat
   *
   */
  private enum Direction {
    FORWARD, BACKWARD, NONE;
  }

  private static final DAGVertex getAssociateVertex(final DAGVertex vtx, Direction dir,
      final Set<DAGVertex> unmappedVertices) {
    Set<DAGEdge> edges;
    DAGVertex associateVtx = null;
    if (dir == Direction.NONE) {
      final String nodeKind = vtx.getKind();
      switch (nodeKind) {
        case MapperDAGVertex.DAG_END_VERTEX:
        case MapperDAGVertex.DAG_FORK_VERTEX:
          edges = vtx.incomingEdges();
          if (edges.size() != 1) {
            throw new PreesmRuntimeException("Fork node should have only one incomming edge");
          }
          final DAGEdge inEdge = edges.iterator().next();
          associateVtx = inEdge.getSource();
          break;
        case MapperDAGVertex.DAG_INIT_VERTEX:
        case MapperDAGVertex.DAG_JOIN_VERTEX:
          edges = vtx.outgoingEdges();
          if (edges.size() != 1) {
            throw new PreesmRuntimeException("Join node should have only one outgoing edge");
          }
          final DAGEdge outEdge = edges.iterator().next();
          associateVtx = outEdge.getTarget();
          break;
        default:
          throw new PreesmRuntimeException("Regular firings are missing. As: " + vtx.getName());
      }
    } else if (dir == Direction.FORWARD) {
      for (DAGEdge outEdge : vtx.outgoingEdges()) {
        DAGVertex tgt = outEdge.getTarget();
        if (tgt != vtx) {
          associateVtx = tgt;
          if (!unmappedVertices.contains(tgt)) {
            break;
          }
        }
      }
      if (associateVtx == null) {
        throw new PreesmRuntimeException(
            "Found a loop while looking forward for associate vertex of: " + vtx.getName());
      }
    } else if (dir == Direction.BACKWARD) {
      for (DAGEdge inEdge : vtx.incomingEdges()) {
        DAGVertex src = inEdge.getSource();
        if (src != vtx) {
          associateVtx = src;
          if (!unmappedVertices.contains(src)) {
            break;
          }
        }
      }
      if (associateVtx == null) {
        throw new PreesmRuntimeException(
            "Found a loop while looking backward for associate vertex of: " + vtx.getName());
      }
    }
    return associateVtx;
  }

  /**
   * Compare DGAVertex per name.
   * 
   * @author ahonorat
   */
  private static class DAGVertexNameComparator implements Comparator<DAGVertex> {

    @Override
    public int compare(DAGVertex arg0, DAGVertex arg1) {
      return arg0.getName().compareTo(arg1.getName());
    }

  }

  /**
   * Checks if schedule from the JSON file is compatible with algo and archi from the scenario.
   */
  private Map<DAGVertex, ScheduleEntry> checkScheduleCompatibility(final Schedule schedule, final MapperDAG dag,
      final Design architecture) {
    final Map<DAGVertex, ScheduleEntry> entries = new TreeMap<>(new DAGVertexNameComparator());
    // basic architecture comparison
    final Architecture scheduleArchi = schedule.getArchitecture();
    final VLNV designVlnv = architecture.getVlnv();
    final boolean sameArchiName = designVlnv.getName().equals(scheduleArchi.getUName());
    final boolean sameArchiVersion = designVlnv.getVersion().equals(scheduleArchi.getVersion());
    if (!sameArchiName || !sameArchiVersion) {
      final String message = "The input schedule architecture is not compatible with "
          + "the architecture specified in the scenario.";
      throw new PreesmRuntimeException(message);
    }

    // basic application comparison
    final String scheduleAppName = schedule.getApplicationName();
    final PiGraph referencePiMMGraph = dag.getReferencePiMMGraph();
    final String pisdfGraphName = referencePiMMGraph.getName();
    final boolean sameAppName = pisdfGraphName.equals(scheduleAppName);
    if (!sameAppName) {
      final String message = "The input schedule application is not compatible with "
          + "the application specified in the scenario.";
      throw new PreesmRuntimeException(message);
    }

    final List<ScheduleEntry> scheduleEntries = schedule.getScheduleEntries();

    final Set<String> scheduledDagVertexName = new HashSet<>();
    for (final ScheduleEntry scheduleEntry : scheduleEntries) {
      final DAGVertex vertex = getMapperDagActor(scheduleEntry.getActorName(),
          scheduleEntry.getSingleRateInstanceNumber(), dag);
      if (vertex == null) {
        final String message = "The schedule entry for single rate actor [" + scheduleEntry.getActorName() + "] "
            + "has no corresponding actor in the single rate graph.";
        throw new PreesmRuntimeException(message);
      } else {
        entries.put(vertex, scheduleEntry);
        scheduledDagVertexName.add(vertex.getName());
        scheduleEntry.setFiringName(vertex.getName());
      }
    }

    for (final AbstractActor actor : referencePiMMGraph.getActors()) {
      final String actorName = actor.getName();
      if (!scheduledDagVertexName.contains(actorName)) {
        final String msg = "Single Rate actor [" + actorName + "] has no schedule entry";
        PreesmLogger.getLogger().log(Level.FINER, msg);
      }
    }

    return entries;
  }

  private DAGVertex getMapperDagActor(String taskName, int nbInstance, MapperDAG dag) {
    DAGVertex res = null;
    for (DAGVertex vertex : dag.vertexSet()) {
      String kind = vertex.getKind();
      if (MapperDAGVertex.DAG_INIT_VERTEX.equalsIgnoreCase(kind)
          || MapperDAGVertex.DAG_END_VERTEX.equalsIgnoreCase(kind)) {
        continue;
      }
      String vname = vertex.getName();
      Pattern p = Pattern.compile("(\\S+)_(\\d+)");
      Matcher m = p.matcher(vname);
      String vshortName = null;
      String vnbInstance = null;
      while (m.find()) {
        vshortName = m.group(1);
        vnbInstance = m.group(2);
      }
      if (vshortName == null || vnbInstance == null) {
        PreesmLogger.getLogger().log(Level.FINER,
            "One SRDAG actor could not be parsed correctly: " + vname + " [" + kind);
      } else if (vshortName.equals(taskName)) {
        if (Integer.parseInt(vnbInstance) == nbInstance) {
          res = vertex;
          break;
        }
      }
    }

    return res;
  }

  private Schedule readSchedule(final String jsonScheduleProjectRelativeFilePath) {
    try {
      final String readURL = URLResolver.readURL(jsonScheduleProjectRelativeFilePath);
      return ScheduleUtils.parseJsonString(readURL);
    } catch (final IOException e) {
      final String message = "Could not read schedule file [" + jsonScheduleProjectRelativeFilePath + "]";
      throw new PreesmRuntimeException(message, e);
    }
  }

  /**
   *
   * @author anmorvan
   *
   */
  private class ScheduleComparator implements Comparator<DAGVertex> {

    private final Map<DAGVertex, ScheduleEntry> entries;
    private final Map<DAGVertex, Long>          topoOrder;

    private ScheduleComparator(final Map<DAGVertex, ScheduleEntry> entries, final Map<DAGVertex, Long> topoOrder) {
      this.entries = entries;
      this.topoOrder = topoOrder;
    }

    @Override
    public int compare(final DAGVertex o1, final DAGVertex o2) {
      if (o1 == o2) {
        return 0;
      }
      long realOrder = topoOrder.get(o1) - topoOrder.get(o2);
      final boolean bothKnown = this.entries.containsKey(o1) && this.entries.containsKey(o2);
      if (bothKnown) {
        final ScheduleEntry schedE1 = this.entries.get(o1);
        final ScheduleEntry schedE2 = this.entries.get(o2);
        long res = (schedE1.getStart() - schedE2.getStart());
        if (res > 0 /* && realOrder > 0 */) {
          return 1;
        } else if (res < 0 /* && realOrder < 0 */) {
          return -1;
        }
        // else if (res != 0) {
        // throw new PreesmRuntimeException("Topological order for actors " + o1.getName() + " and " + o2.getName()
        // + " is not respected. RealOrder: " + realOrder);
        // }
      }

      if (realOrder > 0) {
        return 1;
      } else if (realOrder < 0) {
        return -1;
      }
      return 0;
    }

  }

}
