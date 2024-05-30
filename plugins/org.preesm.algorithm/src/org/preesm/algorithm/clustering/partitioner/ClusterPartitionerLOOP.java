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
import org.preesm.commons.exceptions.PreesmRuntimeException;
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
import org.preesm.model.pisdf.util.ClusteringPatternSeekerLoop;
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

  // Needs to be declare as a class member to be used and modified in lambda
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
    super(graph, scenario, numberOfPEs);
    this.brv = brv;
    this.clusterId = clusterId;
  }

  /**
   * @return Clustered PiGraph.
   */
  @Override
  public PiGraph cluster() {
    // retrieve tle cycle sequence to be coarsely clustered
    final List<AbstractActor> localPluralLOOPs = new ClusteringPatternSeekerLoop(graph).pluralLocalseek();
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
    final List<AbstractActor> graphLocalSingleLOOPs = new ClusteringPatternSeekerLoop(graph).singleLocalseek();
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
      return this.graph;
    }
    // retrieve the obtained or existing single local cycle to be coarse
    final List<AbstractActor> graphNotLocalSingleLOOPs = new ClusteringPatternSeekerLoop(graph).singleNotLocalseek();
    if (!graphNotLocalSingleLOOPs.isEmpty()) {
      final PiGraph subGraph = new PiSDFSubgraphBuilder(graph, graphNotLocalSingleLOOPs, LOOP_PREFIX + clusterId)
          .build();
      // Add constraints of the cluster in the scenario.
      subGraph.setClusterValue(true);
      for (final ComponentInstance component : ClusteringHelper.getListOfCommonComponent(graphNotLocalSingleLOOPs,
          this.scenario)) {
        this.scenario.getConstraints().addConstraint(component, subGraph);
      }
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

    final PiGraph containingGraph = loopActor.getContainingPiGraph();

    int index = 0;
    for (final DataOutputPort out : loopActor.getDataOutputPorts()) {
      if (!out.getFifo().isHasADelay()) {

        final JoinActor jn = PiMMUserFactory.instance.createJoinActor();
        jn.setName("Join_loop_" + loopActor.getName() + index);
        jn.setContainingGraph(loopActor.getContainingPiGraph());

        // connect Join to dout
        final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort("out",
            out.getExpression().evaluate() * originalLoopRv);

        jn.getDataOutputPorts().add(dout);

        final Fifo fout = PiMMUserFactory.instance.createFifo(dout, out.getFifo().getTargetPort(),
            out.getFifo().getType());
        containingGraph.addFifo(fout);

        // connect oEmpty_0 to Join
        final Long rateJoinIn = out.getExpression().evaluate() * originalLoopRv / duplicationValue;
        final DataInputPort din = PiMMUserFactory.instance.createDataInputPort("in_0", rateJoinIn);

        jn.getDataInputPorts().add(din);

        final Fifo fin = PiMMUserFactory.instance.createFifo(out, din, fout.getType());
        containingGraph.addFifo(fin);

        // connect duplicated actors to Join
        for (int i = 1; i < duplicationValue; i++) {

          final DataInputPort dinn = PiMMUserFactory.instance.createDataInputPort("in_" + i, rateJoinIn);
          jn.getDataInputPorts().add(dinn);

          final DataOutputPort doutt = dupActorsList.get(i - 1).getDataOutputPorts().stream()
              .filter(x -> x.getName().equals(out.getName())).findAny().orElseThrow(PreesmRuntimeException::new);

          final Fifo finn = PiMMUserFactory.instance.createFifo(doutt, dinn, fout.getType());
          containingGraph.addFifo(finn);

          index++;
        }
      }
    }
  }

  private void connectDuplicatedDataInputPort(AbstractActor loopActor, List<AbstractActor> dupActorsList,
      Long duplicationValue, Long originalLoopRv) {

    final PiGraph containingGraph = loopActor.getContainingPiGraph();

    int index = 0;
    for (final DataInputPort in : loopActor.getDataInputPorts()) {
      if (!in.getFifo().isHasADelay()) {

        final ForkActor frk = PiMMUserFactory.instance.createForkActor();
        frk.setName("Fork_loop_" + loopActor.getName() + index);
        containingGraph.addActor(frk);

        // connect din to frk
        final DataInputPort din = PiMMUserFactory.instance.createDataInputPort("in",
            in.getExpression().evaluate() * originalLoopRv);

        frk.getDataInputPorts().add(din);

        final Fifo fin = PiMMUserFactory.instance.createFifo(in.getFifo().getSourcePort(), din, in.getFifo().getType());
        containingGraph.addFifo(fin);

        final Long rateForkOut = in.getExpression().evaluate() * originalLoopRv / duplicationValue;

        // connect fork to oEmpty_0
        final DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort("out_0", rateForkOut);
        frk.getDataOutputPorts().add(dout);

        final Fifo fout = PiMMUserFactory.instance.createFifo(dout, in, in.getFifo().getType());
        containingGraph.addFifo(fout);
        // remove extra fifo --> non en fait c'est bon

        // connect fork to duplicated actors
        for (int i = 1; i < duplicationValue; i++) {

          final DataOutputPort doutn = PiMMUserFactory.instance.createDataOutputPort("out_" + i, rateForkOut);
          frk.getDataOutputPorts().add(doutn);

          final DataInputPort dinn = dupActorsList.get(i - 1).getDataInputPorts().stream()
              .filter(x -> x.getName().equals(in.getName())).findAny().orElseThrow(PreesmRuntimeException::new);
          final Fifo foutn = PiMMUserFactory.instance.createFifo(doutn, dinn, in.getFifo().getType());
          containingGraph.addFifo(foutn);
        }
        index++;
      } else {
        // copy delay
        for (int i = 1; i < duplicationValue; i++) {

          final String type = in.getFifo().getType();

          final Delay copyDelay = PiMMUserFactory.instance.copy(in.getFifo().getDelay());
          copyDelay.setName(in.getFifo().getDelay().getName() + i);
          copyDelay.setContainingGraph(loopActor.getContainingPiGraph());

          final DelayActor copyDelayActor = PiMMUserFactory.instance.copy(in.getFifo().getDelay().getActor());
          copyDelayActor.setName(in.getFifo().getDelay().getActor().getName() + i);
          copyDelayActor.setContainingGraph(loopActor.getContainingPiGraph());
          copyDelay.setActor(copyDelayActor);

          // the getter of the initial delay is moved to get the delay of the copied actor
          final DataInputPort getterPort = in.getFifo().getDelay().getActor().getDataOutputPort().getFifo()
              .getTargetPort();

          // the setter of the copied delay is the output of the initial delay
          final Fifo fDelayActorIn = PiMMUserFactory.instance.createFifo(
              in.getFifo().getDelay().getActor().getDataOutputPort(), copyDelayActor.getDataInputPort(), type);
          containingGraph.addFifo(fDelayActorIn);

          final Fifo fDelayActorOut = PiMMUserFactory.instance.createFifo(copyDelayActor.getDataOutputPort(),
              getterPort, type);
          containingGraph.addFifo(fDelayActorOut);

          // connect delay to actor
          final DataInputPort dinn = dupActorsList.get(i - 1).getDataInputPorts().stream()
              .filter(x -> x.getName().equals(in.getName())).findAny().orElseThrow(PreesmRuntimeException::new);
          final DataOutputPort doutt = dupActorsList.get(i - 1).getDataOutputPorts().stream()
              .filter(x -> x.getName().equals(in.getFifo().getSourcePort().getName())).findAny()
              .orElseThrow(PreesmRuntimeException::new);

          final Fifo fdin = PiMMUserFactory.instance.createFifo(doutt, dinn, type);
          containingGraph.addFifo(fdin);
          fdin.assignDelay(copyDelay);
        }
      }
    }
  }
}
