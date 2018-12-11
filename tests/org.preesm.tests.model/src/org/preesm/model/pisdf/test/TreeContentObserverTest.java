/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
