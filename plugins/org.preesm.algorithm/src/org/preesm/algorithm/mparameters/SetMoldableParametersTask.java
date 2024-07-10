/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2020 - 2021)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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
package org.preesm.algorithm.mparameters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.mparameters.DSEpointIR.DSEpointGlobalComparator;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.MoldableParameter;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.check.MoldableParameterExprChecker;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * This task computes and set the best values of moldable parameters.
 *
 * @author ahonorat
 */
@PreesmTask(id = "pisdf-mparams.setter", name = "Moldable Parameters setter",
    shortDescription = "Set the moldable parameters default value according to the best schedule found.",

    description = "Set the moldable parameters default value in the scenario according to the best schedule found."
        + "Works only on homogeneous architectures. "
        + "Different strategies are possible, exhaustive search or heuristics.",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class),
        @Port(name = "architecture", type = Design.class) },

    outputs = { @Port(name = "PiMM", type = PiGraph.class) },

    parameters = {
        @org.preesm.commons.doc.annotations.Parameter(name = SetMoldableParametersTask.DEFAULT_COMPARISONS_NAME,
            description = "Order of comparisons of the metrics (T for throughput or P for power or E for energy "
                + "or L for latency or M for makespan, separated by >). Latency is indexed from 1 to "
                + "the maximum number of pipeline stages allowed.",
            values = { @Value(name = SetMoldableParametersTask.DEFAULT_COMPARISONS_VALUE,
                effect = "Metrics are compare from left to right.") }),
        @org.preesm.commons.doc.annotations.Parameter(name = SetMoldableParametersTask.DEFAULT_THRESHOLDS_NAME,
            description = "Objectives of the metrics. "
                + "Threshold if it is any integer higher than 0, minimize it otherwise.",
            values = { @Value(name = SetMoldableParametersTask.DEFAULT_THRESHOLDS_VALUE,
                effect = "In the same order as the metrics.") }),
        @org.preesm.commons.doc.annotations.Parameter(name = SetMoldableParametersTask.DEFAULT_PARAMS_OBJVS_NAME,
            description = "Tells to minimize (-) or maximize (+) a parameter (after main objectives). May be empty.",
            values = { @Value(name = SetMoldableParametersTask.DEFAULT_PARAMS_OBJVS_VALUE,
                effect = "Syntax: >+parentGraphName/parameterName>-...") }),
        @org.preesm.commons.doc.annotations.Parameter(name = SetMoldableParametersTask.DEFAULT_HEURISTIC_NAME,
            description = "Use a DSE heuristic on all moldable parameter expressions which are integer numbers. "
                + "Only a subset of their expressions are explored.",
            values = { @Value(name = SetMoldableParametersTask.DEFAULT_HEURISTIC_VALUE,
                effect = "False disables the heuristic.") }),
        @org.preesm.commons.doc.annotations.Parameter(name = SetMoldableParametersTask.DEFAULT_DELAY_RETRY_NAME,
            description = "Use a DSE heuristic to try to add delays if it improves the throughput. "
                + "See workflow task pisdf-delays.setter. Number of pipelines is inferred automatically.",
            values = { @Value(name = SetMoldableParametersTask.DEFAULT_DELAY_RETRY_VALUE,
                effect = "False disables the heuristic.") }),
        @org.preesm.commons.doc.annotations.Parameter(name = SetMoldableParametersTask.DEFAULT_SCHEDULER_NAME,
            description = "Set the scheduler used to estimate each configuration point.",
            values = {
                @Value(name = SetMoldableParametersTask.SCHEDULER_PARAM_VALUE_FPGA,
                    effect = "Single FPGA average scheduler."),
                @Value(name = SetMoldableParametersTask.SCHEDULER_PARAM_VALUE_LIST,
                    effect = "Homogeneous periodic list scheduler.") }),
        @org.preesm.commons.doc.annotations.Parameter(name = SetMoldableParametersTask.DEFAULT_CLUSTER_DISTANCE_NAME,
            description = "Set the clustering (positive) distance to be used"
                + "for DSE points on the metrics Pareto front.",
            values = { @Value(name = SetMoldableParametersTask.DEFAULT_CLUSTER_DISTANCE_VALUE,
                effect = "Disables clustering if zero.") }),
        @org.preesm.commons.doc.annotations.Parameter(name = SetMoldableParametersTask.DEFAULT_LOG_NAME,
            description = "Export all explored points with associated metrics in a csv file.",
            values = { @Value(name = SetMoldableParametersTask.DEFAULT_LOG_VALUE,
                effect = "Path relative to the project root.") }) })
public class SetMoldableParametersTask extends AbstractTaskImplementation {

  public static final String SCHEDULER_PARAM_VALUE_LIST = "homogeneousListPeriodic";
  public static final String SCHEDULER_PARAM_VALUE_FPGA = "singleFPGAavegarge";

