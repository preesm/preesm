/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2018 - 2020)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Dylan Gageot [gageot.dylan@gmail.com] (2019)
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
/**
 *
 */
package org.preesm.model.pisdf.statictools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PeriodicElement;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.PortMemoryAnnotation;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.factory.PiMMUserFactory;

/**
 * @author farresti
 *
 *         This structure is wrapper around the PiGraph class that provides multiple useful methods for accessing
 *         special properties of a PiGraph rapidly
 *
 * @deprecated This class will be removed
 */
@Deprecated
public class PiMMHelper {

  private PiMMHelper() {
    // forbid instantiation
  }

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
  public static List<Fifo> getFifosFromCC(final List<AbstractActor> cc) {
    if (cc.isEmpty()) {
      PreesmLogger.getLogger().log(Level.INFO, "No FIFOs to extrac, empty connectedComponent.");
      return Collections.emptyList();
    }
    final boolean containsInterfaceActors = containsInterfaceActors(cc);
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
  private static void extractFifosFromActor(final boolean containsInterfaceActors, final AbstractActor actor,
      final List<Fifo> fifos) {
    for (final DataPort port : actor.getAllDataPorts()) {
      final Fifo fifo = port.getFifo();
      if (fifo == null) {
        throw new PreesmRuntimeException(
            "Actor [" + actor.getVertexPath() + "] data port [" + port.getName() + "] is not connected to a FIFO.");
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
  public static List<Fifo> getFifosFromCCWOSelfLoop(final List<AbstractActor> cc) {
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
  public static List<List<AbstractActor>> getAllConnectedComponents(final PiGraph graph) {
    return PiMMHelper.ccsFetcher(graph);
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
  public static List<List<AbstractActor>> getAllConnectedComponentsWOInterfaces(final PiGraph graph) {
    // First fetch all subgraphs
    final List<List<AbstractActor>> ccsFetcher = PiMMHelper.ccsFetcher(graph);
    // Now remove interfaces from subgraphs
    return PiMMHelper.ccsWOInterfacesFetcher(ccsFetcher);
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
  private static List<List<AbstractActor>> ccsWOInterfacesFetcher(final List<List<AbstractActor>> listCCs) {
    final List<List<AbstractActor>> listCCsWOInterfaces = new ArrayList<>();
    for (final List<AbstractActor> cc : listCCs) {
      final List<AbstractActor> ccWOInterfaces = new ArrayList<>(cc);
      ccWOInterfaces.removeIf(actor -> actor instanceof InterfaceActor);
      if (!ccWOInterfaces.isEmpty()) {
        listCCsWOInterfaces.add(ccWOInterfaces);
      }
    }
    return listCCsWOInterfaces;
  }

  /**
   * Fetch all subgraphs contained in a PiSDF graph.
   *
   * @param graph
   *          the graph to process
   * @param listCCs
   *          list of subgraphs to be updated @ the PiMMHandlerException exception
   */
  private static List<List<AbstractActor>> ccsFetcher(final PiGraph graph) {
    // Fetch all actors without interfaces in the PiGraph
    final List<List<AbstractActor>> listCCs = new ArrayList<>();
    final List<AbstractActor> fullActorList = new ArrayList<>(graph.getActors());
    for (final AbstractActor actor : fullActorList) {
      // Ignore unused delay actor
      if ((actor instanceof final DelayActor delayActor) && !delayActor.getLinkedDelay().isDynamic()) {
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
        PiMMHelper.iterativeCCFetcher(actor, cc);
        listCCs.add(cc);
      }
    }
    return listCCs;
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
    for (final ConfigOutputPort output : actor.getConfigOutputPorts()) {
      final Fifo fifo = output.getOutgoingFifo();
      if (fifo == null && output.getOutgoingDependencies().isEmpty()) {
        throw new PreesmRuntimeException("Actor [" + actor.getVertexPath() + "] config data output port ["
            + output.getName() + "] is not connected to a FIFO nor a parameter.");
      }
      final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
      if (!cc.contains(targetActor)) {
        cc.add(targetActor);
        PiMMHelper.iterativeCCFetcher(targetActor, cc);
      }
    }
    for (final DataOutputPort output : actor.getDataOutputPorts()) {
      final Fifo fifo = output.getOutgoingFifo();
      if (fifo == null) {
        throw new PreesmRuntimeException("Actor [" + actor.getVertexPath() + "] data output port [" + output.getName()
            + "] is not connected to a FIFO.");
      }
      final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
      if (!cc.contains(targetActor)) {
        cc.add(targetActor);
        PiMMHelper.iterativeCCFetcher(targetActor, cc);
      }
    }
    for (final DataInputPort input : actor.getDataInputPorts()) {
      final Fifo fifo = input.getIncomingFifo();
      if (fifo == null) {
        throw new PreesmRuntimeException("Actor [" + actor.getVertexPath() + "] data input port [" + input.getName()
            + "] is not connected to a FIFO.");
      }
      final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
      if (!cc.contains(sourceActor)) {
        cc.add(sourceActor);
        PiMMHelper.iterativeCCFetcher(sourceActor, cc);
      }
    }
  }

  /**
   * Resolve all parameter values of the reference PiGraph and its child sub-graph.
   *
   * @ the PiMMHandlerException exception
   */
  public static void resolveAllParameters(final PiGraph piGraph) {
    final PiSDFParameterResolverVisitor piMMResolverVisitor = new PiSDFParameterResolverVisitor();
    piMMResolverVisitor.doSwitch(piGraph);
  }

  /**
   * Remove dependencies of an actor, the configure input port if not used anymore, and also the actor itself from
   * graph.
   *
   * @param graph
   *          Container of elements to remove.
   * @param actor
   *          To remove from graph.
   */
  public static void removeActorAndDependencies(final PiGraph graph, final AbstractActor actor) {
    for (final ConfigInputPort cip : actor.getConfigInputPorts()) {
      final Dependency incomingDependency = cip.getIncomingDependency();
      graph.removeDependency(incomingDependency);
      final ISetter setter = incomingDependency.getSetter();
      setter.getOutgoingDependencies().remove(incomingDependency);
      if (setter instanceof final Parameter parameter && setter.getOutgoingDependencies().isEmpty()
          && !parameter.isConfigurationInterface()) {
        graph.removeParameter(parameter);
      }
    }
    PreesmLogger.getLogger().fine("Removing Actor: " + actor.getVertexPath());
    graph.removeActor(actor);
  }

  /**
   * Remove dependencies, actor and fifo from graph.
   *
   * @param graph
   *          Container of elements to remove.
   * @param fifo
   *          To remove from graph.
   * @param actor
   *          To remove from graph.
   */
  public static void removeActorAndFifo(final PiGraph graph, final Fifo fifo, final AbstractActor actor) {
    removeActorAndDependencies(graph, actor);
    graph.removeFifo(fifo);
  }

  /**
   * Removes all actors from flat PiGraph if they are not executed. Removes also fifo and ports having rates equal to 0.
   * If not flat, the graph is not modified.
   *
   * @param piGraph
   *          PiGraph to consider
   * @param brv
   *          Repetition vector
   */
  public static void removeNonExecutedActorsAndFifos(final PiGraph piGraph, final Map<AbstractVertex, Long> brv) {
    // remove unused fifos
    for (final Fifo f : piGraph.getFifos()) {
      final DataOutputPort dpi = f.getSourcePort();
      final DataInputPort dpo = f.getTargetPort();
      final long ri = dpi.getExpression().evaluate();
      final long ro = dpo.getExpression().evaluate();
      if (ri == 0 && ro == 0) {
        final Delay d = f.getDelay();
        f.assignDelay(null);
        if (d != null) {
          piGraph.removeDelay(d);
        }
        PreesmLogger.getLogger().fine("Removing unused Fifo: " + f.getId());
        piGraph.removeFifo(f);
        dpi.setOutgoingFifo(null);
        dpo.setIncomingFifo(null);
      }
    }

    for (final AbstractActor actor : piGraph.getActors()) {
      if (brv.getOrDefault(actor, 1L) == 0L) {
        removeActorAndDependencies(piGraph, actor);
      }
    }
    for (final PiGraph childGraph : piGraph.getChildrenGraphs()) {
      removeNonExecutedActorsAndFifos(childGraph, brv);
    }

    removeUnusedPorts(piGraph);

  }

  /**
   * Remove actor ports having no fifo (may happen if source or destination actor has 0 as repetition factor). Does not
   * look at delay actor.
   *
   * @param graph
   *          PiGraph to update
   */
  public static final void removeUnusedPorts(final PiGraph graph) {
    for (final AbstractActor aa : graph.getActors()) {
      final Set<DataInputPort> toRemoveIn = new HashSet<>();
      final Set<DataOutputPort> toRemoveOut = new HashSet<>();
      for (final DataPort p : aa.getAllDataPorts()) {
        final Fifo f = p.getFifo();
        if (f == null) {
          if (p instanceof final DataInputPort datainputport) {
            toRemoveIn.add(datainputport);
          } else if (p instanceof final DataOutputPort dataOutputport) {
            toRemoveOut.add(dataOutputport);
          }
        }
      }
      if ((aa instanceof JoinActor || aa instanceof RoundBufferActor)
          && toRemoveIn.size() < aa.getDataInputPorts().size()) {
        for (final DataInputPort p : toRemoveIn) {
          PreesmLogger.getLogger().fine("Removing unused input Port: " + aa.getVertexPath() + ":" + p.getName());
          aa.getDataInputPorts().remove(p);
        }
      } else if (!toRemoveIn.isEmpty() && !(aa instanceof DelayActor)) {
        throw new PreesmRuntimeException("After removing non executed actors, actor <" + aa.getVertexPath()
            + "> has input ports without fifo, this is not allowed except for special actors if not all ports.");
      }

      if ((aa instanceof ForkActor || aa instanceof BroadcastActor)
          && toRemoveOut.size() < aa.getDataOutputPorts().size()) {
        for (final DataOutputPort p : toRemoveOut) {
          PreesmLogger.getLogger().fine("Removing unused output Port: " + aa.getVertexPath() + ":" + p.getName());
          aa.getDataOutputPorts().remove(p);
        }
      } else if (!toRemoveOut.isEmpty() && !(aa instanceof DelayActor)) {
        throw new PreesmRuntimeException("After removing non executed actors, actor <" + aa.getVertexPath()
            + "> has output ports without fifo, this is not allowed except for special actors if not all ports.");
      }

    }
  }

  /**
   * Remove the persistence levels and replace them with the appropriate interfaces.
   *
   * @ the PiMMHandlerException exception
   */
  public static void removePersistence(final PiGraph piGraph) {
    final List<Delay> toRemove = new ArrayList<>();
    for (final Fifo fifo : piGraph.getFifosWithDelay()) {
      final Delay delay = fifo.getDelay();
      // 0. Rename all the data ports of delay actors
      delay.getActor().getDataInputPort().setName(fifo.getTargetPort().getName());
      delay.getActor().getDataOutputPort().setName(fifo.getSourcePort().getName());
      // 1. For the top graph, we convert every locally persistent delays to permanent ones.
      if (delay.getLevel().equals(PersistenceLevel.LOCAL)) {
        delay.setLevel(PersistenceLevel.PERMANENT);
      }
      if (delay.getSizeExpression().evaluate() == 0) {
        toRemove.add(delay);
      }
    }
    final StringBuilder sb = new StringBuilder("Following delays are removed since their size is 0: ");
    for (final Delay d : toRemove) {
      final Fifo f = d.getContainingFifo();
      f.assignDelay(null);
      piGraph.removeDelay(d);
      sb.append(d.getName() + "; ");
    }
    if (!toRemove.isEmpty()) {
      PreesmLogger.getLogger().log(Level.INFO, sb.toString());
    }
    // 2. We deal with hierarchical stuff
    for (final PiGraph g : piGraph.getChildrenGraphs()) {
      if (!g.isCluster()) {
        recursiveRemovePersistence(g);
      }
    }
  }

  private static void recursiveRemovePersistence(final PiGraph graph) {
    // We assume that if the user want to make a delay persist across multiple levels,
    // he did it explicitly.
    for (final Fifo fifo : graph.getFifosWithDelay()) {
      final Delay delay = fifo.getDelay();
      final String delayShortID = delay.getId();
      delay.getActor().getDataInputPort().setName(fifo.getTargetPort().getName());
      delay.getActor().getDataOutputPort().setName(fifo.getSourcePort().getName());
      if (delay.getLevel().equals(PersistenceLevel.LOCAL)) {
        if (delay.hasGetterActor() || delay.hasSetterActor()) {
          throw new PreesmRuntimeException(
              "Delay with local persistence can not be connected to a setter nor a getter actor.");
        }
        delay.setName(delayShortID);
        replaceLocalDelay(graph, delay);
      } else if (delay.getLevel().equals(PersistenceLevel.PERMANENT)) {
        if (delay.hasGetterActor() || delay.hasSetterActor()) {
          throw new PreesmRuntimeException(
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
      } else if (((delay.hasSetterActor()) && !(delay.hasGetterActor()))
          || ((delay.hasGetterActor()) && (!delay.hasSetterActor()))) {
        throw new PreesmRuntimeException(
            "Asymetric configuration for delay setter / getter actor is not yet supported.\n"
                + "Please Contact PREESM developers.");
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
  private static Delay replaceLocalDelay(final PiGraph graph, final Delay delay) {
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
    // Connect the setter interface to the delay
    final DelayActor originalDelayActor = delay.getActor();
    final Fifo fifoSetter = PiMMUserFactory.instance.createFifo((DataOutputPort) setterIn.getDataPort(),
        originalDelayActor.getDataInputPort(), type);
    // Add the getter FIFO
    // Connect the delay interface to the getter
    final Fifo fifoGetter = PiMMUserFactory.instance.createFifo(originalDelayActor.getDataOutputPort(),
        (DataInputPort) getterOut.getDataPort(), type);
    graph.addFifo(fifoSetter);
    graph.addFifo(fifoGetter);

    // 4. Now we create the feed back FIFO in the upper-level
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
    final Fifo fifoPersistence = PiMMUserFactory.instance.createFifo(outPort, inPort, type);
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

    fifoPersistence.assignDelay(delayPersistence);
    graph.getContainingPiGraph().addDelay(delayPersistence);

    return delayPersistence;
  }

  /**
   * Check if periods on actors are consistent. Set the graph period if some actors are periodic.
   *
   * @param graphBRV
   *          Repetition Vector as a map.
   * @throws PreesmRuntimeException
   *           If several actors define different graph periods.
   */
  public static void checkPeriodicity(final PiGraph piGraph, final Map<AbstractVertex, Long> graphBRV) {

    final long graphPeriod = piGraph.getPeriod().evaluate();

    final Map<PiGraph, Long> levelBRV = new LinkedHashMap<>();
    final Map<Long, List<AbstractVertex>> mapGraphPeriods = new LinkedHashMap<>();

    for (final Entry<AbstractVertex, Long> en : graphBRV.entrySet()) {

      final AbstractVertex av = en.getKey();
      final PiGraph container = av.getContainingPiGraph();
      if (!levelBRV.containsKey(container)) {
        levelBRV.put(container, getHierarchichalRV(container, graphBRV));
      }
      if (av instanceof final PeriodicElement actor) {
        final long actorPeriod = actor.getPeriod().evaluate();
        if (actorPeriod > 0) {
          final Long actorRV = en.getValue() * levelBRV.get(container);
          final long period = actorRV * actorPeriod;
          if (!mapGraphPeriods.containsKey(period)) {
            mapGraphPeriods.put(period, new ArrayList<>());
          }
          mapGraphPeriods.get(period).add(av);
        }
      }
    }

    if (mapGraphPeriods.size() > 1) {
      final StringBuilder sb = new StringBuilder("Different graph periods have been found in actors:");
      for (final Entry<Long, List<AbstractVertex>> en : mapGraphPeriods.entrySet()) {
        sb.append("\n" + en.getKey() + " from: ");
        for (final AbstractVertex a : en.getValue()) {
          sb.append(a.getName() + " / ");
        }
      }
      sb.append("\n");
      PreesmLogger.getLogger().log(Level.SEVERE, sb::toString);
      throw new PreesmRuntimeException("Periods are not consistent, abandon.");
    }
    if (mapGraphPeriods.size() == 1) {
      final long period = mapGraphPeriods.keySet().stream().findAny().orElse(0L);
      if (graphPeriod != 0 && period != 0 && graphPeriod != period) {
        PreesmLogger.getLogger()
            .severe(() -> "Graph period " + graphPeriod + " is different from the one derived from actors: " + period);
        throw new PreesmRuntimeException(
            "Periods are not consistent (graph period different from actors periods), abandon.");
      }
      if (graphPeriod == 0 && period != 0) {
        piGraph.setExpression(period);
        PreesmLogger.getLogger().info(() -> ("The graph period is set to: " + period));
      }
    } else if (graphPeriod == 0) {
      PreesmLogger.getLogger().info("No period for the graph.");
    }
  }

  /**
   * Compute the full repetition vector (among all hierarchies) of an actor.
   *
   * @param aa
   *          Actor to consider.
   * @param graphBRV
   *          Repetition vector map of the whole graph.
   * @return Full repetition vector of the actor according to the hierarchy.
   */
  public static long getHierarchichalRV(final AbstractActor aa, final Map<AbstractVertex, Long> graphBRV) {
    // We need to get the repetition vector of the graph
    final long graphRV = graphBRV.getOrDefault(aa, 1L);
    // We also need to get the total repetition vector of the hierarchy to correctly flatten the hierarchy
    long graphHierarchicallRV = 1L;
    PiGraph containingGraph = aa.getContainingPiGraph();
    while (containingGraph != null) {
      final long currentGraphRV = graphBRV.get(containingGraph) == null ? 1L : graphBRV.get(containingGraph);
      graphHierarchicallRV = graphHierarchicallRV * currentGraphRV;
      containingGraph = containingGraph.getContainingPiGraph();
    }
    // We update the value of the graphRV accordingly
    return graphRV * graphHierarchicallRV;
  }

  /**
   * Check if a vertex is contained in a top-level graph.
   *
   * @param vertex
   *          The vertex to check.
   * @return true if the containing graph is not contained by another graph, false otherwise.
   */
  public static boolean isVertexAtTopLevel(final AbstractVertex vertex) {
    final AbstractVertex parent = vertex.getContainingPiGraph();
    return (parent != null && parent.getContainingPiGraph() == null);
  }

}
