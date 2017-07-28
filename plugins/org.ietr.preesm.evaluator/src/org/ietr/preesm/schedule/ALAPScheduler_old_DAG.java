package org.ietr.preesm.schedule;

import java.util.ArrayList;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.throughput.helpers.Stopwatch;

/**
 * @author hderoui
 *
 */
public class ALAPScheduler_old_DAG {
  // list of actor to execute
  public ArrayList<Actor> actorToExecute;
  public double           maxDate;

  // Schedule the graph : set the starting date and finish date
  /**
   * @param sg
   *          srSDF graph
   * @param ThConstDate
   *          throughput constraint
   * @return duration
   */
  public double schedule(SDFGraph sg, double ThConstDate) {
    // timer
    Stopwatch timerASAPSche = new Stopwatch();
    timerASAPSche.start();

    // set the maxDate constraint
    // maxDate = getMaxDate(sg);
    maxDate = ThConstDate;

    // initialize the 1st element of the list
    actorToExecute = new ArrayList<>();
    initializeList(sg);

    // execute all actors of the list
    while (!actorToExecute.isEmpty()) {
      // execute the actor
      actorToExecute.get(0).consume(-1);

      // verify if the target actors are ready
      for (Edge e : actorToExecute.get(0).InputEdges.values()) {
        if (is_ready(e.sourceActor)) {
          // consume 1 data tokens
          e.sourceActor.produce(-1);
          // set the finish date
          e.sourceActor.startDate = e.sourceActor.finishDate - e.sourceActor.duration;
          // System.out.println("Exec " + e.sourceActor.id +" ::"+ e.sourceActor.startDate+ " = " +e.sourceActor.finishDate+ " - " +e.sourceActor.duration);
          // add the execution to the list
          actorToExecute.add(e.sourceActor);
        }
      }

      // remove the current actor from the list
      actorToExecute.remove(0);
    }

    timerASAPSche.stop();
    System.out.println("DAG Graph Scheduled in " + timerASAPSche.toString());
    return timerASAPSche.value();
  }

  // function to initialize the list of ready actors to execute
  /**
   * @param g
   *          graph
   */
  private void initializeList(SDFGraph g) {
    // loop actors
    for (Actor a : g.actors.values()) {
      // if ready
      if (is_ready(a)) {
        // consume N data tokens
        a.produce(-1);
        if (a.finishDate == Double.POSITIVE_INFINITY) {
          a.finishDate = this.maxDate;
        }
        // set the finish date
        a.startDate = a.finishDate - a.duration;
        // System.out.println("Exec " + a.id +" ::"+ a.startDate+ " = " +a.finishDate+ " - " +a.duration);
        // add the execution to the list
        actorToExecute.add(a);
      }
    }
  }

  // define the max date (throughput max date constraint)
  /**
   * @param g
   *          graph
   * @return max date
   */
  private double getMaxDate(SDFGraph g) {
    double maxDate = 0;
    for (Actor a : g.actors.values()) {
      if (a.finishDate > maxDate) {
        maxDate = a.finishDate;
      }
    }
    System.out.println("THROUGHPUT CONSTRAINT = " + maxDate);
    return maxDate;
  }

  /**
   * @param a
   *          actor
   * @return true
   */
  public boolean is_ready(Actor a) {
    double maxFinishDate = Double.POSITIVE_INFINITY;
    if (a.executionCounter > 0) {
      return false;
    } else {
      boolean ready = true;
      for (Edge e : a.OutputEdges.values()) {
        if (e.delay == 0) {
          ready = false;
          break;
        } else {
          if (e.targetActor.startDate < maxFinishDate) {
            maxFinishDate = e.targetActor.startDate;
          }
        }
      }
      if (a.BaseActor.type != Actor.Type.OUTPUTINTERFACE) {
        a.finishDate = maxFinishDate;
      }
      return ready;
    }
  }

}
