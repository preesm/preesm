/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
package org.preesm.algorithm.mapper.ui.stats;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.preesm.algorithm.mapper.stats.exporter.XMLStatsExporter;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.evaluation.latency.LatencyCost;
import org.preesm.algorithm.synthesis.evaluation.latency.SimpleLatencyEvaluation;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * Task to display and export Gantt from new synthesis results.
 * 
 * @author ahonorat
 */
@PreesmTask(id = "gantt-output", name = "Synthesis Gantt displayer and exporter", category = "Gantt exporters",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class),
        @Port(name = "architecture", type = Design.class), @Port(name = "Schedule", type = Schedule.class),
        @Port(name = "Mapping", type = Mapping.class), @Port(name = "Allocation", type = Allocation.class) },

    parameters = {
        @Parameter(name = StatsEditorSynthesisTask.DISPLAY_PARAM,
            description = "Specify if statistics, including Gantt diagram, must be displayed or not.",
            values = { @Value(name = "true/false") }),
        @Parameter(name = StatsEditorSynthesisTask.EXPORT_PARAM,
            description = "Folder to store Gantt diagram as xml file. Path is relative to the project, "
                + "put \"/\" if at root, may be empty.",
            values = { @Value(name = "/path/to") }) })
public class StatsEditorSynthesisTask extends AbstractTaskImplementation {

  public static final String DISPLAY_PARAM   = "display";
  public static final String DISPLAY_DEFAULT = "true";
  public static final String EXPORT_PARAM    = "file path";
  public static final String EXPORT_DEFAULT  = "";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    // Retrieve inputs
    final Scenario scenario = (Scenario) inputs.get("scenario");
    final Design architecture = (Design) inputs.get("architecture");
    final PiGraph algorithm = (PiGraph) inputs.get("PiMM");

    final Schedule schedule = (Schedule) inputs.get("Schedule");
    final Mapping mapping = (Mapping) inputs.get("Mapping");
    final Allocation memAlloc = (Allocation) inputs.get("Allocation");

    final boolean isDisplay = "true".equalsIgnoreCase(parameters.get(DISPLAY_PARAM));
    final String exportPath = parameters.get(EXPORT_PARAM).trim();
    final boolean isExport = !exportPath.isEmpty();

    if (isDisplay || isExport) {

      PreesmLogger.getLogger().log(Level.INFO, " -- Latency evaluation");
      final ScheduleOrderManager scheduleOM = new ScheduleOrderManager(algorithm, schedule);

      final LatencyCost evaluate = new SimpleLatencyEvaluation().evaluate(algorithm, architecture, scenario, mapping,
          scheduleOM);
      PreesmLogger.getLogger().log(Level.INFO, "Simple latency evaluation : " + evaluate.getValue());

      PreesmLogger.getLogger().log(Level.INFO, "-- Output of Gantt");

      final IStatGenerator statGen = new StatGeneratorSynthesis(architecture, scenario, mapping, memAlloc, evaluate);
      if (isDisplay) {
        final IEditorInput input = new StatEditorInput(statGen);

        // Check if the workflow is running in command line mode
        try {
          // Run statistic editor
          PlatformUI.getWorkbench().getDisplay().asyncExec(new EditorRunnable(input));
        } catch (final IllegalStateException e) {
          PreesmLogger.getLogger().log(Level.INFO, "Gantt display is impossible in this context."
              + " Ignore this log entry if you are running the command line version of Preesm.");
        }
      }

      if (isExport) {
        // Get the root of the workspace
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IWorkspaceRoot root = workspace.getRoot();
        // Get the project
        final String projectName = workflow.getProjectName();
        final IProject project = root.getProject(projectName);

        // Get a complete valid path with all folders existing
        String exportAbsolutePath = project.getLocation() + exportPath;
        final File parent = new File(exportAbsolutePath);
        parent.mkdirs();

        final String fileName = scenario.getScenarioName() + "_stats_pgantt.xml";
        final File file = new File(parent, fileName);
        XMLStatsExporter.exportXMLStats(file, statGen);

      }

    }

    // no output
    return new LinkedHashMap<>();

  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(DISPLAY_PARAM, DISPLAY_DEFAULT);
    parameters.put(EXPORT_PARAM, EXPORT_DEFAULT);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Gantt display and export";
  }

}
