package org.preesm.algorithm.clustering;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.util.PiSDFMergeabilty;

/**
 * @author dgageot
 *
 */
public class RandomClusteringAlgorithm implements IClusteringAlgorithm {

  private Random generator;

  public RandomClusteringAlgorithm(long seed) {
    generator = new Random(seed);
  }

  List<Pair<AbstractActor, AbstractActor>> couples;

  @Override
  public Pair<ScheduleType, List<AbstractActor>> findActors(ClusteringBuilder clusteringBuilder) {
    // Get a random number based on the given generator
    int randomInt = generator.nextInt(couples.size());
    // Return a random couple
    // Build corresponding actor list
    List<AbstractActor> actorsList = new LinkedList<>();
    actorsList.add(couples.get(randomInt).getLeft());
    actorsList.add(couples.get(randomInt).getRight());

    return new ImmutablePair<>(ScheduleType.Sequential, actorsList);
  }

  @Override
  public boolean clusteringComplete(ClusteringBuilder clusteringBuilder) {
    // Get mergeable couple
    couples = PiSDFMergeabilty.getConnectedCouple(clusteringBuilder.getAlgorithm(),
        clusteringBuilder.getRepetitionVector());
    return couples.isEmpty();
  }

}
