/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018 - 2019)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2015 - 2016)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2017)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2015)
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
package org.preesm.codegen.xtend.spider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.preesm.codegen.xtend.spider.utils.SpiderConfig;
import org.preesm.codegen.xtend.spider.visitor.SpiderCodegen;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * The Class SpiderCodegenTask.
 */
@PreesmTask(id = "org.ietr.preesm.pimm.algorithm.spider.codegen.SpiderCodegenTask", name = "Spider Codegen",
    category = "Code Generation",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class),
        @Port(name = "architecture", type = Design.class) },

    shortDescription = "Generate Spider code for dynamic PiSDF.",

    parameters = {
        @Parameter(name = "scheduler", description = "Runtime scheduler to use.",
            values = { @Value(name = "list_on_the_go"), @Value(name = "round_robin"),
                @Value(name = "round_robin_scattered"), @Value(name = "list", effect = "(Default)") }),
        @Parameter(name = "memory-alloc", description = "Runtime memory allocation to use.",
            values = { @Value(name = "special-actors"), @Value(name = "dummy", effect = "(Default)") }),
        @Parameter(name = "shared-memory-size", description = "Size of the shared memory allocated by Spider.",
            values = { @Value(name = "$$n$$", effect = "$$n > 0$$ bytes. (Default = 67108864)") }),
        @Parameter(name = "papify", description = "Use of PAPIFY. Select type of feedback given too",
            values = { @Value(name = "off", effect = "PAPIFY is off"),
                @Value(name = "dump", effect = "PAPIFY is on. Print csv files"),
                @Value(name = "feedback", effect = "PAPIFY is on. Give feedback to the GRT"),
                @Value(name = "both", effect = "PAPIFY is on. Print csv files and give feedback to the GRT") }),
        @Parameter(name = "verbose", description = "Wether to log.",
            values = { @Value(name = "true / false", effect = "") }),
        @Parameter(name = "trace", description = "Wether to trace what is happening at runtime.",
            values = { @Value(name = "true / false", effect = "") }),
        @Parameter(name = "stack-type", description = "Type of stack to use",
            values = { @Value(name = "static", effect = "Use static stack"),
                @Value(name = "dynamic", effect = "Use dynamic stack") }),
        @Parameter(name = "graph-optims", description = "Wether to optimize the graph at runtime or not",
            values = { @Value(name = "true / false", effect = "") }) },

    seeAlso = { "**Spider**: Heulot, Julien; Pelcat, Maxime; Desnos, Karol; Nezan, Jean-François; Aridhi, Slaheddine "
        + "(2014) “SPIDER: A Synchronous Parameterized and Interfaced Dataflow-Based RTOS for Multicore DSPs”. "
        + "EDERC 2014, Milan, Italy." })
public class SpiderCodegenTask extends AbstractTaskImplementation {

  /** The Constant PARAM_PAPIFY. */
  public static final String PARAM_PAPIFY        = "papify";
  /** The Constant PARAM_VERBOSE. */
  public static final String PARAM_VERBOSE       = "verbose";
  /** The Constant PARAM_TRACE. */
  public static final String PARAM_TRACE         = "trace";
  /** The Constant PARAM_MEMALLOC. */
  public static final String PARAM_MEMALLOC      = "memory-alloc";
  /** The Constant PARAM_SHMEMORY_SIZE. */
  public static final String PARAM_SHMEMORY_SIZE = "shared-memory-size";
  /** The Constant PARAM_STACK_TYPE. */
  public static final String PARAM_STACK_TYPE    = "stack-type";
  /** The Constant PARAM_SCHEDULER. */
  public static final String PARAM_SCHEDULER     = "scheduler";
  /** The Constant PARAM_GRAPH_OPTIMS. */
  public static final String PARAM_GRAPH_OPTIMS  = "graph-optims";

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map,
   * org.eclipse.core.runtime.IProgressMonitor, java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    // Retrieve inputs
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final PiGraph pg = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    // Check if we are using papify instrumentation
    final String papifyParameter = parameters.get(SpiderCodegenTask.PARAM_PAPIFY);
    if (papifyParameter != null) {
      if (!scenario.getPapifyConfig().hasValidPapifyConfig()) {
        parameters.put(PARAM_PAPIFY, "off");
      }
    } else {
      parameters.put(PARAM_PAPIFY, "off");
    }

