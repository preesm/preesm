/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2023 - 2024) :
 *
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2023)
 * Mickael Dardaillon [mickael.dardaillon@insa-rennes.fr] (2023 - 2024)
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

package org.preesm.algorithm.schedule.fpga.test;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.preesm.algorithm.schedule.fpga.AbstractGenericFpgaFifoEvaluator.AnalysisResultFPGA;
import org.preesm.algorithm.schedule.fpga.AdfgOjalgoFpgaFifoEvaluator;
import org.preesm.algorithm.schedule.fpga.FpgaAnalysis;
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
  public void diamondPerfectPipelineTest() {
    final List<
        Long> fifoSize = createExecuteDiamondGraph(List.of("10", "10", "1", "100"), List.of("10", "10", "1", "100"));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(0));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(1));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(2));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(3));
  }

  @Test
  public void diamondSlowInputTest() {
    final List<
        Long> fifoSize = createExecuteDiamondGraph(List.of("20", "10", "1", "100"), List.of("20", "10", "1", "100"));
    Assert.assertEquals(12 * 32L, (long) fifoSize.get(0));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(1));
    Assert.assertEquals(12 * 32L, (long) fifoSize.get(2));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(3));
  }

  @Test
  public void diamondSlowOutputTest() {
    final List<
        Long> fifoSize = createExecuteDiamondGraph(List.of("10", "10", "1", "200"), List.of("10", "10", "1", "200"));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(0));
    Assert.assertEquals(102 * 32L, (long) fifoSize.get(1));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(2));
    Assert.assertEquals(102 * 32L, (long) fifoSize.get(3));
  }

  @Test
  public void diamondLatencyBTest() {
    final List<
        Long> fifoSize = createExecuteDiamondGraph(List.of("10", "20", "1", "100"), List.of("10", "10", "1", "100"));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(0));
    Assert.assertEquals(11 * 32L, (long) fifoSize.get(1));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(2));
    Assert.assertEquals(11 * 32L, (long) fifoSize.get(3));
  }

  @Test
  public void diamondLatencyCTest() {
    final List<
        Long> fifoSize = createExecuteDiamondGraph(List.of("10", "10", "4", "100"), List.of("10", "10", "1", "100"));
    Assert.assertEquals(4 * 32L, (long) fifoSize.get(0));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(1));
    Assert.assertEquals(2 * 32L, (long) fifoSize.get(2));
    Assert.assertEquals(4 * 32L, (long) fifoSize.get(3));
  }

  @Test
  public void chainPerfectPipelineTest() {
    final Long fifoSize = createExecuteChainGraph(List.of("10", "10"), List.of("10", "10"));
    Assert.assertEquals((long) fifoSize, 2 * 32L);
  }

  @Test
  public void chainSlowInputTest() {
    final Long fifoSize = createExecuteChainGraph(List.of("20", "10"), List.of("20", "10"));
    Assert.assertEquals((long) fifoSize, 12 * 32L);
  }

  @Test
  public void chainSlowOutputTest() {
    final Long fifoSize = createExecuteChainGraph(List.of("10", "20"), List.of("10", "20"));
    Assert.assertEquals((long) fifoSize, 12 * 32L);
  }

  @Test
  public void chainLatencyTest() {
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
    final AnalysisResultFPGA results = FpgaAnalysis.checkAndAnalyzeAlgorithm(graph, scenario,
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
    final AnalysisResultFPGA results = FpgaAnalysis.checkAndAnalyzeAlgorithm(graph, scenario,
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
