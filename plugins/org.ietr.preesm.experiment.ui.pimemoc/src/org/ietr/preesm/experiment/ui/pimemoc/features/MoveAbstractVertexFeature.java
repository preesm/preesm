package org.ietr.preesm.experiment.ui.pimemoc.features;

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
import org.ietr.preesm.experiment.model.pimemoc.AbstractVertex;

/**
 * The Move Feature for {@link AbstractVertex}
 * 
 * @author kdesnos
 * 
 */
public class MoveAbstractVertexFeature extends DefaultMoveShapeFeature {

	/**
	 * Default constructor for {@link MoveAbstractVertexFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public MoveAbstractVertexFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	protected void postMoveShape(IMoveShapeContext context) {
		// Here, we layout bendpoint of connections incoming/outgoing to this
		// AbstractVertex Ports
		ContainerShape cs = (ContainerShape) context.getPictogramElement();
		EList<Anchor> anchors = cs.getAnchors();

		for (Anchor anchor : anchors) {
			// Retrieve the connection of the anchor. Note that there should
			// never be more than one connection per anchor
			EList<Connection> iConnections = anchor.getIncomingConnections();
			FreeFormConnection connection = null;
			boolean isSrcMove = false;
			if (iConnections.size() > 0) {
				connection = (FreeFormConnection) iConnections.get(0);
				isSrcMove = false;
			}
			EList<Connection> oConnections = anchor.getOutgoingConnections();
			if (oConnections.size() > 0) {
				connection = (FreeFormConnection) oConnections.get(0);
				isSrcMove = true;
			}

			if (connection != null) {
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
				int midHeight = anchor.getGraphicsAlgorithm().getHeight()/2 -1;

				if (isSrcMove || index < 1) {
					ILocation srcLoc = peLayoutService
							.getLocationRelativeToDiagram(connection.getStart());
					Point pSrc = createService.createPoint(srcLoc.getX() + 20,
							srcLoc.getY() + midHeight);
					connection.getBendpoints().add(0, pSrc);
				}
				if (!isSrcMove || index < 1) {

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
