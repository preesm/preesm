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
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.LOOPSeeker;
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
public class ClusterPartitionerLOOP {

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
  private final int                       clusterId;

  /**
   * Builds a ClusterPartitioner object.
   *
   * @param graph
   *          Input graph.
   * @param scenario
   *          Workflow scenario.
   * @param numberOfPEs
   *          number of processing elements
   */
  public ClusterPartitionerLOOP(final PiGraph graph, final Scenario scenario, int numberOfPEs,
      Map<AbstractVertex, Long> brv, int clusterId) {
    this.graph = graph;
    this.scenario = scenario;
    this.numberOfPEs = numberOfPEs;
    this.brv = brv;
    this.clusterId = clusterId;
  }

  /**
   * @return Clustered PiGraph.
   */
  public PiGraph cluster() {
    // retrieve tle cycle sequence to be coarsely clustered
    final List<AbstractActor> localPluralLOOPs = new LOOPSeeker(graph, numberOfPEs, brv).pluralLocalseek();
    if (!localPluralLOOPs.isEmpty()) {
      final PiGraph subGraph = new PiSDFSubgraphBuilder(graph, localPluralLOOPs, "loop_" + clusterId).build();
      extractDelay();
      // Add constraints of the cluster in the scenario.
      subGraph.setClusterValue(true);
      for (final ComponentInstance component : ClusteringHelper.getListOfCommonComponent(localPluralLOOPs,
          this.scenario)) {
        this.scenario.getConstraints().addConstraint(component, subGraph);
      }

      return this.graph;
    }
    // retrieve the obtained or existing single local cycle to be semi-unrolled
    final List<AbstractActor> graphLocalSingleLOOPs = new LOOPSeeker(graph, numberOfPEs, brv).singleLocalseek();
    if (!graphLocalSingleLOOPs.isEmpty()) {
      // if the cycle is divisible by the number of process divide the cycle by the number of process otherwise divide
      // the cycle
      Long duplicationValue;
      if (brv.get(graphLocalSingleLOOPs.get(0)) > numberOfPEs) {
        duplicationValue = ClusterPartitionerURC.gcd((long) numberOfPEs, brv.get(graphLocalSingleLOOPs.get(0)));
      } else {
        duplicationValue = brv.get(graphLocalSingleLOOPs.get(0));
      }
      semiUnroll(graphLocalSingleLOOPs.get(0), duplicationValue, brv.get(graphLocalSingleLOOPs.get(0)));

    }

    return this.graph;
  }

  private void extractDelay() {
    // TODO Auto-generated method stub

  }

  /**
   * Unrolled LOOP pattern on the gcd above number of Processing element pipelined cluster.
   *
   * @param oEmpty
   *          loop element to be duplicate and pipelined
   * @param value
   *          highest divisor of brv(loop) just above the number of processing element
   */
  private void semiUnroll(AbstractActor loopActor, Long duplicationValue, Long originalLoopRv) {
    // duplicate actor
    final List<AbstractActor> dupActorsList = new LinkedList<>();
    for (int i = 1; i < duplicationValue; i++) {
      final AbstractActor dupActor = PiMMUserFactory.instance.copy(loopActor);
      dupActor.setName(loopActor.getName() + "_" + i);
      dupActor.setContainingGraph(loopActor.getContainingGraph());
      dupActorsList.add(dupActor);
    }
    // connect data input
    connectDuplicatedDataInputPort(loopActor, dupActorsList, duplicationValue, originalLoopRv);
    // connect data output
    connectDuplicatedDataOutputPort(loopActor, dupActorsList, duplicationValue, originalLoopRv);
    // interconnect duplicated actor on their delayed port
    for (int i = 0; i < duplicationValue - 2; i++) {
      final Fifo fd = PiMMUserFactory.instance.createFifo();
      dupActorsList.get(i).getDataOutputPorts().stream().filter(x -> x.getFifo() == null)
          .forEach(x -> x.setOutgoingFifo(fd));
      dupActorsList.get(i + 1).getDataInputPorts().stream().filter(x -> x.getFifo() == null)
          .forEach(x -> x.setIncomingFifo(fd));
      fd.setContainingGraph(loopActor.getContainingGraph());
    }
    // remove delay
    ((PiGraph) loopActor.getContainingGraph()).getDelays().stream()
        .filter(x -> x.getContainingFifo().getSourcePort() == null)
        .forEach(x -> ((PiGraph) loopActor.getContainingGraph()).removeDelay(x));
    // remove empty fifo
    ((PiGraph) loopActor.getContainingGraph()).getFifos().stream().filter(x -> x.getSourcePort() == null)
        .forEach(x -> ((PiGraph) loopActor.getContainingGraph()).removeFifo(x));
    ((PiGraph) loopActor.getContainingGraph()).getFifos().stream().filter(x -> x.getTargetPort() == null)
        .forEach(x -> ((PiGraph) loopActor.getContainingGraph()).removeFifo(x));
  }

