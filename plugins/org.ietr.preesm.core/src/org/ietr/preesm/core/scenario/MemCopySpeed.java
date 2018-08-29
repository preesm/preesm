/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2013)
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
package org.ietr.preesm.core.scenario;

// TODO: Auto-generated Javadoc
/**
 * Representing the speed of a memcopy for a given operator type. This speed is composed of 2 elements: setupTime and
 * timePerUnit
 *
 * @author mpelcat
 */
public class MemCopySpeed {

  /** The operator def. */
  private String operatorDef;

  /** The setup time. */
  private long setupTime;

  /** The time per unit. */
  private float timePerUnit;

  /**
   * Instantiates a new mem copy speed.
   *
   * @param operatorDef
   *          the operator def
   * @param setupTime
   *          the setup time
   * @param timePerUnit
   *          the time per unit
   */
  public MemCopySpeed(final String operatorDef, final long setupTime, final float timePerUnit) {
    super();
    this.operatorDef = operatorDef;
    this.setupTime = setupTime;
    this.timePerUnit = timePerUnit;
  }

  /**
   * Gets the operator def.
   *
   * @return the operator def
   */
  public String getOperatorDef() {
    return this.operatorDef;
  }

  /**
   * Gets the setup time.
   *
   * @return the setup time
   */
  public long getSetupTime() {
    return this.setupTime;
  }

  /**
   * Gets the time per unit.
   *
   * @return the time per unit
   */
  public float getTimePerUnit() {
    return this.timePerUnit;
  }

  /**
   * Sets the operator def.
   *
   * @param operatorDef
   *          the new operator def
   */
  public void setOperatorDef(final String operatorDef) {
    this.operatorDef = operatorDef;
  }

  /**
   * Sets the setup time.
   *
   * @param setupTime
   *          the new setup time
   */
  public void setSetupTime(final Long setupTime) {
    this.setupTime = setupTime;
  }

  /**
   * Sets the time per unit.
   *
   * @param timePerUnit
   *          the new time per unit
   */
  public void setTimePerUnit(final Float timePerUnit) {
    this.timePerUnit = timePerUnit;
  }
}
