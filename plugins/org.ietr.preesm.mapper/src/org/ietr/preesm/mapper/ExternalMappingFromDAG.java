package org.ietr.preesm.mapper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.ietr.dftools.algorithm.iterators.TopologicalDAGIterator;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.architecture.slam.attributes.VLNV;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
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

    final String jsonScheduleFilePath = parameters.get(ExternalMappingFromDAG.SCHEDULE_FILE);
    final Schedule schedule = readSchedule(jsonScheduleFilePath);

    checkScheduleCompatibility(schedule, dag, architecture);

    final LatencyAbc abc = LatencyAbc.getInstance(abcParams, dag, architecture, scenario);
    final String mainOperatorName = scenario.getSimulationManager().getMainOperatorName();
    final ComponentInstance mainOperator = architecture.getComponentInstance(mainOperatorName);
    final List<ComponentInstance> componentInstances = architecture.getComponentInstances();

    final TopologicalDAGIterator topologicalDAGIterator = new TopologicalDAGIterator(dag);
    while (topologicalDAGIterator.hasNext()) {
      final MapperDAGVertex next = (MapperDAGVertex) topologicalDAGIterator.next();
      if (abc.isMapable(next, mainOperator, false)) {
        abc.map(next, mainOperator, true, false);
      } else {
        final ComponentInstance compatibleComponent = componentInstances.stream()
            .filter(c -> abc.isMapable(next, c, false)).findFirst().orElseThrow(() -> new RuntimeException(""));
        abc.map(next, compatibleComponent, true, false);
      }
    }
    return abc;
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
      final AbstractVertex lookupVertex = referencePiMMGraph.lookupVertex(srActorName);
      if (lookupVertex == null) {
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
        WorkflowLogger.getLogger().log(Level.WARNING, "Single Rate actor [" + actorName + "] has no schedule entry");
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
