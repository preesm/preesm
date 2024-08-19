package org.preesm.clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.preesm.algorithm.codegen.idl.Prototype;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.clustering.CodegenModelGeneratorSimSDP;
import org.preesm.codegen.xtend.task.CodegenEngine;
import org.preesm.commons.graph.Vertex;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputInterface;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.ExpressionHolder;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.util.PiSDFSubgraphBuilder;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.generator.ScenariosGenerator;
import org.preesm.model.scenario.util.DefaultTypeSizes;
import org.preesm.model.scenario.util.ScenarioUserFactory;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.TimingType;
import org.preesm.model.slam.generator.ArchitecturesGenerator;
import org.preesm.model.slam.utils.SlamUserFactory;
import org.preesm.ui.pisdf.util.SavePiGraph;

public class NodePartitioner {
	/**
	   * Input graph.
	   */
	  private PiGraph  graph;
	  /**
	   * Workflow scenario.
	   */
	  private Scenario scenario;
	  /**
	   * Architecture design.
	   */
	  private Design   archi;

	private String archipath;
	private String workloadpath;
	private String printer;
	private Map<Long,Map<Long,Long>> archiH;//id node/id core/freq
	private Map<Long,Long> archiEq;//id node/nb core
	private Long totArchiEq;
	private Map<Long,Long> timeEq;//id node/cumulative time
	private Map<Long,Long> load;//id node/exceed
	private Map<AbstractVertex, Long> brv; //actor/brv
	private Map<Long,List<AbstractActor>> topoOrderASAP;//id rank/actor
	private Map<Long,String> nodeNames;
	
	private Map<Long,Map<AbstractActor,Long>> subs;//id node/ actor/instances
	private List<Design> archiList = new ArrayList<>();
	
