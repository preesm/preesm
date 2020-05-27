/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2020)
 * Jonathan Piat [jpiat@laas.fr] (2011)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2009 - 2016)
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
package org.preesm.algorithm.mapper.abc.impl.latency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.emf.common.util.ECollections;
import org.preesm.algorithm.mapper.abc.AbcType;
import org.preesm.algorithm.mapper.abc.ImplementationCleaner;
import org.preesm.algorithm.mapper.abc.edgescheduling.AbstractEdgeSched;
import org.preesm.algorithm.mapper.abc.edgescheduling.EdgeSchedType;
import org.preesm.algorithm.mapper.abc.edgescheduling.IEdgeSched;
import org.preesm.algorithm.mapper.abc.order.OrderManager;
import org.preesm.algorithm.mapper.abc.order.VertexOrderList;
import org.preesm.algorithm.mapper.abc.route.CommunicationRouter;
import org.preesm.algorithm.mapper.abc.taskscheduling.AbstractTaskSched;
import org.preesm.algorithm.mapper.abc.taskscheduling.TaskSwitcher;
import org.preesm.algorithm.mapper.abc.taskscheduling.TopologicalTaskSched;
import org.preesm.algorithm.mapper.gantt.GanttData;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGEdge;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.mapper.model.property.VertexMapping;
import org.preesm.algorithm.mapper.model.special.PrecedenceEdge;
import org.preesm.algorithm.mapper.model.special.PrecedenceEdgeAdder;
import org.preesm.algorithm.mapper.model.special.TransferVertex;
import org.preesm.algorithm.mapper.params.AbcParameters;
import org.preesm.algorithm.mapper.timekeeper.TimeKeeper;
import org.preesm.algorithm.mapper.tools.CustomTopologicalIterator;
import org.preesm.algorithm.mapper.tools.SchedulingOrderIterator;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.algorithm.model.iterators.TopologicalDAGIterator;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 * Abc that minimizes latency.
 *
 * @author mpelcat
 */
public abstract class LatencyAbc {

  /** Architecture related to the current simulator. */
  protected Design archi;

  /** Contains the rank list of all the vertices in an implementation. */
  protected OrderManager orderManager = null;

  /**
   * Current directed acyclic graph. It is the external dag graph
   */
  private MapperDAG dag;

  /**
   * Current best latency, if not set, this default to getFinalLatency
   */
  private Long bestLatency = (long) -1;

  /**
   * Current implementation: the internal model that will be used to add edges/vertices and calculate times.
   */
  protected MapperDAG implementation;

  /** Current Abc type. */
  private AbcType abcType = null;

  /** Task scheduler. */
  protected AbstractTaskSched taskScheduler = null;

  /** Scenario with information common to algorithm and architecture. */
  protected Scenario scenario;

  /**
   * Gets internal implementation graph. Use only for debug!
   *
   * @return the implementation
   */
  public MapperDAG getImplementation() {
    return this.implementation;
  }

  /**
   * Gets a new architecture simulator from a simulator type.
   *
   * @param params
   *          the params
   * @param dag
   *          the dag
   * @param archi
   *          the archi
   * @param scenario
   *          the scenario
   * @return single instance of AbstractAbc
   * @throws PreesmException
   *           the workflow exception
   */
  public static LatencyAbc getInstance(final AbcParameters params, final MapperDAG dag, final Design archi,
      final Scenario scenario) {

    LatencyAbc abc = null;
    final AbcType simulatorType = params.getSimulatorType();

    switch (simulatorType) {
      case INFINITE_HOMOGENEOUS:
        abc = new InfiniteHomogeneousAbc(params, dag, archi, scenario);
        break;
      case LOSSELY_TIMED:
        abc = new LooselyTimedAbc(params, dag, archi, simulatorType, scenario);
        break;
      case APPROXIMATELY_TIMED:
        abc = new ApproximatelyTimedAbc(params, dag, archi, simulatorType, scenario);
        break;
      case ACCURATELY_TIMED:
        abc = new AccuratelyTimedAbc(params, dag, archi, simulatorType, scenario);
        break;
      default:
        throw new PreesmRuntimeException("Unsupported simulator type: " + simulatorType);
    }
    return abc;
  }

