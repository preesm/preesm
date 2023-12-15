package org.preesm.algorithm.node.simulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
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
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class AnalysisPage5 {
  List<NetworkInfo> networkInfoNormalList;

  int                    kTh   = 4;
  int                    kMem  = 3;
  int                    kEner = 2;
  int                    kC    = 1;
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
    final ChartPanel chartPanel = new ChartPanel(chart);
    panel.add(chartPanel);

    final JLabel descriptionLabel = new JLabel(description());
    descriptionLabel.setForeground(Color.darkGray);
    descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
    descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
    final Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    descriptionLabel.setBorder(border);
    // descriptionLabel.setSize(new Dimension(600, descriptionLabel.getHeight()));
    // descriptionLabel.setPreferredSize(descriptionLabel.getPreferredSize());
    panel.add(descriptionLabel, BorderLayout.NORTH);

    final JPanel textFieldPanel = createTextFieldPanel(chart, dataset);

    panel.add(textFieldPanel, BorderLayout.SOUTH);

    return panel;
  }

  private JPanel createTextFieldPanel(JFreeChart chart, DefaultCategoryDataset dataset) {

    final JPanel textFieldPanel = new JPanel();
    textFieldPanel.setLayout(new GridLayout(4, 2, 0, 0));
    textFieldPanel.setSize(200, 200);

    final JLabel thLabel = new JLabel("Enter the throughput weight:");
    textFieldPanel.add(thLabel);

    final JTextField thText = new JTextField();
    thText.setColumns(10);
    textFieldPanel.add(thText);

    thText.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        kTh = Integer.valueOf(thText.getText());
        updateDataset(dataset);
        chart.fireChartChanged(); // Notify the chart that the dataset has changed
      }
    });

    final JLabel mLabel = new JLabel("Enter the memory weight:");
    textFieldPanel.add(mLabel);

    final JTextField mText = new JTextField();
    mText.setColumns(10);
    textFieldPanel.add(mText);

    mText.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        kMem = Integer.valueOf(mText.getText());
        updateDataset(dataset);
        chart.fireChartChanged(); // Notify the chart that the dataset has changed
      }
    });

    final JLabel eLabel = new JLabel("Enter the energy weight:");
    textFieldPanel.add(eLabel);

    final JTextField eText = new JTextField();
    eText.setColumns(10);
    textFieldPanel.add(eText);

    eText.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        kEner = Integer.valueOf(eText.getText());
        updateDataset(dataset);
        chart.fireChartChanged(); // Notify the chart that the dataset has changed
      }
    });

    final JLabel cLabel = new JLabel("Enter the cost weight:");
    textFieldPanel.add(cLabel);

    final JTextField cText = new JTextField();
    cText.setColumns(10);
    textFieldPanel.add(cText);

    cText.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        kC = Integer.valueOf(cText.getText());
        updateDataset(dataset);
        chart.fireChartChanged(); // Notify the chart that the dataset has changed
      }
    });
    textFieldPanel.setBackground(Color.white);
    return textFieldPanel;
  }

  private String description() {
    String description = "<html>";
    description += "This chart gives an idea of the pareto optimal network";
    description += " architecture for a given dataflow application.<br>";
    description += "The simulation assesses performance based on four primary criteria, ";
    description += "each individually weighted: Throughput, Memory, Energy, and Cost.<br>";
    description += "These criteria are combined in a weighted linear function, expressed as Pareto";
    description += "(wT x Throughput + wM x Memory + wE x Energy + wC x Cost), ";
    description += "where wT, wM, wE, and wC represent the respective weights assigned to each criterion.<br>";
    description += "Configurations can be dynamically adjusted to observe how changes in weights impact the Pareto ";
    description += "front and guide decision-making in optimizing the application's deployment.";
    description += "</html>";
    return description;
  }

  private JFreeChart barChart(CategoryDataset dataset) {
    final JFreeChart chart = ChartFactory.createBarChart(
        "Pareto(wT x Throughput + wM x memory + wE x Energy + wC x Cost)", "Configuration", "Global score", dataset,
        PlotOrientation.VERTICAL, true, true, false);
    final CategoryPlot plot = chart.getCategoryPlot();
    plot.setBackgroundPaint(Color.white);
    plot.setDomainGridlinePaint(Color.lightGray);
    plot.setRangeGridlinePaint(Color.lightGray);
    final BarRenderer renderer = (BarRenderer) plot.getRenderer();
    renderer.setBarPainter(new StandardBarPainter());
    return chart;
  }

  private void updateDataset(DefaultCategoryDataset dataset) {
    dataset.clear();
    for (final NetworkInfo net : networkInfoNormalList) {
      // final int kMem = 3;
      // final int kEner = 2;
      // final int kC = 1;
      final Double score = net.getThroughput() * kTh + net.getMemory() * kMem + net.getEnergy() * kEner
          + net.getCost() * kC;
      dataset.addValue(score, "configuration", net.getType());
    }
  }

  private DefaultCategoryDataset fillParetoDataSet() {
    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (final NetworkInfo net : networkInfoNormalList) {

      // final int kMem = 3;
      // final int kEner = 2;
      // final int kC = 1;
      final Double score = net.getThroughput() * kTh + net.getMemory() * kMem + net.getEnergy() * kEner
          + net.getCost() * kC;
      dataset.addValue(score, "configuration", net.getType() + ":" + net.getNode());
    }
    return dataset;
  }

}
