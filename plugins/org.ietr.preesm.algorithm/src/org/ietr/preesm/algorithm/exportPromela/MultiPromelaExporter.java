/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
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
package org.ietr.preesm.algorithm.exportPromela;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.utils.paths.PathTools;

/**
 * Workflow task {@link PromelaExporter} for multi-SDF workflows.
 * 
 * @author kdesnos
 *
 */
public class MultiPromelaExporter extends AbstractTaskImplementation {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
			IProgressMonitor monitor, String nodeName, Workflow workflow) throws WorkflowException {

		// Retrieve the inputs
		@SuppressWarnings("unchecked")
		Set<SDFGraph> sdfs = (Set<SDFGraph>) inputs.get(KEY_SDF_GRAPHS_SET);
		PreesmScenario scenario = (PreesmScenario) inputs.get(KEY_SCENARIO);

		String paramFifo = (String) parameters.get(PromelaExporter.PARAM_FIFO_POLICY);
		boolean fifoShared = paramFifo.equalsIgnoreCase(PromelaExporter.VALUE_FIFO_SHARED);

		String paramActor = (String) parameters.get(PromelaExporter.PARAM_ACTOR_POLICY);
		boolean synchronousActor = Boolean.parseBoolean(paramActor);

		// Locate the output file
		String sPath = PathTools.getAbsolutePath(parameters.get("path"), workflow.getProjectName());
		IPath path = new Path(sPath);

		for (SDFGraph sdf : sdfs) {
			PromelaExporterEngine engine = new PromelaExporterEngine();
			engine.printSDFGraphToPromelaFile(sdf, scenario, path, fifoShared, synchronousActor);
		}

		return new HashMap<String, Object>();
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(PromelaExporter.PARAM_PATH, PromelaExporter.VALUE_PATH_DEFAULT);
		parameters.put(PromelaExporter.PARAM_FIFO_POLICY, PromelaExporter.VALUE_FIFO_DEFAULT);
		parameters.put(PromelaExporter.PARAM_ACTOR_POLICY, PromelaExporter.VALUE_ACTOR_DEFAULT);
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Exporting Promela code";
	}
}
