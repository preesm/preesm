/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
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
package org.preesm.model.pisdf.util.topology;

import java.util.ArrayList;
import java.util.List;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 * Visits all successors of a given PiSDF element through the connected Fifos.
 */
public abstract class PiSDFSuccessorSwitch extends PiMMSwitch<Boolean> {

  protected final List<AbstractActor> visitedElements = new ArrayList<>();

  @Override
  public Boolean caseAbstractActor(final AbstractActor actor) {
    if (!this.visitedElements.contains(actor)) {
      this.visitedElements.add(actor);
      actor.getDataOutputPorts().forEach(this::doSwitch);
    }
    return true;
  }

  @Override
  public Boolean caseFifo(final Fifo fifo) {
    final DataInputPort sourcePort = fifo.getTargetPort();
    doSwitch(sourcePort);
    return true;
  }

  @Override
  public Boolean caseDataOutputPort(final DataOutputPort dop) {
    final Fifo fifo = dop.getFifo();
    if (fifo != null) {
      doSwitch(fifo);
    }
    return true;

  }

  @Override
  public Boolean caseDataInputPort(final DataInputPort dip) {
    final AbstractActor containingActor = dip.getContainingActor();
    doSwitch(containingActor);
    return true;
  }

  @Override
  public Boolean caseDataOutputInterface(final DataOutputInterface outputInterface) {
    caseAbstractActor(outputInterface);
    final Port graphPort = outputInterface.getGraphPort();
    doSwitch(graphPort);
    return true;
  }

  @Override
  public Boolean casePiGraph(final PiGraph graph) {
    // only visit children if did not start here
    if (!this.visitedElements.isEmpty()) {
      graph.getActors().forEach(this::doSwitch);
    }
    caseAbstractActor(graph);
    return true;
  }

  /**
   *
   * @author anmorvan
   *
   */
  static class SuccessorFoundException extends RuntimeException {
    private static final long serialVersionUID = 4039813342986334156L;
  }

  /**
   *
   * @author anmorvan
   *
   *         Package visible helper switch that throws a {@link SuccessorFoundException} when an actor equals to the
   *         local attribute is encountered while visiting the successors of the subject of the switch. See
   *         {@link PiSDFTopologyHelper#isSuccessor(AbstractActor, AbstractActor)} for usage.
   *
   */
  static class IsSuccessorSwitch extends PiSDFSuccessorSwitch {
    private final AbstractActor potentialPred;

    public IsSuccessorSwitch(final AbstractActor potentialPred) {
      this.potentialPred = potentialPred;
    }

    @Override
    public Boolean caseAbstractActor(final AbstractActor actor) {
      if (actor.equals(this.potentialPred)) {
        throw new SuccessorFoundException();
      }
      return super.caseAbstractActor(actor);
    }

  }

}
