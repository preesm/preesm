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

package org.ietr.preesm.core.architecture;

/**
 * An interconnection joins one interface of a component to one interface of a
 * component
 * 
 * @author mpelcat
 */
public class Interconnection {

	private ArchitectureComponent cp1;
	private ArchitectureInterface if1;

	private ArchitectureComponent cp2;
	private ArchitectureInterface if2;

	public Interconnection(ArchitectureComponent cp1,
			ArchitectureInterface if1, ArchitectureComponent cp2,
			ArchitectureInterface if2) {
		this.cp1 = cp1;
		this.cp2 = cp2;

		this.if1 = if1;
		this.if2 = if2;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Interconnection) {
			Interconnection intc = (Interconnection) obj;
			return (intc.cp1.equals(this.cp1) && intc.cp2.equals(this.cp2)
					&& intc.if1.equals(this.if1) && intc.if2.equals(this.if2)) ||
					(intc.cp2.equals(this.cp1) && intc.cp1.equals(this.cp2)
							&& intc.if2.equals(this.if1) && intc.if1.equals(this.if2));
		}
		return false;
	}
	
	public ArchitectureComponent getCp1() {
		return cp1;
	}

	public ArchitectureComponent getCp2() {
		return cp2;
	}

	public ArchitectureInterface getInterface(ArchitectureComponentType type) {

		if(cp1.getType() == type) return if1;
		else if(cp2.getType() == type) return if2;
		else return null;
	}
}
