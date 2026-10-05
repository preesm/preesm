/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019 - 2020)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2021 - 2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020 - 2023)
 * Mickaël Dardaillon [mickael.dardaillon@insa-rennes.fr] (2020)
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
package org.preesm.algorithm.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ParallelSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.SequentialSchedule;
import org.preesm.algorithm.synthesis.schedule.ScheduleUtil;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.PiSDFMergeabilty;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.CPU;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 *
 * @author anmorvan
 *
 */
public class ClusteringHelper {

  private ClusteringHelper() {
    // forbid instantiation
  }

  public static final String PISDF_REFERENCE_ACTOR  = "PiSDFActor";
  public static final String PISDF_ACTOR_IS_CLUSTER = "isCluster";

  /**
   *
   */
  public static final List<DataInputPort> getExternalyConnectedPorts(final Schedule cluster) {
    final List<DataInputPort> res = new ArrayList<>();
    final List<AbstractActor> actors = ScheduleUtil.getAllReferencedActors(cluster);
    for (final AbstractActor actor : actors) {
      final EList<DataInputPort> dataInputPorts = actor.getDataInputPorts();
      for (final DataInputPort port : dataInputPorts) {
        final Fifo fifo = port.getFifo();
        // filter ports connected within the cluster
        final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
        if (ECollections.indexOf(actors, sourceActor, 0) != -1) {
          // source actor is within the cluster
          // skip
        } else {
          res.add(port);
        }
      }
    }

    return res;
  }

