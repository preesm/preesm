package org.preesm.model.pisdf.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.preesm.commons.graph.Vertex;
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

  /**
   * This function finds all the actors in a single-actor cycle
   *
   * @return list of single-actor cycle
   */
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

  /**
   * Seeks and returns a list of actors involved in plural local delays. A plural local delay involves a delay with no
   * persistence level, distinct source and target FIFOs, and both getter and setter actors present.
   *
   * @return A list of actors involved in plural local delays.
   */
  public List<AbstractActor> pluralLocalseek() {
    // List to store lists of actors involved in plural local delays
    final List<List<AbstractActor>> actorsLOOPlist = new LinkedList<>();

    for (final Delay delay : graph.getDelays()) {

      // Check if the delay meets criteria for plural local delay
      final List<AbstractActor> actorsLOOP = new LinkedList<>();
      if (delay.getLevel().equals(PersistenceLevel.NONE)
          && !delay.getContainingFifo().getTarget().equals(delay.getContainingFifo().getSource())
          && delay.hasGetterActor() && delay.hasSetterActor()) {

        // Get start and end actors for the delay
        final AbstractActor start = (AbstractActor) delay.getContainingFifo().getTarget();
        final AbstractActor end = (AbstractActor) delay.getContainingFifo().getSource();
        actorsLOOP.add(start);

        // Add successors to the list if not already present
        List<Vertex> successors = start.getDirectSuccessors();
        successors.stream().filter(successor -> !actorsLOOP.contains(successor))
            .forEach(successor -> actorsLOOP.add((AbstractActor) successor));

        // Continue adding successors until end actor is reached
        while (!successors.contains(end)) {
          final List<Vertex> newsuccessors = successors.stream()
              .flatMap(successor -> successor.getDirectSuccessors().stream()).distinct()
              .peek(sucsuccessor -> actorsLOOP.add((AbstractActor) sucsuccessor)).collect(Collectors.toList());
          successors = newsuccessors;

        }

        // Add list of actors involved in the delay to the main list
        actorsLOOPlist.add(actorsLOOP);

      }
    }
    // Return the first list of actors involved in plural local delays
    return actorsLOOPlist.isEmpty() ? Collections.emptyList() : actorsLOOPlist.get(0);
  }

}
