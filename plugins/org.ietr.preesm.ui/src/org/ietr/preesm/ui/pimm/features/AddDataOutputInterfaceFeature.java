/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
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
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

// TODO: Auto-generated Javadoc
/**
 * Add feature to add a new {@link DataOutputInterface} to the {@link PiGraph}.
 *
 * @author kdesnos
 */
public class AddDataOutputInterfaceFeature extends AbstractAddFeature {

  /** The Constant DATA_OUTPUT_TEXT_FOREGROUND. */
  public static final IColorConstant DATA_OUTPUT_TEXT_FOREGROUND = IColorConstant.BLACK;

  /** The Constant DATA_OUTPUT_FOREGROUND. */
  public static final IColorConstant DATA_OUTPUT_FOREGROUND = AddDataOutputPortFeature.DATA_OUTPUT_PORT_FOREGROUND;

  /** The Constant DATA_OUTPUT_BACKGROUND. */
  public static final IColorConstant DATA_OUTPUT_BACKGROUND = AddDataOutputPortFeature.DATA_OUTPUT_PORT_BACKGROUND;

  /**
   * The default constructor of {@link AddDataOutputInterfaceFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public AddDataOutputInterfaceFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.graphiti.func.IAdd#add(org.eclipse.graphiti.features.context.IAddContext)
   */
  @Override
  public PictogramElement add(final IAddContext context) {
    final DataOutputInterface dataOutputInterface = (DataOutputInterface) context.getNewObject();
    final DataInputPort port = dataOutputInterface.getDataInputPorts().get(0);
    final Diagram targetDiagram = (Diagram) context.getTargetContainer();

    // CONTAINER SHAPE WITH ROUNDED RECTANGLE
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);

    // define a default size for the shape
    final int width = 16;
    final int height = 16;
    final int invisibRectHeight = 20;
    final IGaService gaService = Graphiti.getGaService();

    final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(invisibleRectangle, context.getX(), context.getY(), 200, invisibRectHeight);

    RoundedRectangle roundedRectangle; // need to access it later
    {
      final BoxRelativeAnchor boxAnchor = peCreateService.createBoxRelativeAnchor(containerShape);
      boxAnchor.setRelativeWidth(0.0);
      boxAnchor.setRelativeHeight((((double) invisibRectHeight - (double) height)) / 2.0 / invisibRectHeight);
      boxAnchor.setReferencedGraphicsAlgorithm(invisibleRectangle);

      // create and set graphics algorithm for the anchor
      roundedRectangle = gaService.createRoundedRectangle(boxAnchor, 5, 5);
      roundedRectangle.setForeground(manageColor(AddDataOutputInterfaceFeature.DATA_OUTPUT_FOREGROUND));
      roundedRectangle.setBackground(manageColor(AddDataOutputInterfaceFeature.DATA_OUTPUT_BACKGROUND));
      roundedRectangle.setLineWidth(2);
      gaService.setLocationAndSize(roundedRectangle, 0, 0, width, height);

      // if added SinkInterface has no resource we add it to the
      // resource of the graph
      if (dataOutputInterface.eResource() == null) {
        final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());
        graph.getVertices().add(dataOutputInterface);
      }
      link(boxAnchor, port);
    }

    // Name of the SinkInterface - SHAPE WITH TEXT
    {
      // create and set text graphics algorithm
      // create shape for text
      final Shape shape = peCreateService.createShape(containerShape, false);
      final Text text = gaService.createText(shape, dataOutputInterface.getName());
      text.setForeground(manageColor(AddDataOutputInterfaceFeature.DATA_OUTPUT_TEXT_FOREGROUND));
      text.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);
      // vertical alignment has as default value "center"
      text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));
      text.setHeight(20);
      text.setWidth(200);
      link(shape, dataOutputInterface);
    }
    // create link and wire it
    link(containerShape, dataOutputInterface);

    // Add a ChopBoxAnchor for dependencies
    final ChopboxAnchor cba = peCreateService.createChopboxAnchor(containerShape);
    link(cba, dataOutputInterface);

    // Call the layout feature
    layoutPictogramElement(containerShape);

    return containerShape;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.graphiti.func.IAdd#canAdd(org.eclipse.graphiti.features.context.IAddContext)
   */
  @Override
  public boolean canAdd(final IAddContext context) {
    // Check that the user wants to add an SinkInterface to the Diagram
    return (context.getNewObject() instanceof DataOutputInterface) && (context.getTargetContainer() instanceof Diagram);
  }

}
