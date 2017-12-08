package org.ietr.preesm.experiment.model.pimm.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

/**
 *
 * @author anmorvan
 *
 */
public class ActorPath {

  private ActorPath() {
    // no instantiation allowed
  }

  /**
   *
   */
  public static final AbstractActor lookup(final PiGraph graph, final String actorPath) {

    final String safePath = actorPath.replaceAll("/+", "/").replaceAll("^/*" + graph.getName(), "").replaceAll("^/", "").replaceAll("/$", "");
    if (safePath.isEmpty()) {
      return graph;
    }
    final List<String> pathFragments = new ArrayList<>(Arrays.asList(safePath.split("/")));
    final String firstFragment = pathFragments.remove(0);
    final AbstractActor current = graph.getActors().stream().filter(a -> firstFragment.equals(a.getName())).findFirst().orElse(null);
    if (pathFragments.isEmpty()) {
      return current;
    } else {
      final String remainingPathFragments = String.join("/", pathFragments);
      if (current instanceof PiGraph) {
        return ActorPath.lookup((PiGraph) current, remainingPathFragments);
      } else if (current instanceof Actor) {
        final Actor actor = (Actor) current;
        if (actor.isHierarchical()) {
          return ActorPath.lookup(actor.getSubGraph(), remainingPathFragments);
        } else {
          return null;
        }
      } else {
        return null;
      }
    }
  }
}
