package org.ietr.preesm.algorithm.transforms;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a sequence of repeated clustered actors and sequences of actors.
 *
 * @author jhascoet
 */
public class ClustSequence extends AbstractClust {

  private List<AbstractClust> seq = new ArrayList<>();

  public List<AbstractClust> getSeq() {
    return this.seq;
  }

  public void setSeq(final List<AbstractClust> seq) {
    this.seq = seq;
  }
}
