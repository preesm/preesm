/**
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */

package org.ietr.preesm.codegen.xtend.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.dftools.algorithm.iterators.DAGIterator;
import net.sf.dftools.algorithm.model.AbstractEdge;
import net.sf.dftools.algorithm.model.AbstractGraph;
import net.sf.dftools.algorithm.model.CodeRefinement;
import net.sf.dftools.algorithm.model.CodeRefinement.Language;
import net.sf.dftools.algorithm.model.dag.DAGEdge;
import net.sf.dftools.algorithm.model.dag.DAGVertex;
import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.algorithm.model.dag.EdgeAggregate;
import net.sf.dftools.algorithm.model.sdf.SDFEdge;
import net.sf.dftools.algorithm.model.sdf.SDFGraph;
import net.sf.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import net.sf.dftools.algorithm.model.sdf.SDFVertex;
import net.sf.dftools.architecture.slam.ComponentInstance;
import net.sf.dftools.architecture.slam.Design;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.ietr.preesm.codegen.idl.ActorPrototypes;
import org.ietr.preesm.codegen.idl.IDLPrototypeFactory;
import org.ietr.preesm.codegen.idl.Prototype;
import org.ietr.preesm.codegen.model.CodeGenArgument;
import org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.ActorCall;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.model.codegen.Call;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall;
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.serialize.ScenarioParser;
import org.ietr.preesm.core.types.VertexType;
import org.ietr.preesm.experiment.memory.allocation.MemoryAllocator;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;

/**
 * The objective of this class is to generate an intermediate model that will be
 * used to print the generated code. <br>
 * The generation of the intermediate model is based on elements resulting from
 * a workflow execution: an {@link Design architecture}, a scheduled
 * {@link DirectedAcyclicGraph DAG}, a {@link MemoryExclusionGraph Memory
 * Allocation} and a {@link PreesmScenario scenario}. The generated model is
 * composed of objects of the {@link CodegenPackage Codegen EMF model}.
 * 
 * 
 * @author kdesnos
 * 
 */
public class CodegenModelGenerator {
	/**
	 * Targeted {@link Design Architecture} of the code generation
	 */
	private Design archi;

	/**
	 * {@link DirectedAcyclicGraph DAG} used to generate code. This
	 * {@link DirectedAcyclicGraph DAG} must be the result of mapping/scheduling
	 * process.
	 */
	private DirectedAcyclicGraph dag;

	/**
	 * {@link MemoryExclusionGraph MemEx} used to generate code. This
	 * {@link MemoryExclusionGraph MemEx} must be the result of an allocation
	 * process.
	 * 
	 * @see MemoryAllocator
	 */
	private MemoryExclusionGraph memEx;

	/**
	 * {@link PreesmScenario Scenario} at the origin of the call to the
	 * {@link CodegenPrinter Code Generator}.
	 */
	@SuppressWarnings("unused")
	private PreesmScenario scenario;

	/**
	 * This {@link Map} associates each {@link ComponentInstance} to its
	 * corresponding {@link CoreBlock}.
	 */
	protected Map<ComponentInstance, CoreBlock> coreBlocks;

	/**
	 * This {@link SDFGraph} is the original hierarchical {@link SDFGraph}
	 * parsed by the scenario. It will be used to retrieve the original location
	 * of the different IDL and Graphml files.
	 */
	protected SDFGraph originalSDF;

	/**
	 * Constructor of the {@link CodegenModelGenerator}. The constructor
	 * performs verification to ensure that the inputs are valid:
	 * <ul>
	 * <li>The {@link DirectedAcyclicGraph DAG} is scheduled</li>
	 * <li>The {@link DirectedAcyclicGraph DAG} is mapped on the input
	 * {@link Design architecture}</li>
	 * <li>The {@link MemoryExclusionGraph MemEx} is derived from the
	 * {@link DirectedAcyclicGraph DAG}</li>
	 * <li>The {@link MemoryExclusionGraph MemEx} is allocated</li>
	 * </ul>
	 * 
	 * @param archi
	 *            See {@link CodegenPrinter#archi}
	 * @param dag
	 *            See {@link CodegenPrinter#dag}
	 * @param memEx
	 *            See {@link CodegenPrinter#memEx}
	 * @param scenario
	 *            See {@link CodegenPrinter#scenario}
	 * @throws CodegenException
	 *             When one of the previous verification fails.
	 */
	public CodegenModelGenerator(final Design archi,
			final DirectedAcyclicGraph dag, final MemoryExclusionGraph memEx,
			final PreesmScenario scenario) throws CodegenException {
		this.archi = archi;
		this.dag = dag;
		this.memEx = memEx;
		this.scenario = scenario;

		checkInputs(this.archi, this.dag, this.memEx);

		this.coreBlocks = new HashMap<ComponentInstance, CoreBlock>();
		try {
			originalSDF = ScenarioParser.getAlgorithm(scenario
					.getAlgorithmURL());
		} catch (Exception e) {
			// This exception should never happen here. as the algorithm is
			// parsed at the beginning of the workflow execution.
			e.printStackTrace();
		}
	}

