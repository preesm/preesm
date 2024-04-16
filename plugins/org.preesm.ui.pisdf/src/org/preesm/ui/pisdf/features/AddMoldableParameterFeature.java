/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013 - 2020)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2015)
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
package org.preesm.ui.pisdf.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Polygon;
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
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.preesm.model.pisdf.MoldableParameter;
import org.preesm.model.pisdf.PiGraph;

// TODO: Auto-generated Javadoc
/**
 * Add feature to add a {@link MoldableParameter} to the Diagram.
 *
 * @author kdesnos
 */
public class AddMoldableParameterFeature extends AbstractAddFeature {

  /** The Constant PARAMETER_TEXT_FOREGROUND. */
  public static final IColorConstant PARAMETER_TEXT_FOREGROUND = IColorConstant.BLACK;

  /** The Constant PARAMETER_FOREGROUND. */
  public static final IColorConstant PARAMETER_FOREGROUND = IColorConstant.BLACK;

  /** The Constant PARAMETER_BACKGROUND. */
  public static final IColorConstant PARAMETER_BACKGROUND = new ColorConstant(185, 206, 172);

  /** The Constant PARAM_HEIGHT. */
  public static final int PARAM_HEIGHT = 50;

  /**
   * Default constructor of the {@link AddMoldableParameterFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public AddMoldableParameterFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.IAdd#canAdd(org.eclipse.graphiti.features.context.IAddContext)
   */
  @Override
  public boolean canAdd(final IAddContext context) {
    // Check that the user wants to add a Parameter to the Diagram
    return (context.getNewObject() instanceof MoldableParameter) && (context.getTargetContainer() instanceof Diagram);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.IAdd#add(org.eclipse.graphiti.features.context.IAddContext)
   */
  @Override
  public PictogramElement add(final IAddContext context) {
    final MoldableParameter addedParameter = (MoldableParameter) context.getNewObject();
    final Diagram targetDiagram = (Diagram) context.getTargetContainer();

    // CONTAINER SHAPE WITH Triangle
    final IPeCreateService peCreateService = Graphiti.getPeCreateService();
    final ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);

    // define a default size for the shape
    final int width = 80;
    final int height = 40;
    final IGaService gaService = Graphiti.getGaService();

    Polygon house;
    {
      // Create a house shaped polygon
      final int[] xy = new int[] { 12, 0, 24, 26, 24, AddMoldableParameterFeature.PARAM_HEIGHT, 0,
          AddMoldableParameterFeature.PARAM_HEIGHT, 0, 26 };
      house = gaService.createPolygon(containerShape, xy);

      house.setBackground(manageColor(AddMoldableParameterFeature.PARAMETER_BACKGROUND));
      house.setForeground(manageColor(AddMoldableParameterFeature.PARAMETER_FOREGROUND));
      house.setLineWidth(2);
      gaService.setLocationAndSize(house, context.getX(), context.getY(), width, height);

      // if added Class has no resource we add it to the resource
      // of the graph
      if (addedParameter.eResource() == null) {
        final PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());
        graph.addParameter(addedParameter);
      }

      // create link and wire it
      link(containerShape, addedParameter);
    }

    // Name of the actor - SHAPE WITH TEXT
    {
      // create shape for text
      final Shape shape = peCreateService.createShape(containerShape, false);

      // create and set text graphics algorithm
      final Text text = gaService.createText(shape, addedParameter.getName());
      text.setForeground(manageColor(AddMoldableParameterFeature.PARAMETER_TEXT_FOREGROUND));
      text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);

      // vertical alignment has as default value "center"
      text.setFont(gaService.manageDefaultFont(getDiagram(), false, true));
      text.getWidth();
      gaService.setLocationAndSize(text, 0, height - 18, width, 20);

      // create link and wire it
      link(shape, addedParameter);
    }

    // Add a ChopBoxAnchor for the parameter
    final ChopboxAnchor cba = peCreateService.createChopboxAnchor(containerShape);
    link(cba, addedParameter);

    layoutPictogramElement(containerShape);
    return containerShape;
  }

}
