/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
package org.preesm.algorithm.synthesis.schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import org.eclipse.emf.common.util.EList;
import org.jgrapht.Graphs;
import org.jgrapht.alg.TransitiveClosure;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.CommunicationActor;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ReceiveEndActor;
import org.preesm.algorithm.schedule.model.ReceiveStartActor;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.SendEndActor;
import org.preesm.algorithm.schedule.model.SendStartActor;
import org.preesm.algorithm.schedule.model.SequentialActorSchedule;
import org.preesm.algorithm.schedule.model.SequentialHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.commons.CollectionUtil;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.AbstractActorNameComparator;
import org.preesm.model.slam.ComponentInstance;

/**
 * Schedule manager class. Helps build ordered list for the AbstractActors of a schedule, inserting actors, querying,
 * etc.
 *
 * @author anmorvan
 */
public class ScheduleOrderManager {

  /** PiSDF Graph scheduled */
  private final PiGraph                     pigraph;
  /** Schedule managed by this class */
  private final Schedule                    schedule;
  /**  */
  private Map<AbstractActor, ActorSchedule> actorToScheduleMap;

  private DirectedAcyclicGraph<AbstractActor, DAGedge> graphCache                = null;
  private DirectedAcyclicGraph<AbstractActor, DAGedge> transitiveClosureCache    = null;
  boolean                                              isTransitiveCacheComputed = false;
  private List<AbstractActor>                          totalOrderCache           = null;
  private Map<ComponentInstance, List<AbstractActor>>  operatorTotalOrderCache   = new LinkedHashMap<>();

  /**
   * A new object should be created if the pigraph or the schedule has been modified externally.
   */
  public ScheduleOrderManager(final PiGraph pigraph, final Schedule schedule) {
    this.pigraph = pigraph;
    this.schedule = schedule;

    this.actorToScheduleMap = ScheduleOrderManager.actorToScheduleMap(schedule);
  }

  /**
   * Reset the data if the schedule or the pigraph have changed. The current schedule is used during the call for
   * memoization.
   */
  public void reset() {
    graphCache = null;
    transitiveClosureCache = null;
    isTransitiveCacheComputed = false;
    totalOrderCache = null;
    operatorTotalOrderCache = new LinkedHashMap<>();

    actorToScheduleMap = ScheduleOrderManager.actorToScheduleMap(schedule);
  }

  private void resetGraphCache() {
    graphCache = null;
    transitiveClosureCache = null;
    isTransitiveCacheComputed = false;
  }

  /**
   * Type of dependency.
   * 
   * @author ahonorat
   */
  private enum DAGedgeType {
    DATA, SCHEDS, SCHEDH, COM, NONE, RECO;
  }

  /**
   * Dummy class for edge in graph.
   * 
   * @author ahonorat
   */
  private static class DAGedge {
    static int  count = 0;
    int         id;
    DAGedgeType type;

    public DAGedge(DAGedgeType type) {
      id = count++;
      this.type = type;
    }

    public String toString() {
      return "edge n°" + id + " of type " + type;
    }
  }

