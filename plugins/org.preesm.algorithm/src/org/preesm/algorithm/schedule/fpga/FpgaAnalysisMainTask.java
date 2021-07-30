package org.preesm.algorithm.schedule.fpga;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.mapper.ui.stats.EditorRunnable;
import org.preesm.algorithm.mapper.ui.stats.IStatGenerator;
import org.preesm.algorithm.mapper.ui.stats.StatEditorInput;
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
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.statictools.PiSDFFlattener;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.utils.SlamDesignPEtypeChecker;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * This task proposes to analyse throughput bottleneck of a PiGraph executed on FPGA, as well as to estimate its
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
    parameters = { @Parameter(name = FpgaAnalysisMainTask.SHOW_SCHED_PARAM_NAME,
        description = "Whether or not the schedule must be shown at the end.", values = {
            @Value(name = FpgaAnalysisMainTask.SHOW_SCHED_PARAM_VALUE, effect = "False disables this feature.") }), })
public class FpgaAnalysisMainTask extends AbstractTaskImplementation {

  public static final String SHOW_SCHED_PARAM_NAME  = "Show schedule ?";
  public static final String SHOW_SCHED_PARAM_VALUE = "false";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final PiGraph algorithm = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);

    if (!SlamDesignPEtypeChecker.isSingleFPGA(architecture)) {
      throw new PreesmRuntimeException("This task must be called with a single FPGA architecture, abandon.");
    }
    if (algorithm.getAllDelays().stream().anyMatch(x -> (x.getLevel() != PersistenceLevel.PERMANENT))) {
      throw new PreesmRuntimeException("This task must be called on PiGraph with only permanent delays.");
    }

    // Flatten the graph
    final PiGraph flatGraph = PiSDFFlattener.flatten(algorithm, true);
    final Map<AbstractVertex, Long> brv = PiBRV.compute(flatGraph, BRVMethod.LCM);
    // check interfaces
    final Map<InterfaceActor, Pair<Long, Long>> interfaceRates = checkInterfaces(flatGraph, brv);
    if (interfaceRates.values().stream().anyMatch(x -> (x == null))) {
      throw new PreesmRuntimeException("Some interfaces have weird rates (see log), abandon.");
    }
    // schedule the graph
    final Pair<IStatGenerator, Map<Fifo, Long>> eval = AsapFpgaIIevaluator.performAnalysis(flatGraph, scenario, brv);
    final IStatGenerator schedStats = eval.getKey();

    final String showSchedStr = parameters.get(SHOW_SCHED_PARAM_NAME);
    final boolean showSched = Boolean.parseBoolean(showSchedStr);

    // Optionally shows the Gantt diagram
    if (showSched) {
      final IEditorInput input = new StatEditorInput(schedStats);

      // Check if the workflow is running in command line mode
      try {
        // Run statistic editor
        PlatformUI.getWorkbench().getDisplay().asyncExec(new EditorRunnable(input));
      } catch (final IllegalStateException e) {
        PreesmLogger.getLogger().info("Gantt display is impossible in this context."
            + " Ignore this log entry if you are running the command line version of Preesm.");
      }

      FpgaCodeGenerator.generateFiles(scenario, flatGraph, interfaceRates, eval.getValue());

    }

    return new HashMap<>();
  }

  private static Map<InterfaceActor, Pair<Long, Long>> checkInterfaces(final PiGraph flatGraph,
      final Map<AbstractVertex, Long> brv) {
    final Map<InterfaceActor, Pair<Long, Long>> result = new LinkedHashMap<>();
    flatGraph.getActors().stream().filter(x -> (x instanceof InterfaceActor)).forEach(x -> {
      final InterfaceActor ia = (InterfaceActor) x;
      final DataPort iaPort = ia.getDataPort();
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
    return new HashMap<>();
  }

  @Override
  public String monitorMessage() {
    return "FPGA throughput and buffer sizes analysis (without output)";
  }

}
