package org.preesm.codegen.xtend.task;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.generator.AbstractCodegenModelGenerator;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;

/**
 * @author dgageot
 *
 */
public class CodegenTestModelGenerator extends AbstractCodegenModelGenerator {

  final Map<AbstractActor, Schedule> mapper;

  final PiGraph piAlgo;

  /**
   * @param archi
   *          architecture
   * @param algo
   *          PiGraph algorithm
   * @param scenario
   *          scenario
   * @param workflow
   *          workflow
   * @param mapper
   *          schedule associated with cluster
   */
  public CodegenTestModelGenerator(final Design archi, final PiGraph algo, final Scenario scenario,
      final Workflow workflow, final Map<AbstractActor, Schedule> mapper) {
    super(archi, new MapperDAG(algo), null, scenario);
    this.mapper = mapper;
    this.piAlgo = algo;
  }

  @Override
  public List<Block> generate() {

    // Build CoreBlock
    CoreBlock cb = CodegenModelUserFactory.createCoreBlock();
    cb.setCoreID(0);
    cb.setName(scenario.getSimulationInfo().getMainOperator().getInstanceName());
    cb.setCoreType("x86");

    for (Entry<AbstractActor, Schedule> a : mapper.entrySet()) {
      CodegenClusterModelGenerator codegenCluster = new CodegenClusterModelGenerator(cb, a.getValue(), scenario, null,
          null);
      codegenCluster.generate();
    }

    List<Block> blockList = new LinkedList<>();
    blockList.add(cb);

    return blockList;
  }

}
