package org.ietr.preesm.schedule;

import java.util.ArrayList;
import java.util.Hashtable;
import org.apache.commons.lang.math.Fraction;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.mathematicalModels.PeriodicScheduleModel_Gurobi;
import org.ietr.preesm.throughput.transformers.SDFTransformer;

/**
 * this class computes the periodic schedule for an SDF graph :
 * 
 * compute the period w for each actor and determine the maximum throughput of the graph.
 * 
 * @author HDeroui
 *
 */
public class PeriodicSchedule_SDF {

  // methods for MCR problem
  public static enum Method {
    Algorithm, LinearProgram_Gurobi, LinearProgram_GLPK
  };

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

  public double schedule(SDFGraph graph, Method method) {
    double throughput = -1;

    // Step 1: normalize the graph
    SDFTransformer.normalize(graph);

    // if a periodic schedule exists for the graph
    if (isPeriodic(graph)) {
      // Step 2: compute the normalized period K
      // this.computePeriods(g, method);
      this.computePeriodswithoutLoop(graph, method);

      // Step 3: compute actors period and define the maximum throughput of the graph
      this.computeMaxThroughput(graph);

      // Step 4: compute the start date of the first execution of each actor
      this.computeStartingTimes(graph);

    } else {
      System.out.println("a periodic schedule does not exist for this graph");
      // return -1 as an error
    }

    return throughput;
  }

  /**
   * compute all actors period through a mathematical model (or an algorithm)
   */
  public double computePeriods(SDFGraph g, Method method) {
    switch (method) {
      case Algorithm: {
        // use one of the known algorithm to compute the optimal K
        return -1;
      }
      case LinearProgram_Gurobi: {
        System.out.println("computing Kopt !!");
        // ***** add self loop to each actor *****
        ArrayList<String> addedEdgesList = new ArrayList<>(g.actors.size());
        for (Actor a : g.actors.values()) {
          g.createEdge(a.id + "_Loop", a.id, null, a.id, null, a.normalizationValue, a.normalizationValue, a.normalizationValue, null);
          addedEdgesList.add(a.id + "_Loop");
        }

        // use the linear programming to compute the optimal K
        PeriodicScheduleModel_Gurobi model = new PeriodicScheduleModel_Gurobi();
        double period = model.getNormalizedPeriod(g);

        // remove the added edges
        for (String e : addedEdgesList)
          g.removeEdge(e);

        // set he normalized period
        g.normalizedPeriod = period;
        System.out.println("Kopt found !! " + period);
        return period;

      }
      default:
        return -1;
    }
  }

  public double computePeriodswithoutLoop(SDFGraph g, Method method) {
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

  public Fraction computePeriodFractionWithoutLoop(SDFGraph g, Method method) {
    switch (method) {
      case Algorithm: {
        // use one of the known algorithm to compute the optimal K
        return null;
      }
      case LinearProgram_Gurobi: {
        System.out.println("computing Kopt !!");
        // use the linear programming to compute the optimal K
        KPeriodFraction_Gurobi model = new KPeriodFraction_Gurobi();

        // PeriodicScheduleModel model = new PeriodicScheduleModel();
        Fraction k = model.getNormalizedPeriod(g);

        // set he normalized period
        g.normalizedPeriod = model.period;
        System.out.println("Kopt found !! " + g.normalizedPeriod);

        return k;

      }
      default:
        System.out.println("Only \"LinearProgram_Gurobi\" is aloud !");
        return null;
    }
  }

  /**
   * compute the maximum throughput of the graph</br>
   * max Th = min (1/k*Z)
   */
  public double computeMaxThroughput(SDFGraph g) {
    // get the maximum Z
    Double Zmax = .0;
    for (Actor a : g.actors.values())
      if (a.normalizationValue > Zmax)
        Zmax = a.normalizationValue;
    // compute the maximum throughput : Th = 1/Kmin*Zmax
    System.out.println("ZMax = " + Zmax);
    return 1 / (g.normalizedPeriod * Zmax);
  }

  public double computeDurationOfIteration(SDFGraph g) {
    Hashtable<String, Double> S = this.computeStartingTimes(g);
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
  public Hashtable<String, Double> computeStartingTimes(SDFGraph g) {

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
