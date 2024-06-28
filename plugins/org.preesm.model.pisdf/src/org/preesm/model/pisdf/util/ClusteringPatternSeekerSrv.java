/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Dylan Gageot [gageot.dylan@gmail.com] (2020)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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
package org.preesm.model.pisdf.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.PiGraph;

/**
 * This class is used to seek an isolate actor in a given PiGraph with a RV above PE number that form a Single
 * Repetition Vector (SRV)
 *
 * @author orenaud
 *
 */
public class ClusteringPatternSeekerSrv extends ClusteringPatternSeeker {

  private final long                      nPEs;
  private final Map<AbstractVertex, Long> brv;

  /**
   * Builds a SRVSeeker based on a input graph.
   *
   * @param inputGraph
   *          Input graph to search in.
   * @param numberOfPEs
   *          number of PEs
   * @param brv
   *          repetition vector
   */
  public ClusteringPatternSeekerSrv(final PiGraph inputGraph, long numberOfPEs, Map<AbstractVertex, Long> brv) {
    super(inputGraph);
    this.nPEs = numberOfPEs;
    this.brv = brv;
  }

  /**
   * Seek for SRV chain in the input graph.
   *
   * @return first of identified SRV candidate.
   */
  public List<AbstractActor> seek() {
    final List<AbstractActor> actorSRV = new LinkedList<>();

    for (final AbstractActor srvCandidate : graph.getExecutableActors()) {
      if (brv.get(srvCandidate) > nPEs && !srvCandidate.getName().equals("single_source")
          && !srvCandidate.getName().contains("urc")) {
        actorSRV.add(srvCandidate);
        return actorSRV;
      }
    }

    return actorSRV;
  }

}
