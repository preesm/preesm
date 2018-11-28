package org.preesm.model.pisdf.test;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 *
 */
public class TreeContentObserverTest {

  /**
   *
   */
  class TestObserver extends EContentAdapter {
    boolean changed = false;

    @Override
    public void notifyChanged(Notification notification) {
      super.notifyChanged(notification);
      changed = true;
    }
  }

  @Test
  public void testObs() {
    final PiGraph g = PiMMUserFactory.instance.createPiGraph();
    g.setName("Test Graph");
    final Actor producer = PiMMUserFactory.instance.createActor();
    final DataOutputPort outport = PiMMUserFactory.instance.createDataOutputPort();
    producer.getDataOutputPorts().add(outport);

    final Actor consumer = PiMMUserFactory.instance.createActor();
    final DataInputPort inPort = PiMMUserFactory.instance.createDataInputPort();
    consumer.getDataInputPorts().add(inPort);

    g.addActor(producer);
    g.addActor(consumer);

    final TestObserver adapter = new TestObserver();
    g.eAdapters().add(adapter);
    Assert.assertFalse(adapter.changed);
    inPort.setName("in");
    Assert.assertTrue(adapter.changed);
  }
}
