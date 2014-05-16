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

import java.util.HashMap;
import java.util.Map;

import org.ietr.preesm.experiment.core.piscenario.ActorNode;
import org.ietr.preesm.experiment.core.piscenario.PiScenario;
import org.ietr.preesm.experiment.core.piscenario.Timing;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.pimm.cppgenerator.utils.CPPNameGenerator;

public class CPPCodeGenerationLauncher {

	CPPNameGenerator nameGen = new CPPNameGenerator();

	StringBuilder topMethod = new StringBuilder();
	
	private PiScenario scenario;

	private Map<String, Integer> coreTypesCodes;
	
	private Map<String, Map<Integer, String>> timings;
	
	public CPPCodeGenerationLauncher(PiScenario scenario) {
		this.scenario = scenario;
	}

	// Shortcut to stringBuilder.append
	private void append(Object a) {
		topMethod.append(a);
	}

	/**
	 * Main method, launching the generation for the whole PiGraph pg, including
	 * license, includes, constants and top method generation
	 */
	public String generateCPPCode(PiGraph pg) {
		
		generateCoreTypesCodes();
		timings = extractTimings();
		
		CPPCodeGenerationPreProcessVisitor preprocessor = new CPPCodeGenerationPreProcessVisitor();
		StringBuilder tmp = new StringBuilder();
		CPPCodeGenerationVisitor codeGenerator = new CPPCodeGenerationVisitor(
				tmp, preprocessor, timings);
		// Preprocess PiGraph pg in order to collect information on sources and
		// targets of Fifos and Dependecies
		preprocessor.visit(pg);
		// Generate C++ code for the whole PiGraph, at the end, tmp will contain
		// the vertex declaration for pg
		codeGenerator.visit(pg);

		// /Generate the header (license, includes and constants)
		generateHeader();
		append("\n");
		// Generate the prototypes for each method except top
		for (String p : codeGenerator.getPrototypes()) {
			append(p);
			append(";\n");
		}

		// Generate the top method from which the C++ graph building is launch
		openTopMehod(pg);
		//append(tmp);
		closeTopMethod(pg);
		// Concatenate the results
		for (StringBuilder m : codeGenerator.getMethods()) {
			append(m);
		}
		// Returns the final C++ code
		return topMethod.toString();
	}

	private void generateCoreTypesCodes() {
		if (coreTypesCodes == null) {
			coreTypesCodes = new HashMap<String, Integer>();
			int i = 0;
			for (String coreType : scenario.getOperatorTypes()) {
				coreTypesCodes.put(coreType, i);
				i++;
			}
		}
	}

	private Map<String, Map<Integer, String>> extractTimings() {
		Map<String, Map<Integer, String>> timings = new HashMap<String, Map<Integer, String>>();
		
		for (ActorNode node : scenario.getActorTree().getAllActorNodes()) {
			Map<Integer, String> nodeTimings = new HashMap<Integer, String>();			
			for (String operator : node.getTimings().keySet()) {
				Timing timing = node.getTimings().get(operator);
				nodeTimings.put(coreTypesCodes.get(operator), timing.getStringValue());
			}
			timings.put(node.getName(), nodeTimings);
		}
		
		return timings;
	}

	/**
	 * Generate the header of the final C++ file (license, includes and
	 * constants)
	 */
	private void generateHeader() {
		append(getLicense());
		append("\n");
		generateIncludes();
		append("\n");
		generateConstants();
	}

	/**
	 * Generate the needed includes for the final C++ file
	 */
	private void generateIncludes() {
		append("\n#include <string.h>");
		append("\n#include <graphs/PiSDF/PiSDFGraph.h>");
		append("\n#include <addGraph.h>");
	}

	/**
	 * Generate the needed constants for the final C++ file
	 */
	private void generateConstants() {
		append("\nstatic PiSDFGraph* graphs;");
		append("\nstatic int nb_graphs = 0;");
	}

	/**
	 * Generate the top method, responsible for building the whole C++ PiGraph
	 * corresponding to pg
	 */
	private void openTopMehod(PiGraph pg) {
		append("\n/**");
		append("\n * This is the method you need to call to build a complete PiSDF graph.");
		append("\n */");
		// The method does not return anything and is named top
		append("\nPiSDFGraph* top");
		// The method accept as parameter a pointer to the PiSDFGraph graph it
		// will build
		append("(PiSDFGraph* _graphs){");
	}

