package org.preesm.algorithm.node.simulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.xy.DefaultXYZDataset;
import org.preesm.commons.files.PreesmIOHelper;

public class AnalysisPage1bis {
  public static final String CORRELATION_NAME = "correlation.csv";
  static String              path;
  // PearsonsCorrelation correlation = new PearsonsCorrelation();
  double correlationWorkloadLinkload = 0d;
  double correlationWorkloadLatency  = 0d;
  double correlationLinkloadLatency  = 0d;

  public AnalysisPage1bis(String path) {
    AnalysisPage1bis.path = path;
  }

  public JPanel execute() {
    final JPanel panel = new JPanel();

    final DefaultXYZDataset dataset = fillCorrelationDataSet();
    final JLabel descriptionLabel = new JLabel(description());
    descriptionLabel.setForeground(Color.darkGray);
    descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
    descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
    final Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    descriptionLabel.setBorder(border);
    descriptionLabel.setPreferredSize(descriptionLabel.getPreferredSize());
    panel.add(descriptionLabel, BorderLayout.NORTH);
    final Range r = DatasetUtils.findZBounds(dataset);
    final JFreeChart chart = createChart(dataset, "Correlation between Workload, Link Load and Final Latency",
        "STD Workload", "STD link load", "Final Latency", r);
    panel.add(new ChartPanel(chart));
    panel.setLayout(new GridLayout(3, 1));
    return panel;
  }

  private String description() {

    String description = "<html>Correlaton coefficient: </br>";
    description += "workload - linkload: " + correlationWorkloadLinkload + "</br>";
    description += "workload - final latency: " + correlationWorkloadLatency + "</br>";
    description += "linkload - final latency: " + correlationLinkloadLatency + "</br>";
    description += "</html>";
    return description;
  }

  private JFreeChart createChart(DefaultXYZDataset dataset, String title, String x, String y, String z, Range r) {
    final NumberAxis xAxis = new NumberAxis(x);
    xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    final NumberAxis yAxis = new NumberAxis(y);
    yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    final SpectrumPaintScale scale = new SpectrumPaintScale(r);
    final NumberAxis scaleAxis = new NumberAxis(z);
    scaleAxis.setAxisLinePaint(Color.white);
    scaleAxis.setTickMarkPaint(Color.white);
    final PaintScaleLegend legend = new PaintScaleLegend(scale, scaleAxis);
    legend.setSubdivisionCount(128);
    legend.setAxisLocation(AxisLocation.TOP_OR_RIGHT);
    legend.setPadding(new RectangleInsets(10, 10, 10, 10));
    legend.setStripWidth(20);
    legend.setPosition(RectangleEdge.RIGHT);
    legend.setBackgroundPaint(Color.WHITE);
    final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true) {
      private static final long serialVersionUID = 1L;

      @Override
      public Paint getItemFillPaint(int row, int col) {
        return scale.getPaint(dataset.getZValue(row, col));
      }

      @Override
      public Shape getItemShape(int row, int col) {
        // return ShapeUtils.createDiagonalCross(5, 2);
        final double size = 10; // Taille du cercle
        return new Ellipse2D.Double(-size / 2, -size / 2, size, size);
      }
    };
    renderer.setUseFillPaint(true);
    renderer.setSeriesShapesFilled(0, true);
    renderer.setSeriesShapesVisible(0, true);
    final XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
    plot.setBackgroundPaint(Color.lightGray);
    plot.setDomainGridlinesVisible(false);
    plot.setRangeGridlinePaint(Color.white);
    plot.setRenderer((renderer));

    // Add labels to data points
    for (int series = 0; series < dataset.getSeriesCount(); series++) {
      for (int item = 0; item < dataset.getItemCount(series); item++) {
        final double xValue = dataset.getXValue(series, item);
        double yValue = dataset.getYValue(series, item);
        final double zValue = dataset.getZValue(series, item);
        final DecimalFormat decimalFormat = new DecimalFormat("0.0");
        if (!Double.isFinite(yValue)) {
          yValue = 0.0;
        }

        final XYTextAnnotation label = new XYTextAnnotation("(" + decimalFormat.format(xValue) + ", "
            + decimalFormat.format(yValue) + ", " + decimalFormat.format(zValue) + ")", xValue, yValue);
        label.setFont(new Font("SansSerif", Font.PLAIN, 9));
        label.setTextAnchor(TextAnchor.BOTTOM_LEFT);

        plot.addAnnotation(label);
      }
    }

    final JFreeChart chart = new JFreeChart(title, plot);
    chart.addSubtitle(legend);
    chart.removeLegend();
    chart.setBackgroundPaint(Color.white);

    return chart;
  }

  private DefaultXYZDataset fillCorrelationDataSet() {
    final String[] arrayCorrelation = PreesmIOHelper.getInstance().read(path, CORRELATION_NAME).split("\n");
    final DefaultXYZDataset dataset = new DefaultXYZDataset();
    final int size = arrayCorrelation.length - 1;
    final double[][] xyz = new double[3][size];
    for (int i = 1; i < size; i++) {
      final String[] column = arrayCorrelation[i].split(";");
      xyz[0][i] = Double.valueOf(column[0]);
      xyz[1][i] = Double.valueOf(column[1]);
      xyz[2][i] = Double.valueOf(column[2]);
    }
    dataset.addSeries("Series", xyz);
    if (size >= 2) {
      final PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
      correlationWorkloadLinkload = pearsonsCorrelation.correlation(xyz[0], xyz[1]);
      correlationWorkloadLatency = pearsonsCorrelation.correlation(xyz[0], xyz[2]);
      correlationLinkloadLatency = pearsonsCorrelation.correlation(xyz[1], xyz[2]);
      // correlation = new PearsonsCorrelation(xyz);
    }

    return dataset;
  }

}
