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
package org.ietr.preesm.mapper.abc.transaction;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * This is a transaction container that enables the consecutive execution of several listed transactions.
 *
 * @author mpelcat
 */
public class TransactionManager {

  /** The transaction list. */
  LinkedList<Transaction> transactionList = null;

  /** The result list. */
  List<Object> resultList = null;

  /**
   * Instantiates a new transaction manager.
   */
  public TransactionManager() {
    this(null);
  }

  /**
   * Instantiates a new transaction manager.
   *
   * @param resultList
   *          the result list
   */
  public TransactionManager(final List<Object> resultList) {
    super();
    this.transactionList = new LinkedList<>();
    this.resultList = resultList;
  }

  /**
   * Execute.
   */
  public void execute() {
    final Iterator<Transaction> it = this.transactionList.iterator();

    while (it.hasNext()) {
      final Transaction currentT = it.next();
      currentT.execute(this.resultList);
    }
  }

  /**
   * Adds the.
   *
   * @param transaction
   *          the transaction
   */
  public void add(final Transaction transaction) {
    this.transactionList.add(transaction);
  }

  /**
   * Clear.
   */
  public void clear() {
    this.transactionList.clear();
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String s = "{";

    for (final Transaction t : this.transactionList) {
      s += t.toString() + ",";
    }

    s = s.substring(0, s.length() - 1);
    s += "}";

    return s;
  }

}
