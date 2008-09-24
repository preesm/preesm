package org.ietr.preesm.core.expression;

import java.util.UUID;

import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

public class ExprParser {

	public static void main(String[] args) {
		JEP jep = new JEP();
		jep.setAllowUndeclared(true);
		String string = "%2";
		/* ExprParser test = */new ExprParser(string);
	}

	protected String toParse;

	public ExprParser(String val) {
		toParse = val;
	}

	public Node startParser() {
		try {
			JEP jep = new JEP();
			jep.setAllowUndeclared(true);
			try {
				jep.addStandardFunctions();
				jep.addStandardConstants();
				if (toParse.contains("\"")) {
					toParse = toParse.replace("\"", "");
					ASTVarNode var = new ASTVarNode(UUID.randomUUID()
							.hashCode());
					var.setVar(new Parameter(toParse));
					return var;
				}

				System.out.println("Chain to parse : " + toParse);
				toParse = toParse.replace(" ", "");
				if (toParse.charAt(0) == '%') {
					toParse = "ceil(" + toParse.substring(1) + ")";
				}
				for (int i = 1; i < toParse.length(); i++) {
					if (toParse.charAt(i) == '%'
							&& (toParse.charAt(i - 1) == '*'
									|| toParse.charAt(i - 1) == '/'
									|| toParse.charAt(i - 1) == '+'
									|| toParse.charAt(i - 1) == '-' || toParse
									.charAt(i - 1) == '(')) {
						toParse = toParse.substring(0, i) + "ceil"
								+ toParse.substring(i + 1);
					}
				}
				System.out.println("Chain to parse : " + toParse);
				jep.addFunction("ceil", new CeilFunction());
				Node mainNode = jep.parse(toParse);
				return mainNode;

			} catch (ParseException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
