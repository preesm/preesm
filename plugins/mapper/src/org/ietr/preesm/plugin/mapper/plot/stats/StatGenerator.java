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

package org.ietr.preesm.plugin.mapper.plot.stats;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.IAbc;
import org.ietr.preesm.plugin.abc.impl.latency.LatencyAbc;
import org.ietr.preesm.plugin.abc.impl.latency.SpanLengthCalculator;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.InvolvementVertex;
import org.ietr.preesm.plugin.mapper.model.impl.OverheadVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdge;
import org.ietr.preesm.plugin.mapper.model.impl.ReceiveVertex;
import org.ietr.preesm.plugin.mapper.model.impl.SendVertex;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;

/**
 * Generating the statistics to be displayed in stat editor
 * 
 * @author mpelcat
 */
public class StatGenerator {

	private LatencyAbc abc = null;

	private IScenario scenario = null;
	private TextParameters params = null;
	private long finalTime = 0;

	public StatGenerator(IAbc abc, IScenario scenario, TextParameters params) {
		super();
		this.params = params;
		this.scenario = scenario;
		if (abc instanceof LatencyAbc) {
			this.abc = (LatencyAbc) abc;
			
			this.abc.updateFinalCosts();
			this.finalTime = this.abc.getFinalLatency();
		} else {
			PreesmLogger.getLogger().log(Level.SEVERE,
					"Statistics can only be generated from latency ABCs.");
		}
	}

	/**
	 * 
	 */
	/*
	 * public long getDAGComplexSpanLength(RouteCalculator routeCalculator) {
	 * 
	 * MapperDAG taskDag = abc.getDAG().clone(); removeSendReceive(taskDag);
	 * 
	 * MultiCoreArchitecture localArchi = abc.getArchitecture().clone();
	 * 
	 * MediumDefinition mainMediumDef = (MediumDefinition) localArchi
	 * .getMainMedium().getDefinition(); mainMediumDef.setDataRate(0);
	 * mainMediumDef.setOverhead(0);
	 * 
	 * IAbc simu = new InfiniteHomogeneousAbc(EdgeSchedType.Simple, taskDag,
	 * localArchi, scenario); simu.updateFinalCosts(); long span =
	 * simu.getFinalCost();
	 * 
	 * PreesmLogger.getLogger().log(Level.INFO, "infinite homogeneous timing: "
	 * + span);
	 * 
	 * return span;
	 * 
	 * }
	 */

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
		Object span = abc.getDAG().getPropertyBean().getValue(
				SpanLengthCalculator.DAG_SPAN);
		if (span != null && span instanceof Long) {
			return (Long) span;
		}
		return 0;
	}

	/**
	 * The work is the sum of all task lengths excluding vertices added by the mapping.
	 */
	public long getDAGWorkLength() {

		long work = 0;
		MapperDAG dag = abc.getDAG();
		Operator mainOp = abc.getArchitecture().getMainOperator();
		
		for (DAGVertex vertex : dag.vertexSet()) {
			if (!(vertex instanceof TransferVertex)
					&& !(vertex instanceof OverheadVertex)
					&& !(vertex instanceof InvolvementVertex)) {

				// Looks for an operator able to execute currentvertex (preferably
				// the given operator)
				Operator adequateOp = abc.findOperator((MapperDAGVertex)vertex, mainOp);
				
				work += ((MapperDAGVertex) vertex).getInitialVertexProperty()
						.getTime(adequateOp);

				/*PreesmLogger.getLogger().log(
						Level.INFO,
						"task "
								+ vertex.getName()
								+ " duration "
								+ ((MapperDAGVertex) vertex)
										.getInitialVertexProperty().getTime(
												adequateOp));*/
			}
		}

		return work;
	}

	/**
	 * Returns the final time of the current ABC
	 */
	public long getResultTime() {
		if (abc instanceof LatencyAbc) {
			return abc.getFinalLatency();
		}

		return 0;
	}

	/**
	 * Returns the number of operators in the current architecture that execute
	 * vertices
	 */
	public int getNbUsedOperators() {
		int nbUsedOperators = 0;
		for (ArchitectureComponent o : abc.getArchitecture().getComponents(
				ArchitectureComponentType.operator)) {
			if (abc.getFinalCost((Operator) o) > 0) {
				nbUsedOperators++;
			}
		}
		return nbUsedOperators;
	}

	/**
	 * The load is the percentage of a processing resource used for the given
	 * algorithm
	 */
	public Long getLoad(Operator operator) {

		return abc.getLoad(operator);
	}

	/**
	 * The memory is the sum of all buffers allocated by the mapping
	 */
	public Integer getMem(Operator operator) {

		int mem = 0;

		if (abc != null) {

			for (DAGEdge e : abc.getDAG().edgeSet()) {
				MapperDAGEdge me = (MapperDAGEdge) e;
				MapperDAGVertex scr = (MapperDAGVertex) me.getSource();
				MapperDAGVertex tgt = (MapperDAGVertex) me.getTarget();

				if (!(me instanceof PrecedenceEdge)) {
					Operator srcOp = (Operator) scr
							.getImplementationVertexProperty()
							.getEffectiveComponent();
					Operator tgtOp = (Operator) tgt
							.getImplementationVertexProperty()
							.getEffectiveComponent();

					if (srcOp.equals(operator) || tgtOp.equals(operator)) {
						mem += me.getInitialEdgeProperty().getDataSize();
					}
				}
			}
		}

		return mem;

	}

	public IScenario getScenario() {
		return scenario;
	}

	public TextParameters getParams() {
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
