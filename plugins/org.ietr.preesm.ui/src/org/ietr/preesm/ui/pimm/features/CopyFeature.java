package org.ietr.preesm.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICopyContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractCopyFeature;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Delay;

/**
 * Graphiti feature that implements the Copy action for PiMM vertices.
 *
 * @author anmorvan
 *
 */
public class CopyFeature extends AbstractCopyFeature {

  public CopyFeature(final IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public void copy(final ICopyContext context) {
    final PictogramElement[] pes = context.getPictogramElements();
    final Object[] bos = new Object[pes.length];
    for (int i = 0; i < pes.length; i++) {
      final PictogramElement pe = pes[i];
      bos[i] = getBusinessObjectForPictogramElement(pe);
    }
    // put all business objects to the clipboard
    putToClipboard(bos);
  }

  @Override
  public boolean canCopy(final ICopyContext context) {
    final PictogramElement[] pes = context.getPictogramElements();
    if ((pes == null) || (pes.length == 0)) { // nothing selected
      return false;
    }
    // return true, if all selected elements are a Vertices (cannot copy edges)
    for (final PictogramElement pe : pes) {
      final Object bo = getBusinessObjectForPictogramElement(pe);
      if (!(bo instanceof AbstractVertex) && !(bo instanceof Delay)) {
        return false;
      }
    }
    return true;
  }

}
