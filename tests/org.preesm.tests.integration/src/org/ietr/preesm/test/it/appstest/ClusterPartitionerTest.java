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
 * Testing Cluster Partitioner Task
 * 
 * @author dgageot
 *
 */
@RunWith(Parameterized.class)
public class ClusterPartitionerTest {

  static final String   PROJECT   = "org.ietr.preesm.sobel-morpho.partitioner";
  static final String[] SCENARIOS = new String[] { "MPPA2Cluster.scenario" };
  static final String[] WORKFLOWS = new String[] { "CodegenAutomaticClustering.workflow" };

  final String workflow;
  final String scenario;

  public ClusterPartitionerTest(final String workflow, final String scenario) {
    this.scenario = scenario;
    this.workflow = workflow;
  }

  /**
  *
  */
  @Parameters(name = "{0} - {1}")
  public static Collection<Object[]> data() {
    final List<Object[]> params = new ArrayList<>();
    for (String workflow : WORKFLOWS) {
      for (String scenario : SCENARIOS) {
        params.add(new Object[] { workflow, scenario });
      }
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
