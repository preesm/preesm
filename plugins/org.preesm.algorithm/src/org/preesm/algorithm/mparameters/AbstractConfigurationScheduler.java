/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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

  protected final boolean    shouldEstimateMemory;
  protected long             lastEndTime;
  protected Pair<Long, Long> lastMaxLoads;

  /**
   * Default constructor, without memory estimation.
   */
  protected AbstractConfigurationScheduler() {
    this(false);
  }

  /**
   * Constructor offering memory estimation choice.
   *
   * @param shouldEstimateMemory
   *          Whether or not the memory will be estimated. If not supported by the scheduler, memory will be set to 0 in
   *          any case.
   */
  protected AbstractConfigurationScheduler(boolean shouldEstimateMemory) {
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
  public long getLastEndTime() {
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
