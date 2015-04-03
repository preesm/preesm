/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy
 * 
 * [mpelcat,jnezan,kdesnos,jheulot,cguy]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/

package org.ietr.preesm.memory.script;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.types.DataType;
import org.ietr.preesm.memory.allocation.AbstractMemoryAllocatorTask;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;

public class MemoryScriptEngine {
	public static final String VALUE_CHECK_NONE = "None";
	public static final String VALUE_CHECK_FAST = "Fast";
	public static final String VALUE_CHECK_THOROUGH = "Thorough";

	private ScriptRunner sr;
	private boolean verbose;
	private Logger logger;
	private PreesmScenario scenario;

	public MemoryScriptEngine(String valueAlignment, String log, boolean verbose, PreesmScenario scenario) {
		this.verbose = verbose;
		// Get the logger
		this.logger = WorkflowLogger.getLogger();
		this.scenario = scenario;
		int alignment;
		switch (valueAlignment.substring(0,
				Math.min(valueAlignment.length(), 7))) {
		case AbstractMemoryAllocatorTask.VALUE_ALIGNEMENT_NONE:
			alignment = -1;
			break;
		case AbstractMemoryAllocatorTask.VALUE_ALIGNEMENT_DATA:
			alignment = 0;
			break;
		case AbstractMemoryAllocatorTask.VALUE_ALIGNEMENT_FIXED:
			String fixedValue = valueAlignment.substring(7);
			alignment = Integer.parseInt(fixedValue);
			break;
		default:
			alignment = -1;
		}
		if (verbose) {
			logger.log(Level.INFO, "Scripts with alignment:=" + alignment + ".");
		}

		sr = new ScriptRunner(alignment);
		sr.generateLog = !(log.equals(""));
	}

	public void runScripts(DirectedAcyclicGraph dag,
			Map<String, DataType> dataTypes, String checkString) {
		// Retrieve all the scripts
		int nbScripts = sr.findScripts(dag, scenario);

		sr.setDataTypes(dataTypes);

		// Execute all the scripts
		if (verbose) {
			logger.log(Level.INFO, "Running " + nbScripts + " memory scripts.");
		}
		sr.run();

		check(checkString);

		// Pre-process the script result
		if (verbose) {
			logger.log(Level.INFO, "Processing memory script results.");
		}
		sr.process();
	}

	private void check(String checkString) {
		// Check the result
		switch (checkString) {
		case VALUE_CHECK_NONE:
			sr.setCheckPolicy(CheckPolicy.NONE);
			break;
		case VALUE_CHECK_FAST:
			sr.setCheckPolicy(CheckPolicy.FAST);
			break;
		case VALUE_CHECK_THOROUGH:
			sr.setCheckPolicy(CheckPolicy.THOROUGH);
			break;
		default:
			checkString = VALUE_CHECK_FAST;
			sr.setCheckPolicy(CheckPolicy.FAST);
			break;
		}
		if (verbose) {
			logger.log(Level.INFO,
					"Checking results of memory scripts with checking policy: "
							+ checkString + ".");
		}
		sr.check();
	}

	public void updateMemEx(MemoryExclusionGraph meg) {
		// Update memex
		if (verbose) {
			logger.log(Level.INFO, "Updating memory exclusion graph.");
			// Display a message for each divided buffers
			for (List<Buffer> group : sr.bufferGroups) {
				for (Buffer buffer : group) {
					if (buffer.getMatched() != null
							&& buffer.getMatched().size() > 1) {
						logger.log(
								Level.WARNING,
								"Buffer "
										+ buffer
										+ " was divided and will be replaced by a NULL pointer in the generated code.");
					}
				}
			}
		}

		sr.updateMEG(meg);
	}

	public void generateCode(PreesmScenario scenario, String log) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		String codegenPath = scenario.getCodegenManager().getCodegenDirectory()
				+ "/";

		// Create a resource
		scenario.getCodegenManager().getCodegenDirectory();

		IFile iFile = workspace.getRoot().getFile(new Path(codegenPath + log + ".txt"));
		try {
			if (!iFile.exists()) {
				iFile.create(null, false, new NullProgressMonitor());
			}
			iFile.setContents(new ByteArrayInputStream(sr.getLog().toString()
					.getBytes()), true, false, new NullProgressMonitor());
		} catch (CoreException e1) {
			e1.printStackTrace();
		}

	}
}
