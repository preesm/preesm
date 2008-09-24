/**
 * 
 */
package org.ietr.preesm.plugin.mapper.geneticalgo;

import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * One gene in a chromosome
 * 
 * @author pmenuet
 */

public class Gene {

	// Name of the vertex which is represented by this gene
	private String VertexName;

	// Name of the operator on which the vertex represented by the gene is
	// implemented
	private String OperatorId;

	/**
	 * Constructor
	 */
	public Gene() {
		super();
		VertexName = null;
		OperatorId = null;
	}

	/**
	 * Constructor
	 * 
	 * @param vertexId
	 * @param operatorId
	 */
	public Gene(String vertexName, String operatorId) {
		super();
		VertexName = vertexName;
		OperatorId = operatorId;
	}

	/**
	 * Constructor based on a vertex
	 * 
	 * @param MapperDAGVertex
	 * 
	 */
	public Gene(MapperDAGVertex vertex) {
		super();
		VertexName = vertex.getName();
		OperatorId = vertex.getImplementationVertexProperty()
				.getEffectiveComponent().getName();
	}

	/**
	 * clone : Clone the gene
	 * 
	 * @param : void
	 * 
	 * @return : Gene
	 */
	public Gene clone() {
		Gene gene = new Gene(this.getVertexName(), this.getOperatorId());
		return gene;
	}

	/**
	 * toString
	 * 
	 * @param : void
	 * 
	 * @return : String
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "(" + VertexName + " , " + OperatorId + ")";
	}

	/**
	 * Getters and setters
	 * 
	 */
	public String getVertexName() {
		return VertexName;
	}

	public void setVertexName(String vertexId) {
		VertexName = vertexId;
	}

	public String getOperatorId() {
		return OperatorId;
	}

	public void setOperatorId(String operatorId) {
		OperatorId = operatorId;
	}

	/**
	 * isIncomplete : return true if an attribute of the gene is null.
	 * 
	 * @param : void
	 * 
	 * @return : Boolean
	 */
	public boolean isIncomplete() {

		if (VertexName == null || OperatorId == null)
			return true;
		return false;
	}

}
