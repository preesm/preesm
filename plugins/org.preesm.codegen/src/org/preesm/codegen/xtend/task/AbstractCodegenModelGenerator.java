package org.preesm.codegen.xtend.task;

import java.util.List;
import java.util.Map;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.memory.allocation.MemoryAllocator;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.codegen.model.ActorBlock;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;

/**
 * @author dgageot
 *
 */
public abstract class AbstractCodegenModelGenerator {

  /** Targeted {@link Design Architecture} of the code generation. */
  protected Design archi;

  /**
   * {@link DirectedAcyclicGraph DAG} used to generate code. This {@link DirectedAcyclicGraph DAG} must be the result of
   * mapping/scheduling process.
   */
  protected MapperDAG algo;

  /**
   * {@link Map} of {@link String} and {@link MemoryExclusionGraph MEG} used to generate code. These
   * {@link MemoryExclusionGraph MemEx MEGs} must be the result of an allocation process. Each {@link String}
   * corresponds to a memory bank where the associated MEG is allocated.
   *
   * @see MemoryAllocator
   */
  protected Map<String, MemoryExclusionGraph> megs;

  /**
   * {@link PreesmScenario Scenario} at the origin of the call to the {@link AbstractCodegenPrinter Code Generator}.
   */
  protected Scenario scenario;

  /** The workflow. */
  protected Workflow workflow;

  /** The flag to activate PAPIFY instrumentation. */
  protected boolean papifyActive;

  /**
   * @param archi
   *          See {@link AbstractCodegenPrinter#archi}
   * @param algo
   *          See {@link AbstractCodegenPrinter#dag}
   * @param megs
   *          See {@link AbstractCodegenPrinter#megs}
   * @param scenario
   *          See {@link AbstractCodegenPrinter#scenario}
   * @param workflow
   *          the workflow
   */
  public AbstractCodegenModelGenerator(final Design archi, final MapperDAG algo,
      final Map<String, MemoryExclusionGraph> megs, final Scenario scenario, final Workflow workflow) {
    this.archi = archi;
    this.algo = algo;
    this.megs = megs;
    this.scenario = scenario;
    this.workflow = workflow;
  }

  /**
   * Sets PAPIFY flag.
   *
   * @param papifyMonitoring
   *          the flag to set papify instrumentation
   */
  public final void registerPapify(final String papifyMonitoring) {

    if (!papifyMonitoring.equalsIgnoreCase("true")) {
      this.papifyActive = false;
    } else {
      this.papifyActive = true;
    }

  }

  /**
   * Method to generate the intermediate model of the codegen.
   *
   * @return a set of {@link Block blocks}. Each of these block corresponds to a part of the code to generate:
   *         <ul>
   *         <li>{@link CoreBlock A block corresponding to the code executed by a core}</li>
   *         <li>{@link ActorBlock A block corresponding to the code of an non-flattened hierarchical actor}</li>
   *         </ul>
   */
  public abstract List<Block> generate();

  public final Design getArchi() {
    return this.archi;
  }

  public final MapperDAG getAlgo() {
    return this.algo;
  }

  public final Map<String, MemoryExclusionGraph> getMegs() {
    return this.megs;
  }

  public final Scenario getScenario() {
    return this.scenario;
  }

}
