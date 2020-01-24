package org.preesm.algorithm.mparameters;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.synthesis.SynthesisResult;
import org.preesm.algorithm.synthesis.evaluation.latency.LatencyCost;
import org.preesm.algorithm.synthesis.evaluation.latency.SimpleLatencyEvaluation;
import org.preesm.algorithm.synthesis.schedule.ScheduleOrderManager;
import org.preesm.algorithm.synthesis.schedule.algos.IScheduler;
import org.preesm.algorithm.synthesis.schedule.algos.PeriodicScheduler;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
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

    outputs = { @Port(name = "PiMM", type = PiGraph.class) })
public class SetMalleableParametersTask extends AbstractTaskImplementation {

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
    ParameterCombinationExplorer pce = new ParameterCombinationExplorer(mparamsIR, scenario);
    // set the scenario graph since it is used for timings
    Map<Parameter, String> backupParamOverride = new HashMap<>();
    for (Entry<Parameter, String> e : scenario.getParameterValues().entrySet()) {
      backupParamOverride.put(e.getKey(), e.getValue());
    }
    List<Integer> bestConfig = null;
    long bestLatency = Long.MAX_VALUE;
    int index = 0;
    while (pce.setNext()) {
      index++;
      // copy graph since SRDAG transfo has side effects (on parameters and delays)
      final PiGraph graphCopy = PiMMUserFactory.instance.copyPiGraphWithHistory(graph);
      final PiGraph dag = PiSDFToSingleRate.compute(graphCopy, BRVMethod.LCM);
      System.err.println("==> Testing combination: " + index);
      for (Parameter p : graphCopy.getAllParameters()) {
        System.err.println(p.getName() + ": " + p.getExpression().getExpressionAsString());
      }
      for (Parameter p : dag.getAllParameters()) {
        System.err.println(p.getName() + " (in DAG): " + p.getExpression().getExpressionAsString());
      }
      final IScheduler scheduler = new PeriodicScheduler();
      final SynthesisResult scheduleAndMap = scheduler.scheduleAndMap(dag, architecture, scenario);
      // use implementation evaluation of PeriodicScheduler instead?
      final ScheduleOrderManager scheduleOM = new ScheduleOrderManager(dag, scheduleAndMap.schedule);
      final LatencyCost evaluate = new SimpleLatencyEvaluation().evaluate(dag, architecture, scenario,
          scheduleAndMap.mapping, scheduleOM);
      final long latency = evaluate.getValue();
      if (bestConfig == null || bestLatency > latency) {
        bestConfig = pce.recordConfiguration();
        bestLatency = latency;
      } else {
        scenario.getParameterValues().putAll(backupParamOverride);
      }
    }

    if (bestConfig != null) {
      pce.setConfiguration(bestConfig);
      PreesmLogger.getLogger().log(Level.INFO, "Best configuration ensures latency of: " + bestLatency);
      PreesmLogger.getLogger().log(Level.WARNING,
          "The malleable parameters value have been overriden in the scenario!");
    } else {
      PreesmLogger.getLogger().log(Level.WARNING, "No configuration found!");
    }

    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Computing best values of malleable parameters.";
  }

}
