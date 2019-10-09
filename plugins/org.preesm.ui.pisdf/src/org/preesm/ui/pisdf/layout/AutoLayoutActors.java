/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
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
package org.preesm.ui.pisdf.layout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;

/**
 * Methods for {@link AutoLayoutFeature}, dedicated to actors
 * 
 * @author ahonorat, kdesnos
 */
public class AutoLayoutActors {

  /**
   * Create the stages of {@link AbstractActor}. An actor can be put in a stage if all its predecessors have been put in
   * previous stages.
   *
   * @param graph
   *          the {@link PiGraph} whose {@link AbstractActor} are sorted into stages.
   * @param feedbackFifos
   *          the {@link Fifo} that must be ignored when considering predecessors of an {@link AbstractActor}
   * @return the {@link List} of stages, where eac stage is a {@link List} of {@link AbstractActor}.
   */
  protected static List<List<AbstractActor>> stageByStageActorSort(final PiGraph graph,
      final List<Fifo> feedbackFifos) {
    // 1. Sort actor in alphabetical order
    final List<AbstractActor> actors = new ArrayList<>(graph.getActors());

    // 2. Sort by names to ensure reproductibility
    actors.sort((a1, a2) -> a1.getName().compareTo(a2.getName()));

    // 3. Find source actors (actor without input non feedback FIFOs)
    final List<AbstractActor> srcActors = findSrcActors(feedbackFifos, actors);

    // 4. BFS-style stage by stage construction
    List<List<AbstractActor>> res = createActorStages(feedbackFifos, actors, srcActors);
    for (List<AbstractActor> l : res) {
      l.sort(new ComparatorInOutsAndName());
    }

    return res;
  }

  /**
   * Compare actors per number of input/outputs and per name
   * 
   * @author ahonorat
   *
   */
  public static class ComparatorInOutsAndName implements Comparator<AbstractActor> {

    @Override
    public int compare(AbstractActor arg0, AbstractActor arg1) {
      int nb0 = arg0.getAllDataPorts().size();
      int nb1 = arg1.getAllDataPorts().size();
      if (nb0 != nb1) {
        // actors with more connections first.
        return Integer.compare(nb1, nb0);
      }
      return arg0.getName().compareTo(arg1.getName());
    }

  }

  /**
   * Find {@link AbstractActor} without any predecessors. {@link Fifo} passed as parameters are ignored.
   *
   * @param feedbackFifos
   *          {@link List} of ignored {@link Fifo}.
   * @param actors
   *          {@link AbstractActor} containing source actors.
   * @return the list of {@link AbstractActor} that do not have any predecessors
   */
  private static List<AbstractActor> findSrcActors(final List<Fifo> feedbackFifos, final List<AbstractActor> actors) {
    final List<AbstractActor> srcActors = new ArrayList<>();
    for (final AbstractActor actor : actors) {
      boolean hasInputFifos = false;

      for (final DataInputPort port : actor.getDataInputPorts()) {
        final Fifo incomingFifo = port.getIncomingFifo();
        final boolean contains = feedbackFifos.contains(incomingFifo);
        final boolean fifoNotNull = incomingFifo != null;
        hasInputFifos |= !contains && fifoNotNull;
      }

      if (!hasInputFifos) {
        srcActors.add(actor);
      }
    }
    return srcActors;
  }

