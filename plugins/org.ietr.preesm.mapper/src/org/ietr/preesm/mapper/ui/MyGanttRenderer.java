/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2013)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.ietr.preesm.mapper.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.ui.RectangleEdge;

/**
 * This renderer plots rounded rectangles.
 *
 * @author mpelcat
 */
public class MyGanttRenderer extends GanttRenderer {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** Map of the task colors. */
  private final Map<String, Color> colorMap = new LinkedHashMap<>();

  /**
   * Draws the tasks/subtasks for one item.
   *
   * @param g2
   *          the graphics device.
   * @param state
   *          the renderer state.
   * @param dataArea
   *          the data plot area.
   * @param plot
   *          the plot.
   * @param domainAxis
   *          the domain axis.
   * @param rangeAxis
   *          the range axis.
   * @param dataset
   *          the data.
   * @param row
   *          the row index (zero-based).
   * @param column
   *          the column index (zero-based).
   */
  @Override
  protected void drawTasks(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea,
      final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis,
      final GanttCategoryDataset dataset, final int row, final int column) {

    final int count = dataset.getSubIntervalCount(row, column);
    if (count == 0) {
      drawTask(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row, column);
    }

    for (int subinterval = 0; subinterval < count; subinterval++) {

      final RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();

      // value 0
      final Number value0 = dataset.getStartValue(row, column, subinterval);
      if (value0 == null) {
        return;
      }
      double translatedValue0 = rangeAxis.valueToJava2D(value0.doubleValue(), dataArea, rangeAxisLocation);

      // value 1
      final Number value1 = dataset.getEndValue(row, column, subinterval);
      if (value1 == null) {
        return;
      }
      double translatedValue1 = rangeAxis.valueToJava2D(value1.doubleValue(), dataArea, rangeAxisLocation);

      if (translatedValue1 < translatedValue0) {
        final double temp = translatedValue1;
        translatedValue1 = translatedValue0;
        translatedValue0 = temp;
      }

      final double rectStart = calculateBarW0(plot, plot.getOrientation(), dataArea, domainAxis, state, row, column);
      final double rectLength = Math.abs(translatedValue1 - translatedValue0);
      final double rectBreadth = state.getBarWidth();

      // DRAW THE BARS...
      RoundRectangle2D bar = null;

      bar = new RoundRectangle2D.Double(translatedValue0, rectStart, rectLength, rectBreadth, 10.0, 10.0);

      /* Paint seriesPaint = */getItemPaint(row, column);

      if ((((TaskSeriesCollection) dataset).getSeriesCount() > 0)
          && (((TaskSeriesCollection) dataset).getSeries(0).getItemCount() > column)
          && (((TaskSeriesCollection) dataset).getSeries(0).get(column).getSubtaskCount() > subinterval)) {
        g2.setPaint(getRandomBrightColor(
            ((TaskSeriesCollection) dataset).getSeries(0).get(column).getSubtask(subinterval).getDescription()));

      }
      g2.fill(bar);

      if (isDrawBarOutline() && (state.getBarWidth() > BarRenderer.BAR_OUTLINE_WIDTH_THRESHOLD)) {
        g2.setStroke(getItemStroke(row, column));
        g2.setPaint(getItemOutlinePaint(row, column));
        g2.draw(bar);
      }

      // Displaying the tooltip inside the bar if enough space is
      // available
      if (getToolTipGenerator(row, column) != null) {
        // Getting the string to display
        final String tip = getToolTipGenerator(row, column).generateToolTip(dataset, subinterval, column);

        // Truncting the string if it is too long
        String subtip = "";
        if (rectLength > 0) {
          final double percent = (g2.getFontMetrics().getStringBounds(tip, g2).getWidth() + 10) / rectLength;

          if (percent > 1.0) {
            subtip = tip.substring(0, (int) (tip.length() / percent));
          } else if (percent > 0) {
            subtip = tip;
          }

          // Setting font and color
          final Font font = new Font("Garamond", Font.BOLD, 12);
          g2.setFont(font);
          g2.setColor(Color.WHITE);

          // Testing width and displaying
          if (!subtip.isEmpty()) {
            g2.drawString(subtip, (int) translatedValue0 + 5, (int) rectStart + g2.getFontMetrics().getHeight());
          }
        }
      }

      // collect entity and tool tip information...
      if (state.getInfo() != null) {
        final EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
          String tip = null;
          if (getToolTipGenerator(row, column) != null) {
            tip = getToolTipGenerator(row, column).generateToolTip(dataset, subinterval, column);
          }
          String url = null;
          if (getItemURLGenerator(row, column) != null) {
            url = getItemURLGenerator(row, column).generateURL(dataset, row, column);
          }
          final CategoryItemEntity entity = new CategoryItemEntity(bar, tip, url, dataset, dataset.getRowKey(row),
              dataset.getColumnKey(column));
          entities.add(entity);
        }
      }
    }

  }

  /**
   * Chooses colors for the vertices depending on their names.
   *
   * @param name
   *          the name
   * @return the random bright color
   */
  private Color getRandomBrightColor(final String name) {

    Color c = null;

    if (this.colorMap.containsKey(name)) {
      c = this.colorMap.get(name);
    } else {
      if (name.indexOf("__transfer") == 0) {
        c = getRandomColor(190.0, 100.0, 130.0, 20);
      } else if (name.indexOf("__write") == 0) {
        c = getRandomColor(240.0, 100.0, 100.0, 20);
      } else if (name.indexOf("__read") == 0) {
        c = getRandomColor(250.0, 180.0, 180.0, 20);
      } else if (name.indexOf("__overhead") == 0) {
        c = getRandomColor(130.0, 160.0, 100.0, 20);
      } else if (name.indexOf("__involvement") == 0) {
        c = getRandomColor(210.0, 150.0, 50.0, 20);
      } else if ((name.indexOf("__send") == 0) || (name.indexOf("__receive") == 0)) {
        c = getRandomColor(160.0, 130.0, 100.0, 20);
      } else if (name.indexOf("__@") != -1) {
        final int atNumber = name.indexOf("__@");

        final int index = Integer.valueOf(name.substring(atNumber + 3, atNumber + 4)) - 1;
        // iteration task color
        c = getRandomColor(27.0 + ((50 * index) % 200), 182.0 - ((50 * index) % 160), 233.0, 20);
      } else {
        c = getRandomColor(130.0, 100.0, 160.0, 20);
      }

      this.colorMap.put(name, c);
    }

    return c;
  }

  /**
   * Given a color c, returns a random color close to c.
   *
   * @param r
   *          the r
   * @param g
   *          the g
   * @param b
   *          the b
   * @param liberty
   *          the liberty
   * @return the random color
   */
  private Color getRandomColor(final Double r, final Double g, final Double b, final int liberty) {

    final int newR = genNewRandomColorComponent(r, liberty);
    final int newG = genNewRandomColorComponent(g, liberty);
    final int newB = genNewRandomColorComponent(b, liberty);

    return new Color(newR, newG, newB);
  }

  private int genNewRandomColorComponent(final Double r, final int liberty) {
    return (int) Math.round(Math.max(0, Math.min(((Math.random() * liberty) + r), 255)));
  }
}
