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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.util.ClusteringPatternSeekerUrc;
import org.preesm.model.pisdf.util.PiSDFSubgraphBuilder;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.CPU;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.GPU;
import org.preesm.model.slam.TimingType;
import org.preesm.model.slam.check.SlamDesignPEtypeChecker;

/**
 * This class provide an algorithm to cluster a PiSDF graph and balance actor firings of clustered actor between coarse
 * and fine-grained parallelism. Resulting clusters are marked as PiSDF cluster, they have to be schedule with the
 * Cluster Scheduler.
 *
 * @author orenaud
 *
 */
public class ClusterPartitionerURC extends ClusterPartitioner {

  private Map<AbstractVertex, Long> brv;
  private final int                 clusterId;
  private final ScapeMode           scapeMode;
  private final PiGraph             graph;
  Boolean                           isOnGPU = false;

  /**
   * Builds a ClusterPartitioner Unique Repetition Count object.
   *
   * @param graph
   *          Subgraph if it's a hierarchical graph, otherwise the full graph
   *
   * @param scenario
   *          Workflow scenario.
   * @param numberOfPEs
   *          Number of processing elements in compute clusters.
   */
  public ClusterPartitionerURC(PiGraph graph, final Scenario scenario, final int numberOfPEs,
      Map<AbstractVertex, Long> brv, int clusterId, ScapeMode scapeMode) {
    super(graph, scenario, numberOfPEs);
    this.brv = brv;
    this.clusterId = clusterId;
    this.scapeMode = scapeMode;
    this.graph = graph;
  }

  /**
   * @return Clustered PiGraph.
   */
  @Override
  public PiGraph cluster() {

    // Retrieve URC chains in input graph and verify that actors share component constraints.
    final List<List<AbstractActor>> graphURCs = new ClusteringPatternSeekerUrc(this.graph, brv).seek();
    final List<List<AbstractActor>> constrainedURCs = new LinkedList<>();
    if (!graphURCs.isEmpty()) {
      final List<AbstractActor> urc = graphURCs.get(0);// cluster one by one
      if (!ClusteringHelper.getListOfCommonComponent(urc, this.scenario).isEmpty()) {
        constrainedURCs.add(urc);
      }
    }
    // Cluster constrained URC chains.
    if (!graphURCs.isEmpty()) {
      final List<AbstractActor> urc = graphURCs.get(0);// cluster one by one
      final PiGraph subGraph = new PiSDFSubgraphBuilder(this.graph, urc, "urc_" + clusterId).build();
      brv = PiBRV.compute(graph, BRVMethod.LCM);
      // compute mapping
      final Long nPE = mapping(urc, scenario, numberOfPEs, isOnGPU, brv);
      // apply scaling
      final Long scale = computeScalingFactor(subGraph, brv.get(subGraph.getExecutableActors().get(0)), nPE, scapeMode);

      for (final InterfaceActor iActor : subGraph.getDataInterfaces()) {
        iActor.getGraphPort().setExpression(
            iActor.getGraphPort().getExpression().evaluate() * brv.get(subGraph.getExecutableActors().get(0)) / scale);
        iActor.getDataPort().setExpression(iActor.getGraphPort().getExpression().evaluate());
      }

      subGraph.setClusterValue(true);
      // Add constraints of the cluster in the scenario.
      for (final ComponentInstance component : ClusteringHelper.getListOfCommonComponent(urc, this.scenario)) {
        this.scenario.getConstraints().addConstraint(component, subGraph);
      }
    }
    // map the cluster on the CPU or GPU according to timing
    this.graph.setOnGPU(isOnGPU);
    // pseudo check consistency
    PiBRV.compute(this.graph, BRVMethod.LCM);
    return this.graph;
  }

  public static Long mapping(List<AbstractActor> urc, Scenario scenario, int numberOfPEs, Boolean isOnGPU,
      Map<AbstractVertex, Long> brv) {
    final Long timingCPU = timingCPU(urc, scenario, brv);
    final Long timingGPU = timingGPU(urc, scenario);
    if (SlamDesignPEtypeChecker.isOnlyCPU(scenario.getDesign()) || timingCPU < timingGPU) {

      return (long) numberOfPEs;
    }
    isOnGPU = true;

    return scenario.getDesign().getOperatorComponentInstances().stream()
        .filter(opId -> opId.getComponent() instanceof GPU).count();
  }

