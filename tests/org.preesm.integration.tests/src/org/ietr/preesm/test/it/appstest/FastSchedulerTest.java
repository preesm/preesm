package org.ietr.preesm.test.it.appstest;

import java.io.IOException;
import org.eclipse.core.runtime.CoreException;
import org.ietr.preesm.test.it.api.WorkflowRunner;
import org.junit.Assert;
import org.junit.Test;

public class FastSchedulerTest {

  @Test
  public void testFastScheduler() throws IOException, CoreException {
    final String projectName = "org.ietr.preesm.stereo";
    final String scenario = "/Scenarios/" + "8coresC6678.scenario";
    final String workflow = "/Workflows/" + "CodegenFast.workflow";

    final boolean success = WorkflowRunner.runWorkFlow(null, projectName, workflow, scenario);
    Assert.assertTrue("[FAILED] Workflow [" + workflow + "] with scenario [" + scenario + "] failed.", success);
  }

  @Test
  public void testPFastScheduler() throws IOException, CoreException {
    final String projectName = "org.ietr.preesm.stereo";
    final String scenario = "/Scenarios/" + "8coresC6678.scenario";
    final String workflow = "/Workflows/" + "CodegenPFast.workflow";

    final boolean success = WorkflowRunner.runWorkFlow(null, projectName, workflow, scenario);
    Assert.assertTrue("[FAILED] Workflow [" + workflow + "] with scenario [" + scenario + "] failed.", success);
  }

}
