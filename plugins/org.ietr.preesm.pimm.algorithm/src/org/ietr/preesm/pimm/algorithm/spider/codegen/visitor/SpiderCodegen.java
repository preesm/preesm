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
package org.ietr.preesm.pimm.algorithm.spider.codegen.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.scenario.ConstraintGroup;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.HRefinement;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.pimm.algorithm.spider.codegen.utils.SpiderNameGenerator;

public class SpiderCodegen{	
	private PreesmScenario scenario;
	StringBuilder cppString = new StringBuilder();

	// Shortcut for cppString.append()
	private void append(Object a) {
		cppString.append(a);
	}
		
	/* Map core types to core type indexes */
	private Map<String, Integer> coreTypesIds;
	private Map<String, Integer> coreIds;
	
	private SpiderPreProcessVisitor preprocessor;
	
	/* Map timing strings to actors */
	private Map<AbstractActor, Map<String, String>> timings;
	
	/* Map functions to function ix */
	private Map<AbstractActor, Integer> functionMap;
	
	/* Map Port to its description */
	private Map<Port, Integer> portMap;
	
	private HashMap<AbstractActor, Set<String>> constraints;
		
	public SpiderCodegen(PreesmScenario scenario) {
		this.scenario = scenario;
	}
	
	public void initGenerator(PiGraph pg){
		/* Preprocessor visitor */	
		/* Initialize functions, dataports and dependency maps */
		preprocessor = new SpiderPreProcessVisitor();
		preprocessor.visit(pg);
		
		portMap = preprocessor.getPortMap();
		functionMap = preprocessor.getFunctionMap();

		coreTypesIds = new HashMap<String, Integer>();
		int coreTypeId = 0;
		for (String coreType : scenario.getOperatorDefinitionIds())
			coreTypesIds.put(coreType, coreTypeId++);	
		
		coreIds = new HashMap<String, Integer>();
		String mainOperator = scenario.getSimulationManager().getMainOperatorName();
		if(mainOperator == null || mainOperator.equals("")){
			/* Warning */
			mainOperator = scenario.getOrderedOperatorIds().get(0);
			WorkflowLogger.getLogger().warning("No Main Operator selected in scenario, " + mainOperator + " used by default");
		}
		coreIds.put(mainOperator, 0);
		int coreId = 1;
		for (String core : scenario.getOrderedOperatorIds())
			if(!core.equals(mainOperator))
				coreIds.put(core, coreId++);	
		
		// Generate timings
		Map<String, AbstractActor> actorsByNames = preprocessor.getActorNames();
		timings = new HashMap<AbstractActor, Map<String, String>>();
		for (Timing t : scenario.getTimingManager().getTimings()) {
			String actorName = t.getVertexId();
			AbstractActor aa = actorsByNames.get(actorName);
			if(aa != null){ 
				if (!timings.containsKey(aa)) {
					timings.put(aa, new HashMap<String, String>());
				}
				timings.get(aa).put(t.getOperatorDefinitionId(), t.getStringValue());
			}
		}	
		
		// Generate constraints
		constraints = new HashMap<AbstractActor, Set<String>>();
		for (ConstraintGroup cg : scenario.getConstraintGroupManager().getConstraintGroups()) {
			for(String actorPath : cg.getVertexPaths()){
				AbstractActor aa = pg.getHierarchicalActorFromPath(actorPath);
				if(constraints.get(aa) == null)
					constraints.put(aa, new HashSet<String>());
				for(String core : cg.getOperatorIds()){
					constraints.get(aa).add(core);
				}
			}
		}	
		
		// Add Default timings if needed
		for (AbstractActor aa : actorsByNames.values()) {
			if (!timings.containsKey(aa)) {
				timings.put(aa, new HashMap<String, String>());
			}
			for(String coreType : coreTypesIds.keySet()){
				if(!timings.get(aa).containsKey(coreType)){
					timings.get(aa).put(coreType, "100");	
				}
			}
		}	
	}
	
