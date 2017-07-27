package org.ietr.preesm.schedule;

import java.util.ArrayList;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.throughput.helpers.Stopwatch;

/**
 * @author hderoui
 *
 */
public class ALAPScheduler_DAG {
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

  // public int is_ready(Actor a){
  // // initialize the counter with a max value = RV - counter
  // double maxFinishDate = Double.POSITIVE_INFINITY;
  // int maxExecutions = a.repetitionFactor - a.executionCounter;
  // if(maxExecutions <= 0){
  // return 0;
  // }else{
  // for(Edge e : a.OutputEdges.values()){
  // int n = (int) Math.floor(e.delay / e.cons);
  // double d = e.targetActor.startDate;
  // // if n = 0, it means that the actor is not ready to be fired
  // if(n < maxExecutions) maxExecutions = n;
  // if(d < maxFinishDate) maxFinishDate = d;
  // }
  // if(a.type != Actor.Type.OUTPUTINTERFACE)
  // a.finishDate = maxFinishDate;
  // return maxExecutions;
  // }
  // }

  // // alap schedule for dag graph
  //
  // // list of ready actors
  //
  // // list of executed actors
  //
  //
  // // function for ALAP schedule
  // public double schedule(){
  // // need a deadline to respect
  // /*
  // * loop output interfaces to define the deadline
  // *
  // * case of DAG what we can do : (ASAP)
  // * loop all the actors and add those without any inputs edges to the
  // initial list
  // * Schedule the graph by a DFS the same as the traditional schedule
  // *
  // * case of DAG what we can do : (ALAP)
  // * loop all the actors and add those without any outputs edges to the
  // initial list
  // * and set their start date to double.maxvalue
  // *
  // * add the interface too
  // * then schedule the graph like the traditional schedule
  // * (set an array to check if an actor was scheduled or not yet for both
  // ASAP and ALAP)
  // *
  // *
  // *
  // *
  // * */
  //
  //
  // // step 1: initialize the first list
  // // since we are in the case of a DAG its more simple :
  // // we add all actors without output interface
  // // if actor is not an interface then set its start date to max value of a
  // double
  //
  //
  // // step
  //
  //
  // return 0;
  // }
  //
  // // function to initialize the list of actors
  // private void initializeListOfReadyActors(){
  // //initialize the list
  //
  //
  // }
}
