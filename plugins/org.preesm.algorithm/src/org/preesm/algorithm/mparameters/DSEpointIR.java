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
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.xtext.xbase.lib.Pair;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;

/**
 * This class stores main metrics of a DSE point. (Design Space Exploration)
 * 
 * This class is intended to be used only through its provided comparators.
 * 
 * @author ahonorat
 */
public class DSEpointIR {

  // metrics for comparators:
  public final long energy;     // energy, power = energy / durationII
  public final int  latency;    // as factor of durationII
  public final long durationII; // inverse of throughput, makespan = latency * durationII

  public final Map<Pair<String, String>, Long> paramsValues; // values of parameters having objectives

  // not used by comparators:
  public final int     askedCuts;    // > 0 if delay heuristic has been called
  public final int     askedPreCuts; // > 0 if delay heuristic has been called
  public final boolean isSchedulable;

  // minimum point since threshold are positive values
  public static final DSEpointIR ZERO = new DSEpointIR(0, 0, 0, 0, 0, new HashMap<>(), true);

  public static final String CSV_HEADER_STRING = "Schedulability;Power;Latency;DurationII;AskedCuts;AskedPrecuts";

  /**
   * Default constructor, with maximum values everywhere.
   */
  public DSEpointIR() {
    this(Long.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE, 0, 0, new HashMap<>(), false);
  }

  /**
   * New DSE point with heuristic delay informations.
   * 
   * @param energy
   *          the energy
   * @param latency
   *          the latency (as factor of durationII)
   * @param durationII
   *          the durationII (inverse of throughput)
   * @param askedCuts
   *          the number of cuts asked to the delay placement heuristic
   * @param askedPreCuts
   *          the number of preselection cuts asked to the delay placement heuristic
   * @param paramsValues
   *          values of parameters having objectives
   * @param isSchedulable
   *          Whether or not this configuration is schedulable.
   */
  public DSEpointIR(final long energy, final int latency, final long durationII, final int askedCuts,
      final int askedPreCuts, final Map<Pair<String, String>, Long> paramsValues, final boolean isSchedulable) {
    this.energy = energy;
    this.latency = latency;
    this.durationII = durationII;
    this.askedCuts = askedCuts;
    this.askedPreCuts = askedPreCuts;
    this.paramsValues = paramsValues;
    this.isSchedulable = isSchedulable;
  }

  @Override
  public String toString() {
    return "Schedulable:  " + isSchedulable + "  Energy:  " + energy + "  Latency:  " + latency + "x  DurationII:  "
        + durationII + "  Asked cuts: " + askedCuts + " among " + askedPreCuts;
  }

  public String toCsvContentString() {
    // force US because one day we may want to print floating POINT numbers (not coma as in French)
    return String.format(Locale.US, "%b;%d;%d;%d;%d;%d", isSchedulable, energy, latency, durationII, askedCuts,
        askedPreCuts);
  }

  /**
   * Compare two DSE points with comparators in the same order as listed in the constructor arguments. If the first
   * comparator results in 0, the second comparator is called, and so on.
   * 
   * @author ahonorat
   */
  public static class DSEpointGlobalComparator implements Comparator<DSEpointIR> {

    private final List<Comparator<DSEpointIR>> comparators;
    private final ParameterComparator          paramComparator;
    private final boolean                      hasThresholds;
    private final boolean                      delayAcceptance;
    private final int                          delayMaximumLatency;

    /**
     * Builds a global comparator calling successively the comparators in the arguments.
     * 
     * @param comparators
     *          List of comparators to call.
     * @param paramsObjvs
     *          Map of parameters, as pair of (parentName, parameterName), and their objectives (min: - or max: +),
     *          evaluated in given order.
     * 
     */
    public DSEpointGlobalComparator(final List<Comparator<DSEpointIR>> comparators,
        final LinkedHashMap<Pair<String, String>, Character> paramsObjvs) {
      this.comparators = new ArrayList<>(comparators);
      this.delayAcceptance = computesDelayAcceptance(comparators);
      if (!delayAcceptance) {
        this.delayMaximumLatency = 1;
      } else {
        this.delayMaximumLatency = computesMaxLatency(comparators);
      }
      hasThresholds = comparators.stream().anyMatch(x -> x instanceof ThresholdComparator<?>);
      this.paramComparator = new ParameterComparator(paramsObjvs);
      this.comparators.add(paramComparator);
    }

