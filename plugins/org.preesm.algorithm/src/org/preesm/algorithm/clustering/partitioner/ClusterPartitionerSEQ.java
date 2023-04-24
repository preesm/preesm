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
import org.preesm.algorithm.pisdf.autodelays.AutoDelaysTask;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.util.PiSDFSubgraphBuilder;
import org.preesm.model.pisdf.util.SEQSeeker;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;

/**
 * This class provide an algorithm to cluster a PiSDF graph and balance actor firings of clustered actor between coarse
 * and fine-grained parallelism. Resulting clusters are marked as PiSDF cluster, they have to be schedule with the
 * Cluster Scheduler.
 *
 * @author orenaud
 *
 */
public class ClusterPartitionerSEQ {

  /**
   * Input graph.
   */
  private final PiGraph  graph;
  /**
   * Workflow scenario.
   */
  private final Scenario scenario;
  /**
   * Number of PEs in compute clusters.
   */
  private final int      numberOfPEs;

  private final Design archi;

  private final Map<AbstractVertex, Long> brv;

  private final int                 clusterId;
  private final List<AbstractActor> nonClusterableList;

  /**
   * Builds a ClusterPartitioner object.
   *
   * @param graph
   *          Input graph.
   * @param scenario
   *          Workflow scenario.
   * @param numberOfPEs
   *          Number of processing elements in compute clusters.
   * @param brv
   * @param clusterId
   * @param nonClusterableList
   * @param archi
   */
  public ClusterPartitionerSEQ(final PiGraph graph, final Scenario scenario, final int numberOfPEs,
      Map<AbstractVertex, Long> brv, int clusterId, List<AbstractActor> nonClusterableList, Design archi) {
    this.graph = graph;
    this.scenario = scenario;
    this.numberOfPEs = numberOfPEs;
    this.brv = brv;
    this.clusterId = clusterId;
    this.nonClusterableList = nonClusterableList;
    this.archi = archi;
  }

  /**
   * @return Clustered PiGraph.
   */
  public PiGraph cluster() {

    // TODO: Look for actor groups other than SEQ chains.
    // Retrieve SEQ chains in input graph and verify that actors share component constraints.
    if (this.graph.getAllDelays().size() == 0) {
      final List<List<AbstractActor>> graphSEQs = new SEQSeeker(this.graph, this.numberOfPEs, this.brv,
          nonClusterableList, brv, 1).seek();

      // List<List<AbstractActor>> constrainedSEQs = new LinkedList<>();
      // if (!graphSEQs.isEmpty()) {
      // List<AbstractActor> SEQ = graphSEQs.get(0);// cluster one by one //
      // for (List<AbstractActor> SRV : graphSRVs) {
      // if (!ClusteringHelper.getListOfCommonComponent(SEQ, this.scenario).isEmpty()) {
      // constrainedSEQs.add(SEQ);
      // }
      // }

      // Subgraph 1st generation

      if (!graphSEQs.isEmpty()) {
        final List<AbstractActor> SEQ = graphSEQs.get(0);// cluster one by one
        final PiGraph subGraph = new PiSDFSubgraphBuilder(this.graph, SEQ, "seq_" + clusterId).buildSRV();
        final PiGraph subGraphCutted = AutoDelaysTask.addDelays(subGraph, archi, scenario, false, false, false,
            numberOfPEs, numberOfPEs, numberOfPEs + 1);// subGraphCutted
        graph.replaceActor(subGraph, subGraphCutted);
        // subGraphCutted.setClusterValue(true);
        // Add constraints of the cluster in the scenario.
        for (final ComponentInstance component : ClusteringHelper.getListOfCommonComponent(SEQ, this.scenario)) {
          this.scenario.getConstraints().addConstraint(component, subGraphCutted);
        }
        final Map<AbstractVertex, Long> brvcut = PiBRV.compute(this.graph, BRVMethod.LCM);
        // Retrieve pipeline stages
        final List<List<AbstractActor>> graphSEQ2s = new SEQSeeker(subGraphCutted, this.numberOfPEs, brvcut,
            nonClusterableList, brvcut, 2).seek();
        // Subgraph 2st generation
        if (!graphSEQ2s.isEmpty()) {
          final List<AbstractActor> SEQ2 = graphSEQ2s.get(0);// cluster one by one
          final PiGraph subsubGraph = new PiSDFSubgraphBuilder(subGraphCutted, SEQ2, "stage_" + clusterId).buildSRV();
          subsubGraph.setClusterValue(true);
          // Add constraints of the cluster in the scenario.
          for (final ComponentInstance component : ClusteringHelper.getListOfCommonComponent(SEQ2, this.scenario)) {
            this.scenario.getConstraints().addConstraint(component, subsubGraph);
          }
        }
      }
    }
    return this.graph;
  }

}
