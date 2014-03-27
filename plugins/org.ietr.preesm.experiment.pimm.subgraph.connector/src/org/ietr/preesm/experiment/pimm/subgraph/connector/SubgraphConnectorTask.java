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
 * abiding by the rules of distribution of free software.  You can  use, 
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
 ******************************************************************************/
package org.ietr.preesm.experiment.pimm.subgraph.connector;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

public class SubgraphConnectorTask extends AbstractTaskImplementation {

	public static String GRAPH_KEY = "PiMM";
	
	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {
		
		//Get the input
		PiGraph pg = (PiGraph) inputs.get(GRAPH_KEY);
		
		//Visit it with the subgraph connector
		SubgraphConnector connector = new SubgraphConnector();
		connector.visit(pg);
		
		//Replace Actors with refinement by PiGraphs in pg and all its subgraphs
		Map<PiGraph, List<ActorByGraphReplacement>> replacements = connector.getGraphReplacements();
		for (PiGraph key : replacements.keySet()) {
			for (ActorByGraphReplacement r : replacements.get(key)) {
				key.getVertices().remove(r.toBeRemoved());
				key.getVertices().add(r.toBeAdded());
			}
		}
		
		//Return pg
		Map<String, Object> outputs = new HashMap<String, Object>();
		outputs.put(GRAPH_KEY, pg);
		return outputs;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		return Collections.emptyMap();
	}

	@Override
	public String monitorMessage() {
		return "Connecting subgraphs";
	}

}
