/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2024 - 2025) :
 *
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024 - 2025)
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

import java.util.List;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.CompoundDirectedGraph;
import org.eclipse.draw2d.graph.CompoundDirectedGraphLayout;
import org.preesm.graphiti.GraphitiException;

/**
 * This class provides a basic graph layout.
 *
 * @author Matthieu Wipliez
 */
public class GraphLayoutManager extends XYLayout {

  /** The direction. */
  private final int direction;

  /** The part. */
  private final GraphEditPart part;

  /**
   * Creates a new graph layout manager on the given document and with the given direction.
   *
   * @param part
   *          The document to layout.
   * @param direction
   *          The direction, one of:
   *          <UL>
   *          <LI>{@link org.eclipse.draw2d.PositionConstants#EAST}
   *          <LI>{@link org.eclipse.draw2d.PositionConstants#SOUTH}
   *          </UL>
   */
  public GraphLayoutManager(final GraphEditPart part, final int direction) {
    this.part = part;
    this.direction = direction;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.draw2d.XYLayout#calculatePreferredSize(org.eclipse.draw2d.IFigure, int, int)
   */
  @Override
  protected Dimension calculatePreferredSize(final IFigure container, final int wHint, final int hHint) {
    container.validate();
    final List<? extends IFigure> children = container.getChildren();
    final Rectangle result = new Rectangle().setLocation(container.getClientArea().getLocation());
    for (final IFigure child : children) {
      result.union(child.getBounds());
    }
    result.resize(container.getInsets().getWidth(), container.getInsets().getHeight());
    return result.getSize();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.draw2d.LayoutManager#layout(org.eclipse.draw2d.IFigure)
   */
  @Override
  public void layout(final IFigure container) {
    try {
      final CompoundDirectedGraph graph = new CompoundDirectedGraph();
      graph.setDirection(this.direction);

      this.part.addNodes(graph.nodes);
      this.part.addEdges(graph.edges);

      final CompoundDirectedGraphLayout layout = new CompoundDirectedGraphLayout();
      layout.visit(graph);
    } catch (final Exception e) {
      throw new GraphitiException("Could not layout graph", e);
    }

    this.part.updateFigures();
  }
}
