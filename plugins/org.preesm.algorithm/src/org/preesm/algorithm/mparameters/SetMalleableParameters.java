package org.preesm.algorithm.mparameters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.mparameters.DSEpointIR.DSEpointGlobalComparator;
import org.preesm.algorithm.pisdf.autodelays.AutoDelaysTask;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.statictools.PiMMHelper;
import org.preesm.model.pisdf.statictools.PiSDFFlattener;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * This class computes and set the best values of malleable parameters.
 * 
 * @author ahonorat
 */
public class SetMalleableParameters {

  protected final Scenario                   scenario;
  protected final PiGraph                    graph;
  protected final Design                     architecture;
  protected final List<MalleableParameterIR> mparamsIR;
  protected final DSEpointGlobalComparator   globalComparator;
  protected final boolean                    shouldEstimateMemory;
  protected final boolean                    delayRetryValue;

  protected final Map<Parameter, String> backupParamOverride;

  protected StringBuilder logComparator;

  /**
   * Set the right attributes for the DSE.
   * 
   * @param scenario
   *          Scenario to be used.
   * @param graph
   *          Graph to be used.
   * @param architecture
   *          Architecture to be used.
   * @param mparamsIR
   *          Malleable parameters IR to be used.
   * @param globalComparator
   *          Global comparator
   * @param shouldEstimateMemory
   *          If memory should be estimated.
   * @param delayRetryValue
   *          If cuts should be added.
   */
  public SetMalleableParameters(final Scenario scenario, final PiGraph graph, final Design architecture,
      final List<MalleableParameterIR> mparamsIR, final DSEpointGlobalComparator globalComparator,
      final boolean shouldEstimateMemory, final boolean delayRetryValue) {

    this.scenario = scenario;
    this.graph = graph;
    this.architecture = architecture;
    this.mparamsIR = mparamsIR;
    this.globalComparator = globalComparator;
    this.shouldEstimateMemory = shouldEstimateMemory;
    this.delayRetryValue = delayRetryValue;

    // set the scenario graph since it is used for timings
    backupParamOverride = new HashMap<>();
    for (Entry<Parameter, String> e : scenario.getParameterValues().entrySet()) {
      backupParamOverride.put(e.getKey(), e.getValue());
    }

    logComparator = new StringBuilder();
  }

  /**
   * Get the log of DSE points explored for the comparator log.
   * 
   * @return The log in a CSV format.
   */
  public StringBuilder getComparatorLog() {
    return logComparator;
  }

  /**
   * Run the exhaustive DSE.
   * 
   * @param confSched
   *          The configuration scheduler to be used.
   * @return The PiGraph set with values of best parameter.
   */
  public PiGraph exhaustiveDSE(final AbstractConfigurationScheduler confSched) {
    logComparator = new StringBuilder();

    // build and test all possible configurations
    final ParameterCombinationExplorer pce = new ParameterCombinationExplorer(mparamsIR, scenario);
    DSEpointIR bestPoint = new DSEpointIR();
    List<Integer> bestConfig = null;
    int index = 0;
    while (pce.setNext()) {
      index++;

      final DSEpointIR dsep = runAndRetryConfiguration(confSched, index);
      PreesmLogger.getLogger().log(Level.FINE, () -> dsep.toString());
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

    return logAndSetBestPoint(pce, bestPoint, bestConfig);

  }

  /**
   * Run the DSE with number heuristics.
   * 
   * @param confSched
   *          The configuration scheduler to be used.
   * @return The PiGraph set with values of best parameter.
   */
  public PiGraph numbersDSE(final AbstractConfigurationScheduler confSched) {
    logComparator = new StringBuilder();

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

        final DSEpointIR dsep = runAndRetryConfiguration(confSched, indexTot);
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

    return logAndSetBestPoint(bestPceRound, bestPoint, bestConfig);

  }

  protected DSEpointIR runAndRetryConfiguration(final AbstractConfigurationScheduler confSched, final int index) {

    PreesmLogger.getLogger().fine("==> Testing combination: " + index);
    // for (Parameter p : graph.getAllParameters()) {
    // PreesmLogger.getLogger().fine(p.getVertexPath() + ": " + p.getExpression().getExpressionAsString());
    // }

    final PiGraph graphResolvedCopy = PiMMUserFactory.instance.copyPiGraphWithHistory(graph);
    PiMMHelper.resolveAllParameters(graphResolvedCopy);
    final Map<Pair<String, String>, Long> paramsValues = globalComparator.getParamsValues(graphResolvedCopy);
    DSEpointIR res = confSched.runConfiguration(scenario, graph, architecture);
    res = new DSEpointIR(res.energy, res.latency, res.durationII, res.memory, 0, 0, paramsValues, res.isSchedulable);
    logCsvContentMparams(logComparator, mparamsIR, res);

    // retry with extra delays if allowed
    if (delayRetryValue && confSched.supportsExtraDelayCuts() && globalComparator.doesAcceptsMoreDelays()
        && globalComparator.areAllNonThroughputAndEnergyThresholdsMet(res)) {
      PreesmLogger.getLogger().fine("Retrying combination with delays.");

      // compute possible amount of delays
      final int nbCore = architecture.getProcessingElements().get(0).getInstances().size();
      final int iterationDelay = res.latency; // is greater or equal to 1
      int maxCuts = globalComparator.getMaximumLatency(); // so -1 is performed in following test
      if (maxCuts > iterationDelay) {
        // ensure we can add at least one cut
        maxCuts -= iterationDelay;
      } else {
        // we cannot add delays, so no retry
        return res;
      }

      final Pair<Long, Long> maxLoads = confSched.getLastMaxLoads();
      final int nbCuts = globalComparator.computeCutsAmount(maxCuts, nbCore, confSched.getLastMakespan(),
          maxLoads.getValue(), maxLoads.getKey());
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
      // case of flatten graph with extra delays, so parameter names are not same,
      // the real values will be updated by the calling method
      DSEpointIR resRetry = confSched.runConfiguration(scenario, flatGraphWithDelays, architecture);
      // adds cut information and params (from the unflat version since flattning change param names) to the point
      resRetry = new DSEpointIR(resRetry.energy, resRetry.latency, resRetry.durationII, resRetry.memory, nbCuts,
          nbPreCuts, res.paramsValues, resRetry.isSchedulable);
      logCsvContentMparams(logComparator, mparamsIR, resRetry);

      if (globalComparator.compare(resRetry, res) < 0) {
        return resRetry;
      }

    }

    return res;
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
   * @return Original graph, or a flat copy if delays have been added.
   */
  protected PiGraph logAndSetBestPoint(final ParameterCombinationExplorer pce, final DSEpointIR bestPoint,
      final List<Integer> bestConfig) {
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

        final int nbCore = architecture.getProcessingElements().get(0).getInstances().size();
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

  protected static void logCsvContentMparams(final StringBuilder logDSEpoints,
      final List<MalleableParameterIR> mparamsIR, final DSEpointIR point) {
    for (MalleableParameterIR mpir : mparamsIR) {
      logDSEpoints.append(mpir.mp.getExpression().evaluate() + ";");
    }
    logDSEpoints.append(point.toCsvContentString() + "\n");
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

}
