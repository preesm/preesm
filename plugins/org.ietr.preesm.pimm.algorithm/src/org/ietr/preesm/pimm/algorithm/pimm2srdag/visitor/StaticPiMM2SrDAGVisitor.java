/**
 * 
 */
package org.ietr.preesm.pimm.algorithm.pimm2srdag.visitor;

import java.util.LinkedHashMap;
import java.util.Map;
import org.ietr.dftools.algorithm.model.CodeRefinement;
import org.ietr.dftools.algorithm.model.IRefinement;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.parameters.Argument;
import org.ietr.dftools.algorithm.model.parameters.ConstantValue;
import org.ietr.dftools.algorithm.model.parameters.ExpressionValue;
import org.ietr.dftools.algorithm.model.parameters.Value;
import org.ietr.dftools.algorithm.model.parameters.Variable;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.types.SDFExpressionEdgePropertyType;
import org.ietr.dftools.algorithm.model.sdf.types.SDFStringEdgePropertyType;
import org.ietr.preesm.codegen.idl.ActorPrototypes;
import org.ietr.preesm.codegen.idl.Prototype;
import org.ietr.preesm.codegen.model.CodeGenArgument;
import org.ietr.preesm.codegen.model.CodeGenParameter;
import org.ietr.preesm.experiment.model.expression.ExpressionEvaluator;
import org.ietr.preesm.experiment.model.factory.PiMMUserFactory;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.BroadcastActor;
import org.ietr.preesm.experiment.model.pimm.CHeaderRefinement;
import org.ietr.preesm.experiment.model.pimm.ConfigInputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.Configurable;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.JoinActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiSDFRefinement;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;
import org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperEdgeFactory;
import org.ietr.preesm.pimm.algorithm.pimm2sdf.PiGraphExecution;

/**
 * @author farresti
 *
 */
public class StaticPiMM2SrDAGVisitor extends PiMMSwitch<Boolean> {
  /** The result. */
  // SDFGraph created from the outer graph
  protected MapperDAG result;

  /** The execution. */
  // Original list of fixed values for all the parameters of the graph
  protected PiGraphExecution execution;

  /** The pi vx 2 SDF vx. */
  // Map from original PiMM vertices to generated SDF vertices
  protected Map<AbstractVertex, SDFAbstractVertex> piVx2SDFVx = new LinkedHashMap<>();

  /** The pi port 2 vx. */
  // Map from PiMM ports to their vertex (used for SDFEdge creation)
  protected Map<Port, Configurable> piPort2Vx = new LinkedHashMap<>();
  // Map from original PiMM ports to generated SDF ports (used for SDFEdge
  /** The pi port 2 SDF port. */
  // creation)
  protected Map<Port, SDFInterfaceVertex> piPort2SDFPort = new LinkedHashMap<>();

  /** The current SDF refinement. */
  // Current SDF Refinement
  protected IRefinement currentSDFRefinement;

