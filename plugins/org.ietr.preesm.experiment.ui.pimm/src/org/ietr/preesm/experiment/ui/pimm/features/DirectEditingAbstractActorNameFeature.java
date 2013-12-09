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
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.util.VertexNameValidator;

/**
 * This class provide the feature that allows the direct edition of an
 * {@link AbstractActor} name.
 * 
 * @author kdesnos
 * 
 */
public class DirectEditingAbstractActorNameFeature extends AbstractDirectEditingFeature {

	/**
	 * Default constructor of the {@link DirectEditingAbstractActorNameFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public DirectEditingAbstractActorNameFeature(IFeatureProvider fp) {
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
		// support direct editing, if it is a AbstractVertex, and the user clicked
		// directly on the text and not somewhere else in the rectangle
		if (bo instanceof AbstractVertex && ga instanceof Text) {
			return true;
		}
		// direct editing not supported in all other cases
		return false;
	}

	@Override
	public String getInitialValue(IDirectEditingContext context) {
		// return the current name of the AbstractVertex
		PictogramElement pe = context.getPictogramElement();
		AbstractVertex vertex = (AbstractVertex) getBusinessObjectForPictogramElement(pe);
		return vertex.getName();
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		PiGraph graph = (PiGraph) getBusinessObjectForPictogramElement(getDiagram());
		PictogramElement pe = context.getPictogramElement();
		AbstractVertex renamedVertex = (AbstractVertex) getBusinessObjectForPictogramElement(pe);
		VertexNameValidator validator = new VertexNameValidator(graph,
				renamedVertex);
		
		return validator.isValid(value);
	}

	@Override
	public void setValue(String value, IDirectEditingContext context) {
		// set the new name for the AbstractVertex
		PictogramElement pe = context.getPictogramElement();
		AbstractVertex vertex = (AbstractVertex) getBusinessObjectForPictogramElement(pe);
		vertex.setName(value);

		// we know, that pe is the Shape of the Text, so its container is the
		// main shape of the AbstractVertex
		updatePictogramElement(((Shape) pe).getContainer());
		
		// Call the layout feature
        layoutPictogramElement(((Shape) pe).getContainer());
	}

}
