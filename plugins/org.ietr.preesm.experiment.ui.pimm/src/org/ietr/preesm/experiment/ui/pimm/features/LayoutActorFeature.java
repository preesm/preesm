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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.BoxRelativeAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.InputPort;
import org.ietr.preesm.experiment.model.pimm.OutputPort;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;

/**
 * Layout Feature for Actors
 * 
 * @author kdesnos
 * 
 */
public class LayoutActorFeature extends AbstractLayoutFeature {

	/**
	 * Default Constructor of the {@link LayoutActorFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public LayoutActorFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canLayout(ILayoutContext context) {
		// return true, if pictogram element is linked to an Actor
		PictogramElement pe = context.getPictogramElement();
		if (!(pe instanceof ContainerShape)) {
			return false;
		}

		EList<EObject> businessObjects = pe.getLink().getBusinessObjects();
		return businessObjects.size() == 1
				&& businessObjects.get(0) instanceof Actor;
	}

	/**
	 * Return the new Height of the shape. <br>
	 * <br>
	 * 
	 * The new height is computed so that all text are completely visible.
	 * Consequently, the method check the width of all Text children shape.<br>
	 * <br>
	 * <b> The method does not apply the new height to the shape. </b> Use
	 * {@link LayoutActorFeature#setNewHeight} for that purpose.
	 * 
	 * @param childrenShapes
	 *            the children shapes of the Actor
	 * @param anchorShapes
	 *            the anchor shapes of the actor
	 * @return
	 */
	protected int getNewHeight(EList<Shape> childrenShapes,
			EList<Anchor> anchorShapes) {
		// RETRIEVE THE NAME HEIGHT
		int nameHeight = 0;
		{
			// Scan the children shape looking for the actor name
			for (Shape shape : childrenShapes) {
				GraphicsAlgorithm child = shape.getGraphicsAlgorithm();
				// The name should be the only children with type text
				if (child instanceof Text) {
					String text = ((Text) child).getValue();
					Font font = ((Text) child).getFont();

					// Retrieve the size of the text
					IDimension size = GraphitiUi.getUiLayoutService()
							.calculateTextSize(text, font);

					// Retrieve the space of the name
					// (+5 to add space and lighten the actor representation)
					nameHeight = size.getHeight() + 5;
				}
			}
		}

		// RETRIEVE THE ANCHOR HEIGHT
		int anchorMaxHeight = 0;
		{
			int inputsHeight = 0;
			int outputsHeight = 0;
			int cfgInputsHeight = 0;
			int cfgOutputsHeight = 0;
			for (Anchor anchor : anchorShapes) {
				// Retrieve the children of the invisible rectangle of the
				// anchor
				EList<GraphicsAlgorithm> anchorChildren = anchor
						.getGraphicsAlgorithm().getGraphicsAlgorithmChildren();

				// Scan the children of the invisible rectangle looking for
				// the label
				for (GraphicsAlgorithm child : anchorChildren) {
					// The Label of the anchor should be the only child with
					// type Text
					if (child instanceof Text) {
						// Retrieve the size of the text
						String text = ((Text) child).getValue();
						Font font = ((Text) child).getFont();
						IDimension size = GraphitiUi.getUiLayoutService()
								.calculateTextSize(text, font);
						// Write the port font height in
						// AbstractAddActorPortFeature. This is needed when
						// opening a saved graph because in such case
						// PORT_FONT_HEIGHT will remain equal to 0 until a port
						// is added to the graph
						AbstractAddActorPortFeature.PORT_FONT_HEIGHT = size
								.getHeight();
						EObject obj = (EObject) getBusinessObjectForPictogramElement(anchor);

						switch (obj.eClass().getClassifierID()) {
						case PiMMPackage.CONFIG_INPUT_PORT:
							cfgInputsHeight += size.getHeight();
							break;
						case PiMMPackage.CONFIG_OUTPUT_PORT:
							cfgOutputsHeight += size.getHeight();
							break;

						case PiMMPackage.INPUT_PORT:
							inputsHeight += size.getHeight();
							break;
						case PiMMPackage.OUTPUT_PORT:
							outputsHeight += size.getHeight();
							break;
						}
					}
				}
			}
			anchorMaxHeight = Math.max(cfgInputsHeight, cfgOutputsHeight)
					+ Math.max(inputsHeight, outputsHeight);
		}

		return anchorMaxHeight + nameHeight;
	}