  /**
   * Build a DAG from a PiGraph and a schedule that represents precedence (thus hold no data). The graph transitive
   * closure is computed.
   */
  private final DirectedAcyclicGraph<AbstractActor, DAGedge> getGraph() {
    if (graphCache != null) {
      return graphCache;
    } else {

      Supplier<DAGedge> eSupplier = new Supplier<DAGedge>() {
        @Override
        public DAGedge get() {
          return new DAGedge(DAGedgeType.NONE);
        }
      };

      DAGedge.count = 0;
      final DirectedAcyclicGraph<AbstractActor, DAGedge> dag = new DirectedAcyclicGraph<>(null, eSupplier, false);
      final DirectedAcyclicGraph<AbstractActor, DAGedge> tdag = new DirectedAcyclicGraph<>(null, eSupplier, false);

      ScheduleUtil.getAllReferencedActors(schedule).forEach(dag::addVertex);
      ScheduleUtil.getAllReferencedActors(schedule).forEach(tdag::addVertex);
      for (Fifo fifo : pigraph.getFifos()) {
        AbstractActor src = fifo.getSourcePort().getContainingActor();
        AbstractActor tgt = fifo.getTargetPort().getContainingActor();
        if (dag.getAllEdges(src, tgt).isEmpty()) {
          dag.addEdge(src, tgt, new DAGedge(DAGedgeType.DATA));
          tdag.addEdge(src, tgt, new DAGedge(DAGedgeType.DATA));
        }
      }

      ScheduleUtil.getAllReferencedActors(schedule).stream().filter(SendStartActor.class::isInstance)
          .forEach(matchingActor -> {
            final SendStartActor sendStart = SendStartActor.class.cast(matchingActor);
            final SendEndActor sendEnd = sendStart.getSendEnd();
            final ReceiveEndActor receiveEnd = sendStart.getTargetReceiveEnd();
            final ReceiveStartActor receiveStart = receiveEnd.getReceiveStart();

            if (dag.getAllEdges(sendStart, sendEnd).isEmpty()) {
              dag.addEdge(sendStart, sendEnd, new DAGedge(DAGedgeType.COM));
              tdag.addEdge(sendStart, sendEnd, new DAGedge(DAGedgeType.COM));
            }
            if (dag.getAllEdges(receiveStart, receiveEnd).isEmpty()) {
              dag.addEdge(receiveStart, receiveEnd, new DAGedge(DAGedgeType.COM));
              tdag.addEdge(receiveStart, receiveEnd, new DAGedge(DAGedgeType.COM));
            }
            if (dag.getAllEdges(sendEnd, receiveEnd).isEmpty()) {
              dag.addEdge(sendEnd, receiveEnd, new DAGedge(DAGedgeType.COM));
              tdag.addEdge(sendEnd, receiveEnd, new DAGedge(DAGedgeType.COM));
            }
          });

      new SchedulePrecedenceUpdate(dag, tdag).doSwitch(schedule);
      graphCache = dag;
      // transitive closure is not yet computed, at this point it is just a copy of dag.
      transitiveClosureCache = tdag;
      return dag;
    }
  }

  private final DirectedAcyclicGraph<AbstractActor, DAGedge> getTransitiveClosure() {
    if (isTransitiveCacheComputed) {
      return transitiveClosureCache;
    } else {
      // getGraph will initialize tdag
      getGraph();
      TransitiveClosure.INSTANCE.closeDirectedAcyclicGraph(transitiveClosureCache);
      isTransitiveCacheComputed = true;
      return transitiveClosureCache;
    }
  }

  /**
   */
  public boolean isPredecessors(final AbstractActor subject, final AbstractActor target) {
    return getAllPredecessors(subject).contains(target);
  }

  /**
   */
  public boolean isSuccessors(final AbstractActor subject, final AbstractActor target) {
    return getAllSuccessors(subject).contains(target);
  }

  /**
   * Return all the predecessors of actor; according to PiSDF topology AND Schedule precedence;
   *
   * No order is enforced on the resulting list.
   */
  public List<AbstractActor> getAllPredecessors(final AbstractActor actor) {
    final DirectedAcyclicGraph<AbstractActor, DAGedge> graph = getTransitiveClosure();
    return Graphs.predecessorListOf(graph, actor);
  }

  /**
   * Return all the successors of actor; according to PiSDF topology AND Schedule precedence;
   *
   * No order is enforced on the resulting list.
   */
  public List<AbstractActor> getAllSuccessors(final AbstractActor actor) {
    final DirectedAcyclicGraph<AbstractActor, DAGedge> graph = getTransitiveClosure();
    return Graphs.successorListOf(graph, actor);
  }

  /**
   * Return all the predecessors of actor; according to PiSDF topology AND Schedule precedence;
   *
   * No order is enforced on the resulting list.
   */
  public List<AbstractActor> getDirectPredecessors(final AbstractActor actor) {
    final DirectedAcyclicGraph<AbstractActor, DAGedge> graph = getGraph();
    return Graphs.predecessorListOf(graph, actor);
  }

