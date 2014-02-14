/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.codegen.model.com;

import java.util.Iterator;
import java.util.List;

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.preesm.codegen.model.buffer.AbstractBufferContainer;
import org.ietr.preesm.codegen.model.buffer.Buffer;
import org.ietr.preesm.codegen.model.main.AbstractCodeElement;
import org.ietr.preesm.codegen.model.printer.CodeZoneId;
import org.ietr.preesm.codegen.model.printer.IAbstractPrinter;
import org.ietr.preesm.core.architecture.route.AbstractRouteStep;

/**
 * code Element used to launch inter-core communication send or receive.
 * Each communication can be broken up into two phases: starting and ending call.
 * 
 * @author mpelcat
 */
public class CommunicationFunctionCall extends AbstractCodeElement {
	
	public enum Phase {

		START, // Visit of the code element body
		END;

		@Override
		public String toString() {
			if(this == START) return "Start";
			else return "End";
		}
	}

	/**
	 * Transmitted buffers
	 */
	private List<Buffer> bufferSet;

	/**
	 * Medium used
	 */
	private AbstractRouteStep routeStep;

	/**
	 * index of the function call on current core
	 */
	private int comID = -1;
	
	private Phase phase = Phase.START;

	/**
	 * The vertex responsible of this call
	 */
	private SDFAbstractVertex vertex = null;

	public CommunicationFunctionCall(String name,
			AbstractBufferContainer parentContainer, List<Buffer> bufferSet,
			AbstractRouteStep routeStep, SDFAbstractVertex correspondingVertex,
			int comID, Phase phase) {

		super(name, parentContainer, correspondingVertex);

		this.bufferSet = bufferSet;

		this.routeStep = routeStep;
		this.comID = comID;
		this.phase = phase;
		this.vertex = correspondingVertex;
	}

	public void accept(IAbstractPrinter printer, Object currentLocation) {

		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit
																					// self

		if (bufferSet != null) {
			Iterator<Buffer> iterator = bufferSet.iterator();

			while (iterator.hasNext()) {
				Buffer buf = iterator.next();

				buf.accept(printer, currentLocation); // Accept the code
														// container
			}
		}
	}

	public List<Buffer> getBufferSet() {
		return bufferSet;
	}

	public AbstractRouteStep getRouteStep() {
		return routeStep;
	}

	public int getComID() {
		return comID;
	}

	public Phase getPhase() {
		return phase;
	}

	@Override
	public String toString() {

		String code = "";

		code += routeStep.toString() + ",";

		if (bufferSet != null) {
			Iterator<Buffer> iterator = bufferSet.iterator();

			while (iterator.hasNext()) {
				Buffer buf = iterator.next();

				code += buf.toString();
			}
		}

		return code;
	}
	
	/**
	 * Returning the related vertex name if relevant
	 */
	public String getVertexName() {
		if(vertex != null){
			return vertex.getName();
		}
		else{
			return "";
		}
	}
}
