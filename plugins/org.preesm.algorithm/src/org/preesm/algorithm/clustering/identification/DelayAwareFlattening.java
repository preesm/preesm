package org.preesm.algorithm.clustering.identification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.preesm.algorithm.clustering.ClusteringHelper;
import org.preesm.algorithm.clustering.heuristics.VerticalHeuristic;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputInterface;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.statictools.optims.BroadcastRoundBufferOptimization;
import org.preesm.model.pisdf.statictools.optims.ForkJoinOptimization;
import org.preesm.model.pisdf.util.PiMMSwitch;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 * @author rcazoulat
 */
public class DelayAwareFlattening extends PiMMSwitch<Boolean> implements VerticalHeuristic {

  List<AbstractActor> alreadyExploredActors;
  List<Fifo>          alreadyExploredFifos;
  PiGraph             currentParentGraph;
  PiGraph             currentChildGraph;

  public DelayAwareFlattening() {
    this.alreadyExploredActors = new ArrayList<>();
    this.alreadyExploredFifos = new ArrayList<>();
  }

  // ===========================================================================================================
  //
  // VERTICAL HEURISTIC INTERFACE METHODS
  //
  // ===========================================================================================================

  @Override
  public void assessFlatteningBefore(PiGraph parentGraph, PiGraph currentGraph) {
    /** Nothing to do here */
  }

  @Override
  public void assessFlatteningAfter(PiGraph parentGraph, PiGraph currentGraph) {

    ClusteringHelper.addAllSpecialActors(currentGraph);

    this.currentParentGraph = parentGraph;
    this.currentChildGraph = currentGraph;

    currentGraph.getDataInterfaces().forEach(di -> doSwitch(di));
    currentGraph.getDelayActors().forEach(a -> doSwitch(a));

    final ForkJoinOptimization forkJoinOptimization = new ForkJoinOptimization();
    forkJoinOptimization.optimize(parentGraph);

    final BroadcastRoundBufferOptimization brRbOptimization = new BroadcastRoundBufferOptimization();
    brRbOptimization.optimize(parentGraph);
  }

  @Override
  public String getPrefix() {
    return "flattened";
  }

  @Override
  public void initHeuristicParameters(PiGraph graph, Scenario scenario, Design arch,
      Map<String, String> taskParameters) {
    /** No parameters to initialize */
  }

  @Override
  public boolean assessGraph(PiGraph graph) {
    return false;
  }

  // ===========================================================================================================
  //
  // CASES
  //
  // ===========================================================================================================

  @Override
  public Boolean caseAbstractActor(AbstractActor actor) {
    if (this.alreadyExploredActors.contains(actor)) {
      return false;
    }
    this.currentParentGraph.addActor(actor);
    this.alreadyExploredActors.add(actor);
    actor.getAllDataPorts().stream().map(DataPort::getFifo).forEach(fifo -> doSwitch(fifo));
    actor.getConfigInputPorts().forEach(cii -> doSwitch(cii));
    actor.getConfigOutputPorts().forEach(coi -> doSwitch(coi));

    return true;
  }

  @Override
  public Boolean caseDataInputInterface(DataInputInterface dii) {
    final DataOutputPort outsideDop = dii.getGraphPort().getIncomingFifo().getSourcePort();
    final DataInputPort insideDip = dii.getDataPort().getOutgoingFifo().getTargetPort();
    final String fifoType = outsideDop.getFifo().getType();
    this.currentParentGraph.removeFifo(outsideDop.getFifo());
    this.currentChildGraph.removeFifo(insideDip.getFifo());
    if (!this.currentParentGraph.getActors().contains(insideDip.getContainingActor())) {
      this.currentParentGraph.addActor(insideDip.getContainingActor());
    }
    final Fifo fifo = PiMMUserFactory.instance.createFifo(outsideDop, insideDip, fifoType);
    this.currentParentGraph.addFifo(fifo);
    return true;
  }

  @Override
  public Boolean caseDataOutputInterface(DataOutputInterface doi) {
    final DataInputPort outsideDip = doi.getGraphPort().getOutgoingFifo().getTargetPort();
    final DataOutputPort insideDop = doi.getDataPort().getIncomingFifo().getSourcePort();
    final String fifoType = outsideDip.getFifo().getType();
    this.currentParentGraph.removeFifo(outsideDip.getFifo());
    this.currentChildGraph.removeFifo(insideDop.getFifo());
    if (!this.currentParentGraph.getActors().contains(insideDop.getContainingActor())) {
      this.currentParentGraph.addActor(insideDop.getContainingActor());
    }
    final Fifo fifo = PiMMUserFactory.instance.createFifo(insideDop, outsideDip, fifoType);
    this.currentParentGraph.addFifo(fifo);
    return true;
  }

  @Override
  public Boolean caseConfigInputInterface(ConfigInputInterface cii) {

    final ISetter newSetter = cii.getGraphPort().getIncomingDependency().getSetter();

    for (final ConfigInputPort cip : cii.getOutgoingDependencies().stream().map(dep -> dep.getGetter()).toList()) {

    }

    return true;
  }

  @Override
  public Boolean caseConfigOutputInterface(ConfigOutputInterface coi) {
    // WIP
    return true;
  }

  @Override
  public Boolean caseFifo(Fifo fifo) {
    // WIP
    return true;
  }

  // ===========================================================================================================
  //
  // PRIVATE METHODS
  //
  // ===========================================================================================================

}
