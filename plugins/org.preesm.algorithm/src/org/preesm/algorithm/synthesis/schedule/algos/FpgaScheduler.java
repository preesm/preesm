package org.preesm.algorithm.synthesis.schedule.algos;

import org.preesm.algorithm.schedule.fpga.AbstractGenericFpgaFifoEvaluator.AnalysisResultFPGA;
import org.preesm.algorithm.schedule.fpga.FpgaAnalysis;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

public class FpgaScheduler implements IScheduler {

  public String fifoEvaluator;

  public FpgaScheduler(String fifoEvaluator) {
    super();
    this.fifoEvaluator = fifoEvaluator;
  }

  public SynthesisResult scheduleAndMap(final PiGraph piGraph /* SRDAG */, final Design slamDesign,
      final Scenario scenario) {

    // TODO vérifier que ça fait bien ce que je veux
    final AnalysisResultFPGA synthesisResults = FpgaAnalysis.checkAndAnalyzeAlgorithm(piGraph, scenario,
        this.fifoEvaluator);

    return synthesisResults;
  }
}
