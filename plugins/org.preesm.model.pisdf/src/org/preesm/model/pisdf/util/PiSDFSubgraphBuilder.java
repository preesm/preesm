package org.preesm.model.pisdf.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.preesm.commons.CollectionUtil;
import org.preesm.commons.math.MathFunctionsHelper;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 * @author dgageot
 * 
 *         Build a subgraph from given actors.
 *
 */
public class PiSDFSubgraphBuilder extends PiMMSwitch<Boolean> {

  private final List<AbstractActor> subGraphActors;

  private final PiGraph parentGraph;

  private final PiGraph subGraph;

  private List<Fifo> visitedFifo;

  private int nbInputInterface;

  private int nbOutputInterface;

  private int nbInputCfgInterface;

  private final Map<AbstractVertex, Long> repetitionVector;

  private final long subGraphRepetition;

  /**
   * @param parentGraph
   *          parent graph
   * @param subGraphActors
   *          subgraph actors
   * @param subGraphName
   *          subgraph name
   */
  public PiSDFSubgraphBuilder(PiGraph parentGraph, List<AbstractActor> subGraphActors, String subGraphName) {
    this.parentGraph = parentGraph;
    this.subGraphActors = new LinkedList<>(subGraphActors);
    this.subGraph = PiMMUserFactory.instance.createPiGraph();
    this.subGraph.setName(subGraphName);
    this.subGraph.setUrl(this.parentGraph.getUrl() + "/" + subGraphName + ".pi");
    this.visitedFifo = new LinkedList<>();
    this.nbInputInterface = 0;
    this.nbOutputInterface = 0;
    this.nbInputCfgInterface = 0;
    this.repetitionVector = PiBRV.compute(parentGraph, BRVMethod.TOPOLOGY);
    this.subGraphRepetition = MathFunctionsHelper.gcd(CollectionUtil.mapGetAll(repetitionVector, subGraphActors));
  }

  /**
   * @return resulting subgraph
   */
  public PiGraph build() {
    // Add subgraph to parent graph
    this.parentGraph.addActor(subGraph);
    // Add actors to the new subgraph
    for (AbstractActor actor : this.subGraphActors) {
      doSwitch(actor);
    }
    // Check consistency of parent graph
    PiGraphConsistenceChecker.check(this.parentGraph);
    return this.subGraph;
  }

  @Override
  public Boolean caseAbstractActor(AbstractActor object) {
    this.subGraph.addActor(object);
    for (Port port : object.getAllPorts()) {
      doSwitch(port);
    }
    return super.caseAbstractActor(object);
  }

  @Override
  public Boolean caseDataInputPort(DataInputPort object) {
    // If caseFifo returns true, it means that the port lead to an actor outside the subgraph
    if (doSwitch(object.getFifo())) {
      // Setup the input interface
      DataInputInterface inputInterface = PiMMUserFactory.instance.createDataInputInterface();
      String inputName = "in_" + this.nbInputInterface++;
      inputInterface.setName(inputName);
      inputInterface.getDataPort().setName(inputName);
      this.subGraph.addActor(inputInterface);

      // Setup input of hierarchical actor
      DataInputPort inputPort = (DataInputPort) inputInterface.getGraphPort();
      inputPort.setName(inputName); // same name than DataInputInterface
      // Compute port expression
      long actorRepetition = this.repetitionVector.get(object.getContainingActor());
      long portExpression = object.getExpression().evaluate() * actorRepetition / this.subGraphRepetition;
      inputPort.setExpression(portExpression);

      // Interconnect the outside with hierarchical actor
      Fifo incomingFifo = PiMMUserFactory.instance.createFifo();
      inputPort.setIncomingFifo(incomingFifo);
      this.parentGraph.addFifo(incomingFifo);
      Fifo oldFifo = object.getFifo();
      Delay oldDelay = oldFifo.getDelay();
      if (oldDelay != null) {
        incomingFifo.setDelay(oldDelay);
      }
      String dataType = oldFifo.getType();
      incomingFifo.setSourcePort(oldFifo.getSourcePort());
      incomingFifo.setType(dataType);
      this.parentGraph.removeFifo(oldFifo); // remove FIFO from containing graph

      // Setup inside communication with DataInputInterface
      DataOutputPort outputPort = (DataOutputPort) inputInterface.getDataPort();
      outputPort.setExpression(portExpression);
      Fifo insideOutgoingFifo = PiMMUserFactory.instance.createFifo();
      outputPort.setOutgoingFifo(insideOutgoingFifo);
      insideOutgoingFifo.setTargetPort(object);
      insideOutgoingFifo.setType(dataType);
      inputInterface.getDataOutputPorts().add(outputPort);
      this.subGraph.addFifo(insideOutgoingFifo);
    }
    return super.caseDataInputPort(object);
  }

