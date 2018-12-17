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
package org.preesm.algorithm.mapper.params;

import java.util.Map;
import java.util.logging.Level;
import org.preesm.algorithm.mapper.abc.AbcType;
import org.preesm.algorithm.mapper.abc.edgescheduling.EdgeSchedType;
import org.preesm.commons.logger.PreesmLogger;

/**
 * Parameters applied to the current Abc.
 *
 * @author mpelcat
 */
public class AbcParameters {

  /** Simulator type. */
  private AbcType simulatorType = null;

  /** Edge scheduling type. */
  private EdgeSchedType edgeSchedType = null;

  /** true if loads are minimized while minimizing other parameters. */
  private boolean balanceLoads = false;

  /**
   * Constructor from textual parameters.
   *
   * @param textParameters
   *          the text parameters
   */
  public AbcParameters(final Map<String, String> textParameters) {
    this.simulatorType = AbcType.fromString(textParameters.get("simulatorType"));
    this.edgeSchedType = EdgeSchedType.fromString(textParameters.get("edgeSchedType"));
    this.balanceLoads = Boolean.valueOf(textParameters.get("balanceLoads"));

    PreesmLogger.getLogger().log(Level.INFO,
        "The Abc parameters are: simulatorType=looselyTimed/approximatelyTimed/AccuratelyTimed; "
            + "edgeSchedType=Simple/Switcher; balanceLoads=true/false");
  }

  /**
   * Gets the simulator type.
   *
   * @return the simulator type
   */
  public AbcType getSimulatorType() {
    return this.simulatorType;
  }

  /**
   * Gets the edge sched type.
   *
   * @return the edge sched type
   */
  public EdgeSchedType getEdgeSchedType() {
    return this.edgeSchedType;
  }

  /**
   * Checks if is balance loads.
   *
   * @return true, if is balance loads
   */
  public boolean isBalanceLoads() {
    return this.balanceLoads;
  }
}
