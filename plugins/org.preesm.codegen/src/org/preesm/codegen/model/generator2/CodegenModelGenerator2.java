package org.preesm.codegen.model.generator2;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.CoreBlock;
import org.preesm.codegen.model.util.CodegenModelUserFactory;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 *
 * @author anmorvan
 *
 */
public class CodegenModelGenerator2 {

  public static final List<Block> generate(final Design archi, final PiGraph algo, final Scenario scenario,
      final Schedule schedule, final Mapping mapping, final Allocation memAlloc) {
    return generate(archi, algo, scenario, schedule, mapping, memAlloc, false);
  }

  public static final List<Block> generate(final Design archi, final PiGraph algo, final Scenario scenario,
      final Schedule schedule, final Mapping mapping, final Allocation memAlloc, boolean papify) {
    return new CodegenModelGenerator2(archi, algo, scenario, schedule, mapping, memAlloc, papify).generate();
  }

  private final Design     archi;
  private final PiGraph    algo;
  private final Scenario   scenario;
  private final Schedule   schedule;
  private final Mapping    mapping;
  private final Allocation memAlloc;
  private final boolean    papify;

  private final List<Block> coreBlocks;

  private CodegenModelGenerator2(final Design archi, final PiGraph algo, final Scenario scenario,
      final Schedule schedule, final Mapping mapping, final Allocation memAlloc, boolean papify) {
    this.archi = archi;
    this.algo = algo;
    this.scenario = scenario;
    this.schedule = schedule;
    this.mapping = mapping;
    this.memAlloc = memAlloc;
    this.papify = papify;

    this.coreBlocks = new ArrayList<>();
  }

  private List<Block> generate() {

    final EList<ComponentInstance> cmps = this.archi.getOperatorComponentInstances();
    for (ComponentInstance cmp : cmps) {
      final CoreBlock createCoreBlock = CodegenModelUserFactory.createCoreBlock(cmp);
      this.coreBlocks.add(createCoreBlock);
    }

    return this.coreBlocks;

  }

}
