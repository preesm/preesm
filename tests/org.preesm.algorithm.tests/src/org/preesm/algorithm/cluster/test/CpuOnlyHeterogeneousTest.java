package org.preesm.algorithm.cluster.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.preesm.algorithm.clustering.heuristics.HorizontalHeuristic;
import org.preesm.algorithm.clustering.identifier.ClusterBuilder;
import org.preesm.algorithm.clustering.identifier.ClusteringTask;
import org.preesm.algorithm.memory.allocation.tasks.MemoryScriptTask;
import org.preesm.algorithm.schedule.model.CommunicationActor;
import org.preesm.algorithm.schedule.model.ParallelHiearchicalSchedule;
import org.preesm.algorithm.schedule.model.SequentialActorSchedule;
import org.preesm.algorithm.synthesis.PreesmHeterogeneousSynthesisTask;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.schedule.algos.IScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.LegacyListScheduler;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.impl.PiGraphImpl;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.util.ScenarioUserFactory;
import org.preesm.model.slam.CPU;
import org.preesm.model.slam.ComNode;
import org.preesm.model.slam.ComponentHolder;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.DataLink;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.SlamFactory;
import org.preesm.model.slam.TimingType;
import org.preesm.model.slam.VLNV;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

// will be used to test the new PREESM workflow on CPU-orly test cases (though those CPUs might be heterogeneous !)
public class CpuOnlyHeterogeneousTest {
  final PiMMUserFactory PiMMFactory = org.preesm.model.pisdf.factory.PiMMUserFactory.instance;
  SlamFactory           SLAMFactory = SlamFactory.eINSTANCE;
  Scenario              scenario;
  PiGraph               algo;
  Design                homoDesign;
  List<AbstractActor>   listActors;
  ComponentInstance     cpu1;
  ComponentInstance     cpu2;
  ComNode               mem;

