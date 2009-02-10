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

package org.ietr.preesm.core.scenario;

import org.ietr.preesm.core.architecture.IOperatorDefinition;
import org.ietr.preesm.core.expression.Parameter;
import org.nfunk.jep.Variable;
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * @author mpelcat
 */
public class Timing {

	public static final Timing UNAVAILABLE = null;
	public static final int DEFAULT_TASK_TIME = 100;
	public static final int DEFAULT_BROADCAST_TIME = 10;
	public static final int DEFAULT_FORK_TIME = 10;
	public static final int DEFAULT_JOIN_TIME = 10;
	public static final int DEFAULT_INIT_TIME = 10;

	public static void main(String[] args) {
		Variable test = new Parameter("test");
		test.setValue(6546);
		System.out.println(test);
	}

	/**
	 * related operator
	 */
	private IOperatorDefinition operator;

	/**
	 * Definition of the timing
	 */
	private int time;

	/**
	 * related Graph
	 */
	private SDFAbstractVertex vertex;

	public Timing(IOperatorDefinition operator, SDFAbstractVertex vertex) {

		time = DEFAULT_TASK_TIME;
		this.operator = operator;
		this.vertex = vertex;
	}

	public Timing(IOperatorDefinition operator, SDFAbstractVertex vertex,
			int time) {
		this(operator, vertex);
		this.time = time;
	}

	@Override
	public boolean equals(Object obj) {

		boolean equals = false;

		if (obj instanceof Timing) {
			Timing otherT = (Timing) obj;
			equals = operator.equals(otherT.getOperatorDefinition());
			equals &= vertex.getName().equals(
					(otherT.getVertex().getName()));
		}

		return equals;
	}

	public IOperatorDefinition getOperatorDefinition() {
		return operator;
	}

	public int getTime() {

		return time;
	}

	public SDFAbstractVertex getVertex() {
		return vertex;
	}

	public void setTime(int time) {

		this.time = time;
	}

}