  /**
   * Return all the successors of actor; according to PiSDF topology AND Schedule precedence;
   *
   * No order is enforced on the resulting list.
   */
  public List<AbstractActor> getDirectSuccessors(final AbstractActor actor) {
    final DirectedAcyclicGraph<AbstractActor, DAGedge> graph = getGraph();
    return Graphs.successorListOf(graph, actor);
  }

  /**
   * Schedule switch that inserts in the DAG attribute all the precedence edges implied by the Schedule
   */
  private static class SchedulePrecedenceUpdate extends ScheduleSwitch<Boolean> {

    private final DirectedAcyclicGraph<AbstractActor, DAGedge> dag;
    private final DirectedAcyclicGraph<AbstractActor, DAGedge> tdag;

    private SchedulePrecedenceUpdate(final DirectedAcyclicGraph<AbstractActor, DAGedge> dag,
        final DirectedAcyclicGraph<AbstractActor, DAGedge> tdag) {
      this.dag = dag;
      this.tdag = tdag;
    }

    @Override
    public Boolean caseSequentialActorSchedule(final SequentialActorSchedule object) {
      final EList<AbstractActor> actorList = object.getActorList();
      AbstractActor prev = null;
      for (final AbstractActor current : actorList) {
        if (prev != null && dag.getAllEdges(prev, current).isEmpty()) {
          dag.addEdge(prev, current, new DAGedge(DAGedgeType.SCHEDS));
          tdag.addEdge(prev, current, new DAGedge(DAGedgeType.SCHEDS));
        }
        prev = current;
      }
      return true;
    }

    @Override
    public Boolean caseSequentialHiearchicalSchedule(final SequentialHiearchicalSchedule object) {
      final EList<Schedule> scheduleTree = object.getScheduleTree();
      final int size = scheduleTree.size();
      for (int i = 0; i < size; i++) {
        final Schedule current = scheduleTree.get(i);
        final List<AbstractActor> currentActors = ScheduleUtil.getAllReferencedActors(current);
        for (int j = i + 1; j < size; j++) {
          final Schedule succ = scheduleTree.get(j);
          final List<AbstractActor> succActors = ScheduleUtil.getAllReferencedActors(succ);
          // add edge from all actors contained by current to all actors contained in succ
          for (final AbstractActor currentActor : currentActors) {
            for (final AbstractActor succActor : succActors) {
              if (dag.getAllEdges(currentActor, succActor).isEmpty()) {
                dag.addEdge(currentActor, succActor, new DAGedge(DAGedgeType.SCHEDH));
                tdag.addEdge(currentActor, succActor, new DAGedge(DAGedgeType.SCHEDH));
              }
            }
          }
        }
      }
      scheduleTree.forEach(this::doSwitch);
      return true;
    }

    @Override
    public Boolean caseHierarchicalSchedule(HierarchicalSchedule object) {
      object.getScheduleTree().forEach(this::doSwitch);
      return true;
    }

  }

  /**
   * Build the order following the appearance in the schedule tree but also in the topological order. This order is a
   * valid execution scheme according to both schedule and graph topology.
   *
   * Uses {@link ScheduleOrderedVisitor} to build the internal list.
   *
   * The result list is unmodifiable.
   */
  public final List<AbstractActor> buildScheduleAndTopologicalOrderedList() {
    if (totalOrderCache == null) {

      final DirectedAcyclicGraph<AbstractActor, DAGedge> graph = getGraph(); // getTransitiveClosure();
      final List<AbstractActor> totalorder = new ArrayList<>(graph.vertexSet().size());
      new TopologicalOrderIterator<>(graph).forEachRemaining(totalorder::add);
      totalOrderCache = totalorder;
    }
    return Collections.unmodifiableList(totalOrderCache);
  }

  /**
   * Builds the list of actors that will execute on operator according to the given mapping, in schedule and topological
   * order.
   *
   * The result list is unmodifiable.
   */
  public final List<AbstractActor> buildScheduleAndTopologicalOrderedList(final Mapping mapping,
      final ComponentInstance operator) {
    List<AbstractActor> order = operatorTotalOrderCache.get(operator);
    if (order == null) {
      order = new ArrayList<>();
      final List<AbstractActor> scheduleAndTopologicalOrderedList = buildScheduleAndTopologicalOrderedList();
      for (final AbstractActor actor : scheduleAndTopologicalOrderedList) {
        if (mapping.getMapping(actor).contains(operator)) {
          order.add(actor);
        }
      }
      operatorTotalOrderCache.put(operator, order);
    }
    return Collections.unmodifiableList(order);
  }

