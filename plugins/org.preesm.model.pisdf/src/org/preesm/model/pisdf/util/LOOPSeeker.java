package org.preesm.model.pisdf.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
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
public class LOOPSeeker extends PiMMSwitch<Boolean> {

  /**
   * Input graph.
   */
  final PiGraph graph;

  final int                       nPEs;
  final Map<AbstractVertex, Long> brv;

  /**
   * Builds a SRVSeeker based on a input graph.
   *
   * @param inputGraph
   *          Input graph to search in.
   * @param brv
   *          repetition vector
   * @param numberOfPEs
   *
   */
  public LOOPSeeker(final PiGraph inputGraph, int numberOfPEs, Map<AbstractVertex, Long> brv) {
    this.graph = inputGraph;
    this.nPEs = numberOfPEs;
    this.brv = brv;
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
