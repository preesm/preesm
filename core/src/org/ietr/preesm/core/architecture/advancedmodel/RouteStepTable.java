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

import java.util.HashSet;
import java.util.Set;

/**
 * A route step table stores all the route steps of an architecture.
 * 
 * @author pmu
 */
public class RouteStepTable {

	private Set<RouteStepList> table = null;

	public RouteStepTable() {
		table = new HashSet<RouteStepList>();
	}

	public RouteStepList getRouteStepList(String beginName, String endName) {
		for (RouteStepList indexRSL : table) {
			if (indexRSL.getBeginTerminalName().equals(beginName)
					&& indexRSL.getEndTerminalName().equals(endName)) {
				return indexRSL;
			}
		}
		return null;
	}

	private void addRouteStepList(RouteStepList rsl) {
		table.add(rsl);
	}

	public void addRouteStep(RouteStep rs) {
		RouteStepList rsl = this.getRouteStepList(rs.getBeginTerminalName(), rs
				.getEndTerminalName());
		if (rsl != null) {
			rsl.addRouteStep(rs);
		} else {
			rsl = new RouteStepList(rs.getBeginTerminalName(), rs
					.getEndTerminalName());
			rsl.addRouteStep(rs);
			this.addRouteStepList(rsl);
		}
	}

	public Set<RouteStepList> getRouteStepLists() {
		return table;
	}

}
