package org.preesm.algorithm.clustering.identification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.preesm.algorithm.clustering.heuristics.VerticalHeuristic;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigOutputInterface;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
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
  public void assessFlatteningBefore(PiGraph parentGraph, PiGraph childGraph) {
    /** Nothing to do here */
  }

  @Override
  public void assessFlatteningAfter(PiGraph parentGraph, PiGraph childGraph) {
    this.currentParentGraph = parentGraph;
    this.currentChildGraph = childGraph;

    childGraph.getDataInterfaces().forEach(di -> doSwitch(di));
    childGraph.getDelayActors().forEach(a -> doSwitch(a));

    // WIP
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
    actor.getAllDataPorts().stream().map(DataPort::getFifo).forEach(fifo -> doSwitch(fifo));
    actor.getConfigInputPorts().forEach(cii -> doSwitch(cii));
    actor.getConfigOutputPorts().forEach(coi -> doSwitch(coi));
    this.currentParentGraph.addActor(actor);
    this.alreadyExploredActors.add(actor);
    return true;
  }

  @Override
  public Boolean caseDataInputInterface(DataInputInterface dii) {
    // WIP
    return true;
  }

  @Override
  public Boolean caseDataOutputInterface(DataOutputInterface doi) {
    // WIP
    return true;
  }

  @Override
  public Boolean caseConfigInputInterface(ConfigInputInterface cii) {
    // WIP
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
