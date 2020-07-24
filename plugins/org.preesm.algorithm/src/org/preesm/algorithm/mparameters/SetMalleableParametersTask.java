/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
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
        + " Different strategies are possible "
        + "(exhaustive search or heuristics, available if parameter values are only of type Long).",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class),
        @Port(name = "architecture", type = Design.class) },

    outputs = { @Port(name = "PiMM", type = PiGraph.class) },

    parameters = { @org.preesm.commons.doc.annotations.Parameter(
        name = SetMalleableParametersTask.DEFAULT_HEURISTIC_NAME,
        values = { @Value(name = SetMalleableParametersTask.DEFAULT_HEURISTIC_VALUE,
            effect = "Enables to use a DSE heuristic when all malleable parameter expressions are integer numbers.") }),
        @org.preesm.commons.doc.annotations.Parameter(name = SetMalleableParametersTask.DEFAULT_DELAY_RETRY_NAME,
            values = { @Value(name = SetMalleableParametersTask.DEFAULT_DELAY_RETRY_VALUE,
                effect = "Enables to use a DSE heuristic to try to add delays if necessary.") }),
        @org.preesm.commons.doc.annotations.Parameter(name = SetMalleableParametersTask.DEFAULT_COMPARISONS_NAME,
            values = { @Value(name = SetMalleableParametersTask.DEFAULT_COMPARISONS_VALUE,
                effect = "Order of comparisons (T for throughput or P for power or L for latency or M for makespan, "
                    + "separated by >).") }),
        @org.preesm.commons.doc.annotations.Parameter(name = SetMalleableParametersTask.DEFAULT_THRESHOLDS_NAME,
            values = { @Value(name = SetMalleableParametersTask.DEFAULT_THRESHOLDS_VALUE,
                effect = "Taken into account if it is any integer higher than 0.") }) })
public class SetMalleableParametersTask extends AbstractTaskImplementation {

  public static final String DEFAULT_HEURISTIC_VALUE   = "false";
  public static final String DEFAULT_DELAY_RETRY_VALUE = "false";
  public static final String DEFAULT_COMPARISONS_VALUE = "T>P>L";
  public static final String DEFAULT_THRESHOLDS_VALUE  = "0>0>0";

  public static final String DEFAULT_HEURISTIC_NAME   = "Number heuristic";
  public static final String DEFAULT_DELAY_RETRY_NAME = "Retry with delays";
  public static final String DEFAULT_COMPARISONS_NAME = "Comparisons";
  public static final String DEFAULT_THRESHOLDS_NAME  = "Thresholds";

  public static final String COMPARISONS_REGEX = "[PLTM](>[PLTM])*";
  public static final String THRESHOLDS_REGEX  = "[0-9]+(.[0-9]+)?(>[0-9]+(.[0-9]+))*";

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

    final DSEpointGlobalComparator globalComparator = getGlobalComparater(parameters);
    final String delayRetryStr = parameters.get(DEFAULT_DELAY_RETRY_NAME);
    boolean delayRetryValue = Boolean.parseBoolean(delayRetryStr);

