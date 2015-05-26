/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
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
 ******************************************************************************/
package org.ietr.preesm.ui.pimm.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaCreateService;
import org.eclipse.graphiti.services.IPeLayoutService;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Port;

/**
 * The Move Feature for {@link AbstractActor}
 * 
 * @author kdesnos
 * 
 */
public class MoveAbstractActorFeature extends DefaultMoveShapeFeature {

	// List of the connections whose source and target are both moved by the
	// current MoveAbstractActorFeature (if any).
	List<FreeFormConnection> outDoubleConnections = new ArrayList<>();
	List<FreeFormConnection> inDoubleConnections = new ArrayList<>();

	// List the delays that should be moved because both their fifo producer and
	// consumers are selected to be moved.
	Set<Delay> implicitlyMovedDelay = new HashSet<Delay>();

	/**
	 * Default constructor for {@link MoveAbstractActorFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public MoveAbstractActorFeature(IFeatureProvider fp) {
		super(fp);
	}

	/**
	 * Move all bendpoints. Move bendpoints within a container shape. This code
	 * is a copy from the protected function calling private methods in
	 * {@link DefaultMoveShapeFeature}.
	 * 
	 * @param context
	 *            the context
	 */
	protected void moveAllBendpoints(IMoveShapeContext context) {
		Shape shapeToMove = context.getShape();

		int x = context.getX();
		int y = context.getY();

		int deltaX = x - shapeToMove.getGraphicsAlgorithm().getX();
		int deltaY = y - shapeToMove.getGraphicsAlgorithm().getY();

		if (deltaX != 0 || deltaY != 0) {
			List<FreeFormConnection> connectionList = new ArrayList<FreeFormConnection>();

			FreeFormConnection[] containerConnections = calculateContainerConnections(context);
			for (int i = 0; i < containerConnections.length; i++) {
				FreeFormConnection cc = containerConnections[i];
				if (!connectionList.contains(cc)) {
					connectionList.add(cc);
				}
			}

			FreeFormConnection[] connectedConnections = calculateConnectedConnections(context);
			for (int i = 0; i < connectedConnections.length; i++) {
				FreeFormConnection cc = connectedConnections[i];
				if (!connectionList.contains(cc)) {
					connectionList.add(cc);
				}
			}

			// kdesnos addition
			FreeFormConnection[] connectedDelayedFifos = calculateConnectedDelayedFifos(context);
			for (int i = 0; i < connectedDelayedFifos.length; i++) {
				FreeFormConnection cc = connectedDelayedFifos[i];
				if (connectionList.contains(cc)) {
					connectionList.remove(cc);
				}
			}

			for (FreeFormConnection conn : connectionList) {
				moveAllBendpointsOnFFConnection((FreeFormConnection) conn,
						deltaX, deltaY);
			}

			// Move implicitlyMovedDelays
			for (Delay del : implicitlyMovedDelay) {
				moveDelay(context, del);
			}

			// Kdesnos addition: Store the connectionList to avoid moving them a
			// second time in postMoveShape.
			this.outDoubleConnections.addAll(connectionList);
		}
	}

	/**
	 * Move a {@link Delay} and all the bendpoints of the associated
	 * {@link Fifo FIFOs} accorting to the given {@link IMoveShapeContext}.
	 * 
	 * @param context
	 *            the {@link IMoveShapeContext} containing the deltaX and deltaY
	 *            of the displacement
	 * @param del
	 *            the {@link Delay} whose associated graphical elements are to
	 *            be moved.
	 */
	@SuppressWarnings("deprecation")
	protected void moveDelay(IMoveShapeContext context, Delay del) {
		// Get all delays with identical attributes (may not be the
		// right delay is several delays have the same properties.)
		List<PictogramElement> pes = Graphiti.getLinkService()
				.getPictogramElements(getDiagram(), del);

		// Check if the delay is selected in the graphical interface
		PictogramElement[] selectedPictogramElements = getDiagramEditor()
				.getSelectedPictogramElements();
		List<PictogramElement> selectedPEs = new ArrayList<>(
				Arrays.asList(selectedPictogramElements));

		for (PictogramElement pe : pes) {
			// Check that the pe is associated to the right delay (in
			// the java Object sense, not equals() one)
			Object delay = getBusinessObjectForPictogramElement(pe);
			if (delay == del) {

				if (pe.getGraphicsAlgorithm() != null) {
					int oldX = pe.getGraphicsAlgorithm().getX();
					int oldY = pe.getGraphicsAlgorithm().getY();
					// Unless the delay is selected, move it
					if (!selectedPEs.contains(pe)) {
						pe.getGraphicsAlgorithm().setX(
								oldX + context.getDeltaX());
						pe.getGraphicsAlgorithm().setY(
								oldY + context.getDeltaY());
					}

					// Move all bendpoints of the FIFO as well
					List<PictogramElement> fifoPEs = new ArrayList<PictogramElement>();
					List<Anchor> anchors = getAnchors((Shape) pe);
					for (Anchor anchor : anchors) {
						// Always move the incoming connection of the delay
						// (even if the delay is selected.
						fifoPEs.addAll(anchor.getIncomingConnections());
						// Move the outgoing connection of the delay only if the
						// delay itself is not selected
						if (!selectedPEs.contains(pe))
							fifoPEs.addAll(anchor.getOutgoingConnections());
					}

					for (PictogramElement fifoPE : fifoPEs) {
						if (fifoPE instanceof FreeFormConnection) {
							moveAllBendpointsOnFFConnection(
									(FreeFormConnection) fifoPE,
									context.getDeltaX(), context.getDeltaY());
						}
					}
				}
			}
		}
	}

