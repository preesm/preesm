package org.ietr.preesm.plugin.codegen.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.codegen.ImplementationPropertyNames;
import org.ietr.preesm.core.codegen.buffer.AbstractBufferContainer;
import org.ietr.preesm.core.codegen.buffer.Buffer;
import org.ietr.preesm.core.codegen.buffer.BufferAllocation;
import org.ietr.preesm.core.codegen.com.CommunicationFunctionCall;
import org.ietr.preesm.core.codegen.com.CommunicationFunctionInit;
import org.ietr.preesm.core.codegen.com.ReceiveAddress;
import org.ietr.preesm.core.codegen.com.ReceiveDma;
import org.ietr.preesm.core.codegen.com.ReceiveInit;
import org.ietr.preesm.core.codegen.com.SendAddress;
import org.ietr.preesm.core.codegen.com.SendDma;
import org.ietr.preesm.core.codegen.com.SendInit;
import org.ietr.preesm.core.codegen.com.WaitForCore;
import org.ietr.preesm.core.codegen.containers.LinearCodeContainer;
import org.ietr.preesm.core.codegen.model.CodeGenSDFSendVertex;
import org.ietr.preesm.core.codegen.model.VertexType;
import org.ietr.preesm.core.codegen.threads.CommunicationThreadDeclaration;
import org.ietr.preesm.core.codegen.threads.ComputationThreadDeclaration;
import org.ietr.preesm.core.codegen.types.CodeSectionType;
import org.ietr.preesm.core.codegen.types.DataType;
import org.ietr.preesm.plugin.codegen.SourceFileCodeGenerator;
import org.jgrapht.alg.DirectedNeighborIndex;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;

/**
 * Generating communication code (initialization and calls) for a dma Route Step
 * 
 * @author mpelcat
 */
public class DmaComCodeGenerator extends AbstractComCodeGenerator {

	/**
	 * Buffer containing all the addresses of the distant core where to send the
	 * data
	 */
	private Buffer addressBuffer = null;

	private LinearCodeContainer dmaInitCodeContainer = null;

	/**
	 * Index of the communication call among the communication calls with same
	 * route step and direction
	 */
	int sendNextCallIndex = 0;
	int receiveNextCallIndex = 0;

	@SuppressWarnings("unchecked")
	public DmaComCodeGenerator(ComputationThreadDeclaration compThread,
			CommunicationThreadDeclaration comThread,
			SortedSet<SDFAbstractVertex> vertices, AbstractRouteStep step) {
		super(compThread, comThread, vertices, step);

		dmaInitCodeContainer = new LinearCodeContainer(
				this.comThread.getBeginningCode());
		this.comThread.getBeginningCode().addCodeElement(dmaInitCodeContainer);

		// Testing the number of send functions with the given route step
		int addressBufferSize = 0;
		for (SDFAbstractVertex v : vertices) {
			if (v instanceof CodeGenSDFSendVertex) {

				CodeGenSDFSendVertex send = (CodeGenSDFSendVertex) v;
				AbstractRouteStep vStep = (AbstractRouteStep) send
						.getPropertyBean()
						.getValue(
								ImplementationPropertyNames.SendReceive_routeStep);

				if (vStep.equals(step)) {

					Set<SDFEdge> inEdges = send.getBase().incomingEdgesOf(send);
					List<Buffer> bufferSet = comThread.getGlobalContainer()
							.getBuffers(inEdges);

					addressBufferSize += bufferSet.size();
				}
			}
		}

		// Adding the address buffer where the distant addresses of the sending
		// functions are stored in the order of send functions
		if (addressBufferSize != 0) {
			addressBuffer = new Buffer("addressBuffer", addressBufferSize,
					new DataType("void *", 4), null,
					comThread.getGlobalContainer());
			BufferAllocation alloc = new BufferAllocation(addressBuffer);
			comThread.getGlobalContainer().addBuffer(alloc);
		}
	}

	/**
	 * Calls the initialization functions at the beginning of computation and
	 * communication thread executions
	 */
	protected void createinits(CommunicationFunctionCall call,
			AbstractBufferContainer bufferContainer,
			Set<CommunicationFunctionInit> alreadyInits) {

		CommunicationFunctionInit initCom = null;
		WaitForCore wait = null;
		CommunicationFunctionInit initAddress = null;

		// Creating Send and Receive initialization calls
		if (call instanceof SendDma) {
			SendDma send = (SendDma) call;

			initCom = new SendInit(bufferContainer, send.getTarget().getName(),
					send.getRouteStep(), send.getCallIndex());
			wait = new WaitForCore(bufferContainer, send.getRouteStep());

			initAddress = new ReceiveAddress(bufferContainer, send
					.getRouteStep().getReceiver().getName(),
					send.getRouteStep(), send.getCallIndex(), addressBuffer);

		} else if (call instanceof ReceiveDma) {
			ReceiveDma receive = (ReceiveDma) call;

			initCom = new ReceiveInit(bufferContainer, receive.getSource()
					.getName(), receive.getRouteStep(), receive.getCallIndex());
			wait = new WaitForCore(bufferContainer, receive.getRouteStep());

			initAddress = new SendAddress(bufferContainer, receive
					.getRouteStep().getSender().getName(),
					receive.getRouteStep(), receive.getCallIndex(), receive
							.getBufferSet().get(0));
		}

		// Checking that the initialization has not already been done
		if (initCom != null) {
			for (CommunicationFunctionInit oldInit : alreadyInits) {
				if (oldInit.getConnectedCoreId().equals(
						initCom.getConnectedCoreId())
						&& oldInit.getRouteStep()
								.equals(initCom.getRouteStep())) {
					// core wait has already been done
					wait = null;

					if (oldInit.getName().equals(initCom.getName())) {
						// init has already been done with same direction
						initCom = null;
					}
					break;
				}
			}
		}

		// Adding Send and Receive initialization calls
		if (initCom != null) {
			dmaInitCodeContainer.addCodeElementFirst(initCom);
			alreadyInits.add(initCom);
		}

		// Adding other cores wait
		if (wait != null) {
			dmaInitCodeContainer.addCodeElement(wait);
		}

		// Adding other cores wait
		if (initAddress != null) {
			dmaInitCodeContainer.addCodeElement(initAddress);
		}

	}

	/**
	 * Creates a send or a receive call depending on the vertex type
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
					// receive
					// operation is mapped
					SDFAbstractVertex receive = (SDFAbstractVertex) (succList
							.get(0));

					Operator target = (Operator) receive
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
							calls.add(new SendDma(parentContainer, vertex,
									singleBufferSet, rs, target,
									sendNextCallIndex, addressBuffer));
							sendNextCallIndex++;
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
					Operator source = (Operator) send
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
							calls.add(new ReceiveDma(parentContainer, vertex,
									singleBufferSet, rs, source,
									receiveNextCallIndex));
							receiveNextCallIndex++;
						}
					}
				}
			}
		}

		return calls;
	}
}
