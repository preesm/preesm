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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.preesm.commons.graph.Vertex;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PersistenceLevel;
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
  // final Map<AbstractVertex, Long> actorTiming;

  final Map<Long, List<AbstractActor>> topoOrderASAP;
  // final int subgraphGen;

  /**
   * Builds a SEQSeeker based on a input graph.
   *
   * @param inputGraph
   *          Input graph to search in.
   * @param numberOfPEs
   *          numberof PEs
   * @param brv
   *          repetition vector
   */
  public SEQSeeker(final PiGraph inputGraph, int numberOfPEs, Map<AbstractVertex, Long> brv,
      List<AbstractActor> nonClusterableList) {
    this.graph = inputGraph;
    this.identifiedSEQs = new LinkedList<>();
    this.nPEs = numberOfPEs;
    this.brv = brv;
    this.topoOrderASAP = new HashMap<>();
    this.nonClusterableList = nonClusterableList;
  }

  /**
   * Seek for SRV chain in the input graph.
   *
   * @return List of identified URC chain.
   */
  public List<List<AbstractActor>> seek() {
    // Clear the list of identified URCs
    this.identifiedSEQs.clear();
    // detect cycle
    if (graph.getDelays().stream().anyMatch(x -> x.getLevel().equals(PersistenceLevel.NONE))) {
      return identifiedSEQs;
    }
    // compute topological order
    computeTopoASAP();
    // fill seq list
    computeSeqList();
    // Return identified URCs
    return identifiedSEQs;
  }

  private void computeSeqList() {
    final List<AbstractActor> seqList = new LinkedList<>();

    for (final Entry<Long, List<AbstractActor>> entry : topoOrderASAP.entrySet()) {
      Long sum = 0L;
      for (final AbstractActor listActor : entry.getValue()) {
        sum = brv.get(listActor) + sum;
      }
      if (sum < nPEs) {
        for (final AbstractActor listActor : entry.getValue()) {
          seqList.add(listActor);
        }
      }
      if (sum >= nPEs && !seqList.isEmpty()) {
        int count = 0;
        for (final AbstractActor a : seqList) {
          if (a instanceof Actor) {
            count++;
          }
        }
        if (count > nPEs) {
          this.identifiedSEQs.add(seqList);
        }
      }

    }

    if (!seqList.isEmpty()) {
      int count = 0;
      for (final AbstractActor a : seqList) {
        if (a instanceof Actor) {
          count++;
        }
      }
      if (count > nPEs) {
        this.identifiedSEQs.add(seqList);
      }
    }
  }

  private void computeTopoASAP() {
    final List<AbstractActor> temp = new ArrayList<>();
    final List<AbstractActor> entry = new ArrayList<>();
    Long rank = 0L;
    for (final AbstractActor a : graph.getActors()) {
      if (!(a instanceof DelayActor)) {
        temp.add(a);
      }
    }
    // feed the 1st rank
    for (final AbstractActor a : graph.getActors()) {
      if (a.getDataInputPorts().isEmpty()) {
        entry.add(a);
        temp.remove(a);
      }
    }
    topoOrderASAP.put(rank, entry);
    // feed the rest
    while (!temp.isEmpty()) {
      final List<AbstractActor> list = new ArrayList<>();
      for (final AbstractActor a : topoOrderASAP.get(rank)) {
        for (final Vertex aa : a.getDirectSuccessors()) {
          // this is piece of art, don't remove
          final Long rankMatch = rank + 1;
          if (aa.getDirectPredecessors().stream().filter(x -> x instanceof Actor || x instanceof SpecialActor)
              .allMatch(x -> topoOrderASAP.entrySet().stream().filter(y -> y.getKey() < rankMatch)
                  .anyMatch(y -> y.getValue().contains(x)))
              && (!list.contains(aa))) {
            if (!((AbstractVertex) aa).getName().contains("snk_")) {
              list.add((AbstractActor) aa);
            }
            temp.remove(aa);

          }
        }
      }
      // orders the list in descending order of the execution time of the actors in the rank
      final List<AbstractActor> sortedList = new ArrayList<>(list);

      rank++;
      topoOrderASAP.put(rank, sortedList);

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

}
