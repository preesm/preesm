package org.preesm.algorithm.cluster.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.preesm.algorithm.clustering.ActorMerger;
import org.preesm.algorithm.clustering.ClusterBuilder;
import org.preesm.algorithm.clustering.MergingHeuristic;
import org.preesm.algorithm.clustering.MinimalMergingHeuristic;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memory.allocation.tasks.MemoryScriptTask;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.PreesmHeterogeneousSynthesisTask;
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
import org.preesm.model.slam.FPGA;
import org.preesm.model.slam.SlamFactory;
import org.preesm.model.slam.TimingType;
import org.preesm.model.slam.VLNV;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

public class HeterogeneousTest {

  final PiMMUserFactory PiMMFactory = org.preesm.model.pisdf.factory.PiMMUserFactory.instance;
  SlamFactory           SLAMFactory = SlamFactory.eINSTANCE;
  Scenario              scenario;
  PiGraph               algo;
  Design                heteroDesign;
  List<AbstractActor>   listActors;
  ComponentInstance     cpu1;
  ComponentInstance     fpga1;
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
    heteroDesign = SLAMFactory.createDesign();

    algo.setUrl("");
    scenario.setAlgorithm(algo);
    scenario.setDesign(heteroDesign);

    cpu1 = SlamFactory.eINSTANCE.createComponentInstance();
    final CPU cpu = SlamFactory.eINSTANCE.createCPU();
    final VLNV vlnvcpu = SlamFactory.eINSTANCE.createVLNV();
    cpu.setVlnv(vlnvcpu);
    cpu1.setComponent(cpu);
    cpu1.setInstanceName("cpu1");
    cpu1.setHardwareId(0);

    fpga1 = SlamFactory.eINSTANCE.createComponentInstance();
    final FPGA fpga = SlamFactory.eINSTANCE.createFPGA();
    // final CPU fpga = SlamFactory.eINSTANCE.createCPU();
    final VLNV vlnvfpga = SlamFactory.eINSTANCE.createVLNV();
    fpga.setVlnv(vlnvfpga);
    fpga1.setComponent(fpga);
    fpga1.setInstanceName("fpga1");
    fpga1.setHardwareId(1);

    mem = SLAMFactory.createComNode();
    final ComponentInstance mem1 = SLAMFactory.createComponentInstance();
    mem1.setComponent(mem);
    mem1.setInstanceName("shared_mem");
    mem1.setHardwareId(2);

    heteroDesign.getComponentInstances().add(cpu1);
    heteroDesign.getComponentInstances().add(fpga1);
    scenario.getSimulationInfo().setMainComNode(mem1);
    scenario.getSimulationInfo().setMainOperator(cpu1);

    final DataLink link1 = SLAMFactory.createDataLink();
    link1.setDirected(false);
    link1.setSourceComponentInstance(cpu1);
    link1.setDestinationComponentInstance(mem1);
    link1.setUuid("4");
    heteroDesign.getLinks().add(link1);

    final DataLink link2 = SLAMFactory.createDataLink();
    link2.setDirected(false);
    link2.setSourceComponentInstance(fpga1);
    link2.setDestinationComponentInstance(mem1);
    link2.setUuid("5");
    heteroDesign.getLinks().add(link2);

    final ComponentHolder CmpHolder = SLAMFactory.createComponentHolder();
    CmpHolder.getComponents().add(fpga);
    CmpHolder.getComponents().add(cpu);
    heteroDesign.setComponentHolder(CmpHolder);

    listActors = new BasicEList<>();

    for (int i = 0; i < 13; i++) {
      final Actor a = PiMMFactory.createActor("actor" + i);
      final Refinement r = PiMMFactory.createCHeaderRefinement(); // ou pisdf refinement ??
      a.setRefinement(r);
      listActors.add(a);
      algo.addActor(a);

      scenario.getTimings().setExecutionTime(a, cpu1.getComponent(), 10);
      scenario.getTimings().setExecutionTime(a, fpga1.getComponent(), 10);
      scenario.getTimings().setTiming(a, cpu1.getComponent(), TimingType.INITIATION_INTERVAL, "10");
      scenario.getTimings().setTiming(a, fpga1.getComponent(), TimingType.INITIATION_INTERVAL, "10");
    }

    ClusterTestHelper.createFifoLink(listActors.get(0), listActors.get(1), 1, 1, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(0), listActors.get(2), 1, 1, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(1), listActors.get(3), 1, 1, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(2), listActors.get(3), 1, 1, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(3), listActors.get(5), 1, 1, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(4), listActors.get(5), 1, 1, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(5), listActors.get(6), 1, 1, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(5), listActors.get(7), 1, 1, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(5), listActors.get(8), 1, 1, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(6), listActors.get(9), 1, 1, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(6), listActors.get(10), 1, 1, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(7), listActors.get(11), 1, 1, "int", algo);
    ClusterTestHelper.createFifoLink(listActors.get(7), listActors.get(12), 1, 1, "int", algo);

