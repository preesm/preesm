package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimemoc.AbstractVertex;
import org.ietr.preesm.experiment.model.pimemoc.Actor;
import org.ietr.preesm.experiment.model.pimemoc.Port;
import org.ietr.preesm.experiment.model.pimemoc.util.PortNameValidator;
import org.ietr.preesm.experiment.ui.pimemoc.util.PimemocUtil;

/**
 * Custom feature to rename a port.
 * 
 * @author kdesnos
 * 
 */
public class RenameActorPortFeature extends AbstractCustomFeature {

	protected boolean hasDoneChanges = false;

	/**
	 * Default Constructor
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public RenameActorPortFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Rename Port";
	}

	@Override
	public String getDescription() {
		return "Change the name of the Port";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		// allow rename if exactly one pictogram element
		// representing a Port is selected
		boolean ret = false;
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof Port) {
				if (((Port) bo).eContainer() instanceof Actor) {
					ret = true;
				}
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
			if (bo instanceof Port) {
				Port port = (Port) bo;
				AbstractVertex vertex = (AbstractVertex) port.eContainer();
				String currentName = port.getName();

				// Ask user for Port name until a valid name is entered.
				String question = "Enter new port name";
				String newPortName = port.getName();

				newPortName = PimemocUtil.askString(this.getName(), question,
						newPortName, new PortNameValidator(vertex, port,
								newPortName));

				if (newPortName != null && !newPortName.equals(currentName)) {
					this.hasDoneChanges = true;
					port.setName(newPortName);

					// Layout the Port
					layoutPictogramElement(pes[0]);
					updatePictogramElement(pes[0]);

					// Layout the actor
					GraphicsAlgorithm bra = ((BoxRelativeAnchor) pes[0])
							.getReferencedGraphicsAlgorithm();
					layoutPictogramElement(bra.getPictogramElement());
					updatePictogramElement(bra.getPictogramElement());
				}
			}
		}
	}

	@Override
	public boolean hasDoneChanges() {
		return this.hasDoneChanges;
	}

}
