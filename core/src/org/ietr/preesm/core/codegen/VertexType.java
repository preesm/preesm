/**
 * 
 */
package org.ietr.preesm.core.codegen;

import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFEdge;

/**
 * Represents the type of a vertex in its propertybeans
 * 
 * @author mpelcat
 */
public class VertexType {


	/**
	 * ID used to reference the element in a property bean 
	 */
	public static final String propertyBeanName = "vertexType";

	/**
	 * VertexType representing a receive operation
	 */
	public static final VertexType receive = new VertexType("receive");
	
	/**
	 * VertexType representing a send operation
	 */
	public static final VertexType send = new VertexType("send");

	/**
	 * VertexType representing a task
	 */
	public static final VertexType task = new VertexType("task");

	/**
	 * Returns true if this receive operation leads to a send operation
	 */
	static public boolean isIntermediateReceive(DAGVertex vertex){
		
		VertexType vType = (VertexType)vertex.getPropertyBean().getValue(VertexType.propertyBeanName);
		
		// If the communication operation is an intermediate step of a route
		if(vType.isReceive()){
			DAGEdge outEdge = (DAGEdge)(vertex.getBase().outgoingEdgesOf(vertex).toArray()[0]);
			VertexType nextVType = (VertexType)outEdge.getTarget().getPropertyBean().getValue(VertexType.propertyBeanName);
			
			if(nextVType.isSend())
				return true;
		}
		
		return false;
	}
	
	/**
	 * Returns true if this send operation follows a receive operation
	 */
	static public boolean isIntermediateSend(DAGVertex vertex){
		
		VertexType vType = (VertexType)vertex.getPropertyBean().getValue(VertexType.propertyBeanName);
		
		// If the communication operation is an intermediate step of a route
		if(vType.isSend()){
			DAGEdge inEdge = (DAGEdge)(vertex.getBase().incomingEdgesOf(vertex).toArray()[0]);
			VertexType prevVType = (VertexType)inEdge.getSource().getPropertyBean().getValue(VertexType.propertyBeanName);
			
			if(prevVType.isReceive())
				return true;
		}
		
		return false;
	}

	private VertexType(String type) {
		super();
	}

	public boolean isReceive(){
		return (this == receive);
	}
	
	public boolean isSend(){
		return (this == send);
	}
	
	public boolean isTask(){
		return (this == task);
	}
}
