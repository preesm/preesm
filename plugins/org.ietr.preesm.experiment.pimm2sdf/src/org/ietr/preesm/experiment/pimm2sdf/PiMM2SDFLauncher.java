package org.ietr.preesm.experiment.pimm2sdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.experiment.core.piscenario.ActorNode;
import org.ietr.preesm.experiment.core.piscenario.ActorTree;
import org.ietr.preesm.experiment.core.piscenario.ParameterValue;
import org.ietr.preesm.experiment.core.piscenario.ParameterValue.ParameterType;
import org.ietr.preesm.experiment.core.piscenario.PiScenario;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.pimm2sdf.visitor.PiMM2SDFVisitor;

public class PiMM2SDFLauncher {

	private PiScenario piscenario;
	private PiGraph graph;

	public PiMM2SDFLauncher(PiScenario piscenario, PiGraph graph) {
		this.piscenario = piscenario;
		this.graph = graph;
	}

	public Set<SDFGraph> launch() {
		Set<SDFGraph> result = new HashSet<SDFGraph>();

		ActorTree tree = piscenario.getActorTree();
		// Get all the available values for all the parameters
		Map<String, List<Integer>> parametersValues = getAllDynamicParametersValues(tree);
		// Get the values for Parameters directly contained by graph (top-level
		// parameters), if any
		Map<String, List<Integer>> outerParametersValues = new HashMap<String, List<Integer>>();
		// The number of time we need to execute, and thus visit graph, depends
		// on the maximum number of values for all the top-level parameters
		int nbExecutions = 1;
		for (Parameter param : graph.getParameters()) {
			List<Integer> pValues = parametersValues.get(param.getName());
			if (pValues != null) {
				outerParametersValues.put(param.getName(), pValues);
				int nbPValues = pValues.size();
				if (nbPValues > nbExecutions)
					nbExecutions = nbPValues;
			}
		}
		// Visitor creating the SDFGraphs
		PiMM2SDFVisitor visitor;
		PiGraphExecution execution;
		// Values for the parameters for one execution
		Map<String, List<Integer>> currentValues;
		for (int i = 0; i < nbExecutions; i++) {
			// Values for one execution are parametersValues except for
			// top-level Parameters, for which we select only one value for a
			// given execution
			currentValues = parametersValues;
			for (String s : outerParametersValues.keySet()) {
				// Value selection
				List<Integer> availableValues = outerParametersValues.get(s);
				int nbValues = availableValues.size();
				if (nbValues > 0) {
					Integer value = availableValues.get(i % nbValues);
					currentValues.put(s, new ArrayList<Integer>(value));
				}
			}

			execution = new PiGraphExecution(graph, currentValues);
			visitor = new PiMM2SDFVisitor(execution);
			graph.accept(visitor);

			result.add(visitor.getResult());
		}

		return result;
	}

	private Map<String, List<Integer>> getAllDynamicParametersValues(
			ActorTree tree) {
		Map<String, List<Integer>> result = new HashMap<String, List<Integer>>();

		ActorNode root = tree.getRoot();
		result = getDynamicParametersValues(root);

		return result;
	}

	private Map<String, List<Integer>> getDynamicParametersValues(ActorNode node) {
		Map<String, List<Integer>> result = new HashMap<String, List<Integer>>();

		for (ParameterValue paramValue : node.getParamValues()) {
			if (paramValue.getType() == ParameterType.DYNAMIC) {
				result.put(paramValue.getName(), new ArrayList<Integer>(
						paramValue.getValues()));
			}
		}

		for (ActorNode child : node.getChildren()) {
			result.putAll(getDynamicParametersValues(child));
		}

		return result;
	}

}
