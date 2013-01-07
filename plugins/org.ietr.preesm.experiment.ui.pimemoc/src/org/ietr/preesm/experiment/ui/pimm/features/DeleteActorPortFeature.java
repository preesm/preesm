package org.ietr.preesm.experiment.ui.pimm.features;

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
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Port;

/**
 * Delete feature for the ports
 * 
 * @author kdesnos
 * 
 */
public class DeleteActorPortFeature extends DefaultDeleteFeature {

	/**
	 * Default constructor
	 * 
	 * @param fp
	 */
	public DeleteActorPortFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public void delete(IDeleteContext context) {
		// Retrieve the graphic algorithm of the enclosing actor
		BoxRelativeAnchor bra = (BoxRelativeAnchor) context
				.getPictogramElement();
		GraphicsAlgorithm actorGA = bra.getReferencedGraphicsAlgorithm();

		// Begin by deleting the Fifos or dependencies linked to this port
		deleteConnectedConnection(bra);

		// Delete the port
		super.delete(context);
		// Force the layout computation
		layoutPictogramElement(actorGA.getPictogramElement());
	}

	/**
	 * Method to delete the {@link Fifo} or {@link Dependency} connected to the
	 * deleted {@link Port}.
	 * 
	 * @param bra
	 *            the {@link BoxRelativeAnchor} of the deleted {@link Port}
	 */
	protected void deleteConnectedConnection(BoxRelativeAnchor bra) {
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
				// To deactivate dialog box
				delCtxt.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 0));
				
				// Cannot delete directly because this would mean concurrent
				// modifications of the connections Elist
				delFeatures.put(delFeature, delCtxt);
			}
		}

		// Actually delete
		for (IDeleteFeature delFeature : delFeatures.keySet()) {
			delFeature.delete(delFeatures.get(delFeature));
		}
	}

}