  @Before
  public void setup() {

    // ┌────────────────┐
    // │ f0 │
    // └┬──────────────┬┘
    // │ ┌▽─┐
    // │ │f1│
    // │ └┬─┘
    // ┌▽─┐ │
    // │f2│ │
    // └┬─┘ │
    // │┌─────────┐ │
    // ││ f5 │ │
    // │└△─┬─┬─┬─△┘ │
    // │ │ │ │ │┌┴─┐ │
    // │ │ │ │ ││f4│ │
    // │ │ │ │ │└──┘ │
    // │ │ │ │┌▽────┐ │
    // │ │ │ ││ c6 │ │
    // │ │ │ │└┬───┬┘ │
    // │ │ │ │┌▽──┐│ │
    // │ │ │ ││f10││ │
    // │ │ │ │└───┘│ │
    // │ │ │ │┌────▽─┐│
    // │ │ │ ││ f9 ││
    // │ │ │ │└──────┘│
    // │ │ │┌▽────┐ │
    // │ │ ││ f7 │ │
    // │ │ │└┬───┬┘ │
    // │ │ │┌▽──┐│ │
    // │ │ ││c12││ │
    // │ │ │└───┘│ │
    // │ │ │┌────▽─┐ │
    // │ │ ││ f11 │ │
    // │ │ │└──────┘ │
    // │ │┌▽─┐ │
    // │ ││f8│ │
    // │ │└──┘ │
    // ┌▽─┴────────────▽─┐
    // │ c3 │
    // └─────────────────┘

    // ---------- heterogeneous design ----------

    scenario = ScenarioUserFactory.createScenario();
    algo = PiMMFactory.createPiGraph();
    homoDesign = SLAMFactory.createDesign();

    algo.setUrl("");
    scenario.setAlgorithm(algo);
    scenario.setDesign(homoDesign);

    final CPU cpu = SlamFactory.eINSTANCE.createCPU();
    final VLNV vlnvcpu = SlamFactory.eINSTANCE.createVLNV();
    cpu.setVlnv(vlnvcpu);

    cpu1 = SlamFactory.eINSTANCE.createComponentInstance();
    cpu1.setComponent(cpu);
    cpu1.setInstanceName("cpu1");
    cpu1.setHardwareId(0);

    cpu2 = SlamFactory.eINSTANCE.createComponentInstance();
    cpu2.setComponent(cpu);
    cpu2.setInstanceName("cpu2");
    cpu2.setHardwareId(0);

    mem = SLAMFactory.createComNode();
    final ComponentInstance mem1 = SLAMFactory.createComponentInstance();
    mem1.setComponent(mem);
    mem1.setInstanceName("shared_mem");
    mem1.setHardwareId(2);

    homoDesign.getComponentInstances().add(cpu1);
    homoDesign.getComponentInstances().add(cpu2);
    scenario.getSimulationInfo().setMainComNode(mem1);
    scenario.getSimulationInfo().setMainOperator(cpu1);

    final DataLink link1 = SLAMFactory.createDataLink();
    link1.setDirected(false);
    link1.setSourceComponentInstance(cpu1);
    link1.setDestinationComponentInstance(mem1);
    link1.setUuid("4");
    homoDesign.getLinks().add(link1);

    final DataLink link2 = SLAMFactory.createDataLink();
    link2.setDirected(false);
    link2.setSourceComponentInstance(cpu2);
    link2.setDestinationComponentInstance(mem1);
    link2.setUuid("5");
    homoDesign.getLinks().add(link2);

    final ComponentHolder CmpHolder = SLAMFactory.createComponentHolder();
    CmpHolder.getComponents().add(cpu);
    homoDesign.setComponentHolder(CmpHolder);

    listActors = new BasicEList<>();

    for (int i = 0; i < 13; i++) {
      final Actor a = PiMMFactory.createActor("actor" + i);
      final Refinement r = PiMMFactory.createCHeaderRefinement(); // ou pisdf refinement ??
      a.setRefinement(r);
      listActors.add(a);
      algo.addActor(a);

      scenario.getTimings().setExecutionTime(a, cpu1.getComponent(), 10);
      scenario.getTimings().setExecutionTime(a, cpu2.getComponent(), 10);
      scenario.getTimings().setTiming(a, cpu1.getComponent(), TimingType.INITIATION_INTERVAL, "10");
      scenario.getTimings().setTiming(a, cpu2.getComponent(), TimingType.INITIATION_INTERVAL, "10");
    }

    ClusterTestHelper.createFifoLink(listActors.get(0), listActors.get(1), 10, 10, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(0), listActors.get(2), 10, 10, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(1), listActors.get(3), 10, 10, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(2), listActors.get(3), 10, 10, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(3), listActors.get(5), 10, 10, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(4), listActors.get(5), 10, 10, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(5), listActors.get(6), 10, 10, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(5), listActors.get(7), 10, 10, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(5), listActors.get(8), 10, 10, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(6), listActors.get(9), 10, 10, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(6), listActors.get(10), 10, 10, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(7), listActors.get(11), 10, 10, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(7), listActors.get(12), 10, 10, "int", algo);

    final EList<AbstractActor> cpu1Actors = new BasicEList<>();
    cpu1Actors.add(listActors.get(3));
    cpu1Actors.add(listActors.get(6));
    cpu1Actors.add(listActors.get(12)); // has both refinements available

    final EList<AbstractActor> cpu2Actors = new BasicEList<>();
    cpu2Actors.add(listActors.get(0));
    cpu2Actors.add(listActors.get(1));
    cpu2Actors.add(listActors.get(2));
    cpu2Actors.add(listActors.get(4));
    cpu2Actors.add(listActors.get(5));
    cpu2Actors.add(listActors.get(7));
    cpu2Actors.add(listActors.get(8));
    cpu2Actors.add(listActors.get(9));
    cpu2Actors.add(listActors.get(10));
    cpu2Actors.add(listActors.get(11));

    scenario.addConstraints(cpu1, cpu1Actors);
    scenario.addConstraints(cpu2, cpu2Actors);

  }

  @After
  public void teardown() {
    System.out.println("test fini");
  }

