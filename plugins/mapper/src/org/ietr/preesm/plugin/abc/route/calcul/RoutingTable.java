/**
 * 
 */
package org.ietr.preesm.plugin.abc.route.calcul;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.ietr.preesm.core.architecture.route.Route;
import org.ietr.preesm.core.architecture.simplemodel.Operator;

/**
 * 
 * 
 * @author mpelcat
 */
public class RoutingTable {

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
	
	private class RouteList extends ArrayList<Route> {
		private static final long serialVersionUID = -851695207011182681L;
	}
	
	private Map<OperatorCouple,RouteList> table;

	public RoutingTable() {
		super();
		table = new HashMap<OperatorCouple,RouteList>();
	}
	
	public Route getRoute(Operator op1, Operator op2, int index){
		for(OperatorCouple c : table.keySet()){
			if(c.equals(new OperatorCouple(op1,op2))){
				return table.get(c).get(index);
			}
		}
		return null;
	}
	
	public void addRoute(Operator op1, Operator op2, Route route){
		OperatorCouple key = new OperatorCouple(op1,op2);
		
		if(table.containsKey(key)){
			table.get(key).add(route);
		}
		else{
			RouteList list = new RouteList();
			list.add(route);
			table.put(key, list);
		}
	}

	@Override
	public String toString() {
		String result = "";
		for(OperatorCouple couple : table.keySet()){
			result += couple.toString() + " -> " + table.get(couple).toString() + "\n";
		}
		
		return result;
	}
	
	
}
