/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2019)
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
package org.preesm.ui.pisdf.features.helper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.impl.AbstractFeature;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaCreateService;
import org.eclipse.graphiti.services.IPeLayoutService;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Port;
import org.preesm.ui.pisdf.features.MoveAbstractActorFeature;

/**
 * Class dedicated to layout bendpoints on actor ports.
 *
 * @author ahonorat
 */
public interface LayoutActorBendpoints {

  /**
   *
   *
   * @param cs
   *          Actor shape
   * @param af
   *          Feature trigerring this method.
   * @param ignoredConnections
   *          Connections to not take into account while layout.
   */
  @SuppressWarnings("unchecked")
  default void layoutShapeConnectedToBendpoints(final ContainerShape cs, AbstractFeature af,
      final Collection<FreeFormConnection> ignoredConnections) {
    // Here, we layout bendpoint of connections incoming/outgoing to this
    // AbstractVertex Ports
    final EList<Anchor> anchors = cs.getAnchors();

    for (final Anchor anchor : anchors) {
      // If the anchor does not correspond to a port, skip the loop
      // (e.g. ChopBoxAnchors are used as connection points for
      // dependencies)
      if (!(af.getFeatureProvider().getBusinessObjectForPictogramElement(anchor) instanceof Port)) {
        continue;
      }

      // Retrieve the connections of the anchor.
      final Set<FreeFormConnection> connections = new HashSet<>();

      final EList<Connection> iConnections = anchor.getIncomingConnections();

      boolean isSrcMove = false;
      if (!iConnections.isEmpty()) {
        connections.addAll((Collection<? extends FreeFormConnection>) iConnections);
        isSrcMove = false;
      }
      final EList<Connection> oConnections = anchor.getOutgoingConnections();
      if (!oConnections.isEmpty()) {
        connections.addAll((Collection<? extends FreeFormConnection>) oConnections);
        isSrcMove = true;
      }

      // Remove connections whose bendpoints were already moved by
      // moveAllBendpoints call

      connections.removeAll(ignoredConnections);

      for (final FreeFormConnection connection : connections) {
        // Check wether the FIFO corresponding to the connection has a
        // delay
        final Object fifoOrDependency = af.getFeatureProvider().getBusinessObjectForPictogramElement(connection);

        // If the fifo has no delay, it remove a bendpoint if there are
        // at least two
        // if the fifo has a delay, remove a bendpoint (if any).
        final int nbBendpoints = (((fifoOrDependency != null)
            && ((fifoOrDependency instanceof final Fifo fifo) && (fifo.getDelay() != null)))
            || (fifoOrDependency instanceof Dependency)) ? -1 : 0;

        // Check if the last or first Bendpoint exists.
        // If so, move it with the same delta as the associated port.
        // (but it will still be removed and recreated.)
        boolean bendpointExists = false;
        int bendpointX = -1;
        int bendpointY = -1;
        final int index = connection.getBendpoints().size() - 1;
        if ((index > nbBendpoints) && !isSrcMove) {
          bendpointExists = true;
          bendpointX = connection.getBendpoints().get(index).getX();
          bendpointY = connection.getBendpoints().get(index).getY();
          connection.getBendpoints().remove(index);
        }
        if ((index > nbBendpoints) && isSrcMove) {
          bendpointExists = true;
          bendpointX = connection.getBendpoints().get(0).getX();
          bendpointY = connection.getBendpoints().get(0).getY();
          connection.getBendpoints().remove(0);
        }

        // Add one bendpoints it didn't exist, move it otherwise
        final IPeLayoutService peLayoutService = Graphiti.getPeLayoutService();
        final IGaCreateService createService = Graphiti.getGaCreateService();
        final int midHeight = (anchor.getGraphicsAlgorithm().getHeight() / 2) - 1;

        // Creation cases
        if (isSrcMove) {
          final ILocation srcLoc = peLayoutService.getLocationRelativeToDiagram(connection.getStart());
          Point pSrc = null;
          if (!bendpointExists) {
            pSrc = createService.createPoint(srcLoc.getX() + MoveAbstractActorFeature.BENDPOINT_SPACE,
                srcLoc.getY() + midHeight);
          } else {
            pSrc = createService.createPoint(bendpointX, bendpointY);
          }
          connection.getBendpoints().add(0, pSrc);
        }
        if (!isSrcMove) {
          final ILocation trgtLoc = peLayoutService.getLocationRelativeToDiagram(connection.getEnd());
          Point pTrgt = null;
          if (!bendpointExists) {
            pTrgt = createService.createPoint(trgtLoc.getX() - MoveAbstractActorFeature.BENDPOINT_SPACE,
                trgtLoc.getY() + midHeight);
          } else {
            pTrgt = createService.createPoint(bendpointX, bendpointY);
          }
          connection.getBendpoints().add(pTrgt);
        }
      }
    }
  }

}
