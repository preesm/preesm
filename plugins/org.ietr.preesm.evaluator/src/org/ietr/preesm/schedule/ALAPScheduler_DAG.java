package org.ietr.preesm.schedule;

import java.util.ArrayList;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.preesm.throughput.helpers.GraphSimulationHelper;
import org.ietr.preesm.throughput.helpers.Stopwatch;

/**
 * @author hderoui
 *
 */
public class ALAPScheduler_DAG {
  public GraphSimulationHelper         simulator;       // simulator helper
  private Double                       maxDate;         // throughput constraint
  private ArrayList<SDFAbstractVertex> actorsToExecute; // list of actors to execute

  /**
   * Schedule the graph using an ASAP schedule and return the duration of the graph iteration
   * 
   * @param graph
   *          SDF graph
   * @param simulator
   *          graph simulator helper
   * @param ThConstDate
   *          throughput constraint
   * @return the duration of a graph iteration
   */
  public double schedule(SDFGraph graph, GraphSimulationHelper simulator, double ThConstDate) {
    Stopwatch timer = new Stopwatch();
    timer.start();

    // initialize the simulator and the list of actor to execute
    this.simulator = simulator;
    this.maxDate = ThConstDate;
    actorsToExecute = new ArrayList<SDFAbstractVertex>();
    this.initialzeList(graph);

    while (!actorsToExecute.isEmpty()) {
      // execute the first actor of the list
      SDFAbstractVertex currentActor = actorsToExecute.get(0);
      this.simulator.consume(currentActor, -1);

      // verify the target actors of the executed actor if they are ready to be executed
      for (SDFInterfaceVertex input : currentActor.getSources()) {

        // execute 1 time the target actor if it is ready
        SDFAbstractVertex sourceActor = currentActor.getAssociatedEdge(input).getSource();
        if (this.isReady(sourceActor)) {
          // consume 1 time
          this.simulator.produce(sourceActor, -1);

          // set the finish date
          double startDate = this.simulator.getFinishDate(sourceActor) - this.simulator.getActorDuration(sourceActor);
          this.simulator.setStartDate(sourceActor, startDate);

          // add the execution to the list
          actorsToExecute.add(sourceActor);
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

    timer.stop();
    System.out.println("SDF Graph Scheduled in " + timer.toString());
    return maxDate;
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
        this.simulator.produce(actor, -1);
        // set the finish date
        double startDate = this.simulator.getFinishDate(actor) - this.simulator.getActorDuration(actor);
        this.simulator.setStartDate(actor, startDate);
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
    double maxFinishDate = this.maxDate;
    if (this.simulator.getExecutionCounter(actor) > 0) {
      return false;
    } else {
      boolean ready = true;
      for (SDFInterfaceVertex output : actor.getSinks()) {
        SDFEdge edge = actor.getAssociatedEdge(output);
        if (edge.getDelay().intValue() == 0) {
          ready = false;
          break;
        } else {
          if (this.simulator.getStartDate(edge.getTarget()) < maxFinishDate) {
            maxFinishDate = this.simulator.getStartDate(edge.getTarget());
          }
        }
      }
      SDFAbstractVertex baseActor = (SDFAbstractVertex) actor.getPropertyBean().getValue("baseActor");
      if (!(baseActor instanceof SDFSinkInterfaceVertex)) {
        this.simulator.setfinishDate(actor, maxFinishDate);
      }
      return ready;
    }
  }

}
