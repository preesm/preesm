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
package org.ietr.preesm.throughput.tools.transformers;

import java.util.ArrayList;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.types.LongEdgePropertyType;
import org.ietr.preesm.throughput.tools.helpers.Stopwatch;

/**
 * @author hderoui
 *
 *         This class implements srSDF conversions algorithms : srSDF to HSDF graph and DAG.
 */
public interface SrSDFTransformer {

  /**
   * Converts a srSDF graph to an HSDF graph
   *
   * @param srSDF
   *          graph
   * @return HSDF graph
   */
  public static SDFGraph convertToHSDF(final SDFGraph srSDF) {
    final Stopwatch timer = new Stopwatch();
    timer.start();

    // clone the srSDF
    final SDFGraph hsdfGraph = srSDF.copy();
    hsdfGraph.setName(srSDF.getName() + "_HSDF");

    // for each edge set cons=prod=1 and delay=delay/prod
    for (final SDFEdge edge : hsdfGraph.edgeSet()) {
      final long delay = edge.getDelay().longValue() / edge.getProd().longValue();
      edge.setProd(new LongEdgePropertyType(1));
      edge.setCons(new LongEdgePropertyType(1));
      edge.setDelay(new LongEdgePropertyType(delay));
    }

    timer.stop();
    return hsdfGraph;
  }

  /**
   * Converts a srSDF graph to a DAG graph
   *
   * @param srSDF
   *          graph
   * @return DAG
   */
  public static SDFGraph convertToDAG(final SDFGraph srSDF) {
    final Stopwatch timer = new Stopwatch();
    timer.start();

    // clone the srSDF
    final SDFGraph dag = srSDF.copy();
    dag.setName(srSDF.getName() + "_DAG");

    // save the list of edges
    final ArrayList<SDFEdge> edgeList = new ArrayList<>(dag.edgeSet().size());
    for (final SDFEdge edge : dag.edgeSet()) {
      edgeList.add(edge);
    }

    // remove edges with delays
    for (final SDFEdge edge : edgeList) {
      if (edge.getDelay().longValue() != 0) {
        dag.removeEdge(edge);
      }
    }

    timer.stop();
    return dag;
  }

}
