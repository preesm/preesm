package org.preesm.model.slam.utils;

import java.util.List;
import org.preesm.model.slam.CPU;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.ProcessingElement;

/**
 * Class providing common check methods, telling if the design is homogeneous CPU or not for example.
 * 
 * @author ahonorat
 */
public class SlamDesignPEtypeChecker {

  private SlamDesignPEtypeChecker() {
    // do nothing, avoids external instantiation
  }

  /**
   * Checks if the design is homogeneous CPU (all processing elements are instances of the same CPU).
   * 
   * @param design
   *          the design
   * @return true if homogeneous CPU (at least one
   */
  public static boolean isHomogeneousCPU(final Design design) {
    List<ProcessingElement> pes = design.getProcessingElements();
    return (pes.size() == 1 && pes.stream().allMatch(x -> (x instanceof CPU) && !x.getInstances().isEmpty()));
  }

  /**
   * Checks if the design has CPU processing elements only (possibly not the same CPU).
   * 
   * @param design
   *          the design
   * @return true if only CPU (at least one)
   */
  public static boolean isOnlyCPU(final Design design) {
    List<ProcessingElement> pes = design.getProcessingElements();
    return (!pes.isEmpty() && pes.stream().allMatch(x -> (x instanceof CPU))
        && pes.stream().anyMatch(x -> !x.getInstances().isEmpty()));
  }

  /**
   * Checks if the design has a single FPGA processing element only.
   * 
   * @param design
   *          the design
   * @return true if only one processing element in the design, being an FPGA
   */
  public static boolean isSingleFPGA(final Design design) {
    List<ProcessingElement> pes = design.getProcessingElements();
    return (pes.size() == 1 && pes.stream().allMatch(x -> (x instanceof CPU) && x.getInstances().size() == 1));
  }

}
