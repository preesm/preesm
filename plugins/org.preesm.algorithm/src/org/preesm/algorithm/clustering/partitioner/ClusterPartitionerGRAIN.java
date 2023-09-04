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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.util.GRAIN1Seeker;
import org.preesm.model.pisdf.util.GRAIN2Seeker;
import org.preesm.model.pisdf.util.PiSDFSubgraphBuilder;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.TimingType;

/**
 * This class provide an algorithm to cluster a PiSDF graph and balance actor firings of clustered actor between coarse
 * and fine-grained parallelism. Resulting clusters are marked as PiSDF cluster, they have to be schedule with the
 * Cluster Scheduler.
 *
 * @author orenaud
 *
 */
@SuppressWarnings("deprecation")
public class ClusterPartitionerGRAIN {

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

  private final Map<AbstractVertex, Long> brv;
  private final Map<AbstractVertex, Long> actorTiming;
  private final int                       clusterId;
  private final List<AbstractActor>       nonClusterableList;

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
   *          repetition vector
   * @param clusterId
   *          cluster identificator
   * @param nonClusterableList
   *          List of non clusterable actors
   */
  public ClusterPartitionerGRAIN(final PiGraph graph, final Scenario scenario, final int numberOfPEs,
      Map<AbstractVertex, Long> brv, int clusterId, List<AbstractActor> nonClusterableList) {
    this.graph = graph;
    this.scenario = scenario;
    this.numberOfPEs = numberOfPEs;
    this.brv = brv;
    this.clusterId = clusterId;
    this.nonClusterableList = nonClusterableList;
    this.actorTiming = new LinkedHashMap<>();
  }

  /**
   * @return Clustered PiGraph.
   */
  public PiGraph cluster() {
    actorTiming();
    final double memcpySpeed = scenario.getTimings().getMemTimings().get(0).getValue().getTimePerUnit();
    final Long memcpySetUp = scenario.getTimings().getMemTimings().get(0).getValue().getSetupTime();

    // Retrieve GRAIN chains in input graph and verify that actors share component constraints.
    final List<List<AbstractActor>> graphGRAINs = new GRAIN1Seeker(this.graph, this.numberOfPEs, this.brv,
        this.nonClusterableList, this.actorTiming, memcpySpeed, memcpySetUp).seek();
    final List<List<AbstractActor>> constrainedGRAINs = new LinkedList<>();
    if (!graphGRAINs.isEmpty()) {
      final List<AbstractActor> grain = graphGRAINs.get(0);// cluster one by one
      if (!ClusteringHelper.getListOfCommonComponent(grain, this.scenario).isEmpty()) {
        constrainedGRAINs.add(grain);
      }
    }

    // Cluster constrained SRV chains.

    final List<PiGraph> subGraphs = new LinkedList<>();
    if (!graphGRAINs.isEmpty()) {
      final List<AbstractActor> grain = graphGRAINs.get(0);// cluster one by one
      final PiGraph subGraph = new PiSDFSubgraphBuilder(this.graph, grain, "grain_" + clusterId).build();

      subGraph.setClusterValue(true);
      // Add constraints of the cluster in the scenario.
      for (final ComponentInstance component : ClusteringHelper.getListOfCommonComponent(grain, this.scenario)) {
        this.scenario.getConstraints().addConstraint(component, subGraph);
      }
      subGraphs.add(subGraph);
    }

    return this.graph;
  }

  private void actorTiming() {
    final double memcpySpeed = scenario.getTimings().getMemTimings().get(0).getValue().getTimePerUnit();
    final Long memcpySetUp = scenario.getTimings().getMemTimings().get(0).getValue().getSetupTime();
    for (final AbstractActor a : this.graph.getExecutableActors()) {
      if (a instanceof Actor) {
        if (scenario.getTimings().getActorTimings().get(a) == null) {
          this.actorTiming.put(a, 100L);
        } else {
          this.actorTiming.put(a, Long.valueOf(
              scenario.getTimings().getActorTimings().get(a).get(0).getValue().get(TimingType.EXECUTION_TIME)));
        }
      } else if (a instanceof SpecialActor) {
        Long nbToken = 0L;

        if (a instanceof ForkActor) {
          nbToken = a.getDataInputPorts().get(0).getExpression().evaluate();
        } else if (a instanceof JoinActor) {
          nbToken = a.getDataOutputPorts().get(0).getExpression().evaluate();
        } else if (a instanceof BroadcastActor) {
          nbToken = a.getDataInputPorts().get(0).getExpression().evaluate() * a.getDataOutputPorts().size();
        } else if (a instanceof RoundBufferActor) {
          nbToken = a.getDataOutputPorts().get(0).getExpression().evaluate() * a.getDataInputPorts().size();
        }
        this.actorTiming.put(a,
            (long) (nbToken * sizeofbit(a.getDataInputPorts().get(0).getFifo().getType()) * memcpySpeed + memcpySetUp));

      }
    }
  }

  private int sizeofbit(String type) {
    if (type.equals("byte") || type.equals("boolean")) {
      return 8;
    }
    if (type.equals("short") || type.equals("char") || type.equals("uchar")) {
      return 8;
    }
    if (type.equals("int") || type.equals("float")) {
      return 32;
    }
    if (type.equals("Long") || type.equals("double")) {
      return 64;
    }
    return 32;
  }

  /**
   * @deprecated
   */
  @Deprecated
  public PiGraph cluster2() {
    actorTiming();
    final Long longestParallelTiming = computelongestParallelTiming();
    // Retrieve GRAIN chains in input graph and verify that actors share component constraints.
    final List<List<AbstractActor>> graphGRAINs = new GRAIN2Seeker(this.graph, this.numberOfPEs, this.brv,
        this.nonClusterableList, this.actorTiming, longestParallelTiming).seek();
    final List<List<AbstractActor>> constrainedGRAINs = new LinkedList<>();
    if (!graphGRAINs.isEmpty()) {
      final List<AbstractActor> grain = graphGRAINs.get(0);// cluster one by one
      if (!ClusteringHelper.getListOfCommonComponent(grain, this.scenario).isEmpty()) {
        constrainedGRAINs.add(grain);
      }
    }

    // Cluster constrained SRV chains.

    final List<PiGraph> subGraphs = new LinkedList<>();
    if (!graphGRAINs.isEmpty()) {
      final List<AbstractActor> grain = graphGRAINs.get(0);// cluster one by one
      final PiGraph subGraph = new PiSDFSubgraphBuilder(this.graph, grain, "grain_" + clusterId).build();

      subGraph.setClusterValue(true);
      // Add constraints of the cluster in the scenario.
      for (final ComponentInstance component : ClusteringHelper.getListOfCommonComponent(grain, this.scenario)) {
        this.scenario.getConstraints().addConstraint(component, subGraph);
      }
      subGraphs.add(subGraph);
    }

    return this.graph;
  }

  private Long computelongestParallelTiming() {
    Long max = 0L;
    for (final Entry<AbstractVertex, Long> a : actorTiming.entrySet()) {
      if (actorTiming.get(a.getKey()) > max) {
        max = this.actorTiming.get(a.getKey());
      }
    }
    return max;
  }
}