    @Override
    public int compare(DSEpointIR o1, DSEpointIR o2) {

      for (final Comparator<DSEpointIR> comparator : comparators) {
        final int res = comparator.compare(o1, o2);
        if (res != 0) {
          return res;
        }
      }

      return 0;
    }

    /**
     * Computes values of parameters in the current state of their graph.
     * 
     * @param graph
     *          Graph with resolved parameters.
     * @return Map of parameter values of parameter objectives.
     */
    public Map<Pair<String, String>, Long> getParamsValues(final PiGraph graph) {
      return paramComparator.getParamsValues(graph);
    }

    /**
     * 
     * @return Whether or not this comparator has at least one threshold.
     */
    public boolean hasThresholds() {
      return hasThresholds;
    }

    /**
     * Checks if all threshold comparators are met by the current point.
     * 
     * @param point
     *          DSE point to check.
     * @return Whether or not the current point respect all thresholds. If no thresholds, returns true.
     */
    public boolean areAllThresholdMet(DSEpointIR point) {
      final List<Comparator<DSEpointIR>> thresholdComparators = comparators.stream()
          .filter(x -> x instanceof ThresholdComparator).collect(Collectors.toList());
      for (final Comparator<DSEpointIR> comparator : thresholdComparators) {
        int res = comparator.compare(point, ZERO);
        if (res != 0) {
          return false;
        }
      }

      return true;
    }

    /**
     * Checks if all threshold except throughput and energy are met by the current point. If so it may be interesting to
     * add delays. If not, adding delays will worsen the current point result.
     * 
     * @param point
     *          DSE point to check.
     * @return Whether or not the current point does not respect all other threshold than throughput and energy.
     */
    public boolean areAllNonThroughputAndEnergyThresholdsMet(DSEpointIR point) {
      final List<Comparator<DSEpointIR>> thresholdComparators = comparators.stream()
          .filter(x -> x instanceof ThresholdComparator).collect(Collectors.toList());
      for (final Comparator<DSEpointIR> comparator : thresholdComparators) {
        final int res = comparator.compare(point, ZERO);
        if (res != 0
            && !(comparator instanceof ThroughputAtLeastComparator || comparator instanceof EnergyAtMostComparator)) {
          return false;
        }
      }

      return true;
    }

    /**
     * 
     * @return Maximum latency if specified (greater than 0).
     */
    public int getMaximumLatency() {
      return delayMaximumLatency;
    }

    /**
     * Get the maximal latency if specified.
     * 
     * @param comparators
     *          Comparators to check.
     * @return Minimal value of latency threshold, or LONG.MAX_VALUE if no threshold.
     */
    private static int computesMaxLatency(final List<Comparator<DSEpointIR>> comparators) {
      final Optional<Integer> min = comparators.stream().filter(x -> x instanceof LatencyAtMostComparator)
          .map(x -> ((LatencyAtMostComparator) x).threshold).min(Long::compare);
      if (min.isPresent()) {
        return min.get();
      }
      return Integer.MAX_VALUE;
    }

    /**
     * If minimization of latency or makespan objectives are more important than throughput, then, no delays must be
     * added. (Except to respect graph period, not taken into account here).
     * 
     * @return Whether or not more delays can be added.
     */
    public boolean doesAcceptsMoreDelays() {
      return delayAcceptance;
    }

    /**
     * If minimization of latency or makespan objectives are more important than throughput, then, no delays must be
     * added. (Except to respect graph period, not taken into account here).
     * 
     * @return Whether or not more delays can be added.
     */
    private static boolean computesDelayAcceptance(final List<Comparator<DSEpointIR>> comparators) {
      // get index of first occurrence of throughput objective
      int indexFirstT = comparators.size();
      final Optional<Comparator<DSEpointIR>> firstT = comparators.stream()
          .filter(x -> x instanceof ThroughputMaxComparator | x instanceof ThroughputAtLeastComparator).findFirst();
      if (firstT.isPresent()) {
        indexFirstT = comparators.indexOf(firstT.get());
      }
      // same for minimization objectives of latency or makespan
      int indexFirstLMmin = comparators.size();
      final Optional<Comparator<DSEpointIR>> firstLmin = comparators.stream()
          .filter(x -> x instanceof LatencyMinComparator).findFirst();
      if (firstLmin.isPresent()) {
        indexFirstLMmin = comparators.indexOf(firstLmin.get());
      }
      final Optional<Comparator<DSEpointIR>> firstMmin = comparators.stream()
          .filter(x -> x instanceof MakespanMinComparator).findFirst();
      if (firstMmin.isPresent()) {
        indexFirstLMmin = Math.min(indexFirstLMmin, comparators.indexOf(firstMmin.get()));
      }
      // if throughput is more important than latency or makespan, then we can add more delays
      return indexFirstT < indexFirstLMmin;
    }

