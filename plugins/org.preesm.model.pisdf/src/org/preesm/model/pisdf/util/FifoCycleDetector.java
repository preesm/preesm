/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2015)
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
package org.preesm.model.pisdf.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.PiMMSwitch;

// TODO: Auto-generated Javadoc
/**
 * This class provide an Ecore switch to detect cycle dependencies on FIfos.
 */
public class FifoCycleDetector extends PiMMSwitch<Void> {

  /**
   * If this boolean is true, the cycle detection will stop at the first cycle detected.
   */
  protected boolean fastDetection = false;

  /**
   * List of the {@link AbstractActor}s that were already visited and are not involved in cycles.
   */
  protected Set<AbstractActor> visited;

  /**
   * List the {@link AbstractActor} that are currently being visited. If one of them is met again, this means that there
   * is a cycle.
   */
  protected ArrayList<AbstractActor> branch;

  /**
   * Stores all the {@link AbstractActor} cycles that were detected. Each element of the {@link ArrayList} is an
   * {@link ArrayList} containing {@link AbstractActor} forming a cycle. <br>
   * <br>
   * <b> Not all cycles are detected by this algorithm ! </b><br>
   * For example, if two cycles have some links in common, only one of them will be detected.
   */
  protected List<List<AbstractActor>> cycles;

  /**
   * List of {@link Fifo} that will not be considered as part of the {@link PiGraph} when looking for cycles. This list
   * is usefull when trying to identify all cycles in a graph.
   */
  protected Set<Fifo> ignoredFifos;

  /**
   * Default constructor. Assume fast detection is true. (i.e. the detection will stop at the first cycle detected)
   */
  public FifoCycleDetector() {
    this(true);
  }

  /**
   * Instantiates a new fifo cycle detector.
   *
   * @param fastDetection
   *          whether the detection will stop at the first detected cycle (true) or list all cycles (false)
   */
  public FifoCycleDetector(final boolean fastDetection) {
    this.fastDetection = fastDetection;
    this.visited = new LinkedHashSet<>();
    this.branch = new ArrayList<>();
    this.cycles = new ArrayList<>();
    this.ignoredFifos = new LinkedHashSet<>();
  }

