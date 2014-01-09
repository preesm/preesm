/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
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
 ******************************************************************************/
package org.ietr.preesm.experiment.core.piscenario;

import java.util.HashSet;
import java.util.Set;

import com.singularsys.jep.EvaluationException;
import com.singularsys.jep.Jep;
import com.singularsys.jep.JepException;
import com.singularsys.jep.ParseException;

/**
 * Value(s) of a parameter in a graph.
 * It can be: Static, Dependent or Dynamic.
 * @author jheulot
 */
public class ParameterValue {
	/**
	 *  Different type of Parameter.
	 */
	public enum ParameterType{
		STATIC, DEPENDENT, DYNAMIC
	}
	
	/**
	 * The name of the parameter
	 */
	private String name;
	
	/**
	 * The parameter type
	 */
	private ParameterType type;
	
	/**
	 * The corresponding actor parent
	 */
	private ActorNode parent;
	
	/**
	 * Type specific attributes
	 */
	/* STATIC */
	private int value;
	
	/* DYNAMIC */
	private Set<Integer> values;

	/* DEPENDANT */
	private Set<String> inputParameters;
	private String expression;
	
	/**
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * @param inputParameters the inputParameters to set
	 */
	public void setInputParameters(Set<String> inputParameters) {
		this.inputParameters = inputParameters;
	}

	/**
	 * @param expression the expression to set
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

	public ParameterValue(String _name, ParameterType _type, ActorNode _parent) {
		name = _name;
		type = _type;
		value = 0;
		values = new HashSet<Integer>();
		inputParameters = new HashSet<String>();
		parent = _parent;
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
	public ActorNode getParent() {
		return parent;
	}
	
	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @return the values
	 */
	public Set<Integer> getValues() {
		return values;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * @param values the values to set
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
	 * @return if the parameter value is defined correctly
	 */
	public boolean isValid(){
		switch(type){
		case STATIC:
			return true;
		case DYNAMIC:
			return !values.isEmpty();
		case DEPENDENT:
			Jep jep = new Jep();
			try {
				for (String parameter : inputParameters)
					jep.addVariable(parameter, 1);
				jep.parse(expression);
				jep.evaluate();
				return true;
			} catch (JepException e) {
				return false;
			}
		default:
			return false;
		}
	}
}
