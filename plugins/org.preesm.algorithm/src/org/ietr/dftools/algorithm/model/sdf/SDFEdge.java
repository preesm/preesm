/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Antoine Morvan <antoine.morvan.pro@gmail.com> (2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2016)
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
package org.ietr.dftools.algorithm.model.sdf;

import org.ietr.dftools.algorithm.DFToolsAlgoException;
import org.ietr.dftools.algorithm.model.AbstractEdge;
import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType;
import org.ietr.dftools.algorithm.model.InterfaceDirection;
import org.ietr.dftools.algorithm.model.PropertyFactory;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.dftools.algorithm.model.types.ExpressionEdgePropertyType;
import org.ietr.dftools.algorithm.model.types.LongEdgePropertyType;
import org.ietr.dftools.algorithm.model.types.NumericalEdgePropertyTypeFactory;
import org.ietr.dftools.algorithm.model.types.StringEdgePropertyType;
import org.ietr.dftools.algorithm.model.types.TextualEdgePropertyTypeFactory;

/**
 * Class representing an SDFEdge which is an edge with production and consuming rates and length of delay specified.
 *
 * @author jpiat
 * @author kdesnos
 */
public class SDFEdge extends AbstractEdge<SDFGraph, SDFAbstractVertex> {

  /** Property name for property edge_cons. */
  public static final String EDGE_CONS = "edge_cons";

  /** Property name for property edge_delay. */
  public static final String EDGE_DELAY = "edge_delay";

  /** Property name for property edge_prod. */
  public static final String EDGE_PROD = "edge_prod";

  /** Property name for data type. */
  public static final String DATA_TYPE = "data_type";

  /** Property name for property data size. */
  public static final String DATA_SIZE = "data_size";

  /** Property name for property source_port. */
  public static final String SOURCE_PORT = "source_port";

  /** Property name for property target_port. */
  public static final String TARGET_PORT = "target_port";

  /** Property name for property target_port_modifier. */
  public static final String TARGET_PORT_MODIFIER = "target_port_modifier";

  /** Property name for property source_port_modifier. */
  public static final String SOURCE_PORT_MODIFIER = "source_port_modifier";

  /**
   * Modifier used to make a input port a pure input. <br>
   * i.e. the corresponding actor will only read the corresponding data.
   */
  public static final String MODIFIER_READ_ONLY = "read_only";

  /**
   * Modifier used to make a input port an unused input. <br>
   * i.e. the corresponding actor not use the corresponding input.
   */
  public static final String MODIFIER_UNUSED = "unused";

  /**
   * Modifier used to make a input port a pure output. <br>
   * i.e. the corresponding actor will only write the corresponding data but will not use the written data. In other
   * terms, it does not matter if the written data is overwritten by another process, even during the execution of the
   * producer actor.
   */
  public static final String MODIFIER_WRITE_ONLY = "write_only";

  static {
    AbstractEdge.PUBLIC_PROPERTIES.add(SDFEdge.EDGE_CONS);
    AbstractEdge.PUBLIC_PROPERTIES.add(SDFEdge.EDGE_DELAY);
    AbstractEdge.PUBLIC_PROPERTIES.add(SDFEdge.EDGE_PROD);
    AbstractEdge.PUBLIC_PROPERTIES.add(SDFEdge.DATA_TYPE);
    AbstractEdge.PUBLIC_PROPERTIES.add(SDFEdge.SOURCE_PORT_MODIFIER);
    AbstractEdge.PUBLIC_PROPERTIES.add(SDFEdge.TARGET_PORT_MODIFIER);
  }

  /**
   * Creates an SDFEdge with the default values (prod=0,delay=0,cons=0).
   */
  public SDFEdge() {
    super();
    setProd(new LongEdgePropertyType(0));
    setCons(new LongEdgePropertyType(0));
    setDelay(new LongEdgePropertyType(0));
    setDataSize(new LongEdgePropertyType(1));
    setDataType(new StringEdgePropertyType("char"));
  }

