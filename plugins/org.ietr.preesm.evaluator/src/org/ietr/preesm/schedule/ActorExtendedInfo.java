package org.ietr.preesm.schedule;

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;

/**
 * @author HDeroui
 * 
 *         contains additional information about the actor. Used essentially in the simulation process.
 */
public class ActorExtendedInfo {

  public SDFAbstractVertex actor;
  public double            duration;
  public double            startDate;
  public double            finishDate;
  public int               nbExeution;
  public int               executionsCounter;

  /**
   * @param actor
   *          SDF actor
   * @param duration
   *          duration of the actor
   * @param startDate
   *          the start date of an execution of the actor
   * @param finishDate
   *          the finish date of an execution of the actor
   * @param nbExeution
   *          the number of allowed executions
   * @param totalExecutions
   *          the number of executions done during the simulation
   */
  public ActorExtendedInfo(SDFAbstractVertex actor, double duration, double startDate, double finishDate, int nbExeution, int totalExecutions) {
    this.actor = actor;
    this.duration = duration;
    this.startDate = startDate;
    this.finishDate = finishDate;
    this.nbExeution = nbExeution;
    this.executionsCounter = totalExecutions;
  }

}
