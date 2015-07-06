/*********************************************************
Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
Karol Desnos

[mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/
package org.ietr.preesm.algorithm.exportPromela;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.utils.paths.PathTools;

/**
 * Workflow task used to generate a Promela program as specified in : <br>
 * <br>
 * <code>Marc Geilen, Twan Basten, and Sander Stuijk. 2005. Minimising buffer 
 * requirements of synchronous dataflow graphs with model checking. In 
 * Proceedings of the 42nd annual Design Automation Conference (DAC '05). ACM, 
 * New York, NY, USA, 819-824. DOI=10.1145/1065579.1065796 
 * http://doi.acm.org/10.1145/1065579.1065796 </code>
 * 
 * @author kdesnos
 *
 */
public class PromelaExporter extends AbstractTaskImplementation {

	static final public String PARAM_PATH = "path";
	static final public String VALUE_PATH_DEFAULT = "./Code/Promela/graph.pml";

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
			IProgressMonitor monitor, String nodeName, Workflow workflow) throws WorkflowException {
		// Retrieve the inputs
		SDFGraph sdf = (SDFGraph) inputs.get(KEY_SDF_GRAPH);
		PreesmScenario scenario = (PreesmScenario) inputs.get(KEY_SCENARIO);
		Design archi = (Design) inputs.get(KEY_ARCHITECTURE);
		// Locate the output file
		String sPath = PathTools.getAbsolutePath(parameters.get("path"), workflow.getProjectName());
		IPath path = new Path(sPath);

		PromelaExporterEngine engine = new PromelaExporterEngine();
		engine.printSDFGraphToPromelaFile(sdf, scenario, archi, path);

		return new HashMap<String, Object>();
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(PARAM_PATH, VALUE_PATH_DEFAULT);
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Export Promela code";
	}

}
