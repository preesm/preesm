/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
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
package org.preesm.model.pisdf.check;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * Head class to launch check on a PiGraph.
 *
 * @author cguy
 */
@PreesmTask(id = "pisdf-checker", name = "PiSDF Checker",

    inputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class) },
    outputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class) })
public class PiGraphConsistenceCheckerTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    // Get the PiGraph to check
    final PiGraph graph = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    PreesmLogger.getLogger().info("Checking validity of graph " + graph.getName() + ".");
    // Check the graph and display corresponding messages
    final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.NONE,
        CheckerErrorLevel.FATAL_CODEGEN);
    final boolean valid = pgcc.check(graph);
    if (valid) {
      PreesmLogger.getLogger().info("Graph " + graph.getName() + " is valid.");
    }

    // Return the checked graph
    final Map<String, Object> outputs = new LinkedHashMap<>();
    outputs.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, graph);
    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return Collections.emptyMap();
  }

  @Override
  public String monitorMessage() {
    return "Starting checking of PiGraph";
  }

}
