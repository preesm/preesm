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

import net.sf.dftools.algorithm.iterators.SDFIterator;
import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;
import net.sf.dftools.algorithm.model.parameters.ParameterSet;
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.algorithm.model.sdf.SDFEdge;
import net.sf.dftools.algorithm.model.sdf.SDFGraph;
import net.sf.dftools.architecture.slam.ComponentInstance;
import net.sf.dftools.architecture.slam.Design;

import org.ietr.preesm.codegen.idl.ActorPrototypes;
import org.ietr.preesm.codegen.idl.Prototype;
import org.ietr.preesm.codegen.model.CodeGenArgument;
import org.ietr.preesm.codegen.model.CodeGenSDFBroadcastVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFForkVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFGraph;
import org.ietr.preesm.codegen.model.CodeGenSDFJoinVertex;
import org.ietr.preesm.codegen.model.CodeGenSDFRoundBufferVertex;
import org.ietr.preesm.codegen.model.ICodeGenSDFVertex;
import org.ietr.preesm.codegen.model.allocators.VirtualHeapAllocator;
import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;
import org.ietr.preesm.codegen.model.buffer.Buffer;
import org.ietr.preesm.codegen.model.containers.AbstractCodeContainer;
import org.ietr.preesm.codegen.model.containers.ForLoop;
import org.ietr.preesm.codegen.model.containers.LinearCodeContainer;
import org.ietr.preesm.codegen.model.main.SchedulingOrderComparator;
import org.ietr.preesm.codegen.model.main.SourceFile;
import org.ietr.preesm.codegen.model.threads.ComputationThreadDeclaration;
import org.ietr.preesm.codegen.model.types.CodeSectionType;
import org.ietr.preesm.codegen.phase.AbstractPhaseCodeGenerator;
import org.ietr.preesm.codegen.phase.BeginningCodeGenerator;
import org.ietr.preesm.codegen.phase.LoopCodeGenerator;
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
	 * Fills its source file from an SDF and an architecture
	 */
	public void generateSource(CodeGenSDFGraph algorithm, Design architecture) {

		// Gets the tasks vertices allocated to the current operator in
		// scheduling order
		SortedSet<SDFAbstractVertex> ownTaskVertices = getOwnVertices(
				algorithm, VertexType.task);

		// Gets the communication vertices allocated to the current operator in
		// scheduling order
		SortedSet<SDFAbstractVertex> ownCommunicationVertices = getOwnVertices(
				algorithm, VertexType.send);
		ownCommunicationVertices.addAll(getOwnVertices(algorithm,
				VertexType.receive));

		// Buffers defined as global variables are retrieved here. They are
		// added globally to the file
		try {
			allocateBuffers(algorithm);
		} catch (InvalidExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		// Allocation of route step buffers
		allocateRouteSteps(ownCommunicationVertices);

		// Creating computation thread in which all SDF function calls will be
		// located
		ComputationThreadDeclaration computationThread = new ComputationThreadDeclaration(
				file);
		file.addThread(computationThread);

		// Generating code for the beginning phase
		LinearCodeContainer beginning = new LinearCodeContainer(
				computationThread);
		computationThread.addContainer(beginning);
		AbstractPhaseCodeGenerator beginningCodegen = new BeginningCodeGenerator(
				beginning);

		// Inserts the user function calls and adds their parameters; possibly
		// including graph parameters

		fillSection(beginningCodegen, beginning, algorithm.getParameters(),
				ownTaskVertices, ownCommunicationVertices,
				file.getGlobalContainer());

		// Generating code for the computation loop
		ForLoop loop = new ForLoop(computationThread);
		computationThread.addContainer(loop);
		AbstractPhaseCodeGenerator loopCodegen = new LoopCodeGenerator(loop);

		// Inserts the user function calls and adds their parameters; possibly
		// including graph parameters

		fillSection(loopCodegen, loop, algorithm.getParameters(),
				ownTaskVertices, ownCommunicationVertices,
				file.getGlobalContainer());
	}

	private void fillSection(AbstractPhaseCodeGenerator codegen,
			AbstractCodeContainer container, ParameterSet parameterSet,
			SortedSet<SDFAbstractVertex> tasks,
			SortedSet<SDFAbstractVertex> coms,
			AbstractBufferContainer bufferContainer) {

		// PSDF code
		codegen.addDynamicParameter(parameterSet);
		codegen.addUserFunctionCalls(tasks);

		// Inserts the communication function calls, the communication
		// thread semaphore post and pends and the communication
		// initializations
		codegen.addSendsAndReceives(coms, bufferContainer);
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
	public static boolean usesBufferInCodeContainerType(
			SDFAbstractVertex vertex, CodeSectionType codeContainerType,
			Buffer buf, String direction) {

		// Special vertices are considered to use systematically their buffers
		if (codeContainerType.equals(CodeSectionType.loop)
				&& (vertex instanceof CodeGenSDFBroadcastVertex
						|| vertex instanceof CodeGenSDFForkVertex
						|| vertex instanceof CodeGenSDFJoinVertex || vertex instanceof CodeGenSDFRoundBufferVertex)) {
			return true;
		}

		if (!(vertex.getRefinement() instanceof ActorPrototypes)) { // TODO :
																		// treat
																		// when
																		// the
																		// vertex
																		// has a
																		// graph
																		// refinement
			return true;
		}

		if (vertex.getRefinement() == null
				|| !(vertex.getRefinement() instanceof ActorPrototypes)) {
			return true;
		}

		ActorPrototypes prototypes = ((ActorPrototypes) vertex.getRefinement());
		Prototype currentPrototype = null;

		// loop call references its init calls
		if (prototypes != null) {

			switch (codeContainerType) {
			case beginning:
				currentPrototype = prototypes.getInitCall();
				break;
			case loop:
				currentPrototype = prototypes.getLoopCall();
				break;
			default:
				break;
			}
		}

		if (prototypes != null) {
			Set<CodeGenArgument> argSet = currentPrototype.getArguments().keySet();

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
			if (usesBufferInCodeContainerType(vertex, codeContainerType, buf,
					direction)) {
				return true;
			}
		}

		return false;
	}

}
