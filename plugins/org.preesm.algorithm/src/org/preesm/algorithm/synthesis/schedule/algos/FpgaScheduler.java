package org.preesm.algorithm.synthesis.schedule.algos;

import java.util.HashMap;
import java.util.Map;
import org.preesm.algorithm.schedule.fpga.AbstractGenericFpgaFifoEvaluator.AnalysisResultFPGA;
import org.preesm.algorithm.schedule.fpga.FpgaAnalysis;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

public class FpgaScheduler extends AbstractPiMMScheduler {

  public String fifoEvaluator;

  public FpgaScheduler(String fifoEvaluator) {
    super();
    this.fifoEvaluator = fifoEvaluator;
  }

  @Override
  public SynthesisResult scheduleAndMap(final PiGraph piGraph, final Design slamDesign, final Scenario scenario,
      Map<String, String> parameters) {

    return exec(piGraph, slamDesign, scenario, new HashMap<>());
  }

  @Override
  protected SynthesisResult exec(PiGraph piGraph, Design slamDesign, Scenario scenario,
      Map<String, String> parameters) {
    // TODO vérifier que ça fait bien ce que je veux
    final AnalysisResultFPGA synthesisResults = FpgaAnalysis.checkAndAnalyzeAlgorithm(piGraph, scenario,
        this.fifoEvaluator);

    return synthesisResults;
  }

}
