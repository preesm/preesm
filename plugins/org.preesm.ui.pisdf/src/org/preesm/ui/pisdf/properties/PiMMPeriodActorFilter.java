/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013 - 2020)
 * Romina Racca [romina.racca@gmail.com] (2013)
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
package org.preesm.ui.pisdf.properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.PeriodicElement;

/**
 * The Class PiMMActorFilter.
 */
public class PiMMPeriodActorFilter extends AbstractPropertySectionFilter {

  /**
   * Check the given {@link PictogramElement} for acceptance. Returns true, if {@link PictogramElement} is accepted,
   * otherwise false.
   *
   * @param pictogramElement
   *          the pictogram element
   * @return true, if successful
   */
  @Override
  protected boolean accept(final PictogramElement pictogramElement) {
    final EObject eObject = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pictogramElement);

    // ExecutableActor.
    if (eObject instanceof final Actor actor) {
      return actor instanceof PeriodicElement && !actor.isHierarchical() && !actor.isConfigurationActor();
    }

    return false;
  }
}
