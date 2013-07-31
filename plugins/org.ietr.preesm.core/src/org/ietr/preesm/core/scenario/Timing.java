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

package org.ietr.preesm.core.scenario;

/**
 * A timing links a SDF vertex and an operator definition to a time. Ids are
 * used to make the scenario independent from model implementations.
 * 
 * @author mpelcat
 */
public class Timing {

	public static final Timing UNAVAILABLE = null;
	public static final int DEFAULT_TASK_TIME = 100;
	public static final int DEFAULT_BROADCAST_TIME = 10;
	public static final int DEFAULT_FORK_TIME = 10;
	public static final int DEFAULT_JOIN_TIME = 10;
	public static final int DEFAULT_INIT_TIME = 10;
	public static final int DEFAULT_END_TIME = 10;

	/**
	 * related operator
	 */
	private String operatorDefinitionId;

	/**
	 * Definition of the timing
	 */
	private int time;

	/**
	 * related Graph
	 */
	private String sdfVertexId;

	public Timing(String operatorDefinitionId, String sdfVertexId) {

		time = DEFAULT_TASK_TIME;
		this.operatorDefinitionId = operatorDefinitionId;
		this.sdfVertexId = sdfVertexId;
	}

	public Timing(String operatorId, String sdfVertexId, int time) {
		this(operatorId, sdfVertexId);
		this.time = time;
	}

	@Override
	public boolean equals(Object obj) {

		boolean equals = false;

		if (obj instanceof Timing) {
			Timing otherT = (Timing) obj;
			equals = operatorDefinitionId.equals(otherT
					.getOperatorDefinitionId());
			equals &= sdfVertexId.equals((otherT.getSdfVertexId()));
		}

		return equals;
	}

	public String getOperatorDefinitionId() {
		return operatorDefinitionId;
	}

	public int getTime() {

		return time;
	}

	public String getSdfVertexId() {
		return sdfVertexId;
	}

	public void setTime(int time) {

		this.time = time;
	}

	@Override
	public String toString() {
		return "{" + sdfVertexId + "," + operatorDefinitionId + "," + time
				+ "}";
	}

}
