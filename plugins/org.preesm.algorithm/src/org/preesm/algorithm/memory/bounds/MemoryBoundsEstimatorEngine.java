/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023 - 2024)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2015)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2015)
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
package org.preesm.algorithm.memory.bounds;

import java.util.logging.Logger;
import org.jgrapht.graph.DefaultEdge;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionVertex;
import org.preesm.commons.logger.PreesmLogger;

/**
 * The Class MemoryBoundsEstimatorEngine.
 */
public class MemoryBoundsEstimatorEngine {

  private static final String VALUE_SOLVER_HEURISTIC = "Heuristic";
  private static final String VALUE_SOLVER_OSTERGARD = "Ostergard";
  private static final String VALUE_SOLVER_YAMAGUCHI = "Yamaguchi";
  public static final String  VALUE_SOLVER_DEFAULT   = "? C {" + VALUE_SOLVER_HEURISTIC + ", " + VALUE_SOLVER_OSTERGARD
      + ", " + VALUE_SOLVER_YAMAGUCHI + "}";
  private static final String VALUE_VERBOSE_TRUE     = "True";

  private final Logger               logger = PreesmLogger.getLogger();
  private final MemoryExclusionGraph memEx;
  private final boolean              verbose;

  private AbstractMaximumWeightCliqueSolver<MemoryExclusionVertex, DefaultEdge> solver;

  /**
   */
  public MemoryBoundsEstimatorEngine(final MemoryExclusionGraph memEx, final String valueVerbose) {
    this.memEx = memEx;
    this.verbose = valueVerbose.contains(MemoryBoundsEstimatorEngine.VALUE_VERBOSE_TRUE);
  }

  /**
   */
  public void selectSolver(final String valueSolver) {
    if (this.verbose) {
      switch (valueSolver) {
        case VALUE_SOLVER_DEFAULT -> this.logger.info("No solver specified. Heuristic solver used by default.");
        case VALUE_SOLVER_HEURISTIC, VALUE_SOLVER_OSTERGARD, VALUE_SOLVER_YAMAGUCHI ->
          this.logger.info(() -> valueSolver + " solver used.");
        default -> this.logger.info(() -> "Incorrect solver :" + valueSolver + ". Heuristic solver used by default.");
      }
    }

    this.solver = switch (valueSolver) {
      case VALUE_SOLVER_HEURISTIC -> new HeuristicSolver<>(this.memEx);
      case VALUE_SOLVER_OSTERGARD -> new OstergardSolver<>(this.memEx);
      case VALUE_SOLVER_YAMAGUCHI -> new YamaguchiSolver<>(this.memEx);
      default -> new HeuristicSolver<>(this.memEx);
    };
  }

  /**
   */
  public void solve() {
    if (this.verbose) {
      this.logger.info("Maximum-Weight Clique Problem : start solving");
    }

    this.solver.solve();
  }

  public long getMinBound() {
    return this.solver.sumWeight(this.solver.getHeaviestClique());
  }

  public long getMaxBound() {
    return this.solver.sumWeight(this.memEx.vertexSet());
  }

  public double getDensity() {
    return memEx.edgeSet().size() / ((memEx.vertexSet().size() * (memEx.vertexSet().size() - 1)) / 2.0);
  }

}
