package org.preesm.algorithm.node.partitioner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import org.eclipse.core.runtime.IProgressMonitor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

@PreesmTask(id = "DeviationChartTask.identifier", name = "Deviation chart exporter",
    category = "Deviation chart exporters")
public class DeviationChartTask extends AbstractTaskImplementation {
  public static final String WORKLOAD_NAME   = "workload_trend.csv";
  public static final String LATENCY_NAME    = "latency_trend.csv";
  public static final String OCCUPATION_NAME = "occupation_trend.csv";
  public static final String SPEEDUP_NAME    = "speedup_trend.csv";
  public static final String DSE_NAME        = "dse_trend.csv";
  public static final String DSE_PART_NAME   = "dse_part_trend.csv";

  public static final String PAGE1_TITRE1 = "Internode Workload Standard Deviation over Iteration";
  public static final String PAGE1_TITRE2 = "Internode Latency over Iteration";
  public static final String PAGE2_TITRE1 = "Intranode Occupation over Iteration";
  public static final String PAGE2_TITRE2 = "Intranode Speedups over Iteration";
  public static final String PAGE3_TITRE1 = "Final Resource Allocation Time over Iteration";
  public static final String PAGE3_TITRE2 = "Cumulative Resource Allocation Time over Iteration";

  public static final String PAGE1 = "Internode Analysis";
  public static final String PAGE2 = "Intranode Analysis";
  public static final String PAGE3 = "DSE Analysis";

  static String       path            = "";
  static int          iterationNum    = 0;
  static int          iterationOptim  = 0;
  static Double       latencyOptim    = 0d;
  static Double       workloadOptim   = 0d;
  static List<Double> occupationOptim = new ArrayList<>();
  static List<Double> speedupOptim    = new ArrayList<>();
  static Double       finalDSE        = 0d;

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    // read files
    path = "/" + workflow.getProjectName() + "/Scenarios/generated/";

    final JFrame frame = createFrame();
    final JTabbedPane tabbedPane = new JTabbedPane();

    final JPanel tab1 = createTab(PAGE1_TITRE1, PAGE1_TITRE2);
    final JPanel tab2 = createTab(PAGE2_TITRE1, PAGE2_TITRE2);
    final JPanel tab3 = createTab(PAGE3_TITRE1, PAGE3_TITRE2);

    addTab(tabbedPane, PAGE1, tab1, "Description de la page 1");
    addTab(tabbedPane, PAGE2, tab2, "Description de la page 2");
    addTab(tabbedPane, PAGE3, tab3, "Description de la page 3");

    frame.add(tabbedPane);
    frame.setVisible(true);

