/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2023) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2016)
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
package org.preesm.algorithm.memory.allocation.tasks;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.memory.bounds.MemoryBoundsEstimatorEngine;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * Workflow element that takes several memexes as input and computes their memory bounds.
 *
 * @author kdesnos
 *
 */
@PreesmTask(id = "org.ietr.preesm.memory.bounds.SerialMemoryBoundsEstimator", name = "Serial Memory Bounds",
    category = "Memory Optimization",

    inputs = { @Port(name = "MEGs", type = Map.class) },

    shortDescription = "Compute bounds of the amount of memory needed to allocate the MEGs.",

    description = "This task computes the memory bounds (see Memory Bound Estimator Task) for several MEGs, "
        + "like the one produced by the Memory Allocation task.",

    parameters = {
        @Parameter(name = "Verbose",
            description = "How verbose will this task be during its execution. In verbose mode, the task "
                + "will log the name of the used solver, the start and completion time of the bound "
                + "estimation algorithm. Computed bounds are always logged, even if the verbose parameter "
                + "is set to false.",
            values = { @Value(name = "false", effect = "(Default) The task will not log information."),
                @Value(name = "true", effect = "The task will log build and MEG information.") }),
        @Parameter(name = "Solver", description = "Specify which algorithm is used to compute the lower bound.",
            values = {
                @Value(name = "Heuristic",
                    effect = "(Default) Heuristic algorithm described in [1] is used. This technique find an"
                        + " approximate solution."),
                @Value(name = "Ostergard",
                    effect = "Östergård’s algorithm [2] is used. This technique finds an optimal solution, "
                        + "but has a potentially exponential complexity."),
                @Value(name = "Yamaguchi",
                    effect = "Yamaguchi et al.’s algorithm [3] is used. This technique finds an optimal"
                        + " solution, but has a potentially exponential complexity.") }) },

    seeAlso = {
        "**[1]**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Memory bounds for the distributed "
            + "execution of a hierarchical synchronous data-flow graph. In Embedded Computer Systems: "
            + "Architectures, Modeling, and Simulation (SAMOS XII), 2012 International Conference on, 2012.",
        "**[2]**: Patric R. J. Östergård. A new algorithm for the maximum-weight clique problem. Nordic"
            + " J. of Computing, 8(4):424–436, December 2001.",
        "**[3]**: K. Yamaguchi and S. Masuda. A new exact algorithm for the maximum weight clique problem."
            + " In 23rd International Conference on Circuit/Systems, Computers and Communications (ITC-CSCC’08),"
            + " 2008.",
        "**Memory Bounds**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Pre-and post-scheduling memory"
            + " allocation strategies on MPSoCs. In Electronic System Level Synthesis Conference (ESLsyn), 2013." })
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

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    // Rem: Logger is used to display messages in the console
    final Logger logger = PreesmLogger.getLogger();

    // Check Workflow element parameters
    final String valueVerbose = parameters.get(SerialMemoryBoundsEstimator.PARAM_VERBOSE);
    final boolean verbose = valueVerbose.equals(SerialMemoryBoundsEstimator.VALUE_VERBOSE_TRUE);

    final String valueSolver = parameters.get(SerialMemoryBoundsEstimator.PARAM_SOLVER);
    if (verbose) {
      switch (valueSolver) {
        case SerialMemoryBoundsEstimator.VALUE_SOLVER_DEFAULT ->
          logger.log(Level.INFO, "No solver specified. Heuristic solver used by default.");
        case SerialMemoryBoundsEstimator.VALUE_SOLVER_HEURISTIC, SerialMemoryBoundsEstimator.VALUE_SOLVER_OSTERGARD,
            SerialMemoryBoundsEstimator.VALUE_SOLVER_YAMAGUCHI ->
          logger.log(Level.INFO, () -> valueSolver + " solver used.");
        default ->
          logger.log(Level.INFO, () -> "Incorrect solver :" + valueSolver + ". Heuristic solver used by default.");
      }
    }

    @SuppressWarnings("unchecked")
    final Map<String, MemoryExclusionGraph> memExes = (Map<String, MemoryExclusionGraph>) inputs.get("MEGs");

    for (final Entry<String, MemoryExclusionGraph> entry : memExes.entrySet()) {
      final String memory = entry.getKey();
      final MemoryExclusionGraph memEx = entry.getValue();

      final MemoryBoundsEstimatorEngine engine = new MemoryBoundsEstimatorEngine(memEx, valueVerbose);
      engine.selectSolver(valueSolver);
      engine.solve();

      final int nbVertices = memEx.vertexSet().size();
      final double density = engine.getDensity();

      final long minBound = engine.getMinBound();

      final long maxBound = engine.getMaxBound();

      logger.log(Level.INFO, () -> "Memory(" + memory + ") Vertices = " + nbVertices + " Bound_Max = " + maxBound
          + " Bound_Min = " + minBound + " Density = " + density);
    }

    return Collections.emptyMap();
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(SerialMemoryBoundsEstimator.PARAM_SOLVER, SerialMemoryBoundsEstimator.VALUE_SOLVER_DEFAULT);
    parameters.put(SerialMemoryBoundsEstimator.PARAM_VERBOSE, SerialMemoryBoundsEstimator.VALUE_VERBOSE_DEFAULT);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Estimating Memory Bounds for all Memex in input map";
  }

}