	/**
	 * Method to generate the intermediate model of the codegen based on the
	 * {@link Design architecture}, the {@link MemoryExclusionGraph MemEx graph}
	 * , the {@link DirectedAcyclicGraph DAG} and the {@link PreesmScenario
	 * scenario}.
	 * 
	 * @return a set of {@link Block blocks}. Each of these block corresponds to
	 *         a part of the code to generate:
	 *         <ul>
	 *         <li>{@link CoreBlock A block corresponding to the code executed
	 *         by a core}</li>
	 *         <li>{@link ActorBlock A block corresponding to the code of an
	 *         non-flattened hierarchical actor}</li>
	 *         </ul>
	 * @throws CodegenException
	 *             If a vertex has an unknown {@link DAGVertex#getKind() Kind}.
	 */
	public Set<Block> generate() throws CodegenException {
		// 1 - Iterate on the actors of the DAG
		// 1.0 - Identify the core used.
		// 1.1 - Construct the "loop" of each core.
		// 1.2 - The init function of actors is executed on the same core of
		// their first firing.
		// 1.3 - Identify the buffer accessed/owned by each core.

		DAGIterator iter = new DAGIterator(dag);
		while (iter.hasNext()) {

			DAGVertex vert = iter.next();

			// 1.0 - Identify the core used.
			ComponentInstance operator = null;
			CoreBlock operatorBlock = null;
			{
				// This call can not fail as checks were already performed in
				// the constructor
				operator = (ComponentInstance) vert.getPropertyBean().getValue(
						"Operator", ComponentInstance.class);
				// If this is the first time this operator is encountered,
				// Create a Block and store it.
				operatorBlock = coreBlocks.get(operator);
				if (operatorBlock == null) {
					operatorBlock = CodegenFactory.eINSTANCE.createCoreBlock();
					coreBlocks.put(operator, operatorBlock);
				}
			} // end 1.0

			// 1.1 - Construct the "loop" of each core.
			{
				switch (((VertexType) vert.getPropertyBean().getValue(
						"vertexType", VertexType.class)).toString()) {

				case VertexType.TYPE_TASK:
					// May be an actor (Hierarchical or not) call
					// or a Fork Join call
					String vertKind = vert.getPropertyBean().getValue("kind")
							.toString();
					switch (vertKind) {
					case "dag_vertex":
						generateActorCall(operatorBlock, vert);
						break;
					case "dag_fork_vertex":
						break;
					case "dag_join_vertex":
						break;
					}
					break;

				case VertexType.TYPE_SEND:
					break;

				case VertexType.TYPE_RECEIVE:
					break;
				default:
					throw new CodegenException("Vertex " + vert
							+ " has an unknown kind: " + vert.getKind());

				}
			}
		}
		return null;

	}