	public String generateHeaderCode(PiGraph pg) {
		cppString.setLength(0);
		
		/* Put license */
		append(getLicense());
		
		/* Add Include Protection */
		append("#ifndef " + pg.getName().toUpperCase() + "_H\n");
		append("#define " + pg.getName().toUpperCase() + "_H\n\n");
		
		/* Declare Include Files */
		append("#include <spider/spider.h>\n\n");
		
		/* Declare the addGraph method */
		append("#define N_FCT_" + pg.getName().toUpperCase() + " " + functionMap.size() + "\n");
		append("extern lrtFct " + pg.getName().toLowerCase() + "_fcts[N_FCT_" + pg.getName().toUpperCase() + "];\n");
		append("\n");

		/* Declare Fcts */
		append("void init_"+pg.getName()+"(");
		List<Parameter> l = new LinkedList<Parameter>();
		l.addAll(pg.getAllParameters());
		Collections.sort(l, new Comparator<Parameter>() {
			@Override
	        public int compare(Parameter p1, Parameter p2){
	            return  p1.getName().compareTo(p2.getName());
	        }
		});
		StringBuilder parameters_proto = new StringBuilder();
		for(Parameter p : l){
			if(p.isLocallyStatic() && !p.isDependent() && !p.isConfigurationInterface()){
				if(parameters_proto.length() > 0){
					parameters_proto.append(", ");
				}
				parameters_proto.append("Param " + p.getName() + " = " + ((int) Double.parseDouble(p.getExpression().evaluate())));
			}
		}
		append(parameters_proto);
		append(");\n");	
		
		append("void free_"+pg.getName()+"();\n");
		append("\n");
		
		/* Core */
		append("typedef enum{\n");
		List<String> sortedCores = new ArrayList<String>(coreIds.keySet());
		Collections.sort(sortedCores);
		for(int i=0; i<coreIds.size(); i++){
			for(Entry<String, Integer> entry : coreIds.entrySet()){
				if(entry.getValue() == i){
					String core = entry.getKey();
					append("\t" + SpiderNameGenerator.getCoreName(core) 
						+ " = " + coreIds.get(core) + ",\n");
				}					
			}			
		}
		append("} PE;\n\n");
		
		/* Core Type */
		append("typedef enum{\n");
		for(String coreType : coreTypesIds.keySet()){
			append("\t" + SpiderNameGenerator.getCoreTypeName(coreType) 
					+ " = " + coreTypesIds.get(coreType) + ",\n");			
		}
		append("} PEType;\n\n");	
		
		/* Fct Ix */
		append("typedef enum{\n");
		for(AbstractActor aa : functionMap.keySet()){
			append("\t" + SpiderNameGenerator.getFunctionName(aa).toUpperCase() + "_FCT"
					+ " = " + functionMap.get(aa) + ",\n");			
		}
		append("} FctIxs;\n\n");

		/* Close Include Protection */
		append("#endif//" + pg.getName().toUpperCase() + "_H\n");

		return cppString.toString();
	}
	
	/**
	 * Main method, launching the generation for the whole PiGraph pg, including
	 * license, includes, constants and top method generation
	 */
	public String generateGraphCode(PiGraph pg) {	
		cppString.setLength(0);
		
		StringBuilder tmp = new StringBuilder();
		SpiderCodegenVisitor codeGenerator = new SpiderCodegenVisitor(
				tmp, preprocessor, timings, constraints, scenario.getSimulationManager().getDataTypes());
		// Generate C++ code for the whole PiGraph, at the end, tmp will contain
		// the vertex declaration for pg
		codeGenerator.visit(pg);

		// /Generate the header (license, includes and constants)
		append(getLicense());
		append("#include \"" + pg.getName() + ".h\"\n\n");

		// Generate the prototypes for each method except top
		for (String p : codeGenerator.getPrototypes()) {
			append(p);
		}
		append("\n");

		// Generate the top method from which the C++ graph building is launch
		topMehod(pg);
		
		// Concatenate the results
		for (StringBuilder m : codeGenerator.getMethods()) {
			cppString.append(m);
		}
		
		// Add free fct
		append("\n");
		append("void free_" + pg.getName() + "(){\n");
		append("\tSpider::cleanPiSDF();\n");
		append("}\n");
		
		// Returns the final C++ code
		return cppString.toString();
	}
	
	/**
	 * Main method, launching the generation for the whole PiGraph pg, including
	 * license, includes, constants and top method generation
	 */
	public String generateFunctionCode(PiGraph pg) {	
		cppString.setLength(0);
		
		// /Generate the header (license, includes and constants)
		append(getLicense());

		append("#include <spider/spider.h>\n");
		append("#include \"" + pg.getName() + ".h\"\n\n");
		
		Set<String> includeList = new HashSet<String>();
		for(AbstractActor aa : functionMap.keySet()){
			Actor a = (Actor)aa;
			if(a.getRefinement() instanceof HRefinement){			
				if(!includeList.contains(a.getRefinement().getFileName())){
					includeList.add(a.getRefinement().getFileName());
				}
			}
		}
		
		for(String file : includeList){
			append("#include \""+file+"\"\n");
		}
		
		append("\n");
		
		/* Generate prototypes */
		for(AbstractActor aa : functionMap.keySet()){
			append("void ");
			append(SpiderNameGenerator.getFunctionName(aa));
			append("(void* inputFIFOs[], void* outputFIFOs[], Param inParams[], Param outParams[]);\n");
		}
		append("\n");

		/* Generate LrtFct */
		append("lrtFct " + pg.getName() + "_fcts[N_FCT_" + pg.getName().toUpperCase() + "] = {\n");
		for(AbstractActor aa : functionMap.keySet()){
			append("\t&" + SpiderNameGenerator.getFunctionName(aa) + ",\n");			
		}
		append("};\n\n");
		
		// Generate functions
		for(AbstractActor aa : functionMap.keySet()){
			generateFunctionBody(aa);
		}
		
		// Returns the final C++ code
		return cppString.toString();
	}

