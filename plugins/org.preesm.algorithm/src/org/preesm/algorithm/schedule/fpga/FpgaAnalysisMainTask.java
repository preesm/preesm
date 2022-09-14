package org.preesm.algorithm.schedule.fpga;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.mapper.ui.stats.EditorRunnable;
import org.preesm.algorithm.mapper.ui.stats.StatEditorInput;
import org.preesm.algorithm.schedule.fpga.AbstractGenericFpgaFifoEvaluator.AnalysisResultFPGA;
import org.preesm.algorithm.schedule.fpga.TokenPackingAnalysis.PackedFifoConfig;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
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
                @Value(name = AdfgFpgaFifoEvaluator.FIFO_EVALUATOR_ADFG_EXACT,
                    effect = "Evaluate with ADFG exact mode."),
                @Value(name = AdfgFpgaFifoEvaluator.FIFO_EVALUATOR_ADFG_LINEAR,
                    effect = "Evaluate with ADFG linear approximation mode.") }),
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
    final AnalysisResultFPGA res = checkAndAnalyzeAlgorithm(algorithm, scenario, fifoEvaluatorName);

    // optionally pack tokens in BRAM
    final String packTokensStr = parameters.get(PACK_TOKENS_PARAM_NAME);
    final boolean packTokens = Boolean.parseBoolean(packTokensStr);
    if (packTokens) {
      List<PackedFifoConfig> workList = TokenPackingAnalysis.analysis(res, scenario);
      if (!workList.isEmpty()) {
        TokenPackingTransformation.transform(res, scenario, workList);

        // Adding the latency due to packing to the II of corresponding attached actor into the scenario
        for (PackedFifoConfig packedFifoConfig : workList) {
          final long additionalLatency = packedFifoConfig.updatedWidth / packedFifoConfig.originalWidth + 1;
          final long newExecutionTime = Long
              .parseLong(scenario.getTimings().getExecutionTimeOrDefault(packedFifoConfig.attachedActor, fpga))
              + additionalLatency;
          scenario.getTimings().setExecutionTime(packedFifoConfig.attachedActor, fpga, newExecutionTime);
        }
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
    final Map<AbstractVertex, Long> brv = PiBRV.compute(flatGraph, BRVMethod.LCM);
    // check interfaces
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

  private static Map<InterfaceActor, Pair<Long, Long>> checkInterfaces(final PiGraph flatGraph,
      final Map<AbstractVertex, Long> brv) {
    final Map<InterfaceActor, Pair<Long, Long>> result = new LinkedHashMap<>();
    flatGraph.getActors().stream().filter(x -> (x instanceof InterfaceActor)).forEach(x -> {
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
