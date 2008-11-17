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
import org.ietr.preesm.plugin.abc.AbcType;
import org.ietr.preesm.plugin.abc.AbstractAbc;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.impl.AccuratelyTimedAbc;
import org.ietr.preesm.plugin.abc.impl.InfiniteHomogeneousAbc;
import org.ietr.preesm.plugin.abc.impl.LooselyTimedAbc;
import org.ietr.preesm.plugin.mapper.fastalgo.InitialLists;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.ReceiveVertex;
import org.ietr.preesm.plugin.mapper.model.impl.SendVertex;
import org.ietr.preesm.plugin.mapper.plot.GanttPlotter;
import org.ietr.preesm.plugin.mapper.tools.SchedulingOrderIterator;
import org.ietr.preesm.plugin.mapper.tools.TopologicalDAGIterator;
import org.sdf4j.model.PropertyBean;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFGraph;

/**
 * Generating the statistics to be displayed in stat editor
 * 
 * @author mpelcat
 */
public class StatGenerator {



	private MapperDAG dag = null;
	private SDFGraph sdf = null;
	private MultiCoreArchitecture archi = null;
	private IScenario scenario = null;
	private TextParameters params = null;
	private IAbc abc = null;

	public StatGenerator(MultiCoreArchitecture archi, MapperDAG dag,
			TextParameters params, IScenario scenario, SDFGraph sdf) {
		super();
		this.archi = archi;
		this.dag = dag;
		this.params = params;
		this.scenario = scenario;
		this.sdf = sdf;

		//initAbc();
		
		//getDAGComplexSpanLength();
		//getDAGComplexWorkLength();
		//getLoad(archi.getMainOperator());
	}
	
	/**
	 * The span is the shortest possible execution time. It is theoretic
	 * because no communication time is taken into account. We consider that we have
	 * an infinity of cores of main type totally connected with perfect media. The span
	 * complex because the DAG is not serial-parallel but can be any DAG. 
	 */
	public int getDAGComplexSpanLength(){

		MapperDAG taskDag = dag.clone();
		removeSendReceive(taskDag);
		
		MultiCoreArchitecture localArchi = archi.clone();

		MediumDefinition mainMediumDef = (MediumDefinition)localArchi.getMainMedium().getDefinition();
		mainMediumDef.setInvSpeed(0);
		mainMediumDef.setOverhead(0);
		
		IAbc simu = new InfiniteHomogeneousAbc(taskDag, localArchi);
		int span = simu.getFinalTime();
		
		PreesmLogger.getLogger().log(Level.INFO, "infinite homogeneous timing: " + span);
		
		return span;
		
	}

	/**
	 * The work is the sum of all task lengths 
	 */
	public int getDAGComplexWorkLength(){

		int work = 0;
		MapperDAG localDag = getDag().clone();
		StatGenerator.removeSendReceive(localDag);
		MultiCoreArchitecture archi = getArchi();
		
		
		if(localDag != null && archi != null){

			// Gets the appropriate abc to generate the gantt.
			PropertyBean bean = localDag.getPropertyBean();
			AbcType abctype = (AbcType)bean.getValue(AbstractAbc.propertyBeanName);
			
			IAbc simu = AbstractAbc
			.getInstance(abctype, localDag, archi);

			simu.resetDAG();
			simu.implantAllVerticesOnOperator(archi.getMainOperator());
			
			work = simu.getFinalTime();
			
			PreesmLogger.getLogger().log(Level.INFO, "single core timing: " + work);

			return work;
		}
		return -1;
	}
	
	public float getLoad(Operator operator){

		float load = 0;
		
		if(abc != null){
			int totalLatency = abc.getFinalTime();
			int operatorTime = 0;
			
			for(DAGVertex v : abc.getDAG().vertexSet()){
				MapperDAGVertex mv = (MapperDAGVertex)v;
				if(mv.getImplementationVertexProperty().getEffectiveComponent().equals(operator)){
					operatorTime += abc.getCost(mv);
				}
			}

			load = ((float)operatorTime)/totalLatency;
		}
		

		PreesmLogger.getLogger().log(Level.INFO, "load of " + operator.getName() + " : " + load);
		
		return load;
		
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

	
	public void initAbc(){

		MapperDAG localDag = getDag().clone();
		MultiCoreArchitecture archi = getArchi();
		
		
		if(localDag != null && archi != null){

			// Gets the appropriate abc to generate the gantt.
			PropertyBean bean = localDag.getPropertyBean();
			AbcType abctype = (AbcType)bean.getValue(AbstractAbc.propertyBeanName);
			
			abc = AbstractAbc
			.getInstance(abctype, localDag, archi);

			StatGenerator.removeSendReceive(localDag);

			abc.setDAG(localDag);			
			abc.getFinalTime();
			
			PreesmLogger.getLogger().log(Level.INFO, "stat abc of type " + abctype.toString() + " initialized");
		}
	}
}
