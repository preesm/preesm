/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
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
package org.preesm.algorithm.pisdf.pimm2sdf;

import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.emf.ecore.resource.Resource;
import org.preesm.algorithm.codegen.idl.ActorPrototypes;
import org.preesm.algorithm.codegen.idl.Prototype;
import org.preesm.algorithm.codegen.model.CodeGenArgument;
import org.preesm.algorithm.codegen.model.CodeGenParameter;
import org.preesm.algorithm.model.AbstractGraph;
import org.preesm.algorithm.model.CodeRefinement;
import org.preesm.algorithm.model.IRefinement;
import org.preesm.algorithm.model.parameters.Argument;
import org.preesm.algorithm.model.parameters.ConstantValue;
import org.preesm.algorithm.model.parameters.ExpressionValue;
import org.preesm.algorithm.model.parameters.Value;
import org.preesm.algorithm.model.parameters.Variable;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.SDFInterfaceVertex;
import org.preesm.algorithm.model.sdf.SDFVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFForkVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFJoinVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.preesm.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.preesm.algorithm.model.sdf.transformations.SpecialActorPortsIndexer;
import org.preesm.algorithm.model.types.ExpressionEdgePropertyType;
import org.preesm.algorithm.model.types.StringEdgePropertyType;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.ConfigOutputInterface;
import org.preesm.model.pisdf.ConfigOutputPort;
import org.preesm.model.pisdf.Configurable;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.ExecutableActor;
import org.preesm.model.pisdf.Expression;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionParameter;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.PiSDFRefinement;
import org.preesm.model.pisdf.Port;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.statictools.PiGraphExecution;
import org.preesm.model.pisdf.util.PiMMSwitch;

/**
 * The Class StaticPiMM2SDFVisitor.
 */
public class StaticPiMM2SDFVisitor extends PiMMSwitch<Boolean> {

  /** The result. */
  // SDFGraph created from the outer graph
  private SDFGraph result;

  /** The execution. */
  // Original list of fixed values for all the parameters of the graph
  private final PiGraphExecution execution;

  /** The pi vx 2 SDF vx. */
  // Map from original PiMM vertices to generated SDF vertices
  private final Map<AbstractVertex, SDFAbstractVertex> piVx2SDFVx = new LinkedHashMap<>();

  /** The pi port 2 vx. */
  // Map from PiMM ports to their vertex (used for SDFEdge creation)
  private final Map<Port, Configurable> piPort2Vx = new LinkedHashMap<>();
  // Map from original PiMM ports to generated SDF ports (used for SDFEdge
  /** The pi port 2 SDF port. */
  // creation)
  private final Map<Port, SDFInterfaceVertex> piPort2SDFPort = new LinkedHashMap<>();

  /** The current SDF refinement. */
  // Current SDF Refinement
  private IRefinement currentSDFRefinement;

  /**
   * Creates the value.
   *
   * @param str
   *          the str
   * @return the value
   */
  /* Create An IBSDF value depending of the expression */
  private Value createValue(final String str) {
    try {
      final long l = Long.parseLong(str);
      return new ConstantValue(l);
    } catch (final NumberFormatException e) {
      return new ExpressionValue(str);
    }
  }

  /**
   * Instantiates a new abstract pi MM 2 SDF visitor.
   *
   * @param execution
   *          the execution
   */
  public StaticPiMM2SDFVisitor(final PiGraphExecution execution) {
    this.execution = execution;
  }

  /**
   * Transforms parameters from a PiGraph into graph variables of an SDFGraph.
   *
   * @param pg
   *          the PiGraph from which we extract the Parameters
   * @param sdf
   *          the SDFGraph to which we add the graph variables
   */
  private void parameters2GraphVariables(final PiGraph pg, final SDFGraph sdf) {
    for (final Parameter p : pg.getParameters()) {
      final String evaluate = Long.toString(p.getValueExpression().evaluate());
      final Variable var = new Variable(p.getName(), evaluate);
      sdf.addVariable(var);
    }
  }