	/**
	 * Generate the {@link CodegenPackage Codegen Model} for an
	 * {@link ActorCall actor call}. This method will create an
	 * {@link ActorCall} {@link Call} and place it in the {@link LoopBlock} of
	 * the given {@link CoreBlock}. If the called {@link DAGVertex actor} has an
	 * initialization function, this method will check if it has already been
	 * called. If not, it will create a call in the current {@link CoreBlock}.
	 * 
	 * @param operatorBlock
	 *            the {@link CoreBlock} where the {@link ActorCall} is
	 *            performed.
	 * @param dagVertex
	 *            the {@link DAGVertex} corresponding to the {@link ActorCall}.
	 * @throws CodegenException
	 *             if an unflattened hierarchical {@link SDFVertex actor} is
	 *             encountered or an actor without refinement, or a mismatch
	 *             between the IDL and the actor ports, or an actor port is
	 *             connected to no edge.
	 */
	protected void generateActorCall(CoreBlock operatorBlock,
			DAGVertex dagVertex) throws CodegenException {
		// Check whether the ActorCall is a call to a hierarchical actor or not.
		SDFVertex sdfVertex = (SDFVertex) dagVertex.getPropertyBean().getValue(
				"sdf_vertex", SDFVertex.class);
		Object refinement = sdfVertex.getPropertyBean().getValue("graph_desc");

		// If the actor is hierarchical
		if (refinement instanceof AbstractGraph) {
			throw new CodegenException(
					"Unflattened hierarchical actors ("
							+ sdfVertex
							+ ") are not yet supported by the Xtend Code Generation.\n"
							+ "Flatten the graph completely before using this code-generation.");
		} else // If the actor has an IDL refinement
		if (refinement instanceof CodeRefinement
				&& ((CodeRefinement) refinement).getLanguage() == Language.IDL) {

			// Retrieve the IDL File
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			String path = originalSDF
					.getHierarchicalVertexFromPath(sdfVertex.getInfo())
					.getBase().getPropertyStringValue(AbstractGraph.PATH);

			IFile algoFile = workspace.getRoot().getFileForLocation(
					new Path(path));

			IFile idlFile = algoFile.getParent().getFile(
					new Path(((CodeRefinement) refinement).getName()));

			// Retrieve the ActorPrototype
			ActorPrototypes prototypes = IDLPrototypeFactory.INSTANCE
					.create(idlFile.getRawLocation().toOSString());
			Prototype loopPrototype = prototypes.getLoopPrototype();
			// Prototype init = prototypes.getInitPrototype();

			// Create the corresponding FunctionCall
			if (loopPrototype == null) {
				throw new CodegenException(
						"Loop interface is missing from Actor (" + sdfVertex
								+ ") refinement: " + idlFile);
			}
			FunctionCall func = CodegenFactory.eINSTANCE.createFunctionCall();
			func.setName(loopPrototype.getFunctionName());

			// Retrieve the Arguments that must correspond to the incoming data
			// fifos
			for (CodeGenArgument arg : loopPrototype.getArguments().keySet()) {
				// Check that the Actor has the right ports
				SDFInterfaceVertex port;
				switch (arg.getDirection()) {
				case CodeGenArgument.OUTPUT:
					port = sdfVertex.getSink(arg.getName());
					break;
				case CodeGenArgument.INPUT:
					port = sdfVertex.getSource(arg.getName());
					break;
				default:
					port = null;
				}
				if (port == null) {
					throw new CodegenException("Mismatch between actor ("
							+ sdfVertex
							+ ") ports and IDL loop prototype argument"
							+ arg.getName());
				}
				// Retrieve the Edge corresponding to the current Argument
				DAGEdge dagEdge = null;
				SDFEdge srsdfEdge = null;
				switch (arg.getDirection()) {
				case CodeGenArgument.OUTPUT: {
					Set<DAGEdge> edges = dag.outgoingEdgesOf(dagVertex);
					for (DAGEdge edge : edges) {
						EdgeAggregate aggregate = edge.getAggregate();
						for (AbstractEdge<?, ?> subEdge : aggregate) {
							if (subEdge.getSourceLabel().equals(arg.getName())
									&& subEdge.getPropertyBean()
											.getValue(SDFEdge.DATA_TYPE)
											.toString().equals(arg.getType())) {
								dagEdge = edge;
								srsdfEdge = (SDFEdge) subEdge;
							}
						}
					}
				}
					break;
				case CodeGenArgument.INPUT: {
					Set<DAGEdge> edges = dag.incomingEdgesOf(dagVertex);
					for (DAGEdge edge : edges) {
						EdgeAggregate aggregate = edge.getAggregate();
						for (AbstractEdge<?, ?> subEdge : aggregate) {
							if (subEdge.getTargetLabel().equals(arg.getName())
									&& subEdge.getPropertyBean()
											.getValue(SDFEdge.DATA_TYPE)
											.toString().equals(arg.getType())) {
								dagEdge = edge;
								srsdfEdge = (SDFEdge) subEdge;
							}
						}
					}
				}
					break;
				}

				if (dagEdge == null || srsdfEdge == null) {
					throw new CodegenException(
							"The DAGEdge connected to the port " + port
									+ " of Actor (" + dagVertex
									+ ") does not exist.\n"
									+ "Probable cause is that the DAG"
									+ " was aletered before entering"
									+ " the Code generation.");
				}

				// At this point, the dagEdge, srsdfEdge corresponding to the
				// current argument were identified

			}

		} else
		// If the actor has no refinement
		{
			throw new CodegenException(
					"Actor ("
							+ sdfVertex
							+ ") has no valid refinement (IDL or graphml)."
							+ " Associate a refinement to this actor before generating code.");
		}

	}

