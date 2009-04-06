/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.plugin.mapper.plot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.ui.RectangleEdge;

/**
 * This renderer plots rounded rectangles
 * 
 * @author mpelcat
 */
public class MyGanttRenderer extends GanttRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Map of the task colors
	 */
	HashMap<String, Color> colorMap = new HashMap<String, Color>();

	/**
	 * Draws the tasks/subtasks for one item.
	 * 
	 * @param g2
	 *            the graphics device.
	 * @param state
	 *            the renderer state.
	 * @param dataArea
	 *            the data plot area.
	 * @param plot
	 *            the plot.
	 * @param domainAxis
	 *            the domain axis.
	 * @param rangeAxis
	 *            the range axis.
	 * @param dataset
	 *            the data.
	 * @param row
	 *            the row index (zero-based).
	 * @param column
	 *            the column index (zero-based).
	 */
	@Override
	protected void drawTasks(Graphics2D g2, CategoryItemRendererState state,
			Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis,
			ValueAxis rangeAxis, GanttCategoryDataset dataset, int row,
			int column) {

		int count = dataset.getSubIntervalCount(row, column);
		if (count == 0) {
			drawTask(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset,
					row, column);
		}

		for (int subinterval = 0; subinterval < count; subinterval++) {

			RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();

			// value 0
			Number value0 = dataset.getStartValue(row, column, subinterval);
			if (value0 == null) {
				return;
			}
			double translatedValue0 = rangeAxis.valueToJava2D(value0
					.doubleValue(), dataArea, rangeAxisLocation);

			// value 1
			Number value1 = dataset.getEndValue(row, column, subinterval);
			if (value1 == null) {
				return;
			}
			double translatedValue1 = rangeAxis.valueToJava2D(value1
					.doubleValue(), dataArea, rangeAxisLocation);

			if (translatedValue1 < translatedValue0) {
				double temp = translatedValue1;
				translatedValue1 = translatedValue0;
				translatedValue0 = temp;
			}

			double rectStart = calculateBarW0(plot, plot.getOrientation(),
					dataArea, domainAxis, state, row, column);
			double rectLength = Math.abs(translatedValue1 - translatedValue0);
			double rectBreadth = state.getBarWidth();

			// DRAW THE BARS...
			RoundRectangle2D bar = null;

			if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
				bar = new RoundRectangle2D.Double(translatedValue0, rectStart,
						rectLength, rectBreadth, 10.0, 10.0);
			} else if (plot.getOrientation() == PlotOrientation.VERTICAL) {
				bar = new RoundRectangle2D.Double(rectStart, translatedValue0,
						rectBreadth, rectLength, 10.0, 10.0);
			}

			RoundRectangle2D completeBar = null;
			RoundRectangle2D incompleteBar = null;
			Number percent = dataset.getPercentComplete(row, column,
					subinterval);
			double start = getStartPercent();
			double end = getEndPercent();
			if (percent != null) {
				double p = percent.doubleValue();
				if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
					completeBar = new RoundRectangle2D.Double(translatedValue0,
							rectStart + start * rectBreadth, rectLength * p,
							rectBreadth * (end - start), 10.0, 10.0);
					incompleteBar = new RoundRectangle2D.Double(
							translatedValue0 + rectLength * p, rectStart
									+ start * rectBreadth,
							rectLength * (1 - p), rectBreadth * (end - start),
							10.0, 10.0);
				} else if (plot.getOrientation() == PlotOrientation.VERTICAL) {
					completeBar = new RoundRectangle2D.Double(rectStart + start
							* rectBreadth, translatedValue0 + rectLength
							* (1 - p), rectBreadth * (end - start), rectLength
							* p, 10.0, 10.0);
					incompleteBar = new RoundRectangle2D.Double(rectStart
							+ start * rectBreadth, translatedValue0,
							rectBreadth * (end - start), rectLength * (1 - p),
							10.0, 10.0);
				}

			}

			/* Paint seriesPaint = */getItemPaint(row, column);

			if (((TaskSeriesCollection) dataset).getSeriesCount() > 0)
				if (((TaskSeriesCollection) dataset).getSeries(0)
						.getItemCount() > column)
					if (((TaskSeriesCollection) dataset).getSeries(0).get(
							column).getSubtaskCount() > subinterval)
						g2
								.setPaint(getRandomBrightColor(((TaskSeriesCollection) dataset)
										.getSeries(0).get(column).getSubtask(
												subinterval).getDescription()));
			g2.fill(bar);
			if (completeBar != null) {
				g2.setPaint(getCompletePaint());
				g2.fill(completeBar);
			}
			if (incompleteBar != null) {
				g2.setPaint(getIncompletePaint());
				g2.fill(incompleteBar);
			}
			if (isDrawBarOutline()
					&& state.getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
				g2.setStroke(getItemStroke(row, column));
				g2.setPaint(getItemOutlinePaint(row, column));
				g2.draw(bar);
			}

			// collect entity and tool tip information...
			if (state.getInfo() != null) {
				EntityCollection entities = state.getEntityCollection();
				if (entities != null) {
					String tip = null;
					if (getToolTipGenerator(row, column) != null) {
						tip = getToolTipGenerator(row, column).generateToolTip(
								dataset, subinterval, column);
					}
					String url = null;
					if (getItemURLGenerator(row, column) != null) {
						url = getItemURLGenerator(row, column).generateURL(
								dataset, row, column);
					}
					CategoryItemEntity entity = new CategoryItemEntity(bar,
							tip, url, dataset, dataset.getRowKey(row), dataset
									.getColumnKey(column));
					entities.add(entity);
				}
			}
		}
	}

	Color getRandomBrightColor(String name) {

		Color c = null;

		if (colorMap.containsKey(name)) {
			c = colorMap.get(name);
		} else {
			Double r, g, b;

			if (name.indexOf("__transfer") == 0) {
				r = Math.random() * (255 - 190) + 190;
				g = 100.0;
				b = 130.0;
			} else if (name.indexOf("__overhead") == 0) {
				r = 130.0;
				g = Math.random() * (255 - 160) + 160;
				b = 100.0;
			} else if (name.indexOf("__involvement") == 0) {
				r = Math.random() * (255 - 210) + 210;
				g = 150.0;
				b = 50.0;
			} else if (name.indexOf("__send") == 0 || name.indexOf("__receive") == 0) {
				r = Math.random() * (255 - 160) + 160;
				g = 130.0;
				b = 100.0;
			} else {
				r = 130.0;
				g = 100.0;
				b = Math.random() * (255 - 160) + 160;
			}

			c = new Color(r.intValue(), g.intValue(), b.intValue());
			colorMap.put(name, c);
		}

		return c;
	}
}
