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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class stores main metrics of a DSE point. (Design Space Exploration)
 * 
 * This class is intended to be used only through its provided comparators.
 * 
 * @author ahonorat
 */
public class DSEpointIR {

  // metrics for comparators:
  public final double power;      // energy divided by durationII
  public final int    latency;    // as factor of durationII
  public final long   durationII; // inverse of throughput, makespan = latency * durationII

  // not used by comparators:
  public final int askedCuts;    // > 0 if delay heuristic has been called
  public final int askedPreCuts; // > 0 if delay heuristic has been called

  // minimum point since threshold are positive values
  private static final DSEpointIR ZERO = new DSEpointIR(0, 0, 0);

  /**
   * Default constructor, with maximum values everywhere.
   */
  public DSEpointIR() {
    power = Long.MAX_VALUE;
    latency = Integer.MAX_VALUE;
    durationII = Long.MAX_VALUE;
    askedCuts = 0;
    askedPreCuts = 0;
  }

  /**
   * New DSE point.
   * 
   * @param power
   *          the energy
   * @param latency
   *          the latency (as factor of durationII)
   * @param durationII
   *          the durationII (inverse of throughput)
   */
  public DSEpointIR(final double power, final int latency, final long durationII) {
    this(power, latency, durationII, 0, 0);
  }

  /**
   * New DSE point with heuristic delay informations.
   * 
   * @param power
   *          the energy
   * @param latency
   *          the latency (as factor of durationII)
   * @param durationII
   *          the durationII (inverse of throughput)
   * @param askedCuts
   *          the number of cuts asked to the delay placement heuristic
   * @param askedPreCuts
   *          the number of preselection cuts asked to the delay placement heuristic
   */
  public DSEpointIR(final double power, final int latency, final long durationII, final int askedCuts,
      final int askedPreCuts) {
    this.power = power;
    this.latency = latency;
    this.durationII = durationII;
    this.askedCuts = askedCuts;
    this.askedPreCuts = askedPreCuts;
  }

  @Override
  public String toString() {
    return "Energy:  " + power + "  Latency:  " + latency + "x  DurationII:  " + durationII + "  Asked cuts: "
        + askedCuts + " among " + askedPreCuts;
  }

  /**
   * Compare two DSE points with comparators in the same order as listed in the constructor arguments. If the first
   * comparator results in 0, the second comparator is called, and so on.
   * 
   * @author ahonorat
   */
  public static class DSEpointGlobalComparator implements Comparator<DSEpointIR> {

    private final List<Comparator<DSEpointIR>> comparators;
    private final boolean                      hasThresholds;
    private final boolean                      delayAcceptance;
    private final int                          delayMaximumLatency;

    /**
     * Builds a global comparator calling successively the comparators in the arguments.
     * 
     * @param comparators
     *          List of comparators to call.
     * 
     */
    public DSEpointGlobalComparator(final List<Comparator<DSEpointIR>> comparators) {
      this.comparators = comparators;
      this.delayAcceptance = computesDelayAcceptance(comparators);
      if (!delayAcceptance) {
        this.delayMaximumLatency = 1;
      } else {
        this.delayMaximumLatency = computesMaxLatency(comparators);
      }
      hasThresholds = comparators.stream().anyMatch(x -> x instanceof ThresholdComparator<?>);
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
     * Checks if all threshold except throughput are met by the current point.
     * 
     * @param point
     *          DSE point to check.
     * @return Whether or not the current point does not respect all other threshold than throughput.
     */
    public boolean areAllNonThroughputThresholdsMet(DSEpointIR point) {
      final List<Comparator<DSEpointIR>> thresholdComparators = comparators.stream()
          .filter(x -> x instanceof ThresholdComparator).collect(Collectors.toList());
      for (final Comparator<DSEpointIR> comparator : thresholdComparators) {
        final int res = comparator.compare(point, ZERO);
        if (res != 0 && !(comparator instanceof ThroughputAtLeastComparator)) {
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
     * @return Mimimal value of latency threshold, or LONG.MAX_VALUE if no threshold.
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
     *          Tolal load of the firings.
     * @param maxSingleLoad
     *          Maximum load of all firings.
     * @return Number of cuts to ask, between 0 and {@code nbCore} (not included).
     */
    public int computeCutsAmount(int maxCuts, int nbCore, long durationII, long totalLoad, long maxSingleLoad) {
      int res = Math.min(maxCuts, nbCore);
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

  public abstract static class ThresholdComparator<T> implements Comparator<DSEpointIR> {

    public final T threshold;

    public ThresholdComparator(final T threshold) {
      this.threshold = threshold;
    }

  }

  /**
   * Negative if first point has lower energy than second.
   * 
   * @author ahonorat
   */
  public static class PowerMinComparator implements Comparator<DSEpointIR> {

    @Override
    public int compare(DSEpointIR arg0, DSEpointIR arg1) {
      return Double.compare(arg0.power, arg1.power);
    }

  }

  /**
   * Negative if first point has lower energy than second. 0 if both are below the threshold.
   * 
   * @author ahonorat
   */
  public static class PowerAtMostComparator extends ThresholdComparator<Double> {

    public PowerAtMostComparator(final double threshold) {
      super(threshold);
    }

    @Override
    public int compare(DSEpointIR arg0, DSEpointIR arg1) {
      if (arg0.power > threshold || arg1.power > threshold) {
        return Double.compare(arg0.power, arg1.power);
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
