package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Graph;

/**
 * Add Feature to add a {@link Dependency} to the {@link Diagram}
 * 
 * @author kdesnos
 * 
 */
public class AddDependencyFeature extends AbstractAddFeature {

	private static final IColorConstant DEPENDENCY_FOREGROUND = new ColorConstant(
			98, 131, 167);

	/**
	 * Default constructor of th {@link AddDependencyFeature}.
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public AddDependencyFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public PictogramElement add(IAddContext context) {
		IAddConnectionContext addContext = (IAddConnectionContext) context;
		Dependency addedDependency = (Dependency) context.getNewObject();
		IPeCreateService peCreateService = Graphiti.getPeCreateService();

		// if added Dependency has no resource we add it to the resource
		// of the Graph
		if (addedDependency.eResource() == null) {
			Graph graph = (Graph) getBusinessObjectForPictogramElement(getDiagram());
			graph.getDependencies().add(addedDependency);
		}

		// CONNECTION WITH POLYLINE
		FreeFormConnection connection = peCreateService
				.createFreeFormConnection(getDiagram());
		createEdge(addContext, connection);

		// Add the arrow
		ConnectionDecorator cd;
		cd = peCreateService.createConnectionDecorator(connection, false, 1.0,
				true);
		createArrow(cd);

		// create link and wire it
		link(connection, addedDependency);

		return connection;
	}

	@Override
	public boolean canAdd(IAddContext context) {
		// Return true if the given Business object is a Dependency and the
		// context is an instance of IAddConnectionContext
		if (context instanceof IAddConnectionContext
				&& context.getNewObject() instanceof Dependency) {
			return true;
		}
		return false;
	}

	/**
	 * Create the arrow {@link Polyline} that will decorate the
	 * {@link Dependency}
	 * 
	 * @param gaContainer
	 *            the {@link GraphicsAlgorithmContainer}
	 * @return
	 */
	protected Polyline createArrow(GraphicsAlgorithmContainer gaContainer) {
		IGaService gaService = Graphiti.getGaService();
		Polyline polyline = gaService.createPlainPolyline(gaContainer, new int[] {
				-10, 5, 0, 0, -10, -5 });
		polyline.setForeground(manageColor(DEPENDENCY_FOREGROUND));
		polyline.setLineWidth(2);
		return polyline;
	}

	/**
	 * @param addContext
	 * @param connection
	 */
	protected void createEdge(IAddConnectionContext addContext,
			FreeFormConnection connection) {
		// Set the connection src and snk
		connection.setStart(addContext.getSourceAnchor());
		connection.setEnd(addContext.getTargetAnchor());

		// Layout the edge
		// Call the move feature of the anchor owner to layout the connection
		/*
		 * MoveAbstractActorFeature moveFeature = new MoveAbstractActorFeature(
		 * getFeatureProvider()); ContainerShape cs = (ContainerShape)
		 * connection.getStart()
		 * .getReferencedGraphicsAlgorithm().getPictogramElement();
		 * MoveShapeContext moveCtxt = new MoveShapeContext(cs);
		 * moveCtxt.setDeltaX(0); moveCtxt.setDeltaY(0); ILocation csLoc =
		 * Graphiti.getPeLayoutService() .getLocationRelativeToDiagram(cs);
		 * moveCtxt.setLocation(csLoc.getX(), csLoc.getY());
		 * moveFeature.moveShape(moveCtxt);
		 */

		// Create the associated Polyline
		IGaService gaService = Graphiti.getGaService();
		Polyline polyline = gaService.createPolyline(connection);
		polyline.setLineWidth(2);
		polyline.setForeground(manageColor(DEPENDENCY_FOREGROUND));
		polyline.setLineStyle(LineStyle.DASH);
	}

}
