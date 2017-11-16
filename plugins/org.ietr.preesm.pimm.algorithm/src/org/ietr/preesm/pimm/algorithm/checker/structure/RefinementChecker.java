/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
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
package org.ietr.preesm.pimm.algorithm.checker.structure;

import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiSDFRefinement;

// TODO: Auto-generated Javadoc
/**
 * Class to check different properties of the Refinements of the Actors of a PiGraph. Entry point is the checkRefinements method. Actors with invalid
 * refinements are kept in several sets
 *
 * @author cguy
 *
 */
public class RefinementChecker {

  /** The actors without refinement. */
  // Actors with no refinement set
  private final Set<Actor> actorsWithoutRefinement;

  /** The actors with non existing refinement. */
  // Actors with refinement path pointing to non-existing file
  private final Set<Actor> actorsWithNonExistingRefinement;

  /** The actors with invalid extension refinement. */
  // Actors with refinement path pointing to non-authorized format file
  private final Set<Actor> actorsWithInvalidExtensionRefinement;

  /**
   * Instantiates a new refinement checker.
   */
  public RefinementChecker() {
    this.actorsWithoutRefinement = new LinkedHashSet<>();
    this.actorsWithNonExistingRefinement = new LinkedHashSet<>();
    this.actorsWithInvalidExtensionRefinement = new LinkedHashSet<>();
  }

  /**
   * Check the presence and validity of Refinements of all Actors of the graph
   *
   * <p>
   * Precondition: graph has been connected through SubgraphConnector.
   * </p>
   *
   * @param graph
   *          PiGraph to check
   * @return true if all refinements are set and valid, false otherwise
   */
  public boolean checkRefinements(final PiGraph graph) {
    boolean ok = true;
    for (final AbstractActor aa : graph.getVertices()) {
      if (aa instanceof Actor) {
        ok &= checkRefinement((Actor) aa);
      } else if (aa instanceof PiGraph) {
        ok &= checkRefinements((PiGraph) aa);
      }
    }
    return ok;
  }

  /**
   * Check the Refinement of an Actor.
   *
   * @param a
   *          the Actor for which we want to check the Refinement
   * @return true if the refinement is valid, false otherwise
   */
  private boolean checkRefinement(final Actor a) {
    final PiSDFRefinement refinement = a.getRefinement();
    if ((refinement != null) && (refinement.getFilePath() != null) && !refinement.getFilePath().isEmpty()) {
      return checkRefinementExtension(a) && checkRefinementValidity(a);
    } else {
      // a does not have a refinement, or has a refinement containing no
      // file path
      this.actorsWithoutRefinement.add(a);
      return false;
    }
  }

  /**
   * Check the file extension of the Refinement of an Actor
   *
   * <p>
   * Precondition: a has a non-null refinement with a non-null and non-empty filePath.
   * </p>
   *
   * @param a
   *          the Actor for which we want to check the Refinement
   * @return true if the file extension of the refinement of a is a valid one, false otherwise
   */
  private boolean checkRefinementExtension(final Actor a) {
    final IPath path = a.getRefinement().getFilePath();
    final String fileExtension = path.getFileExtension();
    if (!fileExtension.equals("h") && !fileExtension.equals("idl") && !fileExtension.equals("pi")) {
      // File pointed by the refinement of a does not have a valid
      // extension
      this.actorsWithInvalidExtensionRefinement.add(a);
      return false;
    }
    return true;
  }

  /**
   * Check the existence of the file of the Refinement of an Actor
   *
   * <p>
   * Precondition: a has a non-null refinement with a non-null and non-empty filePath.
   * </p>
   *
   * @param a
   *          the Actor for which we want to check the Refinement
   * @return true if the file exists, false otherwise
   */
  private boolean checkRefinementValidity(final Actor a) {
    final IPath path = a.getRefinement().getFilePath();
    final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
    if (!file.exists()) {
      // File pointed by the refinement does not exist
      this.actorsWithNonExistingRefinement.add(a);
      return false;
    }
    return true;
  }

  /**
   * Gets the actors without refinement.
   *
   * @return the actors without refinement
   */
  public Set<Actor> getActorsWithoutRefinement() {
    return this.actorsWithoutRefinement;
  }

  /**
   * Gets the actors with non existing refinement.
   *
   * @return the actors with non existing refinement
   */
  public Set<Actor> getActorsWithNonExistingRefinement() {
    return this.actorsWithNonExistingRefinement;
  }

  /**
   * Gets the actors with invalid extension refinement.
   *
   * @return the actors with invalid extension refinement
   */
  public Set<Actor> getActorsWithInvalidExtensionRefinement() {
    return this.actorsWithInvalidExtensionRefinement;
  }
}
