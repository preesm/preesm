package org.preesm.codegen.model.generator2.clustering;

import java.util.LinkedList;
import java.util.List;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.AbstractActor;

public class APGANScheduleOrderManager extends ScheduleSwitch<List<AbstractActor>> {

  @Override
  public List<AbstractActor> caseHierarchicalSchedule(HierarchicalSchedule hierSched) {
    final List<AbstractActor> result = new LinkedList<>();
    hierSched.getChildren().forEach(sched -> result.addAll(doSwitch(sched)));
    return result;
  }

  @Override
  public List<AbstractActor> caseActorSchedule(ActorSchedule actorSched) {
    return actorSched.getActorList().stream().map(PreesmCopyTracker::getSource).toList();
  }

}
