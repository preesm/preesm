/**
 * 
 */
package org.ietr.preesm.core.codegen;

import org.ietr.preesm.core.architecture.simplemodel.Operator;

/**
 * This class contains property bean names used to exchange vertex properties
 * between the mapper and the code generation plugins.
 * 
 * @author mpelcat
 */
public final class ImplementationPropertyNames {

	// Graph properties
	/**
	 * See {@link AbcType} for available ABC types.
	 */
	public static final String Graph_AbcReferenceType = "AbcReferenceType";
	
	/**
	 * See {@link EdgeSchedType} for available edge scheduling types.
	 */
	public static final String Graph_EdgeSchedReferenceType = "EdgeSchedReferenceType";

	/**
	 * SDFGraph object containing the reference graph for this dag graph.
	 */
	public static final String Graph_SdfReferenceGraph = "SdfReferenceGraph";

	// Vertex properties
	/**
	 * See {@link VertexType} for available vertex types.
	 */
	public static final String Vertex_vertexType = "vertexType";
	
	/**
	 * {@link Operator} on which the vertex is mapped.
	 */
	public static final String Vertex_Operator = "Operator";
	
	/**
	 * integer equal to the total order of the vertex.
	 */
	public static final String Vertex_schedulingOrder = "schedulingOrder";

	// Vertex of type task properties
	/**
	 * integer equal to duration of the task.
	 */
	public static final String Task_duration = "duration";

	// Vertex of type send or receive properties
	/**
	 * Medium transfering the data.
	 */
	public static final String SendReceive_medium = "medium";
	
	/**
	 * integer with the data size.
	 */
	public static final String SendReceive_dataSize = "dataSize";
	
	/**
	 * String with the operator address.
	 */
	public static final String SendReceive_Operator_address = "Operator_address";

	// Vertex of type send properties
	/**
	 * Name of the sender vertex.
	 */
	public static final String Send_senderGraphName = "senderGraphName";
	
	// Vertex of type receive properties
	/**
	 * Name of the receiver vertex.
	 */
	public static final String Receive_receiverGraphName = "receiverGraphName";
}
