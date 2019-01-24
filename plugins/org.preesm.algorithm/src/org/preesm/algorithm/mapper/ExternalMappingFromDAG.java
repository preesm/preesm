/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
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
import org.preesm.algorithm.model.dag.edag.DAGForkVertex;
import org.preesm.algorithm.model.dag.edag.DAGJoinVertex;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.URLResolver;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.PiIdentifiers;
import org.preesm.model.scenario.PreesmScenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.attributes.VLNV;
import org.preesm.model.slam.component.Operator;
import org.preesm.model.slam.utils.DesignTools.ComponentInstanceComparator;

/**
 *
 * @author anmorvan
 *
 */
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
      final InitialLists initial, final PreesmScenario scenario, final AbcParameters abcParams, final MapperDAG dag,
      final Design architecture, final AbstractTaskSched taskSched) {

    // 1- read schedule file
    final String jsonScheduleFilePath = parameters.get(ExternalMappingFromDAG.SCHEDULE_FILE);
    final Schedule schedule = readSchedule(jsonScheduleFilePath);

    // 2- check
    checkScheduleCompatibility(schedule, dag, architecture);

    // 3- sort components to have a relation from ID to component
    final List<ComponentInstance> componentInstances = new ArrayList<>(architecture.getComponentInstances());
    Collections.sort(componentInstances, new ComponentInstanceComparator());

    // 4- prepare map to have relation from vertex to its schedule entry
    final Map<DAGVertex, ScheduleEntry> entries = initEntryMap(dag, schedule);

    // 5.1 - build unordered lists of vertices per core ID
    final List<List<DAGVertex>> orderedVertices = new ArrayList<>();
    for (int i = 0; i < componentInstances.size(); i++) {
      final ComponentInstance componentInstance = componentInstances.get(i);
      if (componentInstance.getComponent() instanceof Operator) {
        orderedVertices.add(new ArrayList<>());
      }
    }
    for (final ScheduleEntry e : schedule.getScheduleEntries()) {
      final Integer coreID = e.getCore();
      final String srActorName = e.getTaskName() + "_" + e.getSingleRateInstanceNumber();
      if (coreID == null) {
        final String message = "Schedule does not specify core ID for actor " + srActorName;
        throw new PreesmRuntimeException(message);
      }
      final List<DAGVertex> list = orderedVertices.get(coreID);
      list.add(dag.getVertex(srActorName));
    }
    // 5.2 - insert missing nodes in the lists (for instance implode/explode nodes if schedule has been done on flatten
    // graph)
    for (final DAGVertex vtx : dag.vertexSet()) {
      if (!entries.containsKey(vtx)) {
        DAGVertex associateVtx = vtx;
        do {
          associateVtx = ExternalMappingFromDAG.getAssociateVertex(associateVtx);
        } while (!entries.containsKey(associateVtx));
        orderedVertices.get(entries.get(associateVtx).getCore().intValue()).add(vtx);
      }
    }

    final LatencyAbc abc = LatencyAbc.getInstance(abcParams, dag, architecture, scenario);
    // 6- sort vertex lists following start dates from the schedule entries and apply schedule
    for (int i = 0; i < orderedVertices.size(); i++) {
      final ComponentInstance componentInstance = componentInstances.get(i);
      final List<DAGVertex> list = orderedVertices.get(i);
      list.sort(new ScheduleComparator(entries));
      for (final DAGVertex v : list) {
        mapVertex(abc, componentInstance, v);
      }
    }

    return abc;
  }

  private void mapVertex(final LatencyAbc abc, final ComponentInstance componentInstance, final DAGVertex v) {
    final MapperDAGVertex vertex = (MapperDAGVertex) v;
    if (abc.isMapable(vertex, componentInstance, false)) {
      abc.map(vertex, componentInstance, true, false);
    } else {
      final String message = "The schedule is invalid: vertex [" + vertex.getName()
          + "] cannot be mapped on component [" + componentInstance.getInstanceName() + "].";
      throw new PreesmRuntimeException(message);
    }
  }

  private static final DAGVertex getAssociateVertex(final DAGVertex vtx) {
    final String nodeKind = vtx.getPropertyStringValue(PiIdentifiers.NODE_KIND);
    final DAGVertex associateVtx;
    switch (nodeKind) {
      case DAGForkVertex.DAG_FORK_VERTEX:
        final Set<DAGEdge> incomingEdges = vtx.incomingEdges();
        if (incomingEdges.size() != 1) {
          throw new PreesmRuntimeException("Fork node should have only one incomming edge");
        }
        final DAGEdge inEdge = incomingEdges.iterator().next();
        associateVtx = inEdge.getSource();
        break;
      case DAGJoinVertex.DAG_JOIN_VERTEX:
        final Set<DAGEdge> outgoingEdges = vtx.outgoingEdges();
        if (outgoingEdges.size() != 1) {
          throw new PreesmRuntimeException("Join node should have only one outgoing edge");
        }
        final DAGEdge outEdge = outgoingEdges.iterator().next();
        associateVtx = outEdge.getTarget();
        break;
      default:
        throw new PreesmRuntimeException("");
    }
    return associateVtx;
  }

  private Map<DAGVertex, ScheduleEntry> initEntryMap(final MapperDAG dag, final Schedule schedule) {
    final Map<DAGVertex, ScheduleEntry> entries = new LinkedHashMap<>();
    for (final ScheduleEntry e : schedule.getScheduleEntries()) {
      final String s = e.getTaskName() + "_" + e.getSingleRateInstanceNumber();
      final DAGVertex vertex = dag.getVertex(s);
      if (vertex == null) {
        final String message = "The schedule entry for single rate actor [" + s + "] "
            + "has no corresponding actor in the single rate graph.";
        throw new PreesmRuntimeException(message);
      }
      entries.put(vertex, e);
    }
    return entries;
  }

  /**
   * Checks if schedule from the JSON file is compatible with algo and archi from the scenario.
   */
  private void checkScheduleCompatibility(final Schedule schedule, final MapperDAG dag, final Design architecture) {
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

    final Map<String, ScheduleEntry> actorNameToScheduleEntry = new LinkedHashMap<>();
    for (final ScheduleEntry scheduleEntry : scheduleEntries) {
      final String srActorName = scheduleEntry.getTaskName() + "_" + scheduleEntry.getSingleRateInstanceNumber();
      final DAGVertex vertex = dag.getVertex(srActorName);
      if (vertex == null) {
        final String message = "The schedule entry for single rate actor [" + srActorName + "] "
            + "has no corresponding actor in the single rate graph.";
        throw new PreesmRuntimeException(message);
      } else {
        actorNameToScheduleEntry.put(srActorName, scheduleEntry);
      }
    }

    for (final AbstractActor actor : referencePiMMGraph.getActors()) {
      final String actorName = actor.getName();
      if (!actorNameToScheduleEntry.containsKey(actorName)) {
        final String msg = "Single Rate actor [" + actorName + "] has no schedule entry";
        PreesmLogger.getLogger().log(Level.WARNING, msg);
      }
    }
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

    private ScheduleComparator(final Map<DAGVertex, ScheduleEntry> entries) {
      this.entries = entries;
    }

    @Override
    public int compare(final DAGVertex o1, final DAGVertex o2) {
      if (o1 == o2) {
        return 0;
      }
      final DAGVertex lhs;
      final DAGVertex rhs;
      int modifier = 0;
      if (!this.entries.containsKey(o1)) {
        lhs = ExternalMappingFromDAG.getAssociateVertex(o1);
        final String kind = o1.getPropertyStringValue(PiIdentifiers.NODE_KIND);
        switch (kind) {
          case DAGJoinVertex.DAG_JOIN_VERTEX:
            modifier = -1;
            break;
          case DAGForkVertex.DAG_FORK_VERTEX:
            modifier = +1;
            break;
          default:
        }
      } else {
        lhs = o1;
      }
      if (!this.entries.containsKey(o2)) {
        rhs = ExternalMappingFromDAG.getAssociateVertex(o2);
        final String kind = o2.getPropertyStringValue(PiIdentifiers.NODE_KIND);
        switch (kind) {
          case DAGJoinVertex.DAG_JOIN_VERTEX:
            modifier = +1;
            break;
          case DAGForkVertex.DAG_FORK_VERTEX:
            modifier = -1;
            break;
          default:
        }
      } else {
        rhs = o2;
      }
      if (rhs != lhs) {
        modifier = 0;
      }
      final ScheduleEntry scheduleEntry1 = this.entries.get(lhs);
      final ScheduleEntry scheduleEntry2 = this.entries.get(rhs);
      return (scheduleEntry1.getTopologicalStart() - scheduleEntry2.getTopologicalStart()) + modifier;
    }

  }

}
