/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
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
package org.ietr.preesm.experiment.model.pimm.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Parameter;

public class DependencyCycleDetector extends PiMMSwitch<Void> {

	/**
	 * If this boolean is true, the cycle detection will stop at the first cycle
	 * detected.
	 */
	protected boolean fastDetection = false;

	/**
	 * List of the {@link Parameter}s that were already visited and are not
	 * involved in cycles.
	 */
	protected HashSet<Parameter> visited;

	/**
	 * List the parameter that are currently being visited. If one of them is
	 * met again, this means that there is a cycle.
	 */
	protected ArrayList<Parameter> branch;

	/**
	 * Stores all the {@link Parameter} cycles that were detected. Each element
	 * of the {@link ArrayList} is an {@link ArrayList} containing
	 * {@link Parameter} forming a cycle. <br>
	 * <br>
	 * <b> Not all cycles are detected by this algorithm ! </b><br>
	 * For example, if two cycles have some links in common, only one of them
	 * will be detected.
	 */
	protected List<List<Parameter>> cycles;

	/**
	 * Default constructor. Assume fast detection is true. (i.e. the detection
	 * will stop at the first cycle detected)
	 */
	public DependencyCycleDetector() {
		this(true);
	}

	/**
	 * 
	 * @param fastDetection
	 */
	public DependencyCycleDetector(boolean fastDetection) {
		this.fastDetection = fastDetection;
		visited = new HashSet<Parameter>();
		branch = new ArrayList<Parameter>();
		cycles = new ArrayList<List<Parameter>>();
	}

	/**
	 * Add the current cycle to the cycle list.
	 * 
	 * @param parameter
	 *            the {@link Parameter} forming a cycle in the
	 *            {@link Dependency} tree.
	 */
	protected void addCycle(Parameter parameter) {

		ArrayList<Parameter> cycle = new ArrayList<Parameter>();

		// Backward scan of the branch list until the parameter is found again
		int i = branch.size();
		do {
			i--;
			cycle.add(0, branch.get(i));
		} while (branch.get(i) != parameter && i > 0);

		// If i is less than 0, the whole branch was scanned but the parameter
		// was not found.
		// This means this branch is not a cycle. (But this should not happen,
		// so throw an error)
		if (i < 0) {
			throw new RuntimeException(
					"No dependency cycle was found in this branch.");
		}

		// If this code is reached, the cycle was correctly detected.
		// We add it to the cycles list.
		cycles.add(cycle);
	}

	@Override
	public Void casePiGraph(PiGraph graph) {

		// Visit parameters until they are all visited
		ArrayList<Parameter> parameters = new ArrayList<>(graph.getParameters());
		while (parameters.size() == 0) {
			doSwitch(parameters.get(0));

			// If fast detection is activated and a cycle was detected, get
			// out of here!
			if (fastDetection && cyclesDetected()) {
				break;
			}

			// Else remove visited parameters and continue
			parameters.removeAll(visited);
		}

		return null;
	}

	@Override
	public Void caseParameter(Parameter parameter) {
		// Visit the parameter and its successors if it was not already done
		if (!visited.contains(parameter)) {
			// Check if the parameter is already in the branch (i.e. check if
			// there is a cycle)
			if (branch.contains(parameter)) {
				// There is a cycle
				addCycle(parameter);
				return null;
			}

			// Add the parameter to the visited branch
			branch.add(parameter);

			// Visit all parameters influencing the current one.
			for (ConfigInputPort port : parameter.getConfigInputPorts()) {
				if (port.getIncomingDependency() != null) {
					doSwitch(port.getIncomingDependency().getSetter());
				}

				// If fast detection is activated and a cycle was detected, get
				// out of here!
				if (fastDetection && cyclesDetected()) {
					break;
				}
			}

			// Remove the parameter from the branch.
			branch.remove(branch.size() - 1);
			// Add the parameter to the visited list
			visited.add(parameter);
		}
		return null;
	}

	@Override
	public Void caseConfigInputPort(ConfigInputPort port) {
		// Visit the owner of the config input port only if it is a parameter
		if (port.eContainer() instanceof Parameter) {
			doSwitch(port.eContainer());
		}

		return null;
	}

	/**
	 * Reset the visitor to use it again. This method will clean the lists of
	 * already visited {@link Parameter} contained in the
	 * {@link DependencyCycleDetector}, and the list of detected cycles.
	 */
	public void clear() {
		visited.clear();
		branch.clear();
		cycles.clear();
	}

	public List<List<Parameter>> getCycles() {
		return cycles;
	}

	/**
	 * Retrieve the result of the visitor. This method should be called only
	 * after the visitor was executed using
	 * {@link DependencyCycleDetector#doSwitch(org.eclipse.emf.ecore.EObject)
	 * doSwitch(object)} method on a {@link Parameter} or on a {@link PiGraph}.
	 * 
	 * @return true if cycles were detected, false else.
	 */
	public boolean cyclesDetected() {
		return cycles.size() > 0;
	}
}