    return new LinkedHashMap<>();
  }

  private static JFrame createFrame() {
    final JFrame frame = new JFrame("SimSDP Curve");
    frame.setSize(1000, 800);
    frame.setLocationRelativeTo(null);
    return frame;
  }

  private static void addTab(JTabbedPane tabbedPane, String title, JPanel panel, String tooltip) {
    tabbedPane.addTab(title, panel);
    if (tooltip != null) {
      tabbedPane.setToolTipTextAt(tabbedPane.indexOfComponent(panel), tooltip);
    }
  }

  private static JPanel createTab(String chartTitle1, String chartTitle2) {
    final JPanel panel = new JPanel();
    final XYSeriesCollection dataset1 = fillDataSet(chartTitle1);
    DefaultCategoryDataset dataset3 = null;
    XYSeriesCollection dataset2 = null;
    if (chartTitle2.equals(PAGE3_TITRE2)) {
      dataset3 = fillDataSet(chartTitle2, 3);
    } else {
      dataset2 = fillDataSet(chartTitle2);
    }
    String yAxisName1;
    String yAxisName2;
    yAxisName1 = getYAxisName(chartTitle1);
    String description = getDescription(chartTitle1);
    createChart(panel, chartTitle1, yAxisName1, dataset1, description);

    yAxisName2 = getYAxisName(chartTitle2);
    description = getDescription(chartTitle2);
    if (chartTitle2.equals(PAGE3_TITRE2)) {
      createChart(panel, chartTitle2, yAxisName2, dataset3, description);
    } else {
      createChart(panel, chartTitle2, yAxisName2, dataset2, description);
    }
    return panel;
  }

  private static String getYAxisName(String chartTitle) {
    // Define Y-axis name based on chart title
    if (chartTitle.equals(PAGE1_TITRE1)) {
      return "Deviation from average (%)";
    }
    if (chartTitle.equals(PAGE1_TITRE2)) {
      return "Latency (cycle)";
    }
    if (chartTitle.equals(PAGE2_TITRE1)) {
      return "Occupation per operator (%)";
    }
    if (chartTitle.equals(PAGE2_TITRE2)) {
      return "Speedup (%)";
    }
    if (chartTitle.equals(PAGE3_TITRE1) || chartTitle.equals(PAGE3_TITRE2)) {
      return "Resource allocation time (s)";
    }
    return "";
  }

  private static String getDescription(String chartTitle) {
    // Define description based on chart title
    String description = "<html>This chart gives an idea of the impact of the efficiency of the application"
        + " graph distribution on your set of nodes via the SimSDP method.<br>" + " The method has iterated over <b>"
        + iterationNum + "</b> iterations, ";
    final String optimStr = "The optimal configuration is achieved with the following attributes: <br>";
    if (chartTitle.equals(PAGE1_TITRE1)) {
      description += "and here is the performance evaluation at inter-node level for each iteration.<br>"

          + "The upper graph show the the internode partitioning based on the computation "
          + "of the workload standard deviation.<br>"
          + "Given that the workload is represented as a percentage, in a well-balanced workload, "
          + "the deviation tends to approach zero.<br>"
          + "Such as: &sigma; = &radic;( (1/N) &sum; (x<sub>i</sub> - &mu;)<sup>2</sup> ) <br>"
          + "Where N is the number of nodes, x<sub>i</sub> is the % of load per node,"
          + " &mu is the average of % of load.<br>"
          + "The lower graph shows the simulated latency of your application execution<br><br>"

          + optimStr + "- Iteration: " + iterationOptim + " <br>" + " - Latency: " + latencyOptim + " cycles <br>"
          + "- Inter-node Workload Deviation: " + String.format("%.2f", workloadOptim) + " %";
    } else if (chartTitle.equals(PAGE2_TITRE1)) {
      description += "and here is the performance evaluation at intra-node level for each iteration.<br>"

          + "The upper graph shows the percentage of occupancy per operator (node) over iterations.<br>"
          + "The lower graph shows the the possible speed-ups per node"
          + " and compare them to th achieved speed-ups over iterations.<br><br>" + optimStr + "- Iteration: "
          + iterationOptim + " <br>" + " - Occupation: ";
      for (int i = 0; i < occupationOptim.size(); i++) {
        description += " Node" + i + "->" + String.format("%.2f", occupationOptim.get(i)) + " %";
        if (i < occupationOptim.size() - 1) {
          description += ", ";
        }
      }
      description += "  <br>" + "- Speed-ups: ";
      int nodeIndex = 0;
      for (int i = 0; i < speedupOptim.size(); i++) {
        if (i % 2 == 0) {
          description += " Node" + nodeIndex + "-> current: " + String.format("%.2f", speedupOptim.get(i)) + " %";
          nodeIndex++;
        } else {
          description += "max: " + String.format("%.2f", speedupOptim.get(i)) + " %";
        }
        if (i < speedupOptim.size() - 1) {
          description += ", ";
        }

      }

    } else {
      description += "and here is the resource allocation time for each iteration.<br>"

          + "The graph shows the resource allocation process time over iterations.<br><br>" + optimStr + "- Iteration: "
          + iterationOptim + " <br>" + " - Cumulative resource allocation time: " + finalDSE + " second <br>";
    }
    description += "</html>";
    return description;
  }

  private static void createChart(JPanel panel, String chartTitle, String yAxisName, XYSeriesCollection dataset,
      String description) {
    JFreeChart chart;

    chart = ChartFactory.createScatterPlot(chartTitle, "Iteration", yAxisName, dataset);

    chart.getLegend().setPosition(RectangleEdge.RIGHT);

    final XYPlot plot = (XYPlot) chart.getPlot();
    plot.setBackgroundPaint(Color.white);
    final NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
    xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    panel.setBackground(Color.white);
    panel.setLayout(new GridLayout(3, 1));
    addDescriptionLabel(panel, description,
        chartTitle.equals(PAGE1_TITRE1) || chartTitle.equals(PAGE2_TITRE1) || chartTitle.equals(PAGE3_TITRE1));
    panel.add(new ChartPanel(chart));
  }

  private static void createChart(JPanel panel, String chartTitle, String yAxisName, DefaultCategoryDataset dataset,
      String description) {
    JFreeChart chart;
    chart = createStackedBarChart(chartTitle, "Iteration", yAxisName, dataset);
    chart.getLegend().setPosition(RectangleEdge.RIGHT);

    final Plot plot = chart.getPlot();
    plot.setBackgroundPaint(Color.white);
    final CategoryPlot catplot = chart.getCategoryPlot();
    final StackedBarRenderer renderer = (StackedBarRenderer) catplot.getRenderer();
    renderer.setBarPainter(new StandardBarPainter());

    panel.setBackground(Color.white);
    panel.setLayout(new GridLayout(3, 1));
    addDescriptionLabel(panel, description,
        chartTitle.equals(PAGE1_TITRE1) || chartTitle.equals(PAGE2_TITRE1) || chartTitle.equals(PAGE3_TITRE1));
    panel.add(new ChartPanel(chart));
  }

  private static JFreeChart createStackedBarChart(String chartTitle, String xAxisLabel, String yAxisLabel,
      DefaultCategoryDataset dataset) {
    return ChartFactory.createStackedBarChart(chartTitle, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL,
        true, true, false);
  }

  private static void addDescriptionLabel(JPanel panel, String description, boolean b) {
    if (b) {

      final JLabel descriptionLabel = new JLabel(description);
      descriptionLabel.setForeground(Color.darkGray);
      descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
      descriptionLabel.setVerticalAlignment(SwingConstants.TOP);

      final Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
      descriptionLabel.setBorder(border);

      descriptionLabel.setPreferredSize(descriptionLabel.getPreferredSize());
      panel.add(descriptionLabel, BorderLayout.NORTH);

    }
  }

  private static XYSeriesCollection fillDataSet(String chartTitle) {
    final XYSeriesCollection dataset = new XYSeriesCollection();
    if (chartTitle.equals(PAGE1_TITRE1)) {
      fillWorkloadDeviationDataSet(dataset);
    } else if (chartTitle.equals(PAGE1_TITRE2)) {
      fillLatencyDataSet(dataset);
    } else if (chartTitle.equals(PAGE2_TITRE1)) {
      fillOccupationDataSet(dataset);
    } else if (chartTitle.equals(PAGE2_TITRE2)) {
      fillSpeedupDataSet(dataset);
    } else if (chartTitle.equals(PAGE3_TITRE1)) {
      fillDSEDataSet(dataset);
    }
    return dataset;
  }

  private static DefaultCategoryDataset fillDataSet(String chartTitle, int i) {

    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    if (chartTitle.equals(PAGE3_TITRE2)) {
      fillDSEpartDataSet(dataset);
    }
    return dataset;
  }

  private static void fillDSEpartDataSet(DefaultCategoryDataset dataset) {
    final String[] arrayDSE = PreesmIOHelper.getInstance().read(path, DSE_PART_NAME).split("\n");

    final String[] categories = { "node partitioning", "thread partitioning", "node simulation" };

    for (int i = 0; i < arrayDSE.length; i++) {
      final String[] column = arrayDSE[i].split(";");
      for (int j = 0; j < column.length; j++) {
        dataset.addValue(Double.valueOf(column[j]), categories[j], "Iteration " + (i + 1));
      }
    }
  }

  private static void fillDSEDataSet(XYSeriesCollection dataset) {
    final String[] arrayDSE = PreesmIOHelper.getInstance().read(path, DSE_NAME).split("\n");
    finalDSE = 0d;
    final XYSeries serie = new XYSeries("Resource allocation time");
    for (int i = 0; i < arrayDSE.length; i++) {
      serie.add(i, Double.valueOf(arrayDSE[i]));
      finalDSE += Double.valueOf(arrayDSE[i]);
    }
    dataset.addSeries(serie);

  }

  private static void fillWorkloadDeviationDataSet(XYSeriesCollection dataset) {
    final String[] arrayWorkload = PreesmIOHelper.getInstance().read(path, WORKLOAD_NAME).split("\n");
    workloadOptim = 0d;
    final XYSeries serie = new XYSeries("Workload Deviation");
    for (int i = 0; i < arrayWorkload.length; i++) {
      serie.add(i, Double.valueOf(arrayWorkload[i]));
    }
    workloadOptim = Double.valueOf(arrayWorkload[iterationOptim]);
    dataset.addSeries(serie);
  }

  private static void fillLatencyDataSet(XYSeriesCollection dataset) {
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
    dataset.addSeries(serie);
  }

  private static void fillOccupationDataSet(XYSeriesCollection dataset) {
    final String[] arrayOccupation = PreesmIOHelper.getInstance().read(path, OCCUPATION_NAME).split("\n");
    String[] column = arrayOccupation[0].split(";");
    final List<XYSeries> series = new ArrayList<>();
    for (int i = 0; i < column.length; i++) {
      final XYSeries serie = new XYSeries("Occupation on Node" + i);
      serie.add(0, Double.valueOf(column[i]));
      series.add(serie);
      dataset.addSeries(serie);
    }
    occupationOptim.clear();
    for (int i = 1; i < arrayOccupation.length; i++) {
      column = arrayOccupation[i].split(";");
      for (int j = 0; j < column.length; j++) {
        series.get(j).add(i, Double.valueOf(column[j]));
        if (i == iterationOptim) {
          occupationOptim.add(Double.valueOf(column[j]));
        }

      }
    }
  }

  private static void fillSpeedupDataSet(XYSeriesCollection dataset) {
    final String[] arrayOccupation = PreesmIOHelper.getInstance().read(path, SPEEDUP_NAME).split("\n");
    String[] column = arrayOccupation[0].split(";");
    final List<XYSeries> series = new ArrayList<>();
    int nodeIndex = 0;
    for (int i = 0; i < column.length; i++) {
      if (i % 2 == 0) {
        final XYSeries serie = new XYSeries("Currently Obtained Speedup on Node " + nodeIndex);
        serie.add(0, Double.valueOf(column[i]));
        series.add(serie);
        dataset.addSeries(serie);
      } else {
        final XYSeries serie = new XYSeries("Maximum Achievable Speedup on Node " + nodeIndex);
        serie.add(0, Double.valueOf(column[i]));
        series.add(serie);
        dataset.addSeries(serie);
        nodeIndex++;
      }
    }
    speedupOptim.clear();
    for (int i = 1; i < arrayOccupation.length; i++) {
      column = arrayOccupation[i].split(";");
      for (int j = 0; j < column.length; j++) {
        series.get(j).add(i, Double.valueOf(column[j]));
        if (i == iterationOptim) {
          speedupOptim.add(Double.valueOf(column[j]));
        }
      }
    }
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return new LinkedHashMap<>();
  }

  @Override
  public String monitorMessage() {
    return "Generate chart of multinode scheduling.";
  }
}
