/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-B license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-B
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
knowledge of the CeCILL-B license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.codegen;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.ietr.dftools.algorithm.iterators.SDFIterator;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.parameters.ParameterSet;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.preesm.codegen.idl.ActorPrototypes;
import org.ietr.preesm.codegen.idl.IDLPrototypeFactory;
import org.ietr.preesm.codegen.idl.Prototype;
import org.ietr.preesm.codegen.model.CodeGenArgument;
import org.ietr.preesm.codegen.model.CodeGenSDFBroadcastVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFFifoPullVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFForkVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFGraph;
import org.ietr.preesm.codegen.model.CodeGenSDFJoinVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFRoundBufferVertex;
import org.ietr.preesm.codegen.model.ICodeGenSDFVertex;
import org.ietr.preesm.codegen.model.allocators.VirtualHeapAllocator;
import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;
import org.ietr.preesm.codegen.model.buffer.Buffer;
import org.ietr.preesm.codegen.model.call.UserFunctionCall;
import org.ietr.preesm.codegen.model.containers.AbstractCodeContainer;
import org.ietr.preesm.codegen.model.containers.CodeSectionType;
import org.ietr.preesm.codegen.model.containers.ForLoop;
import org.ietr.preesm.codegen.model.containers.LinearCodeContainer;
import org.ietr.preesm.codegen.model.containers.CodeSectionType.MajorType;
import org.ietr.preesm.codegen.model.main.ICodeElement;
import org.ietr.preesm.codegen.model.main.SchedulingOrderComparator;
import org.ietr.preesm.codegen.model.main.SourceFile;
import org.ietr.preesm.codegen.model.main.SourceFileList;
import org.ietr.preesm.codegen.model.threads.ComputationThreadDeclaration;
import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.types.DataType;
import org.ietr.preesm.core.types.ImplementationPropertyNames;
import org.ietr.preesm.core.types.VertexType;

/**
 * Generates code for a source file
 * 
 * @author Matthieu Wipliez
 * @author mpelcat
 * @author jpiat
 */
public class SourceFileCodeGenerator {

	SourceFile file;
	VirtualHeapAllocator heap;

	public SourceFileCodeGenerator(SourceFile file) {
		this.file = file;
	}

	/**
	 * Buffers belonging to SDF vertices in the given set are allocated here.
	 * 
	 * @throws InvalidExpressionException
	 */
	public void allocateBuffers(SDFGraph algo)
			throws InvalidExpressionException {
		SDFIterator iterator = new SDFIterator(algo);
		// Iteration on own buffers
		while (iterator.hasNext()) {
			SDFAbstractVertex vertex = iterator.next();
			// retrieving the operator where the vertex is allocated
			ComponentInstance vertexOperator = (ComponentInstance) vertex
					.getPropertyBean().getValue(
							ImplementationPropertyNames.Vertex_Operator);
			if (vertex instanceof ICodeGenSDFVertex
					&& vertexOperator != null
					&& vertexOperator.getInstanceName().equals(
							file.getOperator().getInstanceName())) {
				// Allocating all output buffers of vertex
				allocateVertexOutputBuffers(vertex);
			}
		}
	}

	/**
	 * Allocates all the buffers retrieved from a given buffer aggregate. The
	 * boolean isInputBuffer is true if the aggregate belongs to an incoming
	 * edge and false if the aggregate belongs to an outgoing edge
	 */
	public void allocateEdgeBuffers(SDFEdge edge) {
		String bufferName = edge.getSourceInterface().getName() + "_"
				+ edge.getTargetInterface().getName();
		file.allocateBuffer(edge, bufferName, new DataType(edge.getDataType()
				.toString()));

	}

	/**
	 * Route steps are allocated here. A route steps means that a receive and a
	 * send are called successively. The receive output is allocated.
	 */
	public void allocateRouteSteps(Set<SDFAbstractVertex> comVertices) {

		Iterator<SDFAbstractVertex> vIterator = comVertices.iterator();

		// Iteration on own buffers
		while (vIterator.hasNext()) {
			SDFAbstractVertex vertex = vIterator.next();

			if (VertexType.isIntermediateReceive(vertex)) {
				allocateVertexOutputBuffers(vertex);
			}
		}
	}

