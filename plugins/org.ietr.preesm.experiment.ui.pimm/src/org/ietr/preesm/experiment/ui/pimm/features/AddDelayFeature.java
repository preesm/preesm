package org.ietr.preesm.experiment.ui.pimm.features;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.services.IPeLayoutService;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;

/**
 * Add feature responsible for creating and adding a delay to a {@link Fifo}.
 * 
 * @author kdesnos
 * @author jheulot
 * 
 */
public class AddDelayFeature extends AbstractCustomFeature {

	/**
	 * The default constructor for {@link AddDelayFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public AddDelayFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Add Delay";
	}

	@Override
	public String getDescription() {
		return "Add a Delay to the Fifo";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		// allow if exactly one pictogram element
		// representing an Fifo is selected
		boolean ret = false;
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof Fifo) {
				// Check that the Fifo has no existing delay
				if (((Fifo) bo).getDelay() == null) {
					ret = true;
				}
			}
		}
		return ret;
	}

	@Override
	public void execute(ICustomContext context) {
		// Recheck if the execution is possible (probably useless)
		if (!canExecute(context)) {
			return;
		}

		// Get the Fifo
		PictogramElement[] pes = context.getPictogramElements();
		FreeFormConnection connection = (FreeFormConnection) pes[0];
		Fifo fifo = (Fifo) getBusinessObjectForPictogramElement(connection);

		// Create the Delay and add it to the Fifo
		Delay delay = PiMMFactory.eINSTANCE.createDelay();
		Expression expr = PiMMFactory.eINSTANCE.createExpression();
		delay.setExpression(expr);
		fifo.setDelay(delay);

		// Get the GaService
		IGaService gaService = Graphiti.getGaService();
		// Get the PeCreateService
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		// Get the PeLayoutService
		IPeLayoutService peLayoutService = Graphiti.getPeLayoutService();

		// Get the target Diagram
		Diagram targetDiagram = getDiagram();
		ContainerShape containerShape = peCreateService.createContainerShape(
				targetDiagram, true);

		// Create a graphical representation for the Delay
		Ellipse ellipse;
		{
			ellipse = gaService.createEllipse(containerShape);
			ellipse.setBackground(manageColor(AddActorFeature.ACTOR_FOREGROUND));
			ellipse.setForeground(manageColor(AddActorFeature.ACTOR_FOREGROUND));
			ellipse.setLineWidth(1);
			ellipse.setLineVisible(false);
			gaService.setLocationAndSize(ellipse, context.getX() - 8,
					context.getY() - 8, 16, 16);
		}
		link(containerShape, delay);

		// Add a ChopBoxAnchor for the Delay
		ChopboxAnchor cba = peCreateService.createChopboxAnchor(containerShape);
		link(cba, delay);

		// Connect the polyline to the delay appropriately
		{
			// Create a list of all points of the connection (including source
			// and target anchor)
			List<Point> points;
			{
				ILocation srcLoc = peLayoutService
						.getLocationRelativeToDiagram(connection.getStart());
				Point pSrc = gaService
						.createPoint(srcLoc.getX(), srcLoc.getY());
				ILocation tgtLoc = peLayoutService
						.getLocationRelativeToDiagram(connection.getEnd());
				Point pTgt = gaService
						.createPoint(tgtLoc.getX(), tgtLoc.getY());
				points = new ArrayList<>(connection.getBendpoints());
				points.add(0, pSrc);
				points.add(pTgt);
			}

			// Identify between which pair of points the delay was created
			double smallestDist = 1000.0;
			//Point pBefore = points.get(0);
			Point pAfter = points
					.get(points.size() - 1);
			for (int i = 0; i < points.size() - 1; i++) {
				Point p1 = points.get(i);
				Point p2 = points.get(i + 1);

				// line equation ax+by+c=0
				int a, b, c;
				a = p2.getY() - p1.getY();
				b = p1.getX() - p2.getX();
				c = -(b * p1.getY() + a * p1.getX());

				// Distance of the point to the line
				double dist = Math.abs(a * context.getX() + b * context.getY()
						+ c)
						/ Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
				if (dist < smallestDist) {
					smallestDist = dist;
					// pBefore = p1;
					pAfter = p2;
				}
			}

			// Create a list of preceding and succeeding points.
			List<Point> precedingPoints = new ArrayList<Point>(points.subList(
					0, points.indexOf(pAfter)));
			precedingPoints.remove(0); // remove the anchor point from the list
			connection.getBendpoints().removeAll(precedingPoints);

			// Create the new connection and its polyline
			FreeFormConnection preConnection = peCreateService
					.createFreeFormConnection(getDiagram());
			preConnection.setStart(connection.getStart());
			preConnection.setEnd(cba);
			preConnection.getBendpoints().addAll(precedingPoints);
			// Create the associated Polyline
			Polyline polyline = gaService.createPolyline(preConnection);
			polyline.setLineWidth(2);
			polyline.setForeground(manageColor(AddFifoFeature.FIFO_FOREGROUND));
			link(preConnection, fifo);

			// Reconnect the original connection
			connection.setStart(cba);

			// Select the whole fifo
			// PictogramElement[] pictograms = {preConnection, connection};
			getDiagramEditor().setPictogramElementForSelection(containerShape);
		}

	}
}