  @Override
  public Boolean caseDataOutputPort(DataOutputPort object) {
    // If caseFifo returns true, it means that the port lead to an actor outside the subgraph
    if (doSwitch(object.getFifo())) {
      // Setup the output interface
      DataOutputInterface outputInterface = PiMMUserFactory.instance.createDataOutputInterface();
      String outputName = "out_" + this.nbOutputInterface++;
      outputInterface.setName(outputName);
      outputInterface.getDataPort().setName(outputName);
      this.subGraph.addActor(outputInterface);

      // Setup output of hierarchical actor
      DataOutputPort outputPort = (DataOutputPort) outputInterface.getGraphPort();
      outputPort.setName(outputName); // same name than DataOutputInterface
      // Compute port expression
      long actorRepetition = this.repetitionVector.get(object.getContainingActor());
      long portExpression = object.getExpression().evaluate() * actorRepetition / this.subGraphRepetition;
      outputPort.setExpression(portExpression);

      // Interconnect the outside with hierarchical actor
      Fifo outsideOutgoingFifo = PiMMUserFactory.instance.createFifo();
      outputPort.setOutgoingFifo(outsideOutgoingFifo);
      this.parentGraph.addFifo(outsideOutgoingFifo);
      Fifo oldFifo = object.getFifo();
      Delay oldDelay = oldFifo.getDelay();
      if (oldDelay != null) {
        outsideOutgoingFifo.setDelay(oldDelay);
      }
      String dataType = oldFifo.getType();
      outsideOutgoingFifo.setTargetPort(oldFifo.getTargetPort());
      outsideOutgoingFifo.setType(dataType);
      this.parentGraph.removeFifo(oldFifo); // remove FIFO from containing graph

      // Setup inside communication with DataOutputInterface
      DataInputPort inputDataPort = (DataInputPort) outputInterface.getDataPort();
      inputDataPort.setExpression(portExpression);
      Fifo insideIncomingFifo = PiMMUserFactory.instance.createFifo();
      inputDataPort.setIncomingFifo(insideIncomingFifo);
      insideIncomingFifo.setSourcePort(object);
      insideIncomingFifo.setType(dataType);
      outputInterface.getDataInputPorts().add(inputDataPort);
      this.subGraph.addFifo(insideIncomingFifo);
    }
    return super.caseDataOutputPort(object);
  }

  @Override
  public Boolean caseConfigInputPort(ConfigInputPort object) {
    // Setup the input configuration interface
    ConfigInputInterface inputInterface = PiMMUserFactory.instance.createConfigInputInterface();
    String inputCfgName = "cfg_" + this.nbInputCfgInterface++;
    inputInterface.setName(inputCfgName);
    this.subGraph.addParameter(inputInterface);

    // Setup input of hierarchical actor
    ConfigInputPort inputPort = inputInterface.getGraphPort();
    inputPort.setName(inputCfgName); // same name than ConfigInputInterface

    // Interconnect the outside with hierarchical actor
    Dependency outsideIncomingDependency = PiMMUserFactory.instance.createDependency();
    inputPort.setIncomingDependency(outsideIncomingDependency);
    this.parentGraph.addDependency(outsideIncomingDependency);
    Dependency oldDependency = object.getIncomingDependency();
    outsideIncomingDependency.setSetter(oldDependency.getSetter());

    // Setup inside communication with ConfigInputInterface
    Dependency dependency = object.getIncomingDependency();
    dependency.setSetter(inputInterface);
    this.subGraph.addDependency(dependency);

    return super.caseConfigInputPort(object);
  }

  @Override
  public Boolean caseFifo(Fifo object) {
    // Is the fifo connect two actors of the desired subgraph?
    boolean betweenActorsOfSubGraph = this.subGraphActors.contains(object.getTarget())
        && this.subGraphActors.contains(object.getSource());
    // If fifo should be contained in the subgraph, add it.
    if (betweenActorsOfSubGraph && !this.visitedFifo.contains(object)) {
      this.visitedFifo.add(object);
      this.subGraph.addFifo(object);
      Delay delay = object.getDelay();
      // If there is a delay, add it into the subgraph
      if (delay != null) {
        this.subGraph.addDelay(delay);
      }
    }
    return !betweenActorsOfSubGraph;
  }

}
