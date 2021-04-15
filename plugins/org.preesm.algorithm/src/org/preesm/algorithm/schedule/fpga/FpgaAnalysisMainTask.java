package org.preesm.algorithm.schedule.fpga;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.pisdf.autodelays.HeuristicLoopBreakingDelays;
import org.preesm.algorithm.pisdf.autodelays.TopologicalRanking;
import org.preesm.algorithm.pisdf.autodelays.TopologicalRanking.TopoVisit;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.statictools.PiMMHelper;
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
        @Port(name = "scenario", type = Scenario.class) })
public class FpgaAnalysisMainTask extends AbstractTaskImplementation {

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
    checkInterfaces(flatGraph, brv);

    // Get all sub graph (connected components) composing the current graph
    final List<List<AbstractActor>> subgraphsWOInterfaces = PiMMHelper.getAllConnectedComponentsWOInterfaces(flatGraph);
    // check and set the II for each subgraph
    for (List<AbstractActor> cc : subgraphsWOInterfaces) {
      AsapFpgaIIevaluator.checkAndSetActorInfos(cc, scenario, brv);
    }

    // check the cycles
    final HeuristicLoopBreakingDelays hlbd = new HeuristicLoopBreakingDelays();
    hlbd.performAnalysis(flatGraph, brv);
    // TODO set min durations of all AsapFpgaIIevaluator.ActorScheduleInfos, with cycle latency if in a cycle

    final Map<AbstractActor, TopoVisit> topoRanks = TopologicalRanking.topologicalASAPranking(hlbd);
    // build intermediate list of actors per rank to perform scheduling analysis
    final SortedMap<Integer, Set<AbstractActor>> irRankActors = TopologicalRanking.mapRankActors(topoRanks, false, 0);

    return new HashMap<>();
  }

  private static void checkInterfaces(final PiGraph flatGraph, final Map<AbstractVertex, Long> brv) {
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
      if (aaRate != iaRate) {
        PreesmLogger.getLogger().warning(
            "Interface rate of " + ia.getName() + " does not match with the rate of the actor connected to it.");
      }
    });
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
