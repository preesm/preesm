package org.ietr.preesm.plugin.architransfos.transforms;

import java.util.HashMap;
import java.util.Vector;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.sdf4j.model.AbstractGraph;
import org.sdf4j.model.AbstractVertex;
import org.sdf4j.model.parameters.Argument;
import org.sdf4j.model.parameters.InvalidExpressionException;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.model.sdf.SDFInterfaceVertex;
import org.sdf4j.model.sdf.esdf.SDFBroadcastVertex;
import org.sdf4j.model.sdf.esdf.SDFForkVertex;
import org.sdf4j.model.sdf.esdf.SDFJoinVertex;
import org.sdf4j.model.sdf.esdf.SDFRoundBufferVertex;
import org.sdf4j.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.sdf4j.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.sdf4j.model.sdf.types.SDFIntEdgePropertyType;
import org.sdf4j.model.visitors.AbstractHierarchyFlattening;
import org.sdf4j.model.visitors.SDF4JException;

/**
 * HierarchyFlattening of an architecture with a given depth
 * 
 * @author mpelcat
 * 
 */
public class ArchiHierarchyFlattening extends AbstractHierarchyFlattening<MultiCoreArchitecture>{

	/**
	 * Flatten one vertex given it's parent
	 * 
	 * @param vertex
	 *            The vertex to flatten
	 * @param parentGraph
	 *            The new parent graph
	 * @throws InvalidExpressionException 
	 */
	@SuppressWarnings("unchecked")
	private void treatVertex(ArchitectureComponent vertex, MultiCoreArchitecture parentGraph,
			int depth) throws InvalidExpressionException {
		/*
		Vector<SDFAbstractVertex> vertices = new Vector<SDFAbstractVertex>(
				vertex.getGraphDescription().vertexSet());
		HashMap<SDFAbstractVertex, SDFAbstractVertex> matchCopies = new HashMap<SDFAbstractVertex, SDFAbstractVertex>();
		for (int i = 0; i < vertices.size(); i++) {
			if (!(vertices.get(i) instanceof SDFInterfaceVertex)) {
				SDFAbstractVertex trueVertex = vertices.get(i);
				SDFAbstractVertex cloneVertex = vertices.get(i).clone();
				parentGraph.addVertex(cloneVertex);
				matchCopies.put(trueVertex, cloneVertex);
				cloneVertex.copyProperties(trueVertex);
				cloneVertex.setName(vertex.getName() + "_"
						+ cloneVertex.getName());
				if (trueVertex.getArguments() != null) {
					for (Argument arg : trueVertex.getArguments().values()) {
						arg.setExpressionSolver(trueVertex.getBase());
						Integer valueOfArg = arg.intValue();
						cloneVertex.getArgument(arg.getName()).setValue(
								String.valueOf(valueOfArg));
					}
				}
			}
		}
		Vector<SDFEdge> edges = new Vector<SDFEdge>(vertex
				.getGraphDescription().edgeSet());
		for (int i = 0; i < edges.size(); i++) {
			SDFAbstractVertex sourceVertex = null;
			SDFAbstractVertex targetVertex = null;
			int delayValue = 0;
			if (edges.get(i).getSource() instanceof SDFInterfaceVertex) {
				SDFInterfaceVertex sourceInterface = (SDFInterfaceVertex) edges
						.get(i).getSource();
				if (vertex.getAssociatedEdge(sourceInterface) != null) {
					sourceVertex = vertex.getAssociatedEdge(sourceInterface)
							.getSource();
					delayValue = vertex.getAssociatedEdge(sourceInterface)
							.getDelay().intValue();
					edges.get(i).setSourceInterface(
							vertex.getAssociatedEdge(sourceInterface)
									.getSourceInterface());
				}

			} else {
				sourceVertex = matchCopies.get(edges.get(i).getSource());
			}
			if (edges.get(i).getTarget() instanceof SDFInterfaceVertex) {
				SDFInterfaceVertex targetInterface = (SDFInterfaceVertex) edges
						.get(i).getTarget();
				if (vertex.getAssociatedEdge(targetInterface) != null) {
					targetVertex = vertex.getAssociatedEdge(targetInterface)
							.getTarget();
					delayValue = vertex.getAssociatedEdge(targetInterface)
							.getDelay().intValue();
					edges.get(i).setTargetInterface(
							vertex.getAssociatedEdge(targetInterface)
									.getTargetInterface());
				}

			} else {
				targetVertex = matchCopies.get(edges.get(i).getTarget());
			}
			if (sourceVertex != null && targetVertex != null) {
				SDFEdge newEdge = parentGraph.addEdge(sourceVertex,
						targetVertex);
				newEdge.copyProperties(edges.get(i));
				newEdge.setCons(new SDFIntEdgePropertyType(edges.get(i)
						.getCons().intValue()));
				newEdge.setProd(new SDFIntEdgePropertyType(edges.get(i)
						.getProd().intValue()));
				if (delayValue != 0) {
					newEdge.setDelay(new SDFIntEdgePropertyType(delayValue));
				}
			}
		}
*/
	}

