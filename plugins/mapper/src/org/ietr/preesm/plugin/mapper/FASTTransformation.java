/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.plugin.mapper;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.core.scenario.TimingManager;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.core.workflow.sources.AlgorithmRetriever;
import org.ietr.preesm.plugin.abc.AbcType;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.edgescheduling.EdgeSchedType;
import org.ietr.preesm.plugin.abc.impl.latency.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.abc.taskscheduling.TaskSchedType;
import org.ietr.preesm.plugin.abc.taskscheduling.TopologicalTaskSched;
import org.ietr.preesm.plugin.mapper.algo.fast.FastAlgorithm;
import org.ietr.preesm.plugin.mapper.algo.list.InitialLists;
import org.ietr.preesm.plugin.mapper.graphtransfo.SdfToDagConverter;
import org.ietr.preesm.plugin.mapper.graphtransfo.TagDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.params.FastAlgoParameters;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * FAST Kwok algorithm
 * 
 * @author pmenuet
 */
public class FASTTransformation extends AbstractMapping {

	/**
	 * Main for test
	 */
	public static void main(String[] args) {

		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.FINEST);

		// PreesmLogger.getLogger().setLevel(Level.FINER);

		// Generating archi
		MultiCoreArchitecture archi = Examples.get2C64Archi();

		// Generating random sdf dag
		int nbVertex = 20, minInDegree = 1, maxInDegree = 3, minOutDegree = 1, maxOutDegree = 3;
		SDFGraph graph = AlgorithmRetriever.randomDAG(nbVertex, minInDegree,
				maxInDegree, minOutDegree, maxOutDegree, 50, true);

		// Generating constraints
		IScenario scenario = new Scenario();

		TimingManager tmgr = scenario.getTimingManager();

		for (int i = 1; i <= nbVertex; i++) {
			String name = String.format("Vertex %d", i);
			Timing newt = new Timing((OperatorDefinition) archi
					.getComponentDefinition(ArchitectureComponentType.operator,
							"c64x"), graph.getVertex(name), 100);
			tmgr.addTiming(newt);
		}

		FASTTransformation transformation = new FASTTransformation();
		FastAlgoParameters parameters = new FastAlgoParameters(500, 500, 16,
				true, AbcType.LooselyTimed, EdgeSchedType.Simple);
		transformation.transform(graph, archi, parameters.textParameters(),
				scenario, null);

		logger.log(Level.FINER, "Test fast finished");
	}

	/**
	 * 
	 */
	public FASTTransformation() {
	}

	/**
	 * Function called while running the plugin
	 */
	@Override
	public TaskResult transform(SDFGraph algorithm,
			MultiCoreArchitecture architecture, TextParameters textParameters,
			IScenario scenario, IProgressMonitor monitor) {

		FastAlgoParameters parameters;
		TaskResult result = new TaskResult();

		parameters = new FastAlgoParameters(textParameters);

		MapperDAG dag = SdfToDagConverter.convert(algorithm, architecture,
				scenario, false);

		IAbc simu = new InfiniteHomogeneousAbc(parameters.getEdgeSchedType(),
				dag, architecture, parameters.getSimulatorType().getTaskSchedType());
		
		InitialLists initial = new InitialLists();

		if (!initial.constructInitialLists(dag, simu))
			return result;

		simu.resetDAG();

		IAbc simu2 = AbstractAbc.getInstance(parameters.getSimulatorType(),
				parameters.getEdgeSchedType(), dag, architecture);

		FastAlgorithm fastAlgorithm = new FastAlgorithm();

		PreesmLogger.getLogger().log(Level.INFO,"Mapping");
		dag = fastAlgorithm.map("test", parameters.getSimulatorType(),
				parameters.getEdgeSchedType(), dag, architecture, initial
						.getCpnDominant(), initial.getBlockingNodes(),
				initial.getCriticalpath(), parameters.getMaxCount(),
				parameters.getMaxStep(), parameters.getMargIn(), false, false,
				null, parameters.isDisplaySolutions(), monitor);

		PreesmLogger.getLogger().log(Level.INFO,"Mapping finished");
		
		// Transfer vertices are automatically regenerated
		simu2.setDAG(dag);

		// The transfers are reordered using the best found order during
		// scheduling
		simu2.reorder(fastAlgorithm.getBestTotalOrder());
		TagDAG tagSDF = new TagDAG();

		// The mapper dag properties are put in the property bean to be transfered to code generation
		tagSDF.tag(dag, architecture, scenario, simu2, parameters
				.getEdgeSchedType());
		
		result.setDAG(dag);
		simu2.resetTaskScheduler(TaskSchedType.Simple);
		result.setAbc(simu2);

		return result;
	}

	@Override
	public void transform(SDFGraph algorithm, SDFGraph transformedAlgorithm) {
		// TODO Auto-generated method stub

	}

}
