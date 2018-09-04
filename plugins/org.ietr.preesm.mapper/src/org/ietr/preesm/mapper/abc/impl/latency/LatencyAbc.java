/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2009 - 2016)
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.abc.AbcType;
import org.ietr.preesm.mapper.abc.AbstractAbc;
import org.ietr.preesm.mapper.abc.edgescheduling.AbstractEdgeSched;
import org.ietr.preesm.mapper.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.mapper.abc.impl.ImplementationCleaner;
import org.ietr.preesm.mapper.abc.order.VertexOrderList;
import org.ietr.preesm.mapper.abc.route.CommunicationRouter;
import org.ietr.preesm.mapper.gantt.GanttData;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.special.PrecedenceEdgeAdder;
import org.ietr.preesm.mapper.params.AbcParameters;
import org.ietr.preesm.mapper.timekeeper.TimeKeeper;
import org.ietr.preesm.mapper.tools.SchedulingOrderIterator;

/**
 * Abc that minimizes latency.
 *
 * @author mpelcat
 */
public abstract class LatencyAbc extends AbstractAbc {

  /** Current time keeper: called exclusively by simulator to update the useful time tags in DAG. */
  protected TimeKeeper nTimeKeeper;

  /** The com router. */
  protected CommunicationRouter comRouter = null;

  /** Scheduling the transfer vertices on the media. */
  protected IEdgeSched edgeScheduler;

  /** Current abc parameters. */
  protected AbcParameters params;

  /**
   * Constructor of the simulator from a "blank" implementation where every vertex has not been mapped yet.
   *
   * @param params
   *          the params
   * @param dag
   *          the dag
   * @param archi
   *          the archi
   * @param abcType
   *          the abc type
   * @param scenario
   *          the scenario
   */
  public LatencyAbc(final AbcParameters params, final MapperDAG dag, final Design archi, final AbcType abcType,
      final PreesmScenario scenario) {
    super(dag, archi, abcType, scenario);

    this.params = params;

    this.nTimeKeeper = new TimeKeeper(this.implementation, this.orderManager);
    this.nTimeKeeper.resetTimings();

    // The media simulator calculates the edges costs
    this.edgeScheduler = AbstractEdgeSched.getInstance(params.getEdgeSchedType(), this.orderManager);
    this.comRouter = new CommunicationRouter(archi, scenario, this.implementation, this.edgeScheduler,
        this.orderManager);
  }

