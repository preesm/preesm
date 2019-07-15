package org.preesm.codegen.xtend.task;

import java.util.List;
import java.util.Map;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.codegen.model.Block;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;

/**
 * @author dgageot
 *
 */
public class CodegenClusterModelGenerator extends AbstractCodegenModelGenerator {

  public CodegenClusterModelGenerator(final Design archi, final MapperDAG algo,
      final Map<String, MemoryExclusionGraph> megs, final Scenario scenario, final Workflow workflow) {
    super(archi, algo, megs, scenario, workflow);
  }

  @Override
  public List<Block> generate() {
    // TODO Auto-generated method stub
    return null;
  }

}
