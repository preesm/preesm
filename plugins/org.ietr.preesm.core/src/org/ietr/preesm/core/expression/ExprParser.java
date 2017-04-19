/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
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

package org.ietr.preesm.core.expression;

import java.util.UUID;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

// TODO: Auto-generated Javadoc
/**
 * The Class ExprParser.
 */
public class ExprParser {

  /** The to parse. */
  protected String toParse;

  /**
   * Instantiates a new expr parser.
   *
   * @param val
   *          the val
   */
  public ExprParser(final String val) {
    this.toParse = val;
  }

  /**
   * Start parser.
   *
   * @return the node
   */
  public Node startParser() {
    try {
      final JEP jep = new JEP();
      jep.setAllowUndeclared(true);
      try {
        jep.addStandardFunctions();
        jep.addStandardConstants();
        if (this.toParse.contains("\"")) {
          this.toParse = this.toParse.replace("\"", "");
          final ASTVarNode var = new ASTVarNode(UUID.randomUUID().hashCode());
          var.setVar(new Parameter(this.toParse));
          return var;
        }

        System.out.println("Chain to parse : " + this.toParse);
        this.toParse = this.toParse.replace(" ", "");
        if (this.toParse.charAt(0) == '%') {
          this.toParse = "ceil(" + this.toParse.substring(1) + ")";
        }
        for (int i = 1; i < this.toParse.length(); i++) {
          if ((this.toParse.charAt(i) == '%') && ((this.toParse.charAt(i - 1) == '*') || (this.toParse.charAt(i - 1) == '/')
              || (this.toParse.charAt(i - 1) == '+') || (this.toParse.charAt(i - 1) == '-') || (this.toParse.charAt(i - 1) == '('))) {
            this.toParse = this.toParse.substring(0, i) + "ceil" + this.toParse.substring(i + 1);
          }
        }
        System.out.println("Chain to parse : " + this.toParse);
        jep.addFunction("ceil", new CeilFunction());
        final Node mainNode = jep.parse(this.toParse);
        return mainNode;

      } catch (final ParseException e) {
        e.printStackTrace();
      }
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
