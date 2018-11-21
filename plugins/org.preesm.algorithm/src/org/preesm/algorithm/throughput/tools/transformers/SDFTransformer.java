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
package org.preesm.algorithm.throughput.tools.transformers;

import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.throughput.tools.helpers.GraphStructureHelper;
import org.preesm.algorithm.throughput.tools.helpers.Stopwatch;
import org.preesm.commons.math.MathFunctionsHelper;

/**
 * @author hderoui
 *
 *         This class implements SDF conversions algorithms : SDF to srSDF, HSDF and DAG.
 */
public interface SDFTransformer {

  /**
   * Converts an SDF graph to an HSDF graph : SDF => HSDF
   *
   * @param sdf
   *          graph
   * @return HSDF graph
   */
  public static SDFGraph convertToHSDF(final SDFGraph sdf) {
    final Stopwatch timer = new Stopwatch();
    timer.start();

    // create the SRSDF
    final SDFGraph hsdfGraph = new SDFGraph();
    hsdfGraph.setName(sdf.getName() + "_HSDF");

    // create actors instances
    for (final SDFAbstractVertex a : sdf.vertexSet()) {
      for (long i = 1; i <= a.getNbRepeatAsLong(); i++) {
        // create an instance a_i of the actor a
        GraphStructureHelper.addActor(hsdfGraph, a.getName() + "_" + i, (SDFGraph) a.getGraphDescription(), 1L,
            (Double) a.getPropertyBean().getValue("duration"), 0, a);

      }
    }

    // creates the edges
    for (final SDFEdge e : sdf.edgeSet()) {
      for (long i = 1; i <= e.getSource().getNbRepeatAsLong(); i++) {
        for (long k = 1; k <= e.getProd().longValue(); k += 1) {
          // compute the target actor instance id, and delay
          final long j = ((((e.getDelay().longValue() + ((i - 1) * e.getProd().longValue()) + k) - 1)
              % (e.getCons().longValue() * e.getTarget().getNbRepeatAsLong())) / e.getCons().longValue()) + 1;
          final long d = ((e.getDelay().longValue() + ((i - 1) * e.getProd().longValue()) + k) - 1)
              / (e.getCons().longValue() * e.getTarget().getNbRepeatAsLong());

          // add the edge
          GraphStructureHelper.addEdge(hsdfGraph, e.getSource().getName() + "_" + i, null,
              e.getTarget().getName() + "_" + j, null, 1, 1, d, e);

        }
      }
    }

    timer.stop();
    return hsdfGraph;
  }

  /**
   * Converts an SDF graph to a srSDF graph : SDF => srSDF
   *
   * @param sdf
   *          graph
   * @return srSDF graph
   */
  public static SDFGraph convertToSrSDF(final SDFGraph sdf) {
    final Stopwatch timer = new Stopwatch();
    timer.start();

    // create the SRSDF
    final SDFGraph singleRate = new SDFGraph();
    singleRate.setName(sdf.getName() + "_srSDF");

    // create actors instances
    for (final SDFAbstractVertex a : sdf.vertexSet()) {
      for (int i = 1; i <= a.getNbRepeatAsLong(); i++) {
        // create an instance a_i of the actor a
        GraphStructureHelper.addActor(singleRate, a.getName() + "_" + i, (SDFGraph) a.getGraphDescription(), 1L,
            (Double) a.getPropertyBean().getValue("duration"), 0, a);
      }
    }

    // creates the edges
    for (final SDFEdge e : sdf.edgeSet()) {
      for (long i = 1; i <= e.getSource().getNbRepeatAsLong(); i++) {
        for (long k = 1; k <= e.getProd().longValue(); k += 1) {
          // compute the target actor instance id, cons/prod rate, and delay
          final long l = ((((e.getDelay().longValue() + ((i - 1) * e.getProd().longValue()) + k) - 1)
              % (e.getCons().longValue() * e.getTarget().getNbRepeatAsLong())) % e.getCons().longValue()) + 1;
          final long j = ((((e.getDelay().longValue() + ((i - 1) * e.getProd().longValue()) + k) - 1)
              % (e.getCons().longValue() * e.getTarget().getNbRepeatAsLong())) / e.getCons().longValue()) + 1;
          final long d = ((e.getDelay().longValue() + ((i - 1) * e.getProd().longValue()) + k) - 1)
              / (e.getCons().longValue() * e.getTarget().getNbRepeatAsLong());

          final long ma = e.getProd().longValue() - (k - 1);
          final long mb = e.getCons().longValue() - (l - 1);
          final long m = Math.min(ma, mb);
          k += (m - 1);

          // add the edge
          GraphStructureHelper.addEdge(singleRate, e.getSource().getName() + "_" + i, null,
              e.getTarget().getName() + "_" + j, null, m, m, d * m, e);

        }
      }
    }

    timer.stop();
    return singleRate;
  }

  /**
   * Converts an SDF graph to a reduced HSDF graph : SDF => srSDF => HSDF
   *
   * @param sdf
   *          graph
   * @return HSDF graph with less number of edges
   */
  public static SDFGraph convertToReducedHSDF(final SDFGraph sdf) {
    final Stopwatch timer = new Stopwatch();
    timer.start();

    // convert the SDF graph to a srSDF graph first then convert the srSDF graph to an HSDF graph
    SDFGraph hsdfHraph = SDFTransformer.convertToSrSDF(sdf);
    hsdfHraph = SrSDFTransformer.convertToHSDF(hsdfHraph);

    timer.stop();
    return hsdfHraph;
  }

  /**
   * Converts an SDF graph to a DAG : SDF => srSDF => DAG
   *
   * @param sdf
   *          graph
   * @return DAG
   */
  public static SDFGraph convertToDAG(final SDFGraph sdf) {
    final Stopwatch timer = new Stopwatch();
    timer.start();

    // convert the SDF graph to a srSDF graph first then convert the srSDF graph to a DAG
    SDFGraph dag = SDFTransformer.convertToSrSDF(sdf);
    dag = SrSDFTransformer.convertToDAG(dag);

    timer.stop();
    return dag;
  }

  /**
   * normalize an SDF graph for the liveness test with the sufficient condition and for periodic schedule computation.
   *
   * @param sdf
   *          graph
   */
  public static void normalize(final SDFGraph sdf) {
    final Stopwatch timer = new Stopwatch();
    timer.start();

    double kRv = 1;
    for (final SDFAbstractVertex actor : sdf.vertexSet()) {
      kRv = MathFunctionsHelper.lcm(kRv, actor.getNbRepeatAsLong());
    }

    for (final SDFEdge edge : sdf.edgeSet()) {
      edge.getSource().setPropertyValue("normalizedRate", kRv / edge.getSource().getNbRepeatAsLong());
      edge.getTarget().setPropertyValue("normalizedRate", kRv / edge.getTarget().getNbRepeatAsLong());
      edge.setPropertyValue("normalizationFactor",
          kRv / (edge.getCons().longValue() * edge.getTarget().getNbRepeatAsLong()));
    }

    timer.stop();
  }

}
