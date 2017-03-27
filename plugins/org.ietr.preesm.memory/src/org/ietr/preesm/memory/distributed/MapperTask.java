/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
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
 *******************************************************************************/
package org.ietr.preesm.memory.distributed;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.memory.allocation.AbstractMemoryAllocatorTask;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;

/**
 * Workflow element taking the architecture a Scheduled DAG and a its
 * corresponding *updated* MemEx as inputs and generates specific MemExes for
 * each memory of the architecture.
 * 
 * @author kdesnos
 * 
 */
public class MapperTask extends AbstractTaskImplementation {

	static final public String PARAM_VERBOSE = "Verbose";
	static final public String VALUE_VERBOSE_DEFAULT = "? C {True, False}";
	static final public String VALUE_VERBOSE_TRUE = "True";
	static final public String VALUE_VERBOSE_FALSE = "False";

	static final public String OUTPUT_KEY_MEM_EX = "MemExes";

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
			IProgressMonitor monitor, String nodeName, Workflow workflow) throws WorkflowException {

		// Rem: Logger is used to display messages in the console
		Logger logger = WorkflowLogger.getLogger();

		// Check Workflow element parameters
		String valueVerbose = parameters.get(PARAM_VERBOSE);
		boolean verbose;
		verbose = valueVerbose.equals(VALUE_VERBOSE_TRUE);

		String valuePolicy = parameters.get(AbstractMemoryAllocatorTask.PARAM_DISTRIBUTION_POLICY);

		// Retrieve inputs
		MemoryExclusionGraph memEx = (MemoryExclusionGraph) inputs.get("MemEx");

		// Log the distribution policy used
		if (verbose) {
			logger.log(Level.INFO, "Filling MemExes Vertices set with " + valuePolicy + " policy");
		}

		// Create output
		Map<String, MemoryExclusionGraph> memExes;
		memExes = Distributor.distributeMeg(valuePolicy, memEx, -1);

		// Log results
		if (verbose) {
			logger.log(Level.INFO, "Created " + memExes.keySet().size() + " MemExes");
			for (Entry<String, MemoryExclusionGraph> entry : memExes.entrySet()) {
				double density = entry.getValue().edgeSet().size()
						/ (entry.getValue().vertexSet().size() * (entry.getValue().vertexSet().size() - 1) / 2.0);
				logger.log(Level.INFO, "Memex(" + entry.getKey() + "): " + entry.getValue().vertexSet().size()
						+ " vertices, density=" + density);
			}
		}

		// Output output
		Map<String, Object> output = new HashMap<String, Object>();
		output.put(OUTPUT_KEY_MEM_EX, memExes);
		return output;
	}


	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(PARAM_VERBOSE, VALUE_VERBOSE_DEFAULT);
		parameters.put(AbstractMemoryAllocatorTask.PARAM_DISTRIBUTION_POLICY,
				AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DEFAULT);
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Generating memory specific MemEx";
	}

}
