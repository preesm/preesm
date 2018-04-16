/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2013)
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
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.graphiti.util.IColorConstant;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

// TODO: Auto-generated Javadoc
/**
 * Add feature to add a new {@link Parameter} to the {@link PiGraph}.
 *
 * @author kdesnos
 */
public class AddConfigInputInterfaceFeature extends AbstractAddFeature {

  /** The Constant CFG_IN_TEXT_FOREGROUND. */
  public static final IColorConstant CFG_IN_TEXT_FOREGROUND = IColorConstant.BLACK;

  /** The Constant CFG_IN_FOREGROUND. */
  public static final IColorConstant CFG_IN_FOREGROUND = AddParameterFeature.PARAMETER_FOREGROUND;

  /** The Constant CFG_IN_BACKGROUND. */
  public static final IColorConstant CFG_IN_BACKGROUND = AddParameterFeature.PARAMETER_BACKGROUND;

  /**
   * The default constructor of {@link AddConfigInputInterfaceFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public AddConfigInputInterfaceFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.IAdd#add(org.eclipse.graphiti.features.context.IAddContext)
   */
  @Override
  public PictogramElement add(final IAddContext context) {
    final Parameter addedParam = (Parameter) context.getNewObject();
    final Diagram targetDiagram = (Diagram) context.getTargetContainer();

    // CONTAINER SHAPE WITH ROUNDED RECTANGLE
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);

    // define a default size for the shape
    final int width = 16;
    final int height = 16;
    final IGaService gaService = Graphiti.getGaService();
    final Font font = gaService.manageDefaultFont(getDiagram(), false, true);
    final int fontHeight = GraphitiUi.getUiLayoutService().calculateTextSize("Abcq", font).getHeight();

    final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);
    gaService.setLocationAndSize(invisibleRectangle, context.getX(), context.getY(), 200, height + fontHeight + 2);

    Polygon triangle; // need to access it later
    {
      final BoxRelativeAnchor boxAnchor = peCreateService.createBoxRelativeAnchor(containerShape);
      boxAnchor.setRelativeWidth(0.5);
      boxAnchor.setRelativeHeight(1.0);
      boxAnchor.setReferencedGraphicsAlgorithm(invisibleRectangle);

      // create and set graphics algorithm for the anchor
      final int[] xy = { width / 2, 0, width, height, 0, height };
      triangle = gaService.createPolygon(boxAnchor, xy);
      triangle.setForeground(manageColor(AddConfigInputInterfaceFeature.CFG_IN_FOREGROUND));
      triangle.setBackground(manageColor(AddConfigInputInterfaceFeature.CFG_IN_BACKGROUND));
      triangle.setLineWidth(2);
      gaService.setLocationAndSize(triangle, -width / 2, -height, width, height);

      // if added Parameter has no resource we add it to the
      // resource of the graph
      if (addedParam.eResource() == null) {
        final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());
        graph.addParameter(addedParam);
      }
      link(boxAnchor, addedParam);
    }

    // Name of the ConfigInIf - SHAPE WITH TEXT
    {
      // create and set text graphics algorithm
      // create shape for text
      final Shape shape = peCreateService.createShape(containerShape, false);
      final Text text = gaService.createText(shape, addedParam.getName());
      text.setForeground(manageColor(AddConfigInputInterfaceFeature.CFG_IN_TEXT_FOREGROUND));
      text.setVerticalAlignment(Orientation.ALIGNMENT_TOP);
      text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
      // vertical alignment has as default value "center"
      text.setFont(font);
      text.setHeight(20);
      text.setWidth(200);
      link(shape, addedParam);
    }
    // create link and wire it
    link(containerShape, addedParam);

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
    // Check that the user wants to add an ConfigInputInterface to the
    // Diagram
    return (context.getNewObject() instanceof Parameter) && ((Parameter) context.getNewObject()).isConfigurationInterface()
        && (context.getTargetContainer() instanceof Diagram);
  }

}
