package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
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
		GraphicsAlgorithm actorGA = ((BoxRelativeAnchor) context
				.getPictogramElement()).getReferencedGraphicsAlgorithm();
		super.delete(context);
		// Force the layout computation
		layoutPictogramElement(actorGA.getPictogramElement());
	}

}
