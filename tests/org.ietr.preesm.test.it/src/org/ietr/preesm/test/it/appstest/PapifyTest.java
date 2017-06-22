package org.ietr.preesm.test.it.appstest;

import java.io.IOException;
import org.eclipse.core.runtime.CoreException;
import org.ietr.preesm.test.it.api.WorkflowRunner;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * Add test for the new Papify codegen
 *
 * see commit d105c7dbfdb17314e863e76994910f24cdacf901
 *
 * https://github.com/preesm/preesm/commit/d105c7dbfdb17314e863e76994910f24cdacf901
 *
 * @author anmorvan
 *
 */
public class PapifyTest {

  @Test
  public void testLargeFFT11() throws IOException, CoreException {
    final String projectName = "org.ietr.preesm.sobel_parallel";
    final String[] scenarios = new String[] { "1core.scenario", "2core.scenario", "4core.scenario" };
    final String[] workflows = new String[] { "Codegen.workflow" };

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
