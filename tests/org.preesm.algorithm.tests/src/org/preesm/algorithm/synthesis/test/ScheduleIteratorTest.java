/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2023) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020 - 2022)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.algorithm.synthesis.test;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.preesm.algorithm.schedule.model.ActorSchedule;
import org.preesm.algorithm.schedule.model.ParallelHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.ScheduleFactory;
import org.preesm.algorithm.schedule.model.SequentialActorSchedule;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
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
public class ScheduleIteratorTest {

  @Test
  public void test1() {
    final Pair<PiGraph, ParallelHiearchicalSchedule> createSchedule = createSchedule();
    final ParallelHiearchicalSchedule sched = createSchedule.getRight();
    final List<AbstractActor> simpleOrderedList = ScheduleUtil.getAllReferencedActors(sched);
    StringBuilder sb = new StringBuilder();
    simpleOrderedList.forEach(a -> sb.append(a.getName()));
    assertEquals("ADCB", sb.toString());
  }

  @Test
  public void test2() {
    final Pair<PiGraph, ParallelHiearchicalSchedule> createSchedule = createSchedule();
    final PiGraph graph = createSchedule.getLeft();
    final ParallelHiearchicalSchedule sched = createSchedule.getRight();
    final List<
        AbstractActor> orderedList = new ScheduleOrderManager(graph, sched).buildScheduleAndTopologicalOrderedList();
    StringBuilder sb = new StringBuilder();
    orderedList.forEach(a -> sb.append(a.getName()));
    assertEquals("ACBD", sb.toString());
  }

  @Test
  public void test3() {
    final Pair<PiGraph, ParallelHiearchicalSchedule> createSchedule = createSchedule();
    final PiGraph graph = createSchedule.getLeft();
    final ParallelHiearchicalSchedule sched = createSchedule.getRight();

    final Actor actorE = PiMMUserFactory.instance.createActor("E");
    final ActorSchedule schedule = (ActorSchedule) sched.getScheduleTree().get(1);
    schedule.getActorList().add(actorE);

    final List<
        AbstractActor> orderedList = new ScheduleOrderManager(graph, sched).buildScheduleAndTopologicalOrderedList();
    StringBuilder sb = new StringBuilder();
    orderedList.forEach(a -> sb.append(a.getName()));
    assertEquals("ACBDE", sb.toString());

  }

  @Test
  public void test4() {
    final Pair<PiGraph, ParallelHiearchicalSchedule> createSchedule = createSchedule();
    final PiGraph graph = createSchedule.getLeft();
    final ParallelHiearchicalSchedule sched = createSchedule.getRight();

    final Actor actorE = PiMMUserFactory.instance.createActor("E");
    final ActorSchedule schedule = (ActorSchedule) sched.getScheduleTree().get(1);
    schedule.getActorList().add(0, actorE);

    StringBuilder sb = new StringBuilder();
    final List<
        AbstractActor> orderedList = new ScheduleOrderManager(graph, sched).buildScheduleAndTopologicalOrderedList();
    orderedList.forEach(a -> sb.append(a.getName()));
    assertEquals("AECBD", sb.toString());

  }

  @Test
  public void testPredecessors() {
    final Pair<PiGraph, ParallelHiearchicalSchedule> createSchedule = createSchedule();
    final PiGraph graph = createSchedule.getLeft();
    final ParallelHiearchicalSchedule sched = createSchedule.getRight();
    final ScheduleOrderManager scheduleOrderManager = new ScheduleOrderManager(graph, sched);

    List<AbstractActor> predecessors;
    predecessors = scheduleOrderManager.getAllPredecessors(VertexPath.lookup(graph, "A"));
    assertEquals(true, predecessors.isEmpty());

    predecessors = scheduleOrderManager.getAllPredecessors(VertexPath.lookup(graph, "B"));
    final StringBuilder sb = new StringBuilder();
    predecessors.forEach(a -> sb.append(a.getName()));
    assertEquals("AC", sb.toString());

    predecessors = scheduleOrderManager.getAllPredecessors(VertexPath.lookup(graph, "D"));
    final StringBuilder sb2 = new StringBuilder();
    predecessors.forEach(a -> sb2.append(a.getName()));
    assertEquals("BCA", sb2.toString());

  }

  @Test
  public void testSuccessors() {
    final Pair<PiGraph, ParallelHiearchicalSchedule> createSchedule = createSchedule();
    final PiGraph graph = createSchedule.getLeft();
    final ParallelHiearchicalSchedule sched = createSchedule.getRight();
    final ScheduleOrderManager scheduleOrderManager = new ScheduleOrderManager(graph, sched);

    List<AbstractActor> predecessors;
    predecessors = scheduleOrderManager.getAllSuccessors(VertexPath.lookup(graph, "D"));
    assertEquals(true, predecessors.isEmpty());

    predecessors = scheduleOrderManager.getAllSuccessors(VertexPath.lookup(graph, "B"));
    final StringBuilder sb = new StringBuilder();
    predecessors.forEach(a -> sb.append(a.getName()));
    assertEquals("D", sb.toString());

    predecessors = scheduleOrderManager.getAllSuccessors(VertexPath.lookup(graph, "A"));
    final StringBuilder sb2 = new StringBuilder();
    predecessors.forEach(a -> sb2.append(a.getName()));
    assertEquals("BCD", sb2.toString());

  }

  private Pair<PiGraph, ParallelHiearchicalSchedule> createSchedule() {
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
    return Pair.of(graph, sched);
  }

}
