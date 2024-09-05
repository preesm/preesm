package org.preesm.model.pisdf.util;

import java.util.HashMap;
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
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;

/**
 * This class is used to seek chain of actors in a given PiGraph whose timing are negligible in comparison to the
 * implementation length.
 *
 * @author orenaud
 *
 */
/**
 * @deprecated (when, why, refactoring advice...)
 */
@Deprecated(since = "3.16.0", forRemoval = true)
public class GRAIN2Seeker extends PiMMSwitch<Boolean> {

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

  final Map<AbstractActor, Long> topoOrder;

  final Long longestParallelTiming;

  /**
   * Builds a GRAIN2Seeker based on a input graph.
   *
   * @param inputGraph
   *          Input graph to search in.
   * @param nonClusterableList
   *          List of non clusterable actors
   * @param actorTiming
   *          list of timing
   * @param longestParallelTiming
   *          actor with the longest timing
   *
   */
  public GRAIN2Seeker(final PiGraph inputGraph, int numberOfPEs, Map<AbstractVertex, Long> brv,
      List<AbstractActor> nonClusterableList, Map<AbstractVertex, Long> actorTiming, Long longestParallelTiming) {
    this.graph = inputGraph;
    this.identifiedGRAINs = new LinkedList<>();
    this.nonClusterableList = nonClusterableList;
    this.nPEs = numberOfPEs;
    this.brv = brv;
    this.actorTiming = actorTiming;
    this.topoOrder = new HashMap<>();
    this.longestParallelTiming = longestParallelTiming;
  }

  /**
   * Seek for URC chain in the input graph.
   *
   * @return List of identified URC chain.
   */
  public List<List<AbstractActor>> seek() {
    // Clear the list of identified URCs
    this.identifiedGRAINs.clear();
    // compute topological order
    computeTopoOrder();

    // fill grain list
    computeGrainList();

    // Explore all executable actors in the topologic order of the graph
    // Return identified GRAINs
    return identifiedGRAINs;
  }

  private void computeGrainList() {
    final List<AbstractActor> grainList = new LinkedList<>();
    final List<AbstractActor> grainListcopy = new LinkedList<>();
    Long rank = 0L;
    final float limitRatio = 10f;//
    boolean endList = false;
    // check if ranks contain GRAIN candidate (RV=1 and negligible timing)
    while (!endList) {
      for (final AbstractActor a : this.graph.getExecutableActors()) {
        if (brv.get(a) == 1L
            && this.longestParallelTiming.floatValue() / this.actorTiming.get(a).floatValue() >= limitRatio
            && topoOrder.get(a).equals(rank) && ((grainListcopy.isEmpty()) || (grainListcopy.isEmpty()
                && !a.getDataInputPorts().stream().noneMatch(x -> x.getFifo().isHasADelay())))) {
          grainList.add(a);
        }
      }
      // if there is more than one candidate add it to the list
      if (grainList.isEmpty() && grainListcopy.size() > 1 || rank == this.graph.getExecutableActors().size()) {
        endList = true;
      } else if (grainList.isEmpty() && grainListcopy.size() == 1) {
        grainListcopy.clear();
      } else {
        grainListcopy.addAll(grainList);

        rank++;
        grainList.clear();
      }

    }
    if (grainListcopy.size() == 1) {
      grainListcopy.clear();
    }
    if (!grainListcopy.isEmpty()) {
      this.identifiedGRAINs.add(grainListcopy);
    }
  }

  private void computeTopoOrder() {
    boolean isLast = false;
    final List<AbstractActor> curentRankList = new LinkedList<>();
    final List<AbstractActor> nextRankList = new LinkedList<>();
    // Init
    for (final DataInputInterface i : graph.getDataInputInterfaces()) {
      if (i.getDirectSuccessors().get(0) instanceof Actor || i.getDirectSuccessors().get(0) instanceof SpecialActor) {
        this.topoOrder.put((AbstractActor) i.getDirectSuccessors().get(0), 0L);
        curentRankList.add((AbstractActor) i.getDirectSuccessors().get(0));
      }
    }
    for (final AbstractActor a : graph.getActors()) {
      if (a.getDataInputPorts().isEmpty() && (a instanceof Actor || a instanceof SpecialActor)) {
        this.topoOrder.put(a, 0L);
        curentRankList.add(a);
      }
    }

    // Loop
    Long currentRank = 1L;
    while (!isLast) {
      for (final AbstractActor a : curentRankList) {
        if (!a.getDirectSuccessors().isEmpty()) {
          for (final Vertex aa : a.getDirectSuccessors()) {
            if (!(aa instanceof DataOutputInterface) || !(aa instanceof DelayActor)) {
              boolean flag = false;
              for (final DataInputPort din : ((AbstractActor) aa).getDataInputPorts()) {
                final AbstractActor aaa = (AbstractActor) din.getFifo().getSource();
                if (!topoOrder.containsKey(aaa)
                    && (aaa instanceof Actor || aaa instanceof SpecialActor || aaa instanceof PiGraph)
                    && !din.getFifo().isHasADelay() || nextRankList.contains(aaa)) {
                  flag = true;
                }
              }
              if (!flag && !topoOrder.containsKey(aa)
                  && (aa instanceof Actor || aa instanceof SpecialActor || aa instanceof PiGraph)) {
                topoOrder.put((AbstractActor) aa, currentRank);
                nextRankList.add((AbstractActor) aa);
              }

            }
          }
        }

      }
      if (nextRankList.isEmpty()) {
        isLast = true;
      }
      curentRankList.clear();
      curentRankList.addAll(nextRankList);
      nextRankList.clear();
      currentRank++;
    }
    // add getter setter
    for (final Delay d : graph.getDelays()) {
      if (d.hasGetterActor() && (d.getGetterActor() instanceof Actor || d.getGetterActor() instanceof SpecialActor)) {
        this.topoOrder.put(d.getGetterActor(), currentRank);

      }
      if (d.hasSetterActor() && (d.getSetterActor() instanceof Actor || d.getSetterActor() instanceof SpecialActor)) {
        this.topoOrder.put(d.getSetterActor(), 0L);

      }
    }
  }

  @Override
  public Boolean caseAbstractActor(AbstractActor base) {

    final boolean heterogenousRate = base.getDataOutputPorts().stream()
        .anyMatch(x -> doSwitch(x.getFifo()).booleanValue());
    // Return false if rates are not homogeneous or that the corresponding actor was a sink (no output)

    return !heterogenousRate || base.getDataOutputPorts().isEmpty() || nonClusterableList.contains(base);
  }

  @Override
  public Boolean caseFifo(Fifo fifo) {
    if (!(fifo.getTarget() instanceof Actor)) {
      return false;
    }

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

    // Return true if rates are heterogeneous and that no delay is involved

    return !hiddenDelay && !firstShift && !secondShift && !(fifo.getSource() instanceof DataInputInterface)
        && !(fifo.getTarget() instanceof DataOutputInterface);
  }

}
