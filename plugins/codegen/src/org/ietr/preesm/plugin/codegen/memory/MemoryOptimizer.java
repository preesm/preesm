/**
 * 
 */
package org.ietr.preesm.plugin.codegen.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.implement.AbstractTaskImplementation;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.sdf4j.model.dag.DirectedAcyclicGraph;
import org.sdf4j.model.parameters.InvalidExpressionException;

/**
 * Testing methods to optimize DAG memory use
 * 
 * @author kdesnos
 */
public class MemoryOptimizer extends AbstractTaskImplementation {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName) throws WorkflowException {

		// Copy the input dag to the output
		Map<String, Object> outputs = new HashMap<String, Object>();
		DirectedAcyclicGraph dag = (DirectedAcyclicGraph) inputs.get("DAG");
		outputs.put("DAG", dag);

		// Make a copy of the Input DAG for treatment
		DirectedAcyclicGraph localDAG = (DirectedAcyclicGraph) dag.clone();

		// Rem: Logger is used to display messages in the console
		Logger logger = WorkflowLogger.getLogger();

		logger.log(Level.INFO, "Memory exclusion graph : start building");

		MemoryExclusionGraph memex = new MemoryExclusionGraph();
		try {
			memex.BuildGraph(localDAG);
		} catch (InvalidExpressionException e) {
			// TODO Auto-generated catch block
			throw new WorkflowException(e.getLocalizedMessage());
		}
		logger.log(Level.INFO, "Memory exclusion graph : graph built");
		logger.log(Level.INFO, "There are " + memex.vertexSet().size()
				+ " memory transfers.");
		logger.log(Level.INFO, "There are " + memex.edgeSet().size()
				+ " exclusions.");

		double maxEdges = memex.vertexSet().size()
				* (memex.vertexSet().size() - 1) / 2.0;

		logger.log(Level.INFO, "The edge density of the graph is "
				+ memex.edgeSet().size() / maxEdges + ".");

		MaximumWeightCliqueSolver<MemoryExclusionGraphNode, DefaultEdge> mclique;
		/*
		 * mclique = new MaximumWeightCliqueSolver<MemoryExclusionGraphNode,
		 * DefaultEdge>( memex);
		 * 
		 * logger.log(Level.INFO,
		 * "Maximum-Weight Clique: Starting Pre-Ordering");
		 * mclique.OrderVertexSet();
		 * 
		 * logger.log(Level.INFO, "Maximum-Weight Clique: Starting Search");
		 * mclique.Wnew();
		 * 
		 * logger.log(Level.INFO, "Maximum-Weight Clique is :" +
		 * mclique.GetHeaviestClique()); logger.log(Level.INFO, "With weight :"
		 * + mclique.sumWeight(mclique.GetHeaviestClique()));
		 */

		SimpleGraph<MemoryExclusionGraphNode, DefaultEdge> meminc = memex
				.GetComplementary();
		logger.log(Level.INFO, "Memory Inclusion Graph built");

		mclique = new MaximumWeightCliqueSolver<MemoryExclusionGraphNode, DefaultEdge>(
				meminc);

		logger.log(Level.INFO,
				"Maximum-Weight Stable Set: Starting Pre-Ordering");
		mclique.OrderVertexSet();

		logger.log(Level.INFO, "Maximum-Weight Stable Set: Starting Search");
		mclique.Wnew();

		logger.log(Level.INFO,
				"Maximum-Weight Stable is: " + mclique.GetHeaviestClique());
		logger.log(
				Level.INFO,
				"With weight :"
						+ mclique.sumWeight(mclique.GetHeaviestClique()));

		/*
		 * // Test prog for MaxCliqueProbSolv MemoryExclusionGraph memex = new
		 * MemoryExclusionGraph(); MemoryExclusionGraphNode n1 = new
		 * MemoryExclusionGraphNode("A", "A", 1); MemoryExclusionGraphNode n2 =
		 * new MemoryExclusionGraphNode("B", "B", 2); MemoryExclusionGraphNode
		 * n3 = new MemoryExclusionGraphNode("C", "C", 3);
		 * MemoryExclusionGraphNode n4 = new MemoryExclusionGraphNode("D", "D",
		 * 7);
		 * 
		 * memex.addVertex(n1); memex.addVertex(n2); memex.addVertex(n3);
		 * memex.addVertex(n4);
		 * 
		 * memex.addEdge(n1, n2); memex.addEdge(n1, n3); memex.addEdge(n3, n2);
		 * memex.addEdge(n1, n4);
		 * 
		 * MaximumWeightCliqueSolver<MemoryExclusionGraphNode, DefaultEdge>
		 * mclique;
		 * 
		 * mclique = new MaximumWeightCliqueSolver<MemoryExclusionGraphNode,
		 * DefaultEdge>( memex);
		 * 
		 * mclique.OrderVertexSet();
		 * 
		 * mclique.Wnew();
		 * 
		 * logger.log(Level.INFO, "Maximum-Weight Clique is :" +
		 * mclique.GetHeaviestClique());
		 */
		return outputs;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Optimizing memory.";
	}
}
