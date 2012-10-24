package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.ietr.preesm.experiment.model.pimemoc.Graph;
import org.ietr.preesm.experiment.model.pimemoc.InterfaceVertex;
import org.ietr.preesm.experiment.ui.pimemoc.diagram.PimemocFeatureProviderWithRemove;

/**
 * Delete Feature for {@link InterfaceVertex}
 * 
 * @author kdesnos
 * 
 */
public class DeleteInterfaceVertexFeature extends DefaultDeleteFeature {

	/**
	 * default Constructor of {@link DeleteInterfaceVertexFeature}
	 * 
	 * @param fp
	 *            the feature procider
	 */
	public DeleteInterfaceVertexFeature(IFeatureProvider fp) {
		super(new PimemocFeatureProviderWithRemove(fp.getDiagramTypeProvider()));
	}

	@Override
	public void delete(IDeleteContext context) {
		Graph graph = (Graph) getBusinessObjectForPictogramElement(getDiagram());
		InterfaceVertex removedVertex = (InterfaceVertex) getBusinessObjectForPictogramElement(context
				.getPictogramElement());

		// Delete the InterfaceVertex
		super.delete(context);

		// If the delete was not canceled, remove the corresponding port from
		// the graph
		if (this.hasDoneChanges()) {
			graph.removeInterfaceVertex(removedVertex);
		}

	}
}
