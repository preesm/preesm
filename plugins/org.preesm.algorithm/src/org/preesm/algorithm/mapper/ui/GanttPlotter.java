/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019 - 2020)
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
package org.preesm.algorithm.mapper.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JRootPane;
import javax.swing.WindowConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.IntervalCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.ui.RefineryUtilities;
import org.preesm.algorithm.mapper.gantt.GanttComponent;
import org.preesm.algorithm.mapper.gantt.GanttData;
import org.preesm.algorithm.mapper.gantt.GanttTask;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.files.PreesmResourcesHelper;
import org.preesm.commons.logger.PreesmLogger;

/**
 * Gantt plotter of a mapperdagvertex using JFreeChart.
 *
 * @author mpelcat
 */
public class GanttPlotter {

  /**
   * Initial dimensions of the window
   */
  public static final int WIN_DIMENSION_X = 700;
  public static final int WIN_DIMENSION_Y = 500;

  /**
   * Creates a chart.
   *
   * @param dataset
   *          a dataset.
   * @param idTOcolor
   *          Map that will be used here with tasks colors.
   *
   * @return A chart.
   */
  private static JFreeChart createChart(final IntervalCategoryDataset dataset, final Map<String, Color> idTOcolor) {

    final JFreeChart chart = ChartFactory.createGanttChart("Solution Gantt", // title
        "Operators", // x-axis label
        "Time", // y-axis label
        null, // data
        true, // create legend?
        true, // generate tooltips?
        false // generate URLs?
    );

    final Paint p = GanttPlotter.getBackgroundColorGradient();
    chart.setBackgroundPaint(p);

    final CategoryPlot plot = (CategoryPlot) chart.getPlot();
    plot.setBackgroundPaint(Color.white);
    plot.setDomainGridlinePaint(Color.white);
    plot.setRangeGridlinePaint(Color.black);
    plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
    plot.setOrientation(PlotOrientation.HORIZONTAL);

    final DateAxis xaxis = (DateAxis) plot.getRangeAxis();
    xaxis.setDateFormatOverride(new VertexDateFormat());
    xaxis.setPositiveArrowVisible(true);

    final DefaultDrawingSupplier d = new DefaultDrawingSupplier();

    plot.setDrawingSupplier(d);
    final MyGanttRenderer ren = new MyGanttRenderer(idTOcolor);

    ren.setSeriesItemLabelsVisible(0, false);
    ren.setSeriesVisibleInLegend(0, false);
    ren.setSeriesItemLabelGenerator(0, new IntervalCategoryItemLabelGenerator());
    ren.setSeriesToolTipGenerator(0, new MapperGanttToolTipGenerator());

    ren.setAutoPopulateSeriesShape(false);

    plot.setRenderer(ren);

    plot.setDataset(dataset);
    return chart;

  }

  /**
   * Creates a dataset from a MapperDAGVertex. This dataset is used to prepare display of a Gantt chart with one line
   * per populated SLAM component.
   *
   * @param ganttData
   *          the gantt data
   * @param idTOcolor
   *          Map that will be filled here with tasks colors.
   * @return The dataset.
   */
  private static IntervalCategoryDataset createDataset(final GanttData ganttData, final Map<String, Color> idTOcolor) {

    final TaskSeries series = new TaskSeries("Scheduled");

    // Creating the component lines (operator or communicator)
    final List<GanttComponent> components = ganttData.getComponents();

    for (final GanttComponent cmp : components) {
      final Task currentJFreeCmp = new Task(cmp.getId(), new SimpleTimePeriod(0, 1));
      series.add(currentJFreeCmp);

      // Setting the series length to the maximum end time of a task
      final long finalCost = cmp.getEndTime();
      series.get(cmp.getId()).setDuration(new SimpleTimePeriod(0, finalCost));

      for (final GanttTask ganttTask : cmp.getTasks()) {
        final String taskName = ganttTask.getId();
        final long start = ganttTask.getStartTime();
        final long end = start + ganttTask.getDuration();
        final Task currentJFreeTask = new Task(taskName, new SimpleTimePeriod(start, end));
        final Color color = ganttTask.getColor();
        if (color != null) {
          idTOcolor.put(taskName, color);
        }

        currentJFreeCmp.addSubtask(currentJFreeTask);
      }
    }

    final TaskSeriesCollection collection = new TaskSeriesCollection();
    collection.add(series);

    return collection;

  }

