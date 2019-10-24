package org.preesm.algorithm.synthesis.timer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.schedule.model.CommunicationActor;
import org.preesm.algorithm.schedule.model.ReceiveEndActor;
import org.preesm.algorithm.schedule.model.ReceiveStartActor;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.schedule.model.SendEndActor;
import org.preesm.algorithm.schedule.model.SendStartActor;
import org.preesm.algorithm.schedule.model.util.ScheduleSwitch;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SrdagActor;
import org.preesm.model.pisdf.UserSpecialActor;
import org.preesm.model.pisdf.util.PiMMSwitch;
import org.preesm.model.scenario.MemoryCopySpeedValue;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;

/**
 *
 * @author anmorvan
 *
 */
public class SimpleScheduleTimer {

  /**
   */
  public static final Map<AbstractActor, ExecutionTiming> computeTimings(final PiGraph pigraph, final Schedule schedule,
      final Mapping mapping, final Scenario scenario) {
    final Map<AbstractActor, ExecutionTiming> res = new LinkedHashMap<>();

    final ScheduleOrderManager scheduleOrderManager = new ScheduleOrderManager(pigraph, schedule);
    final List<AbstractActor> orderedActors = scheduleOrderManager.buildScheduleAndTopologicalOrderedList();

    final SimpleActorTiming timer = new SimpleActorTiming(mapping, scenario);

    for (final AbstractActor actor : orderedActors) {
      final long duration = timer.doSwitch(actor);

      final long startTime = scheduleOrderManager.getPredecessors(actor).stream()
          .mapToLong(a -> res.get(a).getEndTime()).max().orElse(0L);

      final ExecutionTiming executionTiming = new ExecutionTiming(actor, startTime, duration);
      res.put(actor, executionTiming);

    }

    return res;
  }

  /**
   *
   */
  static class SimpleActorTiming extends PiMMSwitch<Long> {

    private final Scenario scenario;
    private final Mapping  mapping;

    public SimpleActorTiming(final Mapping mapping, final Scenario scenario) {
      this.mapping = mapping;
      this.scenario = scenario;
    }

    /**
     * Special actors (i.e. communication actors) with dedicated timing switch.
     */
    @Override
    public Long defaultCase(EObject object) {
      if (object instanceof CommunicationActor) {
        return new SimpleCommunicationTiming(mapping, scenario).doSwitch(object);
      }
      throw new UnsupportedOperationException();
    }

    /**
     * Actors with refinements get their timings from the scenario
     */
    @Override
    public Long caseActor(Actor object) {
      final ComponentInstance operator = mapping.getSimpleMapping(object);
      return scenario.getTimings().evaluateTimingOrDefault(object, operator.getComponent());
    }

    /**
     * SRDAG Actors (init/end)
     */
    @Override
    public Long caseSrdagActor(SrdagActor object) {
      return 0L;
    }

    /**
     * Fork/Join/Broadcast/Roundbuffer
     */
    @Override
    public Long caseUserSpecialActor(UserSpecialActor object) {

      final long totalInRate = object.getDataInputPorts().stream()
          .mapToLong(port -> port.getPortRateExpression().evaluate()).sum();
      final long totalOutRate = object.getDataOutputPorts().stream()
          .mapToLong(port -> port.getPortRateExpression().evaluate()).sum();

      final long maxRate = Math.max(totalInRate, totalOutRate);

      final ComponentInstance operator = mapping.getSimpleMapping(object);
      final MemoryCopySpeedValue memTimings = scenario.getTimings().getMemTimings().get(operator.getComponent());

      final double timePerUnit = memTimings.getTimePerUnit();
      return (long) (((double) maxRate) * timePerUnit) + memTimings.getSetupTime();
    }

  }

  /**
   *
   */
  static class SimpleCommunicationTiming extends ScheduleSwitch<Long> {

    private final Scenario scenario;
    private final Mapping  mapping;

    public SimpleCommunicationTiming(final Mapping mapping, final Scenario scenario) {
      this.mapping = mapping;
      this.scenario = scenario;
    }

    @Override
    public Long caseSendEndActor(SendEndActor object) {
      return 0L;
    }

    @Override
    public Long caseReceiveStartActor(ReceiveStartActor object) {
      return 0L;
    }

    @Override
    public Long caseReceiveEndActor(ReceiveEndActor object) {
      return 0L;
    }

    @Override
    public Long caseSendStartActor(SendStartActor object) {
      return 0L;
    }
  }

}
