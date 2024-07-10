/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Daniel Madroñal [daniel.madronal@upm.es] (2018 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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
package org.preesm.codegen.xtend.task;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * The Class CodegenTask.
 *
 * @deprecated see parameter "Papify" in task {@link CodegenTask}
 */
@PreesmTask(id = "org.ietr.preesm.codegen.xtend.task.CodegenPapifyEngineTask", name = "Papify Engine",

    inputs = { @Port(name = "scenario", type = Scenario.class),
        @Port(name = "DAG", type = DirectedAcyclicGraph.class) },
    outputs = { @Port(name = "DAG", type = DirectedAcyclicGraph.class) },

    shortDescription = "Deprecated - does nothing (as of v3.9.1). See parameter 'Papify' in CodegenTask'.\n\nOld doc: "
        + "Generate the required instrumentation code for the application" + " based on the PAPIFY tab information.",

    description = "Deprecated - does nothing (as of v3.9.1). See parameter 'Papify' in CodegenTask'.\n\n"
        + "Old doc: This workflow task is responsible for generating the instrumentation "
        + "of the code for the application based on the PAPIFY tab information.\n\n"
        + "The generated code makes use of 1 macro that enables/disables the monitoring"
        + " in the **preesm.h** user header file:\n"
        + "*  **_PREESM_PAPIFY_MONITOR** : if defined, the code monitoring will take place;\n"

)
@Deprecated
public class CodegenPapifyEngineTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    final MapperDAG algo = (MapperDAG) inputs.get(AbstractWorkflowNodeImplementation.KEY_SDF_DAG);

    PreesmLogger.getLogger().log(Level.WARNING,
        "PapifyEngine task is now deprecated (since v3.9.1) and will be removed eventually. \n"
            + "It does nothing and can be safely removed.\n"
            + "See the new 'Papify' parameter of the codegen tasks for equivalent behavior.");

    final LinkedHashMap<String, Object> output = new LinkedHashMap<>();
    output.put(AbstractWorkflowNodeImplementation.KEY_SDF_DAG, algo);
    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return Collections.emptyMap();
  }

  @Override
  public String monitorMessage() {
    return "Papify Engine - Deprecated (does nothing)";
  }

}
