package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimemoc.Port;

/**
 * Feature to update a port
 * 
 * @author kdesnos
 * 
 */
public class UpdatePortFeature extends AbstractUpdateFeature {

	/**
	 * Default constructor of the {@link UpdatePortFeature}
	 * 
	 * @param fp
	 */
	public UpdatePortFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {
		Object bo = getBusinessObjectForPictogramElement(context
				.getPictogramElement());
		return (bo instanceof Port);
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		// retrieve name from pictogram model
		String pictogramName = null;
		PictogramElement pictogramElement = context.getPictogramElement();
		if (pictogramElement instanceof BoxRelativeAnchor) {
			BoxRelativeAnchor bra = (BoxRelativeAnchor) pictogramElement;
			// The label of the port is the only child with type Text
			for (GraphicsAlgorithm ga : bra.getGraphicsAlgorithm()
					.getGraphicsAlgorithmChildren()) {
				if (ga instanceof Text) {
					pictogramName = ((Text) ga).getValue();
				}
			}
		}

		// retrieve Actor name from business model (from the graph)
		String businessName = null;
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof Port) {
			Port port = (Port) bo;
			businessName = port.getName();
		}

		// update needed, if names are different
		boolean updateNameNeeded = ((pictogramName == null && businessName != null) || (pictogramName != null && !pictogramName
				.equals(businessName)));
		if (updateNameNeeded) {
			return Reason.createTrueReason("Name is out of date\nNew name: "
					+ businessName);
		} else {
			return Reason.createFalseReason();
		}
	}

	@Override
	public boolean update(IUpdateContext context) {
		// retrieve name from business model
		String businessName = null;
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof Port) {
			Port port = (Port) bo;
			businessName = port.getName();
		}

		// Set name in pictogram model
		if (pictogramElement instanceof BoxRelativeAnchor) {
			BoxRelativeAnchor bra = (BoxRelativeAnchor) pictogramElement;
			// The label of the port is the only child with type Text
			for (GraphicsAlgorithm ga : bra.getGraphicsAlgorithm()
					.getGraphicsAlgorithmChildren()) {
				if (ga instanceof Text) {
					((Text) ga).setValue(businessName);
				}
			}
		}
		
		layoutPictogramElement(pictogramElement);		
		
		// Call the layout feature
		GraphicsAlgorithm bra = ((BoxRelativeAnchor) pictogramElement)
				.getReferencedGraphicsAlgorithm();
		layoutPictogramElement(bra.getPictogramElement());
		
		
		// Update not completed
		return true;
	}

}
