/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
package org.ietr.preesm.experiment.model.pimm.util;

import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.ietr.preesm.experiment.model.factory.PiMMUserFactory;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.CHeaderRefinement;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Direction;
import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiSDFRefinement;
import org.ietr.preesm.experiment.model.pimm.Refinement;

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
    if ((ref.getFilePath() != null) && ref.getFilePath().getFileExtension().equals("pi")) {
      final URI refinementURI = URI.createPlatformResourceURI(ref.getFilePath().makeRelative().toString(), true);

      // Check if the file exists
      if (refinementURI != null) {
        final ResourceSet rSet = new ResourceSetImpl();
        Resource resourceRefinement;
        try {
          resourceRefinement = rSet.getResource(refinementURI, true);
          if (resourceRefinement != null) {
            // does resource contain a graph as root object?
            final EList<EObject> contents = resourceRefinement.getContents();
            for (final EObject object : contents) {
              if (object instanceof PiGraph) {
                final PiGraph actor = (PiGraph) object;
                actor.setName(((Actor) ref.eContainer()).getName());
                return actor;
              }
            }
          }
        } catch (final WrappedException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

  @Override
  public AbstractActor caseCHeaderRefinement(final CHeaderRefinement ref) {
    if (ref.getLoopPrototype() != null) {
      // Create the actor returned by the function
      final AbstractActor result = PiMMUserFactory.instance.createActor();

      // Create all its ports corresponding to parameters of the
      // prototype
      final FunctionPrototype loopProto = ref.getLoopPrototype();
      final List<FunctionParameter> loopParameters = loopProto.getParameters();
      for (final FunctionParameter param : loopParameters) {
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
        } else {
          // Config Port
          if (param.getDirection().equals(Direction.IN)) {
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
      }
      return result;
    } else {
      return null;
    }
  }
}