	static String fileError = "Error occurred during file generation: ";
	private String graphPath="";
	private String archiPath="";
	private String scenariiPath="";
	private String includePath="";
	private String workspaceLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
	Map<Long,Map<AbstractActor,Long>> subsCopy = new HashMap<>();
	private PiGraph topGraph = null;
	public NodePartitioner(PiGraph graph, Scenario scenario, Design archi,String archipath,String workloadpath, String printer) {
		this.graph = graph;
		this.scenario = scenario;
		this.archi = archi;
		this.archipath = archipath;
		this.workloadpath = workloadpath;
		this.printer = printer;
		this.archiH = new HashMap<>();
		this.archiEq = new HashMap<>();
		this.totArchiEq = 0L;
		this.timeEq = new HashMap<>();
		this.load = new HashMap<>();
		this.brv = new HashMap<>();
		this.topoOrderASAP= new HashMap<>();
		this.nodeNames = new HashMap<>();
		this.subs = new HashMap<>();
		
	}
	public PiGraph execute() {
		final String[] uriString = graph.getUrl().split("/");
		graphPath = File.separator+uriString[1]+File.separator+uriString[2]+"/generated/";
		scenariiPath = File.separator+uriString[1]+"/Scenarios/generated/";
		archiPath=File.separator+uriString[1]+"/Archi/";
		includePath = uriString[1]+"/Code/include/";
		//0. check level
		if( !graph.getAllChildrenGraphs().isEmpty())
			PreesmLogger.getLogger().log(Level.INFO, "Hierarchical graphs are not handle yet, please feed a flat version");
		
		//1. compute the number of equivalent core
		computeEqCore();
		if(graph.getActorIndex()<totArchiEq)
			PreesmLogger.getLogger().log(Level.INFO, "O(G_app)<O(G_archi) SimSDP 1.0 isn't appropriated (reduce archi or change method)");
		//2. compute cumulative equivalent time
		brv = PiBRV.compute(graph, BRVMethod.LCM);//test
		computeWorkload();
		computeEqTime();
		//3. sort actor in topological as soon as possible order
		computeTopoASAP();
		//homogeneous transform (if delay create sub of cycle path)
		//4. identifies the actors who will form the sub
		computeSubs();
		//5. construct subs
		constructSubs();
		//7. construct top
		constructTop();
		//9. generate main file
		final CodegenModelGeneratorSimSDP generator = new CodegenModelGeneratorSimSDP(archi, topGraph);
		final String apolloFlag = "false";
	    final Collection<Block> codeBlocks = generator.generate(nodeNames);
	    final String selectedPrinter = printer;
		final String codegenPath = scenario.getCodegenDirectory() + File.separator;
		final CodegenEngine engine = new CodegenEngine(codegenPath, codeBlocks, topGraph, archi,
		        scenario);
		if (NodePartitionerTask.VALUE_PRINTER_IR.equals(selectedPrinter)) {
	          engine.initializePrinterIR(codegenPath);
	        }
		engine.registerApollo(apolloFlag);
        engine.registerPrintersAndBlocks(selectedPrinter);
        engine.preprocessPrinters();
        engine.printMainSimSDP();
		
		return null;
		
	}
	  /**
	   * Read a csv file containing implementation length on each node
	   * compute the average implementation length
	   * for each node compute the excess/!excess
	   * file a structure load
	   */
	private void computeWorkload() {
		//1. read file
		if(!workloadpath.isEmpty()) {
		File file =new File(workloadpath);
		Map<Long,Long> wl = new HashMap<>();
		try {
		    FileReader read = new FileReader(file);
		    BufferedReader buffer = new BufferedReader(read);
		    try {
		        String line;
		        while ((line = buffer.readLine()) != null) {
		            String[] split = line.split(";");
		            Long node = Long.valueOf(split[0]);
		            Long workload = Long.valueOf(split[1]);
		            wl.put(node, workload);
		        }
		    } finally {		        
		            buffer.close();
		    }
		} catch (IOException e) {
		    String errorMessage = fileError + workloadpath;
		    PreesmLogger.getLogger().log(Level.INFO, errorMessage);
		}
		// compute average workload
		Long average = 0L;
		for(Long i = 0L; i< wl.size();i++) {
			average = average + wl.get(i)/wl.size();
		}
		for(Long i = 0L; i< wl.size();i++) {
			load.put(i, wl.get(i)-average);
		}
		}
		
	}
	private void constructTop() {
		//1. replace by an empty actor
		topGraph.setName("top");
		int nodeIndex=0;
		topGraph.setUrl(graphPath+topGraph.getName()+".pi");
		for(AbstractActor pi:topGraph.getActors()) {
			if(pi instanceof PiGraph) {
				Actor aEmpty = PiMMUserFactory.instance.createActor();
				aEmpty.setName(pi.getName());
				for (int i = 0; i < pi.getDataInputPorts().size(); i++) {	
		              DataInputPort inputPort = PiMMUserFactory.instance.copy(pi.getDataInputPorts().get(i));
		              aEmpty.getDataInputPorts().add(inputPort);	
	            }
	            for (int i = 0; i < pi.getDataOutputPorts().size(); i++) {
	              DataOutputPort outputPort = PiMMUserFactory.instance.copy(pi.getDataOutputPorts().get(i));
	              aEmpty.getDataOutputPorts().add(outputPort);	            	
	            }
	            for (int i = 0; i < pi.getConfigInputPorts().size(); i++) {
	              ConfigInputPort cfgInputPort = PiMMUserFactory.instance.copy(pi.getConfigInputPorts().get(i));
	              aEmpty.getConfigInputPorts().add(cfgInputPort);
	            }
	            topGraph.replaceActor(pi, aEmpty);
	            nodeIndex++;
			}
		}
		brv = PiBRV.compute(topGraph, BRVMethod.LCM);//test
		// 2. insert delay
		int index = 0;
		for(Fifo f:topGraph.getFifos()) {
			Delay d = PiMMUserFactory.instance.createDelay();
			d.setName(((AbstractActor) f.getSource()).getName()+"_out_"+ ((AbstractActor) f.getTarget()).getName()+"_in_"+index);
			d.setLevel(PersistenceLevel.PERMANENT);
			d.setExpression(f.getSourcePort().getExpression().evaluate());
			d.setContainingGraph(f.getContainingGraph());
			f.assignDelay(d);
			d.getActor().setContainingGraph(f.getContainingGraph());
			index ++;
		}
		// remove extra parameter
		for(AbstractActor a : topGraph.getExecutableActors()) {
			List<String> cfgOccur = new ArrayList<>();
			for(int i=0; i<a.getConfigInputPorts().size();i++) {
				a.getConfigInputPorts().get(i).setName(((AbstractVertex) a.getConfigInputPorts().get(i).getIncomingDependency().getSetter()).getName());
				String name = a.getConfigInputPorts().get(i).getName();
				
				if(cfgOccur.contains(a.getConfigInputPorts().get(i).getName())) {
					topGraph.removeDependency(a.getConfigInputPorts().get(i).getIncomingDependency());
					a.getConfigInputPorts().remove(a.getConfigInputPorts().get(i));
					i--;
				}
				cfgOccur.add(name);
			}
			
		}
		topGraph.getAllParameters().stream().filter(x->!x.getContainingPiGraph().equals(topGraph)).forEach(x-> topGraph.removeParameter(x));
		for(Parameter param: topGraph.getAllParameters()) {
			for(int i = 0; i< param.getOutgoingDependencies().size();i++) {
				if(param.getOutgoingDependencies().get(i).getContainingGraph()!=topGraph) {
					param.getOutgoingDependencies().get(i).setContainingGraph(topGraph);
				}
			}
		}
		for(Dependency i : topGraph.getAllDependencies()) {
			
			boolean getterContained = i.getGetter().getConfigurable()!= null;
			if(!getterContained) {
				i.getSetter().getOutgoingDependencies().remove(i);
				topGraph.removeDependency(i);
			}
		}
		
		//3. export graph
		PiBRV.compute(topGraph, BRVMethod.LCM);
		graphExporter(topGraph);
		final IPath fromPortableString = Path.fromPortableString(scenariiPath);
		final IFile file2 = ResourcesPlugin.getWorkspace().getRoot().getFile(fromPortableString);
		IProject iProject = file2.getProject();
		ArchitecturesGenerator a = new ArchitecturesGenerator(iProject);
		Design topArchi = ArchitecturesGenerator.generateArchitecture(nodeIndex, "top");
		a.saveArchitecture(topArchi);
		topArchi.setUrl(archiPath+"top.slam");
		//archiList.add(subArchi);
		//4. generate scenario
		Scenario subScenario = ScenarioUserFactory.createScenario();
		subScenario.setAlgorithm(topGraph);
		subScenario.setDesign(topArchi);
		String codegenpath = scenario.getCodegenDirectory();
		subScenario.setCodegenDirectory(codegenpath+"/top");
		scenarioExporter(subScenario);
		
	}
	private void constructSubs() {
		//1. split tasks if it's required
		brv = PiBRV.compute(graph, BRVMethod.LCM);
		for(Entry<Long, Map<AbstractActor, Long>> a : subs.entrySet()) {
			Map<AbstractActor, Long> newValue = new HashMap<>(a.getValue());
			subsCopy.put(a.getKey(), newValue);
		}
		for(Long subRank = 0L;subRank< subs.size();subRank++) {
			for(Entry<AbstractActor, Long> a:  subs.get(subRank).entrySet()) {
				if(a.getValue()< brv.get(a.getKey())) {				
					brv.replace(a.getKey(),split(a.getKey(), a.getValue() ,brv.get(a.getKey()),subRank));
				}
			}	
		}						
		final List<PiGraph> sublist = new ArrayList<>();
		for(Long subRank = 0L;subRank< subs.size();subRank++) {
			//2. generate subs
			final List<AbstractActor> list = new ArrayList<>();
			for(AbstractActor a: subsCopy.get(subRank).keySet()) {
				list.add(a);
			}
			final PiGraph subgraph = new PiSDFSubgraphBuilder(graph, list,"sub_"+subRank).build();

			sublist.add(subgraph);
			
		}
		topGraph = PiMMUserFactory.instance.copyPiGraphWithHistory(graph);
		//3. free subs (Interface --> sink; container)
		int nodeIndex = 0;
		for(PiGraph subgraph: sublist) {
			for(DataInputInterface in: subgraph.getDataInputInterfaces()) {
				Actor src = PiMMUserFactory.instance.createActor();
				src.setName("src_"+in.getName());
				Refinement refinement = PiMMUserFactory.instance.createCHeaderRefinement();
	            src.setRefinement(refinement);
	            CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) ((src).getRefinement());
	            Prototype oEmptyPrototype = new Prototype();
	            oEmptyPrototype.setIsStandardC(true);
	            
	           
	            cHeaderRefinement.setFilePath(includePath+src.getName()+".h");
	            FunctionPrototype functionPrototype = PiMMUserFactory.instance.createFunctionPrototype();
	            cHeaderRefinement.setLoopPrototype(functionPrototype);
	            functionPrototype.setName(src.getName());
	            
	            src.setContainingGraph(in.getDirectSuccessors().get(0).getContainingGraph());
				DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
				src.getDataOutputPorts().add(dout);
				dout.setName("out");
				dout.setExpression(in.getDataPort().getExpression());
				in.getDataOutputPorts().get(0).getFifo().setSourcePort(dout);
				FunctionArgument functionArgument = PiMMUserFactory.instance.createFunctionArgument();
	            functionArgument.setName(src.getAllPorts().get(0).getName());
	            functionArgument.setType(dout.getFifo().getType());
	            functionArgument.setDirection(Direction.OUT);
	            functionPrototype.getArguments().add(functionArgument);
				generateFileH(src);
				//set 1 because it's an interface
				for(final Component opId: archi.getProcessingElements()) {
			    			scenario.getTimings().setExecutionTime(src, opId, 1L);	
			    }
	            
			}
			for(DataOutputInterface out : subgraph.getDataOutputInterfaces()) {
				Actor snk = PiMMUserFactory.instance.createActor();
				snk.setName("snk_"+out.getName());
				Refinement refinement = PiMMUserFactory.instance.createCHeaderRefinement();
				snk.setRefinement(refinement);
	            CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) (snk.getRefinement());
	            Prototype oEmptyPrototype = new Prototype();
	            oEmptyPrototype.setIsStandardC(true);
	            
	            cHeaderRefinement.setFilePath(includePath+snk.getName()+".h");
	            FunctionPrototype functionPrototype = PiMMUserFactory.instance.createFunctionPrototype();
	            cHeaderRefinement.setLoopPrototype(functionPrototype);
	            functionPrototype.setName(snk.getName());	            

	            snk.setContainingGraph(out.getContainingGraph());
				DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
				snk.getDataInputPorts().add(din);
				din.setName("in");
				din.setExpression(out.getDataPort().getExpression());
				out.getDataInputPorts().get(0).getFifo().setTargetPort(din);
				FunctionArgument functionArgument = PiMMUserFactory.instance.createFunctionArgument();
	            functionArgument.setName(snk.getAllPorts().get(0).getName());
	            functionArgument.setType(din.getFifo().getType());
	            functionArgument.setDirection(Direction.IN);
	            functionPrototype.getArguments().add(functionArgument);
				generateFileH(snk);
				//set 1 because it's an interface
				for(final Component opId: archi.getProcessingElements()) {
			    			scenario.getTimings().setExecutionTime(snk, opId, 1L);	
			    }
			}
			//merge cfg
			for(Dependency dep: subgraph.getDependencies()) {
				((AbstractVertex) dep.getSetter()).setName(dep.getGetter().getName());
				((ConfigInputInterface)dep.getSetter()).getGraphPort().setName(dep.getGetter().getName());
				
			}
			int paramSize = subgraph.getParameters().size();
			for(int i=0; i <paramSize;i++) {
				for(int j=i+1;j<paramSize;j++) {
					if(subgraph.getParameters().get(i).getName().equals(subgraph.getParameters().get(j).getName())) {						
						subgraph.getParameters().get(i).getOutgoingDependencies().add(subgraph.getParameters().get(j).getOutgoingDependencies().get(0));
						subgraph.removeParameter(subgraph.getParameters().get(j));
						paramSize--;
						j--;
					}
				}
			}
			for(int i=0; i <subgraph.getConfigInputPorts().size();i++) {
				if(!( subgraph.getConfigInputPorts().get(i) instanceof Parameter)) {
					Parameter p = PiMMUserFactory.instance.createParameter(subgraph.getParameters().get(i).getName(), subgraph.getParameters().get(i).getExpression().evaluate());
					Long exp = ((ExpressionHolder) subgraph.getConfigInputPorts().get(i).getIncomingDependency().getSetter()).getExpression().evaluate();
					for(int j=0; j<subgraph.getParameters().get(i).getOutgoingDependencies().size();j++) {
						p.getOutgoingDependencies().add(subgraph.getParameters().get(i).getOutgoingDependencies().get(j));
						j--;
					}
					p.setExpression(exp);
					subgraph.addParameter(p);
					subgraph.removeParameter(subgraph.getParameters().get(i));
					i--;
				}
			}
			
