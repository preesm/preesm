package org.ietr.preesm.experiment.pimm2sdf;

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

	private Map<String, List<Integer>> parameterValues;

	public PiGraphExecution(PiGraph graph, Map<String, List<Integer>> values) {
		this.executedPiGraph = graph;
		this.parameterValues = values;
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
		return this.getValues(p) != null;
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
			int executionIndex) {
		Map<String, List<Integer>> innerParameterValues = new HashMap<String, List<Integer>>();
		for (String s : parameterValues.keySet()) {
			if (getSubgraphParametersNames(subgraph).contains(s)) {
				int size = parameterValues.get(s).size();
				Integer value = parameterValues.get(s).get(
						executionIndex % size);
				innerParameterValues.put(s, new ArrayList<Integer>(value));
			} else {
				innerParameterValues.put(s, parameterValues.get(s));
			}
		}
		return new PiGraphExecution(executedPiGraph, innerParameterValues);
	}

	private Set<String> getSubgraphParametersNames(PiGraph subgraph) {
		Set<String> parametersNames = new HashSet<String>();
		for (Parameter p : subgraph.getParameters())
			parametersNames.add(p.getName());
		return parametersNames;
	}
}
