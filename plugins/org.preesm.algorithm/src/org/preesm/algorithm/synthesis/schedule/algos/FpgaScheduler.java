package org.preesm.algorithm.synthesis.schedule.algos;

import org.preesm.algorithm.schedule.fpga.AbstractGenericFpgaFifoEvaluator.AnalysisResultFPGA;
import org.preesm.algorithm.schedule.fpga.AdfgOjalgoFpgaFifoEvaluator;
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
    // TODO ne pas mettre le fifoEvaluatorName en dur. Le problème est que fifoEvaluatorName est passé en paramètre du
    // workflew preesm, donc il faut trouver une astuce pour y avoir accès dans cette méthode. Le mettre en attribut
    // d'instance et le passer au constructeur ?
    final AnalysisResultFPGA synthesisResults = FpgaAnalysis.checkAndAnalyzeAlgorithm(piGraph, scenario,
        AdfgOjalgoFpgaFifoEvaluator.FIFO_EVALUATOR_ADFG_DEFAULT_EXACT);

    return synthesisResults;
  }
}
