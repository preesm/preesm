/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Alexandre Honorat <ahonorat@insa-rennes.fr> (2018)
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
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
/**
 *
 */
package org.preesm.algorithm.pisdf.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.PortMemoryAnnotation;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.workflow.WorkflowException;

/**
 * @author farresti
 *
 *         This structure is wrapper around the PiGraph class that provides multiple useful methods for accessing
 *         special properties of a PiGraph rapidly
 *
 */
public class PiMMHandler {
  /** The graph. */
  private PiGraph graph;

  /** List of children graph */
  private List<PiMMHandler> childrenList;

  /**
   * List of connectedComponents contained in a graph
   *
   * {@literal text
   * A Connected Component (CC) is defined as fully connected list of vertex inside a graph..
   * A PiSDF graph may contain multiple CCs not linked together.
   *
   * Example of a given PiGraph:
   *  ______________________________________
   * |                                      |
   * |    V - V0      Connected component 1 |
   * |     \                                |
   * |      V1                              |
   * |                                      |
   * |        C                             |
   * |       / \                            |
   * |  I - A - B     Connected component 2 |
   * |______________________________________|
   *  }
   */
  private List<List<AbstractActor>> listConnectedComponents;

  /** List of connectedComponents without interface actors contained in the PiGraph graph */
  private List<List<AbstractActor>> listCCWOInterfaces;

  private static void generateChildren(final PiGraph graph, final List<PiMMHandler> childrenList) {
    for (final PiGraph g : graph.getChildrenGraphs()) {
      childrenList.add(new PiMMHandler(g));
    }
  }

  /**
   * Instantiates a new PiMMHAndler object
   *
   * @param graph
   *          graph on which we are working
   */
  public PiMMHandler(final PiGraph graph) {
    this.graph = graph;
    this.childrenList = new ArrayList<>();
    this.listConnectedComponents = new ArrayList<>();
    this.listCCWOInterfaces = new ArrayList<>();
    PiMMHandler.generateChildren(this.graph, this.childrenList);
  }

  /*
   * Void constructor to access the PiMMHandlerException
   */
  public PiMMHandler() {

  }

  /**
   *
   * @return associated PiGraph
   */
  public PiGraph getReferenceGraph() {
    return this.graph;
  }

  /**
   *
   * @return List of children PiMMHandler (read only access)
   */
  public List<PiMMHandler> getChildrenGraphsHandler() {
    return Collections.unmodifiableList(this.childrenList);
  }

  /**
   * Add an abstract actor to the associated graph and updates all properties of the handler.
   *
   * @param actor
   *          the actor to add
   */
  public void addAbstractActor(final AbstractActor actor) {
    // Special case of delay
    this.graph.addActor(actor);
    // TODO
    // Update properties
  }

