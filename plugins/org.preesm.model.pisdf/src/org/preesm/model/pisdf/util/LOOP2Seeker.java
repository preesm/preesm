package org.preesm.model.pisdf.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.preesm.commons.graph.Vertex;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;

/**
 * This class is used to seek chain of actors in a given PiGraph that form a Single Repetition Vector above PE number
 * (SRV)
 *
 * @author orenaud
 *
 */
public class LOOP2Seeker extends PiMMSwitch<Boolean> {

  /**
   * Input graph.
   */
  final PiGraph                   graph;
  /**
   * List of identified URCs.
   */
  final List<List<AbstractActor>> identifiedLOOP2s;
  /**
   * List of identified URCs.
   */
  final List<Delay>               identifiedDelays;
  final List<Delay>               connectedDelays;

  /**
   * Builds a SRVSeeker based on a input graph.
   *
   * @param inputGraph
   *          Input graph to search in.
   * @param numberOfPEs
   * @param brv
   */
  public LOOP2Seeker(final PiGraph inputGraph) {
    this.graph = inputGraph;
    this.identifiedDelays = new LinkedList<>();
    this.connectedDelays = new LinkedList<>();
    this.identifiedLOOP2s = new LinkedList<>();
  }

  /**
   * Seek for LOOP1 in the input graph.
   *
   * @return List of identified URC chain.
   */
  public List<AbstractActor> seek() {
    // Clear the list of identified URCs
    this.connectedDelays.clear();
    this.identifiedDelays.clear();
    // loop for internal delay
    this.graph.getDelays().stream().forEach(x -> doSwitch(x));
    // if ....
    // retain the biggest
    int max = 0;
    for (final List<AbstractActor> loop2 : this.identifiedLOOP2s) {
      if (loop2.size() < max) {
        this.identifiedLOOP2s.remove(loop2);
      }
      max = loop2.size();
    }
    // this.identifiedLOOP2s.get(2).isEmpty()
    // Return identified LOOP2s
    if (this.identifiedLOOP2s.isEmpty()) {
      return null;
    }
    return identifiedLOOP2s.get(0);
  }

  @Override
  public Boolean caseDelay(Delay delay) {
    final List<AbstractActor> actorLOOP2 = new LinkedList<>();
    final List<AbstractActor> nextlist = new LinkedList<>();
    final AbstractActor first = (AbstractActor) delay.getContainingFifo().getTarget();
    final AbstractActor last = (AbstractActor) delay.getContainingFifo().getSource();

    if (first.equals(last)) {
      return false;
    }
    // actorLOOP2.add(first);
    // nextlist.add(first);
    // int nA = 1;
    // for (int i = 0; i < nA; i++) {
    // for (Vertex n : nextlist.get(i).getDirectSuccessors()) {
    // nextlist.add((AbstractActor) n);
    // nA++;
    // if (n == first) {
    // this.identifiedLOOP2s.add(actorLOOP2);
    // return true;
    // }
    // if (!actorLOOP2.contains(n) && !(n instanceof DataOutputInterface))
    // actorLOOP2.add((AbstractActor) n);
    // }
    // }
    final List<List<AbstractActor>> ranklist = new LinkedList<>();
    final Map<Long, List<AbstractActor>> parallelismCounter = new HashMap<>();
    final List<AbstractActor> la = new ArrayList<>();
    la.add(first);
    parallelismCounter.put(0L, la);
    ranklist.add(la);
    // Loop
    int currentRank = 0;
    int count = 0;
    boolean stop = false;
    while (!stop) {
      for (final AbstractActor a : ranklist.get(currentRank)) {
        final List<AbstractActor> la2 = new ArrayList<>();
        for (final Vertex aa : a.getDirectSuccessors()) {
          boolean flag = false;
          for (final Vertex aaa : aa.getDirectPredecessors()) {
            if (aaa instanceof Actor || aaa instanceof SpecialActor) {
              for (int i = 0; i < ranklist.size(); i++) {
                if (!ranklist.get(i).contains(aaa) && a == first) { //
                  flag = true;
                }
              }
            }
          }
          if (!flag && !la2.contains(aa)) {
            la2.add((AbstractActor) aa);
          }
        }
        count++;
        if (count == ranklist.get(currentRank).size()) {
          count = 0;
          ranklist.add(la2);
          // la.clear();

          currentRank++;
        }
        if (la2.contains(last)) {
          stop = true;
        }
      }
    }
    Long end = 0L;
    for (final Long i : parallelismCounter.keySet()) {
      for (final AbstractActor ii : parallelismCounter.get(i)) {
        if (ii == last) {
          end = i;
        }
      }
    }
    for (int i = 0; i < ranklist.size(); i++) {
      for (final AbstractActor ii : ranklist.get(i)) {
        actorLOOP2.add(ii);
      }
    }

    this.identifiedLOOP2s.add(actorLOOP2);
    return false;
  }

}
