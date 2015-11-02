/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy, Yaset Oliva Venegas
 * 
 * [mpelcat,jnezan,kdesnos,jheulot,cguy,yoliva]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.pimm.algorithm.pimm2sdf.visitor;

import java.util.HashMap;
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
import org.ietr.preesm.experiment.model.pimm.ConfigInputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
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
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.JoinActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;
import org.ietr.preesm.experiment.model.pimm.impl.FunctionParameterImpl;
import org.ietr.preesm.experiment.model.pimm.impl.FunctionPrototypeImpl;
import org.ietr.preesm.experiment.model.pimm.impl.HRefinementImpl;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;
import org.ietr.preesm.pimm.algorithm.pimm2sdf.PiGraphExecution;

public abstract class AbstractPiMM2SDFVisitor extends PiMMVisitor {
	// SDFGraph created from the outer graph
	protected SDFGraph result;
	// Original list of fixed values for all the parameters of the graph
	protected PiGraphExecution execution;

	// Map from original PiMM vertices to generated SDF vertices
	protected Map<AbstractVertex, SDFAbstractVertex> piVx2SDFVx = new HashMap<AbstractVertex, SDFAbstractVertex>();
	// Map from PiMM ports to their vertex (used for SDFEdge creation)
	protected Map<Port, Parameterizable> piPort2Vx = new HashMap<Port, Parameterizable>();
	// Map from original PiMM ports to generated SDF ports (used for SDFEdge
	// creation)
	protected Map<Port, SDFInterfaceVertex> piPort2SDFPort = new HashMap<Port, SDFInterfaceVertex>();

	// Current SDF Refinement
	protected IRefinement currentSDFRefinement;

	// Factory for creation of new Pi Expressions
	protected PiMMFactory piFactory = PiMMFactory.eINSTANCE;

	/* Create An IBSDF value depending of the expression */
	protected Value createValue(String str){
		try{
			int i = Integer.parseInt(str);
			return new ConstantValue(i);
		}catch(NumberFormatException e){
			return new ExpressionValue(str);	
		}	
	}
	
	public AbstractPiMM2SDFVisitor(PiGraphExecution execution) {
		this.execution = execution;
	}

	/**
	 * Entry point of the visitor is to be defined by the subclass
	 */
	@Override
	public abstract void visitPiGraph(PiGraph pg);

	/**
	 * Transforms parameters from a PiGraph into graph variables of an SDFGraph
	 * 
	 * @param pg
	 *            the PiGraph from which we extract the Parameters
	 * @param sdf
	 *            the SDFGraph to which we add the graph variables
	 */
	protected void parameters2GraphVariables(PiGraph pg, SDFGraph sdf) {
		for (Parameter p : pg.getParameters()) {
			Variable var = new Variable(p.getName(), p.getExpression()
					.evaluate());
			sdf.addVariable(var);
		}
	}

	/**
	 * Parameters of a top graph must be visited before parameters of a
	 * subgraph, since the expression of ConfigurationInputInterface depends on
	 * the value of its connected Parameter
	 */
	@Override
	public void visitParameter(Parameter p) {
		if (p.isConfigurationInterface()) {
			ISetter setter = p.getGraphPort().getIncomingDependency()
					.getSetter();
			// Setter of an incoming dependency into a ConfigInputInterface must
			// be a parameter
			if (setter instanceof Parameter) {
				Expression pExp = piFactory.createExpression();
				pExp.setString(((Parameter) setter).getExpression().getString());
				p.setExpression(pExp);
			}
		} else {
			// If there is only one value available for Parameter p, we can set
			// it
			Integer value = execution.getUniqueValue(p);
			if (value != null) {
				Expression pExp = piFactory.createExpression();
				pExp.setString(value.toString());
				p.setExpression(pExp);
			}
		}
	}

	@Override
	public void visitConfigInputInterface(ConfigInputInterface cii) {
		ISetter setter = cii.getGraphPort().getIncomingDependency().getSetter();
		// Setter of an incoming dependency into a ConfigInputInterface must be
		// a parameter
		if (setter instanceof Parameter) {
			Expression pExp = piFactory.createExpression();
			pExp.setString(((Parameter) setter).getExpression().getString());
			cii.setExpression(pExp);
		}
	}

