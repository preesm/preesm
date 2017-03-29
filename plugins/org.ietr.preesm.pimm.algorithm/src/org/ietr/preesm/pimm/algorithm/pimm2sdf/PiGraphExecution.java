/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
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
package org.ietr.preesm.pimm.algorithm.pimm2sdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

/**
 * This class corresponds to one execution of a PiGraph with given values for
 * each parameters
 * 
 * @author cguy
 * 
 */
public class PiGraphExecution {

	private PiGraph executedPiGraph;

	private String executionLabel;
	
	private int executionNumber;
	
	private Map<String, List<Integer>> parameterValues;

	public PiGraphExecution(PiGraph graph, Map<String, List<Integer>> values) {
		this.executedPiGraph = graph;
		this.parameterValues = values;
		this.executionLabel = "";
		this.executionNumber = 0;
	}
	
	public PiGraphExecution(PiGraph graph, Map<String, List<Integer>> values, String label, int number) {
		this.executedPiGraph = graph;
		this.parameterValues = values;
		this.executionLabel = label;
		this.executionNumber = number;
	}

	public List<Integer> getValues(Parameter p) {
		return this.parameterValues.get(p.getName());
	}

	public Integer getUniqueValue(Parameter p) {
		List<Integer> pValues = this.getValues(p);
		if (pValues != null && pValues.size() == 1) {
			return pValues.get(0);
		} else {
			return null;
		}
	}

	public boolean hasValue(Parameter p) {
		return (this.getValues(p) != null && !this.getValues(p).isEmpty());
	}

	public int getNumberOfInnerExecutions(PiGraph subgraph) {
		int maxNumberOfValues = 0;
		for (String s : parameterValues.keySet()) {
			if (getSubgraphParametersNames(subgraph).contains(s)) {
				int size = parameterValues.get(s).size();
				if (size > maxNumberOfValues)
					maxNumberOfValues = size;
			}
		}
		return maxNumberOfValues;
	}

	public PiGraphExecution extractInnerExecution(PiGraph subgraph,
			int selector) {
		Map<String, List<Integer>> innerParameterValues = new HashMap<String, List<Integer>>();
		for (String s : parameterValues.keySet()) {			
			if (getSubgraphParametersNames(subgraph).contains(s)) {
				int size = parameterValues.get(s).size();
				List<Integer> value = new ArrayList<Integer>();
				value.add(parameterValues.get(s).get(
						selector % size));
				innerParameterValues.put(s, value);
				
			} else {
				innerParameterValues.put(s, parameterValues.get(s));
			}			
		}
		return new PiGraphExecution(executedPiGraph, innerParameterValues, executionLabel + "_" + selector, selector);
	}

	private Set<String> getSubgraphParametersNames(PiGraph subgraph) {
		Set<String> parametersNames = new HashSet<String>();
		for (Parameter p : subgraph.getParameters())
			parametersNames.add(p.getName());
		return parametersNames;
	}

	public String getExecutionLabel() {
		return executionLabel;
	}

	public int getExecutionNumber() {
		return executionNumber;
	}
}
