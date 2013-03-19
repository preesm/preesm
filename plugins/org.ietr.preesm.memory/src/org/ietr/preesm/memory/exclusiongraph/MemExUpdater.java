/*********************************************************
Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
Karol Desnos

[mpelcat,jnezan,kdesnos]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.memory.exclusiongraph;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.elements.Workflow;
import net.sf.dftools.workflow.implement.AbstractTaskImplementation;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Workflow element that takes a Scheduled DAG and a MemEx as inputs and update
 * the MemEx according to the DAG Schedule
 * 
 * @author kdesnos
 * 
 */
public class MemExUpdater extends AbstractTaskImplementation {

	static final public String PARAM_VERBOSE = "Verbose";
	static final public String VALUE_VERBOSE_DEFAULT = "? C {True, False}";
	static final public String VALUE_VERBOSE_TRUE = "True";
	static final public String VALUE_VERBOSE_FALSE = "False";

	static final public String PARAM_LIFETIME = "Update with MemObject lifetime";
	static final public String VALUE_LIFETIME_DEFAULT = "? C {True, False}";
	static final public String VALUE_LIFETIME_TRUE = "True";
	static final public String VALUE_LIFETIME_FALSE = "False";

	static final public String OUTPUT_KEY_MEM_EX = "MemEx";

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		// Rem: Logger is used to display messages in the console
		Logger logger = WorkflowLogger.getLogger();

		// Check Workflow element parameters
		String valueVerbose = parameters.get(PARAM_VERBOSE);
		boolean verbose;
		verbose = valueVerbose.equals(VALUE_VERBOSE_TRUE);

		String valueLifetime = parameters.get(PARAM_LIFETIME);
		boolean lifetime;
		lifetime = valueLifetime.equals(VALUE_LIFETIME_TRUE);

		// Retrieve inputs
		DirectedAcyclicGraph dag = (DirectedAcyclicGraph) inputs.get("DAG");
		MemoryExclusionGraph memEx = (MemoryExclusionGraph) inputs.get("MemEx");

		int before = memEx.edgeSet().size();

		if (verbose) {
			logger.log(Level.INFO,
					"Memory exclusion graph : start updating with schedule");

			System.out.print( memEx.vertexSet().size()
					+ ";"
					+ memEx.edgeSet().size()
					/ (memEx.vertexSet().size()
							* (memEx.vertexSet().size() - 1) / 2.0) + ";");
		}

		memEx.updateWithSchedule(dag);

		double density = memEx.edgeSet().size()
				/ (memEx.vertexSet().size() * (memEx.vertexSet().size() - 1) / 2.0);
		if (verbose) {
			logger.log(Level.INFO, "Memory exclusion graph updated with "
					+ memEx.vertexSet().size() + " vertices and density = "
					+ density);
			logger.log(
					Level.INFO,
					"Exclusions removed: "
							+ (before - memEx.edgeSet().size())
							+ " ("
							+ Math.round(100.00
									* (before - memEx.edgeSet().size())
									/ (double) before) + "%)");
			System.out.print(density + ";");
		}

		if (lifetime) {
			before = memEx.edgeSet().size();
			if (verbose) {
				logger.log(Level.INFO,
						"Memory exclusion graph : start updating with memObject lifetimes");
			}

			memEx.updateWithMemObjectLifetimes(dag);

			density = memEx.edgeSet().size()
					/ (memEx.vertexSet().size()
							* (memEx.vertexSet().size() - 1) / 2.0);

			if (verbose) {
				logger.log(Level.INFO, "Memory exclusion graph updated with "
						+ memEx.vertexSet().size() + " vertices and density = "
						+ density);
				logger.log(
						Level.INFO,
						"Exclusions removed: "
								+ (before - memEx.edgeSet().size())
								+ " ("
								+ Math.round(100.00
										* (before - memEx.edgeSet().size())
										/ (double) before) + "%)");
				System.out.println(density + ";");
			}
		}

		// Generate output
		Map<String, Object> output = new HashMap<String, Object>();
		output.put(OUTPUT_KEY_MEM_EX, memEx);
		return output;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(PARAM_VERBOSE, VALUE_VERBOSE_DEFAULT);
		parameters.put(PARAM_LIFETIME, VALUE_LIFETIME_DEFAULT);
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Updating MemEx";
	}

}
