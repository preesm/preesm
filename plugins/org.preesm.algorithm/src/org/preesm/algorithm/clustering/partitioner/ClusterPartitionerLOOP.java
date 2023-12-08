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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.PersistenceLevel;
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
public class ClusterPartitionerLOOP extends ClusterPartitioner {

  private final Map<AbstractVertex, Long> brv;
  private int                             clusterId;

  private static final String LOOP_PREFIX = "loop_";

  // Needs to be declare as a member to be used and modified in lambda
  private int pipelineStage;

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
    super(scenario.getAlgorithm(), scenario, numberOfPEs);
    this.brv = brv;
    this.clusterId = clusterId;
  }

  /**
   * @return Clustered PiGraph.
   */
  @Override
  public PiGraph cluster() {
    // retrieve tle cycle sequence to be coarsely clustered
    final List<AbstractActor> localPluralLOOPs = new LOOPSeeker(graph, numberOfPEs, brv).pluralLocalseek();
    if (!localPluralLOOPs.isEmpty()) {
      final PiGraph subGraph = new PiSDFSubgraphBuilder(graph, localPluralLOOPs, LOOP_PREFIX + clusterId).build();
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
        duplicationValue = MathFunctionsHelper.gcd(numberOfPEs, brv.get(graphLocalSingleLOOPs.get(0)));
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

    final PiGraph containingGraph = loopActor.getContainingPiGraph();

    // duplicate actor
    final List<AbstractActor> dupActorsList = new LinkedList<>();
    for (int i = 1; i < duplicationValue; i++) {
      final AbstractActor dupActor = PiMMUserFactory.instance.copy(loopActor);
      dupActor.setName(loopActor.getName() + "_" + i);
      dupActor.setContainingGraph(containingGraph);
      dupActorsList.add(dupActor);
    }
    // connect data input
    connectDuplicatedDataInputPort(loopActor, dupActorsList, duplicationValue, originalLoopRv);
    // connect data output
    connectDuplicatedDataOutputPort(loopActor, dupActorsList, duplicationValue, originalLoopRv);
    // connect configuration input
    connectDuplicatedConfigInpuPort(loopActor, dupActorsList, duplicationValue);

    // remove empty fifo
    containingGraph.getFifos().stream().filter(x -> x.getSourcePort() == null).forEach(containingGraph::removeFifo);
    containingGraph.getFifos().stream().filter(x -> x.getTargetPort() == null).forEach(containingGraph::removeFifo);

    final List<PiGraph> pipList = new LinkedList<>();
    // generate subgraphs
    final List<AbstractActor> subloop = Collections.singletonList(loopActor);
    final PiGraph subGraph = new PiSDFSubgraphBuilder(graph, subloop, LOOP_PREFIX + clusterId).build();
    pipList.add(subGraph);
    // Add constraints of the cluster in the scenario.
    subGraph.setClusterValue(true);

    ClusteringHelper.getListOfCommonComponent(subloop, this.scenario)
        .forEach(c -> this.scenario.getConstraints().addConstraint(c, subGraph));

    clusterId++;
    for (final AbstractActor dupActor : dupActorsList) {
      final List<AbstractActor> subloopCopy = new LinkedList<>();
      subloopCopy.add(dupActor);
      final PiGraph subGraphCopy = new PiSDFSubgraphBuilder(graph, subloopCopy, LOOP_PREFIX + clusterId).build();
      pipList.add(subGraphCopy);
      // Add constraints of the cluster in the scenario.
      subGraph.setClusterValue(true);

      ClusteringHelper.getListOfCommonComponent(subloop, this.scenario)
          .forEach(c -> this.scenario.getConstraints().addConstraint(c, subGraph));

      clusterId++;
    }

    // Scale and pipeline each loop
    for (final PiGraph sub : pipList) {
      for (final InterfaceActor iActor : sub.getDataInterfaces()) {
        if (!iActor.getDataPort().getFifo().isHasADelay()) {
          Long scale;
          if (iActor instanceof DataInputInterface) {
            scale = iActor.getGraphPort().getFifo().getSourcePort().getExpression().evaluate();
          } else {
            scale = iActor.getGraphPort().getFifo().getTargetPort().getExpression().evaluate();
          }
          iActor.getGraphPort().setExpression(scale);
          iActor.getDataPort().setExpression(iActor.getGraphPort().getExpression().evaluate());
        }
      }
    }

    pipelineStage = 1;
    pipList.forEach(sub -> {
      sub.getDataOutputInterfaces()
          .forEach(doi -> createPipeline(sub, doi.getDataPort(), doi.getGraphPort(), pipelineStage));
      pipelineStage++;
    });

  }

  /**
   * Add delay between each loop actor in order to create pipeline stage. if the output is not a loop output, then the
   * delay must contain as many initial tokens as there are stages before the pipeline output. Otherwise the number of
   * initial token to store equals the loop delay.
   *
   * @param sub
   *          the subgraph as actor
   * @param dataPort
   *          the inner subgraph port
   * @param graphPort
   *          the outer subgraph port
   * @param pipelineStage
   *          the number of stage already pipelined
   *
   */
  private void createPipeline(PiGraph sub, DataPort dataPort, DataPort graphPort, int pipelineStage) {
    final Delay pipDelay = PiMMUserFactory.instance.createDelay();
    pipDelay.setName(graphPort.getContainingActor().getName() + "." + graphPort.getName() + "_"
        + graphPort.getContainingActor().getName() + "." + graphPort.getFifo().getSourcePort().getName());
    pipDelay.setContainingGraph(sub.getContainingPiGraph());
    pipDelay.setLevel(PersistenceLevel.PERMANENT);
    if (dataPort.getFifo().getSourcePort().getContainingActor() instanceof DelayActor) {
      pipDelay.setExpression(dataPort.getExpression().evaluate());
    } else {
      pipDelay.setExpression(dataPort.getExpression().evaluate() * pipelineStage);
    }
    pipDelay.getActor().setContainingGraph(sub.getContainingPiGraph());
    graphPort.getFifo().setDelay(pipDelay);
  }

  private void connectDuplicatedConfigInpuPort(AbstractActor loopActor, List<AbstractActor> dupActorsList,
      Long duplicationValue) {
    for (final ConfigInputPort cfg : loopActor.getConfigInputPorts()) {
      for (int i = 1; i < duplicationValue; i++) {
        dupActorsList.get(i - 1).getConfigInputPorts().stream().filter(x -> x.getName().equals(cfg.getName()))
            .forEach(x -> PiMMUserFactory.instance.createDependency(cfg.getIncomingDependency().getSetter(), x));
        dupActorsList.get(i - 1).getConfigInputPorts().stream().filter(x -> x.getName().equals(cfg.getName())).forEach(
            x -> x.getIncomingDependency().setContainingGraph(cfg.getIncomingDependency().getContainingPiGraph()));
      }
    }
  }

  private void connectDuplicatedDataOutputPort(AbstractActor loopActor, List<AbstractActor> dupActorsList,
      Long duplicationValue, Long originalLoopRv) {
    int index = 0;
    for (final DataOutputPort out : loopActor.getDataOutputPorts()) {
      if (!out.getFifo().isHasADelay()) {
        final JoinActor jn = PiMMUserFactory.instance.createJoinActor();
        jn.setName("Join_loop_" + loopActor.getName() + index);
        jn.setContainingGraph(loopActor.getContainingPiGraph());

        // connect Join to dout
        final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
        dout.setName("out");
        dout.setExpression(out.getExpression().evaluate() * originalLoopRv);
        jn.getDataOutputPorts().add(dout);
        final Fifo fout = PiMMUserFactory.instance.createFifo();
        fout.setType(out.getFifo().getType());
        fout.setSourcePort(dout);
        fout.setTargetPort(out.getFifo().getTargetPort());
        fout.setContainingGraph(loopActor.getContainingPiGraph());

        // connect oEmpty_0 to Join
        final DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
        din.setName("in_0");
        final Long rateJoinIn = out.getExpression().evaluate() * originalLoopRv / duplicationValue;
        din.setExpression(rateJoinIn);
        jn.getDataInputPorts().add(din);
        final Fifo fin = PiMMUserFactory.instance.createFifo();
        fin.setSourcePort(out);
        fin.setTargetPort(din);
        fin.setContainingGraph(loopActor.getContainingPiGraph());
        out.getFifo().setType(fout.getType());

        // connect duplicated actors to Join
        for (int i = 1; i < duplicationValue; i++) {
          final DataInputPort dinn = PiMMUserFactory.instance.createDataInputPort();
          dinn.setName("in_" + i);
          dinn.setExpression(rateJoinIn);
          jn.getDataInputPorts().add(dinn);
          final Fifo finn = PiMMUserFactory.instance.createFifo();
          finn.setType(fout.getType());
          finn.setTargetPort(dinn);
          finn.setContainingGraph(loopActor.getContainingPiGraph());
          dupActorsList.get(i - 1).getDataOutputPorts().stream().filter(x -> x.getName().equals(out.getName()))
              .forEach(x -> x.setOutgoingFifo(finn));
          index++;
        }
      }
    }
  }

  private void connectDuplicatedDataInputPort(AbstractActor loopActor, List<AbstractActor> dupActorsList,
      Long duplicationValue, Long originalLoopRv) {
    int index = 0;
    for (final DataInputPort in : loopActor.getDataInputPorts()) {
      if (!in.getFifo().isHasADelay()) {
        final ForkActor frk = PiMMUserFactory.instance.createForkActor();
        frk.setName("Fork_loop_" + loopActor.getName() + index);
        frk.setContainingGraph(loopActor.getContainingPiGraph());

        // connect din to frk
        final DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
        din.setName("in");
        din.setExpression(in.getExpression().evaluate() * originalLoopRv);

        frk.getDataInputPorts().add(din);
        final Fifo fin = PiMMUserFactory.instance.createFifo();
        fin.setType(in.getFifo().getType());
        fin.setSourcePort(in.getFifo().getSourcePort());
        fin.setTargetPort(din);

        fin.setContainingGraph(loopActor.getContainingPiGraph());

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
        fout.setContainingGraph(loopActor.getContainingPiGraph());
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
          foutn.setContainingGraph(loopActor.getContainingPiGraph());
          dupActorsList.get(i - 1).getDataInputPorts().stream().filter(x -> x.getName().equals(in.getName()))
              .forEach(x -> x.setIncomingFifo(foutn));
        }
        index++;
      } else {
        // copy delay
        for (int i = 1; i < duplicationValue; i++) {
          final Fifo fdin = PiMMUserFactory.instance.createFifo();
          final String type = in.getFifo().getType();
          fdin.setType(type);
          fdin.setContainingGraph(loopActor.getContainingPiGraph());
          final Delay copyDelay = PiMMUserFactory.instance.copy(in.getFifo().getDelay());
          copyDelay.setName(in.getFifo().getDelay().getName() + i);
          copyDelay.setContainingGraph(loopActor.getContainingPiGraph());
          final DelayActor copyDelayActor = PiMMUserFactory.instance.copy(in.getFifo().getDelay().getActor());
          copyDelayActor.setName(in.getFifo().getDelay().getActor().getName() + i);
          copyDelayActor.setContainingGraph(loopActor.getContainingPiGraph());
          copyDelay.setActor(copyDelayActor);
          fdin.assignDelay(copyDelay);
          // the getter of the initial delay is moved to get the delay of the copied actor
          final DataInputPort getterPort = in.getFifo().getDelay().getActor().getDataOutputPort().getFifo()
              .getTargetPort();
          // the setter of the copied delay is the output of the initial delay
          final Fifo fDelayActorIn = PiMMUserFactory.instance.createFifo();
          fDelayActorIn.setType(type);
          fDelayActorIn.setContainingGraph(loopActor.getContainingPiGraph());
          fDelayActorIn.setSourcePort(in.getFifo().getDelay().getActor().getDataOutputPort());
          fDelayActorIn.setTargetPort(copyDelayActor.getDataInputPort());
          final Fifo fDelayActorOut = PiMMUserFactory.instance.createFifo();
          fDelayActorOut.setType(type);
          fDelayActorOut.setContainingGraph(loopActor.getContainingPiGraph());
          fDelayActorOut.setTargetPort(getterPort);
          fDelayActorOut.setSourcePort(copyDelayActor.getDataOutputPort());

          // connect delay to actor
          dupActorsList.get(i - 1).getDataInputPorts().stream().filter(x -> x.getName().equals(in.getName()))
              .forEach(x -> x.setIncomingFifo(fdin));
          dupActorsList.get(i - 1).getDataOutputPorts().stream()
              .filter(x -> x.getName().equals(in.getFifo().getSourcePort().getName()))
              .forEach(x -> x.setOutgoingFifo(fdin));
        }
      }
    }
  }
}
