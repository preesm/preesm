/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019 - 2025) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019 - 2020)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2022 - 2025)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
 * Ophelie-Renaud [ophelie.renaud@insa-rennes.fr] (2025)
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
import java.util.Optional;
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
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.Parameter;
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
  private int nbInputInterface = 0;

  /**
   * Number of output interface of builded subgraph.
   */
  private int nbOutputInterface = 0;

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
    this.subGraph.setExpression(PiMMUserFactory.instance.createExpression()); // Why ?
    this.subGraph.setUrl(this.parentGraph.getUrl() + "/" + subGraphName + ".pi");
    this.visitedFifo = new LinkedList<>();

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
    pgcc.check(this.subGraph);
    pgcc.check(this.parentGraph);

    // This line is here because of a bug occurring when creating a cluster in a cluster.
    // In details, it seems that the actorIndex (existing just to keep track of the number of actors in a graph) of
    // cluster is not updated when adding an existing actor (of parentGraph) in cluster. So we update by hand.
    this.subGraph.setActorIndex(this.subGraph.getActors().size());
    this.subGraph.setFifoWithoutDelayIndex(this.subGraph.getFifosWithoutDelay().size());
    this.subGraph.setFifoWithDelayIndex(this.subGraph.getFifosWithDelay().size());

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
  public Boolean caseDataInputPort(DataInputPort dataInputPort) {

    // If caseFifo returns true, it means that the port lead to an actor outside the subgraph
    if (Boolean.TRUE.equals(doSwitch(dataInputPort.getFifo()))) {

      final AbstractActor parentActor = dataInputPort.getContainingActor();

      // Setup the input interface
      final DataInputInterface inputInterface = PiMMUserFactory.instance
          .createDataInputInterface(dataInputPort.getName() + "_" + this.nbInputInterface++);
      this.subGraph.addActor(inputInterface);

      // Compute port expression
      long interfaceExpr = dataInputPort.getExpression().evaluateAsLong() * this.repetitionVector.get(parentActor)
          / this.subGraphRepetition;
      if (parentActor instanceof DelayActor) {
        interfaceExpr = dataInputPort.getFifo().getTargetPort().getExpression().evaluateAsLong();
      }
      inputInterface.getGraphPort().setExpression(interfaceExpr);
      inputInterface.getDataPort().setExpression(interfaceExpr);

      // Parent graph -> interface
      final Fifo outFifo = dataInputPort.getFifo();
      inputInterface.getGraphPort().setIncomingFifo(outFifo); // set the outer port's incoming fifo

      // Interface -> data input port
      final Fifo inFifo = PiMMUserFactory.instance.createFifo(inputInterface.getDataPort(), dataInputPort,
          outFifo.getType());
      this.subGraph.addFifo(inFifo);
    }
    return super.caseDataInputPort(dataInputPort);
  }

  @Override
  public Boolean caseDataOutputPort(DataOutputPort dataOutputPort) {
    // If caseFifo returns true, it means that the port lead to an actor outside the subgraph
    if (Boolean.TRUE.equals(doSwitch(dataOutputPort.getFifo()))) {

      final AbstractActor parentActor = dataOutputPort.getContainingActor();

      // Setup the input interface
      final DataOutputInterface outputInterface = PiMMUserFactory.instance
          .createDataOutputInterface(dataOutputPort.getName() + "_" + this.nbOutputInterface++);
      this.subGraph.addActor(outputInterface);

      // Compute port expression
      long interfaceExpr = dataOutputPort.getExpression().evaluateAsLong() * this.repetitionVector.get(parentActor)
          / this.subGraphRepetition;
      if (parentActor instanceof DelayActor) {
        interfaceExpr = dataOutputPort.getFifo().getSourcePort().getExpression().evaluateAsLong();
      }
      outputInterface.getGraphPort().setExpression(interfaceExpr);
      outputInterface.getDataPort().setExpression(interfaceExpr);

      // Parent interface -> graph
      final Fifo outFifo = dataOutputPort.getFifo();
      outputInterface.getGraphPort().setOutgoingFifo(outFifo); // set the outer port's incoming fifo

      // Data input port -> interface
      final Fifo inFifo = PiMMUserFactory.instance.createFifo(dataOutputPort, outputInterface.getDataPort(),
          outFifo.getType());
      this.subGraph.addFifo(inFifo);
    }
    return super.caseDataOutputPort(dataOutputPort);
  }

  @Override
  public Boolean caseConfigInputPort(ConfigInputPort configInputPort) {

    final Dependency outerDep = configInputPort.getIncomingDependency();

    // Check if there is already a configIputPort plugged to this parameter
    // Because another actor of the cluster uses it
    final Parameter outerParam = (Parameter) outerDep.getSetter();
    final Optional<ConfigInputInterface> optParam = this.subGraph.getConfigInputInterfaces().stream()
        .filter(cii -> cii.getName().equals(outerParam.getName())).findAny();

    ConfigInputInterface inputInterface;
    if (optParam.isEmpty()) {

      // Create new configInputInterface for the inside
      inputInterface = PiMMUserFactory.instance.createConfigInputInterface(outerParam.getName());
      inputInterface.setExpression(outerParam.getExpression().evaluateAsDouble());
      this.subGraph.addParameter(inputInterface);

      // create a new config link from the original parameter to the new inner one
      final Dependency newOuterDep = PiMMUserFactory.instance.createDependency(outerDep.getSetter(),
          inputInterface.getGraphPort());
      parentGraph.addDependency(newOuterDep);

    } else {
      inputInterface = optParam.get();
    }

    // link the outerDep inside, making it the inner dep
    final Dependency innerDep = outerDep;
    innerDep.setGetter(configInputPort);
    innerDep.setSetter(inputInterface);
    this.subGraph.addDependency(innerDep);

    return super.caseConfigInputPort(configInputPort);
  }

  @Override
  public Boolean caseFifo(Fifo object) {

    // Is the fifo connecting two actors of the desired subgraph?
    final boolean betweenActorsOfSubGraph = this.subGraphActors.contains(object.getTarget())
        && this.subGraphActors.contains(object.getSource());

    // If fifo should be contained in the subgraph, add it.
    if (betweenActorsOfSubGraph && !this.visitedFifo.contains(object)) {
      this.visitedFifo.add(object);
      this.subGraph.addFifo(object);

      // If there is a delay, add it into the subgraph
      final Delay delay = object.getDelay();
      if (delay != null) {
        this.subGraph.addDelay(delay);
        if (delay.getLevel().equals(PersistenceLevel.NONE) && delay.hasGetterActor()) {

          for (final Port delayPort : delay.getDelayActor().getAllPorts()) {
            doSwitch(delayPort);
          }
        }
      }
    }
    return !betweenActorsOfSubGraph;
  }
}
