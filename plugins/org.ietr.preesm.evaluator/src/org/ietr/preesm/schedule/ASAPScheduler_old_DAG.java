package org.ietr.preesm.schedule;

import java.util.ArrayList;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.throughput.helpers.Stopwatch;

/**
 * @author hderoui
 *
 */
public class ASAPScheduler_old_DAG {

  // list of actor to execute
  public Double           iterDur;
  public ArrayList<Actor> actorToExecute;

  // Schedule the graph : set the starting date and finish date
  /**
   * @param sg
   *          srSDF graph
   * @return duration
   */
  public double schedule(SDFGraph sg) {
    // timer
    Stopwatch timerASAPSche = new Stopwatch();
    timerASAPSche.start();

    // initialize the 1st element of the list
    actorToExecute = new ArrayList<>();
    initializeList(sg);
    this.iterDur = 0.;

    // execute all actors of the list
    while (!actorToExecute.isEmpty()) {
      // execute the actor
      actorToExecute.get(0).produce(1);

      // update the duration of 1 iteration
      if (this.iterDur < actorToExecute.get(0).finishDate) {
        this.iterDur = actorToExecute.get(0).finishDate;
      }

      // verify if the target actors are ready
      for (Edge e : actorToExecute.get(0).OutputEdges.values()) {
        if (is_ready(e.targetActor)) {
          // consume 1 data tokens
          e.targetActor.consume(1);
          // set the finish date
          e.targetActor.finishDate = e.targetActor.startDate + e.targetActor.duration;
          // add the execution to the list
          actorToExecute.add(e.targetActor);
        }
      }

      // remove the current actor from the list
      actorToExecute.remove(0);
    }

    timerASAPSche.stop();
    System.out.println("DAG Graph Scheduled in " + timerASAPSche.toString());
    return timerASAPSche.value();
  }

  // function to initialize the list of ready actors to be executed
  private void initializeList(SDFGraph g) {
    // loop actors
    for (Actor a : g.actors.values()) {
      // if ready
      if (is_ready(a)) {
        // consume N data tokens
        a.consume(1);
        // set the finish date
        a.finishDate = a.startDate + a.duration;
        // add the execution to the list
        actorToExecute.add(a);
      }
    }
  }

  // test if an actor is ready to be executed one time
  /**
   * @param a
   *          actor
   * @return true
   */
  public boolean is_ready(Actor a) {
    double maxStartDate = 0;
    if (a.executionCounter > 0) {
      return false;
    } else {
      boolean ready = true;
      for (Edge e : a.InputEdges.values()) {
        if (e.delay == 0) {
          ready = false;
          break;
        } else {
          if (e.sourceActor.finishDate > maxStartDate) {
            maxStartDate = e.sourceActor.finishDate;
          }
        }
      }
      a.startDate = maxStartDate;
      return ready;
    }
  }

}
