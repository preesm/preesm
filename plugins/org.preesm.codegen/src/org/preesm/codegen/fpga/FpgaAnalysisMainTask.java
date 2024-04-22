package org.preesm.codegen.fpga;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.preesm.algorithm.mapper.ui.stats.EditorRunnable;
import org.preesm.algorithm.mapper.ui.stats.StatEditorInput;
import org.preesm.algorithm.schedule.fpga.AbstractGenericFpgaFifoEvaluator.AnalysisResultFPGA;
import org.preesm.algorithm.schedule.fpga.AdfgOjalgoFpgaFifoEvaluator;
import org.preesm.algorithm.schedule.fpga.AsapFpgaFifoEvaluator;
import org.preesm.algorithm.schedule.fpga.FpgaAnalysis;
import org.preesm.algorithm.schedule.fpga.TokenPackingAnalysis;
import org.preesm.algorithm.schedule.fpga.TokenPackingAnalysis.PackedFifoConfig;
import org.preesm.algorithm.schedule.fpga.TokenPackingTransform;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.FPGA;
import org.preesm.model.slam.check.SlamDesignPEtypeChecker;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * This task proposes to analyze throughput bottleneck of a PiGraph executed on FPGA, as well as to estimate its
 * requirements in FIFO sizes.
 *
 * @author ahonorat
 */
@PreesmTask(id = "pisdf-synthesis.fpga-estimations", name = "FPGA estimation (thoughput + FIFO sizes)",
    shortDescription = "Schedule actors and estimates the FIFO sizes.",
    description = "Schedule actors according to their ET and II thanks to an ASAP scheduler. "
        + "Only works for single FPGA architectures with single frequency domain."
        + "Periods in the graph are not taken into account.",
    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "architecture", type = Design.class),
        @Port(name = "scenario", type = Scenario.class) },
    parameters = {
        @Parameter(name = FpgaAnalysisMainTask.SHOW_SCHED_PARAM_NAME,
            description = "Whether or not the schedule must be shown at the end.",
            values = {
                @Value(name = FpgaAnalysisMainTask.SHOW_SCHED_PARAM_VALUE, effect = "False disables this feature.") }),
        @Parameter(name = FpgaAnalysisMainTask.FIFO_EVAL_PARAM_NAME,
            description = "The name of fifo evaluator to be used.",
            values = { @Value(name = AsapFpgaFifoEvaluator.FIFO_EVALUATOR_AVG, effect = "Evaluate with average mode."),
                @Value(name = AsapFpgaFifoEvaluator.FIFO_EVALUATOR_SDF, effect = "Evaluate with SDF mode."),
                @Value(name = AdfgOjalgoFpgaFifoEvaluator.FIFO_EVALUATOR_ADFG_DEFAULT_EXACT,
                    effect = "Evaluate with ADFG exact mode using ojAlgo."),
                @Value(name = AdfgOjalgoFpgaFifoEvaluator.FIFO_EVALUATOR_ADFG_DEFAULT_LINEAR,
                    effect = "Evaluate with ADFG linear approximation mode using ojAlgo.") }),
        @Parameter(name = FpgaAnalysisMainTask.PACK_TOKENS_PARAM_NAME,
            description = "Whether or not the tokens should be packed to otpimize bram usage.",
            values = { @Value(name = FpgaAnalysisMainTask.PACK_TOKENS_PARAM_VALUE,
                effect = "False disables this feature.") }) })
public class FpgaAnalysisMainTask extends AbstractTaskImplementation {

  public static final String SHOW_SCHED_PARAM_NAME  = "Show schedule ?";
  public static final String SHOW_SCHED_PARAM_VALUE = "false";

  public static final String FIFO_EVAL_PARAM_NAME  = "Fifo evaluator: ";
  public static final String FIFO_EVAL_PARAM_VALUE = AsapFpgaFifoEvaluator.FIFO_EVALUATOR_AVG;

  public static final String PACK_TOKENS_PARAM_NAME  = "Pack tokens ?";
  public static final String PACK_TOKENS_PARAM_VALUE = "false";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final PiGraph algorithm = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final String fifoEvaluatorName = parameters.get(FIFO_EVAL_PARAM_NAME);

