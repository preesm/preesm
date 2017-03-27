/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
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
