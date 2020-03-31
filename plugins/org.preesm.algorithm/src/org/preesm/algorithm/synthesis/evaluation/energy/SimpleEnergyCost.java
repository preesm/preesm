package org.preesm.algorithm.synthesis.evaluation.energy;

import org.preesm.algorithm.synthesis.evaluation.ISynthesisCost;

/**
 * Simple energy cost.
 * 
 * @author ahonorat
 */
public class SimpleEnergyCost implements ISynthesisCost<Long> {

  private final Long value;

  /**
   * Default constructor
   * 
   * @param value
   *          Total energy.
   */
  public SimpleEnergyCost(long value) {
    this.value = value;
  }

  @Override
  public int compareTo(ISynthesisCost<Long> arg0) {
    if (arg0 instanceof SimpleEnergyCost) {
      return value.compareTo(arg0.getValue());
    }
    return 0;
  }

  @Override
  public Long getValue() {
    return value;
  }

}
