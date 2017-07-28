package org.ietr.preesm.schedule;

import java.util.ArrayList;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.throughput.helpers.GraphSimulationHelper;

/**
 * @author hderoui
 *
 */
public class ASAPScheduler_DAG {
  public GraphSimulationHelper         simulator;       // simulator helper
  public Double                        dur1Iter;        // duration of one iteration of a graph
  private ArrayList<SDFAbstractVertex> actorsToExecute; // list of actors to execute

  /**
   * Schedule the graph using an ASAP schedule and return the duration of the graph iteration
   * 
   * @param graph
   *          SDF graph
   * @param scenario
   *          contains actors duration
   * @return the duration of a graph iteration
   */
  public double schedule(SDFGraph graph, PreesmScenario scenario) {

    // initialize the simulator and the list of actor to execute
    this.simulator = new GraphSimulationHelper(graph, scenario);
    actorsToExecute = new ArrayList<SDFAbstractVertex>();
    this.initialzeList(graph);
    this.dur1Iter = 0.;

    while (!actorsToExecute.isEmpty()) {
      // execute the first actor of the list
      SDFAbstractVertex currentActor = actorsToExecute.get(0);
      this.simulator.produce(currentActor, 1);

      // update the duration of the iteration
      if (dur1Iter < this.simulator.getFinishDate(currentActor)) {
        dur1Iter = this.simulator.getFinishDate(currentActor);
      }

      // verify the target actors of the executed actor if they are ready to be executed
      for (SDFInterfaceVertex output : currentActor.getSinks()) {

        // execute 1 time the target actor if it is ready
        SDFAbstractVertex targetActor = currentActor.getAssociatedEdge(output).getTarget();
        if (this.isReady(targetActor)) {
          // consume 1 time
          this.simulator.consume(targetActor, 1);

          // set the finish date
          double finishDate = this.simulator.getStartDate(targetActor) + this.simulator.getActorDuration(targetActor);
          this.simulator.setfinishDate(targetActor, finishDate);

          // add the execution to the list
          actorsToExecute.add(targetActor);
        }
      }

      // remove the current actor from the list
      actorsToExecute.remove(0);
    }

    // check if the simulation is completed
    // if (this.simulator.isIterationCompleted()) {
    // System.out.println("Iteration complete !!");
    // } else {
    // System.err.println("Iteration not complete !!");
    // }

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
      // if ready
      if (this.isReady(actor)) {
        // consume N data tokens
        this.simulator.consume(actor, 1);
        // set the finish date
        double finishDate = this.simulator.getStartDate(actor) + this.simulator.getActorDuration(actor);
        this.simulator.setfinishDate(actor, finishDate);
        // add the execution to the list
        this.actorsToExecute.add(actor);
      }
    }
  }

  /**
   * verify if the actor is ready to be executed
   * 
   * @param actor
   *          actor to verify
   * @return true if it is ready
   */
  private boolean isReady(SDFAbstractVertex actor) {
    double maxStartDate = 0;
    if (this.simulator.getExecutionCounter(actor) > 0) {
      return false;
    } else {
      boolean ready = true;
      for (SDFInterfaceVertex input : actor.getSources()) {
        SDFEdge edge = actor.getAssociatedEdge(input);
        if (edge.getDelay().intValue() == 0) {
          ready = false;
          break;
        } else {
          if (this.simulator.getFinishDate(edge.getSource()) > maxStartDate) {
            maxStartDate = this.simulator.getFinishDate(edge.getSource());
          }
        }
      }
      this.simulator.setStartDate(actor, maxStartDate);
      return ready;
    }
  }

}
