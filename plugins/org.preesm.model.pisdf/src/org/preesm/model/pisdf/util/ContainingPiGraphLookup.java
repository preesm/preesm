package org.preesm.model.pisdf.util;

import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.Refinement;

/**
 *
 */
public class ContainingPiGraphLookup extends PiMMSwitch<PiGraph> {

  @Override
  public PiGraph caseAbstractVertex(final AbstractVertex object) {
    return object.getContainingPiGraph();
  }

  @Override
  public PiGraph casePiGraph(final PiGraph graph) {
    return graph;
  }

  @Override
  public PiGraph caseParameter(final Parameter param) {
    return param.getContainingPiGraph();
  }

  @Override
  public PiGraph caseFifo(final Fifo fifo) {
    return fifo.getContainingPiGraph();
  }

  @Override
  public PiGraph caseDependency(final Dependency dependency) {
    return dependency.getContainingPiGraph();
  }

  @Override
  public PiGraph casePort(final Port object) {
    return doSwitch(object.eContainer());
  }

  @Override
  public PiGraph caseRefinement(Refinement object) {
    return doSwitch(object.getRefinementContainer());
  }

  @Override
  public PiGraph caseExpression(Expression object) {
    return doSwitch(object.getHolder());
  }

}
