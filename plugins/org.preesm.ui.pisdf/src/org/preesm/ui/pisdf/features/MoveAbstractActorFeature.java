/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2015)
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
package org.preesm.ui.pisdf.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaCreateService;
import org.eclipse.graphiti.services.IPeLayoutService;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Port;

// TODO: Auto-generated Javadoc
/**
 * The Move Feature for {@link AbstractActor}.
 *
 * @author kdesnos
 */
public class MoveAbstractActorFeature extends DefaultMoveShapeFeature {

  /** The Constant BENDPOINT_SPACE. */
  public static final int BENDPOINT_SPACE = 20;
  // List of the connections whose source and target are both moved by the
  /** The out double connections. */
  // current MoveAbstractActorFeature (if any).
  List<FreeFormConnection> outDoubleConnections = new ArrayList<>();

  /** The in double connections. */
  List<FreeFormConnection> inDoubleConnections = new ArrayList<>();

  // List the delays that should be moved because both their fifo producer and
  /** The implicitly moved delay. */
  // consumers are selected to be moved.
  Set<Delay> implicitlyMovedDelay = new LinkedHashSet<>();

  /**
   * Default constructor for {@link MoveAbstractActorFeature}.
   *
   * @param fp
   *          the feature provider
   */
  public MoveAbstractActorFeature(final IFeatureProvider fp) {
    super(fp);
  }

  /**
   * Move all bendpoints. Move bendpoints within a container shape. This code is a copy from the protected function
   * calling private methods in {@link DefaultMoveShapeFeature}.
   *
   * @param context
   *          the context
   */
  @Override
  protected void moveAllBendpoints(final IMoveShapeContext context) {
    final Shape shapeToMove = context.getShape();

    final int x = context.getX();
    final int y = context.getY();

    final int deltaX = x - shapeToMove.getGraphicsAlgorithm().getX();
    final int deltaY = y - shapeToMove.getGraphicsAlgorithm().getY();

    if ((deltaX != 0) || (deltaY != 0)) {
      final List<FreeFormConnection> connectionList = new ArrayList<>();

      final FreeFormConnection[] containerConnections = calculateContainerConnections(context);
      for (final FreeFormConnection containerConnection : containerConnections) {
        final FreeFormConnection cc = containerConnection;
        if (!connectionList.contains(cc)) {
          connectionList.add(cc);
        }
      }

      final FreeFormConnection[] connectedConnections = calculateConnectedConnections(context);
      for (final FreeFormConnection connectedConnection : connectedConnections) {
        final FreeFormConnection cc = connectedConnection;
        if (!connectionList.contains(cc)) {
          connectionList.add(cc);
        }
      }

      // kdesnos addition
      final FreeFormConnection[] connectedDelayedFifos = calculateConnectedDelayedFifos(context);
      for (final FreeFormConnection cc : connectedDelayedFifos) {
        if (connectionList.contains(cc)) {
          connectionList.remove(cc);
        }
      }

      for (final FreeFormConnection conn : connectionList) {
        moveAllBendpointsOnFFConnection(conn, deltaX, deltaY);
      }

      // Move implicitlyMovedDelays
      for (final Delay del : this.implicitlyMovedDelay) {
        moveDelay(context, del);
      }

      // Kdesnos addition: Store the connectionList to avoid moving them a
      // second time in postMoveShape.
      this.outDoubleConnections.addAll(connectionList);
    }
  }

