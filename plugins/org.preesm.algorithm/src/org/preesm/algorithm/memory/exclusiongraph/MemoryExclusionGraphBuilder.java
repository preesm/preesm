/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Jonathan Piat [jpiat@laas.fr] (2011)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2013)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2012)
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
import org.eclipse.emf.common.util.EMap;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * Workflow element that takes a DAG as input and Create its Memory Exclusion Graph.
 *
 * @author kdesnos
 *
 */
@PreesmTask(id = "org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraphBuilder", name = "MEG Builder",
    category = "Memory Optimization",

    inputs = { @Port(name = "DAG", type = DirectedAcyclicGraph.class),
        @Port(name = "scenario", type = Scenario.class) },
    outputs = { @Port(name = "MemEx", type = MemoryExclusionGraph.class) },

    shortDescription = "Builds the Memory Exclusion Graph (MEG) modeling the memory allocation constraints.",

    description = "The memory allocation technique used in Preesm is based on a Memory Exclusion Graph (MEG). "
        + "A MEG is a graph whose vertices model the memory objects that must be allocated in memory in order "
        + "to run the generated code. In the current version of Preesm, each of these memory objects "
        + "corresponds either to an edge of the Directed Acyclic Graph (DAG) or to a buffer corresponding "
        + "to ”delays” of the graph that store data between executions of a schedule. In the MEG, two memory "
        + "objects are linked by an edge (called an exclusion) if they can not be allocated in overlapping "
        + "memory spaces.",

    parameters = { @Parameter(name = "Verbose",
        description = "How verbose will this task be during its execution. In verbose mode, the task will"
            + " log the start and completion time of the build, as well as characteristics (number of memory"
            + " objects, density of exclusions) of the produced MEG.",
        values = { @Value(name = "false", effect = "(Default) The task will not log information."),
            @Value(name = "true", effect = "The task will log build and MEG information.") }) },

    seeAlso = { "**MEG**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Memory bounds for the distributed "
        + "execution of a hierarchical synchronous data-flow graph. In Embedded Computer Systems: "
        + "Architectures, Modeling, and Simulation (SAMOS XII), 2012 International Conference on, 2012." })
public class MemoryExclusionGraphBuilder extends AbstractTaskImplementation {

  public static final String PARAM_VERBOSE            = "Verbose";
  public static final String VALUE_TRUE_FALSE_DEFAULT = "? C {True, False}";
  public static final String VALUE_TRUE               = "True";
  public static final String VALUE_FALSE              = "False";

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    // Rem: Logger is used to display messages in the console
    final Logger logger = PreesmLogger.getLogger();

    // Check Workflow element parameters
    final boolean verbose = MemoryExclusionGraphBuilder.VALUE_TRUE
        .equalsIgnoreCase(parameters.get(MemoryExclusionGraphBuilder.PARAM_VERBOSE));

    // Retrieve list of types and associated sizes in the scenario
    final Scenario scenario = (Scenario) inputs.get("scenario");
    final EMap<String, Long> dataTypes = scenario.getSimulationInfo().getDataTypes();
    MemoryExclusionVertex.setDataTypes(dataTypes);

    // Make a copy of the Input DAG for treatment
    // The DAG is altered when building the exclusion graph.
    final DirectedAcyclicGraph dag = (DirectedAcyclicGraph) inputs.get("DAG");
    // Clone is deep copy i.e. vertices are thus copied too.
    DirectedAcyclicGraph localDAG = dag.copy();
    if (localDAG == null) {
      localDAG = dag;
    }

    // Build the exclusion graph
    if (verbose) {
      logger.log(Level.INFO, "Memory exclusion graph : start building");
    }
    final MemoryExclusionGraph memEx = new MemoryExclusionGraph();
    memEx.buildGraph(localDAG);
    final double density = memEx.edgeSet().size() / ((memEx.vertexSet().size() * (memEx.vertexSet().size() - 1)) / 2.0);
    if (verbose) {
      logger.log(Level.INFO,
          () -> "Memory exclusion graph built with " + memEx.vertexSet().size() + " vertices and density = " + density);
    }

    // Generate output
    final Map<String, Object> output = new LinkedHashMap<>();
    output.put(AbstractWorkflowNodeImplementation.KEY_MEM_EX, memEx);
    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(MemoryExclusionGraphBuilder.PARAM_VERBOSE, MemoryExclusionGraphBuilder.VALUE_TRUE_FALSE_DEFAULT);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Building MemEx Graph";
  }

}