	/**
	 * Find FIFOs with a delay and both sides connected to selected actors. Put
	 * the {@link Delay} in {@link #implicitlyMovedDelay}.
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private FreeFormConnection[] calculateConnectedDelayedFifos(
			IMoveShapeContext context) {
		List<FreeFormConnection> retList = new ArrayList<FreeFormConnection>();
		Shape shapeToMove = context.getShape();

		int x = context.getX();
		int y = context.getY();

		int deltaX = x - shapeToMove.getGraphicsAlgorithm().getX();
		int deltaY = y - shapeToMove.getGraphicsAlgorithm().getY();

		if (deltaX != 0 || deltaY != 0) {
			List<Anchor> anchors = getAnchors(shapeToMove);

			// Create a list of the corresponding Ports
			List<Port> ports = new ArrayList<Port>();
			for (Anchor anchor : anchors) {
				Object bo = getBusinessObjectForPictogramElement(anchor);
				if (bo instanceof Port) {
					ports.add((Port) bo);
				}
			}

			PictogramElement[] selectedPictogramElements = getDiagramEditor()
					.getSelectedPictogramElements();
			if (selectedPictogramElements != null) {
				for (int i = 0; i < selectedPictogramElements.length; i++) {
					PictogramElement selPe = selectedPictogramElements[i];
					if (selPe instanceof Shape) {
						Shape selShape = (Shape) selPe;
						for (Anchor toAnchor : getAnchors(selShape)) {
							EList<Connection> incomingConnections = toAnchor
									.getIncomingConnections();
							for (Connection inConn : incomingConnections) {
								if (inConn instanceof FreeFormConnection) {
									Object bo = getBusinessObjectForPictogramElement(inConn);
									if (bo instanceof Fifo
											&& ((Fifo) bo).getDelay() != null) {
										Port srcPort = ((Fifo) bo)
												.getSourcePort();
										if (ports.contains(srcPort)) {
											retList.add((FreeFormConnection) inConn);
											Delay del = ((Fifo) bo).getDelay();
											implicitlyMovedDelay.add(del);
											// Add all the connection to the
											// double connections lists
											Fifo fifo = (Fifo) del.eContainer();
											List<PictogramElement> fifoPEs = Graphiti
													.getLinkService()
													.getPictogramElements(
															getDiagram(), fifo);
											for (PictogramElement fifoPE : fifoPEs) {
												if (fifoPE instanceof FreeFormConnection) {
													outDoubleConnections
															.add((FreeFormConnection) fifoPE);
												}
											}
										}
									}
								}
							}
						}
						for (Anchor fromAnchor : getAnchors(selShape)) {
							EList<Connection> outgoingConnections = fromAnchor
									.getOutgoingConnections();
							for (Connection outConn : outgoingConnections) {
								if (outConn instanceof FreeFormConnection) {
									Object bo = getBusinessObjectForPictogramElement(outConn);
									if (bo instanceof Fifo
											&& ((Fifo) bo).getDelay() != null) {
										Port tgtPort = ((Fifo) bo)
												.getTargetPort();
										if (ports.contains(tgtPort)) {
											retList.add((FreeFormConnection) outConn);
											inDoubleConnections
													.add((FreeFormConnection) outConn);
											// Not adding delays to
											// ImplicitlyMovedDelay is voluntary
											// here. (Doing so would move them
											// twice)
											// Add all the connection to the
											// double connections lists
											Delay del = ((Fifo) bo).getDelay();
											Fifo fifo = (Fifo) del.eContainer();
											List<PictogramElement> fifoPEs = Graphiti
													.getLinkService()
													.getPictogramElements(
															getDiagram(), fifo);
											for (PictogramElement fifoPE : fifoPEs) {
												if (fifoPE instanceof FreeFormConnection) {
													outDoubleConnections
															.add((FreeFormConnection) fifoPE);
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
	 * This code is a copy from the private method in
	 * {@link DefaultMoveShapeFeature}.
	 * 
	 * @param connection
	 * @param deltaX
	 * @param deltaY
	 */
	private void moveAllBendpointsOnFFConnection(FreeFormConnection connection,
			int deltaX, int deltaY) {
		List<Point> points = connection.getBendpoints();
		for (int i = 0; i < points.size(); i++) {
			Point point = points.get(i);
			int oldX = point.getX();
			int oldY = point.getY();
			points.set(
					i,
					Graphiti.getGaCreateService().createPoint(oldX + deltaX,
							oldY + deltaY));
		}
	}

