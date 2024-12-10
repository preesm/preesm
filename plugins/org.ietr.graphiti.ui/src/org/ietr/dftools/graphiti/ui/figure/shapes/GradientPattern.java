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

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.widgets.Display;

/**
 * The Class GradientPattern.
 *
 * @author mwipliez
 */
public class GradientPattern {

  private GradientPattern() {
    // forbid instantiation
  }

  /**
   * Paints this {@link IShape} with the given background {@link Color}, in the specified {@link Rectangle} bounds, on
   * the given {@link Graphics}. If the graphics do not have advanced capabilities (such as printer or zoom manager),
   * the function will try to use the current display to draw the {@link IShape} as an image.
   *
   * @param shape
   *          the shape
   * @param backgroundColor
   *          the background color
   * @param bounds
   *          the bounds
   * @param graphics
   *          the graphics
   */
  public static void paintFigure(final IShape shape, final Color backgroundColor, final Rectangle bounds,
      final Graphics graphics) {
    if (graphics instanceof SWTGraphics) {
      // advanced graphics
      final Color fg = new Color(null, 224, 224, 224);

      // square gradient, from left-bottom to right-top
      final int max = Math.max(bounds.width, bounds.height);
      final Pattern pattern = new Pattern(backgroundColor.getDevice(), 0, max, max, 0, backgroundColor, 192, fg, 192);

      graphics.pushState();
      try {
        // Needs advanced capabilities or throws SWTException
        graphics.setAntialias(SWT.ON);
        graphics.setBackgroundPattern(pattern);
      } catch (final RuntimeException e) {
        // No anti alias, not pattern, less pretty but it will work!
      }

      shape.paintSuperFigure(graphics);
      graphics.popState();

      // pattern is not used anymore by graphics => dispose
      pattern.dispose();
    } else {
      // ScaledGraphics and PrinterGraphics do not have advanced
      // capabilities... so we try with SWTGraphics

      // Creates a new image of width x height on the current display
      final Image image = new Image(Display.getCurrent(), bounds.width, bounds.height);

      // Paints the figure on it using SWT graphics
      final GC gc = new GC(image);
      final Graphics swtGraphics = new SWTGraphics(gc);
      GradientPattern.paintFigure(shape, backgroundColor, bounds, swtGraphics);

      // Draws the image on the original graphics
      graphics.drawImage(image, 0, 0);

      // Disposes image (and GC btw) and SWT graphics
      image.dispose();
      swtGraphics.dispose();
    }
  }
}
