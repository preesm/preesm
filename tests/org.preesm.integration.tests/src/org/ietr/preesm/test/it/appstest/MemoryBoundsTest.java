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

/**
 *
 */
@RunWith(Parameterized.class)
public class MemoryBoundsTest {

  final String workflow;
  final String scenario;
  final String projectName;

  /**
   */
  public MemoryBoundsTest(final String workflow, final String scenario, final String projectName) {
    this.scenario = scenario;
    this.workflow = workflow;
    this.projectName = projectName;
  }

  /**
   *
   */
  @Parameters(name = "{2} - {0} - {1}")
  public static Collection<Object[]> data() {

    final List<Object[]> params = new ArrayList<>();

    final String projectName = "org.preesm.algorithm.memory.bounds.test";
    final String scenario = "bufferAggregate.scenario";
    final String[] workflows = new String[] { "CodegenHeuristic.workflow", "CodegenOstergard.workflow",
        "CodegenYamaguchi.workflow" };
    for (final String workflow : workflows) {
      params.add(new Object[] { workflow, scenario, projectName });
    }
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
