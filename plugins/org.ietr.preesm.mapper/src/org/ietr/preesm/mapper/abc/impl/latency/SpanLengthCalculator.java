/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2009 - 2012)
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
package org.ietr.preesm.mapper.abc.impl.latency;

import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.abc.taskscheduling.TaskSchedType;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.params.AbcParameters;

// TODO: Auto-generated Javadoc
/**
 * Using infinite homogeneous simulation to calculate the span length of a dag.
 *
 * @author mpelcat
 */
public class SpanLengthCalculator extends InfiniteHomogeneousAbc {

  /** The Constant DAG_SPAN. */
  public static final String DAG_SPAN = "dag span length";

  /**
   * Instantiates a new span length calculator.
   *
   * @param params
   *          the params
   * @param dag
   *          the dag
   * @param archi
   *          the archi
   * @param taskSchedType
   *          the task sched type
   * @param scenario
   *          the scenario
   * @throws WorkflowException
   *           the workflow exception
   */
  public SpanLengthCalculator(final AbcParameters params, final MapperDAG dag, final Design archi, final TaskSchedType taskSchedType,
      final PreesmScenario scenario) throws WorkflowException {
    super(params, dag, archi, taskSchedType, scenario);

    updateTimings();

    // The span corresponds to the final latency of an infinite homogeneous
    // simulation
    dag.getPropertyBean().setValue(SpanLengthCalculator.DAG_SPAN, getFinalLatency());
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.impl.latency.InfiniteHomogeneousAbc#setEdgeCost(org.ietr.preesm. mapper.model.MapperDAGEdge)
   */
  @Override
  protected void setEdgeCost(final MapperDAGEdge edge) {
    edge.getTiming().setCost(1);
  }
}
