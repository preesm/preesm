/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2012)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/

package org.ietr.preesm.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.abc.AbstractAbc;
import org.ietr.preesm.mapper.abc.IAbc;
import org.ietr.preesm.mapper.abc.impl.latency.InfiniteHomogeneousAbc;
import org.ietr.preesm.mapper.abc.taskscheduling.SimpleTaskSched;
import org.ietr.preesm.mapper.abc.taskscheduling.TopologicalTaskSched;
import org.ietr.preesm.mapper.algo.fast.FastAlgorithm;
import org.ietr.preesm.mapper.algo.list.InitialLists;
import org.ietr.preesm.mapper.graphtransfo.SdfToDagConverter;
import org.ietr.preesm.mapper.graphtransfo.TagDAG;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.params.AbcParameters;
import org.ietr.preesm.mapper.params.FastAlgoParameters;

// TODO: Auto-generated Javadoc
/**
 * FAST is a sequential mapping/scheduling method based on list scheduling followed by a
 * neighborhood search phase. It was invented by Y-K Kwok.
 *
 * @author pmenuet
 * @author mpelcat
 */
public class FASTMapping extends AbstractMapping {

  /**
   * Instantiates a new FAST mapping.
   */
  public FASTMapping() {
  }

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

  /*
   * (non-Javadoc)
   * 
   * @see org.ietr.preesm.mapper.AbstractMapping#execute(java.util.Map, java.util.Map,
   * org.eclipse.core.runtime.IProgressMonitor, java.lang.String,
   * org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs,
      final Map<String, String> parameters, final IProgressMonitor monitor, final String nodeName,
      final Workflow workflow) throws WorkflowException {

    final Map<String, Object> outputs = new HashMap<>();
    final Design architecture = (Design) inputs.get("architecture");
    final SDFGraph algorithm = (SDFGraph) inputs.get("SDF");
    final PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");

    super.execute(inputs, parameters, monitor, nodeName, workflow);

    final FastAlgoParameters fastParams = new FastAlgoParameters(parameters);
    final AbcParameters abcParams = new AbcParameters(parameters);

    MapperDAG dag = SdfToDagConverter.convert(algorithm, architecture, scenario, false);

    if (dag == null) {
      throw (new WorkflowException(" graph can't be scheduled, check console messages"));
    }

    // calculates the DAG span length on the architecture main operator (the
    // tasks that can
    // not be executed by the main operator are deported without transfer
    // time to other operator
    calculateSpan(dag, architecture, scenario, abcParams);

    final IAbc simu = new InfiniteHomogeneousAbc(abcParams, dag, architecture,
        abcParams.getSimulatorType().getTaskSchedType(), scenario);

    final InitialLists initialLists = new InitialLists();
    if (!initialLists.constructInitialLists(dag, simu)) {
      return outputs;
    }

    final TopologicalTaskSched taskSched = new TopologicalTaskSched(simu.getTotalOrder());
    simu.resetDAG();

    final FastAlgorithm fastAlgorithm = new FastAlgorithm(initialLists, scenario);

    WorkflowLogger.getLogger().log(Level.INFO, "Mapping");

    dag = fastAlgorithm.map("test", abcParams, fastParams, dag, architecture, false, false,
        fastParams.isDisplaySolutions(), monitor, taskSched);

    WorkflowLogger.getLogger().log(Level.INFO, "Mapping finished");

    final IAbc simu2 = AbstractAbc.getInstance(abcParams, dag, architecture, scenario);
    // Transfer vertices are automatically regenerated
    simu2.setDAG(dag);

    // The transfers are reordered using the best found order during
    // scheduling
    simu2.reschedule(fastAlgorithm.getBestTotalOrder());
    final TagDAG tagDAG = new TagDAG();

    // The mapper dag properties are put in the property bean to be
    // transfered to code generation
    try {
      tagDAG.tag(dag, architecture, scenario, simu2, abcParams.getEdgeSchedType());
    } catch (final InvalidExpressionException e) {
      throw (new WorkflowException(e.getMessage()));
    }

    outputs.put("DAG", dag);
    // A simple task scheduler avoids new task swaps and ensures reuse of
    // previous order.
    simu2.setTaskScheduler(new SimpleTaskSched());
    outputs.put("ABC", simu2);

    super.clean(architecture, scenario);
    return outputs;
  }

}
