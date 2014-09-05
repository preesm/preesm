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
package org.ietr.preesm.experiment.pimm.cppgenerator.visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

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
import org.ietr.preesm.experiment.pimm.cppgenerator.utils.CPPNameGenerator;
import org.ietr.preesm.experiment.pimm.cppgenerator.visitor.CPPCodeGenerationPreProcessVisitor.DataPortDescription;
import org.ietr.preesm.experiment.pimm.cppgenerator.visitor.CPPCodeGenerationPreProcessVisitor.DependencyDescription;

//TODO: Find a cleaner way to setParentEdge in Interfaces
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

	CPPNameGenerator nameGen = new CPPNameGenerator();

	private CPPCodeGenerationPreProcessVisitor preprocessor;

	// Ordered set for methods prototypes
	private LinkedHashSet<String> prototypes = new LinkedHashSet<String>();

	public LinkedHashSet<String> getPrototypes() {
		return prototypes;
	}

	// Maps to handle hierarchical graphs
	private Map<PiGraph, StringBuilder> graph2method = new LinkedHashMap<PiGraph, StringBuilder>();
	private Map<PiGraph, List<PiGraph>> graph2subgraphs = new HashMap<PiGraph, List<PiGraph>>();

	public Collection<StringBuilder> getMethods() {
		return graph2method.values();
	}

	private StringBuilder currentMethod;
	private PiGraph currentGraph;
	private List<PiGraph> currentSubGraphs;

	// Variables containing the type of the currently visited AbstractActor for
	// AbstractActor generation
	private String currentAbstractActorType;
	private String currentAbstractActorClass;

	// Map linking data ports to their corresponding description
	private Map<Port, DataPortDescription> dataPortMap;
	// Map linking data ports to their corresponding description
	private Map<Dependency, DependencyDescription> dependencyMap;
	// Map linking Fifos to their C++ position in their graph collection
	private Map<Fifo, Integer> fifoMap;
	// Map from Actor names to pairs of CoreType numbers and Timing expressions
	private Map<String, Map<Integer, String>> timings;

	// Index of functions to call in C code
	// TODO: Complete with generation of a table associating indices with
	// pointers of functions
	private int functionIndex = 0;

	// Shortcut for currentMethod.append()
	private void append(Object a) {
		currentMethod.append(a);
	}

	public CPPCodeGenerationVisitor(StringBuilder topMethod,
			CPPCodeGenerationPreProcessVisitor prepocessor,
			Map<String, Map<Integer, String>> timings) {
		this.currentMethod = topMethod;
		this.preprocessor = prepocessor;
		this.dataPortMap = preprocessor.getDataPortMap();
		this.dependencyMap = preprocessor.getDependencyMap();
		this.fifoMap = preprocessor.getFifoMap();
		this.timings = timings;
	}

	/**
	 * When visiting a PiGraph (either the most outer graph or an hierarchical
	 * actor), we should generate a new C++ method
	 */
	@Override
	public void visitPiGraph(PiGraph pg) {
		// We should first generate the C++ code as for any Actor in the outer
		// graph
		currentAbstractActorType = "pisdf_vertex";
		currentAbstractActorClass = "PiSDFVertex";
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
		String pgName = pg.getName();

		append("\n// Method building PiSDFGraph ");
		append(pgName);
		// Generating the method signature
		generateMethodSignature(pg);
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
	private void generateMethodSignature(PiGraph pg) {
		StringBuilder signature = new StringBuilder();
		// The method does not return anything
		signature.append("\nvoid ");
		signature.append(nameGen.getMethodName(pg));
		// The method accept as parameter a pointer to the PiSDFGraph graph it
		// will build and a pointer to the parent actor of graph (i.e., the
		// hierarchical actor)
		signature.append("(PiSDFGraph* _graphs)");
		prototypes.add(signature.toString());
		append(signature);
	}

	/**
	 * Concatenate the body of the method corresponding to a PiGraph to the
	 * currentMethod StringBuilder
	 */
	private void generateMethodBody(PiGraph pg) {
		append("{");

		// Generating parameters
		append("\n\t//Parameters");
		for (Parameter p : pg.getParameters()) {
			p.accept(this);
			append("\n");
		}
		// Generating vertices
		append("\n\t//Vertices");
		for (AbstractActor v : pg.getVertices()) {
			v.accept(this);
			append("\n");
		}
		// Generating edges
		append("\n\t//Edges");
		for (Fifo f : pg.getFifos()) {
			f.accept(this);
			append("\n");
		}
		// Generating call to methods generated for subgraphs, if any
		if (!currentSubGraphs.isEmpty()) {
			append("\n\t//Subgraphs");
			generateCallsToSubgraphs();
		}
		append("\n}\n");
	}

	private void generateCallsToSubgraphs() {
		// For each subgraph of the current graph
		for (PiGraph sg : currentSubGraphs) {
			String sgName = nameGen.getSubraphName(sg);
			String vxName = nameGen.getVertexName(sg);
			// Generate test in order to prevent to reach the limit of graphs
			append("\n\tif(nb_graphs >= MAX_NB_PiSDF_SUB_GRAPHS - 1) exitWithCode(1054);");
			// Get the pointer to the subgraph
			append("\n\tPiSDFGraph *");
			append(sgName);
			append(" = addGraph();");
			// Call the building method of sg with the pointer to the subgraph
			append("\n\t");
			append(nameGen.getMethodName(sg));
			append("(");
			append(sgName);			
			append(");");
			append("\n\t");
			// Set the subgraph as subgraph of the vertex
			append(vxName);
			append("->setSubGraph(");
			append(sgName);
			append(");");
			append("\n");
		}
	}

	/**
	 * Generic visit method for all AbstractActors (Actors, PiGraph)
	 */
	@Override
	public void visitAbstractActor(AbstractActor aa) {
		if (aa.getConfigOutputPorts().size() > 0) {
			currentAbstractActorClass = "PiSDFConfigVertex";
			currentAbstractActorType = "config_vertex";
		}

		String vertexName = nameGen.getVertexName(aa);

		// Call the addVertex method on the current graph
		append("\n\t");
		append(currentAbstractActorClass);
		append(" *");
		append(vertexName);
		append(" = (");
		append(currentAbstractActorClass);
		append("*)graph->addVertex(\"");
		// Pass the name of the AbstractActor
		append(aa.getName());
		append("\",");
		// Pass the type of vertex
		append(currentAbstractActorType);
		append(");");
		// Add connections to parameters if necessary
		for (ConfigOutputPort cop : aa.getConfigOutputPorts()) {
			for (Dependency d : cop.getOutgoingDependencies()) {
				append("\n\t");
				append(vertexName);
				append("->addRelatedParam(");
				append(dependencyMap.get(d).tgtName);
				append(");");
			}
		}

		// Add connections from parameters if necessary
		for (ConfigInputPort cip : aa.getConfigInputPorts()) {
			append("\n\t");
			append(vertexName);
			append("->addParameter(");
			append(dependencyMap.get(cip.getIncomingDependency()).srcName);
			append(");");
		}

		// Set Timings, if any
		Map<Integer, String> aaTimings = timings.get(aa.getName());
		if (aaTimings != null) {
			for (Integer coreTypeCode : aaTimings.keySet()) {
				append("\n\t");
				append(vertexName);
				append("->setTiming(");
				append(coreTypeCode);
				append(", \"");
				append(timings.get(aa.getName()).get(coreTypeCode));
				append("\");");
			}
		}

		// Set function index
		append("\n\t");
		append(vertexName);
		append("->setFunction_index(");
		append(functionIndex);
		functionIndex++;
		append(");");
	}

	@Override
	public void visitActor(Actor a) {
		currentAbstractActorType = "pisdf_vertex";
		currentAbstractActorClass = "PiSDFVertex";
		visitAbstractActor(a);
	}

	@Override
	public void visitDataInputInterface(DataInputInterface dii) {
		String vertexName = nameGen.getVertexName(dii);

		// Adding the vertex to the current graph
		currentAbstractActorType = "input_vertex";
		currentAbstractActorClass = "PiSDFIfVertex";
		visitAbstractActor(dii);
		// Setting direction to 0 (input)
		append("\n\t");
		append(vertexName);
		append("->setDirection(0);");
		// Setting the parent vertex
		append("\n\t");
		append(vertexName);
		append("->setParentVertex(parentVertex);");
		// Setting the parent edge
		append("\n\t");
		append(vertexName);
		append("->setParentEdge(");
		// Getting the Fifo corresponding to the parent edge
		if (dii.getGraphPort() instanceof DataInputPort) {
			DataInputPort dip = (DataInputPort) dii.getGraphPort();
			Fifo incomingFifo = dip.getIncomingFifo();
			append("parentVertex->getInputEdge(");
			// XXX: setParentEdge workaround
			/*
			 * Ugly way to do this. Must suppose that fifos are always obtained
			 * in the same order => Modify the C++ headers? A better way would
			 * be a possibility to get edges from one building method to the
			 * other (since the parentEdge is in the outer graph), maybe a map
			 * from edgeNames to edges with a method getOutputEdgeByName in
			 * BaseVertex
			 */
			append(fifoMap.get(incomingFifo));
			append(")");
		} else {
			throw new RuntimeException("Graph port of DataInputInterface "
					+ dii.getName() + " is not a DataInputPort.");
		}
		append(");");

		// Set function index
		append("\n\t");
		append(vertexName);
		append("->setFunction_index(IF_FUNCT_IX);");
	}

	@Override
	public void visitDataOutputInterface(DataOutputInterface doi) {
		String vertexName = nameGen.getVertexName(doi);

		// Adding the vertex to the current graph
		currentAbstractActorType = "output_vertex";
		currentAbstractActorClass = "PiSDFIfVertex";
		visitAbstractActor(doi);
		// Setting direction to 1 (output)
		append("\n\t");
		append(vertexName);
		append("->setDirection(1);");
		// Setting the parent vertex
		append("\n\t");
		append(vertexName);
		append("->setParentVertex(parentVertex);");
		// Setting the parent edge
		append("\n\t");
		append(vertexName);
		append("->setParentEdge(");
		// Getting the Fifo corresponding to the parent edge
		if (doi.getGraphPort() instanceof DataOutputPort) {
			DataOutputPort dop = (DataOutputPort) doi.getGraphPort();
			Fifo incomingFifo = dop.getOutgoingFifo();
			append("parentVertex->getOutputEdge(");
			// XXX: setParentEdge workaround
			/*
			 * Ugly way to do this. Must suppose that fifos are always obtained
			 * in the same order => Modify the C++ headers? A better way would
			 * be a possibility to get edges from one building method to the
			 * other (since the parentEdge is in the outer graph), maybe a map
			 * from edgeNames to edges with a method getOutputEdgeByName in
			 * BaseVertex
			 */
			append(fifoMap.get(incomingFifo));
			append(")");
		} else {
			throw new RuntimeException("Graph port of DataOutputInterface "
					+ doi.getName() + " is not a DataOutputPort.");
		}
		append(");");

		// Set function index
		append("\n\t");
		append(vertexName);
		append("->setFunction_index(IF_FUNCT_IX);");
	}

	/**
	 * When visiting a FIFO we should add an edge to the current graph
	 */
	@Override
	public void visitFifo(Fifo f) {
		// Call the addEdge method on the current graph
		append("\n\t");
		append("graph->addEdge(");
		// Use the PortDescription of the source port to get the informations
		// about the source node
		DataPortDescription src = dataPortMap.get(f.getSourcePort());
		// Pass the name of the source node
		append(src.actor.getName());
		append(", ");
		// Pass the index of the source port
		append(src.index);
		append(", \"");
		// Pass the production of the source node
		append(src.expression);
		append("\", ");
		// Use the PortDescription of the target port to get the informations
		// about the target node
		DataPortDescription tgt = dataPortMap.get(f.getTargetPort());
		// Pass the name of the target node
		append(tgt.actor.getName());
		append(", ");
		// Pass the index of the target port
		append(tgt.index);
		append(", \"");
		// Pass the consumption of the target node
		append(tgt.expression);
		append("\", \"");
		// Pass the delay of the FIFO
		if (f.getDelay() != null)
			append(f.getDelay().getExpression().getString());
		else
			append("0");
		append("\"");
		append(");");
	}

	/**
	 * When visiting a parameter, we should add a parameter to the current graph
	 */
	@Override
	public void visitParameter(Parameter p) {
		String paramName = nameGen.getParameterName(p);

		append("\n\tPiSDFParameter *");
		append(paramName);
		append(" = graph->addParameter(\"");
		append(p.getName());
		append("\");");
		if (p.isLocallyStatic()) {
			append("\n\t");
			append(paramName);
			append("->setValue(");
			append(p.getExpression().evaluate());
			append(")");
		}
	}

	@Override
	public void visitConfigOutputInterface(ConfigOutputInterface coi) {
		// TODO: Handle ConfigOutputInterface wrt. the COMPA runtime needs (cf.
		// Yaset & Julien)
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
	public void visitBroadcastActor(BroadcastActor ba) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitJoinActor(JoinActor ja) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitForkActor(ForkActor fa) {
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