  /**
   * Constructs a new SDFEdge with its consuming and producing rates with a delay.
   *
   * @param prod
   *          the prod
   * @param cons
   *          the cons
   * @param delay
   *          the delay
   * @param dataType
   *          the data type
   */
  public SDFEdge(final AbstractEdgePropertyType<?> prod, final AbstractEdgePropertyType<?> cons,
      final AbstractEdgePropertyType<?> delay, final AbstractEdgePropertyType<?> dataType) {
    super();
    setProd(prod);
    setCons(cons);
    setDelay(delay);
    setDataType(dataType);
    // Data size will have to be resolved later
    setDataSize(new LongEdgePropertyType(1));
  }

  /**
   * Getter of the property <tt>cons</tt>.
   *
   * @return Returns the cons.
   */
  public AbstractEdgePropertyType<?> getCons() {
    final AbstractEdgePropertyType<?> cons = getPropertyBean().getValue(SDFEdge.EDGE_CONS);
    if (cons instanceof ExpressionEdgePropertyType) {
      ((ExpressionEdgePropertyType) cons).setExpressionSolver(getBase());
    }
    return cons;
  }

  /**
   * Getter of the property <tt>delay</tt>.
   *
   * @return Returns the delay.
   */
  public AbstractEdgePropertyType<?> getDelay() {
    final AbstractEdgePropertyType<?> delay = getPropertyBean().getValue(SDFEdge.EDGE_DELAY);
    if (delay instanceof ExpressionEdgePropertyType) {
      ((ExpressionEdgePropertyType) delay).setExpressionSolver(getBase());
    }
    return delay;
  }

  /**
   * Getter of the property <tt>prod</tt>.
   *
   * @return Returns the prod.
   */
  public AbstractEdgePropertyType<?> getProd() {
    final AbstractEdgePropertyType<?> prod = getPropertyBean().getValue(SDFEdge.EDGE_PROD);
    if (prod instanceof ExpressionEdgePropertyType) {
      ((ExpressionEdgePropertyType) prod).setExpressionSolver(getBase());
    }
    return prod;
  }

  /**
   * Getter of the property <tt>dataSize</tt>.
   *
   * @return Returns the data size.
   */
  public AbstractEdgePropertyType<?> getDataSize() {
    return getPropertyBean().getValue(SDFEdge.DATA_SIZE);
  }

  /**
   * Getter of the property <tt>DATA_TYPE</tt>.
   *
   * @return Returns the prod.
   */
  public AbstractEdgePropertyType<?> getDataType() {
    return getPropertyBean().getValue(SDFEdge.DATA_TYPE);
  }

  /**
   * Give the source interface of this edge.
   *
   * @return The source vertex interface of this edge
   */
  public SDFSinkInterfaceVertex getSourceInterface() {
    return getPropertyBean().getValue(SDFEdge.SOURCE_PORT);
  }

  /**
   * Give the target interface of this edge.
   *
   * @return The interface vertex target of this edge
   */
  public SDFSourceInterfaceVertex getTargetInterface() {
    return getPropertyBean().getValue(SDFEdge.TARGET_PORT);
  }

  /**
   * Gets the source port modifier.
   *
   * @return the source port modifier
   */
  public StringEdgePropertyType getSourcePortModifier() {
    return getPropertyBean().getValue(SDFEdge.SOURCE_PORT_MODIFIER);
  }

  /**
   * Gets the target port modifier.
   *
   * @return the target port modifier
   */
  public StringEdgePropertyType getTargetPortModifier() {
    return getPropertyBean().getValue(SDFEdge.TARGET_PORT_MODIFIER);
  }

  /**
   * Setter of the property <tt>cons</tt>.
   *
   * @param cons
   *          The cons to set.
   */
  public void setCons(final AbstractEdgePropertyType<?> cons) {
    getPropertyBean().setValue(SDFEdge.EDGE_CONS, null, cons);
    if (cons instanceof ExpressionEdgePropertyType) {
      ((ExpressionEdgePropertyType) cons).setExpressionSolver(getBase());
    }
  }

  /**
   * Setter of the property <tt>delay</tt>.
   *
   * @param delay
   *          The delay to set.
   */
  public void setDelay(final AbstractEdgePropertyType<?> delay) {
    getPropertyBean().setValue(SDFEdge.EDGE_DELAY, null, delay);
    if (delay instanceof ExpressionEdgePropertyType) {
      ((ExpressionEdgePropertyType) delay).setExpressionSolver(getBase());
    }
  }

