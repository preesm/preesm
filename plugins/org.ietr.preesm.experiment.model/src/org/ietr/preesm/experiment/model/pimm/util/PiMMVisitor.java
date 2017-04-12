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

/**
 *
 */
public abstract class PiMMVisitor {
  public void visit(final PiMMVisitable v) {
    v.accept(this);
  }

  public abstract void visitAbstractActor(AbstractActor aa);

  public abstract void visitAbstractVertex(AbstractVertex av);

  public abstract void visitActor(Actor a);

  public abstract void visitConfigInputInterface(ConfigInputInterface cii);

  public abstract void visitConfigInputPort(ConfigInputPort cip);

  public abstract void visitConfigOutputInterface(ConfigOutputInterface coi);

  public abstract void visitConfigOutputPort(ConfigOutputPort cop);

  public abstract void visitDataInputInterface(DataInputInterface dii);

  public abstract void visitDataInputPort(DataInputPort dip);

  public abstract void visitDataOutputInterface(DataOutputInterface doi);

  public abstract void visitDataOutputPort(DataOutputPort dop);

  public abstract void visitDelay(Delay d);

  public abstract void visitDependency(Dependency d);

  public abstract void visitExpression(Expression e);

  public abstract void visitFifo(Fifo f);

  public abstract void visitInterfaceActor(InterfaceActor ia);

  public abstract void visitISetter(ISetter is);

  public abstract void visitParameter(Parameter p);

  public abstract void visitParameterizable(Parameterizable p);

  public abstract void visitPiGraph(PiGraph pg);

  public abstract void visitPort(Port p);

  public abstract void visitRefinement(Refinement r);

  public abstract void visitFunctionParameter(FunctionParameter f);

  public abstract void visitFunctionPrototype(FunctionPrototype f);

  public abstract void visitHRefinement(HRefinement h);

  public abstract void visitDataPort(DataPort p);

  public abstract void visitBroadcastActor(BroadcastActor ba);

  public abstract void visitJoinActor(JoinActor ja);

  public abstract void visitForkActor(ForkActor fa);

  public abstract void visitRoundBufferActor(RoundBufferActor rba);

  public abstract void visitExecutableActor(ExecutableActor ea);
}
