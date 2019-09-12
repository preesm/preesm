/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Jonathan Piat [jpiat@laas.fr] (2011)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2009 - 2016)
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
package org.preesm.model.slam.route;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListSet;
import org.apache.commons.lang3.tuple.MutablePair;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.SlamRoute;

/**
 * Table representing the different routes available to go from one operator to another.
 *
 * @author mpelcat
 */
class RoutingTable {

  /**
   * A couple of operators to which the routes are linked.
   */
  private class OperatorCouple extends MutablePair<ComponentInstance, ComponentInstance> {
    private static final long serialVersionUID = -451571160460519876L;

    OperatorCouple(final ComponentInstance op1, final ComponentInstance op2) {
      super(op1, op2);
    }

    @Override
    public String toString() {
      return "(" + getOp1() + "," + getOp2() + ")";
    }

    ComponentInstance getOp1() {
      return getLeft();
    }

    ComponentInstance getOp2() {
      return getRight();
    }
  }

  /**
   * A route transfer comparator that never returns 0.
   */
  private class RouteComparator implements Comparator<SlamRoute> {

    @Override
    public int compare(final SlamRoute o1, final SlamRoute o2) {
      final double difference = RouteCostEvaluator.evaluateTransferCost(o1, 1)
          - RouteCostEvaluator.evaluateTransferCost(o2, 1);
      if (difference >= 0) {
        return 1;
      } else {
        return -1;
      }
    }

  }

  /**
   * A list of routes ordered in inverse order of transfer cost.
   */
  private class RouteList extends ConcurrentSkipListSet<SlamRoute> {

    private static final long serialVersionUID = -851695207011182681L;

    RouteList() {
      super(new RouteComparator());
    }

    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder("|");
      for (final SlamRoute r : this) {
        sb.append(r.toString() + "|");
      }
      return sb.toString();
    }
  }

  /** List of available routes. */
  private final Map<OperatorCouple, RouteList> table;

  /**
   * Instantiates a new routing table.
   *
   */
  RoutingTable() {
    super();
    this.table = new LinkedHashMap<>();
  }

  /**
   * Gets a route with a given index.
   *
   * @param op1
   *          the op 1
   * @param op2
   *          the op 2
   * @return the best route
   */
  SlamRoute getBestRoute(final ComponentInstance op1, final ComponentInstance op2) {
    final OperatorCouple obj = new OperatorCouple(op1, op2);
    if (this.table.containsKey(obj)) {
      return this.table.get(obj).first();
    }
    return null;
  }

  /**
   * Removes all the routes corresponding to the operator couple.
   *
   * @param op1
   *          the op 1
   * @param op2
   *          the op 2
   */
  void removeRoutes(final ComponentInstance op1, final ComponentInstance op2) {
    final OperatorCouple route = new OperatorCouple(op1, op2);
    if (this.table.containsKey(route)) {
      this.table.get(route).clear();
    }
  }

  /**
   * Adds a new route.
   *
   * @param op1
   *          the op 1
   * @param op2
   *          the op 2
   * @param route
   *          the route
   */
  void addRoute(final ComponentInstance op1, final ComponentInstance op2, final SlamRoute route) {
    final OperatorCouple opCouple = new OperatorCouple(op1, op2);
    if (!this.table.containsKey(opCouple)) {
      this.table.put(opCouple, new RouteList());
    }
    this.table.get(opCouple).add(route);
  }

  /**
   * Displays the table.
   *
   * @return the string
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    for (final Entry<OperatorCouple, RouteList> e : this.table.entrySet()) {
      final OperatorCouple couple = e.getKey();
      sb.append(couple + " -> " + e.getValue() + "\n");
    }
    return sb.toString();
  }

}
