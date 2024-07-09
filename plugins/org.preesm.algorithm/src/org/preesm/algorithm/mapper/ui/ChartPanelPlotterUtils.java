/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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

import java.awt.Frame;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.jfree.chart.ChartPanel;
import org.jfree.ui.RefineryUtilities;

/**
 * Group simple methods to plot a ChartPanel.
 *
 * @author ahonorat
 */
public abstract class ChartPanelPlotterUtils {

  private ChartPanelPlotterUtils() {
    // Forbids instantiation
  }

  /**
   * Adds the ChartPanel to the frame
   *
   * @param frame
   *          the window frame
   * @param cp
   *          the chart panel to add in the frame
   */
  public static void plotFrameCP(Frame frame, ChartPanel cp) {
    frame.add(cp);
    // Set the visibility as true, thereby displaying it
    frame.setVisible(true);
    frame.pack();
  }

  /**
   * Listener in order to plot a ChartPanel in an external window if a click occurred on a SWT widget.
   *
   * @author ahonorat
   *
   */
  public static class SelectionAdapterPlottingCP extends SelectionAdapter {

    private final ChartPanel cp;

    /**
     * Builds the listener.
     *
     * @param cp
     *          The chart panel to plot in an external window, if click.
     */
    public SelectionAdapterPlottingCP(ChartPanel cp) {
      this.cp = cp;
    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
      final Frame frame = new JFrame("Gantt Chart Plotter");
      ((JFrame) frame).setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      RefineryUtilities.centerFrameOnScreen(frame);

      plotFrameCP(frame, cp);

    }

  }

}
