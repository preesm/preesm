/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020 - 2023) :
 *
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020 - 2022)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
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
    for (final String workflow : WORKFLOWS_GRAPH) {
      for (final String scenario : SCENARIOS) {
        params.add(new Object[] { workflow, scenario });
      }
    }
    for (final String workflow : WORKFLOWS_CLUSTER) {
      for (final String scenario : SCENARIOS) {
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
    final boolean success = WorkflowRunner.runWorkFlow(null, PROJECT, workflowFilePathStr, scenarioFilePathStr);
    Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
  }
}
