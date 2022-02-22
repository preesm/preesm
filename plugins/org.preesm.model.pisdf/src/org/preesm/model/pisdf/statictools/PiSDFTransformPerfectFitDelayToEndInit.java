/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019)
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
package org.preesm.model.pisdf.statictools;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 * @author dgageot
 *
 */
public class PiSDFTransformPerfectFitDelayToEndInit {

  final PiGraph inputGraph;

  public PiSDFTransformPerfectFitDelayToEndInit(final PiGraph inputGraph) {
    this.inputGraph = inputGraph;
  }

  /**
   * @return
   */
  public PiGraph replacePerfectFitDelay() {
    // Perform copy of input graph
    final PiGraph copyGraph = PiMMUserFactory.instance.copyPiGraphWithHistory(this.inputGraph);

    // Check consistency of the graph (throw exception if recoverable or fatal error)
    final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ANALYSIS,
        CheckerErrorLevel.NONE);
    pgcc.check(copyGraph);

    copyGraph.setName(copyGraph.getName() + "_without_perfect_fit");
    // Compute BRV
    final Map<AbstractVertex, Long> brv = PiBRV.compute(copyGraph, BRVMethod.LCM);
    // Process on delay that are pipeline
    final List<Delay> delays = new LinkedList<>();
    delays.addAll(copyGraph.getAllDelays());
    for (final Delay delay : delays) {
      // Retrieve output and input port
      final Fifo fifo = delay.getContainingFifo();
      final DataOutputPort sourceOutput = fifo.getSourcePort();
      final DataInputPort targetInput = fifo.getTargetPort();
      // Compute tokens exchange
      final long delayTokens = delay.getExpression().evaluate();
      final AbstractActor containingActor = sourceOutput.getContainingActor();
      final Expression expression = sourceOutput.getExpression();
      final long evaluate = expression.evaluate();
      final Long long1 = brv.get(containingActor);
      if (long1 == null) {
        throw new PreesmRuntimeException("Could not get BRV info for " + containingActor);
      }
      final long sourceTokens = long1 * evaluate;
      final long targetTokens = brv.get(targetInput.getContainingActor()) * targetInput.getExpression().evaluate();
      // Verify that it is a perfect fit delay
      if ((delayTokens == sourceTokens) && (delayTokens == targetTokens)) {
        // Create InitActor and EndActor
        final InitActor initActor = PiMMUserFactory.instance.createInitActor();
        final EndActor endActor = PiMMUserFactory.instance.createEndActor();
        // Create DataPort
        initActor.getDataOutputPorts().add(PiMMUserFactory.instance.createDataOutputPort());
        initActor.getDataOutputPort().setName("output");
        endActor.getDataInputPorts().add(PiMMUserFactory.instance.createDataInputPort());
        endActor.getDataInputPort().setName("input");
        // Cross-reference them
        initActor.setEndReference(endActor);
        endActor.setInitReference(initActor);
        // Set name
        final String actorCouple = containingActor.getName() + "_" + targetInput.getContainingActor().getName();
        initActor.setName("init_" + actorCouple);
        endActor.setName("end_" + actorCouple);
        // Set consumption/production
        initActor.getDataOutputPort().setExpression(delayTokens);
        endActor.getDataInputPort().setExpression(delayTokens);
        // Connection
        final String dataType = fifo.getType();
        final Fifo sourceFifo = PiMMUserFactory.instance.createFifo(sourceOutput, endActor.getDataInputPort(),
            dataType);
        final Fifo targetFifo = PiMMUserFactory.instance.createFifo(initActor.getDataOutputPort(), targetInput,
            dataType);
        copyGraph.addActor(initActor);
        copyGraph.addActor(endActor);
        copyGraph.addFifo(sourceFifo);
        copyGraph.addFifo(targetFifo);
        for (final ConfigInputPort cip : delay.getConfigInputPorts()) {
          // copyGraph.getEdges().remove(cip.getIncomingDependency());
          copyGraph.removeDependency(cip.getIncomingDependency());
        }
        copyGraph.removeDelay(delay);
        copyGraph.removeFifo(fifo);
      }
    }

    // Check consistency of the graph (throw exception if recoverable or fatal error)
    final PiGraphConsistenceChecker pgccAfterwards = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ANALYSIS,
        CheckerErrorLevel.NONE);
    pgccAfterwards.check(copyGraph);
    return copyGraph;
  }

}
