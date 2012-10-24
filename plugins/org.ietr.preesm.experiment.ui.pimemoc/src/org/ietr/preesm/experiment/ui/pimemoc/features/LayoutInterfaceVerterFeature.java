package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.ietr.preesm.experiment.model.pimemoc.InterfaceVertex;

/**
 * Layout Feature for {@link InterfaceVertex}
 * 
 * @author kdesnos
 * 
 */
public class LayoutInterfaceVerterFeature extends AbstractLayoutFeature {

	/**
	 * Default constructor of the {@link LayoutInterfaceVerterFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public LayoutInterfaceVerterFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canLayout(ILayoutContext context) {
		// return true, if pictogram element is linked to an InterfaceVertex
		PictogramElement pe = context.getPictogramElement();
		if (!(pe instanceof ContainerShape)) {
			return false;
		}

		EList<EObject> businessObjects = pe.getLink().getBusinessObjects();
		return businessObjects.size() == 1
				&& businessObjects.get(0) instanceof InterfaceVertex;
	}

	@Override
	public boolean layout(ILayoutContext context) {
		// Retrieve the shape and the graphic algorithm
		ContainerShape containerShape = (ContainerShape) context
				.getPictogramElement();
		GraphicsAlgorithm containerGa = containerShape.getGraphicsAlgorithm();
		InterfaceVertex iVertex = (InterfaceVertex) getBusinessObjectForPictogramElement(containerShape);

		// Retrieve the size of the text
		IDimension size = null;
		for (GraphicsAlgorithm ga : containerGa.getGraphicsAlgorithmChildren()) {
			if (ga instanceof Text) {
				size = GraphitiUi.getUiLayoutService().calculateTextSize(
						iVertex.getName(), ((Text) ga).getFont());
			}
		}

		// Layout the invisible rectangle
		containerGa.setWidth(size.getWidth() + 16 + 3);
		// Layout the label
		for (GraphicsAlgorithm ga : containerGa.getGraphicsAlgorithmChildren()) {
			if (ga instanceof Text) {
				if (iVertex.getKind().equals("src")) {
					ga.setWidth(size.getWidth());
					Graphiti.getGaService().setLocation(ga, 0, 0);
				}
			}
		}

		return false;
	}

}
