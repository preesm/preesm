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
import org.ietr.preesm.plugin.codegen.memory.FirstFitAllocator.Policy;
import org.jgrapht.graph.DefaultEdge;
import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;

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

		// // Test prog for MemoryAllocators
		// localDAG = new DirectedAcyclicGraph();
		// DAGDefaultVertexPropertyType prop = new
		// DAGDefaultVertexPropertyType(1);
		//
		// DAGVertex v1 = new DAGVertex("A",prop,prop);
		// v1.setName("A");
		// v1.getPropertyBean().setValue("vertexType", "task");
		//
		//
		// DAGVertex v2 = new DAGVertex("B",prop,prop);
		// v2.setName("B");
		// v2.getPropertyBean().setValue("vertexType", "task");
		//
		// DAGVertex v3 = new DAGVertex("C",prop,prop);
		// v3.setName("C");
		// v3.getPropertyBean().setValue("vertexType", "task");
		//
		// DAGVertex v4 = new DAGVertex("D",prop,prop);
		// v4.setName("D");
		// v4.getPropertyBean().setValue("vertexType", "task");
		//
		// DAGVertex v5 = new DAGVertex("E",prop,prop);
		// v5.setName("E");
		// v5.getPropertyBean().setValue("vertexType", "task");
		//
		// DAGVertex v6 = new DAGVertex("F",prop,prop);
		// v6.setName("F");
		// v6.getPropertyBean().setValue("vertexType", "task");
		//
		// localDAG.addVertex(v1);
		// localDAG.addVertex(v2);
		// localDAG.addVertex(v3);
		// localDAG.addVertex(v4);
		// localDAG.addVertex(v5);
		// localDAG.addVertex(v6);
		//
		//
		// (localDAG.addEdge(v1, v2)).setWeight(new
		// DAGDefaultEdgePropertyType(200));
		// (localDAG.addEdge(v2, v3)).setWeight(new
		// DAGDefaultEdgePropertyType(50));
		// (localDAG.addEdge(v1, v4)).setWeight(new
		// DAGDefaultEdgePropertyType(50));
		// (localDAG.addEdge(v4, v3)).setWeight(new
		// DAGDefaultEdgePropertyType(50));
		// (localDAG.addEdge(v3, v5)).setWeight(new
		// DAGDefaultEdgePropertyType(150));
		// (localDAG.addEdge(v3, v6)).setWeight(new
		// DAGDefaultEdgePropertyType(100));

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

		/*
		 * 
		 * SimpleGraph<MemoryExclusionGraphNode, DefaultEdge> meminc = memex
		 * .getComplementary(); logger.log(Level.INFO,
		 * "Memory Inclusion Graph built");
		 * 
		 * OstergardSolver<MemoryExclusionGraphNode, DefaultEdge> mclique = new
		 * OstergardSolver<MemoryExclusionGraphNode, DefaultEdge>( meminc);
		 * 
		 * logger.log(Level.INFO, "Maximum-Weight Stable Set: Starting Search");
		 * mclique.solve();
		 * 
		 * logger.log(Level.INFO, "Maximum-Weight Stable is: " +
		 * mclique.getHeaviestClique()); logger.log( Level.INFO, "With weight :"
		 * + mclique.sumWeight(mclique.getHeaviestClique()));
		 */