  /**
   * Move a {@link Delay} and all the bendpoints of the associated {@link Fifo FIFOs} accorting to the given
   * {@link IMoveShapeContext}.
   *
   * @param context
   *          the {@link IMoveShapeContext} containing the deltaX and deltaY of the displacement
   * @param del
   *          the {@link Delay} whose associated graphical elements are to be moved.
   */
  protected void moveDelay(final IMoveShapeContext context, final Delay del) {
    // Get all delays with identical attributes (may not be the
    // right delay is several delays have the same properties.)
    final List<PictogramElement> pes = Graphiti.getLinkService().getPictogramElements(getDiagram(), del);

    // Check if the delay is selected in the graphical interface
    final PictogramElement[] selectedPictogramElements = getDiagramBehavior().getDiagramContainer()
        .getSelectedPictogramElements();
    final List<PictogramElement> selectedPEs = new ArrayList<>(Arrays.asList(selectedPictogramElements));

    for (final PictogramElement pe : pes) {
      // Check that the pe is associated to the right delay (in
      // the java Object sense, not equals() one)
      final Object delay = getBusinessObjectForPictogramElement(pe);
      if (delay == del) {

        if (pe.getGraphicsAlgorithm() != null) {
          final int oldX = pe.getGraphicsAlgorithm().getX();
          final int oldY = pe.getGraphicsAlgorithm().getY();
          // Unless the delay is selected, move it
          if (!selectedPEs.contains(pe)) {
            pe.getGraphicsAlgorithm().setX(oldX + context.getDeltaX());
            pe.getGraphicsAlgorithm().setY(oldY + context.getDeltaY());
          }

          // Move all bendpoints of the FIFO as well
          final List<PictogramElement> fifoPEs = new ArrayList<>();
          final List<Anchor> anchors = getAnchors((Shape) pe);
          for (final Anchor anchor : anchors) {
            // Always move the incoming connection of the delay
            // (even if the delay is selected.
            fifoPEs.addAll(anchor.getIncomingConnections());
            // Move the outgoing connection of the delay only if the
            // delay itself is not selected
            if (!selectedPEs.contains(pe)) {
              fifoPEs.addAll(anchor.getOutgoingConnections());
            }
          }

          for (final PictogramElement fifoPE : fifoPEs) {
            if (fifoPE instanceof FreeFormConnection) {
              moveAllBendpointsOnFFConnection((FreeFormConnection) fifoPE, context.getDeltaX(), context.getDeltaY());
            }
          }
        }
      }
    }
  }

