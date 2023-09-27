package org.preesm.algorithm.node.partitioner;

import java.awt.Color;
import java.awt.Dimension;
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
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * display charts of workload and latency deviation during SimSDP iterations.
 *
 *
 * @author orenaud
 */
@PreesmTask(id = "NodeChartExporterTask.identifier", name = "ABC Node exporter", category = "Gantt exporters",

    inputs = { @Port(name = "ABC", type = LatencyAbc.class) },

    shortDescription = "This task exports scheduling results as a *.csv file ."

)
public class NodeChartExporterTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    createAndShowGUI();
    return new LinkedHashMap<>();
  }

  private void createAndShowGUI() {
    final JFrame frame = new JFrame("Afficher une Courbe en Java");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    final XYSeries series = new XYSeries("Données de la Courbe");
    series.add(1.0, 1.0);
    series.add(2.0, 4.0);
    series.add(3.0, 9.0);
    series.add(4.0, 16.0);
    series.add(5.0, 25.0);

    final XYSeriesCollection dataset = new XYSeriesCollection(series);
    final JFreeChart chart = ChartFactory.createXYLineChart("Courbe Quadratique", "X", "Y", dataset,
        PlotOrientation.VERTICAL, true, true, false);

    final XYPlot plot = chart.getXYPlot();
    final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    renderer.setSeriesPaint(0, Color.BLUE);
    plot.setRenderer(renderer);

    frame.add(new ChartPanel(chart));

    frame.setPreferredSize(new Dimension(800, 600));
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
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