	/**
	 * Return the new width of the shape. <br>
	 * <br>
	 * 
	 * The new width is computed so that all text are completely visible.
	 * Consequently, the method check the width of all Text children shape. <br>
	 * <br>
	 * <b> The method does not apply the new width to the shape. </b> Use
	 * {@link LayoutActorFeature#setNewWidth} for that purpose.
	 * 
	 * @param childrenShapes
	 *            the children shapes of the Actor
	 * @param anchorShapes
	 *            the anchor shapes of the actor
	 * @return the new width
	 */
	protected int getNewWidth(EList<Shape> childrenShapes,
			EList<Anchor> anchorShapes) {
		// RETRIEVE THE NAME WIDTH
		int nameWidth = 0;
		{
			// Scan the children shape looking for the actor name
			for (Shape shape : childrenShapes) {
				GraphicsAlgorithm child = shape.getGraphicsAlgorithm();
				// The name should be the only children with type text
				if (child instanceof Text) {
					String text = ((Text) child).getValue();
					Font font = ((Text) child).getFont();

					// Retrieve the size of the text
					IDimension size = GraphitiUi.getUiLayoutService()
							.calculateTextSize(text, font);

					// Retrieve the space of the name
					// (+30 to add space and lighten the actor representation)
					// And allow the addition of a decorator
					nameWidth = size.getWidth() + 30;

				}
			}
		}

		// RETRIEVE THE ANCHOR WIDTH
		int anchorWidth = 0;
		{
			int inputMaxWidth = 0;
			int outputMaxWidth = 0;

			// Retrieve a few constants
			int gaSize = AbstractAddActorPortFeature.PORT_ANCHOR_GA_SIZE;
			int labelGASpace = AbstractAddActorPortFeature.PORT_LABEL_GA_SPACE;

			for (Anchor anchor : anchorShapes) {
				// Retrieve the children of the invisible rectangle of the
				// anchor
				EList<GraphicsAlgorithm> anchorChildren = anchor
						.getGraphicsAlgorithm().getGraphicsAlgorithmChildren();

				// Scan the children of the invisible rectangle looking for
				// the label
				for (GraphicsAlgorithm child : anchorChildren) {
					// The Label of the anchor should be the only child with
					// type Text
					if (child instanceof Text) {
						// Retrieve the size of the text
						String text = ((Text) child).getValue();
						Font font = ((Text) child).getFont();
						IDimension size = GraphitiUi.getUiLayoutService()
								.calculateTextSize(text, font);

						if (((BoxRelativeAnchor) anchor).getRelativeWidth() == 0.0) {
							// This is an input port
							inputMaxWidth = Math.max(size.getWidth() + gaSize
									+ labelGASpace, inputMaxWidth);
						} else {
							// This is an output port
							outputMaxWidth = Math.max(size.getWidth() + gaSize
									+ labelGASpace, outputMaxWidth);
						}
					}
				}
			}
			anchorWidth = inputMaxWidth + outputMaxWidth;
			// We add an extra space of 8 between inputs and output ports to
			// lighten the actor pictogram
			anchorWidth += 8;
		}

		int maxWidth = Math.max(anchorWidth, nameWidth);
		return maxWidth;
	}

	@Override
	public boolean layout(ILayoutContext context) {
		boolean anythingChanged = false;

		// Retrieve the shape and the graphic algorithm
		ContainerShape containerShape = (ContainerShape) context
				.getPictogramElement();
		GraphicsAlgorithm containerGa = containerShape.getGraphicsAlgorithm();

		// Retrieve all contained shapes
		EList<Shape> childrenShapes = containerShape.getChildren();
		EList<Anchor> anchorShapes = containerShape.getAnchors();

		// Get the new Width of the shape
		int newWidth = getNewWidth(childrenShapes, anchorShapes);

		// Apply change if newWidth is different from the current
		if (newWidth != containerGa.getWidth()) {
			setNewWidth(newWidth, childrenShapes);
			containerGa.setWidth(newWidth);
			anythingChanged = true;
		}

		// Get the new Height of the shape
		int newHeight = getNewHeight(childrenShapes, anchorShapes);
		// Apply change (always since it will organize ports even if the shape
		// height did not change)
		setNewHeight(newHeight, childrenShapes, anchorShapes);
		containerGa.setHeight(newHeight);

		// If Anything changed, call the move feature to layout connections
		{
			MoveAbstractActorFeature moveFeature = new MoveAbstractActorFeature(
					getFeatureProvider());
			MoveShapeContext moveCtxt = new MoveShapeContext(containerShape);
			moveCtxt.setDeltaX(0);
			moveCtxt.setDeltaY(0);
			ILocation csLoc = Graphiti.getPeLayoutService()
					.getLocationRelativeToDiagram(containerShape);
			moveCtxt.setLocation(csLoc.getX(), csLoc.getY());
			moveFeature.moveShape(moveCtxt);
		}

		return anythingChanged;
	}

