package org.preesm.model.pisdf.statictools;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 * @author dgageot
 *
 */
public class PiSDFTransformPerfectFitDelayToEndInit {

  final PiGraph inputGraph;

  public PiSDFTransformPerfectFitDelayToEndInit(PiGraph inputGraph) {
    this.inputGraph = inputGraph;
  }

  /**
   * @return
   */
  public PiGraph replacePerfectFitDelay() {
    // Perform copy of input graph
    PiGraph copyGraph = PiMMUserFactory.instance.copyPiGraphWithHistory(inputGraph);
    copyGraph.setName(inputGraph.getName() + "_without_perfect_fit");
    // Compute BRV
    Map<AbstractVertex, Long> brv = PiBRV.compute(inputGraph, BRVMethod.LCM);
    // Process on delay that are pipeline
    List<Delay> delays = new LinkedList<>();
    delays.addAll(copyGraph.getAllDelays());
    for (Delay delay : delays) {
      // Retrieve output and input port
      Fifo fifo = delay.getContainingFifo();
      DataOutputPort sourceOutput = fifo.getSourcePort();
      DataInputPort targetInput = fifo.getTargetPort();
      // Compute tokens exchange
      long delayTokens = delay.getExpression().evaluate();
      long sourceTokens = brv.get(sourceOutput.getContainingActor()) * sourceOutput.getExpression().evaluate();
      long targetTokens = brv.get(targetInput.getContainingActor()) * targetInput.getExpression().evaluate();
      // Verify that it is a perfect fit delay
      if ((delayTokens == sourceTokens) && (delayTokens == targetTokens)) {
        // Create InitActor and EndActor
        InitActor initActor = PiMMUserFactory.instance.createInitActor();
        EndActor endActor = PiMMUserFactory.instance.createEndActor();
        // Create DataPort
        initActor.getDataOutputPorts().add(PiMMUserFactory.instance.createDataOutputPort());
        initActor.getDataOutputPort().setName("output");
        endActor.getDataInputPorts().add(PiMMUserFactory.instance.createDataInputPort());
        endActor.getDataInputPort().setName("input");
        // Cross-reference them
        initActor.setEndReference(endActor);
        endActor.setInitReference(initActor);
        // Set name
        String actorCouple = sourceOutput.getContainingActor().getName() + "_"
            + targetInput.getContainingActor().getName();
        initActor.setName("init_" + actorCouple);
        endActor.setName("end_" + actorCouple);
        // Set consumption/production
        initActor.getDataOutputPort().setExpression(delayTokens);
        endActor.getDataInputPort().setExpression(delayTokens);
        // Connection
        String dataType = fifo.getType();
        Fifo sourceFifo = PiMMUserFactory.instance.createFifo(sourceOutput, endActor.getDataInputPort(), dataType);
        Fifo targetFifo = PiMMUserFactory.instance.createFifo(initActor.getDataOutputPort(), targetInput, dataType);
        copyGraph.addActor(initActor);
        copyGraph.addActor(endActor);
        copyGraph.addFifo(sourceFifo);
        copyGraph.addFifo(targetFifo);
        for (ConfigInputPort cip : delay.getConfigInputPorts()) {
          copyGraph.getEdges().remove(cip.getIncomingDependency());
        }
        copyGraph.removeDelay(delay);
        copyGraph.removeFifo(fifo);
      }
    }

    return copyGraph;
  }

}
