package org.ietr.preesm.codegen.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.algorithm.model.sdf.SDFEdge;
import net.sf.dftools.architecture.slam.ComponentInstance;

import org.ietr.preesm.codegen.SourceFileCodeGenerator;
import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;
import org.ietr.preesm.codegen.model.buffer.Buffer;
import org.ietr.preesm.codegen.model.com.CommunicationFunctionCall;
import org.ietr.preesm.codegen.model.com.CommunicationFunctionInit;
import org.ietr.preesm.codegen.model.com.ReceiveInit;
import org.ietr.preesm.codegen.model.com.ReceiveMsg;
import org.ietr.preesm.codegen.model.com.SendInit;
import org.ietr.preesm.codegen.model.com.SendMsg;
import org.ietr.preesm.codegen.model.com.WaitForCore;
import org.ietr.preesm.codegen.model.threads.CommunicationThreadDeclaration;
import org.ietr.preesm.codegen.model.threads.ComputationThreadDeclaration;
import org.ietr.preesm.codegen.model.types.CodeSectionType;
import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.types.ImplementationPropertyNames;
import org.ietr.preesm.core.types.VertexType;
import org.jgrapht.alg.DirectedNeighborIndex;

/**
 * Generating communication code (initialization and calls) for a message Route
 * Step
 * 
 * @author mpelcat
 */
public class MessageComCodeGenerator extends AbstractComCodeGenerator {

	public MessageComCodeGenerator(ComputationThreadDeclaration compThread,
			CommunicationThreadDeclaration comThread,
			SortedSet<SDFAbstractVertex> vertices, AbstractRouteStep step) {
		super(compThread, comThread, vertices, step);
	}

	/**
	 * Calls the initialization functions at the beginning of computation and
	 * communication thread executions
	 */
	protected void createinits(CommunicationFunctionCall call,
			AbstractBufferContainer bufferContainer,
			Set<CommunicationFunctionInit> alreadyInits) {

		CommunicationFunctionInit init = null;
		WaitForCore wait = null;

		// Creating Send and Receive initialization calls
		if (call instanceof SendMsg) {
			SendMsg send = (SendMsg) call;

			init = new SendInit(bufferContainer, send.getTarget()
					.getInstanceName(), send.getRouteStep(), -1);
			wait = new WaitForCore(bufferContainer, send.getRouteStep());
		} else if (call instanceof ReceiveMsg) {
			ReceiveMsg receive = (ReceiveMsg) call;

			init = new ReceiveInit(bufferContainer, receive.getSource()
					.getInstanceName(), receive.getRouteStep(), -1);
			wait = new WaitForCore(bufferContainer, receive.getRouteStep());
		}

		// Checking that the initialization has not already been done
		if (init != null) {
			for (CommunicationFunctionInit oldInit : alreadyInits) {
				if (oldInit.getConnectedCoreId().equals(
						init.getConnectedCoreId())
						&& oldInit.getRouteStep().equals(init.getRouteStep())) {
					// core wait has already been done
					wait = null;

					if (oldInit.getName().equals(init.getName())) {
						// init has already been done with same direction
						init = null;
					}
					break;
				}
			}
		}

		// Adding Send and Receive initialization calls
		if (init != null) {
			comThread.getBeginningCode().addCodeElementFirst(init);
			alreadyInits.add(init);
		}

		// Adding other cores wait
		if (wait != null) {
			comThread.getBeginningCode().addCodeElement(wait);
		}

	}

	/**
	 * creates a send or a receive depending on the vertex type
	 * 
	 * Such a call makes sense if the sender vertex has a function call in the
	 * current code container and if the sender and function call uses the data
	 */
	@SuppressWarnings("unchecked")
	protected List<CommunicationFunctionCall> createCalls(
			AbstractBufferContainer parentContainer, SDFAbstractVertex vertex,
			CodeSectionType codeContainerType) {

		List<CommunicationFunctionCall> calls = new ArrayList<CommunicationFunctionCall>();

		// retrieving the vertex type
		VertexType type = (VertexType) vertex.getPropertyBean().getValue(
				ImplementationPropertyNames.Vertex_vertexType);

		AbstractRouteStep rs = (AbstractRouteStep) vertex.getPropertyBean()
				.getValue(ImplementationPropertyNames.SendReceive_routeStep);

		DirectedNeighborIndex<SDFAbstractVertex, SDFEdge> neighborindex = new DirectedNeighborIndex<SDFAbstractVertex, SDFEdge>(
				vertex.getBase());

		Set<SDFEdge> inEdges = (vertex.getBase().incomingEdgesOf(vertex));
		Set<SDFEdge> outEdges = (vertex.getBase().outgoingEdgesOf(vertex));
		List<SDFAbstractVertex> predList = neighborindex
				.predecessorListOf(vertex);
		List<SDFAbstractVertex> succList = neighborindex
				.successorListOf(vertex);

		if (type != null && rs != null) {
			if (type.isSend()) {
				SDFAbstractVertex senderVertex = (SDFAbstractVertex) (predList
						.get(0));
				if (hasCallForCodeContainerType(senderVertex, codeContainerType)
						|| VertexType.isIntermediateReceive(senderVertex)) {
					List<Buffer> bufferSet = parentContainer
							.getBuffers(inEdges);

					// The target is the operator on which the corresponding
					// receive operation is mapped
					SDFAbstractVertex receive = (SDFAbstractVertex) (succList
							.get(0));
					ComponentInstance target = (ComponentInstance) receive
							.getPropertyBean()
							.getValue(
									ImplementationPropertyNames.Vertex_Operator);

					// Case of one send for multiple buffers
					// calls.add(new Send(parentContainer, vertex, bufferSet,
					// medium,
					// target));

					// Case of one send per buffer
					for (Buffer buf : bufferSet) {
						if (SourceFileCodeGenerator
								.usesBufferInCodeContainerType(senderVertex,
										codeContainerType, buf, "output")
								|| VertexType.isIntermediateSend(vertex)) {
							List<Buffer> singleBufferSet = new ArrayList<Buffer>();
							singleBufferSet.add(buf);
							calls.add(new SendMsg(parentContainer, vertex,
									singleBufferSet, rs, target));
						}
					}
				}
			} else if (type.isReceive()) {
				SDFAbstractVertex send = (SDFAbstractVertex) (predList.get(0));
				SDFAbstractVertex senderVertex = (SDFAbstractVertex) (neighborindex
						.predecessorListOf(send).get(0));
				if (hasCallForCodeContainerType(senderVertex, codeContainerType)
						|| VertexType.isIntermediateReceive(senderVertex)) {
					List<Buffer> bufferSet = parentContainer
							.getBuffers(outEdges);

					// The source is the operator on which the corresponding
					// send
					// operation is allocated
					ComponentInstance source = (ComponentInstance) send
							.getPropertyBean()
							.getValue(
									ImplementationPropertyNames.Vertex_Operator);

					// Case of one receive for multiple buffers
					// calls.add(new Receive(parentContainer, vertex, bufferSet,
					// medium,
					// source));

					// Case of one receive per buffer
					for (Buffer buf : bufferSet) {
						if (SourceFileCodeGenerator
								.usesBufferInCodeContainerType(senderVertex,
										codeContainerType, buf, "output")
								|| VertexType
										.isIntermediateReceive(senderVertex)) {
							List<Buffer> singleBufferSet = new ArrayList<Buffer>();
							singleBufferSet.add(buf);
							calls.add(new ReceiveMsg(parentContainer, vertex,
									singleBufferSet, rs, source));
						}
					}
				}
			}
		}

		return calls;
	}
}