    final SpiderCodegen launcher = new SpiderCodegen(scenario, architecture);
    final SpiderConfig spiderConfig = new SpiderConfig(parameters);

    final boolean usingPapify = spiderConfig.getUseOfPapify();

    launcher.initGenerator(pg);
    final String graphCode = launcher.generateGraphCode(pg);
    final String fctCode = launcher.generateFunctionCode(pg);
    final String hCode = launcher.generateHeaderCode(pg, spiderConfig);
    // TODO: add config as parameters from workflow
    final String mCode = launcher.generateMainCode(pg, spiderConfig);
    final String papifyCode = launcher.generatePapifyCode(pg, scenario);
    final String archiCode = launcher.generateArchiCode(pg, scenario);

    // Get the workspace
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();

    // Get the name of the folder for code generation
    final String codegenPath = scenario.getCodegenDirectory() + "/";

    if (codegenPath.equals("/")) {
      final String message = "Error: A Codegen folder must be specified in Scenario";
      throw new PreesmRuntimeException(message);
    }

    final IFolder f = workspace.getRoot().getFolder(new Path(codegenPath));
    final IPath rawLocation = f.getRawLocation();
    if (rawLocation == null) {
      throw new PreesmRuntimeException("Could not find target project for given path [" + codegenPath
          + "]. Please change path in the scenario editor.");
    }
    final File folder = new File(rawLocation.toOSString());
    folder.mkdirs();
    if (folder.isDirectory()) {
      // clean the folder
      for (File file : folder.listFiles()) {
        file.delete();
      }
    }

    // Create the files
    final String hFilePath = pg.getName() + ".h";
    final File hFile = new File(folder, hFilePath);

    final String archiFilePath = "archi_" + pg.getName() + ".cpp";
    final File archiFile = new File(folder, archiFilePath);

    final String piGraphfilePath = "pi_" + pg.getName() + ".cpp";
    final File piGraphFile = new File(folder, piGraphfilePath);

    final String piFctfilePath = "fct_" + pg.getName() + ".cpp";
    final File piFctFile = new File(folder, piFctfilePath);

    final String cppMainfilePath = "main.cpp";
    final File cppMainFile = new File(folder, cppMainfilePath);

    // Write the files
    try (FileWriter piGraphWriter = new FileWriter(piGraphFile)) {
      piGraphWriter.write(graphCode);
    } catch (final IOException e) {
      e.printStackTrace();
    }

    try (FileWriter piFctWriter = new FileWriter(piFctFile)) {
      piFctWriter.write(fctCode);
    } catch (final IOException e) {
      e.printStackTrace();
    }

    try (FileWriter hWriter = new FileWriter(hFile)) {
      hWriter.write(hCode);
    } catch (final IOException e) {
      e.printStackTrace();
    }

    try (FileWriter archiWriter = new FileWriter(archiFile)) {
      archiWriter.write(archiCode);
    } catch (final IOException e) {
      e.printStackTrace();
    }

    try (FileWriter cppMainWriter = new FileWriter(cppMainFile)) {
      cppMainWriter.write(mCode);
    } catch (final IOException e) {
      e.printStackTrace();
    }

    // Write papify file
    if (usingPapify) {
      final String cppPapifyfilePath = "papify_" + pg.getName() + ".cpp";
      final File cppPapifyFile = new File(folder, cppPapifyfilePath);
      try (FileWriter cppPapifyWriter = new FileWriter(cppPapifyFile)) {
        cppPapifyWriter.write(papifyCode);
      } catch (final IOException e) {
        e.printStackTrace();
      }
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
