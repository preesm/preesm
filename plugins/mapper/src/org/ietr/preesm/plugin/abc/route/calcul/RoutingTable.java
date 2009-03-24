/**
 * 
 */
package org.ietr.preesm.plugin.abc.route.calcul;

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

	private class OperatorDoublet {

		private Operator op1;
		private Operator op2;

		public OperatorDoublet(Operator op1, Operator op2) {
			super();
			this.op1 = op1;
			this.op2 = op2;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof OperatorDoublet) {
				OperatorDoublet doublet = (OperatorDoublet) obj;
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
	
	private Map<OperatorDoublet,Route> table;

	public RoutingTable() {
		super();
		table = new HashMap<OperatorDoublet,Route>();
	}
	
	public Route getRoute(Operator op1, Operator op2){
		return table.get(new OperatorDoublet(op1,op2));
	}
	
	public void addRoute(Operator op1, Operator op2, Route route){
		table.put(new OperatorDoublet(op1,op2),route);
	}
}
