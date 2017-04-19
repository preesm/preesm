/*******************************************************************************
 * Copyright or © or Copr. 2014 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.memory.bounds;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;
import org.jgrapht.graph.DefaultEdge;

// TODO: Auto-generated Javadoc
/**
 * The Class MemoryBoundsEstimatorEngine.
 */
public class MemoryBoundsEstimatorEngine {

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

  /** The Constant VALUE_VERBOSE_TRUE. */
  public static final String VALUE_VERBOSE_TRUE = "True";

  /** The logger. */
  // Rem: Logger is used to display messages in the console
  private final Logger logger = WorkflowLogger.getLogger();

  /** The mem ex. */
  private final MemoryExclusionGraph memEx;

  /** The verbose. */
  private final boolean verbose;

  /** The solver. */
  private AbstractMaximumWeightCliqueSolver<MemoryExclusionVertex, DefaultEdge> solver;

  /**
   * Instantiates a new memory bounds estimator engine.
   *
   * @param memEx
   *          the mem ex
   * @param valueVerbose
   *          the value verbose
   */
  public MemoryBoundsEstimatorEngine(final MemoryExclusionGraph memEx, final String valueVerbose) {
    this.memEx = memEx;
    this.verbose = valueVerbose.contains(MemoryBoundsEstimatorEngine.VALUE_VERBOSE_TRUE);
  }

  /**
   * Select solver.
   *
   * @param valueSolver
   *          the value solver
   */
  public void selectSolver(final String valueSolver) {
    if (this.verbose) {
      if (valueSolver.equals(MemoryBoundsEstimatorEngine.VALUE_SOLVER_DEFAULT)) {
        this.logger.log(Level.INFO, "No solver specified. Heuristic solver used by default.");
      } else {
        if (valueSolver.equals(MemoryBoundsEstimatorEngine.VALUE_SOLVER_HEURISTIC) || valueSolver.equals(MemoryBoundsEstimatorEngine.VALUE_SOLVER_OSTERGARD)
            || valueSolver.equals(MemoryBoundsEstimatorEngine.VALUE_SOLVER_YAMAGUCHI)) {
          this.logger.log(Level.INFO, valueSolver + " solver used.");
        } else {
          this.logger.log(Level.INFO, "Incorrect solver :" + valueSolver + ". Heuristic solver used by default.");
        }
      }
    }
    switch (valueSolver) {
      case VALUE_SOLVER_HEURISTIC:
        this.solver = new HeuristicSolver<>(this.memEx);
        break;
      case VALUE_SOLVER_OSTERGARD:
        this.solver = new OstergardSolver<>(this.memEx);
        break;
      case VALUE_SOLVER_YAMAGUCHI:
        this.solver = new YamaguchiSolver<>(this.memEx);
        break;
      default:
        this.solver = new HeuristicSolver<>(this.memEx);
    }
  }

  /**
   * Solve.
   */
  public void solve() {
    if (this.verbose) {
      this.logger.log(Level.INFO, "Maximum-Weight Clique Problem : start solving");
    }

    this.solver.solve();
  }

  /**
   * Gets the min bound.
   *
   * @return the min bound
   */
  public int getMinBound() {
    return this.solver.sumWeight(this.solver.getHeaviestClique());
  }

  /**
   * Gets the max bound.
   *
   * @return the max bound
   */
  public int getMaxBound() {
    return this.solver.sumWeight(this.memEx.vertexSet());
  }
}
