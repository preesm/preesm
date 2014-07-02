/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy, Yaset Oliva Venegas
 * 
 * [mpelcat,jnezan,kdesnos,jheulot,cguy,yoliva]@insa-rennes.fr
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
package org.ietr.preesm.core.scenario;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.ietr.preesm.core.scenario.ParameterValue.ParameterType;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

/**
 * Manager class for parameters, storing values given by the user to parameters
 * 
 * @author cguy
 * 
 */
public class ParameterValueManager {
	// Set of ParameterValues
	private Set<ParameterValue> parameterValues;

	public ParameterValueManager() {
		this.parameterValues = new HashSet<ParameterValue>();
	}

	public Set<ParameterValue> getParameterValues() {
		return parameterValues;
	}

	public void setParameterValues(Set<ParameterValue> parameterValues) {
		this.parameterValues = parameterValues;
	}

	public void addStaticParameterValue(String paramName, String value, String parent) {
		ParameterValue pValue = new ParameterValue(paramName,
				ParameterType.STATIC, parent);
		pValue.setValue(value);
		this.parameterValues.add(pValue);
	}

	public void addDynamicParameterValue(String paramName, Set<Integer> values,
			String parent) {
		ParameterValue pValue = new ParameterValue(paramName,
				ParameterType.DYNAMIC, parent);
		pValue.setValues(values);
		this.parameterValues.add(pValue);
	}

	public void addDependentParameterValue(String paramName, String expression,
			Set<String> inputParameters, String parent) {
		ParameterValue pValue = new ParameterValue(paramName,
				ParameterType.DEPENDENT, parent);
		pValue.setExpression(expression);
		pValue.setInputParameters(inputParameters);
		this.parameterValues.add(pValue);
	}

	public void addParameterValue(Parameter param) {
		EObject container = param.eContainer();
		String parent = "";
		if (container instanceof PiGraph) {
			parent = ((PiGraph) container).getName();
		}
		Set<Parameter> inputParameters = new HashSet<Parameter>();
		inputParameters = param.getInputParameters();
		if (param.isLocallyStatic()) {
			// Add a static parameter value
			addStaticParameterValue(param.getName(), null, parent);
		} else {
			if (inputParameters.isEmpty()) {
				Set<Integer> values = new HashSet<Integer>();
				// Add a dynamic parameter value
				addDynamicParameterValue(param.getName(), values, parent);
			} else {
				Set<String> inputParametersNames = new HashSet<String>();
				for (Parameter p : inputParameters) {
					inputParametersNames.add(p.getName());
				}
				addDependentParameterValue(param.getName(), null, inputParametersNames, parent);
			}
		}
	}

	public void updateWith(PiGraph graph) {
		getParameterValues().clear();
		for (Parameter p : graph.getAllParameters()) {
			addParameterValue(p);
		}
	}
}
