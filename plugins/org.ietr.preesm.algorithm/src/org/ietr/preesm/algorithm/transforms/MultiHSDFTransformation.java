package org.ietr.preesm.algorithm.transforms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.visitors.ToHSDFVisitor;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.algorithm.model.visitors.VisitorOutput;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;

public class MultiHSDFTransformation extends AbstractTaskImplementation {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		Set<SDFGraph> result = new HashSet<SDFGraph>();
		Set<SDFGraph> algorithms = (Set<SDFGraph>) inputs
				.get(KEY_SDF_GRAPHS_SET);

		for (SDFGraph algorithm : algorithms) {

			Logger logger = WorkflowLogger.getLogger();

			try {

				logger.setLevel(Level.FINEST);
				logger.log(Level.FINER,
						"Transforming application " + algorithm.getName()
								+ " to HSDF");
				VisitorOutput.setLogger(logger);
				if (algorithm.validateModel(WorkflowLogger.getLogger())) {

					ToHSDFVisitor toHsdf = new ToHSDFVisitor();

					try {
						algorithm.accept(toHsdf);
					} catch (SDF4JException e) {
						e.printStackTrace();
						throw (new WorkflowException(e.getMessage()));
					}
					logger.log(Level.FINER, "HSDF transformation complete");

					SDFGraph hsdf = (SDFGraph) toHsdf.getOutput();
					logger.log(Level.INFO, "HSDF with "
							+ hsdf.vertexSet().size() + " vertices and "
							+ hsdf.edgeSet().size() + " edges.");

					String explImplSuppr;
					if ((explImplSuppr = parameters.get("ExplodeImplodeSuppr")) != null) {
						if (explImplSuppr.equals("true")) {
							logger.log(Level.INFO, "Removing implode/explode ");
							ForkJoinRemover.supprImplodeExplode(hsdf);
							// Kdesnos addition for csv stat. can be removed
							System.out.print(hsdf.vertexSet().size() + ";"
									+ hsdf.edgeSet().size() + ";");
						}
					}

					result.add(hsdf);
				} else {
					throw (new WorkflowException(
							"Graph " + algorithm.getName() + " not valid, not schedulable"));
				}
			} catch (SDF4JException e) {
				throw (new WorkflowException(e.getMessage()));
			}
		}

		Map<String, Object> outputs = new HashMap<String, Object>();
		outputs.put(KEY_SDF_GRAPHS_SET, result);
		return outputs;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> param = new HashMap<String, String>();
		param.put("ExplodeImplodeSuppr", "false");
		return param;
	}

	@Override
	public String monitorMessage() {
		return "SDF graphs to HSDF graphs transformation.";
	}
}