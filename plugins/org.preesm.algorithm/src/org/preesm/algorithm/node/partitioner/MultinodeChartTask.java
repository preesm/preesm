package org.preesm.algorithm.node.partitioner;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

@PreesmTask(id = "MultinodeChartTask.identifier", name = "Chart exporter", category = "Chart exporters")
public class MultinodeChartTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {

    final XYSeriesCollection result = new XYSeriesCollection();
    final XYSeries s1 = new XYSeries("Series 1");
    s1.add(0.0, 2.0);// throughput
    s1.add(90.0, 13.0);// memory
    s1.add(180.0, 9.0);// Cost
    s1.add(270.0, 8.0);// Energy
    result.addSeries(s1);

    final JFreeChart chart = ChartFactory.createPolarChart("Polar Chart Example", // Titre du graphique
        result, // Données
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

    panel.add(new ChartPanel(chart));
    final JFrame frame = new JFrame("SimSDP Curve");
    frame.setSize(1000, 800);
    frame.setLocationRelativeTo(null);
    frame.add(panel);
    frame.setVisible(true);

    return new LinkedHashMap<>();
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return new LinkedHashMap<>();
  }

  @Override
  public String monitorMessage() {
    return "Generate chart of multinode scheduling.";
  }

}
