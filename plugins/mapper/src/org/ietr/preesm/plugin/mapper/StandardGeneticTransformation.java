/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

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

package org.ietr.preesm.plugin.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

import net.sf.dftools.workflow.WorkflowException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.scenario.SDFAndArchitectureScenario;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.impl.latency.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.abc.taskscheduling.TopologicalTaskSched;
import org.ietr.preesm.plugin.mapper.algo.fast.FastPopulation;
import org.ietr.preesm.plugin.mapper.algo.genetic.Chromosome;
import org.ietr.preesm.plugin.mapper.algo.genetic.StandardGeneticAlgorithm;
import org.ietr.preesm.plugin.mapper.algo.list.InitialLists;
import org.ietr.preesm.plugin.mapper.algo.pfast.PFastAlgorithm;
import org.ietr.preesm.plugin.mapper.graphtransfo.SdfToDagConverter;
import org.ietr.preesm.plugin.mapper.graphtransfo.TagDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.params.AbcParameters;
import org.ietr.preesm.plugin.mapper.params.FastAlgoParameters;
import org.ietr.preesm.plugin.mapper.params.GeneticAlgoParameters;
import org.ietr.preesm.plugin.mapper.params.PFastAlgoParameters;
import org.sdf4j.model.parameters.InvalidExpressionException;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Plug-in class for genetic algorithm
 * 
 * @author pmenuet
 */
public class StandardGeneticTransformation extends AbstractMapping {

	/**
	 * 
	 */
	public StandardGeneticTransformation() {
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = super.getDefaultParameters();

		parameters.put("populationSize", "10");
		parameters.put("generationNumber", "10");
		parameters.put("pfastused2makepopulation", "false");

		parameters.put("fastTime", "100");
		parameters.put("fastLocalSearchTime", "10");
		parameters.put("fastNumber", "1");

		return parameters;
	}

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName) throws WorkflowException {

		Map<String, Object> outputs = new HashMap<String, Object>();
		MultiCoreArchitecture architecture = (MultiCoreArchitecture) inputs
				.get("architecture");
		SDFGraph algorithm = (SDFGraph) inputs.get("SDF");
		SDFAndArchitectureScenario scenario = (SDFAndArchitectureScenario) inputs.get("scenario");

		super.execute(inputs, parameters, monitor, nodeName);

		GeneticAlgoParameters genParams = new GeneticAlgoParameters(parameters);
		AbcParameters abcParameters = new AbcParameters(parameters);

		MapperDAG dag = SdfToDagConverter.convert(algorithm, architecture,
				scenario, false);

		// calculates the DAG span length on the architecture main operator (the
		// tasks that can
		// not be executed by the main operator are deported without transfer
		// time to other operator
		calculateSpan(dag, architecture, scenario, abcParameters);

		IAbc simu = new InfiniteHomogeneousAbc(abcParameters, dag,
				architecture, abcParameters.getSimulatorType()
						.getTaskSchedType(), scenario);

		InitialLists initial = new InitialLists();

		if (!initial.constructInitialLists(dag, simu))
			return null;

		TopologicalTaskSched taskSched = new TopologicalTaskSched(
				simu.getTotalOrder());
		simu.resetDAG();

		List<MapperDAG> populationDAG = new ArrayList<MapperDAG>();

		if (genParams.isPfastused2makepopulation()) {
			PFastAlgorithm pfastAlgorithm = new PFastAlgorithm();

			PFastAlgoParameters pFastParams = new PFastAlgoParameters(
					genParams.getFastNumber(), genParams.getFastTime(),
					genParams.getFastLocalSearchTime(), true, 5, 3);

			dag = pfastAlgorithm.map(dag, architecture, scenario, initial,
					abcParameters, pFastParams, true,
					genParams.getPopulationSize(), true, populationDAG,
					taskSched);

		} else {

			FastPopulation population = new FastPopulation(
					genParams.getPopulationSize(), populationDAG,
					abcParameters, architecture);

			FastAlgoParameters fastParams = new FastAlgoParameters(
					genParams.getFastTime(),
					genParams.getFastLocalSearchTime(), false);

			population.constructPopulation(dag, scenario, fastParams);
		}

		IAbc simu2 = AbstractAbc.getInstance(abcParameters, dag, architecture,
				scenario);

		StandardGeneticAlgorithm geneticAlgorithm = new StandardGeneticAlgorithm();

		ConcurrentSkipListSet<Chromosome> result;
		result = geneticAlgorithm.runGeneticAlgo("test", populationDAG,
				architecture, scenario, abcParameters,
				genParams.getPopulationSize(), genParams.getGenerationNumber(),
				false);

		Chromosome chromosome = result.first().clone();

		chromosome.evaluate(abcParameters);

		dag = chromosome.getDag();

		simu2.setDAG(dag);

		// simu2.plotImplementation();

		TagDAG tagSDF = new TagDAG();

		try {
			tagSDF.tag(dag, architecture, scenario, simu2,
					abcParameters.getEdgeSchedType());
		} catch (InvalidExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw (new WorkflowException(e.getMessage()));
		}

		outputs.put("DAG", dag);
		outputs.put("ABC", simu2);

		super.clean(architecture, scenario);
		return outputs;
	}
}
