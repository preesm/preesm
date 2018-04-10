/**
 * 
 */
package org.ietr.preesm.pimm.algorithm.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PortKind;
import org.ietr.preesm.experiment.model.pimm.impl.PiGraphImpl;

/**
 * @author farresti
 * 
 *         This structure is wrapper around the PiGraph class that provides multiple useful methods for accessing special properties of a PiGraph rapidly
 *
 */
public class PiMMHandler extends PiGraphImpl {
  /** The graph. */
  private PiGraph graph;

  /** List of children graph */
  private List<PiMMHandler> childrenList;

  /** List of subgraphs contained in a graph */
  private List<List<AbstractActor>> subgraphs;

  /** List of subgraphs without interfaces contained in a graph */
  private List<List<AbstractActor>> subgraphsWOInterfaces;

  private static void generateChildren(final PiGraph graph, List<PiMMHandler> childrenList) {
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
    this.childrenList = new ArrayList<PiMMHandler>();
    this.subgraphs = new ArrayList<>();
    this.subgraphsWOInterfaces = new ArrayList<>();
    generateChildren(this.graph, this.childrenList);
  }

  /*
   * Void constructor to access the PiMMHandlerException
   */
  public PiMMHandler() {

  }

  /**
   * Set the associated PiGraph. If current BRV map was not empty, it is cleared.
   * 
   * @param graph
   *          the graph to set
   */
  public void setReferenceGraph(final PiGraph graph) {
    this.graph = graph;
    // Resets all the info
    if (!childrenList.isEmpty()) {
      childrenList.clear();
      generateChildren(this.graph, this.childrenList);
    }
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
    if (actor instanceof Delay) {
      this.graph.getDelays().add((Delay) actor);
    } else {
      this.graph.getActors().add(actor);
    }
    // Update properties
  }

  /**
   * Returns the FIFOs of a given subgraph. The function is coherent with the type of subgraph passed as input. This means that if no interface actor are
   * present in the subgraph, the function will not return any FIFO connected to an interface actor.
   * 
   * First access can be slow since the List of subgraph has to be computed.
   * 
   * @return all FIFOs contained in the subgraph, null if the subgraph is empty
   * @throws PiMMHandlerException
   *           the PiMMHandlerException exception
   */
  public List<Fifo> getFifosFromSubgraph(List<AbstractActor> subgraph) throws PiMMHandlerException {
    if (subgraph.isEmpty()) {
      WorkflowLogger.getLogger().log(Level.INFO, "No FIFOs to extrac, empty subgraph.");
      return Collections.<Fifo>emptyList();
    }
    boolean hasInterfaceActors = false;
    for (final AbstractActor actor : subgraph) {
      if (actor instanceof InterfaceActor) {
        hasInterfaceActors = true;
        break;
      }
    }
    List<Fifo> fifos = new ArrayList<>();
    for (final AbstractActor actor : subgraph) {
      for (final DataPort port : actor.getAllDataPorts()) {
        final Fifo fifo = port.getKind() == PortKind.DATA_INPUT ? ((DataInputPort) port).getIncomingFifo() : ((DataOutputPort) port).getOutgoingFifo();
        if (fifo == null) {
          throw new PiMMHandlerException(noFIFOExceptionMessage(actor, port));
        }
        final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
        final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
        if (!hasInterfaceActors && (sourceActor instanceof InterfaceActor || targetActor instanceof InterfaceActor)) {
          continue;
        }
        if (!fifos.contains(fifo)) {
          fifos.add(fifo);
        }
      }
    }
    return fifos;
  }

  /**
   * Returns the FIFOs of a given subgraph. Self-loop FIFOs are ignored.
   * 
   * First access can be slow since the List of subgraph has to be computed.
   * 
   * @return all FIFOs contained in the subgraph, null if the subgraph is empty
   * @throws PiMMHandlerException
   *           the PiMMHandlerException exception
   */
  public List<Fifo> getFifosFromSubgraphWOSelfLoop(List<AbstractActor> subgraph) throws PiMMHandlerException {
    if (subgraph.isEmpty()) {
      WorkflowLogger.getLogger().log(Level.INFO, "No FIFOs to extrac, empty subgraph.");
      return Collections.<Fifo>emptyList();
    }
    List<Fifo> fifos = getFifosFromSubgraph(subgraph);
    fifos.removeIf(fifo -> (fifo.getSourcePort().getContainingActor() == fifo.getTargetPort().getContainingActor()));
    // for (final Fifo fifo : fifos) {
    // final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
    // final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
    // if ((sourceActor == targetActor)) {
    // fifos.remove(fifo);
    // }
    // }
    return fifos;
  }

  /**
   * This method returns the subgraphs contained in a PiSDF graph. A Subgraph is defined as fully connected components. A PiSDF graph may contain multiple
   * subgraph not linked together. The subgraph return by this method contain also PiMM interface actors. Use getAllSubgraphsWOInterfaces to get subgraphs
   * without interface actors.
   * 
   * First access can be slow since the List of subgraph has to be computed.
   * 
   * @return all subgraphs contained in the associated graph (read only access)
   * @throws PiMMHandlerException
   *           the PiMMHandlerException exception
   */
  public List<List<AbstractActor>> getAllSubgraphs() throws PiMMHandlerException {
    if (this.subgraphs.isEmpty()) {
      subgraphsFetcher(this.graph, this.subgraphs);
    }
    return Collections.unmodifiableList(this.subgraphs);
  }

