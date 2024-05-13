package org.ietr.preesm.test.it.appstest;

import java.io.IOException;
import org.eclipse.core.runtime.CoreException;
import org.ietr.preesm.test.it.api.WorkflowRunner;
import org.junit.Assert;
import org.junit.Test;

public class TasksTest {

  @Test
  public void testThroughputShouldBeComputedDuringWorkflow() throws IOException, CoreException {
    final String projectName = "org.ietr.preesm.sobel";
    final String scenario = "1core.scenario";
    final String workflow = "Tasks.workflow";

    final String workflowFilePathStr = "/Workflows/" + workflow;
    final String scenarioFilePathStr = "/Scenarios/" + scenario;
    final boolean success = WorkflowRunner.runWorkFlow(null, projectName, workflowFilePathStr, scenarioFilePathStr);
    Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
  }
}
