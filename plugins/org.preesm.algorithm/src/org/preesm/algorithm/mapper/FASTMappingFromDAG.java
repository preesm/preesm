/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Jonathan Piat [jpiat@laas.fr] (2008 - 2011)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2017)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2012)
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

import java.util.Map;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.abc.taskscheduling.AbstractTaskSched;
import org.preesm.algorithm.mapper.abc.taskscheduling.SimpleTaskSched;
import org.preesm.algorithm.mapper.algo.FastAlgorithm;
import org.preesm.algorithm.mapper.algo.InitialLists;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.params.AbcParameters;
import org.preesm.algorithm.mapper.params.FastAlgoParameters;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * FAST is a sequential mapping/scheduling method based on list scheduling followed by a neighborhood search phase. It
 * was invented by Y-K Kwok.
 *
 * @author pmenuet
 * @author mpelcat
 */
@PreesmTask(id = "org.ietr.preesm.plugin.mapper.fastdag", name = "Fast Scheduling from DAG", category = "Schedulers",

    inputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_SDF_DAG, type = DirectedAcyclicGraph.class),
        @Port(name = AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, type = Design.class),
        @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class) },

    outputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_SDF_DAG, type = DirectedAcyclicGraph.class),
        @Port(name = AbstractWorkflowNodeImplementation.KEY_SDF_ABC, type = LatencyAbc.class) },

    parameters = { @Parameter(name = "edgeSchedType", values = { @Value(name = "Simple") }),
        @Parameter(name = "simulatorType", values = { @Value(name = "LooselyTimed") }),
        @Parameter(name = "Check", values = { @Value(name = "True") }),
        @Parameter(name = "Optimize synchronization", values = { @Value(name = "False") }),
        @Parameter(name = "balanceLoads", values = { @Value(name = "false") }),
        @Parameter(name = "fastTime", values = { @Value(name = "100") }),
        @Parameter(name = "fastLocalSearchTime", values = { @Value(name = "10") })

    })
@Deprecated
public class FASTMappingFromDAG extends AbstractMappingFromDAG {

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.AbstractMapping#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = super.getDefaultParameters();

    parameters.put("fastTime", "100");
    parameters.put("fastLocalSearchTime", "10");
    return parameters;
  }

  @Override
  protected LatencyAbc schedule(final Map<String, Object> outputs, final Map<String, String> parameters,
      final InitialLists initial, final Scenario scenario, final AbcParameters abcParams, final MapperDAG dag,
      final Design architecture, final AbstractTaskSched taskSched) {

    final FastAlgoParameters fastParams = new FastAlgoParameters(parameters);
    final FastAlgorithm fastAlgorithm = new FastAlgorithm(initial, scenario);
    final MapperDAG resDag = fastAlgorithm.map("test", abcParams, fastParams, dag, architecture, false, null,
        taskSched);

    final LatencyAbc simu2 = LatencyAbc.getInstance(abcParams, resDag, architecture, scenario);
    // Transfer vertices are automatically regenerated
    simu2.setDAG(resDag);

    // The transfers are reordered using the best found order during
    // scheduling
    simu2.reschedule(fastAlgorithm.getBestTotalOrder());

    simu2.setTaskScheduler(new SimpleTaskSched());

    return simu2;
  }

}
