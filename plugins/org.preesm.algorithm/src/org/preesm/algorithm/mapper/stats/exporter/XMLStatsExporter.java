/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
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
package org.preesm.algorithm.mapper.stats.exporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import org.preesm.algorithm.mapper.PreesmMapperException;
import org.preesm.algorithm.mapper.abc.impl.latency.LatencyAbc;
import org.preesm.algorithm.mapper.gantt.GanttComponent;
import org.preesm.algorithm.mapper.gantt.GanttData;
import org.preesm.algorithm.mapper.gantt.GanttTask;
import org.preesm.algorithm.mapper.ui.stats.StatGenerator;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.utils.DesignTools;

/**
 * This class exports stats from an IAbc (architecture benchmark computer) in XML format.
 *
 * @author cguy
 */
public class XMLStatsExporter {

  /** The Constant TASKCOLOR. */
  private static final String TASKCOLOR = "#c896fa";

  /** The Constant NL. */
  private static final String NL = "\n";

  /** The Constant TAB. */
  private static final String TAB = "\t";

  /** The Constant NLT. */
  private static final String NLT = "\n\t";

  /** The result. */
  private final StringBuilder result = new StringBuilder();

  /**
   * Append.
   *
   * @param s
   *          the s
   */
  private void append(final Object s) {
    this.result.append(s);
  }

  /**
   * Export generated stats from an IAbc to an xml file.
   *
   * @param abc
   *          the IAbc containing the scheduling of each task
   * @param file
   *          the file
   */
  public void exportXMLStats(final LatencyAbc abc, final File file) {
    // Generate the stats to write in an xml file
    final String content = generateXMLStats(abc);
    // Write the file
    try (FileWriter out = new FileWriter(file)) {
      out.write(content);
    } catch (final IOException e) {
      throw new PreesmMapperException("Could not export stats", e);
    }
  }

  /**
   * Generate a String at an XML format from an IAbc.
   *
   * @param abc
   *          the IAbc containing the scheduling of each task
   * @return a String containing the stats at an xml format
   */
  public String generateXMLStats(final LatencyAbc abc) {
    append(XMLStatsExporter.NL + "<data>");
    // Generate scheduling stats (when and on which core a given task is
    // executed)
    generateSchedulingStats(abc.getGanttData());
    // Generate performance stats (loads of the core; work, span and
    // implementation length; number of cores used over total number of
    // cores)
    generatePerformanceStats(abc);
    append(XMLStatsExporter.NL + "</data>");
    return this.result.toString();
  }

  /**
   * Generate performance stats.
   *
   * @param abc
   *          the abc
   */
  private void generatePerformanceStats(final LatencyAbc abc) {
    // Starting the performace stats
    final StatGenerator statGen = new StatGenerator(abc, abc.getScenario(), null);
    append(XMLStatsExporter.NLT + "<perfs>");
    // Work length
    append(XMLStatsExporter.NLT + XMLStatsExporter.TAB + "work=\"");
    try {
      append(statGen.getDAGWorkLength());
    } catch (final PreesmException e) {
      throw new PreesmMapperException("Could not generate perf stats.", e);
    }
    append("\"");
    // Span length
    append(XMLStatsExporter.NLT + XMLStatsExporter.TAB + "span=\"");
    append(statGen.getDAGSpanLength());
    append("\"");
    // Implementation length
    append(XMLStatsExporter.NLT + XMLStatsExporter.TAB + "impl_length=\"");
    append(statGen.getResultTime());
    append("\"");
    // Implementation number of cores
    append(XMLStatsExporter.NLT + "impl_nbCores=\"");
    append(statGen.getNbMainTypeOperators());
    append("\"");
    // Implementation number of used cores
    append(XMLStatsExporter.NLT + XMLStatsExporter.TAB + "impl_nbUsedCores=\"");
    append(statGen.getNbUsedOperators());
    append("\"");
    final Set<ComponentInstance> opSet = DesignTools.getOperatorInstances(abc.getArchitecture());
    for (final ComponentInstance op : opSet) {
      generateCoreLoad(op, statGen);
    }
    // Ending the performance stats
    append(XMLStatsExporter.NLT + "</perfs>");
  }

  /**
   * Generate core load.
   *
   * @param op
   *          the op
   * @param statGen
   *          the stat gen
   */
  private void generateCoreLoad(final ComponentInstance op, final StatGenerator statGen) {
    // Starting core load stat
    append(XMLStatsExporter.NLT + XMLStatsExporter.TAB + "<core");
    // Id of the core
    append(XMLStatsExporter.NLT + XMLStatsExporter.TAB + XMLStatsExporter.TAB + "id=\"");
    append(op.getInstanceName());
    append("\"");
    // Load of the core
    append(XMLStatsExporter.NLT + XMLStatsExporter.TAB + XMLStatsExporter.TAB + "load=\"");
    append(statGen.getLoad(op));
    append("\"");
    // Memory used of the core
    append(XMLStatsExporter.NLT + XMLStatsExporter.TAB + XMLStatsExporter.TAB + "used_mem=\"");
    append(statGen.getMem(op));
    append("\"");
    // ID for the plotter
    append(XMLStatsExporter.NLT + XMLStatsExporter.TAB + XMLStatsExporter.TAB + ">Core_");
    append(op.getInstanceName().replace(" ", "_"));
    append(".");
    // Ending core load stat
    append("</core>");
  }

  /**
   * Generate scheduling stats.
   *
   * @param data
   *          the data
   */
  private void generateSchedulingStats(final GanttData data) {
    // Print the scheduling stats for each core
    for (final GanttComponent component : data.getComponents()) {
      for (final GanttTask task : component.getTasks()) {
        generateTaskStats(task);
      }
    }
  }

  /**
   * Generate task stats.
   *
   * @param task
   *          the task
   */
  private void generateTaskStats(final GanttTask task) {
    // Starting task
    append(XMLStatsExporter.NLT + "<event");
    // Start time
    append(XMLStatsExporter.NLT + XMLStatsExporter.TAB + "start=\"");
    append(task.getStartTime());
    append("\"");
    // End time
    append(XMLStatsExporter.NLT + XMLStatsExporter.TAB + "end=\"");
    append(task.getStartTime() + task.getDuration());
    append("\"");
    // Task name
    append(XMLStatsExporter.NLT + XMLStatsExporter.TAB + "title=\"");
    append(task.getId());
    append("\"");
    // Core
    append(XMLStatsExporter.NLT + XMLStatsExporter.TAB + "mapping=\"");
    append(task.getComponent().getId());
    append("\"");
    // Color
    append(XMLStatsExporter.NLT + XMLStatsExporter.TAB + "color=\"");
    append(XMLStatsExporter.TASKCOLOR);
    append("\"");
    // Gantt ID for the task
    append(XMLStatsExporter.NLT + XMLStatsExporter.TAB + ">Step_");
    append(task.getId().replace(" ", "_"));
    append(".");
    // Ending task
    append("</event>");
  }

}
