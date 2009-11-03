/**
 * 
 */
package org.ietr.preesm.plugin.transforms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ietr.preesm.core.task.IGraphTransformation;
import org.ietr.preesm.core.task.PreesmException;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.sdf4j.model.parameters.InvalidExpressionException;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;
import org.sdf4j.model.sdf.SDFGraph;
import org.sdf4j.model.sdf.esdf.SDFForkVertex;
import org.sdf4j.model.sdf.esdf.SDFJoinVertex;
import org.sdf4j.model.sdf.esdf.SDFRoundBufferVertex;
import org.sdf4j.model.sdf.types.SDFIntEdgePropertyType;

/**
 * Reducing the number of explodes and implodes to the minimum
 * 
 * @author mpelcat
 * @author jpiat
 */
public class ReduceExplodeImplode implements IGraphTransformation {

	@Override
	public TaskResult transform(SDFGraph algo, TextParameters params)
			throws PreesmException {
		SDFGraph algoClone = (SDFGraph) algo.clone();
		treatDummyImplode(algoClone);
		treatDummyExplode(algoClone);

		try {
			treatExplodeImplodePattern(algoClone);
		} catch (InvalidExpressionException e) {
			throw (new PreesmException(e.getMessage()));
		}

		TaskResult result = new TaskResult();
		result.setSDF(algoClone);
		return result;
	}

	protected void treatDummyImplode(SDFGraph graph) {
		List<SDFAbstractVertex> vertices = new ArrayList<SDFAbstractVertex>(
				graph.vertexSet());
		while (vertices.size() > 0) {
			SDFAbstractVertex vertex = vertices.get(0);
			vertices.remove(0);
			if (vertex instanceof SDFJoinVertex) {
				if (graph.incomingEdgesOf(vertex).size() == 1
						&& graph.outgoingEdgesOf(vertex).size() == 1) {
					SDFEdge inEdge = (SDFEdge) graph.incomingEdgesOf(vertex)
							.toArray()[0];
					SDFEdge outEdge = (SDFEdge) graph.outgoingEdgesOf(vertex)
							.toArray()[0];
					if (outEdge.getTarget() instanceof SDFJoinVertex) {
						SDFAbstractVertex trueSource = inEdge.getSource();
						SDFAbstractVertex trueTarget = outEdge.getTarget();
						int index = ((SDFJoinVertex) trueTarget)
								.getEdgeIndex(outEdge);
						SDFEdge newEdge = graph.addEdge(trueSource, trueTarget);
						newEdge.copyProperties(inEdge);
						newEdge.setSourceInterface(inEdge.getSourceInterface());
						newEdge
								.setTargetInterface(outEdge
										.getTargetInterface());
						((SDFJoinVertex) trueTarget).setConnectionIndex(
								newEdge, index);
						graph.removeEdge(inEdge);
						graph.removeEdge(outEdge);
					} else {
						SDFAbstractVertex trueSource = inEdge.getSource();
						SDFAbstractVertex trueTarget = outEdge.getTarget();
						SDFEdge newEdge = graph.addEdge(trueSource, trueTarget);
						newEdge.copyProperties(inEdge);
						newEdge.setSourceInterface(inEdge.getSourceInterface());
						newEdge
								.setTargetInterface(outEdge
										.getTargetInterface());
						graph.removeEdge(inEdge);
						graph.removeEdge(outEdge);
					}
					graph.removeVertex(vertex);
				}

			}
		}

	}

