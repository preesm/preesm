/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
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
package org.preesm.graphiti.ui.editparts;

import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.AbstractHintLayout;
import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Figures using the StackLayout as their layout manager have their children placed on top of one another. Order of
 * placement is determined by the order in which the children were added, first child added placed on the bottom.
 */
public class ManualGraphLayoutManager extends StackLayout {

  /**
   *
   * @author anmorvan
   *
   */
  @FunctionalInterface
  interface SizeSelector {
    Dimension getSize(IFigure child, int widthHint, int heightHint);
  }

  private Dimension calculateSize(final IFigure figure, final int wHint, final int hHint,
      final SizeSelector sizeSelector) {
    final int widthHint;
    final int heightHint;
    if (wHint > -1) {
      widthHint = Math.max(0, wHint - figure.getInsets().getWidth());
    } else {
      widthHint = wHint;
    }
    if (hHint > -1) {
      heightHint = Math.max(0, hHint - figure.getInsets().getHeight());
    } else {
      heightHint = hHint;
    }
    final Dimension d = new Dimension();
    final List<? extends IFigure> children = figure.getChildren();
    for (final IFigure child : children) {
      if (!isObservingVisibility() || child.isVisible()) {
        d.union(sizeSelector.getSize(child, widthHint, heightHint));
      }
    }

    d.expand(figure.getInsets().getWidth(), figure.getInsets().getHeight());
    d.union(getBorderPreferredSize(figure));
    return d;
  }

  /**
   * Returns the minimum size required by the input container. This is the size of the largest child of the container,
   * as all other children fit into this size.
   *
   * @param figure
   *          the figure
   * @param wHint
   *          the w hint
   * @param hHint
   *          the h hint
   * @return the dimension
   * @see AbstractHintLayout#calculateMinimumSize(IFigure, int, int)
   */
  @Override
  protected Dimension calculateMinimumSize(final IFigure figure, final int wHint, final int hHint) {
    return calculateSize(figure, wHint, hHint, (f, w, h) -> f.getMinimumSize(w, h));
  }

  /**
   * Calculates and returns the preferred size of the given figure. This is the union of the preferred sizes of the
   * widest and the tallest of all its children.
   *
   * @param figure
   *          the figure
   * @param wHint
   *          the w hint
   * @param hHint
   *          the h hint
   * @return the dimension
   * @see AbstractLayout#calculatePreferredSize(IFigure, int, int)
   */
  @Override
  protected Dimension calculatePreferredSize(final IFigure figure, final int wHint, final int hHint) {
    return calculateSize(figure, wHint, hHint, (f, w, h) -> f.getPreferredSize(w, h));
  }

  /**
   * Returns the origin for the given figure.
   *
   * @param parent
   *          the figure whose origin is requested
   * @return the origin
   */
  public Point getOrigin(final IFigure parent) {
    return parent.getClientArea().getLocation();
  }

  /**
   * Layout.
   *
   * @param parent
   *          the parent
   * @see org.eclipse.draw2d.LayoutManager#layout(IFigure)
   */
  @Override
  public void layout(final IFigure parent) {
    final Rectangle r = parent.getClientArea();
    final List<? extends IFigure> children = parent.getChildren();
    for (final IFigure child : children) {
      if (child instanceof ScrollPane) {
        child.setBounds(r);
      }
    }

    final Iterator<? extends IFigure> it = children.iterator();
    final Point offset = getOrigin(parent);
    IFigure f;
    while (it.hasNext()) {
      f = it.next();
      Rectangle bounds = (Rectangle) getConstraint(f);
      if (bounds == null) {
        continue;
      }

      if ((bounds.width == -1) || (bounds.height == -1)) {
        final Dimension preferredSize = f.getPreferredSize(bounds.width, bounds.height);
        bounds = bounds.getCopy();
        if (bounds.width == -1) {
          bounds.width = preferredSize.width;
        }
        if (bounds.height == -1) {
          bounds.height = preferredSize.height;
        }
      }
      bounds = bounds.getTranslated(offset);
      f.setBounds(bounds);
    }
  }
}
