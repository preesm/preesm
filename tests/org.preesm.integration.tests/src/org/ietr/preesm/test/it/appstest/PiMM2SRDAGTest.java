/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2025) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023 - 2025)
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
import java.util.stream.Stream;
import org.eclipse.core.runtime.CoreException;
import org.ietr.preesm.test.it.api.WorkflowRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 *
 */
class PiMM2SRDAGTest {

  static Stream<Arguments> rlData() {
    final String projectName = "org.ietr.preesm.reinforcement_learning";
    final String[] scenarios = new String[] { "1corePrediction.scenario", "4corePrediction.scenario",
      "1coreTraining.scenario", "4coreTraining.scenario" };
    final String[] workflows = new String[] { "StaticPiMM2SRDAGCodegen.workflow" };

    return Stream.of(workflows)
        .flatMap(workflow -> Stream.of(scenarios).map(scenario -> Arguments.of(workflow, scenario, projectName)));
  }

  static Stream<Arguments> sobelData() {
    final String projectName = "org.ietr.preesm.sobel";
    final String[] scenarios = new String[] { "1core.scenario", "4core.scenario", "8coreC6678.scenario" };
    final String[] workflows = new String[] { "StaticPiMM2SRDAGCodegen.workflow", "XMLCodegen.workflow" };

    return Stream.of(workflows)
        .flatMap(workflow -> Stream.of(scenarios).map(scenario -> Arguments.of(workflow, scenario, projectName)));
  }

  static Stream<Arguments> stereoData() {
    final String projectName = "org.ietr.preesm.stereo";
    final String[] scenarios = new String[] { "1core.scenario", "4core.scenario", "8coresC6678.scenario",
      "lowMaxDisparity.scenario", "YUV4core.scenario" };
    final String[] workflows = new String[] { "PiMM2SRDAGCodegenMemoryScriptsMixedMerged.workflow",
      "StaticPiMM2SRDAGCodegen.workflow", "XMLCodegen.workflow" };

    return Stream.of(workflows)
        .flatMap(workflow -> Stream.of(scenarios).map(scenario -> Arguments.of(workflow, scenario, projectName)));
  }

  @ParameterizedTest(name = "{2} - {0} - {1}")
  @MethodSource({ "rlData", "stereoData", "sobelData" })
  void testSrdag(final String workflow, final String scenario, final String projectName)
      throws IOException, CoreException {
    final String workflowFilePathStr = "/Workflows/" + workflow;
    final String scenarioFilePathStr = "/Scenarios/" + scenario;
    final boolean success = WorkflowRunner.runWorkFlow(null, projectName, workflowFilePathStr, scenarioFilePathStr);
    Assertions.assertTrue(success, () -> "Workflow [" + workflow + "] with scenario [" + scenario + "] caused failure");
  }
}
