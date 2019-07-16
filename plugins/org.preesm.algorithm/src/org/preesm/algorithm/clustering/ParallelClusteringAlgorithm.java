package org.preesm.algorithm.clustering;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.algorithm.schedule.ActorSchedule;
import org.preesm.model.algorithm.schedule.ScheduleFactory;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.PiSDFMergeabilty;

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

  private IClusteringAlgorithm secondHandAlgorithm;

  /**
   * 
   */
  public ParallelClusteringAlgorithm() {
    this.forkActors = null;
    this.joinActors = null;
    this.secondHandAlgorithm = new APGANClusteringAlgorithm();
  }

  @Override
  public ActorSchedule findActors(ClusteringBuilder clusteringBuilder) {

    // Clustering state variable
    ClusteringState clusteringState = ClusteringState.SEQUENCE_FIRST;

    // Get list of mergeable couple
    List<Pair<AbstractActor, AbstractActor>> listCouple = PiSDFMergeabilty
        .getConnectedCouple(clusteringBuilder.getAlgorithm());
    List<List<AbstractActor>> listParallel = new LinkedList<>();
    // Get list of fork and join actor
    forkActors = getAllForkActors(clusteringBuilder.getAlgorithm());
    joinActors = getAllJoinActors(clusteringBuilder.getAlgorithm());

    // Sequence first
    {
      List<Pair<AbstractActor, AbstractActor>> toRemove = new LinkedList<>();
      // Remove every couple involving fork and join actors
      for (Pair<AbstractActor, AbstractActor> couple : listCouple) {
        List<AbstractActor> actors = new LinkedList<>();
        actors.add(couple.getLeft());
        actors.add(couple.getRight());
        actors.removeAll(joinActors);
        actors.removeAll(forkActors);
        if (actors.size() < 2) {
          toRemove.add(couple);
        }
      }
      listCouple.removeAll(toRemove);
      if (listCouple.isEmpty()) {
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
    ActorSchedule schedule = null;
    switch (clusteringState) {
      case PARALLEL_PASS:
        schedule = ScheduleFactory.eINSTANCE.createParallelActorSchedule();
        schedule.getOrderedActors().addAll(listParallel.get(0));
        break;
      case SEQUENCE_FIRST:
        schedule = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
        schedule.getOrderedActors().add(listCouple.get(0).getLeft());
        schedule.getOrderedActors().add(listCouple.get(0).getRight());
        break;
      case SEQUENCE_FINAL:
        schedule = secondHandAlgorithm.findActors(clusteringBuilder);
        break;
      default:
        throw new PreesmRuntimeException("ParallelClustering: Unexepected clustering state");
    }

    return schedule;
  }

  @Override
  public boolean clusteringComplete(ClusteringBuilder clusteringBuilder) {
    boolean returnValue = true;
    if (clusteringBuilder.getAlgorithm().getActors().size() > 1) {
      returnValue = false;
    }
    return returnValue;
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
