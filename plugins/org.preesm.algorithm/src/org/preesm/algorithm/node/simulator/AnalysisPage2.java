package org.preesm.algorithm.node.simulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.preesm.commons.files.PreesmIOHelper;

/**
 * This class represents the second page of the analysis results. It includes visualizations of intra-node occupation
 * and speedup trends over iterations.
 *
 * @author orenaud
 */
public class AnalysisPage2 {
  static String              path;
  static int                 iterationOptim;
  static int                 iterationNum    = 0;
  public static final String OCCUPATION_NAME = "occupation_trend.csv";
  public static final String SPEEDUP_NAME    = "speedup_trend.csv";
  static List<Double>        occupationOptim = new ArrayList<>();
  static List<Double>        speedupOptim    = new ArrayList<>();
  public static final String PAGE2_TITRE1    = "Intranode Occupation over Iteration";
  public static final String PAGE2_TITRE2    = "Intranode Speedups over Iteration";
  public static final String ITERATION       = "Iteration ";

  /**
   * Constructs an AnalysisPage2 object with the given parameters.
   *
   * @param path
   *          The file path for reading data.
   * @param iterationOptim
   *          The iteration for optimization.
   */
  public AnalysisPage2(String path, int iterationOptim) {
    AnalysisPage2.path = path;
    AnalysisPage2.iterationOptim = iterationOptim;
  }

  /**
   * Executes the analysis and returns a JPanel containing visualizations.
   *
   * @return A JPanel with intra-node occupation and speedup visualizations.
   */
  public JPanel execute() {
    final JPanel panel = new JPanel();
    final CategoryDataset series1 = fillOccupationDataSet();
    final CategoryDataset series2 = fillSpeedupDataSet();

    final JLabel descriptionLabel = new JLabel(description());
    descriptionLabel.setForeground(Color.darkGray);
    descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
    descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
    final Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    descriptionLabel.setBorder(border);
    descriptionLabel.setPreferredSize(descriptionLabel.getPreferredSize());
    panel.add(descriptionLabel, BorderLayout.NORTH);

    final JFreeChart chart1 = barChart(series1, PAGE2_TITRE1, "Occupation per operator (%)");
    final JFreeChart chart2 = barChart(series2, PAGE2_TITRE2, "Latency Speedup (%)");
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
    final StringConcatenation description = new StringConcatenation();
    description.append("<html>This chart gives an idea of the impact of the efficiency of the application"
        + " graph distribution on your set of nodes via the SimSDP method.<br>" + " The method has iterated over <b>"
        + iterationNum + "</b> iterations, ");

    description.append("and here is the performance evaluation at intra-node level for each iteration.<br>"

        + "The upper graph shows the percentage of occupancy per operator (node) over iterations.<br>"
        + "The lower graph shows the the possible speed-ups per node"
        + " and compare them to th achieved speed-ups over iterations.<br><br>"
        + "The optimal configuration is achieved with the following attributes: <br>" + "- Iteration: " + iterationOptim
        + " <br>" + " - Occupation: ");
    for (int i = 0; i < occupationOptim.size(); i++) {
      description.append(" Node" + i + "->" + String.format("%.2f", occupationOptim.get(i)) + " %");
      if (i < occupationOptim.size() - 1) {
        description.append(", ");
      }
    }
    description.append("  <br>" + "- Speed-ups: ");
    int nodeIndex = 0;
    for (int i = 0; i < speedupOptim.size(); i++) {
      if (i % 2 == 0) {
        description.append(" Node" + nodeIndex + "-> current: " + String.format("%.2f", speedupOptim.get(i)) + " %");
        nodeIndex++;
      } else {
        description.append("max: " + String.format("%.2f", speedupOptim.get(i)) + " %");
      }
      if (i < speedupOptim.size() - 1) {
        description.append(", ");
      }

    }
    description.append("</html>");
    return description.toString();
  }

  /**
   * Creates a JFreeChart for a bar chart with a given dataset, chart title, and y-axis label.
   *
   * @param dataset
   *          The category dataset.
   * @param chartTitle
   *          The title of the chart.
   * @param yAxisLabel
   *          The label for the y-axis.
   * @return The configured JFreeChart.
   */
  private JFreeChart barChart(CategoryDataset dataset, String chartTitle, String yAxisLabel) {
    final String xAxisLabel = "Iteration";

    final JFreeChart chart = ChartFactory.createBarChart(chartTitle, xAxisLabel, yAxisLabel, dataset,
        PlotOrientation.VERTICAL, true, true, false);
    final CategoryPlot plot = chart.getCategoryPlot();
    plot.setBackgroundPaint(Color.white);
    plot.setDomainGridlinePaint(Color.lightGray);
    plot.setRangeGridlinePaint(Color.lightGray);
    final BarRenderer renderer = (BarRenderer) plot.getRenderer();
    renderer.setBarPainter(new StandardBarPainter());
    return chart;
  }

  /**
   * Fills the dataset with intra-node occupation values.
   *
   * @return The CategoryDataset containing intra-node occupation data.
   */
  private static CategoryDataset fillOccupationDataSet() {
    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    occupationOptim.clear();
    final String[] arrayOccupation = PreesmIOHelper.getInstance().read(path, OCCUPATION_NAME).split("\n");
    for (int i = 0; i < arrayOccupation.length; i++) {
      final String[] column = arrayOccupation[i].split(";");
      for (int j = 0; j < column.length; j++) {
        dataset.addValue(Double.valueOf(column[j]), "Occupation on Node" + j, ITERATION + (i + 1));
        if (i == iterationOptim) {
          occupationOptim.add(Double.valueOf(column[j]));
        }
      }
    }
    return dataset;
  }

  /**
   * Fills the dataset with intra-node speedup values.
   *
   * @return The CategoryDataset containing intra-node speedup data.
   */
  private static CategoryDataset fillSpeedupDataSet() {
    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    speedupOptim.clear();
    final String[] arraySpeedup = PreesmIOHelper.getInstance().read(path, SPEEDUP_NAME).split("\n");
    for (int i = 0; i < arraySpeedup.length; i++) {
      final String[] column = arraySpeedup[i].split(";");
      int nodeIndex = 0;
      for (int j = 0; j < column.length; j++) {
        if (j % 2 == 0) {
          dataset.addValue(Double.valueOf(column[j]), "Currently Obtained Speedup on Node" + nodeIndex,
              ITERATION + (i + 1));

        } else {
          dataset.addValue(Double.valueOf(column[j]), "Maximum Achievable Speedup on Node" + nodeIndex,
              ITERATION + (i + 1));
          nodeIndex++;
        }
        if (i == iterationOptim) {
          speedupOptim.add(Double.valueOf(column[j]));
        }

      }
    }

    return dataset;
  }

}
