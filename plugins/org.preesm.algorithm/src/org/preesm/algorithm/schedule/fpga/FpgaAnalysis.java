package org.preesm.algorithm.schedule.fpga;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.schedule.fpga.AbstractGenericFpgaFifoEvaluator.AnalysisResultFPGA;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.statictools.PiSDFFlattener;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.check.FifoTypeChecker;

public class FpgaAnalysis {
  private FpgaAnalysis() {
    // forbid instantiation
  }

  /**
   * Check the inputs and analyze the graph for FPGA scheduling + buffer sizing.
   *
   * @param algorithm
   *          Graph to be analyzed (will be flattened).
   * @param scenario
   *          Application informations.
   * @param fifoEvaluatorName
   *          String representing the evaluator to be used (for scheduling and fifo sizing).
   * @return The analysis results.
   */
  public static AnalysisResultFPGA checkAndAnalyzeAlgorithm(final PiGraph algorithm, final Scenario scenario,
      final String fifoEvaluatorName) {
    if (algorithm.getAllDelays().stream().anyMatch(x -> (x.getLevel() != PersistenceLevel.PERMANENT))) {
      throw new PreesmRuntimeException("This task must be called on PiGraph with only permanent delays.");
    }
    FifoTypeChecker.checkMissingFifoTypeSizes(scenario);

    // Flatten the graph
    final PiGraph flatGraph = PiSDFFlattener.flatten(algorithm, true);
    // Expose delays as actors on the graph
    DelayActorTransform.transform(scenario, flatGraph);
    // check interfaces
    final Map<AbstractVertex, Long> brv = PiBRV.compute(flatGraph, BRVMethod.LCM);
    final Map<InterfaceActor, Pair<Long, Long>> interfaceRates = checkInterfaces(flatGraph, brv);
    if (interfaceRates.values().stream().anyMatch(Objects::isNull)) {
      throw new PreesmRuntimeException("Some interfaces have weird rates (see log), abandon.");
    }

    // schedule the graph
    final AbstractGenericFpgaFifoEvaluator evaluator = AbstractGenericFpgaFifoEvaluator
        .getEvaluatorInstance(fifoEvaluatorName);
    final AnalysisResultFPGA resHolder = new AnalysisResultFPGA(flatGraph, brv, interfaceRates);
    evaluator.performAnalysis(scenario, resHolder);
    return resHolder;
  }

  private static Map<InterfaceActor, Pair<Long, Long>> checkInterfaces(final PiGraph flatGraph,
      final Map<AbstractVertex, Long> brv) {
    final Map<InterfaceActor, Pair<Long, Long>> result = new LinkedHashMap<>();
    flatGraph.getActors().stream().filter(InterfaceActor.class::isInstance).forEach(x -> {
      final InterfaceActor ia = (InterfaceActor) x;
      final DataPort iaPort = ia.getDataPort();
      if (iaPort.getFifo() == null) {
        return; // if not connected, we do not care
      }
      DataPort aaPort = null;
      if (iaPort instanceof DataInputPort) {
        aaPort = iaPort.getFifo().getSourcePort();
      }
      if (iaPort instanceof DataOutputPort) {
        aaPort = iaPort.getFifo().getTargetPort();
      }
      final long aaRate = brv.get(aaPort.getContainingActor()) * aaPort.getExpression().evaluate();
      final long iaRate = iaPort.getExpression().evaluate();
      if (aaRate % iaRate != 0) {
        PreesmLogger.getLogger().warning(
            "Interface rate of " + ia.getName() + " does not divide the total rate of the actor connected to it.");
        result.put(ia, null);
      } else {
        result.put(ia, new Pair<>(iaRate, (aaRate / iaRate)));
      }
    });
    return result;
  }
}