//		// Test prog for MaxCliqueProbSolv 
//		memex = new	MemoryExclusionGraph();
//		MemoryExclusionGraphNode n1 = new MemoryExclusionGraphNode("A", "A", 200);
//		MemoryExclusionGraphNode n2 = new MemoryExclusionGraphNode("B", "B", 50);
//		MemoryExclusionGraphNode n3 = new MemoryExclusionGraphNode("C", "C", 50);
//		MemoryExclusionGraphNode n4 = new MemoryExclusionGraphNode("D", "D", 50);
//		MemoryExclusionGraphNode n5 = new MemoryExclusionGraphNode("E", "E", 150);
//		MemoryExclusionGraphNode n6 = new MemoryExclusionGraphNode("F", "F", 100);
//
//		memex.addVertex(n1);
//		memex.addVertex(n2);
//		memex.addVertex(n3);
//		memex.addVertex(n4);
//		memex.addVertex(n5);
//		memex.addVertex(n6);
//
//		memex.addEdge(n1, n2);
//		memex.addEdge(n1, n3);
//		memex.addEdge(n1, n4);
//		
//		memex.addEdge(n2, n3);
//		memex.addEdge(n2, n4);
//		
//		memex.addEdge(n3, n4);
//		memex.addEdge(n3, n5);
//		memex.addEdge(n3, n6);
//		
//		memex.addEdge(n4, n5);
//		memex.addEdge(n4, n6);
//		
//		memex.addEdge(n5, n6);
		
		// Test of heuristicSolver
		//for(int iter = 0; iter < 5; iter++){
		//Prepare the exclusionGraph
		WeightedGraphGenerator gGen = new WeightedGraphGenerator();
		gGen.setNumberVertices(500);
		gGen.setEdgeDensity(0.83);
		gGen.setMinimumWeight(1001);
		gGen.setMaximumWeight(1010);
		memex = gGen.generateMemoryExclusionGraph();
		
		HeuristicSolver<MemoryExclusionGraphNode, DefaultEdge> hSolver = new HeuristicSolver<MemoryExclusionGraphNode, DefaultEdge>(memex);
		
		logger.log(Level.INFO, "Heur Start");
		long tStart = System.currentTimeMillis();
		hSolver.solve();
		long tStop = System.currentTimeMillis();
		logger.log(Level.INFO, "Heur Stop :"  + hSolver.sumWeight(hSolver.getHeaviestClique()) + " in " + (tStop - tStart) + " OK ? " + hSolver.checkClique(hSolver.getHeaviestClique()));
		long theur = tStop-tStart;
		

		
		MaximumWeightCliqueSolver<MemoryExclusionGraphNode, DefaultEdge> solver;
		solver = new OstergardSolver<MemoryExclusionGraphNode, DefaultEdge>(
				memex);
		
		logger.log(Level.INFO, "Yama Start");
		tStart = System.currentTimeMillis();
		//solver.solve();
		tStop = System.currentTimeMillis();
		logger.log(Level.INFO, "Yama Stop :"  + solver.sumWeight(solver.getHeaviestClique()) + " in " + (tStop - tStart));
		solver.sumWeight(memex.vertexSet());

		logger.log(Level.INFO, "Before : " + memex.vertexSet().size() +" With density "+ memex.edgeSet().size()*2.0/(double)(memex.vertexSet().size()*(memex.vertexSet().size()-1)));
		long tSStart = System.currentTimeMillis();
		memex.simplifier();
		long tSStop = System.currentTimeMillis();
		logger.log(Level.INFO, "Then : " + memex.vertexSet().size() +" With density "+ memex.edgeSet().size()*2.0/(double)(memex.vertexSet().size()*(memex.vertexSet().size()-1)));
		memex.simplifierTwo();
		logger.log(Level.INFO, "Simplified : " + memex.vertexSet().size() + " With density " + memex.edgeSet().size()*2.0/(double)(memex.vertexSet().size()*(memex.vertexSet().size()-1)));

		solver = new OstergardSolver<MemoryExclusionGraphNode, DefaultEdge>(
				memex);
		
		solver.sumWeight(memex.vertexSet());

		logger.log(Level.INFO, "Start");
		tStart = System.currentTimeMillis();
		//solver.solve();
		tStop = System.currentTimeMillis();

		logger.log(Level.INFO,
				"Stop :" + solver.sumWeight(solver.getHeaviestClique()) + " in " + (tStop - tStart) +"+" + (tSStop - tSStart));
		System.out.print("Heur ;"  + hSolver.sumWeight(hSolver.getHeaviestClique()) + "; in ;" + theur );
		System.out.println(";Ost ;;;;"  + solver.sumWeight(solver.getHeaviestClique()) + "; in ;" + (tStop - tStart) +";"+(tSStop - tSStart) );
		//}

		BasicAllocator baAlloc = new BasicAllocator(localDAG);
		
		baAlloc.allocate();
		System.out.print("worst : "+ baAlloc.getMemorySize());
		
		
		
		
		
