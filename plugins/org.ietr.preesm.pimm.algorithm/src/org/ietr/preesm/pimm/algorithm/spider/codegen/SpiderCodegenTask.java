/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2015 - 2016)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2015)
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
package org.ietr.preesm.pimm.algorithm.spider.codegen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.pimm.algorithm.spider.codegen.visitor.SpiderCodegen;

// TODO: Auto-generated Javadoc
/**
 * The Class SpiderCodegenTask.
 */
public class SpiderCodegenTask extends AbstractTaskImplementation {

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map, org.eclipse.core.runtime.IProgressMonitor,
   * java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters, final IProgressMonitor monitor,
      final String nodeName, final Workflow workflow) throws WorkflowException {

    // Retrieve inputs
    final PreesmScenario scenario = (PreesmScenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final PiGraph pg = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);

    final SpiderCodegen launcher = new SpiderCodegen(scenario);

    launcher.initGenerator(pg);
    final String graphCode = launcher.generateGraphCode(pg);
    final String fctCode = launcher.generateFunctionCode(pg);
    final String hCode = launcher.generateHeaderCode(pg);

    // Get the workspace
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();

    // Get the name of the folder for code generation
    final String codegenPath = scenario.getCodegenManager().getCodegenDirectory() + "/";

    if (codegenPath.equals("/")) {
      WorkflowLogger.getLogger().log(Level.SEVERE, "Error: A Codegen folder must be specified in Scenario");
      return Collections.emptyMap();
    }

    final IFolder f = workspace.getRoot().getFolder(new Path(codegenPath));
    final File folder = new File(f.getRawLocation().toOSString());
    folder.mkdirs();

    // Create the files
    final String hFilePath = pg.getName() + ".h";
    final File hFile = new File(folder, hFilePath);

    final String piGraphfilePath = "pi_" + pg.getName() + ".cpp";
    final File piGraphFile = new File(folder, piGraphfilePath);

    final String piFctfilePath = "fct_" + pg.getName() + ".cpp";
    final File piFctFile = new File(folder, piFctfilePath);

    // Write the files
    FileWriter piGraphWriter;
    FileWriter piFctWriter;
    FileWriter hWriter;
    try {
      hWriter = new FileWriter(hFile);
      hWriter.write(hCode);
      hWriter.close();
      piGraphWriter = new FileWriter(piGraphFile);
      piGraphWriter.write(graphCode);
      piGraphWriter.close();
      piFctWriter = new FileWriter(piFctFile);
      piFctWriter.write(fctCode);
      piFctWriter.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }

    // Return an empty output map
    return Collections.emptyMap();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    // Create an empty parameters map
    final Map<String, String> parameters = new LinkedHashMap<>();
    return parameters;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Generating C++ code.";
  }

}
