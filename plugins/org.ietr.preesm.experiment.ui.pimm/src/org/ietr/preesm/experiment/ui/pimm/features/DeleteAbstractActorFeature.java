package org.ietr.preesm.experiment.ui.pimm.features;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MultiDeleteInfo;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;

/**
 * Delete Feature for {@link AbstractActor}
 * 
 * @author kdesnos
 * 
 */
public class DeleteAbstractActorFeature extends DeleteParameterizableFeature {

	/**
	 * Default constructor of {@link DeleteAbstractActorFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public DeleteAbstractActorFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public void preDelete(IDeleteContext context) {
		super.preDelete(context);

		// Delete all the Fifo and dependencies linked to this actor
		ContainerShape cs = (ContainerShape) context.getPictogramElement();

		// First create all the deleteFeatures and their context and store them
		// in a Map. (this is because cs.getAnchor cannot be modified while
		// iterated on)
		Map<IDeleteFeature, IDeleteContext> delFeatures;
		delFeatures = new HashMap<IDeleteFeature, IDeleteContext>();
		for (Anchor anchor : cs.getAnchors()) {
			// Skip the current iteration if the anchor is not a
			// BoxRelativeAnchor
			// The anchor can be a ChopBox anchor (for dependencies)
			if (!(anchor instanceof BoxRelativeAnchor)) {
				continue;
			}

			DeleteActorPortFeature delPortFeature = new DeleteActorPortFeature(
					getFeatureProvider());
			DeleteContext delCtxt = new DeleteContext(anchor);
			MultiDeleteInfo multi = new MultiDeleteInfo(false, false, 0);
			delCtxt.setMultiDeleteInfo(multi);
			delFeatures.put(delPortFeature, delCtxt);
		}

		// Actually delete
		for (IDeleteFeature delFeature : delFeatures.keySet()) {
			delFeature.delete(delFeatures.get(delFeature));
		}

	}

}
