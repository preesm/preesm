package org.preesm.model.pisdf.util;

import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.model.PreesmAdapter;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.PiGraph;

/**
 *
 */
public class SubgraphOriginalActorTracker extends PreesmAdapter {

  /**
   *
   */
  public static final void trackOriginalActor(final Actor actor, final PiGraph graph) {
    final Actor originalActor2 = getOriginalActor(graph);
    if (originalActor2 != null && originalActor2 != actor) {
      throw new PreesmException();
    }
    final SubgraphOriginalActorTracker adapter = new SubgraphOriginalActorTracker(actor);
    graph.eAdapters().add(adapter);
  }

  /**
   *
   */
  public static final Actor getOriginalActor(final PiGraph graph) {
    final SubgraphOriginalActorTracker adapter = PreesmAdapter.adapt(graph, SubgraphOriginalActorTracker.class);
    if (adapter != null) {
      return adapter.getOriginalActor();
    } else {
      return null;
    }
  }

  private final Actor originalActor;

  public SubgraphOriginalActorTracker(final Actor originalActor) {
    super();
    this.originalActor = originalActor;
  }

  public Actor getOriginalActor() {
    return originalActor;
  }
}
