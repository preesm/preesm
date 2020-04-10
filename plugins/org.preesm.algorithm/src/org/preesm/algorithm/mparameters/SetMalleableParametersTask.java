package org.preesm.algorithm.mparameters;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.eclipse.core.runtime.IProgressMonitor;
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

    description = "Set the malleable parameters default value according to the best schedule found."
        + " Different strategies are possible "
        + "(exhaustive search or heuristics, available if values are only of type Long).",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class),
        @Port(name = "architecture", type = Design.class) },

    outputs = { @Port(name = "PiMM", type = PiGraph.class) },

    parameters = { @org.preesm.commons.doc.annotations.Parameter(
        name = SetMalleableParametersTask.DEFAULT_HEURISTIC_NAME,
        values = { @Value(name = SetMalleableParametersTask.DEFAULT_HEURISTIC_VALUE,
            effect = "Enables to use a DSE heuristic when all malleable parameter expressions are integer numbers.") }),
        @org.preesm.commons.doc.annotations.Parameter(name = SetMalleableParametersTask.DEFAULT_COMPARISONS_NAME,
            values = { @Value(name = SetMalleableParametersTask.DEFAULT_COMPARISONS_VALUE,
                effect = "Order of comparisons (T for throughput or E for energy or L for latency separated by >).") }),
        @org.preesm.commons.doc.annotations.Parameter(name = SetMalleableParametersTask.DEFAULT_THRESHOLD1_NAME,
            values = { @Value(name = SetMalleableParametersTask.DEFAULT_THRESHOLD1_VALUE,
                effect = "Taken into account if it is any integer higher than 0.") }),
        @org.preesm.commons.doc.annotations.Parameter(name = SetMalleableParametersTask.DEFAULT_THRESHOLD2_NAME,
            values = { @Value(name = SetMalleableParametersTask.DEFAULT_THRESHOLD2_VALUE,
                effect = "Taken into account if it is any integer higher than 0.") }),
        @org.preesm.commons.doc.annotations.Parameter(name = SetMalleableParametersTask.DEFAULT_THRESHOLD3_NAME,
            values = { @Value(name = SetMalleableParametersTask.DEFAULT_THRESHOLD3_VALUE,
                effect = "Taken into account if it is any integer higher than 0.") }) })
public class SetMalleableParametersTask extends AbstractTaskImplementation {

  public static final String DEFAULT_HEURISTIC_VALUE   = "false";
  public static final String DEFAULT_COMPARISONS_VALUE = "T>E>L";
  public static final String DEFAULT_THRESHOLD1_VALUE  = "0";
  public static final String DEFAULT_THRESHOLD2_VALUE  = "0";
  public static final String DEFAULT_THRESHOLD3_VALUE  = "0";

  public static final String DEFAULT_HEURISTIC_NAME   = "Number heuristic";
  public static final String DEFAULT_COMPARISONS_NAME = "Comparisons";
  public static final String DEFAULT_THRESHOLD1_NAME  = "Threshold 1";
  public static final String DEFAULT_THRESHOLD2_NAME  = "Threshold 2";
  public static final String DEFAULT_THRESHOLD3_NAME  = "Threshold 3";

  public static final String COMPARISONS_REGEX = "[ELT](>[ELT])*";

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
    final boolean allNumbers = !mparams.stream()
        .anyMatch(x -> !MalleableParameterExprChecker.isOnlyNumbers(x.getUserExpression()));
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
    PreesmLogger.getLogger().log(Level.INFO, "The number of parameters configuration is: " + nbCombinations);

    // set the scenario graph since it is used for timings
    final Map<Parameter, String> backupParamOverride = new HashMap<>();
    for (Entry<Parameter, String> e : scenario.getParameterValues().entrySet()) {
      backupParamOverride.put(e.getKey(), e.getValue());
    }

    final Comparator<DSEpointIR> globalComparator = getGlobalComparater(parameters);

    if (heuristicValue) {
      numbersDSE(scenario, graph, architecture, mparamsIR, globalComparator, backupParamOverride);
    } else {
      exhaustiveDSE(scenario, graph, architecture, mparamsIR, globalComparator, backupParamOverride);
    }

