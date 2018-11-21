/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2013)
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
package org.preesm.algorithm.memory.exclusiongraph;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.algorithm.model.parameters.InvalidExpressionException;
import org.preesm.algorithm.transforms.ForkJoinRemover;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.scenario.PreesmScenario;
import org.preesm.scenario.types.DataType;
import org.preesm.workflow.WorkflowException;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

// TODO: Auto-generated Javadoc
/**
 * Workflow element that takes a DAG as input and Create its Memory Exclusion Graph.
 *
 * @author kdesnos
 *
 */
public class MemoryExclusionGraphBuilder extends AbstractTaskImplementation {

  /** The Constant PARAM_VERBOSE. */
  public static final String PARAM_VERBOSE = "Verbose";

  /** The Constant VALUE_TRUE_FALSE_DEFAULT. */
  public static final String VALUE_TRUE_FALSE_DEFAULT = "? C {True, False}";

  /** The Constant VALUE_TRUE. */
  public static final String VALUE_TRUE = "True";

  /** The Constant VALUE_FALSE. */
  public static final String VALUE_FALSE = "False";

  /** The Constant PARAM_SUPPR_FORK_JOIN. */
  public static final String PARAM_SUPPR_FORK_JOIN = "Suppr Fork/Join";

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map,
   * org.eclipse.core.runtime.IProgressMonitor, java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) throws WorkflowException {

    // Rem: Logger is used to display messages in the console
    final Logger logger = PreesmLogger.getLogger();

    // Check Workflow element parameters
    final String valueVerbose = parameters.get(MemoryExclusionGraphBuilder.PARAM_VERBOSE);
    boolean verbose;
    verbose = valueVerbose.equals(MemoryExclusionGraphBuilder.VALUE_TRUE);

    final String valueSupprForkJoin = parameters.get(MemoryExclusionGraphBuilder.PARAM_SUPPR_FORK_JOIN);
    boolean supprForkJoin;
    supprForkJoin = valueSupprForkJoin.equals(MemoryExclusionGraphBuilder.VALUE_TRUE);

    // Retrieve list of types and associated sizes in the scenario
    final PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");
    final Map<String, DataType> dataTypes = scenario.getSimulationManager().getDataTypes();
    MemoryExclusionVertex.setDataTypes(dataTypes);

    // Make a copy of the Input DAG for treatment
    // The DAG is altered when building the exclusion graph.
    final DirectedAcyclicGraph dag = (DirectedAcyclicGraph) inputs.get("DAG");
    // Clone is deep copy i.e. vertices are thus copied too.
    DirectedAcyclicGraph localDAG = dag.copy();
    if (localDAG == null) {
      localDAG = dag;
    }

    // Remove Fork/Join vertices
    if (supprForkJoin) {
      ForkJoinRemover.supprImplodeExplode(localDAG);
    }

    // Build the exclusion graph
    if (verbose) {
      logger.log(Level.INFO, "Memory exclusion graph : start building");
    }
    final MemoryExclusionGraph memEx = new MemoryExclusionGraph();
    try {
      memEx.buildGraph(localDAG);
    } catch (final InvalidExpressionException e) {
      throw new WorkflowException(e.getLocalizedMessage());
    }
    final double density = memEx.edgeSet().size() / ((memEx.vertexSet().size() * (memEx.vertexSet().size() - 1)) / 2.0);
    if (verbose) {
      logger.log(Level.INFO,
          "Memory exclusion graph built with " + memEx.vertexSet().size() + " vertices and density = " + density);
    }

    // Generate output
    final Map<String, Object> output = new LinkedHashMap<>();
    output.put(AbstractWorkflowNodeImplementation.KEY_MEM_EX, memEx);
    return output;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(MemoryExclusionGraphBuilder.PARAM_VERBOSE, MemoryExclusionGraphBuilder.VALUE_TRUE_FALSE_DEFAULT);
    parameters.put(MemoryExclusionGraphBuilder.PARAM_SUPPR_FORK_JOIN,
        MemoryExclusionGraphBuilder.VALUE_TRUE_FALSE_DEFAULT);
    return parameters;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Building MemEx Graph";
  }

}
