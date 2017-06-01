/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
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
package org.ietr.preesm.memory.multiSDFTasks;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.algorithm.transforms.ForkJoinRemover;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.types.DataType;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;

// TODO: Auto-generated Javadoc
/**
 * The Class MultiMemoryExclusionGraphBuilder.
 */
public class MultiMemoryExclusionGraphBuilder extends AbstractTaskImplementation {

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

  /** The Constant OUTPUT_KEY_MEM_EX. */
  public static final String OUTPUT_KEY_MEM_EX = "MemEx";

  /** The Constant OUTPUT_KEY_DAG. */
  public static final String OUTPUT_KEY_DAG = "DAG";

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map, org.eclipse.core.runtime.IProgressMonitor,
   * java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters, final IProgressMonitor monitor,
      final String nodeName, final Workflow workflow) throws WorkflowException {

    // Rem: Logger is used to display messages in the console
    final Logger logger = WorkflowLogger.getLogger();

    // Check Workflow element parameters
    final String valueVerbose = parameters.get(MultiMemoryExclusionGraphBuilder.PARAM_VERBOSE);
    boolean verbose;
    verbose = valueVerbose.equals(MultiMemoryExclusionGraphBuilder.VALUE_TRUE);

    final String valueSupprForkJoin = parameters.get(MultiMemoryExclusionGraphBuilder.PARAM_SUPPR_FORK_JOIN);
    boolean supprForkJoin;
    supprForkJoin = valueSupprForkJoin.equals(MultiMemoryExclusionGraphBuilder.VALUE_TRUE);

    // Retrieve list of types and associated sizes in the scenario
    final PreesmScenario scenario = (PreesmScenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final Map<String, DataType> dataTypes = scenario.getSimulationManager().getDataTypes();
    MemoryExclusionVertex.setDataTypes(dataTypes);

    // Make a copy of the Input DAG for treatment
    // The DAG is altered when building the exclusion graph.
    @SuppressWarnings("unchecked")
    final Set<DirectedAcyclicGraph> dags = (Set<DirectedAcyclicGraph>) inputs.get("DAGs");

    final Map<DirectedAcyclicGraph, MemoryExclusionGraph> dagsAndMemExs = new HashMap<>();

    for (final DirectedAcyclicGraph dag : dags) {
      // Clone is deep copy i.e. vertices are thus copied too.
      DirectedAcyclicGraph localDAG = (DirectedAcyclicGraph) dag.clone();
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
        logger.log(Level.INFO, "Memory exclusion graph built with " + memEx.vertexSet().size() + " vertices and density = " + density);
      }

      dagsAndMemExs.put(dag, memEx);
    }

    // Generate output
    final Map<String, Object> output = new HashMap<>();
    output.put(AbstractWorkflowNodeImplementation.KEY_DAG_AND_MEM_EX_MAP, dagsAndMemExs);
    return output;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new HashMap<>();
    parameters.put(MultiMemoryExclusionGraphBuilder.PARAM_VERBOSE, MultiMemoryExclusionGraphBuilder.VALUE_TRUE_FALSE_DEFAULT);
    parameters.put(MultiMemoryExclusionGraphBuilder.PARAM_SUPPR_FORK_JOIN, MultiMemoryExclusionGraphBuilder.VALUE_TRUE_FALSE_DEFAULT);
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
