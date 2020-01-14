package org.preesm.ui.pisdf.properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;
import org.preesm.model.pisdf.Delay;

/**
 * Filter delays.
 * 
 * @author ahonorat
 */
public class PiMMDelayFilter extends AbstractPropertySectionFilter {

  @Override
  protected boolean accept(PictogramElement pictogramElement) {
    final EObject eObject = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pictogramElement);
    if (eObject == null) {
      return false;
    }

    if (eObject instanceof Delay) {
      return true;
    }

    return false;
  }

}
