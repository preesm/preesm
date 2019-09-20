/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019)
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ParallelActorSchedule;
import org.preesm.algorithm.schedule.model.ParallelSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.SequentialSchedule;
import org.preesm.algorithm.synthesis.schedule.iterator.SimpleScheduleIterator;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.util.PiSDFMergeabilty;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;

/**
 *
 * @author anmorvan
 *
 */
public class ClusteringHelper {

  private ClusteringHelper() {
    // forbid instantiation
  }

  /**
   *
   */
  public static final List<DataInputPort> getExternalyConnectedPorts(final Schedule cluster) {
    List<DataInputPort> res = new ArrayList<>();
    final List<AbstractActor> actors = new SimpleScheduleIterator(cluster).getOrderedList();
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
   *
   */
  public static final long getTotalMachin(final Schedule cluster, final Scenario scenario) {
    final List<DataInputPort> ports = getExternalyConnectedPorts(cluster);
    return ports.stream().mapToLong(p -> p.getPortRateExpression().evaluate()
        * scenario.getSimulationInfo().getDataTypeSizeOrDefault(p.getFifo().getType())).sum();
  }

  /**
   * @param actor
   *          actor to check if it is delayed
   * @return true if actor is delayed, false otherwise
   */
  public static final boolean isActorDelayed(AbstractActor actor) {
    // Retrieve every Fifo with delay connected to actor
    for (DataPort dp : actor.getAllDataPorts()) {
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

    if (schedule instanceof HierarchicalSchedule) {
      HierarchicalSchedule hierSchedule = (HierarchicalSchedule) schedule;
      long maxDepth = iterator;
      long tmpIterator;
      for (Schedule child : hierSchedule.getChildren()) {
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
   * @return bytes needed for execution of schedule
   */
  public static final long getMemorySpaceNeededFor(Schedule schedule) {
    long result = 0;
    if (schedule instanceof HierarchicalSchedule) {
      // Add memory space needed for children in result
      for (Schedule child : schedule.getChildren()) {
        result += getMemorySpaceNeededFor(child);
      }
      // If it is a parallel hierarchical schedule with no attached actor, multiply child memory space result by the
      // repetition of it
      if (!schedule.hasAttachedActor()) {
        long rep = schedule.getRepetition();
        result *= rep;
      } else {
        // Estimate every internal buffer size
        PiGraph graph = (PiGraph) ((HierarchicalSchedule) schedule).getAttachedActor();
        List<Fifo> fifos = ClusteringHelper.getInternalClusterFifo(graph);
        Map<AbstractVertex, Long> brv = PiBRV.compute(graph, BRVMethod.LCM);
        for (Fifo fifo : fifos) {
          result += brv.get(fifo.getSource()) * fifo.getSourcePort().getExpression().evaluate();
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
      final List<AbstractActor> actors = new SimpleScheduleIterator(schedule).getOrderedList();
      AbstractActor actor = actors.get(0);
      long actorTiming = scenario.getTimings().evaluateTimingOrDefault(actor, component);
      if ((schedule instanceof ParallelActorSchedule)) {
        timing = actorTiming;
      } else {
        timing = schedule.getRepetition() * actorTiming;
      }
    }

    return timing;
  }

  private static long getExecutionTimeOfHierarchical(Schedule schedule, Scenario scenario, Component component,
      long timing) {
    // If schedule is sequential
    if (schedule instanceof SequentialSchedule) {
      // Sum timings of all childrens together
      for (Schedule child : schedule.getChildren()) {
        timing += getExecutionTimeOf(child, scenario, component);
      }
    } else {
      // If schedule is parallel
      // Search for the maximun time taken by childrens
      long max = 0;
      for (Schedule child : schedule.getChildren()) {
        long result = getExecutionTimeOf(child, scenario, component);
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
    final AbstractActor sourceActor = (AbstractActor) inFifo.getSource();
    if (sourceActor instanceof DataInputInterface) {
      return ((DataInputPort) ((DataInputInterface) sourceActor).getGraphPort()).getIncomingFifo();
    } else {
      throw new PreesmRuntimeException(
          "ClusteringHelper: cannot find outside-cluster incoming fifo from " + inFifo.getTarget());
    }
  }

  /**
   * Used to get the outgoing Fifo from top level graph
   *
   * @param inFifo
   *          inside outgoing fifo
   * @return outside outgoing fifo
   */
  public static Fifo getOutsideOutgoingFifo(final Fifo inFifo) {
    final AbstractActor targetActor = (AbstractActor) inFifo.getTarget();
    if (targetActor instanceof DataOutputInterface) {
      return ((DataOutputPort) ((DataOutputInterface) targetActor).getGraphPort()).getOutgoingFifo();
    } else {
      throw new PreesmRuntimeException(
          "ClusteringHelper: cannot find outside-cluster outgoing fifo from " + inFifo.getSource());
    }
  }

  /**
   * Used to get setter parameter for a specific ConfigInputPort
   *
   * @param port
   *          port to get parameter from
   * @return parameter
   */
  public static Parameter getSetterParameter(final ConfigInputPort port) {
    final ISetter setter = port.getIncomingDependency().getSetter();
    if (setter instanceof ConfigInputInterface) {
      return getSetterParameter(((ConfigInputInterface) port.getIncomingDependency().getSetter()).getGraphPort());
    } else {
      return (Parameter) setter;
    }
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
    List<Pair<AbstractActor, AbstractActor>> couples = PiSDFMergeabilty.getConnectedCouple(graph, brv);
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
    List<Pair<AbstractActor, AbstractActor>> tmpCouples = new LinkedList<>();
    tmpCouples.addAll(couples);
    couples.clear();
    for (Pair<AbstractActor, AbstractActor> couple : tmpCouples) {
      List<ComponentInstance> componentList = getListOfCommonComponent(
          Arrays.asList(couple.getLeft(), couple.getRight()), scenario);
      if (!componentList.isEmpty()) {
        couples.add(couple);
      }
    }
  }

  /**
   * @param actorList
   *          list of actor
   * @param scenario
   *          scenario
   * @return
   */
  public static List<ComponentInstance> getListOfCommonComponent(List<AbstractActor> actorList, Scenario scenario) {
    List<ComponentInstance> globalList = new LinkedList<>();
    globalList.addAll(scenario.getPossibleMappings(actorList.get(0)));
    for (AbstractActor actor : actorList) {
      List<ComponentInstance> componentList = scenario.getPossibleMappings(actor);
      globalList.retainAll(componentList);
    }
    return globalList;
  }

}
