package org.preesm.ui.pisdf.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.preesm.model.pisdf.PassiveActor;

public class AddPassiveActorFeature extends AbstractAddConfigurableFeature {

  public static final IColorConstant PASSIVE_ACTOR_TEXT_FOREGROUND = IColorConstant.BLACK;
  public static final IColorConstant PASSIVE_ACTOR_FOREGROUND      = new ColorConstant(85, 70, 181);
  public static final IColorConstant PASSIVE_ACTOR_BACKGROUND      = new ColorConstant(198, 218, 245);

  private static final int DEFAULT_WIDTH  = 100;
  private static final int DEFAULT_HEIGHT = 50;

  public AddPassiveActorFeature(final IFeatureProvider fp) {
    super(fp);
  }

  @Override
  public boolean canAdd(final IAddContext context) {
    return (context.getNewObject() instanceof PassiveActor) && (context.getTargetContainer() instanceof Diagram);
  }

  @Override
  int getDefaultWidth() {
    return DEFAULT_WIDTH;
  }

  @Override
  int getDefaultHeight() {
    return DEFAULT_HEIGHT;
  }

  @Override
  IColorConstant getForegroundColor() {
    return PASSIVE_ACTOR_FOREGROUND;
  }

  @Override
  IColorConstant getBackgroundColor() {
    return PASSIVE_ACTOR_BACKGROUND;
  }

  @Override
  IColorConstant getTextForegroundColor() {
    return PASSIVE_ACTOR_TEXT_FOREGROUND;
  }
}
