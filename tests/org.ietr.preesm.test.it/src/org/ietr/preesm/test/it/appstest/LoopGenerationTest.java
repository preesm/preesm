package org.ietr.preesm.test.it.appstest;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.preesm.test.it.api.WorkflowRunner;
import org.junit.Assert;
import org.junit.Test;

/**
*
*/
public class LoopGenerationTest {
  @Test
  public void testLoopGenFlow() throws FileNotFoundException, InvalidModelException, CoreException {
    final String projectName = "org.ietr.preesm.loopgen-sobel-erosion-dilation";

    // fill workflows to set
    final List<String> workflows = new ArrayList<>();
    workflows.add("CodegenDistribNoFlat");
    workflows.add("CodegenDistribFlat1");

    // fill scenarios to set
    final List<String> scenarios = new ArrayList<>();
    scenarios.add("4core");
    scenarios.add("4core2");

    for (final String w : workflows) {
      for (final String s : scenarios) {
        final String workflowFilePathStr = "/Workflows/" + w + ".workflow";
        final String scenarioFilePathStr = "/Scenarios/" + s + ".scenario";
        final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
        Assert.assertTrue("[FAILED] Workflow " + workflowFilePathStr + " Scenario " + scenarioFilePathStr, success);
      }
    }
  }
}
