/**
 * 
 */
package org.ietr.preesm.pimm.algorithm.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.experiment.model.expression.ExpressionEvaluator;
import org.ietr.preesm.experiment.model.factory.PiMMUserFactory;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

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
    this.listConnectedComponents = new ArrayList<>();
    this.listCCWOInterfaces = new ArrayList<>();
    generateChildren(this.graph, this.childrenList);
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
    if (actor instanceof Delay) {
      this.graph.getDelays().add((Delay) actor);
    } else {
      this.graph.getActors().add(actor);
    }
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
   * @throws PiMMHandlerException
   *           the PiMMHandlerException exception
   */
  public List<Fifo> getFifosFromCC(List<AbstractActor> cc) throws PiMMHandlerException {
    if (cc.isEmpty()) {
      WorkflowLogger.getLogger().log(Level.INFO, "No FIFOs to extrac, empty connectedComponent.");
      return Collections.<Fifo>emptyList();
    }
    final boolean containsInterfaceActors = containsInterfaceActors(cc);
    List<Fifo> fifos = new ArrayList<>();
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
   * @throws PiMMHandlerException
   *           the PiMMHandlerException exception
   */
  private void extractFifosFromActor(final boolean containsInterfaceActors, final AbstractActor actor, List<Fifo> fifos) throws PiMMHandlerException {
    for (final DataPort port : actor.getAllDataPorts()) {
      final Fifo fifo = port.getFifo();
      if (fifo == null) {
        throw new PiMMHandlerException(noFIFOExceptionMessage(actor, port));
      }
      final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
      final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
      if (!containsInterfaceActors && (sourceActor instanceof InterfaceActor || targetActor instanceof InterfaceActor)) {
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
   * @throws PiMMHandlerException
   *           the PiMMHandlerException exception
   */
  public List<Fifo> getFifosFromCCWOSelfLoop(List<AbstractActor> cc) throws PiMMHandlerException {
    List<Fifo> fifos = getFifosFromCC(cc);
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
   * @throws PiMMHandlerException
   *           the PiMMHandlerException exception
   */
  public List<List<AbstractActor>> getAllConnectedComponents() throws PiMMHandlerException {
    if (this.listConnectedComponents.isEmpty()) {
      ccsFetcher(this.graph, this.listConnectedComponents);
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
   * @throws PiMMHandlerException
   *           the PiMMHandlerException exception
   */
  public List<List<AbstractActor>> getAllConnectedComponentsWOInterfaces() throws PiMMHandlerException {
    // First fetch all subgraphs
    if (this.listConnectedComponents.isEmpty()) {
      ccsFetcher(this.graph, this.listConnectedComponents);
    }
    // Now remove interfaces from subgraphs
    if (this.listCCWOInterfaces.isEmpty()) {
      ccsWOInterfacesFetcher(this.listConnectedComponents, this.listCCWOInterfaces);
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
  private static void ccsWOInterfacesFetcher(final List<List<AbstractActor>> listCCs, List<List<AbstractActor>> listCCsWOInterfaces) {
    if (listCCs.isEmpty()) {
      return;
    }
    for (List<AbstractActor> cc : listCCs) {
      List<AbstractActor> ccWOInterfaces = new ArrayList<>(cc);
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
   * @throws PiMMHandlerException
   *           the PiMMHandlerException exception
   */
  private static void ccsFetcher(final PiGraph graph, List<List<AbstractActor>> listCCs) throws PiMMHandlerException {
    // Fetch all actors without interfaces in the PiGraph
    List<AbstractActor> fullActorList = new ArrayList<>();
    fullActorList.addAll(graph.getActors());
    // Add delays with connected actors
    for (final Delay delay : graph.getDelays()) {
      if (delay.hasGetterActor() || delay.hasSetterActor()) {
        fullActorList.add(delay);
      }
    }
    for (final AbstractActor actor : fullActorList) {
      boolean alreadyContained = false;
      for (List<AbstractActor> cc : listCCs) {
        if (cc.contains(actor)) {
          alreadyContained = true;
          break;
        }
      }
      if (!alreadyContained) {
        List<AbstractActor> cc = new ArrayList<>();
        cc.add(actor);
        iterativeCCFetcher(actor, cc);
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
   * @throws PiMMHandlerException
   *           the PiMMHandlerException exception
   */
  private static void iterativeCCFetcher(final AbstractActor actor, List<AbstractActor> cc) throws PiMMHandlerException {
    for (final DataOutputPort output : actor.getDataOutputPorts()) {
      final Fifo fifo = output.getOutgoingFifo();
      if (fifo == null) {
        PiMMHandler hdl = new PiMMHandler();
        throw (hdl.new PiMMHandlerException(noFIFOExceptionMessage(actor, output)));
      }
      final AbstractActor targetActor = fifo.getTargetPort().getContainingActor();
      if (!cc.contains(targetActor)) {
        cc.add(targetActor);
        iterativeCCFetcher(targetActor, cc);
      }
    }
    for (final DataInputPort input : actor.getDataInputPorts()) {
      final Fifo fifo = input.getIncomingFifo();
      if (fifo == null) {
        PiMMHandler hdl = new PiMMHandler();
        throw (hdl.new PiMMHandlerException(noFIFOExceptionMessage(actor, input)));
      }
      final AbstractActor sourceActor = fifo.getSourcePort().getContainingActor();
      if (!cc.contains(sourceActor)) {
        cc.add(sourceActor);
        iterativeCCFetcher(sourceActor, cc);
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
   * @throws PiMMHandlerException
   *           the PiMMHandlerException exception
   */
  public void resolveAllParameters() throws PiMMHandlerException {
    final LinkedHashMap<Parameter, Long> parameterValues = new LinkedHashMap<>();

    // First we evaluate constant values and check for dynamic parameters
    for (final Parameter p : this.graph.getAllParameters()) {
      if (p.isLocallyStatic()) {
        if (p instanceof ConfigInputInterface) {
          // We will deal with interfaces later
          continue;
        }
        try {
          final Expression expression = p.getExpression();
          final long value = Long.parseLong(expression.getExpressionString());
          parameterValues.put(p, value);
        } catch (final NumberFormatException e) {
          // The expression associated to the parameter is an
          // expression (and not an constant int value).
          // Leave it as it is, it will be solved later.
        }
      } else {
        throw new PiMMHandlerException("Parameter " + p.getName() + " is depends on a configuration actor. It is thus impossible to use the"
            + " Static PiMM 2 SDF transformation. Try instead the Dynamic PiMM 2 SDF"
            + " transformation (id: org.ietr.preesm.experiment.pimm2sdf.PiMM2SDFTask)");
      }
    }

    // Evaluate remaining parameters of the top level graph.
    // These parameters are defined by expression.
    for (final Parameter p : this.graph.getParameters()) {
      if (!parameterValues.containsKey(p)) {
        // Evaluate the expression wrt. the current values of the
        // parameters and set the result as new expression
        final Expression valueExpression = p.getValueExpression();
        final long value = ExpressionEvaluator.evaluate(valueExpression);
        valueExpression.setExpressionString(Long.toString(value));
        parameterValues.put(p, value);
      }
    }
    // Now evaluate parameters for all hierarchy graphs
    iterativeResolveAllParameters(this.graph, parameterValues);

    // Data port rate evaluation
    // TODO put that in other class ?
    final LinkedHashMap<AbstractActor, JEP> actorParsers = initActorRateParsers(parameterValues);
    iterativeResolveAllPortRates(this.graph, actorParsers);
  }

  /**
   * Iterative function of resolveAllParameters.
   *
   * @param graph
   *          the current graph
   * @param parameterValues
   *          map of all parameters and their resolved value
   * @throws PiMMHandlerException
   *           the PiMMHandlerException exception
   */
  private void iterativeResolveAllParameters(final PiGraph graph, final LinkedHashMap<Parameter, Long> parameterValues) throws PiMMHandlerException {
    // We already resolved all static parameters we could.
    // Now, we resolve interfaces
    for (final ConfigInputInterface cii : graph.getConfigInputInterfaces()) {
      final ConfigInputPort graphPort = cii.getGraphPort();
      final Dependency incomingDependency = graphPort.getIncomingDependency();
      final ISetter setter = incomingDependency.getSetter();
      // Setter of an incoming dependency into a ConfigInputInterface must be
      // a parameter
      if (setter instanceof Parameter) {
        final Expression pExp = PiMMUserFactory.instance.createExpression();
        // When we arrive here all upper graphs have been processed.
        // We can then directly evaluate parameter expression here.
        final Expression valueExpression = ((Parameter) setter).getValueExpression();
        final String expressionString = valueExpression.getExpressionString();
        pExp.setExpressionString(expressionString);
        cii.setExpression(pExp);
        parameterValues.put((Parameter) cii, Long.parseLong(expressionString));
      }
    }
    // Finally, we derive parameter values that have not already been processed
    computeDerivedParameterValues(graph, parameterValues);

    for (final PiGraph g : graph.getChildrenGraphs()) {
      iterativeResolveAllParameters(g, parameterValues);
    }
  }

  /**
   * Set the value of parameters of a PiGraph when possible (i.e., if we have currently only one available value, or if we can compute the value)
   *
   * @param graph
   *          the PiGraph in which we want to set the values of parameters
   */
  private void computeDerivedParameterValues(final PiGraph graph, final LinkedHashMap<Parameter, Long> parameterValues) {
    // If there is no value or list of values for one Parameter, the value
    // of the parameter is derived (i.e., computed from other parameters
    // values), we can evaluate it (after the values of other parameters
    // have been fixed)
    for (final Parameter p : graph.getParameters()) {
      if (!parameterValues.containsKey(p)) {
        // Evaluate the expression wrt. the current values of the
        // parameters and set the result as new expression
        final Expression pExp = PiMMUserFactory.instance.createExpression();
        final Expression valueExpression = p.getValueExpression();
        final long evaluate = ExpressionEvaluator.evaluate(valueExpression);
        pExp.setExpressionString(Long.toString(evaluate));
        p.setExpression(pExp);
        try {
          final long value = Long.parseLong(p.getExpression().getExpressionString());
          parameterValues.put(p, value);
        } catch (final NumberFormatException e) {
          WorkflowLogger.getLogger().log(Level.INFO, "TROLOLOLOLOLOLOLO.");
          break;
        }
      }
    }
  }

  /**
   * Iterative function for resolving data port rates.
   *
   * @param graph
   *          the current graph
   * @param actorParsers
   *          map of actor and corresponding parsers
   * @throws PiMMHandlerException
   *           the PiMMHandlerException exception
   */
  private void iterativeResolveAllPortRates(final PiGraph graph, final LinkedHashMap<AbstractActor, JEP> actorParsers) throws PiMMHandlerException {
    for (final Fifo f : graph.getFifos()) {
      final DataOutputPort sourcePort = f.getSourcePort();
      final DataInputPort targetPort = f.getTargetPort();
      resolvePortRate(sourcePort, actorParsers.get(sourcePort.getContainingActor()));
      resolvePortRate(targetPort, actorParsers.get(targetPort.getContainingActor()));
    }
    for (final PiGraph g : graph.getChildrenGraphs()) {
      iterativeResolveAllPortRates(g, actorParsers);
    }
  }

  /**
   * Fast evaluator for data port rate expression.<br>
   * If rate expression is a constant, then parsing is ignored since it is already done. <br>
   * This implementation uses benefit of the fact that the parser is initialized once for a given actor.
   *
   * @param port
   *          the data port to evaluate
   * @param actorParser
   *          parser of the actor containing the port
   * @throws PiMMHandlerException
   *           the PiMMHandlerException exception
   */
  private void resolvePortRate(final DataPort port, final JEP actorParser) throws PiMMHandlerException {
    final Expression portRateExpression = port.getPortRateExpression();
    try {
      // If we can parse it, then it is constant
      Long.parseLong(portRateExpression.getExpressionString());
    } catch (final NumberFormatException e) {
      try {
        // Now, we deal with expression
        long rate = parsePortExpression(actorParser, portRateExpression.getExpressionString());
        portRateExpression.setExpressionString(Long.toString(rate));
      } catch (ParseException eparse) {
        throw new PiMMHandlerException("failed to parse rate for [" + port.getId() + "] port");
      }
    }
  }

  /**
   * Creates a map between all actors contained in the reference graph and its children and an associated parser for rate evaluation
   *
   * @param parameterValues
   *          map of all parameters and their resolved value
   * @return actorParsers
   * @throws PiMMHandlerException
   *           the PiMMHandlerException exception
   */
  private LinkedHashMap<AbstractActor, JEP> initActorRateParsers(final LinkedHashMap<Parameter, Long> parameterValues) {
    LinkedHashMap<AbstractActor, JEP> actorParsers = new LinkedHashMap<>();
    for (final AbstractActor actor : this.graph.getAllActors()) {
      // Construct a Map that links actor port parameter to their real value in the graph
      final LinkedHashMap<String, Long> portValues = new LinkedHashMap<>();
      actorGraphParametersResolver(actor, parameterValues, portValues);
      actorParsers.put(actor, initJep(portValues));
    }
    // Deals with delay
    for (final Delay delay : this.graph.getAllDelays()) {
      if (delay.hasSetterActor() || delay.hasGetterActor()) {
        // Construct a Map that links actor port parameter to their real value in the graph
        final LinkedHashMap<String, Long> portValues = new LinkedHashMap<>();
        actorGraphParametersResolver(delay, parameterValues, portValues);
        actorParsers.put((AbstractActor) delay, initJep(portValues));
      }
    }
    return actorParsers;
  }

  /**
   * Creates a map between all parameters of an actor and the parameters of the graph
   *
   * @param actor
   *          the actor to evaluate
   * @param parameterValues
   *          map of all parameters and their resolved value
   * @param portValues
   *          map of all actor parameters and the value of the corresponding graph parameter
   */
  private static void actorGraphParametersResolver(final AbstractActor actor, final LinkedHashMap<Parameter, Long> parameterValues,
      final LinkedHashMap<String, Long> portValues) {
    // Data interface actors do not have parameter ports, thus expression is directly graph parameter
    // Same for delays
    if (actor instanceof InterfaceActor || actor instanceof Delay) {
      for (final Parameter p : actor.getInputParameters()) {
        portValues.put(p.getName(), parameterValues.get(p));
      }
    } else {
      // We have to fetch the corresponding parameter port for normal actors
      for (final Parameter p : actor.getInputParameters()) {
        final Port lookupPort = actor.lookupPortConnectedWithParameter(p);
        portValues.put(lookupPort.getName(), parameterValues.get(p));
      }
    }

  }

  private static JEP initJep(final LinkedHashMap<String, Long> portValues) {
    JEP jep = new JEP();
    if (portValues != null) {
      portValues.forEach(jep::addVariable);
    }
    // TODO move to JEP 3 and get rid of these
    jep.addStandardConstants();
    jep.addStandardFunctions();
    return jep;
  }

  private static long parsePortExpression(final JEP jep, final String expressionString) throws ParseException {
    final Node parse = jep.parse(expressionString);
    final Object result = jep.evaluate(parse);
    if (result instanceof Long) {
      return (Long) result;
    } else if (result instanceof Double) {
      return Math.round((Double) result);
    } else {
      throw new ParseException("Unsupported result type " + result.getClass().getSimpleName());
    }
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