  /**
   * Parameters of a top graph must be visited before parameters of a subgraph, since the expression of
   * ConfigurationInputInterface depends on the value of its connected Parameter.
   *
   * @param p
   *          the p
   */
  @Override
  public Boolean caseParameter(final Parameter p) {
    if (p.isConfigurationInterface()) {
      final ConfigInputInterface cii = (ConfigInputInterface) p;
      final ConfigInputPort graphPort = cii.getGraphPort();
      final Dependency incomingDependency = graphPort.getIncomingDependency();
      final ISetter setter = incomingDependency.getSetter();
      // Setter of an incoming dependency into a ConfigInputInterface must
      // be a parameter
      if (setter instanceof Parameter) {
        final Expression setterParam = ((Parameter) setter).getValueExpression();
        cii.setExpression(setterParam.getExpressionAsString());
      }
    } else {
      // If there is only one value available for Parameter p, we can set
      // its
      if (this.execution.hasValue(p)) {
        final Integer value = this.execution.getValue(p);
        p.setExpression(value.longValue());
      }
    }
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitConfigInputInterface(org.ietr.preesm.experiment.model.
   * pimm.ConfigInputInterface)
   */
  @Override
  public Boolean caseConfigInputInterface(final ConfigInputInterface cii) {
    final ConfigInputPort graphPort = cii.getGraphPort();
    final Dependency incomingDependency = graphPort.getIncomingDependency();
    final ISetter setter = incomingDependency.getSetter();
    // Setter of an incoming dependency into a ConfigInputInterface must be
    // a parameter
    if (setter instanceof Parameter) {
      cii.setExpression(((Parameter) setter).getValueExpression().getExpressionAsString());
    }
    return true;
  }

  /**
   * Set the value of parameters of a PiGraph when possible (i.e., if we have currently only one available value, or if
   * we can compute the value)
   *
   * @param graph
   *          the PiGraph in which we want to set the values of parameters
   * @param execution
   *          the list of available values for each parameter
   */
  private void computeDerivedParameterValues(final PiGraph graph, final PiGraphExecution execution) {
    // If there is no value or list of valuse for one Parameter, the value
    // of the parameter is derived (i.e., computed from other parameters
    // values), we can evaluate it (after the values of other parameters
    // have been fixed)
    for (final Parameter p : graph.getParameters()) {
      if (!execution.hasValue(p)) {
        // Evaluate the expression wrt. the current values of the
        // parameters and set the result as new expression
        final Expression valueExpression = p.getValueExpression();
        final long evaluate = valueExpression.evaluate();
        p.setExpression(evaluate);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#caseAbstractActor(org.ietr.preesm.experiment.model.pimm.
   * AbstractActor)
   */
  @Override
  public Boolean caseAbstractActor(final AbstractActor aa) {
    // Handle the target ports (DataInputPort in PISDF,
    // SDFSourceInterfaceVertex in IBSDF) keeping the order
    for (final DataInputPort dip : aa.getDataInputPorts()) {
      // The target SDF port
      SDFSourceInterfaceVertex sdfInputPort;
      // The SDF vertex to which to add the created port
      final SDFAbstractVertex sdfTarget = this.piVx2SDFVx.get(aa);
      if (sdfTarget instanceof SDFSourceInterfaceVertex) {
        // If the SDF vertex is an interface, use it as the port
        sdfInputPort = (SDFSourceInterfaceVertex) sdfTarget;
      } else {
        // Otherwise create a new port and add it to the SDF vertex
        sdfInputPort = new SDFSourceInterfaceVertex();
        sdfInputPort.setName(dip.getName());
        sdfTarget.addSource(sdfInputPort);
      }
      this.piPort2SDFPort.put(dip, sdfInputPort);
      this.piPort2Vx.put(dip, aa);
    }
    // Handle the source ports (DataOuputPort in PISDF,
    // SDFSinkInterfaceVertex in IBSDF) keeping the order
    for (final DataOutputPort dop : aa.getDataOutputPorts()) {
      // The source SDF port
      SDFSinkInterfaceVertex sdfOutputPort;
      // The SDF vertex to which to add the created port
      final SDFAbstractVertex sdfSource = this.piVx2SDFVx.get(aa);
      if (sdfSource instanceof SDFSinkInterfaceVertex) {
        // If the SDF vertex is an interface, use it as the port
        sdfOutputPort = (SDFSinkInterfaceVertex) sdfSource;
      } else {
        // Otherwise create a new port and add it to the SDF vertex
        sdfOutputPort = new SDFSinkInterfaceVertex();
        sdfOutputPort.setName(dop.getName());
        sdfSource.addSink(sdfOutputPort);
      }
      this.piPort2SDFPort.put(dop, sdfOutputPort);
      this.piPort2Vx.put(dop, aa);
    }
    for (final ConfigOutputPort cop : aa.getConfigOutputPorts()) {
      this.piPort2Vx.put(cop, aa);
    }
    caseConfigurable(aa);
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitActor(org.ietr.preesm.experiment.model.pimm.Actor)
   */
  @Override
  public Boolean caseDelayActor(final DelayActor a) {
    // DO NOTHING
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitActor(org.ietr.preesm.experiment.model.pimm.Actor)
   */
  @Override
  public Boolean caseActor(final Actor a) {
    final SDFVertex v = new SDFVertex();
    this.piVx2SDFVx.put(a, v);
    // Handle vertex's name
    v.setName(a.getName());
    // Handle vertex's path inside the graph hierarchy
    v.setInfo(a.getVertexPath());
    // Handle ID
    v.setId(a.getName());
    // Handle vertex's refinement (description of the vertex's behavior:
    // function prototypes or subgraphs)
    final Refinement piRef = a.getRefinement();
    doSwitch(piRef);
    v.setRefinement(this.currentSDFRefinement);
    // Handle path to memory script of the vertex
    if (a.getMemoryScriptPath() != null) {
      v.setPropertyValue(SDFVertex.MEMORY_SCRIPT, a.getMemoryScriptPath().toOSString());
    }
    // Handle input parameters as instance arguments
    for (final ConfigInputPort p : a.getConfigInputPorts()) {
      final Dependency incomingDependency = p.getIncomingDependency();
      if (incomingDependency == null) {
        throw new PreesmException(
            "Actor config input port '" + a.getVertexPath() + "." + p.getName() + "' is not connected.",
            new NullPointerException());
      }
      final ISetter setter = incomingDependency.getSetter();
      if (setter instanceof Parameter) {
        final Parameter param = (Parameter) setter;
        final Argument arg = new Argument(p.getName());
        arg.setValue(param.getName());
        v.getArguments().addArgument(arg);
      }
    }

    caseAbstractActor(a);

    this.result.addVertex(v);
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitFifo(org.ietr.preesm.experiment.model.pimm.Fifo)
   */
  @Override
  public Boolean caseFifo(final Fifo f) {
    final DataOutputPort piOutputPort = f.getSourcePort();
    final DataInputPort piInputPort = f.getTargetPort();

    final Configurable source = this.piPort2Vx.get(piOutputPort);
    final Configurable target = this.piPort2Vx.get(piInputPort);

    if ((source instanceof AbstractVertex) && (target instanceof AbstractVertex)) {
      // Get SDFAbstractVertices corresponding to source and target
      final SDFAbstractVertex sdfSource = this.piVx2SDFVx.get(source);
      final SDFAbstractVertex sdfTarget = this.piVx2SDFVx.get(target);

      // Get the source port created in caseAbstractActor
      final SDFSinkInterfaceVertex sdfOutputPort = (SDFSinkInterfaceVertex) this.piPort2SDFPort.get(piOutputPort);

      // Get the target port created in caseAbstractActor
      final SDFSourceInterfaceVertex sdfInputPort = (SDFSourceInterfaceVertex) this.piPort2SDFPort.get(piInputPort);

      // Handle Delay, Consumption and Production rates
      ExpressionEdgePropertyType delay;
      if (f.getDelay() != null) {
        // Evaluate the expression wrt. the current values of the
        // parameters
        delay = new ExpressionEdgePropertyType(createValue(Long.toString(f.getDelay().getSizeExpression().evaluate())));
      } else {
        delay = new ExpressionEdgePropertyType(new ConstantValue(0L));
      }
      // Evaluate the expression wrt. the current values of the parameters
      final ExpressionEdgePropertyType cons = new ExpressionEdgePropertyType(
          createValue(Long.toString(piInputPort.getPortRateExpression().evaluate())));

      // Evaluate the expression wrt. the current values of the parameters
      final ExpressionEdgePropertyType prod = new ExpressionEdgePropertyType(
          createValue(Long.toString(piOutputPort.getPortRateExpression().evaluate())));

      final SDFEdge edge = this.result.addEdge(sdfSource, sdfOutputPort, sdfTarget, sdfInputPort, prod, cons, delay);

      // Set the data type of the edge
      edge.setDataType(new StringEdgePropertyType(f.getType()));

      // Handle memory annotations
      convertAnnotationsFromTo(piOutputPort, edge, SDFEdge.SOURCE_PORT_MODIFIER);
      convertAnnotationsFromTo(piInputPort, edge, SDFEdge.TARGET_PORT_MODIFIER);
    }
    return true;
  }

  /**
   * Convert annotations from to.
   *
   * @param piPort
   *          the pi port
   * @param edge
   *          the edge
   * @param property
   *          the property
   */
  private void convertAnnotationsFromTo(final DataPort piPort, final SDFEdge edge, final String property) {
    switch (piPort.getAnnotation()) {
      case READ_ONLY:
        edge.setPropertyValue(property, new StringEdgePropertyType(SDFEdge.MODIFIER_READ_ONLY));
        break;
      case WRITE_ONLY:
        edge.setPropertyValue(property, new StringEdgePropertyType(SDFEdge.MODIFIER_WRITE_ONLY));
        break;
      case UNUSED:
        edge.setPropertyValue(property, new StringEdgePropertyType(SDFEdge.MODIFIER_UNUSED));
        break;
      default:
    }
  }

  @Override
  public Boolean caseConfigurable(final Configurable p) {
    for (final ConfigInputPort cip : p.getConfigInputPorts()) {
      this.piPort2Vx.put(cip, p);
    }
    return true;
  }

  @Override
  public Boolean caseInterfaceActor(final InterfaceActor ia) {
    // DO NOTHING
    return true;
  }

  @Override
  public Boolean caseConfigOutputInterface(final ConfigOutputInterface coi) {
    caseInterfaceActor(coi);
    return true;
  }

  @Override
  public Boolean caseDataInputInterface(final DataInputInterface dii) {
    final SDFSourceInterfaceVertex v = new SDFSourceInterfaceVertex();
    this.piVx2SDFVx.put(dii, v);
    v.setName(dii.getName());

    caseAbstractActor(dii);

    this.result.addVertex(v);
    return true;
  }

  @Override
  public Boolean caseDataOutputInterface(final DataOutputInterface doi) {
    final SDFSinkInterfaceVertex v = new SDFSinkInterfaceVertex();
    this.piVx2SDFVx.put(doi, v);
    v.setName(doi.getName());

    caseAbstractActor(doi);

    this.result.addVertex(v);
    return true;
  }

  @Override
  public Boolean casePiSDFRefinement(final PiSDFRefinement r) {
    this.currentSDFRefinement = new CodeRefinement(r.getFilePath());
    return true;
  }

  /** The current prototype. */
  private Prototype currentPrototype;

  /** The current Argument and Parameter. */
  private CodeGenArgument currentArgument;

  /** The current parameter. */
  private CodeGenParameter currentParameter;

  @Override
  public Boolean caseCHeaderRefinement(final CHeaderRefinement h) {
    final ActorPrototypes actorPrototype = new ActorPrototypes(h.getFilePath().toOSString());

    doSwitch(h.getLoopPrototype());
    actorPrototype.setLoopPrototype(this.currentPrototype);

    if (h.getInitPrototype() != null) {
      doSwitch(h.getInitPrototype());
      actorPrototype.setInitPrototype(this.currentPrototype);
    }

    this.currentSDFRefinement = actorPrototype;
    return true;
  }

  @Override
  public Boolean caseFunctionPrototype(final FunctionPrototype f) {
    this.currentPrototype = new Prototype(f.getName());
    for (final FunctionParameter p : f.getParameters()) {
      doSwitch(p);
      if (p.isIsConfigurationParameter()) {
        this.currentPrototype.addParameter(this.currentParameter);
      } else {
        this.currentPrototype.addArgument(this.currentArgument);
      }
    }
    return true;
  }

  @Override
  public Boolean caseFunctionParameter(final FunctionParameter f) {
    if (f.isIsConfigurationParameter()) {
      int direction = 0;
      switch (f.getDirection()) {
        case IN:
          direction = 0;
          break;
        case OUT:
          direction = 1;
          break;
        default:
      }
      this.currentParameter = new CodeGenParameter(f.getName(), direction);
    } else {
      String direction = "";
      switch (f.getDirection()) {
        case IN:
          direction = CodeGenArgument.INPUT;
          break;
        case OUT:
          direction = CodeGenArgument.OUTPUT;
          break;
        default:
      }
      this.currentArgument = new CodeGenArgument(f.getName(), direction);
      this.currentArgument.setType(f.getType());
    }
    return true;
  }

  @Override
  public Boolean caseBroadcastActor(final BroadcastActor ba) {
    final SDFBroadcastVertex bv = new SDFBroadcastVertex();
    this.piVx2SDFVx.put(ba, bv);
    // Handle vertex's name
    bv.setName(ba.getName());
    // Handle vertex's path inside the graph hierarchy
    bv.setInfo(ba.getVertexPath());

    // Handle input parameters as instance arguments
    for (final ConfigInputPort p : ba.getConfigInputPorts()) {
      final ISetter setter = p.getIncomingDependency().getSetter();
      if (setter instanceof Parameter) {
        final Parameter param = (Parameter) setter;
        final Argument arg = new Argument(p.getName());
        arg.setValue(param.getName());
        bv.getArguments().addArgument(arg);
      }
    }

    caseAbstractActor(ba);

    this.result.addVertex(bv);
    return true;
  }

  @Override
  public Boolean caseJoinActor(final JoinActor ja) {
    final SDFJoinVertex jv = new SDFJoinVertex();
    this.piVx2SDFVx.put(ja, jv);
    // Handle vertex's name
    jv.setName(ja.getName());
    // Handle vertex's path inside the graph hierarchy
    jv.setInfo(ja.getVertexPath());

    // Handle input parameters as instance arguments
    for (final ConfigInputPort p : ja.getConfigInputPorts()) {
      final ISetter setter = p.getIncomingDependency().getSetter();
      if (setter instanceof Parameter) {
        final Parameter param = (Parameter) setter;
        final Argument arg = new Argument(p.getName());
        arg.setValue(param.getName());
        jv.getArguments().addArgument(arg);
      }
    }

    caseAbstractActor(ja);

    this.result.addVertex(jv);
    return true;
  }

  @Override
  public Boolean caseForkActor(final ForkActor fa) {
    final SDFForkVertex fv = new SDFForkVertex();
    this.piVx2SDFVx.put(fa, fv);
    // Handle vertex's name
    fv.setName(fa.getName());
    // Handle vertex's path inside the graph hierarchy
    fv.setInfo(fa.getVertexPath());

    // Handle input parameters as instance arguments
    for (final ConfigInputPort p : fa.getConfigInputPorts()) {
      final ISetter setter = p.getIncomingDependency().getSetter();
      if (setter instanceof Parameter) {
        final Parameter param = (Parameter) setter;
        final Argument arg = new Argument(p.getName());
        arg.setValue(param.getName());
        fv.getArguments().addArgument(arg);
      }
    }

    caseAbstractActor(fa);

    this.result.addVertex(fv);
    return true;
  }

  @Override
  public Boolean caseRoundBufferActor(final RoundBufferActor rba) {
    final SDFRoundBufferVertex rbv = new SDFRoundBufferVertex();
    this.piVx2SDFVx.put(rba, rbv);
    // Handle vertex's name
    rbv.setName(rba.getName());
    // Handle vertex's path inside the graph hierarchy
    rbv.setInfo(rba.getVertexPath());

    // Handle input parameters as instance arguments
    for (final ConfigInputPort p : rba.getConfigInputPorts()) {
      final ISetter setter = p.getIncomingDependency().getSetter();
      if (setter instanceof Parameter) {
        final Parameter param = (Parameter) setter;
        final Argument arg = new Argument(p.getName());
        arg.setValue(param.getName());
        rbv.getArguments().addArgument(arg);
      }
    }

    caseAbstractActor(rba);

    this.result.addVertex(rbv);
    return true;
  }

  /**
   * Gets the result.
   *
   * @return the result
   */
  public SDFGraph getResult() {
    return this.result;
  }

  /**
   * Methods below are unused and unimplemented visit methods.
   *
   * @param dop
   *          the dop
   */

  @Override
  public Boolean caseDataOutputPort(final DataOutputPort dop) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDelay(final Delay d) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDataInputPort(final DataInputPort dip) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseExpression(final Expression e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseConfigInputPort(final ConfigInputPort cip) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseConfigOutputPort(final ConfigOutputPort cop) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDependency(final Dependency d) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseISetter(final ISetter is) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean casePort(final Port p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseDataPort(final DataPort p) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean caseExecutableActor(final ExecutableActor ea) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean casePiGraph(final PiGraph pg) {
    // If result == null, then pg is the first PiGraph we encounter
    if (this.result == null) {
      this.result = new SDFGraph();
      this.result.getPropertyBean().setValue(PiGraph.class.getCanonicalName(), pg);
      this.result.setName(pg.getName());

      // Save the original Path to the pigraph in the property bean (used
      // by memory scripts)
      final Resource eResource = pg.eResource();
      if (eResource != null) {
        this.result.setPropertyValue(AbstractGraph.PATH, eResource.getURI().toPlatformString(false));
      }

      // Set the values into the parameters of pg when possible
      for (final Parameter p : pg.getParameters()) {
        doSwitch(p);
      }
      computeDerivedParameterValues(pg, this.execution);
      // Once the values are set, use them to put parameters as graph
      // variables in the resulting SDF graph
      parameters2GraphVariables(pg, this.result);

      // Visit each of the vertices of pg with the values set
      for (final AbstractActor aa : pg.getActors()) {
        doSwitch(aa);
      }
      // And each of the data edges of pg with the values set
      for (final Fifo f : pg.getFifos()) {
        doSwitch(f);
      }

      // Make sure all ports of special actors are indexed and ordered
      // both in top and sub graphes
      SpecialActorPortsIndexer.addIndexes(this.result);
      SpecialActorPortsIndexer.sortIndexedPorts(this.result);
    } else {
      // If result != null, pg is not the first PiGraph we encounter, it is a
      // subgraph
      final SDFVertex v = new SDFVertex();
      this.piVx2SDFVx.put(pg, v);
      // Handle vertex's name
      v.setName(pg.getName());
      // Handle vertex's path inside the graph hierarchy
      v.setInfo(pg.getVertexPath());
      // Handle ID
      v.setId(pg.getName());

      caseAbstractActor(pg);

      // Visit the subgraph
      final StaticPiMM2SDFVisitor innerVisitor = new StaticPiMM2SDFVisitor(this.execution);
      innerVisitor.doSwitch(pg);
      // Set the obtained SDFGraph as refinement for v
      final SDFGraph sdf = innerVisitor.getResult();
      sdf.setName(sdf.getName() + this.execution.getExecutionLabel());
      v.setGraphDescription(sdf);

      this.result.addVertex(v);
    }
    return true;
  }

}
