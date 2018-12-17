/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2010 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2010 - 2012)
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
package org.preesm.algorithm.mapper.ui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;

/**
 * Listening for mouse events to refresh correctly the awt display when Gantt chart is changed.
 *
 * @author mpelcat
 */
public class MouseClickedListener implements MouseMotionListener, ChartMouseListener, MouseListener {

  /** The frame. */
  private final Frame frame;

  /** The dragged. */
  private boolean dragged = false;

  /**
   * Instantiates a new mouse clicked listener.
   *
   * @param frame
   *          the frame
   */
  public MouseClickedListener(final Frame frame) {
    this.frame = frame;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseDragged(final java.awt.event.MouseEvent e) {
    this.dragged = true;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseMoved(final java.awt.event.MouseEvent e) {
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jfree.chart.ChartMouseListener#chartMouseClicked(org.jfree.chart.ChartMouseEvent)
   */
  @Override
  public void chartMouseClicked(final ChartMouseEvent arg0) {
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jfree.chart.ChartMouseListener#chartMouseMoved(org.jfree.chart.ChartMouseEvent)
   */
  @Override
  public void chartMouseMoved(final ChartMouseEvent arg0) {
  }

  /*
   * (non-Javadoc)
   *
   * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseClicked(final java.awt.event.MouseEvent e) {
  }

  /*
   * (non-Javadoc)
   *
   * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseEntered(final java.awt.event.MouseEvent e) {
  }

  /*
   * (non-Javadoc)
   *
   * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseExited(final java.awt.event.MouseEvent e) {
  }

  /*
   * (non-Javadoc)
   *
   * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
   */
  @Override
  public void mousePressed(final java.awt.event.MouseEvent e) {
  }

  /*
   * (non-Javadoc)
   *
   * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
   */
  @Override
  public void mouseReleased(final java.awt.event.MouseEvent e) {
    if (this.dragged) {
      final Dimension d = this.frame.getSize();
      this.frame.setSize(d);

      this.dragged = false;
    }
  }

}
