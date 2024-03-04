package org.preesm.algorithm.node.simulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.preesm.commons.files.PreesmIOHelper;

/**
 * This class represents the fourth page of the analysis results. It includes visualizations of a radar chart comparing
 * different network topologies.
 *
 * @author orenaud
 */
public class AnalysisPage4 {

  private final String        path;
  private static final String NET_NAME = "multicriteria.csv";

  private final List<NetworkInfo> networkInfoList       = new ArrayList<>();
  private final List<NetworkInfo> networkInfoNormalList = new ArrayList<>();

  Map<Integer, Map<Integer, Map<Integer, List<NetworkInfo>>>> nodeNetworkInfoNormalMap = new LinkedHashMap<>();

  int nodeKey          = 0;
  int coreKey          = 0;
  int coreFrequencyKey = 0;

  private static final String DESCRIPTION = """
      <html>
      This chart gives a comprehensive comparison of the 5 main network topologies
      (if they exist) concerning a selected number of nodes, dynamically chosen from a ComboBox.<br>
      The topologies are: Cluster with a Crossbar,  Cluster with a Shared Backbone, Torus Cluster,
      Fat-Tree Cluster and Dragonfly Cluster.<br>
      This visualisation captures the normalised performance metrics of
      Throughput, Memory, Energy, and Cost for each network configuration.<br>
      Normalised value = (x - minvalue) / (maxvalue - minvalue)
      </html>
      """;

  /**
   * Constructs an AnalysisPage4 object with the given parameters.
   *
   * @param path
   *          The file path for reading data.
   */
  public AnalysisPage4(String path) {
    this.path = path;
  }

  /**
   * Executes the analysis and returns a JPanel containing radar chart visualizations.
   *
   * @return A JPanel with radar chart visualizations.
   */
  public JPanel execute() {

    final JPanel panel = new JPanel();
    fillNetworkInfoList();
    nodeKey = networkInfoList.get(0).getNode();
    coreKey = networkInfoList.get(0).getCore();
    coreFrequencyKey = networkInfoList.get(0).getCoreFrequency();
    networkInfoNormalList.clear();
    fillNetworkInfoNormalList();
    nodeNetworkInfoNormalMap.clear();
    fillNodeNetworkInfoNormalMap();

    final XYSeriesCollection dataset = fillMultiCriteriaDataSet();

    final JFreeChart chart = polarChart(dataset);
    panel.setBackground(Color.white);
    panel.setLayout(new BorderLayout());

    final JLabel descriptionLabel = new JLabel(DESCRIPTION);
    descriptionLabel.setForeground(Color.darkGray);
    descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
    descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
    final Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    descriptionLabel.setBorder(border);
    panel.add(descriptionLabel, BorderLayout.NORTH);

    panel.add(new ChartPanel(chart));

    final JPanel textFieldPanel = createTextFieldPanel(chart, dataset);

    panel.add(textFieldPanel, BorderLayout.SOUTH);

    return panel;
  }

  /**
   * Fills the nodeNetworkInfoNormalMap with network information for each node.
   */
  private void fillNodeNetworkInfoNormalMap() {
    for (final NetworkInfo network : networkInfoNormalList) {
      final List<NetworkInfo> newList = new ArrayList<>();
      newList.add(network);
      final Map<Integer, List<NetworkInfo>> newMap = new LinkedHashMap<>();
      newMap.put(network.getCoreFrequency(), newList);
      final Map<Integer, Map<Integer, List<NetworkInfo>>> newMapMap = new LinkedHashMap<>();
      newMapMap.put(network.getCore(), newMap);

      if (!nodeNetworkInfoNormalMap.containsKey(network.getNode())) {

        nodeNetworkInfoNormalMap.put(network.getNode(), newMapMap);
      } else if (!nodeNetworkInfoNormalMap.get(network.getNode()).containsKey(network.getCore())) {

        nodeNetworkInfoNormalMap.get(network.getNode()).put(network.getCore(), newMap);

      } else if (!nodeNetworkInfoNormalMap.get(network.getNode()).get(network.getCore())
          .containsKey(network.getCoreFrequency())) {
        nodeNetworkInfoNormalMap.get(network.getNode()).get(network.getCore()).put(network.getCoreFrequency(), newList);
      } else if (nodeNetworkInfoNormalMap.get(network.getNode()).get(network.getCore()).get(network.getCoreFrequency())
          .stream().noneMatch(x -> x.getType().equals(network.getType()))) {
        nodeNetworkInfoNormalMap.get(network.getNode()).get(network.getCore()).get(network.getCoreFrequency())
            .add(network);
      }
    }

  }

