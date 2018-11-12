/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
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
package org.ietr.dftools.algorithm.model.dag;

import org.ietr.dftools.algorithm.model.AbstractEdge;
import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType;
import org.ietr.dftools.algorithm.model.PropertyFactory;
import org.ietr.dftools.algorithm.model.types.StringEdgePropertyType;

/**
 * Class used to represent an Edge in a Directed Acyclic Graph.
 *
 * @author jpiat
 */
public class DAGEdge extends AbstractEdge<DirectedAcyclicGraph, DAGVertex> {

  /** Key to access to property weight. */
  public static final String WEIGHT = "Weight";

  /** Key to access to property aggregate. */
  public static final String AGGREGATE = "aggregate";

  /** Key to access to property containing_edge. */
  public static final String CONTAINING_EDGE = "containing_edge";

  /** Property name for property target_port_modifier. */
  public static final String TARGET_PORT_MODIFIER = "target_port_modifier";

  /** Property name for property source_port_modifier. */
  public static final String SOURCE_PORT_MODIFIER = "source_port_modifier";

  static {
    AbstractEdge.PUBLIC_PROPERTIES.add(DAGEdge.WEIGHT);
  }

  /**
   * Creates a new empty DAGEdge.
   */
  public DAGEdge() {
    super();
  }

  /**
   * Creates a new DAGEdge with the given property.
   *
   * @param w
   *          the w
   */
  public DAGEdge(final AbstractEdgePropertyType<?> w) {
    super();
    setWeight(w);
  }

  /**
   * Gives this DAGEdge weight.
   *
   * @return This DAGEdge weight
   */
  public AbstractEdgePropertyType<?> getWeight() {
    if (getPropertyBean().getValue(DAGEdge.WEIGHT) != null) {
      return getPropertyBean().getValue(DAGEdge.WEIGHT);
    }
    return null;
  }

  /**
   * Set this DAGEdge weight.
   *
   * @param w
   *          The weight to set for this DAGEdge
   */
  public void setWeight(final AbstractEdgePropertyType<?> w) {
    getPropertyBean().setValue(DAGEdge.WEIGHT, w);
  }

  /**
   * Gives this DAGEdge aggregate.
   *
   * @return This DAGEdge aggregate
   */
  public EdgeAggregate getAggregate() {
    if (getPropertyBean().getValue(DAGEdge.AGGREGATE) != null) {
      return getPropertyBean().getValue(DAGEdge.AGGREGATE);
    } else {
      final EdgeAggregate agg = new EdgeAggregate();
      setAggregate(agg);
      return agg;
    }
  }

  /**
   * Set this DAGEdge aggregate.
   *
   * @param a
   *          The weight to set for this DAGEdge
   */
  public void setAggregate(final EdgeAggregate a) {
    getPropertyBean().setValue(DAGEdge.AGGREGATE, a);
  }

  /**
   * Set this DAGEdge containing edge
   *
   * @param e
   *          the containing edge
   */
  public void setContainingEdge(final DAGEdge e) {
    getPropertyBean().setValue(DAGEdge.CONTAINING_EDGE, e);
  }

  /**
   * Gives this DAGEdge containing edge.
   *
   * @return This DAGEdge containing edge
   */
  public DAGEdge getContainingEdge() {
    if (getPropertyBean().getValue(DAGEdge.CONTAINING_EDGE) != null) {
      return getPropertyBean().getValue(DAGEdge.CONTAINING_EDGE);
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return " w=" + getWeight();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.PropertySource#getFactoryForProperty(java.lang.String)
   */
  @Override
  public PropertyFactory getFactoryForProperty(final String propertyName) {
    return null;
  }

  /**
   * Sets the target port modifier.
   *
   * @param modifier
   *          the new target port modifier
   */
  public void setTargetPortModifier(final AbstractEdgePropertyType<?> modifier) {
    if (modifier != null) {
      getPropertyBean().setValue(DAGEdge.TARGET_PORT_MODIFIER, null, modifier);
    } else {
      getPropertyBean().removeProperty(DAGEdge.TARGET_PORT_MODIFIER);
    }
  }

  /**
   * Sets the source port modifier.
   *
   * @param modifier
   *          the new source port modifier
   */
  public void setSourcePortModifier(final AbstractEdgePropertyType<?> modifier) {
    if (modifier != null) {
      getPropertyBean().setValue(DAGEdge.SOURCE_PORT_MODIFIER, null, modifier);
    } else {
      getPropertyBean().removeProperty(DAGEdge.SOURCE_PORT_MODIFIER);
    }
  }

  /**
   * Gets the source port modifier.
   *
   * @return the source port modifier
   */
  public StringEdgePropertyType getSourcePortModifier() {
    return getPropertyBean().getValue(DAGEdge.SOURCE_PORT_MODIFIER);
  }

  /**
   * Gets the target port modifier.
   *
   * @return the target port modifier
   */
  public StringEdgePropertyType getTargetPortModifier() {
    return getPropertyBean().getValue(DAGEdge.TARGET_PORT_MODIFIER);
  }

}
