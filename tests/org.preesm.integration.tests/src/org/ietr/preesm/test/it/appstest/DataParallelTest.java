/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2023) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2022)
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
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.CoreException;
import org.ietr.preesm.test.it.api.WorkflowRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 *
 * Testing fi.abo.preesm.dataparallel plugin;
 *
 */
@RunWith(Parameterized.class)
public class DataParallelTest {

  static final String   projectName = "fi.abo.preesm.data_par_eg";
  static final String   workflow    = "Graph.workflow";
  static final String[] scenarios   = new String[] { "acyclic_two_actors_1CoreX86.scenario",
      "costStrongComponent_1CoreX86.scenario", "Eg1_par_1CoreX86.scenario", "h263encoder_1CoreX86.scenario",
      "nestedStrongGraph_1CoreX86.scenario", "self_loop_1CoreX86.scenario",
      "semantically_acyclic_cycle_1CoreX86.scenario", "sobel_morpho_1CoreX86.scenario", "stereo_top_1CoreX86.scenario",
      "strict_1_cycle_1CoreX86.scenario", "strict_1_cycle_dual_1CoreX86.scenario", "strict_cycle_1CoreX86.scenario",
      "three_cycles_1CoreX86.scenario", "top_display2_1CoreX86.scenario", "two_actor_cycle_1CoreX86.scenario" };

  final String scenario;

  public DataParallelTest(final String scenario) {
    this.scenario = scenario;
  }

  /**
  *
  */
  @Parameters(name = "{0}")
  public static Collection<Object[]> data() {
    final Object[][] params = new Object[scenarios.length][1];
    int i = 0;
    for (final String scenario : scenarios) {
      params[i][0] = scenario;
      i++;
    }
    return Arrays.asList(params);
  }

  @Test
  public void testAll() throws IOException, CoreException {
    final String workflowFilePathStr = "/Workflows/" + workflow;
    final String scenarioFilePathStr = "/Scenarios/" + scenario;
    final boolean success = WorkflowRunner.runWorkFlow(null, projectName, workflowFilePathStr, scenarioFilePathStr);
    Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
  }
}
