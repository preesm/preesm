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
    if (eObject == null) {
      return false;
    }

    // ConfigOutputPort contained in the Actor.
    if (eObject instanceof ConfigOutputPort) {
      return false;
    }

    // OutputPort contained in the SourceInterface and Actor
    final EObject container = eObject.eContainer();
    if (eObject instanceof DataOutputPort) {
      if (container instanceof DataInputInterface) {
        return true;
      }
      if (container instanceof ExecutableActor) {
        return true;
      }
    }

    // InputPort contained in the SinkInterface and Actor
    if (eObject instanceof DataInputPort) {
      if (container instanceof DataOutputInterface) {
        return true;
      }
      if (container instanceof ExecutableActor) {
        return true;
      }
    }

    return false;
  }

}
