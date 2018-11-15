/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
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
package org.preesm.ui.pisdf.features;

import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.preesm.model.pisdf.Parameter;

// TODO: Auto-generated Javadoc
/**
 * Layout Feature for {@link Parameter}s.
 *
 * @author kdesnos
 * @author jheulot
 */
public class LayoutParameterFeature extends AbstractLayoutFeature {

  /**
   * Default constructor of the {@link LayoutParameterFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public LayoutParameterFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.ILayout#canLayout(org.eclipse.graphiti.features.context.ILayoutContext)
   */
  @Override
  public boolean canLayout(final ILayoutContext context) {
    // return true, if pictogram element is linked to a Parameter
    final PictogramElement pe = context.getPictogramElement();
    if (!(pe instanceof ContainerShape)) {
      return false;
    }

    final EList<EObject> businessObjects = pe.getLink().getBusinessObjects();
    return (businessObjects.size() == 1) && (businessObjects.get(0) instanceof Parameter);
  }

  /**
   * Return the new width of the shape. <br>
   * <br>
   * The new width is computed so that all text are completely visible. Consequently, the method check the width and
   * height of the Text children shape. <br>
   * <br>
   * <b> The method does not apply the new width to the shape. </b> Use {@link LayoutParameterFeature#setNewWith} for
   * that purpose.
   *
   * @param childrenShapes
   *          the children shapes of the Actor
   * @return the new width
   */
  protected int getNewWidth(final EList<Shape> childrenShapes) {
    // RETRIEVE THE NAME WIDTH
    IDimension nameSize = null;
    {
      // Scan the children shape looking for the parameter name
      for (final Shape shape : childrenShapes) {
        final GraphicsAlgorithm child = shape.getGraphicsAlgorithm();
        // The name should be the only children with type text
        if (child instanceof Text) {
          final String text = ((Text) child).getValue();
          final Font font = ((Text) child).getFont();

          // Retrieve the size of the text
          nameSize = GraphitiUi.getUiLayoutService().calculateTextSize(text, font);
        }
      }
    }

    // Set a minimal width of a parameter
    return Math.max(nameSize.getWidth() + 6, 33);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.func.ILayout#layout(org.eclipse.graphiti.features.context.ILayoutContext)
   */
  @Override
  public boolean layout(final ILayoutContext context) {
    boolean anythingChanged = false;

    // Retrieve the shape and the graphic algorithm
    final ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
    final GraphicsAlgorithm containerGa = containerShape.getGraphicsAlgorithm();

    // Retrieve all contained shapes
    final EList<Shape> childrenShapes = containerShape.getChildren();

    final int newWidth = getNewWidth(childrenShapes);

    if (newWidth != containerGa.getWidth()) {
      setNewWidth(newWidth, containerGa, childrenShapes);

      anythingChanged = true;
    }

    return anythingChanged;
  }

  /**
   * Apply the new width to the shape children.
   *
   * @param newWidth
   *          the new width to apply
   * @param containerGa
   *          the container ga
   * @param childrenShapes
   *          the children shapes
   */
  protected void setNewWidth(final int newWidth, final GraphicsAlgorithm containerGa,
      final EList<Shape> childrenShapes) {
    // Scan the children shapes
    for (final Shape shape : childrenShapes) {
      final GraphicsAlgorithm child = shape.getGraphicsAlgorithm();
      if (child instanceof Text) {
        final Orientation align = ((Text) child).getHorizontalAlignment();

        // If the text is the name of the object
        if (align == Orientation.ALIGNMENT_CENTER) {
          // The name is centered and has the same width as the actor
          // rounded rectangle
          child.setWidth(newWidth);
        }
      }
    }

    // Treat the container GA
    // Resize the house-shaped polygon
    {
      containerGa.setWidth(newWidth);
      final int height = containerGa.getHeight(); // The height is constant
      final int[] coord = new int[] { newWidth / 2, 0, newWidth, height - 14, newWidth, height, 0, height, 0,
          height - 14 };

      final List<Point> points = ((Polygon) containerGa).getPoints();
      int i = 0;
      for (final Point p : points) {
        p.setX(coord[i]);
        p.setY(coord[i + 1]);
        i += 2;
      }
    }
  }
}