  /**
   * Sets the DAG as current DAG and retrieves all implementation to calculate timings.
   *
   * @param dag
   *          the new dag
   * @throws WorkflowException
   *           the workflow exception
   */
  @Override
  public void setDAG(final MapperDAG dag) {

    this.dag = dag;
    this.implementation = dag.clone();

    this.orderManager.reconstructTotalOrderFromDAG(this.implementation);

    this.nTimeKeeper = new TimeKeeper(this.implementation, this.orderManager);
    this.nTimeKeeper.resetTimings();

    // Forces the unmapping process before the new mapping process
    final Map<MapperDAGVertex, ComponentInstance> operators = new LinkedHashMap<>();

    for (final DAGVertex v : dag.vertexSet()) {
      final MapperDAGVertex mdv = (MapperDAGVertex) v;
      operators.put(mdv, mdv.getEffectiveOperator());
      mdv.setEffectiveComponent(DesignTools.NO_COMPONENT_INSTANCE);
      this.implementation.getMapperDAGVertex(mdv.getName()).setEffectiveComponent(DesignTools.NO_COMPONENT_INSTANCE);
    }

    this.edgeScheduler = AbstractEdgeSched.getInstance(this.edgeScheduler.getEdgeSchedType(), this.orderManager);
    this.comRouter.setManagers(this.implementation, this.edgeScheduler, this.orderManager);

    final SchedulingOrderIterator iterator = new SchedulingOrderIterator(this.dag, this, true);

    while (iterator.hasNext()) {
      final MapperDAGVertex vertex = iterator.next();
      final ComponentInstance operator = operators.get(vertex);

      map(vertex, operator, false, false);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.AbstractAbc#fireNewMappedVertex(org.ietr.preesm.mapper.model. MapperDAGVertex,
   * boolean)
   */
  @Override
  protected void fireNewMappedVertex(final MapperDAGVertex vertex, final boolean updateRank) {

    final ComponentInstance effectiveOp = vertex.getEffectiveOperator();

    if (effectiveOp == DesignTools.NO_COMPONENT_INSTANCE) {
      WorkflowLogger.getLogger().severe("implementation of " + vertex.getName() + " failed");
    } else {

      final long vertextime = vertex.getInit().getTime(effectiveOp);

      // Set costs
      vertex.getTiming().setCost(vertextime);

      setEdgesCosts(vertex.incomingEdges());
      setEdgesCosts(vertex.outgoingEdges());

      if (updateRank) {
        updateTimings();
        this.taskScheduler.insertVertex(vertex);
      } else {
        this.orderManager.insertGivenTotalOrder(vertex);
      }

    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.AbstractAbc#fireNewUnmappedVertex(org.ietr.preesm.mapper.model. MapperDAGVertex)
   */
  @Override
  protected void fireNewUnmappedVertex(final MapperDAGVertex vertex) {

    // unmapping a vertex resets the cost of the current vertex
    // and its edges

    final ImplementationCleaner cleaner = new ImplementationCleaner(this.orderManager, this.implementation);
    cleaner.removeAllOverheads(vertex);
    cleaner.removeAllInvolvements(vertex);
    cleaner.removeAllTransfers(vertex);
    cleaner.unscheduleVertex(vertex);

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
  public void updateTimings() {
    this.nTimeKeeper.updateTLevels();
  }

  /**
   * Setting edge costs for special types.
   *
   * @param edge
   *          the new edge cost
   */
  @Override
  protected void setEdgeCost(final MapperDAGEdge edge) {

  }

  /**
   * *********Timing accesses**********.
   *
   * @param vertex
   *          the vertex
   * @return the final cost
   */

  /**
   * The cost of a vertex is the end time of its execution (latency minimization)
   */
  @Override
  public final long getFinalCost(MapperDAGVertex vertex) {
    vertex = translateInImplementationVertex(vertex);

    final long finalTime = this.nTimeKeeper.getFinalTime(vertex);

    if (finalTime < 0) {
      WorkflowLogger.getLogger().log(Level.SEVERE, "negative vertex final time");
    }

    return finalTime;

  }

  /**
   * The cost of a component is the end time of its last vertex (latency minimization).
   *
   * @param component
   *          the component
   * @return the final cost
   */
  @Override
  public final long getFinalCost(final ComponentInstance component) {
    return this.nTimeKeeper.getFinalTime(component);
  }

  /**
   * The cost of an implementation is calculated from its latency and loads.
   *
   * @return the final cost
   */
  @Override
  public final long getFinalCost() {

    long cost = getFinalLatency();

    if (this.params.isBalanceLoads()) {
      final long loadBalancing = evaluateLoadBalancing();
      cost += loadBalancing;
    }
    return cost;
  }

  /**
   * The cost of an implementation is calculated from its latency and loads.
   *
   * @return the final latency
   */
  public final long getFinalLatency() {

    final long finalTime = this.nTimeKeeper.getFinalTime();

    if (finalTime < 0) {
      WorkflowLogger.getLogger().log(Level.SEVERE, "negative implementation final latency");
    }

    return finalTime;
  }

  /**
   * Gets the t level.
   *
   * @param vertex
   *          the vertex
   * @param update
   *          the update
   * @return the t level
   */
  public final long getTLevel(MapperDAGVertex vertex, final boolean update) {
    vertex = translateInImplementationVertex(vertex);

    if (update) {
      updateTimings();
    }
    return vertex.getTiming().getTLevel();
  }

  /**
   * Gets the b level.
   *
   * @param vertex
   *          the vertex
   * @param update
   *          the update
   * @return the b level
   */
  public final long getBLevel(MapperDAGVertex vertex, final boolean update) {
    vertex = translateInImplementationVertex(vertex);

    if (update) {
      updateTimings();
    }
    return vertex.getTiming().getBLevel();
  }

  /**
   * Extracting from the Abc information the data to display in the Gantt chart.
   *
   * @return the gantt data
   */
  @Override
  public GanttData getGanttData() {
    final GanttData ganttData = new GanttData();
    ganttData.insertDag(this.implementation);
    return ganttData;
  }

  /**
   * Gets the com router.
   *
   * @return the com router
   */
  public CommunicationRouter getComRouter() {
    return this.comRouter;
  }

  /**
   * Gives an index evaluating the load balancing. This index is actually the standard deviation of the loads considered
   * as values of a random variable
   *
   * @return the long
   */
  public long evaluateLoadBalancing() {

    final List<Long> taskSums = new ArrayList<>();
    long totalTaskSum = 0L;

    for (final ComponentInstance o : this.orderManager.getArchitectureComponents()) {
      final long load = getLoad(o);

      if (load > 0) {
        taskSums.add(load);
        totalTaskSum += load;
      }
    }

    if (!taskSums.isEmpty()) {
      Collections.sort(taskSums, (arg0, arg1) -> {
        long temp = arg0 - arg1;
        if (temp >= 0) {
          temp = 1;
        } else {
          temp = -1;
        }
        return (int) temp;
      });

      final long mean = totalTaskSum / taskSums.size();
      long variance = 0;
      // Calculating the load sum of half the components with the lowest
      // loads
      for (final long taskDuration : taskSums) {
        variance += ((taskDuration - mean) * (taskDuration - mean)) / taskSums.size();
      }

      return (long) Math.sqrt(variance);
    }

    return 0;
  }

  /**
   * Returns the sum of execution times on the given component.
   *
   * @param component
   *          the component
   * @return the load
   */
  public final long getLoad(final ComponentInstance component) {
    return this.orderManager.getBusyTime(component);
  }

  @Override
  public void updateFinalCosts() {
    updateTimings();
  }

  /**
   * Reorders the implementation using the given total order.
   *
   * @param totalOrder
   *          the total order
   */
  @Override
  public void reschedule(final VertexOrderList totalOrder) {

    if ((this.implementation != null) && (this.dag != null)) {

      // Sets the order in the implementation
      for (final VertexOrderList.OrderProperty vP : totalOrder.elements()) {
        final MapperDAGVertex implVertex = (MapperDAGVertex) this.implementation.getVertex(vP.getName());
        if (implVertex != null) {
          implVertex.setTotalOrder(vP.getOrder());
        }

        final MapperDAGVertex dagVertex = (MapperDAGVertex) this.dag.getVertex(vP.getName());
        if (dagVertex != null) {
          dagVertex.setTotalOrder(vP.getOrder());
        }

      }

      // Retrieves the new order in order manager
      this.orderManager.reconstructTotalOrderFromDAG(this.implementation);

      final PrecedenceEdgeAdder adder = new PrecedenceEdgeAdder(this.orderManager, this.implementation);
      adder.removePrecedenceEdges();
      adder.addPrecedenceEdges();

    }
  }
}
