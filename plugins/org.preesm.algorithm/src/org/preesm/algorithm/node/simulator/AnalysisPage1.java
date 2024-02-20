package org.preesm.algorithm.node.simulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.preesm.commons.files.PreesmIOHelper;

/**
 * This class represents the first page of the analysis results. It includes visualizations of workload deviation trends
 * and simulated latency over iterations.
 *
 * @author orenaud
 */
public class AnalysisPage1 {
  private static final String WORKLOAD_NAME = "std_trend.csv";
  private static final String LATENCY_NAME  = "latency_trend.csv";
  private int                 iterationOptim;
  private Double              latencyOptim  = 0d;
  private Double              workloadOptim = 0d;
  private final String        path;

  /**
   * Constructs an AnalysisPage1 object with the given parameters.
   *
   * @param path
   *          The file path for reading data.
   * @param iterationOptim
   *          The iteration for optimization.
   */
  public AnalysisPage1(String path, int iterationOptim) {
    this.path = path;
    this.iterationOptim = iterationOptim;
  }

  /**
   * Executes the analysis and returns a JPanel containing visualizations.
   *
   * @return A JPanel with workload deviation and latency visualizations.
   */
  public JPanel execute() {
    final JPanel panel = new JPanel();
    final XYSeries series1 = fillLatencyDataSet();
    final XYSeries series2 = fillWorkloadDeviationDataSet();

    final JLabel descriptionLabel = new JLabel(description());
    descriptionLabel.setForeground(Color.darkGray);
    descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
    descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
    final Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    descriptionLabel.setBorder(border);
    descriptionLabel.setPreferredSize(descriptionLabel.getPreferredSize());
    panel.add(descriptionLabel, BorderLayout.NORTH);

    final JFreeChart chart = doubleAxisChart(series1, series2);
    panel.setBackground(Color.white);
    panel.setLayout(new GridLayout(2, 1));
    panel.add(new ChartPanel(chart));

    return panel;
  }

  /**
   * Gets the best iteration.
   *
   * @return The optimal iteration.
   */
  public int getIterationOptim() {
    return iterationOptim;
  }

  /**
   * Generates the description text for the analysis page.
   *
   * @return The description text in HTML format.
   */
  private String description() {

    return """
        <html>
          <p align="justify">
            This chart gives an idea of the impact of the efficiency of the
            application graph distribution on your set of nodes via the SimSDP method.<br>
            The method has iterated over <b> %d </b> iterations, and here is the
            performance evaluation at inter-node level for each iteration.<br>

            The upper graph show the the internode partitioning based on the computation of the
            workload standard deviation.<br>
            Given that the workload is represented as a percentage, in a well-balanced workload,
            the deviation tends to approach zero.<br>
            Such as: &sigma; = &radic;( (1/N) &sum; (x<sub>i</sub> - &mu;)<sup>2</sup> ) <br>
            Where N is the number of nodes, x<sub>i</sub> is the %% of load per node, &mu is
            the average of %% of load.<br>
            The lower graph shows the simulated latency of your application execution<br><br>
            The optimal configuration is achieved with the following attributes: <br>
            - Iteration: %d <br>
            - Latency: %.0f cycles <br>
            - Inter-node Workload Deviation: %.2f %%
          </p>
        </html>
            """.formatted(iterationOptim, iterationOptim, latencyOptim, workloadOptim);
  }

  /**
   * Creates a JFreeChart with two Y-axes for workload deviation and throughput trends.
   *
   * @param series1
   *          The series for workload deviation.
   * @param series2
   *          The series for throughput.
   * @return The configured JFreeChart.
   */
  private JFreeChart doubleAxisChart(XYSeries series1, XYSeries series2) {
    final XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series1);
    // Create the first Y axis
    final JFreeChart chart = ChartFactory.createXYLineChart("Workload Deviation and Throughput Trends over Iteration",
        "Iteration", "Throughput", dataset);

    chart.setBackgroundPaint(Color.white);
    chart.getTitle().setPaint(Color.black);
    chart.getXYPlot().setBackgroundPaint(Color.white);
    chart.getXYPlot().setDomainGridlinePaint(Color.lightGray);

    // Create and configure the second dataset
    chart.getXYPlot().setDataset(1, new XYSeriesCollection(series2));
    chart.getXYPlot().mapDatasetToRangeAxis(1, 1);

    // Create the second Y axis
    final NumberAxis yAxis2 = new NumberAxis("Workload");
    yAxis2.setAutoRangeIncludesZero(false);
    chart.getXYPlot().setRangeAxis(1, yAxis2);
    chart.getXYPlot().setRenderer(1, new XYLineAndShapeRenderer());
    return chart;
  }

  /**
   * Fills the dataset with workload deviation values.
   *
   * @return The XYSeries containing workload deviation data.
   */
  private XYSeries fillWorkloadDeviationDataSet() {
    final String[] arrayWorkload = PreesmIOHelper.getInstance().read(path, WORKLOAD_NAME).split("\n");
    workloadOptim = 0d;
    final XYSeries serie = new XYSeries("Workload Deviation");
    for (int i = 0; i < arrayWorkload.length; i++) {
      serie.add(i, Double.valueOf(arrayWorkload[i]));
    }
    workloadOptim = Double.valueOf(arrayWorkload[iterationOptim]);
    return serie;
  }

  /**
   * Fills the dataset with latency values.
   *
   * @return The XYSeries containing latency data.
   */
  private XYSeries fillLatencyDataSet() {
    final String[] arrayLatency = PreesmIOHelper.getInstance().read(path, LATENCY_NAME).split("\n");
    final XYSeries serie = new XYSeries("Latency");
    Double minLatency = Double.MAX_VALUE;
    for (int i = 0; i < arrayLatency.length; i++) {
      serie.add(i, Double.valueOf(arrayLatency[i]));
      if (Double.valueOf(arrayLatency[i]) < minLatency) {
        minLatency = Double.valueOf(arrayLatency[i]);
        iterationOptim = i;
        latencyOptim = minLatency;
      }
    }
    return serie;
  }
}
