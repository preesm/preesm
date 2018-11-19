/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2014 - 2015)
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
package org.ietr.preesm.memory.script;

import bsh.EvalError;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.types.DataType;
import org.ietr.preesm.memory.allocation.AbstractMemoryAllocatorTask;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.workflow.WorkflowException;
import org.preesm.workflow.elements.Workflow;

// TODO: Auto-generated Javadoc
/**
 * The Class MemoryScriptTask.
 */
public class MemoryScriptTask extends AbstractMemoryScriptTask {

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map,
   * org.eclipse.core.runtime.IProgressMonitor, java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) throws WorkflowException {
    // Get verbose parameter
    boolean verbose = false;
    verbose = parameters.get(AbstractMemoryScriptTask.PARAM_VERBOSE).equals(AbstractMemoryScriptTask.VALUE_TRUE);

    // Get the log parameter
    final String log = parameters.get(AbstractMemoryScriptTask.PARAM_LOG);

    // Retrieve the alignment param
    final String valueAlignment = parameters.get(AbstractMemoryAllocatorTask.PARAM_ALIGNMENT);

    // Retrieve the input graph
    final DirectedAcyclicGraph dag = (DirectedAcyclicGraph) inputs.get("DAG");

    // Get the data types from the scenario
    final PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");
    final Map<String, DataType> dataTypes = scenario.getSimulationManager().getDataTypes();

    // Get check policy
    final String checkString = parameters.get(AbstractMemoryScriptTask.PARAM_CHECK);

    final MemoryExclusionGraph meg = (MemoryExclusionGraph) inputs.get("MemEx");

    // execute
    final MemoryScriptEngine engine = new MemoryScriptEngine(valueAlignment, log, verbose);
    try {
      engine.runScripts(dag, dataTypes, checkString);
    } catch (final EvalError e) {
      final String message = "An error occurred during memory scripts execution";
      PreesmLogger.getLogger().log(Level.SEVERE, message, e);
      throw new WorkflowException(message, e);
    }
    engine.updateMemEx(meg);

    if (!log.equals("")) {
      // generate
      engine.generateCode(scenario, log);
    }

    // Outputs
    final Map<String, Object> outputs = new LinkedHashMap<>();
    outputs.put("MemEx", meg);
    return outputs;
  }

  /**
   * This method must be overridden, otherwise, the workflow validator does not find it.
   *
   * @return the default parameters
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    return super.getDefaultParameters();
  }
}
