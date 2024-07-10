/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2023 - 2024) :
 *
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Dardaillon Mickael [mickael.dardaillon@insa-rennes.fr] (2023)
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

package org.preesm.algorithm.schedule.fpga;

import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 * Helper class to generate specific actors for FPGA
 *
 * @author mdardail
 */

public abstract class ActorConstructTransform {

  protected ActorConstructTransform() {
    // Forbids instantiation
  }

  protected static DataInputPort createDataInputPort(final Actor actor, long rate) {
    final DataInputPort inputPort = PiMMUserFactory.instance.createDataInputPort("input");
    inputPort.setExpression(rate);
    actor.getDataInputPorts().add(inputPort);
    return inputPort;
  }

  protected static DataOutputPort createDataOutputPort(final Actor actor, long rate) {
    final DataOutputPort outputPort = PiMMUserFactory.instance.createDataOutputPort("output");
    outputPort.setExpression(rate);
    actor.getDataOutputPorts().add(outputPort);
    return outputPort;
  }

  protected static void connectParameter(final Actor actor, final Parameter param, final String name) {
    final ConfigInputPort width = PiMMUserFactory.instance.createConfigInputPort();
    width.setName(name);
    actor.getConfigInputPorts().add(width);
    final Dependency depInputWidth = PiMMUserFactory.instance.createDependency(param, width);
    actor.getContainingPiGraph().addDependency(depInputWidth);
    width.setIncomingDependency(depInputWidth);
    param.getOutgoingDependencies().add(depInputWidth);
  }

  protected static void createRefinement(final Actor actor, String actorName, String actorFilePath) {
    final FunctionPrototype funcProto = PiMMUserFactory.instance.createFunctionPrototype();
    funcProto.setIsCPPdefinition(true);
    funcProto.setName(actorName);

    createArgument(funcProto, Direction.IN);
    createArgument(funcProto, Direction.OUT);

    final CHeaderRefinement newRefinement = PiMMUserFactory.instance.createCHeaderRefinement();
    newRefinement.setLoopPrototype(funcProto);
    newRefinement.setFilePath(actor.getName() + actorFilePath);
    newRefinement.setRefinementContainer(actor);
    newRefinement.setGenerated(true);
    actor.setRefinement(newRefinement);
  }

  private static void createArgument(FunctionPrototype func, Direction dir) {
    final FunctionArgument arg = PiMMUserFactory.instance.createFunctionArgument();
    arg.setName(dir == Direction.IN ? "input" : "output");
    arg.setDirection(dir);
    arg.setPosition(dir == Direction.IN ? 0 : 1);
    arg.setType(dir == Direction.IN ? "hls::stream<in_t>" : "hls::stream<out_t>");
    arg.setIsCPPdefinition(true);
    func.getArguments().add(arg);
  }
}
