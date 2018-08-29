/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * blaunay <bapt.launay@gmail.com> (2015)
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
package org.ietr.preesm.evaluator;

import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.preesm.core.scenario.PreesmScenario;

// TODO: Auto-generated Javadoc
/**
 * Class used to evaluate the throughput of a SDF or IBSDF graph on its optimal periodic schedule.
 *
 * @author blaunay
 */
public abstract class ThroughputEvaluator {

  /** The scenar. */
  public PreesmScenario scenar;

  /**
   * Launches the evaluation of the throughput.
   *
   * @param inputGraph
   *          the graph to evaluate
   * @return the double
   * @throws InvalidExpressionException
   *           the invalid expression exception
   */
  public abstract double launch(SDFGraph inputGraph) throws InvalidExpressionException;

  /**
   * Returns the throughput of the graph given its optimal normalized period, the throughput is the minimal throughput
   * of an actor of the graph.
   *
   * @param period
   *          the period
   * @param sdf
   *          the sdf
   * @return the double
   */
  protected double throughput_computation(final double period, final SDFGraph sdf) {
    double min_throughput = Double.MAX_VALUE;
    double tmp;
    // use the normalized value Z of every actor
    for (final SDFAbstractVertex vertex : sdf.vertexSet()) {
      // Hierarchical vertex, go look for its internal actors
      if ((vertex.getGraphDescription() != null) && (vertex.getGraphDescription() instanceof SDFGraph)) {
        tmp = throughput_computation(period, (SDFGraph) vertex.getGraphDescription());
      } else {
        // throughput actor = 1/(K*Z)
        if (vertex.getInterfaces().get(0) instanceof SDFSourceInterfaceVertex) {
          tmp = 1.0 / (period
              * (double) (((SDFEdge) vertex.getAssociatedEdge(vertex.getInterfaces().get(0))).getCons().getValue()));
        } else {
          tmp = 1.0 / (period
              * (double) (((SDFEdge) vertex.getAssociatedEdge(vertex.getInterfaces().get(0))).getProd().getValue()));
        }
      }
      // We are looking for the actor with the smallest throughput
      if (tmp < min_throughput) {
        min_throughput = tmp;
      }
    }
    return min_throughput;
  }
}
