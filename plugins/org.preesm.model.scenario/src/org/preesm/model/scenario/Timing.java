/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2013)
 * Pengcheng Mu <pengcheng.mu@insa-rennes.fr> (2008)
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
package org.preesm.model.scenario;

import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.expression.ExpressionEvaluator;
import org.preesm.model.slam.component.Component;

/**
 * A timing links a vertex (either from SDFGraph or from PiGraph) and an operator definition to a time. Ids are used to
 * make the scenario independent from model implementations.
 *
 * @author mpelcat
 */
public class Timing {

  private String stringValue;

  private final Component component;

  private final AbstractActor actor;

  /**
   */

  public Timing(final Component component, final AbstractActor actor) {
    this(component, actor, TimingManager.DEFAULT_TASK_TIME);
  }

  /**
   */
  public Timing(final Component component, final AbstractActor actor, final long time) {
    this(component, actor, String.valueOf(time));
  }

  /**
   */
  public Timing(final Component component, final AbstractActor actor, final String expression) {
    this.component = component;
    this.actor = actor;
    this.stringValue = expression;
  }

  /**
   */
  public Component getComponent() {
    return this.component;
  }

  /**
   * Gets the time.
   *
   * @return time, only if it is available (if the expression have been evaluated)
   */
  public long getTime() {
    return ExpressionEvaluator.evaluate(getActor(), getStringValue());
  }

  public boolean canEvaluate() {
    return ExpressionEvaluator.canEvaluate(getActor(), getStringValue());
  }

  /**
   * Gets the vertex id.
   *
   * @return the vertex id
   */
  public AbstractActor getActor() {
    return this.actor;
  }

  /**
   * The given time is set if it is strictly positive. Otherwise, 1 is set. In every cases, the expression is set as the
   * corresponding string and considered evaluated
   *
   * @param time
   *          the new time we want to set
   */
  public void setTime(final long time) {
    this.stringValue = String.valueOf(time);
  }

  /**
   * Gets the string value.
   *
   * @return the string value
   */
  public String getStringValue() {
    return this.stringValue;
  }

  /**
   * Sets the string value.
   *
   * @param stringValue
   *          the new string value
   */
  public void setStringValue(final String stringValue) {
    this.stringValue = stringValue;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    boolean equals = false;

    if (obj instanceof Timing) {
      final Timing otherT = (Timing) obj;
      equals = this.component.equals(otherT.getComponent());
      equals &= this.actor.equals((otherT.getActor()));
      equals &= this.stringValue.equals((otherT.stringValue));
    }

    return equals;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "{" + this.actor.getVertexPath() + " on " + this.component.getVlnv().getName() + " -> " + this.stringValue
        + "}";
  }
}