  /**
   * This method returns the subgraphs contained in a PiSDF graph. A Subgraph is defined as fully connected components. A PiSDF graph may contain multiple
   * subgraph not linked together. The subgraph return by this method contain also PiMM interface actors. Use getAllSubgraphsWOInterfaces to get subgraphs
   * without interface actors.
   * 
   * First access can be slow since the List of subgraph has to be computed.
   * 
   * @return all subgraphs contained in the associated graph (read only access)
   * @throws PiMMHandlerException
   *           the PiMMHandlerException exception
   */
  public List<List<AbstractActor>> getAllSubgraphsWOInterfaces() throws PiMMHandlerException {
    // First fetch all subgraphs
    if (this.subgraphs.isEmpty()) {
      subgraphsFetcher(this.graph, this.subgraphs);
    }
    // Now remove interfaces from subgraphs
    if (this.subgraphsWOInterfaces.isEmpty()) {
      subgraphsWOInterfacesFetcher(this.subgraphs, this.subgraphsWOInterfaces);
    }
    return Collections.unmodifiableList(this.subgraphsWOInterfaces);
  }

  /**
   * Fetch all subgraphs contained in a PiSDF graph. All subgraphs returned by this method do not contain interface actors.
   * 
   * @param subgraphs
   *          the full subgraphs
   * @param subgraphsWOInterfaces
   *          list of subgraphs without interface actors to be updated
   */
  private static void subgraphsWOInterfacesFetcher(final List<List<AbstractActor>> subgraphs, List<List<AbstractActor>> subgraphsWOInterfaces) {
    if (subgraphs.isEmpty()) {
      return;
    }
    for (List<AbstractActor> subgraph : subgraphs) {
      List<AbstractActor> subgraphWOInterfaces = new ArrayList<>();
      for (final AbstractActor actor : subgraph) {
        if (!(actor instanceof InterfaceActor)) {
          subgraphWOInterfaces.add(actor);
        }
      }
      if (!subgraphWOInterfaces.isEmpty()) {
        subgraphsWOInterfaces.add(subgraphWOInterfaces);
      }
    }
  }

  /**
   * Fetch all subgraphs contained in a PiSDF graph.
   * 
   * @param graph
   *          the graph to process
   * @param subgraphs
   *          list of subgraphs to be updated
   * @throws PiMMHandlerException
   *           the PiMMHandlerException exception
   */
  private static void subgraphsFetcher(final PiGraph graph, List<List<AbstractActor>> subgraphs) throws PiMMHandlerException {
    // Fetch all actors without interfaces in the PiGraph
    List<AbstractActor> listActor = new ArrayList<>();
    listActor.addAll(graph.getActors());
    // Add delays with connected actors
    for (final Delay delay : graph.getDelays()) {
      if (delay.hasGetterActor() || delay.hasSetterActor()) {
        listActor.add(delay);
      }
    }
    for (final AbstractActor actor : listActor) {
      boolean alreadyContained = false;
      for (List<AbstractActor> subgraph : subgraphs) {
        if (subgraph.contains(actor)) {
          alreadyContained = true;
          break;
        }
      }
      if (!alreadyContained) {
        List<AbstractActor> subgraph = new ArrayList<>();
        subgraph.add(actor);
        iterativeSubgraphFetcher(actor, subgraph);
        subgraphs.add(subgraph);
      }

    }
  }

  /**
   * Iterative function of subgraphsFetcher.
   *
   * @param actor
   *          the current actor
   * @param subgraph
   *          the current subgraph
   * @throws PiMMHandlerException
   *           the PiMMHandlerException exception
   */
  private static void iterativeSubgraphFetcher(final AbstractActor actor, List<AbstractActor> subgraph) throws PiMMHandlerException {
    for (final DataOutputPort output : actor.getDataOutputPorts()) {
      final Fifo fifo = output.getOutgoingFifo();
      if (fifo == null) {
        PiMMHandler hdl = new PiMMHandler();
        throw (hdl.new PiMMHandlerException(noFIFOExceptionMessage(actor, output)));
      }
      final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
      // Since we go through the FIFO of an actor, it is possible to get interface actor as target
      if (!subgraph.contains(targetActor)) {
        subgraph.add(targetActor);
        iterativeSubgraphFetcher(targetActor, subgraph);
      }
    }
    for (final DataInputPort input : actor.getDataInputPorts()) {
      final Fifo fifo = input.getIncomingFifo();
      if (fifo == null) {
        PiMMHandler hdl = new PiMMHandler();
        throw (hdl.new PiMMHandlerException(noFIFOExceptionMessage(actor, input)));
      }
      final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
      if (!subgraph.contains(sourceActor)) {
        subgraph.add(sourceActor);
        iterativeSubgraphFetcher(sourceActor, subgraph);
      }
    }
  }

  /**
   * 
   * @return Formatted message for a no FIFO connected exception
   */
  private static String noFIFOExceptionMessage(final AbstractActor actor, final DataPort port) {
    return "Actor [" + actor.getName() + "] has port [" + port.getName() + "] not connected to any FIFO.";
  }

  /**
   * The Class PiMMHandlerException.
   */
  public class PiMMHandlerException extends Exception {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3141592653589793238L;

    /**
     * Instantiates a new static pi MM 2 SDF exception.
     *
     * @param message
     *          the message
     */
    public PiMMHandlerException(final String message) {
      super(message);
    }

    public PiMMHandlerException() {
      super();
    }

  }

}
