package org.ietr.preesm.experiment.ui.pimm.features;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MultiDeleteInfo;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.ui.pimm.diagram.PiMMFeatureProviderWithRemove;

/**
 * Delete feature for {@link Parameter}s.
 * 
 * @author kdesnos
 * 
 */
public class DeleteAbstractVertexFeature extends DefaultDeleteFeature {

	/**
	 * Default constructor for the {@link DeleteAbstractVertexFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public DeleteAbstractVertexFeature(IFeatureProvider fp) {
		super(new PiMMFeatureProviderWithRemove(fp.getDiagramTypeProvider()));
	}

	/**
	 * Method to delete the {@link Dependency} connected to the deleted
	 * {@link AbstractVertex}.
	 * 
	 * @param cba
	 *            the {@link ChopboxAnchor} of the deleted {@link Port}
	 */
	protected void deleteConnectedConnection(ChopboxAnchor cba) {
		// First, the list of connections is scanned in order to fill a map with
		// the deleteFeatures and their context.
		Map<IDeleteFeature, IDeleteContext> delFeatures;
		delFeatures = new HashMap<IDeleteFeature, IDeleteContext>();
		EList<Connection> connections = cba.getIncomingConnections();
		connections.addAll(cba.getOutgoingConnections());
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

	@Override
	public void preDelete(IDeleteContext context) {
		super.preDelete(context);

		// Delete all the dependencies linked to this parameter
		ContainerShape cs = (ContainerShape) context.getPictogramElement();

		// Scan the anchors (There should be only one ChopBoxAnchor)
		for (Anchor anchor : cs.getAnchors()) {
			if (anchor instanceof ChopboxAnchor) {
				deleteConnectedConnection((ChopboxAnchor) anchor);
			}
		}
	}
}
