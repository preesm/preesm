/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
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
 * Testing SCAPE graph transformation
 *
 * @author orenaud
 */

@RunWith(Parameterized.class)
public class SCAPEPartitioningTest {

  final String workflow;
  final String scenario;
  final String projectName;

  public SCAPEPartitioningTest(final String workflow, final String scenario, final String projectName) {
    this.scenario = scenario;
    this.workflow = workflow;
    this.projectName = projectName;
  }

  @Parameters(name = "{2} - {0} - {1}")
  public static Collection<Object[]> data() {

    final String SCAPE2 = "scape2.workflow";
    final String ABC_3C = "ABC_3CoresX86.scenario";
    final String ABC2_3C = "ABC2_3CoresX86.scenario";
    final String ABC3_3C = "ABC3_3CoresX86.scenario";

    final List<Object[]> params = new ArrayList<>();

    final String testProjectName = "org.ietr.preesm.scape";
    final String[] testScenarios = new String[] { "ABC_5CoresX86.scenario", ABC2_3C, ABC_3C, ABC2_3C, ABC_3C, ABC_3C,
        "top_3CoresX86.scenario", "ABC2_3CoresX86.scenario", ABC3_3C };
    final String[] testWorkflows = new String[] { "euclide.workflow", "data.workflow", "pip.workflow",
        "scape1.workflow", SCAPE2, SCAPE2, SCAPE2, "schedule.workflow", SCAPE2 };
    for (int i = 0; i < testScenarios.length; i++) {
      params.add(new Object[] { testWorkflows[i], testScenarios[i], testProjectName });
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
