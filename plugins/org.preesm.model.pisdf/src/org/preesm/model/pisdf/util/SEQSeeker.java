/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Dylan Gageot [gageot.dylan@gmail.com] (2020)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
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
 * This class is used to seek distribute sequential actors in several PiGraph in order to obtain subgraphs of uniform
 * time and fitting with the number of Procesing elements
 *
 * @author orenaud
 *
 */
public class SEQSeeker extends PiMMSwitch<Boolean> {

  /**
   * Input graph.
   */
  final PiGraph graph;

  final int                       nPEs;
  final Map<AbstractVertex, Long> brv;

  /**
   * List of identified SEQs.
   */
  final List<List<AbstractActor>> identifiedSEQs;
  final List<AbstractActor>       nonClusterableList;
  final Map<AbstractVertex, Long> actorTiming;

  final Map<AbstractActor, Long> topoOrder;
  final int                      subgraphGen;

  /**
   * Builds a SEQSeeker based on a input graph.
   *
   * @param inputGraph
   *          Input graph to search in.
   * @param numberOfPEs
   *          numberof PEs
   * @param brv
   *          repetition vector
   * @param subgraphGen
   *          no
   */
  public SEQSeeker(final PiGraph inputGraph, int numberOfPEs, Map<AbstractVertex, Long> brv,
      List<AbstractActor> nonClusterableList, Map<AbstractVertex, Long> actorTiming, int subgraphGen) {
    this.graph = inputGraph;
    this.identifiedSEQs = new LinkedList<>();
    this.nPEs = numberOfPEs;
    this.brv = brv;
    this.actorTiming = actorTiming;
    this.topoOrder = new HashMap<>();
    this.nonClusterableList = nonClusterableList;
    this.subgraphGen = subgraphGen;
  }

  /**
   * Seek for SRV chain in the input graph.
   *
   * @return List of identified URC chain.
   */
  public List<List<AbstractActor>> seek() {
    // Clear the list of identified URCs
    this.identifiedSEQs.clear();
    if ((!this.graph.getName().contains("seq_") && subgraphGen == 1) || (subgraphGen == 2)) {
      // compute topological order
      computeTopoOrder();
      // fill seq list
      computeSeqList(); // SpecialActor)
    }
    // Return identified URCs
    return identifiedSEQs;
  }

  private void computeSeqList() {
    final List<AbstractActor> seqList = new LinkedList<>();
    final List<AbstractActor> seqListcopy = new LinkedList<>();
    Long rank = 0L;
    boolean endList = false;
    // check if ranks contain GRAIN candidate (RV< and negligible timing)
    while (!endList) {
      for (final AbstractActor a : this.graph.getExecutableActors()) {
        if (brv.get(a) < nPEs && topoOrder.get(a).equals(rank) && ((seqListcopy.isEmpty())
            || (!seqListcopy.isEmpty() && !a.getDataInputPorts().stream().anyMatch(x -> x.getFifo().isHasADelay())))) {
          seqList.add(a);
        }
      }
      // if there is more than one candidate add it to the list
      if (seqList.isEmpty() && seqListcopy.size() > 1 || rank == this.graph.getExecutableActors().size()) {
        endList = true;
      } else if (seqList.isEmpty() && seqListcopy.size() == 1) {
        seqListcopy.clear();
      } else {
        seqListcopy.addAll(seqList);

        rank++;
        seqList.clear();
      }

    }
    if (seqListcopy.size() == 1) {
      seqListcopy.clear();
    }
    if (!seqListcopy.isEmpty()) {
      this.identifiedSEQs.add(seqListcopy);
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
                // for (Vertex aaa : aa.getDirectPredecessors()) {
                if (!topoOrder.containsKey(aaa)
                    && (aaa instanceof Actor || aaa instanceof SpecialActor || aaa instanceof PiGraph)
                    && !din.getFifo().isHasADelay() || nextRankList.contains(aaa)) {
                  // predecessors
                  // are in the
                  // list
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
      if (d.hasGetterActor()) {
        if (d.getGetterActor() instanceof Actor || d.getGetterActor() instanceof SpecialActor) {
          this.topoOrder.put(d.getGetterActor(), currentRank);
        }
      }
      if (d.hasSetterActor()) {
        if (d.getSetterActor() instanceof Actor || d.getSetterActor() instanceof SpecialActor) {
          this.topoOrder.put(d.getSetterActor(), 0L);
        }
      }
    }
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
    final boolean cycle = false;

    // Return true if rates are heterogeneous and that no delay is involved
    if (!hiddenDelay && !firstShift && !secondShift && !cycle && !(fifo.getSource() instanceof DataInputInterface)
        && !(fifo.getTarget() instanceof DataOutputInterface)) {

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
