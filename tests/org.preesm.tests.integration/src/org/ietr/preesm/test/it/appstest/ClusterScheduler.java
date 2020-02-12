package org.ietr.preesm.test.it.appstest;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.CoreException;
import org.ietr.preesm.test.it.api.WorkflowRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author dgageot
 *
 */
@RunWith(Parameterized.class)
public class ClusterScheduler {

  static final String[] scenarios = new String[] { "1core.scenario" };
  static final String[] workflows = new String[] { "SchedulingClusters.workflow" };

  static final String projectName = "org.ietr.preesm.cluster.scheduler";

  final String workflow;
  final String scenario;

  public ClusterScheduler(final String workflow, final String scenario) {
    this.scenario = scenario;
    this.workflow = workflow;
  }

  /**
  *
  */
  @Parameters(name = "{0} - {1}")
  public static Collection<Object[]> data() {
    final Object[][] params = new Object[workflows.length * scenarios.length][2];
    int i = 0;
    for (String workflow : workflows) {
      for (String scenario : scenarios) {
        params[i][0] = workflow;
        params[i][1] = scenario;
        i++;
      }
    }
    return Arrays.asList(params);
  }

  @Test
  public void testLargeFFT() throws IOException, CoreException {
    final String workflowFilePathStr = "/Workflows/" + workflow;
    final String scenarioFilePathStr = "/Scenarios/" + scenario;
    final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
    Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
  }
}