  /**
   * Create {@link List} of {@link List} of {@link AbstractActor} where each innermost {@link List} is called a stage.
   * An {@link AbstractActor} is put in a stage only if all its predecessors (not considering feedbackFifos) are already
   * added to previous stages.
   *
   * @param feedbackFifos
   *          {@link List} of {@link Fifo} that are ignored when scanning the predecessors of an actor.
   * @param actors
   *          {@link AbstractActor} to sort.
   * @param srcActors
   *          First stage of {@link Fifo}, given by the {@link #findSrcActors(List, List)}.
   * @return the stage by stage list of actors.
   */
  private static List<List<AbstractActor>> createActorStages(final List<Fifo> feedbackFifos,
      final List<AbstractActor> actors, final List<AbstractActor> srcActors) {
    final List<List<AbstractActor>> stages = new ArrayList<>();

    final List<AbstractActor> processedActors = new ArrayList<>();
    List<AbstractActor> dataInputInterfaces = new ArrayList<>();
    List<AbstractActor> previousStage = dataInputInterfaces;
    Set<AbstractActor> currentStage = new LinkedHashSet<>();
    final List<AbstractActor> dataOutputInterfaces = new ArrayList<>();

    // Keep DataInputInterfaces for the first stage
    for (AbstractActor aa : srcActors) {
      if (aa instanceof DataInputInterface) {
        dataInputInterfaces.add(aa);
        processedActors.add(aa);
      } else {
        currentStage.add(aa);
      }
    }

    // Check if there is any Interface in the first stage
    if (!dataInputInterfaces.isEmpty()) {
      stages.add(dataInputInterfaces);
    }

    boolean test;
    do {
      // iterate returns actors that could not be scheduled in current stage but may be in next stage
      Set<AbstractActor> nextStage = iterate(feedbackFifos, processedActors, currentStage, previousStage,
          dataOutputInterfaces);
      previousStage = new ArrayList<>(currentStage);
      processedActors.addAll(currentStage);
      currentStage = nextStage;
      stages.add(previousStage);

      test = processedActors.size() < actors.size();
    } while (test);

    // If the last stage is empty (if there were only dataOutputInterface)
    // remove it
    if (stages.get(stages.size() - 1).isEmpty()) {
      stages.remove(stages.size() - 1);
    }

    if (!dataOutputInterfaces.isEmpty()) {
      stages.add(dataOutputInterfaces);
    }

    return stages;
  }

  private static Set<AbstractActor> iterate(final List<Fifo> feedbackFifos, final List<AbstractActor> processedActors,
      final Set<AbstractActor> currentStage, final List<AbstractActor> previousStage,
      final List<AbstractActor> dataOutputInterfaces) {

    // Find candidates for the next stage in successors of current one
    findCandidates(feedbackFifos, currentStage, previousStage);

    // Check if all predecessors of the candidates have already been
    // added in a previous stages
    return check(feedbackFifos, processedActors, currentStage, dataOutputInterfaces);
  }

  private static void findCandidates(final List<Fifo> feedbackFifos, final Set<AbstractActor> currentStage,
      final List<AbstractActor> previousStage) {
    for (final AbstractActor actor : previousStage) {
      for (final DataOutputPort port : actor.getDataOutputPorts()) {
        final Fifo outgoingFifo = port.getOutgoingFifo();
        if ((outgoingFifo != null) && !feedbackFifos.contains(outgoingFifo)) {
          final DataInputPort targetPort = outgoingFifo.getTargetPort();
          final AbstractActor targetActor = targetPort.getContainingActor();
          currentStage.add(targetActor);
        }
      }

    }
  }

  private static Set<AbstractActor> check(final List<Fifo> feedbackFifos, final List<AbstractActor> processedActors,
      final Set<AbstractActor> currentStage, final List<AbstractActor> dataOutputInterfaces) {

    Set<AbstractActor> nextStage = new LinkedHashSet<>();

    Iterator<AbstractActor> iter = currentStage.iterator();
    while (iter.hasNext()) {
      final AbstractActor actor = iter.next();
      boolean hasUnstagedPredecessor = false;
      for (final DataInputPort port : actor.getDataInputPorts()) {
        final Fifo incomingFifo = port.getIncomingFifo();
        final boolean isFeedbackFifo = feedbackFifos.contains(incomingFifo);
        boolean containedInProcessedActor = true;
        if (incomingFifo != null) {
          final AbstractActor predecessorActor = incomingFifo.getSourcePort().getContainingActor();
          containedInProcessedActor = processedActors.contains(predecessorActor);
        }
        hasUnstagedPredecessor |= !isFeedbackFifo && !containedInProcessedActor;

        // For delay with setter, the delay Actor must always be in the previous stage
        if ((incomingFifo != null) && (incomingFifo.getDelay() != null)) {
          if (incomingFifo.getDelay().hasSetterActor()) {
            hasUnstagedPredecessor |= !feedbackFifos.contains(incomingFifo)
                && !processedActors.contains(incomingFifo.getDelay().getActor());
            hasUnstagedPredecessor |= !processedActors.contains(incomingFifo.getDelay().getSetterActor());
          }
        }
      }
      if (hasUnstagedPredecessor) {
        iter.remove();
        nextStage.add(actor);
      } else if ((actor instanceof DataOutputInterface)) {
        dataOutputInterfaces.add(actor);
        processedActors.add(actor);
        iter.remove();
      }
    }

    return nextStage;
  }

}
