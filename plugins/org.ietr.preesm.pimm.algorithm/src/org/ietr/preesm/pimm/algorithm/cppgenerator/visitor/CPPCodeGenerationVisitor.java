/**
 * *****************************************************************************
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
 * ****************************************************************************
 */
package org.ietr.preesm.pimm.algorithm.cppgenerator.visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.types.DataType;
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
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.JoinActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;
import org.ietr.preesm.experiment.model.pimm.impl.FunctionParameterImpl;
import org.ietr.preesm.experiment.model.pimm.impl.FunctionPrototypeImpl;
import org.ietr.preesm.experiment.model.pimm.impl.HRefinementImpl;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;
import org.ietr.preesm.pimm.algorithm.cppgenerator.utils.CppNameGenerator;
import org.ietr.preesm.pimm.algorithm.cppgenerator.utils.CppTypeConverter;
import org.ietr.preesm.pimm.algorithm.cppgenerator.utils.CppTypeConverter.PiSDFSubType;

// TODO: Find a cleaner way to setParentEdge in Interfaces
/* 
 * Ugly workaround for setParentEdge in Interfaces. Must suppose that fifos are always obtained in the same order => Modify the C++ headers?
 * A better way would be a possibility to get edges from one building method to the other (since the parentEdge is in the outer graph), 
 * maybe a map from edgeNames to edges with a method getOutputEdgeByName in BaseVertex 
 */

/**
 * PiMM models visitor generating C++ code for COMPA Runtime currentGraph: The
 * most outer graph of the PiMM model currentMethod: The StringBuilder used to
 * write the C++ code
 */
public class CPPCodeGenerationVisitor extends PiMMVisitor {
	private CppPreProcessVisitor preprocessor;

	// Ordered set for methods prototypes
	private LinkedHashSet<String> prototypes = new LinkedHashSet<String>();

	// Maps to handle hierarchical graphs
	private Map<PiGraph, StringBuilder> graph2method = new LinkedHashMap<PiGraph, StringBuilder>();
	private Map<PiGraph, List<PiGraph>> graph2subgraphs = new HashMap<PiGraph, List<PiGraph>>();

	private Map<String, DataType> dataTypes;

	private StringBuilder currentMethod;
	private StringBuilder currentDependentParams;
	
	private PiGraph currentGraph;
	private List<PiGraph> currentSubGraphs;

	// Variables containing the type of the currently visited AbstractActor for
	// AbstractActor generation
	// private String currentAbstractActorType;
	// private String currentAbstractActorClass;

	// Map linking data ports to their corresponding description
	private Map<Port, Integer> portMap;

	private Map<AbstractActor, Integer> functionMap;

	private Map<AbstractActor, Map<String, String>> timings;

	private Map<AbstractActor, Set<String>> constraints;

	public LinkedHashSet<String> getPrototypes() {
		return prototypes;
	}

	public Collection<StringBuilder> getMethods() {
		return graph2method.values();
	}

	// Shortcut for currentMethod.append()
	private void append(Object a) {
		currentMethod.append(a);
	}

	public CPPCodeGenerationVisitor(StringBuilder topMethod,
			CppPreProcessVisitor prepocessor,
			Map<AbstractActor, Map<String, String>> timings,
			Map<AbstractActor, Set<String>> constraints,
			Map<String, DataType> dataTypes) {
		this.currentMethod = topMethod;
		this.preprocessor = prepocessor;
		this.portMap = preprocessor.getPortMap();
		this.functionMap = preprocessor.getFunctionMap();
		this.timings = timings;
		this.constraints = constraints;
		this.dataTypes = dataTypes;
	}

