package org.preesm.algorithm.synthesis.schedule.iterator;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.HierarchicalSchedule;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.algorithm.synthesis.schedule.ScheduleUtil;
import org.preesm.model.pisdf.AbstractActor;

/**
 *
 *
 *
 * @author anmorvan
 *
 */
public class ScheduleAndTopologyIterator extends ScheduleIterator {

  public ScheduleAndTopologyIterator(final Schedule schedule) {
    super(schedule);
  }

  @Override
  public List<AbstractActor> createOrder(final Schedule schedule) {
    final SortedSet<AbstractActor> res = new TreeSet<>(ScheduleUtil.getScheduleComparator(schedule));
    new ActorScheduleSwitch(res).doSwitch(schedule);
    return new ArrayList<>(res);
  }

  /**
   *
   * @author anmorvan
   *
   */
  private class ActorScheduleSwitch extends ScheduleSwitch<Boolean> {

    final SortedSet<AbstractActor> orderedSet;

    public ActorScheduleSwitch(SortedSet<AbstractActor> treeSet) {
      this.orderedSet = treeSet;
    }

    @Override
    public Boolean caseHierarchicalSchedule(final HierarchicalSchedule object) {
      object.getChildren().forEach(this::doSwitch);
      return true;
    }

    @Override
    public Boolean caseActorSchedule(final ActorSchedule object) {
      final List<AbstractActor> actorList = object.getActorList();
      this.orderedSet.addAll(actorList);
      return true;
    }
  }
}
