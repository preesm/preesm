/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.plugin.mapper.algo.genetic;

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
