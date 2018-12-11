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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.abc.taskscheduling.AbstractTaskSched;
import org.preesm.algorithm.mapper.algo.list.InitialLists;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.mapper.params.AbcParameters;
import org.preesm.algorithm.model.iterators.TopologicalDAGIterator;
import org.preesm.model.scenario.PreesmScenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.utils.DesignTools.ComponentInstanceComparator;

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
