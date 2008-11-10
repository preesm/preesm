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


/**
 * 
 */
package org.ietr.preesm.plugin.mapper;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ietr.preesm.core.architecture.Examples;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.scenario.Scenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.core.scenario.TimingManager;
import org.ietr.preesm.core.task.TaskResult;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.core.workflow.sources.AlgorithmRetriever;
import org.ietr.preesm.plugin.abc.AbcType;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.impl.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.mapper.fastalgo.InitialLists;
import org.ietr.preesm.plugin.mapper.fastalgo.ListScheduler;
import org.ietr.preesm.plugin.mapper.fastalgo.ListSchedulingParameters;
import org.ietr.preesm.plugin.mapper.graphtransfo.SdfToDagConverter;
import org.ietr.preesm.plugin.mapper.graphtransfo.TagDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.sdf4j.demo.SDFAdapterDemo;
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
		logger.setLevel(Level.FINEST);

		// Generating archi
		MultiCoreArchitecture architecture = Examples.get2C64Archi();

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
		ListSchedulingParameters parameters = new ListSchedulingParameters(AbcType.LooselyTimed);

		SDFAdapterDemo applet1 = new SDFAdapterDemo();
		applet1.init(graph);
		
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
	public TaskResult transform(SDFGraph algorithm, MultiCoreArchitecture architecture,
			TextParameters textParameters,
			IScenario scenario) {

		
		TaskResult result = new TaskResult();
		ListSchedulingParameters parameters;
		
		parameters = new ListSchedulingParameters(textParameters);

		MapperDAG dag = SdfToDagConverter.convert(algorithm,architecture,scenario, false);
		
		IAbc simu = new InfiniteHomogeneousAbc(
				dag, architecture);

		InitialLists initial = new InitialLists();

		if(!initial.constructInitialLists(dag, simu))
				return null;

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
		
		//simu2.plotImplementation();
		PreesmLogger.getLogger().log(Level.FINER, "Plot finished");

		TagDAG tagSDF = new TagDAG();

		tagSDF.tag(dag,architecture,simu2);

		result.setDAG(dag);

		return result;
	}

}