			//remove empty fifo
			subgraph.getFifos().stream().filter(x->x.getSourcePort()==null).forEach(x-> subgraph.removeFifo(x));
			subgraph.getFifos().stream().filter(x->x.getTargetPort()==null).forEach(subgraph::removeFifo);
			subgraph.getAllActors().stream().filter(x-> x instanceof DataInputInterface ||x instanceof DataOutputInterface).forEach(x-> subgraph.removeActor(x));
			//subgraph.remove
			PiBRV.compute(subgraph, BRVMethod.LCM);
			final String[] uriString = graph.getUrl().split("/");
			subgraph.setUrl(graphPath+subgraph.getName()+".pi");
			//4. export subs
			graphExporter(subgraph);
			
			//5. generate scenarii
			
			
			Scenario subScenario = ScenarioUserFactory.createScenario();
			Design subArchi = archiList.get(nodeIndex);
			subScenario.setDesign(subArchi);//temp
			
			subScenario.setAlgorithm(subgraph);
			// Get com nodes and cores names
		    final List<ComponentInstance> coreIds = new ArrayList<>(subArchi.getOperatorComponentInstances());
		    final List<ComponentInstance> comNodeIds = subArchi.getCommunicationComponentInstances();
			
			 // Set default values for constraints, timings and simulation parameters
		    
