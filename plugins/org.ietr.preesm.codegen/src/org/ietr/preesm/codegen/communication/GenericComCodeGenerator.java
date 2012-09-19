/**
 * 
 */
package org.ietr.preesm.codegen.communication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.logging.Level;

import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.algorithm.model.sdf.SDFEdge;
import net.sf.dftools.architecture.slam.ComponentInstance;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;
import org.ietr.preesm.codegen.model.buffer.Buffer;
import org.ietr.preesm.codegen.model.com.CommunicationFunctionCall;
import org.ietr.preesm.codegen.model.com.CommunicationFunctionCall.Phase;
import org.ietr.preesm.codegen.model.com.CommunicationFunctionInit;
import org.ietr.preesm.codegen.model.com.ReceiveInit;
import org.ietr.preesm.codegen.model.com.ReceiveMsg;
import org.ietr.preesm.codegen.model.com.SendInit;
import org.ietr.preesm.codegen.model.com.SendMsg;
import org.ietr.preesm.codegen.model.com.WaitForCore;
import org.ietr.preesm.codegen.model.containers.AbstractCodeContainer;
import org.ietr.preesm.codegen.model.main.ICodeElement;
import org.ietr.preesm.codegen.model.types.CodeSectionType;
import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.types.ImplementationPropertyNames;
import org.ietr.preesm.core.types.VertexType;
import org.jgrapht.alg.DirectedNeighborIndex;

/**
 * Generating communication code (initialization and calls) for a given type of
 * Route Step The abstract class gathers the common commands
 * 
 * @author mpelcat
 */
public class GenericComCodeGenerator implements IComCodeGenerator {

	/**
	 * Code container where we want to add communication
	 */
	protected AbstractCodeContainer container = null;

	/**
	 * Initializing the Send and Receive channels only for the channels really
	 * used and only once per channel
	 */
	protected Set<CommunicationFunctionInit> alreadyInits = null;

	/**
	 * The considered communication vertices (send, receive)
	 */
	protected SortedSet<SDFAbstractVertex> vertices = null;

	/**
	 * The considered communication vertices (send, receive)
	 */
	protected AbstractRouteStep step = null;

	/**
	 * Generated start communication calls
	 */
	protected List<CommunicationFunctionCall> startComZoneCalls = null;

	/**
	 * Generated end communication calls
	 */
	protected List<CommunicationFunctionCall> endComZoneCalls = null;

	public GenericComCodeGenerator(AbstractCodeContainer container,
			SortedSet<SDFAbstractVertex> vertices, AbstractRouteStep step) {
		super();
		this.container = container;
		this.alreadyInits = new HashSet<CommunicationFunctionInit>();
		this.vertices = vertices;
		this.step = step;

		this.startComZoneCalls = new ArrayList<CommunicationFunctionCall>();
		this.endComZoneCalls = new ArrayList<CommunicationFunctionCall>();
	}

	/**
	 * Creating coms for a given communication vertex
	 */
	@Override
	public void insertComs(SDFAbstractVertex vertex, CodeSectionType sectionType) {

		// Creating and adding the calls to send and receive functions

		// createCalls returns the computing call to which communication calls
		// must be synchronized (sender or receiver call).
		List<ICodeElement> relativeCalls = createCalls(container, vertex,
				sectionType);

		for (int i = 0; i < startComZoneCalls.size(); i++) {
			CommunicationFunctionCall startCall = startComZoneCalls.get(i);
			CommunicationFunctionCall endCall = endComZoneCalls.get(i);
			// Normal case, the sender/receiver is a computing actor:
			// start is added after the last sender/receiver

			if (relativeCalls.size() > 0) {
				container.addCodeElementAfter(
						relativeCalls.get(relativeCalls.size() - 1), startCall);

				if (!VertexType.isIntermediateReceive(vertex)) {
					// Normal case, the sender/receiver is a computing actor:
					// end is added before the first sender/receiver
					container.addCodeElementBefore(relativeCalls.get(0),
							endCall);
				} else {
					// If the vertex is an intermediate communication, end is
					// put
					// just after start
					container.addCodeElementAfter(startCall, endCall);
				}
			}
		}

		// Adding initialization calls for the communication
		// createinits(startCall, compThread.getGlobalContainer(),
		// alreadyInits);

	}

