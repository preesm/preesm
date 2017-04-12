/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
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

package org.ietr.preesm.mapper.abc.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.mapper.model.MapperDAGVertex;

// TODO: Auto-generated Javadoc
/**
 * Association of a rank and a vertex name to export a graph total ordering.
 *
 * @author mpelcat
 */
public class VertexOrderList {

  /**
   * The Class OrderProperty.
   */
  public class OrderProperty {

    /** The name. */
    private final String name;

    /** The order. */
    private final int order;

    /**
     * Instantiates a new order property.
     *
     * @param name
     *          the name
     * @param order
     *          the order
     */
    public OrderProperty(final String name, final int order) {
      super();
      this.name = name;
      this.order = order;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
      return this.name;
    }

    /**
     * Gets the order.
     *
     * @return the order
     */
    public int getOrder() {
      return this.order;
    }

    /**
     * Corresponds to.
     *
     * @param v
     *          the v
     * @return true, if successful
     */
    public boolean correspondsTo(final MapperDAGVertex v) {
      return v.getName().equals(this.name);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return this.name;
    }
  }

  // Maintaining a list of the properties for iterating purpose in the given
  /** The ordered list. */
  // order
  private final List<OrderProperty> orderedList;

  /** The name map. */
  // Maintaining a map of the properties for research purpose of a given name
  private final Map<String, OrderProperty> nameMap;

  /**
   * Instantiates a new vertex order list.
   */
  public VertexOrderList() {
    super();
    this.orderedList = new ArrayList<>();
    this.nameMap = new HashMap<>();
  }

  /**
   * Index of.
   *
   * @param p
   *          the p
   * @return the int
   */
  public int indexOf(final OrderProperty p) {
    return this.orderedList.indexOf(p);
  }

  /**
   * Elements.
   *
   * @return the list
   */
  public List<OrderProperty> elements() {
    return Collections.unmodifiableList(this.orderedList);
  }

  /**
   * Order of.
   *
   * @param name
   *          the name
   * @return the int
   */
  public int orderOf(final String name) {
    if (this.nameMap.get(name) == null) {
      WorkflowLogger.getLogger().log(Level.SEVERE,
          "Vertex could not be scheduled, check constraints: " + name);
    } else {
      return this.nameMap.get(name).getOrder();
    }
    return -1;
  }

  /**
   * Contains.
   *
   * @param name
   *          the name
   * @return true, if successful
   */
  public boolean contains(final String name) {
    return this.nameMap.containsKey(name);
  }

  /**
   * Adds the last.
   *
   * @param p
   *          the p
   */
  public void addLast(final OrderProperty p) {
    this.orderedList.add(p);
    this.nameMap.put(p.getName(), p);
  }

  /**
   * Gets the ordered list.
   *
   * @return the ordered list
   */
  public List<OrderProperty> getOrderedList() {
    return Collections.unmodifiableList(this.orderedList);
  }
}
