/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.mparameters.DSEpointIR.DSEpointGlobalComparator;
import org.preesm.algorithm.pisdf.autodelays.AutoDelaysTask;
import org.preesm.algorithm.pisdf.autodelays.IterationDelayedEvaluator;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.evaluation.energy.SimpleEnergyCost;
import org.preesm.algorithm.synthesis.evaluation.energy.SimpleEnergyEvaluation;
import org.preesm.algorithm.synthesis.evaluation.latency.LatencyCost;
import org.preesm.algorithm.synthesis.evaluation.latency.SimpleLatencyEvaluation;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.algorithm.synthesis.schedule.algos.IScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.PeriodicScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.PreesmSchedulingException;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.MalleableParameter;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.check.MalleableParameterExprChecker;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.statictools.PiMMHelper;
import org.preesm.model.pisdf.statictools.PiSDFFlattener;
import org.preesm.model.pisdf.statictools.PiSDFToSingleRate;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * This task computes and set the best values of malleable parameters.
 * 
 * @author ahonorat
 */
@PreesmTask(id = "pisdf-mparams.setter", name = "Malleable Parameters setter",
    shortDescription = "Set the malleable parameters default value according to the best schedule found.",

    description = "Set the malleable parameters default value in the scenario according to the best schedule found."
        + "Works only on homogeneous architectures. "
        + "Different strategies are possible, exhaustive search or heuristics.",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class),
        @Port(name = "architecture", type = Design.class) },

    outputs = { @Port(name = "PiMM", type = PiGraph.class) },

    parameters = {
        @org.preesm.commons.doc.annotations.Parameter(name = SetMalleableParametersTask.DEFAULT_COMPARISONS_NAME,
            description = "Order of comparisons of the metrics (T for throughput or P for power or E for energy "
                + "or L for latency or M for makespan, separated by >). Latency is indexed from 1 to "
                + "the maximum number of pipeline stages allowed.",
            values = { @Value(name = SetMalleableParametersTask.DEFAULT_COMPARISONS_VALUE,
                effect = "Metrics are compare from left to right.") }),
        @org.preesm.commons.doc.annotations.Parameter(name = SetMalleableParametersTask.DEFAULT_THRESHOLDS_NAME,
            description = "Objectives of the metrics. "
                + "Threshold if it is any integer higher than 0, minimize it otherwise.",
            values = { @Value(name = SetMalleableParametersTask.DEFAULT_THRESHOLDS_VALUE,
                effect = "In the same order as the metrics.") }),
        @org.preesm.commons.doc.annotations.Parameter(name = SetMalleableParametersTask.DEFAULT_PARAMS_OBJVS_NAME,
            description = "Tells to minimize (-) or maximize (+) a parameter (after main objectives). May be empty.",
            values = { @Value(name = SetMalleableParametersTask.DEFAULT_PARAMS_OBJVS_VALUE,
                effect = "Syntax: >+parentGraphName/parameterName>-...") }),
        @org.preesm.commons.doc.annotations.Parameter(name = SetMalleableParametersTask.DEFAULT_HEURISTIC_NAME,
            description = "Use a DSE heuristic on all malleable parameter expressions which are integer numbers. "
                + "Only a subset of their expressions are explored.",
            values = { @Value(name = SetMalleableParametersTask.DEFAULT_HEURISTIC_VALUE,
                effect = "False disables the heuristic.") }),
        @org.preesm.commons.doc.annotations.Parameter(name = SetMalleableParametersTask.DEFAULT_DELAY_RETRY_NAME,
            description = "Use a DSE heuristic to try to add delays if it improves the throughput. "
                + "See workflow task pisdf-delays.setter. Number of pipelines is inferred automatically.",
            values = { @Value(name = SetMalleableParametersTask.DEFAULT_DELAY_RETRY_VALUE,
                effect = "False disables the heuristic.") }),
        @org.preesm.commons.doc.annotations.Parameter(name = SetMalleableParametersTask.DEFAULT_LOG_NAME,
            description = "Export all explored points with associated metrics in a csv file.",
            values = { @Value(name = SetMalleableParametersTask.DEFAULT_LOG_VALUE,
                effect = "Path relative to the project root.") }) })
