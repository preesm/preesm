/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2019)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011 - 2012)
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
package org.preesm.model.scenario.workflow;

import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.PreesmScenario;
import org.preesm.model.scenario.serialize.ScenarioParser;
import org.preesm.model.slam.Design;
import org.preesm.workflow.implement.AbstractScenarioImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * Implementing the new DFTools scenario node behavior for Preesm. This version generates an architecture with the
 * S-LAM2 meta-model type and an algorithm with the IBSDF type
 *
 * @author mpelcat
 * @author kdesnos
 */
@PreesmTask(id = "org.ietr.preesm.scenario.task", name = "scenario",

    outputs = { @Port(name = "scenario", type = PreesmScenario.class),
        @Port(name = "architecture", type = Design.class), @Port(name = "PiMM", type = PiGraph.class) }

)
public class AlgorithmAndArchitectureScenarioNode extends AbstractScenarioImplementation {

  /**
   * The scenario node in Preesm outputs three elements: Algorithm (SDF or PiMM), architecture and scenario.
   *
   * @param path
   *          the path
   * @return the map
   * @throws PreesmException
   *           the workflow exception
   */
  @Override
  public Map<String, Object> extractData(final String path) {

    final Map<String, Object> outputs = new LinkedHashMap<>();

    // Retrieving the scenario from the given path
    final ScenarioParser scenarioParser = new ScenarioParser();

    final Path relativePath = new Path(path);
    final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(relativePath);

    PreesmScenario scenario;
    // Retrieving the algorithm
    PiGraph piAlgorithm = null;

    try {
      scenario = scenarioParser.parseXmlFile(file);
      piAlgorithm = scenario.getAlgorithm();
      applyScenarioParameterValues(scenario, piAlgorithm);
    } catch (FileNotFoundException | CoreException e) {
      throw new PreesmRuntimeException(e.getMessage());
    }

    // Retrieving the architecture
    final Design slamDesign = scenario.getDesign();

    if (!scenario.isProperlySet()) {
      throw new PreesmRuntimeException(
          "Scenario is not complete. Please make sure PiSDF algorithm and Slam design are set properly.");
    }

    outputs.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);
    outputs.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, piAlgorithm);
    outputs.put(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, slamDesign);
    return outputs;
  }

  private void applyScenarioParameterValues(final PreesmScenario scenario, final PiGraph piAlgorithm) {
    for (final Entry<Parameter, String> paramValue : scenario.getParameterValueManager().getParameterValues()
        .entrySet()) {

      final String newValue = paramValue.getValue();
      final String expression = paramValue.getKey().getExpression().getExpressionAsString();

      if (newValue != null) {
        paramValue.getKey().setExpression(newValue);
      } else if (expression != null) {
        paramValue.getKey().setExpression(expression);
      } else {
        // keep value from PiSDF graph
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Scenario, algorithm and architecture parsing.";
  }

}
