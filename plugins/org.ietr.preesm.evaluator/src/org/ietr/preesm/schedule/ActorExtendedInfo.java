package org.ietr.preesm.schedule;

import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;

public class ActorExtendedInfo {

  public SDFAbstractVertex actor;
  public double            duration;
  public double            startDate;
  public double            finishDate;
  public double            nbExeution;
  public double            totalExecutions;

  public ActorExtendedInfo(SDFAbstractVertex actor, double duration, double startDate, double finishDate, double nbExeution, double totalExecutions) {
    this.actor = actor;
    this.duration = duration;
    this.startDate = startDate;
    this.finishDate = finishDate;
    this.nbExeution = nbExeution;
    this.totalExecutions = totalExecutions;
  }

}
