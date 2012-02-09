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

package org.ietr.preesm.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.sf.dftools.algorithm.model.parameters.InvalidExpressionException;
import net.sf.dftools.algorithm.model.sdf.SDFGraph;
import net.sf.dftools.architecture.slam.Design;
import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.abc.AbstractAbc;
import org.ietr.preesm.mapper.abc.IAbc;
import org.ietr.preesm.mapper.abc.impl.latency.InfiniteHomogeneousAbc;
import org.ietr.preesm.mapper.abc.taskscheduling.AbstractTaskSched;
import org.ietr.preesm.mapper.abc.taskscheduling.TopologicalTaskSched;
import org.ietr.preesm.mapper.algo.list.InitialLists;
import org.ietr.preesm.mapper.algo.list.KwokListScheduler;
import org.ietr.preesm.mapper.graphtransfo.SdfToDagConverter;
import org.ietr.preesm.mapper.graphtransfo.TagDAG;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.params.AbcParameters;

/**
 * Plug-in class for list scheduling
 * 
 * @author pmenuet
 * @author mpelcat
 */
public class ListSchedulingTransformation extends AbstractMapping {

	/**
	 * 
	 */
	public ListSchedulingTransformation() {
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = super.getDefaultParameters();
		return parameters;
	}

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName) throws WorkflowException {

		Map<String, Object> outputs = new HashMap<String, Object>();
		Design architecture = (Design) inputs.get("architecture");
		SDFGraph algorithm = (SDFGraph) inputs.get("SDF");
		PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");

		super.execute(inputs, parameters, monitor, nodeName);

		AbcParameters abcParameters = new AbcParameters(parameters);

		MapperDAG dag = SdfToDagConverter.convert(algorithm, architecture,
				scenario, false);

		// DAGExporter exporter = new DAGExporter();
		// exporter.export(dag,
		// "D:\\Projets\\LTE\\PreesmProjects\\MPEG4P2_Encoder_Preesm\\DAG\\test.graphml");

		// calculates the DAG span length on the architecture main operator (the
		// tasks that can
		// not be executed by the main operator are deported without transfer
		// time to other operator)
		calculateSpan(dag, architecture, scenario, abcParameters);

		IAbc simu = new InfiniteHomogeneousAbc(abcParameters, dag,
				architecture, abcParameters.getSimulatorType()
						.getTaskSchedType(), scenario);

		InitialLists initial = new InitialLists();

		if (!initial.constructInitialLists(dag, simu)) {
			WorkflowLogger.getLogger().log(Level.SEVERE, "Error in scheduling");
			return null;
		}

		WorkflowLogger.getLogger().log(Level.INFO, "Mapping");

		// Using topological task scheduling in list scheduling: the t-level
		// order of the infinite homogeneous simulation
		AbstractTaskSched taskSched = new TopologicalTaskSched(
				simu.getTotalOrder());

		// AbstractTaskSched taskSched = new TaskSwitcher();

		simu.resetDAG();
		IAbc simu2 = AbstractAbc.getInstance(abcParameters, dag, architecture,
				scenario);
		simu2.setTaskScheduler(taskSched);

		KwokListScheduler scheduler = new KwokListScheduler();
		scheduler.schedule(dag, initial.getCpnDominant(), simu2, null, null);

		WorkflowLogger.getLogger().log(Level.INFO, "Mapping finished");

		TagDAG tagSDF = new TagDAG();

		try {
			tagSDF.tag(dag, architecture, scenario, simu2,
					abcParameters.getEdgeSchedType());
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
			throw (new WorkflowException(e.getMessage()));
		}

		outputs.put("DAG", dag);
		outputs.put("ABC", simu2);

		super.clean(architecture, scenario);
		return outputs;
	}

}
