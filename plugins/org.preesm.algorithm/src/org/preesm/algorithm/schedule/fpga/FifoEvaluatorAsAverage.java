/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021 - 2022)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2021 - 2024)
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
 * This class evaluates fifo dependencies and size as if all data are produced/consumed according to their average rate,
 * i.e. total tokens divided by (finish time minus start time).
 *
 * @author ahonorat
 */
public class FifoEvaluatorAsAverage extends AbstractAsapFpgaFifoEvaluator {

  public FifoEvaluatorAsAverage(Scenario scenario, HeuristicLoopBreakingDelays hlbd,
      Map<AbstractActor, ActorNormalizedInfos> mapActorNormalizedInfos) {
    super(scenario, hlbd, mapActorNormalizedInfos);
  }

  @Override
  protected Pair<Long, Long> computeMinStartFinishTimeCons(FifoInformations fifoInfos) {
    final long prodRate = fifoInfos.fifo.getSourcePort().getPortRateExpression().evaluateAsLong();
    final long durationUntilFirstProd = (fifoInfos.prodNorms.oriET + prodRate - 1L) / prodRate;
    final long minStartTime = fifoInfos.producer.startTime + durationUntilFirstProd;

    final long consRate = fifoInfos.fifo.getTargetPort().getPortRateExpression().evaluateAsLong();
    final long durationAfterLastCons = (fifoInfos.consNorms.oriET + consRate - 1L) / consRate;
    final long minFinishTime = fifoInfos.producer.finishTime + durationAfterLastCons;

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
    // 1. preamble -- only production of token (at average speed)
    // 2. overlap -- overlap between producer and consumer (at average speed)
    // 3. epilog -- only consumption of token (at average speed)
    // At last, the total size is max (1+2, max(2,3))

    final long consRate = fifoInfos.fifo.getTargetPort().getPortRateExpression().evaluateAsLong();

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