// //TEST of Allocators !		
//		int densiter = 0;
//		String tests = "Nb Nodes;Density;Min Weight;Max Weight;Max Bound;Min Bound;Custom Size;Custom Time;Improved Size;Improved Time;DeGreef Size;DeGreef time;Stable Set Size;StableSet Time;Largest Size;Largest Time;Random Best Size;Random Mediane;Random Time;NB Shuffle;Nb Better\n";
//		for(double density = 0.9;density>0.05;density-=0.1){
//			logger.log(Level.INFO,"DENSITY :" + density);
//			for(int iter = 0;iter<250;iter++){
//				logger.log(Level.INFO,"Iter :" + iter);
//				String iterResult = testAllocator(50, density, 1000, 1001, 1010);
//				System.out.print(iterResult);
//				//logger.log(Level.INFO,"Iter :" + iterResult);
//				tests +=  iterResult;
//			}
//			
//			PrintWriter ecrivain;
//			try {
//				ecrivain = new PrintWriter(new BufferedWriter(new FileWriter(
//						"C:/TEMP/STATS"+densiter+".csv")));
//				
//				ecrivain.println(tests);
//				ecrivain.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			densiter++;
//			
//			
//		tests+="\n";
//		System.out.print("");
//		}
//		logger.log(Level.INFO,tests);
//		
//		System.out.print(tests);
//		
//		PrintWriter ecrivain;
//		try {
//			ecrivain = new PrintWriter(new BufferedWriter(new FileWriter(
//					"C:/TEMP/STATS.csv")));
//			
//			ecrivain.println(tests);
//			ecrivain.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		

//		long tStart;
//		long tFinish;
//
//		WeightedGraphGenerator gGen = new WeightedGraphGenerator();
//
//		// for(int aa=0; aa < 250 ; aa+=10){
//
//		gGen.setNumberVertices(90);
//		gGen.setEdgeDensity(0.2);
//		gGen.setMinimumWeight(1);
//		gGen.setMaximumWeight(1010);
//		int numShuffle = 10000;
//
//		for (int ii = 0; ii < 3; ii++) {
//			// logger.log(Level.INFO, "NODE : " + aa + " ITER  : " + ii);
//
//			memex = gGen.generateMemoryExclusionGraph();
//
//			memex.saveToFile("C:/TEMP/graph.csv");
//
//			OstergardSolver<MemoryExclusionGraphNode, DefaultEdge> ostSolver;
//			ostSolver = new OstergardSolver<MemoryExclusionGraphNode, DefaultEdge>(
//					memex);
//
//			YamaguchiSolver<MemoryExclusionGraphNode, DefaultEdge> yamaSolver;
//			yamaSolver = new YamaguchiSolver<MemoryExclusionGraphNode, DefaultEdge>(
//					memex);
//
//			HybridSolver<MemoryExclusionGraphNode, DefaultEdge> hybrSolver;
//			hybrSolver = new HybridSolver<MemoryExclusionGraphNode, DefaultEdge>(
//					memex);

			/*
			 * logger.log(Level.INFO, "Yama Start"); tStart =
			 * System.currentTimeMillis(); yamaSolver.solve(); tFinish =
			 * System.currentTimeMillis();
			 * 
			 * logger.log(Level.INFO, "Yama [" + (tFinish - tStart) + "]");
			 * logger.log(Level.INFO, "Yama " +
			 * yamaSolver.sumWeight(yamaSolver.getHeaviestClique()));
			 * 
			 * 
			 * logger.log(Level.INFO, "Ost Start"); tStart =
			 * System.currentTimeMillis(); ostSolver.solve(); tFinish =
			 * System.currentTimeMillis();
			 * 
			 * logger.log(Level.INFO, "Ost [" + (tFinish - tStart) + "]");
			 * logger.log(Level.INFO, "Ost " +
			 * ostSolver.sumWeight(ostSolver.getHeaviestClique()));
			 * 
			 * 
			 * 
			 * logger.log(Level.INFO, "Hybrr Start"); tStart =
			 * System.currentTimeMillis(); //if(aa<70) hybrSolver.solve();
			 * tFinish = System.currentTimeMillis();
			 * 
			 * logger.log(Level.INFO, "Hybr [" + (tFinish - tStart) + "]");
			 * logger.log(Level.INFO, "Hybr " +
			 * hybrSolver.sumWeight(hybrSolver.getHeaviestClique())); //} //}
			 */

			// logger.log(Level.INFO, "There are " + memex.vertexSet().size()
			// + " memory transfers.");
			// logger.log(Level.INFO, "There are " + memex.edgeSet().size()
			// + " exclusions.");

