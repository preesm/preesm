package org.preesm.algorithm.node.simulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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

public class AnalysisPage4 {
  static String                   path;
  public static final String      NET_NAME                 = "multicriteria.csv";
  List<NetworkInfo>               networkInfoList          = new ArrayList<>();
  static List<NetworkInfo>        networkInfoNormalList    = new ArrayList<>();
  Map<Integer, List<NetworkInfo>> nodeNetworkInfoNormalMap = new LinkedHashMap<>();
  int                             nodeKey                  = 0;

  public AnalysisPage4(String path) {
    AnalysisPage4.path = path;

  }

  public JPanel execute() {

    final JPanel panel = new JPanel();
    fillNetworkInfoList();
    nodeKey = networkInfoList.get(0).getNode();
    networkInfoNormalList.clear();
    fillNetworkInfoNormalList();
    nodeNetworkInfoNormalMap.clear();
    fillNodeNetworkInfoNormalMap();

    final XYSeriesCollection dataset = fillMultiCriteriaDataSet();

    final JFreeChart chart = polarChart(dataset);
    panel.setBackground(Color.white);
    panel.setLayout(new BorderLayout());

    final JLabel descriptionLabel = new JLabel(description());
    descriptionLabel.setForeground(Color.darkGray);
    descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
    descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
    final Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    descriptionLabel.setBorder(border);
    // descriptionLabel.setPreferredSize(descriptionLabel.getPreferredSize());
    panel.add(descriptionLabel, BorderLayout.NORTH);

    panel.add(new ChartPanel(chart));

    final JPanel textFieldPanel = createTextFieldPanel(chart, dataset);

    panel.add(textFieldPanel, BorderLayout.SOUTH);

    return panel;
  }

  private void fillNodeNetworkInfoNormalMap() {
    for (final NetworkInfo network : networkInfoNormalList) {
      if (!nodeNetworkInfoNormalMap.containsKey(network.getNode())) {
        final List<NetworkInfo> newList = new ArrayList<>();
        newList.add(network);
        nodeNetworkInfoNormalMap.put(network.getNode(), newList);
      } else if (!nodeNetworkInfoNormalMap.get(network.getNode()).stream()
          .anyMatch(x -> x.getType().equals(network.getType()))) {
        nodeNetworkInfoNormalMap.get(network.getNode()).add(network);
      }

    }

  }

  private JPanel createTextFieldPanel(JFreeChart chart, XYSeriesCollection dataset) {
    final JPanel textFieldPanel = new JPanel();
    textFieldPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
    // textFieldPanel.setSize(200, 50);
    final JLabel nodeSelect = new JLabel("Select the number of nodes for network analysis:");
    textFieldPanel.add(nodeSelect);

    final String[] nodeItems = new String[nodeNetworkInfoNormalMap.size()];
    int index = 0;
    for (final int i : nodeNetworkInfoNormalMap.keySet()) {
      nodeItems[index] = i + " nodes";
      index++;

    }
    final JComboBox<String> nodeCombo = new JComboBox<>(nodeItems);
    nodeCombo.setBackground(Color.white);
    textFieldPanel.add(nodeCombo);
    textFieldPanel.setBackground(Color.white);

    nodeCombo.addActionListener(e -> {
      nodeKey = Integer.valueOf(nodeCombo.getSelectedItem().toString().replace(" nodes", ""));
      updateDataset(dataset);
      chart.fireChartChanged();
    });

    return textFieldPanel;
  }

  public static List<NetworkInfo> getNetworkInfoNormalList() {
    return networkInfoNormalList;
  }

