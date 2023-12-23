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

/**
 * This class represents the third page of the analysis results. It includes visualizations of cumulative resource
 * allocation time trends over iterations.
 *
 * @author orenaud
 */
public class AnalysisPage3 {
  public static final String DSE_PART_NAME = "dse_part_trend.csv";

  static int                    iterationNum     = 0;
  static int                    iterationOptim;
  static String                 path;
  static long                   finalDSE         = 0L;
  static DefaultCategoryDataset iterationDataset = new DefaultCategoryDataset();
  static DefaultCategoryDataset fullDataset      = new DefaultCategoryDataset();

  /**
   * Constructs an AnalysisPage3 object with the given parameters.
   *
   * @param path
   *          The file path for reading data.
   * @param iterationOptim
   *          The iteration for optimization.
   */
  public AnalysisPage3(String path, int iterationOptim) {
    AnalysisPage3.path = path;
    AnalysisPage3.iterationOptim = iterationOptim;
  }

  /**
   * Executes the analysis and returns a JPanel containing visualizations.
   *
   * @return A JPanel with cumulative resource allocation time visualizations.
   */
  public JPanel execute() {
    final JPanel panel = new JPanel();
    fillDSEpartDataSet();

    final JLabel descriptionLabel = new JLabel(description());
    descriptionLabel.setForeground(Color.darkGray);
    descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
    descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
    final Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    descriptionLabel.setBorder(border);
    descriptionLabel.setPreferredSize(descriptionLabel.getPreferredSize());
    panel.add(descriptionLabel, BorderLayout.NORTH);

    final JFreeChart chart1 = barChart(fullDataset, "Full Cumulative Resource Allocation Time", "Process");
    final JFreeChart chart2 = barChart(iterationDataset, "Cumulative Resource Allocation Time over Iteration",
        "Iteration");
    panel.setBackground(Color.white);
    panel.setLayout(new GridLayout(3, 1));
    panel.add(new ChartPanel(chart1));
    panel.add(new ChartPanel(chart2));

    return panel;
  }

  /**
   * Generates the description text for the analysis page.
   *
   * @return The description text in HTML format.
   */
  private String description() {
    return "<html>This chart gives an idea of the impact of the efficiency of the application"
        + " graph distribution on your set of nodes via the SimSDP method.<br>" + " The method has iterated over <b>"
        + iterationNum + "</b> iterations, " + "and here is the resource allocation time for each iteration.<br>"

        + "The graph shows the resource allocation process time over iterations.<br><br>"
        + "The optimal configuration is achieved with the following attributes: <br>" + "- Iteration: " + iterationOptim
        + " <br>" + " - Cumulative resource allocation time: " + finalDSE + " ms <br>" + " - Formatted duration:"
        + formatDuration() + "</html>";
  }

  /**
   * Formats the duration in milliseconds into a human-readable string.
   *
   * @return The formatted duration string.
   */
  private String formatDuration() {
    final long milliseconds = finalDSE;
    final long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
    final long hours = TimeUnit.MILLISECONDS.toHours(milliseconds) % 24;
    final long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60;
    final long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;

    return String.format("%d days %02d hours %02d min %02d sec", days, hours, minutes, seconds);
  }

  /**
   * Creates a JFreeChart for a stacked bar chart with a given dataset.
   *
   * @param dataset
   *          The default category dataset.
   * @param yAxis
   *          the chart y axis title
   * @param title
   *          the chart title
   * @return The configured JFreeChart.
   */
  private JFreeChart barChart(DefaultCategoryDataset dataset, String title, String yAxis) {
    JFreeChart chart;
    chart = ChartFactory.createStackedBarChart(title, yAxis, "Resource allocation time (ms)", dataset,
        PlotOrientation.HORIZONTAL, true, true, false);
    chart.getLegend().setPosition(RectangleEdge.RIGHT);

    final Plot plot = chart.getPlot();
    plot.setBackgroundPaint(Color.white);
    final CategoryPlot catplot = chart.getCategoryPlot();
    final StackedBarRenderer renderer = (StackedBarRenderer) catplot.getRenderer();
    renderer.setBarPainter(new StandardBarPainter());
    return chart;
  }

  /**
   * Fills the dataset with cumulative resource allocation time values.
   *
   */
  private static void fillDSEpartDataSet() {
    finalDSE = 0L;

    final String[] line = PreesmIOHelper.getInstance().read(path, DSE_PART_NAME).split("\n");
    Double cumul = 0d;
    for (int i = 1; i < line.length; i++) {
      final String[] column = line[i].split(";");
      final String[] type = column[0].split(":");
      if (!type[0].equals("initialisation")) {
        iterationDataset.addValue(Double.valueOf(column[1]), type[0],
            "Iteration " + type[1] + " Configuration" + type[2]);
        cumul += Double.valueOf(column[1]);
        if (type[0].equals("simulation")) {
          finalDSE += cumul;
          fullDataset.addValue(cumul, "Iteration " + type[1] + " Configuration" + type[2], "");
          cumul = 0d;
        }
      } else {
        fullDataset.addValue(Double.valueOf(column[1]), type[0], "");
        finalDSE += Double.valueOf(column[1]);
      }

    }

  }
}
