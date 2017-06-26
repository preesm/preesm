/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2009 - 2013)
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
package org.ietr.preesm.mapper.scenariogen;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.architecture.slam.component.Operator;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.Activator;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.serialize.ScenarioParser;
import org.ietr.preesm.core.types.ImplementationPropertyNames;

// TODO: Auto-generated Javadoc
/**
 * This class defines a method to load a new scenario and optionally change some constraints from an output DAG.
 *
 * @author mpelcat
 */
public class ScenarioGenerator extends AbstractTaskImplementation {

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();

    parameters.put("scenarioFile", "");
    parameters.put("dagFile", "");

    return parameters;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "generating a scenario";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map, org.eclipse.core.runtime.IProgressMonitor,
   * java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters, final IProgressMonitor monitor,
      final String nodeName, final Workflow workflow) throws WorkflowException {

    final Map<String, Object> outputs = new LinkedHashMap<>();

    WorkflowLogger.getLogger().log(Level.INFO, "Generating scenario");

    final String scenarioFileName = parameters.get("scenarioFile");

    // Retrieving the scenario
    if (scenarioFileName.isEmpty()) {
      WorkflowLogger.getLogger().log(Level.SEVERE, "lack of a scenarioFile parameter");
      return null;
    } else {

      // Retrieving the scenario from the given path
      final ScenarioParser parser = new ScenarioParser();

      final Path relativePath = new Path(scenarioFileName);
      final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(relativePath);

      PreesmScenario scenario;
      // Retrieving the algorithm
      SDFGraph algo;

      try {
        scenario = parser.parseXmlFile(file);

        algo = ScenarioParser.getSDFGraph(scenario.getAlgorithmURL());
      } catch (final Exception e) {
        throw new WorkflowException(e.getMessage());
      }

      if (algo == null) {
        WorkflowLogger.getLogger().log(Level.SEVERE, "cannot retrieve algorithm");
        return null;
      } else {
        outputs.put("SDF", algo);
      }

      // Retrieving the architecture
      final Design archi = ScenarioParser.parseSlamDesign(scenario.getArchitectureURL());
      if (archi == null) {
        WorkflowLogger.getLogger().log(Level.SEVERE, "cannot retrieve architecture");
        return null;
      } else {
        outputs.put("architecture", archi);
      }

    }

    final String dagFileName = parameters.get("dagFile");
    // Parsing the output DAG if present and updating the constraints
    if (dagFileName.isEmpty()) {
      WorkflowLogger.getLogger().log(Level.WARNING, "No dagFile -> retrieving the scenario as is");
    } else {
      final GMLMapperDAGImporter importer = new GMLMapperDAGImporter();

      ((PreesmScenario) outputs.get("scenario")).getConstraintGroupManager().removeAll();
      ((PreesmScenario) outputs.get("scenario")).getTimingManager().removeAll();

      try {
        final Path relativePath = new Path(dagFileName);
        final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(relativePath);

        Activator.updateWorkspace();
        SDFGraph graph = importer.parse(file.getContents(), dagFileName);
        graph = importer.parse(file.getContents(), dagFileName);

        for (final SDFAbstractVertex dagV : graph.vertexSet()) {
          final String vName = (String) dagV.getPropertyBean().getValue("name");
          final String opName = (String) dagV.getPropertyBean().getValue(ImplementationPropertyNames.Vertex_Operator);
          final String timeStr = (String) dagV.getPropertyBean().getValue(ImplementationPropertyNames.Task_duration);
          final SDFAbstractVertex sdfV = ((SDFGraph) outputs.get("SDF")).getVertex(vName);
          final ComponentInstance op = DesignTools.getComponentInstance((Design) outputs.get("architecture"), opName);

          if ((sdfV != null) && (op != null) && (op.getComponent() instanceof Operator)) {
            ((PreesmScenario) outputs.get("scenario")).getConstraintGroupManager().addConstraint(opName, sdfV);
            ((PreesmScenario) outputs.get("scenario")).getTimingManager().setTiming(sdfV.getName(), op.getComponent().getVlnv().getName(),
                Long.parseLong(timeStr));
          }
        }

      } catch (final Exception e) {
        WorkflowLogger.getLogger().log(Level.SEVERE, e.getMessage());
      }

    }

    return outputs;
  }

}
