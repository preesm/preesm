/**
 * 
 */
package org.ietr.preesm.plugin.mapper.plot.stats;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.MediumDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.impl.AccuratelyTimedAbc;
import org.ietr.preesm.plugin.abc.impl.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.abc.impl.LooselyTimedAbc;
import org.ietr.preesm.plugin.mapper.fastalgo.InitialLists;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.ReceiveVertex;
import org.ietr.preesm.plugin.mapper.model.impl.SendVertex;
import org.ietr.preesm.plugin.mapper.tools.TopologicalDAGIterator;
import org.sdf4j.model.dag.DAGVertex;
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

		MapperDAG taskDag = dag.clone();
		removeSendReceive(taskDag);
		//getDAGComplexWorkLength(taskDag);
		getDAGComplexSpanLength(taskDag);
	}
	
	public int getDAGComplexSpanLength(MapperDAG taskDag){

		MultiCoreArchitecture localArchi = archi.clone();

		MediumDefinition mainMediumDef = (MediumDefinition)localArchi.getMainMedium().getDefinition();
		mainMediumDef.setInvSpeed(0);
		mainMediumDef.setOverhead(0);
		
		IAbc simu = new InfiniteHomogeneousAbc(taskDag, localArchi);
		int span = simu.getFinalTime();
		
		PreesmLogger.getLogger().log(Level.INFO, "infinite homogeneous timing: " + span);
		
		return span;
		
	}
	
	public int getDAGComplexWorkLength(MapperDAG taskDag){
		
		IAbc simu = new AccuratelyTimedAbc(taskDag, archi);
		simu.implantAllVerticesOnOperator(archi.getMainOperator());
		
		int work = simu.getFinalTime();
		
		PreesmLogger.getLogger().log(Level.INFO, "Single core timing: " + work);
		
		return work;
		
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
	
	public static void removeSendReceive(MapperDAG localDag){

		// Every send and receive vertices are removed
		Set<DAGVertex> vset = new HashSet<DAGVertex>(localDag.vertexSet());
		for(DAGVertex v:vset)
			if(v instanceof SendVertex || v instanceof ReceiveVertex)
				localDag.removeVertex(v);
		
	}
}
