/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021 - 2022) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021 - 2022)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2021)
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

package org.preesm.algorithm.schedule.fpga;

import java.util.Map;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.pisdf.autodelays.HeuristicLoopBreakingDelays;
import org.preesm.algorithm.schedule.fpga.AbstractGenericFpgaFifoEvaluator.ActorNormalizedInfos;
import org.preesm.commons.math.LongFraction;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.scenario.Scenario;

/**
 * This class evaluates fifo dependencies and size as in the SDF model: all data are produced at the end while they are
 * consumed at the beginning of a firing.
 *
 * @author ahonorat
 */
public class FifoEvaluatorAsArray extends AbstractAsapFpgaFifoEvaluator {

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
    final long dataTypeSize = scenario.getSimulationInfo().getDataTypeSizeInBit(fifoInfos.fifo.getType());
    final long prodRate = fifoInfos.fifo.getSourcePort().getPortRateExpression().evaluateAsLong();

    final long overlapDuration = fifoInfos.producer.finishTime - fifoInfos.consumer.startTime;
    if (overlapDuration <= 0) {
      // simple case, there is no overlap so we must store all productions
      return dataTypeSize * prodRate * fifoInfos.producer.nbFirings;
    }
    // otherwise we split the execution in multiple phases:
    // 1. preamble -- production of token necessary for the first consumption (at max speed)
    // 2. regular prod -- production of token until the consumer start time (may be 0, at average speed)
    // 3. regular overlap -- overlap between producer and consumer (at average speed)
    // 4. regular cons -- consumption of token before the ones of the last producer firing (may be 0, at average speed)
    // 5. epilog -- consumption of token produced by the last producer firing (at max speed)
    // 4+5 could be done at the same time, but we separate them to get the average consumption,
    // with a computation symmetrical to the production
    // At last, the total size is max (1+2+3, max(3,4+5))

    // 1. preamble
    final long preambleSize = prodRate * fifoInfos.nbFiringsProdForFirstFiringCons;
    final long prodIImax = Math.max(fifoInfos.prodNorms.oriII, fifoInfos.prodNorms.cycledII);
    final long preambleDuration = (fifoInfos.nbFiringsProdForFirstFiringCons - 1) * prodIImax
        + fifoInfos.prodNorms.oriET;

    // 5. epilog
    final long consRate = fifoInfos.fifo.getTargetPort().getPortRateExpression().evaluateAsLong();
    final long epilogSize = consRate * fifoInfos.nbFiringsConsForLastFiringProd;
    final long consIImax = Math.max(fifoInfos.consNorms.oriII, fifoInfos.consNorms.cycledII);
    final long epilogDuration = fifoInfos.nbFiringsConsForLastFiringProd * consIImax;
    // at the opposite of the preamble, we do not wait the complete ET but only the II since we start
    // the execution by consuming the token in classic SDF model

    // 2. regular prod, compute average prod firing rates
    final long remainingFiringsProd = fifoInfos.producer.nbFirings - fifoInfos.nbFiringsProdForFirstFiringCons;
    final long durationRegularProd = fifoInfos.producer.finishTime - (fifoInfos.producer.startTime + preambleDuration);
    final LongFraction regularProdFiringRate = new LongFraction(remainingFiringsProd, durationRegularProd);
    // 3+2. firings average
    final long maxFiringProdOverlap = (overlapDuration * regularProdFiringRate.getNumerator())
        / regularProdFiringRate.getDenominator();
    final long firingProdOverlap = Math.min(remainingFiringsProd, maxFiringProdOverlap);
    final long firingProdRegularNotOverlap = remainingFiringsProd - firingProdOverlap;
    final long regularProdNotOverlapSize = prodRate * firingProdRegularNotOverlap;

    // 4. regular cons, compute average cons firing rates
    final long remainingFiringsCons = fifoInfos.consumer.nbFirings - fifoInfos.nbFiringsConsForLastFiringProd;
    final long durationRegularCons = (fifoInfos.consumer.finishTime - epilogDuration) - fifoInfos.consumer.startTime;
    final LongFraction regularConsFiringRate = new LongFraction(remainingFiringsCons, durationRegularCons);
    // 3+4. firings average
    final long maxFiringConsOverlap = (overlapDuration * regularConsFiringRate.getNumerator())
        / regularConsFiringRate.getDenominator();
    final long firingConsOverlap = Math.min(remainingFiringsCons, maxFiringConsOverlap);
    final long firingConsRegularNotOverlap = remainingFiringsCons - firingConsOverlap;
    final long regularConsNotOverlapSize = consRate * firingConsRegularNotOverlap;

    // 3. overlap size, four possible cases, only increasing variation is counted
    // TODO check divisions by 0
    final long overlapProd = prodRate * firingProdOverlap;
    final long overlapCons = consRate * firingConsOverlap;
    long overlapSize = 0L;
    if (prodRate > consRate) {
      if (overlapProd > overlapCons) {
        final long extraPeak = consRate * (firingConsOverlap + firingProdOverlap - 1L) / firingProdOverlap;
        overlapSize = (overlapProd - overlapCons) + extraPeak;
      } else if (firingProdOverlap > 0) {
        overlapSize = prodRate;
      } // else there is no prod so overlap size is 0L (it's only decreasing)
    } else if (overlapProd < overlapCons) {
      final long extraPeak = prodRate * (firingProdOverlap + firingConsOverlap - 1L) / firingConsOverlap;
      overlapSize = extraPeak;
    } else if (firingConsOverlap > 0) {
      overlapSize = (overlapProd - overlapCons) + consRate;
    } else {
      overlapSize = overlapProd;
    }

    // sums everything
    final long sizeProd = preambleSize + regularProdNotOverlapSize + overlapSize;
    final long sizeCons = Math.max(overlapSize, regularConsNotOverlapSize + epilogSize);

    return dataTypeSize * Math.max(sizeProd, sizeCons);
  }

}
