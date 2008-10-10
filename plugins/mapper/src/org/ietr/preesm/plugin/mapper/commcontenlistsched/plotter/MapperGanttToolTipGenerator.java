/**
 * 
 */
package org.ietr.preesm.plugin.mapper.commcontenlistsched.plotter;

import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeriesCollection;

/**
 * @author mpelcat
 * 
 */
public class MapperGanttToolTipGenerator implements CategoryToolTipGenerator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jfree.chart.labels.CategoryToolTipGenerator#generateToolTip(org.jfree
	 * .data.category.CategoryDataset, int, int)
	 */
	@Override
	public String generateToolTip(CategoryDataset set, int row, int column) {

		String tooltip = new String();

		TaskSeriesCollection collection = (TaskSeriesCollection) set;

		if ((collection.getSeries(0).getItemCount() > column)
				&& (collection.getSeries(0).get(column).getSubtaskCount() > row)) {
			Task currentTask = collection.getSeries(0).get(column).getSubtask(
					row);

			tooltip = "\"" + currentTask.getDescription() + "\"" + "("
					+ currentTask.getDuration().getStart().getTime() + "-"
					+ currentTask.getDuration().getEnd().getTime() + ")";
		}

		return tooltip;
	}

}
