/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy, Yaset Oliva Venegas
 *
 * [mpelcat,jnezan,kdesnos,jheulot,cguy,yoliva]@insa-rennes.fr
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software. You can use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty and the software's author, the holder of the
 * economic rights, and the successive licensors have only limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading, using, modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean that it is complicated to manipulate, and that also
 * therefore means that it is reserved for developers and experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and, more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.pimm.algorithm.checker;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.pimm.algorithm.checker.structure.FifoChecker;
import org.ietr.preesm.pimm.algorithm.checker.structure.RefinementChecker;

/**
 * Main class of the checker. Call all the independent checkers and create error
 * messages from their result.
 * 
 * @author cguy
 * @author kdesnos
 * 
 */
public class PiMMAlgorithmChecker {

	private PiGraph graph;
	private Map<String, EObject> errorMsgs;
	private Map<String, EObject> warningMsgs;

	private boolean errors;
	private boolean warnings;

	/**
	 * Check a PiGraph for different properties
	 * 
	 * @param graph
	 *            the PiGraph to check
	 * @return true if no problem have been detected in graph, false otherwise
	 */
	public boolean checkGraph(PiGraph graph) {
		this.graph = graph;
		errorMsgs = new HashMap<String, EObject>();
		warningMsgs = new HashMap<String, EObject>();

		errors = false;
		warnings = false;

		checkGraphRefinements(graph);
		checkGraphFifos(graph);

		return !errors && !warnings;
	}

	private void checkGraphFifos(PiGraph graph) {
		FifoChecker fifoChecker = new FifoChecker();
		if (!fifoChecker.checkFifos(graph)) {
			errors = !fifoChecker.getFifoWithOneZeroRate().isEmpty();
			warnings = !fifoChecker.getFifoWithVoidType().isEmpty() || !fifoChecker.getFifoWithZeroRates().isEmpty();
			for (Fifo f : fifoChecker.getFifoWithOneZeroRate()) {
				String srcActorPath = ((AbstractActor) f.getSourcePort().eContainer()).getName() + "."
						+ f.getSourcePort().getName();
				String tgtActorPath = ((AbstractActor) f.getTargetPort().eContainer()).getName() + "."
						+ f.getTargetPort().getName();
				errorMsgs.put("Fifo between actors " + srcActorPath + " and " + tgtActorPath
						+ " has invalid rates (one equals 0 but not the other).\n", f);
			}
			for (Fifo f : fifoChecker.getFifoWithVoidType()) {
				String srcActorPath = ((AbstractActor) f.getSourcePort().eContainer()).getName() + "."
						+ f.getSourcePort().getName();
				String tgtActorPath = ((AbstractActor) f.getTargetPort().eContainer()).getName() + "."
						+ f.getTargetPort().getName();
				warningMsgs.put("Fifo between actors " + srcActorPath + " and " + tgtActorPath
						+ " has type \"void\" (this is not supported by code generation).\n", f);
			}
			for (Fifo f : fifoChecker.getFifoWithZeroRates()) {
				String srcActorPath = ((AbstractActor) f.getSourcePort().eContainer()).getName() + "."
						+ f.getSourcePort().getName();
				String tgtActorPath = ((AbstractActor) f.getTargetPort().eContainer()).getName() + "."
						+ f.getTargetPort().getName();
				warningMsgs.put("Fifo between actors " + srcActorPath + " and " + tgtActorPath
						+ " has rates equal to 0 (you may have forgotten to set them).\n", f);
			}
		}
	}

	private void checkGraphRefinements(PiGraph graph) {
		RefinementChecker refinementChecker = new RefinementChecker();
		if (!refinementChecker.checkRefinements(graph)) {
			errors = true;
			for (Actor a : refinementChecker.getActorsWithoutRefinement()) {
				errorMsgs.put("Actor " + a.getPath() + " does not have a refinement.\n", a);
			}
			for (Actor a : refinementChecker.getActorsWithInvalidExtensionRefinement()) {
				errorMsgs.put("Refinement " + a.getRefinement().getFilePath() + " of Actor " + a.getPath()
						+ " does not have a valid extension (.h or .idl).\n", a);
			}
			for (Actor a : refinementChecker.getActorsWithNonExistingRefinement()) {
				errorMsgs.put("Refinement  " + a.getRefinement().getFilePath() + " of Actor " + a.getPath()
						+ " does not reference an existing file.\n", a);
			}
		}
	}

	public String getErrorMsg() {
		String result = "Validation of graph " + graph.getName() + " raised the following errors:\n";
		for (String msg : errorMsgs.keySet()) {
			result += "- " + msg;
		}
		return result;
	}

	/**
	 * @return the errorMsgs and the associated {@link EObject}
	 */
	public Map<String, EObject> getErrorMsgs() {
		return errorMsgs;
	}

	public String getOkMsg() {
		String result = "Validation of graph " + graph.getName() + " raised no error or warning:\n";

		return result;
	}

	public String getWarningMsg() {
		String result = "Validation of graph " + graph.getName() + " raised the following warnings:\n";
		for (String msg : warningMsgs.keySet()) {
			result += "- " + msg;
		}
		return result;
	}

	/**
	 * @return the warningMsgs and the associated {@link EObject}
	 */
	public Map<String, EObject> getWarningMsgs() {
		return warningMsgs;
	}

	public boolean isErrors() {
		return errors;
	}

	public boolean isWarnings() {
		return warnings;
	}

}
