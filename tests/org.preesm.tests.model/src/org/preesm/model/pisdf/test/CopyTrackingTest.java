package org.preesm.model.pisdf.test;

import org.junit.Assert;
import org.junit.Test;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 *
 */
public class CopyTrackingTest {

  @Test
  public void testCopy() {
    final PiGraph originalGraph = PiMMUserFactory.instance.createPiGraph();
    final PiGraph copy = PiMMUserFactory.instance.copyWithHistory(originalGraph);
    final PiGraph originalObject = PreesmCopyTracker.getSource(copy);
    Assert.assertEquals(originalGraph, originalObject);
  }

  @Test
  public void testMultiCopy() {
    final PiGraph originalGraph = PiMMUserFactory.instance.createPiGraph();
    final PiGraph copy = PiMMUserFactory.instance.copyWithHistory(originalGraph);
    final PiGraph copy2 = PiMMUserFactory.instance.copyWithHistory(copy);
    final PiGraph copy3 = PiMMUserFactory.instance.copyWithHistory(copy2);
    final PiGraph copy4 = PiMMUserFactory.instance.copyWithHistory(copy3);

    final PiGraph sourceCopy3 = PreesmCopyTracker.getSource(copy3);
    Assert.assertEquals(sourceCopy3, copy2);

    final PiGraph originalSourceCopy4 = PreesmCopyTracker.getOriginalSource(copy4);
    Assert.assertEquals(originalSourceCopy4, originalGraph);

  }
}
