/*******************************************************************************
 * Copyright or © or Copr. 2014 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.core.scenario;

import java.util.HashSet;
import java.util.Set;

import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

/**
 * Value(s) of a parameter in a graph. It can be: Static, Dependent or Dynamic.
 *
 * @author jheulot
 */
public class ParameterValue {
	/**
	 * Different type of Parameter.
	 */
	public enum ParameterType {
		// No configuration input port
		INDEPENDENT,
		// Direct dependency from a configuration actor to this parameter
		ACTOR_DEPENDENT,
		// Configuration input ports, but none directly dependent from a
		// configuration actor
		PARAMETER_DEPENDENT
	}

	/**
	 * Parameter for which we keep value(s)
	 */
	private  Parameter parameter;

	/**
	 * The name of the parameter
	 */
	private String name;

	/**
	 * The parameter type
	 */
	private ParameterType type;

	/**
	 * The corresponding parent vertex
	 */
	private String parentVertex;

	/**
	 * Type specific attributes
	 */
	/* INDEPENDENT */
	private String value;

	/* ACTOR_DEPENDENT */
	private Set<Integer> values;

	/* PARAMETER_DEPENDENT */
	private Set<String> inputParameters;
	private String expression;

	/**
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * @param inputParameters
	 *            the inputParameters to set
	 */
	public void setInputParameters(Set<String> inputParameters) {
		this.inputParameters = inputParameters;
	}

	/**
	 * @param expression
	 *            the expression to set
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

	public ParameterValue(Parameter parameter, ParameterType type, String parent) {
		this.setParameter(parameter);
		this.name = parameter.getName();
		this.type = type;
		this.values = new HashSet<Integer>();
		this.inputParameters = new HashSet<String>();
		this.parentVertex = parent;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the type
	 */
	public ParameterType getType() {
		return type;
	}

	/**
	 * @return the parent
	 */
	public String getParentVertex() {
		return parentVertex;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return the values
	 */
	public Set<Integer> getValues() {
		return values;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @param values
	 *            the values to set
	 */
	public void setValues(Set<Integer> values) {
		this.values = values;
	}

	/**
	 * @return the inputParameters
	 */
	public Set<String> getInputParameters() {
		return inputParameters;
	}

	/**
	 * Test if the parameter value is defined correctly
	 *
	 * @return if the parameter value is defined correctly
	 */
	public boolean isValid() {
		switch (type) {
		case INDEPENDENT:
			return true;
		case ACTOR_DEPENDENT:
			return !values.isEmpty();
		case PARAMETER_DEPENDENT:
			JEP jep = new JEP();
			try {
				for (String parameter : inputParameters)
					jep.addVariable(parameter, 1);
				final Node parse = jep.parse(expression);
				jep.evaluate(parse);
				return true;
			} catch (ParseException e) {
				return false;
			}
		default:
			return false;
		}
	}

	public Parameter getParameter() {
		return parameter;
	}

	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}
}
