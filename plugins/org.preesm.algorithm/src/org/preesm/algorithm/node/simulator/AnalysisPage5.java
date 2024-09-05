package org.preesm.algorithm.node.simulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Paint;
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

/**
 * Represents the analysis page for visualizing Pareto optimal network architectures. The page includes a bar chart
 * showing the global score based on weighted criteria (Final latency, Memory, Energy, and Cost) for different network
 * configurations. Users can dynamically adjust weights to observe the impact on the Pareto front.
 *
 * The chart is created using JFreeChart library.
 *
 * @author orenaud
 */
public class AnalysisPage5 {
  List<NetworkInfo> networkInfoNormalList;

  int                    kTh      = 4;
  int                    kMem     = 3;
  int                    kEner    = 2;
  int                    kC       = 1;
  JFreeChart             chart;
  DefaultCategoryDataset dataset;
  Double                 scoreMin = Double.MAX_VALUE;
  Double                 scoreMax = 0d;

  private static final String DESCRIPTION = """
      <html>
      This chart gives an idea of the pareto optimal network
       architecture for a given dataflow application.<br>
      The simulation assesses performance based on four primary criteria,
      each individually weighted: Final latency, Memory, Energy, and Cost.<br>
      These criteria are combined in a weighted linear function, expressed as Pareto
      (wL x Final latency + wM x Memory + wE x Energy + wC x Cost),
      where wL, wM, wE, and wC represent the respective weights assigned to each criterion.<br>
      Configuration ID are: 1: Cluster with Crossbar, 2: Cluster with a shared backbone,
      3: Torus cluster, 4: Fat-tree cluster, 5: Dragonfly cluster<br>
      Configurations can be dynamically adjusted to observe how changes in weights impact the Pareto
      front and guide decision-making in optimizing the application's deployment.
      </html>
      """;

  /**
   * Constructs an instance of AnalysisPage5 with the given list of NetworkInfo objects.
   *
   * @param networkInfoNormalList
   *          List of NetworkInfo objects to analyze.
   */
  public AnalysisPage5(List<NetworkInfo> networkInfoNormalList) {
    this.networkInfoNormalList = networkInfoNormalList;
  }

  /**
   * Executes the analysis and generates a JPanel containing the Pareto chart, description label, and weight adjustment
   * text fields.
   *
   * @return JPanel containing the visual elements of the analysis page.
   */
  public JPanel execute() {
    final JPanel panel = new JPanel();

    dataset = fillParetoDataSet();
    chart = barChart(dataset);

    panel.setBackground(Color.white);
    panel.setLayout(new BorderLayout());
    final ChartPanel chartPanel = new ChartPanel(chart);
    panel.add(chartPanel);

    final JLabel descriptionLabel = new JLabel(DESCRIPTION);
    descriptionLabel.setForeground(Color.darkGray);
    descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
    descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
    final Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    descriptionLabel.setBorder(border);
    panel.add(descriptionLabel, BorderLayout.NORTH);

    final JPanel textFieldPanel = createTextFieldPanel(chart, dataset);

    panel.add(textFieldPanel, BorderLayout.SOUTH);

    return panel;
  }

