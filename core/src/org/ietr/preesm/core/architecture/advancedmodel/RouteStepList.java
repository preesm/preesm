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
package org.ietr.preesm.core.architecture.advancedmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * A route step list is a list of route steps with the same begin terminal and
 * the same end terminal.
 * 
 * @author pmu
 */
public class RouteStepList {

	private String beginTerminalName;

	private String endTerminalName;

	private List<RouteStep> routeSteps;

	public RouteStepList(String beginTerminalName, String endTerminalName) {
		this.beginTerminalName = beginTerminalName;
		this.endTerminalName = endTerminalName;
		routeSteps = new ArrayList<RouteStep>();
	}

	public String getBeginTerminalName() {
		return beginTerminalName;
	}

	public String getEndTerminalName() {
		return endTerminalName;
	}

	public List<RouteStep> getRouteSteps() {
		return routeSteps;
	}

	public void addRouteStep(int index, RouteStep rs) {
		if (rs.getBeginTerminalName().equals(beginTerminalName)
				&& rs.getEndTerminalName().equals(endTerminalName)) {
			routeSteps.add(index, rs);
		}
	}

	public void addRouteStep(RouteStep rs) {
		if (rs.getBeginTerminalName().equals(beginTerminalName)
				&& rs.getEndTerminalName().equals(endTerminalName)) {
			routeSteps.add(rs);
		}
	}

	public RouteStep getRouteStep(int index) {
		return routeSteps.get(index);
	}

	public RouteStep getRouteStep() {
		return routeSteps.get(0);
	}

}
