/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2009 - 2016)
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
package org.ietr.preesm.mapper.abc.route.calcul;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListSet;
import org.apache.commons.lang3.tuple.MutablePair;
import org.ietr.preesm.core.architecture.route.Route;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.preesm.model.slam.ComponentInstance;

/**
 * Table representing the different routes available to go from one operator to another.
 *
 * @author mpelcat
 */
public class RoutingTable {

  /**
   * A couple of operators to which the routes are linked.
   */
  private class OperatorCouple extends MutablePair<ComponentInstance, ComponentInstance> {
    private static final long serialVersionUID = -451571160460519876L;

    public OperatorCouple(ComponentInstance op1, ComponentInstance op2) {
      super(op1, op2);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return "(" + this.getOp1() + "," + this.getOp2() + ")";
    }

    /**
     * Gets the op 1.
     *
     * @return the op 1
     */
    public ComponentInstance getOp1() {
      return this.getLeft();
    }

    /**
     * Gets the op 2.
     *
     * @return the op 2
     */
    public ComponentInstance getOp2() {
      return this.getRight();
    }
  }

  /**
   * A route transfer comparator that never returns 0.
   */
  private class RouteComparator implements Comparator<Route> {

    /** The transfer size. */
    private final long transferSize;

    /**
     * Instantiates a new route comparator.
     *
     * @param transferSize
     *          the transfer size
     */
    public RouteComparator(final long transferSize) {
      this.transferSize = transferSize;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(final Route o1, final Route o2) {
      long difference = o1.evaluateTransferCost(this.transferSize) - o2.evaluateTransferCost(this.transferSize);
      return (int) difference;
    }

  }

  /**
   * A list of routes ordered in inverse order of transfer cost.
   */
  private class RouteList extends ConcurrentSkipListSet<Route> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -851695207011182681L;

    /**
     * Instantiates a new route list.
     *
     * @param transferSize
     *          the transfer size
     */
    public RouteList(final long transferSize) {
      super(new RouteComparator(transferSize));
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.AbstractCollection#toString()
     */
    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder("|");
      for (final Route r : this) {
        sb.append(r.toString() + "|");
      }
      return sb.toString();
    }
  }

  /** List of available routes. */
  private final Map<OperatorCouple, RouteList> table;

  /** Scenario. */
  private final PreesmScenario scenario;

  /**
   * Instantiates a new routing table.
   *
   * @param scenario
   *          the scenario
   */
  public RoutingTable(final PreesmScenario scenario) {
    super();
    this.table = new LinkedHashMap<>();
    this.scenario = scenario;
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
  public Route getBestRoute(final ComponentInstance op1, final ComponentInstance op2) {
    for (final Entry<OperatorCouple, RouteList> e : this.table.entrySet()) {
      final OperatorCouple c = e.getKey();
      if (c.equals(new OperatorCouple(op1, op2))) {
        return this.table.get(c).first();
      }
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
  public void removeRoutes(final ComponentInstance op1, final ComponentInstance op2) {
    OperatorCouple key = null;
    final OperatorCouple route = new OperatorCouple(op1, op2);
    for (final OperatorCouple c : this.table.keySet()) {
      if (c.equals(route)) {
        key = c;
      }
    }

    if (key != null) {
      this.table.get(key).clear();
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
  public void addRoute(final ComponentInstance op1, final ComponentInstance op2, final Route route) {
    OperatorCouple key = null;
    final OperatorCouple opCouple = new OperatorCouple(op1, op2);
    for (final OperatorCouple c : this.table.keySet()) {
      if (c.equals(opCouple)) {
        key = c;
      }
    }
    RouteList list = null;
    if (key != null) {
      list = this.table.get(key);
    } else {
      list = new RouteList(this.scenario.getSimulationManager().getAverageDataSize());
      this.table.put(opCouple, list);
    }
    list.add(route);
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
      sb.append(couple + " -> " + this.table.get(couple) + "\n");
    }
    return sb.toString();
  }

}
