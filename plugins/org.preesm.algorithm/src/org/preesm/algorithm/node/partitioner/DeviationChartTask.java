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
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

@PreesmTask(id = "DeviationChartTask.identifier", name = "ABC Node exporter", category = "Gantt exporters",

    // inputs = { @Port(name = "ABC", type = LatencyAbc.class) },

    shortDescription = "This task exports scheduling results as a *.csv file .",

    parameters = {

        @Parameter(name = "Multinode", description = "Fill in the SimSDP top-graph timing file",
            values = { @Value(name = "true/false", effect = "Enable to fill in the SimSDP top-graph timing file") }),
        @Parameter(name = "Top", description = "Fill in the SimSDP node workload file",
            values = { @Value(name = "true/false", effect = "Enable to fill in the SimSDP node workload file") }) })
public class DeviationChartTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    final JFrame frame = new JFrame("SimSDP Curve");

    final XYSeries series = new XYSeries("Workload Deviation Trend");
    series.add(1.0, 1.0);
    series.add(2.0, 4.0);
    series.add(3.0, 9.0);
    series.add(4.0, 16.0);
    series.add(5.0, 25.0);

    final XYSeriesCollection dataset = new XYSeriesCollection(series);
    final JFreeChart chart = ChartFactory.createXYLineChart("Curve1", "Round", "Deviation", dataset,
        PlotOrientation.VERTICAL, true, true, false);

    final XYPlot plot = chart.getXYPlot();
    final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    renderer.setSeriesPaint(0, Color.BLUE);
    plot.setRenderer(renderer);

    final XYSeries series2 = new XYSeries("Latency Trend");
    series2.add(1.0, 1.0);
    series2.add(2.0, 4.0);
    series2.add(3.0, 9.0);
    series2.add(4.0, 16.0);
    series2.add(5.0, 25.0);

    final XYSeriesCollection dataset2 = new XYSeriesCollection(series2);
    final JFreeChart chart2 = ChartFactory.createXYLineChart("Curve2", "Round", "Latency", dataset2,
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