//			double maxEdges = memex.vertexSet().size()
//					* (memex.vertexSet().size() - 1) / 2.0;
//
//			double density = memex.edgeSet().size() / maxEdges;
//
//			// logger.log(Level.INFO, "The edge density of the graph is "
//			// + density + ".");
//
//			BasicAllocator baAlloc = new BasicAllocator(memex);
//
//			baAlloc.allocate();
//			int worst = baAlloc.getMemorySize();
//
//			// logger.log(Level.INFO,
//			// "Maximum Memory Size is : "+baAlloc.getMemorySize());
//
//			// logger.log(Level.INFO, "Custom Alloc Start");
//			MemoryAllocator cuAlloc = new CustomAllocator(memex);
//
//			cuAlloc.allocate();
//			int custom = cuAlloc.getMemorySize();
//			// logger.log(Level.INFO, "Custom : "+cuAlloc.getMemorySize());
//
//			// logger.log(Level.INFO, "Improved Custom Alloc Start");
//			cuAlloc = new ImprovedCustomAllocator(memex);
//
//			cuAlloc.allocate();
//			int customImproved = cuAlloc.getMemorySize();

			// logger.log(Level.INFO,
			// "Improved Custom : "+cuAlloc.getMemorySize());
			// logger.log(Level.INFO, ""+((1.0 -
			// (double)cuAlloc.getMemorySize()/(double)baAlloc.getMemorySize())*100.0)
			// + "% was saved over worst allocation possible.");
			// logger.log(Level.INFO,"Allocation check : " +
			// ((cuAlloc.checkAllocation().isEmpty())?"OK":"Problem"));

			// HybridSolver<MemoryExclusionGraphNode, DefaultEdge> hybrSolver;
//			MaximumWeightCliqueSolver<MemoryExclusionGraphNode, DefaultEdge> solver;
//			if (density > 0.85) {
//				solver = new YamaguchiSolver<MemoryExclusionGraphNode, DefaultEdge>(
//						memex);
//			} else {
//				solver = new OstergardSolver<MemoryExclusionGraphNode, DefaultEdge>(
//						memex);
//			}
//
//			if (memex.vertexSet().size() < 150) {
//				solver.solve();
//			}
//
//			int limit = hybrSolver.sumWeight(solver.getHeaviestClique());

			// logger.log(Level.INFO, ""+
			// ((double)cuAlloc.getMemorySize()/(double)hybrSolver.sumWeight(solver.getHeaviestClique()))
			// + "x the minimum limit.");

			// FirstFitAllocator ffAlloc = new FirstFitAllocator(memex);
			// ffAlloc.setNbShuffle(numShuffle);
			// ffAlloc.allocate();
			//
			// logger.log(Level.INFO, "First-Fit gives  : ");
			//
			// ffAlloc.setPolicy(FirstFitAllocator.Policy.worst);
			// logger.log(Level.INFO, "Worst gives  : "+
			// ffAlloc.getMemorySize());
			// ffAlloc.setPolicy(FirstFitAllocator.Policy.best);
			// logger.log(Level.INFO, "Best gives  : "+
			// ffAlloc.getMemorySize());
			// ffAlloc.setPolicy(FirstFitAllocator.Policy.average);
			// logger.log(Level.INFO, "Average gives  : "+
			// ffAlloc.getMemorySize());
			// ffAlloc.setPolicy(FirstFitAllocator.Policy.mediane);
			// logger.log(Level.INFO, "Mediane gives  : "+
			// ffAlloc.getMemorySize());
			// ffAlloc.setPolicy(FirstFitAllocator.Policy.largestFirst);
			// logger.log(Level.INFO, "Largest First gives  : "+
			// ffAlloc.getMemorySize());
			// ffAlloc.setPolicy(FirstFitAllocator.Policy.smallestFirst);
			// logger.log(Level.INFO, "Smallest First gives  : "+
			// ffAlloc.getMemorySize());
			// ffAlloc.setPolicy(FirstFitAllocator.Policy.stableSetOrder);
			// logger.log(Level.INFO, "StableSet order gives  : "+
			// ffAlloc.getMemorySize());