	protected void treatExplodeImplodePattern(SDFGraph graph)
			throws InvalidExpressionException {
		List<SDFAbstractVertex> vertices = new ArrayList<SDFAbstractVertex>(
				graph.vertexSet());
		while (vertices.size() > 0) {
			SDFAbstractVertex vertex = vertices.get(0);
			vertices.remove(0);
			if (vertex instanceof SDFJoinVertex) {
				SDFEdge edge = (SDFEdge) graph.outgoingEdgesOf(vertex)
						.toArray()[0];
				if (edge.getTarget() instanceof SDFForkVertex) {
					SDFJoinVertex joinVertex = (SDFJoinVertex) edge.getSource();
					SDFForkVertex forkVertex = (SDFForkVertex) edge.getTarget();
					List<SDFAbstractVertex> targetVertices = new ArrayList<SDFAbstractVertex>();
					List<SDFAbstractVertex> sourceVertices = new ArrayList<SDFAbstractVertex>();
					Map<SDFAbstractVertex, SDFEdge> connectionEdge = new HashMap<SDFAbstractVertex, SDFEdge>();
					for (SDFEdge inEdge : joinVertex.getIncomingConnections()) {
						if (inEdge.getSource() != null) {
							sourceVertices.add(inEdge.getSource());
							connectionEdge.put(inEdge.getSource(), inEdge);
						}
					}
					for (SDFEdge outEdge : forkVertex.getOutgoingConnections()) {
						if (outEdge.getTarget() != null) {
							targetVertices.add(outEdge.getTarget());
							connectionEdge.put(outEdge.getTarget(), outEdge);
						}
					}
					if (sourceVertices.size() == targetVertices.size()) {
						int inc = 0;
						for (SDFAbstractVertex srcVertex : sourceVertices) {
							SDFEdge newEdge = graph.addEdge(srcVertex,
									targetVertices.get(inc));
							vertices.remove(srcVertex);
							vertices.remove(targetVertices.get(inc));
							newEdge.copyProperties(joinVertex
									.getIncomingConnections().get(inc));
							newEdge.setProd(new SDFIntEdgePropertyType(
									connectionEdge.get(srcVertex).getProd()
											.intValue()));
							newEdge.setCons(new SDFIntEdgePropertyType(
									connectionEdge.get(targetVertices.get(inc))
											.getCons().intValue()));
							newEdge.setSourceInterface(connectionEdge.get(
									srcVertex).getSourceInterface());
							newEdge.setTargetInterface(connectionEdge.get(
									targetVertices.get(inc))
									.getTargetInterface());
							if (edge.getDelay().intValue() > 0) {
								newEdge.setDelay(new SDFIntEdgePropertyType(
										newEdge.getProd().intValue()));
							}
							inc++;
						}
						graph.removeVertex(joinVertex);
						graph.removeVertex(forkVertex);
						vertices.remove(forkVertex);
					}
				}

			}
		}

	}

	protected void treatImplodeRoundBufferPattern(SDFGraph graph) {
		List<SDFAbstractVertex> vertices = new ArrayList<SDFAbstractVertex>(
				graph.vertexSet());
		while (vertices.size() > 0) {
			SDFAbstractVertex vertex = vertices.get(0);
			vertices.remove(0);
			if (vertex instanceof SDFJoinVertex) {
				SDFEdge outEdge = (SDFEdge) graph.outgoingEdgesOf(vertex)
						.toArray()[0];
				if (outEdge.getTarget() instanceof SDFRoundBufferVertex) {
					for (SDFEdge inEdge : graph.incomingEdgesOf(vertex)) {
						SDFAbstractVertex trueSource = inEdge.getSource();
						SDFAbstractVertex trueTarget = outEdge.getTarget();
						SDFEdge newEdge = graph.addEdge(trueSource, trueTarget);
						newEdge.copyProperties(inEdge);
						newEdge.setSourceInterface(inEdge.getSourceInterface());
						newEdge
								.setTargetInterface(outEdge
										.getTargetInterface());
					}
					graph.removeVertex(vertex);
				}

			}
		}

	}

	protected void treatDummyExplode(SDFGraph graph) {
		List<SDFAbstractVertex> vertices = new ArrayList<SDFAbstractVertex>(
				graph.vertexSet());
		while (vertices.size() > 0) {
			SDFAbstractVertex vertex = vertices.get(0);
			vertices.remove(0);
			if (vertex instanceof SDFForkVertex) {
				if (graph.incomingEdgesOf(vertex).size() == 1
						&& graph.outgoingEdgesOf(vertex).size() == 1) {
					SDFEdge inEdge = (SDFEdge) graph.incomingEdgesOf(vertex)
							.toArray()[0];
					SDFEdge outEdge = (SDFEdge) graph.outgoingEdgesOf(vertex)
							.toArray()[0];
					if (inEdge.getSource() instanceof SDFForkVertex) {
						SDFAbstractVertex trueSource = inEdge.getSource();
						SDFAbstractVertex trueTarget = outEdge.getTarget();
						int index = ((SDFForkVertex) trueSource)
								.getEdgeIndex(inEdge);
						SDFEdge newEdge = graph.addEdge(trueSource, trueTarget);
						newEdge.copyProperties(outEdge);
						newEdge.setSourceInterface(inEdge.getSourceInterface());
						newEdge
								.setTargetInterface(outEdge
										.getTargetInterface());
						((SDFForkVertex) trueSource).setConnectionIndex(
								newEdge, index);
						graph.removeEdge(inEdge);
						graph.removeEdge(outEdge);
					} else {
						SDFAbstractVertex trueSource = inEdge.getSource();
						SDFAbstractVertex trueTarget = outEdge.getTarget();
						SDFEdge newEdge = graph.addEdge(trueSource, trueTarget);
						newEdge.copyProperties(outEdge);
						newEdge.setSourceInterface(inEdge.getSourceInterface());
						newEdge
								.setTargetInterface(outEdge
										.getTargetInterface());
						graph.removeEdge(inEdge);
						graph.removeEdge(outEdge);
					}
					graph.removeVertex(vertex);
				}

			}
		}

	}
}
