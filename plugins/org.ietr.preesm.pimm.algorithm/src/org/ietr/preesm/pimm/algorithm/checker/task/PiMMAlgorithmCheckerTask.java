/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
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
package org.ietr.preesm.pimm.algorithm.checker.task;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.pimm.algorithm.checker.PiMMAlgorithmChecker;

/**
 * Head class to launch check on a PiGraph
 * 
 * @author cguy
 * 
 */
public class PiMMAlgorithmCheckerTask extends AbstractTaskImplementation {

	// Rem: Logger is used to display messages in the console
	protected Logger logger = WorkflowLogger.getLogger();

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		// Get the PiGraph to check
		PiGraph graph = (PiGraph) inputs.get(KEY_PI_GRAPH);
		// Check the graph and display corresponding messages
		PiMMAlgorithmChecker checker = new PiMMAlgorithmChecker();
		if (checker.checkGraph(graph))
			logger.log(Level.FINE, checker.getOkMsg().toString());
		else {
			if (checker.isErrors())
				logger.log(Level.SEVERE, checker.getErrorMsg().toString());
			if (checker.isWarnings())
				logger.log(Level.WARNING, checker.getWarningMsg().toString());
		}

		// Return the checked graph
		Map<String, Object> outputs = new HashMap<String, Object>();
		outputs.put(KEY_PI_GRAPH, graph);
		return outputs;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		return Collections.emptyMap();
	}

	@Override
	public String monitorMessage() {
		return "Starting checking of PiGraph";
	}

}
