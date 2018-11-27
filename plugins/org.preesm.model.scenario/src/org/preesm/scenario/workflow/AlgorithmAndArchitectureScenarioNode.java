/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
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
package org.preesm.scenario.workflow;

import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.serialize.PiParser;
import org.preesm.model.slam.Design;
import org.preesm.scenario.ParameterValue;
import org.preesm.scenario.PreesmScenario;
import org.preesm.scenario.serialize.ScenarioParser;
import org.preesm.workflow.WorkflowException;
import org.preesm.workflow.implement.AbstractScenarioImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * Implementing the new DFTools scenario node behavior for Preesm. This version generates an architecture with the
 * S-LAM2 meta-model type and an algorithm with the IBSDF type
 *
 * @author mpelcat
 * @author kdesnos
 *
 */
public class AlgorithmAndArchitectureScenarioNode extends AbstractScenarioImplementation {

  /**
   * The scenario node in Preesm outputs three elements: Algorithm (SDF or PiMM), architecture and scenario.
   *
   * @param path
   *          the path
   * @return the map
   * @throws WorkflowException
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
      final String url = scenario.getAlgorithmURL();
      if (scenario.isIBSDFScenario()) {
        throw new PreesmException("IBSDF is not supported anymore");
      } else if (scenario.isPISDFScenario()) {
        piAlgorithm = PiParser.getPiGraph(url);
        applyScenarioParameterValues(scenario, piAlgorithm);
      }
    } catch (FileNotFoundException | CoreException e) {
      throw new WorkflowException(e.getMessage());
    }

    // Retrieving the architecture
    final Design slamDesign = ScenarioParser.parseSlamDesign(scenario.getArchitectureURL());

    outputs.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);
    outputs.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, piAlgorithm);
    outputs.put(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, slamDesign);
    return outputs;
  }

  private void applyScenarioParameterValues(final PreesmScenario scenario, final PiGraph piAlgorithm) {
    for (final ParameterValue paramValue : scenario.getParameterValueManager().getParameterValues()) {

      final String variableName = paramValue.getName();
      final String newValue = paramValue.getValue();
      final String expression = paramValue.getExpression();
      final String parentVertex = paramValue.getParentVertex();

      if (newValue != null) {
        // note: need to lookup since graph reconnector may have changed Paramter objects
        final Parameter lookupParameterGivenGraph = piAlgorithm.lookupParameterGivenGraph(variableName, parentVertex);
        lookupParameterGivenGraph.setExpression(newValue);
      } else if (expression != null) {
        // note: need to lookup since graph reconnector may have changed Paramter objects
        final Parameter lookupParameterGivenGraph = piAlgorithm.lookupParameterGivenGraph(variableName, parentVertex);
        lookupParameterGivenGraph.setExpression(expression);
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
