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
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.LOOP2Seeker;
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
public class ClusterPartitionerLOOP2 {

  /**
   * Input graph.
   */
  private final PiGraph  graph;
  /**
   * Workflow scenario.
   */
  private final Scenario scenario;

  /**
   * Builds a ClusterPartitioner object.
   *
   * @param graph
   *          Input graph.
   * @param scenario
   *          Workflow scenario.
   */
  public ClusterPartitionerLOOP2(final PiGraph graph, final Scenario scenario) {
    this.graph = graph;
    this.scenario = scenario;
  }

  /**
   * @return Clustered PiGraph.
   */
  public PiGraph cluster() {
    PiGraph loopGraph = this.graph;
    boolean flag = true;
    long index = 0;
    while (loopGraph.getDelayIndex() > 1 || flag) {
      flag = false;
      // Retrieve LOOP chains in input graph and verify that will not induce conflict loop.
      final List<AbstractActor> graphLOOP2s = new LOOP2Seeker(loopGraph).seek();
      if (graphLOOP2s != null) {
        final PiGraph subGraph = new PiSDFSubgraphBuilder(loopGraph, graphLOOP2s, "loop_" + index++).buildSRV();

        // identify the delay to extract
        final List<Delay> d = new LinkedList<>();

        for (final Delay ds : subGraph.getDelays()) {
          if (ds.getContainingFifo().getTarget() == graphLOOP2s.get(0)) {
            d.add(ds);
          }
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
                  // DataOutputPort outputPort = PiMMUserFactory.instance.createDataOutputPort();
                  din.getDataOutputPorts().get(0).setName(subGraph.getName() + d.indexOf(retainedDelay) + "_in");
                  // outputPort.setName(subgraph.getName() + "_in_0");
                  // din.getDataOutputPorts().add(outputPort);
                  din.getDataOutputPorts().get(0).setExpression(p.getFifo().getTargetPort().getExpression().evaluate());
                  final Fifo f = PiMMUserFactory.instance.createFifo();
                  f.setType(p.getFifo().getType());
                  f.setSourcePort(din.getDataOutputPorts().get(0));
                  f.setTargetPort(p);
                  f.setContainingGraph(subGraph);
                  // saveExpression = p.getFifo().getTargetPort().getExpression().evaluate()
                  // p.getFifo().setSourcePort(outputPort);
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
                  // DataInputPort inputPort = PiMMUserFactory.instance.createDataInputPort();
                  dout.getDataInputPorts().get(0).setName(subGraph.getName() + d.indexOf(retainedDelay) + "_out");
                  // inputPort.setName(subgraph.getName() + "_out_0");
                  // dout.getDataInputPorts().add(inputPort);
                  dout.getDataInputPorts().get(0).setExpression(p.getFifo().getSourcePort().getExpression().evaluate());
                  final Fifo f = PiMMUserFactory.instance.createFifo();
                  f.setType(p.getFifo().getType());
                  f.setSourcePort(p);
                  f.setTargetPort(dout.getDataInputPorts().get(0));
                  f.setContainingGraph(subGraph);
                  // p.getFifo().setTargetPort(inputPort);
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
          // Add constraints of the cluster in the scenario.
          subGraph.setClusterValue(true);
          for (final ComponentInstance component : ClusteringHelper.getListOfCommonComponent(graphLOOP2s,
              this.scenario)) {
            this.scenario.getConstraints().addConstraint(component, subGraph);
          }

          loopGraph = subGraph;
          final int i = 0;// sinon passe pas dans subgraph
        }
      }
    }
    // for (PiGraph g : graph.getAllChildrenGraphs())
    // g.setClusterValue(true);
    return this.graph;
  }
}
