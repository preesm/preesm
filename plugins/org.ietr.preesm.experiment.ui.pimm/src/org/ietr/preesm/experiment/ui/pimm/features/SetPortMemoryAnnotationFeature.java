package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation;

public class SetPortMemoryAnnotationFeature extends AbstractCustomFeature {

	private PortMemoryAnnotation currentPMA;
	
	public SetPortMemoryAnnotationFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Set memory annotation";
	}

	@Override
	public String getDescription() {
		return "Set the memoy annotation a Port";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		// Allow setting if exactly one pictogram element
		// representing an Actor is selected
		boolean ret = false;
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof DataPort) {
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public void execute(ICustomContext context) {
		// Re-check if only one element is selected
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof DataPort) {
				((DataPort) bo).setAnnotation(currentPMA);
				currentPMA = null;
				// Call the layout feature
				layoutPictogramElement(pes[0]);
			}
		}
	}

	public PortMemoryAnnotation getCurrentPMA() {
		return currentPMA;
	}

	public void setCurrentPMA(PortMemoryAnnotation currentPMA) {
		this.currentPMA = currentPMA;
	}

}
