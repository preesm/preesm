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
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
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
		AbstractActor vertex = (AbstractActor) port.eContainer();
		PortNameValidator validator = new PortNameValidator(vertex, port);
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
