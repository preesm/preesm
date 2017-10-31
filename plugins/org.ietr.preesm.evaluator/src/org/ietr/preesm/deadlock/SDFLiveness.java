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
  public static boolean evaluate(SDFGraph sdf) {
    Stopwatch timer = new Stopwatch();
    timer.start();

    // try first the Sufficient Condition of liveness
    System.out.println("Liveness evaluation : trying the sufficient condition ...");
    boolean live = sufficientCondition(sdf);

    // if SC fails we can not conclude until we try the symbolic execution
    if (!live) {
      System.err.println("Liveness evaluation : sufficient condition have failed");
      System.out.println("Liveness evaluation : trying the symbolic execution ...");
      live = symbolicExecution(sdf);
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
  public static boolean sufficientCondition(SDFGraph graph) {
    // add the name property for each edge of the graph
    for (SDFEdge e : graph.edgeSet()) {
      e.setPropertyValue("edgeName", Identifier.generateEdgeId());
    }

    // step 1: normalize the graph
    SDFTransformer.normalize(graph);

    // step 2: test the existence of negative circuits

    // set edges value : v = h (use the normalized version of the graph)
    // h = (out - M0 - gcd)* alpha(e)
    Hashtable<String, Double> edgeValue = new Hashtable<>(graph.edgeSet().size());
    for (SDFEdge e : graph.edgeSet()) {
      double gcd = MathFunctionsHelper.gcd(e.getProd().intValue(), e.getCons().intValue());
      double alpha = (double) e.getPropertyBean().getValue("normalizationFactor");
      double h = (e.getDelay().intValue() - e.getCons().intValue() + gcd) * alpha;
      edgeValue.put((String) e.getPropertyBean().getValue("edgeName"), h);
    }

    // initialize the vertex distance
    Hashtable<String, Double> vertexDistance = new Hashtable<>(graph.vertexSet().size());
    for (SDFAbstractVertex a : graph.vertexSet()) {
      vertexDistance.put(a.getName(), Double.POSITIVE_INFINITY);
    }

    // // print the edge value
    // for(Edge e: g.edges.values())
    // System.out.println("v(" + e.sourceActor.id + "," + e.targetActor.id + ") = " + edgeValue.get(e.id));

    // in case of a non strongly connected graph we need to choose many source vertex to evaluate all parts of the graph
    for (SDFAbstractVertex vertexSource : graph.vertexSet()) {
      if (vertexDistance.get(vertexSource.getName()) == Double.POSITIVE_INFINITY) {
        // initialize the source vertex
        vertexDistance.put(vertexSource.getName(), 0.);

        // counter for the V-1 iterations
        int count = 0;

        // a condition for the while loop
        // no need to complete the V-1 iterations if the distance of any actor does not change
        boolean repete = true;

        // relax edges
        while (repete && count < graph.vertexSet().size() - 1) {
          repete = false;
          for (SDFEdge e : graph.edgeSet()) {
            // test the distance
            double newDistance = vertexDistance.get(e.getSource().getName()) + edgeValue.get((String) e.getPropertyBean().getValue("edgeName"));
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
        if (count == graph.vertexSet().size() - 1) {
          // relax all the edges
          for (SDFEdge e : graph.edgeSet()) {
            if (vertexDistance.get(e.getTarget().getName()) > vertexDistance.get(e.getSource().getName())
                + edgeValue.get((String) e.getPropertyBean().getValue("edgeName"))) {
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
  public static boolean symbolicExecution(SDFGraph sdf) {
    // execute the graph until it finishes an iteration
    ASAPScheduler_SDF scheduler = new ASAPScheduler_SDF();
    scheduler.schedule(sdf);

    // the live attribute of the scheduler will indicate if the schedule has succeeded to schedule a complete iteration
    return scheduler.live;
  }
}
