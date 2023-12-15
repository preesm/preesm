package org.preesm.algorithm.node.simulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.category.DefaultCategoryDataset;
import org.preesm.commons.files.PreesmIOHelper;

public class AnalysisPage3 {
  public static final String DSE_PART_NAME = "dse_part_trend.csv";

  static int    iterationNum = 0;
  static int    iterationOptim;
  static String path;
  static long   finalDSE     = 0L;

  public AnalysisPage3(String path, int iterationOptim) {
    AnalysisPage3.path = path;
    AnalysisPage3.iterationOptim = iterationOptim;
  }

  public JPanel execute() {
    final JPanel panel = new JPanel();
    final DefaultCategoryDataset series = fillDSEpartDataSet();

    final JLabel descriptionLabel = new JLabel(description());
    descriptionLabel.setForeground(Color.darkGray);
    descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
    descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
    final Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    descriptionLabel.setBorder(border);
    descriptionLabel.setPreferredSize(descriptionLabel.getPreferredSize());
    panel.add(descriptionLabel, BorderLayout.NORTH);

    final JFreeChart chart = barChart(series);
    panel.setBackground(Color.white);
    panel.setLayout(new GridLayout(2, 1));
    panel.add(new ChartPanel(chart));

    return panel;
  }

  private String description() {
    return "<html>This chart gives an idea of the impact of the efficiency of the application"
        + " graph distribution on your set of nodes via the SimSDP method.<br>" + " The method has iterated over <b>"
        + iterationNum + "</b> iterations, " + "and here is the resource allocation time for each iteration.<br>"

        + "The graph shows the resource allocation process time over iterations.<br><br>"
        + "The optimal configuration is achieved with the following attributes: <br>" + "- Iteration: " + iterationOptim
        + " <br>" + " - Cumulative resource allocation time: " + finalDSE + " ms <br>" + " - Formatted duration:"
        + formatDuration() + "</html>";
  }

  private String formatDuration() {
    final long milliseconds = finalDSE;
    final long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
    final long hours = TimeUnit.MILLISECONDS.toHours(milliseconds) % 24;
    final long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60;
    final long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;

    return String.format("%d days %02d hours %02d min %02d sec", days, hours, minutes, seconds);
  }

  private JFreeChart barChart(DefaultCategoryDataset dataset) {
    JFreeChart chart;
    chart = ChartFactory.createStackedBarChart("Cumulative Resource Allocation Time over Iteration", "Iteration",
        "Resource allocation time (ms)", dataset, PlotOrientation.VERTICAL, true, true, false);
    chart.getLegend().setPosition(RectangleEdge.RIGHT);

    final Plot plot = chart.getPlot();
    plot.setBackgroundPaint(Color.white);
    final CategoryPlot catplot = chart.getCategoryPlot();
    final StackedBarRenderer renderer = (StackedBarRenderer) catplot.getRenderer();
    renderer.setBarPainter(new StandardBarPainter());
    return chart;
  }

  private static DefaultCategoryDataset fillDSEpartDataSet() {
    finalDSE = 0L;
    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    final String[] arrayDSE = PreesmIOHelper.getInstance().read(path, DSE_PART_NAME).split("\n");

    final String[] categories = { "node partitioning", "thread partitioning", "node simulation" };

    for (int i = 0; i < arrayDSE.length; i++) {
      final String[] column = arrayDSE[i].split(";");
      for (int j = 0; j < column.length; j++) {
        dataset.addValue(Double.valueOf(column[j]), categories[j], "Iteration " + (i + 1));
        finalDSE += Double.valueOf(column[j]);
      }
    }
    return dataset;
  }
}
