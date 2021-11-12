package org.preesm.algorithm.mparameters;

import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * Abstract class for schedulers that want to be supported by the parameter DSE task.
 * 
 * @author ahonorat
 */
public abstract class AbstractConfigurationScheduler {

  protected boolean          shouldEstimateMemory;
  protected long             lastEndTime;
  protected Pair<Long, Long> lastMaxLoads;

  /**
   * Default constructor, without memory estimation.
   */
  public AbstractConfigurationScheduler() {
    this(false);
  }

  /**
   * Constructor offering memory estimation choice.
   * 
   * @param shouldEstimateMemory
   *          Whether or not the memory will be estimated. If not supported by the scheduler, memory will be set to 0 in
   *          any case.
   */
  public AbstractConfigurationScheduler(boolean shouldEstimateMemory) {
    this.shouldEstimateMemory = shouldEstimateMemory;
    lastEndTime = 0;
    lastMaxLoads = null;
  }

  /**
   * Tells if the underlying scheduler can also estimate the memory size.
   * 
   * @return Wheter or not the scheduler supports memory estimation.
   */
  public abstract boolean supportsMemoryEstimation();

  /**
   * Tells if the underlying scheduler can also be rerun with extra delays/cuts.
   * 
   * @return Wheter or not the scheduler supports added delays/cuts.
   */
  public abstract boolean supportsExtraDelayCuts();

  /**
   * Run a given DSE configuration (graph with resolved parameters).
   * 
   * @param scenario
   *          Scenario to consider.
   * @param graph
   *          Graph to scheduler.
   * @param architecture
   *          Architecture to consider.
   * @return An incomplete DSEpoint, where parameters and nbCuts are not set.
   */
  public abstract DSEpointIR runConfiguration(final Scenario scenario, final PiGraph graph, final Design architecture);

  /**
   * Returns the last schedule end time, or the graph period if periodic.
   * 
   * @return The end time of the last schedule, or 0 if not yet set.
   */
  public long getLastMakespan() {
    return lastEndTime;
  }

  /**
   * If the scheduler supports extra cuts, then it should provide the maximal firing load and the total load found
   * during previous run. Should return {@code null} if not supported ({@link #supportsExtraDelayCuts}) or not yet
   * computed.
   * 
   * @return If not {@code null}, maximal single firing load as key and total load as value.
   */
  public Pair<Long, Long> getLastMaxLoads() {
    return lastMaxLoads;
  }

}
