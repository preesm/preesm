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

package org.ietr.preesm.mapper.ui.stats;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.abc.IAbc;
import org.ietr.preesm.mapper.abc.impl.latency.LatencyAbc;
import org.ietr.preesm.mapper.abc.impl.latency.SpanLengthCalculator;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.special.InvolvementVertex;
import org.ietr.preesm.mapper.model.special.OverheadVertex;
import org.ietr.preesm.mapper.model.special.PrecedenceEdge;
import org.ietr.preesm.mapper.model.special.ReceiveVertex;
import org.ietr.preesm.mapper.model.special.SendVertex;
import org.ietr.preesm.mapper.model.special.TransferVertex;

/**
 * Generating the statistics to be displayed in stat editor
 * 
 * @author mpelcat
 */
public class StatGenerator {

	private IAbc abc = null;

	private PreesmScenario scenario = null;
	private Map<String, String> params = null;
	private long finalTime = 0;

	public StatGenerator(IAbc abc, PreesmScenario scenario,
			Map<String, String> params) {
		super();
		this.params = params;
		this.scenario = scenario;
		if (abc instanceof LatencyAbc) {
			this.abc = abc;

			this.abc.updateFinalCosts();
			this.finalTime = ((LatencyAbc) this.abc).getFinalLatency();
		} else {
			this.abc = abc;
			this.abc.updateFinalCosts();
			this.finalTime = 0;
		}
	}

	/**
	 * The span is the shortest possible execution time. It is theoretic because
	 * no communication time is taken into account. We consider that we have an
	 * infinity of cores of main type totally connected with perfect media. The
	 * span complex because the DAG is not serial-parallel but can be any DAG.
	 * It is retrieved from the DAG if it was set from the infinite homogeneous
	 * simulation. If there was no such simulation, the span length can not be
	 * recalculated because the original DAG without transfers is no more
	 * available.
	 */
	public long getDAGSpanLength() {
		Object span = abc.getDAG().getPropertyBean()
				.getValue(SpanLengthCalculator.DAG_SPAN);
		if (span != null && span instanceof Long) {
			return (Long) span;
		}
		return 0;
	}

	/**
	 * The work is the sum of all task lengths excluding vertices added by the
	 * mapping.
	 */
	public long getDAGWorkLength() throws WorkflowException {

		long work = 0;
		MapperDAG dag = abc.getDAG();

		ComponentInstance mainOp = DesignTools.getComponentInstance(abc
				.getArchitecture(), scenario.getSimulationManager()
				.getMainOperatorName());

		for (DAGVertex vertex : dag.vertexSet()) {
			if (!(vertex instanceof TransferVertex)
					&& !(vertex instanceof OverheadVertex)
					&& !(vertex instanceof InvolvementVertex)) {

				// Looks for an operator able to execute currentvertex
				// (preferably
				// the given operator)
				ComponentInstance adequateOp = abc.findOperator(
						(MapperDAGVertex) vertex, mainOp);

				work += ((MapperDAGVertex) vertex).getInit()
						.getTime(adequateOp);

				/*
				 * PreesmLogger.getLogger().log( Level.INFO, "task " +
				 * vertex.getName() + " duration " + ((MapperDAGVertex) vertex)
				 * .getInitialVertexProperty().getTime( adequateOp));
				 */
			}
		}

		return work;
	}

	/**
	 * Returns the final time of the current ABC
	 */
	public long getResultTime() {
		if (abc instanceof LatencyAbc) {
			return ((LatencyAbc) abc).getFinalLatency();
		} else {
			return 0l;
		}
	}

	/**
	 * Returns the number of operators in the current architecture that execute
	 * vertices
	 */
	public int getNbUsedOperators() {
		int nbUsedOperators = 0;
		for (ComponentInstance o : DesignTools.getOperatorInstances(abc
				.getArchitecture())) {
			if (abc.getFinalCost(o) > 0) {
				nbUsedOperators++;
			}
		}
		return nbUsedOperators;
	}

	/**
	 * Returns the number of operators with main type
	 */
	public int getNbMainTypeOperators() {
		int nbMainTypeOperators = 0;
		ComponentInstance mainOp = DesignTools.getComponentInstance(abc
				.getArchitecture(), scenario.getSimulationManager()
				.getMainOperatorName());
		nbMainTypeOperators = DesignTools.getInstancesOfComponent(
				abc.getArchitecture(), mainOp.getComponent()).size();
		return nbMainTypeOperators;
	}

	/**
	 * The load is the percentage of a processing resource used for the given
	 * algorithm
	 */
	public long getLoad(ComponentInstance operator) {

		if (abc instanceof LatencyAbc) {
			return ((LatencyAbc) abc).getLoad(operator);
		} else {
			return 0l;
		}
	}

	/**
	 * The memory is the sum of all buffers allocated by the mapping
	 */
	public Integer getMem(ComponentInstance operator) {

		int mem = 0;

		if (abc != null) {

			for (DAGEdge e : abc.getDAG().edgeSet()) {
				MapperDAGEdge me = (MapperDAGEdge) e;
				MapperDAGVertex scr = (MapperDAGVertex) me.getSource();
				MapperDAGVertex tgt = (MapperDAGVertex) me.getTarget();
				
				if(!e.getSource().getPropertyBean().getValue("vertexType").toString().equals("task")
						|| !e.getTarget().getPropertyBean().getValue("vertexType").toString().equals("task")){
					// Skip the edge if source or target is not a task
					continue;
				}
					

				if (!(me instanceof PrecedenceEdge)) {
					ComponentInstance srcOp = scr
							
							.getEffectiveComponent();
					ComponentInstance tgtOp = tgt
							
							.getEffectiveComponent();

					if (srcOp.getInstanceName().equals(
							operator.getInstanceName())
							|| tgtOp.getInstanceName().equals(
									operator.getInstanceName())) {
						mem += me.getInit().getDataSize();
					}
				}
			}
		}

		return mem;

	}

	public PreesmScenario getScenario() {
		return scenario;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public long getFinalTime() {
		return finalTime;
	}

	public IAbc getAbc() {
		return abc;
	}

	public static void removeSendReceive(MapperDAG localDag) {

		// Every send and receive vertices are removed
		Set<DAGVertex> vset = new HashSet<DAGVertex>(localDag.vertexSet());
		for (DAGVertex v : vset)
			if (v instanceof SendVertex || v instanceof ReceiveVertex)
				localDag.removeVertex(v);

	}
}
