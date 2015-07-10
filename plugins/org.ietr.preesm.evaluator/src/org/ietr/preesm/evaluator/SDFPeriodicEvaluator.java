package org.ietr.preesm.evaluator;

import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;

import java.util.*;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.preesm.core.scenario.PreesmScenario;

/**
 * Class used to compute the optimal periodic schedule and its throughput
 * for a given IBSDF
 * 
 * @author blaunay
 * 
 */
public class SDFPeriodicEvaluator extends AbstractTaskImplementation {

	
	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {
		
		// chrono start
		double startTime = System.nanoTime();
		double period;
		
		// Retrieve the input dataflow and the scenario
		SDFGraph inputGraph = (SDFGraph) inputs.get("SDF");
		PreesmScenario scenario = (PreesmScenario) inputs.get("scenario"); 
		
		// Normalize the graph (for each actor, ins=outs)
		NormalizeVisitor normalize = new NormalizeVisitor();
		try {
			inputGraph.accept(normalize);
		} catch (SDF4JException e) {
			throw (new WorkflowException("The graph cannot be normalized"));
		}
		SDFGraph NormSDF = (SDFGraph) normalize.getOutput();
		
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
				//TODO remove liveness ?
				//System.out.println("Liveness : "+(is_alive(NormSDF) != null));
				scheduler = new IBSDFThroughputEvaluator();
			} else {
				// if SDF -> linear program for periodic schedule
				scheduler = new SDFThroughputEvaluator();
			}
			scheduler.scenar = scenario;
			period = scheduler.launch(NormSDF);
			scheduler.throughput_computation(period, inputGraph);
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
		}
		
		Map<String, Object> outputs = new HashMap<String, Object>(); 		
		// TODO Normalized graph in the outputs NOT NECESSARY
		// TODO put the throughput in the outputs
		outputs.put("SDF", NormSDF);
		
		System.out.println("Time : "+(System.nanoTime() - startTime)/Math.pow(10, 9)+" s");
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