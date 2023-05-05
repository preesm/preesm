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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.SpecialActor;

/**
 * This class is used to seek an isolate actor in a given PiGraph with a RV above PE number that form a Single
 * Repetition Vector (SRV)
 *
 * @author orenaud
 *
 */
public class SRVSeeker extends PiMMSwitch<Boolean> {

  /**
   * Input graph.
   */
  final PiGraph graph;

  final int                       nPEs;
  final Map<AbstractVertex, Long> brv;

  /**
   * List of identified URCs.
   */
  final List<List<AbstractActor>> identifiedSRVs;

  /**
   * Builds a SRVSeeker based on a input graph.
   *
   * @param inputGraph
   *          Input graph to search in.
   * @param numberOfPEs
   *          number of PEs
   * @param brv
   *          repetition vector
   */
  public SRVSeeker(final PiGraph inputGraph, int numberOfPEs, Map<AbstractVertex, Long> brv) {
    this.graph = inputGraph;
    this.identifiedSRVs = new LinkedList<>();
    this.nPEs = numberOfPEs;
    this.brv = brv;
  }

  /**
   * Seek for SRV chain in the input graph.
   *
   * @return List of identified URC chain.
   */
  public List<List<AbstractActor>> seek() {
    // Clear the list of identified URCs
    this.identifiedSRVs.clear();
    // Explore all executable actors of the graph
    this.graph.getActors().stream().filter(x -> x instanceof ExecutableActor).forEach(x -> doSwitch(x));
    // Return identified URCs
    return identifiedSRVs;
  }

  @Override
  public Boolean caseAbstractActor(AbstractActor base) {

    // Check that all fifos are homogeneous and without delay
    final boolean homogeneousOutputRates = base.getDataOutputPorts().stream().allMatch(x -> doSwitch(x).booleanValue());

    final boolean homogeneousInputRates = base.getDataInputPorts().stream().allMatch(x -> doSwitch(x).booleanValue());
    // Return false if rates are not homogeneous or that the corresponding actor was a sink (no output)
    if (!homogeneousInputRates || !homogeneousOutputRates || base instanceof SpecialActor) {
      return false;
    }
    if (brv.get(base) > nPEs && gcd(brv.get(base), (long) nPEs) > 1 && !base.getName().contains("srv")) {
      final List<AbstractActor> actorSRV = new LinkedList<>();
      actorSRV.add(base);
      // if brv > core number, too much parallelism

      this.identifiedSRVs.add(actorSRV);
      return true;
    }

    return false;

  }

  // @Override
  // public Boolean caseFifo(Fifo fifo) {
  // // Return true if rates are homogeneous and that no delay is involved
  // int i = 0;
  // if (!fifo.isHasADelay() && (fifo.getSource() instanceof Actor))
  // return true;
  //
  // if (!fifo.isHasADelay() && (fifo.getSource() instanceof BroadcastActor)
  // && !((BroadcastActor) fifo.getSource()).getDataInputPorts().get(0).getFifo().isHasADelay())
  // return true;
  // if (fifo.isHasADelay()
  // && fifo.getDelay().getExpression().evaluate() > fifo.getSourcePort().getExpression().evaluate()
  // && fifo.getDelay().getExpression().evaluate() > fifo.getTargetPort().getExpression().evaluate())
  // return true;
  // if (fifo.getSource() instanceof BroadcastActor
  // && ((BroadcastActor) fifo.getSource()).getDataInputPorts().get(0).getFifo().isHasADelay()
  // && ((BroadcastActor) fifo.getSource()).getDataInputPorts().get(0).getFifo().getDelay().getExpression()
  // .evaluate() > ((BroadcastActor) fifo.getSource()).getDataInputPorts().get(0).getExpression().evaluate())
  // return true;
  // if (!(fifo.getSource() instanceof DataInputInterface))
  // return true;
  //
  // return false;
  // }

  /**
   * Used to compute the greatest common denominator between 2 long values
   *
   * @param long1
   *          long value 1
   * @param long2
   *          long value 2
   */
  private Long gcd(Long long1, Long long2) {
    if (long2 == 0) {
      return long1;
    }
    return gcd(long2, long1 % long2);
  }

  @Override
  public Boolean caseDataInputPort(DataInputPort din) {
    // Return true if rates are homogeneous and that no delay is involved
    final Fifo fifo = din.getIncomingFifo();
    final int i = 0;
    // test if delay is hidden upper level
    if ((fifo.getSource() instanceof DataInputInterface)) {
      return false;
    }

    if ((!fifo.isHasADelay() && (fifo.getSource() instanceof Actor))
        || (!fifo.isHasADelay() && (fifo.getSource() instanceof PiGraph))) {
      return true;
    }

    if (!fifo.isHasADelay() && (fifo.getSource() instanceof BroadcastActor)
        && !((BroadcastActor) fifo.getSource()).getDataInputPorts().get(0).getFifo().isHasADelay()) {
      return true;
    }
    if (fifo.isHasADelay()
        && fifo.getDelay().getExpression().evaluate() > fifo.getSourcePort().getExpression().evaluate()
        && fifo.getDelay().getExpression().evaluate() > fifo.getTargetPort().getExpression().evaluate()) {
      return true;
    }
    if (fifo.getSource() instanceof BroadcastActor
        && ((BroadcastActor) fifo.getSource()).getDataInputPorts().get(0).getFifo().isHasADelay()
        && ((BroadcastActor) fifo.getSource()).getDataInputPorts().get(0).getFifo().getDelay().getExpression()
            .evaluate() > ((BroadcastActor) fifo.getSource()).getDataInputPorts().get(0).getExpression().evaluate()) {
      return true;
    }

    return false;
  }

  @Override
  public Boolean caseDataOutputPort(DataOutputPort dout) {
    // Return true if rates are homogeneous and that no delay is involved
    final Fifo fifo = dout.getOutgoingFifo();
    final int i = 0;
    if (!fifo.isHasADelay() || (fifo.isHasADelay()
        && fifo.getDelay().getExpression().evaluate() > fifo.getSourcePort().getExpression().evaluate()
        && fifo.getDelay().getExpression().evaluate() > fifo.getTargetPort().getExpression().evaluate())) {
      return true;
    }

    return false;
  }

}
