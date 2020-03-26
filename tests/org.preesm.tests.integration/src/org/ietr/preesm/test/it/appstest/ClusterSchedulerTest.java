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
 * Testing Cluster Scheduler Algorithm
 * 
 * @author dgageot
 * @author jheulot
 *
 */
@RunWith(Parameterized.class)
public class ClusterSchedulerTest {

  static final String   PROJECT           = "org.ietr.preesm.cluster.scheduler";
  static final String[] SCENARIOS         = new String[] { "1coreFlat.scenario", "1coreSobel.scenario",
      "4coreSobel.scenario" };
  static final String[] WORKFLOWS_GRAPH   = new String[] { "CodegenGraphPerformance.workflow",
      "CodegenGraphMemory.workflow" };
  static final String[] WORKFLOWS_CLUSTER = new String[] { "CodegenClusterPerformance.workflow",
      "CodegenClusterMemory.workflow" };

  final String workflow;
  final String scenario;

  public ClusterSchedulerTest(final String workflow, final String scenario) {
    this.scenario = scenario;
    this.workflow = workflow;
  }

  /**
  *
  */
  @Parameters(name = "{0} - {1}")
  public static Collection<Object[]> data() {
    final List<Object[]> params = new ArrayList<>();
    for (String workflow : WORKFLOWS_GRAPH) {
      for (String scenario : SCENARIOS) {
        params.add(new Object[] { workflow, scenario });
      }
    }
    for (String workflow : WORKFLOWS_CLUSTER) {
      for (String scenario : SCENARIOS) {
        params.add(new Object[] { workflow, scenario });
      }
      params.add(new Object[] { workflow, "1coreHierarchical.scenario" });
    }
    return params;
  }

  @Test
  public void testClusterScheduler() throws IOException, CoreException {
    final String workflowFilePathStr = "/Workflows/" + workflow;
    final String scenarioFilePathStr = "/Scenarios/" + scenario;
    final boolean success = WorkflowRunner.runWorkFlow(PROJECT, workflowFilePathStr, scenarioFilePathStr);
    Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
  }
}
