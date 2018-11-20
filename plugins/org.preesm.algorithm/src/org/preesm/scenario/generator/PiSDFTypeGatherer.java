package org.preesm.scenario.generator;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 *
 */
public class PiSDFTypeGatherer extends PiMMSwitch<Set<String>> {

  private final Set<String> dataTypes = new LinkedHashSet<>();

  @Override
  public Set<String> casePiGraph(final PiGraph graph) {
    graph.getChildrenGraphs().stream().forEach(this::doSwitch);
    graph.getFifos().stream().forEach(this::doSwitch);
    return dataTypes;
  }

  @Override
  public Set<String> caseFifo(Fifo fifo) {
    dataTypes.add(fifo.getType());
    return Collections.emptySet();
  }
}
