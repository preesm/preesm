/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.algorithm.mparameters.DSEpointIR.DSEpointGlobalComparator;
import org.preesm.algorithm.mparameters.DSEpointIR.DSEpointParetoComparator;
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
 * This class computes and set the best values of moldable parameters.
 *
 * @author ahonorat
 */
public class SetMoldableParameters {

  /**
   * Defines the minimal number of points to be in a cluster. If higher than 1, there might be no cluster at all with a
   * too short distance.
   */
  public static final int NB_POINTS_MIN_CLUSTERING = 2;

  protected final Scenario                  scenario;
  protected final PiGraph                   graph;
  protected final Design                    architecture;
  protected final List<MoldableParameterIR> mparamsIR;
  protected final DSEpointGlobalComparator  globalComparator;
  protected final DSEpointParetoComparator  paretoComparator;
  protected final boolean                   delayRetryValue;

  protected final Map<Parameter, String> backupParamOverride;

  protected StringBuilder                   logComparator;
  protected List<DSEpointIRclusteringProxy> paretoFrontAndDsecr;

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
   *          Moldable parameters IR to be used.
   * @param globalComparator
   *          Global comparator
   * @param delayRetryValue
   *          If cuts should be added.
   */
  public SetMoldableParameters(final Scenario scenario, final PiGraph graph, final Design architecture,
      final List<MoldableParameterIR> mparamsIR, final DSEpointGlobalComparator globalComparator,
      final boolean delayRetryValue) {

    this.scenario = scenario;
    this.graph = graph;
    this.architecture = architecture;
    this.mparamsIR = mparamsIR;
    this.globalComparator = globalComparator;
    this.paretoComparator = new DSEpointParetoComparator(globalComparator.comparators);
    this.delayRetryValue = delayRetryValue;

    // set the scenario graph since it is used for timings
    backupParamOverride = scenario.getParameterValues().entrySet().stream()
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

    logComparator = new StringBuilder();
    paretoFrontAndDsecr = new ArrayList<>();
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
   * Get the log of DSE points on the Pareto front of the metrics only.
   *
   * @return The log in a CSV format.
   */
  public StringBuilder getParetorFrontLog() {
    final StringBuilder sb = new StringBuilder();
    paretoFrontAndDsecr.forEach(x -> sb.append(x.descr));
    return sb;
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
    paretoFrontAndDsecr = new ArrayList<>();

    // build and test all possible configurations
    final ParameterCombinationExplorer pce = new ParameterCombinationExplorer(mparamsIR, scenario);
    DSEpointIR bestPoint = new DSEpointIR();
    List<Integer> bestConfig = null;
    int index = 0;
    while (pce.setNext()) {
      index++;

      final DSEpointIR dsep = runAndRetryConfiguration(confSched, index);
      PreesmLogger.getLogger().log(Level.FINE, dsep::toString);
      if (dsep.isSchedulable) {
        if (globalComparator.compare(dsep, bestPoint) < 0) {
          bestConfig = pce.recordConfiguration();
          bestPoint = dsep;
        }
        paretoFrontierUpdate(paretoFrontAndDsecr, dsep, paretoComparator);
      }
    }
    if (bestConfig == null) {
      resetAllMparams(mparamsIR);
      scenario.getParameterValues().putAll(backupParamOverride);
      PreesmLogger.getLogger().warning("No configuration was good, default moldable parameter values are put back.");
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
    paretoFrontAndDsecr = new ArrayList<>();

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
      PreesmLogger.getLogger().info("New DSE heuristic round: " + indexRound);

      bestLocalPoint = new DSEpointIR();
      bestLocalConfig = null;
      pce = new ParameterCombinationNumberExplorer(mparamsIR, scenario);
      while (pce.setNext()) {
        indexTot++;

        final DSEpointIR dsep = runAndRetryConfiguration(confSched, indexTot);
        PreesmLogger.getLogger().info(dsep::toString);
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
          paretoFrontierUpdate(paretoFrontAndDsecr, dsep, paretoComparator);
        }
      }
      if (bestConfig == null) {
        resetAllMparams(mparamsIR);
        scenario.getParameterValues().putAll(backupParamOverride);
        PreesmLogger.getLogger().warning("No configuration was good, default moldable parameter values are put back.");
        break;
      }
    } while (pce.setForNextPartialDSEround(bestLocalConfig));

    return logAndSetBestPoint(bestPceRound, bestPoint, bestConfig);
  }

