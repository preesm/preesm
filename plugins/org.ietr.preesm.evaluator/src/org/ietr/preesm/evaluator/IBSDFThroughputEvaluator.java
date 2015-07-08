package org.ietr.preesm.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.preesm.core.scenario.PreesmScenario;

/**
 * Class used to search for the optimal periodic schedule and its throughput
 * for a given IBSDF
 * 
 * @author blaunay
 * 
 */
public class IBSDFThroughputEvaluator extends ThroughputEvaluator{
	
	/**
	 * Computes (so not necessarily optimally) the throughput on the optimal
	 * periodic schedule (if it exists) of a given graph under the given scenario
	 * 
	 */
	public double launch(SDFGraph inputGraph, PreesmScenario scenario) throws InvalidExpressionException {
		//TODO Step 1 : compute K_min = max {K_g forall g in G}
		double Kmin = 0;
		double K = 0;
		double eps = 0.01;	// precision of the solution
		
		// Step 2 : Test if k_min a valid period for the graph test_period(K_min,G)
		if (test_period(Kmin, inputGraph)) {
			K = Kmin;			
		} else {			
			// Step 3 : Find a value for K_max
			double Kmax = 50 * Kmin; //TODO tune the coeffs
			// increase Kmax until it is a valid period
			while (!test_period(Kmax, inputGraph)) {
				Kmin = Kmax; 
				Kmax *= 5;  //TODO tune the coeffs
			}
			K = Kmax;
			// Step 4 : Improve (minimize) K
			while (Kmax - Kmin > eps) {
				K = (Kmax + Kmin) / 2;
				if (test_period(K, inputGraph))
					Kmax = K; // continue to search on the interval [Kmin,K]
				else {
					Kmin = K; // continue to search on the interval [K,Kmax]
					K = Kmax;
				}
			}
		}
		return throughput_computation(K, inputGraph);
	}

	
	private boolean test_period(double K, SDFGraph G) {
		return (negative_circuit(G,K) != null);
	}
	
	
	/*TODO
	 * Add looping edges on all the actors before valuation
	 * Add looping edges on the interfaces (replace by actors ? meh)
	 */
	
