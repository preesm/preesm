package org.preesm.algorithm.synthesis;

import java.util.Comparator;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.ParallelHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.algorithm.schedule.model.SequentialActorSchedule;
import org.preesm.algorithm.synthesis.schedule.ScheduleUtil;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.VertexPath;

/**
 *
 * @author anmorvan
 *
 */
public class ScheduleComparatorTest {

  /**
   *
   */
  @Test
  public void testComparator() {
    final Pair<ParallelHiearchicalSchedule, PiGraph> createTestData = createTestData();
    final PiGraph graph = createTestData.getRight();
    final ParallelHiearchicalSchedule schedule = createTestData.getLeft();
    final AbstractActor actorC = VertexPath.lookup(graph, "C");

    final Actor actorE = PiMMUserFactory.instance.createActor("E");
    graph.addActor(actorE);
    final ActorSchedule actorSched = (ActorSchedule) schedule.getScheduleTree().get(1);
    actorSched.getActorList().add(0, actorE);

    final Comparator<AbstractActor> cmp = ScheduleUtil.getScheduleComparator(schedule);

    final int compare = cmp.compare(actorC, actorE);
    System.out.println(compare);
  }

  @Test
  public void test4() {
    final Pair<ParallelHiearchicalSchedule, PiGraph> createTestData = createTestData();
    final ParallelHiearchicalSchedule sched = createTestData.getLeft();

    final Actor actorE = PiMMUserFactory.instance.createActor("E");
    final ActorSchedule schedule = (ActorSchedule) sched.getScheduleTree().get(1);
    schedule.getActorList().add(0, actorE);

    System.out.println(sched.shortPrint());

    final Comparator<AbstractActor> scheduleComparator = ScheduleUtil.getScheduleComparator(sched);
    final int compare = scheduleComparator.compare(schedule.getActorList().get(1), schedule.getActorList().get(0));
    System.out.println(compare);

    // StringBuilder sb = new StringBuilder();
    // final ScheduleIterator simpleScheduleIterator = new ScheduleAndTopologyIterator(sched);
    // simpleScheduleIterator.forEachRemaining(a -> sb.append(a.getName()));
    // assertEquals("AECBD", sb.toString());

  }

  private Pair<ParallelHiearchicalSchedule, PiGraph> createTestData() {
    final Actor actorA = PiMMUserFactory.instance.createActor("A");
    final DataOutputPort aOut1 = PiMMUserFactory.instance.createDataOutputPort("A.out1");
    final DataOutputPort aOut2 = PiMMUserFactory.instance.createDataOutputPort("A.out2");
    actorA.getDataOutputPorts().add(aOut1);
    actorA.getDataOutputPorts().add(aOut2);

    final Actor actorB = PiMMUserFactory.instance.createActor("B");
    final DataInputPort bIn = PiMMUserFactory.instance.createDataInputPort("B.in");
    final DataOutputPort bOut = PiMMUserFactory.instance.createDataOutputPort("B.out");
    actorB.getDataInputPorts().add(bIn);
    actorB.getDataOutputPorts().add(bOut);

    final Actor actorC = PiMMUserFactory.instance.createActor("C");
    final DataInputPort cIn = PiMMUserFactory.instance.createDataInputPort("C.in");
    final DataOutputPort cOut = PiMMUserFactory.instance.createDataOutputPort("C.out");
    actorC.getDataInputPorts().add(cIn);
    actorC.getDataOutputPorts().add(cOut);

    final Actor actorD = PiMMUserFactory.instance.createActor("D");
    final DataInputPort dIn1 = PiMMUserFactory.instance.createDataInputPort("D.in1");
    final DataInputPort dIn2 = PiMMUserFactory.instance.createDataInputPort("D.in2");
    actorD.getDataInputPorts().add(dIn1);
    actorD.getDataInputPorts().add(dIn2);

    final Fifo f1 = PiMMUserFactory.instance.createFifo(aOut1, bIn, "void");
    final Fifo f2 = PiMMUserFactory.instance.createFifo(aOut2, cIn, "void");
    final Fifo f3 = PiMMUserFactory.instance.createFifo(bOut, dIn1, "void");
    final Fifo f4 = PiMMUserFactory.instance.createFifo(cOut, dIn2, "void");

    final PiGraph graph = PiMMUserFactory.instance.createPiGraph();
    graph.addActor(actorA);
    graph.addActor(actorB);
    graph.addActor(actorC);
    graph.addActor(actorD);
    graph.addFifo(f1);
    graph.addFifo(f2);
    graph.addFifo(f3);
    graph.addFifo(f4);

    final SequentialActorSchedule core0 = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
    core0.getActorList().add(actorA);
    core0.getActorList().add(actorD);

    final SequentialActorSchedule core1 = ScheduleFactory.eINSTANCE.createSequentialActorSchedule();
    core1.getActorList().add(actorC);
    core1.getActorList().add(actorB);

    final ParallelHiearchicalSchedule sched = ScheduleFactory.eINSTANCE.createParallelHiearchicalSchedule();
    sched.getChildren().add(core0);
    sched.getChildren().add(core1);
    return Pair.of(sched, graph);
  }
}
