/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.model.pisdf.expression;

import org.nfunk.jep.ASTConstant;
import org.nfunk.jep.ASTFunNode;
import org.nfunk.jep.ASTVarNode;
import org.nfunk.jep.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class ExprPrinterVisitor.
 */
public class ExprPrinterVisitor {

  /**
   * Instantiates a new expr printer visitor.
   */
  public ExprPrinterVisitor() {
  }

  /**
   * To string.
   *
   * @param n
   *          the n
   * @return the string
   */
  public String toString(final Node n) {
    return visit(n);
  }

  /**
   * Visit.
   *
   * @param node
   *          the node
   * @return the string
   */
  public String visit(final ASTConstant node) {
    return node.getValue().toString();
  }

  /**
   * Visit.
   *
   * @param node
   *          the node
   * @return the string
   */
  public String visit(final ASTFunNode node) {
    String expr = new String();
    if (node.isOperator() && (node.jjtGetNumChildren() == 2)) {
      expr += "(";
      expr += visit(node.jjtGetChild(0));
      expr += node.getOperator().getName();
      expr += visit(node.jjtGetChild(1));
      expr += ")";
    } else {
      expr += node.getName();
      expr += "(";
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        expr += visit(node.jjtGetChild(i));
        if (i < node.jjtGetNumChildren()) {
          expr += ",";
        }
      }
      expr += ")";
    }
    return expr;
  }

  /**
   * Visit.
   *
   * @param node
   *          the node
   * @return the string
   */
  public String visit(final ASTVarNode node) {
    return node.getName();
  }

  /**
   * Visit.
   *
   * @param n
   *          the n
   * @return the string
   */
  public String visit(final Node n) {
    if (n instanceof ASTVarNode) {
      return visit((ASTVarNode) n);
    } else if (n instanceof ASTFunNode) {
      return visit((ASTFunNode) n);
    } else if (n instanceof ASTConstant) {
      return visit((ASTConstant) n);
    }
    return null;
  }
}
