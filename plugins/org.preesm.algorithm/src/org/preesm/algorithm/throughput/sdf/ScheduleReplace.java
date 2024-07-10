/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2021) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Hamza Deroui [hamza.deroui@insa-rennes.fr] (2017)
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
package org.preesm.algorithm.throughput.sdf;

import java.util.logging.Level;
import org.apache.commons.lang3.math.Fraction;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.schedule.sdf.ASAPSchedulerSDF;
import org.preesm.algorithm.schedule.sdf.PeriodicSchedulerSDF;
import org.preesm.algorithm.throughput.sdf.tools.GraphStructureHelper;
import org.preesm.algorithm.throughput.sdf.tools.SDFTransformer;
import org.preesm.commons.logger.PreesmLogger;

/**
 * @author hderoui
 *
 */
public class ScheduleReplace {

  /**
   * @param inputGraph
   *          IBSDF graph contains actors duration
   * @return throughput of the graph
   */
  public double evaluate(final SDFGraph inputGraph) {
    PreesmLogger.getLogger().log(Level.FINEST,
        "Computing the throughput of the graph using Schedule-Replace technique ...");

    // Step 1: define the execution duration of each hierarchical actor
    PreesmLogger.getLogger().log(Level.FINEST, "Step 1: define the execution duration of each hierarchical actor");
    for (final SDFAbstractVertex actor : inputGraph.vertexSet()) {
      if (actor.getGraphDescription() != null) {
        // set the duration of the hierarchical actor
        final Double duration = setHierarchicalActorsDuration((SDFGraph) actor.getGraphDescription());
        actor.setPropertyValue("duration", duration);
      }
    }

    // Step 2: convert the topGraph to a srSDF graph
    PreesmLogger.getLogger().log(Level.FINEST, "Step 2: convert the topGraph to a srSDF graph");
    final SDFGraph srSDF = SDFTransformer.convertToSrSDF(inputGraph);

    // Step 3: add a self loop edge to each hierarchical actor
    PreesmLogger.getLogger().log(Level.FINEST, "Step 3: add a self loop edge to each hierarchical actor");
    for (final SDFAbstractVertex actor : srSDF.vertexSet()) {
      final SDFAbstractVertex baseActor = actor.getPropertyBean().getValue("baseActor");
      if (baseActor.getGraphDescription() != null) {
        GraphStructureHelper.addEdge(srSDF, actor.getName(), null, actor.getName(), null, 1, 1, 1, null);
      }
    }

    // Step 4: compute the throughput with the Periodic Schedule
    PreesmLogger.getLogger().log(Level.FINEST, "Step 4: compute the throughput using the Periodic Schedule");
    // normalize the graph
    SDFTransformer.normalize(srSDF);
    // compute its normalized period K
    final PeriodicSchedulerSDF periodic = new PeriodicSchedulerSDF();
    final Fraction k = periodic.computeNormalizedPeriod(srSDF, PeriodicSchedulerSDF.Method.LINEAR_PROGRAMMING_GUROBI);
    // compute its throughput as 1/K
    final double throughput = 1 / k.doubleValue();
    final String msg = "Throughput of the graph = " + throughput;
    PreesmLogger.getLogger().log(Level.FINEST, msg);

    return throughput;
  }

  /**
   * Computes the duration of a subgraph
   *
   * @param subgraph
   *          subgraph of a hierarchical actor
   * @return the duration of the subgraph
   */
  private double setHierarchicalActorsDuration(final SDFGraph subgraph) {
    // recursive function
    for (final SDFAbstractVertex actor : subgraph.vertexSet()) {
      if (actor.getGraphDescription() != null) {
        // set the duration of the hierarchical actor
        final Double duration = setHierarchicalActorsDuration((SDFGraph) actor.getGraphDescription());
        actor.setPropertyValue("duration", duration);
      }
    }

    // compute the subgraph duration using an ASAP schedule
    final ASAPSchedulerSDF asap = new ASAPSchedulerSDF();
    return asap.schedule(subgraph);
  }

}
