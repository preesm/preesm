package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.util.PortNameValidator;

/**
 * Feature to change the name of a port directly in the editor.
 * 
 * @author kdesnos
 * 
 */
public class DirectEditingPortNameFeature extends AbstractDirectEditingFeature {

	/**
	 * Default constructor of the {@link DirectEditingPortNameFeature}<br>
	 * <b> This class has not been tested yet </b>
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public DirectEditingPortNameFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public int getEditingType() {
		return TYPE_TEXT;
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();
		// support direct editing, if it is a Port, and the user clicked
		// directly on the text and not somewhere else in the rectangle
		if (bo instanceof Port && ga instanceof Text) {
			return true;
		}
		// direct editing not supported in all other cases
		return false;
	}

	@Override
	public String getInitialValue(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Port port = (Port) getBusinessObjectForPictogramElement(pe);
		return port.getName();
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Port port = (Port) getBusinessObjectForPictogramElement(pe);
		AbstractVertex vertex = (AbstractVertex) port.eContainer();
		PortNameValidator validator = new PortNameValidator(vertex, port,
				port.getKind());
		return validator.isValid(value);
	}

	@Override
	public void setValue(String value, IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Port port = (Port) getBusinessObjectForPictogramElement(pe);

		// Set the new name
		port.setName(value);

		// Update and layout the vertex
		GraphicsAlgorithm actorGA = ((BoxRelativeAnchor) context
				.getPictogramElement()).getReferencedGraphicsAlgorithm();
		layoutPictogramElement(actorGA.getPictogramElement());
	}

}