public class SetMalleableParametersTask extends AbstractTaskImplementation {

  public static final String DEFAULT_COMPARISONS_VALUE  = "T>P>L";
  public static final String DEFAULT_THRESHOLDS_VALUE   = "0>0>0";
  public static final String DEFAULT_PARAMS_OBJVS_VALUE = ">";
  public static final String DEFAULT_HEURISTIC_VALUE    = "false";
  public static final String DEFAULT_DELAY_RETRY_VALUE  = "false";
  public static final String DEFAULT_LOG_VALUE          = "/Code/generated/";

  public static final String DEFAULT_COMPARISONS_NAME  = "1. Comparisons";
  public static final String DEFAULT_THRESHOLDS_NAME   = "2. Thresholds";
  public static final String DEFAULT_PARAMS_OBJVS_NAME = "3. Params objectives";
  public static final String DEFAULT_HEURISTIC_NAME    = "4. Number heuristic";
  public static final String DEFAULT_DELAY_RETRY_NAME  = "5. Retry with delays";
  public static final String DEFAULT_LOG_NAME          = "6. Log path";

  public static final String COMPARISONS_REGEX = "[EPLTM](>[EPLTM])*";
  public static final String THRESHOLDS_REGEX  = "[0-9]+(.[0-9]+)?(>[0-9]+(.[0-9]+))*";
  public static final String PARAMS_REGEX      = ">|(>[+-][a-zA-Z0-9_]+/[a-zA-Z0-9_]+)*";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final PiGraph graph = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);

    final Map<String, Object> output = new LinkedHashMap<>();
    output.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, graph);

    List<MalleableParameter> mparams = graph.getAllParameters().stream().filter(x -> x instanceof MalleableParameter)
        .map(x -> (MalleableParameter) x).collect(Collectors.toList());

    if (mparams.isEmpty()) {
      return output;
    }

    if (mparams.stream().anyMatch(x -> !x.isLocallyStatic())) {
      throw new PreesmRuntimeException(
          "One or more malleable parameter is not locally static, this is not allowed in this task.");
    }

    List<MalleableParameterIR> mparamsIR = null;
    final boolean allNumbers = mparams.stream()
        .allMatch(x -> MalleableParameterExprChecker.isOnlyNumbers(x.getUserExpression()));
    if (allNumbers) {
      PreesmLogger.getLogger().log(Level.INFO,
          "All malleable parameters are numbers, allowing non exhaustive heuristics.");
    }
    final String heuristicStr = parameters.get(DEFAULT_HEURISTIC_NAME);
    boolean heuristicValue = Boolean.parseBoolean(heuristicStr);
    if (heuristicValue) {
      mparamsIR = mparams.stream().map(x -> {
        if (MalleableParameterExprChecker.isOnlyNumbers(x.getUserExpression())) {
          return new MalleableParameterNumberIR(x);
        } else {
          return new MalleableParameterIR(x);
        }
      }).collect(Collectors.toList());
    } else {
      mparamsIR = mparams.stream().map(x -> new MalleableParameterIR(x)).collect(Collectors.toList());
    }

    long nbCombinations = 1;
    for (MalleableParameterIR mpir : mparamsIR) {
      nbCombinations *= mpir.nbValues;
    }
    PreesmLogger.getLogger().log(Level.INFO, "The number of parameter combinations is: " + nbCombinations);

    // set the scenario graph since it is used for timings
    final Map<Parameter, String> backupParamOverride = new HashMap<>();
    for (Entry<Parameter, String> e : scenario.getParameterValues().entrySet()) {
      backupParamOverride.put(e.getKey(), e.getValue());
    }

    final DSEpointGlobalComparator globalComparator = getGlobalComparator(parameters, graph);
    final String delayRetryStr = parameters.get(DEFAULT_DELAY_RETRY_NAME);
    boolean delayRetryValue = Boolean.parseBoolean(delayRetryStr);

    PiGraph outputGraph; // different of input graph only if delays has been added by the heuristic
    StringBuilder logDSEpoints = new StringBuilder();

    if (heuristicValue) {
      outputGraph = numbersDSE(scenario, graph, architecture, mparamsIR, globalComparator, backupParamOverride,
          delayRetryValue, logDSEpoints);
    } else {
      outputGraph = exhaustiveDSE(scenario, graph, architecture, mparamsIR, globalComparator, backupParamOverride,
          delayRetryValue, logDSEpoints);
    }
    // erase previous value
    output.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, outputGraph);
    // exports log
    final String logPath = parameters.get(DEFAULT_LOG_NAME);
    logCsvFile(logDSEpoints, mparamsIR, workflow, scenario, logPath);

    return output;
  }

  protected void logCsvFile(final StringBuilder logDSEpoints, final List<MalleableParameterIR> mparamsIR,
      final Workflow workflow, final Scenario scenario, final String logPath) {
    final StringBuilder header = new StringBuilder();
    for (MalleableParameterIR mpir : mparamsIR) {
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
    String exportAbsolutePath = project.getLocation() + logPath;
    final File parent = new File(exportAbsolutePath);
    parent.mkdirs();

    final String fileName = scenario.getScenarioName() + "_DSE_log.csv";
    final File file = new File(parent, fileName);
    try (final FileWriter fw = new FileWriter(file, true)) {
      fw.write(header.toString());
      fw.write(logDSEpoints.toString());
    } catch (IOException e) {
      PreesmLogger.getLogger().log(Level.SEVERE,
          "Unhable to write the DSE task log in file:" + exportAbsolutePath + fileName);
    }

  }

  protected static PiGraph exhaustiveDSE(final Scenario scenario, final PiGraph graph, final Design architecture,
      final List<MalleableParameterIR> mparamsIR, final DSEpointGlobalComparator globalComparator,
      final Map<Parameter, String> backupParamOverride, final boolean delayRetryValue,
      final StringBuilder logDSEpoints) {
    // build and test all possible configurations
    final ParameterCombinationExplorer pce = new ParameterCombinationExplorer(mparamsIR, scenario);
    DSEpointIR bestPoint = new DSEpointIR();
    List<Integer> bestConfig = null;
    int index = 0;
    while (pce.setNext()) {
      index++;

      final DSEpointIR dsep = runAndRetryConfiguration(scenario, graph, architecture, index, delayRetryValue,
          globalComparator, logDSEpoints, mparamsIR);
      PreesmLogger.getLogger().log(Level.FINE, dsep.toString());
      if (dsep.isSchedulable && globalComparator.compare(dsep, bestPoint) < 0) {
        bestConfig = pce.recordConfiguration();
        bestPoint = dsep;
      }
    }
    if (bestConfig == null) {
      resetAllMparams(mparamsIR);
      scenario.getParameterValues().putAll(backupParamOverride);
      PreesmLogger.getLogger().warning("No configuration was good, default malleable parameter values are put back.");
    }

    return logAndSetBestPoint(pce, bestPoint, bestConfig, globalComparator, graph, architecture, scenario);

  }

  protected static PiGraph numbersDSE(final Scenario scenario, final PiGraph graph, final Design architecture,
      final List<MalleableParameterIR> mparamsIR, final DSEpointGlobalComparator globalComparator,
      final Map<Parameter, String> backupParamOverride, final boolean delayRetryValue,
      final StringBuilder logDSEpoints) {
    // build and test all possible configurations
    ParameterCombinationNumberExplorer pce = null;
    DSEpointIR bestPoint = new DSEpointIR();
    List<Integer> bestConfig = null;
    ParameterCombinationNumberExplorer bestPceRound = null;
    DSEpointIR bestLocalPoint;
    List<Integer> bestLocalConfig;
    int indexTot = 0;
    int indexRound = 0;
    do {
      indexRound++;
      PreesmLogger.getLogger().log(Level.INFO, "New DSE heuristic round: " + indexRound);

      bestLocalPoint = new DSEpointIR();
      bestLocalConfig = null;
      pce = new ParameterCombinationNumberExplorer(mparamsIR, scenario);
      while (pce.setNext()) {
        indexTot++;

        final DSEpointIR dsep = runAndRetryConfiguration(scenario, graph, architecture, indexTot, delayRetryValue,
            globalComparator, logDSEpoints, mparamsIR);
        PreesmLogger.getLogger().log(Level.FINE, dsep.toString());
        if (dsep.isSchedulable) {
          if (globalComparator.compare(dsep, bestPoint) < 0) {
            bestConfig = pce.recordConfiguration();
            bestPoint = dsep;
            bestPceRound = pce;
          }
          if (globalComparator.compare(dsep, bestLocalPoint) < 0) {
            bestLocalConfig = pce.recordConfiguration();
            bestLocalPoint = dsep;
          }
        }
      }
      if (bestConfig == null) {
        resetAllMparams(mparamsIR);
        scenario.getParameterValues().putAll(backupParamOverride);
        PreesmLogger.getLogger().warning("No configuration was good, default malleable parameter values are put back.");
        break;
      }
    } while (pce.setForNextPartialDSEround(bestLocalConfig));

    return logAndSetBestPoint(bestPceRound, bestPoint, bestConfig, globalComparator, graph, architecture, scenario);

  }

  protected static void logCsvContentMparams(final StringBuilder logDSEpoints,
      final List<MalleableParameterIR> mparamsIR, final DSEpointIR point) {
    for (MalleableParameterIR mpir : mparamsIR) {
      logDSEpoints.append(mpir.mp.getExpression().evaluate() + ";");
    }
    logDSEpoints.append(point.toCsvContentString() + "\n");
  }

  protected static DSEpointIR runAndRetryConfiguration(final Scenario scenario, final PiGraph graph,
      final Design architecture, final int index, final boolean delayRetryValue,
      final DSEpointGlobalComparator globalComparator, final StringBuilder logDSEpoints,
      final List<MalleableParameterIR> mparamsIR) {

    PreesmLogger.getLogger().fine("==> Testing combination: " + index);
    // for (Parameter p : graph.getAllParameters()) {
    // PreesmLogger.getLogger().fine(p.getVertexPath() + ": " + p.getExpression().getExpressionAsString());
    // }

    final PeriodicScheduler scheduler = new PeriodicScheduler();
    DSEpointIR res = runConfiguration(scenario, graph, architecture, scheduler, globalComparator);
    logCsvContentMparams(logDSEpoints, mparamsIR, res);

    if (delayRetryValue && globalComparator.doesAcceptsMoreDelays()
        && globalComparator.areAllNonThroughputAndEnergyThresholdsMet(res)) {
      PreesmLogger.getLogger().fine("Retrying combination with delays.");

      // compute possible amount of delays
      final int nbCore = architecture.getOperatorComponents().get(0).getInstances().size();
      final int iterationDelay = res.latency; // is greater or equal to 1
      int maxCuts = globalComparator.getMaximumLatency(); // so -1 is performed in following test
      if (maxCuts > iterationDelay) {
        // ensure we can add at least one cut
        maxCuts -= iterationDelay;
      } else {
        // we cannot add delays, so no retry
        return res;
      }

      long period = scheduler.getGraphPeriod();
      // original graph period has not been resolved, so we use the flat graph copy instead
      long durationII = period > 0 ? period : scheduler.getLastEndTime();
      final int nbCuts = globalComparator.computeCutsAmount(maxCuts, nbCore, durationII, scheduler.getTotalLoad(),
          scheduler.getMaximalLoad());
      if (nbCuts == 0) {
        // may happen with makespan threshold
        return res;
      }
      final int nbPreCuts = nbCuts + 1;

      // deactivate fine logging for automatic pipelining
      final Level backupLevel = PreesmLogger.getLogger().getLevel();
      PreesmLogger.getLogger().setLevel(Level.SEVERE);
      // copy and flatten transfo graph
      final PiGraph flatGraphCopy = PiSDFFlattener.flatten(graph, true);
      // add more delays
      final PiGraph flatGraphWithDelays = AutoDelaysTask.addDelays(flatGraphCopy, architecture, scenario, false, false,
          false, nbCore, nbPreCuts, nbCuts);
      // reactivate logging
      PreesmLogger.getLogger().setLevel(backupLevel);

      // retry with more delays
      DSEpointIR resRetry = runConfiguration(scenario, flatGraphWithDelays, architecture, scheduler, null);
      // adds cut information and params (from the unflat version since flattning change param names) to the point
      resRetry = new DSEpointIR(resRetry.energy, resRetry.latency, resRetry.durationII, nbCuts, nbPreCuts,
          res.paramsValues, resRetry.isSchedulable);
      logCsvContentMparams(logDSEpoints, mparamsIR, resRetry);

      if (globalComparator.compare(resRetry, res) < 0) {
        return resRetry;
      }

    }

    return res;
  }

  protected static DSEpointIR runConfiguration(final Scenario scenario, final PiGraph graph, final Design architecture,
      final IScheduler scheduler, final DSEpointGlobalComparator globalComparator) {
    final Level backupLevel = PreesmLogger.getLogger().getLevel();
    PreesmLogger.getLogger().setLevel(Level.SEVERE);

    // copy graph since flatten transfo has side effects (on parameters)
    final int iterationDelay = IterationDelayedEvaluator.computeLatency(graph);

    Map<Pair<String, String>, Long> paramsValues;
    if (globalComparator != null) {
      final PiGraph graphResolvedCopy = PiMMUserFactory.instance.copyPiGraphWithHistory(graph);
      PiMMHelper.resolveAllParameters(graphResolvedCopy);
      paramsValues = globalComparator.getParamsValues(graphResolvedCopy);
    } else {
      // case of flatten graph with extra delays, so parameter names are not same,
      // the real values will be updated by the calling method
      paramsValues = new HashMap<>();
    }

    final PiGraph dag = PiSDFToSingleRate.compute(graph, BRVMethod.LCM);
    // for (Parameter p : dag.getAllParameters()) {
    // PreesmLogger.getLogger().fine(p.getName() + " (in DAG): " + p.getExpression().getExpressionAsString());
    // }

    SynthesisResult scheduleAndMap = null;
    try {
      scheduleAndMap = scheduler.scheduleAndMap(dag, architecture, scenario);
    } catch (PreesmSchedulingException e) {
      // put back all messages
      PreesmLogger.getLogger().setLevel(backupLevel);
      PreesmLogger.getLogger().log(Level.WARNING, "Scheduling was impossible.", e);
      return new DSEpointIR(Long.MAX_VALUE, iterationDelay, Long.MAX_VALUE, 0, 0, paramsValues, false);
    }

    // use implementation evaluation of PeriodicScheduler instead?
    final ScheduleOrderManager scheduleOM = new ScheduleOrderManager(dag, scheduleAndMap.schedule);
    final LatencyCost evaluateLatency = new SimpleLatencyEvaluation().evaluate(dag, architecture, scenario,
        scheduleAndMap.mapping, scheduleOM);
    final long durationII = evaluateLatency.getValue();
    final SimpleEnergyCost evaluateEnergy = new SimpleEnergyEvaluation().evaluate(dag, architecture, scenario,
        scheduleAndMap.mapping, scheduleOM);
    final long energy = evaluateEnergy.getValue();

    // put back all messages
    PreesmLogger.getLogger().setLevel(backupLevel);
    return new DSEpointIR(energy, iterationDelay, durationII, 0, 0, paramsValues, true);
  }

  protected static void resetAllMparams(List<MalleableParameterIR> mparamsIR) {
    // we need to consider exprs only since values may be in a different order (if sorted, or if overriden in
    // MalleableParameterNumberIR)
    for (MalleableParameterIR mpir : mparamsIR) {
      if (!mpir.exprs.isEmpty()) {
        mpir.mp.setExpression(mpir.exprs.get(0));
      }
    }
  }

  /**
   * Overrides malleable parameters values, also in scenario. Does nothing if bestConfig is null.
   * 
   * @param pce
   *          used to set the bestConfig.
   * @param bestPoint
   *          Information about bestPoints.
   * @param bestConfig
   *          according to pce.
   * @param globalComparator
   *          comparator used to compute the best point
   * @param scenario
   *          Scenario to consider (only if adding delays).
   * @param architecture
   *          Architecture to consider (only if adding delays).
   * @param graph
   *          Original graph (only if adding delays).
   * @return Original graph, or a flat copy if delays have been added.
   */
  protected static PiGraph logAndSetBestPoint(final ParameterCombinationExplorer pce, final DSEpointIR bestPoint,
      final List<Integer> bestConfig, final DSEpointGlobalComparator globalComparator, final PiGraph graph,
      final Design architecture, final Scenario scenario) {
    if (bestConfig != null) {
      pce.setConfiguration(bestConfig);
      PreesmLogger.getLogger().log(Level.INFO, "Best configuration has metrics: " + bestPoint);
      PreesmLogger.getLogger().log(Level.WARNING,
          "The malleable parameters value have been overriden in the scenario!");
      if (!globalComparator.areAllThresholdMet(bestPoint)) {
        PreesmLogger.getLogger().log(Level.WARNING, "Best configuration does not respect all thresholds.");
      }
      if (bestPoint.askedCuts != 0) {
        PreesmLogger.getLogger().log(Level.WARNING,
            "Delays have been added to the graph (implies graph flattening and parameter expression resolution "
                + "in output graph)!");

        final int nbCore = architecture.getOperatorComponents().get(0).getInstances().size();
        final PiGraph graphCopy = PiMMUserFactory.instance.copyPiGraphWithHistory(graph);
        final PiGraph flatGraph = graphCopy.getChildrenGraphs().isEmpty() ? graphCopy
            : PiSDFFlattener.flatten(graphCopy, true);
        return AutoDelaysTask.addDelays(flatGraph, architecture, scenario, false, false, false, nbCore,
            bestPoint.askedPreCuts, bestPoint.askedCuts);
      }

    } else {
      PreesmLogger.getLogger().log(Level.WARNING, "No configuration found!");
    }
    return graph;
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
        } catch (NumberFormatException e) {
          throw new PreesmRuntimeException("Threshold n°" + i + " must be an integer number.");
        }
      } else {
        try {
          numberThresholds[i] = Double.parseDouble(tabThresholds[i]);
        } catch (NumberFormatException e) {
          throw new PreesmRuntimeException("Threshold n°" + i + " must be a float number.");
        }
      }
    }

    List<Comparator<DSEpointIR>> listComparators = new ArrayList<>();
    for (int i = 0; i < charComparisons.length; i++) {
      final Number thresholdI = numberThresholds[i];
      if (thresholdI.doubleValue() == 0.0D) {
        switch (charComparisons[i]) {
          case 'E':
            listComparators.add(new DSEpointIR.EnergyMinComparator());
            break;
          case 'P':
            listComparators.add(new DSEpointIR.PowerMinComparator());
            break;
          case 'L':
            listComparators.add(new DSEpointIR.LatencyMinComparator());
            break;
          case 'M':
            listComparators.add(new DSEpointIR.MakespanMinComparator());
            break;
          case 'T':
            listComparators.add(new DSEpointIR.ThroughputMaxComparator());
            break;
          default:
            break;
        }
      } else if (thresholdI.doubleValue() > 0.0D) {
        switch (charComparisons[i]) {
          case 'E':
            listComparators.add(new DSEpointIR.EnergyAtMostComparator(thresholdI.longValue()));
            break;
          case 'P':
            listComparators.add(new DSEpointIR.PowerAtMostComparator(thresholdI.doubleValue()));
            break;
          case 'L':
            listComparators.add(new DSEpointIR.LatencyAtMostComparator(thresholdI.intValue()));
            break;
          case 'M':
            listComparators.add(new DSEpointIR.MakespanAtMostComparator(thresholdI.longValue()));
            break;
          case 'T':
            listComparators.add(new DSEpointIR.ThroughputAtLeastComparator(thresholdI.longValue()));
            break;
          default:
            break;
        }
      } else {
        throw new PreesmRuntimeException("Threshold n°" + i + " has an incorrect negative value.");
      }

    }
    final String params = parameters.get(DEFAULT_PARAMS_OBJVS_NAME);
    if (!params.matches(PARAMS_REGEX)) {
      throw new PreesmRuntimeException("Parameters string is not correct. Accepted regex: " + PARAMS_REGEX);
    }
    LinkedHashMap<Pair<String, String>, Character> paramsObjvs = new LinkedHashMap<>();
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
      Parameter param = graph.lookupParameterGivenGraph(parentAndParamNames[1], parent);
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
    parameters.put(DEFAULT_LOG_NAME, DEFAULT_LOG_VALUE);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Computing best values of malleable parameters.";
  }

}