	/**
	 * When visiting a PiGraph (either the most outer graph or an hierarchical
	 * actor), we should generate a new C++ method
	 */
	@Override
	public void visitPiGraph(PiGraph pg) {
		// We should first generate the C++ code as for any Actor in the outer
		// graph

		visitAbstractActor(pg);

		// We add pg as a subgraph of the current graph
		if (currentSubGraphs == null) {
			currentSubGraphs = new ArrayList<PiGraph>();
		}
		currentSubGraphs.add(pg);

		// We stock the informations about the current graph for later use
		PiGraph currentOuterGraph = currentGraph;
		if (currentOuterGraph != null) {
			graph2method.put(currentOuterGraph, currentMethod);
			graph2subgraphs.put(currentOuterGraph, currentSubGraphs);
		}
		// We initialize variables which will stock informations about pg during
		// its method generation
		// The new current graph is pg
		currentGraph = pg;
		// We start a new StringBuilder to generate its method
		currentMethod = new StringBuilder();
		// Currently we know no subgraphs to pg
		currentSubGraphs = new ArrayList<PiGraph>();

		// And then visit pg as a PiGraph, generating the method to build its
		// C++ corresponding PiSDFGraph

		append("\n// Method building PiSDFGraph");
		append(pg.getName() + "\n");

		// Generating the method prototype
		generateMethodPrototype(pg);
		// Generating the method body
		generateMethodBody(pg);

		// If pg has no subgraphs, its method has not been added in graph2method
		// map
		if (!graph2method.containsKey(currentGraph)) {
			graph2method.put(currentGraph, currentMethod);
		}

		// We get back the informations about the outer graph to continue
		// visiting it
		if (currentOuterGraph != null) {
			currentMethod = graph2method.get(currentOuterGraph);
			currentSubGraphs = graph2subgraphs.get(currentOuterGraph);
		}
		currentGraph = currentOuterGraph;
	}

	/**
	 * Concatenate the signature of the method corresponding to a PiGraph to the
	 * currentMethod StringBuilder
	 */
	private void generateMethodPrototype(PiGraph pg) {
		StringBuilder signature = new StringBuilder();
		// The method does not return anything
		signature.append("PiSDFGraph* ");
		signature.append(CppNameGenerator.getMethodName(pg));
		// The method accept as parameter a pointer to the PiSDFGraph graph it
		// will build and a pointer to the parent actor of graph (i.e., the
		// hierarchical actor)
		signature.append("(Archi* archi, Stack* stack)");
		prototypes.add(signature.toString() + ";\n");
		append(signature);
	}

	/**
	 * Concatenate the body of the method corresponding to a PiGraph to the
	 * currentMethod StringBuilder
	 */
	private void generateMethodBody(PiGraph pg) {
		append("{\n");

		int nInIf = 0;
		int nOutif = 0;
		int nConfig = 0;
		int nBody = 0;

		for (AbstractActor v : pg.getVertices()) {
			switch (CppTypeConverter.getType(v)) {
			case PISDF_TYPE_IF:
				if (CppTypeConverter.getSubType(v) == PiSDFSubType.PISDF_SUBTYPE_INPUT_IF)
					nInIf++;
				else
					nOutif++;
				break;
			case PISDF_TYPE_CONFIG:
				nConfig++;
				break;
			case PISDF_TYPE_BODY:
				nBody++;
				break;
			}
		}

		// Create a graph and a top vertex
		append("\tPiSDFGraph* graph = CREATE(stack, PiSDFGraph)(\n"
				+ "\t\t/*Edges*/    " + pg.getFifos().size() + ",\n"
				+ "\t\t/*Params*/   " + pg.getParameters().size() + ",\n"
				+ "\t\t/*InputIf*/  " + nInIf + ",\n" + "\t\t/*OutputIf*/ "
				+ nOutif + ",\n" + "\t\t/*Config*/   " + nConfig + ",\n"
				+ "\t\t/*Body*/     " + nBody + ",\n"
				+ "\t\t/*Archi*/    archi,\n" + "\t\t/*Stack*/    stack);\n");

		// Generating parameters
		append("\n\t/* Parameters */\n");
		currentDependentParams = new StringBuilder();
		for (Parameter p : pg.getParameters()) {
			p.accept(this);
		}
		currentMethod.append(currentDependentParams);
		
		// Generating vertices
		append("\n\t/* Vertices */\n");
		for (AbstractActor v : pg.getVertices()) {
			v.accept(this);
		}
		// Generating edges
		append("\n\t/* Edges */\n");
		for (Fifo f : pg.getFifos()) {
			f.accept(this);
		}

		append("\treturn graph;");
		append("\n}\n");
	}

