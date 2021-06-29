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
      // simple case
      return dataTypeSize * prodRate * fifoInfos.producer.nbFirings;
    }

    final long preambleSize = prodRate * fifoInfos.nbFiringsProdForFirstFiringCons;
    final long consRate = fifoInfos.fifo.getTargetPort().getPortRateExpression().evaluate();
    final long epilogSize = consRate * fifoInfos.nbFiringsConsForLastFiringProd;

    final long minConsStartTime = fifoInfos.consumer.startTime;
    // compute average prod firing rates
    final long remainingFiringsProd = fifoInfos.producer.nbFirings - fifoInfos.nbFiringsProdForFirstFiringCons;
    final long durationRegularProd = fifoInfos.producer.finishTime - minConsStartTime;
    final LongFraction regularProdFiringRate = new LongFraction(remainingFiringsProd, durationRegularProd);
    // compute average cons firing rates
    final long remainingFiringsCons = fifoInfos.consumer.nbFirings - fifoInfos.nbFiringsConsForLastFiringProd;
    final long consII = Math.max(fifoInfos.consNorms.oriII, fifoInfos.consNorms.cycledII);
    final long durationRegularCons = fifoInfos.consumer.finishTime - fifoInfos.nbFiringsConsForLastFiringProd * consII;
    final LongFraction regularConsFiringRate = new LongFraction(remainingFiringsCons, durationRegularCons);

    // firings average
    final long maxFiringProdOverlap = (overlapDuration * regularProdFiringRate.getNumerator())
        / regularProdFiringRate.getDenominator();
    final long firingProdOverlap = Math.min(remainingFiringsProd, maxFiringProdOverlap);
    final long firingProdRegularNotOverlap = remainingFiringsProd - firingProdOverlap;

    final long maxFiringConsOverlap = (overlapDuration * regularConsFiringRate.getNumerator())
        / regularConsFiringRate.getDenominator();
    final long firingConsOverlap = Math.min(remainingFiringsCons, maxFiringConsOverlap);
    final long firingConsRegularNotOverlap = remainingFiringsCons - firingConsOverlap;

    // size prod not overlap but average
    final long regularProdNotOverlapSize = prodRate * firingProdRegularNotOverlap;

    // sizes overlap, four possible cases
    final long overlapProd = prodRate * firingProdOverlap;
    final long overlapCons = consRate * firingConsOverlap;
    long overlapSize = 0L;
    if (prodRate > consRate) {
      if (overlapProd > overlapCons) {
        final long extraPeak = (overlapCons + overlapProd - 1L) / overlapProd;
        overlapSize = (overlapProd - overlapCons) + extraPeak;
      } else if (firingProdOverlap > 0) {
        overlapSize = 1L;
      }
    } else {
      if (overlapProd < overlapCons) {
        final long extraPeak = (overlapProd + overlapCons - 1L) / overlapCons;
        overlapSize = extraPeak;
      } else if (firingConsOverlap > 0) {
        overlapSize = (overlapProd - overlapCons) + 1L;
      } else {
        overlapSize = overlapProd;
      }
    }

    // size cons not overlap but average
    final long regularConsNotOverlapSize = consRate * firingConsRegularNotOverlap;

    // sums everything
    final long sizeProd = preambleSize + regularProdNotOverlapSize + overlapSize;
    final long sizeCons = Math.max(epilogSize + regularConsNotOverlapSize, overlapSize);

    return dataTypeSize * Math.max(sizeProd, sizeCons);
  }

}
