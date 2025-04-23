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
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.preesm.algorithm.clustering.ActorMerger;
import org.preesm.algorithm.clustering.ClusterBuilder;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.algorithm.clustering.MergingHeuristic;
import org.preesm.algorithm.clustering.MinimalMergingHeuristic;
import org.preesm.algorithm.schedule.fpga.AdfgOjalgoFpgaFifoEvaluator;
import org.preesm.algorithm.schedule.sdf.HeterogeneousScheduler;
import org.preesm.algorithm.synthesis.PreesmSynthesisTask;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.algorithm.synthesis.schedule.algos.FpgaScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.IScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.SimpleScheduler;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
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
  Scenario              scenario;
  PiGraph               algo;
  Design                design;
  List<AbstractActor>   listActors;
  ComponentInstance     cpu1;
  ComponentInstance     fpga1;
  ComNode               mem;
  SlamFactory           SLAMFactory = SlamFactory.eINSTANCE;

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

    scenario = ScenarioUserFactory.createScenario();
    algo = PiMMFactory.createPiGraph();
    design = SLAMFactory.createDesign();

    algo.setUrl("");
    scenario.setAlgorithm(algo);
    scenario.setDesign(design);

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

    design.getComponentInstances().add(cpu1);
    design.getComponentInstances().add(fpga1);

    final DataLink link1 = SLAMFactory.createDataLink();
    link1.setDirected(false);
    link1.setSourceComponentInstance(cpu1);
    link1.setDestinationComponentInstance(mem1);
    link1.setUuid("4");
    design.getLinks().add(link1);

    final DataLink link2 = SLAMFactory.createDataLink();
    link2.setDirected(false);
    link2.setSourceComponentInstance(fpga1);
    link2.setDestinationComponentInstance(mem1);
    link2.setUuid("5");
    design.getLinks().add(link2);

    final ComponentHolder CmpHolder = SLAMFactory.createComponentHolder();
    CmpHolder.getComponents().add(fpga);
    CmpHolder.getComponents().add(cpu);
    design.setComponentHolder(CmpHolder);

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

    createFifoLink(listActors.get(0), listActors.get(1), 10, 10, "int", algo);
    createFifoLink(listActors.get(0), listActors.get(2), 10, 10, "int", algo);
    createFifoLink(listActors.get(1), listActors.get(3), 10, 10, "int", algo);
    createFifoLink(listActors.get(2), listActors.get(3), 10, 10, "int", algo);
    createFifoLink(listActors.get(3), listActors.get(5), 10, 10, "int", algo);
    createFifoLink(listActors.get(4), listActors.get(5), 10, 10, "int", algo);
    createFifoLink(listActors.get(5), listActors.get(6), 10, 10, "int", algo);
    createFifoLink(listActors.get(5), listActors.get(7), 10, 10, "int", algo);
    createFifoLink(listActors.get(5), listActors.get(8), 10, 10, "int", algo);
    createFifoLink(listActors.get(6), listActors.get(9), 10, 10, "int", algo);
    createFifoLink(listActors.get(6), listActors.get(10), 10, 10, "int", algo);
    createFifoLink(listActors.get(7), listActors.get(11), 10, 10, "int", algo);
    createFifoLink(listActors.get(7), listActors.get(12), 10, 10, "int", algo);

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

    scenario.getConstraints().addConstraints(cpu1, cpuActors);
    scenario.getConstraints().addConstraints(fpga1, fpgaActors);

  }

  @After
  public void teardown() {
    System.out.println("fin des tests");
  }

  private void createFifoLink(AbstractActor source, AbstractActor sink, int rateSource, int rateSink, String type,
      PiGraph graph) {

    final DataOutputPort sourceOut = PiMMFactory
        .createDataOutputPort(source.getName() + "To" + sink.getName() + "_Source");
    sourceOut.setExpression(rateSource);
    source.getDataOutputPorts().add(sourceOut);

    final DataInputPort sinkIn = PiMMFactory.createDataInputPort(source.getName() + "To" + sink.getName() + "_Sink");
    sinkIn.setExpression(rateSink);
    sink.getDataInputPorts().add(sinkIn);

    final Fifo f = PiMMFactory.createFifo(sourceOut, sinkIn, type);
    graph.addFifo(f);
  }

  private void replaceAndRemoveActor(AbstractActor oldA, AbstractActor newA, PiGraph graph) {

    // clone input and output outer interfaces
    // plug fifos and copy rates
    for (final DataInputPort olddip : oldA.getDataInputPorts()) {
      final DataInputPort newdip = PiMMFactory.createDataInputPort(olddip.getName());

      newdip.setExpression(olddip.getExpression());
      newA.getDataInputPorts().add(newdip);
      newdip.setIncomingFifo(olddip.getFifo());
    }
    for (final DataOutputPort olddop : oldA.getDataOutputPorts()) {
      final DataOutputPort newdop = PiMMFactory.createDataOutputPort(olddop.getName());

      newdop.setExpression(olddop.getExpression());
      newA.getDataOutputPorts().add(newdop);
      newdop.setOutgoingFifo(olddop.getFifo());
    }

    for (final ConfigInputPort oldcip : oldA.getConfigInputPorts()) {
      final ConfigInputPort newcip = PiMMFactory.createConfigInputPort();

      newA.getConfigInputPorts().add(newcip);
      newcip.setIncomingDependency(oldcip.getIncomingDependency());
    }

    // remove the old actor from the graph
    graph.removeActorAndDependencies(oldA);

  }

  @Test
  public void testBuildArchHierarchyGraph() {
    ClusterBuilder.buildArchHierarchyGraph(algo, scenario);

    assertEquals(8, algo.getActors().size());
    final List<AbstractActor> listHierActors = algo.getActors().stream().filter(a -> a instanceof PiGraphImpl).toList();
    assertEquals(3, listHierActors.size());

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
    final Set<AbstractActor> res = ClusterBuilder.buildMergeList(seed, scenario, fpga1, visitedActors, heuristic);

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

  /**
   * Selects the adapted scheduler for a given cluster based on its mapping arch
   *
   * @param cluster
   *          the cluster to map
   * @param scenario
   *          the scenario
   * @return the selected mapper's string name/identifier
   **/
  public String switchSchedulerMapper(AbstractActor cluster, Scenario scenario) {
    final List<ComponentInstance> mappings = ClusteringHelper.getArch(cluster, scenario);
    // Pour le moment, je vais supposer qu'un cluster est mappé à une seule archi. Cela correspond à l'idée que le
    // mapping "niveau archi" est fait entièrement lors de la phase de clustering, qui décide quel cluster est fait sur
    // quel type de PE.

    final ComponentInstance arch = mappings.getFirst();

    if (arch.getComponent() instanceof CPU) {
      return PreesmSynthesisTask.VALUE_SCHEDULER_SIMPLE; // temporaire
    }

    if (arch.getComponent() instanceof FPGA) {
      return AdfgOjalgoFpgaFifoEvaluator.FIFO_EVALUATOR_ADFG_DEFAULT_LINEAR;
    }
    // temporaire

    PreesmLogger.getLogger().log(Level.SEVERE, () -> "No mapper available for component " + arch.getInstanceName());
    return null;

  }

  /***
   * Returns the corresponding scheduler-mapper instance based on its name.
   *
   * @param localSchedulerMapperName
   *          the scheduler's name
   * @return the sceduler-mapper instance
   */
  public IScheduler getSchedulerMapperInstance(String localSchedulerMapperName) {
    // TODO expand switch
    switch (localSchedulerMapperName) {
      case AdfgOjalgoFpgaFifoEvaluator.FIFO_EVALUATOR_ADFG_DEFAULT_LINEAR,
          AdfgOjalgoFpgaFifoEvaluator.FIFO_EVALUATOR_ADFG_DEFAULT_EXACT:
        return new FpgaScheduler(localSchedulerMapperName);
      case PreesmSynthesisTask.VALUE_SCHEDULER_SIMPLE, PreesmSynthesisTask.VALUE_SCHEDULER_PERIODIC,
          PreesmSynthesisTask.VALUE_SCHEDULER_LEGACY:
        return new SimpleScheduler();
      default:
        PreesmLogger.getLogger().log(Level.SEVERE,
            () -> "This scheduler is not implemented : " + localSchedulerMapperName);
        return null;
    }
  }

  @Test
  public void testHeterogeneousScheduler() {
    final Map<String, Object> inputs = new LinkedHashMap<>();
    inputs.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, algo);
    inputs.put(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, design);
    inputs.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);

    final Map<String, String> parameters = new LinkedHashMap<>(); // params copiés de preesm

    final IProgressMonitor monitor = new NullProgressMonitor(); // constucteur au pif
    final String nodeName = ""; // ne semble pas utilisé pour de vrai donc raf
    final Workflow workflow = new Workflow(); // pas utilisé non plus donc raf

    // clusterize the graph
    final List<PiGraph> clustersList = ClusterBuilder.buildArchHierarchyGraph(algo, scenario);

    // ------------------ locally schedule and map the clusters' graphs ------------------

    for (final PiGraph cluster : clustersList) {
      // find the right scheduler-mapper based on the cluster's shared archi : cpu, fpga, cgra...
      final String localSchedulerMapperName = switchSchedulerMapper(cluster, scenario);
      if (localSchedulerMapperName == null) {
        return;
      }

      final IScheduler localSchedulerMapper = getSchedulerMapperInstance(localSchedulerMapperName);
      /*
       * final PreesmSynthesisTask synthesis = new PreesmSynthesisTask();
       *
       * final Map<String, Object> localResults = synthesis.execute(localInputs, localParameters, monitor, nodeName,
       * workflow);
       */
      final SynthesisResult res = localSchedulerMapper.scheduleAndMap(cluster, design, scenario);

    }

    // ------------------ replace hierarchical actors with placeholders ------------------

    for (final AbstractActor actor : algo.getActors()) {
      if (actor instanceof PiGraph) {
        final Actor placeholder = PiMMFactory.createActor(actor.getName() + "_placeholder");
        placeholder.setRefinement(PiMMFactory.createCHeaderRefinement()); // empty refinement for now
        algo.addActor(placeholder);
        replaceAndRemoveActor(actor, placeholder, algo);
        scenario.getConstraints().addConstraint(cpu1, placeholder); // test, idéalement ça serait une "non-archi"
      }
    }

    // On retire la fpga de la liste d'archi pour que le scheduling CPU ne râle pas
    // design.getComponentHolder().getComponents().remove(fpga1.getComponent());

    // vieille api
    /*
     * parameters.put("Check", "True"); parameters.put("Optimize synchronization", "True");
     * parameters.put("balanceLoads", "True"); parameters.put("edgeSchedType", "Simple");
     * parameters.put("simulatorType", "AccuratelyTimed");
     *
     * Map<String, Object> schedule; schedule = HeterogeneousScheduler.schedule(inputs, parameters, monitor, nodeName,
     * workflow);
     *
     * assertNotNull(algo); // très très peu d'idées assertNotNull(schedule);
     *
     * final LatencyAbc ABCSchedule = (LatencyAbc) schedule.get("ABC"); // le latencyABC final MapperDAG resImpl = final
     * MapperDAG resImpl = ABCSchedule.getImplementation(); final MapperDAGVertex actor12 =
     * resImpl.getMapperDAGVertex("actor12"); ABCSchedule.getEffectiveComponent(actor12); ABCSchedule.getFinalLatency();
     * final VertexTiming timing = actor12.getTiming();
     *
     * ABCSchedule.getTotalOrder();
     */

    // nouvelle api
    final var synthesis = new PreesmSynthesisTask();

    parameters.put("scheduler", PreesmSynthesisTask.VALUE_SCHEDULER_SIMPLE);
    parameters.put("allocation", PreesmSynthesisTask.VALUE_ALLOCATORS_SIMPLE);

    final Map<String, Object> results = synthesis.execute(inputs, parameters, monitor, nodeName, workflow);

    final SynthesisResult schedule_mapping = HeterogeneousScheduler.schedule(algo, design, scenario);

    final ScheduleOrderManager scheduleOM = new ScheduleOrderManager(algo, schedule_mapping.schedule);

    assertNotNull(results);
    assertNotNull(scheduleOM);

    // vérifier que les éléments sont mappés où on le veut, et que la durée d'exécution est celle prévue

  }

}
