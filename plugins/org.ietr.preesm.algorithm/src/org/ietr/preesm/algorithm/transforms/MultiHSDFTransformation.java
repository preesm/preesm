/*******************************************************************************
 * Copyright or © or Copr. 2014 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2016)
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
package org.ietr.preesm.algorithm.transforms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.visitors.ToHSDFVisitor;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.algorithm.model.visitors.VisitorOutput;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.algorithm.optimization.clean.joinfork.JoinForkCleaner;

public class MultiHSDFTransformation extends AbstractTaskImplementation {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
			IProgressMonitor monitor, String nodeName, Workflow workflow) throws WorkflowException {

		Set<SDFGraph> result = new HashSet<SDFGraph>();
		@SuppressWarnings("unchecked")
		Set<SDFGraph> algorithms = (Set<SDFGraph>) inputs.get(KEY_SDF_GRAPHS_SET);

		for (SDFGraph algorithm : algorithms) {

			Logger logger = WorkflowLogger.getLogger();

			try {

				logger.setLevel(Level.FINEST);
				logger.log(Level.FINER, "Transforming application " + algorithm.getName() + " to HSDF");
				VisitorOutput.setLogger(logger);
				if (algorithm.validateModel(WorkflowLogger.getLogger())) {

					ToHSDFVisitor toHsdf = new ToHSDFVisitor();
					SDFGraph hsdf = null;
					try {
						algorithm.accept(toHsdf);
						hsdf = toHsdf.getOutput();
						logger.log(Level.FINER, "Minimize special actors");
						JoinForkCleaner.cleanJoinForkPairsFrom(hsdf);
					} catch (SDF4JException e) {
						e.printStackTrace();
						throw (new WorkflowException(e.getMessage()));
					} catch (InvalidExpressionException e) {
						e.printStackTrace();
						throw (new WorkflowException(e.getMessage()));
					}
					logger.log(Level.FINER, "HSDF transformation complete");

					logger.log(Level.INFO, "HSDF with " + hsdf.vertexSet().size() + " vertices and "
							+ hsdf.edgeSet().size() + " edges.");

					String explImplSuppr;
					if ((explImplSuppr = parameters.get("ExplodeImplodeSuppr")) != null) {
						if (explImplSuppr.equals("true")) {
							logger.log(Level.INFO, "Removing implode/explode ");
							ForkJoinRemover.supprImplodeExplode(hsdf);
							// Kdesnos addition for csv stat. can be removed
							System.out.print(hsdf.vertexSet().size() + ";" + hsdf.edgeSet().size() + ";");
						}
					}

					result.add(hsdf);
				} else {
					throw (new WorkflowException("Graph " + algorithm.getName() + " not valid, not schedulable"));
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
