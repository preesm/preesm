/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.ExecutableActor;

/**
 * Filter data ports
 *
 * @author ahonorat
 */
public class PiMMDataPortFilter extends AbstractPropertySectionFilter {

  @Override
  protected boolean accept(PictogramElement pictogramElement) {
    final EObject eObject = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pictogramElement);
    // ConfigOutputPort contained in the Actor.
    if ((eObject == null) || (eObject instanceof ConfigOutputPort)) {
      return false;
    }

    // OutputPort contained in the SourceInterface and Actor
    final EObject container = eObject.eContainer();
    if (eObject instanceof DataOutputPort) {
      if ((container instanceof DataInputInterface) || (container instanceof ExecutableActor)) {
        return true;
      }
    }

    // InputPort contained in the SinkInterface and Actor
    if (eObject instanceof DataInputPort) {
      if ((container instanceof DataOutputInterface) || (container instanceof ExecutableActor)) {
        return true;
      }
    }

    return false;
  }

}
