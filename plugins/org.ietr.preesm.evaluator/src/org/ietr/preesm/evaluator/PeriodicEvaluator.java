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
 * Class used to compute the optimal periodic schedule and its throughput
 * for a given SDF or IBSDF
 * 
 * @author blaunay
 * 
 */
public class PeriodicEvaluator extends AbstractTaskImplementation {

	
	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {
		
		// chrono start
		double startTime = System.nanoTime();
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
		
		System.out.println((System.nanoTime() - startTime)/Math.pow(10, 9));
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