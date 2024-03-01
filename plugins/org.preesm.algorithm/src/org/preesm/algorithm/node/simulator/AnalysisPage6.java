package org.preesm.algorithm.node.simulator;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Shape;
import java.util.List;
import javax.swing.JPanel;
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
import org.jfree.chart.util.ShapeUtils;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.xy.DefaultXYZDataset;

public class AnalysisPage6 {
  List<NetworkInfo> networkInfoList;

  public AnalysisPage6(List<NetworkInfo> networkInfoList) {
    this.networkInfoList = networkInfoList;
  }

  public JPanel execute() {
    final JPanel panel = new JPanel();

    final DefaultXYZDataset dataset1 = convertToXYZArray1();
    final Range r1 = DatasetUtils.findZBounds(dataset1);
    final JFreeChart chart1 = createChart(dataset1, "Pareto Chart with Three Axes", "Final Latency", "Memory", "Energy",
        r1);
    final DefaultXYZDataset dataset2 = convertToXYZArray2();
    final Range r2 = DatasetUtils.findZBounds(dataset2);
    final JFreeChart chart2 = createChart(dataset1, "Pareto Chart with Three Axes", "Final Latency", "Memory", "Cost",
        r2);

    panel.add(new ChartPanel(chart1));
    panel.add(new ChartPanel(chart2));
    panel.setLayout(new GridLayout(3, 1));

    return panel;
  }

  private DefaultXYZDataset convertToXYZArray1() {
    final DefaultXYZDataset dataset = new DefaultXYZDataset();
    final int size = networkInfoList.size();
    final double[][] xyz = new double[3][size];

    for (int i = 0; i < size; i++) {
      final NetworkInfo networkInfo = networkInfoList.get(i);
      xyz[0][i] = networkInfo.getFinalLatency();
      xyz[1][i] = networkInfo.getMemory();
      xyz[2][i] = networkInfo.getEnergy();
    }
    dataset.addSeries("Series", xyz);
    return dataset;
  }

  private DefaultXYZDataset convertToXYZArray2() {
    final DefaultXYZDataset dataset = new DefaultXYZDataset();
    final int size = networkInfoList.size();
    final double[][] xyz = new double[3][size];

    for (int i = 0; i < size; i++) {
      final NetworkInfo networkInfo = networkInfoList.get(i);
      xyz[0][i] = networkInfo.getFinalLatency();
      xyz[1][i] = networkInfo.getMemory();
      xyz[2][i] = networkInfo.getCost();
    }
    dataset.addSeries("Series", xyz);
    return dataset;
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
        return ShapeUtils.createDiagonalCross(5, 2);
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
        final double yValue = dataset.getYValue(series, item);
        final double zValue = dataset.getZValue(series, item);

        final XYTextAnnotation label = new XYTextAnnotation("(" + xValue + ", " + yValue + ", " + zValue + ")", xValue,
            yValue);
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

}
