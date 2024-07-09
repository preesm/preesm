/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2015)
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
package org.preesm.algorithm.memory.allocation.tasks;

import java.util.LinkedHashMap;
import java.util.Map;
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
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * Workflow element that takes a MemoryExclusionGraph as input and computes its memory bounds. It outputs the unmodified
 * MemEx as well as the input and output bounds found.
 *
 * @author kdesnos
 *
 */
@PreesmTask(id = "org.ietr.preesm.memory.bounds.MemoryBoundsEstimator", name = "Memory Bounds Estimator",
    category = "Memory Optimization",

    inputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_MEM_EX, type = MemoryExclusionGraph.class) },

    outputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_MEM_EX, type = MemoryExclusionGraph.class),
        @Port(name = AbstractWorkflowNodeImplementation.KEY_BOUND_MIN, type = Long.class),
        @Port(name = AbstractWorkflowNodeImplementation.KEY_BOUND_MAX, type = Long.class) },

    shortDescription = "Compute bounds of the amount of memory needed to allocate the MEG",

    description = "The analysis technique presented in [1] can be used in Preesm to derive "
        + "bounds for the amount of memory that can be allocated for an application. The "
        + "upper bound corresponds to the worst memory allocation possible for an application."
        + " The lower bound is a theoretical value that limits the minimum amount of memory"
        + " that can be allocated. By definition, the lower bound is not always reachable, which "
        + "means that it might be impossible to find an allocation with this optimal amount of "
        + "memory. The minimum bound is found by solving the Maximum Weight Clique problem on the "
        + "MEG. This task provides a convenient way to evaluate the quality of a memory allocation.",

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
public class MemoryBoundsEstimator extends AbstractTaskImplementation {

  static final String PARAM_SOLVER          = "Solver";
  static final String PARAM_VERBOSE         = "Verbose";
  static final String VALUE_VERBOSE_DEFAULT = "? C {True, False}";

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    // Check Workflow element parameters
    final String valueVerbose = parameters.get(MemoryBoundsEstimator.PARAM_VERBOSE);
    final String valueSolver = parameters.get(MemoryBoundsEstimator.PARAM_SOLVER);

    final MemoryExclusionGraph memEx = (MemoryExclusionGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_MEM_EX);

    final MemoryBoundsEstimatorEngine engine = new MemoryBoundsEstimatorEngine(memEx, valueVerbose);
    engine.selectSolver(valueSolver);
    engine.solve();

    final long minBound = engine.getMinBound();

    final long maxBound = engine.getMaxBound();

    PreesmLogger.getLogger().info(() -> "Bound_Max = " + maxBound + " bits, Bound_Min = " + minBound + " bits.");

    // Generate output
    final Map<String, Object> output = new LinkedHashMap<>();
    output.put(AbstractWorkflowNodeImplementation.KEY_BOUND_MAX, maxBound);
    output.put(AbstractWorkflowNodeImplementation.KEY_BOUND_MIN, minBound);
    output.put(AbstractWorkflowNodeImplementation.KEY_MEM_EX, memEx);
    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(MemoryBoundsEstimator.PARAM_SOLVER, MemoryBoundsEstimatorEngine.VALUE_SOLVER_DEFAULT);
    parameters.put(MemoryBoundsEstimator.PARAM_VERBOSE, MemoryBoundsEstimator.VALUE_VERBOSE_DEFAULT);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Estimating Memory Bounds";
  }

}