	/**
	 * Verification to ensure that the inputs are valid:
	 * <ul>
	 * <li>The {@link DirectedAcyclicGraph DAG} is scheduled</li>
	 * <li>The {@link DirectedAcyclicGraph DAG} is mapped on the input
	 * {@link Design architecture}</li>
	 * <li>The {@link MemoryExclusionGraph MemEx} is derived from the
	 * {@link DirectedAcyclicGraph DAG}</li>
	 * <li>The {@link MemoryExclusionGraph MemEx} is allocated</li>
	 * </ul>
	 * 
	 * @param archi
	 *            See {@link CodegenPrinter#archi}
	 * @param dag
	 *            See {@link CodegenPrinter#dag}
	 * @param memEx
	 *            See {@link CodegenPrinter#memEx}
	 * @throws CodegenException
	 *             When one of the previous verification fails.
	 */
	protected void checkInputs(final Design archi,
			final DirectedAcyclicGraph dag, final MemoryExclusionGraph memEx)
			throws CodegenException {
		// Check that the input DAG is scheduled and Mapped on the targeted
		// architecture
		for (DAGVertex vertex : dag.vertexSet()) {
			ComponentInstance operator = (ComponentInstance) vertex
					.getPropertyBean().getValue("Operator",
							ComponentInstance.class);
			if (operator == null) {
				throw new CodegenException(
						"The DAG Actor "
								+ vertex
								+ " is not mapped on any operator.\n"
								+ " All actors must be mapped before using the code generation.");
			}

			if (!archi.getComponentInstances().contains(operator)) {
				throw new CodegenException("The DAG Actor " + vertex
						+ " is not mapped on an operator " + operator
						+ " that does not belong to the ipnut architecture.");
			}
		}

		for (MemoryExclusionVertex memObj : memEx.vertexSet()) {
			// Check that the MemEx is derived from the Input DAG
			String sourceName = memObj.getSource();
			String sinkName = memObj.getSink();

			if (dag.getVertex(sourceName) == null) {
				throw new CodegenException(
						"MemEx graph memory object ("
								+ memObj
								+ ") refers to a DAG Vertex "
								+ sourceName
								+ " that does not exist in the input DAG.\n"
								+ "Make sure that the MemEx is derived from the input DAG of the codegen.");
			}
			if (dag.getVertex(sinkName) == null) {
				throw new CodegenException(
						"MemEx graph memory object ("
								+ memObj
								+ ") refers to a DAG Vertex "
								+ sinkName
								+ " that does not exist in the input DAG.\n"
								+ "Make sure that the MemEx is derived from the input DAG of the codegen.");
			}
			// If the memObject corresponds to an edge of the DAG
			if (!sinkName.equals(sourceName)) {
				// Check that the edge corresponding to the MemObject exists.
				if (!dag.containsEdge(dag.getVertex(sourceName),
						dag.getVertex(sinkName))) {
					throw new CodegenException(
							"MemEx graph memory object ("
									+ memObj
									+ ") refers to a DAG Edge"
									+ " that does not exist in the input DAG.\n"
									+ "Make sure that the MemEx is derived from the input DAG of the codegen.");
				}
			}

			// Check that the MemEx graph is allocated.
			Integer offset = (Integer) memObj.getPropertyBean()
					.getValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY,
							Integer.class);
			if (offset == null) {
				throw new CodegenException(
						"MemEx graph memory object ("
								+ memObj
								+ ") was not allocated in memory. \n"
								+ "Make sure that the MemEx is processed by an allocation task before entering the codegen.");
			}
		}
	}
}