  /**
   * Creates the value.
   *
   * @param str
   *          the str
   * @return the value
   */
  /* Create An IBSDF value depending of the expression */
  protected Value createValue(final String str) {
    try {
      final int i = Integer.parseInt(str);
      return new ConstantValue(i);
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
  public StaticPiMM2SrDAGVisitor(final PiGraphExecution execution) {
    this.execution = execution;
  }

  /**
   * Convert a PiMM AbstractActor to a DAGVertex.
   *
   * @param a
   *          the AbstractActor
   * @return the DAGVertex
   */
  protected DAGVertex pimm2DAGVertex(final AbstractActor a) {
    final DAGVertex vertex = new DAGVertex();
    // Handle vertex's name
    vertex.setName(a.getName());
    // Handle vertex's path inside the graph hierarchy
    vertex.setInfo(a.getVertexPath());
    // Handle ID
    vertex.setId(a.getName());

    return vertex;
  }

  protected void addSRVertices() {

  }

  /**
   * Transforms parameters from a PiGraph into graph variables of an SDFGraph.
   *
   * @param pg
   *          the PiGraph from which we extract the Parameters
   * @param sdf
   *          the SDFGraph to which we add the graph variables
   */
  protected void parameters2GraphVariables(final PiGraph pg, final SDFGraph sdf) {
    for (final Parameter p : pg.getParameters()) {
      final String evaluate = Long.toString(ExpressionEvaluator.evaluate(p.getValueExpression()));
      final Variable var = new Variable(p.getName(), evaluate);
      sdf.addVariable(var);
    }
  }

  /**
   * Parameters of a top graph must be visited before parameters of a subgraph, since the expression of ConfigurationInputInterface depends on the value of its
   * connected Parameter.
   *
   * @param p
   *          the p
   */
  @Override
  public Boolean caseParameter(Parameter p) {
    if (p.isConfigurationInterface()) {
      final ConfigInputInterface cii = (ConfigInputInterface) p;
      final ConfigInputPort graphPort = cii.getGraphPort();
      final Dependency incomingDependency = graphPort.getIncomingDependency();
      final ISetter setter = incomingDependency.getSetter();
      // Setter of an incoming dependency into a ConfigInputInterface must
      // be a parameter
      if (setter instanceof Parameter) {
        final Expression setterParam = ((Parameter) setter).getValueExpression();
        final Expression pExp = PiMMUserFactory.instance.createExpression();
        pExp.setExpressionString(setterParam.getExpressionString());
        cii.setExpression(pExp);
      }
    } else {
      // If there is only one value available for Parameter p, we can set
      // its
      if (this.execution.hasValue(p)) {
        final Integer value = this.execution.getValue(p);
        final Expression pExp = PiMMUserFactory.instance.createExpression();
        pExp.setExpressionString(value.toString());
        p.setExpression(pExp);
      }
    }
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitConfigInputInterface(org.ietr.preesm.experiment.model.pimm.ConfigInputInterface)
   */
  @Override
  public Boolean caseConfigInputInterface(final ConfigInputInterface cii) {
    final ConfigInputPort graphPort = cii.getGraphPort();
    final Dependency incomingDependency = graphPort.getIncomingDependency();
    final ISetter setter = incomingDependency.getSetter();
    // Setter of an incoming dependency into a ConfigInputInterface must be
    // a parameter
    if (setter instanceof Parameter) {
      final Expression pExp = PiMMUserFactory.instance.createExpression();
      pExp.setExpressionString(((Parameter) setter).getValueExpression().getExpressionString());
      cii.setExpression(pExp);
    }
    return true;
  }

  /**
   * Set the value of parameters of a PiGraph when possible (i.e., if we have currently only one available value, or if we can compute the value)
   *
   * @param graph
   *          the PiGraph in which we want to set the values of parameters
   * @param execution
   *          the list of available values for each parameter
   */
  protected void computeDerivedParameterValues(final PiGraph graph, final PiGraphExecution execution) {
    // If there is no value or list of valuse for one Parameter, the value
    // of the parameter is derived (i.e., computed from other parameters
    // values), we can evaluate it (after the values of other parameters
    // have been fixed)
    for (final Parameter p : graph.getParameters()) {
      if (!execution.hasValue(p)) {
        // Evaluate the expression wrt. the current values of the
        // parameters and set the result as new expression
        final Expression pExp = PiMMUserFactory.instance.createExpression();
        final Expression valueExpression = p.getValueExpression();
        final long evaluate = ExpressionEvaluator.evaluate(valueExpression);
        pExp.setExpressionString(Long.toString(evaluate));
        p.setExpression(pExp);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#caseAbstractActor(org.ietr.preesm.experiment.model.pimm.AbstractActor)
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
      final ISetter setter = p.getIncomingDependency().getSetter();
      if (setter instanceof Parameter) {
        final Parameter param = (Parameter) setter;
        final Argument arg = new Argument(p.getName());
        arg.setValue(param.getName());
        v.getArguments().addArgument(arg);
      }
    }

    caseAbstractActor(a);

    // this.result.addVertex(v);
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
      SDFExpressionEdgePropertyType delay;
      if (f.getDelay() != null) {
        // Evaluate the expression wrt. the current values of the
        // parameters
        delay = new SDFExpressionEdgePropertyType(createValue(Long.toString(ExpressionEvaluator.evaluate(f.getDelay().getSizeExpression()))));
      } else {
        delay = new SDFExpressionEdgePropertyType(new ConstantValue(0));
      }
      // Evaluate the expression wrt. the current values of the parameters
      final SDFExpressionEdgePropertyType cons = new SDFExpressionEdgePropertyType(
          createValue(Long.toString(ExpressionEvaluator.evaluate(piInputPort.getPortRateExpression()))));

      // Evaluate the expression wrt. the current values of the parameters
      final SDFExpressionEdgePropertyType prod = new SDFExpressionEdgePropertyType(
          createValue(Long.toString(ExpressionEvaluator.evaluate(piOutputPort.getPortRateExpression()))));

      // final SDFEdge edge = this.result.addEdge(sdfSource, sdfOutputPort, sdfTarget, sdfInputPort, prod, cons, delay);
      //
      // // Set the data type of the edge
      // edge.setDataType(new SDFStringEdgePropertyType(f.getType()));
      //
      // // Handle memory annotations
      // convertAnnotationsFromTo(piOutputPort, edge, SDFEdge.SOURCE_PORT_MODIFIER);
      // convertAnnotationsFromTo(piInputPort, edge, SDFEdge.TARGET_PORT_MODIFIER);
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
        edge.setPropertyValue(property, new SDFStringEdgePropertyType(SDFEdge.MODIFIER_READ_ONLY));
        break;
      case WRITE_ONLY:
        edge.setPropertyValue(property, new SDFStringEdgePropertyType(SDFEdge.MODIFIER_WRITE_ONLY));
        break;
      case UNUSED:
        edge.setPropertyValue(property, new SDFStringEdgePropertyType(SDFEdge.MODIFIER_UNUSED));
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

    // this.result.addVertex(v);
    return true;
  }

  @Override
  public Boolean caseDataOutputInterface(final DataOutputInterface doi) {
    final SDFSinkInterfaceVertex v = new SDFSinkInterfaceVertex();
    this.piVx2SDFVx.put(doi, v);
    v.setName(doi.getName());

    caseAbstractActor(doi);

    // this.result.addVertex(v);
    return true;
  }

  @Override
  public Boolean casePiSDFRefinement(final PiSDFRefinement r) {
    this.currentSDFRefinement = new CodeRefinement(r.getFilePath());
    return true;
  }

  /** The current prototype. */
  protected Prototype currentPrototype;

  /** The current Argument and Parameter. */
  protected CodeGenArgument currentArgument;

  /** The current parameter. */
  protected CodeGenParameter currentParameter;

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

    // this.result.addVertex(bv);
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

    // this.result.addVertex(jv);
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

    // this.result.addVertex(fv);
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

    // this.result.addVertex(rbv);
    return true;
  }

  /**
   * Gets the result.
   *
   * @return the result
   */
  public MapperDAG getResult() {
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
    if (this.result == null) {
      if (pg.getActors().isEmpty()) {
        return false;
      }
      this.result = new MapperDAG(new MapperEdgeFactory(), pg);
      // Put the root Vertex
      this.result.addVertex(pimm2DAGVertex(pg));

      // updateState(this.result);

      // Starts with the hierarchical actors
      for (final PiGraph subGraph : pg.getChildrenGraphs()) {
        this.doSwitch(subGraph);
      }

    } else {
      // Handle hierarchical actor

    }
    return true;
  }

}
