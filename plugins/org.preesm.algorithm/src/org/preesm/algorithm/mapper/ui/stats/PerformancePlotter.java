/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2015)
 * Jonathan Piat [jpiat@laas.fr] (2008)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2012)
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
package org.preesm.algorithm.mapper.ui.stats;

import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.JRootPane;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.preesm.algorithm.mapper.ui.ChartPanelPlotterUtils;
import org.preesm.commons.logger.PreesmLogger;

/**
 * Plots the performance of a given implementation and compares it to the maximum possible speed ups.
 *
 * @author mpelcat
 */
public class PerformancePlotter {

  private PerformancePlotter() {
    // Forbids instantiation
  }

  /**
   * Initial dimensions of the window
   */
  public static final int WIN_DIMENSION_X = 500;
  public static final int WIN_DIMENSION_Y = 470;

  /**
   * Creates a chart in order to plot the speed-ups.
   *
   * @param statGen
   *          the stats
   * @return A chart.
   */
  private static JFreeChart createChart(final IStatGenerator statGen) {
    final String title = "Comparing the obtained speedup to ideal speedups";

    // Creating the best speedups subplot
    final DefaultXYDataset speedups = setData(statGen);

    // Creating display domain
    final NumberAxis horizontalAxis = new NumberAxis("Number of operators");
    final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(horizontalAxis);

    final NumberAxis xAxis = new NumberAxis("speedups");

    xAxis.setAutoRangeIncludesZero(false);

    final XYSplineRenderer renderer = new XYSplineRenderer();
    final XYPlot subplot = new XYPlot(speedups, null, xAxis, renderer);

    subplot.setBackgroundPaint(Color.white);
    subplot.setDomainGridlinePaint(Color.lightGray);
    subplot.setRangeGridlinePaint(Color.lightGray);
    plot.add(subplot);

    plot.setForegroundAlpha(0.5f);

    final JFreeChart chart = new JFreeChart(title, plot);

    chart.setBorderPaint(Color.white);
    chart.setBorderVisible(true);
    chart.setBackgroundPaint(Color.white);

    plot.setBackgroundPaint(Color.white);
    plot.setDomainGridlinePaint(Color.white);
    plot.setRangeGridlinePaint(Color.white);

    final ValueAxis axis = plot.getDomainAxis();
    axis.setAutoRange(true);

    return chart;

  }

  /**
   * Creates the graph values for input data:.
   *
   * @param statGen
   *          Statistics to draw.
   */
  private static DefaultXYDataset setData(final IStatGenerator statGen) {

    final long workLength = statGen.getDAGWorkLength();
    final long spanLength = statGen.getDAGSpanLength();
    final long resultTime = statGen.getFinalTime();
    final int resultNbMainCores = statGen.getNbMainTypeOperators();

    final double absoluteBestSpeedup = ((double) workLength) / ((double) spanLength);
    final int maxCoreNumber = (int) Math.ceil(absoluteBestSpeedup) + 10;

    final DefaultXYDataset speedups = new DefaultXYDataset();

    // Creating point for current speedup
    final double[][] currentSpeedup = new double[2][1];
    currentSpeedup[0][0] = resultNbMainCores;
    currentSpeedup[1][0] = ((double) workLength) / ((double) resultTime);
    speedups.addSeries("Currently obtained speedup", currentSpeedup);

    // Creating curve for best speedups
    // The speedup is limited y the span length
    final double[][] bestSpeedups = new double[2][maxCoreNumber];

    for (int nbCores = 1; nbCores <= maxCoreNumber; nbCores++) {
      bestSpeedups[0][nbCores - 1] = nbCores;

      if (nbCores < absoluteBestSpeedup) {
        bestSpeedups[1][nbCores - 1] = nbCores;
      } else {
        bestSpeedups[1][nbCores - 1] = absoluteBestSpeedup;
      }
    }
    speedups.addSeries("Maximum achievable speedups", bestSpeedups);

    // Creating curve for best speedups
    // The speedup is limited y the span length
    final double[][] reachableSpeedups = new double[2][maxCoreNumber];

    for (int nbCores = 1; nbCores <= maxCoreNumber; nbCores++) {
      reachableSpeedups[0][nbCores - 1] = nbCores;

      reachableSpeedups[1][nbCores - 1] = ((double) (workLength * nbCores))
          / ((double) ((spanLength * nbCores) + workLength));
    }
    speedups.addSeries("Greedy-Scheduling Theorem bound", reachableSpeedups);

    return speedups;
  }

  /**
   * Display.
   *
   * @param toolkit
   *          managed form toolkit
   * @param parentComposite
   *          where to integrate the chart in the form
   * @param statGen
   *          Statistics to plot.
   */
  public static void display(final FormToolkit toolkit, final Composite parentComposite, final IStatGenerator statGen) {

    final JFreeChart chart = createChart(statGen);
    final ChartPanel cp = new ChartPanel(chart);
    cp.setPreferredSize(new java.awt.Dimension(WIN_DIMENSION_X, WIN_DIMENSION_Y));
    cp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    final Composite composite = new Composite(parentComposite, SWT.EMBEDDED | SWT.FILL);
    parentComposite.setLayout(new FillLayout());
    // see explanations in org.preesm.algorithm.mapper.ui.Ganttplotter#plotDeploymen
    Frame frame = null;
    try {
      frame = SWT_AWT.new_Frame(composite);
    } catch (UnsatisfiedLinkError | SWTError e) {
      PreesmLogger.getLogger().log(Level.INFO,
          "An error occured while loading org.eclipse.swt.awt.SWT_AWT class "
              + "or its associated shared object libswt-awt-gtk-4928+.so, "
              + "thus the speedup diagram is not embedded in Eclipse. See error:\n" + e.getMessage());
      if (!composite.isDisposed()) {
        composite.dispose();
      }
    }
    if (frame != null) {
      // plot inside the window
      final JRootPane root = new JRootPane();
      final Container awtContainer = root.getContentPane();
      awtContainer.add(cp);
      frame.add(root);
      ChartPanelPlotterUtils.plotFrameCP(frame, cp);

    } else {
      // plot in a new window if user clicks on the button
      final Button externalDisplayButton = toolkit.createButton(parentComposite,
          "Click to open the speedup diagram in a new window", SWT.PUSH | SWT.CENTER);
      externalDisplayButton
          .setToolTipText("We must do this because of a bug on Linux due to GTK/Eclipse most probably ...");

      externalDisplayButton.addSelectionListener(new ChartPanelPlotterUtils.SelectionAdapterPlottingCP(cp));

    }

  }

}