  /**
   * Gets the edge sched type.
   *
   * @return the edge sched type
   */
  public abstract EdgeSchedType getEdgeSchedType();

  /**
   * Sets the task scheduler of the current ABC.
   */
  public void setTaskScheduler(final AbstractTaskSched taskScheduler) {

    this.taskScheduler = taskScheduler;
    this.taskScheduler.setOrderManager(this.orderManager);

    if (this.taskScheduler instanceof TopologicalTaskSched) {
      ((TopologicalTaskSched) this.taskScheduler).createTopology(this.implementation);
    }
  }

  public final MapperDAG getDAG() {
    return this.dag;
  }

  public final Design getArchitecture() {
    return this.archi;
  }

  public final Scenario getScenario() {
    return this.scenario;
  }

  /**
   * Gets the effective operator of the vertex. NO_OPERATOR if not set
   *
   * @param vertex
   *          the vertex
   * @return the effective component
   */
  public final ComponentInstance getEffectiveComponent(final MapperDAGVertex vertex) {
    final MapperDAGVertex vertex2 = translateInImplementationVertex(vertex);
    return vertex2.getEffectiveComponent();
  }

  /**
   * Gets the total rank of the given vertex. -1 if the vertex has no rank
   *
   * @param vertex
   *          the vertex
   * @return the sched total order
   */
  public final int getSchedTotalOrder(MapperDAGVertex vertex) {
    vertex = translateInImplementationVertex(vertex);

    return this.orderManager.totalIndexOf(vertex);
  }

  /**
   * Gets the current total schedule of the ABC.
   *
   * @return the total order
   */
  public final VertexOrderList getTotalOrder() {
    return this.orderManager.getTotalOrder().toOrderList();
  }

  /**
   * Maps a single vertex vertex on the operator. If updaterank is true, finds a new place for the vertex in the
   * schedule. Otherwise, use the vertex rank to know where to schedule it.
   *
   * @param dagvertex
   *          the dagvertex
   * @param specifiedOperator
   *          the operator
   * @param updateRank
   *          the update rank
   * @throws PreesmException
   *           the workflow exception
   */
  private final void mapSingleVertex(final MapperDAGVertex dagvertex, final ComponentInstance specifiedOperator,
      final boolean updateRank) {

    final ComponentInstance finalOperator;

    final MapperDAGVertex impvertex = translateInImplementationVertex(dagvertex);
    final ComponentInstance effectiveOperator = impvertex.getEffectiveOperator();

    if (MapperDAGVertex.DAG_END_VERTEX.equals(dagvertex.getKind())) {
      final String initReferenceName = dagvertex.getPropertyBean().getValue(MapperDAGVertex.END_REFERENCE);
      final MapperDAGVertex dagInitVertex = (MapperDAGVertex) dag.getVertex(initReferenceName);

      final MapperDAGVertex implInitVertex = translateInImplementationVertex(dagInitVertex);
      final ComponentInstance initEffectiveOperator = implInitVertex.getEffectiveOperator();
      if (initEffectiveOperator != null) {
        // if init has been mapped (should be the case), forces end to be on same operator
        if (effectiveOperator != null
            && effectiveOperator.getInstanceName().equals(initEffectiveOperator.getInstanceName())) {
          // skip remapping if operator is already properly set
          return;
        } else {
          finalOperator = initEffectiveOperator;
        }
      } else {
        finalOperator = specifiedOperator;
      }
    } else {
      finalOperator = specifiedOperator;
    }

    if (effectiveOperator != null) {
      // Unmapping if necessary before mapping
      unmap(dagvertex);
    }

    // Testing if the vertex or its group can be mapped on the
    // target operator
    if (isMapable(impvertex, finalOperator, false) || !updateRank || (impvertex instanceof TransferVertex)) {

      // Implementation property is set in both DAG and
      // implementation
      // Modifying effective operator of the vertex and all its
      // mapping set!
      dagvertex.setEffectiveComponent(finalOperator);
      impvertex.setEffectiveComponent(finalOperator);

      fireNewMappedVertex(impvertex, updateRank);

    } else {
      final String msg = impvertex + " can not be mapped (single) on " + finalOperator;
      PreesmLogger.getLogger().log(Level.SEVERE, msg);
    }
  }

