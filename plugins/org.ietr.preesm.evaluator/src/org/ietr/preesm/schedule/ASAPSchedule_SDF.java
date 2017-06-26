package org.ietr.preesm.schedule;

import java.util.Hashtable;
import java.util.Map.Entry;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.preesm.core.scenario.PreesmScenario;

/**
 * @author hderoui
 *
 *         ASAP scheduler : Ghamarian + Lee + Hamza version
 *
 */
public class ASAPSchedule_SDF {
  private SimulationHelper                                         simulator;  // simulator helper
  private Double                                                   dur1Iter;   // duration of one iteration of a graph
  private Hashtable<Double, Hashtable<SDFAbstractVertex, Integer>> executions; // list of ready executions to finish

  /**
   * Schedule the graph using an ASAP schedule and return the duration of the graph iteration
   * 
   * @param graph
   *          SDF graph
   * @param scenario
   *          contains actors duration
   * @return
   */
  public double schedule(SDFGraph graph, PreesmScenario scenario) {

    // initialize the simulator
    this.simulator = new SimulationHelper(graph, scenario);
    this.dur1Iter = 0.;

    // initialize the 1st elements of the list
    executions = new Hashtable<Double, Hashtable<SDFAbstractVertex, Integer>>();
    this.initialzeList(graph);

    while (!executions.isEmpty()) {
      // pick the execution list with the earliest finish date
      double t = Double.MAX_VALUE;
      for (double keyT : executions.keySet())
        if (t >= keyT)
          t = keyT;

      // update the duration of the iteration
      if (dur1Iter < t)
        dur1Iter = t;

      // execute the list of executions
      Hashtable<SDFAbstractVertex, Integer> listTExec = executions.get(t);
      executions.remove(t);

      for (Entry<SDFAbstractVertex, Integer> execution : listTExec.entrySet()) {

        // produce n*prod data tokens
        this.simulator.produce(execution.getKey(), execution.getValue());

        // verify the target actors of the executed actor if they are ready to be executed
        for (SDFInterfaceVertex output : execution.getKey().getSinks()) {

          // execute n times the target actor if it is ready
          SDFAbstractVertex targetActor = execution.getKey().getAssociatedEdge(output).getTarget();
          int n = this.simulator.maxExecToCompleteAnIteration(targetActor);

          if (n > 0) {
            // consume N data tokens
            this.simulator.consume(targetActor, n);

            // set the start date
            this.simulator.setStartDate(targetActor, t);

            // set the finish date
            double finishDate = this.simulator.getStartDate(targetActor) + this.simulator.getActorDuration(targetActor);
            this.simulator.setfinishDate(targetActor, finishDate);

            // add the execution to the list
            if (executions.containsKey(finishDate)) {
              Hashtable<SDFAbstractVertex, Integer> listExec = executions.get(finishDate);
              if (listExec.containsKey(targetActor)) {
                int old = listExec.get(targetActor);
                listExec.put(targetActor, (old + n));
              } else {
                listExec.put(targetActor, n);
              }
            } else {
              executions.put(finishDate, new Hashtable<SDFAbstractVertex, Integer>());
              executions.get(finishDate).put(targetActor, n);
            }
          }
        }
      }
    }

    // check if the simulation is completed
    if (this.simulator.isIterationCompleted()) {
      System.out.println("Iteration complete !!");
    } else {
      System.err.println("Iteration not complete !!");
    }

    System.out.println("SDF Graph Scheduled in ");// + timerASAPSche.toString());
    return dur1Iter;
  }

  /**
   * Initialize the list of ready executions
   * 
   * @param g
   *          SDF graph
   */
  private void initialzeList(SDFGraph g) {
    // loop actors
    for (SDFAbstractVertex actor : g.vertexSet()) {
      // get the max n
      int n = this.simulator.maxExecToCompleteAnIteration(actor);
      // if ready
      if (n > 0) {
        // consume N data tokens
        this.simulator.consume(actor, n);
        // set the start date
        this.simulator.setStartDate(actor, 0.);
        // set the finish date
        double finishDate = this.simulator.getStartDate(actor) + this.simulator.getActorDuration(actor);
        this.simulator.setfinishDate(actor, finishDate);
        // add the execution to the list
        if (executions.containsKey(finishDate)) {
          executions.get(finishDate).put(actor, n);
        } else {
          executions.put(finishDate, new Hashtable<SDFAbstractVertex, Integer>());
          executions.get(finishDate).put(actor, n);
        }
      }
    }
  }

}
