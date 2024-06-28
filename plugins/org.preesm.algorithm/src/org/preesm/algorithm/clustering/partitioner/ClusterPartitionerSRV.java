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
package org.preesm.algorithm.clustering.partitioner;

import java.util.List;
import java.util.Map;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.util.ClusteringPatternSeekerSrv;
import org.preesm.model.pisdf.util.PiSDFSubgraphBuilder;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;

/**
 * This class provide an algorithm to cluster a PiSDF graph and balance actor firings of clustered actor between coarse
 * and fine-grained parallelism. Resulting clusters are marked as PiSDF cluster, they have to be schedule with the
 * Cluster Scheduler.
 *
 * @author orenaud
 *
 */
public class ClusterPartitionerSRV extends ClusterPartitioner {

  private final Map<AbstractVertex, Long> brv;

  private final int       clusterId;
  private final ScapeMode scapeMode;
  private final Boolean   memoryOptim;
  Boolean                 isOnGPU = false;

  /**
   * Builds a ClusterPartitioner object.
   *
   * @param graph
   *          pigraph
   * @param scenario
   *          Workflow scenario.
   * @param numberOfPEs
   *          Number of processing elements in compute clusters.
   * @param brv
   *          repetition vector
   * @param clusterId
   *          List of non clusterable actors
   * @param memoryOptim
   *          Enable memory optimization to foster memory script
   */
  public ClusterPartitionerSRV(PiGraph graph, final Scenario scenario, final int numberOfPEs,
      Map<AbstractVertex, Long> brv, int clusterId, ScapeMode scapeMode, Boolean memoryOptim) {
    super(graph, scenario, numberOfPEs);
    this.memoryOptim = memoryOptim;
    this.brv = brv;
    this.clusterId = clusterId;
    this.scapeMode = scapeMode;
  }

  /**
   * @return Clustered PiGraph.
   */
  @Override
  public PiGraph cluster() {

    // Retrieve SRV first candidate in input graph and verify that actors share component constraints.
    final List<AbstractActor> graphSRVs = new ClusteringPatternSeekerSrv(this.graph, this.numberOfPEs, this.brv).seek();

    // Cluster constrained SRV chains.
    if (!graphSRVs.isEmpty()) {

      final PiGraph subGraph = new PiSDFSubgraphBuilder(this.graph, graphSRVs, "srv_" + clusterId).build();

      // compute mapping
      final Long nPE = ClusterPartitionerURC.mapping(graphSRVs, scenario, numberOfPEs, isOnGPU, brv);

      // apply scaling
      final Long scale = ClusterPartitionerURC.computeScalingFactor(subGraph,
          brv.get(subGraph.getExecutableActors().get(0)), nPE, scapeMode);

      for (final InterfaceActor iActor : subGraph.getDataInterfaces()) {
        iActor.getGraphPort().setExpression(
            iActor.getGraphPort().getExpression().evaluate() * brv.get(subGraph.getExecutableActors().get(0)) / scale);
        iActor.getDataPort().setExpression(iActor.getGraphPort().getExpression().evaluate());

      }

      if (memoryOptim.equals(Boolean.TRUE)) {
        // reduce memory exclusion graph matches
        ClusterPartitionerURC.reduceMemExMatches(subGraph, scale, brv);

      }

      // remove empty introduced fifo
      subGraph.getFifos().stream().filter(x -> x.getSourcePort() == null).forEach(subGraph::removeFifo);
      subGraph.getFifos().stream().filter(x -> x.getTargetPort() == null).forEach(subGraph::removeFifo);

      subGraph.setClusterValue(true);
      // Add constraints of the cluster in the scenario.
      for (final ComponentInstance component : ClusteringHelper.getListOfCommonComponent(graphSRVs, this.scenario)) {
        this.scenario.getConstraints().addConstraint(component, subGraph);
      }

    }

    // map the cluster on the CPU or GPU according to timing
    this.graph.setOnGPU(isOnGPU);

    return this.graph;
  }

}