  /**
   * Builds the list of communication actors that will execute on operator according to the given mapping, in schedule
   * and topological order.
   *
   * The result list is unmodifiable.
   */
  public final List<CommunicationActor> buildScheduleAndTopologicalOrderedComm(final Mapping mapping,
      final ComponentInstance operator) {
    List<CommunicationActor> order = new ArrayList<>();
    final List<
        AbstractActor> scheduleAndTopologicalOrderedList = buildScheduleAndTopologicalOrderedList(mapping, operator);
    for (final AbstractActor actor : scheduleAndTopologicalOrderedList) {
      if (actor instanceof CommunicationActor && mapping.getMapping(actor).contains(operator)) {
        order.add((CommunicationActor) actor);
      }
    }
    return Collections.unmodifiableList(order);
  }

  /**
   * Remove an actor from a dag and reconnect its direct incoming neighbors with its direct outgoing neighbors.
   * 
   * @param dag
   *          Graph to consider.
   * @param actor
   *          Actor to remove from the graph, but keeping its incoming/outgoing dependencies.
   */
  private static final void removeAndReconnect(final DirectedAcyclicGraph<AbstractActor, DAGedge> dag,
      final AbstractActor actor) {
    if (dag != null) {
      for (AbstractActor src : Graphs.predecessorListOf(dag, actor)) {
        for (AbstractActor tgt : Graphs.successorListOf(dag, actor)) {
          if (dag.getAllEdges(src, tgt).isEmpty()) {
            dag.addEdge(src, tgt, new DAGedge(DAGedgeType.RECO));
          }
        }
      }
      dag.removeVertex(actor);
    }
  }

  /**
   * Remove the given communication actor from the ActorSchedule that schedules it.
   */
  public final boolean removeCom(final Mapping mapping, final CommunicationActor actor, boolean updateGraphCache) {
    final ActorSchedule actorSchedule = actorToScheduleMap.get(actor);
    if (actorSchedule != null) {
      actorToScheduleMap.remove(actor);
      if (totalOrderCache != null) {
        totalOrderCache.remove(actor);
      }
      for (ComponentInstance ci : mapping.getMapping(actor)) {
        List<AbstractActor> ciSchedTot = operatorTotalOrderCache.get(ci);
        if (ciSchedTot != null) {
          ciSchedTot.remove(actor);
        }
      }
      if (updateGraphCache) {
        removeAndReconnect(graphCache, actor);
        removeAndReconnect(transitiveClosureCache, actor);
      } else {
        resetGraphCache();
      }
      return actorSchedule.getActorList().remove(actor);
    }
    return false;
  }

  private void updateGraphCache(AbstractActor referenceActor, List<AbstractActor> input, boolean after) {
    if (graphCache != null) {

      for (AbstractActor aa : input) {
        graphCache.addVertex(aa);
      }
      if (!input.isEmpty()) {
        for (int i = 1; i < input.size(); i++) {
          graphCache.addEdge(input.get(i - 1), input.get(i), new DAGedge(DAGedgeType.RECO));
        }
        AbstractActor last = input.get(input.size() - 1);
        AbstractActor first = input.get(0);

        if (after) {
          for (AbstractActor suc : Graphs.successorListOf(graphCache, referenceActor)) {
            graphCache.addEdge(last, suc, new DAGedge(DAGedgeType.RECO));
          }
          graphCache.addEdge(referenceActor, first, new DAGedge(DAGedgeType.RECO));
        } else {
          for (AbstractActor pred : Graphs.predecessorListOf(graphCache, referenceActor)) {
            graphCache.addEdge(pred, first, new DAGedge(DAGedgeType.RECO));
          }
          graphCache.addEdge(last, referenceActor, new DAGedge(DAGedgeType.RECO));
        }
      }
    }

  }