    /**
     * Computes a number of cuts to ask to improve the result.
     * 
     * @param maxCuts
     *          Current maximum possible, see {@link getMaximumLatency}
     * @param nbCore
     *          Number of core in the architecture.
     * @param durationII
     *          Duration of the Initiation Interval from previous schedule (or graph period).
     * @param totalLoad
     *          Total load of the firings.
     * @param maxSingleLoad
     *          Maximum load of all firings.
     * @return Number of cuts to ask, between 0 and {@code nbCore} (not included).
     */
    public int computeCutsAmount(int maxCuts, int nbCore, long durationII, long totalLoad, long maxSingleLoad) {
      int res = Math.min(maxCuts, nbCore - 1);
      // cast to long/int == truncating == Math.floor if positive number
      final long minAvgDuration = Math.max(maxSingleLoad, (totalLoad / (long) nbCore));
      final int defaultDelayCut = (int) Math.ceil((double) durationII / minAvgDuration) - 1;
      res = Math.min(res, defaultDelayCut);
      // default value, now let's refine it with latency and makespan thresholds
      final List<Comparator<DSEpointIR>> thresholdComparators = comparators.stream()
          .filter(x -> x instanceof ThresholdComparator).collect(Collectors.toList());
      for (final Comparator<DSEpointIR> comparator : thresholdComparators) {
        if (comparator instanceof MakespanAtMostComparator) {
          final MakespanAtMostComparator mamc = (MakespanAtMostComparator) comparator;
          final int maxDelay = (int) (mamc.threshold / minAvgDuration);
          res = Math.min(res, maxDelay);
        }
        if (comparator instanceof ThroughputAtLeastComparator) {
          final ThroughputAtLeastComparator talc = (ThroughputAtLeastComparator) comparator;
          final int maxDelay = (int) Math.ceil((double) durationII / talc.threshold) - 1;
          res = Math.min(res, maxDelay);
        }

      }
      // ensures no negative
      return Math.max(res, 0);
    }

  }

  public static class ParameterComparator implements Comparator<DSEpointIR> {

    public final LinkedHashMap<Pair<String, String>, Character> paramsMinOrMax;

    /**
     * Comparator of given parameters (minimization or maximization).
     * 
     * @param paramsMinOrMax
     *          Parameters, as pair of (parentName, parameterName), to minimize or maximize (order is kept for
     *          comparisons).
     */
    public ParameterComparator(final LinkedHashMap<Pair<String, String>, Character> paramsMinOrMax) {
      this.paramsMinOrMax = paramsMinOrMax;
    }

    @Override
    public int compare(DSEpointIR arg0, DSEpointIR arg1) {
      for (Entry<Pair<String, String>, Character> en : paramsMinOrMax.entrySet()) {
        final long p0 = arg0.paramsValues.get(en.getKey());
        final long p1 = arg1.paramsValues.get(en.getKey());
        final int cmp = Long.compare(p0, p1);
        if (cmp != 0) {
          if (en.getValue() == '+') {
            // maximization
            return -cmp;
          } else {
            // minimization
            return cmp;
          }
        }
      }
      return 0;
    }

    /**
     * Compute map values of parameters
     * 
     * @param graph
     *          Graph with resolved parameters.
     * @return Map of parameter values.
     */
    public Map<Pair<String, String>, Long> getParamsValues(final PiGraph graph) {
      final Map<Pair<String, String>, Long> paramsValues = new HashMap<>();
      for (Entry<Pair<String, String>, Character> en : paramsMinOrMax.entrySet()) {
        Pair<String, String> p = en.getKey();
        Parameter param = graph.lookupParameterGivenGraph(p.getValue(), p.getKey());
        paramsValues.put(p, param.getExpression().evaluate());
      }
      return paramsValues;
    }

  }

  public abstract static class ThresholdComparator<T> implements Comparator<DSEpointIR> {

    public final T threshold;

    public ThresholdComparator(final T threshold) {
      this.threshold = threshold;
    }

  }

  /**
   * Negative if first point has lower power than second.
   * 
   * @author ahonorat
   */
  public static class PowerMinComparator implements Comparator<DSEpointIR> {

