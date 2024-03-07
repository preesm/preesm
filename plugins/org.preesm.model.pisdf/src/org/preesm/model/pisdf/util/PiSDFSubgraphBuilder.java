/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2022) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019 - 2020)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2022)
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
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 * This class is used to build a subgraph from given list of actors.
 *
 * @author dgageot
 */
public class PiSDFSubgraphBuilder extends PiMMSwitch<Boolean> {

  /**
   * Actors that compose the subgraph.
   */
  private final List<AbstractActor> subGraphActors;

  /**
   * Parent graph of the subgraph.
   */
  private final PiGraph parentGraph;

  /**
   * Subgraph builded with this class.
   */
  private final PiGraph subGraph;

  /**
   * List of visited Fifo in order to explore the PiGraph.
   */
  private final List<Fifo> visitedFifo;

  /**
   * Number of input interface of builded subgraph.
   */
  private int nbInputInterface;

  /**
   * Number of output interface of builded subgraph.
   */
  private int nbOutputInterface;

  /**
   * Number of input configuration interface of builded subgraph.
   */
  private final int nbInputCfgInterface;

  /**
   * Repetition vector of input graph.
   */
  private final Map<AbstractVertex, Long> repetitionVector;

  /**
   * Repetition count of the subgraph.
   */
  private long subGraphRepetition;

  /**
   * Builds a PiSDFSubgraphBuilder object.
   *
   * @param parentGraph
   *          The parent graph.
   * @param subGraphActors
   *          The list of actors that will compose the subgraph.
   * @param subGraphName
   *          The name of the subgraph.
   */
  public PiSDFSubgraphBuilder(PiGraph parentGraph, List<AbstractActor> subGraphActors, String subGraphName) {
    this.parentGraph = parentGraph;
    this.subGraphActors = new LinkedList<>(subGraphActors);
    // Create a PiGraph for the subgraph
    this.subGraph = PiMMUserFactory.instance.createPiGraph();
    this.subGraph.setName(subGraphName);
    this.subGraph.setUrl(this.parentGraph.getUrl() + "/" + subGraphName + ".pi");
    this.visitedFifo = new LinkedList<>();
    this.nbInputInterface = 0;
    this.nbOutputInterface = 0;
    this.nbInputCfgInterface = 0;
    // Compute BRV for the parent graph
    this.repetitionVector = PiBRV.compute(parentGraph, BRVMethod.LCM);
    // Compute repetition count of the subgraph with great common divisor over all subgraph actors repetition counts

    this.subGraphRepetition = MathFunctionsHelper.gcd(CollectionUtil.mapGetAll(repetitionVector, subGraphActors));
    if (subGraphName.contains("sub")) {
      this.subGraphRepetition = 1L;
    }

  }