	/**
	 * Set the value of parameters of a PiGraph when possible (i.e., if we have
	 * currently only one available value, or if we can compute the value)
	 * 
	 * @param graph
	 *            the PiGraph in which we want to set the values of parameters
	 * @param execution
	 *            the list of available values for each parameter
	 */
	protected void computeDerivedParameterValues(PiGraph graph,
			PiGraphExecution execution) {
		// If there is no value or list of valuse for one Parameter, the value
		// of the parameter is derived (i.e., computed from other parameters
		// values), we can evaluate it (after the values of other parameters
		// have been fixed)
		for (Parameter p : graph.getParameters()) {
			if (!execution.hasValue(p)) {
				// Evaluate the expression wrt. the current values of the
				// parameters and set the result as new expression
				Expression pExp = piFactory.createExpression();
				String value = p.getExpression().evaluate();
				pExp.setString(value);
				p.setExpression(pExp);
			}
		}
	}

	@Override
	public void visitAbstractActor(AbstractActor aa) {
		// Handle the target ports (DataInputPort in PISDF,
		// SDFSourceInterfaceVertex in IBSDF) keeping the order
		for (DataInputPort dip : aa.getDataInputPorts()) {
			// The target SDF port
			SDFSourceInterfaceVertex sdfInputPort;
			// The SDF vertex to which to add the created port
			SDFAbstractVertex sdfTarget = piVx2SDFVx.get(aa);
			if (sdfTarget instanceof SDFSourceInterfaceVertex) {
				// If the SDF vertex is an interface, use it as the port
				sdfInputPort = (SDFSourceInterfaceVertex) sdfTarget;
			} else {
				// Otherwise create a new port and add it to the SDF vertex
				sdfInputPort = new SDFSourceInterfaceVertex();
				sdfInputPort.setName(dip.getName());
				sdfTarget.addSource(sdfInputPort);
			}
			piPort2SDFPort.put(dip, sdfInputPort);
			piPort2Vx.put(dip, aa);
		}
		// Handle the source ports (DataOuputPort in PISDF,
		// SDFSinkInterfaceVertex in IBSDF) keeping the order
		for (DataOutputPort dop : aa.getDataOutputPorts()) {
			// The source SDF port
			SDFSinkInterfaceVertex sdfOutputPort;
			// The SDF vertex to which to add the created port
			SDFAbstractVertex sdfSource = piVx2SDFVx.get(aa);
			if (sdfSource instanceof SDFSinkInterfaceVertex) {
				// If the SDF vertex is an interface, use it as the port
				sdfOutputPort = (SDFSinkInterfaceVertex) sdfSource;
			} else {
				// Otherwise create a new port and add it to the SDF vertex
				sdfOutputPort = new SDFSinkInterfaceVertex();
				sdfOutputPort.setName(dop.getName());
				sdfSource.addSink(sdfOutputPort);
			}
			piPort2SDFPort.put(dop, sdfOutputPort);
			piPort2Vx.put(dop, aa);
		}
		for (ConfigOutputPort cop : aa.getConfigOutputPorts()) {
			piPort2Vx.put(cop, aa);
		}
		visitAbstractVertex(aa);
	}

	@Override
	public void visitAbstractVertex(AbstractVertex av) {
		visitParameterizable(av);
	}

	@Override
	public void visitActor(Actor a) {
		SDFVertex v = new SDFVertex();
		piVx2SDFVx.put(a, v);
		// Handle vertex's name
		v.setName(a.getName());
		// Handle vertex's path inside the graph hierarchy
		v.setInfo(a.getPath());
		// Handle ID
		v.setId(a.getName());
		// Handle vertex's refinement (description of the vertex's behavior:
		// function prototypes or subgraphs)
		Refinement piRef = a.getRefinement();
		piRef.accept(this);
		v.setRefinement(currentSDFRefinement);
		// Handle path to memory script of the vertex
		if (a.getMemoryScriptPath() != null) {
			v.setPropertyValue(SDFVertex.MEMORY_SCRIPT, a.getMemoryScriptPath()
					.toOSString());
		}
		// Handle input parameters as instance arguments
		for (ConfigInputPort p : a.getConfigInputPorts()) {
			ISetter setter = p.getIncomingDependency().getSetter();
			if (setter instanceof Parameter) {
				Parameter param = (Parameter) setter;
				Argument arg = new Argument(p.getName());
				arg.setValue(param.getName());
				v.getArguments().addArgument(arg);
			}
		}

		visitAbstractActor(a);

		result.addVertex(v);
	}

