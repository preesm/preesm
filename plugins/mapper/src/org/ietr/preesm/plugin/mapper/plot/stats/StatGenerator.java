/**
 * 
 */
package org.ietr.preesm.plugin.mapper.plot.stats;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.impl.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.mapper.fastalgo.InitialLists;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Generating the statistics to be displayed in stat editor
 * @author mpelcat
 */
public class StatGenerator {



	private MapperDAG dag = null;
	private SDFGraph sdf = null;
	private MultiCoreArchitecture archi = null;
	private IScenario scenario = null;
	private TextParameters params = null;

	public StatGenerator(MultiCoreArchitecture archi, MapperDAG dag,
			TextParameters params, IScenario scenario, SDFGraph sdf) {
		super();
		this.archi = archi;
		this.dag = dag;
		this.params = params;
		this.scenario = scenario;
		this.sdf = sdf;
		
		getSpan();
	}
	
	public void getSpan(){

		InitialLists scheduler = new InitialLists();

		List<MapperDAGVertex> testCPN = new ArrayList<MapperDAGVertex>();
		List<MapperDAGVertex> testBL = new ArrayList<MapperDAGVertex>();
		List<MapperDAGVertex> testfcp = new ArrayList<MapperDAGVertex>();

		IAbc simu = new InfiniteHomogeneousAbc(dag, archi);
		simu.getFinalTime();
		
		scheduler.constructCPN(dag, testCPN, testBL, testfcp, simu);
		
		PreesmLogger.getLogger().log(Level.INFO, testCPN.toString());
	}

	public MapperDAG getDag() {
		return dag;
	}

	public SDFGraph getSdf() {
		return sdf;
	}

	public MultiCoreArchitecture getArchi() {
		return archi;
	}

	public IScenario getScenario() {
		return scenario;
	}

	public TextParameters getParams() {
		return params;
	}
}
