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

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.PiGraph;

/**
 * @author dgageot
 *
 */
public class ParallelClusteringAlgorithm implements IClusteringAlgorithm {

  /**
   * @author dgageot
   *
   */
  private enum ClusteringState {
    SEQUENCE_FIRST, PARALLEL_PASS, SEQUENCE_FINAL
  }

  private List<AbstractActor> forkActors;

  private List<AbstractActor> joinActors;

  List<Pair<AbstractActor, AbstractActor>> couples;

  /**
   * 
   */
  public ParallelClusteringAlgorithm() {
    this.forkActors = null;
    this.joinActors = null;
  }

  @Override
  public Pair<ScheduleType, List<AbstractActor>> findActors(ClusteringBuilder clusteringBuilder) {

    // Clustering state variable
    ClusteringState clusteringState = ClusteringState.SEQUENCE_FIRST;
    List<Pair<AbstractActor, AbstractActor>> couplesSave = new LinkedList<>();
    couplesSave.addAll(couples);

    // Instantiate list of parallel actors
    List<List<AbstractActor>> listParallel = new LinkedList<>();
    // Get list of fork and join actor
    forkActors = getAllForkActors(clusteringBuilder.getAlgorithm());
    joinActors = getAllJoinActors(clusteringBuilder.getAlgorithm());

    // Sequence first
    {
      List<Pair<AbstractActor, AbstractActor>> toRemove = new LinkedList<>();
      // Remove every couple involving fork and join actors
      for (Pair<AbstractActor, AbstractActor> couple : couples) {
        List<AbstractActor> actors = new LinkedList<>();
        actors.add(couple.getLeft());
        actors.add(couple.getRight());
        actors.removeAll(joinActors);
        actors.removeAll(forkActors);
        if (actors.size() < 2) {
          toRemove.add(couple);
        }
      }
      couples.removeAll(toRemove);
      if (couples.isEmpty()) {
        clusteringState = ClusteringState.PARALLEL_PASS;
      }
    }

    // If no sequencable couple, check parallel actors
    if (clusteringState.equals(ClusteringState.PARALLEL_PASS)) {
      // Get all linked actor to fork and join actors
      List<List<AbstractActor>> forkTarget = new LinkedList<>();
      for (AbstractActor fork : forkActors) {
        List<AbstractActor> target = new LinkedList<>();
        fork.getDataOutputPorts().forEach(x -> target.add((AbstractActor) x.getOutgoingFifo().getTarget()));
        forkTarget.add(target);
      }
      List<List<AbstractActor>> joinSource = new LinkedList<>();
      for (AbstractActor join : joinActors) {
        List<AbstractActor> source = new LinkedList<>();
        join.getDataInputPorts().forEach(x -> source.add((AbstractActor) x.getIncomingFifo().getSource()));
        joinSource.add(source);
      }
      // Seek for commons parallel actor
      for (List<AbstractActor> forkTargetSublist : forkTarget) {
        listParallel
            .addAll(joinSource.stream().filter(x -> x.containsAll(forkTargetSublist)).collect(Collectors.toList()));
      }
      // Verify that there is parallel branches
      if (listParallel.isEmpty()) {
        clusteringState = ClusteringState.SEQUENCE_FINAL;
      }
    }

    // Build corresponding actor schedule
    List<AbstractActor> actorsList = new LinkedList<>();
    ScheduleType scheduleType = null;
    Pair<AbstractActor, AbstractActor> bestCouple = null;
    switch (clusteringState) {
      case PARALLEL_PASS:
        scheduleType = ScheduleType.Parallel;
        actorsList.addAll(listParallel.get(0));
        break;
      case SEQUENCE_FIRST:
        scheduleType = ScheduleType.Sequential;
        bestCouple = APGANAlgorithm.getBestCouple(couples, clusteringBuilder.getRepetitionVector());
        actorsList.add(bestCouple.getLeft());
        actorsList.add(bestCouple.getRight());
        break;
      case SEQUENCE_FINAL:
        scheduleType = ScheduleType.Sequential;
        bestCouple = APGANAlgorithm.getBestCouple(couplesSave, clusteringBuilder.getRepetitionVector());
        actorsList.add(bestCouple.getLeft());
        actorsList.add(bestCouple.getRight());
        break;
      default:
        throw new PreesmRuntimeException("ParallelClustering: Unexpected clustering state");
    }

    return new ImmutablePair<>(scheduleType, actorsList);
  }

  @Override
  public boolean clusteringComplete(ClusteringBuilder clusteringBuilder) {
    // Get mergeable couple
    couples = ClusteringHelper.getClusterizableCouples(clusteringBuilder.getAlgorithm(),
        clusteringBuilder.getRepetitionVector(), clusteringBuilder.getScenario());
    return couples.isEmpty();
  }

  private final List<AbstractActor> getAllForkActors(PiGraph graph) {
    List<AbstractActor> forkActorsList = new LinkedList<>();
    for (AbstractActor a : graph.getActors()) {
      List<AbstractActor> explored = new LinkedList<>();
      for (DataOutputPort dop : a.getDataOutputPorts()) {
        AbstractActor child = (AbstractActor) dop.getOutgoingFifo().getTarget();
        if (!explored.contains(child)) {
          explored.add(child);
        }
      }
      // If there is more than one different actor in output
      if (explored.size() > 1) {
        // It is a fork
        forkActorsList.add(a);
      }
    }
    return forkActorsList;
  }

  private final List<AbstractActor> getAllJoinActors(PiGraph graph) {
    List<AbstractActor> joinActorsList = new LinkedList<>();
    for (AbstractActor a : graph.getActors()) {
      List<AbstractActor> explored = new LinkedList<>();
      for (DataInputPort dip : a.getDataInputPorts()) {
        AbstractActor child = (AbstractActor) dip.getIncomingFifo().getSource();
        if (!explored.contains(child)) {
          explored.add(child);
        }
      }
      // If there is more than one different actor in input
      if (explored.size() > 1) {
        // It is a join
        joinActorsList.add(a);
      }
    }
    return joinActorsList;
  }

}