  private void updateTransitiveClosureCache(AbstractActor referenceActor, List<AbstractActor> input, boolean after) {
    if (transitiveClosureCache != null) {
      for (AbstractActor aa : input) {
        transitiveClosureCache.addVertex(aa);
      }
      if (!input.isEmpty()) {
        int sizeI = input.size();
        for (AbstractActor aa : input) {
          for (AbstractActor succ : Graphs.successorListOf(transitiveClosureCache, referenceActor)) {
            transitiveClosureCache.addEdge(aa, succ, new DAGedge(DAGedgeType.RECO));
          }
          for (AbstractActor pred : Graphs.predecessorListOf(transitiveClosureCache, referenceActor)) {
            transitiveClosureCache.addEdge(pred, aa, new DAGedge(DAGedgeType.RECO));
          }
        }
        for (int i = 1; i < sizeI; i++) {
          AbstractActor aa = input.get(i);
          for (int j = 0; j < i; j++) {
            transitiveClosureCache.addEdge(input.get(j), aa, new DAGedge(DAGedgeType.RECO));
          }
          for (int j = i + 1; j < sizeI; j++) {
            transitiveClosureCache.addEdge(aa, input.get(j), new DAGedge(DAGedgeType.RECO));
          }
          if (after) {
            transitiveClosureCache.addEdge(referenceActor, aa, new DAGedge(DAGedgeType.RECO));
          } else {
            transitiveClosureCache.addEdge(aa, referenceActor, new DAGedge(DAGedgeType.RECO));
          }
        }
      }
    }

  }

  /**
   * Find the Schedule in which referenceActor appears, insert communication actors after referenceActor in the found
   * Schedule, update internal structure.
   * 
   * @param mapping
   *          Task mapping.
   * @param referenceActor
   *          Communication actors will be inserted after this actor.
   * @param comStart
   *          First communication actor to insert.
   * @param comEnd
   *          Second communication actor to insert.
   * @param updateGraphCache
   *          If internal graph cache must be updated. Should be {@code true} only if using
   *          {@link #getPredecessorEdgesOf} or {@link #getSuccessorEdgesOf} immediately after.
   */
  public final void insertComStEdAfterInSchedule(final Mapping mapping, final AbstractActor referenceActor,
      final CommunicationActor comStart, final CommunicationActor comEnd, boolean updateGraphCache) {

    final ActorSchedule actorSchedule = actorToScheduleMap.get(referenceActor);
    final EList<AbstractActor> srcActorList = actorSchedule.getActorList();
    actorToScheduleMap.put(comStart, actorSchedule);
    actorToScheduleMap.put(comEnd, actorSchedule);

    CollectionUtil.insertAfter2(srcActorList, referenceActor, comStart, comEnd);
    if (totalOrderCache != null) {
      CollectionUtil.insertAfter2(totalOrderCache, referenceActor, comStart, comEnd);
    }

    final Set<ComponentInstance> affectedCIs = new HashSet<>();
    affectedCIs.addAll(mapping.getMapping(comStart));
    affectedCIs.addAll(mapping.getMapping(comEnd));
    if (affectedCIs.size() != 1) {
      throw new PreesmRuntimeException("Cannot insert communications for actors mapped on several or zero operators");
    }

    for (ComponentInstance ci : affectedCIs) {
      List<AbstractActor> ciSchedTot = operatorTotalOrderCache.get(ci);
      if (ciSchedTot != null) {
        CollectionUtil.insertAfter2(ciSchedTot, referenceActor, comStart, comEnd);
      }
    }

    if (updateGraphCache) {
      List<AbstractActor> input = new ArrayList<>(2);
      input.add(comStart);
      input.add(comEnd);
      updateGraphCache(referenceActor, input, true);
      updateTransitiveClosureCache(referenceActor, input, true);
    } else {
      resetGraphCache();
    }
  }

