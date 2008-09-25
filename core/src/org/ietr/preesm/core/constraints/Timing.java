package org.ietr.preesm.core.constraints;

import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.ietr.preesm.core.expression.Parameter;
import org.ietr.preesm.core.log.PreesmLogger;
import org.nfunk.jep.Variable;
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * @author mpelcat
 */
public class Timing {

	public static final Timing UNAVAILABLE = null;
	public static final int DEFAULTTASKTIME = 100;

	public static void main(String[] args) {
		Variable test = new Parameter("test");
		test.setValue(6546);
		System.out.println(test);
	}

	/**
	 * related operator
	 */
	private OperatorDefinition operator;

	/**
	 * Definition of the timing
	 */
	private int parameter;

	/**
	 * related Graph
	 */
	private SDFAbstractVertex vertex;

	public Timing(OperatorDefinition operator, SDFAbstractVertex vertex) {

		try {
			parameter = -1;
		} catch (Exception e) {
			PreesmLogger.getLogger().severe(e.getMessage());
		}
		this.operator = operator;
		this.vertex = vertex;
	}

	public Timing(OperatorDefinition operator, SDFAbstractVertex vertex,
			int time) {
		this(operator, vertex);
		parameter = (time);
	}

	@Override
	public boolean equals(Object obj) {

		boolean equals = false;

		if (obj instanceof Timing) {
			Timing otherT = (Timing) obj;
			equals = operator.equals(otherT.getOperatorDefinition());
			equals &= vertex.getName().equalsIgnoreCase(
					(otherT.getVertex().getName()));
		}

		return equals;
	}

	public OperatorDefinition getOperatorDefinition() {
		return operator;
	}

	public int getParam() {
		return parameter;
	}

	public int getTime() {

		return parameter;
	}

	public SDFAbstractVertex getVertex() {
		return vertex;
	}

	public void setParam(int param) {
		parameter = param;
	}

	public void setTime(int time) {

		parameter = time;
	}

}
