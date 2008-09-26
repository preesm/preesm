/**
 * 
 */
package org.ietr.preesm.plugin.mapper;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.ietr.preesm.core.constraints.IScenario;
import org.ietr.preesm.core.constraints.Scenario;
import org.ietr.preesm.core.constraints.Timing;
import org.ietr.preesm.core.constraints.TimingManager;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.core.workflow.sources.AlgorithmRetriever;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.ArchitectureSimulatorType;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.infinitehomogeneous.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.mapper.fastalgo.InitialLists;
import org.ietr.preesm.plugin.mapper.fastalgo.ListScheduler;
import org.ietr.preesm.plugin.mapper.fastalgo.ListSchedulingParameters;
import org.ietr.preesm.plugin.mapper.graphtransfo.SdfToDagConverter;
import org.ietr.preesm.plugin.mapper.graphtransfo.TagDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Plug-in class for list scheduling
 * 
 * @author pmenuet
 */
public class ListSchedulingTransformation extends AbstractMapping {

	/**
	 * Main for test
	 */
	public static void main(String[] args) {
		// FASTTransformation transformation = new FASTTransformation();
		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.FINER);

		// Generating archi
		MultiCoreArchitecture architecture = Examples.get4C64Archi();

		// Generating random sdf dag
		int nbVertex = 50, minInDegree = 1, maxInDegree = 3, minOutDegree = 1, maxOutDegree = 3;
		SDFGraph graph = AlgorithmRetriever.randomDAG(nbVertex, minInDegree,
				maxInDegree, minOutDegree, maxOutDegree, 50,true);
		
		
		// Generating constraints
		IScenario scenario = new Scenario();

		TimingManager tmanager = scenario.getTimingManager();

		Iterator<SDFAbstractVertex> it = graph.vertexSet().iterator();
		
		while(it.hasNext()){
			SDFAbstractVertex vertex = it.next();
			
			Timing t = new Timing((OperatorDefinition)architecture.getMainOperator().getDefinition(),vertex);
			t.setTime(100);
			tmanager.addTiming(t);
		}

		ListSchedulingTransformation transformation = new ListSchedulingTransformation();
		ListSchedulingParameters parameters = new ListSchedulingParameters(ArchitectureSimulatorType.AccuratelyTimed);
		
		
		transformation.transform(graph, architecture, parameters.textParameters(), scenario);

		logger.log(Level.FINER, "Test list scheduling finished");
	}
	
	/**
	 * 
	 */
	public ListSchedulingTransformation() {
	}


	@Override
	public void transform(SDFGraph algorithm, SDFGraph transformedAlgorithm) {
		
	}

	/**
	 * Function called while running the plugin
	 */
	@Override
	public TaskResult transform(SDFGraph algorithm, IArchitecture architecture,
			TextParameters textParameters,
			IScenario scenario) {

		
		TaskResult result = new TaskResult();
		ListSchedulingParameters parameters;
		
		parameters = new ListSchedulingParameters(textParameters);

		MapperDAG dag = SdfToDagConverter.convert(algorithm,architecture,scenario, false);
		
		IAbc simu = new InfiniteHomogeneousAbc(
				dag, architecture);

		InitialLists initial = new InitialLists();

		initial.constructInitialLists(dag, simu);

		simu.resetDAG();

		IAbc simu2 = AbstractAbc
				.getInstance(parameters.getSimulatorType(), dag, architecture);

		ListScheduler scheduler = new ListScheduler();

		scheduler.schedule(dag, initial.getCpnDominantList(), initial.getBlockingNodesList(),
			initial.getFinalcriticalpathList(), simu2, null, null);

		PreesmLogger.getLogger().log(Level.FINER, "Plotting");
		//Date date = new Date(1221555438562L);
		//Calendar cal = Calendar.getInstance();
		//DateFormat f = DateFormat.getDateInstance();
		
		simu2.plotImplementation();
		PreesmLogger.getLogger().log(Level.FINER, "Plot finished");

		TagDAG tagSDF = new TagDAG();

		tagSDF.tag(dag,architecture);

		result.setDAG(dag);

		return result;
	}

}
