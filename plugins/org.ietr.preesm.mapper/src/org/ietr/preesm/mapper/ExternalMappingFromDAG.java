package org.ietr.preesm.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGForkVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGJoinVertex;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.architecture.slam.attributes.VLNV;
import org.ietr.dftools.architecture.slam.component.Operator;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.util.DesignTools.ComponentInstanceComparator;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.util.PiIdentifiers;
import org.ietr.preesm.experiment.model.pimm.util.URLResolver;
import org.ietr.preesm.mapper.abc.impl.latency.LatencyAbc;
import org.ietr.preesm.mapper.abc.taskscheduling.AbstractTaskSched;
import org.ietr.preesm.mapper.algo.list.InitialLists;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.params.AbcParameters;
import org.ietr.preesm.mapper.schedule.Architecture;
import org.ietr.preesm.mapper.schedule.Schedule;
import org.ietr.preesm.mapper.schedule.ScheduleEntry;
import org.ietr.preesm.mapper.schedule.ScheduleUtils;

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
    for (ScheduleEntry e : schedule.getScheduleEntries()) {
      final Integer coreID = e.getCore();
      final String srActorName = e.getTaskName() + "_" + e.getSingleRateInstanceNumber();
      if (coreID == null) {
        final String message = "Schedule does not specify core ID for actor " + srActorName;
        throw new PreesmMapperException(message);
      }
      final List<DAGVertex> list = orderedVertices.get(coreID);
      list.add(dag.getVertex(srActorName));
    }
    // 5.2 - insert missing nodes in the lists (for instance implode/explode nodes if schedule has been done on flatten
    // graph)
    for (DAGVertex vtx : dag.vertexSet()) {
      if (!entries.containsKey(vtx)) {
        DAGVertex associateVtx = vtx;
        do {
          associateVtx = getAssociateVertex(associateVtx);
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
      for (DAGVertex v : list) {
        mapVertex(abc, componentInstance, v);
      }
    }

    return abc;
  }

  private void mapVertex(final LatencyAbc abc, ComponentInstance componentInstance, final DAGVertex v) {
    MapperDAGVertex vertex = (MapperDAGVertex) v;
    if (abc.isMapable(vertex, componentInstance, false)) {
      abc.map(vertex, componentInstance, true, false);
    } else {
      final String message = "The schedule is invalid: vertex [" + vertex.getName()
          + "] cannot be mapped on component [" + componentInstance.getInstanceName() + "].";
      throw new PreesmMapperException(message);
    }
  }

  private static final DAGVertex getAssociateVertex(final DAGVertex vtx) {
    final String nodeKind = vtx.getPropertyStringValue(PiIdentifiers.NODE_KIND);
    final DAGVertex associateVtx;
    switch (nodeKind) {
      case DAGForkVertex.DAG_FORK_VERTEX:
        final Set<DAGEdge> incomingEdges = vtx.incomingEdges();
        if (incomingEdges.size() != 1) {
          throw new PreesmMapperException("Fork node should have only one incomming edge");
        }
        final DAGEdge inEdge = incomingEdges.iterator().next();
        associateVtx = inEdge.getSource();
        break;
      case DAGJoinVertex.DAG_JOIN_VERTEX:
        final Set<DAGEdge> outgoingEdges = vtx.outgoingEdges();
        if (outgoingEdges.size() != 1) {
          throw new PreesmMapperException("Join node should have only one outgoing edge");
        }
        final DAGEdge outEdge = outgoingEdges.iterator().next();
        associateVtx = outEdge.getTarget();
        break;
      default:
        throw new PreesmMapperException("");
    }
    return associateVtx;
  }

  private Map<DAGVertex, ScheduleEntry> initEntryMap(final MapperDAG dag, final Schedule schedule) {
    final Map<DAGVertex, ScheduleEntry> entries = new LinkedHashMap<>();
    for (ScheduleEntry e : schedule.getScheduleEntries()) {
      String s = e.getTaskName() + "_" + e.getSingleRateInstanceNumber();
      final DAGVertex vertex = dag.getVertex(s);
      if (vertex == null) {
        final String message = "The schedule entry for single rate actor [" + s + "] "
            + "has no corresponding actor in the single rate graph.";
        throw new PreesmMapperException(message);
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
      throw new PreesmMapperException(message);
    }

    // basic application comparison
    final String scheduleAppName = schedule.getApplicationName();
    final PiGraph referencePiMMGraph = dag.getReferencePiMMGraph();
    final String pisdfGraphName = referencePiMMGraph.getName();
    final boolean sameAppName = pisdfGraphName.equals(scheduleAppName);
    if (!sameAppName) {
      final String message = "The input schedule application is not compatible with "
          + "the application specified in the scenario.";
      throw new PreesmMapperException(message);
    }

    final List<ScheduleEntry> scheduleEntries = schedule.getScheduleEntries();

    final Map<String, ScheduleEntry> actorNameToScheduleEntry = new LinkedHashMap<>();
    for (final ScheduleEntry scheduleEntry : scheduleEntries) {
      final String srActorName = scheduleEntry.getTaskName() + "_" + scheduleEntry.getSingleRateInstanceNumber();
      final DAGVertex vertex = dag.getVertex(srActorName);
      if (vertex == null) {
        final String message = "The schedule entry for single rate actor [" + srActorName + "] "
            + "has no corresponding actor in the single rate graph.";
        throw new PreesmMapperException(message);
      } else {
        actorNameToScheduleEntry.put(srActorName, scheduleEntry);
      }
    }

    for (final AbstractActor actor : referencePiMMGraph.getActors()) {
      final String actorName = actor.getName();
      if (!actorNameToScheduleEntry.containsKey(actorName)) {
        final String msg = "Single Rate actor [" + actorName + "] has no schedule entry";
        WorkflowLogger.getLogger().log(Level.WARNING, msg);
      }
    }
  }

  private Schedule readSchedule(final String jsonScheduleProjectRelativeFilePath) {
    try {
      final String readURL = URLResolver.readURL(jsonScheduleProjectRelativeFilePath);
      return ScheduleUtils.parseJsonString(readURL);
    } catch (final IOException e) {
      final String message = "Could not read schedule file [" + jsonScheduleProjectRelativeFilePath + "]";
      throw new PreesmMapperException(message, e);
    }
  }

  /**
   *
   * @author anmorvan
   *
   */
  class ScheduleComparator implements Comparator<DAGVertex> {

    private final Map<DAGVertex, ScheduleEntry> entries;

    public ScheduleComparator(Map<DAGVertex, ScheduleEntry> entries) {
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
      if (!entries.containsKey(o1)) {
        lhs = getAssociateVertex(o1);
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
      if (!entries.containsKey(o2)) {
        rhs = getAssociateVertex(o2);
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
      final ScheduleEntry scheduleEntry1 = entries.get(lhs);
      final ScheduleEntry scheduleEntry2 = entries.get(rhs);
      return scheduleEntry1.getTopologicalStart() - scheduleEntry2.getTopologicalStart() + modifier;
    }

  }

}
