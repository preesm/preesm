/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2012)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
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
 *******************************************************************************/

package org.ietr.preesm.mapper.params;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.mapper.abc.AbcType;
import org.ietr.preesm.mapper.abc.edgescheduling.EdgeSchedType;

/**
 * Parameters applied to the current Abc
 * 
 * @author mpelcat
 */
public class AbcParameters {

	protected Map<String, String> textParameters = null;

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
		textParameters = new HashMap<String, String>();
		this.simulatorType = simulatorType;
		this.edgeSchedType = edgeSchedType;
		this.balanceLoads = balanceLoads;

		textParameters.put("simulatorType", simulatorType.toString());
		textParameters.put("edgeSchedType", edgeSchedType.toString());
		textParameters.put("balanceLoads", Boolean.toString(balanceLoads));
	}

	/**
	 * Constructor from textual parameters
	 */
	public AbcParameters(Map<String, String> textParameters) {
		this.textParameters = textParameters;
		this.simulatorType = AbcType.fromString(textParameters
				.get("simulatorType"));
		this.edgeSchedType = EdgeSchedType.fromString(textParameters
				.get("edgeSchedType"));
		this.balanceLoads = Boolean.valueOf(textParameters.get("balanceLoads"));

		WorkflowLogger
				.getLogger()
				.log(Level.INFO,
						"The Abc parameters are: simulatorType=looselyTimed/approximatelyTimed/AccuratelyTimed; edgeSchedType=Simple/Switcher; balanceLoads=true/false");
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
