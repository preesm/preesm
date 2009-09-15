package org.ietr.preesm.plugin.architransfo.transforms;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.sdf4j.model.AbstractGraph;
import org.sdf4j.model.AbstractVertex;
import org.sdf4j.model.parameters.InvalidExpressionException;
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
		
		if (depth > 0) {

			int newDepth = depth - 1;
			for (ArchitectureComponent cmp : archi.vertexSet()) {
				if (cmp.getRefinement() != null) {

					cmp.toString();
				}
			}
		}
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
	}

	@SuppressWarnings("unchecked")
	protected void treatSourceInterface(AbstractVertex port,
			AbstractGraph parentGraph, int depth) throws InvalidExpressionException {
	}



}