  /**
   * Creates a JPanel with a ComboBox for selecting the number of nodes for network analysis.
   *
   * @param chart
   *          The radar chart.
   * @param dataset
   *          The dataset for the chart.
   * @return A JPanel with a ComboBox for node selection.
   */
  private JPanel createTextFieldPanel(JFreeChart chart, XYSeriesCollection dataset) {

    final JPanel textFieldPanel = new JPanel();
    textFieldPanel.setLayout(new GridLayout(1, 2, 0, 0));
    final JComboBox<String> archiCombo = addComboBoxWithLabel(textFieldPanel,
        "Select the number of nodes:cores:frequency for network analysis:");
    // node selection

    archiCombo.addActionListener(e -> {
      final String[] key = archiCombo.getSelectedItem().toString().split(":");
      nodeKey = Integer.valueOf(key[0]);
      coreKey = Integer.valueOf(key[1]);
      coreFrequencyKey = Integer.valueOf(key[2]);
      updateDataset(dataset);
      chart.fireChartChanged();
    });

    textFieldPanel.setBackground(Color.white);
    return textFieldPanel;
  }

  private JComboBox<String> addComboBoxWithLabel(JPanel panel, String labelText) {
    final JLabel label = new JLabel(labelText);
    panel.add(label);
    String[] items;
    final List<String> itemList = new ArrayList<>();
    for (final Entry<Integer, Map<Integer, Map<Integer, List<NetworkInfo>>>> node : nodeNetworkInfoNormalMap
        .entrySet()) {
      for (final Entry<Integer, Map<Integer, List<NetworkInfo>>> core : node.getValue().entrySet()) {
        for (final int freq : core.getValue().keySet()) {
          itemList.add(node.getKey() + ":" + core.getKey() + ":" + freq);
        }

      }
    }
    items = itemList.toArray(new String[0]);

    final JComboBox<String> comboBox = new JComboBox<>(items);
    comboBox.setBackground(Color.white);
    panel.add(comboBox);

    return comboBox;
  }

  /**
   * Returns the list of normalized network information.
   *
   * @return The list of normalized network information.
   */
  public List<NetworkInfo> getNetworkInfoNormalList() {
    return networkInfoNormalList;
  }

  public List<NetworkInfo> getnetworkInfoList() {
    return networkInfoList;
  }

  /**
   * Fills the networkInfoNormalList with normalized values.
   */
  private void fillNetworkInfoNormalList() {
    final List<Double> throughputs = extractMetrics(networkInfoList, NetworkInfo::getFinalLatency);
    final List<Double> memorys = extractMetrics(networkInfoList, NetworkInfo::getMemory);
    final List<Double> energys = extractMetrics(networkInfoList, NetworkInfo::getEnergy);
    final List<Double> costs = extractMetrics(networkInfoList, NetworkInfo::getCost);

    final Double thMax = findMax(throughputs);
    Double thMin = findMin(throughputs);
    final Double mMax = findMax(memorys);
    Double mMin = findMin(memorys);
    final Double enerMax = findMax(energys);
    Double enerMin = findMin(energys);
    final Double cMax = findMax(costs);

    thMin = thMin.equals(thMax) ? 0.0 : thMin;
    mMin = mMin.equals(mMax) ? 0.0 : mMin;
    enerMin = enerMin.equals(enerMax) ? 0.0 : enerMin;
    Double cMin = findMin(costs);
    cMin = cMin.equals(cMax) ? 0.0 : cMin;

    for (final NetworkInfo net : networkInfoList) {
      final Double throughput = normalize(net.getFinalLatency(), thMin, thMax);
      final Double memory = normalize(net.getMemory(), mMin, mMax);
      final Double energy = normalize(net.getEnergy(), enerMin, enerMax);
      final Double cost = normalize(net.getCost(), cMin, cMax);
      final NetworkInfo newNetwork = new NetworkInfo(net.getType(), net.getNode(), net.getCore(),
          net.getCoreFrequency(), throughput, memory, energy, cost);
      networkInfoNormalList.add(newNetwork);
    }
  }

  /**
   * Extracts a specific metric from the network information list.
   *
   * @param networkInfoList
   *          The list of network information.
   * @param extractor
   *          The function to extract the metric.
   * @return The list of extracted metrics.
   */
  private List<Double> extractMetrics(List<NetworkInfo> networkInfoList, Function<NetworkInfo, Double> extractor) {
    return networkInfoList.stream().map(extractor).toList();
  }

  /**
   * Finds the maximum value in a list of doubles.
   *
   * @param values
   *          The list of values.
   * @return The maximum value.
   */
  private Double findMax(List<Double> values) {
    return values.stream().max(Double::compareTo).orElse(0.0);
  }

  /**
   * Finds the minimum value in a list of doubles.
   *
   * @param values
   *          The list of values.
   * @return The minimum value.
   */
  private Double findMin(List<Double> values) {
    return values.stream().min(Double::compareTo).orElse(0.0);
  }