  /**
   * Add the current cycle to the cycle list.
   *
   * @param actor
   *          the {@link AbstractActor} forming a cycle in the {@link Dependency} tree.
   */
  protected void addCycle(final AbstractActor actor) {

    final ArrayList<AbstractActor> cycle = new ArrayList<>();

    // Backward scan of the branch list until the actor is found again
    int i = this.branch.size();
    do {
      i--;
      cycle.add(0, this.branch.get(i));
    } while ((this.branch.get(i) != actor) && (i > 0));

    // If i is less than 0, the whole branch was scanned but the actor
    // was not found.
    // This means this branch is not a cycle. (But this should not happen,
    // so throw an error)
    if (i < 0) {
      throw new RuntimeException("No FIFO cycle was found in this branch.");
    }

    // If this code is reached, the cycle was correctly detected.
    // We add it to the cycles list.
    this.cycles.add(cycle);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch#casePiGraph(org.ietr.preesm.experiment.model.pimm.PiGraph)
   */
  @Override
  public Void casePiGraph(final PiGraph graph) {

    // Visit AbstractActor until they are all visited
    final ArrayList<AbstractActor> actors = new ArrayList<>(graph.getActors());
    while (actors.size() != 0) {
      doSwitch(actors.get(0));

      // If fast detection is activated and a cycle was detected, get
      // out of here!
      if (this.fastDetection && cyclesDetected()) {
        break;
      }

      // Else remove visited AbstractActor and continue
      actors.removeAll(this.visited);
    }

    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch#caseAbstractActor(org.ietr.preesm.experiment.model.pimm.
   * AbstractActor)
   */
  @Override
  public Void caseAbstractActor(final AbstractActor actor) {
    // Visit the AbstractActor and its successors if it was not already done
    if (!this.visited.contains(actor)) {
      // Check if the AbstractActor is already in the branch (i.e. check
      // if
      // there is a cycle)
      if (this.branch.contains(actor)) {
        // There is a cycle
        addCycle(actor);
        return null;
      }

      // Add the AbstractActor to the visited branch
      this.branch.add(actor);

      // Visit all AbstractActor depending on the current one.
      for (final DataOutputPort port : actor.getDataOutputPorts()) {
        final Fifo outgoingFifo = port.getOutgoingFifo();
        if ((outgoingFifo != null) && !this.ignoredFifos.contains(outgoingFifo)) {
          doSwitch(outgoingFifo.getTargetPort());
        }

        // If fast detection is activated and a cycle was detected, get
        // out of here!
        if (this.fastDetection && cyclesDetected()) {
          break;
        }
      }

      // Remove the AbstractActor from the branch.
      this.branch.remove(this.branch.size() - 1);
      // Add the AbstractActor to the visited list
      this.visited.add(actor);
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch#caseDataInputPort(org.ietr.preesm.experiment.model.pimm.
   * DataInputPort)
   */
  @Override
  public Void caseDataInputPort(final DataInputPort port) {
    // Visit the owner of the data input port only if it is a AbstractActor
    if (port.eContainer() instanceof AbstractActor) {
      doSwitch(port.eContainer());
    }

    return null;
  }

  /**
   * Reset the visitor to use it again. This method will clean the lists of already visited {@link AbstractActor}
   * contained in the {@link FifoCycleDetector}, and the list of detected cycles.
   */
  public void clear() {
    this.visited.clear();
    this.branch.clear();
    this.cycles.clear();
  }

  /**
   * Add a {@link Fifo} to the {@link #ignoredFifos} {@link Set}.
   *
   * @param fifo
   *          the {@link Fifo} to add.
   */
  public void addIgnoredFifo(final Fifo fifo) {
    this.ignoredFifos.add(fifo);
  }

  /**
   * Remove a {@link Fifo} from the {@link #ignoredFifos} {@link Set}.
   *
   * @param fifo
   *          the {@link Fifo} to remove.
   * @return result from the {@link Set#remove(Object)} operation.
   */
  public boolean removeIgnoredFifo(final Fifo fifo) {
    return this.ignoredFifos.remove(fifo);
  }

  /**
   * Clear the {@link Set} of ignored {@link Fifo}.
   */
  public void clearIgnoredFifos() {
    this.ignoredFifos.clear();
  }

  /**
   * Gets the cycles.
   *
   * @return the cycles
   */
  public List<List<AbstractActor>> getCycles() {
    return this.cycles;
  }

  /**
   * Retrieve the result of the visitor. This method should be called only after the visitor was executed using
   * {@link FifoCycleDetector#doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(object)} method on a
   * {@link AbstractActor} or on a {@link PiGraph}.
   *
   * @return true if cycles were detected, false else.
   */
  public boolean cyclesDetected() {
    return this.cycles.size() > 0;
  }

  /**
   * Adds the ignored fifos.
   *
   * @param fifos
   *          the fifos
   */
  public void addIgnoredFifos(final Collection<Fifo> fifos) {
    this.ignoredFifos.addAll(fifos);
  }

  /**
   * Considering a {@link List} of {@link AbstractActor} forming a cyclic data-path (cf. {@link FifoCycleDetector}),
   * this method returns a {@link List} of all {@link Fifo} involved in this cyclic data-path.
   *
   * @param cycle
   *          A list of {@link AbstractActor} forming a Cycle.
   * @return the list
   */
  public static List<Fifo> findCycleFeedbackFifos(final List<AbstractActor> cycle) {
    // Find the Fifos between each pair of actor of the cycle
    final List<List<Fifo>> cyclesFifos = new ArrayList<>();
    for (int i = 0; i < cycle.size(); i++) {
      final AbstractActor srcActor = cycle.get(i);
      final AbstractActor dstActor = cycle.get((i + 1) % cycle.size());

      final List<Fifo> outFifos = new ArrayList<>();
      srcActor.getDataOutputPorts().forEach(port -> {
        final Fifo outgoingFifo = port.getOutgoingFifo();
        // a port that is not connected does not, obviously, contribute to any cycle
        if (outgoingFifo != null) {
          final DataInputPort fifoTargetPort = outgoingFifo.getTargetPort();
          final boolean equals = dstActor.equals(fifoTargetPort.eContainer());
          if (equals) {
            outFifos.add(outgoingFifo);
          }
        }
      });
      cyclesFifos.add(outFifos);
    }

    // Find a list of FIFO between a pair of actor with delays on all FIFOs
    List<Fifo> feedbackFifos = null;
    for (final List<Fifo> cycleFifos : cyclesFifos) {
      boolean hasDelays = true;
      for (final Fifo fifo : cycleFifos) {
        hasDelays &= (fifo.getDelay() != null);
      }

      if (hasDelays) {
        // Keep the shortest list of feedback delay
        feedbackFifos = ((feedbackFifos == null) || (feedbackFifos.size() > cycleFifos.size())) ? cycleFifos
            : feedbackFifos;
      }
    }
    if (feedbackFifos != null) {
      return feedbackFifos;
    } else {
      // If no feedback fifo with delays were found. Select a list with a
      // small number of fifos
      cyclesFifos.sort((l1, l2) -> l1.size() - l2.size());
      return cyclesFifos.get(0);
    }
  }
}
