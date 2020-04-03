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

    parameters = {
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

  public static final String DEFAULT_COMPARISONS_VALUE = "T>E>L";
  public static final String DEFAULT_THRESHOLD1_VALUE  = "0";
  public static final String DEFAULT_THRESHOLD2_VALUE  = "0";
  public static final String DEFAULT_THRESHOLD3_VALUE  = "0";

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

    final boolean allNumbers = !mparams.stream()
        .anyMatch(x -> !MalleableParameterExprChecker.isOnlyNumbers(x.getUserExpression()));
    if (allNumbers) {
      PreesmLogger.getLogger().log(Level.INFO,
          "All malleable parameters are numbers, allowing non exhaustive heuristics.");
    }

    List<MalleableParameterIR> mparamsIR = mparams.stream().map(x -> new MalleableParameterIR(x))
        .collect(Collectors.toList());
    long nbCombinations = 1;
    for (MalleableParameterIR mpir : mparamsIR) {
      nbCombinations *= mpir.nbValues;
    }
    PreesmLogger.getLogger().log(Level.INFO, "The number of parameters configuration is: " + nbCombinations);

    // build and test all possible configurations
    final ParameterCombinationExplorer pce = new ParameterCombinationExplorer(mparamsIR, scenario);
    // set the scenario graph since it is used for timings
    Map<Parameter, String> backupParamOverride = new HashMap<>();
    for (Entry<Parameter, String> e : scenario.getParameterValues().entrySet()) {
      backupParamOverride.put(e.getKey(), e.getValue());
    }

    Comparator<DSEpointIR> globalComparator = getGlobalComparater(parameters);
    DSEpointIR bestPoint = new DSEpointIR();
    List<Integer> bestConfig = null;
    int index = 0;
    while (pce.setNext()) {
      index++;

      final PiGraph dag = PiSDFToSingleRate.compute(graph, BRVMethod.LCM);
      PreesmLogger.getLogger().fine("==> Testing combination: " + index);
      for (Parameter p : graph.getAllParameters()) {
        PreesmLogger.getLogger().fine(p.getName() + ": " + p.getExpression().getExpressionAsString());
      }
      for (Parameter p : dag.getAllParameters()) {
        PreesmLogger.getLogger().fine(p.getName() + " (in DAG): " + p.getExpression().getExpressionAsString());
      }
      // copy graph since flatten transfo has side effects (on parameters)
      final PiGraph graphCopy = PiMMUserFactory.instance.copyPiGraphWithHistory(graph);
      int iterationDelay = IterationDelayedEvaluator.computeLatency(graphCopy);
      PreesmLogger.getLogger().log(Level.INFO, "Latency in number of iteration: " + iterationDelay);

      final IScheduler scheduler = new PeriodicScheduler();
      final SynthesisResult scheduleAndMap = scheduler.scheduleAndMap(dag, architecture, scenario);
      // use implementation evaluation of PeriodicScheduler instead?
      final ScheduleOrderManager scheduleOM = new ScheduleOrderManager(dag, scheduleAndMap.schedule);
      final LatencyCost evaluateLatency = new SimpleLatencyEvaluation().evaluate(dag, architecture, scenario,
          scheduleAndMap.mapping, scheduleOM);
      final long latency = evaluateLatency.getValue();
      final SimpleEnergyCost evaluateEnergy = new SimpleEnergyEvaluation().evaluate(dag, architecture, scenario,
          scheduleAndMap.mapping, scheduleOM);
      final long energy = evaluateEnergy.getValue();

      DSEpointIR dsep = new DSEpointIR(energy, iterationDelay, latency);

      if (bestConfig == null || globalComparator.compare(dsep, bestPoint) < 0) {
        bestConfig = pce.recordConfiguration();
        bestPoint = dsep;
      } else {
        scenario.getParameterValues().putAll(backupParamOverride);
      }
    }

    if (bestConfig != null) {
      pce.setConfiguration(bestConfig);
      PreesmLogger.getLogger().log(Level.INFO, "Best configuration has metrics: " + bestPoint);
      PreesmLogger.getLogger().log(Level.WARNING,
          "The malleable parameters value have been overriden in the scenario!");
    } else {
      PreesmLogger.getLogger().log(Level.WARNING, "No configuration found!");
    }

    return output;
  }

  public Comparator<DSEpointIR> getGlobalComparater(final Map<String, String> parameters) {
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