	/**
	 * Checks that the given graph (containing several levels of hierarchy) does not
	 * contain negative circuits once its edges values by L-KH, which is the condition 
	 * for it to have a periodic schedule of normalized period K.
	 * 
	 * @return null if the condition not respected
	 */
	private HashMap<String, HashMap<String, Double>> negative_circuit(SDFGraph g, double K) {
		
		// The set of edges that will be used to compute shortest paths
		HashMap<SDFEdge,Double> e = new HashMap<SDFEdge,Double>(g.edgeSet().size());
		// The set of vertices used to compute shortest paths
		HashMap<String,Double> v = new HashMap<String,Double>();
		// Contains the results of the shortest paths
		HashMap<String, HashMap<String,Double>> dist = new HashMap<String, HashMap<String,Double>>();
		
		// Value all arcs of this level with L - K * H
		for (SDFEdge edge : g.edgeSet()) {
				e.put(edge, ((double)(edge.getDelay().getValue())
						+ SDFMathD.gcd((double)(edge.getCons().getValue()), (double)(edge.getProd().getValue()))
						- (double)(edge.getCons().getValue())));
		}		
		
		// We need a copy of the set of vertices, since we will add vertices in the original set 
		// while going through its elements
		Set<SDFAbstractVertex> vertexSetCopy = new HashSet<SDFAbstractVertex>(g.vertexSet());
		for (SDFAbstractVertex vertex : vertexSetCopy) {
			// For each hierarchical actor 
			if (vertex.getGraphDescription() != null
					&& vertex.getGraphDescription() instanceof SDFGraph) {
				
				// compute shortest paths between its in/out ports
				dist = negative_circuit((SDFGraph) vertex.getGraphDescription(), K);
				
				// if null, then subgraph not alive, so the whole graph is not.
				if (dist == null)
					return null;
				else {					
				// Create new nodes corresponding to the interfaces
					for (String input : dist.keySet()) {
					// Create a new vertex for each new input interface
						SDFVertex VertexIn = new SDFVertex();
						VertexIn.setName(input);
					// Create a new port for the incoming edge
						SDFSourceInterfaceVertex inPortIN = new SDFSourceInterfaceVertex();
						inPortIN.setName("in");
						VertexIn.addSource(inPortIN);
					// Add it to the graph
						g.addVertex(VertexIn);
					// Create the new incoming edge of this node
						SDFEdge EdgeToIn = g.addEdge(vertex.getAssociatedEdge(vertex.getInterface(input)).getSource(), VertexIn);
						EdgeToIn.setSourceInterface(vertex.getAssociatedEdge(vertex.getInterface(input)).getSourceInterface());
						EdgeToIn.setTargetInterface(inPortIN);
					// Put it on the list for the BellmanFord algo, remove the ancient one
						e.put(EdgeToIn, e.get(vertex.getAssociatedEdge(vertex.getInterface(input))));
						e.remove(vertex.getAssociatedEdge(vertex.getInterface(input)));
						
						// New node for each output interface
						for (String output : dist.get(input).keySet()) {
							SDFVertex VertexOut = (SDFVertex) g.getVertex(output);
							if (VertexOut == null) {
							// Create vertex out only if it does not exist already
								VertexOut = new SDFVertex();
								VertexOut.setName(output);
							// Create a new port port for the outgoing edge
								SDFSinkInterfaceVertex outPortOUT = new SDFSinkInterfaceVertex();
								outPortOUT.setName("out");
								VertexOut.addSink(outPortOUT);
								g.addVertex(VertexOut);
							// Create the edge going from the node out
								SDFEdge EdgeFromOut = g.addEdge(VertexOut, vertex.getAssociatedEdge(vertex.getInterface(output)).getTarget());
								EdgeFromOut.setSourceInterface(VertexOut.getSink("out"));
								EdgeFromOut.setTargetInterface(vertex.getAssociatedEdge(vertex.getInterface(output)).getTargetInterface());
							// Put it on the list for the BellmanFord algo, remove the ancient one
								e.put(EdgeFromOut, 	e.get(vertex.getAssociatedEdge(vertex.getInterface(output))));
								e.remove(vertex.getAssociatedEdge(vertex.getInterface(output)));
							} 
						// Create the edge linking the new in and out
							SDFEdge EdgeInOut = g.addEdge(VertexIn, VertexOut);
						// port of origin of this edge
							SDFSinkInterfaceVertex outPortIN = new SDFSinkInterfaceVertex();
							outPortIN.setName(output);
							VertexIn.addSink(outPortIN);
							EdgeInOut.setSourceInterface(outPortIN);
						// target port of this edge
							SDFSourceInterfaceVertex inPortOUT = new SDFSourceInterfaceVertex();
							inPortOUT.setName(VertexIn.getName());
							VertexOut.addSource(inPortOUT);
							EdgeInOut.setTargetInterface(inPortOUT);
						// new edge to use for BellmanFord
							e.put(EdgeInOut, dist.get(input).get(output));
						// new vertices to consider for BellmanFord
							v.put(VertexIn.getName(),  Double.POSITIVE_INFINITY);
							v.put(VertexOut.getName(),  Double.POSITIVE_INFINITY);
						}
					}
				}
				// Remove the hierarchical actor from the graph
				g.removeVertex(vertex);
			} else {
				// not a hierarchical actor
				v.put(vertex.getName(),  Double.POSITIVE_INFINITY);
			}
			// clear the map of distances, reused for the next hierarchical actor
			dist.clear();
		}
		
		ArrayList<SDFAbstractVertex> origin;
		
		//when at level zero
		if (g.getParentVertex() == null) {
			// pick a random source node
			origin = new ArrayList<SDFAbstractVertex>();
			origin.add(g.vertexSet().iterator().next());
		} else {
			// otherwise, source nodes of the shortest paths to compute are all the input interfaces
			origin = new ArrayList<SDFAbstractVertex>(new ArrayList<SDFInterfaceVertex>(g.getParentVertex().getSources()));
		}
		
		// BellmanFord from each input
		for (SDFAbstractVertex input : origin) {
			// Source node for the shortest path
			v.put(input.getName(), (double) 0);
			
			// Relaxation
			for (int i=1; i<=v.size()-1; i++) {
				for (Map.Entry<SDFEdge, Double> entry : e.entrySet()) {
					if (v.get(entry.getKey().getSource().getName()) + entry.getValue() < v.get(entry.getKey().getTarget().getName())) {
						v.put(entry.getKey().getTarget().getName(), v.get(entry.getKey().getSource().getName())+entry.getValue());
					}
				}
			}
			// Check for negative cycle
			for (Map.Entry<SDFEdge, Double> entry : e.entrySet()) {
				if (v.get(entry.getKey().getSource().getName())+entry.getValue() < v.get(entry.getKey().getTarget().getName())) {
					// Cycle of negative weight found, condition not respected -> graph not alive
					System.out.println("Negative cycle found in graph "+g+" "+(v.get(entry.getKey().getSource().getName())+entry.getValue()));
					return null;
				}
			}
			// while we are not at level zero, fill the shortest paths table
			if (g.getParentVertex() != null) {
				dist.put(input.getName(), new HashMap<String,Double>());
				// distance from the input to all the outputs
				for (SDFAbstractVertex output : g.getParentVertex().getSinks()) {
					dist.get(input.getName()).put(output.getName(), v.get(output.getName()));
				}
				// reset weight on vertices
				for (SDFAbstractVertex ve : g.vertexSet())
					v.put(ve.getName(), Double.POSITIVE_INFINITY);
				
			} else 
				return dist;
		}
		return dist;
	}
}
