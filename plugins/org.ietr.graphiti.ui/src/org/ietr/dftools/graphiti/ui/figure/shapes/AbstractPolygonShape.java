/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008 - 2010)
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
package org.ietr.dftools.graphiti.ui.figure.shapes;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Polygon;
import org.ietr.dftools.graphiti.GraphitiException;
import org.ietr.dftools.graphiti.ui.figure.PolygonPortAnchor;
import org.ietr.dftools.graphiti.ui.figure.VertexFigure;

/**
 * This class provides an abstract polygon shape for hexagon, losange and triangle shapes.
 *
 * @author Matthieu Wipliez
 *
 */
public abstract class AbstractPolygonShape extends Polygon implements IShape {

  /**
   * Creates a new abstract polygon shape.
   */
  public AbstractPolygonShape() {
    setLayoutManager(new GridLayout(2, false));
    setFill(true);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.graphiti.ui.figure.shapes.IShape#getConnectionAnchor(org.ietr.dftools.graphiti.ui.figure.
   * VertexFigure, java.lang.String, boolean)
   */
  @Override
  public ConnectionAnchor getConnectionAnchor(final VertexFigure figure, final String portName,
      final boolean isOutput) {
    return new PolygonPortAnchor(figure, portName, isOutput);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.graphiti.ui.figure.shapes.IShape#newShape()
   */
  @Override
  public IShape newShape() {
    try {
      return getClass().newInstance();
    } catch (final InstantiationException | IllegalAccessException e) {
      throw new GraphitiException("", e);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.draw2d.Shape#paintFigure(org.eclipse.draw2d.Graphics)
   */
  @Override
  public void paintFigure(final Graphics graphics) {
    GradientPattern.paintFigure(this, getBackgroundColor(), getBounds(), graphics);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.graphiti.ui.figure.shapes.IShape#paintSuperFigure(org.eclipse.draw2d.Graphics)
   */
  @Override
  public void paintSuperFigure(final Graphics graphics) {
    super.paintFigure(graphics);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.draw2d.Figure#useLocalCoordinates()
   */
  @Override
  protected boolean useLocalCoordinates() {
    return true;
  }

}
