package org.preesm.algorithm.mapper.ui.stats;

import java.util.HashMap;
import java.util.Map;
import org.preesm.algorithm.mapper.gantt.GanttData;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 * This class is the default one, given all information in the constructor.
 * 
 * @author ahonorat
 */
public class StatGeneratorPrecomputed extends AbstractStatGenerator {

  public final long                         DAGSpanLength;
  public final long                         DAGWorkLength;
  public final long                         FinalTime;
  public final int                          nbUsedOperators;
  public final Map<ComponentInstance, Long> loads;
  public final Map<ComponentInstance, Long> mems;

  public final GanttData gantt;

  public StatGeneratorPrecomputed(final Design architecture, final Scenario scenario, final long DAGSpanLength,
      final long DAGWorkLength, final long FinalTime, final int nbUsedOperators,
      final Map<ComponentInstance, Long> loads, final Map<ComponentInstance, Long> mems, final GanttData gantt) {
    super(architecture, scenario);

    this.DAGSpanLength = DAGSpanLength;
    this.DAGWorkLength = DAGWorkLength;
    this.FinalTime = FinalTime;
    this.nbUsedOperators = nbUsedOperators;
    this.loads = new HashMap<>(loads);
    this.mems = new HashMap<>(mems);
    this.gantt = gantt;
  }

  @Override
  public long getDAGSpanLength() {
    return DAGSpanLength;
  }

  @Override
  public long getDAGWorkLength() {
    return DAGWorkLength;
  }

  @Override
  public long getFinalTime() {
    return FinalTime;
  }

  @Override
  public long getLoad(ComponentInstance operator) {
    return loads.getOrDefault(operator, 0L);
  }

  @Override
  public long getMem(ComponentInstance operator) {
    return mems.getOrDefault(operator, 0L);
  }

  @Override
  public int getNbUsedOperators() {
    // alternatively, if it was not given:
    // (int) gantt.getComponents().stream().filter(x -> !x.getTasks().isEmpty()).count();
    return nbUsedOperators;
  }

  @Override
  public GanttData getGanttData() {
    return gantt;
  }

}
