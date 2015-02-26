package org.ietr.preesm.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.ui.pimm.util.PiMMUtil;

public class SetFifoTypeFeature extends AbstractCustomFeature {

	public SetFifoTypeFeature(IFeatureProvider fp) {
		super(fp);
	}

	protected boolean hasDoneChanges = false;

	@Override
	public String getName() {
		return "Set the Data Type";
	}

	@Override
	public String getDescription() {
		return "Set/Change the Data Type of a FIFO";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		// Allow setting if exactly one pictogram element
		// representing an Actor is selected
		boolean ret = false;
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof Fifo) {
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
			if (bo instanceof Fifo) {
				Fifo fifo = (Fifo) bo;

				// Ask user for data type.
				String question = "Enter data type for the FIFO";
				String oldType = fifo.getType();
				String type = oldType;

				type = PiMMUtil.askString(getName(), question, type, null);
				if ((type == null && oldType == null)
						|| (type != null && type.equals(oldType))) {
					this.hasDoneChanges = false;
				} else {
					this.hasDoneChanges = true;
				}
				fifo.setType(type);

				// Call the layout feature
				layoutPictogramElement(pes[0]);
			}
		}
	}

	@Override
	public boolean hasDoneChanges() {
		return this.hasDoneChanges;
	}
}