	@Override
	public void visitFifo(Fifo f) {
		DataOutputPort piOutputPort = f.getSourcePort();
		DataInputPort piInputPort = f.getTargetPort();

		Parameterizable source = piPort2Vx.get(piOutputPort);
		Parameterizable target = piPort2Vx.get(piInputPort);

		if (source instanceof AbstractVertex
				&& target instanceof AbstractVertex) {
			// Get SDFAbstractVertices corresponding to source and target
			SDFAbstractVertex sdfSource = piVx2SDFVx.get(source);
			SDFAbstractVertex sdfTarget = piVx2SDFVx.get(target);

			// Get the source port created in visitAbstractActor
			SDFSinkInterfaceVertex sdfOutputPort = (SDFSinkInterfaceVertex) piPort2SDFPort
					.get(piOutputPort);

			// Get the target port created in visitAbstractActor
			SDFSourceInterfaceVertex sdfInputPort = (SDFSourceInterfaceVertex) piPort2SDFPort
					.get(piInputPort);

			// Handle Delay, Consumption and Production rates
			SDFExpressionEdgePropertyType delay;
			if (f.getDelay() != null) {
				// Evaluate the expression wrt. the current values of the
				// parameters
				String piDelay = f.getDelay().getExpression().evaluate();
				delay = new SDFExpressionEdgePropertyType(createValue(piDelay));
			} else {
				delay = new SDFExpressionEdgePropertyType(new ConstantValue(0));
			}
			// Evaluate the expression wrt. the current values of the parameters
			String piCons = piInputPort.getExpression().evaluate();
			SDFExpressionEdgePropertyType cons = new SDFExpressionEdgePropertyType(createValue(piCons));

			// Evaluate the expression wrt. the current values of the parameters
			String piProd = piOutputPort.getExpression().evaluate();
			SDFExpressionEdgePropertyType prod = new SDFExpressionEdgePropertyType(createValue(piProd));

			SDFEdge edge = result.addEdge(sdfSource, sdfOutputPort, sdfTarget,
					sdfInputPort, prod, cons, delay);

			// Set the data type of the edge
			edge.setDataType(new SDFStringEdgePropertyType(f.getType()));

			// Handle memory annotations
			convertAnnotationsFromTo(piOutputPort, edge,
					SDFEdge.SOURCE_PORT_MODIFIER);
			convertAnnotationsFromTo(piInputPort, edge,
					SDFEdge.TARGET_PORT_MODIFIER);
		}
	}

	private void convertAnnotationsFromTo(DataPort piPort, SDFEdge edge,
			String property) {
		switch (piPort.getAnnotation()) {
		case READ_ONLY:
			edge.setPropertyValue(property, new SDFStringEdgePropertyType(
					SDFEdge.MODIFIER_READ_ONLY));
			break;
		case WRITE_ONLY:
			edge.setPropertyValue(property, new SDFStringEdgePropertyType(
					SDFEdge.MODIFIER_WRITE_ONLY));
			break;
		case UNUSED:
			edge.setPropertyValue(property, new SDFStringEdgePropertyType(
					SDFEdge.MODIFIER_UNUSED));
			break;
		default:
		}
	}

	@Override
	public void visitParameterizable(Parameterizable p) {
		for (ConfigInputPort cip : p.getConfigInputPorts()) {
			piPort2Vx.put(cip, p);
		}
	}

	@Override
	public void visitInterfaceActor(InterfaceActor ia) {
		// DO NOTHING
	}

	@Override
	public void visitConfigOutputInterface(ConfigOutputInterface coi) {
		visitInterfaceActor(coi);
	}

	@Override
	public void visitDataInputInterface(DataInputInterface dii) {
		SDFSourceInterfaceVertex v = new SDFSourceInterfaceVertex();
		piVx2SDFVx.put(dii, v);
		v.setName(dii.getName());

		visitAbstractActor(dii);

		result.addVertex(v);
	}

	@Override
	public void visitDataOutputInterface(DataOutputInterface doi) {
		SDFSinkInterfaceVertex v = new SDFSinkInterfaceVertex();
		piVx2SDFVx.put(doi, v);
		v.setName(doi.getName());

		visitAbstractActor(doi);

		result.addVertex(v);
	}

	@Override
	public void visitRefinement(Refinement r) {
		currentSDFRefinement = new CodeRefinement(r.getFilePath());
	}

	// Current Prototype
	protected Prototype currentPrototype;
	// Current Argument and Parameter
	protected CodeGenArgument currentArgument;
	protected CodeGenParameter currentParameter;

	@Override
	public void visitHRefinement(HRefinementImpl h) {
		ActorPrototypes actorPrototype = new ActorPrototypes(h.getFilePath()
				.toOSString());

		h.getLoopPrototype().accept(this);
		actorPrototype.setLoopPrototype(currentPrototype);

		if (h.getInitPrototype() != null) {
			h.getInitPrototype().accept(this);
			actorPrototype.setInitPrototype(currentPrototype);
		}

		currentSDFRefinement = actorPrototype;
	}

	@Override
	public void visitFunctionPrototype(FunctionPrototypeImpl f) {
		currentPrototype = new Prototype(f.getName());
		for (FunctionParameter p : f.getParameters()) {
			p.accept(this);
			if (p.isIsConfigurationParameter())
				currentPrototype.addParameter(currentParameter);
			else
				currentPrototype.addArgument(currentArgument);
		}
	}

