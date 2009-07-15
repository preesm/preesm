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
package org.ietr.preesm.plugin.scheduling.listsched.plotter;

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