	/**
	 * This code is a copy from the private method in
	 * {@link DefaultMoveShapeFeature}.
	 * 
	 * @param context
	 * @return
	 */
	private FreeFormConnection[] calculateContainerConnections(
			IMoveShapeContext context) {
		FreeFormConnection[] ret = new FreeFormConnection[0];

		if (!(context.getShape() instanceof ContainerShape)) {
			return ret;
		}

		List<FreeFormConnection> retList = new ArrayList<FreeFormConnection>();

		Shape shapeToMove = context.getShape();

		int x = context.getX();
		int y = context.getY();

		int deltaX = x - shapeToMove.getGraphicsAlgorithm().getX();
		int deltaY = y - shapeToMove.getGraphicsAlgorithm().getY();

		if (deltaX != 0 || deltaY != 0) {

			List<Anchor> anchorsFrom = getAnchors(shapeToMove);
			List<Anchor> anchorsTo = new ArrayList<Anchor>(anchorsFrom);

			for (Anchor anchorFrom : anchorsFrom) {

				Collection<Connection> outgoingConnections = anchorFrom
						.getOutgoingConnections();

				for (Connection connection : outgoingConnections) {
					for (Anchor anchorTo : anchorsTo) {

						Collection<Connection> incomingConnections = anchorTo
								.getIncomingConnections();
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
	 * 
	 * This code is a copy from the private method in
	 * {@link DefaultMoveShapeFeature}.
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private FreeFormConnection[] calculateConnectedConnections(
			IMoveShapeContext context) {
		List<FreeFormConnection> retList = new ArrayList<FreeFormConnection>();
		Shape shapeToMove = context.getShape();

		int x = context.getX();
		int y = context.getY();

		int deltaX = x - shapeToMove.getGraphicsAlgorithm().getX();
		int deltaY = y - shapeToMove.getGraphicsAlgorithm().getY();

		if (deltaX != 0 || deltaY != 0) {
			List<Anchor> anchors = getAnchors(shapeToMove);

			PictogramElement[] selectedPictogramElements = getDiagramEditor()
					.getSelectedPictogramElements();
			if (selectedPictogramElements != null) {
				for (int i = 0; i < selectedPictogramElements.length; i++) {
					PictogramElement selPe = selectedPictogramElements[i];
					if (selPe instanceof Shape) {
						Shape selShape = (Shape) selPe;
						for (Anchor toAnchor : getAnchors(selShape)) {
							EList<Connection> incomingConnections = toAnchor
									.getIncomingConnections();
							for (Connection inConn : incomingConnections) {
								if (inConn instanceof FreeFormConnection) {
									Anchor startAnchor = inConn.getStart();
									if (anchors.contains(startAnchor)) {
										retList.add((FreeFormConnection) inConn);
									}
								}
							}
						}
						// Kdesnos addition : detect also connectedConnections
						// ending at the current shape to avoid moving them
						// twice in postMoveShape
						for (Anchor toAnchor : getAnchors(selShape)) {
							EList<Connection> outgoingConnections = toAnchor
									.getOutgoingConnections();
							for (Connection outConn : outgoingConnections) {
								if (outConn instanceof FreeFormConnection) {
									Anchor endAnchor = outConn.getEnd();
									if (anchors.contains(endAnchor)) {
										inDoubleConnections
												.add((FreeFormConnection) outConn);
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
	 * This code is a copy from the private method in
	 * {@link DefaultMoveShapeFeature}.
	 * 
	 * @param theShape
	 * @return
	 */
	private List<Anchor> getAnchors(Shape theShape) {
		List<Anchor> ret = new ArrayList<Anchor>();
		ret.addAll(theShape.getAnchors());

		if (theShape instanceof ContainerShape) {
			ContainerShape containerShape = (ContainerShape) theShape;
			List<Shape> children = containerShape.getChildren();
			for (Shape shape : children) {
				if (shape instanceof ContainerShape) {
					ret.addAll(getAnchors((ContainerShape) shape));
				} else {
					ret.addAll(shape.getAnchors());
				}
			}
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void postMoveShape(IMoveShapeContext context) {
		// Here, we layout bendpoint of connections incoming/outgoing to this
		// AbstractVertex Ports
		ContainerShape cs = (ContainerShape) context.getPictogramElement();
		EList<Anchor> anchors = cs.getAnchors();

		for (Anchor anchor : anchors) {
			// If the anchor does not correspond to a port, skip the loop
			// (e.g. ChopBoxAnchors are used as connection points for
			// dependencies)
			if (!(getBusinessObjectForPictogramElement(anchor) instanceof Port)) {
				continue;
			}

			// Retrieve the connections of the anchor.
			List<FreeFormConnection> connections = new ArrayList<>();

			EList<Connection> iConnections = anchor.getIncomingConnections();

			boolean isSrcMove = false;
			if (!iConnections.isEmpty()) {

				connections
						.addAll((Collection<? extends FreeFormConnection>) iConnections);
				isSrcMove = false;
			}
			EList<Connection> oConnections = anchor.getOutgoingConnections();
			if (!oConnections.isEmpty()) {
				connections
						.addAll((Collection<? extends FreeFormConnection>) oConnections);
				isSrcMove = true;
			}

			// Remove connections whose bendpoints were already moved by
			// moveAllBendpoints call

			connections.removeAll(outDoubleConnections);
			connections.removeAll(inDoubleConnections);

			for (FreeFormConnection connection : connections) {
				// Check wether the FIFO corresponding to the connection has a
				// delay
				Object fifo = getBusinessObjectForPictogramElement(connection);

				// If the fifo has no delay, it remove a bendpoint if there are
				// at least two
				// if the fifo has a delay, remove a bendpoint (if any).
				int nbBendpoints = (fifo != null && ((Fifo) fifo).getDelay() != null) ? -1
						: 0;

				// Check if the last or first Bendpoint exists.
				// If so, move it with the same delta as the associated port.
				// (but it will still be removed and recreated.)
				boolean bendpointExists = false;
				int bendpointX = -1, bendpointY = -1;
				int index = connection.getBendpoints().size() - 1;
				if (index > nbBendpoints && !isSrcMove) {
					bendpointExists = true;
					bendpointX = connection.getBendpoints().get(index).getX();
					bendpointY = connection.getBendpoints().get(index).getY();
					connection.getBendpoints().remove(index);
				}
				if (index > nbBendpoints && isSrcMove) {
					bendpointExists = true;
					bendpointX = connection.getBendpoints().get(0).getX();
					bendpointY = connection.getBendpoints().get(0).getY();
					connection.getBendpoints().remove(0);
				}

				// Add one bendpoints it didn't exist, move it otherwise
				IPeLayoutService peLayoutService = Graphiti
						.getPeLayoutService();
				IGaCreateService createService = Graphiti.getGaCreateService();
				int midHeight = anchor.getGraphicsAlgorithm().getHeight() / 2 - 1;

				// Creation cases
				if (isSrcMove) {
					ILocation srcLoc = peLayoutService
							.getLocationRelativeToDiagram(connection.getStart());
					Point pSrc = null;
					if (!bendpointExists) {
						pSrc = createService.createPoint(srcLoc.getX() + 20,
								srcLoc.getY() + midHeight);
					} else {
						pSrc = createService.createPoint(
								bendpointX + context.getDeltaX(), bendpointY
										+ context.getDeltaY());
					}
					connection.getBendpoints().add(0, pSrc);
				}
				if (!isSrcMove) {
					ILocation trgtLoc = peLayoutService
							.getLocationRelativeToDiagram(connection.getEnd());
					Point pTrgt = null;
					if (!bendpointExists) {
						pTrgt = createService.createPoint(trgtLoc.getX() - 20,
								trgtLoc.getY() + midHeight);
					} else {
						pTrgt = createService.createPoint(
								bendpointX + context.getDeltaX(), bendpointY
										+ context.getDeltaY());
					}
					connection.getBendpoints().add(pTrgt);
				}
			}
		}

		return;
	}
}