  private void fillNetworkInfoNormalList() {
    final List<Double> throughputs = extractMetrics(networkInfoList, NetworkInfo::getThroughput);
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
      final Double throughput = normalize(net.getThroughput(), thMin, thMax);
      final Double memory = normalize(net.getMemory(), mMin, mMax);
      final Double energy = normalize(net.getEnergy(), enerMin, enerMax);
      final Double cost = normalize(net.getCost(), cMin, cMax);
      final NetworkInfo newNetwork = new NetworkInfo(net.getType(), net.getNode(), throughput, memory, energy, cost);
      networkInfoNormalList.add(newNetwork);
    }
  }

  private List<Double> extractMetrics(List<NetworkInfo> networkInfoList, Function<NetworkInfo, Double> extractor) {
    return networkInfoList.stream().map(extractor).collect(Collectors.toList());
  }

  private Double findMax(List<Double> values) {
    return values.stream().max(Double::compareTo).orElse(0.0);
  }

  private Double findMin(List<Double> values) {
    if (values.size() < 2) {
      // Gestion du cas où il y a moins de deux valeurs
      return values.stream().findFirst().orElse(0.0);
    }
    // Retrouver le minimum entre les deux premières valeurs
    final Double minBetweenFirstTwo = Math.min(values.get(0), values.get(1));

    // Retrouver le minimum entre le minimum trouvé et le reste des valeurs
    return values.stream().skip(2) // Ignorer les deux premières valeurs
        .reduce(minBetweenFirstTwo, Math::min);
  }
  // private Double findMin(List<Double> values) {
  // return values.stream().min(Double::compareTo).orElse(0.0);
  // }

  private Double normalize(Double value, Double min, Double max) {
    return (value - min) / (max - min);
  }

  private void fillNetworkInfoList() {
    final String[] Networklist = PreesmIOHelper.getInstance().read(path, NET_NAME).split("\n\n");
    for (final String element : Networklist) {

      String type = "";
      int node = 0;
      Double throughput = 0.0;
      Double memory = 0.0;
      Double energy = 0.0;
      Double cost = 0.0;
      final String[] NetworkArg = element.split("\n");
      for (int indexArg = 0; indexArg < NetworkArg.length; indexArg++) {
        final String[] column = NetworkArg[indexArg].split(";");

        switch (column[0]) {
          case "type":
            type = column[1].split(":")[0];
            node = Integer.valueOf(column[1].split(":")[1]);
            break;
          case "throughput":
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

        if (indexArg == (NetworkArg.length - 1)) {
          final NetworkInfo newNetwork = new NetworkInfo(type, node, throughput, memory, energy, cost);
          networkInfoList.add(newNetwork);
        }
      }
    }

  }

  private void updateDataset(XYSeriesCollection dataset) {
    dataset.removeAllSeries();
    for (final NetworkInfo net : nodeNetworkInfoNormalMap.get(nodeKey)) {
      final XYSeries s1 = new XYSeries(net.getType() + ":" + net.getNode());
      s1.add(0.0, net.getThroughput());
      s1.add(90.0, net.getMemory());
      s1.add(180.0, net.getCost());
      s1.add(270.0, net.getEnergy());
      dataset.addSeries(s1);
    }

  }

  private XYSeriesCollection fillMultiCriteriaDataSet() {
    final XYSeriesCollection dataset = new XYSeriesCollection();
    for (final NetworkInfo net : nodeNetworkInfoNormalMap.get(nodeKey)) {
      final XYSeries s1 = new XYSeries(net.getType() + ":" + net.getNode());
      s1.add(0.0, net.getThroughput());
      s1.add(90.0, net.getMemory());
      s1.add(180.0, net.getCost());
      s1.add(270.0, net.getEnergy());
      dataset.addSeries(s1);
    }
    // for (final NetworkInfo net : AnalysisPage4.networkInfoNormalList) {
    // final XYSeries s1 = new XYSeries(net.getType() + ":" + net.getNode());
    // s1.add(0.0, net.getThroughput());
    // s1.add(90.0, net.getMemory());
    // s1.add(180.0, net.getCost());
    // s1.add(270.0, net.getEnergy());
    // result.addSeries(s1);
    // }

    return dataset;
  }

  private JFreeChart polarChart(XYSeriesCollection dataset) {
    final JFreeChart chart = ChartFactory.createPolarChart("Normalized Radar Chart: Network Topology Comparison",
        dataset, // data
        true, // legend
        true, // Infobulles
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

  private String description() {
    String description = "<html>";
    description += "This chart gives a comprehensive comparison of the 5 main network topologies";
    description += "(if they exist) concerning a selected number of nodes, dynamically chosen from a ComboBox.<br>";
    description += "The topologies are: Cluster with a Crossbar,  Cluster with a Shared Backbone, Torus Cluster, ";
    description += "Fat-Tree Cluster and Dragonfly Cluster.<br>";
    description += "This visualization captures the normalized performance metrics of";
    description += "Throughput, Memory, Energy, and Cost for each network configuration.<br>";
    description += "Normalized value = (x - minvalue) / (maxvalue - minvalue)";
    description += "</html>";
    return description;
  }
}
