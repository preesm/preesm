package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.ietr.preesm.experiment.model.pimemoc.Fifo;
import org.ietr.preesm.experiment.model.pimemoc.Graph;

/**
 * Add feature to add a Fifo to the Diagram
 * 
 * @author kdesnos
 * 
 */
public class AddFifoFeature extends AbstractAddFeature {

	private static final IColorConstant FIFO_FOREGROUND = new ColorConstant(98,
			131, 167);

	/**
	 * The default constructor of {@link AddFifoFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public AddFifoFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		// Return true if the given Business object is a Fifo and the context is
		// an instance of IAddConnectionContext
		if (context instanceof IAddConnectionContext
				&& context.getNewObject() instanceof Fifo) {
			return true;
		}
		return false;
	}

	@Override
	public PictogramElement add(IAddContext context) {
		IAddConnectionContext addConContext = (IAddConnectionContext) context;
		Fifo addedFifo = (Fifo) context.getNewObject();
		IPeCreateService peCreateService = Graphiti.getPeCreateService();

		// if added Fifo has no resource we add it to the resource
		// of the diagram
		// in a real scenario the business model would have its own resource
		if (addedFifo.eResource() == null) {
			Graph graph = (Graph) getBusinessObjectForPictogramElement(getDiagram());
			graph.getFifos().add(addedFifo);
		}

		// CONNECTION WITH POLYLINE
		Connection connection = peCreateService
				.createFreeFormConnection(getDiagram());
		connection.setStart(addConContext.getSourceAnchor());
		connection.setEnd(addConContext.getTargetAnchor());

		IGaService gaService = Graphiti.getGaService();
		Polyline polyline = gaService.createPolyline(connection);
		polyline.setLineWidth(2);
		polyline.setForeground(manageColor(FIFO_FOREGROUND));

		// create link and wire it
		link(connection, addedFifo);

		return connection;
	}

}
