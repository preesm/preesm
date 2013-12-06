package org.ietr.preesm.experiment.ui.pimm.decorators;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;
import org.ietr.preesm.experiment.model.pimm.Delay;

/**
 * Class providing methods to retrieve the {@link IDecorator} of a
 * {@link Delay}.<br>
 * 
 * @author jheulot
 * 
 */
public class DelayDecorators {

	/**
	 * Methods that returns all the {@link IDecorator} for a given
	 * {@link Delay}.
	 * 
	 * @param delay
	 *            the treated {@link Delay}
	 * @param pe
	 *            the {@link PictogramElement} to decorate
	 * @return the {@link IDecorator} table.
	 */
	public static IDecorator[] getDecorators(Delay delay,
			PictogramElement pe) {

		List<IDecorator> decorators = new ArrayList<IDecorator>();
		
		// Check if the parameter expression is correct
		IDecorator expressionDecorator = getExpressionDecorator(delay, pe);
		if (expressionDecorator != null) {
			decorators.add(expressionDecorator);
		}
		
		IDecorator[] result = new IDecorator[decorators.size()];
		decorators.toArray(result);

		return result;
	}

	/**
	 * Get the {@link IDecorator} indicating if the
	 * {@link Delay} have a valid expression.
	 * 
	 * @param delay
	 *            the {@link Delay} to test
	 * @param pe
	 *            the {@link PictogramElement} of the {@link Delay}
	 * @return the {@link IDecorator} or <code>null</code>.
	 */
	protected static IDecorator getExpressionDecorator(Delay delay, PictogramElement pe) {
		ImageDecorator imageRenderingDecorator = new ImageDecorator(
				IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK);
		imageRenderingDecorator.setMessage("Problems in parameter resolution");
		
		
		if(((Delay)delay).getExpression().evaluate().contains("Error")){
			imageRenderingDecorator.setX(pe.getGraphicsAlgorithm().getWidth()/2-8);
			imageRenderingDecorator.setY(1);

			return imageRenderingDecorator;
		}
		return null;
	}
}