  protected void paretoFrontierUpdate(final List<DSEpointIRclusteringProxy> listPareto, final DSEpointIR dsep,
      final DSEpointParetoComparator paretoComparator) {

    final Iterator<DSEpointIRclusteringProxy> itPareto = listPareto.iterator();
    DSEpointIR currentParetoPoint;
    while (itPareto.hasNext()) {
      currentParetoPoint = itPareto.next().dsep;
      switch (paretoComparator.compare(dsep, currentParetoPoint)) {
        case -1:
          // the new point is better than the current one, removing it
          itPareto.remove();
          break;
        case 0:
          // the new point is not comparable with others
          break;
        case 1:
          // bad point, we can return immediately
          return;
        default:
          break;
      }
    }
    // add the point
    final StringBuilder sb = new StringBuilder();
    logCsvContentMparams(sb, mparamsIR, dsep);
    listPareto.add(new DSEpointIRclusteringProxy(dsep, sb.toString()));
  }

  protected DSEpointIR runAndRetryConfiguration(final AbstractConfigurationScheduler confSched, final int index) {

    PreesmLogger.getLogger().fine(() -> "==> Testing combination: " + index);

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
      if (maxCuts <= iterationDelay) {
        // we cannot add delays, so no retry
        return res;
      }
      // ensure we can add at least one cut
      maxCuts -= iterationDelay;

      final Pair<Long, Long> maxLoads = confSched.getLastMaxLoads();
      final int nbCuts = globalComparator.computeCutsAmount(maxCuts, nbCore, confSched.getLastEndTime(),
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
   * Overrides moldalbe parameters values, also in scenario. Does nothing if bestConfig is null.
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
      PreesmLogger.getLogger().info(() -> "Best configuration has metrics: " + bestPoint);
      PreesmLogger.getLogger().warning("The moldable parameters value have been overriden in the scenario!");
      if (!globalComparator.areAllThresholdMet(bestPoint)) {
        PreesmLogger.getLogger().warning("Best configuration does not respect all thresholds.");
      }
      if (bestPoint.askedCuts != 0) {
        PreesmLogger.getLogger().warning(
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
      final List<MoldableParameterIR> mparamsIR, final DSEpointIR point) {
    for (final MoldableParameterIR mpir : mparamsIR) {
      logDSEpoints.append(mpir.mp.getExpression().evaluate() + ";");
    }
    logDSEpoints.append(point.toCsvContentString() + "\n");
  }

  protected static void resetAllMparams(List<MoldableParameterIR> mparamsIR) {
    // we need to consider exprs only since values may be in a different order (if sorted, or if overridden in
    // MoldableParameterNumberIR)
    for (final MoldableParameterIR mpir : mparamsIR) {
      if (!mpir.exprs.isEmpty()) {
        mpir.mp.setExpression(mpir.exprs.get(0));
      }
    }
  }

  protected class DSEpointIRclusteringProxy implements Clusterable {

    protected final DSEpointIR dsep;
    protected final String     descr;

    protected DSEpointIRclusteringProxy(final DSEpointIR dsep, final String descr) {
      this.dsep = dsep;
      this.descr = descr;
    }

    @Override
    public double[] getPoint() {
      return dsep.getPoint();
    }

  }

  public void clusterParetoFront(double dist) {
    final DBSCANClusterer<DSEpointIRclusteringProxy> clusterer = new DBSCANClusterer<>(dist, NB_POINTS_MIN_CLUSTERING);
    final List<Cluster<DSEpointIRclusteringProxy>> clusters = clusterer.cluster(paretoFrontAndDsecr);
    // TODO change method, asks nbClusters instead of distance? seems inefficient currently
    // TODO properly log the clusters
    PreesmLogger.getLogger().info(() -> "The clustering algorithm found: " + clusters.size() + " clusters.");
  }

}
