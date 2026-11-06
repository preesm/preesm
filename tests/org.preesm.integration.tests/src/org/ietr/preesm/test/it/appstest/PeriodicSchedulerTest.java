package org.ietr.preesm.test.it.appstest;

import java.io.IOException;
import org.eclipse.core.runtime.CoreException;
import org.ietr.preesm.test.it.api.WorkflowRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PeriodicSchedulerTest {

  @Test
  void testPeriodicScheduler() throws IOException, CoreException {
    final String projectName = "org.ietr.preesm.sobel";
    final String scenario = "/Scenarios/" + "4core.scenario";
    final String workflow = "/Workflows/" + "CodegenPeriod.workflow";

    final boolean success = WorkflowRunner.runWorkFlow(null, projectName, workflow, scenario);
    Assertions.assertTrue(success, "[FAILED] Workflow [" + workflow + "] with scenario [" + scenario + "] failed.");
  }

}
