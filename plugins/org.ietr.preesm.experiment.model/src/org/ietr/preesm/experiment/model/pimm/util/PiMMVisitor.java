/*******************************************************************************
 * Copyright or © or Copr. 2014 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.experiment.model.pimm.util;

import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.BroadcastActor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.HRefinement;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.JoinActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;

// TODO: Auto-generated Javadoc
/**
 * The Class PiMMVisitor.
 */
public abstract class PiMMVisitor {

  /**
   * Visit.
   *
   * @param v
   *          the v
   */
  public void visit(final PiMMVisitable v) {
    v.accept(this);
  }

  /**
   * Visit abstract actor.
   *
   * @param aa
   *          the aa
   */
  public abstract void visitAbstractActor(AbstractActor aa);

  /**
   * Visit abstract vertex.
   *
   * @param av
   *          the av
   */
  public abstract void visitAbstractVertex(AbstractVertex av);

  /**
   * Visit actor.
   *
   * @param a
   *          the a
   */
  public abstract void visitActor(Actor a);

  /**
   * Visit config input interface.
   *
   * @param cii
   *          the cii
   */
  public abstract void visitConfigInputInterface(ConfigInputInterface cii);

  /**
   * Visit config input port.
   *
   * @param cip
   *          the cip
   */
  public abstract void visitConfigInputPort(ConfigInputPort cip);

  /**
   * Visit config output interface.
   *
   * @param coi
   *          the coi
   */
  public abstract void visitConfigOutputInterface(ConfigOutputInterface coi);

  /**
   * Visit config output port.
   *
   * @param cop
   *          the cop
   */
  public abstract void visitConfigOutputPort(ConfigOutputPort cop);

  /**
   * Visit data input interface.
   *
   * @param dii
   *          the dii
   */
  public abstract void visitDataInputInterface(DataInputInterface dii);

  /**
   * Visit data input port.
   *
   * @param dip
   *          the dip
   */
  public abstract void visitDataInputPort(DataInputPort dip);

  /**
   * Visit data output interface.
   *
   * @param doi
   *          the doi
   */
  public abstract void visitDataOutputInterface(DataOutputInterface doi);

  /**
   * Visit data output port.
   *
   * @param dop
   *          the dop
   */
  public abstract void visitDataOutputPort(DataOutputPort dop);

  /**
   * Visit delay.
   *
   * @param d
   *          the d
   */
  public abstract void visitDelay(Delay d);

  /**
   * Visit dependency.
   *
   * @param d
   *          the d
   */
  public abstract void visitDependency(Dependency d);

  /**
   * Visit expression.
   *
   * @param e
   *          the e
   */
  public abstract void visitExpression(Expression e);

  /**
   * Visit fifo.
   *
   * @param f
   *          the f
   */
  public abstract void visitFifo(Fifo f);

  /**
   * Visit interface actor.
   *
   * @param ia
   *          the ia
   */
  public abstract void visitInterfaceActor(InterfaceActor ia);

  /**
   * Visit I setter.
   *
   * @param is
   *          the is
   */
  public abstract void visitISetter(ISetter is);

  /**
   * Visit parameter.
   *
   * @param p
   *          the p
   */
  public abstract void visitParameter(Parameter p);

  /**
   * Visit parameterizable.
   *
   * @param p
   *          the p
   */
  public abstract void visitParameterizable(Parameterizable p);

  /**
   * Visit pi graph.
   *
   * @param pg
   *          the pg
   */
  public abstract void visitPiGraph(PiGraph pg);

  /**
   * Visit port.
   *
   * @param p
   *          the p
   */
  public abstract void visitPort(Port p);

  /**
   * Visit refinement.
   *
   * @param r
   *          the r
   */
  public abstract void visitRefinement(Refinement r);

  /**
   * Visit function parameter.
   *
   * @param f
   *          the f
   */
  public abstract void visitFunctionParameter(FunctionParameter f);

  /**
   * Visit function prototype.
   *
   * @param f
   *          the f
   */
  public abstract void visitFunctionPrototype(FunctionPrototype f);

  /**
   * Visit H refinement.
   *
   * @param h
   *          the h
   */
  public abstract void visitHRefinement(HRefinement h);

  /**
   * Visit data port.
   *
   * @param p
   *          the p
   */
  public abstract void visitDataPort(DataPort p);

  /**
   * Visit broadcast actor.
   *
   * @param ba
   *          the ba
   */
  public abstract void visitBroadcastActor(BroadcastActor ba);

  /**
   * Visit join actor.
   *
   * @param ja
   *          the ja
   */
  public abstract void visitJoinActor(JoinActor ja);

  /**
   * Visit fork actor.
   *
   * @param fa
   *          the fa
   */
  public abstract void visitForkActor(ForkActor fa);

  /**
   * Visit round buffer actor.
   *
   * @param rba
   *          the rba
   */
  public abstract void visitRoundBufferActor(RoundBufferActor rba);

  /**
   * Visit executable actor.
   *
   * @param ea
   *          the ea
   */
  public abstract void visitExecutableActor(ExecutableActor ea);
}
