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
import org.jfree.chart.plot.PolarPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.preesm.commons.files.PreesmIOHelper;

public class AnalysisPage4 {
  static String              path;
  public static final String NET_NAME = "xNet_last.csv";

  public AnalysisPage4(String path) {
    AnalysisPage4.path = path;

  }

  public JPanel execute() {
    final JPanel panel = new JPanel();
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

  private XYSeriesCollection fillMultiCriteriaDataSet() {
    final XYSeriesCollection result = new XYSeriesCollection();

    final String[] name = { "Cluster with a Crossbar", "Cluster with a Shared Backbone", "Torus Cluster",
        "Fat-Tree Cluster", "Dragonfly Cluster" };
    final double[] axes = { 0.0, 90.0, 180.0, 270.0 };
    final String[] arrayNet = PreesmIOHelper.getInstance().read(path, NET_NAME).split("\n");
    for (int i = 0; i < arrayNet.length; i++) {
      final XYSeries s1 = new XYSeries(name[i]);
      final String[] column = arrayNet[i].split(";");
      for (int j = 0; j < column.length; j++) {
        s1.add(axes[j], Double.valueOf(column[j]));
      }

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