//			BestFitAllocator bfAlloc = new BestFitAllocator(memex);
//			bfAlloc.setNbShuffle(numShuffle);
			
			
			// bfAlloc.allocate();

			// logger.log(Level.INFO, "Best-Fit gives  : ");

//			bfAlloc.setPolicy(FirstFitAllocator.Policy.best);
//			bfAlloc.allocate();
//			int bfBest = bfAlloc.getMemorySize();
			
			
			// logger.log(Level.INFO, "Best gives  : "+
			// bfAlloc.getMemorySize());
			// bfAlloc.setPolicy(FirstFitAllocator.Policy.average);
			// logger.log(Level.INFO, "Average gives  : "+
			// bfAlloc.getMemorySize());
			
//			bfAlloc.allocateLargestFirst();
//			int bfLargest = bfAlloc.getMemorySize();
//
//			bfAlloc.allocateStableSetOrder();
//			int bfStable = bfAlloc.getMemorySize();
//
//			DeGreefAllocator dgAlloc = new DeGreefAllocator(memex);
//
//			dgAlloc.allocate();
//			int dg = dgAlloc.getMemorySize();

			// logger.log(Level.INFO, "First-F is  : "+
			// (double)bfAlloc.getMemorySize()/(double)cuAlloc.getMemorySize() +
			// "x the custom Alloc");

//			logger.log(
//					Level.INFO,
//					"There are " + bfAlloc.getNumberBetter(bfStable)
//							+ " better solution (" + 100
//							* (double) bfAlloc.getNumberBetter(bfStable)
//							/ (double) numShuffle + "%)");
			
			// logger.log(Level.INFO,"There is " +
			// (bfAlloc.getNumberBetter(cuAlloc.getMemorySize()+1) -
			// bfAlloc.getNumberBetter(cuAlloc.getMemorySize())) +
			// " equals solution ");

