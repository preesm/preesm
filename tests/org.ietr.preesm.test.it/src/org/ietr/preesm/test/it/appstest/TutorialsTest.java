package org.ietr.preesm.test.it.appstest;

import java.io.FileNotFoundException;
import org.eclipse.core.runtime.CoreException;
import org.ietr.dftools.algorithm.importer.InvalidModelException;
import org.ietr.preesm.test.it.api.WorkflowRunner;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class TutorialsTest {

  @Test
  public void testStereoFlow() throws FileNotFoundException, InvalidModelException, CoreException {
    final String projectName = "org.ietr.preesm.stereo";
    final String workflowFilePathStr = "/Workflows/StaticPiMMCodegen.workflow";
    final String scenarioFilePathStr = "/Scenarios/1core.scenario";
    final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
    Assert.assertTrue(success);
  }
}
