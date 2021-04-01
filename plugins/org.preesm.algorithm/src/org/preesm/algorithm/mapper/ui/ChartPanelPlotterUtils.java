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
      Frame frame = new JFrame("Gantt Chart Plotter");
      ((JFrame) frame).setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      RefineryUtilities.centerFrameOnScreen(frame);

      plotFrameCP(frame, cp);

    }

  }

}
