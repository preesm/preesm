/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * blaunay <bapt.launay@gmail.com> (2015)
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
package org.preesm.algorithm.evaluator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.preesm.algorithm.mathematicalModels.PeriodicScheduleModelOjAlgo;
import org.preesm.algorithm.model.AbstractEdgePropertyType;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.preesm.commons.logger.PreesmLogger;

// TODO: Auto-generated Javadoc
/**
 * Class used to compute the optimal periodic schedule and the throughput of a non hierarchical graph (SDF or already
 * flattened IBSDF).
 *
 * @author blaunay
 */
public class SDFThroughputEvaluator extends ThroughputEvaluator {

  /**
   * Computes the throughput on the optimal periodic schedule (if it exists) of a given graph under the given scenario.
   *
   * @param inputGraph
   *          the normalized graph
   * @return the period of the optimal periodic schedule
   */
  @Override
  public double launch(final SDFGraph inputGraph) {

    double period;
    final SDFGraph sdf = inputGraph.copy();

    // Check condition of existence of a periodic schedule (Bellman Ford)
    final boolean periodic_schedule = has_periodic_schedule(sdf);

    if (periodic_schedule) {
      // Find the cycle with L/H max (using linear program)
      period = period_computation(sdf);
      // Deduce throughput of the schedule
    } else {
      PreesmLogger.getLogger().log(Level.SEVERE, "No periodic schedule for this graph ");
      return 0;
    }
    return period;
  }

  /**
   * Computes the optimal periodic schedule for the given graph and scenario, using a linear program solved by GLPK.
   *
   * @param sdf
   *          the sdf
   * @return the optimal normalized period
   */
  private double period_computation(final SDFGraph sdf) {
    // // Map to associate each edge with an index
    final PeriodicScheduleModelOjAlgo method = new PeriodicScheduleModelOjAlgo();
    return method.computeNormalizedPeriod(sdf).doubleValue();
  }

  /**
   * Checks if a periodic schedule can be computed for the given SDF graph.
   *
   * @param input
   *          the input
   * @return true, if successful
   */
  private boolean has_periodic_schedule(final SDFGraph input) {
    final Map<SDFAbstractVertex, Double> v = new LinkedHashMap<>();
    final Map<SDFEdge, Double> e = new LinkedHashMap<>();

    // Init the weights on the edges (i,j) : w = M0 + gcd(i,j) - Zj
    for (final SDFEdge edge : input.edgeSet()) {
      e.put(edge,
          ((double) (edge.getDelay().getValue())
              + SDFMathD.gcd((double) (edge.getProd().getValue()), (double) (edge.getCons().getValue())))
              - (double) (edge.getCons().getValue()));
    }

    // Initialization : source.dist = 0, v.dist = infinity
    for (final SDFAbstractVertex vertex : input.vertexSet()) {
      // v.dist = infinity
      v.put(vertex, Double.POSITIVE_INFINITY);

      // Add the edge looping on the actor
      final SDFEdge loop = input.addEdge(vertex, vertex);
      final SDFSourceInterfaceVertex in = new SDFSourceInterfaceVertex();
      in.setName(vertex.getName() + "In");
      final SDFSinkInterfaceVertex out = new SDFSinkInterfaceVertex();
      out.setName(vertex.getName() + "Out");
      AbstractEdgePropertyType<?> x;
      if (vertex.getSources().size() != 0) {
        x = vertex.getAssociatedEdge(vertex.getSources().get(0)).getCons();
      } else {
        x = vertex.getAssociatedEdge(vertex.getSinks().get(0)).getProd();
      }
      vertex.addSource(in);
      vertex.addSink(out);
      loop.setSourceInterface(out);
      loop.setTargetInterface(in);
      loop.setDelay(x);
      loop.setCons(x);
      loop.setProd(x);
      e.put(loop, (double) (loop.getDelay().getValue()));
    }
    // launch a Bellman Ford algorithm
    // source.dist = 0
    v.put(input.vertexSet().iterator().next(), (double) 0);

    // Relax all the edges
    for (int i = 1; i <= (v.size() - 1); i++) {
      for (final Map.Entry<SDFEdge, Double> entry : e.entrySet()) {
        if ((v.get(entry.getKey().getSource()) + entry.getValue()) < v.get(entry.getKey().getTarget())) {
          v.put(entry.getKey().getTarget(), v.get(entry.getKey().getSource()) + entry.getValue());
        }
      }
    }

    // Check for negative cycle
    for (final Map.Entry<SDFEdge, Double> entry : e.entrySet()) {
      if ((v.get(entry.getKey().getSource()) + entry.getValue()) < v.get(entry.getKey().getTarget())) {
        // Cycle of negative weight found, condition H(c) > 0 not respected -> no periodic schedule
        return false;
      }
    }
    return true;
  }
}
