/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2015)
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

package org.ietr.preesm.memory.bounds;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;

// TODO: Auto-generated Javadoc
/**
 * Workflow element that takes a MemoryExclusionGraph as input and computes its memory bounds. It outputs the unmodified MemEx as well as the input and output
 * bounds found.
 *
 * @author kdesnos
 *
 */
public class MemoryBoundsEstimator extends AbstractMemoryBoundsEstimator {

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map, org.eclipse.core.runtime.IProgressMonitor,
   * java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters, final IProgressMonitor monitor,
      final String nodeName, final Workflow workflow) throws WorkflowException {

    // Rem: Logger is used to display messages in the console
    final Logger logger = WorkflowLogger.getLogger();

    // Check Workflow element parameters
    final String valueVerbose = parameters.get(AbstractMemoryBoundsEstimator.PARAM_VERBOSE);
    final String valueSolver = parameters.get(AbstractMemoryBoundsEstimator.PARAM_SOLVER);

    final MemoryExclusionGraph memEx = (MemoryExclusionGraph) inputs.get("MemEx");

    final MemoryBoundsEstimatorEngine engine = new MemoryBoundsEstimatorEngine(memEx, valueVerbose);
    engine.selectSolver(valueSolver);
    engine.solve();

    final int minBound = engine.getMinBound();

    final int maxBound = engine.getMaxBound();

    logger.log(Level.INFO, "Bound_Max = " + maxBound + " Bound_Min = " + minBound);

    // Generate output
    final Map<String, Object> output = new HashMap<>();
    output.put(AbstractWorkflowNodeImplementation.KEY_BOUND_MAX, maxBound);
    output.put(AbstractWorkflowNodeImplementation.KEY_BOUND_MIN, minBound);
    output.put(AbstractWorkflowNodeImplementation.KEY_MEM_EX, memEx);
    return output;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new HashMap<>();
    parameters.put(AbstractMemoryBoundsEstimator.PARAM_SOLVER, AbstractMemoryBoundsEstimator.VALUE_SOLVER_DEFAULT);
    parameters.put(AbstractMemoryBoundsEstimator.PARAM_VERBOSE, AbstractMemoryBoundsEstimator.VALUE_VERBOSE_DEFAULT);
    return parameters;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Estimating Memory Bounds";
  }

}