	/**
	 * Calls the initialization functions at the beginning of computation
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
		/*if (init != null) {
			container.getBeginningCode().addCodeElementFirst(init);
			alreadyInits.add(init);
		}

		// Adding other cores wait
		if (wait != null) {
			container.getBeginningCode().addCodeElement(wait);
		}*/

	}

	/**
	 * creates send calls
	 * 
	 * @return the code elements to which the communication must be synchronized
	 */
	@SuppressWarnings("unchecked")
	protected List<ICodeElement> createSendCalls(SDFAbstractVertex comVertex,
			AbstractBufferContainer parentContainer, CodeSectionType sectionType) {

		// Retrieving the communication route step
		AbstractRouteStep rs = (AbstractRouteStep) comVertex.getPropertyBean()
				.getValue(ImplementationPropertyNames.SendReceive_routeStep);

		if (rs == null) {
			WorkflowLogger.getLogger()
					.log(Level.WARNING,
							"Route step is null for send vertex "
									+ comVertex.getName());
			return null;
		}

		DirectedNeighborIndex<SDFAbstractVertex, SDFEdge> neighborindex = new DirectedNeighborIndex<SDFAbstractVertex, SDFEdge>(
				comVertex.getBase());

		List<SDFAbstractVertex> predList = neighborindex
				.predecessorListOf(comVertex);
		List<SDFAbstractVertex> succList = neighborindex
				.successorListOf(comVertex);

		SDFAbstractVertex senderVertex = (SDFAbstractVertex) (predList.get(0));

		Set<SDFEdge> inEdges = (comVertex.getBase().incomingEdgesOf(comVertex));

		List<ICodeElement> relativeSenderCalls = container
				.getCodeElements(senderVertex);

		List<Buffer> bufferSet = parentContainer.getBuffers(inEdges);

		// The target is the operator on which the corresponding
		// receive operation is mapped
		SDFAbstractVertex receive = (SDFAbstractVertex) (succList.get(0));
		ComponentInstance target = (ComponentInstance) receive
				.getPropertyBean().getValue(
						ImplementationPropertyNames.Vertex_Operator);

		// Case of one send per buffer
		for (Buffer buf : bufferSet) {
			/*if (SourceFileCodeGenerator.isBufferUsedInCodeContainerType(
					senderVertex, sectionType, buf, "output")
					|| VertexType.isIntermediateSend(comVertex))*/ {
				List<Buffer> singleBufferSet = new ArrayList<Buffer>();
				singleBufferSet.add(buf);

				int comId = container.getComNumber();

				startComZoneCalls.add(new SendMsg(parentContainer, comVertex,
						singleBufferSet, rs, target, comId, Phase.START));
				endComZoneCalls.add(new SendMsg(parentContainer, comVertex,
						singleBufferSet, rs, target, comId, Phase.END));
				container.incrementComNumber();
			}
		}

		return relativeSenderCalls;
	}

	/**
	 * creates receive calls
	 * 
	 * @return the code elements to which the communication must be synchronized
	 */
	@SuppressWarnings("unchecked")
	protected List<ICodeElement> createReceiveCalls(
			SDFAbstractVertex comVertex, AbstractBufferContainer parentContainer, CodeSectionType sectionType) {

		// Retrieving the communication route step
		AbstractRouteStep rs = (AbstractRouteStep) comVertex.getPropertyBean()
				.getValue(ImplementationPropertyNames.SendReceive_routeStep);

		if (rs == null) {
			WorkflowLogger.getLogger().log(
					Level.WARNING,
					"Route step is null for receive vertex "
							+ comVertex.getName());
			return null;
		}

		DirectedNeighborIndex<SDFAbstractVertex, SDFEdge> neighborindex = new DirectedNeighborIndex<SDFAbstractVertex, SDFEdge>(
				comVertex.getBase());

		List<SDFAbstractVertex> predList = neighborindex
				.predecessorListOf(comVertex);

		SDFAbstractVertex send = (SDFAbstractVertex) (predList.get(0));
		SDFAbstractVertex senderVertex = (SDFAbstractVertex) (neighborindex
				.predecessorListOf(send).get(0));
		SDFAbstractVertex receiverVertex = (SDFAbstractVertex) (neighborindex
				.successorListOf(comVertex).get(0));

		Set<SDFEdge> outEdges = (comVertex.getBase().outgoingEdgesOf(comVertex));

		List<ICodeElement> relativeReceiverCalls = container
				.getCodeElements(receiverVertex);

		List<Buffer> bufferSet = parentContainer.getBuffers(outEdges);

		// The source is the operator on which the corresponding
		// send
		// operation is allocated
		ComponentInstance source = (ComponentInstance) send.getPropertyBean()
				.getValue(ImplementationPropertyNames.Vertex_Operator);

		// Case of one receive per buffer
		for (Buffer buf : bufferSet) {
			/*if (SourceFileCodeGenerator.isBufferUsedInCodeContainerType(
					senderVertex, sectionType, buf, "output")
					|| VertexType.isIntermediateReceive(senderVertex))*/ {
				List<Buffer> singleBufferSet = new ArrayList<Buffer>();
				singleBufferSet.add(buf);

				int comId = container.getComNumber();

				startComZoneCalls.add(new ReceiveMsg(parentContainer,
						comVertex, singleBufferSet, rs, source, comId,
						Phase.START));
				endComZoneCalls.add(new ReceiveMsg(parentContainer, comVertex,
						singleBufferSet, rs, source, comId, Phase.END));
				container.incrementComNumber();
			}
		}

		return relativeReceiverCalls;
	}

	/**
	 * creates send or receive calls depending on the vertex type
	 * 
	 * @return the code elements to which the communication must be synchronized
	 */
	protected List<ICodeElement> createCalls(
			AbstractBufferContainer parentContainer,
			SDFAbstractVertex comVertex, CodeSectionType sectionType) {

		// Clearing previously created calls
		startComZoneCalls.clear();
		endComZoneCalls.clear();

		// retrieving the vertex type
		VertexType type = (VertexType) comVertex.getPropertyBean().getValue(
				ImplementationPropertyNames.Vertex_vertexType);

		if (type != null) {

			// Creating send calls
			if (type.isSend()) {

				return createSendCalls(comVertex, parentContainer, sectionType);

				// Creating receive calls
			} else if (type.isReceive()) {

				return createReceiveCalls(comVertex, parentContainer, sectionType);
			}
		}

		return null;

	}

}