  /**
   * Maps a vertex and its non-trivial group. If the boolean remapGroup is true, the whole group is forced to be
   * unmapped and remapped.
   *
   * @param dagvertex
   *          the dagvertex
   * @param operator
   *          the operator
   * @param updateRank
   *          the update rank
   * @param remapGroup
   *          the remap group
   * @throws PreesmException
   *           the workflow exception
   */
  private final void mapVertexWithGroup(final MapperDAGVertex dagvertex, final ComponentInstance operator,
      final boolean updateRank, final boolean remapGroup) {

    final VertexMapping dagprop = dagvertex.getMapping();

    // Generating a list of vertices to remap in topological order
    final List<MapperDAGVertex> vList = dagprop.getVertices((MapperDAG) dagvertex.getBase());
    final List<MapperDAGVertex> orderedVList = new ArrayList<>();
    // On the whole group otherwise
    final CustomTopologicalIterator iterator = new CustomTopologicalIterator(this.dag, true);
    while (iterator.hasNext()) {
      final MapperDAGVertex v = iterator.next();
      if (vList.contains(v)) {
        orderedVList.add(v);
      }
    }

    PreesmLogger.getLogger().log(Level.FINE, () -> "unmap and remap " + orderedVList + " on " + operator);

    for (final MapperDAGVertex dv : orderedVList) {

      final MapperDAGVertex dvi = translateInImplementationVertex(dv);
      final ComponentInstance previousOperator = dvi.getEffectiveOperator();

      // We unmap systematically the main vertex (impvertex) if it has an
      // effectiveComponent and optionally its group
      final boolean isToUnmap = (previousOperator != null) && (dv.equals(dagvertex) || remapGroup);

      // We map transfer vertices, if rank is kept, and if mappable
      final boolean isToMap = (dv.equals(dagvertex) || remapGroup)
          && (isMapable(dvi, operator, false) || !updateRank || (dv instanceof TransferVertex));

      if (isToUnmap) {
        // Unmapping if necessary before mapping
        PreesmLogger.getLogger().log(Level.FINE, () -> "unmap " + dvi);
        unmap(dvi);

        PreesmLogger.getLogger().log(Level.FINE, () -> "unmapped " + dvi);
      }

      if (isToMap) {
        PreesmLogger.getLogger().log(Level.FINE, () -> "map " + dvi + " to " + operator);

        dv.setEffectiveComponent(operator);
        dvi.setEffectiveComponent(operator);

        fireNewMappedVertex(dvi, updateRank);

        PreesmLogger.getLogger().log(Level.FINE, () -> "mapped " + dvi);

      } else if (dv.equals(dagvertex) || remapGroup) {
        PreesmLogger.getLogger().log(Level.SEVERE, () -> dagvertex + " can not be mapped (group) on " + operator);
        dv.setEffectiveComponent(null);
      }
    }

    PreesmLogger.getLogger().log(Level.FINE, () -> "unmapped and remapped " + orderedVList + " on " + operator);
  }

  /**
   * Maps the vertex with a group on the operator. If updaterank is true, finds a new place for the vertex in the
   * schedule. Otherwise, use the vertex rank to know where to schedule it.
   *
   * @param dagvertex
   *          the dagvertex
   * @param operator
   *          the operator
   * @param updateRank
   *          the update rank
   * @param remapGroup
   *          the remap group
   * @throws PreesmException
   *           the workflow exception
   */
  public final void map(final MapperDAGVertex dagvertex, final ComponentInstance operator, final boolean updateRank,
      final boolean remapGroup) {
    final MapperDAGVertex impvertex = translateInImplementationVertex(dagvertex);

    if (operator != null) {
      // On a single actor if it is alone in the group
      if (impvertex.getMapping().getNumberOfVertices() < 2) {
        mapSingleVertex(dagvertex, operator, updateRank);
      } else {
        // Case of a group with several actors
        mapVertexWithGroup(dagvertex, operator, updateRank, remapGroup);
      }
    } else {
      PreesmLogger.getLogger().log(Level.SEVERE, "Operator asked may not exist");
    }
  }

  /**
   * Sets the total orders in the dag.
   */
  public final void retrieveTotalOrder() {

    this.orderManager.tagDAG(this.dag);
  }

