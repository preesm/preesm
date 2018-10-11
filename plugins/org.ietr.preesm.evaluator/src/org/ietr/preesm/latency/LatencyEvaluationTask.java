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
package org.ietr.preesm.latency;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.deadlock.IBSDFConsistency;
import org.ietr.preesm.deadlock.IBSDFLiveness;
import org.ietr.preesm.schedule.ASAPSchedulerDAG;
import org.ietr.preesm.throughput.tools.helpers.GraphStructureHelper;
import org.ietr.preesm.throughput.tools.helpers.Stopwatch;
import org.ietr.preesm.throughput.tools.transformers.IBSDFTransformer;
import org.ietr.preesm.throughput.tools.transformers.SrSDFTransformer;

/**
 * @author hderoui
 *
 */
public class LatencyEvaluationTask extends AbstractTaskImplementation {

  /**
   * @author hderoui
   *
   *         The supported methods
   */
  public static enum LatencyMethod {
  FAST, // Hierarchical method
  FLAT_LP, // Based on Flattening the hierarchy
  FLAT_SE, // Based on Flattening the hierarchy
  }

  // Plug-in parameters
  public static final String PARAM_METHOD               = "method";
  public static final String PARAM_METHOD_DEFAULT_VALUE = "FAST";
  public Stopwatch           timer;

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) throws WorkflowException {

    // get the input graph, the scenario for actors duration, and the total number of cores
    final SDFGraph inputGraph = GraphStructureHelper.cloneIBSDF((SDFGraph) inputs.get("SDF"));
    final PreesmScenario inputScenario = (PreesmScenario) inputs.get("scenario");
    final boolean multicore = Boolean.valueOf(parameters.get("multicore"));
    final LatencyMethod inputMethod = LatencyMethod.valueOf(parameters.get("method"));

    // init & test
    final boolean deadlockFree = init(inputGraph, inputScenario);
    double latency = 0;
    this.timer = new Stopwatch();

    // Compute the latency of the graph if it is deadlock free
    if (deadlockFree) {
      // multi-core execution
      if (multicore) {

        switch (inputMethod) {
          case FLAT_LP:
            // Based on flattening the hierarchy into a Flat srSDF graph
            this.timer.start();

            // convert the IBSDF graph to a flat srSDF graph then to a dag
            final SDFGraph dag_lp = SrSDFTransformer.convertToDAG(IBSDFTransformer.convertToSrSDF(inputGraph, false));

            // compute the value of the longest path in the dag
            latency = GraphStructureHelper.getLongestPath(dag_lp, null, null);

            this.timer.stop();
            break;

          case FLAT_SE:
            // Based on flattening the hierarchy into a Flat srSDF graph
            this.timer.start();

            // convert the IBSDF graph to a flat srSDF graph then to a dag
            final SDFGraph dag_simu = SrSDFTransformer.convertToDAG(IBSDFTransformer.convertToSrSDF(inputGraph, false));

            // Simulate an ASAP schedule
            final ASAPSchedulerDAG schedule = new ASAPSchedulerDAG();
            latency = schedule.schedule(dag_simu);

            this.timer.stop();
            break;

          case FAST:
            // Based on a hierarchical evaluation of the latency (evaluate-replace)
            final LatencyEvaluationEngine evaluator = new LatencyEvaluationEngine();
            latency = evaluator.getMinLatencyMultiCore(inputGraph, null, false);
            this.timer = evaluator.timer;
            break;

          default:
            WorkflowLogger.getLogger().log(Level.WARNING,
                "The suported methods are: \"flat\" = classical method, \"fast\" = hierarchical method !");
            break;
        }

        // print a message with the latency value
        WorkflowLogger.getLogger().log(Level.INFO, "The minimum Latency value of a multicore execution = " + latency
            + " Cycle, Computed in : " + this.timer.toString());

      } else {
        // single core execution
        final LatencyEvaluationEngine evaluator = new LatencyEvaluationEngine();
        latency = evaluator.getMinLatencySingleCore(inputGraph, inputScenario);
        this.timer = evaluator.timer;

        // print a message with the latency value
        WorkflowLogger.getLogger().log(Level.INFO, "The minimum Latency value of a singlecore execution = " + latency
            + " Cycle, Computed in : " + this.timer.toString());
      }

    } else {
      // print an error message
      WorkflowLogger.getLogger().log(Level.WARNING, "ERROR : The graph is deadlock !!");
    }

    // set the outputs
    final Map<String, Object> outputs = new HashMap<>();
    outputs.put("SDF", inputGraph);
    outputs.put("scenario", inputScenario);
    outputs.put("latency", latency);

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
            scenario.getTimingManager().setTiming(actor.getId(), "x86", 1); // to remove
          }
        } else {
          // keep the duration of input interfaces
          final double duration = scenario.getTimingManager().getTimingOrDefault(actor.getId(), "x86").getTime();
          actor.setPropertyValue("duration", duration);

          // the duration of interfaces in neglected by setting their duration to 0
          // actor.setPropertyValue("duration", 0.);
          // scenario.getTimingManager().setTiming(actor.getId(), "x86", 0); // to remove
        }
      }

      // check the liveness of the graph
      deadlockFree = IBSDFLiveness.evaluate(inputGraph);

    }

    return deadlockFree;
  }

}
