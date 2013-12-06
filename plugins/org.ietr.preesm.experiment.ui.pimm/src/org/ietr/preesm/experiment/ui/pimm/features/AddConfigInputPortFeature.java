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
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.IColorConstant;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.Port;

public class AddConfigInputPortFeature extends AbstractAddActorPortFeature {

	public static final IColorConstant CFG_INPUT_PORT_FOREGROUND = AddActorFeature.ACTOR_FOREGROUND;
	public static final IColorConstant CFG_INPUT_PORT_BACKGROUND = AddParameterFeature.PARAMETER_BACKGROUND;
	public static final PortPosition CFG_INPUT_PORT_POSITION = PortPosition.LEFT;
	public static final String CFG_INPUT_PORT_KIND = "cfg_in";

	/**
	 * Default constructor
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public AddConfigInputPortFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Add Config. Input Port";
	}

	@Override
	public String getDescription() {
		return "Add a configuration input port to the Actor";
	}

	@Override
	public PortPosition getPosition() {
		return CFG_INPUT_PORT_POSITION;
	}

	@Override
	public GraphicsAlgorithm addPortGA(GraphicsAlgorithm containerShape) {

		// Get the GaService
		IGaService gaService = Graphiti.getGaService();
		// Create the port GraphicAlgorithm
		int xy[] = { 0, 0, PORT_ANCHOR_GA_SIZE, (PORT_ANCHOR_GA_SIZE + 2) / 2,
				0, PORT_ANCHOR_GA_SIZE + 2 };
		Polygon triangle = gaService.createPolygon(containerShape, xy);

		triangle.setForeground(manageColor(CFG_INPUT_PORT_FOREGROUND));
		triangle.setBackground(manageColor(CFG_INPUT_PORT_BACKGROUND));
		triangle.setLineWidth(0);
		return triangle;
	}

	@Override
	public GraphicsAlgorithm addPortLabel(GraphicsAlgorithm containerShape,
			String portName) {
		// Get the GaService
		IGaService gaService = Graphiti.getGaService();

		// Create the text
		final Text text = gaService.createText(containerShape);
		text.setValue(portName);
		text.setFont(getPortFont());
		text.setForeground(manageColor(PORT_TEXT_FOREGROUND));

		// Layout the text
		int portFontHeight = AbstractAddActorPortFeature.PORT_FONT_HEIGHT;
		text.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);
		gaService.setHeight(text, portFontHeight);

		return text;
	}

	@Override
	public Port getNewPort(String portName, Actor actor) {
		ConfigInputPort newPort = PiMMFactory.eINSTANCE.createConfigInputPort();
		newPort.setName(portName);
		actor.getConfigInputPorts().add(newPort);
		return newPort;
	}

	@Override
	public String getPortKind() {
		return CFG_INPUT_PORT_KIND;
	}

}
