/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
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

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

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
	
	public Set<ParameterValue> getSortedParameterValues() {
		Set<ParameterValue> result = new ConcurrentSkipListSet<ParameterValue>(new Comparator<ParameterValue>() {
			@Override
			public int compare(ParameterValue o1, ParameterValue o2) {
				return o1.getName().compareTo(o2.getName());
			}			
		});
		result.addAll(parameterValues);
		return result;
	}

	public void setParameterValues(Set<ParameterValue> parameterValues) {
		this.parameterValues = parameterValues;
	}

	public void addIndependentParameterValue(Parameter parameter, String value,
			String parent) {
		ParameterValue pValue = new ParameterValue(parameter,
				ParameterType.INDEPENDENT, parent);
		pValue.setValue(value);
		this.parameterValues.add(pValue);
	}

	public void addActorDependentParameterValue(Parameter parameter,
			Set<Integer> values, String parent) {
		ParameterValue pValue = new ParameterValue(parameter,
				ParameterType.ACTOR_DEPENDENT, parent);
		pValue.setValues(values);
		this.parameterValues.add(pValue);
	}

	public void addParameterDependentParameterValue(Parameter parameter,
			String expression, Set<String> inputParameters, String parent) {
		ParameterValue pValue = new ParameterValue(parameter,
				ParameterType.PARAMETER_DEPENDENT, parent);
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
			if (param.isDependent()) {
				// Add a parameter dependent value (a locally static parameter
				// cannot be actor dependent)
				addParameterDependentParameterValue(param, parent);
			} else {
				// Add an independent parameter value
				addIndependentParameterValue(param, param.getExpression().getString(), parent);
			}
		} else {
			boolean isActorDependent = inputParameters.size() < param
					.getConfigInputPorts().size();

			if (isActorDependent) {
				Set<Integer> values = new HashSet<Integer>();
				values.add(1);
				// Add an actor dependent value
				addActorDependentParameterValue(param, values, parent);
			} else {
				// Add a parameter dependent value
				addParameterDependentParameterValue(param, parent);
			}
		}
	}

	private void addParameterDependentParameterValue(Parameter param,
			String parent) {
		Set<String> inputParametersNames = new HashSet<String>();
		for (Parameter p : param.getInputParameters())
			inputParametersNames.add(p.getName());

		addParameterDependentParameterValue(param, param
				.getExpression().getString(), inputParametersNames, parent);
	}

	public void updateWith(PiGraph graph) {
		getParameterValues().clear();
		for (Parameter p : graph.getAllParameters()) {
			if (!p.isConfigurationInterface())
				addParameterValue(p);
		}
	}
}
