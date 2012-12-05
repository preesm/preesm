package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.ietr.preesm.experiment.model.pimm.Port;

/**
 * Layout Feature for Ports
 * 
 * @author kdesnos
 * 
 */
public class LayoutPortFeature extends AbstractLayoutFeature {

	/**
	 * Default constructor of the {@link LayoutPortFeature}
	 * 
	 * @param fp
	 */
	public LayoutPortFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canLayout(ILayoutContext context) {
		// return true, if pictogram element is linked to an Port
		PictogramElement pe = context.getPictogramElement();
		if (!(pe instanceof BoxRelativeAnchor)) {
			return false;
		}

		EList<EObject> businessObjects = pe.getLink().getBusinessObjects();
		return businessObjects.size() == 1
				&& businessObjects.get(0) instanceof Port;
	}

	@Override
	public boolean layout(ILayoutContext context) {
		// Get the GaService
		IGaService gaService = Graphiti.getGaService();
		// retrieve the boxRelativeAnchor and port
		BoxRelativeAnchor bra = (BoxRelativeAnchor) context
				.getPictogramElement();
		EList<EObject> businessObjects = bra.getLink().getBusinessObjects();
		Port port = (Port) businessObjects.get(0);

		// Retrieve the size of the text
		IDimension size = null;
		for (GraphicsAlgorithm ga : bra.getGraphicsAlgorithm()
				.getGraphicsAlgorithmChildren()) {
			if (ga instanceof Text) {
				size = GraphitiUi.getUiLayoutService().calculateTextSize(
						port.getName(), ((Text) ga).getFont());
			}
		}

		// define a few constant
		int anchorGaSize = AbstractAddActorPortFeature.PORT_ANCHOR_GA_SIZE;
		int labelGaSpace = AbstractAddActorPortFeature.PORT_LABEL_GA_SPACE;
		int portFontHeight = size.getHeight();

		// Layout the invisible rectangle
		if (bra.getRelativeWidth() == 0.0) {
			gaService.setLocationAndSize(bra.getGraphicsAlgorithm(), 0, 0,
					size.getWidth() + anchorGaSize + labelGaSpace,
					size.getHeight());
		} else {
			gaService.setLocationAndSize(bra.getGraphicsAlgorithm(),
					-size.getWidth() - anchorGaSize - labelGaSpace, 0,
					size.getWidth() + anchorGaSize + labelGaSpace,
					size.getHeight());
		}

		// Layout the children of the bra
		for (GraphicsAlgorithm ga : bra.getGraphicsAlgorithm()
				.getGraphicsAlgorithmChildren()) {
			if (ga instanceof Text) {
				gaService.setWidth(ga, bra.getGraphicsAlgorithm().getWidth()
						- anchorGaSize);
				if (bra.getRelativeWidth() == 0.0) {
					// input port
					gaService.setLocation(ga, anchorGaSize, 0);
				} else {
					// output port
					gaService.setLocation(ga, 0, 0);
				}
			}			

			if (ga instanceof Rectangle) {
				if (bra.getRelativeWidth() == 0.0) {
					// input port
					gaService.setLocation(ga, 0, 1 + (portFontHeight - anchorGaSize) / 2);
				} else {
					// output port
					gaService.setLocation(ga, bra.getGraphicsAlgorithm()
							.getWidth() - anchorGaSize,
							1 + (portFontHeight - anchorGaSize) / 2);
				}
			}
		}
		return true;
	}

}
