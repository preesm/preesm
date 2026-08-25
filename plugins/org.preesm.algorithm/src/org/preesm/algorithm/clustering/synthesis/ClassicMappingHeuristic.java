package org.preesm.algorithm.clustering.synthesis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.preesm.algorithm.clustering.heuristics.MappingHeuristic;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInterface;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;

public class ClassicMappingHeuristic extends MappingHeuristic {

  @Override
  public Component selectComponent(PiGraph cluster) {
    final Set<Component> tmp = new HashSet<>();

    final List<
        AbstractActor> clusterActors = cluster.getActors().stream().filter(a -> !(a instanceof DataInterface)).toList();
    for (final AbstractActor a : clusterActors) {
      final List<ComponentInstance> cis = scenario.getPossibleMappings(a);

      // Verifying that every actor has at least one component type in common with the other actors of the cluster
      if (a != clusterActors.getFirst()) {
        actorIsIsolated(a, tmp, cis);
      } else {
        // Adding the component types of first actor only. It is sufficient
        for (final ComponentInstance ci : cis) {
          if (!tmp.contains(ci.getComponent())) {
            tmp.add(ci.getComponent());
          }
        }
      }
    }

    return tmp.stream().toList().getFirst();
  }

  private void actorIsIsolated(AbstractActor a, Set<Component> tmp, List<ComponentInstance> cis) {
    boolean isolated = true;
    for (final ComponentInstance ci : cis) {
      if (tmp.contains(ci.getComponent())) {
        isolated = false;
        break;
      }
    }
    if (isolated) {
      throw new PreesmRuntimeException("Actor " + a.getName()
          + " doesn't have at least one common component type than the other checked actors in the cluster.");
    }
  }

}
