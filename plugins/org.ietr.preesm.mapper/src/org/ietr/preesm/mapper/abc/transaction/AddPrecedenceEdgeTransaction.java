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

package org.ietr.preesm.mapper.abc.transaction;

import java.util.List;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.special.PrecedenceEdge;

// TODO: Auto-generated Javadoc
/**
 * Transaction executing the addition of a {@link PrecedenceEdge}.
 *
 * @author mpelcat
 */
public class AddPrecedenceEdgeTransaction extends Transaction {

  // Inputs

  /** Implementation DAG to which the edge is added. */
  private MapperDAG implementation = null;

  /** Source of the added edge. */
  private MapperDAGVertex source = null;

  /** Destination of the added edge. */
  private MapperDAGVertex destination = null;

  /** Boolean precising which one between the source and the target created this transaction. */
  public static final int simpleDelete = 0; // Removing the edge only

  /** The Constant compensateSourceRemoval. */
  public static final int compensateSourceRemoval = 1; // Removing the edge

  /** The Constant compensateTargetRemoval. */
  // and adding a new edge between the target and its predecessor
  public static final int compensateTargetRemoval = 2; // Removing the edge
  // and adding a new edge between the source and its successor

  // Generated objects
  /** edges added. */
  private PrecedenceEdge precedenceEdge = null;

  /**
   * Instantiates a new adds the precedence edge transaction.
   *
   * @param implementation
   *          the implementation
   * @param source
   *          the source
   * @param destination
   *          the destination
   */
  public AddPrecedenceEdgeTransaction(final MapperDAG implementation, final MapperDAGVertex source,
      final MapperDAGVertex destination) {
    super();
    this.destination = destination;
    this.implementation = implementation;
    this.source = source;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.transaction.Transaction#execute(java.util.List)
   */
  @Override
  public void execute(final List<Object> resultList) {
    super.execute(resultList);

    this.precedenceEdge = new PrecedenceEdge(this.source, this.destination);
    this.precedenceEdge.getTiming().setCost(0);
    this.implementation.addEdge(this.source, this.destination, this.precedenceEdge);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.transaction.Transaction#toString()
   */
  @Override
  public String toString() {
    return ("AddPrecedence(" + this.precedenceEdge.toString() + ")");
  }

}
