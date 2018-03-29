/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
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
package org.ietr.preesm.pimm.algorithm.checker;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.pimm.algorithm.checker.structure.FifoChecker;
import org.ietr.preesm.pimm.algorithm.checker.structure.RefinementChecker;

// TODO: Auto-generated Javadoc
/**
 * Main class of the checker. Call all the independent checkers and create error messages from their result.
 *
 * @author cguy
 * @author kdesnos
 *
 */
public class PiMMAlgorithmChecker {

  /** The graph. */
  private PiGraph graph;

  /** The error msgs. */
  private Map<String, EObject> errorMsgs;

  /** The warning msgs. */
  private Map<String, EObject> warningMsgs;

  /** The errors. */
  private boolean errors;

  /** The warnings. */
  private boolean warnings;

  /**
   * Check a PiGraph for different properties.
   *
   * @param graph
   *          the PiGraph to check
   * @return true if no problem have been detected in graph, false otherwise
   */
  public boolean checkGraph(final PiGraph graph) {
    this.graph = graph;
    this.errorMsgs = new LinkedHashMap<>();
    this.warningMsgs = new LinkedHashMap<>();

    this.errors = false;
    this.warnings = false;

    checkGraphRefinements(graph);
    checkGraphFifos(graph);

    return !this.errors && !this.warnings;
  }

  /**
   * Check graph fifos.
   *
   * @param graph
   *          the graph
   */
  private void checkGraphFifos(final PiGraph graph) {
    final FifoChecker fifoChecker = new FifoChecker();
    if (!fifoChecker.checkFifos(graph)) {
      this.errors = !fifoChecker.getFifoWithOneZeroRate().isEmpty();
      this.warnings = !fifoChecker.getFifoWithVoidType().isEmpty() || !fifoChecker.getFifoWithZeroRates().isEmpty();
      for (final Fifo f : fifoChecker.getFifoWithOneZeroRate()) {
        final String srcVertexPath = ((AbstractVertex) f.getSourcePort().eContainer()).getName() + "." + f.getSourcePort().getName();
        final String tgtVertexPath = ((AbstractVertex) f.getTargetPort().eContainer()).getName() + "." + f.getTargetPort().getName();
        this.errorMsgs.put("Fifo between actors " + srcVertexPath + " and " + tgtVertexPath + " has invalid rates (one equals 0 but not the other)", f);
      }
      for (final Fifo f : fifoChecker.getFifoWithVoidType()) {
        final String srcVertexPath = ((AbstractVertex) f.getSourcePort().eContainer()).getName() + "." + f.getSourcePort().getName();
        final String tgtVertexPath = ((AbstractVertex) f.getTargetPort().eContainer()).getName() + "." + f.getTargetPort().getName();
        this.warningMsgs.put("Fifo between actors " + srcVertexPath + " and " + tgtVertexPath + " has type \"void\" (this is not supported by code generation)",
            f);
      }
      for (final Fifo f : fifoChecker.getFifoWithZeroRates()) {
        final String srcVertexPath = ((AbstractVertex) f.getSourcePort().eContainer()).getName() + "." + f.getSourcePort().getName();
        final String tgtVertexPath = ((AbstractVertex) f.getTargetPort().eContainer()).getName() + "." + f.getTargetPort().getName();
        this.warningMsgs.put("Fifo between actors " + srcVertexPath + " and " + tgtVertexPath + " has rates equal to 0 (you may have forgotten to set them)",
            f);
      }
    }
  }

  /**
   * Check graph refinements.
   *
   * @param graph
   *          the graph
   */
  private void checkGraphRefinements(final PiGraph graph) {
    final RefinementChecker refinementChecker = new RefinementChecker();
    if (!refinementChecker.checkRefinements(graph)) {
      this.errors = true;
      for (final Actor a : refinementChecker.getActorsWithoutRefinement()) {
        this.errorMsgs.put("Actor " + a.getVertexPath() + " does not have a refinement", a);
      }
      for (final Actor a : refinementChecker.getActorsWithInvalidExtensionRefinement()) {
        this.errorMsgs
            .put("Refinement " + a.getRefinement().getFilePath() + " of Actor " + a.getVertexPath() + " does not have a valid extension (.h, .idl, or .pi)", a);
      }
      for (final Actor a : refinementChecker.getActorsWithNonExistingRefinement()) {
        this.errorMsgs.put("Refinement  " + a.getRefinement().getFilePath() + " of Actor " + a.getVertexPath() + " does not reference an existing file", a);
      }
    }
  }

  /**
   * Gets the error msg.
   *
   * @return the error msg
   */
  public String getErrorMsg() {
    String result = "Validation of graph " + this.graph.getName() + " raised the following errors:\n";
    for (final String msg : this.errorMsgs.keySet()) {
      result += "- " + msg + "\n";
    }
    return result;
  }

  /**
   * Gets the error msgs.
   *
   * @return the errorMsgs and the associated {@link EObject}
   */
  public Map<String, EObject> getErrorMsgs() {
    return this.errorMsgs;
  }

  /**
   * Gets the ok msg.
   *
   * @return the ok msg
   */
  public String getOkMsg() {
    final String result = "Validation of graph " + this.graph.getName() + " raised no error or warning:";

    return result;
  }

  /**
   * Gets the warning msg.
   *
   * @return the warning msg
   */
  public String getWarningMsg() {
    String result = "Validation of graph " + this.graph.getName() + " raised the following warnings:\n";
    for (final String msg : this.warningMsgs.keySet()) {
      result += "- " + msg + "\n";
    }
    return result;
  }

  /**
   * Gets the warning msgs.
   *
   * @return the warningMsgs and the associated {@link EObject}
   */
  public Map<String, EObject> getWarningMsgs() {
    return this.warningMsgs;
  }

  /**
   * Checks if is errors.
   *
   * @return true, if is errors
   */
  public boolean isErrors() {
    return this.errors;
  }

  /**
   * Checks if is warnings.
   *
   * @return true, if is warnings
   */
  public boolean isWarnings() {
    return this.warnings;
  }

}
