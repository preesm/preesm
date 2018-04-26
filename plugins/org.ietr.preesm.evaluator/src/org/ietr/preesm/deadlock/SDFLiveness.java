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
package org.ietr.preesm.deadlock;

import java.util.Hashtable;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.schedule.ASAPScheduler_SDF;
import org.ietr.preesm.throughput.tools.helpers.MathFunctionsHelper;
import org.ietr.preesm.throughput.tools.helpers.Stopwatch;
import org.ietr.preesm.throughput.tools.parsers.Identifier;
import org.ietr.preesm.throughput.tools.transformers.SDFTransformer;

/**
 * @author hderoui
 *
 */
public abstract class SDFLiveness {

  /**
   * @param sdf
   *          input graph
   * @return true if live, false if not
   */
  public static boolean evaluate(final SDFGraph sdf) {
    final Stopwatch timer = new Stopwatch();
    timer.start();

    // try first the Sufficient Condition of liveness
    System.out.println("Liveness evaluation : trying the sufficient condition ...");
    boolean live = SDFLiveness.sufficientCondition(sdf);

    // if SC fails we can not conclude until we try the symbolic execution
    if (!live) {
      System.err.println("Liveness evaluation : sufficient condition have failed");
      System.out.println("Liveness evaluation : trying the symbolic execution ...");
      live = SDFLiveness.symbolicExecution(sdf);
    }

    timer.stop();
    if (live) {
      System.out.println("SDF Graph " + sdf.getName() + " is live !!  evaluated in " + timer.toString());
    } else {
      System.err.println("SDF Graph " + sdf.getName() + " is deadlock !!  evaluated in " + timer.toString());
    }

    return live;
  }

  /**
   * Test the sufficient condition (SC) of liveness of SDF graph. If the SC is satisfied, the graph is live.
   *
   * @param graph
   *          input graph
   * @return true if SC satisfied, false if not
   */
  public static boolean sufficientCondition(final SDFGraph graph) {
    // add the name property for each edge of the graph
    for (final SDFEdge e : graph.edgeSet()) {
      e.setPropertyValue("edgeName", Identifier.generateEdgeId());
    }

    // step 1: normalize the graph
    SDFTransformer.normalize(graph);

    // step 2: test the existence of negative circuits

    // set edges value : v = h (use the normalized version of the graph)
    // h = (out - M0 - gcd)* alpha(e)
    final Hashtable<String, Double> edgeValue = new Hashtable<>(graph.edgeSet().size());
    for (final SDFEdge e : graph.edgeSet()) {
      final double gcd = MathFunctionsHelper.gcd(e.getProd().intValue(), e.getCons().intValue());
      final double alpha = (double) e.getPropertyBean().getValue("normalizationFactor");
      final double h = ((e.getDelay().intValue() - e.getCons().intValue()) + gcd) * alpha;
      edgeValue.put((String) e.getPropertyBean().getValue("edgeName"), h);
    }

    // initialize the vertex distance
    final Hashtable<String, Double> vertexDistance = new Hashtable<>(graph.vertexSet().size());
    for (final SDFAbstractVertex a : graph.vertexSet()) {
      vertexDistance.put(a.getName(), Double.POSITIVE_INFINITY);
    }

    // // print the edge value
    // for(Edge e: g.edges.values())
    // System.out.println("v(" + e.sourceActor.id + "," + e.targetActor.id + ") = " + edgeValue.get(e.id));

    // in case of a non strongly connected graph we need to choose many source vertex to evaluate all parts of the graph
    for (final SDFAbstractVertex vertexSource : graph.vertexSet()) {
      if (vertexDistance.get(vertexSource.getName()) == Double.POSITIVE_INFINITY) {
        // initialize the source vertex
        vertexDistance.put(vertexSource.getName(), 0.);

        // counter for the V-1 iterations
        int count = 0;

        // a condition for the while loop
        // no need to complete the V-1 iterations if the distance of any actor does not change
        boolean repete = true;

        // relax edges
        while (repete && (count < (graph.vertexSet().size() - 1))) {
          repete = false;
          for (final SDFEdge e : graph.edgeSet()) {
            // test the distance
            final double newDistance = vertexDistance.get(e.getSource().getName()) + edgeValue.get(e.getPropertyBean().getValue("edgeName"));
            if (vertexDistance.get(e.getTarget().getName()) > newDistance) {
              // update the distance
              vertexDistance.put(e.getTarget().getName(), newDistance);
              // we need to perform another iteration
              repete = true;
            }
          }
          // Increments the iteration counter
          count++;
        }

        // check for negative circuit if we complete the v-1 iterations
        if (count == (graph.vertexSet().size() - 1)) {
          // relax all the edges
          for (final SDFEdge e : graph.edgeSet()) {
            if (vertexDistance
                .get(e.getTarget().getName()) > (vertexDistance.get(e.getSource().getName()) + edgeValue.get(e.getPropertyBean().getValue("edgeName")))) {
              // negative circuit detected if a part of the graph is not live the global graph is not too
              System.err.println("Negativ cycle detected !!");
              // System.err.println("This graph has no Periodic Schedule !");
              return false;
            }
          }
        }
      }
    }

    return true;
  }

  /**
   * Execute the graph until it finish an iteration. The graph is live if it succeeds to complete an iteration.
   *
   * @param sdf
   *          input graph
   * @return true if live, false if not
   */
  public static boolean symbolicExecution(final SDFGraph sdf) {
    // execute the graph until it finishes an iteration
    final ASAPScheduler_SDF scheduler = new ASAPScheduler_SDF();
    scheduler.schedule(sdf);

    // the live attribute of the scheduler will indicate if the schedule has succeeded to schedule a complete iteration
    return scheduler.live;
  }
}
