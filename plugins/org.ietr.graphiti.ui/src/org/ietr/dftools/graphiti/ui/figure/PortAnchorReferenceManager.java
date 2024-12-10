/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008 - 2011)
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

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * This class provides a reference manager for port anchors. It contains a main method, getReferencePoint, which clients
 * call to get a reference point for their connection anchor. There is one reference manager for each connection anchor.
 *
 * @author Matthieu Wipliez
 *
 */
public class PortAnchorReferenceManager {

  /** The figure. */
  private final VertexFigure figure;

  /** The is output. */
  private final boolean isOutput;

  /** The port name. */
  private final String portName;

  /**
   * Creates a new port anchor reference manager.
   *
   * @param figure
   *          The owning vertex figure.
   * @param portName
   *          The port name associated with this connection anchor.
   * @param isOutput
   *          Whether the connection is input (false) or output (true).
   */
  public PortAnchorReferenceManager(final VertexFigure figure, final String portName, final boolean isOutput) {
    this.figure = figure;
    this.portName = portName;
    this.isOutput = isOutput;
  }

  /**
   * Returns a reference point for the given connection anchor. It uses the underlying vertex figure to retrieve the
   * label associated with the port name given at creation time.
   *
   * @param anchor
   *          An abstract {@link ConnectionAnchor}.
   * @return A reference {@link Point}.
   */
  public Point getReferencePoint(final AbstractConnectionAnchor anchor) {
    if ((this.portName == null) || this.portName.isEmpty()) {
      return null;
    } else {
      Label label;
      if (this.isOutput) {
        label = this.figure.getOutputPortLabel(this.portName);
        if (label == null) {
          return null;
        }

        final Rectangle bounds = label.getBounds();
        final int x = bounds.x + bounds.width + 5;
        final int y = bounds.y + (bounds.height / 2);

        final Point ref = new Point(x, y);
        label.translateToAbsolute(ref);

        return ref;
      } else {
        label = this.figure.getInputPortLabel(this.portName);
        if (label == null) {
          return null;
        }

        final Rectangle bounds = label.getBounds();
        final int x = bounds.x - 5;
        final int y = bounds.y + (bounds.height / 2);

        final Point ref = new Point(x, y);
        label.translateToAbsolute(ref);

        return ref;
      }
    }
  }

}