  /**
   * maps all the vertices on the given operator if possible. If a vertex can not be executed on the given operator,
   * looks for another operator with same type. If again none is found, looks for any other operator able to execute the
   * vertex.
   *
   * @param operator
   *          the operator
   * @return true, if successful
   * @throws PreesmException
   *           the workflow exception
   */
  public final boolean mapAllVerticesOnOperator(final ComponentInstance operator) {

    boolean possible = true;
    MapperDAGVertex currentvertex;

    final TopologicalDAGIterator iterator = new TopologicalDAGIterator(this.dag);

    /*
     * The listener is mapped in each vertex
     */
    while (iterator.hasNext()) {
      currentvertex = (MapperDAGVertex) iterator.next();

      // Looks for an operator able to execute currentvertex (preferably
      // the given operator)
      final ComponentInstance adequateOp = findOperator(currentvertex, operator, true);

      if (adequateOp != null) {
        // Mapping in list order without remapping the group
        map(currentvertex, adequateOp, true, false);
      } else {
        PreesmLogger.getLogger()
            .severe("The current mapping algorithm necessitates that all vertices can be mapped on an operator");
        PreesmLogger.getLogger()
            .severe("Problem with: " + currentvertex.getName() + ". Consider changing the scenario.");

        possible = false;
      }
    }

    return possible;
  }

  /**
   * Looks for operators able to execute currentvertex. If the boolean protectGroupMapping is true and at least one
   * vertex is mapped in the current vertex group, this unique operator is returned. Otherwise, the intersection of the
   * available operators for the group is returned.
   *
   * @param vertex
   *          the vertex
   * @param protectGroupMapping
   *          the protect group mapping
   * @return the candidate operators
   * @throws PreesmException
   *           the workflow exception
   */
  public List<ComponentInstance> getCandidateOperators(MapperDAGVertex vertex, final boolean protectGroupMapping) {

    vertex = translateInImplementationVertex(vertex);

    List<ComponentInstance> initOperators = null;
    final VertexMapping vm = vertex.getMapping();

    if (vm != null) {
      // Delegating the list construction to a mapping group
      initOperators = vm.getCandidateComponents(vertex, protectGroupMapping);
    } else {
      initOperators = vertex.getInit().getInitialOperatorList();
      final String msg = "Found no mapping group for vertex " + vertex;
      PreesmLogger.getLogger().log(Level.WARNING, msg);
    }

    if (initOperators.isEmpty()) {
      final String message = "Empty operator set for a vertex: " + vertex.getName()
          + ". Consider relaxing constraints in scenario.";
      PreesmLogger.getLogger().log(Level.SEVERE, message);
      throw new PreesmRuntimeException(message);
    }

    return initOperators;
  }

  /**
   * Looks for an operator able to execute currentvertex (preferably the given operator or an operator with same type)
   * If the boolean protectGroupMapping is true and at least one vertex is mapped in the current vertex group, this
   * unique operator is compared to the prefered one. Otherwise, the prefered operator is checked of belonging to
   * available operators of the group.
   *
   * @param currentvertex
   *          the currentvertex
   * @param preferedOperator
   *          the prefered operator
   * @param protectGroupMapping
   *          the protect group mapping
   * @return the component instance
   * @throws PreesmException
   *           the workflow exception
   */

  public final ComponentInstance findOperator(final MapperDAGVertex currentvertex,
      final ComponentInstance preferedOperator, final boolean protectGroupMapping) {

    final List<ComponentInstance> opList = getCandidateOperators(currentvertex, protectGroupMapping);

    if (ECollections.indexOf(opList, preferedOperator, 0) != -1) {
      return preferedOperator;
    } else {

      // Search among the operators with same type than the prefered one
      for (final ComponentInstance op : opList) {
        if ((preferedOperator != null)
            && op.getComponent().getVlnv().getName().equals(preferedOperator.getComponent().getVlnv().getName())) {
          return op;
        }
      }

      // Search among the operators with other type than the prefered one
      for (final ComponentInstance op : opList) {
        if (isMapable(currentvertex, op, true)) {
          return op;
        }
      }
    }

    return null;
  }

