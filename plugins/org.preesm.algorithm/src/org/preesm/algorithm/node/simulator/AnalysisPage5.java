package org.preesm.algorithm.node.simulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class AnalysisPage5 {
  List<NetworkInfo> networkInfoNormalList;

  int                    kth = 1;
  JFreeChart             chart;
  DefaultCategoryDataset dataset;

  public AnalysisPage5(List<NetworkInfo> networkInfoNormalList) {
    this.networkInfoNormalList = networkInfoNormalList;
  }

  public JPanel execute() {
    final JPanel panel = new JPanel();

    dataset = fillParetoDataSet();
    chart = barChart(dataset);

    panel.setBackground(Color.white);
    panel.setLayout(new BorderLayout());
    panel.add(new ChartPanel(chart));

    final JLabel descriptionLabel = new JLabel(description());
    descriptionLabel.setForeground(Color.darkGray);
    descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
    descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
    final Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    descriptionLabel.setBorder(border);
    descriptionLabel.setPreferredSize(descriptionLabel.getPreferredSize());
    panel.add(descriptionLabel, BorderLayout.NORTH);

    final JPanel textFieldPanel = createTextFieldPanel(chart, dataset);
    panel.add(textFieldPanel, BorderLayout.SOUTH);

    return panel;
  }

  private JPanel createTextFieldPanel(JFreeChart chart, DefaultCategoryDataset dataset) {
    final JPanel textFieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

    final JLabel thLabel = new JLabel("Enter the throughput weight:");
    textFieldPanel.add(thLabel);

    final JTextField thText = new JTextField();
    thText.setColumns(10);
    textFieldPanel.add(thText);

    thText.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        kth = Integer.valueOf(thText.getText());
        updateDataset(dataset);
        chart.fireChartChanged(); // Notify the chart that the dataset has changed
      }
    });

    return textFieldPanel;
  }

  private String description() {
    // TODO Auto-generated method stub
    return null;
  }

  private JFreeChart barChart(CategoryDataset dataset) {
    final JFreeChart chart = ChartFactory.createBarChart("oui", "Configuration", "Global score", dataset,
        PlotOrientation.VERTICAL, true, true, false);
    final CategoryPlot plot = chart.getCategoryPlot();
    plot.setBackgroundPaint(Color.white);
    plot.setDomainGridlinePaint(Color.lightGray);
    plot.setRangeGridlinePaint(Color.lightGray);
    return chart;
  }

  private void updateDataset(DefaultCategoryDataset dataset) {
    dataset.clear();
    for (final NetworkInfo net : networkInfoNormalList) {
      final int kMem = 3;
      final int kEner = 2;
      final int kC = 1;
      final Double score = net.getThroughput() * kth + net.getMemory() * kMem + net.getEnergy() * kEner
          + net.getCost() * kC;
      dataset.addValue(score, "configuration", net.getType());
    }
  }

  private DefaultCategoryDataset fillParetoDataSet() {
    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (final NetworkInfo net : networkInfoNormalList) {

      final int kMem = 3;
      final int kEner = 2;
      final int kC = 1;
      final Double score = net.getThroughput() * kth + net.getMemory() * kMem + net.getEnergy() * kEner
          + net.getCost() * kC;
      dataset.addValue(score, "configuration", net.getType());
    }
    return dataset;
  }

}
