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
package org.ietr.preesm.pimm.checker.structure;

import java.util.HashSet;
import java.util.Set;

import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

/**
 * Class to check different properties of the Fifos of a PiGraph. Entry point is
 * the checkFifos method. Invalid Fifos are kept in several sets.
 * 
 * @author cguy
 * 
 */
public class FifoChecker {
	// Fifos with "void" type (authorized but can lead to problems when
	// generating code)
	private Set<Fifo> fifoWithVoidType;
	// Fifos with one rate (production or consumption) to 0 but not the other
	private Set<Fifo> fifoWithOneZeroRate;
	// Fifos with rates (production and consumption) to 0 (authorized but user
	// may have forgotten to set rates)
	private Set<Fifo> fifoWithZeroRates;

	public FifoChecker() {
		fifoWithVoidType = new HashSet<Fifo>();
		fifoWithOneZeroRate = new HashSet<Fifo>();
		fifoWithZeroRates = new HashSet<Fifo>();
	}

	/**
	 * Check types and rates of the Fifos of a PiGraph
	 * 
	 * @param graph
	 *            the PiGraph for which we check Fifos
	 * @return true if all the Fifos of graph are valid, false otherwise
	 */
	public boolean checkFifos(PiGraph graph) {
		boolean ok = true;
		for (Fifo f : graph.getFifos())
			ok &= checkFifo(f);
		return ok;
	}

	/**
	 * Check type and rates of a Fifo
	 * 
	 * @param f
	 *            the Fifo to check
	 * @return true if f is valid, false otherwise
	 */
	private boolean checkFifo(Fifo f) {
		boolean ok = true;
		ok &= checkFifoType(f);
		ok &= checkFifoRates(f);
		return ok;
	}

	/**
	 * Check production and consumption rates of a Fifo. If one of the rate
	 * equals 0, the Fifo is invalid. If both rates equal 0, the Fifo is valid
	 * but the user may have forgotten to set the rates
	 * 
	 * @param f
	 *            the Fifo to check
	 * @return true if no rate of f is at 0, false otherwise
	 */
	private boolean checkFifoRates(Fifo f) {
		if (f.getSourcePort().getExpression().getString().equals("0")) {
			if (f.getTargetPort().getExpression().getString().equals("0")) {
				fifoWithZeroRates.add(f);
				return false;
			} else {
				fifoWithOneZeroRate.add(f);
				return false;
			}
		} else if (f.getTargetPort().getExpression().getString().equals("0")) {
			fifoWithOneZeroRate.add(f);
			return false;
		}
		return true;
	}

	/**
	 * Check the type of a Fifo. If the type is "void", code won't be generated.
	 * 
	 * @param f
	 *            the Fifo to check
	 * @return true if the type of f is not void, false otherwise
	 */
	private boolean checkFifoType(Fifo f) {
		if (f.getType().equals("void")) {
			fifoWithVoidType.add(f);
			return false;
		}
		return true;
	}

	public Set<Fifo> getFifoWithVoidType() {
		return fifoWithVoidType;
	}

	public Set<Fifo> getFifoWithOneZeroRate() {
		return fifoWithOneZeroRate;
	}

	public Set<Fifo> getFifoWithZeroRates() {
		return fifoWithZeroRates;
	}
}
