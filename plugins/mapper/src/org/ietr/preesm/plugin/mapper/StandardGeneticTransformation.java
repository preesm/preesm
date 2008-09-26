/**
 * 
 */
package org.ietr.preesm.plugin.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
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
import org.ietr.preesm.plugin.mapper.fastalgo.FastAlgoParameters;
import org.ietr.preesm.plugin.mapper.fastalgo.FastPopulation;
import org.ietr.preesm.plugin.mapper.fastalgo.InitialLists;
import org.ietr.preesm.plugin.mapper.geneticalgo.Chromosome;
import org.ietr.preesm.plugin.mapper.geneticalgo.GeneticAlgoParameters;
import org.ietr.preesm.plugin.mapper.geneticalgo.StandardGeneticAlgorithm;
import org.ietr.preesm.plugin.mapper.graphtransfo.SdfToDagConverter;
import org.ietr.preesm.plugin.mapper.graphtransfo.TagDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.pfastalgo.PFastAlgoParameters;
import org.ietr.preesm.plugin.mapper.pfastalgo.PFastAlgorithm;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Plug-in class for genetic algorithm
 * 
 * @author pmenuet
 */
public class StandardGeneticTransformation extends AbstractMapping {

	/**
	 * Main for test
	 */
	public static void main(String[] args) {
		// FASTTransformation transformation = new FASTTransformation();
		Logger logger = PreesmLogger.getLogger();
		logger.setLevel(Level.FINER);

		// PreesmLogger.getLogger().setLevel(Level.FINER);

		// Generating archi
		MultiCoreArchitecture archi = Examples.get4C64Archi();

		// Generating random sdf dag
		int nbVertex = 100, minInDegree = 1, maxInDegree = 3, minOutDegree = 1, maxOutDegree = 3;
		SDFGraph graph = AlgorithmRetriever.randomDAG(nbVertex, minInDegree,
				maxInDegree, minOutDegree, maxOutDegree, 50,true);

		// Generating constraints
		IScenario scenario = new Scenario();

		TimingManager tmgr = scenario.getTimingManager();

		for (int i = 1; i <= nbVertex; i++) {
			String name = String.format("Vertex %d", i);
			Timing newt = new Timing(archi.getOperatorDefinition("c64x"), graph
					.getVertex(name), 100);
			tmgr.addTiming(newt);
		}

		StandardGeneticTransformation transformation = new StandardGeneticTransformation();
		GeneticAlgoParameters parameters = new GeneticAlgoParameters(100, 25,
				ArchitectureSimulatorType.ApproximatelyTimed, false);

		transformation.transform(graph, archi, parameters.textParameters(), scenario);

		logger.log(Level.FINER, "Test fast finished");
	}

	/**
	 * 
	 */
	public StandardGeneticTransformation() {
	}

	/**
	 * Function called while running the plugin
	 */
	@Override
	public TaskResult transform(SDFGraph algorithm, IArchitecture architecture,
			TextParameters textParameters,
			IScenario scenario) {

		
		TaskResult transfoResult = new TaskResult();
		GeneticAlgoParameters parameters;

		parameters = new GeneticAlgoParameters(textParameters);

		MapperDAG dag = SdfToDagConverter.convert(algorithm,architecture,scenario, false);

		IAbc simu = new InfiniteHomogeneousAbc(
				dag, architecture);

		InitialLists initial = new InitialLists();

		initial.constructInitialLists(dag, simu);

		simu.resetDAG();

		List<MapperDAG> populationDAG = new ArrayList<MapperDAG>();

		if (parameters.isPfastused2makepopulation()) {
			PFastAlgorithm pfastAlgorithm = new PFastAlgorithm();

			PFastAlgoParameters parameter = new PFastAlgoParameters(8, 20, 16, 5,
					3, parameters.getSimulatorType());

			dag = pfastAlgorithm.map(dag, architecture, parameter
					.getProcNumber(), parameter.getNodesmin(), initial,
					parameter.getMaxCount(), parameter.getMaxStep(), parameter
							.getMargIn(), parameters.getSimulatorType(), true,
					parameters.getPopulationSize(), populationDAG);

		} else {

			FastPopulation population = new FastPopulation(parameters
					.getPopulationSize(), populationDAG, parameters
					.getSimulatorType(), architecture);

			FastAlgoParameters parameter = new FastAlgoParameters(50, 50, 8,
					parameters.getSimulatorType());

			population.constructPopulation(dag, parameter.getMaxCount(),
					parameter.getMaxStep(), parameter.getMargIn());
		}

		IAbc simu2 = AbstractAbc
				.getInstance(parameters.getSimulatorType(), dag, architecture);

		StandardGeneticAlgorithm geneticAlgorithm = new StandardGeneticAlgorithm();

		ConcurrentSkipListSet<Chromosome> result;
		result = geneticAlgorithm.runGeneticAlgo("test", populationDAG,
				architecture, parameters.getSimulatorType(), parameters
						.getPopulationSize(), parameters.getGenerationNumber(),
				false);

		Chromosome chromosome = result.first().clone();

		chromosome.evaluate(parameters.getSimulatorType());

		dag = chromosome.getDag();

		simu2.setDAG(dag);

		simu2.plotImplementation();

		TagDAG tagSDF = new TagDAG();

		tagSDF.tag(dag,architecture);

		transfoResult.setDAG(dag);

		return transfoResult;
	}

	@Override
	public void transform(SDFGraph algorithm, SDFGraph transformedAlgorithm) {
		// TODO Auto-generated method stub
	}


}
