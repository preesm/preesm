package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.ietr.preesm.experiment.model.pimemoc.Actor;
import org.ietr.preesm.experiment.model.pimemoc.OutputPort;
import org.ietr.preesm.experiment.model.pimemoc.PIMeMoCFactory;
import org.ietr.preesm.experiment.model.pimemoc.Port;

public class AddOutputPortFeature extends AbstractAddActorPortFeature {

	public static final IColorConstant OUTPUT_PORT_FOREGROUND = AddActorFeature.ACTOR_FOREGROUND;
	public static final IColorConstant OUTPUT_PORT_BACKGROUND = new ColorConstant(
			234, 153, 153);
	public static final PortPosition OUTPUT_PORT_POSITION = PortPosition.RIGHT;
	public static final String OUTPUT_PORT_KIND = "output";

	/**
	 * Default constructor
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public AddOutputPortFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Add Output Port";
	}

	@Override
	public String getDescription() {
		return "Add an output port to the Actor";
	}

	@Override
	public PortPosition getPosition() {
		return OUTPUT_PORT_POSITION;
	}

	@Override
	public GraphicsAlgorithm addPortGA(GraphicsAlgorithm containerShape) {
		// Get the GaService
		IGaService gaService = Graphiti.getGaService();
		// Create the port GraphicAlcorithm
		Rectangle rectangle = gaService.createPlainRectangle(containerShape);
		rectangle.setForeground(manageColor(OUTPUT_PORT_FOREGROUND));
		rectangle.setBackground(manageColor(OUTPUT_PORT_BACKGROUND));
		rectangle.setLineWidth(1);
		gaService.setSize(rectangle, PORT_ANCHOR_GA_SIZE, PORT_ANCHOR_GA_SIZE);
		return rectangle;
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
		text.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
		gaService.setHeight(text, portFontHeight);

		return text;
	}

	@Override
	public Port getNewPort(String portName, Actor actor) {
		OutputPort newPort = PIMeMoCFactory.eINSTANCE.createOutputPort();
		newPort.setName(portName);
		actor.getOutputPorts().add(newPort);
		return newPort;
	}

	@Override
	public String getPortKind() {
		return OUTPUT_PORT_KIND;
	}
}
