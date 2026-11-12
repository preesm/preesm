package org.preesm.algorithm.synthesis.schedule.algos;

import java.util.Iterator;
import java.util.List;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PassiveActor;

public class RemovePassiveActorsInMappingScheduling extends ScheduleSwitch<Boolean> {

  Schedule schedule;
  Mapping  mapping;

  RemovePassiveActorsInMappingScheduling(Schedule s, Mapping m) {
    schedule = s;
    mapping = m;
  }

  public void remove() {
    doSwitch(schedule);
  }

  @Override
  public Boolean caseHierarchicalSchedule(HierarchicalSchedule hierSched) {

    hierSched.getChildren().forEach(this::doSwitch);

    return true;
  }

  @Override
  public Boolean caseActorSchedule(ActorSchedule sched) {

    final List<AbstractActor> actors = sched.getActorList();

    final Iterator<AbstractActor> it = actors.iterator();
    while (it.hasNext()) {
      final AbstractActor a = it.next();
      if (a instanceof final PassiveActor pa && pa.getBufferSize() != pa.getRealBufferSize()) {
        mapping.getMappings().removeKey(pa);
        it.remove();
      }
    }
    return true;
  }

}
