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
import com.singularsys.jep.ParseException;

/**
 * Timing structure of an actor.
 * @author jheulot
 *
 */
public class Timing {
	/**
	 * Set of Usable Parameters 
	 */
	private Set<String> inputParameters;
	
	/**
	 * Expression String
	 */
	private String stringValue;
	private static final String DEFAULT_EXPRESSION_VALUE = "100";

	
	public Timing() {
		inputParameters = new HashSet<String>();
		stringValue = DEFAULT_EXPRESSION_VALUE;
	}
	
	/**
	 * @return the parentActor
	 */
	public Set<String> getInputParameters() {
		return inputParameters;
	}

	/**
	 * @return the stringValue
	 */
	public String getStringValue() {
		return stringValue;
	}

	/**
	 * @param parentActor the parentActor to set
	 */
	public void setInputParameters(Set<String> _inputParameters) {
		inputParameters = _inputParameters;
	}

	/**
	 * @param stringValue the stringValue to set
	 */
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
	
	/**
	 * Test if the {@link Timing} can be parsed (Check Syntax Errors).
	 * @return true if it can be parsed.
	 */
	public boolean canParse(){
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
	 * @return true if it can be evaluated.
	 */
	public boolean canEvaluate(){
		Jep jep = new Jep();
		
		String value = stringValue;
		
		for (String parameter : inputParameters) {			
			int startingIndex=0;
			String operators = "*+-/^";
			while(startingIndex<value.length()){
				if(value.substring(startingIndex).contains(parameter)){
					int index = value.substring(startingIndex).indexOf(parameter) + startingIndex;
					
					// Verify that the parameter is surrounded by operators.
					if(index == 0 || operators.contains(""+value.charAt(index-1))){
						if (index+parameter.length() == value.length() 
								|| operators.contains(""+value.charAt(index+parameter.length()))){
							
							String evaluatedParam;
							evaluatedParam =  "1";
							value = value.substring(0, index) 
									+ value.substring(index).replaceFirst(parameter, "("+evaluatedParam+")");
						}
					}
					startingIndex = index+1;
				} else {
					break;
				}
			}
			
		}
		
		try {
			jep.parse(value);
			jep.evaluate();
			return true;
		} catch (ParseException | EvaluationException e) {
			return false;
		}
	}
}
