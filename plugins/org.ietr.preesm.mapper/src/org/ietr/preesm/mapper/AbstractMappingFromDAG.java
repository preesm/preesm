/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Julien Hascoet <jhascoet@kalray.eu> (2017)
 * Jonathan Piat <jpiat@laas.fr> (2009)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2017)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
package org.ietr.preesm.mapper;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.abc.impl.latency.SpanLengthCalculator;
import org.ietr.preesm.mapper.abc.route.calcul.RouteCalculator;
import org.ietr.preesm.mapper.checker.CommunicationOrderChecker;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.optimizer.RedundantSynchronizationCleaner;
import org.ietr.preesm.mapper.params.AbcParameters;

/**
 * Generic class representing the scheduling algorithm behaviour.
 *
 * @author pmenuet
 * @author mpelcat
 * @author kdesnos
 */
public abstract class AbstractMappingFromDAG extends AbstractTaskImplementation {

  /** The Constant PARAM_CHECK. */
  public static final String PARAM_CHECK = "Check";

  /** The Constant PARAM_OPTIMIZE. */
  public static final String PARAM_OPTIMIZE = "Optimize synchronization";

  /** The Constant VALUE_CHECK_FALSE. */
  public static final String VALUE_FALSE = "False";

  /** The Constant VALUE_CHECK_TRUE. */
  public static final String VALUE_TRUE = "True";

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map, org.eclipse.core.runtime.IProgressMonitor,
   * java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters, final IProgressMonitor monitor,
      final String nodeName, final Workflow workflow) {

    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final MapperDAG dag = (MapperDAG) inputs.get(AbstractWorkflowNodeImplementation.KEY_SDF_DAG);
    final PreesmScenario scenario = (PreesmScenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);

    // Asking to recalculate routes
    RouteCalculator.recalculate(architecture, scenario);

    final Map<String, Object> outputs = new LinkedHashMap<>();
    schedule(architecture, scenario, dag, parameters, outputs);
    return outputs;
  }

  protected abstract void schedule(final Design architecture, final PreesmScenario scenario, final MapperDAG dag, final Map<String, String> parameters,
      final Map<String, Object> outputs);

  /**
   * Generic mapping message.
   *
   * @return the string
   */
  @Override
  public String monitorMessage() {
    return "Mapping/Scheduling";
  }

  /**
   * Returns parameters common to all mappers.
   *
   * @return the default parameters
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();

    parameters.put("simulatorType", "LooselyTimed");
    parameters.put("edgeSchedType", "Simple");
    parameters.put("balanceLoads", "false");
    parameters.put(AbstractMappingFromDAG.PARAM_CHECK, AbstractMappingFromDAG.VALUE_CHECK_TRUE);
    parameters.put(AbstractMapping.PARAM_OPTIMIZE, AbstractMapping.VALUE_FALSE);
    return parameters;
  }

  /**
   * Clean.
   *
   * @param architecture
   *          the architecture
   * @param scenario
   *          the scenario
   */
  protected void clean(final Design architecture, final PreesmScenario scenario) {
    // Asking to delete route
    RouteCalculator.deleteRoutes(architecture, scenario);
  }

  /**
   * This method performs optional checks to verify the integrity of a schedule. It should be call at the end of all scheduler implementations.
   *
   * @param parameters
   *          {@link Map} of parameters values that were given to the mapper workflow task.
   * @param dag
   *          Scheduled {@link DirectedAcyclicGraph}.
   */
  protected void checkSchedulingResult(final Map<String, String> parameters, final DirectedAcyclicGraph dag) {
    if (parameters.get(AbstractMappingFromDAG.PARAM_CHECK).equals(AbstractMappingFromDAG.VALUE_CHECK_TRUE)) {
      CommunicationOrderChecker.checkCommunicationOrder(dag);
    }
  }

  /**
   * This method performs optional optimization of the communications generated in the schedule.
   * 
   * @param parameters
   *          {@link Map} of parameters values that were given to the mapper workflow task.
   * @param dag
   *          Scheduled {@link DirectedAcyclicGraph}.
   */
  protected void removeRedundantSynchronization(final Map<String, String> parameters, final DirectedAcyclicGraph dag) {
    if (parameters.get(AbstractMapping.PARAM_OPTIMIZE).equals(AbstractMapping.VALUE_TRUE)) {
      RedundantSynchronizationCleaner.cleanRedundantSynchronization(dag);
    }
  }

  /**
   * Calculates the DAG span length on the architecture main operator (the tasks that cannot be executed by the main operator are deported without transfer time
   * to other operator).
   *
   * @param dag
   *          the dag
   * @param archi
   *          the archi
   * @param scenario
   *          the scenario
   * @param parameters
   *          the parameters
   * @throws WorkflowException
   *           the workflow exception
   */
  protected void calculateSpan(final MapperDAG dag, final Design archi, final PreesmScenario scenario, final AbcParameters parameters)
      throws WorkflowException {

    final SpanLengthCalculator spanCalc = new SpanLengthCalculator(parameters, dag, archi, parameters.getSimulatorType().getTaskSchedType(), scenario);
    spanCalc.resetDAG();

  }
}
