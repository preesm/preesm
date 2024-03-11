package org.preesm.model.pisdf.util;

import java.util.LinkedList;
import java.util.List;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiGraph;

/**
 * This class is used to seek chain of actors in a given PiGraph that form a Single Repetition Vector above PE number
 * (SRV)
 *
 * @author orenaud
 *
 */
public class ClusteringPatternSeekerLoop extends ClusteringPatternSeeker {

  /**
   * Builds a LoopSeeker based on a input graph.
   *
   * @param inputGraph
   *          Input graph to search in.
   *
   */
  public ClusteringPatternSeekerLoop(final PiGraph inputGraph) {
    super(inputGraph);
  }

  public List<AbstractActor> singleLocalseek() {
    final List<AbstractActor> actorLOOP = new LinkedList<>();
    for (final Delay delays : graph.getDelays()) {
      if (delays.getLevel().equals(PersistenceLevel.NONE)
          && delays.getContainingFifo().getTarget().equals(delays.getContainingFifo().getSource())
          && delays.hasGetterActor() && delays.hasSetterActor()) {
        actorLOOP.add((AbstractActor) delays.getContainingFifo().getTarget());
        return actorLOOP;
      }
    }

    return actorLOOP;
  }

  public List<AbstractActor> pluralLocalseek() {
    final List<AbstractActor> actorLOOP = new LinkedList<>();
    for (final Delay delays : graph.getDelays()) {
      if (delays.getLevel().equals(PersistenceLevel.NONE)
          && !delays.getContainingFifo().getTarget().equals(delays.getContainingFifo().getSource())
          && delays.hasGetterActor() && delays.hasSetterActor()) {
        // TODO
      }
    }
    return actorLOOP;
  }

}