	/**
	 * Flatten the hierarchy of the given graph to the given depth
	 * 
	 * @param sdf
	 *            The graph to flatten
	 * @param depth
	 *            The depth to flatten the graph
	 * @param log
	 *            The logger in which output information
	 * @throws SDF4JException
	 */
	public void flattenGraph(MultiCoreArchitecture archi, int depth) throws SDF4JException {
		/*
		if (depth > 0) {
			int newDepth = depth - 1;
			for (SDFAbstractVertex vertex : sdf.vertexSet()) {
				if (vertex.getGraphDescription() != null) {
					try {
						prepareHierarchy(vertex, newDepth);
					} catch (InvalidExpressionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw(new SDF4JException(e.getMessage()));
					}
				}
			}
			SDFHierarchyInstanciation instantiate = new SDFHierarchyInstanciation();
			sdf.accept(instantiate);
			output = instantiate.getOutput();
			if (!output.isSchedulable()) {
				throw (new SDF4JException("graph not schedulable"));
			}
			Vector<SDFAbstractVertex> vertices = new Vector<SDFAbstractVertex>(
					output.vertexSet());
			for (int i = 0; i < vertices.size(); i++) {
				if (vertices.get(i).getGraphDescription() != null) {
					try {
						treatVertex(vertices.get(i), output, newDepth);
					} catch (InvalidExpressionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw(new SDF4JException(e.getMessage()));
					}
					
					output.removeVertex(vertices.get(i));
				}
			}

			output.getPropertyBean().setValue("schedulable", true);
			flattenGraph(output, newDepth);

		} else {
			return;
		}*/
		output = archi;
	}

	@SuppressWarnings("unchecked")
	protected void treatSinkInterface(AbstractVertex port,
			AbstractGraph parentGraph, int depth) throws InvalidExpressionException {
		/*
		if(! (port instanceof SDFSinkInterfaceVertex)){
			return ;
		}
		SDFSinkInterfaceVertex vertex = (SDFSinkInterfaceVertex) port;
		boolean needRoundBuffer = false;
		boolean needImplode = false;
		Vector<SDFEdge> inEdges = new Vector<SDFEdge>(parentGraph
				.incomingEdgesOf(vertex));
		for (SDFEdge inEdge : inEdges) {
			if (inEdge.getCons().intValue() < (inEdge.getProd().intValue() * inEdge
					.getSource().getNbRepeat())) {
				needRoundBuffer = true;
				break;
			} else if (inEdge.getCons().intValue() > (inEdge.getProd()
					.intValue())) {
				needImplode = true;
			}
		}
		if (needRoundBuffer) {
			SDFRoundBufferVertex roundBuffer = new SDFRoundBufferVertex();
			SDFSourceInterfaceVertex input = new SDFSourceInterfaceVertex();
			input.setName("in");
			SDFSinkInterfaceVertex output = new SDFSinkInterfaceVertex();
			output.setName("out");
			roundBuffer.setName("roundBuffer_" + vertex.getName());
			parentGraph.addVertex(roundBuffer);
			SDFEdge edge = (SDFEdge) parentGraph.addEdge(roundBuffer, vertex);
			edge.copyProperties(inEdges.get(0));
			edge.setProd(new SDFIntEdgePropertyType(inEdges.get(0).getCons()
					.intValue()));
			edge.setCons(new SDFIntEdgePropertyType(inEdges.get(0).getCons()
					.intValue()));
			edge.setSourceInterface(output);
			while (inEdges.size() > 0) {
				SDFEdge treatEdge = inEdges.get(0);
				SDFAbstractVertex source = treatEdge.getSource();
				SDFEdge newEdge = (SDFEdge) parentGraph.addEdge(source, roundBuffer);
				newEdge.copyProperties(treatEdge);
				newEdge.setProd(new SDFIntEdgePropertyType(treatEdge.getProd()
						.intValue()));
				newEdge.setCons(new SDFIntEdgePropertyType(treatEdge.getProd()
						.intValue()
						* treatEdge.getSource().getNbRepeat()));
				newEdge.setTargetInterface(input);
				newEdge.setSourceInterface(treatEdge.getSourceInterface());
				parentGraph.removeEdge(treatEdge);
				inEdges.remove(0);
			}
		} else if (needImplode && depth == 0) {
			SDFJoinVertex implodeBuffer = new SDFJoinVertex();
			SDFSourceInterfaceVertex input = new SDFSourceInterfaceVertex();
			input.setName("in");
			SDFSinkInterfaceVertex output = new SDFSinkInterfaceVertex();
			output.setName("out");
			implodeBuffer.setName("implode_" + vertex.getName());
			parentGraph.addVertex(implodeBuffer);
			SDFEdge edge = (SDFEdge) parentGraph.addEdge(implodeBuffer, vertex);
			edge.copyProperties(inEdges.get(0));
			edge.setProd(new SDFIntEdgePropertyType(inEdges.get(0).getCons()
					.intValue()));
			edge.setCons(new SDFIntEdgePropertyType(inEdges.get(0).getCons()
					.intValue()));
			edge.setSourceInterface(output);
			while (inEdges.size() > 0) {
				SDFEdge treatEdge = inEdges.get(0);
				SDFAbstractVertex source = treatEdge.getSource();
				SDFEdge newEdge = (SDFEdge)parentGraph.addEdge(source, implodeBuffer);
				newEdge.copyProperties(treatEdge);
				newEdge.setProd(new SDFIntEdgePropertyType(treatEdge.getProd()
						.intValue()));
				newEdge.setCons(new SDFIntEdgePropertyType(treatEdge.getProd()
						.intValue()
						* source.getNbRepeat()));
				newEdge.setTargetInterface(input);
				newEdge.setSourceInterface(treatEdge.getSourceInterface());
				parentGraph.removeEdge(treatEdge);
				inEdges.remove(0);
			}
		}*/
	}

