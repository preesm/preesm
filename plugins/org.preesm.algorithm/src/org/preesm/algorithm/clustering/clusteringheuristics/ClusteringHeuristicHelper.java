package org.preesm.algorithm.clustering.clusteringheuristics;

import java.util.ArrayList;
import java.util.List;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.slam.Component;

/**
 * The goal of this class is to store generic static methods that can be used by many clustering heuristics. For more
 * detail, go see the description of each individual methods.
 */
public class ClusteringHeuristicHelper {

  private ClusteringHeuristicHelper() {
  }

  /**
   * TODO : make the method
   *
   * @param actors
   *          the list of actors
   * @return the list of components, where every input actors are mapped to. If
   */
  public static List<Component> getCommonComponents(List<AbstractActor> actors) {
    final List<Component> components = new ArrayList<>();

    return components;
  }

}