    final EList<AbstractActor> cpuActors = new BasicEList<>();
    cpuActors.add(listActors.get(3));
    cpuActors.add(listActors.get(6));
    cpuActors.add(listActors.get(12)); // has both refinements available

    final EList<AbstractActor> fpgaActors = new BasicEList<>();
    fpgaActors.add(listActors.get(0));
    fpgaActors.add(listActors.get(1));
    fpgaActors.add(listActors.get(2));
    fpgaActors.add(listActors.get(4));
    fpgaActors.add(listActors.get(5));
    fpgaActors.add(listActors.get(7));
    fpgaActors.add(listActors.get(8));
    fpgaActors.add(listActors.get(9));
    fpgaActors.add(listActors.get(10));
    fpgaActors.add(listActors.get(11));

    scenario.addConstraints(cpu1, cpuActors);
    scenario.addConstraints(fpga1, fpgaActors);
  }

  @After
  public void teardown() {
    System.out.println("test fini");
  }

  @Test
  public void testBuildArchHierarchyGraph() {
    ClusterBuilder.buildArchHierarchyGraph(algo, scenario, ""); // default clustering heuristic

    assertEquals(7, algo.getActors().size());
    final List<AbstractActor> listHierActors = algo.getActors().stream().filter(a -> a instanceof PiGraphImpl).toList();
    assertEquals(4, listHierActors.size()); // 4 clusters : 0-1-2, 4-5-7-8, and 9 and 10 that have only one actor

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
  public void testBuildMergeList() {

    final AbstractActor seed = listActors.get(5);
    final Set<AbstractActor> visitedActors = new HashSet<>();
    final MergingHeuristic heuristic = new MinimalMergingHeuristic();
    final Set<AbstractActor> res = ClusterBuilder.buildMergeList(seed, scenario, fpga1.getComponent(), visitedActors,
        heuristic);

    assertEquals(5, res.size());
    assertTrue(res.contains(listActors.get(5)));
    assertTrue(res.contains(listActors.get(7)));
    assertTrue(res.contains(listActors.get(8)));
    assertTrue(res.contains(listActors.get(11)));
    assertTrue(res.contains(listActors.get(4)));
    assertFalse(res.contains(listActors.get(12)));
  }

  @Test
  public void testMergeActors() {
    final Set<AbstractActor> listActorsToMerge = new HashSet<>();
    listActorsToMerge.add(listActors.get(5));
    listActorsToMerge.add(listActors.get(7));
    listActorsToMerge.add(listActors.get(11));
    listActorsToMerge.add(listActors.get(8));

    ActorMerger.mergeActors(algo, listActorsToMerge, "FPGA_Actors");

    assertEquals(10, algo.getActors().size());
    // 13 original actors + 1 new hierarchical actor - 4 merged actors

    final List<
        AbstractActor> hetero_graph = algo.getActors().stream().filter(e -> e.getName().equals("FPGA_Actors")).toList();
    assertEquals(1, hetero_graph.size());
    assertTrue(hetero_graph.getFirst() instanceof PiGraph);
    assertEquals(8, ((PiGraph) hetero_graph.getFirst()).getActors().size()); // 4 actors + 4 data interfaces
    assertEquals(0, hetero_graph.getFirst().getConfigInputPorts().size());
    assertNotNull(listActors.get(6).getDataInputPorts().getFirst().getFifo().getSourcePort());
  }

  @Test
  public void testHeterogeneousScheduler() {
    final Map<String, Object> inputs = new LinkedHashMap<>();
    inputs.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, algo);
    inputs.put(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, heteroDesign);
    inputs.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);

    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put("scheduler", "legacy");

    final IProgressMonitor monitor = new NullProgressMonitor(); // constucteur au pif
    final String nodeName = ""; // ne semble pas utilisé pour de vrai donc raf
    final Workflow workflow = new Workflow(); // pas utilisé non plus donc raf

    final var task = new PreesmHeterogeneousSynthesisTask();

    // ce n'est pas la valeur FALSE pour le mettre à false, mais du vide...
    parameters.put(MemoryScriptTask.PARAM_LOG, "");
    final Map<String, Object> res = task.execute(inputs, parameters, monitor, nodeName, workflow);

    // vérifier que les éléments sont mappés où on le veut, et que la durée d'exécution est celle prévue

    final Schedule resSchedule = (Schedule) res.get("Schedule");
    final Mapping resMapping = (Mapping) res.get("Mapping");

  }

}
