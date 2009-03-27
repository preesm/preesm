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

package org.ietr.preesm.plugin.abc.route.calcul;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

import org.ietr.preesm.core.architecture.route.Route;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.scenario.IScenario;

/**
 * Table representing the different routes available to go from one operator to
 * another
 * 
 * @author mpelcat
 */
public class RoutingTable {

	/**
	 * A couple of operators to which the routes are linked
	 */
	private class OperatorCouple {

		private Operator op1;
		private Operator op2;

		public OperatorCouple(Operator op1, Operator op2) {
			super();
			this.op1 = op1;
			this.op2 = op2;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof OperatorCouple) {
				OperatorCouple doublet = (OperatorCouple) obj;
				if (doublet.getOp1().equals(getOp1())
						&& doublet.getOp2().equals(getOp2())) {
					return true;
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return "(" + op1 + "," + op2 + ")";
		}

		public Operator getOp1() {
			return op1;
		}

		public Operator getOp2() {
			return op2;
		}
	}

	/**
	 * A route transfer comparator that never returns 0.
	 */
	private class RouteComparator implements Comparator<Route>{
		private long transferSize = 0;
		
		public RouteComparator(long transferSize) {
			super();
			this.transferSize = transferSize;
		}

		@Override
		public int compare(Route o1, Route o2) {
			int difference = (int)(o1.evaluateTransfer(transferSize) - o2.evaluateTransfer(transferSize));
			if(difference == 0){
				difference = 1;
			}
			return difference;
		}
		
	}
	
	/**
	 * A list of routes ordered in inverse order of transfer cosr
	 */
	private class RouteList extends ConcurrentSkipListSet<Route> {
		private static final long serialVersionUID = -851695207011182681L;

		public RouteList(long transferSize) {
			super(new RouteComparator(transferSize));
		}

		@Override
		public String toString() {
			String result = "|";
			for (Route r : this) {
				result += r.toString() + "|";
			}
			return result;
		}
	}

	/**
	 * List of available routes
	 */
	private Map<OperatorCouple, RouteList> table;

	/**
	 * Scenario
	 */
	private IScenario scenario;

	public RoutingTable(IScenario scenario) {
		super();
		table = new HashMap<OperatorCouple, RouteList>();
		this.scenario = scenario;
	}

	/**
	 * Gets a route with a given index
	 */
	public Route getBestRoute(Operator op1, Operator op2) {
		for (OperatorCouple c : table.keySet()) {
			if (c.equals(new OperatorCouple(op1, op2))) {
				return table.get(c).first();
			}
		}
		return null;
	}

	/**
	 * Removes all the routes corresponding to the operator couple
	 */
	public void removeRoutes(Operator op1, Operator op2) {
		OperatorCouple key = null;
		for (OperatorCouple c : table.keySet()) {
			if (c.equals(new OperatorCouple(op1, op2))) {
				key = c;
			}
		}

		if (key != null){
			table.get(key).clear();
		}
	}

	/**
	 * Adds a new route
	 */
	public void addRoute(Operator op1, Operator op2, Route route) {
		OperatorCouple key = null;
		for (OperatorCouple c : table.keySet()) {
			if (c.equals(new OperatorCouple(op1, op2))) {
				key = c;
			}
		}
		RouteList list = null;
		if (key != null) {
			list = table.get(key);
		} else {
			list = new RouteList(scenario.getSimulationManager().getAverageDataSize());
			table.put(new OperatorCouple(op1, op2), list);
		}
		list.add(route);
	}

	/**
	 * Displays the table
	 */
	@Override
	public String toString() {
		String result = "";
		for (OperatorCouple couple : table.keySet()) {
			result += couple.toString() + " -> " + table.get(couple).toString()
					+ "\n";
		}

		return result;
	}

}
