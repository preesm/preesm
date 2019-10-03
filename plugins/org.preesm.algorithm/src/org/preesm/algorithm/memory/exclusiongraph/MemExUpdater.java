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
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * Workflow element that takes a Scheduled DAG and a MemEx as inputs and update the MemEx according to the DAG Schedule.
 *
 * @author kdesnos
 */
@PreesmTask(id = "org.ietr.preesm.memory.exclusiongraph.MemExUpdater", name = "MEG Updater",
    category = "Memory Optimization",

    inputs = { @Port(name = "DAG", type = DirectedAcyclicGraph.class),
        @Port(name = "MemEx", type = MemoryExclusionGraph.class) },
    outputs = { @Port(name = "MemEx", type = MemoryExclusionGraph.class) },

    shortDescription = "Relax memory allocation constraints of the MEG using scheduling information.",

    description = "The MEG used in Preesm can be updated with scheduling information to remove exclusions between "
        + "memory objects and make better allocations possible.",

    parameters = { @Parameter(name = "Verbose",
        description = "How verbose will this task be during its execution. In verbose mode, the task will log "
            + "the start and completion time of the update, as well as characteristics (number of memory objects,"
            + " density of exclusions) of the MEGs both before and after the update.",
        values = { @Value(name = "false", effect = "(Default) The task will not log information."),
            @Value(name = "true", effect = "The task will log build and MEG information.") }) },

    seeAlso = { "**MEG update**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Pre-and post-scheduling memory"
        + " allocation strategies on MPSoCs. In Electronic System Level Synthesis Conference (ESLsyn), 2013." })
public class MemExUpdater extends AbstractTaskImplementation {

  public static final String PARAM_VERBOSE            = "Verbose";
  public static final String VALUE_TRUE_FALSE_DEFAULT = "? C {True, False}";

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    // Check Workflow element parameters
    final boolean verbose = "true".equalsIgnoreCase(parameters.get(MemExUpdater.PARAM_VERBOSE));

    // Retrieve inputs
    final DirectedAcyclicGraph dag = (DirectedAcyclicGraph) inputs.get("DAG");
    final MemoryExclusionGraph memEx = (MemoryExclusionGraph) inputs.get("MemEx");

    final MemExUpdaterEngine engine = new MemExUpdaterEngine(dag, memEx, verbose);
    engine.createLocalDag();
    engine.update();

    // Generate output
    final Map<String, Object> output = new LinkedHashMap<>();
    output.put(AbstractWorkflowNodeImplementation.KEY_MEM_EX, memEx);
    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(MemExUpdater.PARAM_VERBOSE, MemExUpdater.VALUE_TRUE_FALSE_DEFAULT);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Updating MemEx";
  }

}
