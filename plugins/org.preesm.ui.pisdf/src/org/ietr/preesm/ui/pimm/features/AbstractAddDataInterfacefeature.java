/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
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
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;

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

  public AbstractAddDataInterfacefeature(final IFeatureProvider fp) {
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
    gaService.setLocationAndSize(invisibleRectangle, context.getX(), context.getY(), 200,
        AbstractAddDataInterfacefeature.INVISIBLE_RECTANGLE_HEIGHT);

    final BoxRelativeAnchor boxAnchor = peCreateService.createBoxRelativeAnchor(containerShape);
    boxAnchor.setRelativeWidth(getRelativeWidth());
    boxAnchor.setRelativeHeight(((double) AbstractAddDataInterfacefeature.INVISIBLE_RECTANGLE_HEIGHT
        - (double) AbstractAddDataInterfacefeature.HEIGHT) / 2.0
        / AbstractAddDataInterfacefeature.INVISIBLE_RECTANGLE_HEIGHT);
    boxAnchor.setReferencedGraphicsAlgorithm(invisibleRectangle);

    // create and set graphics algorithm for the anchor
    final RoundedRectangle roundedRectangle = gaService.createRoundedRectangle(boxAnchor, 5, 5);
    roundedRectangle.setForeground(manageColor(getForegroundColor()));
    roundedRectangle.setBackground(manageColor(getBackgroundColor()));
    roundedRectangle.setLineWidth(AbstractAddDataInterfacefeature.LINE_WIDTH);
    gaService.setLocationAndSize(roundedRectangle, getX(), AbstractAddDataInterfacefeature.Y,
        AbstractAddDataInterfacefeature.WIDTH, AbstractAddDataInterfacefeature.HEIGHT);

    // if added interface has no resource we add it to the
    // resource of the graph
    if (dataInterface.eResource() == null) {
      final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());
      graph.addActor(dataInterface);
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
