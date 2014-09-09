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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.dftools.workflow.tools.WorkflowLogger;

import com.singularsys.jep.Jep;
import com.singularsys.jep.JepException;
import com.singularsys.jep.ParseException;

/**
 * A timing links a vertex (either from SDFGraph or from PiGraph) and an
 * operator definition to a time. Ids are used to make the scenario independent
 * from model implementations.
 * 
 * @author mpelcat
 */
public class Timing {

	public static final Timing UNAVAILABLE = null;
	public static final long DEFAULT_TASK_TIME = 100;
	public static final long DEFAULT_SPECIAL_VERTEX_TIME = 10;
	private static final String DEFAULT_EXPRESSION_VALUE = "100";
	public static final String DEFAULT_SPECIAL_VERTEX_EXPRESSION_VALUE = "10";

	/**
	 * Long value of the timing, only available if isEvaluated is at true
	 */
	private long time;

	/**
	 * String expression of the timing, can contains parameters from input
	 * parameters
	 */
	private String stringValue;

	/**
	 * Boolean indicatin if the expression has been evaluated and thus if the
	 * time is available
	 */
	private boolean isEvaluated;

	/**
	 * Operator to which the timing is related (i.e., processing element on
	 * which the vertex of the graph takes the given time to execute)
	 */
	private String operatorDefinitionId;

	/**
	 * Set of Usable Parameters for the expression
	 */
	private Set<String> inputParameters;

	/**
	 * Vertex for which the timing is given
	 */
	private String vertexId;

	/**
	 * Constructors
	 */

	public Timing(String operatorDefinitionId, String vertexId) {
		time = DEFAULT_TASK_TIME;
		stringValue = DEFAULT_EXPRESSION_VALUE;
		inputParameters = new HashSet<String>();
		this.operatorDefinitionId = operatorDefinitionId;
		this.vertexId = vertexId;
		this.isEvaluated = true;
	}

	public Timing(String operatorId, String sdfVertexId, long time) {
		this(operatorId, sdfVertexId);
		this.time = time;
		this.stringValue = String.valueOf(time);
		this.isEvaluated = true;
	}

	public Timing(String operatorId, String vertexId, String expression) {
		this(operatorId, vertexId);
		this.stringValue = expression;
		this.isEvaluated = false;
		this.tryToEvaluateWith(new HashMap<String, Integer>());
	}

	public Timing(String operatorId, String vertexId, String expression,
			Set<String> inputParameters) {
		this(operatorId, vertexId);
		this.stringValue = expression;
		this.isEvaluated = false;
		this.inputParameters = inputParameters;
		this.tryToEvaluateWith(new HashMap<String, Integer>());
	}

	public String getOperatorDefinitionId() {
		return operatorDefinitionId;
	}

	/**
	 * @return time, only if it is available (if the expression have been
	 *         evaluated)
	 */
	public long getTime() {
		if (isEvaluated)
			return time;
		else
			return -1;
	}

	public String getVertexId() {
		return vertexId;
	}

	/**
	 * The given time is set if it is strictly positive. Otherwise, 1 is set. In
	 * every cases, the expression is set as the corresponding string and
	 * considered evaluated
	 * 
	 * @param time
	 *            the new time we want to set
	 */
	public void setTime(long time) {
		if (time > 0) {
			this.time = time;
		} else {
			WorkflowLogger.getLogger().log(
					Level.WARNING,
					"Trying to set a non strictly positive time for vertex "
							+ vertexId);
			this.time = 1;
		}
		this.stringValue = String.valueOf(this.time);
		this.isEvaluated = true;
	}

	public Set<String> getInputParameters() {
		return inputParameters;
	}

	public void setInputParameters(Set<String> inputParameters) {
		this.inputParameters = inputParameters;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
		this.isEvaluated = false;
		this.tryToEvaluateWith(new HashMap<String, Integer>());
	}

	public boolean isEvaluated() {
		return isEvaluated;
	}

	/**
	 * Test if the {@link Timing} can be parsed (Check Syntax Errors).
	 * 
	 * @return true if it can be parsed.
	 */
	public boolean canParse() {
		Jep jep = new Jep();
		try {
			jep.parse(stringValue);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	/**
	 * Test if the {@link Timing} can be evaluated (Check Parameters Errors).
	 * 
	 * @return true if it can be evaluated.
	 */
	public boolean canEvaluate() {
		Jep jep = new Jep();
		try {
			for (String parameter : inputParameters)
				jep.addVariable(parameter, 1);
			jep.parse(stringValue);
			jep.evaluate();
			return true;
		} catch (JepException e) {
			return false;
		}
	}

	/**
	 * Evaluate the timing expression with the given values for parameters. If
	 * the evaluation is successful, isEvaluated is set to true
	 * 
	 * @param parametersValues
	 *            the map of parameters names and associated values with which
	 *            we want to evaluate the expression
	 */
	public void tryToEvaluateWith(Map<String, Integer> parametersValues) {
		Jep jep = new Jep();
		try {
			for (String parameter : parametersValues.keySet())
				jep.addVariable(parameter, parametersValues.get(parameter));
			jep.parse(stringValue);
			long result = ((Double) jep.evaluate()).longValue();
			this.time = result;
			this.isEvaluated = true;
		} catch (JepException e) {
			this.isEvaluated = false;
		}
	}

	@Override
	public boolean equals(Object obj) {
		boolean equals = false;

		if (obj instanceof Timing) {
			Timing otherT = (Timing) obj;
			equals = operatorDefinitionId.equals(otherT
					.getOperatorDefinitionId());
			equals &= vertexId.equals((otherT.getVertexId()));
		}

		return equals;
	}

	@Override
	public String toString() {
		if (isEvaluated)
			return "{" + vertexId + "," + operatorDefinitionId + "," + time
					+ "}";
		else
			return "{" + vertexId + "," + operatorDefinitionId + ","
					+ stringValue + "}";
	}
}
