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
			memex.buildGraph(localDAG);
		} catch (InvalidExpressionException e) {
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
				

		//OstergardSolver<MemoryExclusionGraphNode, DefaultEdge> mclique;
		
		
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
		
		/*

		SimpleGraph<MemoryExclusionGraphNode, DefaultEdge> meminc = memex
				.GetComplementary();
		logger.log(Level.INFO, "Memory Inclusion Graph built");

		mclique = new OstergardSolver<MemoryExclusionGraphNode, DefaultEdge>(
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
						*/

		/*
		// Test prog for MaxCliqueProbSolv 
		MemoryExclusionGraph memex = new MemoryExclusionGraph();
		MemoryExclusionGraphNode n1 = new MemoryExclusionGraphNode("A", "A", 1);
		MemoryExclusionGraphNode n2 = new MemoryExclusionGraphNode("B", "B", 2);
		MemoryExclusionGraphNode n3 = new MemoryExclusionGraphNode("C", "C", 2);
		MemoryExclusionGraphNode n4 = new MemoryExclusionGraphNode("D", "D", 4);
		MemoryExclusionGraphNode n5 = new MemoryExclusionGraphNode("E", "E", 7);

		memex.addVertex(n1); 
		memex.addVertex(n2); 
		memex.addVertex(n3);
		memex.addVertex(n4);
		memex.addVertex(n5);

		memex.addEdge(n1, n2);
		memex.addEdge(n1, n3);
		memex.addEdge(n1, n4);
		memex.addEdge(n1, n5);
		memex.addEdge(n2, n3);
		memex.addEdge(n3, n4);
		
*/
		/*
		
		YamaguchiSolver<MemoryExclusionGraphNode, DefaultEdge>
		mclique;

		mclique = new YamaguchiSolver<MemoryExclusionGraphNode,
				DefaultEdge>( memex);
		
		logger.log(Level.INFO, "Yama Start");
		mclique.Solve();
		logger.log(Level.INFO, "Yama Stop");
				
		logger.log(Level.INFO, "yamabis" + mclique.GetHeaviestClique());
		logger.log(Level.INFO, "yamabis" + mclique.max);
		*/
		
		
		
		/*
		
		OstergardSolver<MemoryExclusionGraphNode, DefaultEdge> mclique2;
		
		mclique2 = new OstergardSolver<MemoryExclusionGraphNode, DefaultEdge>(memex);
		
		logger.log(Level.INFO, "Ost Start");
		mclique2.Solve();
		logger.log(Level.INFO, "Ost Stop");
		*/
		/*
		
		YamaguchiTerSolver<MemoryExclusionGraphNode, DefaultEdge>
		mclique3;

		mclique3 = new YamaguchiTerSolver<MemoryExclusionGraphNode,
				DefaultEdge>( memex);
		
		logger.log(Level.INFO, "Yama Start");
		mclique3.Solve();
		logger.log(Level.INFO, "Yama Stop");
		
		logger.log(Level.INFO, "yamater" + mclique3.GetHeaviestClique());
		logger.log(Level.INFO, "yamater" + mclique3.max);
		*/
		
		//logger.log(Level.INFO, "1" + mclique.GetHeaviestClique());
		//logger.log(Level.INFO, "OST" + mclique2.GetHeaviestClique());
		//logger.log(Level.INFO, "OST" + mclique2.max);

	
		//mclique.Wnew();

		//logger.log(Level.INFO, "Maximum-Weight Clique is :" +
		//		mclique.GetHeaviestClique());
		/*
		YamaguchiSolver<MemoryExclusionGraphNode, DefaultEdge> mcliqueHybr1;
		mcliqueHybr1 = new YamaguchiSolver<MemoryExclusionGraphNode, DefaultEdge>(memex);
		
		OstergardSolver<MemoryExclusionGraphNode, DefaultEdge> mcliqueHybr2;
		mcliqueHybr2 = new OstergardSolver<MemoryExclusionGraphNode, DefaultEdge>(memex);
		mcliqueHybr2.OrderVertexSet();
		
		HashSet<MemoryExclusionGraphNode> vertexSet = new HashSet<MemoryExclusionGraphNode>();
		vertexSet.addAll(memex.vertexSet());
		
		ArrayList<Integer> cost = new ArrayList<Integer>();
		mcliqueHybr1.OrderVertexSet(vertexSet, cost);
		
		for(int i = cost.size() -1 ; i>=0 && mcliqueHybr2.GetHeaviestClique().isEmpty() ; i--)
		{
			logger.log(Level.INFO, "Cost : " + cost.get(i));
			mcliqueHybr2.max = cost.get(i);
			mcliqueHybr2.Wnew();
		}
		
		logger.log(Level.INFO, "OST : " + mcliqueHybr2.GetHeaviestClique());
		logger.log(Level.INFO, "OST : " + mcliqueHybr2.max);
		*/
		
		HybridSolver<MemoryExclusionGraphNode, DefaultEdge> mcliqueHybr;
		mcliqueHybr = new HybridSolver<MemoryExclusionGraphNode, DefaultEdge>(memex);
		
		logger.log(Level.INFO, "Hybrid Start");
		mcliqueHybr.solve();
		logger.log(Level.INFO, "Hybrid Stop");
		
		logger.log(Level.INFO, "Hybrid" + mcliqueHybr.getHeaviestClique());
		logger.log(Level.INFO, "Hybrid" + mcliqueHybr.max);
		
		 
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
