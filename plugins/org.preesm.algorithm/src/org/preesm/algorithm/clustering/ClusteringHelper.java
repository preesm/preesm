/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.ParallelSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.algorithm.schedule.model.SequentialActorSchedule;
import org.preesm.algorithm.schedule.model.SequentialHiearchicalSchedule;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.scenario.Scenario;

/**
 *
 * @author anmorvan
 *
 */
public class ClusteringHelper {

  /**
   *
   */
  public static final List<DataInputPort> getExternalyConnectedPorts(final Schedule cluster) {
    List<DataInputPort> res = new ArrayList<>();
    final List<AbstractActor> actors = cluster.getActors();
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
   * Used to get great common divisor repetition count of a list of actors
   * 
   * @param actorList
   *          actor to compute gcd repetition from
   * @param repetitionVector
   *          repetition vector of corresponding graph
   * 
   * @return gcd repetition count
   */
  public static final long computeGcdRepetition(List<AbstractActor> actorList,
      Map<AbstractVertex, Long> repetitionVector) {
    // Retrieve all repetition value
    List<Long> actorsRepetition = new LinkedList<>();
    for (AbstractActor a : actorList) {
      if (repetitionVector.containsKey(a)) {
        actorsRepetition.add(repetitionVector.get(a));
      } else {
        throw new PreesmRuntimeException("ClusteringHelper: Repetition vector does not contains key of " + a.getName());
      }
    }
    // Compute gcd
    long clusterRepetition = actorsRepetition.get(0);
    for (int i = 1; i < actorsRepetition.size(); i++) {
      clusterRepetition = ArithmeticUtils.gcd(clusterRepetition, actorsRepetition.get(i));
    }
    return clusterRepetition;
  }

  /**
   * @param actor
   *          actor to check if it is self looped
   * @return true if actor is self looped, false otherwise
   */
  public static final boolean isActorSelfLooped(AbstractActor actor) {
    for (DataInputPort dip : actor.getDataInputPorts()) {
      AbstractActor source = (AbstractActor) dip.getIncomingFifo().getSource();
      if (source.equals(actor)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param schedule
   *          schedule to look at if there is sequential actor schedule inside
   * @return true if there is sequential actor schedule inside
   */
  public static final boolean isSequentialActorScheduleInside(Schedule schedule) {
    if (schedule instanceof HierarchicalSchedule) {
      for (Schedule child : schedule.getChildren()) {
        if (isSequentialActorScheduleInside(child)) {
          return true;
        }
      }
    } else if (schedule instanceof SequentialActorSchedule) {
      return true;
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
      long tmpIterator = iterator;
      for (Schedule child : hierSchedule.getChildren()) {
        tmpIterator = getParallelismDepth(child, iterator);
        if (tmpIterator > maxDepth) {
          maxDepth = tmpIterator;
        }
      }
      iterator = maxDepth;
    }

    if (schedule instanceof ParallelSchedule) {
      iterator++;
    }

    return iterator;
  }

  /**
   * @param schedule
   *          schedule to rework
   * @param depthTarget
   *          depth target
   * @return reworked schedule
   */
  public static final boolean limitParallelismDepth(Schedule schedule, long depthTarget) {
    // Check if limitation has to be performed
    if (getParallelismDepth(schedule, 0) <= depthTarget) {
      return false;
    }

    // Perform limitation
    setParallelismDepth(schedule, 0, depthTarget);

    return true;
  }

  /**
   * @param schedule
   *          schedule to analyze
   * @param iterator
   *          iterator to exploration counter on
   * @return reworked schedule
   */
  public static final Schedule setParallelismDepth(Schedule schedule, long iterator, long depthTarget) {

    // Parallel schedule? Increment iterator because of new parallel layer
    if (schedule instanceof ParallelSchedule) {
      iterator++;
    }

    // Explore and replace children
    if (schedule instanceof HierarchicalSchedule) {
      HierarchicalSchedule hierSchedule = (HierarchicalSchedule) schedule;
      int i = 0;
      for (Schedule child : hierSchedule.getChildren()) {
        hierSchedule.getChildren().set(i, setParallelismDepth(child, iterator, depthTarget));
        i++;
      }
    }

    // Rework if parallel is below the depth target
    if ((schedule instanceof ParallelSchedule) && (iterator > depthTarget)) {
      if (schedule instanceof HierarchicalSchedule) {
        SequentialHiearchicalSchedule sequenceSchedule = ScheduleFactory.eINSTANCE
            .createSequentialHiearchicalSchedule();
        sequenceSchedule.setAttachedActor(((HierarchicalSchedule) schedule).getAttachedActor());
        sequenceSchedule.setRepetition(schedule.getRepetition());
        sequenceSchedule.getChildren().addAll(schedule.getChildren());
        return sequenceSchedule;
      } else if (schedule instanceof ActorSchedule) {
        SequentialActorSchedule actorSchedule = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
        actorSchedule.setRepetition(schedule.getRepetition());
        actorSchedule.getActors().addAll(((ActorSchedule) schedule).getActors());
        return actorSchedule;
      }
    }

    return schedule;
  }

}
