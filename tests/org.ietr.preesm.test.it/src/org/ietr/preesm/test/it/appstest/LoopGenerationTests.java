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
public class LoopGenerationTests {
  @Test
  public void testLoopGenFlow() throws FileNotFoundException, InvalidModelException, CoreException {
    final String projectName = "org.ietr.preesm.loopgen-sobel-erosion-dilation";

    // fill workflows to set
    final List<String> workflows = new ArrayList<String>();
    workflows.add("CodegenDistribNoFlat");
    workflows.add("CodegenDistribFlat1");

    // fill scenarios to set
    final List<String> scenarios = new ArrayList<String>();
    scenarios.add("4core");
    scenarios.add("4core2");

    for (String w : workflows) {
      for (String s : scenarios) {
        final String workflowFilePathStr = "/Workflows/" + w + ".workflow";
        final String scenarioFilePathStr = "/Scenarios/" + s + ".scenario";
        final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
        if (success == false) {
          System.out.print("[FAILED] Workflow " + workflowFilePathStr + " Scenario " + scenarioFilePathStr);
        }
        Assert.assertTrue(success);
      }
    }
  }
}
