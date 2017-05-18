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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.xtext.xbase.lib.Pair;
import org.ietr.dftools.algorithm.iterators.DAGIterator;
import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.AbstractVertex;
import org.ietr.dftools.algorithm.model.CodeRefinement;
import org.ietr.dftools.algorithm.model.CodeRefinement.Language;
import org.ietr.dftools.algorithm.model.IInterface;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.algorithm.model.dag.edag.DAGBroadcastVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGEndVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGForkVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGInitVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGJoinVertex;
import org.ietr.dftools.algorithm.model.parameters.Argument;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFEndVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFInitVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import org.ietr.dftools.algorithm.model.sdf.transformations.IbsdfFlattener;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.algorithm.transforms.AbstractClust;
import org.ietr.preesm.algorithm.transforms.ClustSequence;
import org.ietr.preesm.algorithm.transforms.ClustVertex;
import org.ietr.preesm.algorithm.transforms.HSDFBuildLoops;
import org.ietr.preesm.codegen.idl.ActorPrototypes;
import org.ietr.preesm.codegen.idl.IDLPrototypeFactory;
import org.ietr.preesm.codegen.idl.Prototype;
import org.ietr.preesm.codegen.model.CodeGenArgument;
import org.ietr.preesm.codegen.model.CodeGenParameter;
import org.ietr.preesm.codegen.xtend.model.codegen.ActorBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.ActorCall;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer;
import org.ietr.preesm.codegen.xtend.model.codegen.BufferIterator;
import org.ietr.preesm.codegen.xtend.model.codegen.Call;
import org.ietr.preesm.codegen.xtend.model.codegen.CodeElt;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.xtend.model.codegen.Communication;
import org.ietr.preesm.codegen.xtend.model.codegen.CommunicationNode;
import org.ietr.preesm.codegen.xtend.model.codegen.Constant;
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.Delimiter;
import org.ietr.preesm.codegen.xtend.model.codegen.Direction;
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall;
import org.ietr.preesm.codegen.xtend.model.codegen.FifoOperation;
import org.ietr.preesm.codegen.xtend.model.codegen.FiniteLoopBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.FunctionCall;
import org.ietr.preesm.codegen.xtend.model.codegen.IntVar;
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.NullBuffer;
import org.ietr.preesm.codegen.xtend.model.codegen.PortDirection;
import org.ietr.preesm.codegen.xtend.model.codegen.Semaphore;
import org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication;
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall;
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialType;
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer;
import org.ietr.preesm.codegen.xtend.model.codegen.Variable;
import org.ietr.preesm.core.architecture.route.MessageRouteStep;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.types.BufferAggregate;
import org.ietr.preesm.core.types.BufferProperties;
import org.ietr.preesm.core.types.DataType;
import org.ietr.preesm.core.types.ImplementationPropertyNames;
import org.ietr.preesm.core.types.VertexType;
import org.ietr.preesm.memory.allocation.AbstractMemoryAllocatorTask;
import org.ietr.preesm.memory.allocation.MemoryAllocator;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;
import org.ietr.preesm.memory.script.Range;
import org.w3c.dom.ls.LSInput;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

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

	/** The shared {@link Buffer} */
	// private Buffer sharedBuffer;

	/**
	 * {@link Map} of the main {@link Buffer} for the code generation. Each
	 * {@link Buffer} in this {@link List} contains one or more
	 * {@link SubBuffer} and is associated to a unique memory bank, whose name
	 * is given by the associated {@link String} in the {@link Map}.
	 */
	private Map<String, Buffer> mainBuffers;

	/**
	 * Map used to keep track of the number of {@link Buffer} created with a
	 * given name. Since buffer are named using ports names, duplicates name may
	 * happen and number must be added to ensure correctness.
	 */
	private Map<String, Integer> bufferNames;

	/**
	 * {@link DirectedAcyclicGraph DAG} used to generate code. This
	 * {@link DirectedAcyclicGraph DAG} must be the result of mapping/scheduling
	 * process.
	 */
	private DirectedAcyclicGraph dag;

	/**
	 * {@link Map} of {@link String} and {@link MemoryExclusionGraph MEG} used
	 * to generate code. These {@link MemoryExclusionGraph MemEx MEGs} must be
	 * the result of an allocation process. Each {@link String} corresponds to a
	 * memory bank where the associated MEG is allocated.
	 * 
	 * @see MemoryAllocator
	 */
	private Map<String, MemoryExclusionGraph> megs;

	/**
	 * {@link PreesmScenario Scenario} at the origin of the call to the
	 * {@link AbstractCodegenPrinter Code Generator}.
	 */
	private PreesmScenario scenario;

	private Workflow workflow;

	/**
	 * This {@link Map} associates each {@link ComponentInstance} to its
	 * corresponding {@link CoreBlock}.
	 */
	protected Map<ComponentInstance, CoreBlock> coreBlocks;

	/**
	 * This {@link Map} associates each {@link BufferProperties} aggregated in
	 * the {@link DAGEdge edges} of the {@link DirectedAcyclicGraph DAG} to its
	 * corresponding {@link Buffer}.
	 */
	private Map<BufferProperties, Buffer> srSDFEdgeBuffers;

	/**
	 * This {@link BiMap} associates each {@link DAGEdge} to its corresponding
	 * {@link Buffer}.
	 */
	private BiMap<DAGEdge, Buffer> dagEdgeBuffers;

	/**
	 * This {@link Map} associates each {@link Pair} of init and end
	 * {@link DAGVertex} to their corresponding {@link Pair} of {@link Buffer},
	 * the first for the FIFO head, and the second for the FIFO body (if any).
	 */
	private Map<Pair<DAGVertex, DAGVertex>, Pair<Buffer, Buffer>> dagFifoBuffers;

	/**
	 * This {@link Map} associates a {@link SDFInitVertex} to its corresponding
	 * {@link FifoOperation#POP Pop} {@link FifoCall}.
	 */
	private Map<SDFInitVertex, FifoCall> popFifoCalls;

	/**
	 * This {@link Map} associates each {@link DAGVertex} to its corresponding
	 * {@link Call}. It will be filled during when creating the function call of
	 * actors and updated later by inserting {@link Communication} {@link Call
	 * calls}. For {@link Communication}, only the End Receive and the Start
	 * Send communications will be stored in this map to avoid having multiple
	 * calls for a unique {@link DAGVertex}.
	 */
	private BiMap<DAGVertex, Call> dagVertexCalls;

	/**
	 * This {@link Map} associates a unique communication ID to a list of all
	 * the {@link Communication} {@link Call Calls} in involves. The
	 * communication id is a {@link String} formed as follow:<br>
	 * <code>SenderCore__SenderVertexName___ReceiverCore__ReceiverVertexName </code>
	 */
	private Map<String, List<Communication>> communications;

	/** 
	 * This is used to compute working the buffer offset inside the working memory.
	 * It is reinitialize to zero at the end of each hierarchical actor print.
	 */
	private int currentWorkingMemOffset = 0;
	
	/**
	 * This {@link Map} associates a dag hierarchical vertex to the internal allocated working memory.
	 */
	private Map<DAGVertex, Buffer> linkHSDFVertexBuffer;
	
	/**
	 * During the code generation of hierarchical actors, this {@link Map} associates internal edges to a buffer
	 * to link input/output buffer (edge) when printing internal vertex and subbuffers of the internal working memory
	 * of hierarchical actor. This {@link Map} is cleared at the end of the hierarchical actor print. 
	 */
	private Map<SDFEdge, Buffer> linkHSDFEdgeBuffer;

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
	 *            See {@link AbstractCodegenPrinter#archi}
	 * @param dag
	 *            See {@link AbstractCodegenPrinter#dag}
	 * @param megs
	 *            See {@link AbstractCodegenPrinter#megs}
	 * @param scenario
	 *            See {@link AbstractCodegenPrinter#scenario}
	 * @throws CodegenException
	 *             When one of the previous verification fails.
	 */
	public CodegenModelGenerator(final Design archi, final DirectedAcyclicGraph dag,
			final Map<String, MemoryExclusionGraph> megs, final PreesmScenario scenario, Workflow workflow)
			throws CodegenException {
		this.archi = archi;
		this.dag = dag;
		this.megs = megs;
		this.scenario = scenario;
		this.workflow = workflow;

		checkInputs(this.archi, this.dag, this.megs);
		this.bufferNames = new HashMap<String, Integer>();
		this.mainBuffers = new HashMap<String, Buffer>();
		this.coreBlocks = new HashMap<ComponentInstance, CoreBlock>();
		this.srSDFEdgeBuffers = new HashMap<BufferProperties, Buffer>();
		this.dagEdgeBuffers = HashBiMap.create(dag.edgeSet().size());
		this.dagFifoBuffers = new HashMap<Pair<DAGVertex, DAGVertex>, Pair<Buffer, Buffer>>();
		this.dagVertexCalls = HashBiMap.create(dag.vertexSet().size());
		this.communications = new HashMap<String, List<Communication>>();
		this.popFifoCalls = new HashMap<SDFInitVertex, FifoCall>();
		this.linkHSDFVertexBuffer = new HashMap<DAGVertex, Buffer>();
		this.linkHSDFEdgeBuffer= new HashMap<SDFEdge, Buffer>();
	}

	/**
	 * Verification to ensure that the inputs are valid:
	 * <ul>
	 * <li>The {@link DirectedAcyclicGraph DAG} is scheduled</li>
	 * <li>The {@link DirectedAcyclicGraph DAG} is mapped on the input
	 * {@link Design architecture}</li>
	 * <li>The {@link MemoryExclusionGraph MEGs} are derived from the
	 * {@link DirectedAcyclicGraph DAG}</li>
	 * <li>The {@link MemoryExclusionGraph MEGs} are allocated</li>
	 * </ul>
	 * 
	 * @param archi
	 *            See {@link AbstractCodegenPrinter#archi}
	 * @param dag
	 *            See {@link AbstractCodegenPrinter#dag}
	 * @param megs
	 *            See {@link AbstractCodegenPrinter#megs}
	 * @throws CodegenException
	 *             When one of the previous verification fails.
	 */
	protected void checkInputs(final Design archi, final DirectedAcyclicGraph dag,
			final Map<String, MemoryExclusionGraph> megs) throws CodegenException {
		// Check that the input DAG is scheduled and Mapped on the targeted
		// architecture
		for (DAGVertex vertex : dag.vertexSet()) {
			ComponentInstance operator = (ComponentInstance) vertex.getPropertyBean().getValue("Operator",
					ComponentInstance.class);
			if (operator == null) {
				throw new CodegenException("The DAG Actor " + vertex + " is not mapped on any operator.\n"
						+ " All actors must be mapped before using the code generation.");
			}

			if (!archi.getComponentInstances().contains(operator)) {
				throw new CodegenException("The DAG Actor " + vertex + " is not mapped on an operator " + operator
						+ " that does not belong to the ipnut architecture.");
			}
		}

		for (MemoryExclusionGraph meg : megs.values()) {
			for (MemoryExclusionVertex memObj : meg.vertexSet()) {
				// Check that the MemEx is derived from the Input DAG
				String sourceName = memObj.getSource();
				String sinkName = memObj.getSink();

				// If the MObject is a part of a divide buffer
				sourceName = sourceName.replaceFirst("^part[0-9]+_", "");

				boolean isFifo = sourceName.startsWith("FIFO");
				if (isFifo) {
					sourceName = sourceName.substring(10, sourceName.length());
				}

				if (dag.getVertex(sourceName) == null) {
					throw new CodegenException("MemEx graph memory object (" + memObj + ") refers to a DAG Vertex "
							+ sourceName + " that does not exist in the input DAG.\n"
							+ "Make sure that the MemEx is derived from the input DAG of the codegen.");
				}
				if (dag.getVertex(sinkName) == null) {
					throw new CodegenException("MemEx graph memory object (" + memObj + ") refers to a DAG Vertex "
							+ sinkName + " that does not exist in the input DAG.\n"
							+ "Make sure that the MemEx is derived from the input DAG of the codegen.");
				}
				// If the memObject corresponds to an edge of the DAG
				if (!sinkName.equals(sourceName) && !isFifo) {
					// Check that the edge corresponding to the MemObject
					// exists.
					if (!dag.containsEdge(dag.getVertex(sourceName), dag.getVertex(sinkName))) {
						throw new CodegenException("MemEx graph memory object (" + memObj + ") refers to a DAG Edge"
								+ " that does not exist in the input DAG.\n"
								+ "Make sure that the MemEx is derived from the input DAG of the codegen.");
					}
				}

				// Check that the MemEx graph is allocated.
				Integer offset = (Integer) memObj.getPropertyBean()
						.getValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY, Integer.class);
				if (offset == null) {
					throw new CodegenException("MemEx graph memory object (" + memObj
							+ ") was not allocated in memory. \n"
							+ "Make sure that the MemEx is processed by an allocation task before entering the codegen.");
				}
			}
		}
	}

	/**
	 * Finds the {@link MemoryExclusionVertex} associated to the given
	 * {@link DAGEdge} in the {@link #megs}.
	 * 
	 * @param dagEdge
	 *            {@link DAGEdge} whose associated {@link MemoryExclusionVertex}
	 *            is to be found.
	 * @return the found {@link MemoryExclusionVertex}
	 * @throws CodegenException
	 *             throws an exception if the {@link MemoryExclusionVertex}
	 *             associated to a {@link DAGEdge} could not be found in any
	 *             {@link #megs}.
	 */
	protected MemoryExclusionVertex findMObject(DAGEdge dagEdge) throws CodegenException {
		MemoryExclusionVertex mObject = null;
		// Find the associated memory object
		for (MemoryExclusionGraph meg : megs.values()) {
			mObject = meg.getVertex(new MemoryExclusionVertex(dagEdge));
			if (mObject != null) {
				break;
			}
		}
		if (mObject == null) {
			throw new CodegenException("Memory Object associated to DAGEdge " + dagEdge
					+ " could not be found in any memory exclusion graph.");
		}

		return mObject;
	}

	/**
	 * Insert the {@link Communication} in the {@link LoopBlock} of the
	 * {@link CoreBlock} passed as a parameter. All {@link DAGVertex} consuming
	 * or producing data handled by the {@link Communication} must have been
	 * scheduled {@link #generateActorFiring(CoreBlock, DAGVertex) generated}
	 * before calling this method.<br>
	 * <br>
	 * In the current version, Send primitives are inserted as follow:<br>
	 * <code>(SendEnd)-(ProducingActor)-(SendStart)</code><br>
	 * and Receive primitives as follow:<br>
	 * <code>(ReceiveEnd)-(ConsumingActor)-(ReceiveStart)</code> <br>
	 * The SendEnd and ReceiveStart placed like this enable the
	 * reception/sending for the next iteration. <br>
	 * 
	 * 
	 * @param operatorBlock
	 *            the {@link CoreBlock} on which the communication is executed.
	 * @param dagVertex
	 *            the {@link DAGVertex} corresponding to the given
	 *            {@link Communication}.
	 * @param newComm
	 *            the {@link Communication} {@link Call} to insert.
	 */
	protected void futurinsertCommunication(CoreBlock operatorBlock, DAGVertex dagVertex, Communication newComm) {
		if (dagVertex != null && newComm != null) { // dumb if only here to
													// remove waring
			// This method was kept only as a hint on how to implement future
			// "smarter" insertion where send receive zones may span over
			// multiple iterations. This method will be safe only when each
			// buffer has a dedicated space in memory.
			throw new RuntimeException("This method is not completely coded, do not use it !");
			// This method does not work, especially because the insertion of RS
			// and SE is corrupted in case of multistep com.
		}

		// Retrieve the vertex that must be before/after the communication.
		DAGVertex producerOrConsumer = null;
		if (newComm.getDirection().equals(Direction.SEND)) {
			// Get the producer.
			producerOrConsumer = dag.incomingEdgesOf(dagVertex).iterator().next().getSource();
		} else {
			producerOrConsumer = dag.outgoingEdgesOf(dagVertex).iterator().next().getTarget();
		}

		// Get the corresponding call
		Call prodOrConsumerCall = dagVertexCalls.get(producerOrConsumer);
		int index = operatorBlock.getLoopBlock().getCodeElts().indexOf(prodOrConsumerCall);
		// If the index was found
		if (index != -1) {
			if (newComm.getDelimiter().equals(Delimiter.START)) {
				// Insert after the producer/consumer
				operatorBlock.getLoopBlock().getCodeElts().add(index + 1, newComm);
			} else {
				// Insert before the producer/consumer
				operatorBlock.getLoopBlock().getCodeElts().add(index, newComm);
			}

			// Save the communication in the dagVertexCalls map only if it is a
			// SS or a ER
			if ((newComm.getDelimiter().equals(Delimiter.START) && newComm.getDirection().equals(Direction.SEND))
					|| (newComm.getDelimiter().equals(Delimiter.END)
							&& newComm.getDirection().equals(Direction.RECEIVE))) {
				dagVertexCalls.put(dagVertex, newComm);
			}
		} else {
			// The index was not found, this may happen when a multi-step
			// communication occurs
			// The receive end of the first step of a multistep communication
			// will be the first to be processed.
			if (newComm.getDelimiter().equals(Delimiter.END) && newComm.getDirection().equals(Direction.RECEIVE)) {
				// Insert it according to its scheduled place.
				int dagVertexSchedulingOrder = (Integer) dagVertex.getPropertyBean()
						.getValue(ImplementationPropertyNames.Vertex_schedulingOrder, Integer.class);
				int insertionIndex = 0;
				for (CodeElt codeElt : operatorBlock.getLoopBlock().getCodeElts()) {
					// Iterate over the calls of the current operator
					if (codeElt instanceof Call) {
						DAGVertex vertex = dagVertexCalls.inverse().get(codeElt);

						if (vertex == null) {
							// this will happen when a ReceiveStart or a Receive
							// End is encountered, since they have no
							// corresponding vertices in the DAG
						} else if ((Integer) vertex.getPropertyBean().getValue(
								ImplementationPropertyNames.Vertex_schedulingOrder,
								Integer.class) > dagVertexSchedulingOrder) {
							break;
						}
					}
					insertionIndex++;
				}
				// Do the insertion
				operatorBlock.getLoopBlock().getCodeElts().add(insertionIndex, newComm);
				dagVertexCalls.put(dagVertex, newComm);
			} else if (newComm.getDelimiter().equals(Delimiter.START)
					&& newComm.getDirection().equals(Direction.RECEIVE)) {
				// In multistep communications, RS will be processed (inserted)
				// before the associated "consumer" (a SS) is inserted. In such
				// case, the RS is simply inserted right before its associated
				// RE. When the SS is processed, it will automatically be
				// inserted right after its producer (i.e. the RE) and hence,
				// just before the RS. (This imply that the SS performes a copy
				// of the data during its execution, which is to be expected in
				// multistep comm)

				int insertionIndex = operatorBlock.getLoopBlock().getCodeElts().indexOf(newComm.getReceiveEnd());
				// Do the insertion
				operatorBlock.getLoopBlock().getCodeElts().add(insertionIndex + 1, newComm);
				// Do not save RS in dagVertexCalls !
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
		// -1- Add all hosted MemoryObject back in te MemEx
		// 0 - Create the Buffers of the MemEx

		// 1 - Iterate on the actors of the DAG
		// 1.0 - Identify the core used.
		// 1.1 - Construct the "loop" & "init" of each core.
		// 2 - Put the buffer declaration in their right place

		// -1 - Add all hosted MemoryObject back in te MemEx
		restoreHostedVertices();

		// 0 - Create the Buffers of the MemEx
		generateBuffers();

		// 1 - Create a dagVertexList in SCHEDULING Order !
		List<DAGVertex> vertexInSchedulingOrder = new ArrayList<DAGVertex>();
		{
			DAGIterator iter = new DAGIterator(dag);
			// Fill a Map with Scheduling order and DAGvertices
			TreeMap<Integer, DAGVertex> orderedDAGVertexMap = new TreeMap<Integer, DAGVertex>();

			while (iter.hasNext()) {
				DAGVertex vertex = iter.next();
				Integer order = (Integer) vertex.getPropertyBean()
						.getValue(ImplementationPropertyNames.Vertex_schedulingOrder, Integer.class);
				orderedDAGVertexMap.put(order, vertex);
			}
			vertexInSchedulingOrder.addAll(orderedDAGVertexMap.values());
		}

		// 1 - Iterate on the actors of the DAG in their scheduling order !
		for (DAGVertex vert : vertexInSchedulingOrder) {

			// 1.0 - Identify the core used.
			ComponentInstance operator = null;
			CoreBlock operatorBlock = null;
			{
				// This call can not fail as checks were already performed in
				// the constructor
				operator = (ComponentInstance) vert.getPropertyBean()
						.getValue(ImplementationPropertyNames.Vertex_Operator, ComponentInstance.class);
				// If this is the first time this operator is encountered,
				// Create a Block and store it.
				operatorBlock = coreBlocks.get(operator);
				if (operatorBlock == null) {
					operatorBlock = CodegenFactory.eINSTANCE.createCoreBlock();
					operatorBlock.setName(operator.getInstanceName());
					operatorBlock.setCoreType(operator.getComponent().getVlnv().getName());
					coreBlocks.put(operator, operatorBlock);
				}
			} // end 1.0

			// 1.1 - Construct the "loop" of each core.
			{
				String vertexType = ((VertexType) vert.getPropertyBean()
						.getValue(ImplementationPropertyNames.Vertex_vertexType, VertexType.class)).toString();
				switch (vertexType) {

				case VertexType.TYPE_TASK:
					// May be an actor (Hierarchical or not) call
					// or a Fork Join call
					String vertKind = vert.getPropertyBean().getValue(AbstractVertex.KIND).toString();
					switch (vertKind) {
					case DAGVertex.DAG_VERTEX:
						generateActorFiring(operatorBlock, vert);
						break;
					case DAGForkVertex.DAG_FORK_VERTEX:
						generateSpecialCall(operatorBlock, vert);
						break;
					case DAGJoinVertex.DAG_JOIN_VERTEX:
						generateSpecialCall(operatorBlock, vert);
						break;
					case DAGBroadcastVertex.DAG_BROADCAST_VERTEX:
						generateSpecialCall(operatorBlock, vert);
						break;
					case DAGInitVertex.DAG_INIT_VERTEX:
						generateFifoCall(operatorBlock, vert);
						break;
					case DAGEndVertex.DAG_END_VERTEX:
						generateFifoCall(operatorBlock, vert);
						break;
					default:
						throw new CodegenException("DAGVertex " + vert + " has an unknown kind: " + vertKind);
					}
					break;

				case VertexType.TYPE_SEND:
					generateCommunication(operatorBlock, vert, VertexType.TYPE_SEND);
					break;

				case VertexType.TYPE_RECEIVE:
					generateCommunication(operatorBlock, vert, VertexType.TYPE_RECEIVE);
					break;
				default:
					throw new CodegenException("Vertex " + vert + " has an unknown type: " + vert.getKind());
				}
			}
		}

		// 2 - Put the buffer definition in their right place
		generateBufferDefinitions();

		return new HashSet<Block>(coreBlocks.values());
	}

	private void p(String s) {
		Logger logger = WorkflowLogger.getLogger();
		logger.log(Level.INFO, s);
	}

	private int getSDFVertexNbRepeated(SDFAbstractVertex s) {
		ActorPrototypes prototypes = null;
		int vertexRep = 1;
		Object vertex_ref = s.getPropertyBean().getValue(AbstractVertex.REFINEMENT);
		if (vertex_ref instanceof ActorPrototypes)
			prototypes = (ActorPrototypes) vertex_ref;
		if (prototypes != null) {
			/* get repetition vector */
			try {
				vertexRep = s.getNbRepeatAsInteger();
			} catch (InvalidExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// throw new CodegenException("getSDFVertexNbRepeated failed");
			p("getSDFVertexNbRepeated failed");
		}
		return vertexRep;
	}

	@SuppressWarnings("unused")
	private SDFInterfaceVertex hasCommonSources(SDFAbstractVertex a, SDFAbstractVertex b) {
		for (int i = 0; i < a.getSources().size(); i++) {
			for (int j = 0; j < b.getSources().size(); j++) {
				SDFEdge ae = a.getAssociatedEdge(a.getSources().get(i));
				SDFEdge be = b.getAssociatedEdge(b.getSources().get(j));
				// if(a.getSources().get(i) == b.getSources().get(j))
				// p("sources ae source " + ae.getSourceLabel() + " ae target "
				// + ae.getTargetLabel() +
				// " be source " + be.getSourceLabel() + " be target " +
				// be.getTargetLabel() );
				if (ae == be) {
					return b.getSources().get(j);
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	private SDFInterfaceVertex hasCommonSinks(SDFAbstractVertex a, SDFAbstractVertex b) {
		for (int i = 0; i < a.getSinks().size(); i++) {
			for (int j = 0; j < b.getSinks().size(); j++) {
				SDFEdge ae = a.getAssociatedEdge(a.getSinks().get(i));
				SDFEdge be = b.getAssociatedEdge(b.getSinks().get(j));
				// if(a.getSinks().get(i) == b.getSinks().get(j))
				// p("sinks ae source " + ae.getSourceLabel() + " ae target " +
				// ae.getTargetLabel() +
				// " be source " + be.getSourceLabel() + " be target " +
				// be.getTargetLabel() );
				if (ae == be) {
					return b.getSinks().get(j);
				}
			}
		}
		return null;
	}

	private SDFEdge hasCommonEdges(SDFAbstractVertex prev, SDFAbstractVertex check, SDFAbstractVertex top) {
		if (prev == top) {
			// p("Hierarchical hasCommonEdges " + prev.getName() + " " +
			// check.getName());
			for (int i = 0; i < prev.getSources().size(); i++) {
				for (int j = 0; j < check.getSources().size(); j++) {
					SDFEdge edgePrev = prev.getAssociatedEdge(
							prev.getSources().get(i)); /* left actor */
					SDFEdge edgeCheck = check.getAssociatedEdge(
							check.getSources().get(j)); /* right actor */
					// if(edgePrev.getTargetLabel().hashCode() ==
					// edgeCheck.getSourceLabel().hashCode())
					if (edgePrev.getTargetLabel() == edgeCheck.getSourceLabel()) {
						// p("MATCH " + edgePrev.getTargetLabel().hashCode());
						// p("SOURCES Prev " + edgePrev.getSourceLabel() + " " +
						// edgePrev.getTargetLabel() );
						// p("SOURCES Check " + edgeCheck.getSourceLabel() + " "
						// + edgeCheck.getTargetLabel() );
						return edgePrev;
					}
				}
			}
		} else {
			// p("In graph hasCommonEdges " + prev.getName() + " " +
			// check.getName());
			for (int i = 0; i < prev.getSinks().size(); i++) {
				for (int j = 0; j < check.getSources().size(); j++) {
					SDFEdge edgePrev = prev.getAssociatedEdge(
							prev.getSinks().get(i)); /* left actor */
					SDFEdge edgeCheck = check.getAssociatedEdge(
							check.getSources().get(j)); /* right actor */
					// if(edgePrev.getTargetLabel().hashCode() ==
					// edgeCheck.getTargetLabel().hashCode())
					if (edgePrev.getTargetLabel() == edgeCheck.getTargetLabel()) {
						// p("MATCH " + edgePrev.getTargetLabel().hashCode());
						// p("SOURCES Prev " + edgePrev.getSourceLabel() + " " +
						// edgePrev.getTargetLabel() );
						// p("SOURCES Check " + edgeCheck.getSourceLabel() + " "
						// + edgeCheck.getTargetLabel() );
						return edgePrev;
					}
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	private void pv(SDFAbstractVertex v) {
		p("\tActor Name Dump Params: " + v.getName());
		p("\t\tInterfaces: " + v.getInterfaces());
		p("\t\tSources: " + v.getSources());
		p("\t\tSinks: " + v.getSinks());
	}

	/**
	 * Method to generate the intermediate model of hierarchical actors of the
	 * codegen based on the {@link Design architecture}, the
	 * {@link MemoryExclusionGraph MemEx graph} , the
	 * {@link DirectedAcyclicGraph DAG} and the {@link PreesmScenario scenario}.
	 * 
	 * @return 0 on success
	 * @throws SDF4JException 
	 * @throws WorkflowException 
	 */
	private int tryGenerateRepeatActorFiring(CoreBlock operatorBlock, DAGVertex dagVertex) throws SDF4JException, WorkflowException {
		// Check whether the ActorCall is a call to a hierarchical actor or not.
		SDFVertex sdfVertex = (SDFVertex) dagVertex.getPropertyBean().getValue(DAGVertex.SDF_VERTEX, SDFVertex.class);
		Object refinement = sdfVertex.getPropertyBean().getValue(AbstractVertex.REFINEMENT);

		p("Generating code for hierarchical actor " + sdfVertex.getName());
		if (refinement instanceof AbstractGraph) {
			// p(sdfVertex.getName());
			// pv(sdfVertex);
			SDFGraph graph = (SDFGraph) sdfVertex.getGraphDescription();
			int nbActor = 0;
			List<SDFAbstractVertex> repVertexs = new ArrayList<SDFAbstractVertex>();
			List<SDFInterfaceVertex> interfaces = new ArrayList<SDFInterfaceVertex>();

			// flat everything
			IbsdfFlattener flattener = new IbsdfFlattener(graph,10);
			SDFGraph resultGraph = null;
			try {
				flattener.flattenGraph();
				resultGraph = flattener.getFlattenedGraph();
			} catch (SDF4JException e) {
				throw (new WorkflowException(e.getMessage()));
			}
			resultGraph.validateModel(WorkflowLogger.getLogger()); // compute repetition vectors
			if(resultGraph.isSchedulable() == false){
				throw (new WorkflowException("HSDF Build Loops generate clustering: Graph not schedulable"));
			}
			
			// Check nb actor for loop generation as only one actor in the
			// hierarchy is supported yet
			p("Flattened Graph Results\n");
			for (SDFAbstractVertex v : resultGraph.vertexSet()) {
				if (v instanceof SDFVertex) {
					//SDFAbstractVertex repVertex = v;
					repVertexs.add(v);
					nbActor++;
					p("Actor " +  v.getName() + " repeated " + getSDFVertexNbRepeated(v));
					// pv(v);
					// SDFInterfaceVertex iV = null; if((iV = hasCommonSources(v, sdfVertex)) != null) {
					// p("hasCommonSources: " + iV + " Actors " + v.getName() +
					// " and " + sdfVertex.getName()); } if((iV = hasCommonSinks(v, sdfVertex)) != null) {
					// p("hasCommonSinks: " + iV + " Actors " + v.getName() + " and " + sdfVertex.getName()); }
				}
				if (v instanceof SDFInterfaceVertex) {
					interfaces.add((SDFInterfaceVertex) v);
					p("Interface Vertex " + v.getName());
				}
				//if (v instanceof SDFInterfaceVertex) {
				//	
				//} else if (v instanceof SDFVertex) {
				//	p("\tSDF Vertex " + v.getName());
				//} else {
				//	p("\tSDF Abs Vertex " + v.getName());
				//}
			}

			HSDFBuildLoops loopBuilder = new HSDFBuildLoops();
			AbstractClust clust = null;
			try {
				clust = loopBuilder.generateClustering(resultGraph);
				loopBuilder.printClusteringSchedule(clust);
			} catch (WorkflowException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SDF4JException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (InvalidExpressionException e3){
				e3.printStackTrace();
			}

			//SDFRandomGraph sdfRandom = new SDFRandomGraph();
			/*DirectedAcyclicGraphGenerator sdfRandom = new DirectedAcyclicGraphGenerator();
			SDFGraph g = null;
			g = sdfRandom.createAcyclicRandomGraph(40, 1, 20, 1, 20);
			try {
				loopBuilder.generateClustering(g);
			} catch (WorkflowException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SDF4JException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			//REPVertex r = repV;
			//while(r != null){
			//	//p(r.r + " (" + r.ra + " * " + r.a.getName() + " " + r.rb + " * " + r.b.getName() + ")");
			//	if(r.get != null){
			//		r = r.repVertex;
			//	}else{
			//		break; 
			//	}
			//}

			// put vertex in the hierarchical actor in right order
			//List<SDFAbstractVertex> sortedRepVertexs = new ArrayList<SDFAbstractVertex>();			
			//SDFAbstractVertex prev = sdfVertex;
			//for (int nbActorLeft = 0; nbActorLeft < nbActor; nbActorLeft++) {
			//	int success = 0;
			//	// p("Try find right actor for actor " + prev.getName());
			//	for (SDFAbstractVertex v : repVertexs) {
			//		// p("Test left actor " + v.getName() + " with right actor "
			//		// + prev.getName());
			//		if (hasCommonEdges(prev, v, sdfVertex) != null) {
			//			sortedRepVertexs.add(v);
			//			repVertexs.remove(v);
			//			prev = v;
			//			success = 1;
			//			break;
			//		}
			//	}
			//	if (success == 0) {
			//		throw new CodegenException(
			//				"Hierarchical codegen failed to find right actor for actor " + prev.getName());
			//	}
			//}
			// for(SDFAbstractVertex v : sortedRepVertexs){
			// p("Sorted Vertex " + v.getName() + " rep " +
			// getSDFVertexNbRepeated(v));
			// }
			// check that hierarchical actor interfaces sinks or sources size is
			List<SDFAbstractVertex> inputRepVertexs = new ArrayList<SDFAbstractVertex>(); 
			List<SDFAbstractVertex> outputRepVertexs = new ArrayList<SDFAbstractVertex>();
			for (SDFInterfaceVertex i : interfaces) {
				p("Current interface " + i.getName() + " dir " + i.getDirection());
				for (SDFInterfaceVertex s : i.getSources()) {
					SDFAbstractVertex a = i.getAssociatedEdge(s).getTarget();
					SDFAbstractVertex b = i.getAssociatedEdge(s).getSource();
					if(a instanceof SDFVertex){
						outputRepVertexs.add(a);
						p("1 input target " + a.getName());
					}
					if(b instanceof SDFVertex){
						inputRepVertexs.add(b);
						p("2 input source " + b.getName());
					}
				}
				for (SDFInterfaceVertex s : i.getSinks()) {
					SDFAbstractVertex a = i.getAssociatedEdge(s).getTarget();
					SDFAbstractVertex b = i.getAssociatedEdge(s).getSource();
					if(a instanceof SDFVertex){
						inputRepVertexs.add(a);
						p("3 output target " + a.getName());
					}
					if(b instanceof SDFVertex){
						outputRepVertexs.add(b);
						p("4 output source " + b.getName());
					}
				}
				/*i.getInterfaces()
				if(i.getDirection().toString().equals("Output") == true){
					for (SDFInterfaceVertex s : i.getSources()) {
						SDFAbstractVertex a = i.getAssociatedEdge(s).getTarget();
						SDFAbstractVertex b = i.getAssociatedEdge(s).getSource();
						if(a instanceof SDFVertex){
							outputRepVertexs.add(a);
						}
						if(b instanceof SDFVertex){
							outputRepVertexs.add(b);
						}
					}
				}else if(i.getDirection().toString().equals("Input") == true){
					for (SDFInterfaceVertex s : i.getSinks()) {
						SDFAbstractVertex a = i.getAssociatedEdge(s).getTarget();
						SDFAbstractVertex b = i.getAssociatedEdge(s).getSource();
						if(a instanceof SDFVertex){
							inputRepVertexs.add(a);
						}
						if(b instanceof SDFVertex){
							inputRepVertexs.add(b);
						}
					}	
				}*/
				/*for (SDFInterfaceVertex s : i.getSources()) {
					SDFAbstractVertex a = i.getAssociatedEdge(s).getTarget();
					SDFAbstractVertex b = i.getAssociatedEdge(s).getSource();
					if(a instanceof SDFVertex){
						outputRepVertexs.add(a);
					}
					if(b instanceof SDFVertex){
						outputRepVertexs.add(b);
					}
					p("Output Source actor " + i.getAssociatedEdge(s).getSource().getName() + " dir " + i.getDirection().toString());
					p("Output Target actor " + i.getAssociatedEdge(s).getTarget().getName() + " dir " + i.getDirection().toString());
				}
				for (SDFInterfaceVertex s : i.getSinks()) {
					SDFAbstractVertex a = i.getAssociatedEdge(s).getTarget();
					SDFAbstractVertex b = i.getAssociatedEdge(s).getSource();
					if(a instanceof SDFVertex){
						outputRepVertexs.add(a);
					}
					if(b instanceof SDFVertex){
						outputRepVertexs.add(b);
					}
					p("Input Source actor " + i.getAssociatedEdge(s).getSource().getName() + " dir " + i.getDirection().toString());
					p("Input Target actor " + i.getAssociatedEdge(s).getTarget().getName() + " dir " + i.getDirection().toString());
				}*/
				//if (i.getSources().size() > 1 || i.getSinks().size() > 1) {
				//	throw new CodegenException("Hierarchical codegen failed for hierarchical actor "
				//			+ sdfVertex.getName() + " number of sinks " + i.getSinks().size() + " or sources "
				//			+ i.getSources().size() + " is great than 1");
				//}
			}
			for(SDFAbstractVertex s : inputRepVertexs){
				p("Input Vertex " + s.getName());
			}
			for(SDFAbstractVertex s : outputRepVertexs){
				p("Output Vertex " + s.getName());
			}
			//for (int i = 0; i < nbActor; i++) {
			//	SDFAbstractVertex repVertex = sortedRepVertexs.get(i);
			//	p("Codegen Model Vertex " + repVertex.getName());				
			//}
			List<AbstractClust> listScheduleLoop = loopBuilder.getLoopClust(clust);
			
			for(AbstractClust c : listScheduleLoop){
				if(c instanceof ClustVertex){
					p("ListScheduleLoop ClustVertex " + ((ClustVertex)c).getVertex().getName() + " repeat " + getSDFVertexNbRepeated(((ClustVertex)c).getVertex()) + " rep clust " + ((ClustVertex)c).getRepeat());
				}else if(c instanceof ClustSequence){
					p("ListScheduleLoop ClustSequence ForLoop iter " + ((ClustSequence)c).getRepeat());
				}else{
					p("ListScheduleLoop Failed to dump cluster");
				}
			}
			
			p("Printing Code\n");

			int forLoopIter = 0;
			//for(int currentIdx = 0;currentIdx<listScheduleLoop.size();currentIdx++){
			//int nbClust = listScheduleLoop.size();
			AbstractClust current = loopBuilder.getLoopClustFirstV2(clust);
			List <AbstractClust> prevs = new ArrayList<AbstractClust>();
			List <FiniteLoopBlock> upperLoops = new ArrayList<FiniteLoopBlock>();
			while(current != null){
				//AbstractClust current = listScheduleLoop.get(currentIdx);
				if(current instanceof ClustVertex){
					SDFAbstractVertex repVertex = ((ClustVertex) current).getVertex();
					p("ClustVertex " + repVertex.getName() + " repetition vector " + getSDFVertexNbRepeated(repVertex));
					//p("Codegen Model Generator " + repVertex.getName());
					ActorPrototypes prototypes = null;
					Object vertex_ref = repVertex.getPropertyBean().getValue(AbstractVertex.REFINEMENT);
					if (vertex_ref instanceof ActorPrototypes)
						prototypes = (ActorPrototypes) vertex_ref;
					if (prototypes != null) {
						String iteratorIndex = new String("iteratorIndex" + Integer.toString(forLoopIter++));
						Prototype loopPrototype = prototypes.getLoopPrototype();
						/* get repetition vector */
						//int vertexRep = getSDFVertexNbRepeated(repVertex);
						int vertexRep = current.getRepeat();
						// p("Actor " + repVertex.getName() + " Repeat " +
						// vertexRep);

						// create code elements and setup them
						FunctionCall repFunc = CodegenFactory.eINSTANCE.createFunctionCall();
						FiniteLoopBlock forLoop = CodegenFactory.eINSTANCE.createFiniteLoopBlock();
						IntVar var = CodegenFactory.eINSTANCE.createIntVar();
						var.setName(iteratorIndex);
						forLoop.setIter(var);
						forLoop.setNbIter(vertexRep);
						operatorBlock.getLoopBlock().getCodeElts().add(forLoop);
						repFunc.setName(loopPrototype.getFunctionName());
						// Function call set to the hierarchical actor
						repFunc.setActorName(dagVertex.getName());

						// retrieve and set variables to be called by the function
						SDFAbstractVertex repVertexCallVar = resultGraph.getVertex(((ClustVertex) current).getVertex().getName());
						Entry<List<Variable>, List<PortDirection>> callVars = generateRepeatedCallVariables(operatorBlock,
								forLoop, upperLoops, dagVertex, repVertexCallVar, loopPrototype, var, inputRepVertexs, outputRepVertexs,
								interfaces);
						// logger.log(Level.INFO, "generateFunctionCall name " + dagVertex.getName());
						// Put Variables in the function call
						for (int idx = 0; idx < callVars.getKey().size(); idx++) {
							repFunc.addParameter(callVars.getKey().get(idx), callVars.getValue().get(idx));
							//p("Called var " + idx + " " + callVars.getKey().get(idx).getName() + " " + callVars.getValue().get(idx).getName());
						}
						// identifyMergedInputRange(callVars); //NOT SUPPORTED YET
						// for (CodeGenArgument arg : loopPrototype.getArguments().keySet()) { p("Arg Buffer "
						// + arg.getName()); } for (CodeGenParameter param : loopPrototype.getParameters().keySet()) {
						// p("Arg Parameter " + param.getName()); }

						// Add the function call to the for loop block
						forLoop.getCodeElts().add(repFunc);

						registerCallVariableToCoreBlock(operatorBlock, repFunc); // for declaration in the file
						// operatorBlock.getLoopBlock().getCodeElts().add(repFunc);
						// Save the functionCall in the dagvertexFunctionCall Map
						dagVertexCalls.put(dagVertex, repFunc);

						if(upperLoops.size() != 0){
							upperLoops.get(upperLoops.size()-1).getCodeElts().add(forLoop);
						}

					} else {
						throw new CodegenException(
								"Actor (" + sdfVertex + ") has no valid refinement (.idl, .h or .graphml)."
										+ " Associate a refinement to this actor before generating code.");
					}
				}else if(current instanceof ClustSequence){
					if(current.getRepeat() != 1){
						String iteratorIndex = new String("clustSeqIteratorIndex" + Integer.toString(forLoopIter++));
						FiniteLoopBlock forLoop = CodegenFactory.eINSTANCE.createFiniteLoopBlock();
						IntVar var = CodegenFactory.eINSTANCE.createIntVar();
						var.setName(iteratorIndex);
						forLoop.setIter(var);
						forLoop.setNbIter(current.getRepeat());
						operatorBlock.getLoopBlock().getCodeElts().add(forLoop);
						if(upperLoops.size() != 0){
							upperLoops.get(upperLoops.size()-1).getCodeElts().add(forLoop);
							
						}
						upperLoops.add(forLoop);
						p("ClustSequence ForLoop " + iteratorIndex + " repetition " + current.getRepeat());
					}
				}
				current = loopBuilder.getLoopClustV2(clust);
				//nbClust--;
			}
			this.linkHSDFEdgeBuffer.clear();
			this.currentWorkingMemOffset = 0;
			p("hierarchial actor dump done ok");
		}
		return 0;
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
	protected void generateActorFiring(CoreBlock operatorBlock, DAGVertex dagVertex) throws CodegenException {
		// Check whether the ActorCall is a call to a hierarchical actor or not.
		SDFVertex sdfVertex = (SDFVertex) dagVertex.getPropertyBean().getValue(DAGVertex.SDF_VERTEX, SDFVertex.class);
		Object refinement = sdfVertex.getPropertyBean().getValue(AbstractVertex.REFINEMENT);

		// If the actor is hierarchical
		if (refinement instanceof AbstractGraph) {
			// try to generate for loop on a hierarchical actor
			p("tryGenerateRepeatActorFiring " + dagVertex.getName());
			try {
				if (tryGenerateRepeatActorFiring(operatorBlock, dagVertex) == 0) {

					p("Hierarchical actor " + dagVertex.getName() + " generation Successed");

				} else {
					p("Hierarchical actor " + dagVertex.getName() + " printing Failed");
					throw new CodegenException("Unflattened hierarchical actors (" + sdfVertex
							+ ") are not yet supported by the Xtend Code Generation.\n"
							+ "Flatten the graph completely before using this code-generation.");
				} 
			} catch (SDF4JException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WorkflowException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			ActorPrototypes prototypes = null;
			// If the actor has an IDL refinement
			if (refinement instanceof CodeRefinement && ((CodeRefinement) refinement).getLanguage() == Language.IDL) {
				// Retrieve the prototypes associated to the actor
				prototypes = getActorPrototypes(sdfVertex);
			}
			// Or if we already extracted prototypes from a .h refinement
			else if (refinement instanceof ActorPrototypes) {
				prototypes = (ActorPrototypes) refinement;
			}

			if (prototypes != null) {
				// Generate the loop functionCall
				{
					Prototype loopPrototype = prototypes.getLoopPrototype();
					if (loopPrototype == null) {
						throw new CodegenException(
								"Actor " + sdfVertex + " has no loop interface in its IDL refinement.");
					}
					FunctionCall functionCall = generateFunctionCall(dagVertex, loopPrototype, false);

					registerCallVariableToCoreBlock(operatorBlock, functionCall);

					// Add the function call to the operatorBlock
					operatorBlock.getLoopBlock().getCodeElts().add(functionCall);

					// Save the functionCall in the dagvertexFunctionCall Map
					dagVertexCalls.put(dagVertex, functionCall);
				}

				// Generate the init FunctionCall (if any)
				{
					Prototype initPrototype = prototypes.getInitPrototype();
					if (initPrototype != null) {
						FunctionCall functionCall = generateFunctionCall(dagVertex, initPrototype, true);

						registerCallVariableToCoreBlock(operatorBlock, functionCall);
						// Add the function call to the operatorBlock
						operatorBlock.getInitBlock().getCodeElts().add(functionCall);
					}

				}
			}
			// If the actor has no refinement
			else {
				throw new CodegenException("Actor (" + sdfVertex + ") has no valid refinement (.idl, .h or .graphml)."
						+ " Associate a refinement to this actor before generating code.");
			}
		}

	}

	/**
	 * Generate the {@link Buffer} definition. This method sets the
	 * {@link Buffer#setCreator(Block) Creator} attributes. Also re-order the
	 * buffer definitions list so that containers are always defined before
	 * content.
	 * 
	 */
	protected void generateBufferDefinitions() {
		for (Entry<String, Buffer> entry : mainBuffers.entrySet()) {

			String memoryBank = entry.getKey();
			Buffer mainBuffer = entry.getValue();

			// Identify the corresponding operator block.
			// (also find out if the Buffer is local (i.e. not shared between
			// several CoreBlock)
			CoreBlock correspondingOperatorBlock = null;
			final boolean isLocal;
			{
				final String correspondingOperatorID;

				if (memoryBank.equals("Shared")) {
					// If the memory bank is shared, let the main operator
					// declare the Buffer.
					correspondingOperatorID = scenario.getSimulationManager().getMainOperatorName();
					isLocal = false;

					// Check that the main operator block exists.
					CoreBlock mainOperatorBlock = null;
					{
						for (Entry<ComponentInstance, CoreBlock> componentEntry : coreBlocks.entrySet()) {
							if (componentEntry.getKey().getInstanceName().equals(correspondingOperatorID)) {
								mainOperatorBlock = componentEntry.getValue();
							}
						}
					}

					// If the main operator does not exist
					if (mainOperatorBlock == null) {
						// Create it
						mainOperatorBlock = CodegenFactory.eINSTANCE.createCoreBlock();
						ComponentInstance componentInstance = archi.getComponentInstance(correspondingOperatorID);
						mainOperatorBlock.setName(componentInstance.getInstanceName());
						mainOperatorBlock.setCoreType(componentInstance.getComponent().getVlnv().getName());
						coreBlocks.put(componentInstance, mainOperatorBlock);
					}

				} else {
					// else, the operator corresponding to the memory bank will
					// do the work
					correspondingOperatorID = memoryBank;
					isLocal = true;
				}

				// Find the block
				for (Entry<ComponentInstance, CoreBlock> componentEntry : coreBlocks.entrySet()) {
					if (componentEntry.getKey().getInstanceName().equals(correspondingOperatorID)) {
						correspondingOperatorBlock = componentEntry.getValue();
					}
				}
			}

			// Recursively set the creator for the current Buffer and all its
			// subBuffer
			recusriveSetBufferCreator(mainBuffer, correspondingOperatorBlock, isLocal);

			ECollections.sort(correspondingOperatorBlock.getDefinitions(), new Comparator<Variable>() {

				@Override
				public int compare(Variable o1, Variable o2) {
					if (o1 instanceof Buffer && o2 instanceof Buffer) {
						int sublevelO1 = 0;
						if (o1 instanceof SubBuffer) {
							Buffer b = (Buffer) o1;
							while (b instanceof SubBuffer) {
								sublevelO1++;
								b = ((SubBuffer) b).getContainer();
							}
						}

						int sublevelO2 = 0;
						if (o2 instanceof SubBuffer) {
							Buffer b = (Buffer) o2;
							while (b instanceof SubBuffer) {
								sublevelO2++;
								b = ((SubBuffer) b).getContainer();
							}
						}

						return sublevelO1 - sublevelO2;
					}
					if (o1 instanceof Buffer) {
						return 1;
					}
					if (o2 instanceof Buffer) {
						return -1;
					}
					return 0;
				}
			});
		}
	}

	/**
	 * This method creates a {@link Buffer} for each {@link DAGEdge} of the
	 * {@link #dag}. It also calls
	 * {@link #generateSubBuffers(Buffer, DAGEdge, Integer)} to create distinct
	 * {@link SubBuffer} corresponding to all the {@link SDFEdge} of the
	 * single-rate {@link SDFGraph} from which the {@link #dag} is derived.<br>
	 * <br>
	 * In this method, the {@link #sharedBuffer}, and the
	 * {@link #dagEdgeBuffers} attributes are filled.
	 * 
	 * @throws CodegenException
	 *             if a {@link DAGEdge} is associated to several
	 *             {@link MemoryExclusionVertex}. (Happens if
	 *             {@link AbstractMemoryAllocatorTask#VALUE_DISTRIBUTION_DISTRIBUTED_ONLY}
	 *             distribution policy is used during memory allocation.)
	 * 
	 */
	protected void generateBuffers() throws CodegenException {
		// Create a main Buffer for each MEG
		for (Entry<String, MemoryExclusionGraph> entry : megs.entrySet()) {

			String memoryBank = entry.getKey();
			MemoryExclusionGraph meg = entry.getValue();

			// Create the Main Shared buffer
			Integer size = (Integer) meg.getPropertyBean().getValue(MemoryExclusionGraph.ALLOCATED_MEMORY_SIZE,
					Integer.class);

			Buffer mainBuffer = CodegenFactory.eINSTANCE.createBuffer();
			mainBuffer.setSize(size);
			mainBuffer.setName(memoryBank);
			mainBuffer.setType("char");
			mainBuffer.setTypeSize(1); // char is 1 byte
			mainBuffers.put(memoryBank, mainBuffer);

			@SuppressWarnings("unchecked")
			Map<DAGEdge, Integer> allocation = (Map<DAGEdge, Integer>) meg.getPropertyBean()
					.getValue(MemoryExclusionGraph.DAG_EDGE_ALLOCATION, (new HashMap<DAGEdge, Integer>()).getClass());

			// generate the subbuffer for each dagedge
			for (Entry<DAGEdge, Integer> dagAlloc : allocation.entrySet()) {
				// If the buffer is not a null buffer
				if (dagAlloc.getValue() != -1) {
					SubBuffer dagEdgeBuffer = CodegenFactory.eINSTANCE.createSubBuffer();

					// Old Naming (too long)
					String comment = dagAlloc.getKey().getSource().getName() + " > "
							+ dagAlloc.getKey().getTarget().getName();
					dagEdgeBuffer.setComment(comment);

					String name = dagAlloc.getKey().getSource().getName() + "__"
							+ dagAlloc.getKey().getTarget().getName();

					name = generateUniqueBufferName(name);
					dagEdgeBuffer.setName(name);
					dagEdgeBuffer.setContainer(mainBuffer);
					dagEdgeBuffer.setOffset(dagAlloc.getValue());
					dagEdgeBuffer.setType("char");
					dagEdgeBuffer.setTypeSize(1);

					// Generate subsubbuffers. Each subsubbuffer corresponds to
					// an
					// edge
					// of the single rate SDF Graph
					Integer dagEdgeSize = generateSubBuffers(dagEdgeBuffer, dagAlloc.getKey(), dagAlloc.getValue());

					// also accessible with dagAlloc.getKey().getWeight();
					dagEdgeBuffer.setSize(dagEdgeSize);

					// Save the DAGEdgeBuffer
					DAGVertex originalSource = dag.getVertex(dagAlloc.getKey().getSource().getName());
					DAGVertex originalTarget = dag.getVertex(dagAlloc.getKey().getTarget().getName());
					DAGEdge originalDagEdge = dag.getEdge(originalSource, originalTarget);
					if (!dagEdgeBuffers.containsKey(originalDagEdge)) {
						dagEdgeBuffers.put(originalDagEdge, dagEdgeBuffer);
					} else {
						throw new CodegenException("\n"
								+ AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DISTRIBUTED_ONLY
								+ " distribution policy during memory allocation not yet supported in code generation.\n"
								+ "DAGEdge " + originalDagEdge
								+ " is already associated to a Buffer and cannot be associated to a second one.");
					}
				} else {
					// the buffer is a null buffer
					NullBuffer dagEdgeBuffer = CodegenFactory.eINSTANCE.createNullBuffer();

					// Old Naming (too long)
					String comment = dagAlloc.getKey().getSource().getName() + " > "
							+ dagAlloc.getKey().getTarget().getName();
					dagEdgeBuffer.setComment("NULL_" + comment);
					dagEdgeBuffer.setContainer(mainBuffer);

					// Generate subsubbuffers. Each subsubbuffer corresponds to
					// an
					// edge
					// of the single rate SDF Graph
					Integer dagEdgeSize = generateSubBuffers(dagEdgeBuffer, dagAlloc.getKey(), dagAlloc.getValue());

					// We set the size to keep the information
					dagEdgeBuffer.setSize(dagEdgeSize);

					// Save the DAGEdgeBuffer
					DAGVertex originalSource = dag.getVertex(dagAlloc.getKey().getSource().getName());
					DAGVertex originalTarget = dag.getVertex(dagAlloc.getKey().getTarget().getName());
					DAGEdge originalDagEdge = dag.getEdge(originalSource, originalTarget);
					dagEdgeBuffers.put(originalDagEdge, dagEdgeBuffer);
				}
			}

			// Generate buffers for each fifo
			@SuppressWarnings("unchecked")
			Map<MemoryExclusionVertex, Integer> fifoAllocation = (Map<MemoryExclusionVertex, Integer>) meg
					.getPropertyBean().getValue(MemoryExclusionGraph.DAG_FIFO_ALLOCATION);
			for (Entry<MemoryExclusionVertex, Integer> fifoAlloc : fifoAllocation.entrySet()) {
				SubBuffer fifoBuffer = CodegenFactory.eINSTANCE.createSubBuffer();

				// Old Naming (too long)
				String comment = fifoAlloc.getKey().getSource() + " > " + fifoAlloc.getKey().getSink();
				fifoBuffer.setComment(comment);

				String name = fifoAlloc.getKey().getSource() + "__" + fifoAlloc.getKey().getSink();
				name = generateUniqueBufferName(name);
				fifoBuffer.setName(name);
				fifoBuffer.setContainer(mainBuffer);
				fifoBuffer.setOffset(fifoAlloc.getValue());
				fifoBuffer.setType("char");
				fifoBuffer.setSize(fifoAlloc.getKey().getWeight());

				// Get Init vertex
				DAGVertex dagEndVertex = dag
						.getVertex(fifoAlloc.getKey().getSource().substring(("FIFO_Head_").length()));
				DAGVertex dagInitVertex = dag.getVertex(fifoAlloc.getKey().getSink());

				Pair<DAGVertex, DAGVertex> key = new Pair<DAGVertex, DAGVertex>(dagEndVertex, dagInitVertex);
				Pair<Buffer, Buffer> value = dagFifoBuffers.get(key);
				if (value == null) {
					value = new Pair<Buffer, Buffer>(null, null);
					dagFifoBuffers.put(key, value);
				}
				if (fifoAlloc.getKey().getSource().startsWith("FIFO_Head_")) {
					dagFifoBuffers.put(key, new Pair<Buffer, Buffer>(fifoBuffer, value.getValue()));
				} else {
					dagFifoBuffers.put(key, new Pair<Buffer, Buffer>(value.getKey(), fifoBuffer));
				}
			}

			// Generate subbuffers for each working mem.
			@SuppressWarnings("unchecked")
			Map<MemoryExclusionVertex, Integer> workingMemoryAllocation = (Map<MemoryExclusionVertex, Integer>) (meg
					.getPropertyBean().getValue(MemoryExclusionGraph.WORKING_MEM_ALLOCATION));
			for (Entry<MemoryExclusionVertex, Integer> e : workingMemoryAllocation.entrySet()) {
				SubBuffer workingMemBuffer = CodegenFactory.eINSTANCE.createSubBuffer();
				MemoryExclusionVertex mObj = e.getKey();
				int weight = mObj.getWeight();
				workingMemBuffer.setContainer(mainBuffer);
				workingMemBuffer.setOffset(e.getValue());
				workingMemBuffer.setSize(weight);
				workingMemBuffer.setName("wMem_" + mObj.getVertex().getName());
				workingMemBuffer.setType("char");
				workingMemBuffer.setTypeSize(1); // char is 1 byte
				this.linkHSDFVertexBuffer.put(dag.getVertex(mObj.getVertex().getName()), workingMemBuffer);
				//p("wMem add working buffer " + workingMemBuffer.getName() + " key " + dag.getVertex(mObj.getVertex().getName()).getName());
			}
		}
	}

	/**
	 * This method generates the list of variable corresponding to a prototype
	 * of the {@link DAGVertex} firing. The {@link Prototype} passed as a
	 * parameter must belong to the processedoutput__input__1 {@link DAGVertex}.
	 * 
	 * @param dagVertex
	 *            the {@link DAGVertex} corresponding to the
	 *            {@link FunctionCall}.
	 * @param prototype
	 *            the prototype whose {@link Variable variables} are retrieved
	 * @param isInit
	 *            Whethet the given prototype is an Init or a loop call. (We do
	 *            not check missing arguments in the IDL for init Calls)
	 * @throws CodegenException
	 *             Exception is thrown if:
	 *             <ul>
	 *             <li>There is a mismatch between the {@link Prototype}
	 *             parameter and and the actor ports</li>
	 *             <li>an actor port is connected to no edge.</li>
	 *             <li>No {@link Buffer} in {@link #srSDFEdgeBuffers}
	 *             corresponds to the edge connected to a port of the
	 *             {@link DAGVertex}</li>
	 *             <li>There is a mismatch between Parameters declared in the
	 *             IDL and in the {@link SDFGraph}</li>
	 *             <li>There is a missing argument in the IDL Loop
	 *             {@link Prototype}</li>
	 *             </ul>
	 */
	protected Entry<List<Variable>, List<PortDirection>> generateCallVariables(DAGVertex dagVertex, Prototype prototype,
			boolean isInit) throws CodegenException {
		// Retrieve the sdf vertex and the refinement.
		SDFVertex sdfVertex = (SDFVertex) dagVertex.getPropertyBean().getValue(DAGVertex.SDF_VERTEX, SDFVertex.class);

		// Sorted list of the variables used by the prototype.
		// The integer is only used to order the variable and is retrieved
		// from the prototype
		TreeMap<Integer, Variable> variableList = new TreeMap<Integer, Variable>();
		TreeMap<Integer, PortDirection> directionList = new TreeMap<Integer, PortDirection>();

		// Retrieve the Variable corresponding to the arguments of the prototype
		// This loop manages only buffers (data buffer and NOT parameters)
		for (CodeGenArgument arg : prototype.getArguments().keySet()) {
			PortDirection dir = null;

			// Check that the Actor has the right ports
			SDFInterfaceVertex port;
			switch (arg.getDirection()) {
			case CodeGenArgument.OUTPUT:
				port = sdfVertex.getSink(arg.getName());
				dir = PortDirection.OUTPUT;
				// logger.log(Level.INFO, "Codegen interface OUTPUT " +
				// port.getName() + " value " + arg.getName());
				break;
			case CodeGenArgument.INPUT:
				port = sdfVertex.getSource(arg.getName());
				dir = PortDirection.INPUT;
				// logger.log(Level.INFO, "Codegen interface INPUT " +
				// port.getName() + " value " + arg.getName() + " " +
				// sdfVertex.getSource(arg.getName()).getName() );
				break;
			default:
				port = null;
			}
			if (port == null) {
				throw new CodegenException("Mismatch between actor (" + sdfVertex
						+ ") ports and IDL loop prototype argument " + arg.getName());
			}

			// Retrieve the Edge corresponding to the current Argument
			DAGEdge dagEdge = null;
			BufferProperties subBufferProperties = null;
			switch (arg.getDirection()) {
			case CodeGenArgument.OUTPUT: {
				Set<DAGEdge> edges = dag.outgoingEdgesOf(dagVertex);
				for (DAGEdge edge : edges) {
					BufferAggregate bufferAggregate = (BufferAggregate) edge.getPropertyBean()
							.getValue(BufferAggregate.propertyBeanName);
					for (BufferProperties buffProperty : bufferAggregate) {
						if (buffProperty.getSourceOutputPortID().equals(arg.getName())
						/*
						 * && buffProperty.getDataType().equals( arg.getType())
						 */) {
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
					BufferAggregate bufferAggregate = (BufferAggregate) edge.getPropertyBean()
							.getValue(BufferAggregate.propertyBeanName);
					for (BufferProperties buffProperty : bufferAggregate) {
						if (buffProperty.getDestInputPortID().equals(arg.getName())
						/*
						 * && buffProperty.getDataType().equals( arg.getType())
						 */) {
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
			/*
			 * logger.log(Level.INFO, "Edge " + dagEdge.getSource().getName() +
			 * " " + dagEdge.getTarget().getName() + " to args " +
			 * subBufferProperties.getDestInputPortID() + " " +
			 * subBufferProperties.getSourceOutputPortID() + " " +
			 * subBufferProperties.getDataType() );
			 */

			if (dagEdge == null || subBufferProperties == null) {
				throw new CodegenException("The DAGEdge connected to the port  " + port + " of Actor (" + dagVertex
						+ ") does not exist.\n" + "Possible cause is that the DAG" + " was altered before entering"
						+ " the Code generation.\n" + "This error may also happen if the port type "
						+ "in the graph and in the IDL are not identical");
			}

			// At this point, the dagEdge, srsdfEdge corresponding to the
			// current argument were identified
			// Get the corresponding Variable
			Variable var = this.srSDFEdgeBuffers.get(subBufferProperties);
			if (var == null) {
				throw new CodegenException("Edge connected to " + arg.getDirection() + " port " + arg.getName()
						+ " of DAG Actor " + dagVertex + " is not present in the input MemEx.\n"
						+ "There is something wrong in the Memory Allocation task.");
			}

			variableList.put(prototype.getArguments().get(arg), var);
			directionList.put(prototype.getArguments().get(arg), dir);
			// logger.log(Level.INFO, "Get corresponding variable " +
			// prototype.getFunctionName() + " nbargs " + prototype.getNbArgs()
			// + " args " +
			// prototype.getArguments().get(arg) + " " +
			// prototype.getArguments().get(arg).toString() + " " +
			// var.getName());
		}

		// Check that all incoming DAGEdge exist in the function call
		if (!isInit) {
			for (IInterface port : sdfVertex.getInterfaces()) {
				boolean found = false;
				for (CodeGenArgument arguments : prototype.getArguments().keySet()) {
					if (port.getName().equals(arguments.getName())) {
						found = true;
						break;
					}
				}
				if (found == false) {
					throw new CodegenException("SDF port \"" + port.getName() + "\" of actor \"" + sdfVertex
							+ "\" has no corresponding parameter in the associated IDL.");
				}
			}
		}

		// Retrieve the Variables corresponding to the Parameters of the
		// prototype
		// This loop manages only parameters (parameters and NOT buffers)
		for (CodeGenParameter param : prototype.getParameters().keySet()) {
			// Check that the actor has the right parameter
			Argument actorParam = sdfVertex.getArgument(param.getName());

			if (actorParam == null) {
				throw new CodegenException("Actor " + sdfVertex + " has no match for parameter " + param.getName()
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
			directionList.put(prototype.getParameters().get(param), PortDirection.NONE);
			// logger.log(Level.INFO, "Variable to Param " +
			// prototype.getParameters().get(param) + " " + param.getName() + "
			// " + constant.getName());

			// // Retrieve the variable from its context (i.e. from its original
			// // (sub)graph)
			// org.ietr.dftools.algorithm.model.parameters.Variable originalVar
			// =
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

		return new AbstractMap.SimpleEntry<List<Variable>, List<PortDirection>>(
				new ArrayList<Variable>(variableList.values()), new ArrayList<PortDirection>(directionList.values()));
	}

	/**
	 * This method generates the list of variable corresponding to a prototype
	 * of the {@link DAGVertex} firing. The {@link Prototype} passed as a
	 * parameter must belong to the processedoutput__input__1 {@link DAGVertex}.
	 * 
	 * @param dagVertex
	 *            the {@link DAGVertex} corresponding to the
	 *            {@link FunctionCall}.
	 * @param prototype
	 *            the prototype whose {@link Variable variables} are retrieved
	 * @param isInit
	 *            Whethet the given prototype is an Init or a loop call. (We do
	 *            not check missing arguments in the IDL for init Calls)
	 * @throws CodegenException
	 *             Exception is thrown if:
	 *             <ul>
	 *             <li>There is a mismatch between the {@link Prototype}
	 *             parameter and and the actor ports</li>
	 *             <li>an actor port is connected to no edge.</li>
	 *             <li>No {@link Buffer} in {@link #srSDFEdgeBuffers}
	 *             corresponds to the edge connected to a port of the
	 *             {@link DAGVertex}</li>
	 *             <li>There is a mismatch between Parameters declared in the
	 *             IDL and in the {@link SDFGraph}</li>
	 *             <li>There is a missing argument in the IDL Loop
	 *             {@link Prototype}</li>
	 *             </ul>
	 */
	protected Entry<List<Variable>, List<PortDirection>> generateRepeatedCallVariables(CoreBlock operatorBlock,
			FiniteLoopBlock loopBlock, List <FiniteLoopBlock> upperLoops, DAGVertex dagVertex, SDFAbstractVertex sdfAbsVertex, Prototype prototype,
			IntVar iterVar, List<SDFAbstractVertex> inputRepVertexs, List<SDFAbstractVertex> outputRepVertexs,
			List<SDFInterfaceVertex> interfaces) throws CodegenException {
		// Retrieve the sdf vertex and the refinement.
		/*
		 * SDFVertex sdfVertex = (SDFVertex)
		 * dagVertex.getPropertyBean().getValue( DAGVertex.SDF_VERTEX,
		 * SDFVertex.class);
		 */
		SDFVertex sdfVertex = (SDFVertex) sdfAbsVertex;
		// p("generateRepeatedCallVariables sdfAbsVertex " +
		// sdfAbsVertex.getName() + " function name " +
		// prototype.getFunctionName() + " dagVertex " + dagVertex.getName());
		// Sorted list of the variables used by the prototype.
		// The integer is only used to order the variable and is retrieved
		// from the prototype
		TreeMap<Integer, Variable> variableList = new TreeMap<Integer, Variable>();
		TreeMap<Integer, PortDirection> directionList = new TreeMap<Integer, PortDirection>();

		boolean isInputActor = false;
		boolean isOutputActor = false;
		for (SDFAbstractVertex v : inputRepVertexs) {
			if (v == sdfVertex) {
				isInputActor = true;
				// p("Actor " + sdfVertex.getName() + " is an input vertex of
				// hirarchical actor " + dagVertex.getName());
			}
		}
		for (SDFAbstractVertex v : outputRepVertexs) {
			if (v == sdfVertex) {
				isOutputActor = true;
				// p("Actor " + sdfVertex.getName() + " is an output vertex of
				// hirarchical actor " + dagVertex.getName());
			}
		}

		// Retrieve the Variable corresponding to the arguments of the prototype
		// This loop manages only buffers (data buffer and NOT parameters)
		for (CodeGenArgument arg : prototype.getArguments().keySet()) {
			IntVar currentIterVar = CodegenFactory.eINSTANCE.createIntVar();
			currentIterVar.setName(iterVar.getName());

			PortDirection dir = null;
			boolean isInputActorTmp = isInputActor;
			boolean isOutputActorTmp = isOutputActor;

			// Check that the Actor has the right ports
			SDFInterfaceVertex port;
			switch (arg.getDirection())
			{
				case CodeGenArgument.OUTPUT:
				{
					port = sdfVertex.getSink(arg.getName());
					dir = PortDirection.OUTPUT;
					if (isInputActorTmp == true) {
						if ((sdfVertex.getAssociatedEdge(port).getTarget() instanceof SDFInterfaceVertex) == false) // check target not an interface of hierarchical actor
						{
							isInputActorTmp = false;
						}
					}
					// p("Codegen interface OUTPUT " + port.getName() + " value " +
					// arg.getName());
					break;
				}
				case CodeGenArgument.INPUT:
				{
					port = sdfVertex.getSource(arg.getName());
					dir = PortDirection.INPUT;
					if (isOutputActorTmp == true) {
						if ((sdfVertex.getAssociatedEdge(port).getSource() instanceof SDFInterfaceVertex) == false) // check target not an interface of hierarchical actor
						{
							isOutputActorTmp = false;
						}
					}
					// p("Codegen interface INPUT " + port.getName() + " value " +
					// arg.getName() + " " +
					// sdfVertex.getSource(arg.getName()).getName() );
					break;
				}
				default:
				{
					port = null;
				}
			}
			if (port == null) {
				throw new CodegenException("Mismatch between actor (" + sdfVertex
						+ ") ports and IDL loop prototype argument " + arg.getName());
			}

			// Retrieve the Edge corresponding to the current Argument
			// This is only done because of the scheduler that is merging
			// consecutive buffers of an actor
			DAGEdge dagEdge = null;
			BufferProperties subBufferProperties = null;
			if (isInputActorTmp == true || isOutputActorTmp == true) {
				switch (arg.getDirection()) {
					case CodeGenArgument.OUTPUT: {
						Set<DAGEdge> edges = dag.outgoingEdgesOf(dagVertex);
						for (DAGEdge edge : edges) {
							BufferAggregate bufferAggregate = (BufferAggregate) edge.getPropertyBean()
									.getValue(BufferAggregate.propertyBeanName);
							for (BufferProperties buffProperty : bufferAggregate) {
								if (buffProperty.getSourceOutputPortID().equals(arg.getName())
								/*
								 * && buffProperty.getDataType().equals(
								 * arg.getType())
								 */) {
									// check that this edge is not connected to a
									// receive vertex
									if (edge.getTarget().getKind() != null) {
										dagEdge = edge;
										subBufferProperties = buffProperty;
									}
								}
							}
						}
						break;
					}
					case CodeGenArgument.INPUT: {
						Set<DAGEdge> edges = dag.incomingEdgesOf(dagVertex);
						for (DAGEdge edge : edges) {
							BufferAggregate bufferAggregate = (BufferAggregate) edge.getPropertyBean()
									.getValue(BufferAggregate.propertyBeanName);
							for (BufferProperties buffProperty : bufferAggregate) {
								if (buffProperty.getDestInputPortID().equals(arg.getName())
								/*
								 * && buffProperty.getDataType().equals(
								 * arg.getType())
								 */) {
									// check that this edge is not connected to a
									// send
									// vertex
									if (edge.getSource().getKind() != null) {
										dagEdge = edge;
										subBufferProperties = buffProperty;
									}
								}
							}
						}
						break;
					}
				}
				/*
				 * logger.log(Level.INFO, "Edge " +
				 * dagEdge.getSource().getName() + " " +
				 * dagEdge.getTarget().getName() + " to args " +
				 * subBufferProperties.getDestInputPortID() + " " +
				 * subBufferProperties.getSourceOutputPortID() + " " +
				 * subBufferProperties.getDataType() );
				 */

				if (dagEdge == null || subBufferProperties == null) {
					throw new CodegenException("The DAGEdge connected to the port  " + port + " of Actor (" + dagVertex
							+ ") does not exist.\n" + "Possible cause is that the DAG" + " was altered before entering"
							+ " the Code generation.\n" + "This error may also happen if the port type "
							+ "in the graph and in the IDL are not identical");
				}
			}

			// At this point, the dagEdge, srsdfEdge corresponding to the
			// current argument were identified
			// Get the corresponding Variable
			// p("dagEdge subBufferProperties " + subBufferProperties.getSize()
			// + " " + subBufferProperties.getDataType());

			Variable var = null;
			SDFEdge currentEdge = sdfVertex.getAssociatedEdge(port);
			int bufIterSize = 0;
			int bufSize = 0;

			int rep = 1;
			try {
				rep = sdfVertex.getNbRepeatAsInteger();
			} catch (InvalidExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (isInputActorTmp == true || isOutputActorTmp == true) {
				var = this.srSDFEdgeBuffers.get(subBufferProperties);
				bufIterSize = subBufferProperties.getSize() / rep;
				bufSize = subBufferProperties.getSize();
			} else {
				try {
					if (arg.getDirection() == CodeGenArgument.INPUT) {
						bufIterSize = currentEdge.getCons().intValue();// / rep;
						bufSize = currentEdge.getCons().intValue() * rep;
					} else {
						bufIterSize = currentEdge.getProd().intValue();// / rep;
						bufSize = currentEdge.getProd().intValue() * rep;
					}
				} catch (InvalidExpressionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				SubBuffer workingMemBuf = (SubBuffer) this.linkHSDFVertexBuffer.get(dagVertex);
				SubBuffer buf = (SubBuffer)this.linkHSDFEdgeBuffer.get(currentEdge);
				//p("Tried get linkHSDFVertexBuffer key " + dagVertex.getName() + " working buf " + workingMemBuf.getName());
				if (buf == null) {
					buf = CodegenFactory.eINSTANCE.createSubBuffer();
					//p("linkHSDFEdgeBuffer buffer Name " + e.getValue().getName() + " "  + e.getKey() + " coretype " + operatorBlock.getCoreType() + " corename " + operatorBlock.getName());
					//currentIterVar.setName(upperLoopOffsets + " + " + currentIterVar.getName());
					buf.setName(workingMemBuf.getName() + "_" + Integer.toString(this.currentWorkingMemOffset));
					buf.setContainer(workingMemBuf);
					buf.setOffset(this.currentWorkingMemOffset);
					buf.setSize(bufSize);
					buf.setType("char");
					buf.setTypeSize(1);
					this.currentWorkingMemOffset += bufSize;
					//p("Internal working buffer " + buf.getName());
					this.linkHSDFEdgeBuffer.put(currentEdge, buf);
				}
				var = buf;
			}

			BufferIterator bufIter = CodegenFactory.eINSTANCE.createBufferIterator();
			if (var == null) {
				throw new CodegenException("Edge connected to " + arg.getDirection() + " port " + arg.getName()
						+ " of DAG Actor " + dagVertex + " is not present in the input MemEx.\n"
						+ "There is something wrong in the Memory Allocation task.");
			}

			String upperLoopOffsets = new String();
			if(upperLoops.size() != 0){
				upperLoopOffsets = "(" + Integer.toString(loopBlock.getNbIter()) + "*" + Integer.toString(bufIterSize) + ") * ( " + upperLoops.get(upperLoops.size()-1).getIter().getName();
				for(int i = 0; i<upperLoops.size()-1;i++){
					upperLoopOffsets += " + (" + upperLoops.get(upperLoops.size()-2-i).getIter().getName() + "*" + Integer.toString(upperLoops.get(upperLoops.size()-1-i).getNbIter()) + ")";
				}
				upperLoopOffsets += " )";
				currentIterVar.setName(upperLoopOffsets + " + " + currentIterVar.getName());
			}

			bufIter.setName(var.getName());
			bufIter.setContainer(((SubBuffer) var).getContainer());
			bufIter.setIter(currentIterVar);
			bufIter.setTypeSize(((SubBuffer) var).getTypeSize());
			bufIter.setType(((SubBuffer) var).getType());
			bufIter.setOffset(((SubBuffer) var).getOffset());
			bufIter.setIterSize(bufIterSize);
			bufIter.setSize(bufSize);

			if (arg.getDirection() == CodeGenArgument.INPUT) {
				loopBlock.getInBuffers().add(bufIter);
			} else if (arg.getDirection() == CodeGenArgument.OUTPUT) {
				loopBlock.getOutBuffers().add(bufIter);
			} else {
				throw new CodegenException("Args INPUT / OUTPUT failed\n");
			}

			/* register to call block */
			if (var instanceof Constant) {
				var.setCreator(operatorBlock);
			}
			var.getUsers().add(operatorBlock);
			// var.setCreator(operatorBlock);
			//registerCallVariableToCoreBlock(operatorBlock, var);
			/*
			 * if(var.getCreator() == null) { throw new
			 * CodegenException("GenerateRepeatedCallVariable " + var.getName()
			 * + " getCreator is null boooo"); }
			 */

			variableList.put(prototype.getArguments().get(arg), bufIter);
			directionList.put(prototype.getArguments().get(arg), dir);
			// logger.log(Level.INFO, "Get corresponding variable " +
			// prototype.getFunctionName() + " nbargs " + prototype.getNbArgs()
			// + " args " +
			// prototype.getArguments().get(arg) + " " +
			// prototype.getArguments().get(arg).toString() + " " +
			// var.getName());
		}

		// Retrieve the Variables corresponding to the Parameters of the
		// prototype
		// This loop manages only parameters (parameters and NOT buffers)
		for (CodeGenParameter param : prototype.getParameters().keySet()) {
			// Check that the actor has the right parameter
			Argument actorParam = sdfVertex.getArgument(param.getName());

			if (actorParam == null) {
				throw new CodegenException("Actor " + sdfVertex + " has no match for parameter " + param.getName()
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
			directionList.put(prototype.getParameters().get(param), PortDirection.NONE);
			// p("Variable to Param " + prototype.getParameters().get(param) + "
			// " + param.getName() + " " + constant.getName());

			// // Retrieve the variable from its context (i.e. from its original
			// // (sub)graph)
			// org.ietr.dftools.algorithm.model.parameters.Variable originalVar
			// =
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
			directionList.put(prototype.getParameters().get(param), PortDirection.NONE);
		}
		return new AbstractMap.SimpleEntry<List<Variable>, List<PortDirection>>(
				new ArrayList<Variable>(variableList.values()), new ArrayList<PortDirection>(directionList.values()));
	}

	/**
	 * Generate the {@link CodegenPackage Codegen Model} for communication
	 * "firing". This method will create an {@link Communication} and place it
	 * in the {@link LoopBlock} of the {@link CoreBlock} passed as a parameter.
	 * 
	 * @param operatorBlock
	 *            the {@link CoreBlock} where the actor {@link Communication} is
	 *            performed.
	 * @param dagVertex
	 *            the {@link DAGVertex} corresponding to the actor firing.
	 * @param direction
	 *            the Type of communication ({@link VertexType#TYPE_SEND} or
	 *            {@link VertexType#TYPE_RECEIVE}).
	 * @throws CodegenException
	 *             Exception is thrown if:
	 *             <ul>
	 *             </ul>
	 * 
	 */
	protected void generateCommunication(CoreBlock operatorBlock, DAGVertex dagVertex, String direction)
			throws CodegenException {
		// Create the communication
		SharedMemoryCommunication newComm = CodegenFactory.eINSTANCE.createSharedMemoryCommunication();
		Direction dir = (direction.equals(VertexType.TYPE_SEND)) ? Direction.SEND : Direction.RECEIVE;
		Delimiter delimiter = (direction.equals(VertexType.TYPE_SEND)) ? Delimiter.START : Delimiter.END;
		newComm.setDirection(dir);
		newComm.setDelimiter(delimiter);
		MessageRouteStep routeStep = (MessageRouteStep) dagVertex.getPropertyBean()
				.getValue(ImplementationPropertyNames.SendReceive_routeStep, MessageRouteStep.class);
		for (ComponentInstance comp : routeStep.getNodes()) {
			CommunicationNode comNode = CodegenFactory.eINSTANCE.createCommunicationNode();
			comNode.setName(comp.getInstanceName());
			comNode.setType(comp.getComponent().getVlnv().getName());
			newComm.getNodes().add(comNode);
		}

		// Find the corresponding DAGEdge buffer(s)
		DAGEdge dagEdge = (DAGEdge) dagVertex.getPropertyBean()
				.getValue(ImplementationPropertyNames.SendReceive_correspondingDagEdge, DAGEdge.class);
		Buffer buffer = dagEdgeBuffers.get(dagEdge);
		if (buffer == null) {
			throw new CodegenException("No buffer found for edge" + dagEdge);
		}
		newComm.setData(buffer);

		// Set the name of the communication
		// SS <=> Start Send
		// RE <=> Receive End
		String commName = "__" + buffer.getName();
		commName += "__" + operatorBlock.getName();
		newComm.setName(((newComm.getDirection().equals(Direction.SEND)) ? "SS" : "RE") + commName);

		// Find corresponding communications (SS/SE/RS/RE)
		registerCommunication(newComm, dagEdge, dagVertex);

		// Insert the new communication to the loop of the codeblock
		insertCommunication(operatorBlock, dagVertex, newComm);

		// Register the dag buffer to the core
		registerCallVariableToCoreBlock(operatorBlock, newComm);

		// Set the semaphore for the new Comm. (this may be a share memory comm
		// specific feature)
		// probably some work to do here when trying to support new
		// communication means.
		generateSemaphore(operatorBlock, newComm);

		// Create the corresponding SE or RS
		SharedMemoryCommunication newCommZoneComplement = CodegenFactory.eINSTANCE.createSharedMemoryCommunication();
		newCommZoneComplement.setDirection(dir);
		newCommZoneComplement.setDelimiter((delimiter.equals(Delimiter.START)) ? Delimiter.END : Delimiter.START);
		newCommZoneComplement.setData(buffer);
		newCommZoneComplement.setName(((newComm.getDirection().equals(Direction.SEND)) ? "SE" : "RS") + commName);
		for (ComponentInstance comp : routeStep.getNodes()) {
			CommunicationNode comNode = CodegenFactory.eINSTANCE.createCommunicationNode();
			comNode.setName(comp.getInstanceName());
			comNode.setType(comp.getComponent().getVlnv().getName());
			newCommZoneComplement.getNodes().add(comNode);
		}

		// Find corresponding communications (SS/SE/RS/RE)
		registerCommunication(newCommZoneComplement, dagEdge, dagVertex);

		// Insert the new communication to the loop of the codeblock
		insertCommunication(operatorBlock, dagVertex, newCommZoneComplement);

		// No semaphore here, semaphore are only for SS->RE and RE->SR
	}

	/**
	 * Generate the {@link FifoCall} that corresponds to the {@link DAGVertex}
	 * passed as a parameter and add it to the {@link CoreBlock#getLoopBlock()
	 * loop block} of the given {@link CoreBlock}. Also generate the
	 * corresponding init call and add it to the
	 * {@link CoreBlock#getInitBlock()}.
	 * 
	 * @param operatorBlock
	 *            the {@link CoreBlock} that executes the {@link DAGVertex}
	 * @param dagVertex
	 *            A {@link DAGInitVertex} or a {@link DAGEndVertex} that
	 *            respectively correspond to a pull and a push operation.
	 * @throws CodegenException
	 *             if the passed vertex is not a {@link DAGInitVertex} nor a
	 *             {@link DAGEndVertex}
	 */
	protected void generateFifoCall(CoreBlock operatorBlock, DAGVertex dagVertex) throws CodegenException {
		// Retrieve the sdf vertex
		SDFAbstractVertex sdfVertex = (SDFAbstractVertex) dagVertex.getPropertyBean().getValue(DAGVertex.SDF_VERTEX,
				SDFAbstractVertex.class);

		// Create the Fifo call and set basic property
		FifoCall fifoCall = CodegenFactory.eINSTANCE.createFifoCall();
		fifoCall.setName(dagVertex.getName());

		// Find the type of FiFo operation
		String kind = dagVertex.getPropertyStringValue(AbstractVertex.KIND);
		switch (kind) {
		case DAGInitVertex.DAG_INIT_VERTEX:
			fifoCall.setOperation(FifoOperation.POP);
			break;
		case DAGEndVertex.DAG_END_VERTEX:
			fifoCall.setOperation(FifoOperation.PUSH);
			break;
		default:
			throw new CodegenException("DAGVertex " + dagVertex + " does not corresponds to a Fifo primitive.");
		}

		// Get buffer used by the FifoCall (in/out)
		Set<DAGEdge> edges;
		PortDirection dir;
		if (fifoCall.getOperation().equals(FifoOperation.POP)) {
			edges = dag.outgoingEdgesOf(dagVertex);
			dir = PortDirection.OUTPUT;
		} else {
			edges = dag.incomingEdgesOf(dagVertex);
			dir = PortDirection.INPUT;
		}
		// There might be more than one edge, if one is connected to a
		// send/receive
		Buffer buffer = null;
		{
			DAGEdge edge = null;
			for (DAGEdge currentEdge : edges) {
				if (currentEdge.getSource().getPropertyBean()
						.getValue(ImplementationPropertyNames.Vertex_vertexType, VertexType.class)
						.equals(VertexType.TASK)
						&& currentEdge.getTarget().getPropertyBean()
								.getValue(ImplementationPropertyNames.Vertex_vertexType, VertexType.class)
								.equals(VertexType.TASK)) {
					edge = currentEdge;
				}
			}
			if (edge == null) {
				throw new CodegenException(
						"DAGVertex " + dagVertex + " is not connected to any " + VertexType.TYPE_TASK + " vertex.");
			}

			BufferAggregate aggregate = (BufferAggregate) edge.getPropertyBean()
					.getValue(BufferAggregate.propertyBeanName, BufferAggregate.class);
			BufferProperties bufferProperty = aggregate.get(0);
			buffer = srSDFEdgeBuffers.get(bufferProperty);
			if (buffer == null) {
				throw new CodegenException("DAGEdge " + edge + " was not allocated in memory.");
			}
			fifoCall.addParameter(buffer, dir);
		}

		// Retrieve the internal buffer
		DAGVertex dagEndVertex;
		DAGVertex dagInitVertex;
		if (fifoCall.getOperation().equals(FifoOperation.POP)) {
			dagInitVertex = dagVertex;
			String endVertexName = ((SDFInitVertex) sdfVertex).getEndReference().getName();
			dagEndVertex = dag.getVertex(endVertexName);

			// COmmented below : creation of the buffer "online"
			/*
			 * // Pop operation is always the first encountered in scheduling //
			 * order. // Get the depth of the fifo, and create the storage
			 * buffer Buffer storageBuffer =
			 * CodegenFactory.eINSTANCE.createBuffer(); String comment =
			 * "fifo: " + sdfVertex.getName() + " > " + ((SDFInitVertex)
			 * sdfVertex).getEndReference().getName();
			 * storageBuffer.setComment(comment); String name = "fifo__" +
			 * sdfVertex.getName() + "__" + ((SDFInitVertex)
			 * sdfVertex).getEndReference().getName(); name =
			 * generateUniqueBufferName(name); storageBuffer.setName(name);
			 * storageBuffer.setCreator(operatorBlock);
			 * storageBuffer.getUsers().add(operatorBlock); Integer size =
			 * ((SDFInitVertex) sdfVertex).getInitSize();
			 * storageBuffer.setSize(size);
			 * storageBuffer.setType(buffer.getType());
			 * 
			 * fifoCall.setHeadBuffer(storageBuffer);
			 */
		} else {
			dagEndVertex = dagVertex;
			String initVertexName = ((SDFEndVertex) sdfVertex).getEndReference().getName();
			dagInitVertex = dag.getVertex(initVertexName);
		}
		Pair<Buffer, Buffer> buffers = dagFifoBuffers.get(new Pair<DAGVertex, DAGVertex>(dagEndVertex, dagInitVertex));
		if (buffers == null || buffers.getKey() == null) {
			throw new CodegenException("No buffer was allocated for the the following pair of end/init vertices: "
					+ dagEndVertex.getName() + " " + dagInitVertex.getName());
		}
		fifoCall.setHeadBuffer(buffers.getKey());
		fifoCall.setBodyBuffer(buffers.getValue());
		if (fifoCall.getOperation().equals(FifoOperation.POP)) {
			buffers.getKey().setCreator(operatorBlock);
			if (buffers.getValue() != null) {
				buffers.getValue().setCreator(operatorBlock);
			}
		}

		buffers.getKey().getUsers().add(operatorBlock);
		if (buffers.getValue() != null) {
			buffers.getValue().getUsers().add(operatorBlock);
		}

		// Register associated fifo calls (push/pop)
		if (fifoCall.getOperation().equals(FifoOperation.POP)) {
			// Pop operations are the first to be encountered.
			// We simply store the dagVertex with its associated fifoCall in a
			// Map. This Map will be used when processing the associated Push
			// operation
			popFifoCalls.put((SDFInitVertex) sdfVertex, fifoCall);

		} else { // Push case
			// Retrieve the corresponding Pop
			FifoCall popCall = popFifoCalls.remove(((SDFEndVertex) sdfVertex).getEndReference());
			popCall.setFifoHead(fifoCall);
			fifoCall.setFifoTail(popCall);
		}

		// Add the Fifo call to the loop of its coreBlock
		operatorBlock.getLoopBlock().getCodeElts().add(fifoCall);
		dagVertexCalls.put(dagVertex, fifoCall);
		buffer.getUsers().add(operatorBlock);

		// Create the init call (only the first time te fifo is encountered)
		if (fifoCall.getOperation().equals(FifoOperation.POP)) {
			FifoCall fifoInitCall = CodegenFactory.eINSTANCE.createFifoCall();
			fifoInitCall.setOperation(FifoOperation.INIT);
			fifoInitCall.setFifoHead(fifoCall);
			fifoInitCall.setName(fifoCall.getName());
			fifoInitCall.setHeadBuffer(fifoCall.getHeadBuffer());
			fifoInitCall.setBodyBuffer(fifoCall.getBodyBuffer());
			operatorBlock.getInitBlock().getCodeElts().add(fifoInitCall);
		}
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
	 * @param isInit
	 *            Indicicate whether this function call corresponds to an
	 *            initialization call (in such case,
	 *            {@link #generateCallVariables(DAGVertex, Prototype, boolean)}
	 *            does not need to check for missing parameter in the prototype.
	 * @return The {@link FunctionCall} corresponding to the {@link DAGVertex
	 *         actor} firing.
	 * @throws CodegenException
	 * 
	 * 
	 */
	protected FunctionCall generateFunctionCall(DAGVertex dagVertex, Prototype prototype, boolean isInit)
			throws CodegenException {
		// Create the corresponding FunctionCall
		FunctionCall func = CodegenFactory.eINSTANCE.createFunctionCall();
		func.setName(prototype.getFunctionName());
		func.setActorName(dagVertex.getName());

		// SDFVertex sdfVertex = (SDFVertex)
		// dagVertex.getPropertyBean().getValue(DAGVertex.SDF_VERTEX,
		// SDFVertex.class);

		// Retrieve the Arguments that must correspond to the incoming data
		// fifos
		Entry<List<Variable>, List<PortDirection>> callVars = generateCallVariables(dagVertex, prototype, isInit);
		// logger.log(Level.INFO, "generateFunctionCall name " +
		// dagVertex.getName());
		// Put Variables in the function call
		for (int idx = 0; idx < callVars.getKey().size(); idx++) {
			func.addParameter(callVars.getKey().get(idx), callVars.getValue().get(idx));
			// logger.log(Level.INFO, "generateFunctionCall " + idx + " " +
			// callVars.getKey().get(idx).getName() + " " +
			// callVars.getValue().get(idx).getName() );
		}

		identifyMergedInputRange(callVars);

		return func;
	}

	/**
	 * Generate the semaphore associated to the given
	 * {@link SharedMemoryCommunication}.
	 * 
	 * @param operatorBlock
	 *            the {@link CoreBlock} on which the
	 *            {@link SharedMemoryCommunication} is executed
	 * @param newComm
	 *            the {@link SharedMemoryCommunication}
	 */
	protected void generateSemaphore(CoreBlock operatorBlock, SharedMemoryCommunication newComm) {
		boolean ss_re = ((newComm.getDirection().equals(Direction.SEND)
				&& newComm.getDelimiter().equals(Delimiter.START))
				|| (newComm.getDirection().equals(Direction.RECEIVE) && newComm.getDelimiter().equals(Delimiter.END)));

		// For SS->RE

		// First check if a semaphore was already created for corresponding
		// calls.
		Set<Communication> correspondingComm = new HashSet<Communication>();
		if (ss_re) {
			correspondingComm.add(newComm.getReceiveEnd());
			correspondingComm.add(newComm.getSendStart());
		}

		Semaphore semaphore = null;

		for (Communication comm : correspondingComm) {
			if (comm instanceof SharedMemoryCommunication) {
				semaphore = ((SharedMemoryCommunication) comm).getSemaphore();
			}
			if (semaphore != null) {
				break;
			}
		}

		// If no semaphore was found, create one
		if (semaphore == null) {
			semaphore = CodegenFactory.eINSTANCE.createSemaphore();
			semaphore.setCreator(operatorBlock);
			semaphore.setName("sem_" + newComm.getId() + "_" + ((ss_re) ? "SSRE" : "RRSR"));
			FunctionCall initSem = CodegenFactory.eINSTANCE.createFunctionCall();
			initSem.addParameter(semaphore, PortDirection.NONE);

			Constant cstShared = CodegenFactory.eINSTANCE.createConstant();
			cstShared.setType("int");
			cstShared.setValue(0);
			initSem.addParameter(cstShared, PortDirection.NONE);
			cstShared.setCreator(operatorBlock);

			Constant cstInitVal = CodegenFactory.eINSTANCE.createConstant();
			cstInitVal.setType("int");
			if (ss_re) {
				cstInitVal.setValue(0);
			}

			cstInitVal.setName("init_val");
			initSem.addParameter(cstInitVal, PortDirection.NONE);
			cstInitVal.setCreator(operatorBlock);

			initSem.setName("sem_init");
			initSem.setActorName(newComm.getData().getComment());

			operatorBlock.getInitBlock().getCodeElts().add(initSem);
		}

		// Put the semaphore in the com
		newComm.setSemaphore(semaphore);

		// Register the core of the current block as a semaphore user
		semaphore.getUsers().add(operatorBlock);

	}

	/**
	 * Generate the {@link CodegenPackage Codegen Model} for a "special actor"
	 * (fork, join, broadcast or roundbuffer) firing. This method will create an
	 * {@link SpecialCall} and place it in the {@link LoopBlock} of the
	 * {@link CoreBlock} passed as a parameter.
	 * 
	 * @param operatorBlock
	 *            the {@link CoreBlock} where the special actor firing is
	 *            performed.
	 * @param dagVertex
	 *            the {@link DAGVertex} corresponding to the actor firing.
	 * @throws CodegenException
	 * 
	 */
	protected void generateSpecialCall(CoreBlock operatorBlock, DAGVertex dagVertex) throws CodegenException {
		// get the corresponding SDFVertex
		SDFAbstractVertex sdfVertex = (SDFAbstractVertex) dagVertex.getPropertyBean().getValue(DAGVertex.SDF_VERTEX,
				SDFAbstractVertex.class);

		SpecialCall f = CodegenFactory.eINSTANCE.createSpecialCall();
		f.setName(dagVertex.getName());
		String vertexType = dagVertex.getPropertyStringValue(AbstractVertex.KIND);
		switch (vertexType) {
		case DAGForkVertex.DAG_FORK_VERTEX:
			f.setType(SpecialType.FORK);
			break;
		case DAGJoinVertex.DAG_JOIN_VERTEX:
			f.setType(SpecialType.JOIN);
			break;
		case DAGBroadcastVertex.DAG_BROADCAST_VERTEX:
			if (sdfVertex instanceof SDFRoundBufferVertex) {
				f.setType(SpecialType.ROUND_BUFFER);
				break;
			} else if (sdfVertex instanceof SDFBroadcastVertex) {
				f.setType(SpecialType.BROADCAST);
				break;
			}
			// Do not break here !
		default:
			throw new CodegenException("DAGVertex " + dagVertex + " has an unknown type: " + vertexType);
		}

		// Retrieve input/output edge in correct order !
		@SuppressWarnings("unchecked")
		Map<Integer, SDFEdge> orderedEdges = (Map<Integer, SDFEdge>) sdfVertex.getPropertyBean()
				.getValue(DAGForkVertex.EDGES_ORDER);
		SDFGraph srSDFGraph = (SDFGraph) sdfVertex.getPropertyBean().getValue("base");

		for (int i = 0; i < orderedEdges.size(); i++) {
			// Find the corresponding DAGEdge.
			DAGEdge dagEdge = null;
			{
				DAGVertex source = null;
				DAGVertex target = null;

				// Get the target or the source of the currentEdge
				SDFAbstractVertex sourceOrTargetVertex = null;
				if (f.getType().equals(SpecialType.FORK) || f.getType().equals(SpecialType.BROADCAST)) {
					sourceOrTargetVertex = srSDFGraph.getEdgeTarget(orderedEdges.get(i));
					source = dagVertex;
					target = dag.getVertex(sourceOrTargetVertex.getName());
				} else { // join or roundbuffer
					sourceOrTargetVertex = srSDFGraph.getEdgeSource(orderedEdges.get(i));
					target = dagVertex;
					source = dag.getVertex(sourceOrTargetVertex.getName());
				}
				// For broadcast and round
				// buffersf.getType().equals(SpecialType.BROADCAST) vertices,
				// respectively skip the input and the outputs
				if ((f.getType().equals(SpecialType.BROADCAST) || f.getType().equals(SpecialType.ROUND_BUFFER))
						&& target != null && target.equals(source)) {
					continue;
				}

				dagEdge = dag.getEdge(source, target);
			}

			if (dagEdge == null) {
				throw new CodegenException(
						"DAGEdge corresponding to srSDFEdge " + orderedEdges.get(i) + " was not found.");
			}

			// Find the corresponding BufferProperty
			BufferProperties subBuffProperty = null;
			BufferAggregate buffers = (BufferAggregate) dagEdge.getPropertyBean()
					.getValue(BufferAggregate.propertyBeanName, BufferAggregate.class);
			for (BufferProperties subBufferProperties : buffers) {
				// The source and target actor are the same, check that the
				// ports are corrects
				if (orderedEdges.get(i).getTargetLabel().equals(subBufferProperties.getDestInputPortID())
						&& orderedEdges.get(i).getSourceLabel().equals(subBufferProperties.getSourceOutputPortID())) {
					subBuffProperty = subBufferProperties;
					break;
				}
			}

			if (subBuffProperty == null) {
				throw new CodegenException("Buffer property with ports " + orderedEdges.get(i).getTargetLabel()
						+ " and " + orderedEdges.get(i).getSourceLabel() + " was not found in DAGEdge aggregate "
						+ dagEdge);
			}

			// Get the corresponding Buffer
			Buffer buffer = srSDFEdgeBuffers.get(subBuffProperty);
			if (buffer == null) {
				throw new CodegenException("Buffer corresponding to DAGEdge" + dagEdge + "was not allocated.");
			}
			// Add it to the specialCall
			if (f.getType().equals(SpecialType.FORK) || f.getType().equals(SpecialType.BROADCAST)) {
				f.addOutputBuffer(buffer);
			} else {
				f.addInputBuffer(buffer);
			}
		}

		// Find the last buffer that correspond to the
		// exploded/broadcasted/joined/roundbuffered edge
		DAGEdge lastEdge = null;
		{
			// The vertex may have a maximum of 2 incoming/outgoing edges
			// but only one should be linked to the producer/consumer
			// the other must be linked to a send/receive vertex
			Set<DAGEdge> candidates;
			if (f.getType().equals(SpecialType.FORK) || f.getType().equals(SpecialType.BROADCAST)) {
				candidates = dag.incomingEdgesOf(dagVertex);
			} else {
				candidates = dag.outgoingEdgesOf(dagVertex);
			}

			if (candidates.size() > 2) {
				String direction;
				if (f.getType().equals(SpecialType.FORK) || f.getType().equals(SpecialType.BROADCAST)) {
					direction = "incoming";
				} else {
					direction = "outgoing";
				}
				throw new CodegenException(f.getType().getName() + " vertex " + dagVertex + " more than 1 " + direction
						+ "edge. Check the exported DAG.");
			}
			for (DAGEdge edge : candidates) {
				if (edge.getSource().getPropertyBean()
						.getValue(ImplementationPropertyNames.Vertex_vertexType, VertexType.class)
						.equals(VertexType.TASK)
						&& edge.getTarget().getPropertyBean()
								.getValue(ImplementationPropertyNames.Vertex_vertexType, VertexType.class)
								.equals(VertexType.TASK)) {
					lastEdge = edge;
				}
			}
			if (lastEdge == null) {
				// This should never happen. It would mean that a
				// "special vertex" does receive data only from send/receive
				// vertices
				throw new CodegenException(
						f.getType().getName() + " vertex " + dagVertex + "is not properly connected.");
			}
		}

		BufferAggregate bufferAggregate = (BufferAggregate) lastEdge.getPropertyBean()
				.getValue(BufferAggregate.propertyBeanName);
		// there should be only one buffer in the aggregate
		BufferProperties lastBuffProperty = bufferAggregate.get(0);
		Buffer lastBuffer = srSDFEdgeBuffers.get(lastBuffProperty);

		// Add it to the specialCall
		if (f.getType().equals(SpecialType.FORK) || f.getType().equals(SpecialType.BROADCAST)) {
			f.addInputBuffer(lastBuffer);
		} else {
			f.addOutputBuffer(lastBuffer);
		}

		operatorBlock.getLoopBlock().getCodeElts().add(f);
		dagVertexCalls.put(dagVertex, f);

		identifyMergedInputRange(new AbstractMap.SimpleEntry<List<Variable>, List<PortDirection>>(f.getParameters(),
				f.getParameterDirections()));
		registerCallVariableToCoreBlock(operatorBlock, f);
	}

	/**
	 * This method create a {@link SubBuffer} for each {@link SDFEdge}
	 * aggregated in the given {@link DAGEdge}. {@link SubBuffer} information
	 * are retrieved from the {@link #megs} of the {@link CodegenModelGenerator}
	 * . All created {@link SubBuffer} are referenced in the
	 * {@link #srSDFEdgeBuffers} map.
	 * 
	 * @param parentBuffer
	 *            the {@link Buffer} containing the generated {@link SubBuffer}
	 * @param dagEdge
	 *            the {@link DAGEdge} whose {@link Buffer} is generated.
	 * @param offset
	 *            the of the {@link DAGEdge} in the {@link Buffer}
	 * @return the total size of the subbuffers
	 * @throws CodegenException
	 *             If a {@link DataType} used in the graph is not declared in
	 *             the {@link PreesmScenario}.
	 * 
	 */
	protected Integer generateSubBuffers(Buffer parentBuffer, DAGEdge dagEdge, Integer offset) throws CodegenException {

		Map<String, DataType> dataTypes = scenario.getSimulationManager().getDataTypes();

		BufferAggregate buffers = (BufferAggregate) dagEdge.getPropertyBean().getValue(BufferAggregate.propertyBeanName,
				BufferAggregate.class);

		// Retrieve the corresponding memory object from the MEG
		MemoryExclusionVertex memObject = findMObject(dagEdge);
		@SuppressWarnings("unchecked")
		List<Integer> interSubbufferSpace = (List<Integer>) memObject.getPropertyBean()
				.getValue(MemoryExclusionVertex.INTER_BUFFER_SPACES, List.class);

		Integer aggregateOffset = new Integer(0);
		int idx = 0;
		for (BufferProperties subBufferProperties : buffers) {
			Buffer buff = null;
			// If the parent buffer is not null
			if (!(parentBuffer instanceof NullBuffer)) {
				SubBuffer subBuff = CodegenFactory.eINSTANCE.createSubBuffer();
				buff = subBuff;
				// Old naming techniques with complete path to port. (too long,
				// kept
				// as a comment)
				String comment = dagEdge.getSource().getName();
				comment += '_' + subBufferProperties.getSourceOutputPortID();
				comment += " > " + dagEdge.getTarget().getName();
				comment += '_' + subBufferProperties.getDestInputPortID();
				subBuff.setComment(comment);

				// Buffer is named only with ports ID
				String name = subBufferProperties.getSourceOutputPortID();
				name += "__" + subBufferProperties.getDestInputPortID();

				// Check for duplicates
				name = generateUniqueBufferName(name);

				subBuff.setName(name);
				subBuff.setContainer(parentBuffer);
				subBuff.setOffset(aggregateOffset);
				subBuff.setType(subBufferProperties.getDataType());
				subBuff.setSize(subBufferProperties.getSize());

				// Save the created SubBuffer
				srSDFEdgeBuffers.put(subBufferProperties, subBuff);
			} else {
				// The parent buffer is a null buffer
				NullBuffer nullBuff = CodegenFactory.eINSTANCE.createNullBuffer();
				buff = nullBuff;
				// Old naming techniques with complete path to port. (too long,
				// kept
				// as a comment)
				String comment = dagEdge.getSource().getName();
				comment += '_' + subBufferProperties.getSourceOutputPortID();
				comment += " > " + dagEdge.getTarget().getName();
				comment += '_' + subBufferProperties.getDestInputPortID();
				nullBuff.setComment("NULL_" + comment);
				nullBuff.setContainer(parentBuffer);

				// Save the created SubBuffer
				srSDFEdgeBuffers.put(subBufferProperties, nullBuff);
			}

			// If an interSubbufferSpace was defined, add it
			if (interSubbufferSpace != null) {
				aggregateOffset += interSubbufferSpace.get(idx);
			}
			idx++;

			// Increment the aggregate offset with the size of the current
			// subBuffer multiplied by the size of the datatype
			if (subBufferProperties.getDataType().equals("typeNotFound")) {
				throw new CodegenException("There is a problem with datatypes.\n"
						+ "Please make sure that all data types are defined in the Simulation tab of the scenario editor.");
			}
			DataType subBuffDataType = dataTypes.get(subBufferProperties.getDataType());
			if (subBuffDataType == null) {
				throw new CodegenException(
						"Data type " + subBufferProperties.getDataType() + " is undefined in the scenario.");
			}
			buff.setTypeSize(subBuffDataType.getSize());
			aggregateOffset += (buff.getSize() * subBuffDataType.getSize());
		}

		return aggregateOffset;
	}

	/**
	 * Using the {@link #bufferNames} map, this methods gives a new unique
	 * {@link Buffer} name beginning with the string passed as a parameter.
	 * Names that are longer than 28 characters will be shortened to this
	 * length..
	 * 
	 * @param name
	 *            the buffer name
	 * @return a unique name for the buffer
	 */
	protected String generateUniqueBufferName(String name) {
		Integer idx;
		String key = new String(name);
		if (key.length() > 28) {
			key = key.substring(0, 28);
		}
		if ((idx = bufferNames.get(key)) == null) {
			idx = new Integer(0);
			bufferNames.put(key, idx);
		}
		String bufferName = key + "__" + idx;
		idx += 1;
		bufferNames.put(key, idx);
		return bufferName;
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
	protected ActorPrototypes getActorPrototypes(SDFVertex sdfVertex) throws CodegenException {
		Object refinement = sdfVertex.getPropertyBean().getValue(AbstractVertex.REFINEMENT);

		// Check that it has an IDL refinement.
		if (!(refinement instanceof CodeRefinement) || ((CodeRefinement) refinement).getLanguage() != Language.IDL) {
			throw new CodegenException("generateFunctionCall was called with a DAG Vertex withoud IDL");
		}

		// Retrieve the IDL File
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();

		IPath path = ((CodeRefinement) refinement).getPath();
		IFile idlFile;
		// XXX: workaround for existing IBSDF projects where refinements are
		// under the form "../folder/file"
		if (path.toOSString().startsWith("..")) {
			String projectName = workflow.getProjectName();
			IProject project = root.getProject(projectName);
			path = new Path(project.getLocation() + path.toString().substring(2));
			idlFile = root.getFileForLocation(path);
		} else {
			idlFile = root.getFile(path);
		}

		// Retrieve the ActorPrototype
		IPath rawPath = idlFile.getRawLocation();
		String rawLocation = rawPath.toOSString();
		ActorPrototypes prototypes = IDLPrototypeFactory.INSTANCE.create(rawLocation);
		return prototypes;
	}

	/**
	 * The purpose of this method is to identify {@link Range} of input
	 * {@link Buffer} that are allocated in a memory space overlapping with a
	 * {@link Range} of output {@link Buffer}. Information on overlapping
	 * {@link Range} is saved in the {@link Buffer#getMergedRange() mergedRange}
	 * of the input {@link Buffer}. This information will be used for cache
	 * coherence purpose during code generation.
	 * 
	 * @param callVars
	 *            {@link Entry} containing a {@link List} of call
	 *            {@link Variable} of a function associated to a {@link List} of
	 *            their {@link PortDirection}.
	 * @throws CodegenException
	 *             throws an exception if the {@link MemoryExclusionVertex}
	 *             associated to a {@link DAGEdge} could not be found in any
	 *             {@link #megs}.
	 */
	protected void identifyMergedInputRange(Entry<List<Variable>, List<PortDirection>> callVars)
			throws CodegenException {

		// Separate input and output buffers
		List<Buffer> inputs = new ArrayList<Buffer>();
		List<Buffer> outputs = new ArrayList<Buffer>();
		for (int i = 0; i < callVars.getKey().size(); i++) {
			if (callVars.getValue().get(i) == PortDirection.INPUT) {
				inputs.add((Buffer) callVars.getKey().get(i));
			} else if (callVars.getValue().get(i) == PortDirection.OUTPUT) {
				outputs.add((Buffer) callVars.getKey().get(i));
			}
		}

		// For each output find the allocated range
		// (or Ranges in case of a divided buffer)
		List<Pair<Buffer, Range>> outputRanges = new ArrayList<>();
		for (Buffer output : outputs) {
			// If the input is not a NullBufer
			if (!(output instanceof NullBuffer)) {
				// Find the parent Buffer container b
				// and the offset within b.
				int start = 0;
				Buffer b = output;
				while (b instanceof SubBuffer) {
					start += ((SubBuffer) b).getOffset();
					b = ((SubBuffer) b).getContainer();
				}
				int end = start + (output.getSize() * output.getTypeSize());

				// Save allocated range
				outputRanges.add(new Pair<Buffer, Range>(b, new Range(start, end)));
			} else {
				// The output is a NullBuffer (i.e. it is divided)
				// Find the allocation of its ranges
				DAGEdge dagEdge = dagEdgeBuffers.inverse().get(((NullBuffer) output).getContainer());

				// Get the real ranges from the memObject
				MemoryExclusionVertex mObject = findMObject(dagEdge);

				@SuppressWarnings("unchecked")
				List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> realRanges = (List<Pair<MemoryExclusionVertex, Pair<Range, Range>>>) mObject
						.getPropertyBean().getValue(MemoryExclusionVertex.REAL_TOKEN_RANGE_PROPERTY);

				// Find the actual allocation range of each real range.
				for (Pair<MemoryExclusionVertex, Pair<Range, Range>> realRange : realRanges) {
					DAGEdge hostDagEdge = realRange.getKey().getEdge();
					DAGVertex originalSource = dag.getVertex(hostDagEdge.getSource().getName());
					DAGVertex originalTarget = dag.getVertex(hostDagEdge.getTarget().getName());
					DAGEdge originalDagEdge = dag.getEdge(originalSource, originalTarget);
					Buffer hostBuffer = dagEdgeBuffers.get(originalDagEdge);
					// Get the allocated range
					int start = realRange.getValue().getValue().getStart();
					Buffer b = hostBuffer;
					while (b instanceof SubBuffer) {
						start += ((SubBuffer) b).getOffset();
						b = ((SubBuffer) b).getContainer();
					}
					int end = start + realRange.getValue().getValue().getLength();
					// Save allocated range
					outputRanges.add(new Pair<Buffer, Range>(b, new Range(start, end)));
				}
			}
		}

		// Find if an inputBuffer has an overlap with an outputRange
		// For each input find the allocated range
		// Map<Buffer,Pair<Buffer,Range>> inputRanges = new HashMap<>();
		for (Buffer input : inputs) {
			// If the input is not a NullBufer
			if (!(input instanceof NullBuffer)) {
				// Find the parent Buffer container b
				// and the offset within b.
				int start = 0;
				Buffer b = input;
				while (b instanceof SubBuffer) {
					start += ((SubBuffer) b).getOffset();
					b = ((SubBuffer) b).getContainer();
				}
				int end = start + (input.getSize() * input.getTypeSize());

				// Find the input range that are also covered by the output
				// ranges
				List<Range> inRanges = new ArrayList<Range>();
				inRanges.add(new Range(start, end));

				// Check output ranges one by one
				for (Pair<Buffer, Range> outputRange : outputRanges) {
					if (outputRange.getKey() == b) {
						inRanges = Range.difference(inRanges, outputRange.getValue());
					}
				}
				List<Range> mergedRanges = new ArrayList<Range>();
				mergedRanges.add(new Range(start, end));
				mergedRanges = Range.difference(mergedRanges, inRanges);

				// Save only if a part of the input buffer is merged
				if (mergedRanges.size() != 0) {
					Range.translate(mergedRanges, -start);
					input.setMergedRange(new BasicEList<>(mergedRanges));
				}
			}
		}

	}

	/**
	 * Insert the {@link Communication} calls in the {@link LoopBlock} of the
	 * given {@link CoreBlock}.
	 * 
	 * @param operatorBlock
	 *            the {@link CoreBlock} on which the communication is executed.
	 * @param dagVertex
	 *            the {@link DAGVertex} corresponding to the given
	 *            {@link Communication}.
	 * @param newComm
	 *            the {@link Communication} {@link Call} to insert.
	 * 
	 * @throws CodegenException
	 *             if the newComm is a SendRelease or a ReceiveReserve.
	 */
	protected void insertCommunication(CoreBlock operatorBlock, DAGVertex dagVertex, Communication newComm)
			throws CodegenException {

		// Do this only for SS and RE
		if ((newComm.getDelimiter().equals(Delimiter.START) && newComm.getDirection().equals(Direction.SEND))
				|| (newComm.getDelimiter().equals(Delimiter.END) && newComm.getDirection().equals(Direction.RECEIVE))) {

			// Do the insertion
			operatorBlock.getLoopBlock().getCodeElts().add(newComm);

			// Save the communication in the dagVertexCalls map only if it
			// is a
			// SS or a ER
			if ((newComm.getDelimiter().equals(Delimiter.START) && newComm.getDirection().equals(Direction.SEND))
					|| (newComm.getDelimiter().equals(Delimiter.END)
							&& newComm.getDirection().equals(Direction.RECEIVE))) {
				dagVertexCalls.put(dagVertex, newComm);
			}

		} else {
			// Code reached for RS, SE, RR and SR
			// Retrieve the corresponding ReceiveEnd or SendStart
			Call zoneReference = null;
			if (newComm.getDirection().equals(Direction.SEND)) {
				zoneReference = newComm.getSendStart();
			}
			if (newComm.getDirection().equals(Direction.RECEIVE)) {
				zoneReference = newComm.getReceiveEnd();
			}

			// Get the index for the zone complement
			int index = operatorBlock.getLoopBlock().getCodeElts().indexOf(zoneReference);

			// For SE and RS
			if ((newComm.getDelimiter().equals(Delimiter.START) && newComm.getDirection().equals(Direction.RECEIVE))
					|| (newComm.getDelimiter().equals(Delimiter.END)
							&& newComm.getDirection().equals(Direction.SEND))) {

				// DO the insertion
				if (newComm.getDelimiter().equals(Delimiter.START)) {
					// Insert the RS before the RE
					operatorBlock.getLoopBlock().getCodeElts().add(index, newComm);
				} else {
					// Insert the SE after the SS
					operatorBlock.getLoopBlock().getCodeElts().add(index + 1, newComm);
				}
				// DO NOT save the SE and RS in the dagVertexCall.
			}
		}
	}

	/**
	 * Insert the {@link Communication} in the {@link LoopBlock} of the
	 * {@link CoreBlock} passed as a parameter. All {@link DAGVertex} consuming
	 * or producing data handled by the {@link Communication} must have been
	 * scheduled {@link #generateActorFiring(CoreBlock, DAGVertex) generated}
	 * before calling this method.<br>
	 * <br>
	 * In the current version, Send primitives are inserted as follow:<br>
	 * <code>(ProducingActor)-(SendStart)-(SendEnd)</code><br>
	 * and Receive primitives as follow:<br>
	 * <code>(ReceiveStart)-(ReceiveEnd)-(ConsumingActor)</code> <br>
	 * The SendEnd and ReceiveStart placed like this do not enable the
	 * reception/sending for the next iteration. <br>
	 * {@link #futureInsertCommunication(CoreBlock, DAGVertex, Communication)
	 * see this method to implement future comm insertion.}
	 * 
	 * 
	 * @param operatorBlock
	 *            the {@link CoreBlock} on which the communication is executed.
	 * @param dagVertex
	 *            the {@link DAGVertex} corresponding to the given
	 *            {@link Communication}.
	 * @param newComm
	 *            the {@link Communication} {@link Call} to insert.
	 * 
	 * @throws CodegenException
	 *             if the newComm is a SendRelease or a ReceiveReserve.
	 */
	@Deprecated
	protected void oldInsertCommunication(CoreBlock operatorBlock, DAGVertex dagVertex, Communication newComm)
			throws CodegenException {

		// Retrieve the vertex that must be before/after the communication.
		DAGVertex producerOrConsumer = null;
		if (newComm.getDirection().equals(Direction.SEND)) {
			// Get the producer.
			producerOrConsumer = dag.incomingEdgesOf(dagVertex).iterator().next().getSource();
		} else {
			producerOrConsumer = dag.outgoingEdgesOf(dagVertex).iterator().next().getTarget();
		}

		// Get the corresponding call
		Call prodOrConsumerCall = dagVertexCalls.get(producerOrConsumer);
		int prodOrConsumerindex = operatorBlock.getLoopBlock().getCodeElts().indexOf(prodOrConsumerCall);

		// Do this only for SS and RE
		if ((newComm.getDelimiter().equals(Delimiter.START) && newComm.getDirection().equals(Direction.SEND))
				|| (newComm.getDelimiter().equals(Delimiter.END) && newComm.getDirection().equals(Direction.RECEIVE))) {

			// If the index of the corresponding call was found
			if (prodOrConsumerindex != -1) {
				if (newComm.getDelimiter().equals(Delimiter.START)) {
					// SS
					// Insert after the producer/consumer
					operatorBlock.getLoopBlock().getCodeElts().add(prodOrConsumerindex + 1, newComm);
				} else {
					// RE
					// Insert before the producer/consumer
					operatorBlock.getLoopBlock().getCodeElts().add(prodOrConsumerindex, newComm);
				}

				// Save the communication in the dagVertexCalls map only if it
				// is a
				// SS or a ER
				if ((newComm.getDelimiter().equals(Delimiter.START) && newComm.getDirection().equals(Direction.SEND))
						|| (newComm.getDelimiter().equals(Delimiter.END)
								&& newComm.getDirection().equals(Direction.RECEIVE))) {
					dagVertexCalls.put(dagVertex, newComm);
				}
			} else {
				// The index was not found, this may happen when a multi-step
				// communication occurs
				// The receive end of the first step of a multistep
				// communication
				// will be the first to be processed.
				if (newComm.getDelimiter().equals(Delimiter.END) && newComm.getDirection().equals(Direction.RECEIVE)) {
					// Insert it according to its scheduled place.
					int dagVertexSchedulingOrder = (Integer) dagVertex.getPropertyBean()
							.getValue(ImplementationPropertyNames.Vertex_schedulingOrder, Integer.class);
					int insertionIndex = 0;
					for (CodeElt codeElt : operatorBlock.getLoopBlock().getCodeElts()) {
						// Iterate over the calls of the current operator
						if (codeElt instanceof Call) {
							DAGVertex vertex = dagVertexCalls.inverse().get(codeElt);

							if (vertex == null) {
								// this will happen when a ReceiveStart,
								// ReceiveRelease or a SendEnd, SendReserve is
								// encountered, since they have no corresponding
								// vertices in the DAG
							} else if ((Integer) vertex.getPropertyBean().getValue(
									ImplementationPropertyNames.Vertex_schedulingOrder,
									Integer.class) > dagVertexSchedulingOrder) {
								break;
							}
						}
						insertionIndex++;
					}
					// Do the insertion
					operatorBlock.getLoopBlock().getCodeElts().add(insertionIndex, newComm);
					dagVertexCalls.put(dagVertex, newComm);
				}
			}
		} else {
			// Code reached for RS, SE, RR and SR
			// Retrieve the corresponding ReceiveEnd or SendStart
			Call zoneReference = null;
			if (newComm.getDirection().equals(Direction.SEND)) {
				zoneReference = newComm.getSendStart();
			}
			if (newComm.getDirection().equals(Direction.RECEIVE)) {
				zoneReference = newComm.getReceiveEnd();
			}

			// Get the index for the zone complement
			int index = operatorBlock.getLoopBlock().getCodeElts().indexOf(zoneReference);

			// For SE and RS
			if ((newComm.getDelimiter().equals(Delimiter.START) && newComm.getDirection().equals(Direction.RECEIVE))
					|| (newComm.getDelimiter().equals(Delimiter.END)
							&& newComm.getDirection().equals(Direction.SEND))) {

				// DO the insertion
				if (newComm.getDelimiter().equals(Delimiter.START)) {
					// Insert the RS before the RE
					operatorBlock.getLoopBlock().getCodeElts().add(index, newComm);
				} else {
					// Insert the SE after the SS
					operatorBlock.getLoopBlock().getCodeElts().add(index + 1, newComm);
				}
				// DO NOT save the SE and RS in the dagVertexCall.
			}
		}
	}

	/**
	 * {@link Buffer#setCreator(Block) Set the creator} of the given
	 * {@link Buffer} to the given {@link CoreBlock}, and recursively iterate
	 * over the {@link Buffer#getChildrens() children} {@link SubBuffer} of this
	 * {@link Buffer} to set the same {@link Buffer#setCreator(Block) creator}
	 * for them.
	 * 
	 * @param buffer
	 *            The {@link Buffer} whose creator is to be set.
	 * @param correspondingOperatorBlock
	 *            The creator {@link Block}.
	 * @param isLocal
	 *            boolean used to set the {@link Buffer#isLocal()} property of
	 *            all {@link Buffer}
	 */
	private void recusriveSetBufferCreator(Buffer buffer, CoreBlock correspondingOperatorBlock, boolean isLocal) {
		// Set the creator for the current buffer
		buffer.setCreator(correspondingOperatorBlock);
		buffer.setLocal(isLocal);

		// Do the same recursively for all its children subbuffers
		for (SubBuffer subBuffer : buffer.getChildrens()) {
			recusriveSetBufferCreator(subBuffer, correspondingOperatorBlock, isLocal);
		}
	}

	/**
	 * Register the {@link Variable} used by the {@link Call} as used by the
	 * {@link CoreBlock} passed as a parameter.
	 * 
	 * @param operatorBlock
	 *            the {@link CoreBlock} that is a user of the variables.
	 * @param call
	 *            the {@link Call} whose {@link Variable variables} are
	 *            registered
	 */
	protected void registerCallVariableToCoreBlock(CoreBlock operatorBlock, Call call) {
		// Register the core Block as a user of the function variable
		for (Variable var : call.getParameters()) {
			// Currently, constants do not need to be declared nor
			// have creator since their value is directly used.
			// Consequently the used block can also be declared as the creator
			// Logger logger = WorkflowLogger.getLogger();
			// p("Codegen registerCallVariableToCoreBlock " + var.getName());
			if (var instanceof Constant) {
				var.setCreator(operatorBlock);
				// p("Codegen registerCallVariableToCoreBlock Creator " +
				// var.getCreator().getName());
			}
			// var.setCreator(operatorBlock);
			var.getUsers().add(operatorBlock);
		}
	}

	/**
	 * This method find the {@link Communication communications} associated to
	 * the {@link Communication} passed as a parameter. Communication are
	 * associated if they are involved in the communication of the same buffer
	 * but with different {@link Direction} and {@link Delimiter}. The
	 * {@link Communication#getSendStart()}, {@link Communication#getSendEnd()},
	 * {@link Communication#getSendReserve()},
	 * {@link Communication#getReceiveStart()},
	 * {@link Communication#getReceiveEnd()} and
	 * {@link Communication#getReceiveRelease()} attributes are updated by this
	 * method.<br>
	 * <br>
	 * The methods also associates a common {@link Communication#getId() Id} to
	 * all associated communications.
	 * 
	 * @param newCommmunication
	 *            The {@link Communication} to register.
	 * @param dagEdge
	 *            The {@link DAGEdge} associated to the communication.
	 * @param dagVertex
	 *            the {@link DAGVertex} (Send or Receive) at the origin of the
	 *            newCommunication creation.
	 */
	protected void registerCommunication(Communication newCommmunication, DAGEdge dagEdge, DAGVertex dagVertex) {
		// Retrieve the routeStep corresponding to the vertex.
		// In case of multi-step communication, this is the easiest
		// way to retrieve the target and source of the communication
		// corresponding to the current Send/ReceiveVertex
		MessageRouteStep routeStep = (MessageRouteStep) dagVertex.getPropertyBean()
				.getValue(ImplementationPropertyNames.SendReceive_routeStep, MessageRouteStep.class);

		String commID = routeStep.getSender().getInstanceName();
		commID += "__" + dagEdge.getSource().getName();
		commID += "___" + routeStep.getReceiver().getInstanceName();
		commID += "__" + dagEdge.getTarget().getName();
		List<Communication> associatedCommunications = communications.get(commID);

		// Get associated Communications and set ID
		if (associatedCommunications == null) {
			associatedCommunications = new ArrayList<Communication>();
			newCommmunication.setId(communications.size());
			communications.put(commID, associatedCommunications);
		} else {
			newCommmunication.setId(associatedCommunications.get(0).getId());
		}

		// Register other comm to the new
		for (Communication com : associatedCommunications) {
			if (com.getDirection().equals(Direction.SEND)) {
				if (com.getDelimiter().equals(Delimiter.START))
					newCommmunication.setSendStart(com);
				if (com.getDelimiter().equals(Delimiter.END))
					newCommmunication.setSendEnd(com);
			}
			if (com.getDirection().equals(Direction.RECEIVE)) {
				if (com.getDelimiter().equals(Delimiter.START))
					newCommmunication.setReceiveStart(com);
				if (com.getDelimiter().equals(Delimiter.END))
					newCommmunication.setReceiveEnd(com);
			}
		}

		// Register new comm to its co-workers
		associatedCommunications.add(newCommmunication);
		for (Communication com : associatedCommunications) {
			if (newCommmunication.getDirection().equals(Direction.SEND)) {
				if (newCommmunication.getDelimiter().equals(Delimiter.START))
					com.setSendStart(newCommmunication);
				if (newCommmunication.getDelimiter().equals(Delimiter.END))
					com.setSendEnd(newCommmunication);
			} else {
				if (newCommmunication.getDelimiter().equals(Delimiter.START))
					com.setReceiveStart(newCommmunication);
				if (newCommmunication.getDelimiter().equals(Delimiter.END))
					com.setReceiveEnd(newCommmunication);
			}
		}
	}

	/**
	 * The purpose of this function is to restore to their original size the
	 * {@link MemoryExclusionVertex} that were merged when applying memory
	 * scripts.
	 */
	protected void restoreHostedVertices() {
		for (MemoryExclusionGraph meg : megs.values()) {
			@SuppressWarnings("unchecked")
			Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>> hostBuffers = (Map<MemoryExclusionVertex, Set<MemoryExclusionVertex>>) meg
					.getPropertyBean().getValue(MemoryExclusionGraph.HOST_MEMORY_OBJECT_PROPERTY);
			if (hostBuffers != null) {
				for (Entry<MemoryExclusionVertex, Set<MemoryExclusionVertex>> entry : hostBuffers.entrySet()) {
					// Since host vertices are naturally aligned, no need to
					// restore
					// them

					// Restore the real size of hosted vertices
					Set<MemoryExclusionVertex> vertices = entry.getValue();

					for (MemoryExclusionVertex vertex : vertices) {
						// For non-divided vertices
						if (vertex.getWeight() != 0) {
							int emptySpace = (int) vertex.getPropertyBean()
									.getValue(MemoryExclusionVertex.EMPTY_SPACE_BEFORE);

							// Put the vertex back to its real size
							vertex.setWeight(vertex.getWeight() - emptySpace);

							// And set the allocated offset
							int allocatedOffset = (int) vertex.getPropertyBean()
									.getValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY);

							vertex.setPropertyValue(MemoryExclusionVertex.MEMORY_OFFSET_PROPERTY,
									allocatedOffset + emptySpace);
							@SuppressWarnings("unchecked")
							Map<DAGEdge, Integer> dagEdgeAllocation = (Map<DAGEdge, Integer>) meg.getPropertyBean()
									.getValue(MemoryExclusionGraph.DAG_EDGE_ALLOCATION);
							dagEdgeAllocation.put(vertex.getEdge(), allocatedOffset + emptySpace);
						}
					}
				}
			}
		}
	}
}