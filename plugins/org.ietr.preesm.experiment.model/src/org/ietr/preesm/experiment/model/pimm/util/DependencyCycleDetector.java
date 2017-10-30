/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
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
package org.ietr.preesm.experiment.model.pimm.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

// TODO: Auto-generated Javadoc
/**
 * This class provide an Ecore switch to detect cycle dependencies.
 */
public class DependencyCycleDetector extends PiMMSwitch<Void> {

  /**
   * If this boolean is true, the cycle detection will stop at the first cycle detected.
   */
  protected boolean fastDetection = false;

  /**
   * List of the {@link Parameter}s that were already visited and are not involved in cycles.
   */
  protected Set<Parameter> visited;

  /**
   * List the parameter that are currently being visited. If one of them is met again, this means that there is a cycle.
   */
  protected ArrayList<Parameter> branch;

  /**
   * Stores all the {@link Parameter} cycles that were detected. Each element of the {@link ArrayList} is an {@link ArrayList} containing {@link Parameter}
   * forming a cycle. <br>
   * <br>
   * <b> Not all cycles are detected by this algorithm ! </b><br>
   * For example, if two cycles have some links in common, only one of them will be detected.
   */
  protected List<List<Parameter>> cycles;

  /**
   * Default constructor. Assume fast detection is true. (i.e. the detection will stop at the first cycle detected)
   */
  public DependencyCycleDetector() {
    this(true);
  }

  /**
   * Instantiates a new dependency cycle detector.
   *
   * @param fastDetection
   *          whether the detection will stop at the first detected cycle (true) or list all cycles (false)
   */
  public DependencyCycleDetector(final boolean fastDetection) {
    this.fastDetection = fastDetection;
    this.visited = new LinkedHashSet<>();
    this.branch = new ArrayList<>();
    this.cycles = new ArrayList<>();
  }

  /**
   * Add the current cycle to the cycle list.
   *
   * @param parameter
   *          the {@link Parameter} forming a cycle in the {@link Dependency} tree.
   */
  protected void addCycle(final Parameter parameter) {

    final ArrayList<Parameter> cycle = new ArrayList<>();

    // Backward scan of the branch list until the parameter is found again
    int i = this.branch.size();
    do {
      i--;
      cycle.add(0, this.branch.get(i));
    } while ((this.branch.get(i) != parameter) && (i > 0));

    // If i is less than 0, the whole branch was scanned but the parameter
    // was not found.
    // This means this branch is not a cycle. (But this should not happen,
    // so throw an error)
    if (i < 0) {
      throw new RuntimeException("No dependency cycle was found in this branch.");
    }

    // If this code is reached, the cycle was correctly detected.
    // We add it to the cycles list.
    this.cycles.add(cycle);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch#casePiGraph(org.ietr.preesm.experiment.model.pimm.PiGraph)
   */
  @Override
  public Void casePiGraph(final PiGraph graph) {

    // Visit parameters until they are all visited
    final ArrayList<Parameter> parameters = new ArrayList<>(graph.getParameters());
    while (parameters.size() != 0) {
      doSwitch(parameters.get(0));

      // If fast detection is activated and a cycle was detected, get
      // out of here!
      if (this.fastDetection && cyclesDetected()) {
        break;
      }

      // Else remove visited parameters and continue
      parameters.removeAll(this.visited);
    }

    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch#caseParameter(org.ietr.preesm.experiment.model.pimm.Parameter)
   */
  @Override
  public Void caseParameter(final Parameter parameter) {
    // Visit the parameter and its successors if it was not already done
    if (!this.visited.contains(parameter)) {
      // Check if the parameter is already in the branch (i.e. check if
      // there is a cycle)
      if (this.branch.contains(parameter)) {
        // There is a cycle
        addCycle(parameter);
        return null;
      }

      // Add the parameter to the visited branch
      this.branch.add(parameter);

      // Visit all parameters influencing the current one.
      for (final ConfigInputPort port : parameter.getConfigInputPorts()) {
        final Dependency incomingDependency = port.getIncomingDependency();
        if (incomingDependency != null) {
          final ISetter setter = incomingDependency.getSetter();
          if (setter != null) {
            doSwitch(setter);
          }
        }

        // If fast detection is activated and a cycle was detected, get
        // out of here!
        if (this.fastDetection && cyclesDetected()) {
          break;
        }
      }

      // Remove the parameter from the branch.
      this.branch.remove(this.branch.size() - 1);
      // Add the parameter to the visited list
      this.visited.add(parameter);
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch#caseConfigInputPort(org.ietr.preesm.experiment.model.pimm.ConfigInputPort)
   */
  @Override
  public Void caseConfigInputPort(final ConfigInputPort port) {
    // Visit the owner of the config input port only if it is a parameter
    if (port.eContainer() instanceof Parameter) {
      doSwitch(port.eContainer());
    }

    return null;
  }

  /**
   * Reset the visitor to use it again. This method will clean the lists of already visited {@link Parameter} contained in the {@link DependencyCycleDetector},
   * and the list of detected cycles.
   */
  public void clear() {
    this.visited.clear();
    this.branch.clear();
    this.cycles.clear();
  }

  /**
   * Gets the cycles.
   *
   * @return the cycles
   */
  public List<List<Parameter>> getCycles() {
    return this.cycles;
  }

  /**
   * Retrieve the result of the visitor. This method should be called only after the visitor was executed using
   * {@link DependencyCycleDetector#doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(object)} method on a {@link Parameter} or on a {@link PiGraph}.
   *
   * @return true if cycles were detected, false else.
   */
  public boolean cyclesDetected() {
    return this.cycles.size() > 0;
  }
}