	private String generateConfigVertex(AbstractActor aa) {
		String vertexName = CppNameGenerator.getVertexName(aa);

		String fctIx;
		if (functionMap.containsKey(aa))
			fctIx = CppNameGenerator.getFunctionName(aa).toUpperCase() + "_FCT";
		else
			fctIx = "-1";

		// Call the addVertex method on the current graph
		append("\tPiSDFVertex* " + vertexName);
		append(" = graph->addConfigVertex(\n");
		append("\t\t/*Name*/    \"" + aa.getName() + "\",\n");
		append("\t\t/*FctId*/   " + fctIx + ",\n");
		append("\t\t/*SubType*/ " + "PISDF_SUBTYPE_NORMAL" + ",\n");
		append("\t\t/*InData*/  " + aa.getDataInputPorts().size() + ",\n");
		append("\t\t/*OutData*/ " + aa.getDataOutputPorts().size() + ",\n");
		append("\t\t/*InParam*/ " + aa.getConfigInputPorts().size() + ",\n");
		append("\t\t/*OutParam*/" + aa.getConfigOutputPorts().size() + ");\n");

		return vertexName;
	}

	private String generateBodyVertex(AbstractActor aa) {
		String vertexName = CppNameGenerator.getVertexName(aa);

		String fctIx;
		if (functionMap.containsKey(aa))
			fctIx = CppNameGenerator.getFunctionName(aa).toUpperCase() + "_FCT";
		else
			fctIx = "-1";

		// Call the addVertex method on the current graph
		append("\tPiSDFVertex* " + vertexName);
		append(" = graph->addBodyVertex(\n");
		append("\t\t/*Name*/    \"" + aa.getName() + "\",\n");
		append("\t\t/*FctId*/   " + fctIx + ",\n");
		append("\t\t/*InData*/  " + aa.getDataInputPorts().size() + ",\n");
		append("\t\t/*OutData*/ " + aa.getDataOutputPorts().size() + ",\n");
		append("\t\t/*InParam*/ " + aa.getConfigInputPorts().size() + ");\n");

		return vertexName;
	}

	private String generateHierarchicalVertex(AbstractActor aa) {
		String vertexName = CppNameGenerator.getVertexName(aa);
		PiGraph subGraph = ((PiGraph) aa);

		// Call the addVertex method on the current graph
		append("\tPiSDFVertex* " + vertexName);
		append(" = graph->addHierVertex(\n");
		append("\t\t/*Name*/    \"" + aa.getName() + "\",\n");
		append("\t\t/*Graph*/   " + CppNameGenerator.getMethodName(subGraph)
				+ "(archi, stack),\n");
		append("\t\t/*InData*/  " + aa.getDataInputPorts().size() + ",\n");
		append("\t\t/*OutData*/ " + aa.getDataOutputPorts().size() + ",\n");
		append("\t\t/*InParam*/ " + aa.getConfigInputPorts().size() + ");\n");

		return vertexName;
	}

