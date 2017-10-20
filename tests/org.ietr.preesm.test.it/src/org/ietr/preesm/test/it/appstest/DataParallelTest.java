package org.ietr.preesm.test.it.appstest;

import java.io.IOException;
import org.eclipse.core.runtime.CoreException;
import org.ietr.preesm.test.it.api.WorkflowRunner;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * Testing fi.abo.preesm.dataparallel plugin;
 *
 */
public class DataParallelTest {

  @Test
  public void testAll() throws IOException, CoreException {
    final String projectName = "fi.abo.preesm.data_par_eg";
    final String[] scenarios = new String[] { "acyclic_two_actors_1CoreX86.scenario", "costStrongComponent_1CoreX86.scenario", "Eg1_par_1CoreX86.scenario",
        "h263encoder_1CoreX86.scenario", "nestedStrongGraph_1CoreX86.scenario", "self_loop_1CoreX86.scenario", "semantically_acyclic_cycle_1CoreX86.scenario",
        "sobel_morpho_1CoreX86.scenario", "stereo_top_1CoreX86.scenario", "strict_1_cycle_1CoreX86.scenario", "strict_1_cycle_dual_1CoreX86.scenario",
        "strict_cycle_1CoreX86.scenario", "three_cycles_1CoreX86.scenario", "top_display2_1CoreX86.scenario", "two_actor_cycle_1CoreX86.scenario" };
    final String[] workflows = new String[] { "Graph.workflow" };

    for (final String workflow : workflows) {
      for (final String scenario : scenarios) {
        final String workflowFilePathStr = "/Workflows/" + workflow;
        final String scenarioFilePathStr = "/Scenarios/" + scenario;
        final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
        Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
      }
    }
  }
}