    @Override
    public int compare(DSEpointIR arg0, DSEpointIR arg1) {
      return Double.compare((double) arg0.energy / (double) arg0.durationII,
          (double) arg1.energy / (double) arg1.durationII);
    }

  }

  /**
   * Negative if first point has lower power than second. 0 if both are below the threshold.
   * 
   * @author ahonorat
   */
  public static class PowerAtMostComparator extends ThresholdComparator<Double> {

    public PowerAtMostComparator(final double threshold) {
      super(threshold);
    }

    @Override
    public int compare(DSEpointIR arg0, DSEpointIR arg1) {
      double power0 = (double) arg0.energy / (double) arg0.durationII;
      double power1 = (double) arg1.energy / (double) arg1.durationII;
      if (power0 > threshold || power1 > threshold) {
        return Double.compare(power0, power1);
      }
      return 0;
    }

  }

  /**
   * Negative if first point has lower energy than second.
   * 
   * @author ahonorat
   */
  public static class EnergyMinComparator implements Comparator<DSEpointIR> {

    @Override
    public int compare(DSEpointIR arg0, DSEpointIR arg1) {
      return Long.compare(arg0.energy, arg1.energy);
    }

  }

  /**
   * Negative if first point has lower energy than second. 0 if both are below the threshold.
   * 
   * @author ahonorat
   */
  public static class EnergyAtMostComparator extends ThresholdComparator<Long> {

    public EnergyAtMostComparator(final long threshold) {
      super(threshold);
    }

    @Override
    public int compare(DSEpointIR arg0, DSEpointIR arg1) {
      if (arg0.energy > threshold || arg1.energy > threshold) {
        return Long.compare(arg0.energy, arg1.energy);
      }
      return 0;
    }

  }

  /**
   * Negative if first point has latency lower than second.
   * 
   * @author ahonorat
   */
  public static class LatencyMinComparator implements Comparator<DSEpointIR> {

    @Override
    public int compare(DSEpointIR arg0, DSEpointIR arg1) {
      return Integer.compare(arg0.latency, arg1.latency);
    }

  }

  /**
   * Negative if first point has lower latency than second. 0 if both are below the threshold.
   * 
   * @author ahonorat
   */
  public static class LatencyAtMostComparator extends ThresholdComparator<Integer> {

    public LatencyAtMostComparator(final int threshold) {
      super(threshold);
    }

    @Override
    public int compare(DSEpointIR arg0, DSEpointIR arg1) {
      if (arg0.latency > threshold || arg1.latency > threshold) {
        return Integer.compare(arg0.latency, arg1.latency);
      }
      return 0;
    }

  }

  /**
   * Negative if first point has makespan lower than second (same as {@link LatencyMinComparator}.
   * 
   * @author ahonorat
   */
  public static class MakespanMinComparator implements Comparator<DSEpointIR> {

    @Override
    public int compare(DSEpointIR arg0, DSEpointIR arg1) {
      return Long.compare(arg0.latency, arg1.latency);
    }

  }

  /**
   * Negative if first point has lower makespan than second. 0 if both are below the threshold.
   * 
   * @author ahonorat
   */
  public static class MakespanAtMostComparator extends ThresholdComparator<Long> {

    public MakespanAtMostComparator(final long threshold) {
      super(threshold);
    }

    @Override
    public int compare(DSEpointIR arg0, DSEpointIR arg1) {
      if (arg0.latency * arg0.durationII > threshold || arg1.latency * arg1.durationII > threshold) {
        return Long.compare(arg0.latency, arg1.latency);
      }
      return 0;
    }

  }

  /**
   * Negative if first point has lower durationII than second.
   * 
   * @author ahonorat
   */
  public static class ThroughputMaxComparator implements Comparator<DSEpointIR> {

    @Override
    public int compare(DSEpointIR arg0, DSEpointIR arg1) {
      return Long.compare(arg0.durationII, arg1.durationII);
    }

  }

  /**
   * Negative if first point has lower durationII than second. 0 if both are below the threshold.
   * 
   * @author ahonorat
   */
  public static class ThroughputAtLeastComparator extends ThresholdComparator<Long> {

    public ThroughputAtLeastComparator(final long threshold) {
      super(threshold);
    }

    @Override
    public int compare(DSEpointIR arg0, DSEpointIR arg1) {
      if (arg0.durationII > threshold || arg1.durationII > threshold) {
        return Long.compare(arg0.durationII, arg1.durationII);
      }
      return 0;
    }

  }

}
