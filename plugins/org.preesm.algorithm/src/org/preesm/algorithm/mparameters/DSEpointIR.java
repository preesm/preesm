package org.preesm.algorithm.mparameters;

import java.util.Comparator;
import java.util.List;

/**
 * This class stores main metrics of a DSE point. (Design Space Exploration)
 * 
 * This class is intended to be used only through its provided comparators.
 * 
 * @author ahonorat
 */
public class DSEpointIR {

  public final long energy;
  public final int  latency;    // as factor of durationII
  public final long durationII; // inverse of throughput

  /**
   * Default constructor, with maximum values everywhere.
   */
  public DSEpointIR() {
    energy = Long.MAX_VALUE;
    latency = Integer.MAX_VALUE;
    durationII = Long.MAX_VALUE;
  }

  /**
   * New DSE point.
   * 
   * @param energy
   *          the energy
   * @param latency
   *          the latency (as factor of durationII)
   * @param durationII
   *          the durationII (inverse of throughput)
   */
  public DSEpointIR(final long energy, final int latency, final long durationII) {
    this.energy = energy;
    this.latency = latency;
    this.durationII = durationII;
  }

  @Override
  public String toString() {
    return "Energy:  " + energy + "  Latency:  " + latency + "x  DurationII:  " + durationII;
  }

  /**
   * Compare two DSE points with comparators in the same order as listed in the constructor arguments. If the first
   * comparator results in 0, the second comparator is called, and so on.
   * 
   * @author ahonorat
   */
  public static class DSEpointGlobalComparator implements Comparator<DSEpointIR> {

    private final List<Comparator<DSEpointIR>> comparators;

    /**
     * Builds a global comparator calling successively the comparators in the arguments.
     * 
     * @param comparators
     *          List of comparators to call.
     * 
     */
    public DSEpointGlobalComparator(final List<Comparator<DSEpointIR>> comparators) {
      this.comparators = comparators;
    }

    @Override
    public int compare(DSEpointIR o1, DSEpointIR o2) {

      for (Comparator<DSEpointIR> comparator : comparators) {
        int res = comparator.compare(o1, o2);
        if (res != 0) {
          return res;
        }
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
  public static class EnergyAtMostComparator implements Comparator<DSEpointIR> {

    private final long threshold;

    public EnergyAtMostComparator(final long threshold) {
      this.threshold = threshold;
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
   * Negative if first point has latency energy than second.
   * 
   * @author ahonorat
   */
  public static class LatencyMinComparator implements Comparator<DSEpointIR> {

    @Override
    public int compare(DSEpointIR arg0, DSEpointIR arg1) {
      return Long.compare(arg0.latency, arg1.latency);
    }

  }

  /**
   * Negative if first point has lower latency than second. 0 if both are below the threshold.
   * 
   * @author ahonorat
   */
  public static class LatencyAtMostComparator implements Comparator<DSEpointIR> {

    private final long threshold;

    public LatencyAtMostComparator(final long threshold) {
      this.threshold = threshold;
    }

    @Override
    public int compare(DSEpointIR arg0, DSEpointIR arg1) {
      if (arg0.latency > threshold || arg1.latency > threshold) {
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
  public static class ThroughputAtLeastComparator implements Comparator<DSEpointIR> {

    private final long threshold;

    public ThroughputAtLeastComparator(final long threshold) {
      this.threshold = threshold;
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
