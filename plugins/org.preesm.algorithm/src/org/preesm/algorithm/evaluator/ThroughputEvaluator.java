/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Baptiste Launay [bapt.launay@gmail.com] (2015)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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
package org.preesm.algorithm.evaluator;

import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.preesm.model.scenario.Scenario;

/**
 * Class used to evaluate the throughput of a SDF or IBSDF graph on its optimal periodic schedule.
 *
 * @author blaunay
 */
public abstract class ThroughputEvaluator {

  /** The scenar. */
  private Scenario scenar;

  /**
   * Launches the evaluation of the throughput.
   *
   * @param inputGraph
   *          the graph to evaluate
   * @return the double
   */
  public abstract double launch(SDFGraph inputGraph);

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
  protected double throughputComputation(final double period, final SDFGraph sdf) {
    double minThroughput = Double.MAX_VALUE;
    double tmp;
    // use the normalized value Z of every actor
    for (final SDFAbstractVertex vertex : sdf.vertexSet()) {
      // Hierarchical vertex, go look for its internal actors
      if (vertex.getGraphDescription() instanceof SDFGraph) {
        tmp = throughputComputation(period, (SDFGraph) vertex.getGraphDescription());
      } else if (vertex.getInterfaces().get(0) instanceof SDFSourceInterfaceVertex) {
        // throughput actor = 1/(K*Z)
        tmp = 1.0 / (period
            * (double) (((SDFEdge) vertex.getAssociatedEdge(vertex.getInterfaces().get(0))).getCons().getValue()));
      } else {
        tmp = 1.0 / (period
            * (double) (((SDFEdge) vertex.getAssociatedEdge(vertex.getInterfaces().get(0))).getProd().getValue()));
      }
      // We are looking for the actor with the smallest throughput
      if (tmp < minThroughput) {
        minThroughput = tmp;
      }
    }
    return minThroughput;
  }

  public Scenario getScenar() {
    return scenar;
  }

  public void setScenar(Scenario scenar) {
    this.scenar = scenar;
  }
}
