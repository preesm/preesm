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
package org.ietr.dftools.graphiti.ui.figure;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.DelegatingLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.ietr.dftools.graphiti.model.Edge;
import org.ietr.dftools.graphiti.model.ObjectType;
import org.ietr.dftools.graphiti.model.Parameter;
import org.ietr.dftools.graphiti.model.ParameterPosition;

/**
 * This class provides drawing for a dependency.
 *
 * @author Samuel Beaussier
 * @author Nicolas Isch
 * @author Matthieu Wipliez
 */
public class EdgeFigure extends PolylineConnection {

  /** The edge. */
  private final Edge edge;

  /** The parameter figures. */
  private final Map<String, Label> parameterFigures;

  /**
   * Creates the Figure associated to the connection.
   *
   * @param edge
   *          The Edge model associated with this figure.
   */
  public EdgeFigure(final Edge edge) {
    // Sets Layout Manager
    setLayoutManager(new DelegatingLayout());
    this.edge = edge;

    this.parameterFigures = new LinkedHashMap<>();

    // update parameters
    for (final Parameter parameter : edge.getParameters()) {
      refresh(parameter.getName(), edge.getValue(parameter.getName()));
    }

    final Boolean directed = (Boolean) edge.getType().getAttribute(ObjectType.ATTRIBUTE_DIRECTED);
    if ((directed == null) || Boolean.TRUE.equals(directed)) {
      // we consider the edge directed by default.
      setTargetDecoration(new PolygonDecoration());
    }

    Color color = (Color) edge.getType().getAttribute(ObjectType.ATTRIBUTE_COLOR);
    if (color == null) {
      color = ColorConstants.black;
    }

    setForegroundColor(color);
    setLineWidth(1);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.draw2d.Shape#paintFigure(org.eclipse.draw2d.Graphics)
   */
  @Override
  public void paintFigure(final Graphics graphics) {
    if (graphics instanceof SWTGraphics) {
      graphics.pushState();
      try {
        // Needs advanced capabilities or throws SWTException
        graphics.setAntialias(SWT.ON);
      } catch (final RuntimeException e) {
        // No anti alias, less pretty but it will work!
      }

      super.paintFigure(graphics);
      graphics.popState();
    } else {
      // ScaledGraphics and PrinterGraphics do not have advanced
      // capabilities... so we try with SWTGraphics
      super.paintFigure(graphics);
    }
  }

  /**
   * Refresh.
   *
   * @param parameterName
   *          the parameter name
   * @param value
   *          the value
   */
  public void refresh(final String parameterName, Object value) {
    final Label label = this.parameterFigures.get(parameterName);
    if (label == null) {
      value = this.edge.getValue(parameterName);
      if (value != null) {
        final Parameter parameter = this.edge.getParameter(parameterName);
        final ParameterPosition position = parameter.getPosition();

        if (position != null) {
          final Label parameterLabel = new Label(value.toString());
          parameterLabel.setForegroundColor(new Color(null, 224, 0, 0));
          final Object locator = new PropertyLocator(this, position);
          add(parameterLabel, locator);
          this.parameterFigures.put(parameterName, parameterLabel);
        }
      }
    } else {
      label.setText(value.toString());
    }
  }
}
