package org.preesm.algorithm.node.simulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
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
  static String              path;
  public static final String NET_NAME              = "multicriteria.csv";
  List<NetworkInfo>          networkInfoList       = new ArrayList<>();
  static List<NetworkInfo>   networkInfoNormalList = new ArrayList<>();

  public AnalysisPage4(String path) {
    AnalysisPage4.path = path;

  }

  public JPanel execute() {
    final JPanel panel = new JPanel();
    fillNetworkInfoList();
    networkInfoNormalList.clear();
    fillNetworkInfoNormalList();
    final XYSeriesCollection dataset = fillMultiCriteriaDataSet();

    final JFreeChart chart = polarChart(dataset);
    panel.setBackground(Color.white);
    panel.setLayout(new GridLayout(2, 1));
    panel.add(new ChartPanel(chart));

    final JLabel descriptionLabel = new JLabel(description());
    descriptionLabel.setForeground(Color.darkGray);
    descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
    descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
    final Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    descriptionLabel.setBorder(border);
    descriptionLabel.setPreferredSize(descriptionLabel.getPreferredSize());
    panel.add(descriptionLabel, BorderLayout.NORTH);

    return panel;
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
    return values.stream().min(Double::compareTo).orElse(0.0);
  }

  private Double normalize(Double value, Double min, Double max) {
    return (value - min) / (max - min);
  }

  private void fillNetworkInfoList() {
    final String[] arrayNet = PreesmIOHelper.getInstance().read(path, NET_NAME).split("\n");
    String type = "";
    int node = 0;
    Double throughput = 0.0;
    Double memory = 0.0;
    Double energy = 0.0;
    Double cost = 0.0;
    for (int i = 0; i < arrayNet.length; i++) {
      final String[] column = arrayNet[i].split(";");

      switch (column[0]) {
        case "type":
          type = column[1];
          break;
        case "node":
          node = Integer.valueOf(column[1]);
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

      if ((i - 4) % 5 == 0) {
        final NetworkInfo newNetwork = new NetworkInfo(type, node, throughput, memory, energy, cost);
        networkInfoList.add(newNetwork);
      }
    }

  }

  private XYSeriesCollection fillMultiCriteriaDataSet() {
    final XYSeriesCollection result = new XYSeriesCollection();

    for (final NetworkInfo net : AnalysisPage4.networkInfoNormalList) {
      final XYSeries s1 = new XYSeries(net.getType());
      s1.add(0.0, net.getThroughput());
      s1.add(90.0, net.getMemory());
      s1.add(180.0, net.getCost());
      s1.add(270.0, net.getEnergy());
      result.addSeries(s1);
    }

    return result;
  }

  private JFreeChart polarChart(XYSeriesCollection dataset) {
    final JFreeChart chart = ChartFactory.createPolarChart("Polar Chart Example", // Titre du graphique
        dataset, // Données
        true, // Légende
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
    plot.setAxis(1, new NumberAxis("Throughput")); // 0
    plot.setAxis(2, new NumberAxis("Energy")); // 270
    plot.setAxis(3, new NumberAxis("Cost"));// 180
    return chart;
  }

  private String description() {
    // TODO Auto-generated method stub
    return null;
  }
}
