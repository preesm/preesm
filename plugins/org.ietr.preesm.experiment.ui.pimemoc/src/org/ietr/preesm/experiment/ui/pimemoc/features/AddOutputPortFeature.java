package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;

public class AddOutputPortFeature extends AbstractAddActorPortFeature {
	
	public static final IColorConstant OUTPUT_PORT_FOREGROUND = AddActorFeature.ACTOR_FOREGROUND;
	public static final IColorConstant OUTPUT_PORT_BACKGROUND = new ColorConstant(
			234, 153, 153);
	public static final PortPosition OUTPUT_PORT_POSITION = PortPosition.RIGHT;

	/**
	 * Default constructor
	 * @param fp
	 * the feature provider
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
	public IColorConstant getForegreoundColor() {
		return OUTPUT_PORT_FOREGROUND;
	}

	@Override
	public IColorConstant getBackgroundColor() {
		return OUTPUT_PORT_BACKGROUND;
	}

	@Override
	public PortPosition getPosition() {
		return OUTPUT_PORT_POSITION;
	}
	

}
