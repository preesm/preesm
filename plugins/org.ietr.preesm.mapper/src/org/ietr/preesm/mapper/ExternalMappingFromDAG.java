package org.ietr.preesm.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.ietr.dftools.algorithm.iterators.TopologicalDAGIterator;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGForkVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGJoinVertex;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.architecture.slam.attributes.VLNV;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.util.DesignTools.ComponentInstanceComparator;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.util.PiIdentifiers;
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
import org.ietr.preesm.utils.files.URLResolver;

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

    final LatencyAbc abc = LatencyAbc.getInstance(abcParams, dag, architecture, scenario);
    final TopologicalDAGIterator topologicalDAGIterator = new TopologicalDAGIterator(dag);
    while (topologicalDAGIterator.hasNext()) {
      final MapperDAGVertex vertex = (MapperDAGVertex) topologicalDAGIterator.next();
      final ScheduleEntry scheduleEntry = entries.get(vertex);
      if (scheduleEntry != null) {
        mapScheduledVertex(abc, componentInstances, vertex, scheduleEntry);
      } else {
        mapNonScheduledVertex(abc, componentInstances, entries, vertex);
      }
    }
    return abc;
  }

  private void mapScheduledVertex(final LatencyAbc abc, final List<ComponentInstance> componentInstances,
      final MapperDAGVertex vertex, final ScheduleEntry scheduleEntry) {
    final Integer core = scheduleEntry.getCore();
    final ComponentInstance componentInstance = componentInstances.get(core);

    if (abc.isMapable(vertex, componentInstance, false)) {
      abc.map(vertex, componentInstance, true, false);
    } else {
      final ComponentInstance compatibleComponent = componentInstances.stream()
          .filter(c -> abc.isMapable(vertex, c, false)).findFirst().orElseThrow(() -> new RuntimeException(""));
      abc.map(vertex, compatibleComponent, true, false);
    }
  }

  private void mapNonScheduledVertex(final LatencyAbc abc, final List<ComponentInstance> componentInstances,
      final Map<DAGVertex, ScheduleEntry> entries, final MapperDAGVertex vertex) {
    final String nodeKind = vertex.getPropertyStringValue(PiIdentifiers.NODE_KIND);
    switch (nodeKind) {
      case DAGForkVertex.DAG_FORK_VERTEX:
        // insert right after the source of the fork, on the same node
        final Set<DAGEdge> incomingEdges = vertex.incomingEdges();
        if (incomingEdges.size() != 1) {
          throw new PreesmMapperException("Fork node should have only one incomming edge");
        }
        final DAGEdge edge = incomingEdges.iterator().next();
        final DAGVertex source = edge.getSource();
        final ScheduleEntry sourceScheduleEntry = entries.get(source);
        if (sourceScheduleEntry == null) {
          final String message = "This video explains why the schedule entry should not be null : "
              + "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
          throw new PreesmMapperException(message);
        }
        final ComponentInstance sourceComponentInstance = componentInstances.get(sourceScheduleEntry.getCore());
        abc.map(vertex, sourceComponentInstance, true, false);
        break;
      case DAGJoinVertex.DAG_JOIN_VERTEX:
        // insert right before the target of the join, on the same node
        final Set<DAGEdge> outgoingEdges = vertex.outgoingEdges();
        if (outgoingEdges.size() != 1) {
          throw new PreesmMapperException("Join node should have only one outgoing edge");
        }
        final DAGEdge outEdge = outgoingEdges.iterator().next();
        final DAGVertex target = outEdge.getTarget();
        final ScheduleEntry targetScheduleEntry = entries.get(target);
        if (targetScheduleEntry == null) {
          final String message = "This video explains why the schedule entry should not be null : "
              + "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
          throw new PreesmMapperException(message);
        }
        final ComponentInstance targetComponentInstance = componentInstances.get(targetScheduleEntry.getCore());
        abc.map(vertex, targetComponentInstance, true, false);
        break;
      default:
        throw new PreesmMapperException("Unsupported node kind " + nodeKind);
    }
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

}
