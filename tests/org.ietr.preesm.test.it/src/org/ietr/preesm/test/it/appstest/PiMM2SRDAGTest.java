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
public class PiMM2SRDAGTest {

  final String workflow;
  final String scenario;
  final String projectName;

  /**
   */
  public PiMM2SRDAGTest(final String workflow, final String scenario, final String projectName) {
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

    String projectName = "org.ietr.preesm.reinforcement_learning";
    String[] scenarios = new String[] { "1corePrediction.scenario", "4corePrediction.scenario",
        "1coreTraining.scenario", "4coreTraining.scenario" };
    String[] workflows = new String[] { "StaticPiMM2SRDAGCodegen.workflow", "StaticPiMMCodegen.workflow" };
    for (String workflow : workflows) {
      for (String scenario : scenarios) {
        params.add(new Object[] { workflow, scenario, projectName });
      }
    }

    projectName = "org.ietr.preesm.stereo";
    scenarios = new String[] { "1core.scenario", "4core.scenario", "8coresC6678.scenario", "lowMaxDisparity.scenario",
        "YUV4core.scenario" };
    workflows = new String[] { "PiMM2SRDAGCodegenMemoryScriptsMixedMerged.workflow", "StaticPiMM2SRDAGCodegen.workflow",
        "XMLCodegen.workflow" };
    for (String workflow : workflows) {
      for (String scenario : scenarios) {
        params.add(new Object[] { workflow, scenario, projectName });
      }
    }

    projectName = "org.ietr.preesm.sobel";
    scenarios = new String[] { "1core.scenario", "4core.scenario", "8coreC6678.scenario" };
    workflows = new String[] { "StaticPiMM2SRDAGCodegen.workflow", "XMLCodegen.workflow" };
    for (String workflow : workflows) {
      for (String scenario : scenarios) {
        params.add(new Object[] { workflow, scenario, projectName });
      }
    }

    return params;
  }

  @Test
  public void testSrdag() throws IOException, CoreException {
    final String workflowFilePathStr = "/Workflows/" + workflow;
    final String scenarioFilePathStr = "/Scenarios/" + scenario;
    final boolean success = WorkflowRunner.runWorkFlow(projectName, workflowFilePathStr, scenarioFilePathStr);
    Assert.assertTrue("Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure", success);
  }
}
