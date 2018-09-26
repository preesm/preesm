package org.ietr.preesm.mapper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.ietr.dftools.algorithm.iterators.TopologicalDAGIterator;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.abc.impl.latency.LatencyAbc;
import org.ietr.preesm.mapper.abc.taskscheduling.AbstractTaskSched;
import org.ietr.preesm.mapper.algo.list.InitialLists;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.params.AbcParameters;
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
    params.put(SCHEDULE_FILE, "/schedule.json");
    return params;
  }

  @Override
  protected LatencyAbc schedule(final Map<String, Object> outputs, final Map<String, String> parameters,
      final InitialLists initial, final PreesmScenario scenario, final AbcParameters abcParams, final MapperDAG dag,
      final Design architecture, final AbstractTaskSched taskSched) {

    final String jsonScheduleFilePath = parameters.get(SCHEDULE_FILE);
    try {
      final String readURL = URLResolver.readURL(jsonScheduleFilePath);
    } catch (IOException e) {
      final String message = "Could not read schedule file [" + jsonScheduleFilePath + "]";
      throw new PreesmMapperException(message, e);
    }

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

}
