/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
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
package org.ietr.preesm.ui.pimm.features;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Port;

// TODO: Auto-generated Javadoc
/**
 * Custom feature to move up a port.
 *
 * @author jheulot
 * @author kdesnos
 *
 */
public class MoveUpActorPortFeature extends AbstractCustomFeature {

  /** The has done changes. */
  protected boolean hasDoneChanges = false;

  /** The Constant HINT. */
  public static final String HINT = "up";

  /**
   * Default Constructor.
   *
   * @param fp
   *          the feature provider
   */
  public MoveUpActorPortFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
   */
  @Override
  public String getName() {
    return "Move up Port\tCtrl+Up_Arrow";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
   */
  @Override
  public String getDescription() {
    return "Move up the Port";
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.
   * ICustomContext)
   */
  @Override
  public boolean canExecute(final ICustomContext context) {
    // allow move up if exactly one pictogram element
    // representing a Port is selected
    // and it is not the first port
    boolean ret = false;
    final PictogramElement[] pes = context.getPictogramElements();
    if ((pes != null) && (pes.length == 1)) {
      final Object bo = getBusinessObjectForPictogramElement(pes[0]);
      if (bo instanceof Port) {
        final Port port = (Port) bo;
        if (port.eContainer() instanceof ExecutableActor) {
          final ExecutableActor actor = (ExecutableActor) (port.eContainer());
          switch (port.getKind()) {
            case DATA_INPUT:
              ret = actor.getDataInputPorts().size() > 1;
              ret = ret && (actor.getDataInputPorts().indexOf(port) > 0);
              break;
            case DATA_OUTPUT:
              ret = actor.getDataOutputPorts().size() > 1;
              ret = ret && (actor.getDataOutputPorts().indexOf(port) > 0);
              break;
            case CFG_INPUT:
              ret = actor.getConfigInputPorts().size() > 1;
              ret = ret && (actor.getConfigInputPorts().indexOf(port) > 0);
              break;
            case CFG_OUTPUT:
              ret = actor.getConfigOutputPorts().size() > 1;
              ret = ret && (actor.getConfigOutputPorts().indexOf(port) > 0);
              break;
            default:
          }
        }
      }
    }
    return ret;
  }

  /**
   * Avoid negative coordinates.
   *
   * @return true, if successful
   */
  protected boolean avoidNegativeCoordinates() {
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
   */
  @Override
  public void execute(final ICustomContext context) {
    // Re-check if only one element is selected
    final PictogramElement[] pes = context.getPictogramElements();
    if ((pes != null) && (pes.length == 1)) {
      final PictogramElement anchorToMoveUp = pes[0];
      final Object bo = getBusinessObjectForPictogramElement(anchorToMoveUp);
      if (bo instanceof Port) {
        Port portToMoveUp = null;
        Port portToMoveDown = null;
        ExecutableActor actor;

        portToMoveUp = (Port) bo;
        actor = (ExecutableActor) (portToMoveUp.eContainer());

        int portToMoveUpIndex = -1;
        int portToMoveDownIndex = -1;
        // Switch Port into Actor Object
        switch (portToMoveUp.getKind()) {
          case DATA_INPUT:
            portToMoveUpIndex = actor.getDataInputPorts().indexOf(portToMoveUp);
            portToMoveDown = actor.getDataInputPorts().get(portToMoveUpIndex - 1);
            break;
          case DATA_OUTPUT:
            portToMoveUpIndex = actor.getDataOutputPorts().indexOf(portToMoveUp);
            portToMoveDown = actor.getDataOutputPorts().get(portToMoveUpIndex - 1);
            break;
          case CFG_INPUT:
            portToMoveUpIndex = actor.getConfigInputPorts().indexOf(portToMoveUp);
            portToMoveDown = actor.getConfigInputPorts().get(portToMoveUpIndex - 1);
            break;
          case CFG_OUTPUT:
            portToMoveUpIndex = actor.getConfigOutputPorts().indexOf(portToMoveUp);
            portToMoveDown = actor.getConfigOutputPorts().get(portToMoveUpIndex - 1);
            break;
          default:
        }
        portToMoveDownIndex = portToMoveUpIndex - 1;

        // Get Graphical Elements
        int anchorToMoveUpIndex;
        int anchorToMoveDownIndex = -1;
        final ContainerShape csActor = (ContainerShape) ((BoxRelativeAnchor) anchorToMoveUp)
            .getReferencedGraphicsAlgorithm().getPictogramElement();
        final EList<Anchor> anchors = csActor.getAnchors();

        anchorToMoveUpIndex = anchors.indexOf(anchorToMoveUp);
        Anchor anchorToMoveDown = null;
        for (final Anchor a : anchors) {
          if (a.getLink().getBusinessObjects().get(0).equals(portToMoveDown)) {
            anchorToMoveDownIndex = anchors.indexOf(a);
            anchorToMoveDown = a;
            break;
          }
        }

        if (anchorToMoveDownIndex == -1) {
          return;
        }

        // Get the FFC connected to these ports (if any)
        final List<Point> pointsToMoveUp = getPointToMove((Anchor) anchorToMoveUp, portToMoveUp);
        final List<Point> pointsToMoveDown = getPointToMove(anchorToMoveDown, portToMoveDown);

        // Do Modifications
        this.hasDoneChanges = true;

        switch (portToMoveUp.getKind()) {
          case DATA_INPUT:
            actor.getDataInputPorts().move(portToMoveDownIndex, portToMoveUpIndex);
            break;
          case DATA_OUTPUT:
            actor.getDataOutputPorts().move(portToMoveDownIndex, portToMoveUpIndex);
            break;
          case CFG_INPUT:
            actor.getConfigInputPorts().move(portToMoveDownIndex, portToMoveUpIndex);
            break;
          case CFG_OUTPUT:
            actor.getConfigOutputPorts().move(portToMoveDownIndex, portToMoveUpIndex);
            break;
          default:
        }
        anchors.move(anchorToMoveDownIndex, anchorToMoveUpIndex);

        // Layout the Port
        layoutPictogramElement(anchorToMoveUp);
        updatePictogramElement(anchorToMoveUp);

        // Layout the actor
        layoutPictogramElement(csActor);
        updatePictogramElement(csActor);

        // Realign the ports to move
        realignPoints((Anchor) anchorToMoveUp, pointsToMoveUp);
        realignPoints(anchorToMoveDown, pointsToMoveDown);
      }
    }
  }

  /**
   * Realign points.
   *
   * @param anchor
   *          the anchor
   * @param points
   *          the points
   */
  protected void realignPoints(final Anchor anchor, final List<Point> points) {
    final ILocation anchorLoc = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(anchor);
    final int yPos = anchorLoc.getY() + (anchor.getGraphicsAlgorithm().getHeight() / 2);
    for (final Point p : points) {
      p.setY(yPos);
    }
  }

  /**
   * Gets the point to move.
   *
   * @param anchor
   *          the anchor
   * @param portToMove
   *          the port to move
   * @return the point to move
   * @throws RuntimeException
   *           the runtime exception
   */
  protected List<Point> getPointToMove(final Anchor anchor, final Port portToMove) throws RuntimeException {
    List<Connection> edges = new ArrayList<>();
    boolean fromStart = true;
    switch (portToMove.getKind()) {
      case DATA_INPUT:
        edges = anchor.getIncomingConnections();
        fromStart = false;
        break;

      case DATA_OUTPUT:
        edges = anchor.getOutgoingConnections();
        fromStart = true;
        break;

      case CFG_INPUT:
        edges = anchor.getIncomingConnections();
        fromStart = false;
        break;

      case CFG_OUTPUT:
        final EList<Dependency> outgoingDependencies = ((ConfigOutputPort) portToMove).getOutgoingDependencies();
        fromStart = true;
        if (outgoingDependencies.size() > 0) {
          edges = anchor.getOutgoingConnections();
        }
        break;
      default:
    }

    // If the port is connected to edges
    final List<Point> pointsToMove = new ArrayList<>();
    for (final Connection edge : edges) {
      final LinkedList<Point> points = new LinkedList<>(((FreeFormConnection) edge).getBendpoints());
      final Iterator<Point> iter = (fromStart) ? points.iterator() : points.descendingIterator();
      for (; iter.hasNext();) {
        final Point point = iter.next();
        final ILocation anchorLoc = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(anchor);
        final int posY = anchorLoc.getY() + (anchor.getGraphicsAlgorithm().getHeight() / 2);
        if ((((posY + 1) >= point.getY()) && ((posY - 1) <= point.getY()))) {
          pointsToMove.add(point);
        } else {
          break;
        }
      }
    }
    return pointsToMove;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.graphiti.features.impl.AbstractFeature#hasDoneChanges()
   */
  @Override
  public boolean hasDoneChanges() {
    return this.hasDoneChanges;
  }

}
