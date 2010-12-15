package org.ietr.preesm.plugin.codegen.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.ietr.preesm.core.architecture.IOperator;
import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.com.CommunicationFunctionCall;
import org.ietr.preesm.core.codegen.com.CommunicationFunctionInit;
import org.ietr.preesm.core.codegen.com.ReceiveInit;
import org.ietr.preesm.core.codegen.com.ReceiveMsg;
import org.ietr.preesm.core.codegen.com.SendInit;
import org.ietr.preesm.core.codegen.com.SendMsg;
import org.ietr.preesm.core.codegen.com.WaitForCore;
import org.ietr.preesm.core.codegen.model.VertexType;
import org.ietr.preesm.core.codegen.threads.CommunicationThreadDeclaration;
import org.ietr.preesm.core.codegen.threads.ComputationThreadDeclaration;
import org.ietr.preesm.core.codegen.types.CodeSectionType;
import org.ietr.preesm.plugin.codegen.SourceFileCodeGenerator;
import org.jgrapht.alg.DirectedNeighborIndex;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;

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

			init = new SendInit(bufferContainer, send.getTarget().getName(),
					send.getRouteStep(), -1);
			wait = new WaitForCore(bufferContainer, send.getRouteStep());
		} else if (call instanceof ReceiveMsg) {
			ReceiveMsg receive = (ReceiveMsg) call;

			init = new ReceiveInit(bufferContainer, receive.getSource()
					.getName(), receive.getRouteStep(), -1);
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
					IOperator target = (IOperator) receive
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
					IOperator source = (IOperator) send
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
