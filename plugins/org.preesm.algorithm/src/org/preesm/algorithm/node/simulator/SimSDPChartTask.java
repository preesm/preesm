package org.preesm.algorithm.node.simulator;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

@PreesmTask(id = "SimSDPChartTask.identifier", name = "Deviation chart exporter",
    category = "Deviation chart exporters")
public class SimSDPChartTask extends AbstractTaskImplementation {
  public static final String PAGE1 = "Internode Analysis";
  public static final String PAGE2 = "Intranode Analysis";
  public static final String PAGE3 = "DSE Analysis";
  public static final String PAGE4 = "Radar Analysis";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {
    final String path = "/" + workflow.getProjectName() + "/Scenarios/generated/";
    final int iterationOptim = 0;
    final JFrame frame = createFrame();
    final JTabbedPane tabbedPane = new JTabbedPane();
    final JPanel tab1 = new AnalysisPage1(path, iterationOptim).execute();
    final JPanel tab2 = new AnalysisPage2(path, iterationOptim).execute();
    final JPanel tab3 = new AnalysisPage3(path, iterationOptim).execute();
    final JPanel tab4 = new AnalysisPage4(path).execute();
    addTab(tabbedPane, PAGE1, tab1, "Description de la page 1");
    addTab(tabbedPane, PAGE2, tab2, "Description de la page 2");
    addTab(tabbedPane, PAGE3, tab3, "Description de la page 3");
    addTab(tabbedPane, PAGE4, tab4, "Description de la page 4");

    frame.add(tabbedPane);
    frame.setVisible(true);

    return new LinkedHashMap<>();
  }

  private static void addTab(JTabbedPane tabbedPane, String title, JPanel panel, String tooltip) {
    tabbedPane.addTab(title, panel);
    if (tooltip != null) {
      tabbedPane.setToolTipTextAt(tabbedPane.indexOfComponent(panel), tooltip);
    }
  }

  private JFrame createFrame() {
    final JFrame frame = new JFrame("SimSDP Analyis");
    frame.setSize(1000, 800);
    frame.setLocationRelativeTo(null);
    return frame;
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
