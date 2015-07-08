package org.ietr.preesm.evaluator;


import org.ietr.dftools.algorithm.model.IInterface;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.visitors.IGraphVisitor;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;

public class NormalizeVisitor implements
IGraphVisitor<SDFGraph, SDFAbstractVertex, SDFEdge>{
	
	private SDFGraph outputGraph;
	
	public SDFGraph getOutput() {
		return outputGraph;
	}
	
	public void visit(SDFGraph sdf) throws SDF4JException {
		
		outputGraph = sdf;
			
		//change values on the edges from int to long before normalization
		prepareNorm(outputGraph);
		// Normalization bottom->up
		normalizeup(outputGraph);
		// Normalization up->bottom
		normalizedown(outputGraph, 0);
	}
		
	
	/**
	 * Converts all the data on the edges of the given graph from int to long, which is
	 * more convenient to do the normalization (32 bits may not be enough).
	 */
	private void prepareNorm(SDFGraph g) {
		// Use double instead of int for all edges
		try {
			for (SDFEdge edge : g.edgeSet()) {	
				edge.setProd(new SDFDoubleEdgePropertyType((double)(edge.getProd().intValue())));
				edge.setDelay(new SDFDoubleEdgePropertyType((double)(edge.getDelay().intValue())));
				edge.setCons(new SDFDoubleEdgePropertyType((double)(edge.getCons().intValue())));
			}
		} catch (InvalidExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Apply the same to the lower levels of hierarchy
		for (SDFAbstractVertex v : g.vertexSet()) {
			if (v.getGraphDescription() != null
					&& v.getGraphDescription() instanceof SDFGraph) {
				prepareNorm((SDFGraph) v.getGraphDescription());
			}
		}
	}
	
	
	/**
	 * First step of the normalization of the graph, normalizing all levels
	 * of hierarchy from the bottom to the top of the graph.
	 */
	private double normalizeup(SDFGraph g) {
		// M = ppcm (N_t * z_t, N_t * in_a)
		double M = (double) 1;
		double z = (double) 1;
		double in,out;
		try {
			for (SDFAbstractVertex vertex : g.vertexSet()) {
				z = (double) 1;
				if (! vertex.getKind().equals("port")) {
					if (vertex.getGraphDescription() != null
							&& vertex.getGraphDescription() instanceof SDFGraph) {
						// If it's a hierarchical actor, normalize its subgraph to obtain the z
						z = normalizeup((SDFGraph) vertex.getGraphDescription());
						
						// Retrieve the values on the output edges, used to compute M (ppcm (N_t * in_a))
						for (SDFInterfaceVertex port : vertex.getSinks()) {
							M = SDFMathD.lcm(M, vertex.getNbRepeatAsInteger() *
											(double)((SDFEdge) vertex.getAssociatedEdge(port)).getProd().getValue());
						}
						for (SDFInterfaceVertex port : vertex.getSources()) {
							M = SDFMathD.lcm(M, vertex.getNbRepeatAsInteger() *
											(double)((SDFEdge) vertex.getAssociatedEdge(port)).getCons().getValue());
						}
					} else {					
						// the vertex is a "normal" actor, we compute its z with the in & out rates
						// of its adjacent edges
						for (IInterface port : vertex.getInterfaces()) {
							if (port.getDirection().toString().equals("Input")) {
								out = (double)((SDFEdge) vertex.getAssociatedEdge(port)).getCons().getValue();
								z = SDFMathD.lcm(z,out);
								M = SDFMathD.lcm(M, vertex.getNbRepeatAsInteger() * out);
							} else {
								in = (double) ((SDFEdge) vertex.getAssociatedEdge(port)).getProd().getValue();
								z = SDFMathD.lcm(z,in);
								// ppcm (N_t * in_a)
								M = SDFMathD.lcm(M, vertex.getNbRepeatAsInteger() * in);
							}
						}
					}
					// M = ppcm(N_t * z_t)
					M = SDFMathD.lcm(M, vertex.getNbRepeatAsInteger() * z);
				}
			}
			
			// new Z for each actor : M / repet_actor
			for (SDFEdge edge : g.edgeSet()) {
				// sink port
				if (edge.getTarget().getKind().equals("port")) {
					in = (double) edge.getProd().getValue();
					edge.setProd(new SDFDoubleEdgePropertyType(M/edge.getSource().getNbRepeatAsInteger()));
					edge.setCons(new SDFDoubleEdgePropertyType(M));
					edge.setDelay(new SDFDoubleEdgePropertyType(((double)edge.getProd().getValue()/in)*(double)edge.getDelay().getValue()));
				} else {
					// source port
					if (edge.getSource().getKind().equals("port")){
						in = (double) edge.getCons().getValue();
						edge.setCons(new SDFDoubleEdgePropertyType(M/edge.getTarget().getNbRepeatAsInteger()));
						edge.setProd(new SDFDoubleEdgePropertyType(M));
						edge.setDelay(new SDFDoubleEdgePropertyType(((double)edge.getCons().getValue()/in)*(double)edge.getDelay().getValue()));
					} else {
						in = (double) edge.getProd().getValue();
						edge.setProd(new SDFDoubleEdgePropertyType(M/edge.getSource().getNbRepeatAsInteger()));
						edge.setCons(new SDFDoubleEdgePropertyType(M/edge.getTarget().getNbRepeatAsInteger()));
						//System.out.println((double)edge.getProd().getValue()+" / "+(double)in+" = "+((double)edge.getProd().getValue()/(double)in));
						edge.setDelay(new SDFDoubleEdgePropertyType(((double)edge.getProd().getValue()/in)*(double)(edge.getDelay().getValue())));
					}
				}
			}		
		} catch (InvalidExpressionException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}
		return M;
	}
	
	
	/*
	// used to remove broadcasts from the graph (not necessary to evaluate liveness)
	 private void removeBroadcast(SDFGraph g) {
		ArrayList<SDFAbstractVertex> toRemove = new ArrayList<SDFAbstractVertex>();
		SDFInterfaceVertex out;
		for (SDFAbstractVertex vertex : g.vertexSet()) {
			if (vertex.getKind() == "Broadcast") {
				toRemove.add(vertex);
				// The incoming edge of the broadcast
				SDFEdge previous_in = vertex.getAssociatedEdge(vertex.getSources().get(0));
				// Broadcast replaced by its number of outgoing edges
				for (int i=0; i<vertex.getSinks().size(); i++) {
					out = vertex.getSinks().get(i);
					SDFEdge newEdge = g.addEdge(previous_in.getSource(), vertex.getAssociatedEdge(out).getTarget());
					newEdge.setProd(previous_in.getProd());
					newEdge.setCons(previous_in.getCons());
					newEdge.setDelay(previous_in.getDelay());
					SDFSinkInterfaceVertex outPort = new SDFSinkInterfaceVertex();
					outPort.setName(vertex.getAssociatedEdge(out).getTarget().getName()+i);
					previous_in.getSource().addSink(outPort);
					newEdge.setSourceInterface(outPort);
					newEdge.setTargetInterface(vertex.getAssociatedEdge(out).getTargetInterface());
				}
				SDFAbstractVertex s = previous_in.getSource();
				g.removeEdge(previous_in);
				s.removeSink(previous_in);
				System.out.println(previous_in.getSourceInterface().getName());
			}
		}
		// Remove effectively all the broadcasts
		//for (SDFAbstractVertex vertex : toRemove)
			//g.removeVertex(vertex);
		for (SDFAbstractVertex vertex : g.vertexSet()) {
			System.out.println(vertex+" "+vertex.getInterfaces().size());
		}
	}*/

	
	/**
	 * Second step of the normalization of the graph, making sure that
	 * the normalization between levels is coherent.
	 */
	private void normalizedown(SDFGraph g, double Z) {
		double M = 1;
		double z_up;
		
		// if Z == 0, it is the level zero of hierarchy, nothing to do here
		if (Z != 0) {
			// retrieve the value on the interfaces
			for (SDFAbstractVertex v : g.vertexSet()) {
				if (v.getKind().equals("port")) {
					if (v.getInterfaces().get(0).getDirection().toString().equals("Input"))
						M = (double)((SDFEdge) v.getAssociatedEdge(v.getInterfaces().get(0))).getCons().getValue();
					else
						M = (double)((SDFEdge) v.getAssociatedEdge(v.getInterfaces().get(0))).getProd().getValue();
				}
			}
			// if Z == M, no need to multiply anything
			if (Z != M) {
				// Need to multiply rates of the subgraph on the edges by Z/M
				for (SDFEdge edge : g.edgeSet()) {
					edge.setProd(new SDFDoubleEdgePropertyType((double)(edge.getProd().getValue()) * (Z/M)));
					edge.setCons(new SDFDoubleEdgePropertyType((double)(edge.getCons().getValue()) * (Z/M)));
					edge.setDelay(new SDFDoubleEdgePropertyType((double)(edge.getDelay().getValue()) * (Z/M)));
				}
			}
		}

		for (SDFAbstractVertex vertex : g.vertexSet()) {
			// For each hierarchic actor
			if (vertex.getGraphDescription() != null && vertex.getGraphDescription() instanceof SDFGraph) {
				// Retrieve the normalization value of the actor (Z)
				if (vertex.getInterfaces().get(0).getDirection().toString().equals("Input"))
					z_up = (double)(((SDFEdge) vertex.getAssociatedEdge(vertex.getInterfaces().get(0))).getCons().getValue());
				else
					z_up = (double)(((SDFEdge) vertex.getAssociatedEdge(vertex.getInterfaces().get(0))).getProd().getValue());
				// Continue the normalization in the lower levels
				normalizedown((SDFGraph) vertex.getGraphDescription(),z_up);
			}
		}
	}
	

	@Override
	public void visit(SDFEdge sdfEdge) {
	}

	
	@Override
	public void visit(SDFAbstractVertex sdfVertex) throws SDF4JException {
	}
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////
// Normalization for non hierarchical SDF (more efficient if the graph doesnot use hierarchy)
/*
	int lcm = 1;
	int coeff;
	try {
		for (SDFEdge edge : outputGraph.edgeSet())
			lcm = SDFMath.lcm(lcm, (edge.getProd().intValue() * edge.getSource().getNbRepeatAsInteger()));
		
		for (SDFEdge edge : outputGraph.edgeSet()) {
			coeff = (lcm/edge.getSource().getNbRepeatAsInteger())/edge.getProd().intValue();
			edge.setProd(new SDFIntEdgePropertyType(coeff * edge.getProd().intValue()));
			edge.setDelay(new SDFIntEdgePropertyType(coeff * edge.getDelay().intValue()));
			edge.setCons(new SDFIntEdgePropertyType(coeff * edge.getCons().intValue()));
		}
	} catch (InvalidExpressionException e) {
		// Auto-generated catch block
		e.printStackTrace();
	}
*/
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	
}