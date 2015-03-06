/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy, Yaset Oliva Venegas
 * 
 * [mpelcat,jnezan,kdesnos,jheulot,cguy,yoliva]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.mapper.stats.exporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.mapper.abc.IAbc;
import org.ietr.preesm.mapper.gantt.GanttComponent;
import org.ietr.preesm.mapper.gantt.GanttData;
import org.ietr.preesm.mapper.gantt.GanttTask;
import org.ietr.preesm.mapper.ui.stats.StatGenerator;

/**
 * This class exports stats from an IAbc (architecture benchmark computer) in
 * XML format
 * 
 * @author cguy
 *
 */
public class XMLStatsExporter {

	private static final String TASKCOLOR = "#c896fa";
	private static final String NL = "\n";
	private static final String TAB = "\t";
	private static final String NLT = "\n\t";

	private StringBuffer result = new StringBuffer();

	private void append(Object s) {
		result.append(s);
	}

	/**
	 * Export generated stats from an IAbc to an xml file
	 * 
	 * @param abc
	 *            the IAbc containing the scheduling of each task
	 * @param xmlPath
	 *            the IPath where to write the stats
	 */
	public void exportXMLStats(IAbc abc, File file) {
		// Generate the stats to write in an xml file
		String content = generateXMLStats(abc);
		// Write the file
		FileWriter out;
		try {
			out = new FileWriter(file);
			out.write(content);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generate a String at an XML format from an IAbc
	 * 
	 * @param abc
	 *            the IAbc containing the scheduling of each task
	 * @return a String containing the stats at an xml format
	 */
	public String generateXMLStats(IAbc abc) {
		append(NL + "<data>");
		// Generate scheduling stats (when and on which core a given task is
		// executed)
		generateSchedulingStats(abc.getGanttData());
		// Generate performance stats (loads of the core; work, span and
		// implementation length; number of cores used over total number of
		// cores)
		generatePerformanceStats(abc);
		append(NL + "</data>");
		return result.toString();
	}

	private void generatePerformanceStats(IAbc abc) {
		// Starting the performace stats
		StatGenerator statGen = new StatGenerator(abc, abc.getScenario(), null);
		append(NLT + "<perfs>");
		// Work length
		append(NLT + TAB + "work=\"");
		try {
			append(statGen.getDAGWorkLength());
		} catch (WorkflowException e) {
			e.printStackTrace();
		}
		append("\"");
		// Span length
		append(NLT + TAB + "span=\"");
		append(statGen.getDAGSpanLength());
		append("\"");
		// Implementation length
		append(NLT + TAB + "impl_length=\"");
		append(statGen.getResultTime());
		append("\"");
		// Implementation number of cores
		append(NLT + "impl_nbCores=\"");
		append(statGen.getNbMainTypeOperators());
		append("\"");
		// Implementation number of used cores
		append(NLT + TAB + "impl_nbUsedCores=\"");
		append(statGen.getNbUsedOperators());
		append("\"");
		Set<ComponentInstance> opSet = DesignTools.getOperatorInstances(abc
				.getArchitecture());
		for (ComponentInstance op : opSet)
			generateCoreLoad(op, statGen);
		// Ending the performance stats
		append(NLT + "</perfs>");
	}

	private void generateCoreLoad(ComponentInstance op, StatGenerator statGen) {
		// Starting core load stat
		append(NLT + TAB + "<core");
		// Id of the core
		append(NLT + TAB + TAB + "id=\"");
		append(op.getInstanceName());
		append("\"");
		// Load of the core
		append(NLT + TAB + TAB + "load=\"");
		append(statGen.getLoad(op));
		append("\"");
		// Memory used of the core
		append(NLT + TAB + TAB + "used_mem=\"");
		append(statGen.getMem(op));
		append("\"");
		// ID for the plotter
		append(NLT + TAB + TAB + ">Core_");
		append(op.getInstanceName().replace(" ", "_"));
		append(".");
		// Ending core load stat
		append("</core>");
	}

	private void generateSchedulingStats(GanttData data) {
		// Print the scheduling stats for each core
		for (GanttComponent component : data.getComponents())
			for (GanttTask task : component.getTasks())
				generateTaskStats(task);
	}

	private void generateTaskStats(GanttTask task) {
		// Starting task
		append(NLT + "<event");
		// Start time
		append(NLT + TAB + "start=\"");
		append(task.getStartTime());
		append("\"");
		// End time
		append(NLT + TAB + "end=\"");
		append(task.getStartTime() + task.getDuration());
		append("\"");
		// Task name
		append(NLT + TAB + "title=\"");
		append(task.getId());
		append("\"");
		// Core
		append(NLT + TAB + "mapping=\"");
		append(task.getComponent().getId());
		append("\"");
		// Color
		append(NLT + TAB + "color=\"");
		append(TASKCOLOR);
		append("\"");
		// Gantt ID for the task
		append(NLT + TAB + ">Step_");
		append(task.getId().replace(" ", "_"));
		append(".");
		// Ending task
		append("</event>");
	}

}
