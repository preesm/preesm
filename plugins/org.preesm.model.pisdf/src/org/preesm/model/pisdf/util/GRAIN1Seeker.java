package org.preesm.model.pisdf.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.preesm.commons.graph.Vertex;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;

/**
 * This class is used to seek chain of actors in a given PiGraph whose granularity is so fine that the buffer transfer
 * time is more important than the time of the actor itself without internal state (see the article of Pino et al. "A
 * Hierarchical Multiprocessor Scheduling System For DSP Applications").
 *
 * @author orenaud
 *
 */
public class GRAIN1Seeker extends PiMMSwitch<Boolean> {

  /**
   * Input graph.
   */
  final PiGraph                   graph;
  final int                       nPEs;
  final Map<AbstractVertex, Long> brv;

  /**
   * List of identified URCs.
   */
  final List<List<AbstractActor>> identifiedGRAINs;

  final List<AbstractActor> nonClusterableList;

  final Map<AbstractVertex, Long> actorTiming;

  final double memcpySpeed;
  final Long   memcpySetUp;

  /**
   * Builds a URCSeeker based on a input graph.
   *
   * @param inputGraph
   *          Input graph to search in.
   * @param nonClusterableList
   * @param actorTiming
   * @param memcpySpeed
   * @param memcpySetUp
   * @param scenario
   */
  public GRAIN1Seeker(final PiGraph inputGraph, int numberOfPEs, Map<AbstractVertex, Long> brv,
      List<AbstractActor> nonClusterableList, Map<AbstractVertex, Long> actorTiming, double memcpySpeed,
      Long memcpySetUp) {
    this.graph = inputGraph;
    this.identifiedGRAINs = new LinkedList<>();
    this.nonClusterableList = nonClusterableList;
    this.nPEs = numberOfPEs;
    this.brv = brv;
    this.actorTiming = actorTiming;
    this.memcpySpeed = memcpySpeed;
    this.memcpySetUp = memcpySetUp;

  }

  /**
   * Seek for URC chain in the input graph.
   *
   * @return List of identified URC chain.
   */
  public List<List<AbstractActor>> seek() {
    // Clear the list of identified URCs
    this.identifiedGRAINs.clear();
    // Explore all executable actors of the graph
    this.graph.getActors().stream().filter(x -> x instanceof ExecutableActor).forEach(x -> doSwitch(x));
    // Return identified URCs
    return identifiedGRAINs;
  }

  @Override
  public Boolean caseAbstractActor(AbstractActor base) {

    final boolean heterogenousRate = base.getDataOutputPorts().stream()
        .anyMatch(x -> doSwitch(x.getFifo()).booleanValue());
    // Return false if rates are not homogeneous or that the corresponding actor was a sink (no output)
    if (!heterogenousRate || base.getDataOutputPorts().isEmpty() || nonClusterableList.contains(base)) {
      return false;
    }

    return true;
  }

  @Override
  public Boolean caseFifo(Fifo fifo) {
    if (!(fifo.getTarget() instanceof Actor)) {
      return false;
    }
    final Long srcTime = this.actorTiming.get(fifo.getSource());
    final Long srcScale = this.brv.get(fifo.getSource()) / this.nPEs;
    final Long snkTime = this.actorTiming.get(fifo.getTarget());
    final Long snkScale = this.brv.get(fifo.getTarget()) / this.nPEs;
    final Long SpecialActorTime = (long) (fifo.getSourcePort().getExpression().evaluate() * sizeofbit(fifo.getType())
        * this.brv.get(fifo.getSource()) * this.memcpySpeed + this.memcpySetUp);// memspeed = 1/input preesm param
    final List<AbstractActor> actorGrain = new LinkedList<>();
    // hidden delay cond
    final boolean hiddenDelay = fifo.isHasADelay();

    // precedence shift cond
    boolean firstShift = false;
    for (final DataOutputPort p : ((AbstractActor) fifo.getSource()).getDataOutputPorts()) {
      if (p.getFifo().isHasADelay()) {
        firstShift = true;
      }
    }

    boolean secondShift = false;
    for (final DataInputPort p : ((AbstractActor) fifo.getTarget()).getDataInputPorts()) {
      if (p.getFifo().isHasADelay()) {
        secondShift = true;
      }
    }

    // cycle introduction
    boolean cycle = false;

    for (final Vertex v : ((AbstractActor) fifo.getSource()).getDirectSuccessors()) {
      if (!v.equals(fifo.getTarget()) && !cycle) {
        // do {
        if (!v.getDirectSuccessors().isEmpty()) {
          for (final Vertex vv : v.getDirectSuccessors()) {
            if (vv.equals(fifo.getTarget())) {
              cycle = true;
              break;
            }
          }
        }
      }
    }

    // Return true if rates are heterogeneous and that no delay is involved
    if ((fifo.getSourcePort().getExpression().evaluate() != fifo.getTargetPort().getExpression().evaluate())
        && !hiddenDelay && !firstShift && !secondShift && !cycle && !(fifo.getSource() instanceof DataInputInterface)
        && !(fifo.getTarget() instanceof DataOutputInterface)
        && (srcTime * srcScale + SpecialActorTime + snkTime * snkScale >= srcTime * this.brv.get(fifo.getSource())
            + snkTime * this.brv.get(fifo.getTarget()))) {
      actorGrain.add((AbstractActor) fifo.getSource());
      actorGrain.add((AbstractActor) fifo.getTarget());
      this.identifiedGRAINs.add(actorGrain);
      return true;
    }
    return false;
  }

  private int sizeofbit(String type) {
    // TODO Auto-generated method stub
    if (type.equals("byte") || type.equals("boolean")) {
      return 8;
    }
    if (type.equals("short") || type.equals("char") || type.equals("uchar")) {
      return 8;
    }
    if (type.equals("int") || type.equals("float")) {
      return 32;
    }
    if (type.equals("Long") || type.equals("double")) {
      return 64;
    }
    return 32;
  }

}
