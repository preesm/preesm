/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.algorithm.transforms;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.transformations.IbsdfFlattener;
import org.ietr.dftools.algorithm.model.sdf.visitors.ConsistencyChecker;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.algorithm.model.visitors.VisitorOutput;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;

/**
 * Class used to flatten the hierarchy of a given graph
 * 
 * @author jpiat
 * @author mpelcat
 */
public class HierarchyFlattening extends AbstractTaskImplementation {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		Map<String, Object> outputs = new HashMap<String, Object>();
		SDFGraph algorithm = (SDFGraph) inputs.get("SDF");
		String depthS = parameters.get("depth");

		int depth;
		if (depthS != null) {
			depth = Integer.decode(depthS);
		} else {
			depth = 1;
		}

		Logger logger = WorkflowLogger.getLogger();

		if (depth == 0) {
			outputs.put("SDF", algorithm.clone());
			logger.log(Level.INFO, "flattening depth = 0: no flattening");
			return outputs;
		}

		logger.setLevel(Level.FINEST);
		VisitorOutput.setLogger(logger);
		ConsistencyChecker checkConsistent = new ConsistencyChecker();
		if (checkConsistent.verifyGraph(algorithm)) {
			logger.log(Level.FINER,
					"flattening application " + algorithm.getName()
							+ " at level " + depth);

			IbsdfFlattener flattener = new IbsdfFlattener(algorithm,depth);
			VisitorOutput.setLogger(logger);
			try {
				if (algorithm.validateModel(WorkflowLogger.getLogger())) {
					try {
						flattener.flattenGraph();
						
					} catch (SDF4JException e) {
						throw (new WorkflowException(e.getMessage()));
					}
					logger.log(Level.FINER, "flattening complete");
					SDFGraph resultGraph = flattener.getFlattenedGraph();//flatHier.getOutput();
					outputs.put("SDF", resultGraph);
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
			outputs.put("SDF", algorithm.clone());
			throw (new WorkflowException(
					"Inconsistent Hierarchy, graph can't be flattened"));
		}

		return outputs;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("depth", "1");
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Flattening algorithm hierarchy.";
	}
}
