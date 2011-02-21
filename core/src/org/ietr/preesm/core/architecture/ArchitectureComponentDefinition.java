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

import java.util.logging.Level;

import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.ietr.preesm.core.architecture.parser.VLNV;
import org.ietr.preesm.core.architecture.simplemodel.ContentionNodeDefinition;
import org.ietr.preesm.core.architecture.simplemodel.DmaDefinition;
import org.ietr.preesm.core.architecture.simplemodel.MediumDefinition;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.architecture.simplemodel.ParallelNodeDefinition;
import org.ietr.preesm.core.architecture.simplemodel.RamDefinition;

/**
 * The architecture component definition gives component specifications
 * 
 * @author mpelcat
 */
public abstract class ArchitectureComponentDefinition {

	/**
	 * Category of the component definition: "medium" or "operator"
	 */
	protected String category;

	/**
	 * ID of the architecture component definition (examples: TCP, C64x+...)
	 */
	private VLNV vlnv;

	/**
	 * Constructor
	 */
	public ArchitectureComponentDefinition(VLNV vlnv, String category) {
		this.vlnv = vlnv;

		this.category = new String(category);
	}

	@Override
	public final boolean equals(Object obj) {

		if (obj.getClass().equals(this.getClass())) {
			ArchitectureComponentDefinition def = (ArchitectureComponentDefinition) obj;
			return vlnv.equals(def.getVlnv())
					&& category.equalsIgnoreCase(def.category);
		}
		return false;
	}

	public VLNV getVlnv() {
		return vlnv;
	}

	/**
	 * Compares two definitions for category
	 */
	public boolean sameCategory(ArchitectureComponentDefinition othertype) {
		return (category.compareTo(othertype.category) == 0);
	}

	/**
	 * Compares two definitions for id
	 */
	public boolean sameId(ArchitectureComponentDefinition othertype) {
		return (vlnv.equals(othertype.vlnv));
	}

	public abstract ArchitectureComponentType getType();

	/**
	 * Cloning a definition and its properties
	 */
	public final ArchitectureComponentDefinition clone() {

		ArchitectureComponentDefinition newdef = null;

		if (getType().equals(ArchitectureComponentType.contentionNode)) {
			newdef = new ContentionNodeDefinition(this.getVlnv());
			((ContentionNodeDefinition) newdef)
					.setDataRate(((ContentionNodeDefinition) this)
							.getDataRate());
		} else if (getType().equals(ArchitectureComponentType.dma)) {
			DmaDefinition dmaDef = (DmaDefinition) this;
			newdef = new DmaDefinition(this.getVlnv());
			for (String opName : dmaDef.getSetupTimes().keySet()) {
				((DmaDefinition) newdef).addSetupTime(opName,
						(int) dmaDef.getSetupTime(opName));
			}
		} else if (getType().equals(ArchitectureComponentType.medium)) {
			MediumDefinition def = (MediumDefinition) this;
			newdef = new MediumDefinition(this.getVlnv(), def.getDataRate(),
					def.getOverheadTime());
		} else if (getType().equals(ArchitectureComponentType.operator)) {
			newdef = new OperatorDefinition(this.getVlnv());
			((OperatorDefinition) newdef)
					.setDataCopySpeed(((OperatorDefinition) this)
							.getDataCopySpeed());
		} else if (getType().equals(ArchitectureComponentType.parallelNode)) {
			newdef = new ParallelNodeDefinition(this.getVlnv());
			((ParallelNodeDefinition) newdef)
					.setDataRate(((ParallelNodeDefinition) this).getDataRate());
		} else if (getType().equals(ArchitectureComponentType.ram)) {
			RamDefinition ramDef = (RamDefinition) this;
			newdef = new RamDefinition(this.getVlnv());
			for (String opName : ramDef.getSetupTimes().keySet()) {
				((RamDefinition) newdef).addSetupTime(opName,
						(int) ramDef.getSetupTime(opName));
			}
		} else {
			WorkflowLogger.getLogger().log(Level.SEVERE,
					"Cloning unknown type archi component definition.");
		}

		return newdef;
	}

	/**
	 * Duplicates in the common definition the parameters from the input
	 * definition
	 */
	public abstract void fill(ArchitectureComponentDefinition origin);
}
