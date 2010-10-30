/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
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
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/
 
package org.ietr.preesm.core.codegen;

import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.codegen.model.VertexType;

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
	 * {@link Operator} on which the vertex can be mapped.
	 */
	public static final String Vertex_Available_Operators = "availableOperators";
	
	/**
	 * {@link OperatorDefinition} of the vertex operator.
	 */
	public static final String Vertex_OperatorDef = "OperatorDef";
	
	/**
	 * integer equal to the total order of the vertex.
	 */
	public static final String Vertex_schedulingOrder = "schedulingOrder";

	// Vertex of type task properties
	/**
	 * integer equal to duration of the task.
	 */
	public static final String Task_duration = "duration";

	/**
	 * Id of the original vertex with no hierarchy info.
	 */
	public static final String Vertex_originalVertexId = "originalId";
	
	// Vertex of type send or receive properties
	/**
	 * Route step transfering the data.
	 */
	public static final String SendReceive_routeStep = "routeStep";
	
	/**
	 * integer with the data size.
	 */
	public static final String SendReceive_dataSize = "dataSize";
	
	/**
	 * String with the operator address.
	 */
	public static final String SendReceive_Operator_address = "Operator_address";

	/**
	 * {@link OperatorDefinition} of the operator executing the send or receive.
	 */
	public static final String SendReceive_OperatorDef = "OperatorDef";
	
	
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