  /**
   * Plot deployment.
   *
   * @param ganttData
   *          the gantt data
   * @param managedForm
   *          the parent control, may be null (then opens a new window)
   */
  public static void plotDeployment(final GanttData ganttData, final IManagedForm managedForm) {

    // JChart init
    final Map<String, Color> idTOcolor = new TreeMap<>();
    final JFreeChart chart = createChart(createDataset(ganttData, idTOcolor), idTOcolor);
    final ChartPanel cp = new ChartPanel(chart);
    cp.setPreferredSize(new java.awt.Dimension(GanttPlotter.WIN_DIMENSION_X, GanttPlotter.WIN_DIMENSION_Y));
    cp.setMouseZoomable(true, true);
    cp.setMouseWheelEnabled(true);
    final CategoryPlot categoryPlot = cp.getChart().getCategoryPlot();
    categoryPlot.setRangePannable(true);

    final JMenuItem menuItem = new JMenuItem("Help ...");
    cp.getPopupMenu().add(menuItem);

    final JFrame helpFrame = new JFrame("Gantt Help");
    helpFrame.setSize(WIN_DIMENSION_X / 2, WIN_DIMENSION_Y / 2);
    helpFrame.setLocationRelativeTo(cp);
    helpFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    try {
      final URL resource = PreesmResourcesHelper.getInstance().resolve("GanttHelp.html", GanttPlotter.class);
      final JEditorPane comp = new JEditorPane(resource);
      helpFrame.getContentPane().add(comp, BorderLayout.PAGE_START);
    } catch (final IOException ex) {
      throw new PreesmRuntimeException("Could not load Gantt Help file", ex);
    }
    menuItem.addActionListener(e -> helpFrame.setVisible(true));

    // Frame init
    if (managedForm == null) {
      final Frame frame = new JFrame("Gantt Chart Plotter");
      ((JFrame) frame).setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      RefineryUtilities.centerFrameOnScreen(frame);

      ChartPanelPlotterUtils.plotFrameCP(frame, cp);

    } else {

      final ScrolledForm form = managedForm.getForm();
      final FillLayout layout = new FillLayout();
      form.getBody().setLayout(layout);

      final Composite composite = new Composite(form.getBody(), SWT.EMBEDDED | SWT.FILL);
      Frame frame = null;

      // This fix is now required as we now ship PREESM with the JustJ JDK.
      // Leaving the original code just in case this issue is resolved some day.

      // if (!SystemUtils.IS_OS_LINUX) {
      if (false) {
        // related to PREESM issue #303
        // may not work because of a libswt-awt-gtk4928+.so bug which is not our responsability
        // see following bug report
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=558874
        // another bug prevents to generate the exception correctly, it has been fixed in latest swt
        // see following bug report
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=558681
        // This bug appears in other parts of Preesm using SWT_AWT:
        // org/preesm/algorithm/mapper/ui/BestCostPlotter.java
        // (but we do not care since calling code is deprecated: Fast and PFast schedulers)
        // org/preesm/algorithm/mapper/ui/stats/PerformancePlotter.java
        // (we care but not too much since it is not used with the new Synthesis)
        frame = SWT_AWT.new_Frame(composite);
      } else {
        // we catch this error since we can recover from it
        // Level.WARNING may stop the workflow depending on options
        // and as we are in a separate thread, events are not always well managed ...
        // so we use Level.INFO instead
        PreesmLogger.getLogger().log(Level.INFO,
            "An error occured while loading org.eclipse.swt.awt.SWT_AWT class "
                + "or its associated shared object libswt-awt-gtk-4928+.so, "
                + "thus the Gantt diagram is not embedded in Eclipse.");
        // the created composite cannot be used so we dispose it and force to reset layout
        if (!composite.isDisposed()) {
          composite.dispose();
        }
        form.getBody().layout(true, true);
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
        final FormToolkit toolkit = managedForm.getToolkit();
        final Button externalDisplayButton = toolkit.createButton(form.getBody(),
            "Click to open the Gantt diagram in a new window", SWT.PUSH | SWT.CENTER);
        externalDisplayButton
            .setToolTipText("We must do this because of a bug on Linux due to GTK/Eclipse most probably ...");

        externalDisplayButton.addSelectionListener(new ChartPanelPlotterUtils.SelectionAdapterPlottingCP(cp));

      }
    }
  }

  /**
   * Gets the background color gradient.
   *
   * @return the background color gradient
   */
  public static LinearGradientPaint getBackgroundColorGradient() {
    final Point2D start = new Point2D.Float(0, 0);
    final Point2D end = new Point2D.Float(WIN_DIMENSION_X, WIN_DIMENSION_Y);
    final float[] dist = { 0.0f, 0.8f };
    final Color[] colors = { new Color(170, 160, 190), Color.WHITE };
    return new LinearGradientPaint(start, end, dist, colors);
  }

}