    // check everything and perform analysis
    final FPGA fpga = checkAndGetSingleFPGA(architecture);
    AnalysisResultFPGA res = FpgaAnalysis.checkAndAnalyzeAlgorithm(algorithm, scenario, fifoEvaluatorName);

    // optionally pack tokens in BRAM
    final String packTokensStr = parameters.get(PACK_TOKENS_PARAM_NAME);
    final boolean packTokens = Boolean.parseBoolean(packTokensStr);
    if (packTokens) {
      final List<PackedFifoConfig> workList = TokenPackingAnalysis.analysis(res, scenario);
      if (!workList.isEmpty()) {
        TokenPackingTransform.transform(res, scenario, workList);

        // Adding the latency due to packing to the execution time of corresponding attached actor into the scenario
        // Added latency is equal to packing ratio + 1
        for (final PackedFifoConfig packedFifoConfig : workList) {
          final long additionalLatency = packedFifoConfig.updatedWidth / packedFifoConfig.originalWidth + 1;
          final long newExecutionTime = scenario.getTimings()
              .evaluateExecutionTimeOrDefault(packedFifoConfig.attachedActor, fpga) + additionalLatency;
          scenario.getTimings().setExecutionTime(packedFifoConfig.attachedActor, fpga, newExecutionTime);
        }

        // Rerun adfg to get new fifo depth with modified timing (because of packer/unpacker delay)
        res = FpgaAnalysis.checkAndAnalyzeAlgorithm(algorithm, scenario, fifoEvaluatorName);
        // res.flatGraph is new, so actor from worklist do not match with res.flatGraph anymore
        // Workaround is to generate a new worklist2 from the new res, which SHOULD match with worklist
        // A better solution would be to match worklist actors (from the previous res.flatGraph) to the new
        // res.flatGraph, a match by name sould be enough.
        // TODO: Fix this.
        final List<PackedFifoConfig> workList2 = TokenPackingAnalysis.analysis(res, scenario);
        TokenPackingTransform.transform(res, scenario, workList2);
      }
    }

    // Optionally shows the Gantt diagram
    final String showSchedStr = parameters.get(SHOW_SCHED_PARAM_NAME);
    final boolean showSched = Boolean.parseBoolean(showSchedStr);
    if (showSched) {
      if (res.statGenerator == null) {
        PreesmLogger.getLogger()
            .warning("The selected FIFO evaluator does not give any schedule or was not able to compute it.");
      } else {
        final IEditorInput input = new StatEditorInput(res.statGenerator);
        // Check if the workflow is running in command line mode
        try {
          // Run statistic editor
          PlatformUI.getWorkbench().getDisplay().asyncExec(new EditorRunnable(input));
        } catch (final IllegalStateException e) {
          PreesmLogger.getLogger().info("Gantt display is impossible in this context."
              + " Ignore this log entry if you are running the command line version of Preesm.");
        }
      }
    }

    // codegen
    FpgaCodeGenerator.generateFiles(scenario, fpga, res);

    return new HashMap<>();
  }

  /**
   * Check that platform is composed of single FPGA and returns it
   *
   * @param architecture
   *          Architecture to inspect
   * @return The single FPGA
   */
  private FPGA checkAndGetSingleFPGA(final Design architecture) {
    if (!SlamDesignPEtypeChecker.isSingleFPGA(architecture)) {
      throw new PreesmRuntimeException("This task must be called with a single FPGA architecture, abandon.");
    }
    return (FPGA) architecture.getProcessingElements().get(0);
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> defaultParams = new LinkedHashMap<>();
    defaultParams.put(SHOW_SCHED_PARAM_NAME, SHOW_SCHED_PARAM_VALUE);
    defaultParams.put(FIFO_EVAL_PARAM_NAME, FIFO_EVAL_PARAM_VALUE);
    return defaultParams;
  }

  @Override
  public String monitorMessage() {
    return "FPGA throughput and buffer sizes analysis (without output)";
  }

}
