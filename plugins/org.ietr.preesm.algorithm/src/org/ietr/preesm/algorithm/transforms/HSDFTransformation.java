/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.algorithm.transforms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.dftools.algorithm.iterators.SDFIterator;
import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.algorithm.model.sdf.SDFEdge;
import net.sf.dftools.algorithm.model.sdf.SDFGraph;
import net.sf.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import net.sf.dftools.algorithm.model.sdf.esdf.SDFJoinVertex;
import net.sf.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import net.sf.dftools.algorithm.model.sdf.visitors.ToHSDFVisitor;
import net.sf.dftools.algorithm.model.visitors.SDF4JException;
import net.sf.dftools.algorithm.model.visitors.VisitorOutput;
import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.elements.Workflow;
import net.sf.dftools.workflow.implement.AbstractTaskImplementation;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Class used to transform a SDF graph into a HSDF graph. Actually into a single
 * rate graph (close enough :->)
 * 
 * @author jpiat
 * @author mpelcat
 * 
 */
public class HSDFTransformation extends AbstractTaskImplementation {

	@SuppressWarnings("unchecked")
	private void supprImplodeExplode(SDFGraph hsdf) {
		/*
		 * Declarations & initializations
		 */
		SDFIterator iterSDFVertices;
		try {
			iterSDFVertices = new SDFIterator(hsdf);
		} catch (InvalidExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (RuntimeException e) {
			Logger logger = WorkflowLogger.getLogger();
			logger.log(
					Level.SEVERE,
					"Explode/Implode vertices were not removed because:\n"
							+ e.getMessage());
			return;
		}

		// Keep track of the initial number of edge to check if the right number
		// of edges were removed
		int nbEdgeBefore = hsdf.edgeSet().size();

		// Count the number of edges added because of broadcasts and round
		// buffers
		int nbEdgeAdded = 0;

		// Be careful, DAGiterator does not seem to work well if dag is
		// modified throughout the iteration.
		// That's why we use first copy the ordered dag vertex set.
		ArrayList<SDFAbstractVertex> sdfVertices = new ArrayList<SDFAbstractVertex>(
				hsdf.vertexSet().size());
		while (iterSDFVertices.hasNext()) {
			SDFAbstractVertex vert = iterSDFVertices.next();
			sdfVertices.add(vert);
		}

		// Remove dag vertex of type implode explode
		// And identify source vertices (vertices without predecessors)
		HashSet<SDFAbstractVertex> nonTaskVertices = new HashSet<SDFAbstractVertex>(); // Set
																						// of
		// non-task vertices

		for (SDFAbstractVertex vert : sdfVertices) {
			// boolean isTask = vert.getPropertyBean().getValue("vertexType")
			// .toString().equals("task");
			String vertKind = "";

			// Only task vertices have a kind
			if (true) {
				vertKind = vert.getKind();
			}

			// If the vertex is a task (an implode or explode vertex)
			if (vertKind.equals("fork") || vertKind.equals("join")) {

				if (vertKind.equals("fork")
						&& (vert.getBase().incomingEdgesOf(vert).size() > 1)) {
					WorkflowLogger.getLogger().log(
							Level.SEVERE,
							"Skipped Fork vertex with multiple inputs ("
									+ vert.getId() + ")");
					continue;
				}

				if ((vert.getBase().outgoingEdgesOf(vert).size() > 1)
						&& (vert.getBase().incomingEdgesOf(vert).size() > 1)) {
					WorkflowLogger.getLogger().log(
							Level.SEVERE,
							"Skipped Fork/Join vertex with both multiple inputs and outputs ("
									+ vert.getId() + ")");
					continue;
				}

				// Then link incoming/outgoing edges of the implode/explode
				// directly to the target/source of explosion.
				Set<SDFEdge> outgoingEdges = vert.getBase().outgoingEdgesOf(
						vert);
				Set<SDFEdge> incomingEdges = vert.getBase().incomingEdgesOf(
						vert);
				for (SDFEdge incomingEdge : incomingEdges) {
					for (SDFEdge outgoingEdge : outgoingEdges) {
						// One of this two nested loop will have only one
						// iteration. Indeed, an implode vertex only has 1
						// outgoing edge and a explode vertex only has 1
						// incoming edge.

						// // Check that the edge is linked to a task (we do
						// // not
						// // consider edges linked to send/receive)
						// if (incomingEdge.getSource().getPropertyBean()
						// .getValue("vertexType").toString()
						// .equals("task")
						// && outgoingEdge.getTarget().getPropertyBean()
						// .getValue("vertexType").toString()
						// .equals("task")) {

						// Select the edges whose properties must be
						// copied to the new edge
						SDFEdge edge;

						edge = (vertKind.equals("join")) ? incomingEdge
								: outgoingEdge;

						// Create the new edge that bypass the
						// explode/implode
						SDFEdge newEdge = hsdf.addEdge(
								incomingEdge.getSource(),
								outgoingEdge.getTarget());

						newEdge.copyProperties(edge);
						newEdge.setSourceLabel(incomingEdge.getSourceLabel());
						newEdge.setTargetLabel(outgoingEdge.getTargetLabel());
						newEdge.setTargetInterface(outgoingEdge
								.getTargetInterface());
						newEdge.setSourceInterface(incomingEdge
								.getSourceInterface());

						// }
					}
				}
				nonTaskVertices.add(vert);

				// Remove the vertex from the graph
				hsdf.removeVertex(vert);
			} else if (vert instanceof SDFBroadcastVertex
					&& !(vert instanceof SDFRoundBufferVertex)) {
				// WorkflowLogger.getLogger().log(Level.SEVERE, "BROADCAST");

				// If the broadcast follows an implosion duplicate the broadcast
				Set<SDFEdge> incomingEdges = vert.getBase().incomingEdgesOf(
						vert);
				if (incomingEdges.size() > 1) {

					Set<SDFEdge> outgoingEdges = vert.getBase()
							.outgoingEdgesOf(vert);
					// Create a broadcast for each incoming edge instead of one
					// for
					// all of them
					for (SDFEdge inEdge : incomingEdges) {
						// ignore the edge coming from implode vertex
						if (inEdge.getSource() instanceof SDFJoinVertex) {
							continue;
						}

						// Create new vertex
						SDFBroadcastVertex newVert = (SDFBroadcastVertex) vert
								.clone();
						hsdf.addVertex(newVert);

						// Link it to its input
						SDFEdge newInEdge = hsdf.addEdge(inEdge.getSource(),
								newVert);
						newInEdge.copyProperties(inEdge);
						newInEdge.setSourceLabel(inEdge.getSourceLabel());
						newInEdge.setTargetLabel(inEdge.getTargetLabel());
						newInEdge.setTargetInterface(inEdge
								.getTargetInterface());
						newInEdge.setSourceInterface(inEdge
								.getSourceInterface());

						// Link it to its outputs
						for (SDFEdge outEdge : outgoingEdges) {
							// Create the new edge from broadcast to all
							// sink
							SDFEdge newOutEdge = hsdf.addEdge(newVert,
									outEdge.getTarget());

							newOutEdge.copyProperties(inEdge);
							newOutEdge.setSourceLabel(outEdge.getSourceLabel());
							newOutEdge.setTargetLabel(outEdge.getTargetLabel());
							newOutEdge.setTargetInterface(outEdge
									.getTargetInterface());
							newOutEdge.setSourceInterface(outEdge
									.getSourceInterface());
						}
					}
					// Update the number of edge added
					// The -1 is because outgoing edges of broadcast
					// were already present once.
					nbEdgeAdded += (outgoingEdges.size())
							* (incomingEdges.size() - 1);

					// Remove the vertex from the graph
					hsdf.removeVertex(vert);
				}
			} else if (vert instanceof SDFRoundBufferVertex) {
				// WorkflowLogger.getLogger().log(Level.SEVERE, "RoundBuffer");

				// Check if the roundbuffer precedes an explosion
				// It seems that in this case, no "explode" vertex was added :s
				Set<SDFEdge> outgoingEdges = vert.getBase().outgoingEdgesOf(
						vert);
				if (outgoingEdges.size() > 1) {
					Set<SDFEdge> incomingEdges = vert.getBase()
							.incomingEdgesOf(vert);

					// Duplicate the roundbuffer for each outgoing edge
					for (SDFEdge outEdge : outgoingEdges) {
						// Create new vertex
						SDFRoundBufferVertex newVert = (SDFRoundBufferVertex) vert
								.clone();
						hsdf.addVertex(newVert);

						// link it to its output.
						SDFEdge newOutEdge = hsdf.addEdge(newVert,
								outEdge.getTarget());

						newOutEdge.copyProperties(outEdge);
						newOutEdge.setSourceLabel(outEdge.getSourceLabel());
						newOutEdge.setTargetLabel(outEdge.getTargetLabel());
						newOutEdge.setTargetInterface(outEdge
								.getTargetInterface());
						newOutEdge.setSourceInterface(outEdge
								.getSourceInterface());

						// Link it to its inputs
						for (SDFEdge inEdge : incomingEdges) {
							SDFEdge newInEdge = hsdf.addEdge(
									inEdge.getSource(), newVert);
							newInEdge.copyProperties(outEdge);
							newInEdge.setSourceLabel(inEdge.getSourceLabel());
							newInEdge.setTargetLabel(inEdge.getTargetLabel());
							newInEdge.setTargetInterface(inEdge
									.getTargetInterface());
							newInEdge.setSourceInterface(inEdge
									.getSourceInterface());
						}
					}
					// Update the number of edge added
					nbEdgeAdded += (outgoingEdges.size() - 1)
							* (incomingEdges.size());

					// Remove the vertex from the graph
					hsdf.removeVertex(vert);
				}
			}
		}
		// hsdf.removeAllVertices(nonTaskVertices);
		if (nonTaskVertices.size() != (nbEdgeBefore + nbEdgeAdded - hsdf
				.edgeSet().size())) {
			WorkflowLogger.getLogger().log(
					Level.SEVERE,
					"Expecting "
							+ nonTaskVertices.size()
							+ " edges removed but got "
							+ (nbEdgeBefore + nbEdgeAdded - hsdf.edgeSet()
									.size()) + " edges removed instead");
			WorkflowLogger
					.getLogger()
					.log(Level.SEVERE,
							"Consider deactivating Implode/Explode suprression in HSDF workflow element parameters");
		} else {
			WorkflowLogger
					.getLogger()
					.log(Level.INFO,
							""
									+ nonTaskVertices.size()
									+ " implode/explode vertices removed (and as many edges)");
		}
	}

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		Map<String, Object> outputs = new HashMap<String, Object>();
		SDFGraph algorithm = (SDFGraph) inputs.get("SDF");

		Logger logger = WorkflowLogger.getLogger();

		try {

			logger.setLevel(Level.FINEST);
			logger.log(Level.FINER,
					"Transforming application " + algorithm.getName()
							+ " to HSDF");
			VisitorOutput.setLogger(logger);
			if (algorithm.validateModel(WorkflowLogger.getLogger())) {

				ToHSDFVisitor toHsdf = new ToHSDFVisitor();

				try {
					algorithm.accept(toHsdf);
				} catch (SDF4JException e) {
					e.printStackTrace();
					throw (new WorkflowException(e.getMessage()));
				}
				logger.log(Level.FINER, "HSDF transformation complete");

				SDFGraph hsdf = (SDFGraph) toHsdf.getOutput();
				logger.log(Level.INFO, "HSDF with " + hsdf.vertexSet().size()
						+ " vertices and " + hsdf.edgeSet().size() + " edges.");

				String explImplSuppr;
				if ((explImplSuppr = parameters.get("ExplodeImplodeSuppr")) != null) {
					if (explImplSuppr.equals("true")) {
						logger.log(Level.INFO, "Removing implode/explode ");
						supprImplodeExplode(hsdf);
						// Kdesnos addition for csv stat. can be removed 
						System.out.print(hsdf.vertexSet().size()+";"+hsdf.edgeSet().size()+";");
					}
				}

				outputs.put("SDF", hsdf);
			} else {
				throw (new WorkflowException("Graph not valid, not schedulable"));
			}
		} catch (SDF4JException e) {
			throw (new WorkflowException(e.getMessage()));
		}

		return outputs;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> param = new HashMap<String, String>();
		param.put("ExplodeImplodeSuppr", "false");
		return param;
	}

