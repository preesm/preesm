/**
 * 
 */
package org.ietr.preesm.pimm.algorithm.pimm2srdag.visitor;

import java.util.LinkedHashMap;
import java.util.Map;
import org.ietr.dftools.algorithm.model.CodeRefinement;
import org.ietr.dftools.algorithm.model.IRefinement;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGBroadcastVertex;
import org.ietr.dftools.algorithm.model.dag.types.DAGDefaultVertexPropertyType;
import org.ietr.dftools.algorithm.model.parameters.Argument;
import org.ietr.dftools.algorithm.model.parameters.ConstantValue;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.types.SDFExpressionEdgePropertyType;
import org.ietr.dftools.algorithm.model.sdf.types.SDFStringEdgePropertyType;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.codegen.idl.ActorPrototypes;
import org.ietr.preesm.codegen.idl.Prototype;
import org.ietr.preesm.codegen.model.CodeGenArgument;
import org.ietr.preesm.codegen.model.CodeGenParameter;
import org.ietr.preesm.experiment.model.expression.ExpressionEvaluator;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.BroadcastActor;
import org.ietr.preesm.experiment.model.pimm.CHeaderRefinement;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
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
import org.ietr.preesm.experiment.model.pimm.JoinActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiSDFRefinement;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;
import org.ietr.preesm.experiment.model.pimm.util.PiMMSwitch;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.pimm.algorithm.pimm2sdf.PiGraphExecution;

/**
 * @author farresti
 *
 */
public class StaticPiMM2SrDAGVisitor extends PiMMSwitch<Boolean> {
  /** The result. */
  // SRDAG graph created from the outer graph
  private MapperDAG result;

  /** Basic repetition vector of the graph */
  private Map<AbstractVertex, Integer> brv;

  /** Counter for hierarchical actors indexes */
  private int hCounter;

  /** Counter for actors indexes */
  private int aCounter;

  /** The execution. */
  // Original list of fixed values for all the parameters of the graph
  protected PiGraphExecution execution;

  /** The pi vx 2 SDF vx. */
  // Map from original PiMM vertices to generated SDF vertices
  protected Map<AbstractVertex, DAGVertex> piVx2DAGVx = new LinkedHashMap<>();

  /** The pi port 2 vx. */
  // Map from PiMM ports to their vertex (used for SDFEdge creation)
  protected Map<Port, Configurable> piPort2Vx = new LinkedHashMap<>();
  // Map from original PiMM ports to generated SDF ports (used for SDFEdge
  /** The pi port 2 SDF port. */
  // creation)
  protected Map<Port, SDFInterfaceVertex> piPort2SDFPort = new LinkedHashMap<>();

  /** The current SDF refinement. */
  // Current SDF Refinement
  protected IRefinement currentRefinement;

  /**
   * Instantiates a new abstract pi MM 2 SR-DAG visitor.
   *
   * @param dag
   *          the dag
   */
  public StaticPiMM2SrDAGVisitor(final MapperDAG dag, Map<AbstractVertex, Integer> brv) {
    this.result = dag;
    this.brv = brv;
  }

