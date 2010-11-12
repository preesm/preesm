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

/**
 * Representation of an architecture component type
 * 
 * @author mpelcat
 */
public class ArchitectureComponentType {

	// Simple model
	public static final ArchitectureComponentType operator = new ArchitectureComponentType(
			"operator");
	public static final ArchitectureComponentType medium = new ArchitectureComponentType(
			"medium");
	public static final ArchitectureComponentType dma = new ArchitectureComponentType(
			"dma");
	public static final ArchitectureComponentType parallelNode = new ArchitectureComponentType(
			"parallelNode");
	public static final ArchitectureComponentType contentionNode = new ArchitectureComponentType(
			"contentionNode");
	public static final ArchitectureComponentType ram = new ArchitectureComponentType(
			"ram");

	private String name = null;

	public ArchitectureComponentType(String name) {
		super();
		this.name = name;
	}

	public static ArchitectureComponentType getType(String name) {

		// Simple model
		if (name.equalsIgnoreCase("operator"))
			return operator;
		else if (name.equalsIgnoreCase("medium"))
			return medium;
		else if (name.equalsIgnoreCase("dma"))
			return dma;
		else if (name.equalsIgnoreCase("parallelNode"))
			return parallelNode;
		else if (name.equalsIgnoreCase("contentionNode"))
			return contentionNode;
		else if (name.equalsIgnoreCase("ram"))
			return ram;

		else
			return null;
	}

	public String getName() {
		return name;
	}
}
