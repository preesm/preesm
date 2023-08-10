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
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.PiSDFSubgraphBuilder;
import org.preesm.model.pisdf.util.URCSeeker;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;

/**
 * This class provide an algorithm to cluster a PiSDF graph and balance actor firings of clustered actor between coarse
 * and fine-grained parallelism. Resulting clusters are marked as PiSDF cluster, they have to be schedule with the
 * Cluster Scheduler.
 *
 * @author dgageot
 * @author orenaud
 *
 */
public class ClusterPartitionerURC {

  /**
   * Input graph.
   */
  private final PiGraph                   graph;
  /**
   * Workflow scenario.
   */
  private final Scenario                  scenario;
  /**
   * Number of PEs in compute clusters.
   */
  private final int                       numberOfPEs;
  private final Map<AbstractVertex, Long> brv;
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
   */
  public ClusterPartitionerURC(final PiGraph graph, final Scenario scenario, final int numberOfPEs,
      Map<AbstractVertex, Long> brv, int clusterId, List<AbstractActor> nonClusterableList) {
    this.graph = graph;
    this.scenario = scenario;
    this.numberOfPEs = numberOfPEs;
    this.brv = brv;
    this.clusterId = clusterId;
    this.nonClusterableList = nonClusterableList;
  }

  /**
   * @return Clustered PiGraph.
   */
  public PiGraph cluster() {

    // Retrieve URC chains in input graph and verify that actors share component constraints.
    final List<List<AbstractActor>> graphURCs = new URCSeeker(this.graph, this.nonClusterableList, this.brv).seek();
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

      // identify the delay to extract
      final List<Delay> d = new LinkedList<>();

      for (final Delay ds : subGraph.getDelays()) {
        d.add(ds);
      }
      for (final Delay retainedDelay : d) {
        // remove delay from the subgraph + add it to the upper graph
        subGraph.getContainingPiGraph().addDelay(retainedDelay);
        // replace delay connection by interface
        for (final AbstractActor a : subGraph.getExecutableActors()) {
          for (final DataInputPort p : a.getDataInputPorts()) {
            if (p.getFifo().isHasADelay()) {
              if (p.getFifo().getDelay().equals(retainedDelay)) {
                final DataInputInterface din = PiMMUserFactory.instance.createDataInputInterface();
                din.setName(subGraph.getName() + d.indexOf(retainedDelay) + "_in");
                din.getDataOutputPorts().get(0).setName(subGraph.getName() + d.indexOf(retainedDelay) + "_in");
                din.getDataOutputPorts().get(0).setExpression(p.getFifo().getTargetPort().getExpression().evaluate());
                final Fifo f = PiMMUserFactory.instance.createFifo();
                f.setType(p.getFifo().getType());
                f.setSourcePort(din.getDataOutputPorts().get(0));
                f.setTargetPort(p);
                f.setContainingGraph(subGraph);
                din.setContainingGraph(subGraph);
                // connect interfaces to delay
                din.getGraphPort().setExpression(p.getFifo().getTargetPort().getExpression().evaluate());
                retainedDelay.getContainingFifo().setTargetPort((DataInputPort) din.getGraphPort());
              }
            }
          }
          for (final DataOutputPort p : a.getDataOutputPorts()) {
            if (p.getFifo().isHasADelay()) {
              if (p.getFifo().getDelay().equals(retainedDelay)) {
                final DataOutputInterface dout = PiMMUserFactory.instance.createDataOutputInterface();
                dout.setName(subGraph.getName() + d.indexOf(retainedDelay) + "_out");
                dout.getDataInputPorts().get(0).setName(subGraph.getName() + d.indexOf(retainedDelay) + "_out");
                dout.getDataInputPorts().get(0).setExpression(p.getFifo().getSourcePort().getExpression().evaluate());
                final Fifo f = PiMMUserFactory.instance.createFifo();
                f.setType(p.getFifo().getType());
                f.setSourcePort(p);
                f.setTargetPort(dout.getDataInputPorts().get(0));
                f.setContainingGraph(subGraph);
                dout.setContainingGraph(subGraph);
                // connect interfaces to delay
                dout.getGraphPort().setExpression(p.getFifo().getSourcePort().getExpression().evaluate());
                retainedDelay.getContainingFifo().setSourcePort((DataOutputPort) dout.getGraphPort());

              }
            }
          }

        }
        retainedDelay.getContainingFifo().setContainingGraph(subGraph.getContainingPiGraph());//
        // case getter setter
        if (retainedDelay.hasSetterActor()) {
          retainedDelay.getSetterActor().setContainingGraph(subGraph.getContainingPiGraph());
        }
        if (retainedDelay.hasGetterActor()) {
          retainedDelay.getGetterActor().setContainingGraph(subGraph.getContainingPiGraph());
        }
      }
      // apply scaling
      final Long scale = computeScalingFactor(subGraph);
      for (final DataInputInterface din : subGraph.getDataInputInterfaces()) {
        din.getGraphPort().setExpression(
            din.getGraphPort().getExpression().evaluate() * brv.get(subGraph.getExecutableActors().get(0)) / scale);// scale
        din.getDataPort().setExpression(din.getGraphPort().getExpression().evaluate());
      }
      for (final DataOutputInterface dout : subGraph.getDataOutputInterfaces()) {
        dout.getGraphPort().setExpression(
            dout.getGraphPort().getExpression().evaluate() * brv.get(subGraph.getExecutableActors().get(0)) / scale);
        dout.getDataPort().setExpression(dout.getGraphPort().getExpression().evaluate());
      }

      subGraph.setClusterValue(true);
      // Add constraints of the cluster in the scenario.
      for (final ComponentInstance component : ClusteringHelper.getListOfCommonComponent(urc, this.scenario)) {
        this.scenario.getConstraints().addConstraint(component, subGraph);
      }
    }
    // Compute BRV and balance actor firings between coarse and fine-grained parallelism.
    PiBRV.compute(this.graph, BRVMethod.LCM);
    return this.graph;
  }

  /**
   * Used to compute the scaling factor :)
   *
   * @param subGraph
   *          graph
   */
  private Long computeScalingFactor(PiGraph subGraph) {
    final Map<AbstractVertex, Long> rv = PiBRV.compute(subGraph, BRVMethod.LCM);
    final Long[] numbers = rv.values().toArray(new Long[rv.size()]);
    Long scale;
    if (subGraph.getDataInputInterfaces().stream().anyMatch(x -> x.getGraphPort().getFifo().isHasADelay())
        && subGraph.getDataOutputInterfaces().stream().anyMatch(x -> x.getGraphPort().getFifo().isHasADelay())) {
      final Long ratio = computeDelayRatio(subGraph);
      scale = gcdSuite(ratio, numbers);
    } else {
      scale = ncDivisor((long) numberOfPEs, numbers);
    }
    if (scale == 0L) {
      scale = 1L;
    }
    return scale;
  }

  private Long computeDelayRatio(PiGraph subGraph) {
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
  private Long gcd(Long a, Long b) {
    while (b != 0L) {
      final Long temp = b;
      b = a % b;
      a = temp;
    }
    return a;
  }

  /**
   * Function to calculate the GCD of a list of numbers
   *
   * @param nC
   *          number of Processing Element
   * @param number
   *          List of number
   */
  private Long gcdSuite(Long nC, Long[] numbers) {
    if (numbers.length != 0) {
      Long pgcd = nC;
      for (final Long num : numbers) {
        pgcd = gcd(pgcd, num);
      }
      return pgcd;
    }
    return 1L;
  }

  private Long ncDivisor(Long nc, Long[] numbers) {
    Long gcd = numbers[0];

    for (int i = 1; i < numbers.length; i++) {
      gcd = gcd(gcd, numbers[i]);
    }
    Long multiple = 1L;
    if (gcd > nc) {
      multiple = (long) Math.ceil((double) nc / gcd);
    }

    return gcd * multiple;
  }

}