  /**
   * Find FIFOs with a delay and both sides connected to selected actors. Put the {@link Delay} in
   * {@link #implicitlyMovedDelay}.
   *
   * @param context
   *          the context
   * @return the free form connection[]
   */
  private FreeFormConnection[] calculateConnectedDelayedFifos(final IMoveShapeContext context) {
    final List<FreeFormConnection> retList = new ArrayList<>();
    final Shape shapeToMove = context.getShape();

    final int x = context.getX();
    final int y = context.getY();

    final int deltaX = x - shapeToMove.getGraphicsAlgorithm().getX();
    final int deltaY = y - shapeToMove.getGraphicsAlgorithm().getY();

    if ((deltaX != 0) || (deltaY != 0)) {
      final List<Anchor> anchors = getAnchors(shapeToMove);

      // Create a list of the corresponding Ports
      final List<Port> ports = new ArrayList<>();
      for (final Anchor anchor : anchors) {
        final Object bo = getBusinessObjectForPictogramElement(anchor);
        if (bo instanceof Port) {
          ports.add((Port) bo);
        }
      }

      final PictogramElement[] selectedPictogramElements = getDiagramBehavior().getDiagramContainer()
          .getSelectedPictogramElements();
      if (selectedPictogramElements != null) {
        for (final PictogramElement selectedPictogramElement : selectedPictogramElements) {
          final PictogramElement selPe = selectedPictogramElement;
          if ((selPe instanceof Shape) && !(selPe instanceof Diagram)) {
            final Shape selShape = (Shape) selPe;
            for (final Anchor toAnchor : getAnchors(selShape)) {
              final EList<Connection> incomingConnections = toAnchor.getIncomingConnections();
              for (final Connection inConn : incomingConnections) {
                if (inConn instanceof FreeFormConnection) {
                  final Object bo = getBusinessObjectForPictogramElement(inConn);
                  if ((bo instanceof Fifo) && (((Fifo) bo).getDelay() != null)) {
                    final Port srcPort = ((Fifo) bo).getSourcePort();
                    if (ports.contains(srcPort)) {
                      retList.add((FreeFormConnection) inConn);
                      final Delay del = ((Fifo) bo).getDelay();
                      this.implicitlyMovedDelay.add(del);
                      // Add all the connection to the
                      // double connections lists
                      final Fifo fifo = (Fifo) del.getContainingFifo();
                      final List<PictogramElement> fifoPEs = Graphiti.getLinkService()
                          .getPictogramElements(getDiagram(), fifo);
                      for (final PictogramElement fifoPE : fifoPEs) {
                        if (fifoPE instanceof FreeFormConnection) {
                          this.outDoubleConnections.add((FreeFormConnection) fifoPE);
                        }
                      }
                    }
                  }
                }
              }
            }
            for (final Anchor fromAnchor : getAnchors(selShape)) {
              final EList<Connection> outgoingConnections = fromAnchor.getOutgoingConnections();
              for (final Connection outConn : outgoingConnections) {
                if (outConn instanceof FreeFormConnection) {
                  final Object bo = getBusinessObjectForPictogramElement(outConn);
                  if ((bo instanceof Fifo) && (((Fifo) bo).getDelay() != null)) {
                    final Port tgtPort = ((Fifo) bo).getTargetPort();
                    if (ports.contains(tgtPort)) {
                      retList.add((FreeFormConnection) outConn);
                      this.inDoubleConnections.add((FreeFormConnection) outConn);
                      // Not adding delays to
                      // ImplicitlyMovedDelay is voluntary
                      // here. (Doing so would move them
                      // twice)
                      // Add all the connection to the
                      // double connections lists
                      final Delay del = ((Fifo) bo).getDelay();
                      final Fifo fifo = (Fifo) del.getContainingFifo();
                      final List<PictogramElement> fifoPEs = Graphiti.getLinkService()
                          .getPictogramElements(getDiagram(), fifo);
                      for (final PictogramElement fifoPE : fifoPEs) {
                        if (fifoPE instanceof FreeFormConnection) {
                          this.outDoubleConnections.add((FreeFormConnection) fifoPE);
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return retList.toArray(new FreeFormConnection[retList.size()]);
  }

  /**
   * This code is a copy from the private method in {@link DefaultMoveShapeFeature}.
   *
   * @param connection
   *          the connection
   * @param deltaX
   *          the delta X
   * @param deltaY
   *          the delta Y
   */
  private void moveAllBendpointsOnFFConnection(final FreeFormConnection connection, final int deltaX,
      final int deltaY) {
    final List<Point> points = connection.getBendpoints();
    for (int i = 0; i < points.size(); i++) {
      final Point point = points.get(i);
      final int oldX = point.getX();
      final int oldY = point.getY();
      points.set(i, Graphiti.getGaCreateService().createPoint(oldX + deltaX, oldY + deltaY));
    }
  }

  /**
   * This code is a copy from the private method in {@link DefaultMoveShapeFeature}.
   *
   * @param context
   *          the context
   * @return the free form connection[]
   */
  private FreeFormConnection[] calculateContainerConnections(final IMoveShapeContext context) {
    final FreeFormConnection[] ret = new FreeFormConnection[0];

    if (!(context.getShape() instanceof ContainerShape)) {
      return ret;
    }

    final List<FreeFormConnection> retList = new ArrayList<>();

    final Shape shapeToMove = context.getShape();

    final int x = context.getX();
    final int y = context.getY();

    final int deltaX = x - shapeToMove.getGraphicsAlgorithm().getX();
    final int deltaY = y - shapeToMove.getGraphicsAlgorithm().getY();

    if ((deltaX != 0) || (deltaY != 0)) {

      final List<Anchor> anchorsFrom = getAnchors(shapeToMove);
      final List<Anchor> anchorsTo = new ArrayList<>(anchorsFrom);

      for (final Anchor anchorFrom : anchorsFrom) {

        final Collection<Connection> outgoingConnections = anchorFrom.getOutgoingConnections();

        for (final Connection connection : outgoingConnections) {
          for (final Anchor anchorTo : anchorsTo) {

            final Collection<Connection> incomingConnections = anchorTo.getIncomingConnections();
            if (incomingConnections.contains(connection)) {
              if (connection instanceof FreeFormConnection) {
                retList.add((FreeFormConnection) connection);
              }
            }
          }
        }
      }
    }
    return retList.toArray(ret);
  }

  /**
   * This code is a copy from the private method in {@link DefaultMoveShapeFeature}.
   *
   * @param context
   *          the context
   * @return the free form connection[]
   */
  private FreeFormConnection[] calculateConnectedConnections(final IMoveShapeContext context) {
    final List<FreeFormConnection> retList = new ArrayList<>();
    final Shape shapeToMove = context.getShape();

    final int x = context.getX();
    final int y = context.getY();

    final int deltaX = x - shapeToMove.getGraphicsAlgorithm().getX();
    final int deltaY = y - shapeToMove.getGraphicsAlgorithm().getY();

    if ((deltaX != 0) || (deltaY != 0)) {
      final List<Anchor> anchors = getAnchors(shapeToMove);

      final PictogramElement[] selectedPictogramElements = getDiagramBehavior().getDiagramContainer()
          .getSelectedPictogramElements();
      if (selectedPictogramElements != null) {
        for (final PictogramElement selectedPictogramElement : selectedPictogramElements) {
          final PictogramElement selPe = selectedPictogramElement;
          if ((selPe instanceof Shape) && !(selPe instanceof Diagram)) {
            final Shape selShape = (Shape) selPe;
            for (final Anchor toAnchor : getAnchors(selShape)) {
              final EList<Connection> incomingConnections = toAnchor.getIncomingConnections();
              for (final Connection inConn : incomingConnections) {
                if (inConn instanceof FreeFormConnection) {
                  final Anchor startAnchor = inConn.getStart();
                  if (anchors.contains(startAnchor)) {
                    retList.add((FreeFormConnection) inConn);
                  }
                }
              }
            }
            // Kdesnos addition : detect also connectedConnections
            // ending at the current shape to avoid moving them
            // twice in postMoveShape
            for (final Anchor toAnchor : getAnchors(selShape)) {
              final EList<Connection> outgoingConnections = toAnchor.getOutgoingConnections();
              for (final Connection outConn : outgoingConnections) {
                if (outConn instanceof FreeFormConnection) {
                  final Anchor endAnchor = outConn.getEnd();
                  if (anchors.contains(endAnchor)) {
                    this.inDoubleConnections.add((FreeFormConnection) outConn);
                  }
                }
              }
            }
          }
        }
      }
    }
    return retList.toArray(new FreeFormConnection[0]);
  }

  /**
   * This code is a copy from the private method in {@link DefaultMoveShapeFeature}.
   *
   * @param theShape
   *          the the shape
   * @return the anchors
   */
  private List<Anchor> getAnchors(final Shape theShape) {
    final List<Anchor> ret = new ArrayList<>();
    ret.addAll(theShape.getAnchors());

    if (theShape instanceof ContainerShape) {
      final ContainerShape containerShape = (ContainerShape) theShape;
      final List<Shape> children = containerShape.getChildren();
      for (final Shape shape : children) {
        if (shape instanceof ContainerShape) {
          ret.addAll(getAnchors(shape));
        } else {
          ret.addAll(shape.getAnchors());
        }
      }
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature#postMoveShape(org.eclipse.graphiti.features.context.
   * IMoveShapeContext)
   */
  @SuppressWarnings("unchecked")
  @Override
  protected void postMoveShape(final IMoveShapeContext context) {
    // Here, we layout bendpoint of connections incoming/outgoing to this
    // AbstractVertex Ports
    final ContainerShape cs = (ContainerShape) context.getPictogramElement();
    final EList<Anchor> anchors = cs.getAnchors();

    for (final Anchor anchor : anchors) {
      // If the anchor does not correspond to a port, skip the loop
      // (e.g. ChopBoxAnchors are used as connection points for
      // dependencies)
      if (!(getBusinessObjectForPictogramElement(anchor) instanceof Port)) {
        continue;
      }

      // Retrieve the connections of the anchor.
      final List<FreeFormConnection> connections = new ArrayList<>();

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

      connections.removeAll(this.outDoubleConnections);
      connections.removeAll(this.inDoubleConnections);

      for (final FreeFormConnection connection : connections) {
        // Check wether the FIFO corresponding to the connection has a
        // delay
        final Object fifoOrDependency = getBusinessObjectForPictogramElement(connection);

        // If the fifo has no delay, it remove a bendpoint if there are
        // at least two
        // if the fifo has a delay, remove a bendpoint (if any).
        final int nbBendpoints = (((fifoOrDependency != null)
            && ((fifoOrDependency instanceof Fifo) && (((Fifo) fifoOrDependency).getDelay() != null)))
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
            pSrc = createService.createPoint(bendpointX + context.getDeltaX(), bendpointY + context.getDeltaY());
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
            pTrgt = createService.createPoint(bendpointX + context.getDeltaX(), bendpointY + context.getDeltaY());
          }
          connection.getBendpoints().add(pTrgt);
        }
      }
    }

    return;
  }
}
