/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2012)
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
package org.preesm.algorithm.mapper.abc.transaction;

import java.util.ArrayList;
import java.util.List;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;

/**
 * This is a transaction container that enables the consecutive execution of several listed transactions.
 *
 * @author mpelcat
 */
public class TransactionManager {

  private final ArrayList<Transaction> transactionList;
  private final List<MapperDAGVertex>  resultList;

  public TransactionManager() {
    this(null);
  }

  /**
   * Instantiates a new transaction manager.
   *
   * @param resultList
   *          the result list
   */
  public TransactionManager(final List<MapperDAGVertex> resultList) {
    super();
    this.transactionList = new ArrayList<>();
    this.resultList = resultList;
  }

  /**
   * Execute.
   */
  public void execute() {
    for (final Transaction t : transactionList) {
      t.execute(resultList);
    }
  }

  public void add(final Transaction transaction) {
    transactionList.add(transaction);
  }

  public void clear() {
    transactionList.clear();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("{");

    for (final Transaction t : transactionList) {
      sb.append(t.toString() + ",");
    }
    String s = sb.substring(0, sb.length() - 1);
    s += "}";

    return s;
  }

}
