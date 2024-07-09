/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2021) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Hamza Deroui [hamza.deroui@insa-rennes.fr] (2018)
 * Mickaël Dardaillon [mickael.dardaillon@insa-rennes.fr] (2020)
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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.deadlock.IBSDFConsistency;
import org.preesm.algorithm.deadlock.IBSDFLiveness;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.schedule.sdf.ASAPSchedulerDAG;
import org.preesm.algorithm.throughput.sdf.tools.GraphStructureHelper;
import org.preesm.algorithm.throughput.sdf.tools.IBSDFTransformer;
import org.preesm.algorithm.throughput.sdf.tools.SrSDFTransformer;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * @author hderoui
 *
 */
@PreesmTask(id = "org.ietr.preesm.latency.LatencyEvaluationPlugin", name = "Latency Evaluation",

    inputs = { @Port(name = "SDF", type = SDFGraph.class), @Port(name = "scenario", type = Scenario.class) },

    outputs = { @Port(name = "SDF", type = SDFGraph.class), @Port(name = "scenario", type = Scenario.class),
        @Port(name = "latency", type = Double.class) },

    parameters = {

        @Parameter(name = "multicore", values = { @Value(name = "true/false", effect = "") }),

        @Parameter(name = "method",
            values = { @Value(name = "FAST", effect = "(default) Hierarchical method"),
                @Value(name = "FLAT_LP", effect = "Based on Flattening the hierarchy"),
                @Value(name = "FLAT_SE", effect = "Based on Flattening the hierarchy")

            })

    })
public class LatencyEvaluationTask extends AbstractTaskImplementation {

  private static final String DURATION_LITTERAL = "duration";
  private static final String METHOD_LITTERAL   = "method";

  /**
   * @author hderoui
   *
   *         The supported methods
   */

  private enum LatencyMethod {
    FAST, // default: Hierarchical method
    FLAT_LP, // Based on Flattening the hierarchy
    FLAT_SE, // Based on Flattening the hierarchy
  }

  // Plug-in parameters
  public static final String PARAM_METHOD               = METHOD_LITTERAL;
  public static final String PARAM_METHOD_DEFAULT_VALUE = "FAST";

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    // get the input graph, the scenario for actors duration, and the total number of cores
    final SDFGraph inputGraph = GraphStructureHelper.cloneIBSDF((SDFGraph) inputs.get("SDF"));
    final Scenario inputScenario = (Scenario) inputs.get("scenario");
    final boolean multicore = Boolean.parseBoolean(parameters.get("multicore"));
    final LatencyMethod inputMethod = LatencyMethod.valueOf(parameters.get(METHOD_LITTERAL));

    // init & test
    final boolean deadlockFree = init(inputGraph, inputScenario);
    double latency = 0;

    // Compute the latency of the graph if it is deadlock free
    if (deadlockFree) {
      // multi-core execution
      if (multicore) {

        switch (inputMethod) {
          case FLAT_LP:
            // Based on flattening the hierarchy into a Flat srSDF graph

            // convert the IBSDF graph to a flat srSDF graph then to a dag
            final SDFGraph dagLp = SrSDFTransformer.convertToDAG(IBSDFTransformer.convertToSrSDF(inputGraph, false));

            // compute the value of the longest path in the dag
            latency = GraphStructureHelper.getLongestPath(dagLp, null, null);

            break;

          case FLAT_SE:
            // Based on flattening the hierarchy into a Flat srSDF graph

            // convert the IBSDF graph to a flat srSDF graph then to a dag
            final SDFGraph dagSimu = SrSDFTransformer.convertToDAG(IBSDFTransformer.convertToSrSDF(inputGraph, false));

            // Simulate an ASAP schedule
            final ASAPSchedulerDAG schedule = new ASAPSchedulerDAG();
            latency = schedule.schedule(dagSimu);

            break;

          case FAST:
            // Based on a hierarchical evaluation of the latency (evaluate-replace)
            final LatencyEvaluationEngine evaluator = new LatencyEvaluationEngine();
            latency = evaluator.getMinLatencyMultiCore(inputGraph, null, false);
            break;

          default:
            PreesmLogger.getLogger().log(Level.WARNING,
                "The suported methods are: \"flat\" = classical method, \"fast\" = hierarchical method !");
            break;
        }

        // print a message with the latency value
        final String message = "The minimum Latency value of a multicore execution = " + latency + " Cycles";
        PreesmLogger.getLogger().log(Level.INFO, message);

      } else {
        // single core execution
        final LatencyEvaluationEngine evaluator = new LatencyEvaluationEngine();
        latency = evaluator.getMinLatencySingleCore(inputGraph, inputScenario);

        // print a message with the latency value
        final String message = "The minimum Latency value of a singlecore execution = " + latency + " Cycles";
        PreesmLogger.getLogger().log(Level.INFO, message);
      }

    } else {
      // print an error message
      PreesmLogger.getLogger().log(Level.WARNING, "ERROR : The graph is deadlock !!");
    }

    // set the outputs
    final Map<String, Object> outputs = new LinkedHashMap<>();
    outputs.put("SDF", inputGraph);
    outputs.put("scenario", inputScenario);
    outputs.put("latency", latency);

    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    return Collections.emptyMap();
  }

  @Override
  public String monitorMessage() {
    return "Evaluating graph latency ...";
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
  private boolean init(final SDFGraph inputGraph, final Scenario scenario) {
    // check the consistency by computing the RV of the graph
    boolean deadlockFree = IBSDFConsistency.computeRV(inputGraph);

    // check the liveness of the graph if consistent
    if (deadlockFree) {

      // Copy actors duration from the scenario to actors properties
      for (final SDFAbstractVertex actor : inputGraph.getAllVertices()) {
        if ("vertex".equals(actor.getKind())) {
          if (actor.getGraphDescription() == null) {
            // if atomic actor then copy the duration indicated in the scenario
            final double duration = scenario.getTimings().evaluateExecutionTimeOrDefault(
                (AbstractActor) actor.getReferencePiVertex(),
                scenario.getSimulationInfo().getMainOperator().getComponent());
            actor.setPropertyValue(DURATION_LITTERAL, duration);
          } else {
            // if hierarchical actor then as default the duration is 1
            // the real duration of the hierarchical actor will be defined later by scheduling its subgraph
            actor.setPropertyValue(DURATION_LITTERAL, 1.);
            scenario.getTimings().setExecutionTime((AbstractActor) actor.getReferencePiVertex(),
                scenario.getSimulationInfo().getMainOperator().getComponent(), 1); // to remove
          }
        } else {
          // keep the duration of input interfaces
          final double duration = scenario.getTimings().evaluateExecutionTimeOrDefault(
              (AbstractActor) actor.getReferencePiVertex(),
              scenario.getSimulationInfo().getMainOperator().getComponent());
          actor.setPropertyValue(DURATION_LITTERAL, duration);

        }
      }

      // check the liveness of the graph
      IBSDFLiveness.evaluate(inputGraph);

    }

    return deadlockFree;
  }

}
