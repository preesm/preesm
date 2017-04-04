/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2013)
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

package org.ietr.preesm.memory.exclusiongraph;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;

/**
 * Workflow element that takes a Scheduled DAG and a MemEx as inputs and update
 * the MemEx according to the DAG Schedule
 * 
 * @author kdesnos
 * 
 */
public class MemExUpdater extends AbstractMemExUpdater {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		// Check Workflow element parameters
		String valueVerbose = parameters.get(PARAM_VERBOSE);
		boolean verbose = valueVerbose.equals(VALUE_TRUE);

		String valueLifetime = parameters.get(PARAM_LIFETIME);
		boolean lifetime = valueLifetime.equals(VALUE_TRUE);

		String valueSupprForkJoin = parameters.get(PARAM_SUPPR_FORK_JOIN);
		boolean forkJoin = valueSupprForkJoin.equals(VALUE_TRUE);

		// Retrieve inputs
		DirectedAcyclicGraph dag = (DirectedAcyclicGraph) inputs.get("DAG");
		MemoryExclusionGraph memEx = (MemoryExclusionGraph) inputs.get("MemEx");

		MemExUpdaterEngine engine = new MemExUpdaterEngine(dag, memEx, verbose);
		engine.createLocalDag(forkJoin);	
		engine.update(lifetime);

		// Generate output
		Map<String, Object> output = new HashMap<String, Object>();
		output.put(KEY_MEM_EX, memEx);
		return output;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(PARAM_VERBOSE, VALUE_TRUE_FALSE_DEFAULT);
		parameters.put(PARAM_LIFETIME, VALUE_TRUE_FALSE_DEFAULT);
		parameters.put(PARAM_SUPPR_FORK_JOIN, VALUE_TRUE_FALSE_DEFAULT);
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Updating MemEx";
	}

}