	/**
	 * Generic visit method for all AbstractActors (Actors, PiGraph)
	 */
	@Override
	public void visitAbstractActor(AbstractActor aa) {
		String vertexName;

		if (aa instanceof Actor && ((Actor) aa).isConfigurationActor())
			vertexName = generateConfigVertex(aa);
		else if (aa instanceof PiGraph)
			vertexName = generateHierarchicalVertex(aa);
		else
			vertexName = generateBodyVertex(aa);

		// Add connections to parameters if necessary
		for (ConfigOutputPort cop : aa.getConfigOutputPorts()) {
			for (Dependency d : cop.getOutgoingDependencies()) {
				append("\t" + vertexName + "->addOutParam(");
				append(portMap.get(cop) + ", ");
				append(CppNameGenerator.getParameterName((Parameter) d
						.getGetter().eContainer()));
				append(");\n");
			}
		}

		// Add connections from parameters if necessary
		for (ConfigInputPort cip : aa.getConfigInputPorts()) {
			append("\t" + vertexName + "->addInParam(");
			append(portMap.get(cip) + ", ");
			append(CppNameGenerator.getParameterName((Parameter) cip
					.getIncomingDependency().getSetter()));
			append(");\n");
		}

		if (aa instanceof Actor && !(aa instanceof PiGraph)) {
			if (constraints.get(aa) != null) {
				for (String core : constraints.get(aa)) {
					append("\t" + vertexName + "->isExecutableOnPE(");
					append(CppNameGenerator.getCoreName(core) + ");\n");
				}
			}
		}

		Map<String, String> aaTimings = timings.get(aa);
		if (aaTimings != null) {
			for (String coreType : aaTimings.keySet()) {
				append("\t" + vertexName + "->setTimingOnType(");
				append(CppNameGenerator.getCoreTypeName(coreType) + ", \"");
				append(aaTimings.get(coreType));
				append("\", stack);\n");
			}
		}

		append("\n");
	}

	@Override
	public void visitActor(Actor a) {
		visitAbstractActor(a);
	}

	@Override
	public void visitDataInputInterface(DataInputInterface dii) {
		String vertexName = CppNameGenerator.getVertexName(dii);

		append("\tPiSDFVertex* " + vertexName);
		append(" = graph->addInputIf(\n");
		append("\t\t/*Name*/    \"" + vertexName + "\",\n");
		append("\t\t/*InParam*/ " + dii.getConfigInputPorts().size() + ");\n");

		// Add connections from parameters if necessary
		for (ConfigInputPort cip : dii.getConfigInputPorts()) {
			append("\t" + vertexName + "->addInParam(");
			append(portMap.get(cip) + ", ");
			append(CppNameGenerator.getParameterName((Parameter) cip
					.getIncomingDependency().getSetter()));
			append(");\n");
		}
		append("\n");
	}

	@Override
	public void visitDataOutputInterface(DataOutputInterface doi) {
		String vertexName = CppNameGenerator.getVertexName(doi);

		append("\tPiSDFVertex* " + vertexName);
		append(" = graph->addOutputIf(\n");
		append("\t\t/*Name*/    \"" + vertexName + "\",\n");
		append("\t\t/*InParam*/ " + doi.getConfigInputPorts().size() + ");\n");

		// Add connections from parameters if necessary
		for (ConfigInputPort cip : doi.getConfigInputPorts()) {
			append("\t" + vertexName + "->addInParam(");
			append(portMap.get(cip) + ", ");
			append(CppNameGenerator.getParameterName((Parameter) cip
					.getIncomingDependency().getSetter()));
			append(");\n");
		}
		append("\n");
	}

