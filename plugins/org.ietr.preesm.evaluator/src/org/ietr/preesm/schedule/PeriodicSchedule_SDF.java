package org.ietr.preesm.schedule;

import java.util.ArrayList;
import java.util.Hashtable;
import org.apache.commons.lang.math.Fraction;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.mathematicalModels.PeriodicScheduleModel_Gurobi;
import org.ietr.preesm.throughput.helpers.GraphStructureHelper;
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
public class PeriodicSchedule_SDF {

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
   * @param g
   *          SDF graph
   * @return true if periodic schedule exists
   */
  public boolean isPeriodic(SDFGraph g) {
    // TODO add the liveness module
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
    Fraction throughput = Fraction.getFraction(-1);

    // Step 1: normalize the graph
    SDFTransformer.normalize(graph);

    // if a periodic schedule exists for the graph
    if (isPeriodic(graph)) {
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
      System.out.println("a periodic schedule does not exist for this graph");
      // return -1 as an error
    }

    return throughput.doubleValue();
  }

  public double computeNormalizedPeriod(SDFGraph g, Method method) {
    switch (method) {
      case Algorithm: {
        // use one of the known algorithm to compute the optimal K
        return -1;
      }
      case LinearProgram_Gurobi: {
        System.out.println("computing Kopt !!");
        // use the linear programming to compute the optimal K
        PeriodicScheduleModel_Gurobi model = new PeriodicScheduleModel_Gurobi();

        // PeriodicScheduleModel model = new PeriodicScheduleModel();
        double period = model.getNormalizedPeriod(g);

        // set he normalized period
        g.normalizedPeriod = period;
        System.out.println("Kopt found !! " + period);

        return period;

      }
      case LinearProgram_GLPK: {
        System.out.println("computing Kopt !!");
        // use the linear programming to compute the optimal K
        PeriodicScheduleModel model = new PeriodicScheduleModel();

        // PeriodicScheduleModel model = new PeriodicScheduleModel();
        double period = model.getNormalizedPeriod(g);

        // set he normalized period
        g.normalizedPeriod = period;
        System.out.println("Kopt found !! " + period);

        return period;

      }
      default:
        return -1;
    }
  }

  /**
   * computes the execution period of each actor and define the maximum throughput of the periodic schedule as MaxTh = min (1/k*Z)
   * 
   * @param graph
   *          SDF graph
   * @return the maximum throughput of the periodic schedule
   */
  public Fraction computeActorsPeriod(SDFGraph graph) {
    // get the normalized period of the graph
    Fraction k = (Fraction) graph.getPropertyBean().getValue("normalizedPeriod");

    // define the maximum execution period
    Fraction maxW = Fraction.getFraction(0);

    // define the execution period of each actor of the graph as w=k*z
    for (SDFAbstractVertex actor : graph.vertexSet()) {
      Fraction w = k.multiplyBy(Fraction.getFraction((int) actor.getPropertyBean().getValue("normalizedRate")));
      actor.setPropertyValue("executionPeriod", w.doubleValue());

      // test if the current execution period is greater than maxW
      if (w.doubleValue() > maxW.doubleValue()) {
        maxW = w.reduce();
      }
    }

    // return the maximum throughput as 1/maxW
    Fraction maxThroughput = Fraction.getFraction(maxW.getDenominator(), maxW.getNumerator());
    return maxThroughput;
  }

  public double computeDurationOfIteration(SDFGraph g) {
    Hashtable<String, Double> S = this.computeActorsFirstExecutionStartDate(g);
    // Hashtable<String, Double> vertexFinishTime = new Hashtable<>(g.actors.size());
    double K = g.normalizedPeriod;

    double maxS = 0;

    // compute the finish time for every actor
    // System.out.println("finish Time :");
    for (Actor a : g.actors.values()) {
      double Z = a.normalizationValue;
      double RV = a.repetitionFactor;
      double l = a.duration;

      double finishTime = S.get(a.id) + (RV - 1) * K * Z + l;
      // vertexFinishTime.put(a.id, finishTime);
      // System.out.println("S("+a.id+")= " + S.get(a.id)+" + " + (RV-1) + "*" + K + "*" + Z +" + "+ l +" = "+ finishTime + " == "+ RV*K*Z);

      if (finishTime > maxS)
        maxS = finishTime;
    }

    // for now take the hall iteration
    // duration = RV*W = RV*K*Z
    // WRONG!! RV*K*Z = RV*Z*K = D*K = average time for iteration
    // Actor a = g.actors.elements().nextElement();
    // double d = a.repetitionFactor*K*a.normalizationValue;

    // duration = max(S(outI)+RV*l , RV*K*Z)

    // System.out.println("L="+maxS);
    return maxS;
  }