  /**
   * Test if a given CC contains an interface actor or not.
   *
   *
   * @param cc
   *          the connected component to test
   * @return true if an InterfaceActor has been found, false else
   */
  private static boolean containsInterfaceActors(final List<AbstractActor> cc) {
    for (final AbstractActor actor : cc) {
      if (actor instanceof InterfaceActor) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the FIFOs contained in a given connectedComponent.<br>
   * The function is coherent with the type of connectedComponent passed as input. <br>
   * This means that if no interface actor are present in the CC, the function will not return any FIFO connected to an
   * interface actor.
   *
   * First call to the method will be slower as it needs to extract the FIFOs.<br>
   * Subsequent calls will directly return the corresponding list.
   *
   * @param cc
   *          the connectedComponent to evaluate
   * @return all FIFOs contained in the connectedComponent, null if the connectedComponent is empty @ the
   *         PiMMHandlerException exception
   */
  public List<Fifo> getFifosFromCC(final List<AbstractActor> cc) {
    if (cc.isEmpty()) {
      PreesmLogger.getLogger().log(Level.INFO, "No FIFOs to extrac, empty connectedComponent.");
      return Collections.<Fifo>emptyList();
    }
    final boolean containsInterfaceActors = PiMMHandler.containsInterfaceActors(cc);
    final List<Fifo> fifos = new ArrayList<>();
    for (final AbstractActor actor : cc) {
      extractFifosFromActor(containsInterfaceActors, actor, fifos);
    }
    return fifos;
  }

  /**
   * Extract the FIFOs connected to a given actor.
   *
   * @param containsInterfaceActors
   *          this boolean ensures that only FIFO leading to actors contained in the CC list will be returned.
   * @param actor
   *          the actor to evaluate
   * @param fifos
   *          list of Fifo to update @ the PiMMHandlerException exception
   */
  private void extractFifosFromActor(final boolean containsInterfaceActors, final AbstractActor actor,
      final List<Fifo> fifos) {
    for (final DataPort port : actor.getAllDataPorts()) {
      final Fifo fifo = port.getFifo();
      if (fifo == null) {
        throw new PreesmException(PiMMHandler.noFIFOExceptionMessage(actor, port));
      }
      final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
      final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
      if (!containsInterfaceActors
          && ((sourceActor instanceof InterfaceActor) || (targetActor instanceof InterfaceActor))) {
        continue;
      }
      if (!fifos.contains(fifo)) {
        fifos.add(fifo);
      }
    }
  }

  /**
   * Returns the FIFOs of a given connectedComponent. Self-loop FIFOs are ignored. *
   *
   * @param cc
   *          the connectedComponent to evaluate
   * @return all FIFOs contained in the connectedComponent, null if the subgraph is empty @ the PiMMHandlerException
   *         exception
   */
  public List<Fifo> getFifosFromCCWOSelfLoop(final List<AbstractActor> cc) {
    final List<Fifo> fifos = getFifosFromCC(cc);
    fifos.removeIf(fifo -> (fifo.getSourcePort().getContainingActor() == fifo.getTargetPort().getContainingActor()));
    return fifos;
  }

  /**
   * This method returns the CCs contained in the reference PiSDF graph.
   *
   * The CCs returned by this method contain PiMM interface actors. <br>
   * Use getAllCCWOInterfaces to get CCs without interface actors.
   *
   * First access can be slow since the List of CCs has to be computed.
   *
   * @return all CCs contained in the reference graph (read only access) @ the PiMMHandlerException exception
   */
  public List<List<AbstractActor>> getAllConnectedComponents() {
    if (this.listConnectedComponents.isEmpty()) {
      PiMMHandler.ccsFetcher(this.graph, this.listConnectedComponents);
    }
    return Collections.unmodifiableList(this.listConnectedComponents);
  }

  /**
   * This method returns the CCs contained in the reference PiSDF graph. <br>
   * The returned List does not contain any interface actors.
   *
   * First access can be slow since the List of CCs has to be computed. <br>
   * This method uses getAllConnectedComponents to extract connected components, i.e a call to getAllConnectedComponents
   * afterward will be fast.
   *
   * @return all CCs contained in the associated graph (read only access) @ the PiMMHandlerException exception
   */
  public List<List<AbstractActor>> getAllConnectedComponentsWOInterfaces() {
    // First fetch all subgraphs
    if (this.listConnectedComponents.isEmpty()) {
      PiMMHandler.ccsFetcher(this.graph, this.listConnectedComponents);
    }
    // Now remove interfaces from subgraphs
    if (this.listCCWOInterfaces.isEmpty()) {
      PiMMHandler.ccsWOInterfacesFetcher(this.listConnectedComponents, this.listCCWOInterfaces);
    }
    return Collections.unmodifiableList(this.listCCWOInterfaces);
  }

  /**
   * Fetch all subgraphs contained in a PiSDF graph. All subgraphs returned by this method do not contain interface
   * actors.
   *
   * @param listCCs
   *          the full subgraphs
   * @param listCCsWOInterfaces
   *          list of subgraphs without interface actors to be updated
   */
  private static void ccsWOInterfacesFetcher(final List<List<AbstractActor>> listCCs,
      final List<List<AbstractActor>> listCCsWOInterfaces) {
    if (listCCs.isEmpty()) {
      return;
    }
    for (final List<AbstractActor> cc : listCCs) {
      final List<AbstractActor> ccWOInterfaces = new ArrayList<>(cc);
      ccWOInterfaces.removeIf(actor -> actor instanceof InterfaceActor);
      if (!ccWOInterfaces.isEmpty()) {
        listCCsWOInterfaces.add(ccWOInterfaces);
      }
    }
  }

  /**
   * Fetch all subgraphs contained in a PiSDF graph.
   *
   * @param graph
   *          the graph to process
   * @param listCCs
   *          list of subgraphs to be updated @ the PiMMHandlerException exception
   */
  private static void ccsFetcher(final PiGraph graph, final List<List<AbstractActor>> listCCs) {
    // Fetch all actors without interfaces in the PiGraph
    final List<AbstractActor> fullActorList = new ArrayList<>();
    fullActorList.addAll(graph.getActors());
    for (final AbstractActor actor : fullActorList) {
      // Ignore unused delay actor
      if ((actor instanceof DelayActor) && !((DelayActor) actor).getLinkedDelay().isDynamic()) {
        continue;
      }
      boolean alreadyContained = false;
      for (final List<AbstractActor> cc : listCCs) {
        if (cc.contains(actor)) {
          alreadyContained = true;
          break;
        }
      }
      if (!alreadyContained) {
        final List<AbstractActor> cc = new ArrayList<>();
        cc.add(actor);
        PiMMHandler.iterativeCCFetcher(actor, cc);
        listCCs.add(cc);
      }
    }
  }

  /**
   * Iterative function of ccsFetcher.
   *
   * @param actor
   *          the current actor
   * @param cc
   *          the current connected component @ the PiMMHandlerException exception
   */
  private static void iterativeCCFetcher(final AbstractActor actor, final List<AbstractActor> cc) {
    for (final DataOutputPort output : actor.getDataOutputPorts()) {
      final Fifo fifo = output.getOutgoingFifo();
      if (fifo == null) {
        throw new PreesmException(PiMMHandler.noFIFOExceptionMessage(actor, output));
      }
      final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
      if (!cc.contains(targetActor)) {
        cc.add(targetActor);
        PiMMHandler.iterativeCCFetcher(targetActor, cc);
      }
    }
    for (final DataInputPort input : actor.getDataInputPorts()) {
      final Fifo fifo = input.getIncomingFifo();
      if (fifo == null) {
        throw new PreesmException(PiMMHandler.noFIFOExceptionMessage(actor, input));
      }
      final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
      if (!cc.contains(sourceActor)) {
        cc.add(sourceActor);
        PiMMHandler.iterativeCCFetcher(sourceActor, cc);
      }
    }
  }

  /**
   *
   * @return Formatted message for a no FIFO connected exception
   */
  private static String noFIFOExceptionMessage(final AbstractActor actor, final DataPort port) {
    return "Actor [" + actor.getName() + "] data port [" + port.getName() + "] is not connected to a FIFO.";
  }

  /**
   * Resolve all parameter values of the reference PiGraph and its child sub-graph.
   *
   * @ the PiMMHandlerException exception
   */
  public void resolveAllParameters() {
    final PiMMResolverVisitor piMMResolverVisitor = new PiMMResolverVisitor(new LinkedHashMap<>());
    piMMResolverVisitor.doSwitch(this.graph);
  }

  /**
   * Remove the persistence levels and replace them with the appropriate interfaces.
   *
   * @ the PiMMHandlerException exception
   */
  public void removePersistence() {
    for (final Fifo fifo : this.graph.getFifosWithDelay()) {
      final Delay delay = fifo.getDelay();
      // 0. Rename all the data ports of delay actors
      delay.getActor().getDataInputPort().setName(fifo.getTargetPort().getName());
      delay.getActor().getDataOutputPort().setName(fifo.getSourcePort().getName());
      // 1. For the top graph, we convert every locally persistent delays to permanent ones.
      if (delay.getLevel().equals(PersistenceLevel.LOCAL)) {
        delay.setLevel(PersistenceLevel.PERMANENT);
      }
    }
    // 2. We deal with hierarchical stuff
    for (final PiGraph g : graph.getChildrenGraphs()) {
      recursiveRemovePersistence(g);
    }
  }

  private void recursiveRemovePersistence(final PiGraph graph) {
    // We assume that if the user want to make a delay persist across multiple levels,
    // he did it explicitly.
    for (final Fifo fifo : graph.getFifosWithDelay()) {
      final Delay delay = fifo.getDelay();
      String delayShortID = delay.getShortId();
      delay.getActor().getDataInputPort().setName(fifo.getTargetPort().getName());
      delay.getActor().getDataOutputPort().setName(fifo.getSourcePort().getName());
      if (delay.getLevel().equals(PersistenceLevel.LOCAL)) {
        if (delay.hasGetterActor() || delay.hasSetterActor()) {
          throw new PreesmException(
              "Delay with local persistence can not be connected to a setter nor a getter actor.");
        }
        delay.setName(delayShortID);
        replaceLocalDelay(graph, delay);
      } else if (delay.getLevel().equals(PersistenceLevel.PERMANENT)) {
        if (delay.hasGetterActor() || delay.hasSetterActor()) {
          throw new PreesmException(
              "Delay with global persistence can not be connected to a setter nor a getter actor.");
        }
        // In the case of a permanent delay we have to make it go up to the top.
        PiGraph currentGraph = graph;
        Delay currentDelay = delay;
        do {
          final Delay newDelay = replaceLocalDelay(currentGraph, currentDelay);
          newDelay.setLevel(PersistenceLevel.PERMANENT);
          // Update current graph and delay
          currentGraph = currentGraph.getContainingPiGraph();
          currentDelay = newDelay;
        } while (currentGraph.getContainingPiGraph() != null);
      } else {
        if (((delay.hasSetterActor()) && !(delay.hasGetterActor()))
            || ((delay.hasGetterActor()) && (!delay.hasSetterActor()))) {
          throw new PreesmException("Asymetric configuration for delay setter / getter actor is not yet supported.\n"
              + "Please Contact PREESM developers.");
        }
      }
    }
    for (final PiGraph g : graph.getChildrenGraphs()) {
      recursiveRemovePersistence(g);
    }
  }

  /**
   * Replace a locally persistent delay.
   *
   * @param graph
   *          the graph in which the delay is contained
   * @param delay
   *          the delay
   * @return newly created delay in the upper graph
   */
  private Delay replaceLocalDelay(final PiGraph graph, final Delay delay) {
    final Fifo containingFifo = delay.getContainingFifo();
    final String type = containingFifo.getType();
    final String delayExpression = delay.getSizeExpression().getExpressionAsString();
    // 1. First we remove the level of persistence associated with the delay
    delay.setLevel(PersistenceLevel.LOCAL);

    // 2. We create the interfaces that we need to communicate with the upper level
    // Add the DataInputInterface to the graph
    final DataInputInterface setterIn = PiMMUserFactory.instance.createDataInputInterface();
    // We remove the "delay_" mention at the beginning
    // TODO: fix this with much shorter name !
    final DataInputPort targetPort = containingFifo.getTargetPort();
    final String setterName = "in_" + targetPort.getContainingActor().getName() + "_" + targetPort.getName();
    setterIn.setName(setterName);
    setterIn.getDataPort().setName(setterName);
    setterIn.getDataPort().setAnnotation(PortMemoryAnnotation.READ_ONLY);
    setterIn.getDataPort().setExpression(delayExpression);
    // Add the DataOutputInterface to the graph
    final DataOutputInterface getterOut = PiMMUserFactory.instance.createDataOutputInterface();
    final DataOutputPort sourcePort = containingFifo.getSourcePort();
    final String getterName = "out_" + sourcePort.getContainingActor().getName() + "_" + sourcePort.getName();
    getterOut.setName(getterName);
    getterOut.getDataPort().setName(getterName);
    getterOut.getDataPort().setAnnotation(PortMemoryAnnotation.WRITE_ONLY);
    getterOut.getDataPort().setExpression(delayExpression);
    graph.addActor(setterIn);
    graph.addActor(getterOut);

    // 3. Now we connect the newly created interfaces to the delay
    // Add the setter FIFO
    final Fifo fifoSetter = PiMMUserFactory.instance.createFifo();
    fifoSetter.setType(type);
    // Connect the setter interface to the delay
    fifoSetter.setSourcePort((DataOutputPort) setterIn.getDataPort());
    final DelayActor originalDelayActor = delay.getActor();
    fifoSetter.setTargetPort(originalDelayActor.getDataInputPort());
    // Add the getter FIFO
    final Fifo fifoGetter = PiMMUserFactory.instance.createFifo();
    fifoGetter.setType(type);
    // Connect the delay interface to the getter
    fifoGetter.setSourcePort(originalDelayActor.getDataOutputPort());
    fifoGetter.setTargetPort((DataInputPort) getterOut.getDataPort());
    graph.addFifo(fifoSetter);
    graph.addFifo(fifoGetter);

    // 4. Now we create the feed back FIFO in the upper-level
    final Fifo fifoPersistence = PiMMUserFactory.instance.createFifo();
    fifoPersistence.setType(type);
    // 5. We set the expression of the corresponding ports on the graph
    final DataInputPort inPort = (DataInputPort) setterIn.getGraphPort();
    // Add the input port
    inPort.setExpression(delayExpression);
    inPort.setAnnotation(PortMemoryAnnotation.WRITE_ONLY);
    // Add the output port
    final DataOutputPort outPort = (DataOutputPort) getterOut.getGraphPort();
    outPort.setExpression(delayExpression);
    outPort.setAnnotation(PortMemoryAnnotation.READ_ONLY);
    // Now set the source / target port of the FIFO
    fifoPersistence.setSourcePort(outPort);
    fifoPersistence.setTargetPort(inPort);
    graph.getContainingPiGraph().addFifo(fifoPersistence);

    // 5. Finally we add a delay to this FIFO as well
    final Delay delayPersistence = PiMMUserFactory.instance.createDelay();
    final String name = graph.getName() + "_" + setterIn.getName() + "__" + getterOut.getName();
    delayPersistence.setName(name);
    delayPersistence.setLevel(PersistenceLevel.NONE);
    delayPersistence.setExpression(delayExpression);
    final DelayActor newDelayActor = delayPersistence.getActor();
    newDelayActor.setName(name);
    newDelayActor.getDataInputPort().setName(originalDelayActor.getDataInputPort().getName());
    newDelayActor.getDataOutputPort().setName(originalDelayActor.getDataOutputPort().getName());

    fifoPersistence.setDelay(delayPersistence);
    graph.getContainingPiGraph().addDelay(delayPersistence);

    return delayPersistence;
  }

  /**
   * azmokfaze
   *
   * @param graphBRV
   *          Repetition Vector as a map.
   */
  public static void checkPeriodicity(final Map<AbstractVertex, Long> graphBRV) {

    final Map<PiGraph, Long> levelBRV = new HashMap<>();
    final Map<Long, List<Actor>> mapGraphPeriods = new HashMap<>();

    for (final Entry<AbstractVertex, Long> en : graphBRV.entrySet()) {
      final AbstractVertex av = en.getKey();
      final PiGraph container = av.getContainingPiGraph();
      if (!levelBRV.containsKey(container)) {
        levelBRV.put(container, getHierarchichalRV(container, graphBRV));
      }
      if (av instanceof PiGraph) {
        continue;
      } else if (av instanceof Actor) {
        final Actor actor = (Actor) av;
        final long actorPeriod = actor.getPeriod().evaluate();
        if (actorPeriod > 0) {
          final Long actorRV = en.getValue() * levelBRV.get(container);
          final long period = actorRV * actorPeriod;
          if (!mapGraphPeriods.containsKey(period)) {
            mapGraphPeriods.put(period, new ArrayList<>());
          }
          mapGraphPeriods.get(period).add(actor);
        }
      }
    }
    if (mapGraphPeriods.size() > 1) {
      StringBuilder sb = new StringBuilder("Different graph periods have been found:");
      for (final Entry<Long, List<Actor>> en : mapGraphPeriods.entrySet()) {
        sb.append("\n" + en.getKey() + " from: ");
        for (Actor a : en.getValue()) {
          sb.append(a.getName() + " / ");
        }
      }
      sb.append("\n");
      PreesmLogger.getLogger().log(Level.SEVERE, sb.toString());
      throw new WorkflowException("Periods are not consistent, abandon.");
    } else if (mapGraphPeriods.size() == 1) {
      long period = 0;
      for (Long p : mapGraphPeriods.keySet()) {
        period = p;
      }
      PreesmLogger.getLogger().log(Level.INFO, "The graph period is set to: " + period);
    } else {
      PreesmLogger.getLogger().log(Level.INFO, "No period for the graph.");
    }
  }

  /**
   * zerze
   *
   * @param graph
   *          zerz
   * @param graphBRV
   *          zerze
   * @return rggr
   */
  public static Long getHierarchichalRV(final PiGraph graph, final Map<AbstractVertex, Long> graphBRV) {
    // We need to get the repetition vector of the graph
    final Long graphRV = graphBRV.get(graph) == null ? 1 : graphBRV.get(graph);
    // We also need to get the total repetition vector of the hierarchy to correctly flatten the hierarchy
    Long graphHierarchicallRV = (long) (1);
    PiGraph containingGraph = graph.getContainingPiGraph();
    while (containingGraph != null) {
      final Long currentGraphRV = graphBRV.get(containingGraph) == null ? 1 : graphBRV.get(containingGraph);
      graphHierarchicallRV = graphHierarchicallRV * currentGraphRV;
      containingGraph = containingGraph.getContainingPiGraph();
    }
    // We update the value of the graphRV accordingly
    return graphRV * graphHierarchicallRV;
  }

}