    return output;
  }

  protected void exhaustiveDSE(final Scenario scenario, final PiGraph graph, final Design architecture,
      List<MalleableParameterIR> mparamsIR, final Comparator<DSEpointIR> globalComparator,
      final Map<Parameter, String> backupParamOverride) {
    // build and test all possible configurations
    final ParameterCombinationExplorer pce = new ParameterCombinationExplorer(mparamsIR, scenario);
    DSEpointIR bestPoint = new DSEpointIR();
    List<Integer> bestConfig = null;
    int index = 0;
    while (pce.setNext()) {
      index++;

      final DSEpointIR dsep = runConfiguration(scenario, graph, architecture, index);
      if (dsep != null) {
        PreesmLogger.getLogger().log(Level.FINE, dsep.toString());
        if (bestConfig == null || globalComparator.compare(dsep, bestPoint) < 0) {
          bestConfig = pce.recordConfiguration();
          bestPoint = dsep;
        }
      }
    }
    if (bestConfig == null) {
      resetAllMparams(mparamsIR);
      scenario.getParameterValues().putAll(backupParamOverride);
    }
    logAndSetBestPoint(pce, bestPoint, bestConfig);

  }

  protected void numbersDSE(final Scenario scenario, final PiGraph graph, final Design architecture,
      List<MalleableParameterIR> mparamsIR, final Comparator<DSEpointIR> globalComparator,
      final Map<Parameter, String> backupParamOverride) {
    // build and test all possible configurations
    ParameterCombinationNumberExplorer pce = null;
    DSEpointIR bestPoint = new DSEpointIR();
    List<Integer> bestConfig = null;
    int indexTot = 0;
    int indexRound = 0;
    do {
      indexRound++;
      PreesmLogger.getLogger().log(Level.INFO, "New DSE heuristic round: " + indexRound);

      pce = new ParameterCombinationNumberExplorer(mparamsIR, scenario);
      while (pce.setNext()) {
        indexTot++;

        final DSEpointIR dsep = runConfiguration(scenario, graph, architecture, indexTot);
        if (dsep != null) {
          PreesmLogger.getLogger().log(Level.FINE, dsep.toString());
          if (bestConfig == null || globalComparator.compare(dsep, bestPoint) < 0) {
            bestConfig = pce.recordConfiguration();
            bestPoint = dsep;
          }
        }
      }
      if (bestConfig == null) {
        resetAllMparams(mparamsIR);
        scenario.getParameterValues().putAll(backupParamOverride);
        break;
      }
    } while (pce.setForNextPartialDSEround(bestConfig));

    logAndSetBestPoint(pce, bestPoint, bestConfig);

  }

  protected DSEpointIR runConfiguration(final Scenario scenario, final PiGraph graph, final Design architecture,
      final int index) {
    PreesmLogger.getLogger().fine("==> Testing combination: " + index);
    for (Parameter p : graph.getAllParameters()) {
      PreesmLogger.getLogger().fine(p.getName() + ": " + p.getExpression().getExpressionAsString());
    }
    final Level backupLevel = PreesmLogger.getLogger().getLevel();
    PreesmLogger.getLogger().setLevel(Level.SEVERE);
    final PiGraph dag = PiSDFToSingleRate.compute(graph, BRVMethod.LCM);
    // for (Parameter p : dag.getAllParameters()) {
    // PreesmLogger.getLogger().fine(p.getName() + " (in DAG): " + p.getExpression().getExpressionAsString());
    // }

    // copy graph since flatten transfo has side effects (on parameters)
    final PiGraph graphCopy = PiMMUserFactory.instance.copyPiGraphWithHistory(graph);
    int iterationDelay = IterationDelayedEvaluator.computeLatency(graphCopy);

    final IScheduler scheduler = new PeriodicScheduler();
    SynthesisResult scheduleAndMap = null;
    try {
      scheduleAndMap = scheduler.scheduleAndMap(dag, architecture, scenario);
    } catch (PreesmSchedulingException e) {
      // put back all messages
      PreesmLogger.getLogger().setLevel(backupLevel);
      PreesmLogger.getLogger().log(Level.WARNING, "Scheudling was impossible.", e);
      return null;
    }
    // use implementation evaluation of PeriodicScheduler instead?
    final ScheduleOrderManager scheduleOM = new ScheduleOrderManager(dag, scheduleAndMap.schedule);
    final LatencyCost evaluateLatency = new SimpleLatencyEvaluation().evaluate(dag, architecture, scenario,
        scheduleAndMap.mapping, scheduleOM);
    final long latency = evaluateLatency.getValue();
    final SimpleEnergyCost evaluateEnergy = new SimpleEnergyEvaluation().evaluate(dag, architecture, scenario,
        scheduleAndMap.mapping, scheduleOM);
    final long energy = evaluateEnergy.getValue();

    // put back all messages
    PreesmLogger.getLogger().setLevel(backupLevel);
    return new DSEpointIR(energy, iterationDelay, latency);
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
   */
  protected void logAndSetBestPoint(final ParameterCombinationExplorer pce, final DSEpointIR bestPoint,
      final List<Integer> bestConfig) {
    if (bestConfig != null) {
      pce.setConfiguration(bestConfig);
      PreesmLogger.getLogger().log(Level.INFO, "Best configuration has metrics: " + bestPoint);
      PreesmLogger.getLogger().log(Level.WARNING,
          "The malleable parameters value have been overriden in the scenario!");
    } else {
      PreesmLogger.getLogger().log(Level.WARNING, "No configuration found!");
    }
  }

  /**
   * Instantiate the global comparator, based on parameters.
   * 
   * @param parameters
   *          User parameters.
   * @return Global comparator to compare all points of DSE.
   */
  public static Comparator<DSEpointIR> getGlobalComparater(final Map<String, String> parameters) {
    final String comparisons = parameters.get(DEFAULT_COMPARISONS_NAME);
    if (!comparisons.matches(COMPARISONS_REGEX)) {
      throw new PreesmRuntimeException("Comparisons string is not correct. Accepted regex: " + COMPARISONS_REGEX);
    }
    final String[] tabComparisons = comparisons.split(">");
    final char[] charComparisons = new char[tabComparisons.length];
    for (int i = 0; i < tabComparisons.length; i++) {
      charComparisons[i] = tabComparisons[i].charAt(0);
    }

    long threshold1 = 0;
    long threshold2 = 0;
    long threshold3 = 0;
    try {
      threshold1 = Long.parseLong(parameters.get(DEFAULT_THRESHOLD1_NAME));
      threshold2 = Long.parseLong(parameters.get(DEFAULT_THRESHOLD2_NAME));
      threshold3 = Long.parseLong(parameters.get(DEFAULT_THRESHOLD3_NAME));
    } catch (NumberFormatException e) {
      throw new PreesmRuntimeException("Threshold must be a number.");
    }
    final long[] tabThreshold = { threshold1, threshold2, threshold3 };

    List<Comparator<DSEpointIR>> listComparators = new ArrayList<>();
    for (int i = 0; i < charComparisons.length; i++) {
      final long thresholdI = tabThreshold[i % 3];
      if (thresholdI == 0) {
        switch (charComparisons[i]) {
          case 'E':
            listComparators.add(new DSEpointIR.EnergyMinComparator());
            break;
          case 'L':
            listComparators.add(new DSEpointIR.LatencyMinComparator());
            break;
          case 'T':
            listComparators.add(new DSEpointIR.ThroughputMaxComparator());
            break;
          default:
            break;
        }
      } else if (thresholdI > 0) {
        switch (charComparisons[i]) {
          case 'E':
            listComparators.add(new DSEpointIR.EnergyAtMostComparator(thresholdI));
            break;
          case 'L':
            listComparators.add(new DSEpointIR.LatencyAtMostComparator((int) thresholdI));
            break;
          case 'T':
            listComparators.add(new DSEpointIR.ThroughputAtLeastComparator(thresholdI));
            break;
          default:
            break;
        }
      } else {
        throw new PreesmRuntimeException("Threshold " + (i % 3) + " has an incorrect negative value.");
      }

    }
    return new DSEpointIR.DSEpointGlobalComparator(listComparators);
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(DEFAULT_HEURISTIC_NAME, DEFAULT_HEURISTIC_VALUE);
    parameters.put(DEFAULT_COMPARISONS_NAME, DEFAULT_COMPARISONS_VALUE);
    parameters.put(DEFAULT_THRESHOLD1_NAME, DEFAULT_THRESHOLD1_VALUE);
    parameters.put(DEFAULT_THRESHOLD2_NAME, DEFAULT_THRESHOLD2_VALUE);
    parameters.put(DEFAULT_THRESHOLD3_NAME, DEFAULT_THRESHOLD3_VALUE);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Computing best values of malleable parameters.";
  }

}
