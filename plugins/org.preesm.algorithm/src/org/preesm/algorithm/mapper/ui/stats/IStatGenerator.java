/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