//			logger.log(Level.INFO, "\nWorst :\t\t" + worst + "\tLimit :\t\t"
//					+ limit + "\nCustom :\t" + custom + "\tImproved :\t"
//					+ customImproved + "\tDeGreef :\t" + dg + "\nStableSet :\t"
//					+ bfStable + "\tLargest First :\t" + bfLargest
//					+ "\tBest BestFit :\t" + bfBest);

		//}// for ii

		// Those two line will probably corrupt the allocation
		// DAGEdge edge = cuAlloc.getAllocation().keySet().iterator().next();
		// cuAlloc.getAllocation().put(edge,
		// cuAlloc.getAllocation().get(edge)+1);

		// logger.log(Level.INFO,"Allocation check : " +
		// ((cuAlloc.checkAllocation().isEmpty())?"OK":"Problem"));

		return outputs;
	}

	/**
	 * 
	 * @param nbNodes
	 * @param density
	 * @param nbShuffle
	 * @param minWeight
	 * @param maxWeight
	 * @return Results in form :<br>
	 *         <table border>
	 *         <tr>
	 *         <td>nbNodes</td>
	 *         <td>density</td>
	 *         <td>minWeight</td>
	 *         <td>maxWeight</td>
	 *         <td>worstSize</td>
	 *         <td>minBound</td>
	 *         <td>customSize</td>
	 *         <td>customTime</td>
	 *         <td>improvedSize</td>
	 *         <td>improvedTime</td>
	 *         <td>DeGreefSize</td>
	 *         <td>DeGreefTime</td>
	 *         <td>StableSetSize</td>
	 *         <td>StableSetTime</td>
	 *         <td>LargestSize</td>
	 *         <td>LargestTime</td>
	 *         <td>RandomBestSize</td>
	 *         <td>RandomMedianeSize</td>
	 *         <td>RandomTime</td>
	 *         <td>RandomNbShuffle</td>
	 *         <td>RandomBetterThanStableSet</td>
	 *         </tr>
	 *         </table>
	 */
	public String testAllocator(int nbNodes, double density, int nbShuffle,
			int minWeight, int maxWeight) {

		String result = "" + nbNodes + ";" + density + ";" + minWeight + ";"
				+ maxWeight + ";";
		long tStart;
		long tFinish;

		// Prepare the exclusionGraph
		WeightedGraphGenerator gGen = new WeightedGraphGenerator();
		gGen.setNumberVertices(nbNodes);
		gGen.setEdgeDensity(density);
		gGen.setMinimumWeight(minWeight);
		gGen.setMaximumWeight(maxWeight);
		MemoryExclusionGraph memex = gGen.generateMemoryExclusionGraph();

		MemoryAllocator memAlloc;

		memAlloc = new BasicAllocator(memex);
		memAlloc.allocate();
		result += memAlloc.getMemorySize() + ";";

		MaximumWeightCliqueSolver<MemoryExclusionGraphNode, DefaultEdge> solver;
		if (density > 0.85) {
			solver = new OstergardSolver<MemoryExclusionGraphNode, DefaultEdge>(
					memex);
		} else {
			solver = new OstergardSolver<MemoryExclusionGraphNode, DefaultEdge>(
					memex);
		}

		if (memex.vertexSet().size() < 120) {
			//solver.solve();
		}
		result += solver.sumWeight(solver.getHeaviestClique()) + ";";

		memAlloc = new CustomAllocator(memex);
		tStart = System.currentTimeMillis();
		memAlloc.allocate();
		tFinish = System.currentTimeMillis();
		result += memAlloc.getMemorySize() + ";" + (tFinish - tStart) + ";";
		
		memAlloc = new ImprovedCustomAllocator(memex);
		tStart = System.currentTimeMillis();
		memAlloc.allocate();
		tFinish = System.currentTimeMillis();
		result += memAlloc.getMemorySize() + ";" + (tFinish - tStart) + ";";
		
		memAlloc = new DeGreefAllocator(memex);
		tStart = System.currentTimeMillis();
		memAlloc.allocate();
		tFinish = System.currentTimeMillis();
		result += memAlloc.getMemorySize() + ";" + (tFinish - tStart) + ";";
		
		memAlloc = new BestFitAllocator(memex);
		tStart = System.currentTimeMillis();
		((BestFitAllocator)memAlloc).allocateStableSetOrder();
		tFinish = System.currentTimeMillis();
		result += memAlloc.getMemorySize() + ";" + (tFinish - tStart) + ";";
		int referenceComparison = memAlloc.getMemorySize();
		
		memAlloc = new BestFitAllocator(memex);
		tStart = System.currentTimeMillis();
		((BestFitAllocator)memAlloc).allocateLargestFirst();
		tFinish = System.currentTimeMillis();
		result += memAlloc.getMemorySize() + ";" + (tFinish - tStart) + ";";
		
		memAlloc = new BestFitAllocator(memex);
		((BestFitAllocator)memAlloc).setNbShuffle(nbShuffle);
		((BestFitAllocator)memAlloc).setPolicy(Policy.best);
		tStart = System.currentTimeMillis();
		memAlloc.allocate();
		tFinish = System.currentTimeMillis();
		result += memAlloc.getMemorySize() + ";";
		((BestFitAllocator)memAlloc).setPolicy(Policy.mediane);
		result += memAlloc.getMemorySize() + ";" + (tFinish - tStart) + ";";
		result += nbShuffle + ";";
		result += ((BestFitAllocator)memAlloc).getNumberBetter(referenceComparison)+";\n";

		return result;

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
