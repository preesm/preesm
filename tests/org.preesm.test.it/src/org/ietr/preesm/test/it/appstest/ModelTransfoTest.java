/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
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
 */
@RunWith(Parameterized.class)
public class ModelTransfoTest {

  final String  workflow;
  final String  scenario;
  final String  projectName;
  final boolean shouldSuccess;

  /**
   */
  public ModelTransfoTest(final String projectName, final String workflow, final String scenario,
      final boolean shouldSuccess) {
    this.scenario = scenario;
    this.workflow = workflow;
    this.projectName = projectName;
    this.shouldSuccess = shouldSuccess;
  }

  /**
   *
   */
  @Parameters(name = "{index} - {0}:: {1} - {2} => {3}")
  public static Collection<Object[]> data() {
    final List<Object[]> params = new ArrayList<>();

    String projectName = "org.ietr.preesm.model_tests";
    String[] scenarios = new String[] { "Tests_Flat/simple.scenario", "Tests_Flat/delay_simple.scenario",
        "Tests_Flat/delay_setter_getter.scenario" };
    String[] workflows = new String[] { "StaticPiMM2SRDAGCodegen.workflow", "StaticPiMMFlattenerCodegen.workflow" };
    for (String workflow : workflows) {
      for (String scenario : scenarios) {
        params.add(new Object[] { projectName, workflow, scenario, true });
      }
    }

    projectName = "org.ietr.preesm.model_tests";
    scenarios = new String[] { "Tests_H/simple_H.scenario", "Tests_H/simple_H_delay_p.scenario",
        "Tests_H/simple_H_delay_no_p.scenario", "Tests_H/simple_H_delay_p_no_p.scenario",
        "Tests_H/simple_H_delay_p_p.scenario" };
    workflows = new String[] { "StaticPiMM2SRDAGCodegen.workflow", "StaticPiMMFlattenerCodegen.workflow" };
    for (String workflow : workflows) {
      for (String scenario : scenarios) {
        final boolean shouldFail = scenario.equals("Tests_H/simple_H_delay_p_no_p.scenario")
            && workflow.equals("StaticPiMMFlattenerCodegen.workflow");
        params.add(new Object[] { projectName, workflow, scenario, !shouldFail });
      }
    }

    projectName = "org.ietr.preesm.model_tests";
    scenarios = new String[] { "Tests_H/nested_H_simple.scenario", "Tests_H/nested_H_BR.scenario",
        "Tests_H/nested_H_RB.scenario", "Tests_H/nested_H_delay_p.scenario", "Tests_H/nested_H_delay_no_p.scenario",
        "Tests_H/nested_H_delay_p_no_p.scenario", "Tests_H/nested_H_delay_p_p.scenario" };
    workflows = new String[] { "StaticPiMM2SRDAGCodegen.workflow", "StaticPiMMFlattenerCodegen.workflow" };
    for (String workflow : workflows) {
      for (String scenario : scenarios) {
        final boolean shouldFail = scenario.equals("Tests_H/nested_H_delay_p_no_p.scenario")
            && workflow.equals("StaticPiMMFlattenerCodegen.workflow");
        params.add(new Object[] { projectName, workflow, scenario, !shouldFail });
      }
    }

    return params;
  }

  @Test
  public void testModelTransfos() throws IOException, CoreException {
    final String workflowFilePathStr = "/Workflows/" + workflow;
    final String scenarioFilePathStr = "/Scenarios/" + scenario;
    final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
    Assert.assertEquals("Workflow [" + workflow + "] with scenario [" + scenario + "] ended unexpectedly",
        shouldSuccess, success);
  }

}
