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
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
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
   * @param actor
   *          actor to retrieve self looped delay from
   * @return list of delays
   */
  public static final List<Delay> getSelfLoopedDelays(AbstractActor actor) {
    List<Delay> delays = new LinkedList<>();
    for (DataInputPort dip : actor.getDataInputPorts()) {
      AbstractActor source = (AbstractActor) dip.getIncomingFifo().getSource();
      if (source.equals(actor)) {
        delays.add(dip.getIncomingFifo().getDelay());
      }
    }
    return delays;
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
      long tmpIterator;
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
        if (!schedule.hasAttachedActor()) {
          Schedule childrenSchedule = schedule.getChildren().get(0);
          childrenSchedule.setRepetition(schedule.getRepetition());
          return childrenSchedule;
        } else {
          SequentialHiearchicalSchedule sequenceSchedule = ScheduleFactory.eINSTANCE
              .createSequentialHiearchicalSchedule();
          sequenceSchedule.setAttachedActor(((HierarchicalSchedule) schedule).getAttachedActor());
          sequenceSchedule.setRepetition(schedule.getRepetition());
          sequenceSchedule.getChildren().addAll(schedule.getChildren());
          return sequenceSchedule;
        }
      } else if (schedule instanceof ActorSchedule) {
        SequentialActorSchedule actorSchedule = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
        actorSchedule.setRepetition(schedule.getRepetition());
        actorSchedule.getActorList().addAll(((ActorSchedule) schedule).getActors());
        return actorSchedule;
      }
    }

    return schedule;
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
          "CodegenClusterModelGenerator: cannot find outside-cluster incoming fifo from " + inFifo.getTarget());
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
          "CodegenClusterModelGenerator: cannot find outside-cluster outgoing fifo from " + inFifo.getSource());
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

}
