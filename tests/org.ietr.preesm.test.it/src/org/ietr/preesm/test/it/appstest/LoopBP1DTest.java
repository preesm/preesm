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
public class LoopBP1DTest {
  @Test
  public void testLoopBP1DFlow() throws FileNotFoundException, InvalidModelException, CoreException {
    final String projectName = "org.ietr.preesm.bp1d";

    // fill workflows to set
    final List<String> workflows = new ArrayList<>();
    workflows.add("CodegenDistribNoFlat");

    // fill scenarios to set
    final List<String> scenarios = new ArrayList<>();
    scenarios.add("1core");
    scenarios.add("4core");
    scenarios.add("MPPA2Explicit");

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
