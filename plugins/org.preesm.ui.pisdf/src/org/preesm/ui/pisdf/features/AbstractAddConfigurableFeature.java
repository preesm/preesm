package org.preesm.ui.pisdf.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;
import org.preesm.model.pisdf.Configurable;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.PiGraph;

public abstract class AbstractAddConfigurableFeature extends AbstractAddFeature {

  protected AbstractAddConfigurableFeature(final IFeatureProvider fp) {
    super(fp);
  }

  abstract int getDefaultWidth();

  abstract int getDefaultHeight();

  abstract IColorConstant getForegroundColor();

  abstract IColorConstant getBackgroundColor();

  abstract IColorConstant getTextForegroundColor();

  public PictogramElement add(final IAddContext context) {
    final Configurable addedActor = (Configurable) context.getNewObject();
    final Diagram targetDiagram = (Diagram) context.getTargetContainer();

    // CONTAINER SHAPE WITH ROUNDED RECTANGLE
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);

    // Create and link shape UI element
    createConfigurableShape(context, containerShape);

    // Create and link text UI element
    createConfigurableText(context, containerShape);

    // Add a ChopBoxAnchor for the actor
    // this ChopBoxAnchor is used to create connection from an actor to
    // another rather than between ports (output and input ports are then created)
    final ChopboxAnchor cba = peCreateService.createChopboxAnchor(containerShape);
    link(cba, addedActor);

    // Call the layout feature
    layoutPictogramElement(containerShape);
    return containerShape;
  }

  // This method is actually only for executable actors, and need to be overridden for other configurable
  void createConfigurableShape(IAddContext context, ContainerShape containerShape) {

    final ExecutableActor addedActor = (ExecutableActor) context.getNewObject();
    final IGaService gaService = Graphiti.getGaService();

    final int width = getDefaultWidth();
    final int height = getDefaultHeight();

    // create and set graphics algorithm
    final RoundedRectangle roundedRectangle = gaService.createRoundedRectangle(containerShape, 5, 5);
    roundedRectangle.setForeground(manageColor(getForegroundColor()));
    roundedRectangle.setBackground(manageColor(getBackgroundColor()));
    roundedRectangle.setLineWidth(2);
    gaService.setLocationAndSize(roundedRectangle, context.getX(), context.getY(), width, height);

    // if added Class has no resource we add it to the resource of the diagram
    // in a real scenario the business model would have its own resource
    if (addedActor.eResource() == null) {
      final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());
      graph.addActor(addedActor);
    }
    // create link and wire it
    link(containerShape, addedActor);
  }

  void createConfigurableText(IAddContext context, ContainerShape containerShape) {

    final ExecutableActor addedActor = (ExecutableActor) context.getNewObject();

    final IGaService gaService = Graphiti.getGaService();
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();

    final int width = getDefaultWidth();

    // create shape for text
    final Shape shape = peCreateService.createShape(containerShape, false);

    // create and set text graphics algorithm
    final Text text = gaService.createText(shape, addedActor.getName());
    text.setForeground(manageColor(getTextForegroundColor()));
    text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);

    // vertical alignment has as default value "center"
    text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));
    gaService.setLocationAndSize(text, 0, 0, width, 20);

    // create link and wire it
    link(shape, addedActor);
  }

}
