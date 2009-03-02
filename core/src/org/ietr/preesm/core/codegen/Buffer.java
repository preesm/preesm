/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.core.codegen;

import org.ietr.preesm.core.codegen.printer.CodeZoneId;
import org.ietr.preesm.core.codegen.printer.IAbstractPrinter;
import org.sdf4j.model.sdf.SDFEdge;

/**
 * Buffer abstraction
 * 
 * @author mpelcat
 */
public class Buffer {

	// Buffer representing an edge: characteristic names
	
	/**
	 * The edge this buffer correspond to 
	 */
	private SDFEdge edge ;

	/**
	 * destination name of the corresponding edge
	 */
	private String destID;

	/**
	 * destination port name of the corresponding edge
	 */
	private String destInputPortID;

	// Buffer generated from a name
	/**
	 * Buffer name if not generated from edge characteristics
	 */
	private String name;

	/**
	 * Size of the allocated buffer
	 */
	private Integer size;

	/**
	 * source name of the corresponding edge
	 */
	private String sourceID;

	/**
	 * source port name of the corresponding edge
	 */
	private String sourceOutputPortID;

	/**
	 * Type of the allocated buffer
	 */
	private DataType type;

	public Buffer(String name, Integer size, DataType type,
			SDFEdge edge) {

		this.sourceID = null;
		this.destID = null;
		this.sourceOutputPortID = null;
		this.destInputPortID = null;

		this.size = size;
		this.type = type;
		this.edge = edge ;
		this.name = name;
	}

	public Buffer(String sourceID, String destID, String sourceOutputPortID,
			String destInputPortID, Integer size, DataType type,
			SDFEdge edge) {

		this.sourceID = sourceID;
		this.destID = destID;
		this.sourceOutputPortID = sourceOutputPortID;
		this.destInputPortID = destInputPortID;

		this.size = size;
		this.type = type;

		this.edge = edge;

		this.name = null;
	}

	public void accept(IAbstractPrinter printer, Object currentLocation) {

		currentLocation = printer.visit(this, CodeZoneId.body, currentLocation); // Visit self
	}

	public SDFEdge getEdge(){
		return edge ;
	}

	public String getName() {

		if (name == null)
			return sourceID + sourceOutputPortID + destID + destInputPortID;
		else
			return name;
	}

	public Integer getSize() {
		return size;
	}

	public DataType getType() {
		return type;
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getDestInputPortID() {
		return destInputPortID;
	}

	public String getSourceOutputPortID() {
		return sourceOutputPortID;
	}
}
