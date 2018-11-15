package org.preesm.ui.pisdf.decorators;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.IDecorator;

/**
 *
 */
public class PiMMDecoratorAdapter extends EContentAdapter {

  private final Map<PictogramElement, IDecorator[]> pesAndDecorators;

  public PiMMDecoratorAdapter() {
    this.pesAndDecorators = new LinkedHashMap<>();
  }

  /**
   * Is called each time a change is made in the PiGraph
   */
  @Override
  public void notifyChanged(Notification notification) {

    switch (notification.getFeatureID(Resource.class)) {
      case Notification.SET:
      case Notification.ADD:
      case Notification.REMOVE:
      case Notification.UNSET:
        // Notification.CREATE equivalent, but without deprecation warning
      case 0:
        getPesAndDecorators().clear();
        break;
      default:
        // do nothing
    }
    super.notifyChanged(notification);
  }

  public Map<PictogramElement, IDecorator[]> getPesAndDecorators() {
    return pesAndDecorators;
  }

}
