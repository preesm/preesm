/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
package org.preesm.algorithm.mapper.exporter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.commons.files.PathTools;
import org.preesm.commons.files.WorkspaceUtils;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.workflow.WorkflowException;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * Block in workflow exporting a DAG that contains all information of an implementation.
 *
 * @author mpelcat
 */
public class ImplExportTransform extends AbstractTaskImplementation {

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map,
   * org.eclipse.core.runtime.IProgressMonitor, java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) throws WorkflowException {

    final DirectedAcyclicGraph dag = (DirectedAcyclicGraph) inputs.get("DAG");

    final String sGraphmlPath = PathTools.getAbsolutePath(parameters.get("path"), workflow.getProjectName());
    final Path graphmlPath = new Path(sGraphmlPath);

    // Exporting the DAG in a GraphML
    if (!graphmlPath.isEmpty()) {
      exportGraphML(dag, graphmlPath);
    }

    WorkspaceUtils.updateWorkspace();

    final Map<String, Object> outputs = new LinkedHashMap<>();
    outputs.put("xml", sGraphmlPath);
    return outputs;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();

    parameters.put("path", "");
    return parameters;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Exporting implementation.";
  }

  /**
   * Export graph ML.
   *
   * @param dag
   *          the dag
   * @param path
   *          the path
   */
  private void exportGraphML(final DirectedAcyclicGraph dag, final Path path) {

    final MapperDAG mapperDag = (MapperDAG) dag;

    final ImplementationExporter exporter = new ImplementationExporter();
    final MapperDAG clone = mapperDag.copy();
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IFile iGraphMLFile = workspace.getRoot().getFile(path);

    if (iGraphMLFile.getLocation() != null) {
      exporter.export(clone, iGraphMLFile.getLocation().toOSString());
    } else {
      PreesmLogger.getLogger().log(Level.SEVERE, "The output file " + path + " can not be written.");
    }
  }

}
