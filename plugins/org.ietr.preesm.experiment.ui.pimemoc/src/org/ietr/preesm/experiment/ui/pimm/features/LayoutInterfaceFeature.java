package org.ietr.preesm.experiment.ui.pimm.features;

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
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.SinkInterface;
import org.ietr.preesm.experiment.model.pimm.SourceInterface;

/**
 * Layout Feature for {@link InterfaceActor} and Config Input Interface (i.e.
 * {@link Parameter}).
 * 
 * @author kdesnos
 * 
 */
public class LayoutInterfaceFeature extends AbstractLayoutFeature {

	/**
	 * Default constructor of the {@link LayoutInterfaceFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public LayoutInterfaceFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canLayout(ILayoutContext context) {
		// return true, if pictogram element is linked to an InterfaceVertex or
		// a Parameter used as a configuration input interface
		PictogramElement pe = context.getPictogramElement();
		if (!(pe instanceof ContainerShape)) {
			return false;
		}

		EList<EObject> businessObjects = pe.getLink().getBusinessObjects();
		return businessObjects.size() == 1
				&& (businessObjects.get(0) instanceof InterfaceActor || (businessObjects
						.get(0) instanceof Parameter && ((Parameter) businessObjects
						.get(0)).isConfigurationInterface()));
	}

	@Override
	public boolean layout(ILayoutContext context) {
		// Retrieve the shape and the graphic algorithm
		ContainerShape containerShape = (ContainerShape) context
				.getPictogramElement();
		GraphicsAlgorithm containerGa = containerShape.getGraphicsAlgorithm();
		AbstractVertex vertex = (AbstractVertex) getBusinessObjectForPictogramElement(containerShape);

		// Retrieve the size of the text
		IDimension size = null;
		for (Shape shape : containerShape.getChildren()) {
			GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
			if (ga instanceof Text) {
				size = GraphitiUi.getUiLayoutService().calculateTextSize(
						vertex.getName(), ((Text) ga).getFont());
			}
		}

		if (vertex instanceof InterfaceActor) {
			// Layout the invisible rectangle
			containerGa.setWidth(size.getWidth() + 16 + 3);
			// Layout the label
			for (Shape shape : containerShape.getChildren()) {
				GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
				if (ga instanceof Text) {
					switch (((InterfaceActor) vertex).getKind()) {
					case SourceInterface.KIND:
						ga.setWidth(size.getWidth());
						Graphiti.getGaService().setLocation(ga, 0, 0);
						break;
					case SinkInterface.KIND:
						ga.setWidth(size.getWidth());
						Graphiti.getGaService().setLocation(ga, 16 + 3, 0);
						break;
					case ConfigOutputInterface.KIND:
						ga.setWidth(size.getWidth());
						Graphiti.getGaService().setLocation(ga, 16 + 3, 0);
						break;
					}

				}
			}
		}

		if (vertex instanceof Parameter) {
			int width = (size.getWidth() < 18) ? 18 : size.getWidth();
			// Layout the invisible rectangle
			containerGa.setWidth(width);
			// Layout the label
			for (Shape shape : containerShape.getChildren()) {
				GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
				if (ga instanceof Text) {
					ga.setWidth(width);
				}
			}
		}
		return true;
	}

}
