/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2015)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2013)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2012 - 2015)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2015)
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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.ui.pisdf.features.helper.DelayOppositeFifoRetriever;
import org.preesm.ui.pisdf.features.helper.LayoutActorBendpoints;
import org.preesm.ui.pisdf.util.DiagramPiGraphLinkHelper;

/**
 * The Move Feature for {@link AbstractActor}.
 * <p>
 * This class also triggers the move of Fifos and Delays. However, when a delay is selected first, along with other
 * actors, the delay and its connected fifos are not correctly moved. User must ensure to select actors first. It works
 * fine if selection region with mouse.
 *
 *
 * @author kdesnos
 * @author ahonorat
 */
public class MoveAbstractActorFeature extends DefaultMoveShapeFeature
    implements DelayOppositeFifoRetriever, LayoutActorBendpoints {

  /** The Constant BENDPOINT_SPACE. */
  public static final int BENDPOINT_SPACE = 20;

  /** The moved connections, linked to the abstract actor. */
  Set<FreeFormConnection> movedConnections = new HashSet<>();

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

    if ((deltaX == 0) && (deltaY == 0)) {
      // Early exit
      return;
    }

    final Set<FreeFormConnection> connectionSet = new HashSet<>();

    final FreeFormConnection[] containerConnections = calculateContainerConnectionsLocal(context);
    Collections.addAll(connectionSet, containerConnections);

    final FreeFormConnection[] connectedConnections = calculateConnectedConnectionsLocal(context);
    Collections.addAll(connectionSet, connectedConnections);

    // Check if the delay is selected in the graphical interface
    final PictogramElement[] selectedPictogramElements = getDiagramBehavior().getDiagramContainer()
        .getSelectedPictogramElements();

    final List<PictogramElement> selectedPEs = new ArrayList<>(Arrays.asList(selectedPictogramElements));
    final List<Object> listBO = selectedPEs.stream().map(e -> getBusinessObjectForPictogramElement(e))
        .collect(Collectors.toList());

    for (final Delay d : implicitlyMovedDelay) {
      if (!listBO.contains(d)) {
        listBO.add(d);
        final ContainerShape cs = DiagramPiGraphLinkHelper.getDelayPE(getDiagram(), d.getContainingFifo());
        selectedPEs.add(cs);
      }
    }

    final List<Object> listBOd = new ArrayList<>();
    for (final Object o : listBO) {
      if (o instanceof final Delay delay) {
        listBOd.add(delay.getActor());
      } else {
        listBOd.add(o);
      }
    }

    // add fifo to/from getter/setter if also selected
    findExtraConnectedDelaysAndConnections(implicitlyMovedDelay, listBOd, selectedPEs, connectionSet,
        implicitlyMovedDelay);

    // Move implicitlyMovedDelays
    implicitlyMovedDelay.forEach(del -> moveDelay(context, del));

    connectionSet.forEach(conn -> moveAllBendpointsOnFFConnectionLocal(conn, deltaX, deltaY));
  }

  /**
   * We also move fifo between delays and setter/getter, and delays that they may contain.
   * <p>
   * This function may not be complete.
   *
   * @param delaysToTest
   *          The current list of delays to test.
   * @param listBOd
   *          The current list of selected business objects with delay actors instead of delay.
   * @param selectedPEs
   *          The current list of selected elements.
   */
  protected void findExtraConnectedDelaysAndConnections(final Set<Delay> delaysToTest, final List<Object> listBOd,
      final List<PictogramElement> selectedPEs, Set<FreeFormConnection> allConnections, Set<Delay> allDelays) {

    final Set<FreeFormConnection> newConnections = new HashSet<>();
    final Set<Delay> newDelays = new HashSet<>();

    for (final Delay del : delaysToTest) {
      final ContainerShape cs = DiagramPiGraphLinkHelper.getDelayPE(getDiagram(), del.getContainingFifo());

      final int indexSetter = listBOd.indexOf(del.getSetterActor());
      if (indexSetter >= 0) {
        final PictogramElement setter = selectedPEs.get(indexSetter);
        for (final Anchor ac : cs.getAnchors()) {
          for (final Connection in : ac.getIncomingConnections()) {

            final Object o = getBusinessObjectForPictogramElement(in);
            if (o instanceof final Fifo f) {
              final Delay d = f.getDelay();
              Connection opposite = null;
              if (d != null && !allDelays.contains(d)) {
                newDelays.add(d);
                opposite = getSourceConnection(this, d, in.getStart().getParent(), in);
              }
              final Connection testSetterCo = opposite == null ? in : opposite;

              if (testSetterCo.getStart().getParent() == setter) {
                if (!allConnections.contains(in)) {
                  newConnections.add((FreeFormConnection) in);
                }
                if (opposite != null) {
                  newConnections.add((FreeFormConnection) opposite);
                }
              }
            }
          }
        }
      }

      final int indexGetter = listBOd.indexOf(del.getGetterActor());
      if (indexGetter >= 0 && !(del.getGetterActor() instanceof DelayActor)) {
        final PictogramElement getter = selectedPEs.get(indexGetter);
        for (final Anchor ac : cs.getAnchors()) {
          for (final Connection out : ac.getOutgoingConnections()) {

            final Object o = getBusinessObjectForPictogramElement(out);
            if (o instanceof final Fifo f) {
              final Delay d = f.getDelay();
              Connection opposite = null;
              if (d != null && !allDelays.contains(d)) {
                newDelays.add(d);
                opposite = getTargetConnection(this, d, out.getEnd().getParent(), out);
              }
              final Connection testGetterCo = opposite == null ? out : opposite;

              if (testGetterCo.getEnd().getParent() == getter) {
                if (!allConnections.contains(out)) {
                  newConnections.add((FreeFormConnection) out);
                }
                if (opposite != null) {
                  newConnections.add((FreeFormConnection) opposite);
                }
              }

            }
          }
        }
      }
    }

    movedConnections.addAll(newConnections);
    allConnections.addAll(newConnections);

    allDelays.addAll(newDelays);

    if (!newDelays.isEmpty()) {
      findExtraConnectedDelaysAndConnections(newDelays, listBOd, selectedPEs, allConnections, allDelays);
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
    // right delay if several delays have the same properties.)
    final List<PictogramElement> pes = Graphiti.getLinkService().getPictogramElements(getDiagram(), del);

    for (final PictogramElement pe : pes) {
      // Check that the pe is associated to the right delay (in
      // the java Object sense, not equals() one)
      final Object delay = getBusinessObjectForPictogramElement(pe);
      if (delay == del && pe.getGraphicsAlgorithm() != null) {
        final int oldX = pe.getGraphicsAlgorithm().getX();
        final int oldY = pe.getGraphicsAlgorithm().getY();
        // Unless the delay is selected, move it
        pe.getGraphicsAlgorithm().setX(oldX + context.getDeltaX());
        pe.getGraphicsAlgorithm().setY(oldY + context.getDeltaY());
      }
    }
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
  private void moveAllBendpointsOnFFConnectionLocal(final FreeFormConnection connection, final int deltaX,
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
   * This code is a copy from the private method in {@link DefaultMoveShapeFeature}. Seems to retrieve self-loops.
   *
   * @param context
   *          the context
   * @return the free form connection[]
   */
  private FreeFormConnection[] calculateContainerConnectionsLocal(final IMoveShapeContext context) {
    final FreeFormConnection[] ret = new FreeFormConnection[0];

    if (!(context.getShape() instanceof ContainerShape)) {
      // Early exit
      return ret;
    }

    final List<FreeFormConnection> retList = new ArrayList<>();

    final Shape shapeToMove = context.getShape();

    final int x = context.getX();
    final int y = context.getY();

    final int deltaX = x - shapeToMove.getGraphicsAlgorithm().getX();
    final int deltaY = y - shapeToMove.getGraphicsAlgorithm().getY();

    if ((deltaX == 0) && (deltaY == 0)) {
      // Early exit
      return retList.toArray(ret);
    }

    final List<Anchor> anchorsFrom = getAnchorsLocal(shapeToMove);
    final List<Anchor> anchorsTo = new ArrayList<>(anchorsFrom);

    for (final Anchor anchorFrom : anchorsFrom) {

      final Collection<Connection> outgoingConnections = anchorFrom.getOutgoingConnections();

      for (final Connection connection : outgoingConnections) {

        final Object objFifo = getBusinessObjectForPictogramElement(connection);
        Connection targetConnection = null;
        Delay delay = null;
        if (objFifo instanceof final Fifo fifo) {
          delay = fifo.getDelay();
          if (delay != null) {
            // Is the second half of the connection the one given to the delete
            // context, except if delay on setter.
            final AnchorContainer parent = connection.getEnd().getParent();
            final Object obj = getBusinessObjectForPictogramElement(parent);
            if (obj instanceof final Delay d && obj == delay) {
              targetConnection = getTargetConnection(this, d, parent, connection);
            }
          }
        }

        for (final Anchor anchorTo : anchorsTo) {

          final Collection<Connection> incomingConnections = anchorTo.getIncomingConnections();
          if (incomingConnections.contains(connection) && connection instanceof final FreeFormConnection ffc) {
            retList.add(ffc);
          }

          if (targetConnection != null && incomingConnections.contains(targetConnection)
              && targetConnection instanceof final FreeFormConnection targetFfc) {
            this.implicitlyMovedDelay.add(delay);
            retList.add(targetFfc);
            retList.add((FreeFormConnection) connection);
            // we do not add self connections to the list of moved connections since we want the bendpoints
            // to be recreated (they will be aligned and removed otherwise)
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
  private FreeFormConnection[] calculateConnectedConnectionsLocal(final IMoveShapeContext context) {
    final List<FreeFormConnection> retList = new ArrayList<>();
    final Shape shapeToMove = context.getShape();

    final int x = context.getX();
    final int y = context.getY();

    final int deltaX = x - shapeToMove.getGraphicsAlgorithm().getX();
    final int deltaY = y - shapeToMove.getGraphicsAlgorithm().getY();

    if ((deltaX == 0) && (deltaY == 0)) {
      // Early exit
      return retList.toArray(new FreeFormConnection[0]);
    }

    final List<Anchor> anchors = getAnchorsLocal(shapeToMove);

    final PictogramElement[] selectedPictogramElements = getDiagramBehavior().getDiagramContainer()
        .getSelectedPictogramElements();

    if (selectedPictogramElements == null) {
      // Early exit
      return retList.toArray(new FreeFormConnection[0]);
    }

    for (final PictogramElement selectedPictogramElement : selectedPictogramElements) {
      final PictogramElement selPe = selectedPictogramElement;
      final Object objSel = getBusinessObjectForPictogramElement(selPe);
      if (selPe != shapeToMove && (selPe instanceof final Shape selShape) && !(selPe instanceof Diagram)
          && !(objSel instanceof Delay)) {
        final List<Anchor> selShapeAnchors = getAnchorsLocal(selShape);
        for (final Anchor toAnchor : selShapeAnchors) {
          final EList<Connection> incomingConnections = toAnchor.getIncomingConnections();
          for (final Connection inConn : incomingConnections) {

            if (inConn instanceof final FreeFormConnection inCon) {
              final Object objFifo = getBusinessObjectForPictogramElement(inCon);
              Connection sourceConnection = null;
              Delay delay = null;
              if (objFifo instanceof final Fifo fifo) {
                delay = fifo.getDelay();
                if (delay != null) {
                  // Is the second half of the inConn the one given to the delete
                  // context, except if delay on setter.
                  final AnchorContainer parent = inCon.getStart().getParent();
                  final Object obj = getBusinessObjectForPictogramElement(parent);
                  if (obj instanceof final Delay d && obj == delay) {
                    sourceConnection = getSourceConnection(this, d, parent, inCon);
                  }
                }
              }
              final Anchor startAnchor = inConn.getStart();
              if (anchors.contains(startAnchor)) {
                retList.add(inCon);
                this.movedConnections.add(inCon);
              }

              if (sourceConnection != null) {
                final Anchor startSourceAnchor = sourceConnection.getStart();
                if (anchors.contains(startSourceAnchor)) {
                  this.implicitlyMovedDelay.add(delay);
                  retList.add((FreeFormConnection) sourceConnection);
                  retList.add(inCon);
                  this.movedConnections.add((FreeFormConnection) sourceConnection);
                  this.movedConnections.add(inCon);
                }
              }
            }
          }
        }
      }
    }

    return retList.toArray(new FreeFormConnection[0]);
  }

  private List<Anchor> getAnchorsLocal(Shape theShape) {
    final List<Anchor> ret = new ArrayList<>(theShape.getAnchors());
    if (theShape instanceof final ContainerShape containerShape) {
      final List<Shape> children = containerShape.getChildren();
      for (final Shape shape : children) {
        if (shape instanceof ContainerShape) {
          ret.addAll(getAnchorsLocal(shape));
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
  @Override
  protected void postMoveShape(final IMoveShapeContext context) {
    // Here, we layout bendpoint of connections incoming/outgoing to this
    // AbstractVertex Ports
    final ContainerShape cs = (ContainerShape) context.getPictogramElement();

    layoutShapeConnectedToBendpoints(cs, this, movedConnections);
  }
}
