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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.schedule.model.ScapeSchedule;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * This class computes the schedule of the cluster using the authentic APGAN method, which relies on Repetition Count
 * computation, specifically the Greatest Common Divisor (GCD) of the repetition vectors (RVs) of a pair of connected
 * actors. The fundamental principle of APGAN involves iteratively clustering pairs of actors until a single entity is
 * obtained. Initiating the process by clustering pairs with the maximum repetition count has been demonstrated to
 * result in a schedule with minimal memory requirements. The resulting schedule consists of nested looped schedules,
 * designed to make the behavior of the cluster sequential.
 *
 * @see "https://apps.dtic.mil/sti/pdfs/ADA455067.pdf"
 * @author orenaud
 *
 */
@PreesmTask(id = "scape.scedule.task.identifier", name = "SCAPE schedule Task",
    inputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class) },
    outputs = { @Port(name = "schedule", type = List.class) })

public class ScheduleScapeTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final List<ScapeSchedule> schedule = new ScheduleScape(scenario.getAlgorithm()).execute();
    // Build output map
    final Map<String, Object> output = new HashMap<>();
    // return scenario updated
    output.put("schedule", schedule);
    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return Collections.emptyMap();
  }

  @Override
  public String monitorMessage() {
    return "Starting Execution of Scheduling SCAPE Task";
  }

}
