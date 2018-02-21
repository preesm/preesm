/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2017)
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
package org.ietr.preesm.throughput;

import org.apache.commons.lang3.math.Fraction;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.schedule.ASAPScheduler_SDF;
import org.ietr.preesm.schedule.PeriodicScheduler_SDF;
import org.ietr.preesm.throughput.tools.helpers.GraphStructureHelper;
import org.ietr.preesm.throughput.tools.helpers.Stopwatch;
import org.ietr.preesm.throughput.tools.transformers.SDFTransformer;

/**
 * @author hderoui
 *
 */
public class ScheduleReplace {

  public Stopwatch timer;
  // private PreesmScenario preesmScenario;

  /**
   * @param inputGraph
   *          IBSDF graph contains actors duration
   * @return throughput of the graph
   */
  public double evaluate(SDFGraph inputGraph) {
    // this.preesmScenario = scenario;
    System.out.println("Computing the throughput of the graph using Schedule-Replace technique ...");
    this.timer = new Stopwatch();
    timer.start();

    // Step 1: define the execution duration of each hierarchical actor
    System.out.println("Step 1: define the execution duration of each hierarchical actor");
    for (SDFAbstractVertex actor : inputGraph.vertexSet()) {
      if (actor.getGraphDescription() != null) {
        // set the duration of the hierarchical actor
        Double duration = this.setHierarchicalActorsDuration((SDFGraph) actor.getGraphDescription());
        actor.setPropertyValue("duration", duration);
        // if (preesmScenario != null) {
        // this.preesmScenario.getTimingManager().setTiming(actor.getId(), "x86", duration.longValue());
        // }
      }
    }

    // Step 2: convert the topGraph to a srSDF graph
    System.out.println("Step 2: convert the topGraph to a srSDF graph");
    SDFGraph srSDF = SDFTransformer.convertToSrSDF(inputGraph);

    // Step 3: add a self loop edge to each hierarchical actor
    System.out.println("Step 3: add a self loop edge to each hierarchical actor");
    for (SDFAbstractVertex actor : srSDF.vertexSet()) {
      SDFAbstractVertex baseActor = (SDFAbstractVertex) actor.getPropertyBean().getValue("baseActor");
      if (baseActor.getGraphDescription() != null) {
        GraphStructureHelper.addEdge(srSDF, actor.getName(), null, actor.getName(), null, 1, 1, 1, null);
      }
    }

    // Step 4: compute the throughput with the Periodic Schedule
    System.out.println("Step 4: compute the throughput using the Periodic Schedule");
    // normalize the graph
    SDFTransformer.normalize(srSDF);
    // compute its normalized period K
    PeriodicScheduler_SDF periodic = new PeriodicScheduler_SDF();
    Fraction k = periodic.computeNormalizedPeriod(srSDF, PeriodicScheduler_SDF.Method.LinearProgram_Gurobi);
    // compute its throughput as 1/K
    double throughput = 1 / k.doubleValue();
    timer.stop();
    System.out.println("Throughput of the graph = " + throughput + " computed in " + timer.toString());

    return throughput;
  }

  /**
   * Computes the duration of a subgraph
   * 
   * @param subgraph
   *          subgraph of a hierarchical actor
   * @return the duration of the subgraph
   */
  public double setHierarchicalActorsDuration(SDFGraph subgraph) {
    // recursive function
    for (SDFAbstractVertex actor : subgraph.vertexSet()) {
      if (actor.getGraphDescription() != null) {
        // set the duration of the hierarchical actor
        Double duration = this.setHierarchicalActorsDuration((SDFGraph) actor.getGraphDescription());
        actor.setPropertyValue("duration", duration);
        // this.preesmScenario.getTimingManager().setTiming(actor.getId(), "x86", duration.longValue());
      }
    }

    // compute the subgraph duration using an ASAP schedule
    ASAPScheduler_SDF asap = new ASAPScheduler_SDF();
    return asap.schedule(subgraph);
  }

}
