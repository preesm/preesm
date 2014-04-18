package org.ietr.preesm.experiment.pimm.mapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.core.piscenario.PiScenario;
import org.ietr.preesm.mapper.AbstractMapping;
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

public class MultiSDFListSchedulingMapping extends AbstractMapping {
	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = super.getDefaultParameters();
		return parameters;
	}

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		Design architecture = (Design) inputs.get(KEY_ARCHITECTURE);
		Set<SDFGraph> algorithms = (Set<SDFGraph>) inputs
				.get(KEY_SDF_GRAPHS_SET);
		PiScenario scenario = (PiScenario) inputs.get(KEY_SDF_SCENARIO);

		super.execute(inputs, parameters, monitor, nodeName, workflow);

		Set<MapperDAG> dags = new HashSet<MapperDAG>();
		Set<IAbc> abcs = new HashSet<IAbc>();

		for (SDFGraph algorithm : algorithms) {

			AbcParameters abcParameters = new AbcParameters(parameters);

			MapperDAG dag = SdfToDagConverter.convert(algorithm, architecture,
					scenario, false);

			// calculates the DAG span length on the architecture main operator
			// (the tasks that can not be executed by the main operator are
			// deported without transfer time to other operator)
			calculateSpan(dag, architecture, scenario, abcParameters);

			IAbc simu = new InfiniteHomogeneousAbc(abcParameters, dag,
					architecture, abcParameters.getSimulatorType()
							.getTaskSchedType(), scenario);

			InitialLists initial = new InitialLists();

			if (!initial.constructInitialLists(dag, simu)) {
				WorkflowLogger.getLogger().log(Level.SEVERE,
						"Error in scheduling");
				return null;
			}

			WorkflowLogger.getLogger().log(Level.INFO, "Mapping");

			// Using topological task scheduling in list scheduling: the t-level
			// order of the infinite homogeneous simulation
			AbstractTaskSched taskSched = new TopologicalTaskSched(
					simu.getTotalOrder());

			simu.resetDAG();
			IAbc simu2 = AbstractAbc.getInstance(abcParameters, dag,
					architecture, scenario);
			simu2.setTaskScheduler(taskSched);

			KwokListScheduler scheduler = new KwokListScheduler();
			scheduler
					.schedule(dag, initial.getCpnDominant(), simu2, null, null);

			WorkflowLogger.getLogger().log(Level.INFO, "Mapping finished");

			TagDAG tagSDF = new TagDAG();

			try {
				tagSDF.tag(dag, architecture, scenario, simu2,
						abcParameters.getEdgeSchedType());
			} catch (InvalidExpressionException e) {
				e.printStackTrace();
				throw (new WorkflowException(e.getMessage()));
			}

			dags.add(dag);
			abcs.add(simu2);
		}

		Map<String, Object> outputs = new HashMap<String, Object>();
		outputs.put(KEY_SDF_DAG_SET, dags);
		outputs.put(KEY_SDF_ABC_SET, abcs);

		super.clean(architecture, scenario);
		return outputs;
	}
}
