/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2018 - 2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2015)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2014)
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
package org.preesm.algorithm.memory.script;

import bsh.EvalError;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.preesm.algorithm.memory.allocation.tasks.MemoryAllocatorTask;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.SimulationInfo;

/**
 * The Class MemoryScriptEngine.
 */
public class MemoryScriptEngine {

  private static final String VALUE_CHECK_NONE     = "None";
  private static final String VALUE_CHECK_FAST     = "Fast";
  private static final String VALUE_CHECK_THOROUGH = "Thorough";
  private final ScriptRunner  sr;
  private final boolean       verbose;
  private final Logger        logger               = PreesmLogger.getLogger();

  /**
   * Instantiates a new memory script engine.
   *
   * @param valueAlignment
   *          the value alignment
   * @param log
   *          the log
   * @param verbose
   *          the verbose
   */
  public MemoryScriptEngine(final boolean falseSharingPreventionFlag, final String valueAlignment, final String log,
      final boolean verbose) {
    this.verbose = verbose;
    // Get the logger

    final long alignment = switch (valueAlignment.substring(0, Math.min(valueAlignment.length(), 7))) {
      case MemoryAllocatorTask.VALUE_ALIGNEMENT_NONE -> -1;
      case MemoryAllocatorTask.VALUE_ALIGNEMENT_DATA -> 0;
      case MemoryAllocatorTask.VALUE_ALIGNEMENT_FIXED -> {
        final String fixedValue = valueAlignment.substring(7);
        yield Long.parseLong(fixedValue);
      }
      default -> -1;
    };

    if (verbose) {
      String message = "Scripts with alignment:=" + alignment + " bits.";
      this.logger.log(Level.INFO, message);

      if (falseSharingPreventionFlag) {
        message = "False sharing prevention mechanism is activated.";
      } else {
        message = "False sharing prevention mechanism is NOT activated.";
      }
      this.logger.log(Level.INFO, message);
    }

    this.sr = new ScriptRunner(falseSharingPreventionFlag, alignment);
    this.sr.setGenerateLog(!(log.equals("")));
  }

  /**
   * Run scripts.
   *
   * @param dag
   *          the dag
   * @param simulationInfo
   *          the simulationInfo
   * @param checkString
   *          the check string
   */
  public void runScripts(final DirectedAcyclicGraph dag, final SimulationInfo simulationInfo, final String checkString)
      throws EvalError {
    // Retrieve all the scripts
    final int nbScripts = this.sr.findScripts(dag);

    this.sr.setSimulationInfo(simulationInfo);

    // Execute all the scripts
    if (this.verbose) {
      final String message = "Running " + nbScripts + " memory scripts.";
      this.logger.log(Level.INFO, message);
    }
    this.sr.run();

    check(checkString);

    // Pre-process the script result
    if (this.verbose) {
      this.logger.log(Level.INFO, "Processing memory script results.");
    }
    this.sr.process();
  }

  /**
   */
  private void check(String checkString) {
    // Check the result
    switch (checkString) {
      case VALUE_CHECK_NONE -> this.sr.setCheckPolicy(CheckPolicy.NONE);
      case VALUE_CHECK_FAST -> this.sr.setCheckPolicy(CheckPolicy.FAST);
      case VALUE_CHECK_THOROUGH -> this.sr.setCheckPolicy(CheckPolicy.THOROUGH);
      default -> {
        checkString = MemoryScriptEngine.VALUE_CHECK_FAST;
        this.sr.setCheckPolicy(CheckPolicy.FAST);
      }
    }

    if (this.verbose) {
      final String message = "Checking results of memory scripts with checking policy: " + checkString + ".";
      this.logger.log(Level.INFO, message);
    }
    this.sr.check();
  }

  /**
   */
  public void updateMemEx(final MemoryExclusionGraph meg) {
    // Update memex
    if (this.verbose) {
      this.logger.log(Level.INFO, "Updating memory exclusion graph.");
      // Display a message for each divided buffers
      for (final List<Buffer> group : this.sr.getBufferGroups()) {
        for (final Buffer buffer : group) {
          if ((buffer.matched != null) && (buffer.matched.size() > 1)) {
            final String message = "Buffer " + buffer
                + " was divided and will be replaced by a NULL pointer in the generated code.";
            this.logger.log(Level.INFO, message);
          }
        }
      }
    }

    this.sr.updateMEG(meg);
  }

  /**
   */
  public void generateCode(final Scenario scenario, final String log) {
    if (scenario.getCodegenDirectory() == null) {
      throw new PreesmRuntimeException("Codegen path has not been specified in scenario, cannot go further.");
    }
    final String codegenPath = scenario.getCodegenDirectory() + IPath.SEPARATOR;
    final IFile iFile;
    try {
      iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(codegenPath + log + ".txt"));
    } catch (final Exception e) {
      throw new PreesmRuntimeException(
          "Could not access code generation target path folder. Please check its value in the scenario.", e);
    }
    try {
      ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
      final IFolder iFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(codegenPath));
      if (!iFolder.exists()) {
        iFolder.create(false, true, new NullProgressMonitor());
      }
      if (!iFile.exists()) {
        iFile.create(new ByteArrayInputStream("".getBytes()), false, new NullProgressMonitor());
      }
      try (final ByteArrayInputStream source = new ByteArrayInputStream(this.sr.getLog().toString().getBytes())) {
        iFile.setContents(source, true, false, new NullProgressMonitor());
      }

    } catch (final CoreException | IOException e) {
      throw new PreesmRuntimeException("Could not write logs", e);
    }
  }
}
