/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.util.PortNameValidator;
import org.ietr.preesm.experiment.ui.pimm.util.PiMMUtil;

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
				if (((Port) bo).eContainer() instanceof ExecutableActor) {
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
				AbstractActor vertex = (AbstractActor) port.eContainer();
				String currentName = port.getName();

				// Ask user for Port name until a valid name is entered.
				String question = "Enter new port name";
				String newPortName = port.getName();

				newPortName = PiMMUtil.askString(this.getName(), question,
						newPortName, new PortNameValidator(vertex, port));

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
