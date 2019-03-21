/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Hamza Deroui <hamza.deroui@insa-rennes.fr> (2017)
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
package org.preesm.algorithm.deadlock;

import java.util.logging.Level;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.throughput.tools.Stopwatch;
import org.preesm.commons.logger.PreesmLogger;

/**
 * @author hderoui
 *
 */
public abstract class IBSDFConsistency {

  /**
   * Compute the Repetition factor of all actors of the hierarchy
   *
   * @param graph
   *          IBSDF graph
   * @return true if consistent
   */
  public static boolean computeRV(final SDFGraph graph) {
    final Stopwatch timer = new Stopwatch();
    timer.start();

    // step 1: compute the RV of the top graph
    if (!SDFConsistency.computeRV(graph)) {
      timer.stop();
      PreesmLogger.getLogger().log(Level.SEVERE,
          "IBSDF RV computation : " + graph.getName() + " is not consistent !! evaluated in " + timer.toString());
      return false;
    } else {
      // step 2: compute the RV of each subgraph
      for (final SDFAbstractVertex actor : graph.vertexSet()) {
        if (actor.getGraphDescription() != null) {
          if (!IBSDFConsistency.computeRV((SDFGraph) actor.getGraphDescription())) {
            timer.stop();
            return false;
          }
        }
      }
      timer.stop();
      PreesmLogger.getLogger().log(Level.INFO,
          "IBSDF RV computation : " + graph.getName() + " is consistent !! evaluated in " + timer.toString());
      return true;
    }
  }
}