	@Override
	public String monitorMessage() {
		return "HSDF Transformation.";
	}

	/*
	 * @Override public TaskResult transform(SDFGraph algorithm, TextParameters
	 * params) throws PreesmException { try { Logger logger =
	 * AbstractWorkflowLogger.getLogger(); logger.setLevel(Level.FINEST);
	 * logger.log(Level.FINER, "Transforming application " + algorithm.getName()
	 * + " to HSDF"); VisitorOutput.setLogger(logger); if
	 * (algorithm.validateModel(AbstractWorkflowLogger.getLogger())) {
	 * net.sf.dftools.algorithm.model.sdf.visitors.OptimizedToHSDFVisitor toHsdf
	 * = new OptimizedToHSDFVisitor(); try { algorithm.accept(toHsdf); } catch
	 * (SDF4JException e) { e.printStackTrace(); throw (new
	 * PreesmException(e.getMessage())); } logger.log(Level.FINER,
	 * "HSDF transformation complete"); TaskResult result = new TaskResult();
	 * result.setSDF((SDFGraph) toHsdf.getOutput()); return result; } else {
	 * throw (new PreesmException("Graph not valid, not schedulable")); } }
	 * catch (SDF4JException e) { throw (new PreesmException(e.getMessage())); }
	 * }
	 */

}
