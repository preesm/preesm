/**
 *
 */
package org.ietr.preesm.pimm.algorithm.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.experiment.model.factory.PiMMUserFactory;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.DelayActor;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.PersistenceLevel;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation;

/**
 * @author farresti
 *
 *         This structure is wrapper around the PiGraph class that provides multiple useful methods for accessing special properties of a PiGraph rapidly
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
   * This means that if no interface actor are present in the CC, the function will not return any FIFO connected to an interface actor.
   *
   * First call to the method will be slower as it needs to extract the FIFOs.<br>
   * Subsequent calls will directly return the corresponding list.
   *
   * @param cc
   *          the connectedComponent to evaluate
   * @return all FIFOs contained in the connectedComponent, null if the connectedComponent is empty
   * @throws PiMMHelperException
   *           the PiMMHandlerException exception
   */
  public List<Fifo> getFifosFromCC(final List<AbstractActor> cc) throws PiMMHelperException {
    if (cc.isEmpty()) {
      WorkflowLogger.getLogger().log(Level.INFO, "No FIFOs to extrac, empty connectedComponent.");
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
   *          list of Fifo to update
   * @throws PiMMHelperException
   *           the PiMMHandlerException exception
   */
  private void extractFifosFromActor(final boolean containsInterfaceActors, final AbstractActor actor, final List<Fifo> fifos) throws PiMMHelperException {
    for (final DataPort port : actor.getAllDataPorts()) {
      final Fifo fifo = port.getFifo();
      if (fifo == null) {
        throw new PiMMHelperException(PiMMHandler.noFIFOExceptionMessage(actor, port));
      }
      final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
      final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
      if (!containsInterfaceActors && ((sourceActor instanceof InterfaceActor) || (targetActor instanceof InterfaceActor))) {
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
   * @return all FIFOs contained in the connectedComponent, null if the subgraph is empty
   * @throws PiMMHelperException
   *           the PiMMHandlerException exception
   */
  public List<Fifo> getFifosFromCCWOSelfLoop(final List<AbstractActor> cc) throws PiMMHelperException {
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
   * @return all CCs contained in the reference graph (read only access)
   * @throws PiMMHelperException
   *           the PiMMHandlerException exception
   */
  public List<List<AbstractActor>> getAllConnectedComponents() throws PiMMHelperException {
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
   * This method uses getAllConnectedComponents to extract connected components, i.e a call to getAllConnectedComponents afterward will be fast.
   *
   * @return all CCs contained in the associated graph (read only access)
   * @throws PiMMHelperException
   *           the PiMMHandlerException exception
   */
  public List<List<AbstractActor>> getAllConnectedComponentsWOInterfaces() throws PiMMHelperException {
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
   * Fetch all subgraphs contained in a PiSDF graph. All subgraphs returned by this method do not contain interface actors.
   *
   * @param listCCs
   *          the full subgraphs
   * @param listCCsWOInterfaces
   *          list of subgraphs without interface actors to be updated
   */
  private static void ccsWOInterfacesFetcher(final List<List<AbstractActor>> listCCs, final List<List<AbstractActor>> listCCsWOInterfaces) {
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
   *          list of subgraphs to be updated
   * @throws PiMMHelperException
   *           the PiMMHandlerException exception
   */
  private static void ccsFetcher(final PiGraph graph, final List<List<AbstractActor>> listCCs) throws PiMMHelperException {
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
   *          the current connected component
   * @throws PiMMHelperException
   *           the PiMMHandlerException exception
   */
  private static void iterativeCCFetcher(final AbstractActor actor, final List<AbstractActor> cc) throws PiMMHelperException {
    for (final DataOutputPort output : actor.getDataOutputPorts()) {
      final Fifo fifo = output.getOutgoingFifo();
      if (fifo == null) {
        throw new PiMMHelperException(PiMMHandler.noFIFOExceptionMessage(actor, output));
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
        throw new PiMMHelperException(PiMMHandler.noFIFOExceptionMessage(actor, input));
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
   * @throws PiMMHelperException
   *           the PiMMHandlerException exception
   */
  public void resolveAllParameters() throws PiMMHelperException {
    final PiMMResolverVisitor piMMResolverVisitor = new PiMMResolverVisitor(new LinkedHashMap<>());
    piMMResolverVisitor.doSwitch(this.graph);
  }

  /**
   * Remove the persistence levels and replace them with the appropriate interfaces.
   * 
   * @throws PiMMHelperException
   *           the PiMMHandlerException exception
   */
  public void removePersistence() throws PiMMHelperException {
    // 1. We deal with hierarchical stuff
    recursiveRemovePersistence(graph);
    // 2. For the top graph, we convert every locally persistent delays to permanent ones.
    for (final Fifo fifo : this.graph.getFifosWithDelay()) {
      final Delay delay = fifo.getDelay();
      if (delay.getLevel().equals(PersistenceLevel.LOCAL)) {
        delay.setLevel(PersistenceLevel.PERMANENT);
      }
    }
  }

  private void recursiveRemovePersistence(final PiGraph graph) throws PiMMHelperException {
    final boolean isTop = graph.getContainingPiGraph() == null;
    if (!isTop) {
      // We assume that if the user want to make a delay persist across multiple levels,
      // he did it explicitly.
      for (final Fifo fifo : graph.getFifosWithDelay()) {
        final Delay delay = fifo.getDelay();
        String delayShortID = delay.getShortId();
        if (delay.getLevel().equals(PersistenceLevel.LOCAL)) {
          if (delay.hasGetterActor() || delay.hasSetterActor()) {
            throw new PiMMHelperException("Delay with local persistence can not be connected to a setter nor a getter actor.");
          }
          delay.setName(delayShortID);
          replaceLocalDelay(graph, delay);
        } else if (delay.getLevel().equals(PersistenceLevel.PERMANENT)) {
          if (delay.hasGetterActor() || delay.hasSetterActor()) {
            throw new PiMMHelperException("Delay with global persistence can not be connected to a setter nor a getter actor.");
          }
          // In the case of a permanent delay we have to make it go up to the top.
          PiGraph currentGraph = graph;
          Delay currentDelay = delay;
          do {
            currentDelay.setName(delayShortID);
            final Delay newDelay = replaceLocalDelay(currentGraph, currentDelay);
            newDelay.setLevel(PersistenceLevel.PERMANENT);
            // Update current graph and delay
            delayShortID = currentGraph.getName() + "_" + delayShortID;
            currentGraph = currentGraph.getContainingPiGraph();
            currentDelay = newDelay;
          } while (currentGraph.getContainingPiGraph() != null);
        } else {
          if (((delay.hasSetterActor()) && !(delay.hasGetterActor())) || ((delay.hasGetterActor()) && (!delay.hasSetterActor()))) {
            throw new PiMMHelperException("Asymetric configuration for delay setter / getter actor is not yet supported.\nPlease Contact PREESM developers.");
          }
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
    final String type = delay.getContainingFifo().getType();
    final String delayExpression = delay.getSizeExpression().getExpressionString();
    // 1. First we remove the level of persistence associated with the delay
    delay.setLevel(PersistenceLevel.NONE);

    // 2. We create the interfaces that we need to communicate with the upper level
    // Add the DataInputInterface to the graph
    final DataInputInterface setterIn = PiMMUserFactory.instance.createDataInputInterface();
    // We remove the "delay_" mention at the beginning
    // TODO: fix this with much shorter name !
    final String delayShortID = delay.getName();
    final String setterName = "in_" + delayShortID;
    setterIn.setName(setterName);
    setterIn.getDataPort().setName(setterName);
    setterIn.getDataPort().setAnnotation(PortMemoryAnnotation.READ_ONLY);
    setterIn.getDataPort().getExpression().setExpressionString(delayExpression);
    // Add the DataOutputInterface to the graph
    final DataOutputInterface getterOut = PiMMUserFactory.instance.createDataOutputInterface();
    final String getterName = "out_" + delayShortID;
    getterOut.setName(getterName);
    getterOut.getDataPort().setName(getterName);
    getterOut.getDataPort().setAnnotation(PortMemoryAnnotation.WRITE_ONLY);
    getterOut.getDataPort().getExpression().setExpressionString(delayExpression);
    graph.addActor(setterIn);
    graph.addActor(getterOut);

    // 3. Now we connect the newly created interfaces to the delay
    // Add the setter FIFO
    final Fifo fifoSetter = PiMMUserFactory.instance.createFifo();
    fifoSetter.setType(type);
    // Connect the setter interface to the delay
    fifoSetter.setSourcePort((DataOutputPort) setterIn.getDataPort());
    fifoSetter.setTargetPort(delay.getActor().getDataInputPort());
    // Add the getter FIFO
    final Fifo fifoGetter = PiMMUserFactory.instance.createFifo();
    fifoGetter.setType(type);
    // Connect the delay interface to the getter
    fifoGetter.setSourcePort(delay.getActor().getDataOutputPort());
    fifoGetter.setTargetPort((DataInputPort) getterOut.getDataPort());
    graph.addFifo(fifoSetter);
    graph.addFifo(fifoGetter);

    // 4. Now we create the feed back FIFO in the upper-level
    final Fifo fifoPersistence = PiMMUserFactory.instance.createFifo();
    fifoPersistence.setType(type);
    // 5. We set the expression of the corresponding ports on the graph
    final DataInputPort inPort = (DataInputPort) setterIn.getGraphPort();
    // Add the input port
    inPort.getExpression().setExpressionString(delayExpression);
    inPort.setAnnotation(PortMemoryAnnotation.WRITE_ONLY);
    // Add the output port
    final DataOutputPort outPort = (DataOutputPort) getterOut.getGraphPort();
    outPort.getExpression().setExpressionString(delayExpression);
    outPort.setAnnotation(PortMemoryAnnotation.READ_ONLY);
    // Now set the source / target port of the FIFO
    fifoPersistence.setSourcePort(outPort);
    fifoPersistence.setTargetPort(inPort);
    graph.getContainingPiGraph().addFifo(fifoPersistence);

    // 5. Finally we add a delay to this FIFO as well
    final Delay delayPersistence = PiMMUserFactory.instance.createDelay();
    delayPersistence.setLevel(PersistenceLevel.NONE);
    delayPersistence.getSizeExpression().setExpressionString(delayExpression);
    fifoPersistence.setDelay(delayPersistence);
    graph.getContainingPiGraph().addDelay(delayPersistence);

    return delayPersistence;
  }

}
