package org.ietr.preesm.algorithm.transforms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.visitors.ConsistencyChecker;
import org.ietr.dftools.algorithm.model.sdf.visitors.SDFHierarchyFlattening;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.algorithm.model.visitors.VisitorOutput;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;

public class MultiHierarchyFlattening extends AbstractTaskImplementation {

	private final static String DEPTH_KEY = "depth";
	
	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		Set<SDFGraph> result = new HashSet<SDFGraph>();
		@SuppressWarnings("unchecked")
		Set<SDFGraph> algorithms = (Set<SDFGraph>) inputs
				.get(KEY_SDF_GRAPHS_SET);
		String depthS = parameters.get(DEPTH_KEY);

		int depth;
		if (depthS != null) {
			depth = Integer.decode(depthS);
		} else {
			depth = 1;
		}

		Logger logger = WorkflowLogger.getLogger();

		if (depth == 0) {
			for (SDFGraph algorithm : algorithms) {
				result.add(algorithm.clone());
			}
			logger.log(Level.INFO, "flattening depth = 0: no flattening");
		} else {
			for (SDFGraph algorithm : algorithms) {

				logger.setLevel(Level.FINEST);
				VisitorOutput.setLogger(logger);
				ConsistencyChecker checkConsistent = new ConsistencyChecker();
				if (checkConsistent.verifyGraph(algorithm)) {
					logger.log(Level.FINER, "flattening application "
							+ algorithm.getName() + " at level " + depth);
					SDFHierarchyFlattening flatHier = new SDFHierarchyFlattening();
					VisitorOutput.setLogger(logger);
					try {
						if (algorithm.validateModel(WorkflowLogger.getLogger())) {
							try {
								flatHier.flattenGraph(algorithm, depth);
							} catch (SDF4JException e) {
								throw (new WorkflowException(e.getMessage()));
							}
							logger.log(Level.FINER, "flattening complete");
							SDFGraph resultGraph = (SDFGraph) flatHier
									.getOutput();
							result.add(resultGraph);
						} else {
							throw (new WorkflowException(
									"Graph not valid, not schedulable"));
						}
					} catch (SDF4JException e) {
						throw (new WorkflowException(e.getMessage()));
					}
				} else {
					logger.log(Level.SEVERE,
							"Inconsistent Hierarchy, graph can't be flattened");
					result.add(algorithm.clone());
					throw (new WorkflowException(
							"Inconsistent Hierarchy, graph can't be flattened"));
				}
			}
		}

		Map<String, Object> outputs = new HashMap<String, Object>();
		outputs.put(KEY_SDF_GRAPHS_SET, result);
		return outputs;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put(DEPTH_KEY, "1");
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Flattening algorithms hierarchies.";
	}

}
