package org.ietr.preesm.experiment.ui.pimm.shapes;

import java.util.List;

import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.impl.PolygonImpl;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

/**
 * Class used to handle the {@link GraphicsAlgorithm} of a {@link Triangle}
 * {@link Polygon}.
 * 
 * @author kdesnos
 * 
 */

@SuppressWarnings("restriction")
public class Triangle extends PolygonImpl {

	/**
	 * Default points
	 */
	protected static int xy[] = new int[] { 20, 0, 40, 40, 0, 40 };

	public Triangle(ContainerShape containerShape) {
		super();

		// Copy the behavior of IGaCreateService#CreatePolygon()
		IGaService gaService = Graphiti.getGaService();
		// Function is protected
		// gaService.setDefaultGraphicsAlgorithmAttributes(this);
		{
			gaService.setLocationAndSize(this, 0, 0, 0, 0);
			this.setLineStyle(LineStyle.SOLID);
			this.setLineWidth(1);
			this.setTransparency(0d);
		}
		this.setFilled(true);
		// Function is static and private
		// setContainer(this, containerShape);
		{
			if (containerShape instanceof PictogramElement) {
				PictogramElement pe = (PictogramElement) containerShape;
				pe.setGraphicsAlgorithm(this);
			} else if (containerShape instanceof GraphicsAlgorithm) {
				GraphicsAlgorithm parentGa = (GraphicsAlgorithm) containerShape;
				parentGa.getGraphicsAlgorithmChildren().add(this);
			}
		}

		for (int i = 0; i < 3; i++) {
			Point p = gaService.createPoint(xy[2 * i], xy[2 * i + 1]);
			this.getPoints().add(p);
		}
	}

	protected void setSize(int width, int height) {
		int coord[] = new int[] { width / 2, 0, width, height, 0, height };

		List<Point> points = this.getPoints();
		int i = 0;
		for (Point p : points) {
			p.setX(coord[i]);
			p.setY(coord[i + 1]);
			i += 2;
		}
	}

	@Override
	public void setWidth(int newWidth) {
		super.setWidth(newWidth);
		setSize(newWidth, height);
	}

	@Override
	public void setHeight(int newHeight) {
		super.setHeight(newHeight);
		setSize(width, newHeight);
	}
}
