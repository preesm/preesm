/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2014)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.ietr.preesm.mapper.abc.impl.latency;

import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.abc.AbcType;
import org.ietr.preesm.mapper.abc.edgescheduling.EdgeSchedType;
import org.ietr.preesm.mapper.abc.taskscheduling.TaskSchedType;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.params.AbcParameters;
import org.ietr.preesm.mapper.tools.TLevelIterator;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.component.ComNode;

/**
 * Simulates an architecture having as many cores as necessary to execute one operation on one core. All core have the
 * main operator definition. These cores are all interconnected with media corresponding to the main medium definition.
 *
 * @author mpelcat
 */
public class InfiniteHomogeneousAbc extends LatencyAbc {

  /**
   * Constructor.
   *
   * @param params
   *          the params
   * @param dag
   *          the dag
   * @param archi
   *          the archi
   * @param scenario
   *          the scenario
   * @throws WorkflowException
   *           the workflow exception
   */
  public InfiniteHomogeneousAbc(final AbcParameters params, final MapperDAG dag, final Design archi,
      final PreesmScenario scenario) {
    this(params, dag, archi, TaskSchedType.SIMPLE, scenario);
  }

  /**
   * Constructor of the simulator from a "blank" implementation where every vertex has not been mapped yet.
   *
   * @param params
   *          the params
   * @param dag
   *          the dag
   * @param archi
   *          the archi
   * @param taskSchedType
   *          the task sched type
   * @param scenario
   *          the scenario
   * @throws WorkflowException
   *           the workflow exception
   */
  public InfiniteHomogeneousAbc(final AbcParameters params, final MapperDAG dag, final Design archi,
      final TaskSchedType taskSchedType, final PreesmScenario scenario) {
    super(params, dag, archi, AbcType.InfiniteHomogeneous, scenario);
    getType().setTaskSchedType(taskSchedType);

    final ComponentInstance mainComNode = DesignTools.getComponentInstance(archi,
        scenario.getSimulationManager().getMainComNodeName());

    final ComponentInstance mainOperator = DesignTools.getComponentInstance(archi,
        scenario.getSimulationManager().getMainOperatorName());

    if (mainComNode != null) {
      PreesmLogger.getLogger().info("Infinite homogeneous simulation");
    } else {
      PreesmLogger.getLogger()
          .severe("Current architecture has no main communication node. Please set a main communication node.");
    }

    if (mainOperator == null) {
      PreesmLogger.getLogger().severe("Current architecture has no main operator. Please set a main operator.");
    }

    // The InfiniteHomogeneousArchitectureSimulator is specifically done
    // to map all vertices on the main operator definition but consider
    // as many cores as there are tasks.
    mapAllVerticesOnOperator(mainOperator);

    updateFinalCosts();
    this.orderManager.resetTotalOrder();
    final TLevelIterator iterator = new TLevelIterator(this.implementation, true);

    while (iterator.hasNext()) {
      final MapperDAGVertex v = iterator.next();
      this.orderManager.addLast(v);
    }

    retrieveTotalOrder();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.impl.latency.LatencyAbc#fireNewMappedVertex(org.ietr.preesm.mapper.
   * model.MapperDAGVertex, boolean)
   */
  @Override
  protected void fireNewMappedVertex(final MapperDAGVertex vertex, final boolean updateRank) {

    final ComponentInstance effectiveOp = vertex.getEffectiveOperator();

    /*
     * mapping a vertex sets the cost of the current vertex and its edges
     *
     * As we have an infinite homogeneous architecture, each communication is done through the unique type of medium
     */
    if (effectiveOp == DesignTools.NO_COMPONENT_INSTANCE) {
      PreesmLogger.getLogger().severe("implementation of " + vertex.getName() + " failed. No operator was assigned.");

      vertex.getTiming().setCost(0);

    } else {

      // Setting vertex time
      final long vertextime = vertex.getInit().getTime(effectiveOp);
      vertex.getTiming().setCost(vertextime);

      // Setting edges times

      setEdgesCosts(vertex.incomingEdges());
      setEdgesCosts(vertex.outgoingEdges());

      if (updateRank) {
        this.nTimeKeeper.updateTLevels();
        this.taskScheduler.insertVertex(vertex);
      } else {
        this.orderManager.insertGivenTotalOrder(vertex);
      }

    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.impl.latency.LatencyAbc#fireNewUnmappedVertex(org.ietr.preesm.mapper
   * .model.MapperDAGVertex)
   */
  @Override
  protected void fireNewUnmappedVertex(final MapperDAGVertex vertex) {
    // unmapping a vertex resets the cost of the current vertex
    // and its edges
    // Keeps the total order
    this.orderManager.remove(vertex, false);
    vertex.getTiming().reset();
    resetCost(vertex.incomingEdges());
    resetCost(vertex.outgoingEdges());

  }

  /**
   * Asks the time keeper to update timings. Crucial and costly operation. Depending on the king of timings we want,
   * calls the necessary updates.
   */
  @Override
  public final void updateTimings() {
    this.nTimeKeeper.updateTandBLevels();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.impl.latency.LatencyAbc#setEdgeCost(org.ietr.preesm.mapper.model. MapperDAGEdge)
   */
  @Override
  protected void setEdgeCost(final MapperDAGEdge edge) {

    final long edgesize = edge.getInit().getDataSize();

    /**
     * In a Infinite Homogeneous Architecture, each communication is supposed to be done on the main medium. The
     * communication cost is simply calculated from the main medium speed.
     */
    final String mainComName = this.scenario.getSimulationManager().getMainComNodeName();
    final ComponentInstance mainCom = DesignTools.getComponentInstance(this.archi, mainComName);

    if (mainCom != null) {

      final long cost = (long) (edgesize / ((ComNode) mainCom.getComponent()).getSpeed());

      edge.getTiming().setCost(cost);
    } else {
      Float speed = 1f;
      speed = edgesize * speed;
      edge.getTiming().setCost(speed.intValue());
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.impl.latency.LatencyAbc#getEdgeSchedType()
   */
  @Override
  public EdgeSchedType getEdgeSchedType() {
    return null;
  }
}