	/**
	 * Allocates buffers belonging to vertex. If isInputBuffer is true,
	 * allocates the input buffers, otherwise allocates output buffers.
	 */
	@SuppressWarnings("unchecked")
	public void allocateVertexOutputBuffers(SDFAbstractVertex vertex) {
		Set<SDFEdge> edgeSet;

		edgeSet = new HashSet<SDFEdge>(vertex.getBase().outgoingEdgesOf(vertex));
		// Removes edges between two operators
		removeInterEdges(edgeSet);

		// Iteration on all the edges of each vertex belonging to ownVertices
		for (SDFEdge edge : edgeSet) {
			allocateEdgeBuffers(edge);
		}
	}

	/**
	 * Fills its source file from an algorithm, an architecture and a prototype retriever
	 */
	public void generateComputationSource(CodeGenSDFGraph algorithm, Design architecture,
			IDLPrototypeFactory idlPrototypeFactory) {

		// Gets the tasks vertices allocated to the current operator in
		// scheduling order
		SortedSet<SDFAbstractVertex> ownTaskVertices = getOwnVertices(
				algorithm, VertexType.TASK);

		// Buffers defined as global variables are retrieved here. They are
		// added globally to the file
		
		try {
			allocateBuffers(algorithm);
		} catch (InvalidExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		// Creating computation thread in which all function calls will be
		// located
		ComputationThreadDeclaration computationThread = new ComputationThreadDeclaration(
				file);
		file.addThread(computationThread);
		
		// Creating linear container to insert communication initialization to be customized in XSLT
		CodeSectionType sectionType = new CodeSectionType(MajorType.COMINIT);
		LinearCodeContainer comInit = new LinearCodeContainer(
				computationThread, sectionType, "COMINIT");
		computationThread.addContainer(comInit);
		
		generateFifoInitSection(computationThread, ownTaskVertices);
		
		// The maximum number of init phases is kept for all cores. May be
		// improved later
		int numberOfInitPhases = idlPrototypeFactory.getNumberOfInitPhases();

		// Generating code for the init phases from upper to lower number
		for (int i = numberOfInitPhases - 1; i >= 0; i--) {
			generateInitSection(computationThread, algorithm.getParameters(),
					ownTaskVertices,
					file.getGlobalContainer(), i);
		}

		// Generating code for the computation loop
		generateLoopSection(computationThread, algorithm.getParameters(),
				ownTaskVertices,
				file.getGlobalContainer());
	}

	/**
	 * Generates a section to initialize fifos
	 */
	public void generateFifoInitSection(ComputationThreadDeclaration computationThread, Set<SDFAbstractVertex> vertices) {
		CodeSectionType sectionType = new CodeSectionType(MajorType.FIFOINIT);
		LinearCodeContainer fifoInit = new LinearCodeContainer(
				computationThread, sectionType, "Fifo Initialization Section");
		computationThread.addContainer(fifoInit);
		
		for (SDFAbstractVertex vertex : vertices) {
			
			// Initializing FIFO that are considered like user functions
			if (vertex instanceof CodeGenSDFFifoPullVertex) {
				ICodeElement beginningCall = new UserFunctionCall(
						(CodeGenSDFFifoPullVertex) vertex, fifoInit,
						sectionType, false);

				fifoInit.addInitCodeElement(beginningCall);
			} 
		}
	}

	/**
	 * Fills its source file from an algorithm, an architecture and a prototype retriever
	 */
	public void generateCommunicationSource(CodeGenSDFGraph algorithm, Design architecture,
			IDLPrototypeFactory idlPrototypeFactory, SourceFileList sourceFiles) {

		// Gets the communication vertices allocated to the current operator in
		// scheduling order
		SortedSet<SDFAbstractVertex> ownCommunicationVertices = getOwnVertices(
				algorithm, VertexType.SEND);
		ownCommunicationVertices.addAll(getOwnVertices(algorithm,
				VertexType.RECEIVE));

		// Allocation of route step buffers
		allocateRouteSteps(ownCommunicationVertices);

		// The maximum number of init phases is kept for all cores. May be
		// improved later
		int numberOfInitPhases = idlPrototypeFactory.getNumberOfInitPhases();

		// Generating code for the init phases from upper to lower number
		for (int i = numberOfInitPhases - 1; i >= 0; i--) {
			CodeSectionType codeSectionType = new CodeSectionType(MajorType.INIT,i);
			AbstractCodeContainer initContainer = file.getContainer(codeSectionType);
			addCommunication(initContainer, ownCommunicationVertices,
					file.getGlobalContainer(), codeSectionType, sourceFiles);
		}

		CodeSectionType codeSectionType = new CodeSectionType(MajorType.LOOP);
		AbstractCodeContainer loopContainer = file.getContainer(codeSectionType);
		// Generating code for the computation loop
		addCommunication(loopContainer, ownCommunicationVertices,
				file.getGlobalContainer(), codeSectionType, sourceFiles);
	}

	/**
	 * Generates and fill a code section
	 */
	private PhaseCodeGenerator generateInitSection(
			ComputationThreadDeclaration computationThread,ParameterSet parameterSet,
			SortedSet<SDFAbstractVertex> tasks,
			AbstractBufferContainer bufferContainer, int initIndex) {
		CodeSectionType sectionType = new CodeSectionType(MajorType.INIT,initIndex);
		LinearCodeContainer init = new LinearCodeContainer(
				computationThread, sectionType, "Initialization phase number " + initIndex);
		computationThread.addContainer(init);
		PhaseCodeGenerator initCodegen = new PhaseCodeGenerator(init);

		// Inserts the user function calls and adds their parameters; possibly
		// including graph parameters

		addComputation(initCodegen, init, parameterSet, tasks,
				bufferContainer,sectionType);
		
		return initCodegen;
	}
	
	/**
	 * Generates and fill a code section
	 */
	private void generateLoopSection(
			ComputationThreadDeclaration computationThread,ParameterSet parameterSet,
			SortedSet<SDFAbstractVertex> tasks,
			AbstractBufferContainer bufferContainer) {
		CodeSectionType sectionType = new CodeSectionType(MajorType.LOOP);
		ForLoop loop = new ForLoop(computationThread,
				sectionType, "Main loop of computation");
		computationThread.addContainer(loop);
		PhaseCodeGenerator loopCodegen = new PhaseCodeGenerator(loop);

		// Inserts the user function calls and adds their parameters; possibly
		// including graph parameters

		addComputation(loopCodegen, loop, parameterSet, tasks,
				bufferContainer, sectionType);
	}

	/**
	 * Fill a code section with computation
	 */
	private void addComputation(PhaseCodeGenerator codegen,
			AbstractCodeContainer container, ParameterSet parameterSet,
			SortedSet<SDFAbstractVertex> tasks,
			AbstractBufferContainer bufferContainer,
			CodeSectionType sectionType) {

		// PSDF code
		codegen.addDynamicParameter(parameterSet);
		codegen.addUserFunctionCalls(tasks, sectionType);
	}

	/**
	 * Fill a code section with communication
	 */
	private void addCommunication(AbstractCodeContainer codeContainer,
			SortedSet<SDFAbstractVertex> coms,
			AbstractBufferContainer bufferContainer,
			CodeSectionType sectionType,
			SourceFileList sourceFiles) {

		for (SDFAbstractVertex vertex : coms) {
			AbstractRouteStep step = (AbstractRouteStep) vertex
					.getPropertyBean().getValue(
							ImplementationPropertyNames.SendReceive_routeStep);

			// Delegates the com creation to the appropriate generator
			ComCodeGenerator generator = new ComCodeGenerator(codeContainer, coms, step);

			// Creates all functions and buffers related to the given vertex
			generator.insertComs(vertex, sectionType, sourceFiles);
		}
	}

	/**
	 * Gets every task vertices allocated to the current operator in their
	 * scheduling order
	 */
	public SortedSet<SDFAbstractVertex> getOwnVertices(
			CodeGenSDFGraph algorithm, VertexType currentType) {

		// Iterating tasks in their scheduling order
		ConcurrentSkipListSet<SDFAbstractVertex> schedule = new ConcurrentSkipListSet<SDFAbstractVertex>(
				new SchedulingOrderComparator());
		Iterator<SDFAbstractVertex> iterator = algorithm.vertexSet().iterator();

		while (iterator.hasNext()) {
			SDFAbstractVertex vertex = iterator.next();

			// retrieving the operator where the vertex is allocated
			ComponentInstance vertexOperator = (ComponentInstance) vertex
					.getPropertyBean().getValue(
							ImplementationPropertyNames.Vertex_Operator);

			// retrieving the type of the vertex
			VertexType vertexType = (VertexType) vertex.getPropertyBean()
					.getValue(ImplementationPropertyNames.Vertex_vertexType);

			// If the vertex is allocated on the current operator, we add it to
			// the set in scheduling order
			if (vertexOperator != null
					&& vertexOperator.getInstanceName().equals(
							file.getOperator().getInstanceName())
					&& vertexType != null && vertexType.equals(currentType)
					&& !schedule.contains(vertex)) {
				schedule.add(vertex);
			}
		}

		return schedule;
	}

	/**
	 * Removing edges that are redundant information with send/receive.
	 */
	public void removeInterEdges(Set<SDFEdge> edgeSet) {
		Iterator<SDFEdge> eIterator = edgeSet.iterator();

		while (eIterator.hasNext()) {
			SDFEdge edge = eIterator.next();
			if (!edge
					.getSource()
					.getPropertyBean()
					.getValue(ImplementationPropertyNames.Vertex_Operator)
					.equals(edge
							.getTarget()
							.getPropertyBean()
							.getValue(
									ImplementationPropertyNames.Vertex_Operator))) {
				eIterator.remove();
			}
		}
	}

	/**
	 * Returns true if the vertex function prototype of the given
	 * codeContainerType uses the buffer buf
	 */
	public static boolean isBufferUsedInCodeContainerType(
			SDFAbstractVertex vertex, CodeSectionType codeContainerType,
			Buffer buf, String direction) {

		// Special vertices are considered to use systematically their buffers
		if (codeContainerType.equals(MajorType.LOOP)
				&& (vertex instanceof CodeGenSDFBroadcastVertex
						|| vertex instanceof CodeGenSDFForkVertex
						|| vertex instanceof CodeGenSDFJoinVertex || vertex instanceof CodeGenSDFRoundBufferVertex)) {
			return true;
		}

		if (!(vertex.getRefinement() instanceof ActorPrototypes)) {
			// TODO: manage hierarchy
			return true;
		}

		if (vertex.getRefinement() == null
				|| !(vertex.getRefinement() instanceof ActorPrototypes)) {
			return true;
		}

		// An actor has several soociated prototypes for the init phases and
		// loop phase
		// Getting the prototype from desired phase
		ActorPrototypes prototypes = ((ActorPrototypes) vertex.getRefinement());
		Prototype currentPrototype = null;

		// loop call references its init calls
		if (prototypes != null) {
			currentPrototype = prototypes.getPrototype(codeContainerType);
		}

		// Checking the use of the buffer in the arguments
		if (currentPrototype != null) {
			Set<CodeGenArgument> argSet = currentPrototype.getArguments()
					.keySet();

			for (CodeGenArgument arg : argSet) {
				if (direction.equals("output")) {
					if (((arg.getDirection().equals(CodeGenArgument.OUTPUT)) || (arg
							.getDirection().equals(CodeGenArgument.INOUT)))
							&& arg.getName()
									.equals(buf.getSourceOutputPortID())) {
						return true;
					}
				} else {
					if (((arg.getDirection().equals(CodeGenArgument.INPUT)) || (arg
							.getDirection().equals(CodeGenArgument.INOUT)))
							&& arg.getName().equals(buf.getDestInputPortID())) {
						return true;
					}
				}
			}
		}

		// return true;
		return false;
	}

	/**
	 * Returns true if the vertex function prototype of the given
	 * codeContainerType uses at least one of the buffers in bufs
	 */
	public static boolean usesBuffersInCodeContainerType(
			SDFAbstractVertex vertex, CodeSectionType codeContainerType,
			List<Buffer> bufs, String direction) {

		for (Buffer buf : bufs) {
			if (isBufferUsedInCodeContainerType(vertex, codeContainerType, buf,
					direction)) {
				return true;
			}
		}

		return false;
	}

}