	/**
	 * Apply the new height of the shape.
	 * 
	 * @param newHeigt
	 *            the new height to apply
	 * @param childrenShapes
	 *            the children shape (contain the name)
	 * @param anchorShapes
	 *            the anchors of the shape
	 * @return true if something was changed
	 */
	protected boolean setNewHeight(int newHeigt, EList<Shape> childrenShapes,
			EList<Anchor> anchorShapes) {
		boolean anythingChanged = false;

		// No need to change the height of the name

		// Retrieve and separate the inputs and outputs ports
		List<BoxRelativeAnchor> inputs = new ArrayList<>();
		List<BoxRelativeAnchor> outputs = new ArrayList<>();
		int nbConfigInput = 0;
		int nbConfigOutput = 0;
		for (Anchor anchor : anchorShapes) {

			if (getBusinessObjectForPictogramElement(anchor) instanceof ConfigInputPort) {
				inputs.add(nbConfigInput, (BoxRelativeAnchor) anchor);
				nbConfigInput++;
			} else if (getBusinessObjectForPictogramElement(anchor) instanceof InputPort) {
				inputs.add((BoxRelativeAnchor) anchor);
			}

			if (getBusinessObjectForPictogramElement(anchor) instanceof ConfigOutputPort) {
				outputs.add(nbConfigOutput, (BoxRelativeAnchor) anchor);
				nbConfigOutput++;
			} // The else is important because a ConfigOutputPort IS an
				// OutputPort
			else if (getBusinessObjectForPictogramElement(anchor) instanceof OutputPort) {
				outputs.add((BoxRelativeAnchor) anchor);
			}
		}
		int maxNbConfigPort = Math.max(nbConfigInput, nbConfigOutput);

		// Place the inputs
		int portFontHeight = AbstractAddActorPortFeature.PORT_FONT_HEIGHT;
		// The first port is placed below the name
		int y = portFontHeight + 5; // font.height + a space of 5
		for (int i = 0; i < inputs.size(); i++) {
			int configSpace = (i < nbConfigInput) ? 0 : maxNbConfigPort
					- nbConfigInput;
			double relativeHeight = (y + (i + configSpace) * portFontHeight)
					/ (double) newHeigt;
			if (inputs.get(i).getRelativeHeight() != relativeHeight) {
				anythingChanged = true;
				inputs.get(i).setRelativeHeight(relativeHeight);
			}

		}

		// Place the outputs
		y = portFontHeight + 5; // font.height + a space of 5
		for (int i = 0; i < outputs.size(); i++) {
			int configSpace = (i < nbConfigOutput) ? 0 : maxNbConfigPort
					- nbConfigOutput;
			double relativeHeight = (y + (i + configSpace) * portFontHeight)
					/ (double) newHeigt;
			if (outputs.get(i).getRelativeHeight() != relativeHeight) {
				anythingChanged = true;
				outputs.get(i).setRelativeHeight(relativeHeight);
			}
		}

		return anythingChanged;
	}

	/**
	 * Apply the new width of the shape.
	 * 
	 * @param newWidth
	 *            the new width of the actor
	 * @param childrenShapes
	 *            the children shapes to resize
	 */
	protected void setNewWidth(int newWidth, EList<Shape> childrenShapes) {
		// Scan the children shapes
		for (Shape shape : childrenShapes) {
			GraphicsAlgorithm child = shape.getGraphicsAlgorithm();
			if (child instanceof Text) {
				Orientation align = ((Text) child).getHorizontalAlignment();

				// If the text is the name of the object
				if (align == Orientation.ALIGNMENT_CENTER) {
					// The name is centered and has the same width as the actor
					// rounded rectangle
					child.setWidth(newWidth);
				}
			}
		}

		// No need to layout the ports width (for now)
	}
}
