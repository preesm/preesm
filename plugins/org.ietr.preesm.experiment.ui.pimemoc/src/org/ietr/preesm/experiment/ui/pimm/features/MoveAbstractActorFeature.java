package org.ietr.preesm.experiment.ui.pimm.features;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaCreateService;
import org.eclipse.graphiti.services.IPeLayoutService;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Port;

/**
 * The Move Feature for {@link AbstractActor}
 * 
 * @author kdesnos
 * 
 */
public class MoveAbstractActorFeature extends DefaultMoveShapeFeature {

	/**
	 * Default constructor for {@link MoveAbstractActorFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public MoveAbstractActorFeature(IFeatureProvider fp) {
		super(fp);
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

			for (FreeFormConnection connection : connections) {
				// Remove the last or first Bendpoint (if any)
				int index = connection.getBendpoints().size() - 1;
				if (index > -1 && !isSrcMove) {
					connection.getBendpoints().remove(index);
				}
				if (index > -1 && isSrcMove) {
					connection.getBendpoints().remove(0);
				}

				// Add one bendpoints or two bendpoints if the connection
				// originally had less than two bendpoints
				IPeLayoutService peLayoutService = Graphiti
						.getPeLayoutService();
				IGaCreateService createService = Graphiti.getGaCreateService();
				int midHeight = anchor.getGraphicsAlgorithm().getHeight() / 2 - 1;

				if (isSrcMove) {
					ILocation srcLoc = peLayoutService
							.getLocationRelativeToDiagram(connection.getStart());
					Point pSrc = createService.createPoint(srcLoc.getX() + 20,
							srcLoc.getY() + midHeight);
					connection.getBendpoints().add(0, pSrc);
				}
				if (!isSrcMove) {
					ILocation trgtLoc = peLayoutService
							.getLocationRelativeToDiagram(connection.getEnd());
					Point pTrgt = createService.createPoint(
							trgtLoc.getX() - 20, trgtLoc.getY() + midHeight);
					connection.getBendpoints().add(pTrgt);
				}
			}
		}

		return;
	}
}
