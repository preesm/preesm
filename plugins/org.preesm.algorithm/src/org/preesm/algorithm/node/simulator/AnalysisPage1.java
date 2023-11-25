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

public class AnalysisPage1 {
  public static final String WORKLOAD_NAME = "workload_trend.csv";
  public static final String LATENCY_NAME  = "latency_trend.csv";
  static int                 iterationNum  = 0;
  static int                 iterationOptim;
  static Double              latencyOptim  = 0d;
  static Double              workloadOptim = 0d;
  static String              path;

  public AnalysisPage1(String path, int iterationOptim) {
    AnalysisPage1.path = path;
    AnalysisPage1.iterationOptim = iterationOptim;
  }

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

  private String description() {
    return "<html>This chart gives an idea of the impact of the efficiency of the application"
        + " graph distribution on your set of nodes via the SimSDP method.<br>" + " The method has iterated over <b>"
        + iterationNum + "</b> iterations, "
        + "and here is the performance evaluation at inter-node level for each iteration.<br>"

        + "The upper graph show the the internode partitioning based on the computation "
        + "of the workload standard deviation.<br>"
        + "Given that the workload is represented as a percentage, in a well-balanced workload, "
        + "the deviation tends to approach zero.<br>"
        + "Such as: &sigma; = &radic;( (1/N) &sum; (x<sub>i</sub> - &mu;)<sup>2</sup> ) <br>"
        + "Where N is the number of nodes, x<sub>i</sub> is the % of load per node,"
        + " &mu is the average of % of load.<br>"
        + "The lower graph shows the simulated latency of your application execution<br><br>"
        + "The optimal configuration is achieved with the following attributes: <br>" + "- Iteration: " + iterationOptim
        + " <br>" + " - Latency: " + latencyOptim + " cycles <br>" + "- Inter-node Workload Deviation: "
        + String.format("%.2f", workloadOptim) + " %" + "</html>";
  }

  private JFreeChart doubleAxisChart(XYSeries series1, XYSeries series2) {
    final XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series1);
    // Create the first Y axis
    final JFreeChart chart = ChartFactory.createXYLineChart("Dual Axis Chart", "Iteration", "Throughput", dataset);

    chart.setBackgroundPaint(Color.white);
    chart.getTitle().setPaint(Color.black);

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

  private static XYSeries fillWorkloadDeviationDataSet() {
    final String[] arrayWorkload = PreesmIOHelper.getInstance().read(path, WORKLOAD_NAME).split("\n");
    workloadOptim = 0d;
    final XYSeries serie = new XYSeries("Workload Deviation");
    for (int i = 0; i < arrayWorkload.length; i++) {
      serie.add(i, Double.valueOf(arrayWorkload[i]));
    }
    workloadOptim = Double.valueOf(arrayWorkload[iterationOptim]);
    return serie;
  }

  private static XYSeries fillLatencyDataSet() {
    final String[] arrayLatency = PreesmIOHelper.getInstance().read(path, LATENCY_NAME).split("\n");
    final XYSeries serie = new XYSeries("Latency");
    Double minLatency = Double.MAX_VALUE;
    iterationNum = arrayLatency.length;
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
