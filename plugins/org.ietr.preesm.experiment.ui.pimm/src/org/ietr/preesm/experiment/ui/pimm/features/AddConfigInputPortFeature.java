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
