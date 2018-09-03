/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
package org.ietr.preesm.mapper.params;

import java.util.Map;
import java.util.logging.Level;
import org.ietr.dftools.workflow.tools.WorkflowLogger;

// TODO: Auto-generated Javadoc
/**
 * Class which purpose is setting the parameters for the fast algorithm.
 *
 * @author pmenuet
 * @author mpelcat
 */

public class FastAlgoParameters {

  /** true if we need to display the intermediate solutions. */
  private boolean displaySolutions;

  /** Time in seconds we want to run FAST. */
  private int fastTime = 200;

  /** Time in seconds spent in one FAST local neighborhood. */
  private int fastLocalSearchTime = 10;

  /**
   * Instantiates a new fast algo parameters.
   *
   * @param textParameters
   *          the text parameters
   */
  public FastAlgoParameters(final Map<String, String> textParameters) {

    this.displaySolutions = Boolean.valueOf(textParameters.get("displaySolutions"));
    if (Integer.valueOf(textParameters.get("fastTime")) > 0) {
      this.fastTime = Integer.valueOf(textParameters.get("fastTime"));
    }
    if (Integer.valueOf(textParameters.get("fastLocalSearchTime")) > 0) {
      this.fastLocalSearchTime = Integer.valueOf(textParameters.get("fastLocalSearchTime"));
    }

    WorkflowLogger.getLogger().log(Level.INFO, "The Fast algo parameters are: displaySolutions=true/false; "
        + "fastTime in seconds; fastLocalSearchTime in seconds");
  }

  /**
   * Constructors.
   *
   * @param fastTime
   *          the fast time
   * @param fastLocalSearchTime
   *          the fast local search time
   * @param displaySolutions
   *          the display solutions
   */

  public FastAlgoParameters(final int fastTime, final int fastLocalSearchTime, final boolean displaySolutions) {

    this.displaySolutions = displaySolutions;
    this.fastTime = fastTime;
    this.fastLocalSearchTime = fastLocalSearchTime;
  }

  /**
   * Getters and setters.
   *
   * @return true, if is display solutions
   */

  public boolean isDisplaySolutions() {
    return this.displaySolutions;
  }

  /**
   * Sets the display solutions.
   *
   * @param displaySolutions
   *          the new display solutions
   */
  public void setDisplaySolutions(final boolean displaySolutions) {
    this.displaySolutions = displaySolutions;
  }

  /**
   * Returns the time in seconds for the whole FAST process.
   *
   * @return the fast time
   */
  public int getFastTime() {
    return this.fastTime;
  }

  /**
   * Returns the time in seconds spent in one FAST local neighborhood.
   *
   * @return the fast local search time
   */
  public int getFastLocalSearchTime() {
    return this.fastLocalSearchTime;
  }
}
