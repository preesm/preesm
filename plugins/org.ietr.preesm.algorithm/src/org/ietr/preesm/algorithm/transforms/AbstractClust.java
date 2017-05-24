package org.ietr.preesm.algorithm.transforms;

/**
 * Abstract class used to represent the repetition vector of loops.
 *
 * @author jhascoet
 */
public abstract class AbstractClust {

  private int repeat;

  public int getRepeat() {
    return this.repeat;
  }

  public void setRepeat(final int repeat) {
    this.repeat = repeat;
  }
}