  /**
   * Checks in the vertex implementation properties if it can be mapped on the given operator.
   *
   * @param vertex
   *          the vertex
   * @param operator
   *          the operator
   * @param protectGroupMapping
   *          the protect group mapping
   * @return true, if is mapable
   * @throws PreesmException
   *           the workflow exception
   */

  public final boolean isMapable(final MapperDAGVertex vertex, final ComponentInstance operator,
      final boolean protectGroupMapping) {
    final List<ComponentInstance> candidateOperators = getCandidateOperators(vertex, protectGroupMapping);
    return ECollections.indexOf(candidateOperators, operator, 0) != -1;
  }

  /**
   * *********Useful tools**********.
   *
   * @param edges
   *          the edges
   */

  /**
   * resets the costs of a set of edges
   */
  protected final void resetCost(final Set<DAGEdge> edges) {
    final Iterator<DAGEdge> iterator = edges.iterator();

    while (iterator.hasNext()) {

      final MapperDAGEdge edge = (MapperDAGEdge) iterator.next();
      if (!(edge instanceof PrecedenceEdge)) {
        edge.getTiming().resetCost();
      }
    }
  }

  /**
   * Returns the implementation vertex corresponding to the DAG vertex.
   *
   * @param vertex
   *          the vertex
   * @return the mapper DAG vertex
   */
  public final MapperDAGVertex translateInImplementationVertex(final MapperDAGVertex vertex) {

    final MapperDAGVertex internalVertex = this.implementation.getMapperDAGVertex(vertex.getName());

    if (internalVertex == null) {
      final String message = "No simulator internal vertex with id " + vertex.getName();
      throw new PreesmRuntimeException(message, new NullPointerException());
    }
    return internalVertex;
  }

  /**
   * resets the costs of a set of edges.
   *
   * @param edge
   *          the edge
   * @return the mapper DAG edge
   */
  private final MapperDAGEdge translateInImplementationEdge(final MapperDAGEdge edge) {

    final MapperDAGVertex sourceVertex = translateInImplementationVertex((MapperDAGVertex) edge.getSource());
    final MapperDAGVertex destVertex = translateInImplementationVertex((MapperDAGVertex) edge.getTarget());

    return (MapperDAGEdge) this.implementation.getEdge(sourceVertex, destVertex);
  }

  /**
   * Unmaps all vertices from implementation.
   */
  public final void resetImplementation() {

    final Iterator<DAGVertex> iterator = this.implementation.vertexSet().iterator();

    while (iterator.hasNext()) {
      unmap((MapperDAGVertex) iterator.next());
    }
  }

  /**
   * Unmaps all vertices in both implementation and DAG Resets the order manager only at the end.
   */
  public final void resetDAG() {

    final Iterator<DAGVertex> iterator = this.dag.vertexSet().iterator();

    while (iterator.hasNext()) {
      final MapperDAGVertex v = (MapperDAGVertex) iterator.next();
      if (v.hasEffectiveComponent()) {
        unmap(v);
      }
    }

    this.orderManager.resetTotalOrder();
  }

  /**
   * Removes the vertex implementation In silent mode, does not update implementation timings.
   *
   * @param dagvertex
   *          the dagvertex
   */
  private final void unmap(final MapperDAGVertex dagvertex) {

    final MapperDAGVertex impvertex = translateInImplementationVertex(dagvertex);

    fireNewUnmappedVertex(impvertex);

    dagvertex.setEffectiveComponent(null);

    impvertex.setEffectiveComponent(null);
  }

  /**
   * Gets the cost of the given edge in the implementation.
   *
   * @param edge
   *          the edge
   * @return the cost
   */
  public final long getCost(MapperDAGEdge edge) {
    edge = translateInImplementationEdge(edge);
    return edge.getTiming().getCost();

  }