  /**
   * compute the duration of a complete iteration
   * 
   * @param g
   * @return
   */
  public Hashtable<String, Double> computeActorsFirstExecutionStartDate(SDFGraph g) {

    // compute starting times
    /*
     * see Ben Abid paper : step 1: add a dummy vertex to the graph step 2: connect the new actor to every actor of the graph with a null value step 3: use the
     * bellman ford algorithm to compute the starting times as the longest path from the dummy node to all actors
     */

    // Revalue the edges : v = L + k*(out - M - gcd) (use the normalized version of the graph)
    Hashtable<String, Double> edgeValue = new Hashtable<>(g.edges.size());
    for (Edge e : g.edges.values()) {
      double v = e.sourceActor.duration + g.normalizedPeriod * (e.prod - e.initialMarking - tool.gcd(e.cons, e.prod)) * e.normalizationFactor;
      edgeValue.put(e.id, v);
    }

    // initialize the vertex distance
    Hashtable<String, Double> vertexDistance = new Hashtable<>(g.actors.size());
    for (Actor a : g.actors.values())
      vertexDistance.put(a.id, .0);

    // // saving the parents (No need for now)
    // Hashtable<String, String> vertexParent = new Hashtable<>(g.actors.size());
    // for (Actor a : g.actors.values())
    // vertexParent.put(a.id, "");

    // source vertex to evaluate all parts of the graph
    // add the dummy actor to the graph
    g.createActor("dummy", null, null, null, null);
    // connect the dummy actor to all actors
    for (Actor a : g.actors.values()) {
      g.createEdge("dummy_" + a.id, "dummy", null, a.id, null, 1., 1., 0., null);
      vertexDistance.put(a.id, .0);
    }
    // set the distance between the dummy actor and the rest of actor to 0
    for (Edge e : g.actors.get("dummy").OutputEdges.values())
      edgeValue.put(e.id, .0);

    // // print the edge value
    // for(Edge e: g.edges.values())
    // System.out.println("v(" + e.sourceActor.id + "," + e.targetActor.id + ") = " + edgeValue.get(e.id));

    // // initialize the source vertex
    // //vertexDistance.put("dummy", .0); // it is already done in the previous loop
    // System.out.println("the source vertex = dummy");
    System.out.println("G = (" + g.actors.size() + " , " + g.edges.size() + ")");

    // counter for the V-1 iterations
    int count = 0;

    // a condition for the while loop
    // no need to complete the V-1 iterations if the distance of any actor
    // does not change
    boolean repete = true;

    // relax edges
    while (repete && count < g.actors.size() - 1) {
      repete = false;
      for (Edge e : g.edges.values()) {
        // test the distance
        if (vertexDistance.get(e.targetActor.id) < vertexDistance.get(e.sourceActor.id) + edgeValue.get(e.id)) {
          // update the distance
          vertexDistance.put(e.targetActor.id, vertexDistance.get(e.sourceActor.id) + edgeValue.get(e.id));
          // update the parent
          // vertexParent.replace(e.targetActor.id, e.sourceActor.id);
          // we need to perform another iteration
          repete = true;
        }
      }
      // Increments the iteration counter
      count++;
    }

    // check for negative circuit if we complete the v-1 iterations
    // if (count == g.actors.size() - 1) {
    // // relax all the edges
    // for (Edge e : g.edges.values()) {
    // if (vertexDistance.get(e.targetActor.id) < vertexDistance.get(e.sourceActor.id) + edgeValue.get(e.id)) {
    // // negative circuit detected
    // // if a part of the graph is not live the global graph is
    // // not too
    // System.out.println("Negativ circuit detected !!");
    // }
    // }
    // }

    // remove the dummy actor
    g.removeActor("dummy");

    // Print distance to verify
    // for (Actor a : g.actors.values()){
    // a.startDate = vertexDistance.get(a.id);
    // a.finishDate = a.startDate + a.duration;
    // System.out.println(a.id + " = " + a.startDate + " to " + a.finishDate);
    // }

    return vertexDistance;
  }

}
