/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2025) :
 *
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2025)
 * Ophelie-Renaud [ophelie.renaud@insa-rennes.fr] (2025)
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

package org.preesm.algorithm.clustering.scape;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * This class partition subdivide data-parallelism task in order to prepare the scaling of the SCAPE method. For more
 * details, see conference paper: "SimSDP: Dataflow Application Distribution on Heterogeneous Multi-Node Multi-Core
 * Architectures, published at xx 2024
 *
 * @author orenaud
 *
 */
@PreesmTask(id = "euclide.transfo.task.identifier", name = "Euclide Task",
    inputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class) },
    outputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class) })

public class EuclideTransfoTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final PiGraph transfo = new EuclideTransfo(scenario).execute();

    for (final Entry<ComponentInstance, EList<AbstractActor>> gp : scenario.getConstraints().getGroupConstraints()) {
      final int size = gp.getValue().size();
      for (int i = 1; !gp.getValue().isEmpty(); i++) {
        final int k = size - i;
        gp.getValue().remove(k);
      }

      transfo.getAllActors().forEach(actor -> gp.getValue().add(actor));
    }

    final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ALL,
        CheckerErrorLevel.FATAL_ALL);
    pgcc.check(transfo);
    // Build output map
    final Map<String, Object> output = new HashMap<>();
    // return topGraph
    output.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, transfo);
    // return scenario updated
    output.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);

    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return new LinkedHashMap<>();
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of Euclide Task";
  }

}
