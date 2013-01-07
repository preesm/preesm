package org.ietr.preesm.experiment.ui.pimm.features;

import java.util.List;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Fifo;

/**
 * Delete feature to remove a {@link Delay} from a {@link Fifo}
 * 
 * @author kdesnos
 * 
 */
public class DeleteDelayFeature extends DeleteParameterizableFeature {

	/**
	 * Default Constructor of the {@link DeleteDelayFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public DeleteDelayFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public void preDelete(IDeleteContext context) {
		// Transform the two connections linked to the delay back into a single
		// one. before deleting the delay.

		// Retrieve the two connections
		ContainerShape cs = (ContainerShape) context.getPictogramElement();
		ChopboxAnchor cba = (ChopboxAnchor) cs.getAnchors().get(0);
		List<Connection> incomingConnections = cba.getIncomingConnections();
		// There can be dependency incoming connection. Find the unique fifo
		// incoming connection
		Connection preConnection = null;
		for (Connection connection : incomingConnections) {
			Object obj = getBusinessObjectForPictogramElement(connection);
			if (obj instanceof Fifo) {
				preConnection = connection;
				break;
			}
		}
		// There is only one outgoing connection, the Fifo one.
		Connection postConnection = cba.getOutgoingConnections().get(0);

		// Copy the bendpoints to the unique remaining connection.
		// Reconnect it.
		((FreeFormConnection) postConnection).getBendpoints().addAll(0,
				((FreeFormConnection) preConnection).getBendpoints());
		postConnection.setStart(preConnection.getStart());

		// Remove the preConnection (but not the associated Fifo)
		IRemoveContext rmCtxt = new RemoveContext(preConnection);
		IRemoveFeature rmFeature = this.getFeatureProvider().getRemoveFeature(
				rmCtxt);
		if (rmFeature.canRemove(rmCtxt)) {
			rmFeature.remove(rmCtxt);
		} else {
			throw new RuntimeException(
					"Could not delete Delay because a Connection could not be removed.");
		}

		// Super call to delete the dependencies linked to the delay
		super.preDelete(context);
	}

}
