package org.ietr.preesm.test.it.appstest;

import java.io.IOException;
import org.eclipse.core.runtime.CoreException;
import org.ietr.preesm.test.it.api.WorkflowRunner;
import org.junit.Assert;
import org.junit.Test;

/**
 * Integration test for the throughput plug-in
 * 
 * @author hderoui
 *
 */
public class ThroughputPluginTest {

  @Test
  public void testThroughputShouldBeComputedDuringWorkflow() throws IOException, CoreException {
    final String projectName = "org.ietr.preesm.tutorials.throughputEvaluationIBSDF";
    final String[] scenarios = new String[] { "scenario.scenario" };
    final String[] workflows = new String[] { "ThroughputEvaluation.workflow" };

    for (final String workflow : workflows) {
      for (final String scenario : scenarios) {
        final String workflowFilePathStr = "/Workflows/" + workflow;
        final String scenarioFilePathStr = "/Scenarios/" + scenario;
        final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
        Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
      }
    }
  }
}
