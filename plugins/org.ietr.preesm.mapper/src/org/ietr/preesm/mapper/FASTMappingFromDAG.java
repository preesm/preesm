/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Jonathan Piat <jpiat@laas.fr> (2008 - 2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2017)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
package org.ietr.preesm.mapper;

import java.util.Map;
import java.util.logging.Level;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.abc.AbstractAbc;
import org.ietr.preesm.mapper.abc.IAbc;
import org.ietr.preesm.mapper.abc.impl.latency.InfiniteHomogeneousAbc;
import org.ietr.preesm.mapper.abc.taskscheduling.SimpleTaskSched;
import org.ietr.preesm.mapper.abc.taskscheduling.TopologicalTaskSched;
import org.ietr.preesm.mapper.algo.fast.FastAlgorithm;
import org.ietr.preesm.mapper.algo.list.InitialLists;
import org.ietr.preesm.mapper.graphtransfo.TagDAG;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.params.AbcParameters;
import org.ietr.preesm.mapper.params.FastAlgoParameters;

/**
 * FAST is a sequential mapping/scheduling method based on list scheduling followed by a neighborhood search phase. It was invented by Y-K Kwok.
 *
 * @author pmenuet
 * @author mpelcat
 */
public class FASTMappingFromDAG extends AbstractMappingFromDAG {

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.AbstractMapping#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = super.getDefaultParameters();

    parameters.put("displaySolutions", "false");
    parameters.put("fastTime", "100");
    parameters.put("fastLocalSearchTime", "10");
    return parameters;
  }

  protected void schedule(final Design architecture, final PreesmScenario scenario, final MapperDAG dag, final Map<String, String> parameters,
      final Map<String, Object> outputs) {

    final FastAlgoParameters fastParams = new FastAlgoParameters(parameters);
    final AbcParameters abcParams = new AbcParameters(parameters);

    if (dag == null) {
      throw (new WorkflowException(" graph can't be scheduled, check console messages"));
    }

    // calculates the DAG span length on the architecture main operator (the
    // tasks that can
    // not be executed by the main operator are deported without transfer
    // time to other operator
    calculateSpan(dag, architecture, scenario, abcParams);

    final IAbc simu = new InfiniteHomogeneousAbc(abcParams, dag, architecture, abcParams.getSimulatorType().getTaskSchedType(), scenario);

    final InitialLists initialLists = new InitialLists();
    if (!initialLists.constructInitialLists(dag, simu)) {
      return;
    }

    final TopologicalTaskSched taskSched = new TopologicalTaskSched(simu.getTotalOrder());
    simu.resetDAG();

    final FastAlgorithm fastAlgorithm = new FastAlgorithm(initialLists, scenario);

    WorkflowLogger.getLogger().log(Level.INFO, "Mapping");

    MapperDAG resDag = fastAlgorithm.map("test", abcParams, fastParams, dag, architecture, false, false, fastParams.isDisplaySolutions(), null, taskSched);

    WorkflowLogger.getLogger().log(Level.INFO, "Mapping finished");

    final IAbc simu2 = AbstractAbc.getInstance(abcParams, resDag, architecture, scenario);
    // Transfer vertices are automatically regenerated
    simu2.setDAG(resDag);

    // The transfers are reordered using the best found order during
    // scheduling
    simu2.reschedule(fastAlgorithm.getBestTotalOrder());
    final TagDAG tagDAG = new TagDAG();

    // The mapper dag properties are put in the property bean to be
    // transfered to code generation
    try {
      tagDAG.tag(resDag, architecture, scenario, simu2, abcParams.getEdgeSchedType());
    } catch (final InvalidExpressionException e) {
      throw (new WorkflowException(e.getMessage()));
    }

    // A simple task scheduler avoids new task swaps and ensures reuse of
    // previous order.
    simu2.setTaskScheduler(new SimpleTaskSched());
    outputs.put(AbstractWorkflowNodeImplementation.KEY_SDF_DAG, resDag);
    outputs.put(AbstractWorkflowNodeImplementation.KEY_SDF_ABC, simu2);

    super.clean(architecture, scenario);
    super.checkSchedulingResult(parameters, resDag);

  }

}
