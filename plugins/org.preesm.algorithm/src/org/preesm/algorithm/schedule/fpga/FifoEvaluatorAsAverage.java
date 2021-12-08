package org.preesm.algorithm.schedule.fpga;

import java.util.Map;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.pisdf.autodelays.HeuristicLoopBreakingDelays;
import org.preesm.algorithm.schedule.fpga.AsapFpgaIIevaluator.ActorNormalizedInfos;
import org.preesm.commons.math.LongFraction;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.scenario.Scenario;

public class FifoEvaluatorAsAverage extends AbstractFifoEvaluator {

  /**
   * This class evalutes fifo dependencies and size as if all data are produced/consumed according to their average
   * rate, i.e. total tokens divided by (finish time minus start time).
   * 
   * @author ahonorat
   */
  public FifoEvaluatorAsAverage(Scenario scenario, HeuristicLoopBreakingDelays hlbd,
      Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos) {
    super(scenario, hlbd, mapActorNormalizedInfos);
  }

  @Override
  protected Pair<Long, Long> computeMinStartFinishTimeCons(FifoInformations fifoInfos) {
    final long prodRate = fifoInfos.fifo.getSourcePort().getPortRateExpression().evaluate();
    final long durationUntilFirstProd = (fifoInfos.prodNorms.oriET + prodRate - 1L) / prodRate;
    final long minStartTime = fifoInfos.producer.startTime + durationUntilFirstProd;

    final long consRate = fifoInfos.fifo.getTargetPort().getPortRateExpression().evaluate();
    final long durationAfterLastCons = (fifoInfos.consNorms.oriET + consRate - 1L) / consRate;
    final long minFinishTime = fifoInfos.producer.finishTime + durationAfterLastCons;

    return new Pair<>(minStartTime, minFinishTime);
  }

  @Override
  protected long computeFifoSize(FifoInformations fifoInfos) {
    final long dataTypeSize = scenario.getSimulationInfo().getDataTypeSizeOrDefault(fifoInfos.fifo.getType());
    final long prodRate = fifoInfos.fifo.getSourcePort().getPortRateExpression().evaluate();

    final long overlapDuration = fifoInfos.producer.finishTime - fifoInfos.consumer.startTime;
    if (overlapDuration <= 0) {
      // simple case, there is no overlap so we must store all productions
      return dataTypeSize * prodRate * fifoInfos.producer.nbFirings;
    }
    // otherwise we split the execution in multiple phases:
    // 1. preamble -- only production of token (at average speed)
    // 2. overlap -- overlap between producer and consumer (at average speed)
    // 3. epilog -- only consumption of token (at average speed)
    // At last, the total size is max (1+2, max(2,3))

    final long consRate = fifoInfos.fifo.getTargetPort().getPortRateExpression().evaluate();

    // 1. compute average prod token rates
    final long totalProductionSize = prodRate * fifoInfos.producer.nbFirings;
    final long totalProductionDuration = fifoInfos.producer.finishTime - fifoInfos.producer.startTime;
    final LongFraction averageTokenProduction = new LongFraction(totalProductionSize, totalProductionDuration);
    final long preambleSize = ((totalProductionDuration - overlapDuration) * averageTokenProduction.getNumerator())
        / averageTokenProduction.getDenominator();

    // 3. compute average cons token rates
    final long totalConsumptionSize = consRate * fifoInfos.consumer.nbFirings;
    final long totalConsumptionDuration = fifoInfos.consumer.finishTime - fifoInfos.consumer.startTime;
    final LongFraction averageTokenConsumption = new LongFraction(totalConsumptionSize, totalConsumptionDuration);
    final long epilogSize = ((totalConsumptionDuration - overlapDuration) * averageTokenConsumption.getNumerator())
        / averageTokenConsumption.getDenominator();

    // 2. overlap sizes, two possible cases, only increasing variation is counted
    // TODO check divisions by 0
    final long overlapProdSize = totalProductionSize - preambleSize;
    final long overlapConsSize = totalConsumptionSize - epilogSize;
    long overlapSize = 0L;
    if (overlapProdSize > overlapConsSize) {
      final long extraPeak = (overlapProdSize + overlapConsSize - 1L) / overlapConsSize;
      overlapSize = (overlapProdSize - overlapConsSize) + extraPeak;
    } else if (overlapProdSize > 0) {
      overlapSize = 1L;
    } // else there is no prod so overlap size is 0L (it's only decreasing)

    // sums everything
    final long sizeProd = preambleSize + overlapSize;
    final long sizeCons = Math.max(overlapSize, epilogSize);

    return dataTypeSize * Math.max(sizeProd, sizeCons);
  }

}
