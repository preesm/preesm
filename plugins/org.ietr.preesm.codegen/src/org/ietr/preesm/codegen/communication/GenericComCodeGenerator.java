/**
 * 
 */
package org.ietr.preesm.codegen.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.logging.Level;

import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;
import net.sf.dftools.algorithm.model.sdf.SDFEdge;
import net.sf.dftools.architecture.slam.ComponentInstance;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.ietr.preesm.codegen.model.ICodeGenSDFVertex;
import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;
import org.ietr.preesm.codegen.model.buffer.Buffer;
import org.ietr.preesm.codegen.model.com.CommunicationFunctionCall;
import org.ietr.preesm.codegen.model.com.CommunicationFunctionCall.Phase;
import org.ietr.preesm.codegen.model.com.ReceiveMsg;
import org.ietr.preesm.codegen.model.com.SendMsg;
import org.ietr.preesm.codegen.model.containers.AbstractCodeContainer;
import org.ietr.preesm.codegen.model.main.ICodeElement;
import org.ietr.preesm.codegen.model.main.SourceFile;
import org.ietr.preesm.codegen.model.main.SourceFileList;
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
	 * This class gathers communication function calls and existing calls that
	 * help insert them in existing code.
	 */
	public class ComCalls {
		/**
		 * Generated start communication calls
		 */
		protected List<CommunicationFunctionCall> startComZoneCalls = null;

		/**
		 * Generated end communication calls (for non-blocking communications)
		 */
		protected List<CommunicationFunctionCall> endComZoneCalls = null;

		/**
		 * Sender and receiver calls that make this communication necessary
		 */
		List<ICodeElement> receiverCalls = null;
		List<ICodeElement> senderCalls = null;

		/**
		 * Communication vertex corresponding to the calls
		 */
		SDFAbstractVertex vertex;

		public ComCalls(SDFAbstractVertex vertex) {
			super();
			this.startComZoneCalls = new ArrayList<CommunicationFunctionCall>();
			this.endComZoneCalls = new ArrayList<CommunicationFunctionCall>();
			this.senderCalls = new ArrayList<ICodeElement>();
			this.receiverCalls = new ArrayList<ICodeElement>();
			this.vertex = vertex;
		}

		public List<CommunicationFunctionCall> getStartComZoneCalls() {
			return startComZoneCalls;
		}

		public void addStartComZoneCall(
				CommunicationFunctionCall startComZoneCall) {
			this.startComZoneCalls.add(startComZoneCall);
		}

		public List<CommunicationFunctionCall> getEndComZoneCalls() {
			return endComZoneCalls;
		}

		public void addEndComZoneCall(CommunicationFunctionCall endComZoneCall) {
			this.endComZoneCalls.add(endComZoneCall);
		}

		public List<ICodeElement> getReceiverCalls() {
			return receiverCalls;
		}

		public void setReceiverCalls(List<ICodeElement> relativeCalls) {
			this.receiverCalls = relativeCalls;
		}

		public List<ICodeElement> getSenderCalls() {
			return senderCalls;
		}

		public void setSenderCalls(List<ICodeElement> senderCalls) {
			this.senderCalls = senderCalls;
		}

		public SDFAbstractVertex getVertex() {
			return vertex;
		}

		/**
		 * testing if calls are necessary
		 * @return true if the communication calls are necessary because used by both 
		 * sender and receiver code
		 */
		public boolean isNecessary() {
			return !getSenderCalls().isEmpty() && !getReceiverCalls().isEmpty();
		}
	}

	/**
	 * Code container where we want to add communication
	 */
	protected AbstractCodeContainer container = null;

	/**
	 * The considered communication vertices (send, receive)
	 */
	protected SortedSet<SDFAbstractVertex> vertices = null;

	/**
	 * The considered communication vertices (send, receive)
	 */
	protected AbstractRouteStep step = null;

	public GenericComCodeGenerator(AbstractCodeContainer container,
			SortedSet<SDFAbstractVertex> vertices, AbstractRouteStep step) {
		super();
		this.container = container;
		this.vertices = vertices;
		this.step = step;
	}

	/**
	 * Creating coms for a given communication vertex
	 */
	@Override
	public void insertComs(SDFAbstractVertex vertex,
			CodeSectionType sectionType, SourceFileList sourceFiles) {

		// Creating and adding the calls to send and receive functions

		// createCalls returns the computing call to which communication calls
		// must be synchronized (sender or receiver call).
		ComCalls comCalls = createComCalls(container, vertex, sectionType,
				sourceFiles);

		List<CommunicationFunctionCall> startComZoneCalls = comCalls
				.getStartComZoneCalls();
		List<CommunicationFunctionCall> endComZoneCalls = comCalls
				.getEndComZoneCalls();

		List<ICodeElement> relativeCalls = null;

		// retrieving the vertex type
		VertexType type = (VertexType) vertex.getPropertyBean().getValue(
				ImplementationPropertyNames.Vertex_vertexType);

		if (comCalls.isNecessary()) {
			if (type.isSend()) {
				relativeCalls = comCalls.getSenderCalls();
			} else if (type.isReceive()) {
				relativeCalls = comCalls.getReceiverCalls();
			}

			WorkflowLogger.getLogger().log(
					Level.INFO,
					"Relative computation call found for communication: "
							+ vertex.getName()
							+ ". Inserted communication call in section "
							+ sectionType);

		} else {

			WorkflowLogger.getLogger().log(
					Level.WARNING,
					"No relative computation call found for communication: "
							+ vertex.getName()
							+ ". No inserted communication call in section "
							+ sectionType);

			return;
		}

		for (int i = 0; i < startComZoneCalls.size(); i++) {
			CommunicationFunctionCall startCall = startComZoneCalls.get(i);
			CommunicationFunctionCall endCall = endComZoneCalls.get(i);
			// Normal case, the sender/receiver is a computing actor:
			// start is added after the last sender/receiver
			// but scheduling order is respected relative to other vertices

			int schedulingOrder = (Integer) vertex.getPropertyBean().getValue(
					ImplementationPropertyNames.Vertex_schedulingOrder);
			
			// Starting from last relative call and checking for calls with lower scheduling order
			ICodeElement previousElement = relativeCalls.get(relativeCalls.size() - 1);
			int position = container.getCodeElementPosition(previousElement);
			ICodeElement currentElement = previousElement;
			
			while(currentElement != null){
				SDFAbstractVertex currentVertex = currentElement.getCorrespondingVertex();
				int currentSchedulingOrder = (Integer) currentVertex.getPropertyBean().getValue(
						ImplementationPropertyNames.Vertex_schedulingOrder);
				
				if(currentSchedulingOrder <= schedulingOrder){
					previousElement = currentElement;
				}
				else break;

				position++;
				currentElement = container.getCodeElement(position);
			}
			
			container.addCodeElementAfter(previousElement, startCall);

			if (!VertexType.isIntermediateReceive(vertex)) {
				// Normal case, the sender/receiver is a computing actor:
				// end is added before the first sender/receiver

				// but scheduling order is respected relative to other vertices
				
				// Starting from first relative call and checking for calls with higher scheduling order
				ICodeElement nextElement = relativeCalls.get(0);
				/*position = container.getCodeElementPosition(nextElement);
				currentElement = nextElement;
				
				while(currentElement != null){
					SDFAbstractVertex currentVertex = currentElement.getCorrespondingVertex();
					int currentSchedulingOrder = (Integer) currentVertex.getPropertyBean().getValue(
							ImplementationPropertyNames.Vertex_schedulingOrder);
					
					if(currentSchedulingOrder > schedulingOrder){
						previousElement = currentElement;
					}
					else break;

					position--;
					currentElement = container.getCodeElement(position);
				}*/
				
				container.addCodeElementBefore(nextElement, endCall);
			} else {
				// If the vertex is an intermediate communication, end is
				// put
				// just after start
				container.addCodeElementAfter(startCall, endCall);
			}
		}

		// Adding initialization calls for the communication
		// createinits(startCall, compThread.getGlobalContainer(),
		// alreadyInits);

	}

	/**
	 * creates send calls
	 * 
	 * @return the code elements to which the communication must be synchronized
	 */
	@SuppressWarnings("unchecked")
	protected ComCalls createSendCalls(SDFAbstractVertex comVertex,
			AbstractBufferContainer parentContainer,
			CodeSectionType sectionType, SourceFileList sourceFiles) {

		ComCalls comCalls = new ComCalls(comVertex);

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

		// Getting relative sender calls to synchronize with
		List<SDFAbstractVertex> predList = neighborindex
				.predecessorListOf(comVertex);
		SDFAbstractVertex senderVertex = (SDFAbstractVertex) (predList.get(0));

		// The target is the operator on which the corresponding
		// receive operation is mapped
		List<SDFAbstractVertex> succList = neighborindex
				.successorListOf(comVertex);
		SDFAbstractVertex receive = (SDFAbstractVertex) (succList.get(0));
		ComponentInstance target = (ComponentInstance) receive
				.getPropertyBean().getValue(
						ImplementationPropertyNames.Vertex_Operator);

		succList = neighborindex.successorListOf(comVertex);
		SDFAbstractVertex receiveVertex = (SDFAbstractVertex) (succList.get(0));

		succList = neighborindex.successorListOf(receiveVertex);
		ICodeGenSDFVertex receiverVertex = (ICodeGenSDFVertex) (succList.get(0));

		SourceFile receiverFile = sourceFiles.get(receiverVertex.getOperator());

		// Checking if the relative receive generated code
		// in the equivalent section of another source. Otherwise, no com is
		// necessary
		List<ICodeElement> relativeReceiverCode = receiverFile.getContainer(
				sectionType)
				.getCodeElements((SDFAbstractVertex) receiverVertex);

		comCalls.setSenderCalls(container.getCodeElements(senderVertex));
		comCalls.setReceiverCalls(relativeReceiverCode);

		Set<SDFEdge> inEdges = (comVertex.getBase().incomingEdgesOf(comVertex));
		List<Buffer> bufferSet = parentContainer.getBuffers(inEdges);

		// Case of one send per buffer
		for (Buffer buf : bufferSet) {
			/*
			 * if (SourceFileCodeGenerator.isBufferUsedInCodeContainerType(
			 * senderVertex, sectionType, buf, "output") ||
			 * VertexType.isIntermediateSend(comVertex))
			 */{
				List<Buffer> singleBufferSet = new ArrayList<Buffer>();
				singleBufferSet.add(buf);

				int comId = container.getComNumber();

				comCalls.addStartComZoneCall(new SendMsg(parentContainer,
						comVertex, singleBufferSet, rs, target, comId,
						Phase.START));
				comCalls.addEndComZoneCall(new SendMsg(parentContainer,
						comVertex, singleBufferSet, rs, target, comId,
						Phase.END));
				container.incrementComNumber();
			}
		}

		return comCalls;
	}

	/**
	 * creates receive calls
	 * 
	 * @return the code elements to which the communication must be synchronized
	 */
	@SuppressWarnings("unchecked")
	protected ComCalls createReceiveCalls(SDFAbstractVertex comVertex,
			AbstractBufferContainer parentContainer,
			CodeSectionType sectionType, SourceFileList sourceFiles) {

		ComCalls comCalls = new ComCalls(comVertex);

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
		ICodeGenSDFVertex senderVertex = (ICodeGenSDFVertex) (neighborindex
				.predecessorListOf(send).get(0));

		SourceFile senderFile = sourceFiles.get(senderVertex.getOperator());

		// Checking if the relative receive generated code
		// in the equivalent section of another source. Otherwise, no com is
		// necessary
		List<ICodeElement> relativeSenderCode = senderFile.getContainer(
				sectionType).getCodeElements((SDFAbstractVertex) senderVertex);

		SDFAbstractVertex receiverVertex = (SDFAbstractVertex) (neighborindex
				.successorListOf(comVertex).get(0));

		comCalls.setSenderCalls(relativeSenderCode);
		comCalls.setReceiverCalls(container.getCodeElements(receiverVertex));

		Set<SDFEdge> outEdges = (comVertex.getBase().outgoingEdgesOf(comVertex));

		List<Buffer> bufferSet = parentContainer.getBuffers(outEdges);

		// The source is the operator on which the corresponding
		// send
		// operation is allocated
		ComponentInstance source = (ComponentInstance) send.getPropertyBean()
				.getValue(ImplementationPropertyNames.Vertex_Operator);

		// Case of one receive per buffer
		for (Buffer buf : bufferSet) {
			/*
			 * if (SourceFileCodeGenerator.isBufferUsedInCodeContainerType(
			 * senderVertex, sectionType, buf, "output") ||
			 * VertexType.isIntermediateReceive(senderVertex))
			 */{
				List<Buffer> singleBufferSet = new ArrayList<Buffer>();
				singleBufferSet.add(buf);

				int comId = container.getComNumber();

				comCalls.addStartComZoneCall(new ReceiveMsg(parentContainer,
						comVertex, singleBufferSet, rs, source, comId,
						Phase.START));
				comCalls.addEndComZoneCall(new ReceiveMsg(parentContainer,
						comVertex, singleBufferSet, rs, source, comId,
						Phase.END));
				container.incrementComNumber();
			}
		}

		return comCalls;
	}

	/**
	 * creates send or receive calls depending on the vertex type
	 * 
	 * @return the code elements to which the communication must be synchronized
	 */
	protected ComCalls createComCalls(AbstractBufferContainer parentContainer,
			SDFAbstractVertex comVertex, CodeSectionType sectionType,
			SourceFileList sourceFiles) {

		// retrieving the vertex type
		VertexType type = (VertexType) comVertex.getPropertyBean().getValue(
				ImplementationPropertyNames.Vertex_vertexType);

		if (type != null) {

			// Creating send calls
			if (type.isSend()) {

				return createSendCalls(comVertex, parentContainer, sectionType,
						sourceFiles);

				// Creating receive calls
			} else if (type.isReceive()) {

				return createReceiveCalls(comVertex, parentContainer,
						sectionType, sourceFiles);
			}
		}

		return null;

	}

}