  /**
   * Find the Schedule in which referenceActor appears, insert communication actors before referenceActor in the found
   * Schedule, update internal structure.
   * 
   * @param mapping
   *          Task mapping.
   * @param referenceActor
   *          Communication actors will be inserted before this actor.
   * @param comStart
   *          First communication actor to insert.
   * @param comEnd
   *          Second communication actor to insert.
   * @param updateGraphCache
   *          If internal graph cache must be updated. Should be {@code true} only if using
   *          {@link #getPredecessorEdgesOf} or {@link #getSuccessorEdgesOf} immediately after.
   */
  public final void insertComStEdBeforeInSchedule(final Mapping mapping, final AbstractActor referenceActor,
      final CommunicationActor comStart, final CommunicationActor comEnd, boolean updateGraphCache) {

    final ActorSchedule actorSchedule = actorToScheduleMap.get(referenceActor);
    actorToScheduleMap.put(comStart, actorSchedule);
    actorToScheduleMap.put(comEnd, actorSchedule);

    final EList<AbstractActor> srcActorList = actorSchedule.getActorList();
    CollectionUtil.insertBefore2(srcActorList, referenceActor, comStart, comEnd);
    if (totalOrderCache != null) {
      CollectionUtil.insertBefore2(totalOrderCache, referenceActor, comStart, comEnd);
    }

    final Set<ComponentInstance> affectedCIs = new HashSet<>();
    affectedCIs.addAll(mapping.getMapping(comStart));
    affectedCIs.addAll(mapping.getMapping(comEnd));
    if (affectedCIs.size() != 1) {
      throw new PreesmRuntimeException("Cannot insert communications for actors mapped on several operators");
    }

    for (ComponentInstance ci : affectedCIs) {
      List<AbstractActor> ciSchedTot = operatorTotalOrderCache.get(ci);
      if (ciSchedTot != null) {
        CollectionUtil.insertBefore2(ciSchedTot, referenceActor, comStart, comEnd);
      }
    }

    if (updateGraphCache) {
      List<AbstractActor> input = new ArrayList<>(2);
      input.add(comStart);
      input.add(comEnd);
      updateGraphCache(referenceActor, input, false);
      updateTransitiveClosureCache(referenceActor, input, false);
    } else {
      resetGraphCache();
    }
  }

  /**
   * Builds a map that associate for every actor in the schedule its refering ActorSchedule.
   */
  private static final Map<AbstractActor, ActorSchedule> actorToScheduleMap(final Schedule schedule) {
    final Map<AbstractActor, ActorSchedule> res = new TreeMap<>(new AbstractActorNameComparator());
    new ScheduleSwitch<Boolean>() {
      @Override
      public Boolean caseHierarchicalSchedule(final HierarchicalSchedule hSched) {
        hSched.getScheduleTree().forEach(this::doSwitch);
        return true;
      }

      @Override
      public Boolean caseActorSchedule(final ActorSchedule aSched) {
        aSched.getActorList().forEach(a -> res.put(a, aSched));
        return true;
      }
    }.doSwitch(schedule);
    return res;
  }

  /**
   * Get all edges on the paths to all predecessors of actor. Will loop infinitely if actor is not part of a DAG.
   */
  public final List<Fifo> getAllPredecessorEdgesOf(final AbstractActor actor) {
    final Set<Fifo> result = new LinkedHashSet<>();
    actor.getDataInputPorts().stream().map(DataPort::getFifo).forEach(result::add);
    final List<AbstractActor> allPredecessorsOf = getAllPredecessors(actor);
    allPredecessorsOf.stream().map(AbstractActor::getDataInputPorts).flatMap(List::stream).map(DataPort::getFifo)
        .forEach(result::add);
    return Collections.unmodifiableList(new ArrayList<>(result));
  }

  /**
   * Get all edges on the paths to all successors of actor. Will loop infinitely if actor is not part of a DAG.
   */
  public final List<Fifo> getAllSuccessorEdgesOf(final AbstractActor actor) {
    final Set<Fifo> result = new LinkedHashSet<>();
    actor.getDataOutputPorts().stream().map(DataPort::getFifo).forEach(result::add);
    final List<AbstractActor> allSuccessorsOf = getAllSuccessors(actor);
    allSuccessorsOf.stream().map(AbstractActor::getDataOutputPorts).flatMap(List::stream).map(DataPort::getFifo)
        .forEach(result::add);
    return Collections.unmodifiableList(new ArrayList<>(result));
  }
}
