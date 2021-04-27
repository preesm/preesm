package org.preesm.algorithm.schedule.fpga;

import java.util.Map;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.pisdf.autodelays.HeuristicLoopBreakingDelays;
import org.preesm.algorithm.schedule.fpga.AsapFpgaIIevaluator.ActorNormalizedInfos;
import org.preesm.commons.math.LongFraction;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.scenario.Scenario;

/**
 * This class evalutes fifo dependencies and size as in the SDF model: all data are produced at the end while they are
 * consumed at the beginning of a firing.
 * 
 * @author ahonorat
 */
public class FifoEvaluatorAsArray extends AbstractFifoEvaluator {

  public FifoEvaluatorAsArray(final Scenario scenario, final HeuristicLoopBreakingDelays hlbd,
      final Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos) {
    super(scenario, hlbd, mapActorNormalizedInfos);
  }

  @Override
  protected Pair<Long, Long> computeMinStartFinishTimeCons(final FifoInformations fifoInfos) {
    final long prodII = Math.max(fifoInfos.prodNorms.oriII, fifoInfos.prodNorms.cycledII);
    final long minStartTime = fifoInfos.producer.startTime + (fifoInfos.nbFiringsProdForFirstFiringCons - 1) * prodII
        + fifoInfos.prodNorms.oriET;

    final long consII = Math.max(fifoInfos.consNorms.oriII, fifoInfos.consNorms.cycledII);
    final long minFinishTime = fifoInfos.producer.finishTime + (fifoInfos.nbFiringsConsForLastFiringProd - 1) * consII
        + fifoInfos.consNorms.oriET;
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

    final int indexFifo = fifoInfos.fifoAbs.fifos.indexOf(fifoInfos.fifo);
    final long minConsStartTime = fifoInfos.consumer.minInStartTimes.get(indexFifo);
    // compute average prod firing rates
    final long remainingFiringsProd = fifoInfos.producer.nbFirings - fifoInfos.nbFiringsProdForFirstFiringCons;
    final long durationRegularProd = fifoInfos.producer.finishTime - minConsStartTime;
    final LongFraction regularProdRate = new LongFraction(remainingFiringsProd, durationRegularProd);
    // compute average cons firing rates
    final long remainingFiringsCons = fifoInfos.consumer.nbFirings - fifoInfos.nbFiringsConsForLastFiringProd;
    final long consII = Math.max(fifoInfos.consNorms.oriII, fifoInfos.consNorms.cycledII);
    final long durationRegularCons = fifoInfos.consumer.finishTime - fifoInfos.nbFiringsConsForLastFiringProd * consII;
    final LongFraction regularConsRate = new LongFraction(remainingFiringsCons, durationRegularCons);

    // size overlap
    final LongFraction regularProdOverConsRate = regularProdRate.divide(regularConsRate);
    final long overlapSize = (overlapDuration * regularProdOverConsRate.getNumerator()
        + regularProdOverConsRate.getDenominator() - 1L) / regularProdOverConsRate.getDenominator();

    // sizes not overlap but average
    final long durationRegularProdWithoutOverlap = durationRegularProd - overlapDuration;
    final long regularProdNotOverlapSize = (durationRegularProdWithoutOverlap * regularProdRate.getNumerator()
        + regularProdRate.getDenominator() - 1L) / regularProdRate.getDenominator();
    final long durationRegularConsWithoutOverlap = durationRegularCons - overlapDuration;
    final long regularConsNotOverlapSize = (durationRegularConsWithoutOverlap * regularConsRate.getNumerator()
        + regularConsRate.getDenominator() - 1L) / regularConsRate.getDenominator();

    // sums everything
    final long sizeProd = preambleSize + regularProdNotOverlapSize + overlapSize;
    final long sizeCons = epilogSize + regularConsNotOverlapSize + overlapSize;

    return dataTypeSize * Math.max(sizeProd, sizeCons);
  }

}
