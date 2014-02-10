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
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;

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
					if (vertex instanceof DataInputInterface) {
						ga.setWidth(size.getWidth());
						Graphiti.getGaService().setLocation(ga, 0, 0);
					}
					else if (vertex instanceof DataOutputInterface) {
						ga.setWidth(size.getWidth());
						Graphiti.getGaService().setLocation(ga, 16 + 3, 0);	
					}
					else if (vertex instanceof ConfigOutputInterface) {
						ga.setWidth(size.getWidth());
						Graphiti.getGaService().setLocation(ga, 16 + 3, 0);
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
