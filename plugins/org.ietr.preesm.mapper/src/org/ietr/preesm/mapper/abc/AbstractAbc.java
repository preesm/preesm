/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2014)
 * Pengcheng Mu <pengcheng.mu@insa-rennes.fr> (2008)
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
package org.ietr.preesm.mapper.abc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.ietr.dftools.algorithm.iterators.TopologicalDAGIterator;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGInitVertex;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.abc.impl.latency.AccuratelyTimedAbc;
import org.ietr.preesm.mapper.abc.impl.latency.ApproximatelyTimedAbc;
import org.ietr.preesm.mapper.abc.impl.latency.InfiniteHomogeneousAbc;
import org.ietr.preesm.mapper.abc.impl.latency.LooselyTimedAbc;
import org.ietr.preesm.mapper.abc.order.OrderManager;
import org.ietr.preesm.mapper.abc.order.VertexOrderList;
import org.ietr.preesm.mapper.abc.taskscheduling.AbstractTaskSched;
import org.ietr.preesm.mapper.abc.taskscheduling.TaskSwitcher;
import org.ietr.preesm.mapper.abc.taskscheduling.TopologicalTaskSched;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.property.VertexMapping;
import org.ietr.preesm.mapper.model.special.PrecedenceEdge;
import org.ietr.preesm.mapper.model.special.TransferVertex;
import org.ietr.preesm.mapper.params.AbcParameters;
import org.ietr.preesm.mapper.tools.CustomTopologicalIterator;

// TODO: Auto-generated Javadoc
/**
 * An architecture simulator calculates costs for a given partial or total implementation.
 *
 * @author mpelcat
 */
public abstract class AbstractAbc implements IAbc {

  /** Architecture related to the current simulator. */
  protected Design archi;

  /** Contains the rank list of all the vertices in an implementation. */
  protected OrderManager orderManager = null;

  /**
   * Current directed acyclic graph. It is the external dag graph
   */
  protected MapperDAG dag;

  /**
   * Current implementation: the internal model that will be used to add edges/vertices and calculate times.
   */
  protected MapperDAG implementation;

  /** Current Abc type. */
  protected AbcType abcType = null;

  /** Task scheduler. */
  protected AbstractTaskSched taskScheduler = null;

  /** Scenario with information common to algorithm and architecture. */
  protected PreesmScenario scenario;

  /**
   * Gets internal implementation graph. Use only for debug!
   *
   * @return the implementation
   */
  @Override
  public MapperDAG getImplementation() {
    return this.implementation;
  }

  /**
   * Activating traces. Put to true only for debug!
   */
  private static final boolean debugTraces = false;

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
   * @throws WorkflowException
   *           the workflow exception
   */
  public static IAbc getInstance(final AbcParameters params, final MapperDAG dag, final Design archi,
      final PreesmScenario scenario) throws WorkflowException {

    AbstractAbc abc = null;
    final AbcType simulatorType = params.getSimulatorType();

    if (simulatorType == AbcType.InfiniteHomogeneous) {
      abc = new InfiniteHomogeneousAbc(params, dag, archi, scenario);
    } else if (simulatorType == AbcType.LooselyTimed) {
      abc = new LooselyTimedAbc(params, dag, archi, simulatorType, scenario);
    } else if (simulatorType == AbcType.ApproximatelyTimed) {
      abc = new ApproximatelyTimedAbc(params, dag, archi, simulatorType, scenario);
    } else if (simulatorType == AbcType.AccuratelyTimed) {
      abc = new AccuratelyTimedAbc(params, dag, archi, simulatorType, scenario);
    }

    return abc;
  }

