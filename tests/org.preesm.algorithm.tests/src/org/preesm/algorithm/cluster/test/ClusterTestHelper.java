package org.preesm.algorithm.cluster.test;

import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

public class ClusterTestHelper {
  static final PiMMUserFactory PiMMFactory = PiMMUserFactory.instance;

  public static void createFifoLink(AbstractActor source, AbstractActor sink, int rateSource, int rateSink, String type,
      PiGraph graph) {

    final DataOutputPort sourceOut = PiMMFactory
        .createDataOutputPort(source.getName() + "To" + sink.getName() + "_Source");
    sourceOut.setExpression(rateSource);
    source.getDataOutputPorts().add(sourceOut);

    final DataInputPort sinkIn = PiMMFactory.createDataInputPort(source.getName() + "To" + sink.getName() + "_Sink");
    sinkIn.setExpression(rateSink);
    sink.getDataInputPorts().add(sinkIn);

    final Fifo f = PiMMFactory.createFifo(sourceOut, sinkIn, type);
    graph.addFifo(f);
  }

}
