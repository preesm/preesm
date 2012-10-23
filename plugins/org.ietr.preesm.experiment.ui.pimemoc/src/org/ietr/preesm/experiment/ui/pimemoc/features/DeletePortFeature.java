package org.ietr.preesm.experiment.ui.pimemoc.features;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MultiDeleteInfo;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.ietr.preesm.experiment.model.pimemoc.Fifo;
import org.ietr.preesm.experiment.model.pimemoc.Port;
import org.ietr.preesm.experiment.ui.pimemoc.diagram.PimemocFeatureProviderWithRemove;

/**
 * Delete feature for the ports
 * 
 * @author kdesnos
 * 
 */
public class DeletePortFeature extends DefaultDeleteFeature {

	/**
	 * Default constructor
	 * 
	 * @param fp
	 */
	public DeletePortFeature(IFeatureProvider fp) {
		super(new PimemocFeatureProviderWithRemove(fp.getDiagramTypeProvider()));
	}

	@Override
	public void delete(IDeleteContext context) {
		// Retrieve the graphic algorithm of the enclosing actor
		BoxRelativeAnchor bra = (BoxRelativeAnchor) context
				.getPictogramElement();
		GraphicsAlgorithm actorGA = bra.getReferencedGraphicsAlgorithm();

		// Begin by deleting the Fifos linked to this port
		deleteConnectedFifo(bra);

		// Delete the port
		super.delete(context);
		// Force the layout computation
		layoutPictogramElement(actorGA.getPictogramElement());
	}

	/**
	 * Method to delete the {@link Fifo} connected to the deleted {@link Port}.
	 * 
	 * @param bra
	 *            the {@link BoxRelativeAnchor} of the deleted {@link Port}
	 */
	protected void deleteConnectedFifo(BoxRelativeAnchor bra) {
		// First, the list of connections is scanned in order to fill a map with
		// the deleteFeatures and their context.
		Map<IDeleteFeature, IDeleteContext> delFeatures;
		delFeatures = new HashMap<IDeleteFeature, IDeleteContext>();
		EList<Connection> connections = bra.getIncomingConnections();
		connections.addAll(bra.getOutgoingConnections());
		for (Connection connect : connections) {
			DeleteContext delCtxt = new DeleteContext(connect);
			delCtxt.setMultiDeleteInfo(null);
			IDeleteFeature delFeature = getFeatureProvider().getDeleteFeature(
					delCtxt);
			if (delFeature.canDelete(delCtxt)) {
				// Cannot delete directly because this would mean concurrent
				// modifications of the connections Elist
				delCtxt.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 0));
				delFeatures.put(delFeature, delCtxt);
			}
		}

		// Actually delete
		for (IDeleteFeature delFeature : delFeatures.keySet()) {
			delFeature.delete(delFeatures.get(delFeature));
		}
	}

}
