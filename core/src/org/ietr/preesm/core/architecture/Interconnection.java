/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

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

package org.ietr.preesm.core.architecture;

import org.sdf4j.model.AbstractEdge;

/**
 * An interconnection joins one interface of a component to one interface of a
 * component
 * 
 * @author mpelcat
 */
public class Interconnection extends
		AbstractEdge<MultiCoreArchitecture, ArchitectureComponent> {

	private boolean directed = false;
	private boolean setup = false;

	private ArchitectureInterface srcIf = null;
	private ArchitectureInterface tgtIf = null;

	public Interconnection() {

	}

	public Interconnection(ArchitectureInterface if1, ArchitectureInterface if2) {
		this.srcIf = if1;
		this.tgtIf = if2;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Interconnection) {
			Interconnection intc = (Interconnection) obj;
			return (intc.getSource().equals(this.getSource())
					&& intc.getTarget().equals(this.getTarget())
					&& intc.srcIf.equals(this.srcIf) && intc.tgtIf
					.equals(this.tgtIf))
					|| (intc.getTarget().equals(this.getSource())
							&& intc.getSource().equals(this.getTarget())
							&& intc.tgtIf.equals(this.srcIf) && intc.srcIf
							.equals(this.tgtIf));
		}
		return false;
	}

	public ArchitectureInterface getSrcIf() {
		return srcIf;
	}

	public ArchitectureInterface getTgtIf() {
		return tgtIf;
	}

	public void setSrcIf(ArchitectureInterface srcIf) {
		this.srcIf = srcIf;
	}

	public void setTgtIf(ArchitectureInterface tgtIf) {
		this.tgtIf = tgtIf;
	}

	public ArchitectureInterface getInterface(ArchitectureComponent cmp) {

		if (getSource().equals(cmp))
			return srcIf;
		else if (getTarget().equals(cmp))
			return tgtIf;
		else
			return null;
	}

	public boolean isDirected() {
		return directed;
	}

	public void setDirected(boolean directed) {
		this.directed = directed;
	}

	public boolean isSetup() {
		return setup;
	}

	public void setSetup(boolean setup) {
		this.setup = setup;
	}

	@Override
	public String toString() {
		return "(" + getSource().toString() + "," + getTarget().toString()
				+ ")";
	}
}
