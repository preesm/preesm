package org.preesm.algorithm.node.partitioner;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JFrame;
import org.eclipse.core.runtime.IProgressMonitor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.files.PreesmIOHelper;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

@PreesmTask(id = "DeviationChartTask.identifier", name = "Deviation chart exporter",
    category = "Deviation chart exporters")
public class DeviationChartTask extends AbstractTaskImplementation {
  public static final String WORKLOAD_NAME = "workload_trend.csv";
  public static final String LATENCY_NAME  = "latency_trend.csv";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    final String path = "/" + workflow.getProjectName() + "/Scenarios/generated/";
    final JFrame frame = new JFrame("SimSDP Curve");

    final XYSeries series = new XYSeries("Workload Deviation Trend");
    // read deviation trend
    final String[] arrayWorkload = PreesmIOHelper.getInstance().read(path, WORKLOAD_NAME).split("\n");
    for (int i = 0; i < arrayWorkload.length; i++) {
      series.add(i, Double.valueOf(arrayWorkload[i]));
    }

    final XYSeriesCollection dataset = new XYSeriesCollection(series);
    final JFreeChart chart = ChartFactory.createXYLineChart("Internode Workload Standard Deviation", "Round",
        "Deviation", dataset, PlotOrientation.VERTICAL, true, true, false);

    final XYPlot plot = chart.getXYPlot();
    final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    renderer.setSeriesPaint(0, Color.BLUE);
    plot.setRenderer(renderer);

    final XYSeries series2 = new XYSeries("Latency Trend");
    // read latency trend
    final String[] arrayLatency = PreesmIOHelper.getInstance().read(path, LATENCY_NAME).split("\n");
    for (int i = 0; i < arrayLatency.length; i++) {
      series2.add(i, Double.valueOf(arrayLatency[i]));
    }

    final XYSeriesCollection dataset2 = new XYSeriesCollection(series2);
    final JFreeChart chart2 = ChartFactory.createXYLineChart("Latency", "Round", "Latency", dataset2,
        PlotOrientation.VERTICAL, true, true, false);

    final XYPlot plot2 = chart2.getXYPlot();
    final XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer();
    renderer2.setSeriesPaint(0, Color.RED);
    plot2.setRenderer(renderer2);
    frame.setLayout(new GridLayout(1, 2));
    frame.add(new ChartPanel(chart));
    frame.add(new ChartPanel(chart2));

    frame.setPreferredSize(new Dimension(1000, 600));
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    return new LinkedHashMap<>();
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Generate chart of multinode scheduling.";
  }

}
