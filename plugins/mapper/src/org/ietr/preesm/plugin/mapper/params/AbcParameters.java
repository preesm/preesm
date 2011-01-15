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

package org.ietr.preesm.plugin.mapper.params;

import java.util.logging.Level;

import org.ietr.preesm.core.task.TextParameters;
import org.ietr.preesm.workflow.tools.WorkflowLogger;
import org.ietr.preesm.plugin.abc.AbcType;
import org.ietr.preesm.plugin.abc.edgescheduling.EdgeSchedType;

/**
 * Parameters applied to the current Abc
 * 
 * @author mpelcat
 */
public class AbcParameters {

	protected TextParameters textParameters = null;

	/**
	 * Simulator type
	 */
	private AbcType simulatorType = null;

	/**
	 * Edge scheduling type
	 */
	private EdgeSchedType edgeSchedType = null;

	/**
	 * true if loads are minimized while minimizing other parameters
	 */
	private boolean balanceLoads = false;

	/**
	 * Constructor creating a new text parameter
	 */
	public AbcParameters(AbcType simulatorType, EdgeSchedType edgeSchedType,
			boolean balanceLoads) {
		textParameters = new TextParameters();
		this.simulatorType = simulatorType;
		this.edgeSchedType = edgeSchedType;
		this.balanceLoads = balanceLoads;

		textParameters.addVariable("simulatorType", simulatorType.toString());
		textParameters.addVariable("edgeSchedType", edgeSchedType.toString());
		textParameters.addVariable("balanceLoads", balanceLoads);
	}

	/**
	 * Constructor from textual parameters
	 */
	public AbcParameters(TextParameters textParameters) {
		this.textParameters = textParameters;
		this.simulatorType = AbcType.fromString(textParameters
				.getVariable("simulatorType"));
		this.edgeSchedType = EdgeSchedType.fromString(textParameters
				.getVariable("edgeSchedType"));
		this.balanceLoads = textParameters.getBooleanVariable("balanceLoads");

		WorkflowLogger
				.getLogger()
				.log(
						Level.INFO,
						"The Abc parameters are: simulatorType=looselyTimed/approximatelyTimed/AccuratelyTimed; edgeSchedType=Simple/Switcher; balanceLoads=true/false");
	}

	/**
	 * Generates textual parameters from its internal parameters
	 */
	public TextParameters textParameters() {
		return textParameters;
	}

	public AbcType getSimulatorType() {
		return simulatorType;
	}

	public EdgeSchedType getEdgeSchedType() {
		return edgeSchedType;
	}

	public boolean isBalanceLoads() {
		return balanceLoads;
	}
}