	/**
	 * When visiting a FIFO we should add an edge to the current graph
	 */
	@Override
	public void visitFifo(Fifo f) {
		// Call the addEdge method on the current graph
		append("\tgraph->connect(\n");

		DataOutputPort srcPort = f.getSourcePort();
		DataInputPort snkPort = f.getTargetPort();

		int typeSize;
		if (dataTypes.containsKey(f.getType())) {
			typeSize = dataTypes.get(f.getType()).getSize();
		} else {
			WorkflowLogger
					.getLogger()
					.warning(
							"Type "
									+ f.getType()
									+ " is not defined in scenario (considered size = 1).");
			typeSize = 1;
		}

		AbstractVertex srcActor = (AbstractVertex) srcPort.eContainer();
		AbstractVertex snkActor = (AbstractVertex) snkPort.eContainer();
		
		String srcProd = srcPort.getExpression().getString();
		String snkProd = snkPort.getExpression().getString();

		
		/* Change port name in prod/cons/delay */
		for(ConfigInputPort cfgPort : srcActor.getConfigInputPorts()){
			String paramName = ((Parameter)cfgPort.getIncomingDependency().getSetter()).getName();
			srcProd = srcProd.replaceAll("\\b"+cfgPort.getName()+"\\b", paramName);
		}

		for(ConfigInputPort cfgPort : snkActor.getConfigInputPorts()){
			String paramName = ((Parameter)cfgPort.getIncomingDependency().getSetter()).getName();
			snkProd = snkProd.replaceAll("\\b"+cfgPort.getName()+"\\b", paramName);
		}

		String delay = "0";
		if (f.getDelay() != null){
			delay   = f.getDelay().getExpression().getString();
			
			for(ConfigInputPort cfgPort : f.getDelay().getConfigInputPorts()){
				String paramName = ((Parameter)cfgPort.getIncomingDependency().getSetter()).getName();
				delay   = delay.replaceAll("\\b"+cfgPort.getName()+"\\b", paramName);
			}
		}
		

		append("\t\t/*Src*/ "
				+ CppNameGenerator.getVertexName(srcActor) 
				+ ", /*SrcPrt*/ " + portMap.get(srcPort)
//				+ ", /*Prod*/ \"(" + srcProd + ")*sizeof(" + f.getType() + ")\",\n");
				+ ", /*Prod*/ \"(" + srcProd + ")*" + typeSize + "\",\n");

		append("\t\t/*Snk*/ "
				+ CppNameGenerator.getVertexName(snkActor) 
				+ ", /*SnkPrt*/ " + portMap.get(snkPort)
//				+ ", /*Cons*/ \"(" + snkProd + ")*sizeof(" + f.getType() + ")\",\n");
				+ ", /*Cons*/ \"(" + snkProd + ")*" + typeSize + "\",\n");

		if (f.getDelay() != null)
//			append("\t\t/*Delay*/ \"(" + delay + ")*sizeof(" + f.getType() + ")\",0);\n\n");
			append("\t\t/*Delay*/ \"(" + delay + ")*" + typeSize + "\",0);\n\n");
		else
			append("\t\t/*Delay*/ \"0\",0);\n\n");			
	}

	/**
	 * When visiting a parameter, we should add a parameter to the current graph
	 */
	@Override
	public void visitParameter(Parameter p) {
		String paramName = CppNameGenerator.getParameterName(p);


		if (!p.isLocallyStatic()) {
			if(p.getConfigInputPorts().size() == 1
					&& ! (p.getConfigInputPorts().get(0).getIncomingDependency().getSetter() instanceof Parameter)){
				/* DYNAMIC */
				append("\tPiSDFParam *"
						+ paramName
						+ " = graph->addDynamicParam(" + "\"" + p.getName() + "\""
						+ ");\n");				
			}else{
				/* DEPENDANT */
				currentDependentParams.append(
						"\tPiSDFParam *"
						+ paramName
						+ " = graph->addDependentParam(" + "\"" + p.getName() + "\", \""
						+ p.getExpression().getString()
						+ "\");\n");				
			}
		} else if (p.getGraphPort() instanceof ConfigInputPort) {
			/* HERITED */
			append("\tPiSDFParam *"
					+ paramName
					+ " = graph->addHeritedParam(" + "\"" + p.getName() + "\", "
					+ portMap.get(p.getGraphPort()) + ");\n");
		} else if (p.getConfigInputPorts().isEmpty()) {
			/* STATIC */
			append("\tPiSDFParam *"
					+ paramName
					+ " = graph->addStaticParam(" + "\"" + p.getName() + "\", "
					+ (int) Double.parseDouble(p.getExpression().evaluate())
					+ ");\n");
		} else {
			/* DEPENDANT */
			currentDependentParams.append(
					"\tPiSDFParam *"
					+ paramName
					+ " = graph->addDependentParam(" + "\"" + p.getName() + "\", \""
					+ p.getExpression().getString()
					+ "\");\n");
		}
	}