		 // Add a main core (first of the list)
		    if (!coreIds.isEmpty()) {
		    	subScenario.getSimulationInfo().setMainOperator(coreIds.get(0));
		    }
		    if (!comNodeIds.isEmpty()) {
		    	subScenario.getSimulationInfo().setMainComNode(comNodeIds.get(0));
		    }
		    
		    //subScenario.getTimings().setExcelFileURL("");
		    csvGenerator(subScenario, subgraph, subArchi);
		    for(final Component opId: subArchi.getProcessingElements()) {
		    	for(final AbstractActor aa: subgraph.getAllActors()) {
		    		if(aa instanceof Actor)
		    			subScenario.getTimings().setExecutionTime(aa, opId, this.scenario.getTimings().getExecutionTimeOrDefault(aa, archi.getProcessingElement(opId.getVlnv().getName())));
		    	}
		    	
		    }
		    subScenario.getTimings().getMemTimings().addAll(scenario.getTimings().getMemTimings());
		    
		    for(final ComponentInstance coreId : coreIds) {
		    	for(final AbstractActor aa: subgraph.getAllActors())
		    		if(aa instanceof Actor) {
		    			subScenario.getConstraints().addConstraint(coreId, aa);}
		    	subScenario.getConstraints().addConstraint(coreId, subgraph);
		    	subScenario.getSimulationInfo().addSpecialVertexOperator(coreId);
		    }
		    
		 // Add a average transfer size
		    subScenario.getSimulationInfo().setAverageDataSize(scenario.getSimulationInfo().getAverageDataSize());
		    // Set the default data type sizes
		    for (final Fifo f : subScenario.getAlgorithm().getAllFifos()) {
		      final String typeName = f.getType();
		      subScenario.getSimulationInfo().getDataTypes().put(typeName,
		          DefaultTypeSizes.getInstance().getDefaultTypeSize(typeName));
		    }
		    //add constraint
		    subScenario.getConstraints().setGroupConstraintsFileURL("");
		    for(Entry<ComponentInstance, EList<AbstractActor>> gp: subScenario.getConstraints().getGroupConstraints()) {
		    	for (final AbstractActor actor : subgraph.getAllActors()) {
    		        gp.getValue().add(actor);
    		      }
		    }
//		    for(Entry<Parameter, String> p: scenario.getParameterValues())
//		    	subScenario.getParameterValues().add(p);