  /**
   * Convert a PiMM AbstractActor to a DAGVertex.
   *
   * @param a
   *          the AbstractActor
   * @return the DAGVertex
   */
  private void pimm2srdag(final AbstractActor a, final DAGVertex vertex) {
    final String nameInfoID = a.getVertexPath() + "_" + Integer.toString(aCounter);
    // Handle vertex's name
    vertex.setName(nameInfoID);
    // Handle vertex's path inside the graph hierarchy
    vertex.setInfo(nameInfoID);
    // Handle ID
    vertex.setId(nameInfoID);
    // Set Repetition vector
    vertex.setNbRepeat(new DAGDefaultVertexPropertyType(this.brv.get(a)));
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#caseAbstractActor(org.ietr.preesm.experiment.model.pimm.AbstractActor)
   */
  @Override
  public Boolean caseAbstractActor(final AbstractActor actor) {
    // // Handle the target ports (DataInputPort in PISDF,
    // // SDFSourceInterfaceVertex in IBSDF) keeping the order
    // for (final DataInputPort dip : aa.getDataInputPorts()) {
    // // The target SDF port
    // SDFSourceInterfaceVertex sdfInputPort;
    // // The SDF vertex to which to add the created port
    // final SDFAbstractVertex sdfTarget = this.piVx2SDFVx.get(aa);
    // if (sdfTarget instanceof SDFSourceInterfaceVertex) {
    // // If the SDF vertex is an interface, use it as the port
    // sdfInputPort = (SDFSourceInterfaceVertex) sdfTarget;
    // } else {
    // // Otherwise create a new port and add it to the SDF vertex
    // sdfInputPort = new SDFSourceInterfaceVertex();
    // sdfInputPort.setName(dip.getName());
    // sdfTarget.addSource(sdfInputPort);
    // }
    // this.piPort2SDFPort.put(dip, sdfInputPort);
    // this.piPort2Vx.put(dip, aa);
    // }
    // // Handle the source ports (DataOuputPort in PISDF,
    // // SDFSinkInterfaceVertex in IBSDF) keeping the order
    // for (final DataOutputPort dop : aa.getDataOutputPorts()) {
    // // The source SDF port
    // SDFSinkInterfaceVertex sdfOutputPort;
    // // The SDF vertex to which to add the created port
    // final SDFAbstractVertex sdfSource = this.piVx2SDFVx.get(aa);
    // if (sdfSource instanceof SDFSinkInterfaceVertex) {
    // // If the SDF vertex is an interface, use it as the port
    // sdfOutputPort = (SDFSinkInterfaceVertex) sdfSource;
    // } else {
    // // Otherwise create a new port and add it to the SDF vertex
    // sdfOutputPort = new SDFSinkInterfaceVertex();
    // sdfOutputPort.setName(dop.getName());
    // sdfSource.addSink(sdfOutputPort);
    // }
    // this.piPort2SDFPort.put(dop, sdfOutputPort);
    // this.piPort2Vx.put(dop, aa);
    // }
    // for (final ConfigOutputPort cop : aa.getConfigOutputPorts()) {
    // this.piPort2Vx.put(cop, aa);
    // }
    // caseConfigurable(aa);
    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitActor(org.ietr.preesm.experiment.model.pimm.Actor)
   */
  @Override
  public Boolean caseActor(final Actor actor) {
    final DAGVertex vertex = new DAGVertex();
    // Set default properties from the PiMM actor
    pimm2srdag(actor, vertex);
    // Handle path to memory script of the vertex
    if (actor.getMemoryScriptPath() != null) {
      vertex.setPropertyValue(SDFVertex.MEMORY_SCRIPT, actor.getMemoryScriptPath().toOSString());
    }
    // Handle vertex's refinement (description of the vertex's behavior:
    // function prototypes or subgraphs)
    final Refinement piRefinement = actor.getRefinement();
    doSwitch(piRefinement);
    vertex.setRefinement(this.currentRefinement);
    this.result.addVertex(vertex);
    // caseAbstractActor(a);
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
  public Boolean caseDataInputInterface(final DataInputInterface dii) {
    final SDFSourceInterfaceVertex v = new SDFSourceInterfaceVertex();
    this.piVx2SDFVx.put(dii, v);
    v.setName(dii.getName());

    caseAbstractActor(dii);

    return true;
  }

  @Override
  public Boolean caseDataOutputInterface(final DataOutputInterface doi) {
    final SDFSinkInterfaceVertex v = new SDFSinkInterfaceVertex();
    this.piVx2SDFVx.put(doi, v);
    v.setName(doi.getName());

    caseAbstractActor(doi);

    return true;
  }

  @Override
  public Boolean casePiSDFRefinement(final PiSDFRefinement refinement) {
    this.currentRefinement = new CodeRefinement(refinement.getFilePath());
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

    this.currentRefinement = actorPrototype;
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
  public Boolean caseBroadcastActor(final BroadcastActor actor) {
    final DAGBroadcastVertex vertex = new DAGBroadcastVertex();
    pimm2srdag(actor, vertex);
    // Check the good use of the broadcast
    final DataInputPort dataInputPort = actor.getDataInputPorts().get(0);
    final Expression portRateExpression = dataInputPort.getPortRateExpression();
    final long cons = Long.parseLong(portRateExpression.getExpressionString());
    for (final DataOutputPort out : actor.getDataOutputPorts()) {
      final Expression outPortRateExpression = out.getPortRateExpression();
      final long prod = Long.parseLong(outPortRateExpression.getExpressionString());
      if (prod != cons) {
        WorkflowLogger.getLogger()
            .warning("Warning: Broadcast have different production/consumption: prod(" + Long.toString(prod) + ") != cons(" + Long.toString(cons) + ")");
      }
    }
    // add a dag vertex to the single rate graph
    dag.addVertex(dagVertex);

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
    if (pg.getActors().isEmpty()) {
      return false;
    }
    final DAGVertex vertex = this.result.getVertex(pg.getVertexPath() + "_" + Integer.toString(this.hCounter));
    if (vertex == null) {
      throw new RuntimeException("Failed to convert PiMM 2 SR-DAG.\nVertex [" + pg.getVertexPath() + "_" + Integer.toString(this.hCounter) + "] not found.");
    } else {
      // Remove the hierarchical vertex from the DAG
      // We are going to replace it with its contents
      this.result.removeVertex(vertex);
    }

    // Add SR-Vertices
    for (final AbstractActor actor : pg.getActors()) {
      for (int i = 0; i < this.brv.get(actor); ++i) {
        this.aCounter = i;
        // Treat hierarchical graphs as normal actors
        // This populate the DAG with the right amount of hierarchical instances w.r.t the BRV value
        if (actor instanceof PiGraph) {
          caseAbstractActor(actor);
          continue;
        }
        doSwitch(actor);
      }
    }

    // Link SR-Vertices

    // Go check hierarchical graphs
    for (final PiGraph g : pg.getChildrenGraphs()) {
      // We have to iterate for every number of hierarchical graph we populated
      // TODO this is not optimal. Find better approach
      for (int i = 0; i < this.brv.get(g); ++i) {
        this.hCounter = i;
        doSwitch(g);
      }
    }
    return true;
  }

}
