/**
 * 
 */
package org.ietr.preesm.core.codegen;

/**
 * This class contains property bean names used to exchange vertex properties
 * between the mapper and the code generation plugins.
 * 
 * @author mpelcat
 */
public final class ImplementationPropertyNames {

	public static final String Graph_AbcReferenceType = "AbcReferenceType";
	public static final String Graph_EdgeSchedReferenceType = "EdgeSchedReferenceType";
	public static final String Graph_SdfReferenceGraph = "SdfReferenceGraph";
	
	public static final String Vertex_vertexType = "vertexType";
	public static final String Vertex_Operator = "Operator";
	public static final String Vertex_schedulingOrder = "schedulingOrder";

	public static final String Task_duration = "duration";
	
	public static final String SendReceive_medium = "medium";
	public static final String SendReceive_dataSize = "dataSize";
	public static final String SendReceive_Operator_address = "Operator_address";
	
	public static final String Send_senderGraphName = "senderGraphName";
	public static final String Receive_receiverGraphName = "receiverGraphName";
}
