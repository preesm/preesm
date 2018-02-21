/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2017 - 2018)
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
package org.ietr.preesm.schedule;

import java.util.ArrayList;
import java.util.Hashtable;
import org.apache.commons.lang3.math.Fraction;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.evaluator.Activator;
import org.ietr.preesm.mathematicalModels.PeriodicScheduleModel_ojAlgo;
import org.ietr.preesm.mathematicalModels.SolverMethod;
import org.ietr.preesm.throughput.tools.helpers.GraphStructureHelper;
import org.ietr.preesm.throughput.tools.helpers.MathFunctionsHelper;
import org.ietr.preesm.throughput.tools.helpers.Stopwatch;
import org.ietr.preesm.throughput.tools.transformers.SDFTransformer;

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
    Algorithm, LinearProgram_Gurobi, LinearProgram_GLPK, LinearProgram_ojAlgo
  }

  public static final Method METHOD_DEFAULT_VALUE = Method.LinearProgram_ojAlgo;

  /**
   * tests if a periodic schedule exists for an SDF graph using the same sufficient condition of liveness of SDF graphs.
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
    Stopwatch timer = new Stopwatch();
    timer.start();

    System.out.println("Scheduling the graph ...");
    double throughput = -1.;

    // Step 1: normalize the graph
    SDFTransformer.normalize(graph);

    // if a periodic schedule exists for the graph
    if (isPeriodic(graph)) {
      // System.out.println("This graph admit a periodic schedule !");

      // add a self loop edge for each actor if selfLoopEdge = true
      ArrayList<SDFEdge> selfLoopEdgesList = null;
      if (selfLoopEdge) {
        selfLoopEdgesList = new ArrayList<>(graph.vertexSet().size());
        for (SDFAbstractVertex actor : graph.vertexSet()) {
          int z = ((Double) actor.getPropertyBean().getValue("normalizedRate")).intValue();
          Double alpha = 1.;
          SDFEdge edge = GraphStructureHelper.addEdge(graph, actor.getName(), null, actor.getName(), null, z, z, z, null);
          selfLoopEdgesList.add(edge);
          edge.setPropertyValue("normalizationFactor", alpha);
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
      this.computeActorsStartingTime(graph);

      timer.stop();
      System.out.println("Schedule completed in " + timer.toString());

    } else {

      timer.stop();
      System.err.println("A Periodic Schedule does not exist for this graph");
      // return -1 as an error
    }

    return throughput;
  }

  /**
   * computes the throughput of a graph based on the periodic schedule
   * 
   * @param graph
   *          SDF graph
   * @param method
   *          method
   * @param selfLoopEdge
   *          boolean
   * @return throughput
   */
  public Double computeGraphThroughput(SDFGraph graph, Method method, boolean selfLoopEdge) {
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
    Stopwatch timer = new Stopwatch();
    timer.start();

    Fraction period = null;

    // Set Gurobi as the default solver
    if (method == null) {
      method = Method.LinearProgram_Gurobi;
    }

    if (Activator.solverMethodRegistry.containsKey(method)) {
      SolverMethod solverMethod = Activator.solverMethodRegistry.get(method);
      period = solverMethod.computeNormalizedPeriod(graph);
    } else {
      // use the default method : GLPK
      System.err.println(method.toString() + " method is not available ! \nTrying to use " + METHOD_DEFAULT_VALUE.toString() + " ...");
      SolverMethod solverMethod = Activator.solverMethodRegistry.get(METHOD_DEFAULT_VALUE);
      // if the activator have not been executed yet, then instantiate the solverMethod manually ()
      if (solverMethod == null) {
        solverMethod = new PeriodicScheduleModel_ojAlgo();
      }
      period = solverMethod.computeNormalizedPeriod(graph);
    }

    timer.stop();
    System.out.println("Normalized period K= " + period + " computed in " + timer.toString());
    return period;
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
    // double w = (double) actor.getPropertyBean().getValue("executionPeriod");
    double k = ((Fraction) graph.getPropertyBean().getValue("normalizedPeriod")).doubleValue();
    double graphPeriod = k * ((Double) actor.getPropertyBean().getValue("normalizedRate")) * actor.getNbRepeatAsInteger();
    return graphPeriod;
  }

  /**
   * compute the start date of the first execution of each actor
   * 
   * @param graph
   *          SDF graph
   */
  public void computeActorsStartingTime(SDFGraph graph) {
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
