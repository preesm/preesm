package org.preesm.algorithm.mapper.ui.stats;

import org.preesm.algorithm.mapper.gantt.GanttData;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 * Generates stats for the mapper UI (interface)
 * 
 * @author ahonorat
 */
public interface IStatGenerator {

  /**
   * The span is the shortest possible execution time. It is theoretic because no communication time is taken into
   * account. We consider that we have an infinity of cores of main type totally connected with perfect media. The span
   * complex because the DAG is not serial-parallel but can be any DAG. It is retrieved from the DAG if it was set from
   * the infinite homogeneous simulation. If there was no such simulation, the span length can not be recalculated
   * because the original DAG without transfers is no more available.
   *
   * @return the DAG span length
   */
  public abstract long getDAGSpanLength();

  /**
   * The work is the sum of all task lengths excluding vertices added by the mapping.
   *
   * @return the DAG work length
   * @throws PreesmException
   *           the workflow exception
   */
  public abstract long getDAGWorkLength();

  /**
   * Returns the maximum end time.
   *
   * @return the final time
   */
  public abstract long getFinalTime();

  /**
   * The load is the percentage of a processing resource used for the given algorithm.
   *
   * @param operator
   *          the operator
   * @return the load
   */
  public abstract long getLoad(final ComponentInstance operator);

  /**
   * The memory is the sum of all buffers allocated by the mapping.
   *
   * @param operator
   *          the operator
   * @return the mem
   */
  public abstract long getMem(final ComponentInstance operator);

  /**
   * Returns the number of operators with main type.
   *
   * @return the nb main type operators
   */
  public abstract int getNbMainTypeOperators();

  /**
   * Returns the number of operators in the current architecture that execute vertices.
   *
   * @return the nb used operators
   */
  public abstract int getNbUsedOperators();

  /**
   * Data used to plot the Gantt of the schedule.
   * 
   * @return GanttData of the schedule.
   */
  public abstract GanttData getGanttData();

  public abstract Design getDesign();

  public abstract Scenario getScenario();

}
