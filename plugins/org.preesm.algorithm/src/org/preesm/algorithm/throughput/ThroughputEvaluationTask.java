/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2017 - 2018)
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
/*********************************************************
Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
Karol Desnos, Hamza Deroui

[mpelcat,jnezan,kdesnos, hderoui]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use,
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info".

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability.

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or
data to be ensured and,  more generally, to use and operate it in the
same conditions as regards security.

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.preesm.algorithm.throughput;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.deadlock.IBSDFConsistency;
import org.preesm.algorithm.deadlock.IBSDFLiveness;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.throughput.tools.GraphStructureHelper;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.scenario.PreesmScenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * @author hderoui
 *
 *         Throughput plug-in for the evaluation of IBSDF and SDF graphs throughput
 */
public class ThroughputEvaluationTask extends AbstractTaskImplementation {

  /**
   * @author hderoui
   *
   *         The supported methods
   */
  private enum ThroughputMethod {
    SR, // Schedule-Replace technique
    ESR, // Evaluate-Schedule-Replace method
    HPeriodic, // Hierarchical Periodic Schedule method
    Classical, // Based on Flattening the hierarchy
  }

  // Plug-in parameters
  public static final String PARAM_METHOD               = "method";
  public static final String PARAM_METHOD_DEFAULT_VALUE = "SR";

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) throws PreesmException {

    // get the input graph, the scenario for actors duration, and the method to use
    final SDFGraph inputGraph = GraphStructureHelper.cloneIBSDF((SDFGraph) inputs.get("SDF"));
    final PreesmScenario inputScenario = (PreesmScenario) inputs.get("scenario");
    final ThroughputMethod inputMethod = ThroughputMethod.valueOf(parameters.get("method"));

    // init & test
    final boolean deadlockFree = init(inputGraph, inputScenario);
    double throughput = 0;

    if (deadlockFree) {
      // Compute the throughput of the graph
      switch (inputMethod) {
        case SR:
          // Schedule-Replace technique
          final ScheduleReplace sr = new ScheduleReplace();
          throughput = sr.evaluate(inputGraph);
          break;

        case ESR:
          // Evaluate-Schedule-Replace method
          final EvaluateScheduleReplace esr = new EvaluateScheduleReplace();
          throughput = esr.evaluate(inputGraph);
          break;

        case HPeriodic:
          // Hierarchical Periodic Schedule method
          final HPeriodicSchedule HPeriodic = new HPeriodicSchedule();
          throughput = HPeriodic.evaluate(inputGraph);
          break;

        case Classical:
          // Based on flattening the hierarchy into a Flat srSDF graph
          final ClassicalMethod classicalMethod = new ClassicalMethod();
          throughput = classicalMethod.evaluate(inputGraph, false);
          break;

        default:
          PreesmLogger.getLogger().log(Level.WARNING, "Method not yet suported !");
          break;
      }

      // print the computed throughput
      PreesmLogger.getLogger().log(Level.INFO, "Throughput value = " + throughput + " nbIter/clockCycle");

    } else {
      // print an error message
      PreesmLogger.getLogger().log(Level.WARNING,
          "ERROR : The graph is deadlock !! Throughput value = 0 nbIter/clockCycle");
    }

    // set the outputs
    final Map<String, Object> outputs = new HashMap<>();
    outputs.put("SDF", inputGraph);
    outputs.put("scenario", inputScenario);
    outputs.put("throughput", throughput);

    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new HashMap<>();
    parameters.put(ThroughputEvaluationTask.PARAM_METHOD, ThroughputEvaluationTask.PARAM_METHOD_DEFAULT_VALUE);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Evaluating the graph throughput";
  }

  /**
   * Check the deadlock freeness of the graph and initialize it before computing the throughput
   *
   * @param inputGraph
   *          SDF/IBSDF graph
   * @param scenario
   *          contains actors duration
   *
   * @return true if deadlock free, false if not
   */
  private boolean init(final SDFGraph inputGraph, final PreesmScenario scenario) {
    // test the inputs
    // TestPlugin.start(null, null);

    // check the consistency by computing the RV of the graph
    boolean deadlockFree = IBSDFConsistency.computeRV(inputGraph);

    // check the liveness of the graph if consistent
    if (deadlockFree) {

      // Copy actors duration from the scenario to actors properties
      for (final SDFAbstractVertex actor : inputGraph.getAllVertices()) {
        if (actor.getKind() == "vertex") {
          if (actor.getGraphDescription() == null) {
            // if atomic actor then copy the duration indicated in the scenario
            final double duration = scenario.getTimingManager().getTimingOrDefault(actor.getId(), "x86").getTime();
            actor.setPropertyValue("duration", duration);
          } else {
            // if hierarchical actor then as default the duration is 1
            // the real duration of the hierarchical actor will be defined later by scheduling its subgraph
            actor.setPropertyValue("duration", 1.);
            // scenario.getTimingManager().setTiming(actor.getId(), "x86", 1); // to remove
          }
        } else {
          // the duration of interfaces in neglected by setting their duration to 0
          actor.setPropertyValue("duration", 0.);
          // scenario.getTimingManager().setTiming(actor.getId(), "x86", 0); // to remove
        }
      }

      // check the liveness of the graph
      deadlockFree = IBSDFLiveness.evaluate(inputGraph);

    }

    return deadlockFree;
  }

}