	@Override
	public void visitFunctionParameter(FunctionParameterImpl f) {
		if (f.isIsConfigurationParameter()) {
			int direction = 0;
			switch (f.getDirection()) {
			case IN:
				direction = 0;
				break;
			case OUT:
				direction = 1;
				break;
			}
			currentParameter = new CodeGenParameter(f.getName(), direction);
		} else {
			String direction = "";
			switch (f.getDirection()) {
			case IN:
				direction = CodeGenArgument.INPUT;
				break;
			case OUT:
				direction = CodeGenArgument.OUTPUT;
				break;
			}
			currentArgument = new CodeGenArgument(f.getName(), direction);
			currentArgument.setType(f.getType());
		}
	}

	@Override
	public void visitBroadcastActor(BroadcastActor ba) {
		SDFBroadcastVertex bv = new SDFBroadcastVertex();
		piVx2SDFVx.put(ba, bv);
		// Handle vertex's name
		bv.setName(ba.getName());
		// Handle vertex's path inside the graph hierarchy
		bv.setInfo(ba.getPath());

		// Handle input parameters as instance arguments
		for (ConfigInputPort p : ba.getConfigInputPorts()) {
			ISetter setter = p.getIncomingDependency().getSetter();
			if (setter instanceof Parameter) {
				Parameter param = (Parameter) setter;
				Argument arg = new Argument(p.getName());
				arg.setValue(param.getName());
				bv.getArguments().addArgument(arg);
			}
		}

		visitAbstractActor(ba);

		result.addVertex(bv);
	}

	@Override
	public void visitJoinActor(JoinActor ja) {
		SDFJoinVertex jv = new SDFJoinVertex();
		piVx2SDFVx.put(ja, jv);
		// Handle vertex's name
		jv.setName(ja.getName());
		// Handle vertex's path inside the graph hierarchy
		jv.setInfo(ja.getPath());

		// Handle input parameters as instance arguments
		for (ConfigInputPort p : ja.getConfigInputPorts()) {
			ISetter setter = p.getIncomingDependency().getSetter();
			if (setter instanceof Parameter) {
				Parameter param = (Parameter) setter;
				Argument arg = new Argument(p.getName());
				arg.setValue(param.getName());
				jv.getArguments().addArgument(arg);
			}
		}

		visitAbstractActor(ja);

		result.addVertex(jv);
	}

	@Override
	public void visitForkActor(ForkActor fa) {
		SDFForkVertex fv = new SDFForkVertex();
		piVx2SDFVx.put(fa, fv);
		// Handle vertex's name
		fv.setName(fa.getName());
		// Handle vertex's path inside the graph hierarchy
		fv.setInfo(fa.getPath());

		// Handle input parameters as instance arguments
		for (ConfigInputPort p : fa.getConfigInputPorts()) {
			ISetter setter = p.getIncomingDependency().getSetter();
			if (setter instanceof Parameter) {
				Parameter param = (Parameter) setter;
				Argument arg = new Argument(p.getName());
				arg.setValue(param.getName());
				fv.getArguments().addArgument(arg);
			}
		}

		visitAbstractActor(fa);

		result.addVertex(fv);
	}

	@Override
	public void visitRoundBufferActor(RoundBufferActor rba) {
		SDFRoundBufferVertex rbv = new SDFRoundBufferVertex();
		piVx2SDFVx.put(rba, rbv);
		// Handle vertex's name
		rbv.setName(rba.getName());
		// Handle vertex's path inside the graph hierarchy
		rbv.setInfo(rba.getPath());

		// Handle input parameters as instance arguments
		for (ConfigInputPort p : rba.getConfigInputPorts()) {
			ISetter setter = p.getIncomingDependency().getSetter();
			if (setter instanceof Parameter) {
				Parameter param = (Parameter) setter;
				Argument arg = new Argument(p.getName());
				arg.setValue(param.getName());
				rbv.getArguments().addArgument(arg);
			}
		}

		visitAbstractActor(rba);

		result.addVertex(rbv);
	}

	public SDFGraph getResult() {
		return result;
	}

	/**
	 * Methods below are unused and unimplemented visit methods
	 */

	@Override
	public void visitDataOutputPort(DataOutputPort dop) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitDelay(Delay d) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitDataInputPort(DataInputPort dip) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitExpression(Expression e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitConfigInputPort(ConfigInputPort cip) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitConfigOutputPort(ConfigOutputPort cop) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitDependency(Dependency d) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitISetter(ISetter is) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitPort(Port p) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitDataPort(DataPort p) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitExecutableActor(ExecutableActor ea) {
		throw new UnsupportedOperationException();
	}
}
