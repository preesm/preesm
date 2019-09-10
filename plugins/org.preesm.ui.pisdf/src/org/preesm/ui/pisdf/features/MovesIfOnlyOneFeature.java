package org.preesm.ui.pisdf.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;

/**
 * This class is dedicated to avoid moving twice delays and fifos when several elements are selected.
 * 
 * @author ahonorat
 */
public class MovesIfOnlyOneFeature extends DefaultMoveShapeFeature {

  public MovesIfOnlyOneFeature(IFeatureProvider fp) {
    super(fp);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void moveShape(IMoveShapeContext context) {
    if (getDiagramBehavior().getDiagramContainer().getSelectedPictogramElements().length > 1) {
      return;
    }
    super.moveShape(context);
  }
}
