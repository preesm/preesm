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
import java.util.logging.Level;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.math.MathFunctionsHelper;
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
 *
 */
public class ClusterPartitioner {

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
  public ClusterPartitioner(final PiGraph graph, final Scenario scenario, final int numberOfPEs,
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

    // TODO: Look for actor groups other than URC chains.
    // Retrieve URC chains in input graph and verify that actors share component constraints.
    final List<List<AbstractActor>> graphURCs = new URCSeeker(this.graph, this.nonClusterableList, this.brv).seek();
    final List<List<AbstractActor>> constrainedURCs = new LinkedList<>();
    if (!graphURCs.isEmpty()) {
      final List<AbstractActor> URC = graphURCs.get(0);// cluster one by one

      // for (List<AbstractActor> URC : graphURCs) {
      if (!ClusteringHelper.getListOfCommonComponent(URC, this.scenario).isEmpty()) {
        constrainedURCs.add(URC);
      }
    }

    // Cluster constrained URC chains.
    final List<PiGraph> subGraphs = new LinkedList<>();
    if (!graphURCs.isEmpty()) {
      final List<AbstractActor> URC = graphURCs.get(0);// cluster one by one
      final PiGraph subGraph = new PiSDFSubgraphBuilder(this.graph, URC, "urc_" + clusterId).build();

      // identify the delay to extract
      final List<Delay> d = new LinkedList<>();

      for (final Delay ds : subGraph.getDelays()) {
        d.add(ds);
      }
      for (final Delay retainedDelay : d) {
        // remove delay from the subgraph + add it to the upper graph
        subGraph.getContainingPiGraph().addDelay(retainedDelay);
        final Long saveExpression = 0L;
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

      subGraph.setClusterValue(true);
      // Add constraints of the cluster in the scenario.
      for (final ComponentInstance component : ClusteringHelper.getListOfCommonComponent(URC, this.scenario)) {
        this.scenario.getConstraints().addConstraint(component, subGraph);
      }
      subGraphs.add(subGraph);
    }

    // Compute BRV and balance actor firings between coarse and fine-grained parallelism.
    final Map<AbstractVertex, Long> brv = PiBRV.compute(this.graph, BRVMethod.LCM);
    for (final PiGraph subgraph : subGraphs) {
      final long factor = MathFunctionsHelper.gcd(brv.get(subgraph), this.numberOfPEs);
      final String message = String.format("%1$s: firings balanced by %3$d, leaving %2$d firings at coarse-grained.",
          subgraph.getName(), brv.get(subgraph) / factor, factor);
      PreesmLogger.getLogger().log(Level.INFO, message);
      // new PiGraphFiringBalancer(subgraph, factor).balance();
    }

    return this.graph;
  }

}