  public static final String DEFAULT_COMPARISONS_VALUE      = "T>P>L";
  public static final String DEFAULT_THRESHOLDS_VALUE       = "0>0>0";
  public static final String DEFAULT_PARAMS_OBJVS_VALUE     = ">";
  public static final String DEFAULT_HEURISTIC_VALUE        = "false";
  public static final String DEFAULT_DELAY_RETRY_VALUE      = "false";
  public static final String DEFAULT_SCHEDULER_VALUE        = SCHEDULER_PARAM_VALUE_LIST;
  public static final String DEFAULT_CLUSTER_DISTANCE_VALUE = "0.0";
  public static final String DEFAULT_LOG_VALUE              = "/Code/generated/";

  public static final String DEFAULT_COMPARISONS_NAME      = "1. Comparisons";
  public static final String DEFAULT_THRESHOLDS_NAME       = "2. Thresholds";
  public static final String DEFAULT_PARAMS_OBJVS_NAME     = "3. Params objectives";
  public static final String DEFAULT_HEURISTIC_NAME        = "4. Number heuristic";
  public static final String DEFAULT_DELAY_RETRY_NAME      = "5. Retry with delays";
  public static final String DEFAULT_SCHEDULER_NAME        = "6. Scheduler";
  public static final String DEFAULT_CLUSTER_DISTANCE_NAME = "7. Clustering distance";
  public static final String DEFAULT_LOG_NAME              = "8. Log path";

