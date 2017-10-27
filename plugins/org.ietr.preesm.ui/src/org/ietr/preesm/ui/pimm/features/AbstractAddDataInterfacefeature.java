package org.ietr.preesm.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.IColorConstant;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

/**
 *
 *
 *
 * @author anmorvan
 *
 */
public abstract class AbstractAddDataInterfacefeature extends AbstractAddFeature {

  // define a default size for the shape
  public static final int WIDTH                      = 16;
  public static final int HEIGHT                     = 16;
  public static final int INVISIBLE_RECTANGLE_HEIGHT = 20;
  public static final int Y                          = 0;
  public static final int LINE_WIDTH                 = 2;

  // sub type related variables
  protected abstract IColorConstant getTextForegroundColor();

  protected abstract IColorConstant getForegroundColor();

  protected abstract IColorConstant getBackgroundColor();

  protected abstract double getRelativeWidth();

  protected abstract int getX();

  public AbstractAddDataInterfacefeature(IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.IAdd#add(org.eclipse.graphiti.features.context.IAddContext)
   */
  @Override
  public PictogramElement add(final IAddContext context) {
    final InterfaceActor dataInterface = (InterfaceActor) context.getNewObject();
    final DataPort port = dataInterface.getDataPort();

    final Diagram targetDiagram = (Diagram) context.getTargetContainer();

    // CONTAINER SHAPE WITH ROUNDED RECTANGLE
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);

    final IGaService gaService = Graphiti.getGaService();

    final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(invisibleRectangle, context.getX(), context.getY(), 200, INVISIBLE_RECTANGLE_HEIGHT);

    final BoxRelativeAnchor boxAnchor = peCreateService.createBoxRelativeAnchor(containerShape);
    boxAnchor.setRelativeWidth(getRelativeWidth());
    boxAnchor.setRelativeHeight(((double) INVISIBLE_RECTANGLE_HEIGHT - (double) HEIGHT) / 2.0 / INVISIBLE_RECTANGLE_HEIGHT);
    boxAnchor.setReferencedGraphicsAlgorithm(invisibleRectangle);

    // create and set graphics algorithm for the anchor
    RoundedRectangle roundedRectangle = gaService.createRoundedRectangle(boxAnchor, 5, 5);
    roundedRectangle.setForeground(manageColor(getForegroundColor()));
    roundedRectangle.setBackground(manageColor(getBackgroundColor()));
    roundedRectangle.setLineWidth(LINE_WIDTH);
    gaService.setLocationAndSize(roundedRectangle, getX(), Y, WIDTH, HEIGHT);

    // if added interface has no resource we add it to the
    // resource of the graph
    if (dataInterface.eResource() == null) {
      final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());
      graph.getVertices().add(dataInterface);
    }
    link(boxAnchor, port);

    // Name - SHAPE WITH TEXT
    // create and set text graphics algorithm
    // create shape for text
    final Shape shape = peCreateService.createShape(containerShape, false);
    final Text text = gaService.createText(shape, dataInterface.getName());
    text.setForeground(manageColor(getTextForegroundColor()));
    text.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);
    // vertical alignment has as default value "center"
    text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));
    text.setHeight(20);
    text.setWidth(200);
    link(shape, dataInterface);
    // create link and wire it
    link(containerShape, dataInterface);

    // Add a ChopBoxAnchor for dependencies
    final ChopboxAnchor cba = peCreateService.createChopboxAnchor(containerShape);
    link(cba, dataInterface);

    // Call the layout feature
    layoutPictogramElement(containerShape);

    return containerShape;
  }

}
