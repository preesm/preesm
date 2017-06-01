/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2016)
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
package org.ietr.preesm.memory.bounds;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;
import org.jgrapht.graph.DefaultEdge;

// TODO: Auto-generated Javadoc
/**
 * Workflow element that takes several memexes as input and computes their memory bounds.
 *
 * @author kdesnos
 *
 */
public class SerialMemoryBoundsEstimator extends AbstractTaskImplementation {

  /** The Constant PARAM_SOLVER. */
  public static final String PARAM_SOLVER = "Solver";

  /** The Constant VALUE_SOLVER_DEFAULT. */
  public static final String VALUE_SOLVER_DEFAULT = "? C {Heuristic, Ostergard, Yamaguchi}";

  /** The Constant VALUE_SOLVER_OSTERGARD. */
  public static final String VALUE_SOLVER_OSTERGARD = "Ostergard";

  /** The Constant VALUE_SOLVER_YAMAGUCHI. */
  public static final String VALUE_SOLVER_YAMAGUCHI = "Yamaguchi";

  /** The Constant VALUE_SOLVER_HEURISTIC. */
  public static final String VALUE_SOLVER_HEURISTIC = "Heuristic";

  /** The Constant PARAM_VERBOSE. */
  public static final String PARAM_VERBOSE = "Verbose";

  /** The Constant VALUE_VERBOSE_DEFAULT. */
  public static final String VALUE_VERBOSE_DEFAULT = "? C {True, False}";

  /** The Constant VALUE_VERBOSE_TRUE. */
  public static final String VALUE_VERBOSE_TRUE = "True";

  /** The Constant VALUE_VERBOSE_FALSE. */
  public static final String VALUE_VERBOSE_FALSE = "False";

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
    final String valueVerbose = parameters.get(SerialMemoryBoundsEstimator.PARAM_VERBOSE);
    boolean verbose;
    verbose = valueVerbose.equals(SerialMemoryBoundsEstimator.VALUE_VERBOSE_TRUE);

    final String valueSolver = parameters.get(SerialMemoryBoundsEstimator.PARAM_SOLVER);
    if (verbose) {
      if (valueSolver.equals(SerialMemoryBoundsEstimator.VALUE_SOLVER_DEFAULT)) {
        logger.log(Level.INFO, "No solver specified. Heuristic solver used by default.");
      } else {
        if (valueSolver.equals(SerialMemoryBoundsEstimator.VALUE_SOLVER_HEURISTIC) || valueSolver.equals(SerialMemoryBoundsEstimator.VALUE_SOLVER_OSTERGARD)
            || valueSolver.equals(SerialMemoryBoundsEstimator.VALUE_SOLVER_YAMAGUCHI)) {
          logger.log(Level.INFO, valueSolver + " solver used.");
        } else {
          logger.log(Level.INFO, "Incorrect solver :" + valueSolver + ". Heuristic solver used by default.");
        }
      }
    }

    // MemoryExclusionGraph memEx = (MemoryExclusionGraph)
    // inputs.get("MemEx");
    @SuppressWarnings("unchecked")
    final Map<String, MemoryExclusionGraph> memExes = (Map<String, MemoryExclusionGraph>) inputs.get("MEGs");

    for (final String memory : memExes.keySet()) {
      final MemoryExclusionGraph memEx = memExes.get(memory);
      final int nbVertices = memEx.vertexSet().size();
      final double density = memEx.edgeSet().size() / ((memEx.vertexSet().size() * (memEx.vertexSet().size() - 1)) / 2.0);
      // Derive bounds
      AbstractMaximumWeightCliqueSolver<MemoryExclusionVertex, DefaultEdge> solver = null;
      if (valueSolver.equals(SerialMemoryBoundsEstimator.VALUE_SOLVER_HEURISTIC)) {
        solver = new HeuristicSolver<>(memEx);
      }
      if (valueSolver.equals(SerialMemoryBoundsEstimator.VALUE_SOLVER_OSTERGARD)) {
        solver = new OstergardSolver<>(memEx);
      }
      if (valueSolver.equals(SerialMemoryBoundsEstimator.VALUE_SOLVER_YAMAGUCHI)) {
        solver = new YamaguchiSolver<>(memEx);
      }
      if (solver == null) {
        solver = new HeuristicSolver<>(memEx);
      }

      solver.solve();
      final int minBound = solver.sumWeight(solver.getHeaviestClique());
      final int maxBound = solver.sumWeight(memEx.vertexSet());

      logger.log(Level.INFO,
          "Memory(" + memory + ") Vertices = " + nbVertices + " Bound_Max = " + maxBound + " Bound_Min = " + minBound + " Density = " + density);
    }

    // Generate output
    final Map<String, Object> output = new HashMap<>();
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
    parameters.put(SerialMemoryBoundsEstimator.PARAM_SOLVER, SerialMemoryBoundsEstimator.VALUE_SOLVER_DEFAULT);
    parameters.put(SerialMemoryBoundsEstimator.PARAM_VERBOSE, SerialMemoryBoundsEstimator.VALUE_VERBOSE_DEFAULT);
    return parameters;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Estimating Memory Bounds for all Memex in input map";
  }

}