  /**
   * ABC constructor.
   *
   * @param dag
   *          the dag
   * @param archi
   *          the archi
   * @param abcType
   *          the abc type
   * @param scenario
   *          the scenario
   */
  protected AbstractAbc(final MapperDAG dag, final Design archi, final AbcType abcType, final PreesmScenario scenario) {

    this.abcType = abcType;
    this.orderManager = new OrderManager(archi);

    this.dag = dag;

    // implementation is a duplicate from dag
    this.implementation = dag.clone();

    // Initializes relative constraints
    initRelativeConstraints();

    this.archi = archi;
    this.scenario = scenario;

    // Schedules the tasks in topological and alphabetical order. Some
    // better order should be looked for
    setTaskScheduler(new TaskSwitcher());
  }

  /**
   * Setting common constraints to all non-special vertices and their related init and end vertices.
   */
  private void initRelativeConstraints() {
    for (final DAGVertex v : this.implementation.vertexSet()) {
      populateRelativeConstraint((MapperDAGVertex) v);
    }
  }

  /**
   * Setting common constraints to a non-special vertex and its related init and end vertices.
   *
   * @param vertex
   *          the vertex
   */
  private void populateRelativeConstraint(final MapperDAGVertex vertex) {

    final Set<MapperDAGVertex> verticesToAssociate = new LinkedHashSet<>();
    verticesToAssociate.add(vertex);

    if (SpecialVertexManager.isInit(vertex)) {
      final String endReferenceName = (String) vertex.getPropertyBean().getValue(DAGInitVertex.END_REFERENCE);
      final MapperDAGVertex end = (MapperDAGVertex) (this.dag.getVertex(endReferenceName));
      verticesToAssociate.add(end);
    }

  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.IAbc#getDAG()
   */
  @Override
  public final MapperDAG getDAG() {
    return this.dag;
  }

  /**
   * Called whenever the implementation of a vertex occurs.
   *
   * @param vertex
   *          the vertex
   * @param updateRank
   *          the update rank
   */
  protected abstract void fireNewMappedVertex(MapperDAGVertex vertex, boolean updateRank);

  /**
   * Called whenever the unimplementation of a vertex occurs.
   *
   * @param vertex
   *          the vertex
   */
  protected abstract void fireNewUnmappedVertex(MapperDAGVertex vertex);

  /**
   * Gets the architecture.
   *
   * @return the architecture
   */
  @Override
  public final Design getArchitecture() {
    return this.archi;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.IAbc#getScenario()
   */
  @Override
  public final PreesmScenario getScenario() {
    return this.scenario;
  }

  /**
   * Gets the effective operator of the vertex. NO_OPERATOR if not set
   *
   * @param vertex
   *          the vertex
   * @return the effective component
   */
  @Override
  public final ComponentInstance getEffectiveComponent(MapperDAGVertex vertex) {
    vertex = translateInImplementationVertex(vertex);
    return vertex.getEffectiveComponent();
  }

  /**
   * *********Costs accesses**********.
   *
   * @return the final cost
   */

  @Override
  public abstract long getFinalCost();

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.IAbc#getFinalCost(org.ietr.preesm.mapper.model.MapperDAGVertex)
   */
  @Override
  public abstract long getFinalCost(MapperDAGVertex vertex);

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.IAbc#getFinalCost(org.ietr.dftools.architecture.slam. ComponentInstance)
   */
  @Override
  public abstract long getFinalCost(ComponentInstance component);

  /**
   * *********Implementation accesses**********.
   *
   * @param vertex
   *          the vertex
   * @return the scheduling order
   */

  /**
   * Gets the rank of the given vertex on its operator. -1 if the vertex has no rank
   */
  @Override
  public final int getSchedulingOrder(MapperDAGVertex vertex) {
    vertex = translateInImplementationVertex(vertex);

    return this.orderManager.localIndexOf(vertex);
  }

  /**
   * Gets the total rank of the given vertex. -1 if the vertex has no rank
   *
   * @param vertex
   *          the vertex
   * @return the sched total order
   */
  @Override
  public final int getSchedTotalOrder(MapperDAGVertex vertex) {
    vertex = translateInImplementationVertex(vertex);

    return this.orderManager.totalIndexOf(vertex);
  }

  /**
   * Gets the current total schedule of the ABC.
   *
   * @return the total order
   */
  @Override
  public final VertexOrderList getTotalOrder() {
    return this.orderManager.getTotalOrder().toOrderList();
  }

  /**
   * Maps a single vertex vertex on the operator. If updaterank is true, finds a new place for the vertex in the
   * schedule. Otherwise, use the vertex rank to know where to schedule it.
   *
   * @param dagvertex
   *          the dagvertex
   * @param operator
   *          the operator
   * @param updateRank
   *          the update rank
   * @throws WorkflowException
   *           the workflow exception
   */
  private final void mapSingleVertex(final MapperDAGVertex dagvertex, final ComponentInstance operator,
      final boolean updateRank) throws WorkflowException {
    final MapperDAGVertex impvertex = translateInImplementationVertex(dagvertex);

    if (impvertex.getEffectiveOperator() != DesignTools.NO_COMPONENT_INSTANCE) {
      // Unmapping if necessary before mapping
      unmap(dagvertex);
    }

    // Testing if the vertex or its group can be mapped on the
    // target operator
    if (isMapable(impvertex, operator, false) || !updateRank || (impvertex instanceof TransferVertex)) {

      // Implementation property is set in both DAG and
      // implementation
      // Modifying effective operator of the vertex and all its
      // mapping set!
      dagvertex.setEffectiveOperator(operator);
      impvertex.setEffectiveOperator(operator);

      fireNewMappedVertex(impvertex, updateRank);

    } else {
      WorkflowLogger.getLogger().log(Level.SEVERE,
          impvertex.toString() + " can not be mapped (single) on " + operator.toString());
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
   * @throws WorkflowException
   *           the workflow exception
   */
  private final void mapVertexWithGroup(final MapperDAGVertex dagvertex, final ComponentInstance operator,
      final boolean updateRank, final boolean remapGroup) throws WorkflowException {

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

    if (AbstractAbc.debugTraces) {
      System.out.println("unmap and remap " + orderedVList + " on " + operator);
    }

    for (final MapperDAGVertex dv : orderedVList) {

      final MapperDAGVertex dvi = translateInImplementationVertex(dv);
      final ComponentInstance previousOperator = dvi.getEffectiveOperator();

      // We unmap systematically the main vertex (impvertex) if it has an
      // effectiveComponent and optionally its group
      final boolean isToUnmap = (previousOperator != DesignTools.NO_COMPONENT_INSTANCE)
          && (dv.equals(dagvertex) || remapGroup);

      // We map transfer vertices, if rank is kept, and if mappable
      final boolean isToMap = (dv.equals(dagvertex) || remapGroup)
          && (isMapable(dvi, operator, false) || !updateRank || (dv instanceof TransferVertex));

      if (isToUnmap) {
        // Unmapping if necessary before mapping
        if (AbstractAbc.debugTraces) {
          System.out.println("unmap " + dvi);
        }
        unmap(dvi);

        if (AbstractAbc.debugTraces) {
          System.out.println("unmapped " + dvi);
        }
      }

      if (isToMap) {
        if (AbstractAbc.debugTraces) {
          System.out.println("map " + dvi + " to " + operator);
        }

        dv.setEffectiveOperator(operator);
        dvi.setEffectiveOperator(operator);

        fireNewMappedVertex(dvi, updateRank);

        if (AbstractAbc.debugTraces) {
          System.out.println("mapped " + dvi);
        }

      } else if (dv.equals(dagvertex) || remapGroup) {
        WorkflowLogger.getLogger().log(Level.SEVERE,
            dagvertex.toString() + " can not be mapped (group) on " + operator.toString());
        dv.setEffectiveOperator(DesignTools.NO_COMPONENT_INSTANCE);
        dv.setEffectiveOperator(DesignTools.NO_COMPONENT_INSTANCE);
      }
    }

    if (AbstractAbc.debugTraces) {
      System.out.println("unmapped and remapped " + orderedVList + " on " + operator);
    }
  }

  /**
   * Maps the vertex aith a group on the operator. If updaterank is true, finds a new place for the vertex in the
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
   * @throws WorkflowException
   *           the workflow exception
   */
  @Override
  public final void map(final MapperDAGVertex dagvertex, final ComponentInstance operator, final boolean updateRank,
      final boolean remapGroup) throws WorkflowException {
    final MapperDAGVertex impvertex = translateInImplementationVertex(dagvertex);

    if (operator != DesignTools.NO_COMPONENT_INSTANCE) {
      // On a single actor if it is alone in the group
      if (impvertex.getMapping().getNumberOfVertices() < 2) {
        mapSingleVertex(dagvertex, operator, updateRank);
      } else {
        // Case of a group with several actors
        mapVertexWithGroup(dagvertex, operator, updateRank, remapGroup);
      }
    } else {
      WorkflowLogger.getLogger().log(Level.SEVERE, "Operator asked may not exist");
    }
  }

  /**
   * Sets the total orders in the dag.
   */
  @Override
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
   * @throws WorkflowException
   *           the workflow exception
   */
  @Override
  public final boolean mapAllVerticesOnOperator(final ComponentInstance operator) throws WorkflowException {

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
        WorkflowLogger.getLogger()
            .severe("The current mapping algorithm necessitates that all vertices can be mapped on an operator");
        WorkflowLogger.getLogger()
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
   * @throws WorkflowException
   *           the workflow exception
   */
  @Override
  public List<ComponentInstance> getCandidateOperators(MapperDAGVertex vertex, final boolean protectGroupMapping)
      throws WorkflowException {

    vertex = translateInImplementationVertex(vertex);

    List<ComponentInstance> initOperators = null;
    final VertexMapping vm = vertex.getMapping();

    if (vm != null) {
      // Delegating the list construction to a mapping group
      initOperators = vm.getCandidateComponents(vertex, protectGroupMapping);
    } else {
      initOperators = vertex.getInit().getInitialOperatorList();
      WorkflowLogger.getLogger().log(Level.WARNING, "Found no mapping group for vertex " + vertex);
    }

    if (initOperators.isEmpty()) {
      final String message = "Empty operator set for a vertex: " + vertex.getName()
          + ". Consider relaxing constraints in scenario.";
      WorkflowLogger.getLogger().log(Level.SEVERE, message);
      throw new WorkflowException(message);
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
   * @throws WorkflowException
   *           the workflow exception
   */

  @Override
  public final ComponentInstance findOperator(final MapperDAGVertex currentvertex,
      final ComponentInstance preferedOperator, final boolean protectGroupMapping) throws WorkflowException {

    ComponentInstance adequateOp = null;
    final List<ComponentInstance> opList = getCandidateOperators(currentvertex, protectGroupMapping);

    if (DesignTools.contains(opList, preferedOperator)) {
      adequateOp = preferedOperator;
    } else {

      // Search among the operators with same type than the prefered one
      for (final ComponentInstance op : opList) {
        if ((preferedOperator != null)
            && op.getComponent().getVlnv().getName().equals(preferedOperator.getComponent().getVlnv().getName())) {
          adequateOp = op;
        }
      }

      // Search among the operators with other type than the prefered one
      if (adequateOp == null) {
        for (final ComponentInstance op : opList) {
          adequateOp = op;
          return adequateOp;
        }
      }
    }

    return adequateOp;
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
   * @throws WorkflowException
   *           the workflow exception
   */

  @Override
  public final boolean isMapable(final MapperDAGVertex vertex, final ComponentInstance operator,
      final boolean protectGroupMapping) throws WorkflowException {

    return DesignTools.contains(getCandidateOperators(vertex, protectGroupMapping), operator);
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
  @Override
  public final MapperDAGVertex translateInImplementationVertex(final MapperDAGVertex vertex) {

    final MapperDAGVertex internalVertex = this.implementation.getMapperDAGVertex(vertex.getName());

    if (internalVertex == null) {
      WorkflowLogger.getLogger().log(Level.SEVERE, "No simulator internal vertex with id " + vertex.getName());
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

    if ((destVertex == null) || (sourceVertex == null)) {
      WorkflowLogger.getLogger().log(Level.SEVERE,
          "Implementation vertex with id " + edge.getSource() + " or " + edge.getTarget() + " not found");
    } else {
      final MapperDAGEdge internalEdge = (MapperDAGEdge) this.implementation.getEdge(sourceVertex, destVertex);
      return internalEdge;
    }

    return null;
  }

  /**
   * Unmaps all vertices from implementation.
   */
  @Override
  public final void resetImplementation() {

    final Iterator<DAGVertex> iterator = this.implementation.vertexSet().iterator();

    while (iterator.hasNext()) {
      unmap((MapperDAGVertex) iterator.next());
    }
  }

  /**
   * Unmaps all vertices in both implementation and DAG Resets the order manager only at the end.
   */
  @Override
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
  @Override
  public final void unmap(final MapperDAGVertex dagvertex) {

    final MapperDAGVertex impvertex = translateInImplementationVertex(dagvertex);

    fireNewUnmappedVertex(impvertex);

    dagvertex.setEffectiveOperator(DesignTools.NO_COMPONENT_INSTANCE);

    impvertex.setEffectiveOperator(DesignTools.NO_COMPONENT_INSTANCE);
  }

  /**
   * Removes the vertex implementation of a group of vertices that share the same mapping. In silent mode, does not
   * update implementation timings
   *
   * @param dagvertices
   *          the dagvertices
   */
  public final void unmap(final List<MapperDAGVertex> dagvertices) {

    MapperDAGVertex cImpVertex = null;
    MapperDAGVertex cDagVertex = null;
    for (final MapperDAGVertex dagvertex : dagvertices) {
      final MapperDAGVertex impvertex = translateInImplementationVertex(dagvertex);

      fireNewUnmappedVertex(impvertex);
      cDagVertex = dagvertex;
      cImpVertex = impvertex;
    }

    cDagVertex.setEffectiveOperator(DesignTools.NO_COMPONENT_INSTANCE);
    cImpVertex.setEffectiveOperator(DesignTools.NO_COMPONENT_INSTANCE);
  }

  /**
   * Gets the cost of the given vertex.
   *
   * @param vertex
   *          the vertex
   * @return the cost
   */
  @Override
  public final long getCost(MapperDAGVertex vertex) {
    vertex = translateInImplementationVertex(vertex);
    return vertex.getTiming().getCost();
  }

  /**
   * Gets the cost of the given edge in the implementation.
   *
   * @param edge
   *          the edge
   * @return the cost
   */
  @Override
  public final long getCost(MapperDAGEdge edge) {
    edge = translateInImplementationEdge(edge);
    return edge.getTiming().getCost();

  }

  /**
   * Setting the cost of an edge is delegated to ABC implementations.
   *
   * @param edge
   *          the new edge cost
   */
  protected abstract void setEdgeCost(MapperDAGEdge edge);

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
  @Override
  public final AbcType getType() {
    return this.abcType;
  }

  /**
   * Prepares task rescheduling.
   *
   * @param taskScheduler
   *          the new task scheduler
   */
  @Override
  public void setTaskScheduler(final AbstractTaskSched taskScheduler) {

    this.taskScheduler = taskScheduler;
    this.taskScheduler.setOrderManager(this.orderManager);

    if (this.taskScheduler instanceof TopologicalTaskSched) {
      ((TopologicalTaskSched) this.taskScheduler).createTopology(this.implementation);
    }
  }

}