  /**
   * Sets the target port modifier.
   *
   * @param modifier
   *          the new target port modifier
   */
  public void setTargetPortModifier(final AbstractEdgePropertyType<?> modifier) {
    if (modifier != null) {
      getPropertyBean().setValue(SDFEdge.TARGET_PORT_MODIFIER, null, modifier);
    } else {
      getPropertyBean().removeProperty(SDFEdge.TARGET_PORT_MODIFIER);
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
      getPropertyBean().setValue(SDFEdge.SOURCE_PORT_MODIFIER, null, modifier);
    } else {
      getPropertyBean().removeProperty(SDFEdge.SOURCE_PORT_MODIFIER);
    }
  }

  /**
   * Setter of the property <tt>prod</tt>.
   *
   * @param prod
   *          The prod to set.
   */
  public void setProd(final AbstractEdgePropertyType<?> prod) {
    getPropertyBean().setValue(SDFEdge.EDGE_PROD, null, prod);
    if (prod instanceof ExpressionEdgePropertyType) {
      ((ExpressionEdgePropertyType) prod).setExpressionSolver(getBase());
    }
  }

  /**
   * Setter of the property <tt>DATA_TYPE</tt>.
   *
   * @param type
   *          The type to set.
   */
  public void setDataType(final AbstractEdgePropertyType<?> type) {
    getPropertyBean().setValue(SDFEdge.DATA_TYPE, null, type);
  }

  /**
   * Setter of the property <tt>DATA_SIZE</tt>.
   *
   * @param type
   *          The type to set.
   */
  public void setDataSize(final AbstractEdgePropertyType<?> type) {
    getPropertyBean().setValue(SDFEdge.DATA_SIZE, null, type);
  }

  /**
   * Set this edge source interface.
   *
   * @param source
   *          The source interface to set for this edge
   */
  public void setSourceInterface(final SDFInterfaceVertex source) {
    getPropertyBean().setValue(SDFEdge.SOURCE_PORT, null, source);
    if (source != null) {
      source.setDirection(InterfaceDirection.OUTPUT);
    }
  }

  /**
   * Set this edge target interface.
   *
   * @param target
   *          The target interface to set for this edge
   */
  public void setTargetInterface(final SDFInterfaceVertex target) {
    getPropertyBean().setValue(SDFEdge.TARGET_PORT, null, target);
    if (target != null) {
      target.setDirection(InterfaceDirection.INPUT);
    }
  }

  /**
   * Test if the given edge has the same properties than this edge.
   *
   * @param edge
   *          The edge to compare with
   * @return True if the given edge has the same properties, false otherwise
   */
  public boolean compare(final SDFEdge edge) {

    try {
      return super.compare(edge) && edge.getSourceInterface().getName().equals(getSourceInterface().getName())
          && edge.getTargetInterface().getName().equals(getTargetInterface().getName())
          && (getCons().longValue() == edge.getCons().longValue())
          && (getProd().longValue() == edge.getProd().longValue())
          && (getDelay().longValue() == edge.getDelay().longValue());
    } catch (final InvalidExpressionException e) {
      throw new DFToolsAlgoException("Could not compare edges", e);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getSource().toString() + "." + getSourceInterface().getName() + " > " + getTarget().toString() + "."
        + getTargetInterface().getName() + " {d=" + getDelay() + ", p=" + getProd() + ", c=" + getCons() + "}";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.PropertySource#getFactoryForProperty(java.lang.String)
   */
  @Override
  public PropertyFactory getFactoryForProperty(final String propertyName) {
    if (propertyName.equals(SDFEdge.EDGE_CONS) || propertyName.equals(SDFEdge.EDGE_PROD)
        || propertyName.equals(SDFEdge.EDGE_DELAY)) {
      return NumericalEdgePropertyTypeFactory.getInstance();
    } else if (propertyName.equals(SDFEdge.DATA_TYPE) || propertyName.equals(SDFEdge.SOURCE_PORT_MODIFIER)
        || propertyName.equals(SDFEdge.TARGET_PORT_MODIFIER)) {
      return TextualEdgePropertyTypeFactory.getInstance();
    }
    return null;
  }
}
