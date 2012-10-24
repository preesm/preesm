package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.ietr.preesm.experiment.model.pimemoc.AbstractVertex;

public class UpdateAbstractVertexFeature extends AbstractUpdateFeature {

	/**
	 * Default constructor of the {@link UpdateAbstractVertexFeature}
	 * 
	 * @param fp
	 */
	public UpdateAbstractVertexFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {
		Object bo = getBusinessObjectForPictogramElement(context
				.getPictogramElement());
		return (bo instanceof AbstractVertex);
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		// retrieve name from pictogram model
		String pictogramName = null;
		PictogramElement pictogramElement = context.getPictogramElement();
		if (pictogramElement instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) pictogramElement;
			for (Shape shape : cs.getChildren()) {
				if (shape.getGraphicsAlgorithm() instanceof Text) {
					Text text = (Text) shape.getGraphicsAlgorithm();
					pictogramName = text.getValue();
				}
			}
		}

		// retrieve AbstractVertex name from business model (from the graph)
		String businessName = null;
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof AbstractVertex) {
			AbstractVertex vertex = (AbstractVertex) bo;
			businessName = vertex.getName();
		}

		// update needed, if names are different
		boolean updateNameNeeded = ((pictogramName == null && businessName != null) || (pictogramName != null && !pictogramName
				.equals(businessName)));
		if (updateNameNeeded) {
			return Reason.createTrueReason("Name is out of date\nNew name: "
					+ businessName);
		}

		return Reason.createFalseReason();
	}

	@Override
	public boolean update(IUpdateContext context) {
		// retrieve name from business model
		String businessName = null;
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof AbstractVertex) {
			AbstractVertex vertex = (AbstractVertex) bo;
			businessName = vertex.getName();
		}

		// Set name in pictogram model
		if (pictogramElement instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) pictogramElement;
			for (Shape shape : cs.getChildren()) {
				if (shape.getGraphicsAlgorithm() instanceof Text) {
					Text text = (Text) shape.getGraphicsAlgorithm();
					text.setValue(businessName);
					return true;
				}
			}
		}
		// Update not completed
		return false;
	}
}