  /**
   * Performs subgraph actors extraction from parent graph.
   *
   * @return The resulting subgraph.
   */
  public PiGraph build() {
    // Add subgraph to parent graph
    this.parentGraph.addActor(subGraph);
    // Add actors to the new subgraph
    for (final AbstractActor actor : this.subGraphActors) {
      doSwitch(actor);
    }

    // Check consistency of parent graph
    // Check consistency of the graph (throw exception if recoverable or fatal error)
    final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ANALYSIS,
        CheckerErrorLevel.NONE);
    pgcc.check(this.parentGraph);
    return this.subGraph;
  }

  @Override
  public Boolean caseAbstractActor(AbstractActor object) {
    this.subGraph.addActor(object);
    for (final Port port : object.getAllPorts()) {
      doSwitch(port);
    }
    return super.caseAbstractActor(object);
  }

  @Override
  public Boolean caseDataInputPort(DataInputPort object) {
    // If caseFifo returns true, it means that the port lead to an actor outside the subgraph
    if (doSwitch(object.getFifo())) {
      // Setup the input interface
      final DataInputInterface inputInterface = PiMMUserFactory.instance.createDataInputInterface();
      final String inputName = "in_" + this.nbInputInterface++;
      inputInterface.setName(inputName);
      inputInterface.getDataPort().setName(inputName);
      this.subGraph.addActor(inputInterface);

      // Setup input of hierarchical actor
      final DataInputPort inputPort = (DataInputPort) inputInterface.getGraphPort();
      inputPort.setName(inputName); // same name than DataInputInterface
      // Compute port expression
      final long actorRepetition = this.repetitionVector.get(object.getContainingActor());
      long portExpression = object.getExpression().evaluate() * actorRepetition / this.subGraphRepetition;
      if (object.getContainingActor() instanceof DelayActor) {
        portExpression = object.getFifo().getTargetPort().getExpression().evaluate();
      }
      inputPort.setExpression(portExpression);

      // Interconnect the outside with hierarchical actor
      final Fifo incomingFifo = PiMMUserFactory.instance.createFifo();
      inputPort.setIncomingFifo(incomingFifo);
      final Fifo oldFifo = object.getFifo();
      final Delay oldDelay = oldFifo.getDelay();
      this.parentGraph.removeFifo(oldFifo); // remove FIFO from containing graph
      if (oldDelay != null) {
        incomingFifo.assignDelay(oldDelay);
      }
      this.parentGraph.addFifo(incomingFifo);
      final String dataType = oldFifo.getType();
      incomingFifo.setSourcePort(oldFifo.getSourcePort());
      incomingFifo.setType(dataType);

      // Setup inside communication with DataInputInterface
      final DataOutputPort outputPort = (DataOutputPort) inputInterface.getDataPort();
      outputPort.setExpression(portExpression);
      final Fifo insideOutgoingFifo = PiMMUserFactory.instance.createFifo();
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
      final DataOutputInterface outputInterface = PiMMUserFactory.instance.createDataOutputInterface();
      final String outputName = "out_" + this.nbOutputInterface++;
      outputInterface.setName(outputName);
      outputInterface.getDataPort().setName(outputName);
      this.subGraph.addActor(outputInterface);

      // Setup output of hierarchical actor
      final DataOutputPort outputPort = (DataOutputPort) outputInterface.getGraphPort();
      outputPort.setName(outputName); // same name than DataOutputInterface
      // Compute port expression
      final long actorRepetition = this.repetitionVector.get(object.getContainingActor());
      long portExpression = object.getExpression().evaluate() * actorRepetition / this.subGraphRepetition;
      if (object.getContainingActor() instanceof DelayActor) {
        portExpression = object.getFifo().getSourcePort().getExpression().evaluate();
      }
      outputPort.setExpression(portExpression);

      // Interconnect the outside with hierarchical actor
      final Fifo outsideOutgoingFifo = PiMMUserFactory.instance.createFifo();
      outputPort.setOutgoingFifo(outsideOutgoingFifo);
      this.parentGraph.addFifo(outsideOutgoingFifo);
      final Fifo oldFifo = object.getFifo();
      final Delay oldDelay = oldFifo.getDelay();
      this.parentGraph.removeFifo(oldFifo); // remove FIFO from containing graph
      if (oldDelay != null) {
        outsideOutgoingFifo.assignDelay(oldDelay);
      }
      final String dataType = oldFifo.getType();
      outsideOutgoingFifo.setTargetPort(oldFifo.getTargetPort());
      outsideOutgoingFifo.setType(dataType);

      // Setup inside communication with DataOutputInterface
      final DataInputPort inputDataPort = (DataInputPort) outputInterface.getDataPort();
      inputDataPort.setExpression(portExpression);
      final Fifo insideIncomingFifo = PiMMUserFactory.instance.createFifo();
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
    final Boolean interfaceExist = false;
    final ConfigInputInterface inputInterface = PiMMUserFactory.instance.createConfigInputInterface();
    final String inputCfgName = object.getName();
    if (this.subGraph.getParametersNames().contains(object.getName())) {
      // this.subGraph.getConfigInputInterfaces()
      inputInterface = this.subGraph.getConfigInputInterfaces().stream()
          .filter(x -> x.getName().equals(object.getName())).findAny().orElseThrow();
      interfaceExist = true;
    } else {

      inputInterface.setName(inputCfgName);
      this.subGraph.addParameter(inputInterface);
    }

    if (this.subGraph.getName().matches("^sub\\d+")) {
      // Setup the input configuration interface
      Boolean interfaceExist = false;
      ConfigInputInterface inputInterface = PiMMUserFactory.instance.createConfigInputInterface();
      final String inputCfgName = object.getName();
      if (this.subGraph.getParametersNames().contains(object.getName())) {
        // this.subGraph.getConfigInputInterfaces()
        inputInterface = this.subGraph.getConfigInputInterfaces().stream()
            .filter(x -> x.getName().equals(object.getName())).findAny().orElseThrow();
        interfaceExist = true;
      } else {


    // Interconnect the outside with hierarchical actor
    if (Boolean.FALSE.equals(interfaceExist)) {
      final Dependency outsideIncomingDependency = PiMMUserFactory.instance.createDependency();
      inputPort.setIncomingDependency(outsideIncomingDependency);

      this.parentGraph.addDependency(outsideIncomingDependency);

      final Dependency oldDependency = object.getIncomingDependency();
      outsideIncomingDependency.setSetter(oldDependency.getSetter());
    }
    // Setup inside communication with ConfigInputInterface
    final Dependency dependency = object.getIncomingDependency();
    dependency.setSetter(inputInterface);
    this.subGraph.addDependency(dependency);

      // Interconnect the outside with hierarchical actor
      if (Boolean.FALSE.equals(interfaceExist)) {
        final Dependency outsideIncomingDependency = PiMMUserFactory.instance.createDependency();
        inputPort.setIncomingDependency(outsideIncomingDependency);

        this.parentGraph.addDependency(outsideIncomingDependency);

        final Dependency oldDependency = object.getIncomingDependency();
        outsideIncomingDependency.setSetter(oldDependency.getSetter());
      }
      // Setup inside communication with ConfigInputInterface
      final Dependency dependency = object.getIncomingDependency();
      dependency.setSetter(inputInterface);
      this.subGraph.addDependency(dependency);
    } else {
      // Setup the input configuration interface
      final ConfigInputInterface inputInterface = PiMMUserFactory.instance.createConfigInputInterface();
      final String inputCfgName = "cfg_" + this.nbInputCfgInterface++;
      inputInterface.setName(inputCfgName);
      this.subGraph.addParameter(inputInterface);

      // Setup input of hierarchical actor
      final ConfigInputPort inputPort = inputInterface.getGraphPort();
      inputPort.setName(inputCfgName); // same name than ConfigInputInterface

      // Interconnect the outside with hierarchical actor
      final Dependency outsideIncomingDependency = PiMMUserFactory.instance.createDependency();
      inputPort.setIncomingDependency(outsideIncomingDependency);
      this.parentGraph.addDependency(outsideIncomingDependency);
      final Dependency oldDependency = object.getIncomingDependency();
      outsideIncomingDependency.setSetter(oldDependency.getSetter());

      // Setup inside communication with ConfigInputInterface
      final Dependency dependency = object.getIncomingDependency();
      dependency.setSetter(inputInterface);
      this.subGraph.addDependency(dependency);
    }
    return super.caseConfigInputPort(object);
  }

  @Override
  public Boolean caseFifo(Fifo object) {
    // Is the fifo connect two actors of the desired subgraph?
    final boolean betweenActorsOfSubGraph = this.subGraphActors.contains(object.getTarget())
        && this.subGraphActors.contains(object.getSource());
    // If fifo should be contained in the subgraph, add it.
    if (betweenActorsOfSubGraph && !this.visitedFifo.contains(object)) {
      this.visitedFifo.add(object);
      this.subGraph.addFifo(object);
      final Delay delay = object.getDelay();
      // If there is a delay, add it into the subgraph
      if (delay != null) {
        this.subGraph.addDelay(delay);
        if (delay.getLevel().equals(PersistenceLevel.NONE) && delay.hasGetterActor()) {
          for (final Port delayPort : delay.getActor().getAllPorts()) {
            doSwitch(delayPort);
          }
        }
      }
    }
    return !betweenActorsOfSubGraph;
  }

}
