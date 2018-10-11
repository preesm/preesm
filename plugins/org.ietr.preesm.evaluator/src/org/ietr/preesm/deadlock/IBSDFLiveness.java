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
package org.ietr.preesm.deadlock;

import java.util.Map;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.throughput.tools.helpers.GraphStructureHelper;

/**
 * @author hderoui
 *
 */
public interface IBSDFLiveness {

  /**
   * Test if each subgraph in the hierarchy is live including the top graph
   *
   * @param ibsdf
   *          input graph
   * @return true if live, false if not.
   */
  public static boolean evaluate(final SDFGraph ibsdf) {
    // step 1 check the liveness of the top graph
    boolean live = SDFLiveness.evaluate(ibsdf);

    // step 2 check the liveness of the subgraphs
    if (live) {
      // get the list of hierarchical actors
      final Map<String, SDFAbstractVertex> allHierarchicalActors = GraphStructureHelper.getAllHierarchicalActors(ibsdf);

      // check the liveness of the subgraph of each hierarchical actor in the list
      for (final SDFAbstractVertex h : allHierarchicalActors.values()) {
        live = SDFLiveness.evaluate((SDFGraph) h.getGraphDescription());
        // if the subgraph is not live return false
        if (!live) {
          return false;
        }
      }
    }

    return live;
  }
}