  private void connectDuplicatedDataOutputPort(AbstractActor loopActor, List<AbstractActor> dupActorsList,
      Long duplicationValue, Long originalLoopRv) {
    for (final DataOutputPort out : loopActor.getDataOutputPorts()) {
      if (!out.getFifo().isHasADelay()) {
        final JoinActor jn = PiMMUserFactory.instance.createJoinActor();
        jn.setName("Join_" + loopActor.getName());
        jn.setContainingGraph(loopActor.getContainingGraph());

        // connect Join to dout
        final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
        dout.setName("out");
        dout.setExpression(out.getExpression().evaluate() * originalLoopRv);
        jn.getDataOutputPorts().add(dout);
        final Fifo fout = PiMMUserFactory.instance.createFifo();
        fout.setSourcePort(dout);
        fout.setTargetPort(out.getFifo().getTargetPort());
        fout.setContainingGraph(loopActor.getContainingGraph());

        // connect oEmpty_0 to Join
        final DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
        din.setName("in_0");
        final Long rateJoinIn = out.getExpression().evaluate() * originalLoopRv / duplicationValue;
        din.setExpression(rateJoinIn);
        jn.getDataInputPorts().add(din);
        final Fifo fin = PiMMUserFactory.instance.createFifo();
        fin.setSourcePort(out);
        fin.setTargetPort(din);
        fin.setContainingGraph(loopActor.getContainingGraph());

        // connect duplicated actors to Join
        for (int i = 1; i < duplicationValue; i++) {
          final DataInputPort dinn = PiMMUserFactory.instance.createDataInputPort();
          dinn.setName("in_" + i);
          dinn.setExpression(rateJoinIn);
          jn.getDataInputPorts().add(dinn);
          final Fifo finn = PiMMUserFactory.instance.createFifo();
          finn.setTargetPort(dinn);
          finn.setContainingGraph(loopActor.getContainingGraph());
          dupActorsList.get(i - 1).getDataOutputPorts().stream().filter(x -> x.getName().equals(out.getName()))
              .forEach(x -> x.setOutgoingFifo(finn));

        }
      } else {

        final Fifo fdout = PiMMUserFactory.instance.createFifo();
        dupActorsList.get((int) (duplicationValue - 2)).getDataOutputPorts().stream().filter(x -> x.getFifo() == null)
            .forEach(x -> x.setOutgoingFifo(fdout));
        fdout.setTargetPort(out.getFifo().getDelay().getGetterPort());
        fdout.setContainingGraph(loopActor.getContainingGraph());

        // connect oEmpty delayed output to 1st duplicated actor
        final Fifo fdin = PiMMUserFactory.instance.createFifo();
        fdin.setSourcePort(out);
        dupActorsList.get(0).getDataInputPorts().stream().filter(x -> x.getFifo() == null)
            .forEach(x -> x.setIncomingFifo(fdin));
        fdin.setContainingGraph(loopActor.getContainingGraph());

      }
    }
  }

  private void connectDuplicatedDataInputPort(AbstractActor loopActor, List<AbstractActor> dupActorsList,
      Long duplicationValue, Long originalLoopRv) {
    for (final DataInputPort in : loopActor.getDataInputPorts()) {
      if (!in.getFifo().isHasADelay()) {
        final ForkActor frk = PiMMUserFactory.instance.createForkActor();
        frk.setName("Fork_" + loopActor.getName());
        frk.setContainingGraph(loopActor.getContainingGraph());

        // connect din to frk
        final DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
        din.setName("in");
        din.setExpression(in.getExpression().evaluate() * originalLoopRv);

        frk.getDataInputPorts().add(din);
        final Fifo fin = PiMMUserFactory.instance.createFifo();
        fin.setType(in.getFifo().getType());
        fin.setSourcePort(in.getFifo().getSourcePort());
        fin.setTargetPort(din);

        fin.setContainingGraph(loopActor.getContainingGraph());

        // connect fork to oEmpty_0
        final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
        dout.setName("out_0");
        final Long rateForkOut = in.getExpression().evaluate() * originalLoopRv / duplicationValue;
        dout.setExpression(rateForkOut);
        frk.getDataOutputPorts().add(dout);
        final Fifo fout = PiMMUserFactory.instance.createFifo();
        fout.setType(in.getFifo().getType());
        fout.setSourcePort(dout);
        fout.setTargetPort(in);
        fout.setContainingGraph(loopActor.getContainingGraph());
        // remove extra fifo --> non en fait c'est bon

        // connect fork to duplicated actors
        for (int i = 1; i < duplicationValue; i++) {
          final DataOutputPort doutn = PiMMUserFactory.instance.createDataOutputPort();
          doutn.setName("out_" + i);
          doutn.setExpression(rateForkOut);
          frk.getDataOutputPorts().add(doutn);
          final Fifo foutn = PiMMUserFactory.instance.createFifo();
          foutn.setType(in.getFifo().getType());
          foutn.setSourcePort(doutn);
          foutn.setContainingGraph(loopActor.getContainingGraph());
          dupActorsList.get(i - 1).getDataInputPorts().stream().filter(x -> x.getName().equals(in.getName()))
              .forEach(x -> x.setIncomingFifo(foutn));
        }
      } else {
        final Fifo fd = PiMMUserFactory.instance.createFifo();
        fd.setSourcePort(in.getFifo().getDelay().getSetterPort());
        fd.setTargetPort(in);
        fd.setContainingGraph(loopActor.getContainingGraph());

      }
    }
  }
}