  /**
   * Creates a JPanel containing text fields for adjusting weights and listening for user input.
   *
   * @param chart
   *          The JFreeChart instance associated with the analysis.
   * @param dataset
   *          The dataset containing network configuration scores.
   * @return JPanel with weight adjustment text fields.
   */
  private JPanel createTextFieldPanel(JFreeChart chart, DefaultCategoryDataset dataset) {

    final JPanel textFieldPanel = new JPanel();
    textFieldPanel.setLayout(new GridLayout(4, 2, 0, 0));
    textFieldPanel.setSize(200, 200);

    final JLabel thLabel = new JLabel("Enter the final latency weight:");
    textFieldPanel.add(thLabel);

    final JTextField thText = new JTextField();
    thText.setSize(thText.getHeight(), 5);
    textFieldPanel.add(thText);

    thText.addActionListener(e -> {
      kTh = Integer.valueOf(thText.getText());
      updateDatasetUser(dataset);
      chart.fireChartChanged(); // Notify the chart that the dataset has changed
    });

    final JLabel mLabel = new JLabel("Enter the memory weight:");
    textFieldPanel.add(mLabel);

    final JTextField mText = new JTextField();
    mText.setColumns(5);
    textFieldPanel.add(mText);

    mText.addActionListener(e -> {
      kMem = Integer.valueOf(mText.getText());
      updateDatasetUser(dataset);
      chart.fireChartChanged(); // Notify the chart that the dataset has changed
    });

    final JLabel eLabel = new JLabel("Enter the energy weight:");
    textFieldPanel.add(eLabel);

    final JTextField eText = new JTextField();
    eText.setColumns(10);
    textFieldPanel.add(eText);

    eText.addActionListener(e -> {
      kEner = Integer.valueOf(eText.getText());
      updateDatasetUser(dataset);
      chart.fireChartChanged(); // Notify the chart that the dataset has changed

    });

    final JLabel cLabel = new JLabel("Enter the cost weight:");
    textFieldPanel.add(cLabel);

    final JTextField cText = new JTextField();
    cText.setColumns(10);
    textFieldPanel.add(cText);

    cText.addActionListener(e -> {
      kC = Integer.valueOf(cText.getText());
      updateDatasetUser(dataset);
      chart.fireChartChanged(); // Notify the chart that the dataset has changed

    });
    textFieldPanel.setBackground(Color.white);
    return textFieldPanel;
  }

  /**
   * Creates a JFreeChart bar chart based on the provided dataset.
   *
   * @param dataset
   *          The dataset containing network configuration scores.
   * @return JFreeChart instance representing the Pareto chart.
   */
  private JFreeChart barChart(CategoryDataset dataset) {
    final JFreeChart jChart = ChartFactory.createBarChart(
        "Pareto(wL x Final latency + wM x memory + wE x Energy + wC x Cost)", "Configuration", "Global score", dataset,
        PlotOrientation.VERTICAL, true, true, false);
    final CategoryPlot plot = jChart.getCategoryPlot();
    plot.setBackgroundPaint(Color.white);
    plot.setDomainGridlinePaint(Color.lightGray);
    plot.setRangeGridlinePaint(Color.lightGray);

    // Create a custom renderer
    final BarRenderer customRenderer = new BarRenderer() {
      private static final long serialVersionUID = 1L;

      @Override
      public Paint getItemPaint(int row, int column) {
        // Récupération de la valeur de la barre
        final double value = dataset.getValue(row, column).doubleValue();

        // Définition des couleurs en fonction de la valeur
        if (value == scoreMin/* valeur initiale */) {
          return Color.green;
        }
        if (value == scoreMax /* valeur maximale */) {
          return Color.red;
        }
        // Couleur par défaut pour les autres valeurs
        return Color.gray;
      }
    };

    customRenderer.setBarPainter(new StandardBarPainter());
    customRenderer.setShadowVisible(false);
    plot.setRenderer(customRenderer);
    return jChart;
  }

  /**
   * Updates the dataset based on user-adjusted weights and notifies the chart of the changes.
   *
   * @param dataset
   *          The dataset containing network configuration scores.
   */
  private void updateDatasetUser(DefaultCategoryDataset dataset) {
    dataset.clear();

    updateDataset(dataset, "configuration");
  }

  /**
   * Fills the initial Pareto dataset based on the original network configurations.
   *
   * @return DefaultCategoryDataset containing the initial network configuration scores.
   */
  private DefaultCategoryDataset fillParetoDataSet() {
    final DefaultCategoryDataset defaultDataset = new DefaultCategoryDataset();

    return updateDataset(defaultDataset, "configuration ID : number of nodes : number of cores : core frequency");
  }

  private DefaultCategoryDataset updateDataset(DefaultCategoryDataset dataset, String rowKey) {
    for (final NetworkInfo net : networkInfoNormalList) {

      final Double score = net.getFinalLatency() * kTh + net.getMemory() * kMem + net.getEnergy() * kEner
          + net.getCost() * kC;
      dataset.addValue(score, rowKey,
          net.getType() + ":" + net.getNode() + ":" + net.getCore() + ":" + net.getCoreFrequency());
      if (score != 0 && score < scoreMin) {
        scoreMin = score;
      }
      if (score > scoreMax) {
        scoreMax = score;
      }
    }
    return dataset;
  }

}