  /**
   * @param actor
   *          actor to check if it is delayed
   * @return true if actor is delayed, false otherwise
   */
  public static final boolean isActorDelayed(AbstractActor actor) {
    // Retrieve every Fifo with delay connected to actor
    for (final DataPort dp : actor.getAllDataPorts()) {
      if (dp.getFifo().getDelay() != null) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param schedule
   *          schedule to analyze
   * @param iterator
   *          iterator to exploration counter on
   * @return depth of parallelism
   */
  public static final long getParallelismDepth(Schedule schedule, long iterator) {

    if (schedule instanceof final HierarchicalSchedule hierSchedule) {
      long maxDepth = iterator;
      long tmpIterator;
      for (final Schedule child : hierSchedule.getChildren()) {
        tmpIterator = getParallelismDepth(child, iterator);
        if (tmpIterator > maxDepth) {
          maxDepth = tmpIterator;
        }
      }
      iterator = maxDepth;
    }

    // Increment iterator because we found a parallel area
    if (schedule instanceof ParallelSchedule) {
      iterator++;
    }

    return iterator;
  }

  /**
   * @param schedule
   *          schedule to get memory space needed for
   * @return bits needed for execution of schedule
   */
  public static final long getMemorySpaceNeededFor(Schedule schedule) {
    long result = 0;
    if (schedule instanceof HierarchicalSchedule) {
      // Add memory space needed for children in result
      for (final Schedule child : schedule.getChildren()) {
        result += getMemorySpaceNeededFor(child);
      }
      // If it is a parallel hierarchical schedule with no attached actor, multiply child memory space result by the
      // repetition of it
      if (!schedule.hasAttachedActor()) {
        final long rep = schedule.getRepetition();
        result *= rep;
      } else {
        // Estimate every internal buffer size
        final PiGraph graph = (PiGraph) ((HierarchicalSchedule) schedule).getAttachedActor();
        final List<Fifo> fifos = ClusteringHelper.getInternalClusterFifo(graph);
        final Map<AbstractVertex, Long> brv = PiBRV.compute(graph, BRVMethod.LCM);
        for (final Fifo fifo : fifos) {
          result += brv.get(fifo.getSource()) * fifo.getSourcePort().getExpression().evaluateAsLong();
        }
      }
    }
    return result;
  }

  /**
   * @param schedule
   *          schedule to get execution time from
   * @return execution time
   */
  public static final long getExecutionTimeOf(Schedule schedule, Scenario scenario, Component component) {
    long timing = 0;

    // If schedule is hierarchical
    if (schedule instanceof HierarchicalSchedule) {
      timing = getExecutionTimeOfHierarchical(schedule, scenario, component, timing);
    } else {
      // Retrieve timing from actors
      final List<AbstractActor> actors = ScheduleUtil.getAllReferencedActors(schedule);
      final AbstractActor actor = actors.get(0);
      final long actorTiming = scenario.getTimings().evaluateExecutionTimeOrDefault(actor, component);
      timing = schedule.getRepetition() * actorTiming;
    }

    return timing;
  }

  private static long getExecutionTimeOfHierarchical(Schedule schedule, Scenario scenario, Component component,
      long timing) {
    // If schedule is sequential
    if (schedule instanceof SequentialSchedule) {
      // Sum timings of all childrens together
      for (final Schedule child : schedule.getChildren()) {
        timing += getExecutionTimeOf(child, scenario, component);
      }
    } else {
      // If schedule is parallel
      // Search for the maximun time taken by childrens
      long max = 0;
      for (final Schedule child : schedule.getChildren()) {
        final long result = getExecutionTimeOf(child, scenario, component);
        if (result > max) {
          max = result;
        }
      }
      // Add max execution time to timing
      timing += max;
    }

    // If it is repeated, multiply timing by the time of
    if (schedule.getRepetition() > 1) {
      timing *= schedule.getRepetition();
    }
    return timing;
  }

  /**
   * Used to get list of Fifo that interconnect actor included in the graph
   *
   * @param graph
   *          graph to get internal cluster Fifo from
   * @return list of Fifo that connect actor inside of graph
   */
  public static final List<Fifo> getInternalClusterFifo(final PiGraph graph) {
    final List<Fifo> internalFifo = new LinkedList<>();
    for (final Fifo fifo : graph.getFifos()) {
      // If the fifo connect two included actor,
      if (!(fifo.getSource() instanceof DataInputInterface) && !(fifo.getTarget() instanceof DataOutputInterface)) {
        // add it to internalFifo list
        internalFifo.add(fifo);
      }
    }
    return internalFifo;
  }

  /**
   * Used to get the incoming Fifo from top level graph
   *
   * @param inFifo
   *          inside incoming fifo
   * @return outside incoming fifo
   */
  public static Fifo getOutsideIncomingFifo(final Fifo inFifo) {
    final AbstractActor sourceActor = inFifo.getSource();
    if (sourceActor instanceof final DataInputInterface inputInterface) {
      return inputInterface.getGraphPort().getIncomingFifo();
    }
    throw new PreesmRuntimeException(
        "ClusteringHelper: cannot find outside-cluster incoming fifo from " + inFifo.getTarget());
  }

  /**
   * Used to get the outgoing Fifo from top level graph
   *
   * @param inFifo
   *          inside outgoing fifo
   * @return outside outgoing fifo
   */
  public static Fifo getOutsideOutgoingFifo(final Fifo inFifo) {
    final AbstractActor targetActor = inFifo.getTarget();
    if (targetActor instanceof final DataOutputInterface outputInterface) {
      return (outputInterface.getGraphPort()).getOutgoingFifo();
    }
    throw new PreesmRuntimeException(
        "ClusteringHelper: cannot find outside-cluster outgoing fifo from " + inFifo.getSource());
  }

  /**
   * Used to get setter parameter for a specific ConfigInputPort
   *
   * @param port
   *          port to get parameter from
   * @return parameter
   */
  public static Parameter getSetterParameter(final ConfigInputPort port) {
    final Dependency dep = port.getIncomingDependency();
    if (dep == null) {
      return null;
    }
    final ISetter setter = dep.getSetter();
    if (setter instanceof ConfigInputInterface) {
      return getSetterParameter(((ConfigInputInterface) port.getIncomingDependency().getSetter()).getGraphPort());
    }
    return (Parameter) setter;
  }

  /**
   * @param graph
   *          input graph
   * @param brv
   *          repetition vector
   * @param scenario
   *          scenario
   * @return list of clusterizable couple
   */
  public static List<Pair<AbstractActor, AbstractActor>> getClusterizableCouples(final PiGraph graph,
      final Map<AbstractVertex, Long> brv, Scenario scenario) {
    final List<Pair<AbstractActor, AbstractActor>> couples = PiSDFMergeabilty.getConnectedCouple(graph, brv);
    // Remove couples of actors that are not in the same constraints
    ClusteringHelper.removeConstrainedCouples(couples, scenario);
    return couples;
  }

  /**
   * @param couples
   *          list of mergeable couple
   * @param scenario
   *          scenario
   */
  public static void removeConstrainedCouples(List<Pair<AbstractActor, AbstractActor>> couples, Scenario scenario) {
    final List<Pair<AbstractActor, AbstractActor>> tmpCouples = new LinkedList<>(couples);
    couples.clear();
    for (final Pair<AbstractActor, AbstractActor> couple : tmpCouples) {
      final List<ComponentInstance> componentList = getListOfCommonComponent(
          Arrays.asList(couple.getLeft(), couple.getRight()), scenario);
      if (!componentList.isEmpty()) {
        couples.add(couple);
      }
    }
  }

  /**
   * This method will compute the number of core for the current node. For now, multi-node in PREESM doesn't exist, and
   * this function works only for CPUs. It will only balance the number of cores if they are heterogeneous.
   *
   * @param inputScenario
   *          the input scenario needed to make the compute.
   * @return the equivalent node
   */
  public static Long computeSingleNodeCoreEquivalent(Scenario inputScenario) {
    final PiGraph inputGraph = inputScenario.getAlgorithm();
    final Design inputArchi = inputScenario.getDesign();
    // filter CPU component
    final List<ComponentInstance> cpuInstances = inputArchi.getOperatorComponentInstances().stream()
        .filter(opId -> opId.getComponent() instanceof CPU).toList();
    Long coreEq = 0L;
    int actorNumber = 0;
    for (final AbstractActor actor : inputGraph.getExecutableActors()) {
      // sink and source actor replace interface for SimSDP
      if (actor instanceof Actor && !actor.getName().contains("src_") && !actor.getName().contains("snk_")
          && !(actor instanceof DelayActor)) {

        Long sumTiming = 0L;
        Long slow = Long.valueOf(inputScenario.getTimings().getExecutionTimeOrDefault(actor,
            inputArchi.getOperatorComponentInstances().stream().map(ComponentInstance::getComponent)
                .filter(CPU.class::isInstance).findFirst().orElseThrow()));

        for (final ComponentInstance cpu : cpuInstances) {
          sumTiming += Long.valueOf(inputScenario.getTimings().getExecutionTimeOrDefault(actor, cpu.getComponent()));
          final Long timeSeek = Long
              .valueOf(inputScenario.getTimings().getExecutionTimeOrDefault(actor, cpu.getComponent()));

          slow = timeSeek < slow ? timeSeek : slow;

        }
        coreEq += (sumTiming / slow);
        actorNumber++;
      }
    }
    coreEq = actorNumber > 0 ? coreEq / actorNumber
        : inputArchi.getOperatorComponentInstances().stream().filter(opId -> opId.getComponent() instanceof CPU)
            .count();

    return coreEq;
  }

  /**
   * This method will return a list of component that are a possible mapping for every actor in the actor list.
   *
   * @param actorList
   *          list of actor
   * @param scenario
   *          scenario
   * @return List of components
   */
  public static List<ComponentInstance> getListOfCommonComponent(List<AbstractActor> actorList, Scenario scenario) {
    final List<ComponentInstance> globalList = new LinkedList<>(scenario.getPossibleMappings(actorList.get(0)));
    for (final AbstractActor actor : actorList) {
      final List<ComponentInstance> componentList = scenario.getPossibleMappings(actor);
      globalList.retainAll(componentList);
    }
    return globalList;
  }

  public static final String         INIT_PREFIX         = "init_";
  public static final String         LOOP_PREFIX         = "loop_";
  protected static final Set<String> existingClusterName = new HashSet<>();

  public static String getInitPrototypeName(PiGraph cluster) {
    final String clusterName = cluster.getName();

    int i = 0;
    String newClusterName = clusterName;
    while (existingClusterName.contains(newClusterName)) {
      newClusterName = clusterName + "_" + i++;
    }
    return INIT_PREFIX + newClusterName;
  }

  public static String getLoopPrototypeName(PiGraph cluster) {
    final String clusterName = cluster.getName();

    int i = 0;
    String newClusterName = clusterName;
    while (existingClusterName.contains(newClusterName)) {
      newClusterName = clusterName + "_" + i++;
    }
    return LOOP_PREFIX + newClusterName;
  }

  /**
   * APGAN Scheduler can't work if there is getter and setter actors for a delay in a cluster. This helper method can be
   * used while validating detected clusters in different identification heuristics.
   *
   * @param cluster
   *          Set of actors in cluster
   * @return true if there is getter / setter actors in cluster
   */
  public static boolean clusterHasGetterAndSetterActors(Set<AbstractActor> cluster) {
    return !cluster.stream().allMatch(a -> {
      boolean sub = true;

      for (final DataPort dp : a.getAllDataPorts()) {
        if (dp.getFifo().getDelay() != null) {
          final Delay delay = dp.getFifo().getDelay();

          // If delay has getter/setter,
          if (delay.getDelayActor().getDataInputPort().getIncomingFifo() != null
              || delay.getDelayActor().getDataOutputPort().getOutgoingFifo() != null) {
            sub = false;
            break;
          }
        }
      }
      return sub;
    });
  }

  /**
   * WIP
   *
   * @param cluster
   *          input cluster
   * @return true if correct
   */
  public static boolean smartCheckDelays(Set<AbstractActor> cluster) {
    return cluster.stream().allMatch(a -> {
      boolean sub = true;
      for (final DataPort p : a.getAllDataPorts()) {
        sub &= smartCheckDelay(p.getFifo(), cluster);
      }
      return sub;
    });
  }

  public static boolean checkDelays(Set<AbstractActor> cluster) {
    return cluster.stream().allMatch(a -> {
      boolean sub = true;
      for (final DataPort p : a.getAllDataPorts()) {
        sub &= !(p.getFifo().isDelayPresent());
      }
      return sub;
    });
  }

  /**
   * WIP
   *
   * @param fifo
   *          input fifo
   * @param cluster
   *          input cluster
   * @return true if correct
   */
  private static boolean smartCheckDelay(Fifo fifo, Set<AbstractActor> cluster) {
    if (fifo.isDelayPresent()) {
      final AbstractActor source = fifo.getSource();
      final AbstractActor target = fifo.getTarget();
      final boolean sourceIsLast = source.getDataOutputPorts().stream()
          .anyMatch(p -> !cluster.contains(p.getFifo().getTarget()));
      final boolean targetIsFirst = target.getDataInputPorts().stream()
          .anyMatch(p -> !cluster.contains(p.getFifo().getSource()));

      if (sourceIsLast && targetIsFirst) {
        return true;
      }
      if (sourceIsLast) {
        return target.getDataInputPorts().stream().allMatch(p -> p.getFifo().getSource() == source);
      }

      if (targetIsFirst) {
        return source.getDataOutputPorts().stream().allMatch(p -> p.getFifo().getTarget() == target);
      }
      return false;
    }
    return true;
  }

  /**
   * This method generates {@link SpecialActor special actors} with one {@link DataInputPort input} and one
   * {@link DataOutputPort output}, to be able to perform smart cluster memory {@link Allocation allocation}. This
   * allocation is made with a {@link PiGraph PiSDF}, not with a SrDAG. That is why we are generating special actors
   * that would have been generated in the SrDAG.
   *
   * @param cluster
   *          the input cluster (PiSDF graph)
   */
  public static void addSpecialActors(PiGraph cluster) {
    // I think adding Fork and Join actors is useless to improve memory reuse in cluster
    addBroadcastActors(cluster);
    addRoundBufferActors(cluster);
  }

  public static void addAllSpecialActors(PiGraph graph) {
    addSpecialActors(graph);
    for (final PiGraph child : graph.getChildrenGraphs()) {
      addAllSpecialActors(child);
    }
  }

  /**
   * For every {@link DataInputInterface data input interface}, it checks if a {@link BroadcastActor broadcast actor}
   * needs to be generated. The condition is : if a is linked to b, a being the data input interface, and brv value of b
   * is strictly higher than 1, then we can add a {@link BroadcastActor broadcast actor} to make a smart cluster memory
   * allocation in a future step of the {@link ClusterSynthesisTask cluster synthesis task}.
   *
   * @param cluster
   *          the input cluster
   */
  private static void addBroadcastActors(PiGraph cluster) {

    long nameCounter = 0;

    final Map<AbstractVertex, Long> brv = PiBRV.compute(cluster, BRVMethod.LCM);

    for (final DataInputInterface a : cluster.getDataInputInterfaces()) {
      final DataOutputPort aOut = a.getDataPort();
      final Fifo a2b = aOut.getFifo();
      final AbstractActor b = a2b.getTarget();
      final DataInputPort bIn = a2b.getTargetPort();
      final long aExpr = a.getGraphPort().getExpression().evaluateAsLong();
      final long bInExpr = bIn.getExpression().evaluateAsLong();

      // If b is executed only once or next actor is already a broadcast actor, it means adding a broadcast is not
      // necessary
      if (brv.get(b) * bInExpr == aExpr || b instanceof BroadcastActor) {
        continue;
      }

      // Creating broadcast actor
      final BroadcastActor brd = PiMMUserFactory.instance.createBroadcastActor();
      brd.setName("brd_" + nameCounter++);

      // Creating in/out broadcast ports
      final DataInputPort brdIn = PiMMUserFactory.instance.createDataInputPort();
      brdIn.setName("brd_in");
      final DataOutputPort brdOut = PiMMUserFactory.instance.createDataOutputPort();
      brdOut.setName("brd_out");
      brd.getDataInputPorts().add(brdIn);
      brd.getDataOutputPorts().add(brdOut);

      // Setting expression of ports
      aOut.setExpression(aExpr); // otherwise it bugs...
      brdIn.setExpression(aExpr);
      brdOut.setExpression(brv.get(b) * bInExpr);

      // Linking broadcast with a and b
      final String dataType = a2b.getType();
      final Fifo a2brd = a2b;
      a2brd.setTargetPort(brdIn);
      final Fifo brd2b = PiMMUserFactory.instance.createFifo(brdOut, bIn, dataType);
      cluster.addActor(brd);
      cluster.addFifo(brd2b);
    }
  }

  /**
   * For every {@link DataOutputInterface data input interface}, it checks if a {@link RoundBufferActor round buffer
   * actor} needs to be generated. The condition is : if a is linked to b, b being the data input interface, and brv
   * value of a is strictly higher than 1, then we can add a {@link RoundBufferActor round buffer actor} to allow a
   * smart cluster memory allocation in a future step of the {@link ClusterSynthesisTask cluster synthesis task}.
   *
   * @param cluster
   *          the input cluster
   */
  private static void addRoundBufferActors(PiGraph cluster) {

    long nameCounter = 0;

    final Map<AbstractVertex, Long> brv = PiBRV.compute(cluster, BRVMethod.LCM);

    for (final DataOutputInterface b : cluster.getDataOutputInterfaces()) {
      final DataInputPort bIn = b.getDataPort();
      final Fifo a2b = bIn.getFifo();
      final AbstractActor a = a2b.getSource();
      final DataOutputPort aOut = a2b.getSourcePort();
      final long aOutExpr = aOut.getExpression().evaluateAsLong();
      final long bExpr = b.getGraphPort().getExpression().evaluateAsLong();

      // If a is executed only once or next actor is already a round buffer actor, it means adding a broadcast is not
      // necessary
      if (brv.get(a) * aOutExpr == bExpr || b instanceof RoundBufferActor) {
        continue;
      }

      // Creating round buffer actor
      final RoundBufferActor rdb = PiMMUserFactory.instance.createRoundBufferActor();
      rdb.setName("rdb_" + nameCounter++);

      // Creating in/out broadcast ports
      final DataInputPort rdbIn = PiMMUserFactory.instance.createDataInputPort();
      rdbIn.setName("rdb_in");
      final DataOutputPort rdbOut = PiMMUserFactory.instance.createDataOutputPort();
      rdbOut.setName("rdb_out");
      rdb.getDataInputPorts().add(rdbIn);
      rdb.getDataOutputPorts().add(rdbOut);

      // Setting expression of ports
      bIn.setExpression(bExpr); // otherwise it bugs...
      rdbIn.setExpression(brv.get(a) * aOutExpr);
      rdbOut.setExpression(bExpr);

      // Linking broadcast with a and b
      final String dataType = a2b.getType();
      final Fifo a2rdb = a2b;
      a2rdb.setTargetPort(rdbIn);
      final Fifo brd2b = PiMMUserFactory.instance.createFifo(rdbOut, bIn, dataType);
      cluster.addActor(rdb);
      cluster.addFifo(brd2b);
    }
  }

  /**
   * Computes the scope repetition of current schedule. For example, if s.getRoot = a2(b3(c2d)), computeScopeRepetition
   * of b3(c2d) will be equal to 2, and computeScopeRepetition of c2d will be equal to 2 * 3 = 6.
   *
   * @param s
   *          current schedule
   * @return the scope repetition of s
   */
  public static long computeScopeRepetition(final Schedule s) {
    long scopeRepetition = s.getRepetition();
    if (scopeRepetition == 0) {
      scopeRepetition = 1;
    }
    Schedule parent = s.getParent();
    while (parent != null) {
      final long parentRep = parent.getRepetition();
      scopeRepetition *= (parentRep == 0) ? 1 : parentRep;
      parent = parent.getParent();
    }
    return scopeRepetition;
  }
}
