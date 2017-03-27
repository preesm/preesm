/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
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
 *******************************************************************************/
package org.ietr.preesm.ui.pimm.features;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.util.PiIdentifiers;

/**
 * Custom feature to move down a port.
 * 
 * @author jheulot
 * @author kdesnos
 * 
 */
public class MoveDownActorPortFeature extends MoveUpActorPortFeature {

	protected boolean hasDoneChanges = false;
	
	public final static String HINT = "down";

	/**
	 * Default Constructor
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public MoveDownActorPortFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Move down Port\tCtrl+Down_Arrow";
	}

	@Override
	public String getDescription() {
		return "Move down the Port";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		// allow move up if exactly one pictogram element
		// representing a Port is selected
		// and it is not the first port
		boolean ret = false;
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof Port) {
				Port port = (Port) bo;
				if (port.eContainer() instanceof ExecutableActor) {
					ExecutableActor actor = (ExecutableActor) (port
							.eContainer());
					String kind = port.getKind();
					if (kind.compareTo("input") == 0) {
						ret = actor.getDataInputPorts().size() > 1;
						ret = ret
								&& actor.getDataInputPorts().indexOf(port) < actor
										.getDataInputPorts().size() - 1;
					} else if (kind.compareTo("output") == 0) {
						ret = actor.getDataOutputPorts().size() > 1;
						ret = ret
								&& actor.getDataOutputPorts().indexOf(port) < actor
										.getDataOutputPorts().size() - 1;
					} else if (kind.compareTo("cfg_input") == 0) {
						ret = actor.getConfigInputPorts().size() > 1;
						ret = ret
								&& actor.getConfigInputPorts().indexOf(port) < actor
										.getConfigInputPorts().size() - 1;
					} else if (kind.compareTo("cfg_output") == 0) {
						ret = actor.getConfigOutputPorts().size() > 1;
						ret = ret
								&& actor.getConfigOutputPorts().indexOf(port) < actor
										.getConfigOutputPorts().size() - 1;
					}
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
			PictogramElement anchorToMoveDown = pes[0];
			Object bo = getBusinessObjectForPictogramElement(anchorToMoveDown);
			if (bo instanceof Port) {
				Port portToMoveDown = null, portToMoveUp = null;
				int portToMoveDownIndex = -1;
				ExecutableActor actor;

				portToMoveDown = (Port) bo;
				actor = (ExecutableActor) (portToMoveDown.eContainer());
				String portKind = portToMoveDown.getKind();

				// Switch Port into Actor Object
				switch (portKind) {
				case PiIdentifiers.DATA_INPUT_PORT:
					portToMoveDownIndex = actor.getDataInputPorts().indexOf(
							portToMoveDown);
					portToMoveUp = actor.getDataInputPorts().get(
							portToMoveDownIndex + 1);
					break;
				case PiIdentifiers.DATA_OUTPUT_PORT:
					portToMoveDownIndex = actor.getDataOutputPorts().indexOf(
							portToMoveDown);
					portToMoveUp = actor.getDataOutputPorts().get(
							portToMoveDownIndex + 1);
					break;
				case PiIdentifiers.CONFIGURATION_INPUT_PORT:
					portToMoveDownIndex = actor.getConfigInputPorts().indexOf(
							portToMoveDown);
					portToMoveUp = actor.getConfigInputPorts().get(
							portToMoveDownIndex + 1);
					break;
				case PiIdentifiers.CONFIGURATION_OUPUT_PORT:
					portToMoveDownIndex = actor.getConfigOutputPorts().indexOf(
							portToMoveDown);
					portToMoveUp = actor.getConfigOutputPorts().get(
							portToMoveDownIndex + 1);
					break;
				}
				// Change context to use portToMoveUp feature
				// Get Graphical Elements
				ContainerShape csActor = (ContainerShape) ((BoxRelativeAnchor) anchorToMoveDown)
						.getReferencedGraphicsAlgorithm().getPictogramElement();
				EList<Anchor> anchors = csActor.getAnchors();

				Anchor anchorToMoveUp = null;
				for (Anchor a : anchors) {
					if (a.getLink().getBusinessObjects().get(0)
							.equals(portToMoveUp)) {
						anchorToMoveUp = a;
						break;
					}
				}

				context.getPictogramElements()[0] = anchorToMoveUp;
				super.execute(context);
			}
		}
	}
}
