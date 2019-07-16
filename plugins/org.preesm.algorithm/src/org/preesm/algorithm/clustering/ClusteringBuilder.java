package org.preesm.algorithm.clustering;

import java.util.LinkedHashMap;
import java.util.Map;
import org.preesm.model.algorithm.schedule.Schedule;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 * @author dgageot
 *
 */
public class ClusteringBuilder {

  private Map<AbstractActor, Schedule> scheduleMapping;

  private PiGraph algorithm;

  private Map<AbstractVertex, Long> repetitionVector;

  /**
   * @param algorithm
   *          PiGraph to clusterize
   * @param clusteringAlgorithm
   *          type of clustering algorithm
   */
  public ClusteringBuilder(final PiGraph algorithm, final String clusteringAlgorithm) {
    this.scheduleMapping = new LinkedHashMap<>();
    this.algorithm = algorithm;
    this.repetitionVector = null;
  }

  public PiGraph getAlgorithm() {
    return algorithm;
  }

  public Map<AbstractActor, Schedule> getScheduleMapping() {
    return scheduleMapping;
  }

  public Map<AbstractVertex, Long> getRepetitionVector() {
    return repetitionVector;
  }

  /**
   * @param newHierarchy
   *          new hierarchy
   * @param port
   *          port to connect outside
   * @param name
   *          name of port
   * @param newExpression
   *          prod/cons value
   */
  private final InterfaceActor setPortAsHInterface(PiGraph newHierarchy, Port port, String name, long newExpression) {
    if (port instanceof DataInputPort) {
      return setDataInputPortAsHInterface(newHierarchy, (DataInputPort) port, name, newExpression);
    } else if (port instanceof DataOutputPort) {
      return setDataOutputPortAsHInterface(newHierarchy, (DataOutputPort) port, name, newExpression);
    } else {
      return null;
    }
  }

  /**
   * @param newHierarchy
   *          new hierarchy
   * @param insideInputPort
   *          DataInputPort to connect outside
   * @param name
   *          name of port
   * @param newExpression
   *          prod/cons value
   */
  private final DataInputInterface setDataInputPortAsHInterface(PiGraph newHierarchy, DataInputPort insideInputPort,
      String name, long newExpression) {
    // Setup DataInputInterface
    DataInputInterface inputInterface = PiMMUserFactory.instance.createDataInputInterface();
    inputInterface.setName(name);
    inputInterface.getDataPort().setName(name);
    newHierarchy.addActor(inputInterface);

    // Setup input of hierarchical actor
    DataInputPort inputPort = (DataInputPort) inputInterface.getGraphPort();
    inputPort.setName(name); // same name than DataInputInterface
    inputPort.setExpression(newExpression);

    // Interconnect the outside with hierarchical actor
    inputPort.setIncomingFifo(PiMMUserFactory.instance.createFifo());
    newHierarchy.getContainingPiGraph().addFifo(inputPort.getFifo());
    Fifo oldFifo = insideInputPort.getFifo();
    String dataType = oldFifo.getType();
    inputPort.getIncomingFifo().setSourcePort(oldFifo.getSourcePort());
    inputPort.getIncomingFifo().setType(dataType);
    newHierarchy.getContainingPiGraph().removeFifo(oldFifo); // remove FIFO from containing graph

    // Setup inside communication with DataInputInterface
    DataOutputPort outputDataPort = (DataOutputPort) inputInterface.getDataPort();
    outputDataPort.setExpression(newExpression);
    outputDataPort.setOutgoingFifo(PiMMUserFactory.instance.createFifo());
    outputDataPort.getOutgoingFifo().setTargetPort(insideInputPort);
    outputDataPort.getOutgoingFifo().setType(dataType);
    inputInterface.getDataOutputPorts().add(outputDataPort);
    newHierarchy.addFifo(outputDataPort.getFifo());

    return inputInterface;
  }

  /**
   * @param newHierarchy
   *          new hierarchy
   * @param insideOutputPort
   *          DataOutputPort to connect outside
   * @param name
   *          name of port
   * @param newExpression
   *          prod/cons value
   */
  private final DataOutputInterface setDataOutputPortAsHInterface(PiGraph newHierarchy, DataOutputPort insideOutputPort,
      String name, long newExpression) {
    // Setup DataOutputInterface
    DataOutputInterface outputInterface = PiMMUserFactory.instance.createDataOutputInterface();
    outputInterface.setName(name);
    outputInterface.getDataPort().setName(name);
    newHierarchy.addActor(outputInterface);

    // Setup output of hierarchical actor
    DataOutputPort outputPort = (DataOutputPort) outputInterface.getGraphPort();
    outputPort.setName(name); // same name than DataOutputInterface
    outputPort.setExpression(newExpression);

    // Interconnect the outside with hierarchical actor
    outputPort.setOutgoingFifo(PiMMUserFactory.instance.createFifo());
    newHierarchy.getContainingPiGraph().addFifo(outputPort.getFifo());
    Fifo oldFifo = insideOutputPort.getFifo();
    String dataType = oldFifo.getType();
    outputPort.getOutgoingFifo().setTargetPort(oldFifo.getTargetPort());
    outputPort.getOutgoingFifo().setType(dataType);
    newHierarchy.getContainingPiGraph().removeFifo(oldFifo); // remove FIFO from containing graph

    // Setup inside communication with DataOutputInterface
    DataInputPort inputDataPort = (DataInputPort) outputInterface.getDataPort();
    inputDataPort.setExpression(newExpression);
    inputDataPort.setIncomingFifo(PiMMUserFactory.instance.createFifo());
    inputDataPort.getIncomingFifo().setSourcePort(insideOutputPort);
    inputDataPort.getIncomingFifo().setType(dataType);
    outputInterface.getDataInputPorts().add(inputDataPort);
    newHierarchy.addFifo(inputDataPort.getFifo());

    return outputInterface;
  }

  /**
   * @param newHierarchy
   *          new hierarchy
   * @param insideInputPort
   *          ConfigInputPort to connect outside
   * @return generated ConfigInputInterface
   */
  private final ConfigInputInterface setConfigInputPortAsHInterface(PiGraph newHierarchy,
      ConfigInputPort insideInputPort, String name) {
    // Setup ConfigInputInterface
    ConfigInputInterface inputInterface = PiMMUserFactory.instance.createConfigInputInterface();
    inputInterface.setName(name);
    newHierarchy.addParameter(inputInterface);

    // Setup input of hierarchical actor
    ConfigInputPort inputPort = inputInterface.getGraphPort();
    inputPort.setName(name); // same name than ConfigInputInterface

    // Interconnect the outside with hierarchical actor
    inputPort.setIncomingDependency(PiMMUserFactory.instance.createDependency());
    newHierarchy.getContainingPiGraph().addDependency(inputPort.getIncomingDependency());
    Dependency oldDependency = insideInputPort.getIncomingDependency();
    inputPort.getIncomingDependency().setSetter(oldDependency.getSetter());

    // Setup inside communication with ConfigInputInterface
    Dependency dependency = insideInputPort.getIncomingDependency();
    dependency.setSetter(inputInterface);
    newHierarchy.addDependency(dependency);

    return inputInterface;
  }

}
