package org.preesm.model.pisdf.util;

import java.util.ArrayDeque;
import java.util.Deque;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;

/**
 *
 */
public class PiGraphConsistenceChecker extends PiMMSwitch<Boolean> {

  /**
   *
   */
  public static final void checkOrFail(final PiGraph graph) {
    final boolean check = check(graph);
    if (!check) {
      throw new PreesmException();
    }
  }

  /**
   *
   */
  public static final boolean check(final PiGraph graph) {
    final PiGraphConsistenceChecker piGraphConsistenceChecker = new PiGraphConsistenceChecker();
    final boolean graphIsConsistent = piGraphConsistenceChecker.doSwitch(graph);
    final boolean hierarchyIsConsistent = piGraphConsistenceChecker.graphStack.isEmpty();
    return graphIsConsistent && hierarchyIsConsistent;
  }

  private final Deque<PiGraph> graphStack;

  private PiGraphConsistenceChecker() {
    this.graphStack = new ArrayDeque<>();
  }

  @Override
  public Boolean casePiGraph(final PiGraph graph) {
    graphStack.push(graph);
    boolean res = caseAbstractActor(graph);
    res = res && graph.getActors().stream().allMatch(this::doSwitch);
    res = res && graph.getFifos().stream().allMatch(this::doSwitch);
    res = res && graph.getDependencies().stream().allMatch(this::doSwitch);
    res = res && graph.getChildrenGraphs().stream().allMatch(this::doSwitch);
    graphStack.pop();
    return res;
  }

  @Override
  public Boolean caseFifo(final Fifo fifo) {
    return true;
  }

  @Override
  public Boolean caseDependency(final Dependency dependency) {
    return true;
  }

  @Override
  public Boolean caseAbstractActor(final AbstractActor actor) {
    boolean res = actor.getAllDataPorts().stream().allMatch(this::doSwitch);
    res = res && actor.getConfigInputPorts().stream().allMatch(this::doSwitch);
    res = res && actor.getConfigOutputPorts().stream().allMatch(this::doSwitch);
    return res;
  }

  @Override
  public Boolean caseConfigInputPort(final ConfigInputPort configInputPort) {
    return true;
  }

  @Override
  public Boolean caseConfigOutputPort(final ConfigOutputPort configOutputPort) {
    return true;
  }

  @Override
  public Boolean caseDataInputPort(final DataInputPort dataInputPort) {
    return true;
  }

  @Override
  public Boolean caseDataOutputPort(final DataOutputPort dataOutputPort) {
    return true;
  }
}
