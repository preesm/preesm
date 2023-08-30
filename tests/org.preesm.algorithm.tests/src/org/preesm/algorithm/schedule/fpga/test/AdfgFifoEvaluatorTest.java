package org.preesm.algorithm.schedule.fpga.test;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.preesm.algorithm.schedule.fpga.AbstractGenericFpgaFifoEvaluator.AnalysisResultFPGA;
import org.preesm.algorithm.schedule.fpga.AdfgOjalgoFpgaFifoEvaluator;
import org.preesm.algorithm.schedule.fpga.FpgaAnalysisMainTask;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.test.PiGraphGenerator;
import org.preesm.model.pisdf.util.VertexPath;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.util.ScenarioUserFactory;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.FPGA;
import org.preesm.model.slam.TimingType;
import org.preesm.model.slam.generator.ArchitecturesGenerator;

public class AdfgFifoEvaluatorTest {

  @Test
  public void DiamondPerfectPipelineTest() {
    final List<
        Long> fifoSize = createExecuteDiamondGraph(List.of("10", "10", "1", "100"), List.of("10", "10", "1", "100"));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(0));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(1));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(2));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(3));
  }

  @Test
  public void DiamondSlowInputTest() {
    final List<
        Long> fifoSize = createExecuteDiamondGraph(List.of("20", "10", "1", "100"), List.of("20", "10", "1", "100"));
    Assert.assertEquals(12 * 32L, (long) fifoSize.get(0));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(1));
    Assert.assertEquals(12 * 32L, (long) fifoSize.get(2));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(3));
  }

  @Test
  public void DiamondSlowOutputTest() {
    final List<
        Long> fifoSize = createExecuteDiamondGraph(List.of("10", "10", "1", "200"), List.of("10", "10", "1", "200"));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(0));
    Assert.assertEquals(102 * 32L, (long) fifoSize.get(1));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(2));
    Assert.assertEquals(102 * 32L, (long) fifoSize.get(3));
  }

  @Test
  public void DiamondLatencyBTest() {
    final List<
        Long> fifoSize = createExecuteDiamondGraph(List.of("10", "20", "1", "100"), List.of("10", "10", "1", "100"));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(0));
    Assert.assertEquals(11 * 32L, (long) fifoSize.get(1));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(2));
    Assert.assertEquals(11 * 32L, (long) fifoSize.get(3));
  }

  @Test
  public void DiamondLatencyCTest() {
    final List<
        Long> fifoSize = createExecuteDiamondGraph(List.of("10", "10", "4", "100"), List.of("10", "10", "1", "100"));
    Assert.assertEquals(4 * 32L, (long) fifoSize.get(0));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(1));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(2));
    Assert.assertEquals(4 * 32L, (long) fifoSize.get(3));
  }

  @Test
  public void ChainPerfectPipelineTest() {
    final Long fifoSize = createExecuteChainGraph(List.of("10", "10"), List.of("10", "10"));
    Assert.assertEquals((long) fifoSize, 2 * 32L);
  }

  @Test
  public void ChainSlowInputTest() {
    final Long fifoSize = createExecuteChainGraph(List.of("20", "10"), List.of("20", "10"));
    Assert.assertEquals((long) fifoSize, 12 * 32L);
  }

  @Test
  public void ChainSlowOutputTest() {
    final Long fifoSize = createExecuteChainGraph(List.of("10", "20"), List.of("10", "20"));
    Assert.assertEquals((long) fifoSize, 12 * 32L);
  }

  @Test
  public void ChainLatencyTest() {
    final Long fifoSize = createExecuteChainGraph(List.of("20", "20"), List.of("10", "10"));
    Assert.assertEquals((long) fifoSize, 11 * 32L);
  }

  private List<Long> createExecuteDiamondGraph(List<String> executionTimes, List<String> initiationIntervals) {
    // Graph
    final PiGraph graph = PiGraphGenerator.createDiamondGraph();
    graph.setUrl("test.pi"); // Add fake URL to pass check in earlier transform

    // Design
    final Design design = ArchitecturesGenerator.generateFpgaArchitecture();

    // Scenario
    final Scenario scenario = ScenarioUserFactory.createScenario();
    scenario.setDesign(design);

    // Set mapping constraints
    final AbstractActor actorA = getMappedActor(graph, design, scenario, "A");
    final AbstractActor actorB = getMappedActor(graph, design, scenario, "B");
    final AbstractActor actorC = getMappedActor(graph, design, scenario, "C");
    final AbstractActor actorD = getMappedActor(graph, design, scenario, "D");
    scenario.getSimulationInfo().getDataTypes().put("int", 32L);

    // Set timing constraints
    setTimings(scenario, actorA, executionTimes.get(0), initiationIntervals.get(0));
    setTimings(scenario, actorB, executionTimes.get(1), initiationIntervals.get(1));
    setTimings(scenario, actorC, executionTimes.get(2), initiationIntervals.get(2));
    setTimings(scenario, actorD, executionTimes.get(3), initiationIntervals.get(3));

    // Run buffer sizing
    final AnalysisResultFPGA results = FpgaAnalysisMainTask.checkAndAnalyzeAlgorithm(graph, scenario,
        AdfgOjalgoFpgaFifoEvaluator.FIFO_EVALUATOR_ADFG_DEFAULT_EXACT);

    // Extract fifo size
    final AbstractActor actorBFlat = VertexPath.lookup(results.flatGraph, "B");
    final Fifo fifoAB = actorBFlat.getDataInputPorts().get(0).getFifo();
    final Fifo fifoBD = actorBFlat.getDataOutputPorts().get(0).getFifo();
    final AbstractActor actorCFlat = VertexPath.lookup(results.flatGraph, "C");
    final Fifo fifoAC = actorCFlat.getDataInputPorts().get(0).getFifo();
    final Fifo fifoCD = actorCFlat.getDataOutputPorts().get(0).getFifo();
    return List.of(results.flatFifoSizes.get(fifoAB), results.flatFifoSizes.get(fifoBD),
        results.flatFifoSizes.get(fifoAC), results.flatFifoSizes.get(fifoCD));
  }

  private Long createExecuteChainGraph(List<String> executionTimes, List<String> initiationIntervals) {
    // Graph
    final PiGraph graph = PiGraphGenerator.createChainGraph();
    graph.setUrl("test.pi"); // Add fake URL to pass check in earlier transform

    // Design
    final Design design = ArchitecturesGenerator.generateFpgaArchitecture();

    // Scenario
    final Scenario scenario = ScenarioUserFactory.createScenario();
    scenario.setDesign(design);

    // Set mapping constraints
    final AbstractActor actorA = getMappedActor(graph, design, scenario, "A");
    final AbstractActor actorB = getMappedActor(graph, design, scenario, "B");
    scenario.getSimulationInfo().getDataTypes().put("int", 32L);

    // Set timing constraints
    setTimings(scenario, actorA, executionTimes.get(0), initiationIntervals.get(0));
    setTimings(scenario, actorB, executionTimes.get(1), initiationIntervals.get(1));

    // Run buffer sizing
    final AnalysisResultFPGA results = FpgaAnalysisMainTask.checkAndAnalyzeAlgorithm(graph, scenario,
        AdfgOjalgoFpgaFifoEvaluator.FIFO_EVALUATOR_ADFG_DEFAULT_EXACT);

    // Extract fifo size
    final AbstractActor actorBFlat = VertexPath.lookup(results.flatGraph, "B");
    final Fifo fifoAB = actorBFlat.getDataInputPorts().get(0).getFifo();
    return results.flatFifoSizes.get(fifoAB);
  }

  private AbstractActor getMappedActor(final PiGraph graph, final Design design, final Scenario scenario,
      final String actorName) {
    final AbstractActor actor = VertexPath.lookup(graph, actorName);
    scenario.getConstraints().addConstraint(design.getComponentInstance("Fpga"), actor);
    return actor;
  }

  private void setTimings(final Scenario scenario, final AbstractActor actor, String executionTime,
      String initiationInterval) {
    final Component fpgaComponent = scenario.getDesign().getComponent((FPGA.class.getSimpleName()));
    scenario.getTimings().setTiming(actor, fpgaComponent, TimingType.EXECUTION_TIME, executionTime);
    scenario.getTimings().setTiming(actor, fpgaComponent, TimingType.INITIATION_INTERVAL, initiationInterval);
  }
}
