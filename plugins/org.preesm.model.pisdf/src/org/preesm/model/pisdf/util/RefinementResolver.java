/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
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

import java.util.List;
import java.util.Optional;
import org.eclipse.core.runtime.Path;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.PiSDFRefinement;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.serialize.PiParser;

/**
 *
 * @author anmorvan
 *
 */
public final class RefinementResolver extends PiMMSwitch<AbstractActor> {

  private RefinementResolver() {
    // disallow external instantiation
  }

  public static final AbstractActor resolveAbstractActor(final Refinement r) {
    return new RefinementResolver().doSwitch(r);
  }

  @Override
  public AbstractActor casePiSDFRefinement(final PiSDFRefinement ref) {
    final Path path = Optional.ofNullable(ref.getFilePath()).map(Path::new).orElse(null);
    if ((path != null) && path.getFileExtension().equals("pi")) {
      final PiGraph piGraph = PiParser.getPiGraph(ref.getFilePath());
      piGraph.setName(((Actor) ref.eContainer()).getName());
      return piGraph;
    }
    return null;
  }

  @Override
  public AbstractActor caseCHeaderRefinement(final CHeaderRefinement ref) {
    if (ref.getLoopPrototype() == null) {
      return null;
    }
    // Create the actor returned by the function
    final AbstractActor result = PiMMUserFactory.instance.createActor();

    // Create all its ports corresponding to parameters of the
    // prototype
    final FunctionPrototype loopProto = ref.getLoopPrototype();
    final List<FunctionArgument> loopParameters = loopProto.getArguments();
    for (final FunctionArgument param : loopParameters) {
      if (!param.isIsConfigurationParameter()) {
        // Data Port
        if (param.getDirection().equals(Direction.IN)) {
          // Data Input
          final DataInputPort port = PiMMUserFactory.instance.createDataInputPort();
          port.setName(param.getName());
          result.getDataInputPorts().add(port);
        } else {
          // Data Output
          final DataOutputPort port = PiMMUserFactory.instance.createDataOutputPort();
          port.setName(param.getName());
          result.getDataOutputPorts().add(port);
        }
        // Config Port
      } else if (param.getDirection().equals(Direction.IN)) {
        // Config Input
        final ConfigInputPort port = PiMMUserFactory.instance.createConfigInputPort();
        port.setName(param.getName());
        result.getConfigInputPorts().add(port);
      } else {
        // Config Output
        final ConfigOutputPort port = PiMMUserFactory.instance.createConfigOutputPort();
        port.setName(param.getName());
        result.getConfigOutputPorts().add(port);
      }
    }
    return result;
  }
}