    PiGraph outputGraph; // different of input graph only if delays has been added by the heuristic
    if (heuristicValue) {
      outputGraph = numbersDSE(scenario, graph, architecture, mparamsIR, globalComparator, backupParamOverride,
          delayRetryValue);
    } else {
      outputGraph = exhaustiveDSE(scenario, graph, architecture, mparamsIR, globalComparator, backupParamOverride,
          delayRetryValue);
    }
    // erase previous value
    output.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, outputGraph);

    return output;
  }

  protected static PiGraph exhaustiveDSE(final Scenario scenario, final PiGraph graph, final Design architecture,
      List<MalleableParameterIR> mparamsIR, final DSEpointGlobalComparator globalComparator,
      final Map<Parameter, String> backupParamOverride, boolean delayRetryValue) {
    // build and test all possible configurations
    final ParameterCombinationExplorer pce = new ParameterCombinationExplorer(mparamsIR, scenario);
    DSEpointIR bestPoint = new DSEpointIR();
    List<Integer> bestConfig = null;
    int index = 0;
    while (pce.setNext()) {
      index++;

      final DSEpointIR dsep = runAndRetryConfiguration(scenario, graph, architecture, index, delayRetryValue,
          globalComparator);
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

    return logAndSetBestPoint(pce, bestPoint, bestConfig, globalComparator, graph, architecture, scenario);

  }

  protected static PiGraph numbersDSE(final Scenario scenario, final PiGraph graph, final Design architecture,
      List<MalleableParameterIR> mparamsIR, final DSEpointGlobalComparator globalComparator,
      final Map<Parameter, String> backupParamOverride, boolean delayRetryValue) {
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

        final DSEpointIR dsep = runAndRetryConfiguration(scenario, graph, architecture, indexTot, delayRetryValue,
            globalComparator);
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

    return logAndSetBestPoint(pce, bestPoint, bestConfig, globalComparator, graph, architecture, scenario);

  }

  protected static DSEpointIR runAndRetryConfiguration(final Scenario scenario, final PiGraph graph,
      final Design architecture, final int index, final boolean delayRetryValue,
      final DSEpointGlobalComparator globalComparator) {

    PreesmLogger.getLogger().fine("==> Testing combination: " + index);
    for (Parameter p : graph.getAllParameters()) {
      PreesmLogger.getLogger().fine(p.getName() + ": " + p.getExpression().getExpressionAsString());
    }

    // copy graph since flatten transfo has side effects (on parameters)
    final PiGraph graphCopy = PiMMUserFactory.instance.copyPiGraphWithHistory(graph);
    int iterationDelay = IterationDelayedEvaluator.computeLatency(graphCopy);

    final PeriodicScheduler scheduler = new PeriodicScheduler();
    DSEpointIR res = runConfiguration(scenario, graph, architecture, index, scheduler, iterationDelay);

    if (delayRetryValue && globalComparator.doesAcceptsMoreDelays()
        && globalComparator.areAllNonThroughputThresholdsMet(res)) {
      PreesmLogger.getLogger().fine("Retrying combination with delays.");

      // compute possible amount of delays
      final int nbCore = architecture.getOperatorComponents().get(0).getInstances().size();
      int maxCuts = globalComparator.getMaximumLatency();
      if (maxCuts > iterationDelay) {
        // we can add at least one cut
        maxCuts -= iterationDelay;
      } else {
        // we cannot add delays, so no retry
        return res;
      }

      long period = graph.getPeriod().evaluate();
      long durationII = period > 0 ? period : scheduler.getLastEndTime();
      final int nbCuts = globalComparator.computeCutsAmount(maxCuts, nbCore, durationII, scheduler.getTotalLoad(),
          scheduler.getMaximalLoad());
      final int nbPreCuts = nbCuts + 1;

      // add more delays
      // copy graph since flatten transfo has side effects (on parameters)
      final PiGraph graphCopy2 = PiMMUserFactory.instance.copyPiGraphWithHistory(graph);
      final PiGraph flatGraph = graphCopy2.getChildrenGraphs().isEmpty() ? graphCopy2
          : PiSDFFlattener.flatten(graphCopy2, true);
      final PiGraph flatGraphWithDelays = AutoDelaysTask.addDelays(flatGraph, architecture, scenario, false, false,
          false, nbCore, nbPreCuts, nbCuts);
      iterationDelay = IterationDelayedEvaluator.computeLatency(flatGraphWithDelays);

      // retry with more delays
      DSEpointIR resRetry = runConfiguration(scenario, flatGraphWithDelays, architecture, index, scheduler,
          iterationDelay);
      if ((res == null) || (resRetry != null && globalComparator.compare(resRetry, res) < 0)) {
        return new DSEpointIR(resRetry.power, resRetry.latency, resRetry.durationII, nbCuts, nbPreCuts);
      }
    }

    return res;
  }

  protected static DSEpointIR runConfiguration(final Scenario scenario, final PiGraph graph, final Design architecture,
      final int index, final IScheduler scheduler, final int iterationDelay) {
    final Level backupLevel = PreesmLogger.getLogger().getLevel();
    PreesmLogger.getLogger().setLevel(Level.SEVERE);
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
    return new DSEpointIR((double) energy / (double) latency, iterationDelay, latency);
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
   * @return Global comparator to compare all points of DSE.
   */
  public static DSEpointGlobalComparator getGlobalComparater(final Map<String, String> parameters) {
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
    return new DSEpointIR.DSEpointGlobalComparator(listComparators);
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(DEFAULT_HEURISTIC_NAME, DEFAULT_HEURISTIC_VALUE);
    parameters.put(DEFAULT_DELAY_RETRY_NAME, DEFAULT_DELAY_RETRY_VALUE);
    parameters.put(DEFAULT_COMPARISONS_NAME, DEFAULT_COMPARISONS_VALUE);
    parameters.put(DEFAULT_THRESHOLDS_NAME, DEFAULT_THRESHOLDS_VALUE);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Computing best values of malleable parameters.";
  }

}
