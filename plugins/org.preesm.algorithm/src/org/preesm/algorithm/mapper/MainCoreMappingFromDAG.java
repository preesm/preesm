package org.preesm.algorithm.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.preesm.algorithm.iterators.TopologicalDAGIterator;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.abc.taskscheduling.AbstractTaskSched;
import org.preesm.algorithm.mapper.algo.list.InitialLists;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.mapper.params.AbcParameters;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.utils.DesignTools.ComponentInstanceComparator;
import org.preesm.scenario.PreesmScenario;

/**
 *
 * @author anmorvan
 *
 */
public class MainCoreMappingFromDAG extends AbstractMappingFromDAG {

  @Override
  public Map<String, String> getDefaultParameters() {
    return new LinkedHashMap<>();
  }

  @Override
  protected LatencyAbc schedule(final Map<String, Object> outputs, final Map<String, String> parameters,
      final InitialLists initial, final PreesmScenario scenario, final AbcParameters abcParams, final MapperDAG dag,
      final Design architecture, final AbstractTaskSched taskSched) {

    // 1- sort components to have a relation from ID to component
    final List<ComponentInstance> componentInstances = new ArrayList<>(architecture.getComponentInstances());
    Collections.sort(componentInstances, new ComponentInstanceComparator());
    final String mainOperatorName = scenario.getSimulationManager().getMainOperatorName();
    final ComponentInstance mainOperator = architecture.getComponentInstance(mainOperatorName);

    final LatencyAbc abc = LatencyAbc.getInstance(abcParams, dag, architecture, scenario);

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
