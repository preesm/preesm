package org.ietr.preesm.experiment.ui.pimm.decorators;

import java.util.List;

import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.util.DependencyCycleDetector;
import org.ietr.preesm.experiment.ui.pimm.util.PiMMImageProvider;

/**
 * Class providing methods to retrieve the {@link IDecorator} of an
 * {@link Parameter}.<br>
 * <b> This decorators only works for Parameters and not for configuration input
 * interfaces.</b>
 * 
 * @author kdesnos
 * 
 */
public class ParameterDecorators {

	/**
	 * Methods that returns all the {@link IDecorator} for a given
	 * {@link Parameter}.
	 * 
	 * @param parameter
	 *            the treated {@link Parameter}
	 * @param pe
	 *            the {@link PictogramElement} to decorate
	 * @return the {@link IDecorator} table.
	 */
	public static IDecorator[] getDecorators(Parameter parameter,
			PictogramElement pe) {

		// Check if the parameter belongs to a cycle
		DependencyCycleDetector detector = new DependencyCycleDetector();
		detector.doSwitch(parameter);
		if (detector.cyclesDetected()) {
			for (List<Parameter> cycle : detector.getCycles()) {
				if (cycle.contains(parameter)) {
					ImageDecorator imageRenderingDecorator = new ImageDecorator(
							IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK);
					String message = "Parameter belongs to a cycle: ";
					for (Parameter param : cycle) {
						message += param.getName() + ">";
					}
					message += parameter.getName();
					imageRenderingDecorator.setMessage(message);
					imageRenderingDecorator.setX((pe.getGraphicsAlgorithm()
							.getWidth() / 4) - 4);
					imageRenderingDecorator.setY(8);

					return new IDecorator[] { imageRenderingDecorator };
				}

				// If the parameter is not contained in a detected cycle but
				// cycles were detected
				// its locally static status cannot be determined
				ImageDecorator imageRenderingDecorator = new ImageDecorator(
						IPlatformImageConstants.IMG_ECLIPSE_WARNING_TSK);

				imageRenderingDecorator
						.setMessage("Parameter depends on parameters contained in a cycle.");
				imageRenderingDecorator.setX((pe.getGraphicsAlgorithm()
						.getWidth() / 4) - 4);
				imageRenderingDecorator.setY(8);

				return new IDecorator[] { imageRenderingDecorator };
			}

		}

		// Check if the parameter is locally static if
		if (!parameter.isLocallyStatic()) {
			ImageDecorator imageRenderingDecorator = new ImageDecorator(
					PiMMImageProvider.IMG_WHITE_DOT_BLUE_LINE);

			imageRenderingDecorator
					.setMessage("Dynamically Configurable Parameter");
			imageRenderingDecorator
					.setX((pe.getGraphicsAlgorithm().getWidth() / 4) - 2);
			imageRenderingDecorator.setY(8);

			return new IDecorator[] { imageRenderingDecorator };
		}

		return new IDecorator[0];
	}
}
