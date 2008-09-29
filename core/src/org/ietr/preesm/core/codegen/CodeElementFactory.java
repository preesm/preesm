package org.ietr.preesm.core.codegen;

import org.sdf4j.model.AbstractVertex;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.model.sdf.SDFVertex;

public class CodeElementFactory {

	public static ICodeElement createElement(String name,
			AbstractBufferContainer parentContainer, DAGVertex vertex) {
		if (vertex.getCorrespondingSDFVertex().getGraphDescription() == null
				&& vertex.getNbRepeat().intValue() > 1) {
			FiniteForLoop loop = new FiniteForLoop(parentContainer, vertex, vertex.getNbRepeat().intValue());
			loop.addCall(new UserFunctionCall(name, vertex, loop));
			return loop;
		} else if (vertex.getCorrespondingSDFVertex().getGraphDescription() == null
				&& vertex.getNbRepeat().intValue() == 1) {
			return new UserFunctionCall(name, vertex, parentContainer);
		} else if (vertex.getCorrespondingSDFVertex().getGraphDescription() != null
				&& vertex.getNbRepeat().intValue() > 1) {
			SDFGraph graph = vertex.getCorrespondingSDFVertex()
					.getGraphDescription();
			FiniteForLoop loop = new FiniteForLoop(parentContainer, vertex, vertex.getNbRepeat().intValue());
			for (SDFAbstractVertex child : graph.vertexSet()) {
				loop.addCall(CodeElementFactory
						.createElement(name, loop, child));
			}
			return loop;
		} else {
			SDFGraph graph = vertex.getCorrespondingSDFVertex()
					.getGraphDescription();
			CompoundCodeElement compound = new CompoundCodeElement(name,
					parentContainer, vertex);
			for (SDFAbstractVertex child : graph.vertexSet()) {
				compound.addCall(CodeElementFactory.createElement(name,
						parentContainer, child));
			}
			return compound;
		}
	}

	public static ICodeElement createElement(String name,
			AbstractBufferContainer parentContainer, SDFAbstractVertex vertex) {
		if (vertex.getGraphDescription() == null) {
			return new UserFunctionCall(name, vertex, parentContainer);
		} else if (vertex.getGraphDescription() != null
				&& vertex.getBase().getVRB().get(vertex) > 1) {
			SDFGraph graph = vertex.getGraphDescription();
			FiniteForLoop loop = new FiniteForLoop(parentContainer,
					(SDFVertex) vertex, vertex.getBase().getVRB().get(vertex)) ;
			for (SDFAbstractVertex child : graph.vertexSet()) {
				loop.addCall(CodeElementFactory
						.createElement(name, loop, child));
			}
			return loop;
		} else {
			SDFGraph graph = vertex.getGraphDescription();
			CompoundCodeElement compound = new CompoundCodeElement(name,
					parentContainer, (SDFVertex) vertex);
			for (SDFAbstractVertex child : graph.vertexSet()) {
				compound.addCall(CodeElementFactory.createElement(name,
						parentContainer, child));
			}
			return compound;
		}
	}

	/**
	 * Creates an element from an AbstractVertex
	 * 
	 * @param name
	 * @param parentContainer
	 * @param vertex
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ICodeElement createElement(String name,
			AbstractBufferContainer parentContainer, AbstractVertex vertex) {
		if (vertex instanceof DAGVertex) {
			return createElement(name, parentContainer, (DAGVertex) vertex);
		} else if (vertex instanceof SDFAbstractVertex) {
			return createElement(name, parentContainer,
					(SDFAbstractVertex) vertex);
		}
		return null;
	}
}
