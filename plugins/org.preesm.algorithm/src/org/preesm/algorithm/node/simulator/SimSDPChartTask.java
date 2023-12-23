package org.preesm.algorithm.node.simulator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * Preesm task for exporting deviation charts.
 *
 * This task generates a JFrame with multiple tabs for different analysis pages. Each page corresponds to a specific
 * aspect of the analysis, such as internode analysis, intranode analysis, DSE analysis, network analysis, pareto
 * analysis, and a final thank-you page.
 *
 * The task provides a visual representation of simulation results.
 *
 * @author orenaud
 */
@PreesmTask(id = "SimSDPChartTask.identifier", name = "Deviation chart exporter",
    category = "Deviation chart exporters")
public class SimSDPChartTask extends AbstractTaskImplementation {
  public static final String PAGE1     = "Internode Analysis";
  public static final String PAGE2     = "Intranode Analysis";
  public static final String PAGE3     = "DSE Analysis";
  public static final String PAGE4     = "Network Analysis";
  public static final String PAGE5     = "Pareto Analysis";
  public static final String PAGE_LAST = "Thank you";

  /**
   * Executes the deviation chart export task.
   *
   * @param inputs
   *          Input parameters for the task.
   * @param parameters
   *          Additional parameters for the task.
   * @param monitor
   *          Progress monitor for the task execution.
   * @param nodeName
   *          Name of the node executing the task.
   * @param workflow
   *          The workflow in which the task is executed.
   * @return A map containing the results of the task execution.
   * @throws InterruptedException
   *           If the execution is interrupted.
   */
  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) throws InterruptedException {
    final String path = "/" + workflow.getProjectName() + "/Simulation/";
    int iterationOptim = 0;
    final JFrame frame = createFrame();
    frame.setSize(800, 800);
    final JTabbedPane tabbedPane = new JTabbedPane();
    final JPanel tab1 = new AnalysisPage1(path, iterationOptim).execute();
    iterationOptim = AnalysisPage1.getIterationOptim();
    final JPanel tab2 = new AnalysisPage2(path, iterationOptim).execute();
    final JPanel tab3 = new AnalysisPage3(path, iterationOptim).execute();
    final JPanel tab4 = new AnalysisPage4(path).execute();
    final List<NetworkInfo> networkInfoNormalList = AnalysisPage4.getNetworkInfoNormalList();
    final JPanel tab5 = new AnalysisPage5(networkInfoNormalList).execute();

    final JPanel tabLast = new AnalysisLastPage().execute();
    addTab(tabbedPane, PAGE1, tab1, "Description de la page 1");
    addTab(tabbedPane, PAGE2, tab2, "Description de la page 2");
    addTab(tabbedPane, PAGE3, tab3, "Description de la page 3");
    addTab(tabbedPane, PAGE4, tab4, "Description de la page 4");
    addTab(tabbedPane, PAGE5, tab5, "Description de la page 5");

    addTab(tabbedPane, PAGE_LAST, tabLast, "Description de la page de fin");

    frame.add(tabbedPane);

    frame.setVisible(true);

    return new LinkedHashMap<>();
  }

  /**
   * Adds a tab to the JTabbedPane with the specified title, panel, and tooltip.
   *
   * @param tabbedPane
   *          The JTabbedPane to which the tab is added.
   * @param title
   *          The title of the tab.
   * @param panel
   *          The JPanel associated with the tab.
   * @param tooltip
   *          The tooltip for the tab.
   */
  private static void addTab(JTabbedPane tabbedPane, String title, JPanel panel, String tooltip) {
    tabbedPane.addTab(title, panel);
    if (tooltip != null) {
      tabbedPane.setToolTipTextAt(tabbedPane.indexOfComponent(panel), tooltip);
    }
  }

  /**
   * Creates and returns a JFrame for the deviation chart export.
   *
   * @return The created JFrame.
   */
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
