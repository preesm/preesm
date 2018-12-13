/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2018)
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
package org.preesm.algorithm.latency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.throughput.tools.helpers.GraphStructureHelper;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.scenario.PreesmScenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * @author hderoui
 *
 */
public class LatencyExplorationTask extends AbstractTaskImplementation {

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) throws PreesmException {

    // get the input graph, the scenario for actors duration, and the total number of cores
    final SDFGraph inputGraph = GraphStructureHelper.cloneIBSDF((SDFGraph) inputs.get("SDF"));
    final PreesmScenario inputScenario = (PreesmScenario) inputs.get("scenario");
    final Integer nbCores = Integer.parseInt(parameters.get("nbCores"));

    // list of latency in function of cores number
    final ArrayList<Double> latencyList = new ArrayList<>(nbCores);

    // explore the latency
    // no available cores => latency = 0
    latencyList.add(0, 0.);

    // latency of a single core execution
    final LatencyEvaluationEngine evaluator = new LatencyEvaluationEngine();
    final double maxLatency = evaluator.getMinLatencySingleCore(inputGraph, inputScenario);
    latencyList.add(1, maxLatency);

    // latency of a multicore execution
    for (int n = 2; n <= nbCores; n++) {
      final double l = 1;
      // compute the latency of the graph using n cores
      // call the class for scheduling the graph using n cores
      // parameters(n, null) if null do not construct/return the gantt chart
      latencyList.add(n, l);
    }

    // WorkflowLogger.getLogger().log(Level.INFO, "Throughput value");
    // WorkflowLogger.getLogger().log(Level.WARNING, "ERROR : The graph is deadlock !!");

    // set the outputs
    final Map<String, Object> outputs = new HashMap<>();
    // outputs.put("SDF", inputGraph);
    // outputs.put("scenario", inputScenario);

    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new HashMap<>();
    // parameters.put(,);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Exploring graph latency ...";
  }

}
