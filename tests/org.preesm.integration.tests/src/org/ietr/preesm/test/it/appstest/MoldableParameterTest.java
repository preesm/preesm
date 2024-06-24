package org.ietr.preesm.test.it.appstest;

import java.io.IOException;
import org.eclipse.core.runtime.CoreException;
import org.ietr.preesm.test.it.api.WorkflowRunner;
import org.junit.Assert;
import org.junit.Test;

public class MoldableParameterTest {

  @Test
  public void testScheduleOverflow() throws IOException, CoreException {
    final String projectName = "preesmSiftMparam";
    final String scenario = "/Scenarios/" + "Hvideo_1CoresX86.scenario";
    final String workflow = "/Workflows/" + "LatestCodegenMparamsSelection.workflow";

    final boolean success = WorkflowRunner.runWorkFlow(null, projectName, workflow, scenario);
    Assert.assertTrue("[FAILED] Workflow [" + workflow + "] with scenario [" + scenario + "] failed.", success);
  }

}