  public static final String COMPARISONS_REGEX = "[EPLTMS](>[EPLTMS])*";
  public static final String THRESHOLDS_REGEX  = "\\d+(.\\d+)?(>\\d+(.\\d+))*";
  public static final String PARAMS_REGEX      = ">|(>[+-]\\w+/\\w+)*";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final PiGraph graph = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);

    final Map<String, Object> output = new LinkedHashMap<>();
    output.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, graph);

    final List<MoldableParameter> mparams = graph.getAllParameters().stream()
        .filter(MoldableParameter.class::isInstance).map(x -> (MoldableParameter) x).collect(Collectors.toList());

    if (mparams.isEmpty()) {
      return output;
    }

    if (mparams.stream().anyMatch(x -> !x.isLocallyStatic())) {
      throw new PreesmRuntimeException(
          "One or more moldable parameter is not locally static, this is not allowed in this task.");
    }

    List<MoldableParameterIR> mparamsIR = null;
    final boolean allNumbers = mparams.stream()
        .allMatch(x -> MoldableParameterExprChecker.isOnlyNumbers(x.getUserExpression()));
    if (allNumbers) {
      PreesmLogger.getLogger().info("All moldable parameters are numbers, allowing non exhaustive heuristics.");
    }
    final String heuristicStr = parameters.get(DEFAULT_HEURISTIC_NAME);
    final boolean heuristicValue = Boolean.parseBoolean(heuristicStr);
    if (heuristicValue) {
      mparamsIR = mparams.stream().map(x -> {
        if (MoldableParameterExprChecker.isOnlyNumbers(x.getUserExpression())) {
          return new MoldableParameterNumberIR(x);
        }
        return new MoldableParameterIR(x);
      }).collect(Collectors.toList());
    } else {
      mparamsIR = mparams.stream().map(MoldableParameterIR::new).collect(Collectors.toList());
    }

    final long nbCombinations = mparamsIR.stream().map(mpir -> mpir.nbValues).reduce(1, (prod, e) -> prod * e);

    PreesmLogger.getLogger().info(() -> "The number of parameter combinations is: " + nbCombinations);

    final String delayRetryStr = parameters.get(DEFAULT_DELAY_RETRY_NAME);
    boolean delayRetryValue = Boolean.parseBoolean(delayRetryStr);
    final String comparisons = parameters.get(DEFAULT_COMPARISONS_NAME);
    final boolean shouldEstimateMemory = comparisons.contains("S");
    final DSEpointGlobalComparator globalComparator = getGlobalComparator(parameters, graph);

    final String schedulerName = parameters.get(DEFAULT_SCHEDULER_NAME).toLowerCase();
    AbstractConfigurationScheduler acs;
    if (SCHEDULER_PARAM_VALUE_LIST.equalsIgnoreCase(schedulerName)) {
      acs = new ConfigurationSchedulerPeriodic(shouldEstimateMemory);
    } else if (SCHEDULER_PARAM_VALUE_FPGA.equalsIgnoreCase(schedulerName)) {
      // for FPGA, the memory will be evaluated in any case
      acs = new ConfigurationSchedulerFPGA();
    } else {
      throw new PreesmRuntimeException("Unknown scheduler.");
    }

    if (shouldEstimateMemory && !acs.supportsMemoryEstimation()) {
      PreesmLogger.getLogger().warning("Your DSE comparison objectives asks for memory estimation "
          + "but the scheduler you asked does not support it, ignoring it.");
    }

    if (delayRetryValue && !acs.supportsExtraDelayCuts()) {
      delayRetryValue = false;
      PreesmLogger.getLogger()
          .warning("You ask for extra delays/cuts but the scheduler you asked does not support it, ignoring it.");
    }

    PiGraph outputGraph; // different of input graph only if delays has been added by the heuristic
    final SetMoldableParameters smp = new SetMoldableParameters(scenario, graph, architecture, mparamsIR,
        globalComparator, delayRetryValue);

    String suffix = "";
    if (heuristicValue) {
      outputGraph = smp.numbersDSE(acs);
      suffix = "numberh";
    } else {
      outputGraph = smp.exhaustiveDSE(acs);
      suffix = "exhaustive";
    }
    // erase previous value
    output.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, outputGraph);
    // clustering of Pareto front
    final String clusteringDistanceStr = parameters.get(DEFAULT_CLUSTER_DISTANCE_NAME);
    try {
      final double dist = Double.parseDouble(clusteringDistanceStr);
      if (dist > 0.0d) {
        smp.clusterParetoFront(dist);
      }
    } catch (final NumberFormatException e) {
      PreesmLogger.getLogger().warning(
          () -> "The clustering distance could not be parsed to a number, ignoring this step. " + e.getMessage());
    }

    // exports log
    final String logPath = parameters.get(DEFAULT_LOG_NAME);
    logCsvFile(smp.getComparatorLog(), mparamsIR, workflow, scenario, logPath, suffix);
    logCsvFile(smp.getParetorFrontLog(), mparamsIR, workflow, scenario, logPath, "pareto");

    return output;
  }

  protected void logCsvFile(final StringBuilder logDSEpoints, final List<MoldableParameterIR> mparamsIR,
      final Workflow workflow, final Scenario scenario, final String logPath, final String suffix) {
    final StringBuilder header = new StringBuilder();
    for (final MoldableParameterIR mpir : mparamsIR) {
      header.append(mpir.mp.getName() + ";");
    }
    header.append(DSEpointIR.CSV_HEADER_STRING + "\n");

    // Get the root of the workspace
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IWorkspaceRoot root = workspace.getRoot();
    // Get the project
    final String projectName = workflow.getProjectName();
    final IProject project = root.getProject(projectName);

    // Get a complete valid path with all folders existing
    final String exportAbsolutePath = project.getLocation() + logPath;
    final File parent = new File(exportAbsolutePath);
    parent.mkdirs();

    final String fileName = scenario.getScenarioName() + "_" + suffix + "DSE_log.csv";
    final File file = new File(parent, fileName);
    try (final FileWriter fw = new FileWriter(file, true)) {
      fw.write(header.toString());
      fw.write(logDSEpoints.toString());
    } catch (final IOException e) {
      PreesmLogger.getLogger()
          .severe(() -> "Unhable to write the DSE task log in file:" + exportAbsolutePath + fileName);
    }

  }

  /**
   * Instantiate the global comparator, based on parameters.
   *
   * @param parameters
   *          User parameters.
   * @param graph
   *          PiGraph containing parameters.
   * @return Global comparator to compare all points of DSE.
   */
  public static DSEpointGlobalComparator getGlobalComparator(final Map<String, String> parameters,
      final PiGraph graph) {
    final String comparisons = parameters.get(DEFAULT_COMPARISONS_NAME);
    if (!comparisons.matches(COMPARISONS_REGEX)) {
      throw new PreesmRuntimeException("Comparisons string is not correct. Accepted regex: " + COMPARISONS_REGEX);
    }
    final String[] tabComparisons = comparisons.split(">");
    final char[] charComparisons = new char[tabComparisons.length];
    for (int i = 0; i < tabComparisons.length; i++) {
      charComparisons[i] = tabComparisons[i].charAt(0);
    }

    final String thresholds = parameters.get(DEFAULT_THRESHOLDS_NAME);
    if (!thresholds.matches(THRESHOLDS_REGEX)) {
      throw new PreesmRuntimeException("Thresholds string is not correct. Accepted regex: " + THRESHOLDS_REGEX);
    }
    final String[] tabThresholds = thresholds.split(">");
    if (tabThresholds.length != tabComparisons.length) {
      throw new PreesmRuntimeException("The number of thresolds must be the same as the number of comparators.");
    }
    final Number[] numberThresholds = new Number[tabThresholds.length];
    for (int i = 0; i < tabThresholds.length; i++) {
      if (charComparisons[i] != 'P') {
        try {
          numberThresholds[i] = Long.parseLong(tabThresholds[i]);
        } catch (final NumberFormatException e) {
          throw new PreesmRuntimeException("Threshold n°" + i + " must be an integer number.");
        }
      } else {
        try {
          numberThresholds[i] = Double.parseDouble(tabThresholds[i]);
        } catch (final NumberFormatException e) {
          throw new PreesmRuntimeException("Threshold n°" + i + " must be a float number.");
        }
      }
    }

    final List<Comparator<DSEpointIR>> listComparators = new ArrayList<>();
    for (int i = 0; i < charComparisons.length; i++) {
      final Number thresholdI = numberThresholds[i];
      if (thresholdI.doubleValue() == 0.0D) {
        switch (charComparisons[i]) {
          case 'E' -> listComparators.add(new DSEpointIR.EnergyMinComparator());
          case 'P' -> listComparators.add(new DSEpointIR.PowerMinComparator());
          case 'L' -> listComparators.add(new DSEpointIR.LatencyMinComparator());
          case 'M' -> listComparators.add(new DSEpointIR.MakespanMinComparator());
          case 'T' -> listComparators.add(new DSEpointIR.ThroughputMaxComparator());
          case 'S' -> listComparators.add(new DSEpointIR.MemoryMinComparator());
          default -> {
            // empty
          }
        }
      } else if (thresholdI.doubleValue() > 0.0D) {
        switch (charComparisons[i]) {
          case 'E' -> listComparators.add(new DSEpointIR.EnergyAtMostComparator(thresholdI.longValue()));
          case 'P' -> listComparators.add(new DSEpointIR.PowerAtMostComparator(thresholdI.doubleValue()));
          case 'L' -> listComparators.add(new DSEpointIR.LatencyAtMostComparator(thresholdI.intValue()));
          case 'M' -> listComparators.add(new DSEpointIR.MakespanAtMostComparator(thresholdI.longValue()));
          case 'T' -> listComparators.add(new DSEpointIR.ThroughputAtLeastComparator(thresholdI.longValue()));
          case 'S' -> listComparators.add(new DSEpointIR.MemoryAtMostComparator(thresholdI.longValue()));
          default -> {
            // empty
          }
        }

      } else {
        throw new PreesmRuntimeException("Threshold n°" + i + " has an incorrect negative value.");
      }

    }
    final String params = parameters.get(DEFAULT_PARAMS_OBJVS_NAME);
    if (!params.matches(PARAMS_REGEX)) {
      throw new PreesmRuntimeException("Parameters string is not correct. Accepted regex: " + PARAMS_REGEX);
    }
    final LinkedHashMap<Pair<String, String>, Character> paramsObjvs = new LinkedHashMap<>();
    final String[] tabParams = params.split(">");
    for (final String tabParam : tabParams) {
      if (tabParam.isEmpty()) {
        // first occurence of ">" in the string
        continue;
      }
      final String[] parentAndParamNames = tabParam.split("/");
      if (parentAndParamNames.length != 2) {
        throw new PreesmRuntimeException(
            "Parameters string is not correct. It should be: [+-]<ParentPiGraphName>/<ParameterName>");
      }
      final char minOrMax = parentAndParamNames[0].charAt(0);
      final String parent = parentAndParamNames[0].substring(1);
      final Parameter param = graph.lookupParameterGivenGraph(parentAndParamNames[1], parent);
      if (param == null) {
        PreesmLogger.getLogger().log(Level.WARNING, "Parameter: " + tabParam + " has not been found, ignored.");
      } else {
        paramsObjvs.put(new Pair<>(parent, parentAndParamNames[1]), minOrMax);
      }
    }

    return new DSEpointIR.DSEpointGlobalComparator(listComparators, paramsObjvs);
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(DEFAULT_COMPARISONS_NAME, DEFAULT_COMPARISONS_VALUE);
    parameters.put(DEFAULT_THRESHOLDS_NAME, DEFAULT_THRESHOLDS_VALUE);
    parameters.put(DEFAULT_PARAMS_OBJVS_NAME, DEFAULT_PARAMS_OBJVS_VALUE);
    parameters.put(DEFAULT_HEURISTIC_NAME, DEFAULT_HEURISTIC_VALUE);
    parameters.put(DEFAULT_DELAY_RETRY_NAME, DEFAULT_DELAY_RETRY_VALUE);
    parameters.put(DEFAULT_SCHEDULER_NAME, DEFAULT_SCHEDULER_VALUE);
    parameters.put(DEFAULT_CLUSTER_DISTANCE_NAME, DEFAULT_CLUSTER_DISTANCE_VALUE);
    parameters.put(DEFAULT_LOG_NAME, DEFAULT_LOG_VALUE);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Computing best values of moldable parameters.";
  }

}
