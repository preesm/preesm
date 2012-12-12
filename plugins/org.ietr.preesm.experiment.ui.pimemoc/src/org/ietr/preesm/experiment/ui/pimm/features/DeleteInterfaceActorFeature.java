package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.ui.pimm.diagram.PiMMFeatureProviderWithRemove;

/**
 * Delete Feature for {@link InterfaceActor}
 * 
 * @author kdesnos
 * 
 */
public class DeleteInterfaceActorFeature extends DeleteAbstractActorFeature {

	protected InterfaceActor removedVertex;

	/**
	 * default Constructor of {@link DeleteInterfaceActorFeature}
	 * 
	 * @param fp
	 *            the feature procider
	 */
	public DeleteInterfaceActorFeature(IFeatureProvider fp) {
		super(new PiMMFeatureProviderWithRemove(fp.getDiagramTypeProvider()));
	}

	@Override
	public void postDelete(IDeleteContext context) {
		super.postDelete(context);

		Graph graph = (Graph) getBusinessObjectForPictogramElement(getDiagram());

		// Remove the corresponding port from the graph
		if (this.hasDoneChanges()) {
			graph.removeInterfaceVertex(removedVertex);
		}
	}

	@Override
	public void preDelete(IDeleteContext context) {
		// Do not remove the super, it deals with Fifo connected to this
		// interface
		super.preDelete(context);

		// Backup the removed InterfaceVertex to remove its Port from the Graph
		// attributes
		removedVertex = (InterfaceActor) getBusinessObjectForPictogramElement(context
				.getPictogramElement());
	}
}