	@Override
	public void visitBroadcastActor(BroadcastActor ba) {
		append("\tPiSDFVertex* " + CppNameGenerator.getVertexName(ba));
		append(" = graph->addSpecialVertex(\n");
		append("\t\t/*Type*/    " + "PISDF_SUBTYPE_BROADCAST" + ",\n");
		append("\t\t/*InData*/  " + ba.getDataInputPorts().size() + ",\n");
		append("\t\t/*OutData*/ " + ba.getDataOutputPorts().size() + ",\n");
		append("\t\t/*InParam*/ " + ba.getConfigInputPorts().size() + ");\n");

		// Add connections from parameters if necessary
		for (ConfigInputPort cip : ba.getConfigInputPorts()) {
			append("\t" + CppNameGenerator.getVertexName(ba) + "->addInParam(");
			append(portMap.get(cip) + ", ");
			append(CppNameGenerator.getParameterName((Parameter) cip
					.getIncomingDependency().getSetter()));
			append(");\n");
		}
		append("\n");
	}

	@Override
	public void visitJoinActor(JoinActor ja) {
		append("\tPiSDFVertex* " + CppNameGenerator.getVertexName(ja));
		append(" = graph->addSpecialVertex(\n");
		append("\t\t/*Type*/    " + "PISDF_SUBTYPE_JOIN" + ",\n");
		append("\t\t/*InData*/  " + ja.getDataInputPorts().size() + ",\n");
		append("\t\t/*OutData*/ " + ja.getDataOutputPorts().size() + ",\n");
		append("\t\t/*InParam*/ " + ja.getConfigInputPorts().size() + ");\n");

		// Add connections from parameters if necessary
		for (ConfigInputPort cip : ja.getConfigInputPorts()) {
			append("\t" + CppNameGenerator.getVertexName(ja) + "->addInParam(");
			append(portMap.get(cip) + ", ");
			append(CppNameGenerator.getParameterName((Parameter) cip
					.getIncomingDependency().getSetter()));
			append(");\n");
		}
		append("\n");
	}

	@Override
	public void visitForkActor(ForkActor fa) {
		append("\tPiSDFVertex* " + CppNameGenerator.getVertexName(fa));
		append(" = graph->addSpecialVertex(\n");
		append("\t\t/*Type*/    " + "PISDF_SUBTYPE_FORK" + ",\n");
		append("\t\t/*InData*/  " + fa.getDataInputPorts().size() + ",\n");
		append("\t\t/*OutData*/ " + fa.getDataOutputPorts().size() + ",\n");
		append("\t\t/*InParam*/ " + fa.getConfigInputPorts().size() + ");\n");

		// Add connections from parameters if necessary
		for (ConfigInputPort cip : fa.getConfigInputPorts()) {
			append("\t" + CppNameGenerator.getVertexName(fa) + "->addInParam(");
			append(portMap.get(cip) + ", ");
			append(CppNameGenerator.getParameterName((Parameter) cip
					.getIncomingDependency().getSetter()));
			append(");\n");
		}
		append("\n");
	}

	@Override
	public void visitConfigOutputInterface(ConfigOutputInterface coi) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitDataInputPort(DataInputPort dip) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitAbstractVertex(AbstractVertex av) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitConfigInputInterface(ConfigInputInterface cii) {
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
	public void visitDataOutputPort(DataOutputPort dop) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitDelay(Delay d) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitDependency(Dependency d) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitExpression(Expression e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitInterfaceActor(InterfaceActor ia) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitISetter(ISetter is) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitParameterizable(Parameterizable p) {
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
	public void visitRefinement(Refinement r) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitFunctionParameter(
			FunctionParameterImpl functionParameterImpl) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitFunctionPrototype(
			FunctionPrototypeImpl functionPrototypeImpl) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitHRefinement(HRefinementImpl hRefinementImpl) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitRoundBufferActor(RoundBufferActor rba) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitExecutableActor(ExecutableActor ea) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Class allowing to stock necessary information about graphs when moving
	 * through the graph hierarchy
	 */
	// private class GraphDescription {
	// List<PiGraph> subGraphs;
	// StringBuilder method;
	//
	// public GraphDescription(List<PiGraph> subGraphs, StringBuilder method) {
	// this.subGraphs = subGraphs;
	// this.method = method;
	// }
	//
	// }
}
