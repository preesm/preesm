/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Hascoet <jhascoet@kalray.eu> (2016)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2014)
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
package org.ietr.preesm.pimm.algorithm.pimm2sdf.visitor;

import java.util.LinkedHashMap;
import java.util.Map;
import org.ietr.dftools.algorithm.model.CodeRefinement;
import org.ietr.dftools.algorithm.model.IRefinement;
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
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiSDFRefinement;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;
import org.ietr.preesm.experiment.model.pimm.util.PiMMDefaultVisitor;
import org.ietr.preesm.pimm.algorithm.pimm2sdf.PiGraphExecution;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractPiMM2SDFVisitor.
 */
public abstract class AbstractPiMM2SDFVisitor extends PiMMDefaultVisitor {

  /** The result. */
  // SDFGraph created from the outer graph
  protected SDFGraph result;

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

  /** The pi factory. */
  // Factory for creation of new Pi Expressions
  protected PiMMFactory piFactory = PiMMFactory.eINSTANCE;

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
  public AbstractPiMM2SDFVisitor(final PiGraphExecution execution) {
    this.execution = execution;
  }

  /**
   * Entry point of the visitor is to be defined by the subclass.
   *
   * @param pg
   *          the pg
   */
  @Override
  public abstract void visitPiGraph(PiGraph pg);

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
      final Variable var = new Variable(p.getName(), p.getValueExpression().evaluate());
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
  public void visitParameter(final Parameter p) {
    if (p.isConfigurationInterface()) {
      final ConfigInputPort graphPort = p.getGraphPort();
      final Dependency incomingDependency = graphPort.getIncomingDependency();
      final ISetter setter = incomingDependency.getSetter();
      // Setter of an incoming dependency into a ConfigInputInterface must
      // be a parameter
      if (setter instanceof Parameter) {
        final Expression setterParam = ((Parameter) setter).getValueExpression();
        final Expression pExp = this.piFactory.createExpression();
        pExp.setExpressionString(setterParam.getExpressionString());
        p.setValueExpression(pExp);
      }
    } else {
      // If there is only one value available for Parameter p, we can set
      // its
      if (this.execution.hasValue(p)) {
        final Integer value = this.execution.getValue(p);
        final Expression pExp = this.piFactory.createExpression();
        pExp.setExpressionString(value.toString());
        p.setValueExpression(pExp);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitConfigInputInterface(org.ietr.preesm.experiment.model.pimm.ConfigInputInterface)
   */
  @Override
  public void visitConfigInputInterface(final ConfigInputInterface cii) {
    final ISetter setter = cii.getGraphPort().getIncomingDependency().getSetter();
    // Setter of an incoming dependency into a ConfigInputInterface must be
    // a parameter
    if (setter instanceof Parameter) {
      final Expression pExp = this.piFactory.createExpression();
      pExp.setExpressionString(((Parameter) setter).getValueExpression().getExpressionString());
      cii.setValueExpression(pExp);
    }
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
        final Expression pExp = this.piFactory.createExpression();
        final String value = p.getValueExpression().evaluate();
        pExp.setExpressionString(value);
        p.setValueExpression(pExp);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitAbstractActor(org.ietr.preesm.experiment.model.pimm.AbstractActor)
   */
  @Override
  public void visitAbstractActor(final AbstractActor aa) {
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
    visitConfigurable(aa);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitActor(org.ietr.preesm.experiment.model.pimm.Actor)
   */
  @Override
  public void visitActor(final Actor a) {
    final SDFVertex v = new SDFVertex();
    this.piVx2SDFVx.put(a, v);
    // Handle vertex's name
    v.setName(a.getName());
    // Handle vertex's path inside the graph hierarchy
    v.setInfo(a.getPath());
    // Handle ID
    v.setId(a.getName());
    // Handle vertex's refinement (description of the vertex's behavior:
    // function prototypes or subgraphs)
    final Refinement piRef = a.getRefinement();
    piRef.accept(this);
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

    visitAbstractActor(a);

    this.result.addVertex(v);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitFifo(org.ietr.preesm.experiment.model.pimm.Fifo)
   */
  @Override
  public void visitFifo(final Fifo f) {
    final DataOutputPort piOutputPort = f.getSourcePort();
    final DataInputPort piInputPort = f.getTargetPort();

    final Configurable source = this.piPort2Vx.get(piOutputPort);
    final Configurable target = this.piPort2Vx.get(piInputPort);

    if ((source instanceof AbstractVertex) && (target instanceof AbstractVertex)) {
      // Get SDFAbstractVertices corresponding to source and target
      final SDFAbstractVertex sdfSource = this.piVx2SDFVx.get(source);
      final SDFAbstractVertex sdfTarget = this.piVx2SDFVx.get(target);

      // Get the source port created in visitAbstractActor
      final SDFSinkInterfaceVertex sdfOutputPort = (SDFSinkInterfaceVertex) this.piPort2SDFPort.get(piOutputPort);

      // Get the target port created in visitAbstractActor
      final SDFSourceInterfaceVertex sdfInputPort = (SDFSourceInterfaceVertex) this.piPort2SDFPort.get(piInputPort);

      // Handle Delay, Consumption and Production rates
      SDFExpressionEdgePropertyType delay;
      if (f.getDelay() != null) {
        // Evaluate the expression wrt. the current values of the
        // parameters
        final String piDelay = f.getDelay().getSizeExpression().evaluate();
        delay = new SDFExpressionEdgePropertyType(createValue(piDelay));
      } else {
        delay = new SDFExpressionEdgePropertyType(new ConstantValue(0));
      }
      // Evaluate the expression wrt. the current values of the parameters
      final String piCons = piInputPort.getPortRateExpression().evaluate();
      final SDFExpressionEdgePropertyType cons = new SDFExpressionEdgePropertyType(createValue(piCons));

      // Evaluate the expression wrt. the current values of the parameters
      final String piProd = piOutputPort.getPortRateExpression().evaluate();
      final SDFExpressionEdgePropertyType prod = new SDFExpressionEdgePropertyType(createValue(piProd));

      final SDFEdge edge = this.result.addEdge(sdfSource, sdfOutputPort, sdfTarget, sdfInputPort, prod, cons, delay);

      // Set the data type of the edge
      edge.setDataType(new SDFStringEdgePropertyType(f.getType()));

      // Handle memory annotations
      convertAnnotationsFromTo(piOutputPort, edge, SDFEdge.SOURCE_PORT_MODIFIER);
      convertAnnotationsFromTo(piInputPort, edge, SDFEdge.TARGET_PORT_MODIFIER);
    }
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

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitParameterizable(org.ietr.preesm.experiment.model.pimm.Parameterizable)
   */
  @Override
  public void visitConfigurable(final Configurable p) {
    for (final ConfigInputPort cip : p.getConfigInputPorts()) {
      this.piPort2Vx.put(cip, p);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitInterfaceActor(org.ietr.preesm.experiment.model.pimm.InterfaceActor)
   */
  @Override
  public void visitInterfaceActor(final InterfaceActor ia) {
    // DO NOTHING
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitConfigOutputInterface(org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface)
   */
  @Override
  public void visitConfigOutputInterface(final ConfigOutputInterface coi) {
    visitInterfaceActor(coi);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitDataInputInterface(org.ietr.preesm.experiment.model.pimm.DataInputInterface)
   */
  @Override
  public void visitDataInputInterface(final DataInputInterface dii) {
    final SDFSourceInterfaceVertex v = new SDFSourceInterfaceVertex();
    this.piVx2SDFVx.put(dii, v);
    v.setName(dii.getName());

    visitAbstractActor(dii);

    this.result.addVertex(v);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitDataOutputInterface(org.ietr.preesm.experiment.model.pimm.DataOutputInterface)
   */
  @Override
  public void visitDataOutputInterface(final DataOutputInterface doi) {
    final SDFSinkInterfaceVertex v = new SDFSinkInterfaceVertex();
    this.piVx2SDFVx.put(doi, v);
    v.setName(doi.getName());

    visitAbstractActor(doi);

    this.result.addVertex(v);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitRefinement(org.ietr.preesm.experiment.model.pimm.Refinement)
   */
  @Override
  public void visitRefinement(final PiSDFRefinement r) {
    this.currentSDFRefinement = new CodeRefinement(r.getFilePath());
  }

  /** The current prototype. */
  // Current Prototype
  protected Prototype currentPrototype;

  /** The current argument. */
  // Current Argument and Parameter
  protected CodeGenArgument currentArgument;

  /** The current parameter. */
  protected CodeGenParameter currentParameter;

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitHRefinement(org.ietr.preesm.experiment.model.pimm.HRefinement)
   */
  @Override
  public void visitHRefinement(final CHeaderRefinement h) {
    final ActorPrototypes actorPrototype = new ActorPrototypes(h.getFilePath().toOSString());

    h.getLoopPrototype().accept(this);
    actorPrototype.setLoopPrototype(this.currentPrototype);

    if (h.getInitPrototype() != null) {
      h.getInitPrototype().accept(this);
      actorPrototype.setInitPrototype(this.currentPrototype);
    }

    this.currentSDFRefinement = actorPrototype;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitFunctionPrototype(org.ietr.preesm.experiment.model.pimm.FunctionPrototype)
   */
  @Override
  public void visitFunctionPrototype(final FunctionPrototype f) {
    this.currentPrototype = new Prototype(f.getName());
    for (final FunctionParameter p : f.getParameters()) {
      p.accept(this);
      if (p.isIsConfigurationParameter()) {
        this.currentPrototype.addParameter(this.currentParameter);
      } else {
        this.currentPrototype.addArgument(this.currentArgument);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitFunctionParameter(org.ietr.preesm.experiment.model.pimm.FunctionParameter)
   */
  @Override
  public void visitFunctionParameter(final FunctionParameter f) {
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
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitBroadcastActor(org.ietr.preesm.experiment.model.pimm.BroadcastActor)
   */
  @Override
  public void visitBroadcastActor(final BroadcastActor ba) {
    final SDFBroadcastVertex bv = new SDFBroadcastVertex();
    this.piVx2SDFVx.put(ba, bv);
    // Handle vertex's name
    bv.setName(ba.getName());
    // Handle vertex's path inside the graph hierarchy
    bv.setInfo(ba.getPath());

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

    visitAbstractActor(ba);

    this.result.addVertex(bv);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitJoinActor(org.ietr.preesm.experiment.model.pimm.JoinActor)
   */
  @Override
  public void visitJoinActor(final JoinActor ja) {
    final SDFJoinVertex jv = new SDFJoinVertex();
    this.piVx2SDFVx.put(ja, jv);
    // Handle vertex's name
    jv.setName(ja.getName());
    // Handle vertex's path inside the graph hierarchy
    jv.setInfo(ja.getPath());

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

    visitAbstractActor(ja);

    this.result.addVertex(jv);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitForkActor(org.ietr.preesm.experiment.model.pimm.ForkActor)
   */
  @Override
  public void visitForkActor(final ForkActor fa) {
    final SDFForkVertex fv = new SDFForkVertex();
    this.piVx2SDFVx.put(fa, fv);
    // Handle vertex's name
    fv.setName(fa.getName());
    // Handle vertex's path inside the graph hierarchy
    fv.setInfo(fa.getPath());

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

    visitAbstractActor(fa);

    this.result.addVertex(fv);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitRoundBufferActor(org.ietr.preesm.experiment.model.pimm.RoundBufferActor)
   */
  @Override
  public void visitRoundBufferActor(final RoundBufferActor rba) {
    final SDFRoundBufferVertex rbv = new SDFRoundBufferVertex();
    this.piVx2SDFVx.put(rba, rbv);
    // Handle vertex's name
    rbv.setName(rba.getName());
    // Handle vertex's path inside the graph hierarchy
    rbv.setInfo(rba.getPath());

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

    visitAbstractActor(rba);

    this.result.addVertex(rbv);
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
  public void visitDataOutputPort(final DataOutputPort dop) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitDelay(org.ietr.preesm.experiment.model.pimm.Delay)
   */
  @Override
  public void visitDelay(final Delay d) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitDataInputPort(org.ietr.preesm.experiment.model.pimm.DataInputPort)
   */
  @Override
  public void visitDataInputPort(final DataInputPort dip) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitExpression(org.ietr.preesm.experiment.model.pimm.Expression)
   */
  @Override
  public void visitExpression(final Expression e) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitConfigInputPort(org.ietr.preesm.experiment.model.pimm.ConfigInputPort)
   */
  @Override
  public void visitConfigInputPort(final ConfigInputPort cip) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitConfigOutputPort(org.ietr.preesm.experiment.model.pimm.ConfigOutputPort)
   */
  @Override
  public void visitConfigOutputPort(final ConfigOutputPort cop) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitDependency(org.ietr.preesm.experiment.model.pimm.Dependency)
   */
  @Override
  public void visitDependency(final Dependency d) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitISetter(org.ietr.preesm.experiment.model.pimm.ISetter)
   */
  @Override
  public void visitISetter(final ISetter is) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitPort(org.ietr.preesm.experiment.model.pimm.Port)
   */
  @Override
  public void visitPort(final Port p) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitDataPort(org.ietr.preesm.experiment.model.pimm.DataPort)
   */
  @Override
  public void visitDataPort(final DataPort p) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor#visitExecutableActor(org.ietr.preesm.experiment.model.pimm.ExecutableActor)
   */
  @Override
  public void visitExecutableActor(final ExecutableActor ea) {
    throw new UnsupportedOperationException();
  }
}
