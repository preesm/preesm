/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
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

package org.preesm.model.slam.check;

import java.util.List;
import org.preesm.model.slam.CPU;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.FPGA;
import org.preesm.model.slam.GPU;
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
    final List<ProcessingElement> pes = design.getProcessingElements();
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
    final List<ProcessingElement> pes = design.getProcessingElements();
    return (!pes.isEmpty() && pes.stream().allMatch(x -> (x instanceof CPU))
        && pes.stream().anyMatch(x -> !x.getInstances().isEmpty()));
  }

  /**
   * Checks if the design has CPU and GPU processing elements only (possibly not the same CPU/GPU).
   *
   * @param design
   *          the design
   * @return true if dual CPU GPU (at least one each)
   */
  public static boolean isDualCPUGPU(final Design design) {
    final List<ProcessingElement> pes = design.getProcessingElements();
    return (!pes.isEmpty() && pes.stream().anyMatch(CPU.class::isInstance)
        && pes.stream().anyMatch(GPU.class::isInstance)
        && pes.stream().allMatch(x -> ((x instanceof CPU) || (x instanceof GPU)))
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
    final List<ProcessingElement> pes = design.getProcessingElements();
    return (pes.size() == 1 && pes.stream().allMatch(x -> (x instanceof FPGA) && x.getInstances().size() == 1));
  }

}
