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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
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

  private final Map<AbstractVertex, Long> brv;
  private final int                       clusterId;
  private final ScapeMode                 scapeMode;
  private final Boolean                   memoryOptim;

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

      // compute mapping
      final Object[] result = mapping(urc, scenario, numberOfPEs, brv);
      final Long nPE = (Long) result[0];
      final Boolean isOnGPU = (Boolean) result[1];
      // apply scaling
      final Long scale = computeScalingFactor(subGraph, brv.get(subGraph.getExecutableActors().get(0)), nPE, scapeMode);

      for (final InterfaceActor iActor : subGraph.getDataInterfaces()) {
        iActor.getGraphPort().setExpression(
            iActor.getGraphPort().getExpression().evaluate() * brv.get(subGraph.getExecutableActors().get(0)) / scale);
        iActor.getDataPort().setExpression(iActor.getGraphPort().getExpression().evaluate());

      }

      if (memoryOptim.equals(Boolean.TRUE)) {
        // reduce memory exclusion graph matches
        reduceMemExMatches(subGraph, scale, brv);

        subGraph.getFifos().stream().filter(x -> x.getSourcePort() == null).forEach(subGraph::removeFifo);
        subGraph.getFifos().stream().filter(x -> x.getTargetPort() == null).forEach(subGraph::removeFifo);

        // pseudo merge redundant FIFO
        mergeFIFO(subGraph);
        // subGraph.getAllDataPorts().forEach(port -> System.out.println(port.getName()));

      }

      // remove empty introduced FIFO
      subGraph.getFifos().stream().filter(x -> x.getSourcePort() == null).forEach(subGraph::removeFifo);
      subGraph.getFifos().stream().filter(x -> x.getTargetPort() == null).forEach(subGraph::removeFifo);

      subGraph.setClusterValue(true);
      // Add constraints of the cluster in the scenario.
      for (final ComponentInstance component : ClusteringHelper.getListOfCommonComponent(urc, this.scenario)) {
        this.scenario.getConstraints().addConstraint(component, subGraph);
      }
      // map the cluster on the CPU or GPU according to timing
      subGraph.setOnGPU(isOnGPU);
      PreesmLogger.getLogger().log(Level.INFO, "subgraph: " + subGraph.getName() + " is on GPU: " + subGraph.isOnGPU());
    }

    return this.graph;
  }

  private void mergeFIFO(PiGraph subGraph) {
    final Map<BroadcastActor, List<InterfaceActor>> sourceMap = redundantFIFOMap(subGraph);

    // check matching opportunities
    for (final Entry<BroadcastActor, List<InterfaceActor>> map : sourceMap.entrySet()) {
      // merge redundant buffer when they are more than 2
      if (map.getValue().size() > 1) {
        final DataPort sourcePort = map.getValue().get(0).getDataPort();
        final DataPort targetPort0 = map.getValue().get(0).getDataPort().getFifo().getTargetPort();
        // add a new broadcast inside cluster to retrieve the scaling
        final BroadcastActor brd = PiMMUserFactory.instance.createBroadcastActor();
        brd.setName("Broadcast_Merge_" + map.getKey().getName());
        brd.setContainingGraph(subGraph);
        // connect interface to the new broadcast
        final DataInputPort dataInputPort = PiMMUserFactory.instance.createDataInputPort();
        dataInputPort.setName("in");
        final Long dt = map.getValue().get(0).getDataPort().getExpression().evaluate();
        dataInputPort.setExpression(dt);
        brd.getDataInputPorts().add(dataInputPort);
        final Fifo fin = PiMMUserFactory.instance.createFifo();
        fin.setType(sourcePort.getFifo().getType());
        fin.setSourcePort((DataOutputPort) sourcePort);
        fin.setTargetPort(dataInputPort);
        fin.setContainingGraph(subGraph);
        // connect broadcast to former targets
        for (final InterfaceActor iActor : map.getValue()) {
          final DataOutputPort dataOutputPort;
          if (iActor.equals(map.getValue().get(0))) {
            dataOutputPort = PiMMUserFactory.instance.copy(iActor.getDataOutputPorts().get(0));
            final Fifo fout = PiMMUserFactory.instance.createFifo();
            fout.setType(fin.getType());
            fout.setSourcePort(dataOutputPort);
            fout.setTargetPort((DataInputPort) targetPort0);
            fout.setContainingGraph(subGraph);
          } else {
            dataOutputPort = iActor.getDataOutputPorts().get(0);
          }
          brd.getDataOutputPorts().add(dataOutputPort);

          // remove FIFO connecting the former redundant interface
          if (!iActor.equals(map.getValue().get(0))) {
            final Fifo oldFifo = iActor.getGraphPort().getFifo();
            final DataOutputPort oldPort = iActor.getGraphPort().getFifo().getSourcePort();
            ((AbstractActor) iActor.getGraphPort().getFifo().getSource()).getDataOutputPorts().remove(oldPort);
            iActor.getGraphPort().getContainingActor().getContainingPiGraph().removeFifo(oldFifo);
          }
        }
        // remove former redundant interface
        subGraph.getDataInputInterfaces().stream().filter(x -> x.getDataOutputPorts().isEmpty())
            .forEach(subGraph::removeActor);
      }

    }

    subGraph.getAllDataPorts().forEach(port -> port.setName(port.getName().substring(port.getName().indexOf('_') + 1)));
    subGraph.getDataInterfaces()
        .forEach(port -> port.setName(port.getName().substring(port.getName().indexOf('_') + 1)));
  }

  private Map<BroadcastActor, List<InterfaceActor>> redundantFIFOMap(PiGraph subGraph) {
    final Map<BroadcastActor, List<InterfaceActor>> sourceMap = new HashMap<>();
    // Identify the mergeable buffer
    for (final InterfaceActor iActor : subGraph.getDataInterfaces()) {
      if (iActor.getGraphPort().getFifo().getSource() instanceof final BroadcastActor broadcast) {
        // Check if the map already contains the broadcast actor
        if (sourceMap.containsKey(broadcast)) {
          // Add the InterfaceActor to the existing list
          sourceMap.get(broadcast).add(iActor);
        } else {
          // Create a new list and add the InterfaceActor
          final List<InterfaceActor> list = new ArrayList<>();
          list.add(iActor);
          // Put the new list in the map
          sourceMap.put(broadcast, list);
        }
      }
    }
    return sourceMap;
  }

  public static void reduceMemExMatches(PiGraph subGraph, Long scale, Map<AbstractVertex, Long> rv) {
    for (final InterfaceActor iActor : subGraph.getDataInterfaces()) {
      if (iActor.getGraphPort().getFifo().getSource() instanceof final BroadcastActor broadcast) {
        final Long brdInput = broadcast.getDataInputPorts().get(0).getExpression().evaluate();
        final long interfaceRate = iActor.getGraphPort().getExpression().evaluate();
        final DataInputPort targetPort = iActor.getDataPort().getFifo().getTargetPort();
        // set output broadcast expression
        iActor.getGraphPort().getFifo().getSourcePort().setExpression(brdInput * scale);
        // scale cluster in put on broadcast new value
        iActor.getGraphPort().setExpression(brdInput * rv.get(broadcast));
        iActor.getDataPort().setExpression(brdInput * rv.get(broadcast));

        // add a new broadcast inside cluster to retrieve the scaling
        final BroadcastActor brd = PiMMUserFactory.instance.createBroadcastActor();
        brd.setName("Broadcast_" + broadcast.getName());
        brd.setContainingGraph(subGraph);
        // connect interface to the new broadcast
        final DataInputPort dataInputPort = PiMMUserFactory.instance.createDataInputPort();
        dataInputPort.setName("in");
        final Long dt = brdInput * rv.get(broadcast);
        dataInputPort.setExpression(dt);
        brd.getDataInputPorts().add(dataInputPort);
        final Fifo fin = PiMMUserFactory.instance.createFifo();
        fin.setType(iActor.getDataPort().getFifo().getType());
        fin.setSourcePort(iActor.getDataPort().getFifo().getSourcePort());
        fin.setTargetPort(dataInputPort);
        fin.setContainingGraph(subGraph);
        // connect broadcast to target
        final DataOutputPort dataOutputPort = PiMMUserFactory.instance.createDataOutputPort();
        dataOutputPort.setName("out");
        final Long rt = interfaceRate;
        dataOutputPort.setExpression(rt);
        brd.getDataOutputPorts().add(dataOutputPort);
        final Fifo fout = PiMMUserFactory.instance.createFifo();
        fout.setType(iActor.getDataPort().getFifo().getType());
        fout.setSourcePort(dataOutputPort);
        fout.setTargetPort(targetPort);
        fout.setContainingGraph(subGraph);

      }

    }

  }

  public static Object[] mapping(List<AbstractActor> urc, Scenario scenario, int numberOfPEs,
      Map<AbstractVertex, Long> brv) {
    final Component cpu = scenario.getDesign().getOperatorComponentInstances().stream()
        .map(ComponentInstance::getComponent).filter(component -> component instanceof CPU).findFirst().orElseThrow();
    final Long timingCPU = timingCPU(urc, scenario, brv, cpu);
    final Long timingGPU = timingGPU(urc, scenario);

    // EWEN MODIFS
    boolean isGPUPossible = false;
    for (final AbstractActor act : urc) {
      for (final ComponentInstance comp : scenario.getPossibleMappings(act)) {
        if (comp.getInstanceName().equals("GPU")) {
          isGPUPossible = true;
        }
      }
    }

    if (timingCPU > timingGPU && isGPUPossible) {
      for (final AbstractActor act : urc) {
        for (final ComponentInstance comp : scenario.getPossibleMappings(act)) {
          if (comp.getInstanceName().equals("GPU")) {
            act.setOnGPU(true);
          }
        }
      }
    }
    // END EWEN MODIFS

    if (SlamDesignPEtypeChecker.isOnlyCPU(scenario.getDesign()) || timingCPU < timingGPU || !isGPUPossible) {

      return new Object[] { (long) numberOfPEs, Boolean.FALSE };
    }

    final Long gpuCount = scenario.getDesign().getOperatorComponentInstances().stream()
        .filter(opId -> opId.getComponent() instanceof GPU).count();
    return new Object[] { gpuCount, Boolean.TRUE };
  }

  public static Long timingCPU(List<AbstractActor> urc, Scenario scenario, Map<AbstractVertex, Long> brv,
      Component cpu) {

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

  public static Long timingGPU(List<AbstractActor> urc, Scenario scenario) {
    if (SlamDesignPEtypeChecker.isDualCPUGPU(scenario.getDesign())) {
      final GPU gpu = (GPU) scenario.getDesign().getOperatorComponentInstances().stream()
          .map(ComponentInstance::getComponent).filter(component -> component instanceof GPU).findFirst().orElseThrow();

      final Long offloading = offLoadingCost(gpu, urc);
      final Long timing = urc.stream().filter(actor -> actor instanceof Actor).mapToLong(actor -> {
        final AbstractActor aaa = scenario.getTimings().getActorTimings().keySet().stream()
            .filter(aa -> actor.getName().equals(aa.getName())).findFirst().orElse(null);

        return scenario.getTimings().evaluateTimingOrDefault(aaa, gpu, TimingType.EXECUTION_TIME);
      }).sum();
      return offloading + timing;

    }
    return Long.MAX_VALUE;
  }

  private static Long offLoadingCost(GPU gpu, List<AbstractActor> urc) {
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