	/**
	 * Generate the top method, responsible for building the whole C++ PiGraph
	 * corresponding to pg
	 */
	private void topMehod(PiGraph pg) {
		String sgName = pg.getName();
		
		append("/**\n");
		append(" * This is the method you need to call to build a complete PiSDF graph.\n");
		append(" */\n");
		
		// The method does not return anything and is named top
		append("void init_"+pg.getName()+"(");		
		
		StringBuilder params = new StringBuilder();
		List<Parameter> l = new LinkedList<Parameter>();
		l.addAll(pg.getAllParameters());
		Collections.sort(l, new Comparator<Parameter>() {
			@Override
	        public int compare(Parameter p1, Parameter p2){
	            return  p1.getName().compareTo(p2.getName());
	        }
		});
		StringBuilder parameters_proto = new StringBuilder();
		for(Parameter p : l){
			if(p.isLocallyStatic() && !p.isDependent() && !p.isConfigurationInterface()){
				if(parameters_proto.length() > 0){
					parameters_proto.append(", ");
					params.append(", ");
				}
				parameters_proto.append("Param " + p.getName());
				params.append(p.getName());
			}
		}
		append(parameters_proto);
		append("){\n");
		
		// Create a top graph and a top vertex
		append("\tPiSDFGraph* topGraph = Spider::createGraph(\n"
				+ "\t\t/*Edges*/    0,\n"
				+ "\t\t/*Params*/   0,\n"
				+ "\t\t/*InputIf*/  0,\n"
				+ "\t\t/*OutputIf*/ 0,\n"
				+ "\t\t/*Config*/   0,\n"
				+ "\t\t/*Body*/     1);\n\n");
		
		append("\tSpider::addHierVertex(\n"
				+ "\t\t/*Graph*/    topGraph,\n"
				+ "\t\t/*Name*/     \"top\",\n"
				+ "\t\t/*Graph*/    " + sgName + "(" + params.toString() + "),\n"
				+ "\t\t/*InputIf*/  0,\n"
				+ "\t\t/*OutputIf*/ 0,\n"
				+ "\t\t/*Params*/   0);\n\n");
		
		append("\tSpider::setGraph(topGraph);\n");
		append("}\n");
	}
	
	private void generateFunctionBody(AbstractActor aa) {
		append("void ");
		append(SpiderNameGenerator.getFunctionName(aa));
		append("(void* inputFIFOs[], void* outputFIFOs[], Param inParams[], Param outParams[]){\n");
		
		Actor a = (Actor)aa;
		if(a.getRefinement() != null && a.getRefinement() instanceof HRefinement){
			HRefinement href = (HRefinement) a.getRefinement();
			FunctionPrototype proto = href.getLoopPrototype();

			append("\t" + proto.getName() + "(\n");
			int maxParamSize = 0;
			for(FunctionParameter param : proto.getParameters()){
				maxParamSize = Math.max(maxParamSize, param.getName().length());				
			}
			
			boolean first = true;
			for(FunctionParameter param : proto.getParameters()){
				if(first){
					first = false;
				}else{
					append(",\n");					
				}
				boolean found = false;
				switch(param.getDirection()){
				case IN:
					if(param.isIsConfigurationParameter()){
						for(Port port : a.getConfigInputPorts()){
							if(port.getName().equals(param.getName())){
								append("\t\t/* " + String.format("%1$-" + maxParamSize + "s", param.getName()) 
										+ " */ (Param) inParams[" + portMap.get(port) + "]");
								found = true;
							}
						}						
					}else{
						for(Port port : a.getDataInputPorts()){
							if(port.getName().equals(param.getName())){
								append("\t\t/* " + String.format("%1$-" + maxParamSize + "s", param.getName()) 
										+ " */ ("+param.getType()+"*) inputFIFOs[" + portMap.get(port) + "]");
								found = true;
							}
						}						
					}
					break;
				case OUT:
					if(param.isIsConfigurationParameter()){
						for(Port port : a.getConfigOutputPorts()){
							if(port.getName().equals(param.getName())){
								append("\t\t/* " + String.format("%1$-" + maxParamSize + "s", param.getName()) 
										+ " */ (Param*) &outParams[" + portMap.get(port) + "]");
								found = true;
							}
						}						
					}else{
						for(Port port : a.getDataOutputPorts()){
							if(port.getName().equals(param.getName())){
								append("\t\t/* " + String.format("%1$-" + maxParamSize + "s", param.getName()) 
										+ " */ ("+param.getType()+"*) outputFIFOs[" + portMap.get(port) + "]");
								found = true;
							}
						}
					}
					break;
				}
				if(!found){
					WorkflowLogger.getLogger().warning("Port " + param.getName() + " in Actor " + a.getName() + " not found.");
				}
			}
			append("\n\t);\n");
		}
		append("}\n\n");
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
			+ " */\n\n";
	}

	public Map<String, Integer> getCoreTypesCodes() {
		return coreTypesIds;
	}
}
