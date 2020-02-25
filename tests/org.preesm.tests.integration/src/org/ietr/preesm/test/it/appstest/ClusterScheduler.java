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

  static final String[] scenarios = new String[] { "1coreFlat.scenario", "1coreHierarchical.scenario" };
  static final String[] workflows = new String[] { "CodegenGraph.workflow", "CodegenCluster.workflow" };

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
    final Object[][] params = new Object[2][2];
    params[0][0] = workflows[0];
    params[0][1] = scenarios[0];
    params[1][0] = workflows[1];
    params[1][1] = scenarios[1];
    return Arrays.asList(params);
  }

  @Test
  public void testClusterScheduler() throws IOException, CoreException {
    final String workflowFilePathStr = "/Workflows/" + workflow;
    final String scenarioFilePathStr = "/Scenarios/" + scenario;
    final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
    Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
  }
}