  private static Long timingCPU(List<AbstractActor> urc, Scenario scenario, Map<AbstractVertex, Long> brv) {

    final Component cpu = scenario.getDesign().getOperatorComponentInstances().stream()
        .map(ComponentInstance::getComponent).filter(component -> component instanceof CPU).findFirst().orElseThrow();
    Long sum = 0L;
    for (final AbstractActor actor : urc) {
      if (actor instanceof Actor) {
        final AbstractActor aaa = scenario.getTimings().getActorTimings().keySet().stream()
            .filter(aa -> actor.getName().equals(aa.getName())).findFirst().orElse(null);

        sum += scenario.getTimings().evaluateTimingOrDefault(aaa, cpu, TimingType.EXECUTION_TIME) * brv.get(actor);
      }
    }
    return sum;
  }

  private static Long timingGPU(List<AbstractActor> urc, Scenario scenario) {
    if (SlamDesignPEtypeChecker.isDualCPUGPU(scenario.getDesign())) {
      final GPU gpu = (GPU) scenario.getDesign().getOperatorComponentInstances().stream()
          .map(ComponentInstance::getComponent).filter(component -> component instanceof GPU).findFirst().orElseThrow();
      // If GPU parameters are different from 0, use their value, otherwise default to 1
      final Long dedicatedMemSpeed = (long) gpu.getDedicatedMemSpeed() != 0 ? (long) gpu.getDedicatedMemSpeed() : 1;
      final Long unifiedMemSpeed = (long) gpu.getUnifiedMemSpeed() != 0 ? (long) gpu.getUnifiedMemSpeed() : 1;
      final String memoryToUse = gpu.getMemoryToUse();

      Long sum = 0L;
      // build the list of PiGraph future DataPort
      final List<DataPort> dataPortList = new LinkedList<>();

      for (final AbstractActor actor : urc) {
        for (final DataPort port : actor.getAllDataPorts()) {
          // filter add port if target XOR source is in the list
          if (urc.contains(port.getFifo().getSource()) ^ urc.contains(port.getFifo().getTarget())) {
            dataPortList.add(port);
          }
        }
      }

      for (final DataPort dataPort : dataPortList) {
        final Long gpuInputSize = dataPort.getPortRateExpression().evaluate();
        double time;

        if (memoryToUse.equalsIgnoreCase("unified")) {
          time = ((double) gpuInputSize / (double) unifiedMemSpeed);
        } else {
          time = ((double) gpuInputSize / (double) dedicatedMemSpeed);
        }

        sum += (long) time;
      }

      return sum;
    }
    return Long.MAX_VALUE;
  }

  /**
   * Used to compute the scaling factor :)
   *
   * @param subGraph
   *          graph
   * @param clustredActorRepetition
   *          repetition vector coefficient of the clustered actors
   */
  public static Long computeScalingFactor(PiGraph subGraph, Long clustredActorRepetition, Long nPE,
      ScapeMode scapeMode) {

    Long scale;
    if (scapeMode == ScapeMode.DATA
        && subGraph.getDataInterfaces().stream().anyMatch(x -> x.getGraphPort().getFifo().isHasADelay())) {
      final Long ratio = computeDelayRatio(subGraph);
      scale = MathFunctionsHelper.gcd(ratio, clustredActorRepetition);
    } else {
      scale = ncDivisor(nPE, clustredActorRepetition);
    }
    if (scale == 0L) {
      scale = 1L;
    }
    return scale;
  }

  private static Long computeDelayRatio(PiGraph subGraph) {
    long count = 0L;
    for (final DataInputInterface din : subGraph.getDataInputInterfaces()) {
      if (din.getGraphPort().getFifo().isHasADelay()) {
        final long ratio = din.getGraphPort().getFifo().getDelay().getExpression().evaluate()
            / din.getGraphPort().getExpression().evaluate();
        count = Math.max(count, ratio);
      }
    }
    return count;
  }

  /**
   * Used to compute the greatest common divisor between 2 values
   *
   */
  private static Long ncDivisor(Long nC, Long n) {
    Long i;
    Long ncDivisor = 0L;
    for (i = 1L; i <= n; i++) {
      if (n % i == 0 && (i >= nC)) {
        // Sélectionne le premier diviseur qui est plus grand que nC
        ncDivisor = i;
        break; // On sort de la boucle car on a trouvé le diviseur souhaité
      }
    }
    return ncDivisor;
  }

}
