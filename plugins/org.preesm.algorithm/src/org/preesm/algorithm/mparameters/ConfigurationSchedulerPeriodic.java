package org.preesm.algorithm.mparameters;

import java.util.logging.Level;
import java.util.stream.Collectors;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memalloc.model.PhysicalBuffer;
import org.preesm.algorithm.pisdf.autodelays.IterationDelayedEvaluator;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.evaluation.energy.SimpleEnergyCost;
import org.preesm.algorithm.synthesis.evaluation.energy.SimpleEnergyEvaluation;
import org.preesm.algorithm.synthesis.evaluation.latency.LatencyCost;
import org.preesm.algorithm.synthesis.evaluation.latency.SimpleLatencyEvaluation;
import org.preesm.algorithm.synthesis.memalloc.IMemoryAllocation;
import org.preesm.algorithm.synthesis.memalloc.LegacyMemoryAllocation;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.algorithm.synthesis.schedule.algos.PeriodicScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.PreesmSchedulingException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.statictools.PiSDFToSingleRate;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * Class to run the periodic scheduler on a DSE configuration.
 * 
 * @author ahonorat
 */
public class ConfigurationSchedulerPeriodic extends AbstractConfigurationScheduler {

  protected final PeriodicScheduler scheduler;

  /**
   * Default constructor, without memory estimation.
   */
  public ConfigurationSchedulerPeriodic() {
    this(false);
  }

  /**
   * Constructor offering memory estimation choice.
   * 
   * @param shouldEstimateMemory
   *          Whether or not the memory will be estimated. If not supported by the scheduler, memory will be set to 0 in
   *          any case.
   */
  public ConfigurationSchedulerPeriodic(final boolean shouldEstimateMemory) {
    super(shouldEstimateMemory);
    scheduler = new PeriodicScheduler();
  }

  @Override
  public boolean supportsMemoryEstimation() {
    return true;
  }

  @Override
  public boolean supportsExtraDelayCuts() {
    return true;
  }

  @Override
  public DSEpointIR runConfiguration(Scenario scenario, PiGraph graph, Design architecture) {
    final Level backupLevel = PreesmLogger.getLogger().getLevel();
    PreesmLogger.getLogger().setLevel(Level.SEVERE);

    final int iterationDelay = IterationDelayedEvaluator.computeLatency(graph);

    final PiGraph dag = PiSDFToSingleRate.compute(graph, BRVMethod.LCM);
    // for (Parameter p : dag.getAllParameters()) {
    // PreesmLogger.getLogger().fine(p.getName() + " (in DAG): " + p.getExpression().getExpressionAsString());
    // }

    SynthesisResult scheduleAndMap = null;
    try {
      scheduleAndMap = scheduler.scheduleAndMap(dag, architecture, scenario);
    } catch (PreesmSchedulingException e) {
      // put back all messages
      PreesmLogger.getLogger().setLevel(backupLevel);
      PreesmLogger.getLogger().log(Level.WARNING, "Scheduling was impossible.", e);
      return new DSEpointIR(Long.MAX_VALUE, iterationDelay, Long.MAX_VALUE, Long.MAX_VALUE, 0, 0, null, false);
    }

    long period = scheduler.getGraphPeriod();
    // original graph period has not been resolved, so we use the flat graph copy instead
    lastEndTime = period > 0 ? period : scheduler.getLastEndTime();
    final long maxSingleLoad = scheduler.getMaximalFiringLoad();
    final long totalLoad = scheduler.getTotalLoad();
    lastMaxLoads = new Pair<>(maxSingleLoad, totalLoad);

    // use implementation evaluation of PeriodicScheduler instead?
    final ScheduleOrderManager scheduleOM = new ScheduleOrderManager(dag, scheduleAndMap.schedule);
    final LatencyCost evaluateLatency = new SimpleLatencyEvaluation().evaluate(dag, architecture, scenario,
        scheduleAndMap.mapping, scheduleOM);
    final long durationII = evaluateLatency.getValue();
    final SimpleEnergyCost evaluateEnergy = new SimpleEnergyEvaluation().evaluate(dag, architecture, scenario,
        scheduleAndMap.mapping, scheduleOM);
    final long energy = evaluateEnergy.getValue();

    long memory = 0L;
    if (shouldEstimateMemory) {
      // computation of the memory footprint
      final IMemoryAllocation legacyAlloc = new LegacyMemoryAllocation();
      final Allocation alloc = legacyAlloc.allocateMemory(dag, architecture, scenario, scheduleAndMap.schedule,
          scheduleAndMap.mapping);
      memory = alloc.getPhysicalBuffers().stream().collect(Collectors.summingLong(PhysicalBuffer::getSizeInBit));
    }
    // put back all messages
    PreesmLogger.getLogger().setLevel(backupLevel);

    return new DSEpointIR(energy, iterationDelay, durationII, memory, 0, 0, null, true);
  }

}
