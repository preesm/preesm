/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * blaunay <bapt.launay@gmail.com> (2015)
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
package org.ietr.preesm.evaluator;

import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;

import java.util.*;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.preesm.core.scenario.PreesmScenario;

/**
 * Main class used to compute the optimal periodic schedule and its throughput
 * for a given SDF or IBSDF, returns the throughput and the graph normalized
 * (such that for each actor, prod and cons rates are the same)
 * 
 * @author blaunay
 * 
 */
public class PeriodicEvaluator extends AbstractTaskImplementation {

	
	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {
		
		double period, throughput = 0;
		
		// Retrieve the input dataflow and the scenario
		SDFGraph inputGraph = (SDFGraph) inputs.get("SDF");
		PreesmScenario scenario = (PreesmScenario) inputs.get("scenario"); 
		
		// Normalize the graph (for each actor, ins=outs)
		WorkflowLogger.getLogger().log(Level.INFO, "Normalization");
		NormalizeVisitor normalize = new NormalizeVisitor();
		try {
			inputGraph.accept(normalize);
		} catch (SDF4JException e) {
			throw (new WorkflowException("The graph cannot be normalized"));
		}
		SDFGraph NormSDF = (SDFGraph) normalize.getOutput();
		WorkflowLogger.getLogger().log(Level.INFO, "Normalization finished");
		
		// Find out if graph hierarchic (IBSDF) or not
		boolean hierarchical = false;
		for (SDFAbstractVertex vertex : NormSDF.vertexSet()) {
			hierarchical = hierarchical || (vertex.getGraphDescription() != null
					&& vertex.getGraphDescription() instanceof SDFGraph);
		}
		
		try {
			// if IBSDF -> hierarchical algorithm
			ThroughputEvaluator scheduler;
			if (hierarchical) {
				scheduler = new IBSDFThroughputEvaluator();
			} else {
				// if SDF -> linear program for periodic schedule
				scheduler = new SDFThroughputEvaluator();
			}
			WorkflowLogger.getLogger().log(Level.INFO, "Computation of the optimal periodic schedule");
			scheduler.scenar = scenario;
			period = scheduler.launch(NormSDF);
			throughput = scheduler.throughput_computation(period, inputGraph);
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
		}
		
		Map<String, Object> outputs = new HashMap<String, Object>(); 		
		// Normalized graph in the outputs 
		outputs.put("SDF", NormSDF);
		// Throughput in the outputs
		outputs.put("Throughput", throughput);
		
		return outputs;
	}
	
	
	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		return parameters;
	}

	
	@Override
	public String monitorMessage() {
		return "Evaluation of the throughput with a periodic schedule ";
	}
}