	private void closeTopMethod(PiGraph pg) {
		String sgName = nameGen.getSubraphName(pg);

		String topGraphName = "top";
		String topVertexName = "vxTop";
		
		// Create a top graph and a top vertex
		append("\n\t");
		append("graphs = _graphs");
		append("\n\t");
		append("PiSDFGraph* ");
		append(topGraphName);
		append(" = addGraph()");
		append("\n\t");
		append("PiSDFVertex* ");
		append(topVertexName);
		append(" = (PiSDFVertex *)");
		append(topGraphName);
		append("->addVertex(\"");
		append(topVertexName);
		append("\", pisdf_vertex);");
		
		// Get the pointer to the subgraph
		append("\n\t");
		append("PiSDFGraph *");
		append(sgName);
		append(" = addGraph();");
		
		// Add the pointer to top vertex as subgraph
		append("\n\t");
		append(topVertexName);
		append("->setSubgraph(");
		append(sgName);
		append(";");
		append("\n\t");
		append(sgName);
		append("->setParentVertex(");
		append(topVertexName);
		append(";");
		
		// Call the building method of sg with the pointer
		append("\n\t");
		append(nameGen.getMethodName(pg));
		append("(");
		// Pass the pointer to the subgraph
		append(sgName);
		append(");");

		// Return the top graph
		append("\n\t");
		append("return ");
		append(topGraphName);
		append(";");
		append("\n}\n");
	}

	public String addGraph() {
		StringBuilder result = new StringBuilder();
		// Put license
		result.append(getLicense());
		// Declare global variables
		result.append("\n\nstatic PiSDFGraph* graphs;");
		result.append("\nstatic int nbGraphs = 0;");
		// Declare the addGraph method
		result.append("\n\nPiSDFGraph* addGraph() {");
		result.append("\n\tif(nbGraphs >= MAX_NB_PiSDF_GRAPHS) exitWithCode(1054);");
		result.append("\n\tPiSDFGraph* graph = &(graphs[nbGraphs++]);");
		result.append("\n\tgraph->reset();");
		result.append("\n\treturn graph;");
		result.append("\n}");
		return result.toString();
	}
	
	/**
	 * License for PREESM
	 */
	public String getLicense() {
		return "/**\n"
			+ " * *****************************************************************************\n"
			+ " * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,\n"
			+ " * Karol Desnos, Julien Heulot, Clément Guy, Yaset Oliva Venegas\n"
			+ " *\n"
			+ " * [mpelcat,jnezan,kdesnos,jheulot,cguy,yoliva]@insa-rennes.fr\n"
			+ " *\n"
			+ " * This software is a computer program whose purpose is to prototype\n"
			+ " * parallel applications.\n"
			+ " *\n"
			+ " * This software is governed by the CeCILL-C license under French law and\n"
			+ " * abiding by the rules of distribution of free software.  You can  use,\n"
			+ " * modify and/ or redistribute the software under the terms of the CeCILL-C\n"
			+ " * license as circulated by CEA, CNRS and INRIA at the following URL\n"
			+ " * \"http://www.cecill.info\".\n"
			+ " *\n"
			+ " * As a counterpart to the access to the source code and  rights to copy,\n"
			+ " * modify and redistribute granted by the license, users are provided only\n"
			+ " * with a limited warranty  and the software's author,  the holder of the\n"
			+ " * economic rights,  and the successive licensors  have only  limited\n"
			+ " * liability.\n"
			+ " *\n"
			+ " * In this respect, the user's attention is drawn to the risks associated\n"
			+ " * with loading,  using,  modifying and/or developing or reproducing the\n"
			+ " * software by the user in light of its specific status of free software,\n"
			+ " * that may mean  that it is complicated to manipulate,  and  that  also\n"
			+ " * therefore means  that it is reserved for developers  and  experienced\n"
			+ " * professionals having in-depth computer knowledge. Users are therefore\n"
			+ " * encouraged to load and test the software's suitability as regards their\n"
			+ " * requirements in conditions enabling the security of their systems and/or\n"
			+ " * data to be ensured and,  more generally, to use and operate it in the\n"
			+ " * same conditions as regards security.\n"
			+ " *\n"
			+ " * The fact that you are presently reading this means that you have had\n"
			+ " * knowledge of the CeCILL-C license and that you accept its terms.\n"
			+ " * ****************************************************************************\n"
			+ " */";
	}

	public Map<String, Integer> getCoreTypesCodes() {
		return coreTypesCodes;
	}

	public Map<String, Map<Integer, String>> getTimings() {
		return timings;
	}
}
