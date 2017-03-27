/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

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

package org.ietr.preesm.core.types;

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;


/**
 * Represents the type of a vertex in its propertybeans
 * 
 * @author mpelcat
 */
public class VertexType {
	
	/**
	 * String used to qualify receive actors
	 */
	public static final String TYPE_RECEIVE = "receive";
	/**
	 * String used to qualify send actors
	 */
	public static final String TYPE_SEND = "send";
	/**
	 * String used to qualify task actors
	 */
	public static final String TYPE_TASK = "task";

	/**
	 * VertexType representing a receive operation
	 */
	public static final VertexType RECEIVE = new VertexType(TYPE_RECEIVE);

	/**
	 * VertexType representing a send operation
	 */
	public static final VertexType SEND = new VertexType(TYPE_SEND);

	/**
	 * VertexType representing a task
	 */
	public static final VertexType TASK = new VertexType(TYPE_TASK);

	/**
	 * Returns true if this receive operation leads to a send operation
	 */
	static public boolean isIntermediateReceive(SDFAbstractVertex vertex) {

		VertexType vType = (VertexType) vertex.getPropertyBean().getValue(
				ImplementationPropertyNames.Vertex_vertexType);

		// If the communication operation is an intermediate step of a route
		if (vType.isReceive()) {
			for (SDFEdge outEdge : ((SDFGraph) vertex.getBase())
					.outgoingEdgesOf(vertex)) {

				VertexType nextVType = (VertexType) outEdge
						.getTarget()
						.getPropertyBean()
						.getValue(ImplementationPropertyNames.Vertex_vertexType);

				if (nextVType.isSend()) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Returns true if this send operation follows a receive operation
	 */
	static public boolean isIntermediateSend(SDFAbstractVertex vertex) {

		VertexType vType = (VertexType) vertex.getPropertyBean().getValue(
				ImplementationPropertyNames.Vertex_vertexType);

		// If the communication operation is an intermediate step of a route
		if (vType.isSend()) {
			SDFEdge inEdge = (SDFEdge) (((SDFGraph) vertex.getBase())
					.incomingEdgesOf(vertex).toArray()[0]);
			VertexType prevVType = (VertexType) inEdge.getSource()
					.getPropertyBean()
					.getValue(ImplementationPropertyNames.Vertex_vertexType);

			if (prevVType.isReceive())
				return true;
		}

		return false;
	}

	/**
	 * VertexType representing a task
	 */
	private String type = "";

	private VertexType(String type) {
		super();
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof VertexType) {
			return (((VertexType) obj).type.equals(type));
		}
		return false;
	}

	public boolean isReceive() {
		return (this == RECEIVE);
	}

	public boolean isSend() {
		return (this == SEND);
	}

	public boolean isTask() {
		return (this == TASK);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return type;
	}

}