  @Test
  public void testBuildArchHierarchyGraph() {
    final HorizontalHeuristic hh = (HorizontalHeuristic) ClusteringTask
        .getHeuristic("heterogeneous");
    ClusterBuilder.buildHorizontalClusters(algo, scenario, homoDesign, hh, null, false); // default clustering

    // since there are only CPUs, there should be no clustering with the current clustering algorithm
    assertEquals(13, algo.getActors().size());
    final List<AbstractActor> listHierActors = algo.getActors().stream().filter(a -> a instanceof PiGraphImpl).toList();
    assertEquals(0, listHierActors.size());

    // check all actors have a rate of 10 in all their data ports
    algo.getActors().stream().flatMap(actor -> actor.getAllDataPorts().stream())
        .map(dp -> dp.getExpression().evaluateAsLong()).allMatch(t -> t == 10);

    // get all subactors whose rate is null and print them
    final var nullExpressions = algo.getActors().stream().filter(PiGraph.class::isInstance).map(PiGraph.class::cast)
        .flatMap(g -> g.getActors().stream()).flatMap(a -> a.getAllDataPorts().stream())
        .filter(dp -> dp.getExpression() == null).toList();
    for (final DataPort dp : nullExpressions) {
      System.out.print(dp.getName());
    }
    assertTrue(nullExpressions.isEmpty());

  }

  @Test
  public void testHeterogeneousScheduler() {
    final Map<String, Object> inputs = new LinkedHashMap<>();
    inputs.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, algo);
    inputs.put(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, homoDesign);
    inputs.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);

    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put("scheduler", "legacy");
    parameters.put("allocation", "legacy"); // for reference task

    final IProgressMonitor monitor = new NullProgressMonitor(); // constucteur au pif
    final String nodeName = ""; // ne semble pas utilisé pour de vrai donc raf
    final Workflow workflow = new Workflow(); // pas utilisé non plus donc raf

    final PreesmHeterogeneousSynthesisTask task = new PreesmHeterogeneousSynthesisTask();
    // ce n'est pas la valeur FALSE pour le mettre à false, mais du vide...
    parameters.put(MemoryScriptTask.PARAM_LOG, "");
    final Map<String, Object> res = task.execute(inputs, parameters, monitor, nodeName, workflow);

    // final PreesmSynthesisTask refTask = new PreesmSynthesisTask();
    // final Map<String, Object> reference = refTask.execute(inputs, parameters, monitor, nodeName, workflow);

    final IScheduler scheduler = new LegacyListScheduler();
    final SynthesisResult scheduleAndMap = scheduler.scheduleAndMap(algo, homoDesign, scenario);

    // Compare the output schedule : since there has been no clustering involved, they should be the same
    // Are they always this type ? Should this test support a variety of schedule types ?
    final ParallelHiearchicalSchedule outputSchedule = (ParallelHiearchicalSchedule) res.get("Schedule");
    final ParallelHiearchicalSchedule refSchedule = (ParallelHiearchicalSchedule) scheduleAndMap.schedule;

    // same number of PEs
    assertEquals(refSchedule.getScheduleTree().size(), outputSchedule.getScheduleTree().size());

    // same number of actors scheduled in each PE
    for (int i = 0; i < refSchedule.getScheduleTree().size(); i++) {
      final SequentialActorSchedule actorListReference = (SequentialActorSchedule) refSchedule.getScheduleTree().get(i);
      final SequentialActorSchedule actorListResult = (SequentialActorSchedule) outputSchedule.getScheduleTree().get(i);

      final int expectedNumberOfComms = 14;

      assertEquals(actorListReference.getActorList().size(),
          actorListResult.getActorList().size() - expectedNumberOfComms);
    }

    // check same schedule per PE
    for (int i = 0; i < refSchedule.getScheduleTree().size(); i++) {
      final EList<AbstractActor> actorListReference = ((SequentialActorSchedule) refSchedule.getScheduleTree().get(i))
          .getActorList();
      final List<AbstractActor> actorListResult = ((SequentialActorSchedule) outputSchedule.getScheduleTree().get(i))
          .getActorList().stream().filter(a -> !(a instanceof CommunicationActor)).toList();

      for (int actorIndex = 0; actorIndex < actorListReference.size(); actorIndex++) {
        assertEquals(actorListReference.get(actorIndex), actorListResult.get(actorIndex));
      }
    }
  }

}
