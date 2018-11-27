/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2013)
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
package org.preesm.algorithm.memory.exclusiongraph;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

// TODO: Auto-generated Javadoc
/**
 * Workflow element that takes a Scheduled DAG and a MemEx as inputs and update the MemEx according to the DAG Schedule.
 *
 * @author kdesnos
 */
public class MemExUpdater extends AbstractMemExUpdater {

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map,
   * org.eclipse.core.runtime.IProgressMonitor, java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) throws PreesmException {

    // Check Workflow element parameters
    final String valueVerbose = parameters.get(AbstractMemExUpdater.PARAM_VERBOSE);
    final boolean verbose = valueVerbose.equals(AbstractMemExUpdater.VALUE_TRUE);

    final String valueLifetime = parameters.get(AbstractMemExUpdater.PARAM_LIFETIME);
    final boolean lifetime = valueLifetime.equals(AbstractMemExUpdater.VALUE_TRUE);

    final String valueSupprForkJoin = parameters.get(AbstractMemExUpdater.PARAM_SUPPR_FORK_JOIN);
    final boolean forkJoin = valueSupprForkJoin.equals(AbstractMemExUpdater.VALUE_TRUE);

    // Retrieve inputs
    final DirectedAcyclicGraph dag = (DirectedAcyclicGraph) inputs.get("DAG");
    final MemoryExclusionGraph memEx = (MemoryExclusionGraph) inputs.get("MemEx");

    final MemExUpdaterEngine engine = new MemExUpdaterEngine(dag, memEx, verbose);
    engine.createLocalDag(forkJoin);
    engine.update(lifetime);

    // Generate output
    final Map<String, Object> output = new LinkedHashMap<>();
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
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(AbstractMemExUpdater.PARAM_VERBOSE, AbstractMemExUpdater.VALUE_TRUE_FALSE_DEFAULT);
    parameters.put(AbstractMemExUpdater.PARAM_LIFETIME, AbstractMemExUpdater.VALUE_TRUE_FALSE_DEFAULT);
    parameters.put(AbstractMemExUpdater.PARAM_SUPPR_FORK_JOIN, AbstractMemExUpdater.VALUE_TRUE_FALSE_DEFAULT);
    return parameters;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Updating MemEx";
  }

}
