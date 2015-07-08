package org.ietr.preesm.evaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.gnu.glpk.*;
import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType;
import org.ietr.dftools.algorithm.model.IInterface;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.core.scenario.PreesmScenario;

/**
 * Class used to compute the optimal periodic schedule and the throughput
 * of a non hierarchical graph (SDF or flattened IBSDF)
 * 
 * @author blaunay
 * 
 */
public class SDFThroughputEvaluator  {

	/**
	 * Computes the throughput on the optimal periodic schedule (if it exists) of a
	 * given graph under the given scenario
	 * 
	 * @param inputGraph the normalized graph
	 * @param scenario 
	 * @return the throughput on the optimal periodic schedule
	 * @throws InvalidExpressionException
	 */
	public double launch(SDFGraph inputGraph, PreesmScenario scenario) throws InvalidExpressionException {
		
		double period;
		double throughput = 0;
		
		long startTime = System.nanoTime();
		
		// Check condition of existence of a periodic schedule (Bellman Ford)
		boolean periodic_schedule = has_periodic_schedule(inputGraph);
		
		if (periodic_schedule) {
			// Find the cycle with L/H max (using linear program)
			period = period_computation(inputGraph, scenario);
			System.out.println("Period : "+period);
			// Deduce throughput of the schedule
			throughput = throughput_computation(period, inputGraph);
			System.out.println("Throughput : "+throughput);
		} else {
			System.out.println("No periodic schedule for this graph.");
		}
		System.out.println("Time : "+(System.nanoTime() - startTime)/Math.pow(10, 9));
		return throughput;
	}
	
	
	/**
	 * Returns the maximum throughput of the graph given its optimal normalized period.
	 */
	private double throughput_computation(double period, SDFGraph sdf) {
		// Get the period of each actor, to deduce their throughput
		double[] throughputs = new double[sdf.vertexSet().size()];
		int i = 0;
		for (SDFAbstractVertex vertex : sdf.vertexSet()) {
			throughputs[i] =  1/(period *(double)(((SDFEdge) vertex.getAssociatedEdge(vertex.getInterfaces().get(0)))
					.getProd().getValue()));
			i++;	
		}
		// The minimal throughput is the throughput of the schedule
		Arrays.sort(throughputs);
		return throughputs[0];
	}

	
	/**
	 * Computes the optimal periodic schedule for the given graph and scenario,
	 * using a linear program solved by GLPK.
	 * 
	 * @return the optimal normalized period
	 */
	private double period_computation(SDFGraph sdf, PreesmScenario scenar) throws InvalidExpressionException {
		// Map to associate each edge with an index
		ArrayList<SDFEdge> edges = new ArrayList<SDFEdge>(sdf.edgeSet());
		double H;
		int n,r,k;
		long L;
		
		// Now we create the model for the problem and for GLPK
		glp_prob prob;
		
		prob = GLPK.glp_create_prob();
		GLPK.glp_set_prob_name(prob, "Max_Cycle_L/H");
		GLPK.glp_set_obj_dir(prob, GLPK.GLP_MAX); // maximization problem
		
		// Number of constraints nbvertex+1
		GLPK.glp_add_rows(prob, sdf.vertexSet().size()+1);
		// Number of variables nbedge
		GLPK.glp_add_cols(prob, sdf.edgeSet().size());
		
		for (int j=1; j<=edges.size(); j++) {
			// Bounds on the variables : >=0
			GLPK.glp_set_col_bnds(prob, j, GLPK.GLP_LO, 0.0, 0.0);
			// Continuous variables
			GLPK.glp_set_col_kind(prob, j, GLPK.GLP_CV);
			// Retrieve timing of the actor (coef in obj function)
			L = scenar.getTimingManager().getTimingOrDefault(edges.get(j-1).getTarget().getId(), "x86").getTime();
			GLPK.glp_set_obj_coef(prob, j, L);
		}
		
		// Set the bounds on constraints
		for (int j=1; j<=sdf.vertexSet().size(); j++) 
			GLPK.glp_set_row_bnds(prob, j, GLPK.GLP_FX, 0.0, 0.0);
		GLPK.glp_set_row_bnds(prob, sdf.vertexSet().size()+1, GLPK.GLP_FX, 1, 1);
		
		SWIGTYPE_p_int ind = GLPK.new_intArray(edges.size()+1);
		SWIGTYPE_p_double val = GLPK.new_doubleArray(edges.size()+1);
		
		// First constraint
		n = 1; 
		for (SDFAbstractVertex vertex : sdf.vertexSet()) {
			k = 1; 
			for (IInterface port : vertex.getInterfaces()) {
				r = edges.indexOf(vertex.getAssociatedEdge(port));
				if (port.getDirection().toString() == "Input") {
					// check that the edge does not loop on the actor
					if (edges.get(r).getSource() != vertex) {
						GLPK.intArray_setitem(ind, k, r+1);
						GLPK.doubleArray_setitem(val, k, 1.0);
						k++;
					}
				} else {
					if (edges.get(r).getTarget() != vertex) {
						GLPK.intArray_setitem(ind, k, r+1);
						GLPK.doubleArray_setitem(val, k, -1.0);
						k++;
					}
				}
			}
			GLPK.glp_set_mat_row(prob, n, k-1, ind, val);
			n++;
			ind = GLPK.new_intArray(edges.size()+1);
			val = GLPK.new_doubleArray(edges.size()+1);
		}
		
		// Second constraint : sum of H.x = 1
		for (int j=1; j<=edges.size(); j++) {
			GLPK.intArray_setitem(ind,j,j);
			H = (double)(edges.get(j-1).getDelay().getValue()) + 
					SDFMathD.gcd((double)(edges.get(j-1).getCons().getValue()),(double)(edges.get(j-1).getProd().getValue()))
					- (double)(edges.get(j-1).getCons().getValue());
			GLPK.doubleArray_setitem(val, j, H);		
		}
		
		GLPK.glp_set_mat_row(prob, sdf.vertexSet().size()+1, edges.size(), ind, val);
		
		glp_smcp parm = new glp_smcp();
		GLPK.glp_init_smcp(parm);
		parm.setMsg_lev(GLPK.GLP_MSG_OFF);
		// Launch the resolution
		GLPK.glp_simplex(prob, parm);		
		
		//Write the complete model in a file (optional)
		//GLPK.glp_write_lp(prob, null, "model.lp");
		
		// The objective value gives us the normalized period		
		double period = GLPK.glp_get_obj_val(prob);
		GLPK.glp_delete_prob(prob);
		GLPK.glp_free_env();
		return period;
	}
	
	
	/**
	 * Checks if a periodic schedule can be computed for the given graph.
	 */
	private boolean has_periodic_schedule(SDFGraph input) {
		HashMap<SDFAbstractVertex,Double> v = new HashMap<SDFAbstractVertex,Double>();
		HashMap<SDFEdge,Double> e = new HashMap<SDFEdge,Double>();
		
		// Init the weights on the edges (i,j) : w = M0 + gcd(i,j) - Zj
		for (SDFEdge edge : input.edgeSet()) {
			e.put(edge, (double)(edge.getDelay().getValue()) + SDFMathD.gcd((double)(edge.getProd().getValue()),(double)(edge.getCons().getValue()))
						- (double)(edge.getCons().getValue()));
		}

		// Initialization : source.dist = 0, v.dist = infinity
		// We do not care which vertex is the source
		for (SDFAbstractVertex vertex : input.vertexSet())	{
			// v.dist = infinity
			v.put(vertex, Double.POSITIVE_INFINITY); 
			
			// Add the edge looping on the actor
			// Edge is actually added to the graph, because simpler
			SDFEdge loop = input.addEdge(vertex, vertex);
			/*SDFSourceInterfaceVertex in = new SDFSourceInterfaceVertex();
			in.setName(vertex.getName()+"loopIn");
			SDFSourceInterfaceVertex out = new SDFSourceInterfaceVertex();
			in.setName(vertex.getName()+"loopOut");
			vertex.addSource(in);
			vertex.addSink(out);
			loop.setSourceInterface(out);
			loop.setTargetInterface(in);*/
			AbstractEdgePropertyType<?> x;
			if (vertex.getSources().size() != 0) {
				x = ((SDFEdge) vertex.getAssociatedEdge(vertex.getSources().get(0))).getCons();
			} else {
				x = ((SDFEdge) vertex.getAssociatedEdge(vertex.getSinks().get(0))).getProd();
			}
			loop.setDelay(x); loop.setCons(x); loop.setProd(x);
			e.put(loop, (double)(loop.getDelay().getValue()));
		}
		// source.dist = 0
		v.put(input.vertexSet().iterator().next(), (double)0);
		
		// Relax all the edges
		for (int i=1; i<=v.size()-1; i++) {
			for (Map.Entry<SDFEdge, Double> entry : e.entrySet()) {
				if (v.get(entry.getKey().getSource())+entry.getValue() < v.get(entry.getKey().getTarget()))
					v.put(entry.getKey().getTarget(), v.get(entry.getKey().getSource())+entry.getValue());
			}
		}
		
		// Check for negative cycle
		for (Map.Entry<SDFEdge, Double> entry : e.entrySet()) {
			if (v.get(entry.getKey().getSource())+entry.getValue() < v.get(entry.getKey().getTarget())) {
				// Cycle of negative weight found, condition H(c) > 0 not respected -> no periodic schedule
				return false;
			}
		}
		return true;
	}	
}
