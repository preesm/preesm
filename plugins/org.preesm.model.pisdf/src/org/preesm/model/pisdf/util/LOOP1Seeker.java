package org.preesm.model.pisdf.util;

import java.util.LinkedList;
import java.util.List;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;

/**
 * This class is used to seek chain of actors in a given PiGraph that form a Single Repetition Vector above PE number
 * (SRV)
 *
 * @author orenaud
 *
 */
public class LOOP1Seeker extends PiMMSwitch<Boolean> {

  /**
   * Input graph.
   */
  final PiGraph graph;

  /**
   * List of identified URCs.
   */
  final List<Delay> identifiedDelays;
  final List<Delay> connectedDelays;

  /**
   * Builds a SRVSeeker based on a input graph.
   *
   * @param inputGraph
   *          Input graph to search in.
   * @param numberOfPEs
   * @param brv
   */
  public LOOP1Seeker(final PiGraph inputGraph) {
    this.graph = inputGraph;
    this.identifiedDelays = new LinkedList<>();
    this.connectedDelays = new LinkedList<>();

  }

  /**
   * Seek for LOOP1 in the input graph.
   *
   * @return List of identified LOOP1 chain.
   */
  public boolean seek() {
    // Clear the list of identified URCs
    this.connectedDelays.clear();
    this.identifiedDelays.clear();
    // if hierarchical actor is wrapped by delay
    // this.graph.getDataInputInterfaces().stream().anyMatch(x -> doSwitch(x.getGraphPort().getFifo()));
    this.graph.getDataInputInterfaces().stream().forEach(x -> doSwitch(x.getGraphPort().getFifo()));
    this.graph.getDataOutputInterfaces().stream().forEach(x -> doSwitch(x.getGraphPort().getFifo()));
    // if no delay inside
    if (this.graph.getDelayIndex() == 0 && !this.identifiedDelays.isEmpty() && this.graph.getActorIndex() > 1) {
      return true;
    }
    // Return identified URCs
    return false;
  }

  @Override
  public Boolean caseFifo(Fifo fifo) {
    // Return true if rates are homogeneous and that no delay is involved
    final int i = 0;
    if (fifo.isHasADelay()) {
      if (this.connectedDelays.contains(fifo.getDelay())) {
        this.identifiedDelays.add(fifo.getDelay());
        return true;
      } else {
        this.connectedDelays.add(fifo.getDelay());
      }
    }
    return false;
  }

}
