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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import net.sf.dftools.algorithm.iterators.DAGIterator;
import net.sf.dftools.algorithm.model.AbstractGraph;
import net.sf.dftools.algorithm.model.CodeRefinement;
import net.sf.dftools.algorithm.model.CodeRefinement.Language;
import net.sf.dftools.algorithm.model.dag.DAGEdge;
import net.sf.dftools.algorithm.model.dag.DAGVertex;
import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.algorithm.model.parameters.Argument;
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
import org.ietr.preesm.codegen.model.CodeGenParameter;
import org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.ActorCall;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.xtend.model.codegen.Constant;
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall;
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer;
import org.ietr.preesm.codegen.xtend.model.codegen.Variable;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.serialize.ScenarioParser;
import org.ietr.preesm.core.types.BufferAggregate;
import org.ietr.preesm.core.types.BufferProperties;
import org.ietr.preesm.core.types.DataType;
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
	 * This {@link Map} associates each {@link BufferProperties} aggregated in
	 * the {@link DAGEdge edges} of the {@link DirectedAcyclicGraph DAG} to its
	 * corresponding {@link Buffer}.
	 */
	private Map<BufferProperties, Buffer> dagEdgeBuffers;

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
		this.dagEdgeBuffers = new HashMap<BufferProperties, Buffer>();
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
		// 0 - Create the Buffers of the MemEx
		// 1 - Iterate on the actors of the DAG
		// 1.0 - Identify the core used.
		// 1.1 - Construct the "loop" & "init" of each core.
		// 2 - Put the buffer declaration in their right place

		// 0 - Create the Buffers of the MemEx
		generateBuffers();

		// 1 - Iterate on the actors of the DAG
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
					operatorBlock.setName(operator.getInstanceName());
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
						generateActorFiring(operatorBlock, vert);
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
		return new HashSet<Block>(coreBlocks.values());
	}

	/**
	 * Generate the {@link CodegenPackage Codegen Model} for an actor firing.
	 * This method will create an {@link ActorCall} or a {@link FunctionCall}
	 * and place it in the {@link LoopBlock} of the {@link CoreBlock} passed as
	 * a parameter. If the called {@link DAGVertex actor} has an initialization
	 * function, this method will check if it has already been called. If not,
	 * it will create a call in the current {@link CoreBlock}.
	 * 
	 * @param operatorBlock
	 *            the {@link CoreBlock} where the actor firing is performed.
	 * @param dagVertex
	 *            the {@link DAGVertex} corresponding to the actor firing.
	 * @throws CodegenException
	 *             Exception is thrown if:
	 *             <ul>
	 *             <li>An unflattened hierarchical {@link SDFVertex actor} is
	 *             encountered an actor without refinement</li>
	 *             </ul>
	 * 
	 */
	protected void generateActorFiring(CoreBlock operatorBlock,
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
			// Retrieve the prototypes associated to the actor
			ActorPrototypes prototypes = getActorPrototypes(sdfVertex);

			// Generate the loop functionCall
			{
				Prototype loopPrototype = prototypes.getLoopPrototype();
				if (loopPrototype == null) {
					throw new CodegenException("Actor " + sdfVertex
							+ " has no loop interface in its IDL refinement.");
				}
				FunctionCall functionCall = generateFunctionCall(dagVertex,
						loopPrototype);

				// Register the core Block as a user of the function variable
				for (Variable var : functionCall.getParameters()) {
					// Currently, constants do not need to be declared nor
					// have creator since their value is directly used.
					if (!(var instanceof Constant)) {
						var.getUsers().add(operatorBlock);
					}
				}
				// Add the function call to the operatorBlock
				operatorBlock.getLoopBlock().getCodeElts().add(functionCall);
			}

			// Generate the init FunctionCall (if any)
			{
				Prototype initPrototype = prototypes.getInitPrototype();
				if (initPrototype != null) {
					FunctionCall functionCall = generateFunctionCall(dagVertex,
							initPrototype);

					// Register the core Block as a user of the function
					// variable
					for (Variable var : functionCall.getParameters()) {
						// Currently, constants do not need to be declared nor
						// have creator since their value is directly used.
						if (!(var instanceof Constant)) {
							var.getUsers().add(operatorBlock);
						}
					}
					// Add the function call to the operatorBlock
					operatorBlock.getInitBlock().getCodeElts()
							.add(functionCall);
				}

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
	 * 
	 */
	protected void generateBuffers() {
		// Right now, all memory allocations are performed only in shared
		// memory.
		// If it changes one day there will be a specific Memory exclusion graph
		// for each memory.

		// Create the Main Shared buffer
		Integer size = (Integer) memEx.getPropertyBean().getValue(
				MemoryExclusionGraph.ALLOCATED_MEMORY_SIZE, Integer.class);
		Buffer sharedBuffer = CodegenFactory.eINSTANCE.createBuffer();
		sharedBuffer.setSize(size);

		@SuppressWarnings("unchecked")
		Map<DAGEdge, Integer> allocation = (Map<DAGEdge, Integer>) memEx
				.getPropertyBean().getValue(
						MemoryExclusionGraph.DAG_EDGE_ALLOCATION,
						(new HashMap<DAGEdge, Integer>()).getClass());

		// generate the subbuffers allocated in memory. Each sub buffer
		// corresponds to an edge of the single rate SDF Graph
		for (Entry<DAGEdge, Integer> dagAlloc : allocation.entrySet()) {
			generateSubBuffers(sharedBuffer, dagAlloc.getKey(),
					dagAlloc.getValue());
		}
	}

	/**
	 * This method generates the list of variable corresponding to a prototype
	 * of the {@link DAGVertex} firing. The {@link Prototype} passed as a
	 * parameter must belong to the processed {@link DAGVertex}.
	 * 
	 * @param dagVertex
	 *            the {@link DAGVertex} corresponding to the
	 *            {@link FunctionCall}.
	 * @param prototype
	 *            the prototype whose {@link Variable variables} are retrieved
	 * @throws CodegenException
	 *             Exception is thrown if:
	 *             <ul>
	 *             <li>There is a mismatch between the {@link Prototype}
	 *             parameter and and the actor ports</li>
	 *             <li>an actor port is connected to no edge.</li>
	 *             <li>No {@link Buffer} in {@link #dagEdgeBuffers} corresponds
	 *             to the edge connected to a port of the {@link DAGVertex}</li>
	 *             <li>There is a mismatch between Parameters declared in the
	 *             IDL and in the {@link SDFGraph}</li>
	 *             </ul>
	 */
	protected List<Variable> generateCallVariables(DAGVertex dagVertex,
			Prototype prototype) throws CodegenException {
		// Retrieve the sdf vertex and the refinement.
		SDFVertex sdfVertex = (SDFVertex) dagVertex.getPropertyBean().getValue(
				DAGVertex.SDF_VERTEX, SDFVertex.class);

		// Sorted list of the variables used by the prototype.
		// The integer is only used to order the variable and is retrieved
		// from the prototype
		TreeMap<Integer, Variable> variableList = new TreeMap<Integer, Variable>();

		// Retrieve the Variable corresponding to the arguments of the prototype
		for (CodeGenArgument arg : prototype.getArguments().keySet()) {
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
						+ sdfVertex + ") ports and IDL loop prototype argument"
						+ arg.getName());
			}

			// Retrieve the Edge corresponding to the current Argument
			DAGEdge dagEdge = null;
			BufferProperties subBufferProperties = null;
			switch (arg.getDirection()) {
			case CodeGenArgument.OUTPUT: {
				Set<DAGEdge> edges = dag.outgoingEdgesOf(dagVertex);
				for (DAGEdge edge : edges) {
					BufferAggregate bufferAggregate = (BufferAggregate) edge
							.getPropertyBean().getValue(
									BufferAggregate.propertyBeanName);
					for (BufferProperties buffProperty : bufferAggregate) {
						if (buffProperty.getSourceOutputPortID().equals(
								arg.getName())
								&& buffProperty.getDataType().equals(
										arg.getType())) {
							// check that this edge is not connected to a
							// receive vertex
							if (edge.getTarget().getKind() != null) {
								dagEdge = edge;
								subBufferProperties = buffProperty;
							}
						}
					}
				}
			}
				break;
			case CodeGenArgument.INPUT: {
				Set<DAGEdge> edges = dag.incomingEdgesOf(dagVertex);
				for (DAGEdge edge : edges) {
					BufferAggregate bufferAggregate = (BufferAggregate) edge
							.getPropertyBean().getValue(
									BufferAggregate.propertyBeanName);
					for (BufferProperties buffProperty : bufferAggregate) {
						if (buffProperty.getDestInputPortID().equals(
								arg.getName())
								&& buffProperty.getDataType().equals(
										arg.getType())) {
							// check that this edge is not connected to a send
							// vertex
							if (edge.getSource().getKind() != null) {
								dagEdge = edge;
								subBufferProperties = buffProperty;
							}
						}
					}
				}
			}
				break;
			}

			if (dagEdge == null || subBufferProperties == null) {
				throw new CodegenException("The DAGEdge connected to the port "
						+ port + " of Actor (" + dagVertex
						+ ") does not exist.\n"
						+ "Probable cause is that the DAG"
						+ " was altered before entering"
						+ " the Code generation.");
			}

			// At this point, the dagEdge, srsdfEdge corresponding to the
			// current argument were identified
			// Get the corresponding Variable
			Variable var = this.dagEdgeBuffers.get(subBufferProperties);
			if (var == null) {
				throw new CodegenException(
						"Edge connected to "
								+ arg.getDirection()
								+ " port "
								+ arg.getName()
								+ " of DAG Actor "
								+ dagVertex
								+ " is not present in the input MemEx.\n"
								+ "There is something wrong in the Memory Allocation task.");
			}
			variableList.put(prototype.getArguments().get(arg), var);
		}

		// Retrieve the Variables corresponding to the Parameters of the
		// prototype
		for (CodeGenParameter param : prototype.getParameters().keySet()) {
			// Check that the actor has the right parameter
			Argument actorParam = sdfVertex.getArgument(param.getName());

			if (actorParam == null) {
				throw new CodegenException("Actor " + sdfVertex
						+ " has no match for parameter " + param.getName()
						+ " declared in the IDL.");
			}

			Constant constant = CodegenFactory.eINSTANCE.createConstant();
			constant.setName(param.getName());
			try {
				constant.setValue(actorParam.intValue());
			} catch (Exception e) {
				// Exception should never happen here since the expression was
				// evaluated before during the Workflow execution
				e.printStackTrace();
			}
			constant.setType("long");
			variableList.put(prototype.getParameters().get(param), constant);

			// // Retrieve the variable from its context (i.e. from its original
			// // (sub)graph)
			// net.sf.dftools.algorithm.model.parameters.Variable originalVar =
			// originalSDF
			// .getHierarchicalVertexFromPath(sdfVertex.getInfo())
			// .getBase().getVariables().getVariable(actorParam.getName());
			//
			// Constant constant = sdfVariableConstants.get(originalVar);
			// if (constant == null) {
			// constant = CodegenFactory.eINSTANCE.createConstant();
			// constant.setName(originalVar.getName());
			// //constant.setValue(originalVar.getValue());
			// }
		}

		return new ArrayList<Variable>(variableList.values());
	}

	/**
	 * This method generate the {@link FunctionCall} corresponding to a
	 * {@link Prototype} associated to a {@link DAGVertex}, both passed as
	 * parameters.
	 * 
	 * @param dagVertex
	 *            the {@link DAGVertex} corresponding to the
	 *            {@link FunctionCall}.
	 * @param prototype
	 *            the {@link Prototype IDL prototype} of the
	 *            {@link FunctionCall} to generate.
	 * @return The {@link FunctionCall} corresponding to the {@link DAGVertex
	 *         actor} firing.
	 * @throws CodegenException
	 * 
	 * 
	 */
	protected FunctionCall generateFunctionCall(DAGVertex dagVertex,
			Prototype prototype) throws CodegenException {
		// Create the corresponding FunctionCall
		FunctionCall func = CodegenFactory.eINSTANCE.createFunctionCall();
		func.setName(prototype.getFunctionName());

		// Retrieve the Arguments that must correspond to the incoming data
		// fifos
		List<Variable> callVars = generateCallVariables(dagVertex, prototype);
		// Put Variables in the function call
		func.getParameters().addAll(callVars);

		return func;
	}

	/**
	 * This method create a {@link SubBuffer} for each {@link SDFEdge}
	 * aggregated in the given {@link DAGEdge}. {@link SubBuffer} information
	 * are retrieved from the {@link #memEx} of the
	 * {@link CodegenModelGenerator}.
	 * 
	 * @param parentBuffer
	 *            the {@link Buffer} containing the generated {@link SubBuffer}
	 * @param dagEdge
	 *            the {@link DAGEdge} whose {@link Buffer} is generated.
	 * @param offset
	 *            the of the {@link DAGEdge} in the {@link Buffer}
	 * 
	 */
	protected void generateSubBuffers(Buffer parentBuffer, DAGEdge dagEdge,
			Integer offset) {

		Map<String, DataType> dataTypes = scenario.getSimulationManager()
				.getDataTypes();

		BufferAggregate buffers = (BufferAggregate) dagEdge.getPropertyBean()
				.getValue(BufferAggregate.propertyBeanName,
						BufferAggregate.class);

		Integer aggregateOffset = new Integer(0);
		for (BufferProperties subBufferProperties : buffers) {
			SubBuffer subBuff = CodegenFactory.eINSTANCE.createSubBuffer();
			String name = dagEdge.getSource().getName();
			name += '_' + subBufferProperties.getSourceOutputPortID();
			name += "__" + dagEdge.getTarget().getName();
			name += '_' + subBufferProperties.getDestInputPortID();
			subBuff.setName(name);
			subBuff.setContainer(parentBuffer);
			subBuff.setOffset(offset + aggregateOffset);
			subBuff.setType(subBufferProperties.getDataType());
			subBuff.setSize(subBufferProperties.getSize());

			// Increment the aggregate offset with the size of the current
			// subBuffer multiplied by the size of the datatype
			aggregateOffset += (subBuff.getSize() * dataTypes.get(
					subBufferProperties.getDataType()).getSize());

			// Save the created SubBuffer
			dagEdgeBuffers.put(subBufferProperties, subBuff);
		}

		return;
	}

	/**
	 * Retrieve the {@link ActorPrototypes prototypes} defined in the IDL
	 * {@link CodeRefinement refinement} of the {@link SDFVertex} passed as a
	 * parameter
	 * 
	 * @param sdfVertex
	 *            the {@link SDFVertex} whose IDL refinement is parsed to
	 *            retrieve the corresponding {@link ActorPrototypes}
	 * @return the parsed {@link ActorPrototypes}.
	 * @throws CodegenException
	 *             Exception is thrown if:
	 *             <ul>
	 *             <li>The {@link DAGVertex} has no IDL Refinement</li>
	 *             </ul>
	 */
	protected ActorPrototypes getActorPrototypes(SDFVertex sdfVertex)
			throws CodegenException {
		Object refinement = sdfVertex.getPropertyBean().getValue("graph_desc");

		// Check that it has an IDL refinement.
		if (!(refinement instanceof CodeRefinement)
				|| ((CodeRefinement) refinement).getLanguage() != Language.IDL) {
			throw new CodegenException(
					"generateFunctionCall was called with a DAG Vertex withoud IDL");
		}

		// Retrieve the IDL File
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		String path = originalSDF
				.getHierarchicalVertexFromPath(sdfVertex.getInfo()).getBase()
				.getPropertyStringValue(AbstractGraph.PATH);

		IFile algoFile = workspace.getRoot().getFileForLocation(new Path(path));

		IFile idlFile = algoFile.getParent().getFile(
				new Path(((CodeRefinement) refinement).getName()));

		// Retrieve the ActorPrototype
		ActorPrototypes prototypes = IDLPrototypeFactory.INSTANCE
				.create(idlFile.getRawLocation().toOSString());
		return prototypes;
	}
}