			subScenario.setCodegenDirectory("/"+uriString[1]+"/Code/generated/"+subgraph.getName());
			subScenario.setSizesAreInBit(true);
			scenarioExporter(subScenario);
		}
		
		
		
	}
	private void csvGenerator(Scenario scenar, PiGraph pigraph, Design architecture) {
		// Create the list of core_types
        List<Component> coreTypes = new ArrayList<>(architecture.getProcessingElements()); // List of core_types

        // Create the CSV header
        StringConcatenation content = new StringConcatenation();
        content.append("Actors;");
		for(Component coreType: coreTypes) {
			coreType.getVlnv().getName();
			content.append(coreType.getVlnv().getName()+";");
		}
			
		content.append("\n"); 

        // Create data rows
        for (AbstractActor actor : pigraph.getActors()) {
        	if(actor instanceof Actor && !(actor instanceof SpecialActor)) {
        		content.append(actor.getVertexPath().replace(graph.getName()+"/", "")+";");
            

            for (Component coreType : coreTypes) {
                String executionTime = scenar.getTimings().getExecutionTimeOrDefault(actor, coreType);
                content.append(executionTime);
            }

            content.append("\n");
        	}
        }
String path = workspaceLocation+ scenariiPath+pigraph.getName()+".csv";
scenar.getTimings().setExcelFileURL(scenariiPath+pigraph.getName()+".csv");
try (FileOutputStream outputStream = new FileOutputStream(path)) {
    byte[] bytes = content.toString().getBytes();
    outputStream.write(bytes);
} catch (IOException e) {
	String errorMessage = fileError + e.getMessage();
	PreesmLogger.getLogger().log(Level.INFO, errorMessage);
} 

    
	}
	private void generateFileH(Actor snk) {

		final String[] uriString = graph.getUrl().split("/");
		String content = "// jfécekejepeu \n #ifndef "+snk.getName().toUpperCase()+"_H \n #define "+snk.getName().toUpperCase()+"_H \n void "+snk.getName()+"("+snk.getAllDataPorts().get(0).getFifo().getType()+" "+snk.getAllDataPorts().get(0).getName()+"); \n #endif";
		String path = workspaceLocation+"/"+uriString[1]+"/Code/include/"+snk.getName()+".h";
		 try (FileOutputStream outputStream = new FileOutputStream(path)) {
	            byte[] bytes = content.getBytes();
	            outputStream.write(bytes);
	        } catch (IOException e) {
	        	String errorMessage = fileError + e.getMessage();
	        	PreesmLogger.getLogger().log(Level.INFO, errorMessage);
	        } 
		
	}
	private Long split(AbstractActor key, Long rv1, Long rv2,Long subRank) {
		//if data pattern
			// copy instance
			AbstractActor copy = PiMMUserFactory.instance.copy(key);
			copy.setContainingGraph(key.getContainingGraph());
			int index = 0;
			for(DataInputPort in:key.getDataInputPorts()) {
				if(!in.getFifo().isHasADelay()) {
					ForkActor frk = PiMMUserFactory.instance.createForkActor();
					frk.setName("Fork_"+key.getName()+index);
					frk.setContainingGraph(key.getContainingGraph());
					
					//connect din to frk
					DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
					din.setName("in");
					Long dt = in.getFifo().getSourcePort().getExpression().evaluate()*brv.get(in.getFifo().getSource());
					Long rt = in.getExpression().evaluate();
					din.setExpression(dt);
					
					frk.getDataInputPorts().add(din);
					Fifo fin = PiMMUserFactory.instance.createFifo();
					fin.setType(in.getFifo().getType());
					fin.setSourcePort(in.getFifo().getSourcePort());
					fin.setTargetPort(din);
					
					fin.setContainingGraph(key.getContainingGraph());
					
					//connect fork to oEmpty_0
					DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
					dout.setName("out_0");
					dout.setExpression(dt-(rv1*rt));
					frk.getDataOutputPorts().add(dout);
					Fifo fout = PiMMUserFactory.instance.createFifo();
					fout.setType(in.getFifo().getType());
					fout.setSourcePort(dout);
					fout.setTargetPort(in);
					fout.setContainingGraph(key.getContainingGraph());
					
					// connect fork to duplicated actors
						DataOutputPort doutn = PiMMUserFactory.instance.createDataOutputPort();
						doutn.setName("out_"+1);
						doutn.setExpression(dt-((rv2-rv1)*rt));
						frk.getDataOutputPorts().add(doutn);
						Fifo foutn = PiMMUserFactory.instance.createFifo();
						foutn.setType(fin.getType());
						foutn.setSourcePort(doutn);
						foutn.setContainingGraph(key.getContainingGraph());
					copy.getDataInputPorts().stream().filter(x->x.getName().equals(in.getName())).forEach(x -> x.setIncomingFifo(foutn));

					subsCopy.get(subRank).put(frk, 1L);
					index++;
				}else {
					//if setter
//					if(in.getFifo().getDelay().hasSetterActor()) {
//						Fifo fd = PiMMUserFactory.instance.createFifo();
//						fd.setSourcePort(in.getFifo().getDelay().getSetterPort());
//						fd.setTargetPort(in);
//						fd.setContainingGraph(key.getContainingGraph());
//						
//					}//else {
//						PreesmLogger.getLogger().log(Level.INFO, "unrolled loops requires setter and getter affected to a local delay");
//						Actor set = PiMMUserFactory.instance.createActor();
//						//InitActor set = PiMMUserFactory.instance.createInitActor();
//						set.setName("setter");
//						Refinement refinement = PiMMUserFactory.instance.createCHeaderRefinement();
//
//			            set.setRefinement(refinement);
//			            //((Actor) oEmpty).getRefinement().getFileName()
//			            // Set the refinement
//			            CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) (((Actor) set).getRefinement());
//			            Prototype oEmptyPrototype = new Prototype();
//			            oEmptyPrototype.setIsStandardC(true);
//			            //String fileName = ;//lastLevel.getActorPath().replace("/", "_");
//			            cHeaderRefinement.setFilePath(((Actor) key).getRefinement().getFilePath());
//			            FunctionPrototype functionPrototype = PiMMUserFactory.instance.createFunctionPrototype();
//			            cHeaderRefinement.setLoopPrototype(functionPrototype);
//			            functionPrototype.setName(((Actor) key).getRefinement().getFileName());
//						
//						//set.setEndReference(oEmpty);
//						set.setContainingGraph(key.getContainingGraph());
//						DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
//						set.getDataOutputPorts().add(dout);
//						dout.setName("out");
//						dout.setExpression(in.getFifo().getDelay().getExpression().evaluate());
//						Fifo fd = PiMMUserFactory.instance.createFifo();
//						fd.setSourcePort(set.getDataOutputPorts().get(0));
//						fd.setTargetPort(in);
//						fd.setContainingGraph(key.getContainingGraph());
//						subs.get(subRank).put(set, 1L);
//					}
				}
			}
		index = 0;
			for(DataOutputPort out:key.getDataOutputPorts()) {
				if(!out.getFifo().isHasADelay()) {
					JoinActor jn = PiMMUserFactory.instance.createJoinActor();
					jn.setName("Join_"+key.getName()+index);
					jn.setContainingGraph(key.getContainingGraph());
					
					//connect Join to dout
					DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
					
					dout.setName("out");
					Long dt=out.getFifo().getTargetPort().getExpression().evaluate()*brv.get(out.getFifo().getTarget());
					Long rt = out.getExpression().evaluate();
					dout.setExpression(dt);
					jn.getDataOutputPorts().add(dout);
					Fifo fout = PiMMUserFactory.instance.createFifo();
					fout.setType(out.getFifo().getType());
					fout.setSourcePort(dout);
					fout.setTargetPort(out.getFifo().getTargetPort());
					fout.setContainingGraph(key.getContainingGraph());
					
					//connect oEmpty_0 to Join
					DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
					din.setName("in_0");
					din.setExpression(dt-(rv1*rt));
					jn.getDataInputPorts().add(din);
					Fifo fin = PiMMUserFactory.instance.createFifo();
					fin.setSourcePort(out);
					fin.setTargetPort(din);
					fin.setContainingGraph(key.getContainingGraph());
					out.getFifo().setType(fout.getType());
					
					// connect duplicated actors to Join
						DataInputPort dinn = PiMMUserFactory.instance.createDataInputPort();
						dinn.setName("in_"+1);
						dinn.setExpression(dt-(rv2-rv1)*out.getFifo().getSourcePort().getExpression().evaluate());
						jn.getDataInputPorts().add(dinn);
						Fifo finn = PiMMUserFactory.instance.createFifo();
						finn.setType(fout.getType());
						finn.setTargetPort(dinn);
						finn.setContainingGraph(key.getContainingGraph());
					copy.getDataOutputPorts().stream().filter(x->x.getName().equals(out.getName())).forEach(x -> x.setOutgoingFifo(finn));
					
					//}
					if(subsCopy.get(subRank+1)==null) {
						subsCopy.get(subRank).put(jn, 1L);
						subsCopy.get(subRank).remove(key);
						subsCopy.get(subRank).put(copy, rv2-rv1);
					}else {
						subsCopy.get(subRank+1).put(jn, 1L);
						subsCopy.get(subRank+1).remove(key);
						subsCopy.get(subRank+1).put(copy, rv2-rv1);
					}
					index++;
				}
			else {
	
				//if getter
				// connect last one to getter
				//Fifo fd = PiMMUserFactory.instance.createFifo();
				
//				if(out.getFifo().getDelay().hasGetterActor()) {
//					Fifo fdout = PiMMUserFactory.instance.createFifo();
//					copy.getDataOutputPorts().stream().filter(x -> x.getFifo()==null).forEach(x -> x.setOutgoingFifo(fdout));
//					fdout.setTargetPort(out.getFifo().getDelay().getGetterPort());
//					fdout.setContainingGraph(key.getContainingGraph());
//					
//				}//else {
//					PreesmLogger.getLogger().log(Level.INFO, "unrolled loops requires setter and getter affected to a local delay");
//					//EndActor get = PiMMUserFactory.instance.createEndActor();
//					Actor get = PiMMUserFactory.instance.createActor();
//					get.setName("getter");
//					///******
//					Refinement refinement = PiMMUserFactory.instance.createCHeaderRefinement();
//
//		            get.setRefinement(refinement);
//		            //((Actor) oEmpty).getRefinement().getFileName()
//		            // Set the refinement
//		            CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) (((Actor) get).getRefinement());
//		            Prototype oEmptyPrototype = new Prototype();
//		            oEmptyPrototype.setIsStandardC(true);
//		            //String fileName = ;//lastLevel.getActorPath().replace("/", "_");
//		            cHeaderRefinement.setFilePath(((Actor) oEmpty).getRefinement().getFilePath());
//		            FunctionPrototype functionPrototype = PiMMUserFactory.instance.createFunctionPrototype();
//		            cHeaderRefinement.setLoopPrototype(functionPrototype);
//		            functionPrototype.setName(((Actor) oEmpty).getRefinement().getFileName());/**/
//					//get.setInitReference(dupActorsList.get((int) (value-2)));
//		            
//					get.setContainingGraph(oEmpty.getContainingGraph());
//					DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
//					get.getDataInputPorts().add(din);
//					din.setName("in");
//					din.setExpression(out.getFifo().getDelay().getExpression().evaluate());
//					Fifo fdout = PiMMUserFactory.instance.createFifo();
//					dupActorsList.get((int) (value-2)).getDataOutputPorts().stream().filter(x->x.getFifo()==null).forEach(x -> x.setOutgoingFifo(fdout));
//					fdout.setTargetPort(get.getDataInputPorts().get(0));
//					fdout.setContainingGraph(oEmpty.getContainingGraph());
//				}
				//connect oEmpty delayed output to 1st duplicated actor
				Fifo fdin = PiMMUserFactory.instance.createFifo();
				fdin.setSourcePort(out);
				copy.getDataInputPorts().stream().filter(x->x.getFifo()==null).forEach(x -> x.setIncomingFifo(fdin));
				fdin.setContainingGraph(key.getContainingGraph());
				
				
			}
			}
			for(ConfigInputPort cfg:key.getConfigInputPorts()) {
				copy.getConfigInputPorts().stream().filter(x -> x.getName().equals(cfg.getName())).forEach(x->PiMMUserFactory.instance.createDependency(cfg.getIncomingDependency().getSetter(), x));
				copy.getConfigInputPorts().stream().filter(x -> x.getName().equals(cfg.getName())).forEach(x -> x.getIncomingDependency().setContainingGraph(cfg.getIncomingDependency().getContainingGraph()));

			}
			// interconnect duplicated actor on their delayed port
//			for(int i = 0;i<=2;i++) {
//			Fifo fd = PiMMUserFactory.instance.createFifo();
//			copy.getDataOutputPorts().stream().filter(x->x.getFifo()==null).forEach(x -> x.setOutgoingFifo(fd));
//			copy.getDataInputPorts().stream().filter(x->x.getFifo()==null).forEach(x -> x.setIncomingFifo(fd));
//			fd.setContainingGraph(key.getContainingGraph());
//			}
			//set 1 because it's an interface
			for(final Component opId: archi.getProcessingElements()) {
		    			scenario.getTimings().setExecutionTime(copy, opId, scenario.getTimings().getExecutionTimeOrDefault(key, opId));	
		    }
			//remove delay
			((PiGraph)key.getContainingGraph()).getDelays().stream().filter(x->x.getContainingFifo().getSourcePort()==null).forEach(x->((PiGraph)key.getContainingGraph()).removeDelay(x));
			//remove empty fifo
			((PiGraph)key.getContainingGraph()).getFifos().stream().filter(x->x.getSourcePort()==null).forEach(x-> ((PiGraph)key.getContainingGraph()).removeFifo(x));
			((PiGraph)key.getContainingGraph()).getFifos().stream().filter(x->x.getTargetPort()==null).forEach(x->((PiGraph)key.getContainingGraph()).removeFifo(x));
		
			brv = PiBRV.compute(graph, BRVMethod.LCM);
			return 	rv2 - rv1;

	}
	private void computeSubs() {
		Long nodeID = 0L;
		Long timTemp = 0L;
		preprocessCycle();
		Map<AbstractActor,Long> list = new HashMap<>();
		Long lastEntry = (long) (topoOrderASAP.entrySet().size()-1);
		int lastKey = (topoOrderASAP.get(lastEntry).size()-1);
		AbstractActor lastActor = topoOrderASAP.get(lastEntry).get(lastKey);
		for(Entry<Long, List<AbstractActor>> entry: topoOrderASAP.entrySet()) {
			for(AbstractActor a:entry.getValue()) {
				Long count = 0L;
				while(count< brv.get(a)) {
				//1. compute actor timing on slowest core
					Long slow = 0L;
					if(a instanceof Actor) {
						if(scenario.getTimings().getActorTimings().get(a)!=null) {
						slow = Long.valueOf(scenario.getTimings().getActorTimings().get(a).get(0).getValue().get(TimingType.EXECUTION_TIME));
				for(int i = 0; i<scenario.getTimings().getActorTimings().get(a).size(); i++) {
					Long timeSeek = Long.valueOf(scenario.getTimings().getActorTimings().get(a).get(i).getValue().get(TimingType.EXECUTION_TIME));
					 if(timeSeek<slow) {
						 slow = timeSeek;
					 }
				}
						}else {
							slow = 100L;
						}
				}
				
				//add instance while lower than target sub time
				Long i;
				for( i=0L; count +i<brv.get(a) && timTemp < timeEq.get(nodeID); i++) {
					timTemp = timTemp + slow;					
				}
				count = count+i;
				list.put(a, i);
				if(timTemp > timeEq.get(nodeID) || a.equals(lastActor)) {//ou si last actor
					
					subs.put(nodeID, list);
					nodeID++;
					list = new HashMap<>();
					timTemp = 0L;
				}
				}
			}
		}
	}
	private void preprocessCycle() {
		// TODO Identify cycle actor list
		//2. create subs
		
	}

	private void computeTopoASAP() {
		List<AbstractActor> temp = new ArrayList<>();
		List<AbstractActor> entry = new ArrayList<>();
		Long rank  = 0L;
		for(AbstractActor a: graph.getActors()) {
			temp.add(a);
		}
		// feed the 1st rank
		for(AbstractActor a: graph.getActors()) {
			if(a.getDataInputPorts().isEmpty()) {
				entry.add(a);
				temp.remove(a);
			}
		}
		topoOrderASAP.put(rank, entry);
		// feed the rest		
		while(!temp.isEmpty()) {
			List <AbstractActor> list = new ArrayList<>();
			for(AbstractActor a: topoOrderASAP.get(rank)) {
				for(Vertex aa: a.getDirectSuccessors()) {
					//this is piece of art, don't remove
					final Long rankMatch = rank+1;
					if(aa.getDirectPredecessors().stream().filter(x ->x instanceof Actor||x instanceof SpecialActor).allMatch(x -> topoOrderASAP.entrySet().stream().filter(y -> y.getKey()<rankMatch).anyMatch(y -> y.getValue().contains(x))) && (!list.contains(aa))) {
						list.add((AbstractActor) aa);
						temp.remove(aa);
						
					}					
				}
			}
			//orders the list in descending order of the execution time of the actors in the rank
			List<AbstractActor> sortedList = new ArrayList<>(list);
	        Collections.sort(sortedList, (actor1, actor2) -> {
	            double time1 = slowestTime(actor1);
	            double time2 = slowestTime(actor2);
	            return Double.compare(time2, time1); 
	        });
			rank++;
			topoOrderASAP.put(rank, sortedList);			
		}
	}
	private Long slowestTime(AbstractActor actor) {
		Long slow;
		if(scenario.getTimings().getActorTimings().get(actor)!=null) {
		slow = Long.valueOf(scenario.getTimings().getActorTimings().get(actor).get(0).getValue().get(TimingType.EXECUTION_TIME));
		for(int i = 0; i<scenario.getTimings().getActorTimings().get(actor).size(); i++) {
			Long timeSeek = Long.valueOf(scenario.getTimings().getActorTimings().get(actor).get(i).getValue().get(TimingType.EXECUTION_TIME));
			 if(timeSeek<slow) {
				 slow = timeSeek;
			 }
			}
		}else {
			slow=100L;
		}
		return slow;
	}
	private void computeEqTime() {
		// total equivalent cumulative time
		Long totTCeq = 0L;
		for(AbstractActor a: graph.getExecutableActors()) {
			if (a instanceof Actor) {
				Long slow;
				if(scenario.getTimings().getActorTimings().get(a)!=null) {
				slow = Long.valueOf(scenario.getTimings().getActorTimings().get(a).get(0).getValue().get(TimingType.EXECUTION_TIME));
				for(int i = 0; i<scenario.getTimings().getActorTimings().get(a).size(); i++) {
					Long timeSeek = Long.valueOf(scenario.getTimings().getActorTimings().get(a).get(i).getValue().get(TimingType.EXECUTION_TIME));
					 if(timeSeek<slow) {
						 slow = timeSeek;
					 }
					}
				}else {
					slow=100L;
				}
				totTCeq = slow * brv.get(a)+ totTCeq;
			}
		}
		// construct structure
		for(long i = 0; i< archiEq.keySet().size(); i++) {
			Long timeEqSeek = totTCeq * archiEq.get(i)/totArchiEq;
			timeEq.put(i, timeEqSeek);
		}
	}
	private void computeEqCore() {
		// Read temporary architecture file, extract composition and build structure
		// file -> |Node name|coreID|frequency|
		File file =new File(archipath);
		Long minFreq = Long.MAX_VALUE;//MHz
		try {
            FileReader read = new FileReader(file);
            BufferedReader buffer = new BufferedReader(read);
            long nodeID = 0L;
            String line;
            while ((line = buffer.readLine()) != null) {
            	String[] split = line.split(";");
            	if(!archiH.isEmpty()) {
            		
            		if(!nodeNames.containsValue(split[0])) {
            			nodeID++;
            			Long node = nodeID;
            			Long core = Long.valueOf(split[1]);
            			Long freq = Long.valueOf(split[2]);
            			Map<Long, Long> basis = new HashMap<>();
            			basis.put(core, freq);
            			archiH.put(node, basis);
            			nodeNames.put(nodeID, split[0]);
            			if(freq < minFreq)
            				minFreq = freq;
            		}else{
            			Long core = Long.valueOf(split[1]);
            			Long freq = Long.valueOf(split[2]);
            			
            			archiH.get(nodeID).put(core, freq);
            			if(freq < minFreq)
            				minFreq = freq;
            		}
            	}else {
            		Long node = nodeID;
        			Long core = Long.valueOf(split[1]);
        			Long freq = Long.valueOf(split[2]);
        			Map<Long, Long> basis = new HashMap<>();
        			basis.put(core, freq);
        			archiH.put(node, basis);
        			nodeNames.put(nodeID, split[0]);
        			if(freq < minFreq)
        				minFreq = freq;
            	}
            	
            	
            }
            buffer.close();
            read.close();
        } catch (IOException e) {
        	String errorMessage = fileError + e.getMessage();
        	PreesmLogger.getLogger().log(Level.INFO, errorMessage);
        }
		Map<Long,Long> architemp = new HashMap<>();
		//construct equivalent archi structure
		for(Long i =  0L; i<archiH.keySet().size();i++) {
			Long coreEq=0L;
			for(Entry<Long, Long> j: archiH.get(i).entrySet()) {
				Long ratio = j.getValue()/minFreq;
				coreEq = coreEq + ratio;
			}

			architemp.put(i, coreEq);
			totArchiEq = totArchiEq + coreEq;
		}	
		// sort in descending order of performance
		//first is the one with the max number of highest frequency
		
		//if homogeneous frequency first is the one with highest core


		// Ordonner les nœuds en fonction de archiEq, archiH et les critères donnés
		List<Map.Entry<Long, Long>> nodeList = new ArrayList<>(architemp.entrySet());
		nodeList.sort((node1, node2) -> Long.compare(node2.getValue(), node1.getValue())); // Tri décroissant

		Long newIndex = 0L;
		for (Map.Entry<Long, Long> entry : nodeList) {
			archiEq.put(newIndex++, entry.getValue());
			final IPath fromPortableString = Path.fromPortableString(scenariiPath);
			final IFile file2 = ResourcesPlugin.getWorkspace().getRoot().getFile(fromPortableString);
			IProject iProject = file2.getProject();
			ArchitecturesGenerator a = new ArchitecturesGenerator(iProject);
			Design subArchi = ArchitecturesGenerator.generateArchitecture(entry.getValue().intValue(), "Node"+(newIndex-1));
			a.saveArchitecture(subArchi);
			subArchi.setUrl(archiPath+"Node"+(newIndex-1)+".slam");
			archiList.add(subArchi);
			//a.generateAndSaveArchitecture(entry.getValue().intValue(),"Node"+(newIndex-1));

		}

	}
	
	

	private void graphExporter(PiGraph printgraph) {
		PiBRV.compute(printgraph, BRVMethod.LCM);
		final IPath fromPortableString = Path.fromPortableString(graphPath);
		final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(fromPortableString);
		IProject iProject = file.getProject();
		SavePiGraph.savePiGraphInFolder(iProject, fromPortableString, printgraph, "");
		
	}
	
	private void scenarioExporter(Scenario scenario) {
		Set<Scenario> scenarios = new HashSet<>();
		scenarios.add(scenario);
		
		final IPath fromPortableString = Path.fromPortableString(scenariiPath);
		final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(fromPortableString);
		IProject iProject = file.getProject();
		scenario.setScenarioURL(scenariiPath+scenario.getAlgorithm().getName()+".scenario");
		ScenariosGenerator s = new ScenariosGenerator(iProject);
		IFolder  scenarioDir = iProject.getFolder("Scenarios/generated");
		try {
			s.saveScenarios(scenarios, scenarioDir);
		} catch (CoreException e) {
			String errorMessage = fileError + e.getMessage();
        	PreesmLogger.getLogger().log(Level.INFO, errorMessage);
		}
	}
	
	private void slamExporter(Design architecture) {
		final IPath fromPortableString = Path.fromPortableString(scenariiPath);
		final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(fromPortableString);
		IProject iProject = file.getProject();
		ArchitecturesGenerator a = new ArchitecturesGenerator(iProject);
		a.saveArchitecture(architecture);
		//a.generateAndSaveArchitecture(1);
	}
	 

}
