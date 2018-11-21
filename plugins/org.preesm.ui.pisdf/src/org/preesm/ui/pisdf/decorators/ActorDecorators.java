/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2014)
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
package org.preesm.ui.pisdf.decorators;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.PiSDFRefinement;
import org.preesm.model.pisdf.Port;
import org.preesm.ui.pisdf.diagram.PiMMImageProvider;
import org.preesm.ui.pisdf.util.PortEqualityHelper;

// TODO: Auto-generated Javadoc
/**
 * Class providing methods to retrieve the {@link IDecorator} of an {@link Actor}.
 *
 * @author kdesnos
 */
public class ActorDecorators {

  /**
   * Get the {@link IDecorator} indicating if the {@link Actor#isConfigurationActor()}.
   *
   * @param actor
   *          the {@link Actor} to test
   * @param pe
   *          the {@link PictogramElement} of the {@link Actor}
   * @return the {@link IDecorator} or <code>null</code>.
   */
  protected static IDecorator getConfigurationActorDecorator(final Actor actor, final PictogramElement pe) {
    if (actor.isConfigurationActor()) {
      final ImageDecorator imageRenderingDecorator = new ImageDecorator(PiMMImageProvider.IMG_WHITE_DOT_GREY_LINE);

      imageRenderingDecorator.setMessage("Configuration Actor");
      imageRenderingDecorator.setX((pe.getGraphicsAlgorithm().getWidth()) - 13);
      imageRenderingDecorator.setY(5);

      return imageRenderingDecorator;
    }
    return null;
  }

  /**
   * Methods that returns all the {@link IDecorator} for a given {@link Actor} .
   *
   * @param actor
   *          the treated {@link Actor}
   * @param pe
   *          the {@link PictogramElement} to decorate
   * @return the {@link IDecorator} table.
   */
  public static IDecorator[] getDecorators(final Actor actor, final PictogramElement pe) {

    final List<IDecorator> decorators = new ArrayList<>();

    // Check if there is a mismatch with refinement ports
    final IDecorator mismatchDecorator = ActorDecorators.getPortMismatchDecorators(actor);
    if (mismatchDecorator != null) {
      decorators.add(mismatchDecorator);
    }

    // Check if the actor is a configuration actor
    final IDecorator configDecorator = ActorDecorators.getConfigurationActorDecorator(actor, pe);
    if (configDecorator != null) {
      decorators.add(configDecorator);
    }

    final IDecorator[] result = new IDecorator[decorators.size()];
    decorators.toArray(result);

    return result;
  }

  /**
   * Get the {@link IDecorator}s indicating that the {@link Port}s of the {@link Actor} and those of its
   * {@link PiSDFRefinement} are not coherent.
   *
   * @param actor
   *          the {@link Actor} to test.
   * @return the {@link IDecorator} or <code>null</code> if none.
   */
  protected static IDecorator getPortMismatchDecorators(final Actor actor) {
    final IReason reason = ActorDecorators.portsUpdateNeeded(actor);
    if (reason.toBoolean()) {
      final ImageDecorator imageRenderingDecorator = new ImageDecorator(
          IPlatformImageConstants.IMG_ECLIPSE_WARNING_TSK);

      imageRenderingDecorator.setMessage(reason.getText());
      imageRenderingDecorator.setX(1);
      imageRenderingDecorator.setY(2);

      return imageRenderingDecorator;
    }
    return null;
  }

  /**
   * This method will check whether the {@link Port}s of the {@link Actor} {@link PiSDFRefinement} are different from
   * the current.
   *
   * @param actor
   *          the tested {@link Actor}
   * @return a reason stating if an update of the ports is needed
   */
  public static IReason portsUpdateNeeded(final Actor actor) {
    final AbstractActor vertex = actor.getChildAbstractActor();
    if (vertex != null) {
      final Map<SimpleEntry<Port, Port>, IReason> m = PortEqualityHelper.buildEquivalentPortsMap(actor, vertex);

      String reasons = "";
      for (final Entry<SimpleEntry<Port, Port>, IReason> e : m.entrySet()) {
        if (!e.getValue().toBoolean()) {
          if (e.getValue().getText().equals(PortEqualityHelper.NULL_PORT)) {
            final Port actorPort = e.getKey().getKey();
            final Port refinePort = e.getKey().getValue();
            if (actorPort != null) {
              reasons += "\nPort \"" + actorPort.getName() + "\" not present in refinement.";
            } else {
              reasons += "\nRefinement has an extra " + refinePort.getKind() + " port \"" + refinePort.getName()
                  + "\".";
            }
          }
        }
      }
      if (!reasons.equals("")) {
        return Reason.createTrueReason("Ports are out of sync with the refinement." + reasons);
      }
    }
    return Reason.createFalseReason();
  }

}