  /**
   * Normalizes a value between 0 and 1.
   *
   * @param value
   *          The value to normalize.
   * @param min
   *          The minimum value in the range.
   * @param max
   *          The maximum value in the range.
   * @return The normalized value.
   */
  private Double normalize(Double value, Double min, Double max) {
    return (value - min) / (max - min);
  }

  /**
   * Fills the networkInfoList with data from the input file.
   */
  private void fillNetworkInfoList() {
    final String[] networklist = PreesmIOHelper.getInstance().read(path, NET_NAME).split("\n\n");
    for (final String element : networklist) {

      String type = "";
      int node = 0;
      int core = 0;
      int coreFrequency = 0;
      Double throughput = 0.0;
      Double memory = 0.0;
      Double energy = 0.0;
      Double cost = 0.0;
      final String[] networkArg = element.split("\n");
      for (int indexArg = 0; indexArg < networkArg.length; indexArg++) {
        final String[] column = networkArg[indexArg].split(";");

        switch (column[0]) {
          case "type":
            type = column[1].split(":")[0];
            node = Integer.valueOf(column[1].split(":")[1]);
            core = Integer.valueOf(column[1].split(":")[2]);
            coreFrequency = Integer.valueOf(column[1].split(":")[3]);
            break;
          case "finalLatency":
            throughput = Double.valueOf(column[1]);
            break;
          case "memory":
            memory = Double.valueOf(column[1]);
            break;
          case "energy":
            energy = Double.valueOf(column[1]);
            break;
          case "cost":
            cost = Double.valueOf(column[1]);
            break;
          default:
            break;
        }

        if (indexArg == (networkArg.length - 1)) {
          final NetworkInfo newNetwork = new NetworkInfo(type, node, core, coreFrequency, throughput, memory, energy,
              cost);
          networkInfoList.add(newNetwork);
        }
      }
    }

  }

  /**
   * Updates the dataset based on the selected node key.
   *
   * @param dataset
   *          The dataset to update.
   */
  private void updateDataset(XYSeriesCollection dataset) {
    dataset.removeAllSeries();

    for (final NetworkInfo net : nodeNetworkInfoNormalMap.get(nodeKey).get(coreKey).get(coreFrequencyKey)) {
      final XYSeries s1 = new XYSeries(
          net.getTypeID() + ":" + net.getNode() + ":" + net.getCore() + ":" + net.getCoreFrequency());
      s1.add(0.0, net.getFinalLatency());
      s1.add(90.0, net.getMemory());
      s1.add(180.0, net.getCost());
      s1.add(270.0, net.getEnergy());
      dataset.addSeries(s1);
    }

  }

  /**
   * Fills the dataset with multi-criteria network information.
   *
   * @return The XYSeriesCollection containing multi-criteria network information.
   */
  private XYSeriesCollection fillMultiCriteriaDataSet() {
    final XYSeriesCollection dataset = new XYSeriesCollection();

    for (final NetworkInfo net : nodeNetworkInfoNormalMap.get(nodeKey).get(coreKey).get(coreFrequencyKey)) {
      final XYSeries s1 = new XYSeries(
          net.getTypeID() + ":" + net.getNode() + ":" + net.getCore() + ":" + net.getCoreFrequency());
      s1.add(0.0, net.getFinalLatency());
      s1.add(90.0, net.getMemory());
      s1.add(180.0, net.getCost());
      s1.add(270.0, net.getEnergy());
      dataset.addSeries(s1);
    }

    return dataset;
  }

  /**
   * Creates a polar chart with the given dataset.
   *
   * @param dataset
   *          The XYSeriesCollection dataset.
   * @return The JFreeChart radar chart.
   */
  private JFreeChart polarChart(XYSeriesCollection dataset) {
    final JFreeChart chart = ChartFactory.createPolarChart("Normalized Radar Chart: Network Topology Comparison",
        dataset, // data
        true, // legend
        true, // tooltips
        false // URL
    );
    chart.setBorderPaint(Color.white);
    chart.setBorderVisible(true);
    chart.setBackgroundPaint(Color.white);

    final JPanel panel = new JPanel();
    panel.setBackground(Color.white);

    final PolarPlot plot = (PolarPlot) chart.getPlot();
    plot.setBackgroundPaint(Color.white);
    plot.setAngleGridlinePaint(Color.black);
    plot.setRadiusGridlinePaint(Color.lightGray);

    plot.setAxis(0, new NumberAxis("Memory")); // 90
    plot.setAxis(1, new NumberAxis("Final latency")); // 0
    plot.setAxis(2, new NumberAxis("Energy")); // 270
    plot.setAxis(3, new NumberAxis("Cost"));// 180
    return chart;
  }

}
