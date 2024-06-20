package org.ietr.preesm.test.it.appstest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.ietr.preesm.test.it.api.WorkflowRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TasksTest {

  final String workflow;
  final String scenario;
  final String projectName;

  public TasksTest(final String workflow, final String scenario, final String projectName) {
    this.scenario = scenario;
    this.workflow = workflow;
    this.projectName = projectName;
  }

  @Parameters(name = "{2} - {0} - {1}")
  public static Collection<Object[]> data() {

    final List<Object[]> params = new ArrayList<>();

    params.add(new Object[] { "Tasks.workflow", "1core.scenario", "org.ietr.preesm.sobel" });

    params.add(new Object[] { "CodegenEnergy.workflow", "4core.scenario", "org.ietr.preesm.sobel" });

    params.add(new Object[] { "CodegenAutoDelay.workflow", "4coreX86.scenario", "org.ietr.preesm.stabilization" });

    return params;
  }

  @Test
  public void test() throws IOException, CoreException {
    final String workflowFilePathStr = "/Workflows/" + workflow;
    final String scenarioFilePathStr = "/Scenarios/" + scenario;
    final boolean success = WorkflowRunner.runWorkFlow(null, projectName, workflowFilePathStr, scenarioFilePathStr);
    Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
  }
}
