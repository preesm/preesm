package org.ietr.preesm.test.it.appstest;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.ietr.preesm.test.it.api.WorkflowRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.preesm.algorithm.synthesis.PreesmHeterogeneousSynthesisTask;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.test.PiGraphGenerator;
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
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

public class ConstraintSchedulerTest {

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
    scenario = ScenarioUserFactory.createScenario();
    algo = PiGraphGenerator.createDiamondGraph();
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

    algo.setRefinement(PiMMUserFactory.instance.createPiSDFRefinement());
    for (final Actor a : algo.getActorsWithRefinement()) {
      a.setRefinement(PiMMUserFactory.instance.createCHeaderRefinement());
      scenario.getTimings().setTiming(a, fpga, TimingType.EXECUTION_TIME, "50");
      scenario.getTimings().setTiming(a, fpga, TimingType.INITIATION_INTERVAL, "50");
    }

    scenario.getConstraints().addConstraint(fpga1, algo);
    scenario.getConstraints().addConstraints(fpga1, algo.getActors());

    scenario.getTimings().setTiming(algo, fpga, null, null);

    algo.setName("algo");
  }

  @Test
  public void constraintSchedulerTest() throws CoreException, IOException {
    final Map<String, Object> inputs = new LinkedHashMap<>();
    inputs.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, algo);
    inputs.put(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, heteroDesign);
    inputs.put(AbstractWorkflowNodeImplementation.KEY_SCENARIO, scenario);
    scenario.setCodegenDirectory("/fpga-scheduler-test/Code/generated/");

    final String projectName = "fpga-scheduler-test";
    final String sc = "/Scenarios/" + "test.scenario";
    final String workflow = "/Workflows/" + "ConstraintScheduling.workflow";

    final boolean success = WorkflowRunner.runWorkFlow(null, projectName, workflow, sc);

    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put("scheduler", "legacy");
    parameters.put("fpgascheduler", PreesmHeterogeneousSynthesisTask.VALUE_SCHEDULER_FPGA_CONSTRAINT);
    inputs.put(PreesmHeterogeneousSynthesisTask.VALUE_CYCLE_MAX, "5000");
    inputs.put(PreesmHeterogeneousSynthesisTask.VALUE_K_VALUE, "1");

    // final FPGAConstraintScheduler task = new FPGAConstraintScheduler(true, false, 0, 500000, 1);
    // final SynthesisResult res = task.scheduleAndMap(algo, heteroDesign, scenario);

    // System.out.println("Scheduling : " + res.latency.getValue());
  }

  @After
  public void teardown() {
    System.out.println("test fini");
  }

}