	@SuppressWarnings("unchecked")
	protected void treatSourceInterface(AbstractVertex port,
			AbstractGraph parentGraph, int depth) throws InvalidExpressionException {
		/*if(! (port instanceof SDFSourceInterfaceVertex)){
			return ;
		}
		SDFSourceInterfaceVertex vertex = (SDFSourceInterfaceVertex) port;
		boolean needBroadcast = false;
		boolean needExplode = false;
		Vector<SDFEdge> outEdges = new Vector<SDFEdge>(parentGraph
				.outgoingEdgesOf(vertex));
		if(outEdges.size() > 1){
			needBroadcast = true;
		}else{
			for (SDFEdge outEdge : outEdges) {
				if (outEdge.getProd().intValue() < (outEdge.getCons().intValue() * outEdge
						.getTarget().getNbRepeat())) {
					needBroadcast = true;
					break;
				} else if (outEdge.getProd().intValue() > (outEdge.getCons()
						.intValue())) {
					needExplode = true;
				}
			}
		}
		if (needBroadcast) {
			SDFBroadcastVertex broadcast = new SDFBroadcastVertex();
			SDFSourceInterfaceVertex input = new SDFSourceInterfaceVertex();
			input.setName("in");
			SDFSinkInterfaceVertex output = new SDFSinkInterfaceVertex();
			output.setName("out");
			broadcast.setName("broadcast_" + vertex.getName());
			parentGraph.addVertex(broadcast);
			SDFEdge edge = (SDFEdge) parentGraph.addEdge(vertex, broadcast);
			edge.copyProperties(outEdges.get(0));
			edge.setProd(new SDFIntEdgePropertyType(outEdges.get(0).getProd()
					.intValue()));
			edge.setCons(new SDFIntEdgePropertyType(outEdges.get(0).getProd()
					.intValue()));
			edge.setTargetInterface(input);
			while (outEdges.size() > 0) {
				SDFEdge treatEdge = outEdges.get(0);
				SDFAbstractVertex target = treatEdge.getTarget();
				SDFEdge newEdge = (SDFEdge) parentGraph.addEdge(broadcast, target);
				newEdge.copyProperties(treatEdge);
				newEdge.setCons(new SDFIntEdgePropertyType(treatEdge.getCons()
						.intValue()));
				newEdge.setProd(new SDFIntEdgePropertyType(treatEdge.getCons()
						.intValue()
						* target.getNbRepeat()));
				newEdge.setSourceInterface(output);
				newEdge.setTargetInterface(treatEdge.getTargetInterface());
				parentGraph.removeEdge(treatEdge);
				outEdges.remove(0);
			}

		} else if (needExplode && depth == 0) {
			SDFForkVertex explode = new SDFForkVertex();
			SDFSourceInterfaceVertex input = new SDFSourceInterfaceVertex();
			input.setName("in");
			SDFSinkInterfaceVertex output = new SDFSinkInterfaceVertex();
			output.setName("out");
			explode.setName("explode_" + vertex.getName());
			parentGraph.addVertex(explode);
			SDFEdge edge = (SDFEdge) parentGraph.addEdge(vertex, explode);
			edge.copyProperties(outEdges.get(0));
			edge.setProd(new SDFIntEdgePropertyType(outEdges.get(0).getProd()
					.intValue()));
			edge.setCons(new SDFIntEdgePropertyType(outEdges.get(0).getProd()
					.intValue()));
			edge.setTargetInterface(input);
			while (outEdges.size() > 0) {
				SDFEdge treatEdge = outEdges.get(0);
				SDFAbstractVertex target = treatEdge.getTarget();
				SDFEdge newEdge = (SDFEdge) parentGraph.addEdge(explode, target);
				newEdge.copyProperties(treatEdge);
				newEdge.setCons(new SDFIntEdgePropertyType(treatEdge.getCons()
						.intValue()));
				newEdge.setProd(new SDFIntEdgePropertyType(treatEdge.getCons()
						.intValue()
						* treatEdge.getTarget().getNbRepeat()));
				newEdge.setSourceInterface(output);
				newEdge.setTargetInterface(treatEdge.getTargetInterface());
				parentGraph.removeEdge(treatEdge);
				outEdges.remove(0);
			}

		}*/
	}



}