  /**
   * An edge cost represents its cost taking into account a possible complex transfer of data.
   *
   * @param edgeset
   *          the new edges costs
   */
  protected final void setEdgesCosts(final Set<DAGEdge> edgeset) {

    final Iterator<DAGEdge> iterator = edgeset.iterator();

    while (iterator.hasNext()) {
      final MapperDAGEdge edge = (MapperDAGEdge) iterator.next();

      if (!(edge instanceof PrecedenceEdge)) {
        setEdgeCost(edge);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.IAbc#getType()
   */
  public final AbcType getType() {
    return this.abcType;
  }

  /**
   * Prepares task rescheduling.
   *
   * @param taskScheduler
   *          the new task scheduler
   * @Override public void setTaskScheduler(final AbstractTaskSched taskScheduler) {
   *
   *           this.taskScheduler = taskScheduler; this.taskScheduler.setOrderManager(this.orderManager);
   *
   *           if (this.taskScheduler instanceof TopologicalTaskSched) { ((TopologicalTaskSched)
   *           this.taskScheduler).createTopology(this.implementation); } }
   *
   *           /** Current time keeper: called exclusively by simulator to update the useful time tags in DAG.
   */
  protected TimeKeeper nTimeKeeper;

  /** The com router. */
  protected CommunicationRouter comRouter = null;

  /** Scheduling the transfer vertices on the media. */
  protected IEdgeSched edgeScheduler;

  /** Current abc parameters. */
  private final AbcParameters params;

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
      final Scenario scenario) {

    this.abcType = abcType;
    this.orderManager = new OrderManager(archi);

    this.dag = dag;

    // implementation is a duplicate from dag
    this.implementation = dag.copy();

    this.archi = archi;
    this.scenario = scenario;

    // Schedules the tasks in topological and alphabetical order. Some
    // better order should be looked for
    setTaskScheduler(new TaskSwitcher());
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
   * @throws PreesmException
   *           the workflow exception
   */
  public void setDAG(final MapperDAG dag) {

    this.dag = dag;
    this.implementation = dag.copy();

    this.orderManager.reconstructTotalOrderFromDAG(this.implementation);

    this.nTimeKeeper = new TimeKeeper(this.implementation, this.orderManager);
    this.nTimeKeeper.resetTimings();

    // Forces the unmapping process before the new mapping process
    final Map<MapperDAGVertex, ComponentInstance> operators = new LinkedHashMap<>();

    for (final DAGVertex v : dag.vertexSet()) {
      final MapperDAGVertex mdv = (MapperDAGVertex) v;
      operators.put(mdv, mdv.getEffectiveOperator());
      mdv.setEffectiveComponent(null);
      this.implementation.getMapperDAGVertex(mdv.getName()).setEffectiveComponent(null);
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
  protected void fireNewMappedVertex(final MapperDAGVertex vertex, final boolean updateRank) {

    final ComponentInstance effectiveOp = vertex.getEffectiveOperator();

    if (effectiveOp == null) {
      PreesmLogger.getLogger().severe("implementation of " + vertex.getName() + " failed");
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
   * Asks the time keeper to update timings. Crucial and costly operation. Depending on the kind of timings we want,
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
  public final long getFinalCost(MapperDAGVertex vertex) {
    vertex = translateInImplementationVertex(vertex);

    final long finalTime = this.nTimeKeeper.getFinalTime(vertex);

    if (finalTime < 0) {
      PreesmLogger.getLogger().log(Level.SEVERE, "negative vertex final time");
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
  public final long getFinalCost(final ComponentInstance component) {
    return this.nTimeKeeper.getFinalTime(component);
  }

  /**
   * The cost of an implementation is calculated from its latency and loads.
   *
   * @return the final cost
   */
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
      PreesmLogger.getLogger().log(Level.SEVERE, "negative implementation final latency");
    }

    return finalTime;
  }

  /**
   * .The best latency possible. In order to set it properly, one should do an InfiniteHomogeneous simulation first and
   * set the value accordingly.
   *
   * @return the best latency if set, getFinalLatency else
   */
  public final long getBestLatency() {
    if (bestLatency < 0) {
      return getFinalLatency();
    }
    return bestLatency;
  }

  /**
   * Set the best latency of this LatencyAbc.
   * 
   * @param bestLatency
   *          value to set.
   */
  public final void setBestLatency(final long bestLatency) {
    if (bestLatency <= getFinalLatency()) {
      this.bestLatency = bestLatency;
    }
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
  private long evaluateLoadBalancing() {

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

  public void updateFinalCosts() {
    updateTimings();
  }

  /**
   * Reorders the implementation using the given total order.
   *
   * @param totalOrder
   *          the total order
   */
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
