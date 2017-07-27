package org.ietr.preesm.schedule;

import java.util.ArrayList;
import java.util.Hashtable;
import org.apache.commons.lang3.math.Fraction;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.mathematicalModels.PeriodicScheduleModel_GLPK;
import org.ietr.preesm.mathematicalModels.PeriodicScheduleModel_Gurobi;
import org.ietr.preesm.throughput.helpers.GraphStructureHelper;
import org.ietr.preesm.throughput.helpers.MathFunctionsHelper;
import org.ietr.preesm.throughput.transformers.SDFTransformer;

/**
 * this class computes the periodic schedule for an SDF graph. It computes the execution period W and the start date of the first execution for each actor. It
 * also define the maximum throughput of a periodic schedule of the graph.
 * 
 * TODO add the method that return the start date of an execution t of an actor a
 * 
 * TODO use only one method to compute the normalized period of the graph which return a fraction instead of a double.
 * 
 * @author HDeroui
 *
 */
public class PeriodicScheduler_SDF {

  /**
   * @author HDeroui
   *
   *         methods for MCR problem
   * 
   */
  public static enum Method {
    Algorithm, LinearProgram_Gurobi, LinearProgram_GLPK
  }

  /**
   * tests if a periodic schedule exists for an SDF graph using the same sufficient condition of Alix Munier for the liveness of SDF graphs.
   * 
   * @param graph
   *          SDF graph
   * @return true if periodic schedule exists
   */
  public boolean isPeriodic(SDFGraph graph) {
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
   * Schedules an SDF graph with a periodic schedule. It computes the execution period W and the start date S0 of the first execution for each actor of the
   * graph.
   * 
   * @param graph
   *          SDF graph
   * @param method
   *          for the MCR problem
   * @param selfLoopEdge
   *          if true the method will add a self loop edge for each actor to add an execution dependency between the instances of each actor
   * @return throughput
   */

  public double schedule(SDFGraph graph, Method method, boolean selfLoopEdge) {
    System.out.println("Scheduling the graph ...");
    double throughput = -1.;

    // Step 1: normalize the graph
    SDFTransformer.normalize(graph);

    // if a periodic schedule exists for the graph
    if (isPeriodic(graph)) {
      System.out.println("This graph admit a periodic schedule !");

      // add a self loop edge for each actor if selfLoopEdge = true
      ArrayList<SDFEdge> selfLoopEdgesList = null;
      if (selfLoopEdge) {
        selfLoopEdgesList = new ArrayList<>(graph.vertexSet().size());
        for (SDFAbstractVertex actor : graph.vertexSet()) {
          SDFEdge edge = GraphStructureHelper.addEdge(graph, actor.getName(), null, actor.getName(), null,
              (Integer) actor.getPropertyBean().getValue("normalizedRate"), (Integer) actor.getPropertyBean().getValue("normalizedRate"),
              (Integer) actor.getPropertyBean().getValue("normalizedRate"), null);
          selfLoopEdgesList.add(edge);
        }
      }

      // Step 2: compute the normalized period K
      this.computeNormalizedPeriod(graph, method);

      // remove the self loop edges added before
      if (selfLoopEdge) {
        for (SDFEdge edge : selfLoopEdgesList) {
          graph.removeEdge(edge);
        }
      }

      // Step 3: compute actors period and define the maximum throughput of the computed periodic schedule
      throughput = this.computeActorsPeriod(graph);

      // Step 4: compute the start date of the first execution of each actor
      this.computeActorsFirstExecutionStartDate(graph);

    } else {
      System.err.println("A Periodic Schedule does not exist for this graph");
      // return -1 as an error
    }

    return throughput;
  }

  /**
   * @param graph
   *          SDF graph
   * @param method
   *          Math model or Algorithm
   */
  public Fraction computeNormalizedPeriod(SDFGraph graph, Method method) {
    System.out.println("Computing the normalized period of the graph ...");
    switch (method) {
      case Algorithm: {
        // use one of the known algorithm to compute the optimal K
        return null;
      }
      case LinearProgram_Gurobi: {
        // use the linear programming to compute the optimal K
        PeriodicScheduleModel_Gurobi model = new PeriodicScheduleModel_Gurobi();
        return model.computeNormalizedPeriod(graph);
      }
      case LinearProgram_GLPK: {
        // use the linear programming to compute the optimal K
        PeriodicScheduleModel_GLPK model = new PeriodicScheduleModel_GLPK();
        return model.computeNormalizedPeriod(graph);
      }
      default:
        return null;
    }
  }

  /**
   * computes the execution period of each actor and define the maximum throughput of the periodic schedule as MaxTh = min (1/k*Z)
   * 
   * @param graph
   *          SDF graph
   * @return the maximum throughput of the periodic schedule
   */
  public double computeActorsPeriod(SDFGraph graph) {
    System.out.println("Computing Actors duration ...");
    // get the normalized period of the graph
    Fraction k = (Fraction) graph.getPropertyBean().getValue("normalizedPeriod");

    // define the maximum execution period
    Fraction maxW = Fraction.getFraction(0);

    // define the execution period of each actor of the graph as w=k*z
    for (SDFAbstractVertex actor : graph.vertexSet()) {
      Fraction w = k.multiplyBy(Fraction.getFraction((Double) actor.getPropertyBean().getValue("normalizedRate")));
      actor.setPropertyValue("executionPeriod", w.doubleValue());

      // test if the current execution period is greater than maxW
      if (w.doubleValue() > maxW.doubleValue()) {
        maxW = w.reduce();
      }
    }

    // return the maximum throughput as 1/maxW
    double maxThroughput = 1 / maxW.doubleValue();
    return maxThroughput;
  }

  /**
   * Computes the duration of the first iteration of the graph
   * 
   * @param graph
   *          SDF graph
   * @return latency
   */
  public double computeGraphLatency(SDFGraph graph) {
    System.out.println("Computing graph latency ...");
    // formule : S(t) = S(t0) + w*t
    // finish date = S(t) + L
    // for each actor computes the finish date of its last execution (RV)
    // latency = the max finish date
    double maxFinishDate = 0;

    // compute the finish time for every actor
    for (SDFAbstractVertex actor : graph.vertexSet()) {
      double s0 = (double) actor.getPropertyBean().getValue("firstExecutionStartDate");
      double w = (double) actor.getPropertyBean().getValue("executionPeriod");
      double l = (double) actor.getPropertyBean().getValue("duration"); // or use the scenario

      // finish date
      double finishDateOfLastExecution = s0 + w * actor.getNbRepeatAsInteger() + l;

      if (finishDateOfLastExecution > maxFinishDate) {
        maxFinishDate = finishDateOfLastExecution;
      }
    }

    return maxFinishDate;
  }

  /**
   * compute the duration of the graph period as RV(a)*W(a) where a is an arbitrary actor of the graph
   * 
   * @param graph
   *          SDF graph
   * @return graph period
   */
  public double computeGraphPeriod(SDFGraph graph) {
    System.out.println("Computing graph period ...");
    // get an arbitrary actor from the graph
    SDFAbstractVertex actor = graph.vertexSet().iterator().next();
    double w = (double) actor.getPropertyBean().getValue("executionPeriod");
    double graphPeriod = actor.getNbRepeatAsInteger() * w;

    return graphPeriod;
  }

  /**
   * compute the start date of the first execution of each actor
   * 
   * @param graph
   *          SDF graph
   */
  public void computeActorsFirstExecutionStartDate(SDFGraph graph) {
    System.out.println("Computing actors first execution start date ...");
    /*
     * see Ben Abid paper : step 1: add a dummy vertex to the graph step 2: connect the new actor to every actor of the graph with a null value step 3: use the
     * bellman ford algorithm to compute the starting times as the longest path from the dummy node to all actors
     */

    // set edges value : v = L + k*h (use the normalized version of the graph)
    // h = (out - M0 - gcd)* alpha(e)
    Hashtable<String, Double> edgeValue = new Hashtable<>(graph.edgeSet().size());
    for (SDFEdge e : graph.edgeSet()) {
      double l = (double) e.getSource().getPropertyBean().getValue("duration");
      double k = ((Fraction) graph.getPropertyBean().getValue("normalizedPeriod")).doubleValue();
      double gcd = MathFunctionsHelper.gcd(e.getProd().intValue(), e.getCons().intValue());
      double alpha = (double) e.getPropertyBean().getValue("normalizationFactor");
      double h = (e.getCons().intValue() - e.getDelay().intValue() - gcd) * alpha;
      double v = l + k * h;
      edgeValue.put((String) e.getPropertyBean().getValue("edgeName"), v);
    }

    // initialize the vertex distance
    Hashtable<String, Double> vertexDistance = new Hashtable<>(graph.vertexSet().size());
    // for (SDFAbstractVertex a : graph.vertexSet()) {
    // vertexDistance.put(a.getName(), .0);
    // }

    // source vertex to evaluate all parts of the graph
    // add the dummy actor to the graph
    SDFAbstractVertex dummy = GraphStructureHelper.addActor(graph, "dummy", null, null, null, null, null);

    // connect the dummy actor to all actors
    for (SDFAbstractVertex a : graph.vertexSet()) {
      // add an edge between the dummy actor and the selected actor
      SDFEdge e = GraphStructureHelper.addEdge(graph, "dummy", null, a.getName(), null, 1, 1, 0, null);
      // set the value of the added edge to 0
      edgeValue.put((String) e.getPropertyBean().getValue("edgeName"), 0.);
      // set the vertex distance to 0
      vertexDistance.put(a.getName(), .0);
    }

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
        if (vertexDistance.get(e.getTarget().getName()) < newDistance) {
          // update the distance
          vertexDistance.put(e.getTarget().getName(), newDistance);
          // we need to perform another iteration
          repete = true;
        }
      }
      // Increments the iteration counter
      count++;
    }

    // remove the dummy actor
    graph.removeVertex(dummy);

    // save the start date into actors properties
    for (SDFAbstractVertex a : graph.vertexSet()) {
      a.setPropertyValue("firstExecutionStartDate", vertexDistance.get(a.getName()));
    }
  }
}
