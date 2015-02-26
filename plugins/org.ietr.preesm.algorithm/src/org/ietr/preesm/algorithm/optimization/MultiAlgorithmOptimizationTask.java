/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy, Yaset Oliva Venegas
 *
 * [mpelcat,jnezan,kdesnos,jheulot,cguy,yoliva]@insa-rennes.fr
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software. You can use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty and the software's author, the holder of the
 * economic rights, and the successive licensors have only limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading, using, modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean that it is complicated to manipulate, and that also
 * therefore means that it is reserved for developers and experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and, more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.algorithm.optimization;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.preesm.algorithm.optimization.clean.joinfork.JoinForkCleaner;

/**
 * Head class to launch optimizations on a SDFGraph
 * 
 * @author cguy
 * 
 */
public class MultiAlgorithmOptimizationTask extends AbstractTaskImplementation {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		// Get the SDFGraph to optimize
		@SuppressWarnings("unchecked")
		Set<SDFGraph> graphs = (Set<SDFGraph>) inputs.get(KEY_SDF_GRAPHS_SET);

		// First pass is to clean the graph from useless pairs of join-fork
		// vertices which can hinder scheduling
		JoinForkCleaner cleaner = new JoinForkCleaner();
		// JoinForkCleaner evaluates some rates and delays expressions, and thus
		// can throw InvalidExpressionExceptions, even if at this point of the
		// workflow, there should have been already raised
		for(SDFGraph graph : graphs){
			try {
				while(cleaner.cleanJoinForkPairsFrom(graph));
			} catch (InvalidExpressionException e) {
				System.err.println("SDFGraph " + graph.getName()
						+ " contains invalid expressions.");
				e.printStackTrace();
			}
		}

		Map<String, Object> outputs = new HashMap<String, Object>();
		outputs.put(KEY_SDF_GRAPHS_SET, graphs);
		return outputs;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		return Collections.emptyMap();
	}

	@Override
	public String monitorMessage() {
		return "Starting optimization of SDFGraph";
	}

}
