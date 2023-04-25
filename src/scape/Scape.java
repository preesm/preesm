package scape;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;

import org.preesm.algorithm.clustering.partitioner.ClusterPartitioner;
import org.preesm.algorithm.clustering.partitioner.ClusterPartitionerGRAIN;
import org.preesm.algorithm.clustering.partitioner.ClusterPartitionerLOOP2;
import org.preesm.algorithm.clustering.partitioner.ClusterPartitionerSEQ;
import org.preesm.algorithm.clustering.partitioner.ClusterPartitionerSRV;
import org.preesm.algorithm.codegen.idl.Prototype;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.codegen.model.Block;
import org.preesm.codegen.model.ClusterRaiserBlock;
import org.preesm.codegen.model.clustering.CodegenModelGeneratorClusteringRaiser;
import org.preesm.codegen.xtend.task.CodegenEngine;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.AbstractVertex;
import org.preesm.model.pisdf.Actor;
import org.preesm.model.pisdf.BroadcastActor;
import org.preesm.model.pisdf.CHeaderRefinement;
import org.preesm.model.pisdf.ConfigInputPort;
import org.preesm.model.pisdf.DataInputInterface;
import org.preesm.model.pisdf.DataInputPort;
import org.preesm.model.pisdf.DataOutputInterface;
import org.preesm.model.pisdf.DataOutputPort;
import org.preesm.model.pisdf.DataPort;
import org.preesm.model.pisdf.Delay;
import org.preesm.model.pisdf.DelayActor;
import org.preesm.model.pisdf.Dependency;
import org.preesm.model.pisdf.Direction;
import org.preesm.model.pisdf.EndActor;
import org.preesm.model.pisdf.Fifo;
import org.preesm.model.pisdf.ForkActor;
import org.preesm.model.pisdf.FunctionArgument;
import org.preesm.model.pisdf.FunctionPrototype;
import org.preesm.model.pisdf.ISetter;
import org.preesm.model.pisdf.InitActor;
import org.preesm.model.pisdf.InterfaceActor;
import org.preesm.model.pisdf.JoinActor;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PersistenceLevel;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.Refinement;
import org.preesm.model.pisdf.RoundBufferActor;
import org.preesm.model.pisdf.SpecialActor;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.brv.PiBRV;
import org.preesm.model.pisdf.check.CheckerErrorLevel;
import org.preesm.model.pisdf.check.PiGraphConsistenceChecker;
import org.preesm.model.pisdf.factory.PiMMUserFactory;
import org.preesm.model.pisdf.statictools.PiMMHelper;
import org.preesm.model.pisdf.util.LOOP1Seeker;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.scenario.ScenarioFactory;
import org.preesm.model.scenario.util.ScenarioUserFactory;
import org.preesm.model.slam.Component;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.TimingType;

public class Scape {
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
	  /**
	   * Size of the stack.
	   */
	  private Long stackSize;
	  /**
	   * Number of Processing elements.
	   */
	  private Long coreNumber;
	  /**
	   * Number of hierarchical level.
	   */
	  private int levelNumber;
	  /**
	   * SCAPE mode : 1 (...); 2 (...); 3 (...).
	   */
	  private int mode;
	  /**
	   * Fix the number of actor maximum of the SrDAG of the output graph.
	   */
	  private int srdagMax;
	  /**
	   * (not available yet).
	   */
	  private boolean parallelCluster;
	  /**
	   * assign elements of the same cluster to the same component.
	   */
	  private Component clusterComponent;
	  /**
	   * Repetition Vector.
	   */
	  private Map<AbstractVertex, Long> brv;
	  /**
	   * Store the splitted clusterable levels.
	   */
	  protected List<PiGraph> levelSCAPED = new ArrayList<>();
	  /**
	   * Store the non clusterable actor chosen manually by the operator.
	   */
	  private List<AbstractActor> nonClusterableList;

	  private int clusterIndex = -1;//topological index
	  private int clusterId = 0;// index cluster created

	public Scape(PiGraph graph, Scenario scenario, Design archi, Long stackSize, Long coreNumber,
			int mode, int levelNumber, List<AbstractActor> nonClusterableList) {
	    this.graph = graph;
	    this.scenario = scenario;
	    this.archi = archi;
	    this.stackSize = stackSize;
	    this.coreNumber = coreNumber;
	    this.mode = mode;
	    this.levelNumber = levelNumber;
	    this.srdagMax = 4000;
	    this.parallelCluster = false;
	    this.nonClusterableList = nonClusterableList;
	}

	public PiGraph execute() {
	    
	    int levelCount = 0;
	    //int patternLoop = 0;//several generation of urc srv
	    String previousInitFunc ="";
	    PiGraph upperLevel = graph;
	    boolean isLastCluster = false;
	    boolean fullRaiserMode = false;
	    Long sumTiming = (long) 0;
	    boolean clusterID = false;
	    boolean grainOptim1 = false;
	    boolean grainOptim2 = false;
	    boolean grainEnable = true;
	    //boolean srcOptim = false;
	    boolean isLOOP1;
	    boolean isPipLoop;
	    
	    /** 0. LEVEL IDENTIFICATION **/
		for(PiGraph g : graph.getAllChildrenGraphs())
			g.setClusterValue(true);
		graph.setClusterValue(true);
	    int totalLevelCount = computeLevel();//compute the number of level of the input graph
	    if(totalLevelCount <levelNumber) 
	    	PreesmLogger.getLogger().log(Level.SEVERE, "the levelNumber: "+levelNumber+" to cluster should be less or equal to the total level of the input graph at least: "+totalLevelCount);
	    int haSCAPEDCount = computehaSCAPED(totalLevelCount);
	    brv = PiBRV.compute(graph, BRVMethod.LCM);
	    boolean pipProhibited = false;

for(PiGraph haSCAPED : this.levelSCAPED){//loop on identified entry level
	
	//init bool
	isLastCluster = false;
	clusterID = false;
	grainOptim1 = false;
	grainOptim2 = false;
	isLOOP1 = false;
	isPipLoop = false;
	levelCount  = haSCAPED.getAllChildrenGraphs().size()+1;
	    for (int indexLevel = 1; indexLevel <= levelCount; indexLevel++) {
	    	List<String[]> loopedBuffer = new ArrayList<>();
	    	Long scale = 1L;
	    	Map<AbstractVertex, Long> rv = new LinkedHashMap<>();
		    Map<AbstractVertex, Long> rv2 = new LinkedHashMap<>();
	    	clusterID = false;
	    	grainOptim1 = false;
	    	grainOptim2 = false;
	    	isPipLoop = false;
	    	
	    	if(indexLevel==levelCount) {
	    		isLastCluster = true;//prototype is different
	    		if(coreNumber==1)//pas sure de ça
	    			fullRaiserMode = true;
	    	}
	      /** 1.1 PATTERN IDENTIFICATION **/
	    	
	      PiGraph lastLevel = computeBottomLevel(haSCAPED, totalLevelCount);//retrieve the bottom level
	      upperLevel = graph;
	      if (lastLevel.getContainingPiGraph() != null) {//if not top Graph
	        upperLevel = lastLevel.getContainingPiGraph();
	      }
	      if(mode==1 &&!lastLevel.equals(haSCAPED)) {
	    	//create LOOP subgraphs 
				int isAddedLevel=lastLevel.getAllChildrenGraphs().size();
	    	  lastLevel = new ClusterPartitionerLOOP2(lastLevel, scenario).cluster();//LOOP2 transfo
				brv = PiBRV.compute(graph, BRVMethod.LCM);
				if(isAddedLevel!= lastLevel.getAllChildrenGraphs().size()) {//added level
					clusterID=true;
			    	  isLastCluster = false;
			    	  pipProhibited = true;
			    	  lastLevel = computeBottomLevel(haSCAPED, totalLevelCount);//retrieve the bottom level
				      upperLevel = lastLevel.getContainingPiGraph();
			      }
				//Or let it be
	      }

//If SCAPE1 and curent level is the parameter level or if SCAPE2 then identification of particular patterns
		if((mode==1 &&lastLevel.equals(haSCAPED)) ||mode==2){
			brv = PiBRV.compute(graph, BRVMethod.LCM);//test
			// Cluster input graph
			int isurcsize = lastLevel.getAllChildrenGraphs().size();
			
			//create LOOP subgraph
			if(!clusterID &&!pipProhibited)
				lastLevel = new ClusterPartitionerLOOP2(lastLevel, scenario).cluster();//LOOP2 transfo
			brv = PiBRV.compute(graph, BRVMethod.LCM);
			if(isurcsize!= lastLevel.getAllChildrenGraphs().size()) {//added level
				clusterID=true;
		    	  isLastCluster = false;
		    	  isPipLoop = true;
		    	  pipProhibited = true;
		    	  
		    	  //construct struct memcpy(in_buffer, out_buffer, size)
		    	  for(DataInputPort din : lastLevel.getChildrenGraphs().get(0).getDataInputPorts()) {
		    		  if(din.getFifo().isHasADelay()) {
		    			  String[] loop = {"","",""};
		    			  loop[0] = din.getName();
		    			  loop[1] =  din.getFifo().getSourcePort().getName();
		    			  loop[2] =  String.valueOf( din.getFifo().getDelay().getExpression().evaluate());
		    			  loopedBuffer.add(loop);
		    		  }
		    	  }
		    	  
		      }
			//create sequential subgraphs of uniform time
			if(!clusterID&&!pipProhibited)
				lastLevel = new ClusterPartitionerSEQ(lastLevel, scenario, coreNumber.intValue(),brv,clusterId,nonClusterableList,archi).cluster();//SEQ transfo
			brv = PiBRV.compute(graph, BRVMethod.LCM);
			if(isurcsize!= lastLevel.getAllChildrenGraphs().size()) {//added level
				clusterID=true;
		    	  isLastCluster = false;
		    	  pipProhibited = true;
		      }
			//create URC subgraph
			lastLevel = new ClusterPartitioner(lastLevel, scenario, coreNumber.intValue(),brv,clusterId,nonClusterableList).cluster();//URC transfo
			brv = PiBRV.compute(graph, BRVMethod.LCM);//test
			if(isurcsize!= lastLevel.getAllChildrenGraphs().size()) {//added level
				clusterID=true;
		    	  isLastCluster = false;
		      }
			//create SRV subgraph
			if(!clusterID)
				lastLevel = new ClusterPartitionerSRV(lastLevel, scenario, coreNumber.intValue(),brv,clusterId,nonClusterableList).cluster();//SRV transfo
			brv = PiBRV.compute(graph, BRVMethod.LCM);
			if(isurcsize!= lastLevel.getAllChildrenGraphs().size()) {//added level
				clusterID=true;
		    	  isLastCluster = false;
		      }
			//create GRAIN1 subgraph
			if(!clusterID&&!grainEnable)
				lastLevel = new ClusterPartitionerGRAIN(lastLevel, scenario, coreNumber.intValue(),brv,clusterId,nonClusterableList).cluster();//GRAIN1 transfo
			brv = PiBRV.compute(graph, BRVMethod.LCM);
			if(isurcsize!= lastLevel.getAllChildrenGraphs().size()) {//added level
				clusterID=true;
		    	  isLastCluster = false;
		      }
			//create GRAIN2 subgraph
			if(!clusterID&& !grainOptim1&&!grainEnable)
				lastLevel = new ClusterPartitionerGRAIN(lastLevel, scenario, coreNumber.intValue(),brv,clusterId,nonClusterableList).cluster2();//GRAIN2 transfo
			brv = PiBRV.compute(graph, BRVMethod.LCM);
			if(isurcsize!= lastLevel.getAllChildrenGraphs().size()) {//added level
				clusterID=true;
		    	  isLastCluster = false;
		      }

		    if(clusterID) { // if adding level
			    lastLevel = computeBottomLevel(haSCAPED, totalLevelCount);//retrieve the bottom level
			    upperLevel = graph;
			    if (lastLevel.getContainingPiGraph() != null) {//if not top Graph
			      upperLevel = lastLevel.getContainingPiGraph();
			    }
			    int indexA = 0;
			    Long clustBRVurc[] = new Long [lastLevel.getExecutableActors().size()];
			    for(AbstractActor a : lastLevel.getExecutableActors()) {
				    	  if(brv.containsKey(a)) {
				    		  clustBRVurc[indexA] = brv.get(a)*brv.get(lastLevel);
				    		indexA ++;
				    	  }
			    }
			    scale = gcdJustAboveCoreNumber(clustBRVurc,clustBRVurc.length, lastLevel, upperLevel, isPipLoop);
		    }
    	 }//end pattern ID step
		
	    //If all pattern are identified on corresponding level permit to check upper or stop iteration
	    if(!clusterID&&(mode==2||(mode==1&&lastLevel.equals(haSCAPED)))) {
	    	lastLevel.setClusterValue(false);
	    }else {
	    	clusterId++;
	    	indexLevel--;
	    }
if(lastLevel.isClusterValue()) {
	      	    brv = PiBRV.compute(graph, BRVMethod.LCM);

	      	  /** 1.2 BRV SCALING to ARCHITECTURE **/
	      	    
	      if(mode==2||(mode==1&&lastLevel.equals(haSCAPED))){
	    	  for(DataInputInterface din:lastLevel.getDataInputInterfaces()) {
	    		  if(!isLOOP1&& loopedBuffer.isEmpty())
	    			  din.getGraphPort().setExpression(din.getGraphPort().getExpression().evaluate()*brv.get(lastLevel)/scale);//scale hierarchical actor rate
	    		  else if(!((String[]) loopedBuffer.get(0))[0].equals(din.getGraphPort().getName()))
	    			  din.getGraphPort().setExpression(din.getGraphPort().getExpression().evaluate()*brv.get(lastLevel)/scale);//scale hierarchical actor rate
	  	    	
	    		  if(isLOOP1 &&!din.getGraphPort().getFifo().isHasADelay())
	    				  din.getGraphPort().setExpression(din.getGraphPort().getExpression().evaluate()*brv.get(lastLevel));
	    			  din.getDataPort().setExpression(din.getGraphPort().getExpression().evaluate());
	    	  }
		      for(DataOutputInterface dout:lastLevel.getDataOutputInterfaces()) {
		    	  
		    	  if(!isLOOP1&& loopedBuffer.isEmpty())
		    	  dout.getGraphPort().setExpression(dout.getGraphPort().getExpression().evaluate()*brv.get(lastLevel)/scale);//scale hierarchical actor rate
		    	  else if(!((String[]) loopedBuffer.get(0))[1].equals(dout.getGraphPort().getName()))
		    		  dout.getGraphPort().setExpression(dout.getGraphPort().getExpression().evaluate()*brv.get(lastLevel)/scale);
		    	  if(isLOOP1 &&!dout.getGraphPort().getFifo().isHasADelay())
		    		  dout.getGraphPort().setExpression(dout.getGraphPort().getExpression().evaluate()*brv.get(lastLevel));
		    	  if(dout.getGraphPort().getFifo().getTarget() instanceof SpecialActor)//adjust special actor rates
					  dout.getGraphPort().getFifo().getTargetPort().setExpression(dout.getGraphPort().getExpression().evaluate());
		    	  dout.getDataPort().setExpression(dout.getGraphPort().getExpression().evaluate());
		      }
	      brv = PiBRV.compute(graph, BRVMethod.LCM);//keep
	      }
	      if(mode==1||(!(!clusterID && !isLOOP1&&!grainOptim1&&!grainOptim2))){// si no urc added + param level = 1-> no cluster?
	      for(AbstractActor a : lastLevel.getActors())
	    	  if(brv.containsKey(a)) {
	    		  rv.put(a, brv.get(a));
	    		  rv2.put(a, brv.get(a));
	    	  }

	      /** 2. IBSDF TRANSFO **/
	      if(!isLOOP1)
	      checkIBSDF(lastLevel,rv, clusterID||grainOptim1||grainOptim2);
	      PiGraph copiedCluster = PiMMUserFactory.instance.copy(lastLevel);
	      copiedCluster.setClusterValue(true);//

	      /** 3. LAST LEVEL SCHEDULE **/

	      String schedulesMap = "";
	      schedulesMap = registerClusterSchedule(lastLevel, rv);
	      PreesmLogger.getLogger().log(Level.INFO, "cluster ("+lastLevel.getActorPath()+") :" + indexLevel +"/"+ levelCount + " schedule :" + schedulesMap);

	      final String message = "cluster with " + rv2.size() + " actor and "
	          + lastLevel.getAllFifos().size() + " fifo " + lastLevel.getAllParameters().size() + " parameter "
	          + lastLevel.getAllDependencies().size() + " dependency";
	      PreesmLogger.getLogger().log(Level.INFO, message); // cluster with 3 actor and 2 fifo

	      /** 4. LAST LEVEL SCENARIO **/
	      Scenario clusterScenario = lastLevelScenario(lastLevel);
	      // retrieve cluster timing
	      sumTiming = clusterTiming(brv, lastLevel, scenario);


	      /** 5.1 LAST LEVEL CODEGEN **/
	      // Generate intermediate model
	       final CodegenModelGeneratorClusteringRaiser generator = new CodegenModelGeneratorClusteringRaiser(archi,
	       clusterScenario, schedulesMap, stackSize);
	       // Retrieve the APOLLO flag
	        final String apolloFlag = "false";
	        final Collection<Block> codeBlocks = generator.generate(isLastCluster, brv,loopedBuffer);// 2
	        for(Block b : codeBlocks) {
	        	if(previousInitFunc!="")
	        		((ClusterRaiserBlock) b).setInitFunc(previousInitFunc);
	        	if(((ClusterRaiserBlock) b).getInitBuffers()!=null ||((ClusterRaiserBlock) b).getInitBlock()!=null) {
	        		previousInitFunc = b.getName();
	        	}
	        }
	        // Retrieve the desired printer and target folder path
	        final String selectedPrinter = "C";
	        final String clusterPath = scenario.getCodegenDirectory() + File.separator;
	        // Create the codegen engine
	        final CodegenEngine engine = new CodegenEngine(clusterPath, codeBlocks, copiedCluster, archi, clusterScenario);
	        if (ScapeTask.VALUE_PRINTER_IR.equals(selectedPrinter)) {
	          engine.initializePrinterIR(clusterPath);
	        }
	        engine.registerApollo(apolloFlag);
	        engine.registerPrintersAndBlocks(selectedPrinter);
	        engine.preprocessPrinters();
	        engine.printClusterRaiser();// 3

	        /** 6.1 LAST LEVEL REMOVE **/
	        for(AbstractActor a : lastLevel.getOnlyActors()) {// actors + spe + Din/out
	        	brv.remove(a);
	        }
	        Long value = brv.get(lastLevel);
	        brv.remove(lastLevel);
	        if(brv.size()==1)
	        	value = (long) 1;
	        remove(lastLevel,copiedCluster , upperLevel, isLastCluster, fullRaiserMode, value,isLOOP1, isPipLoop);
	        updateTiming(sumTiming,lastLevel);
	        PreesmLogger.getLogger().log(Level.INFO, "cluster code generated : " + clusterPath);

	      }
	    }// end loop
	}
	}

	    /** 7.OUTPUT TOPGRAPH **/

	    for(PiGraph g : graph.getAllChildrenGraphs())
	    for(Parameter p : g.getAllParameters()) {
	    	int k = p.getOutgoingDependencies().size()-1;
	    	for(int i = 0; i<p.getOutgoingDependencies().size();i++)
	    		if(p.getOutgoingDependencies().get(k-i).eContainer()== null) {
	    			p.getOutgoingDependencies().get(k-i).setContainingGraph(g);
	    		}
	    }

	    List<PiGraph> listSub = new ArrayList<>();
	    listSub.add(graph);
	    for(PiGraph g : graph.getAllChildrenGraphs())
	    	listSub.add(g);
	    for(PiGraph g : graph.getAllChildrenGraphs()) {
	    	for(Dependency d : g.getDependencies())
			   if( d.getGetter().eContainer() instanceof Delay)
				   if(!(listSub.contains(d.getGetter().eContainer().eContainer()))) {
						   g.removeDependency(d);
						   final ISetter setter = d.getSetter();
						      setter.getOutgoingDependencies().remove(d);
						      if (setter instanceof Parameter && setter.getOutgoingDependencies().isEmpty()
						              && !((Parameter) setter).isConfigurationInterface()) {
						            g.removeParameter((Parameter) setter);
						          }
				   }

	    }
	    int index = 0;
	    for(AbstractActor a: graph.getAllExecutableActors()) {
	    	if(a instanceof Actor)
	    	if (a.getName().contains("loop"))
	    		for(int i=0;i<a.getDataOutputPorts().size();i++)
	    			if(a.getDataOutputPorts().get(i).getFifo().getTarget() instanceof Actor)
	    			if(((AbstractActor) a.getDataOutputPorts().get(i).getFifo().getTarget()).getName().contains("loop")) {
	    				
	    				Delay d = PiMMUserFactory.instance.createDelay();
	    				d.setName(a.getName()+".out-"+((AbstractActor) a.getDataOutputPorts().get(i).getFifo().getTarget()).getName()+".in_"+index);
	    				d.setLevel(PersistenceLevel.PERMANENT);
	    				d.setExpression(brv.get(a)*a.getDataOutputPorts().get(i).getExpression().evaluate());
	    				d.setContainingGraph(a.getContainingGraph());
	    				a.getDataOutputPorts().get(i).getFifo().assignDelay(d);
	    				d.getActor().setContainingGraph(a.getContainingGraph());
	    				index++;
	    			}
	    				
	    }
	    final String message1 = "final Graph with " + graph.getAllExecutableActors().size() + " actor and "
	        + graph.getAllFifos().size() + " fifo " + graph.getAllParameters().size() + " parameter "
	        + graph.getAllDependencies().size() + " dependency";
	    PreesmLogger.getLogger().log(Level.INFO, message1); // cluster with 2 actor and 1 fifo

	    /** consistency check **/
	    for (PiGraph g : graph.getAllChildrenGraphs())
	        g.setClusterValue(false);
	    graph.setClusterValue(false);

	    final PiGraphConsistenceChecker pgcc = new PiGraphConsistenceChecker(CheckerErrorLevel.FATAL_ANALYSIS,
	            CheckerErrorLevel.NONE);
	        pgcc.check(graph);
	    PiGraph graphCopy = PiMMUserFactory.instance.copyPiGraphWithHistory(graph);
	    PiMMHelper.resolveAllParameters(graphCopy);
	    Map<AbstractVertex, Long> repetitionVector = PiBRV.compute(graph, BRVMethod.LCM);//test to comment

	    return graph;
	}



	  /**
	   * Unrolled LOOP pattern on the gcd above number of Processing element pipelined cluster.
	   * 
	   * @param oEmpty
	   *          loop element to be duplicate and pipelined
	   * @param value
	   *          highest divisor of brv(loop) just above the number of processing element
	   */
	private void PipLoopTransfo(AbstractActor oEmpty, Long value) {
		List<AbstractActor> dupActorsList = new LinkedList<>();	
		for(int i = 1;i< value;i++) {
			AbstractActor dupActor = PiMMUserFactory.instance.copy(oEmpty);
			dupActor.setName(oEmpty.getName()+"_"+i);
			dupActor.setContainingGraph(oEmpty.getContainingGraph());
			dupActorsList.add(dupActor);
		}
		for(DataInputPort in:oEmpty.getDataInputPorts()) {
			if(!in.getFifo().isHasADelay()) {
				ForkActor frk = PiMMUserFactory.instance.createForkActor();
				frk.setName("Fork_"+oEmpty.getName());
				frk.setContainingGraph(oEmpty.getContainingGraph());
				
				//connect din to frk
				DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
				din.setName("in");
				din.setExpression(in.getFifo().getSourcePort().getExpression().evaluate());
				
				frk.getDataInputPorts().add(din);
				Fifo fin = PiMMUserFactory.instance.createFifo();
				fin.setType(in.getFifo().getType());
				fin.setSourcePort(in.getFifo().getSourcePort());
				fin.setTargetPort(din);
				
				fin.setContainingGraph(oEmpty.getContainingGraph());
				
				//connect fork to oEmpty_0
				DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
				dout.setName("out_0");
				dout.setExpression(in.getFifo().getTargetPort().getExpression().evaluate());
				frk.getDataOutputPorts().add(dout);
				Fifo fout = PiMMUserFactory.instance.createFifo();
				fout.setType(in.getFifo().getType());
				fout.setSourcePort(dout);
				fout.setTargetPort(in);
				fout.setContainingGraph(oEmpty.getContainingGraph());
				// remove extra fifo --> non en fait c'est bon
				
				// connect fork to duplicated actors
				for(int i = 1; i < value;i++) {
					DataOutputPort doutn = PiMMUserFactory.instance.createDataOutputPort();
					doutn.setName("out_"+i);
					doutn.setExpression(in.getFifo().getTargetPort().getExpression().evaluate());
					frk.getDataOutputPorts().add(doutn);
					Fifo foutn = PiMMUserFactory.instance.createFifo();
					foutn.setType(in.getFifo().getType());
					foutn.setSourcePort(doutn);
					foutn.setContainingGraph(oEmpty.getContainingGraph());
				dupActorsList.get(i-1).getDataInputPorts().stream().filter(x->x.getName().equals(in.getName())).forEach(x -> x.setIncomingFifo(foutn));
				
				}
			}else {
				//if setter
				if(in.getFifo().getDelay().hasSetterActor()) {
					Fifo fd = PiMMUserFactory.instance.createFifo();
					fd.setSourcePort(in.getFifo().getDelay().getSetterPort());
					fd.setTargetPort(in);
					fd.setContainingGraph(oEmpty.getContainingGraph());
					
				}else {
					PreesmLogger.getLogger().log(Level.INFO, "unrolled loops requires setter and getter affected to a local delay");
					Actor set = PiMMUserFactory.instance.createActor();
					//InitActor set = PiMMUserFactory.instance.createInitActor();
					set.setName("setter");
					Refinement refinement = PiMMUserFactory.instance.createCHeaderRefinement();

		            set.setRefinement(refinement);
		            //((Actor) oEmpty).getRefinement().getFileName()
		            // Set the refinement
		            CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) (((Actor) set).getRefinement());
		            Prototype oEmptyPrototype = new Prototype();
		            oEmptyPrototype.setIsStandardC(true);
		            //String fileName = ;//lastLevel.getActorPath().replace("/", "_");
		            cHeaderRefinement.setFilePath(((Actor) oEmpty).getRefinement().getFilePath());
		            FunctionPrototype functionPrototype = PiMMUserFactory.instance.createFunctionPrototype();
		            cHeaderRefinement.setLoopPrototype(functionPrototype);
		            functionPrototype.setName(((Actor) oEmpty).getRefinement().getFileName());
					
					//set.setEndReference(oEmpty);
					set.setContainingGraph(oEmpty.getContainingGraph());
					DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
					set.getDataOutputPorts().add(dout);
					dout.setName("out");
					dout.setExpression(in.getFifo().getDelay().getExpression().evaluate());
					Fifo fd = PiMMUserFactory.instance.createFifo();
					fd.setSourcePort(set.getDataOutputPorts().get(0));
					fd.setTargetPort(in);
					fd.setContainingGraph(oEmpty.getContainingGraph());
				}
			}
		}
	
		for(DataOutputPort out:oEmpty.getDataOutputPorts()) {
			if(!out.getFifo().isHasADelay()) {
				JoinActor jn = PiMMUserFactory.instance.createJoinActor();
				jn.setName("Join_"+oEmpty.getName());
				jn.setContainingGraph(oEmpty.getContainingGraph());
				
				//connect Join to dout
				DataOutputPort dout = PiMMUserFactory.instance.createDataOutputPort();
				dout.setName("out");
				dout.setExpression(out.getFifo().getTargetPort().getExpression().evaluate());
				jn.getDataOutputPorts().add(dout);
				Fifo fout = PiMMUserFactory.instance.createFifo();
				fout.setSourcePort(dout);
				fout.setTargetPort(out.getFifo().getTargetPort());
				fout.setContainingGraph(oEmpty.getContainingGraph());
				
				//connect oEmpty_0 to Join
				DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
				din.setName("in_0");
				din.setExpression(out.getFifo().getSourcePort().getExpression().evaluate());
				jn.getDataInputPorts().add(din);
				Fifo fin = PiMMUserFactory.instance.createFifo();
				fin.setSourcePort(out);
				fin.setTargetPort(din);
				fin.setContainingGraph(oEmpty.getContainingGraph());
				// remove extra fifo --> non en fait c'est bon
				
				// connect duplicated actors to Join
				for(int i = 1; i < value;i++) {
					DataInputPort dinn = PiMMUserFactory.instance.createDataInputPort();
					dinn.setName("in_"+i);
					dinn.setExpression(out.getFifo().getSourcePort().getExpression().evaluate());
					jn.getDataInputPorts().add(dinn);
					Fifo finn = PiMMUserFactory.instance.createFifo();
					finn.setTargetPort(dinn);
					finn.setContainingGraph(oEmpty.getContainingGraph());
				dupActorsList.get(i-1).getDataOutputPorts().stream().filter(x->x.getName().equals(out.getName())).forEach(x -> x.setOutgoingFifo(finn));
				
				}
			}
		else {
			
			
		
			
			//if getter
			// connect last one to getter
			//Fifo fd = PiMMUserFactory.instance.createFifo();
			
			if(out.getFifo().getDelay().hasGetterActor()) {
				Fifo fdout = PiMMUserFactory.instance.createFifo();
				dupActorsList.get((int) (value-2)).getDataOutputPorts().stream().filter(x->x.getFifo()==null).forEach(x -> x.setOutgoingFifo(fdout));
				fdout.setTargetPort(out.getFifo().getDelay().getGetterPort());
				fdout.setContainingGraph(oEmpty.getContainingGraph());
				
			}else {
				PreesmLogger.getLogger().log(Level.INFO, "unrolled loops requires setter and getter affected to a local delay");
				//EndActor get = PiMMUserFactory.instance.createEndActor();
				Actor get = PiMMUserFactory.instance.createActor();
				get.setName("getter");
				///******
				Refinement refinement = PiMMUserFactory.instance.createCHeaderRefinement();

	            get.setRefinement(refinement);
	            //((Actor) oEmpty).getRefinement().getFileName()
	            // Set the refinement
	            CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) (((Actor) get).getRefinement());
	            Prototype oEmptyPrototype = new Prototype();
	            oEmptyPrototype.setIsStandardC(true);
	            //String fileName = ;//lastLevel.getActorPath().replace("/", "_");
	            cHeaderRefinement.setFilePath(((Actor) oEmpty).getRefinement().getFilePath());
	            FunctionPrototype functionPrototype = PiMMUserFactory.instance.createFunctionPrototype();
	            cHeaderRefinement.setLoopPrototype(functionPrototype);
	            functionPrototype.setName(((Actor) oEmpty).getRefinement().getFileName());/**/
				//get.setInitReference(dupActorsList.get((int) (value-2)));
	            
				get.setContainingGraph(oEmpty.getContainingGraph());
				DataInputPort din = PiMMUserFactory.instance.createDataInputPort();
				get.getDataInputPorts().add(din);
				din.setName("in");
				din.setExpression(out.getFifo().getDelay().getExpression().evaluate());
				Fifo fdout = PiMMUserFactory.instance.createFifo();
				dupActorsList.get((int) (value-2)).getDataOutputPorts().stream().filter(x->x.getFifo()==null).forEach(x -> x.setOutgoingFifo(fdout));
				fdout.setTargetPort(get.getDataInputPorts().get(0));
				fdout.setContainingGraph(oEmpty.getContainingGraph());
			}
			//connect oEmpty delayed output to 1st duplicated actor
			Fifo fdin = PiMMUserFactory.instance.createFifo();
			fdin.setSourcePort(out);
			dupActorsList.get(0).getDataInputPorts().stream().filter(x->x.getFifo()==null).forEach(x -> x.setIncomingFifo(fdin));
			fdin.setContainingGraph(oEmpty.getContainingGraph());
			
			
		}
		}
		// interconnect duplicated actor on their delayed port
		for(int i = 0;i<value-2;i++) {
		Fifo fd = PiMMUserFactory.instance.createFifo();
		dupActorsList.get(i).getDataOutputPorts().stream().filter(x->x.getFifo()==null).forEach(x -> x.setOutgoingFifo(fd));
		dupActorsList.get(i+1).getDataInputPorts().stream().filter(x->x.getFifo()==null).forEach(x -> x.setIncomingFifo(fd));
		fd.setContainingGraph(oEmpty.getContainingGraph());
		}
		//remove delay
		((PiGraph)oEmpty.getContainingGraph()).getDelays().stream().filter(x->x.getContainingFifo().getSourcePort()==null).forEach(x->((PiGraph)oEmpty.getContainingGraph()).removeDelay(x));
		//remove empty fifo
		((PiGraph)oEmpty.getContainingGraph()).getFifos().stream().filter(x->x.getSourcePort()==null).forEach(x->((PiGraph)oEmpty.getContainingGraph()).removeFifo(x));
		((PiGraph)oEmpty.getContainingGraph()).getFifos().stream().filter(x->x.getTargetPort()==null).forEach(x->((PiGraph)oEmpty.getContainingGraph()).removeFifo(x));
		
		int j=0;
	}
	/**
	   * Unrolled LOOP pattern on the gcd above number of Processing element pipelined cluster.
	   * 
	   * @param oEmpty
	   *          loop element to be duplicate and pipelined
	   * @param value
	   *          highest divisor of brv(loop) just above the number of processing element
	   *          @return bottomLevel
	   */
	private PiGraph computeBottomLevel(PiGraph haSCAPE, int totalLevelCount) {
		PiGraph bottomLevel = null;
		int countMax = 0;
	    int levelCountTemp = 1;
	    AbstractActor  bottomActor = null;

	    for(AbstractActor a : haSCAPE.getAllActors()) {
	    	PiGraph g = a.getContainingPiGraph();
	    	levelCountTemp = 1;
	    	if(g.isClusterValue()) {
	    while(g.getContainingPiGraph()!=null) {
	    	g = g.getContainingPiGraph();
	    	levelCountTemp++;
	    }
	    if(levelCountTemp>countMax) {
	    	bottomActor = a;
	    countMax = Math.max(countMax, levelCountTemp);
	    bottomLevel = a.getContainingPiGraph();
	    }
	    	}

	    		}

		return bottomLevel;
	}
	/**
	   * Compute level to be clustered
	   * 
	   * @param totalLevelCount
	   *          number of level in the input graph
	   * @return count
	   *          the number of subgraphs located at the level entered in the clustering task parameter
	   */
	private int computehaSCAPED(int totalLevelCount) {
	    int count = 0;
	    int levelCountTemp = 1;
	    int LastClusterLevelID=1;
	    if(mode==1){//IF SCAPE1 only level below the  level entered in input parameter are clustered
	    	LastClusterLevelID = this.levelNumber;

	    for(AbstractActor a : graph.getAllActors()) {
	    	PiGraph g = a.getContainingPiGraph();
	    	levelCountTemp = 1;
		    while(g.getContainingPiGraph()!=null) {
		    	g = g.getContainingPiGraph();
		    	levelCountTemp++;
		    }
		    
		    if(levelCountTemp == totalLevelCount-LastClusterLevelID+1&& !levelSCAPED.contains(a.getContainingGraph())) {
		    	count ++;
		    	this.levelSCAPED.add(a.getContainingPiGraph());
		    }
	    }}
	    else {//If SCAPE2 each level of the input graph are clustered
	    	count++;
	    	this.levelSCAPED.add(graph);
	    }
	    return count;
	}

	private void checkDelayHomogneousRate() {
		// TODO Auto-generated method stub
		for(PiGraph g : graph.getAllChildrenGraphs()) {
			for(Fifo df: g.getFifosWithDelay()) {
				//Broadcast
				if(df.getTargetPort().getExpression().evaluate()>df.getSourcePort().getExpression().evaluate()) {
					BroadcastActor brd = PiMMUserFactory.instance.createBroadcastActor();
			        brd.setName("BR_" + ((AbstractActor) df.getSource()).getName() + "_" + ((AbstractActor) df.getTarget()).getName());
			        DataOutputPort outputPort = PiMMUserFactory.instance.createDataOutputPort();
			        outputPort.setName("output_Brd");
			        brd.getDataOutputPorts().add(outputPort);
			        brd.getDataOutputPorts().get(0).setExpression(df.getTargetPort().getExpression().evaluate());

			        DataInputPort inputPort = PiMMUserFactory.instance.createDataInputPort();
			        inputPort.setName("input_Brd");
			        inputPort.setExpression(df.getSourcePort().getExpression().evaluate());
			        brd.getDataInputPorts().add(inputPort);

			        // connect

			        Fifo brdFifo = PiMMUserFactory.instance.createFifo();
			        brdFifo.setTargetPort(inputPort);
			        brdFifo.setSourcePort(df.getSourcePort());

			        brd.getDataOutputPorts().get(0).setOutgoingFifo(df);
			        brdFifo.setContainingGraph(g);
			        brd.setContainingGraph(g);

				}else if(df.getTargetPort().getExpression().evaluate()>df.getSourcePort().getExpression().evaluate()) {
					RoundBufferActor rnd = PiMMUserFactory.instance.createRoundBufferActor();
			        rnd.setName("RB_" + ((AbstractActor) df.getSource()).getName() + "_" + ((AbstractActor) df.getTarget()).getName());
			        DataOutputPort outputPort = PiMMUserFactory.instance.createDataOutputPort();
			        outputPort.setName("output_Rnd");
			        rnd.getDataOutputPorts().add(outputPort);
			        rnd.getDataOutputPorts().get(0).setExpression(df.getTargetPort().getExpression().evaluate());


			        DataInputPort inputPort = PiMMUserFactory.instance.createDataInputPort();
			        inputPort.setName("input_Rnd");
			        inputPort.setExpression(df.getSourcePort().getExpression().evaluate());
			        rnd.getDataInputPorts().add(inputPort);


			        // connect

			        Fifo rndFifo = PiMMUserFactory.instance.createFifo();
			        rndFifo.setTargetPort(inputPort);
			        rndFifo.setSourcePort(df.getSourcePort());

			        rnd.getDataOutputPorts().get(0).setOutgoingFifo(df);
			        rndFifo.setContainingGraph(g);
			        rnd.setContainingGraph(g);

				}
			}
		}
	}

	/**
	   * Used to compute optimal number of cluster to obtain the best resulting SrDAG from the returning PiGraph
	   *
	   * @param brv2
	   *          repetition vector of the input graph
	   * @param levelCount
	   *          number f hierarchical actor that can result in cluster
	   */
	/*private void computeClusterNumber(Map<AbstractVertex, Long> brv2, int levelCount) {
		Map<AbstractVertex, Long> fullBrv = brv2;
		Long fullSrDAG = (long) 0;
		Map<Long, Long> nbClusterToBrv = new LinkedHashMap<>();
		Long partSrDAG = (long) 0;
		Long maxValue = (long) 0;
		AbstractVertex maxVertex = null;
		for(Entry<AbstractVertex, Long> entry :fullBrv.entrySet()) {
			if(entry.getKey() instanceof Actor ||entry.getKey() instanceof SpecialActor)
			fullSrDAG = fullSrDAG + entry.getValue();
			if(entry.getValue() > maxValue) {
				maxValue = entry.getValue();
				maxVertex = entry.getKey();
			}
		}
		if(fullSrDAG < srdagMax )
			nbClusterToBrv.put((long) 0, fullSrDAG);
		maxVertex.getContainingPiGraph();
		PreesmLogger.getLogger().log(Level.INFO, "sum brv 0 : "+fullSrDAG);
		
			for(int i = 1; i<levelCount; i++) {
				for(Entry<AbstractVertex, Long> entry :fullBrv.entrySet()) {
					fullSrDAG = fullSrDAG + entry.getValue();
					if(entry.getValue() > maxValue) {
						maxValue = entry.getValue();
						maxVertex = entry.getKey();
					}
				}
				for(AbstractActor a :maxVertex.getContainingPiGraph().getActors()) {
					partSrDAG = partSrDAG + fullBrv.get(a);
					fullBrv.remove(a);
				}
				PreesmLogger.getLogger().log(Level.INFO, "sum brv"+i+" : "+partSrDAG);
				if(fullSrDAG < srdagMax )
					nbClusterToBrv.put((long) i, partSrDAG);
			}
		
		// retrieve best k
		float k = 1;
		int bestNbCluster = 0;
		for(Entry<Long, Long> entry : nbClusterToBrv.entrySet()) {
			float kbis = entry.getValue()%coreNumber;
			if(kbis < k) {
				k  = kbis;
				bestNbCluster = entry.getKey().intValue();
			}
		}
		clusterNumber = bestNbCluster;
	}*/
	  /**
	   * Used to store timing inside the cluster
	   *
	   * @param sumTiming
	   *          sum of contained actors timing
	   * @param lastLevel
	   *          PiGraph of the cluster
	   */
	private void updateTiming(Long sumTiming, PiGraph lastLevel) {

        AbstractActor aaa = null;
        if (sumTiming != null) {
          for (AbstractActor aa : graph.getAllActors()) {
            if (lastLevel.getName().equals(aa.getName())||aa.getName().contains(lastLevel.getName())) {
              aaa = aa;
              // update timing
              scenario.getTimings().setTiming(aaa, clusterComponent, TimingType.EXECUTION_TIME,
                  String.valueOf(sumTiming));

              //mapping
            //test working memory
//      	    AbstractVertex av = graph.lookupAllVertex(aaa.getName());
//      	    SDFAbstractVertex sav = new SDFVertex(av);
//      	    Mapping map = null ;
//      	    map.getMappings().put(aaa, null);
//      	    sav.getPropertyBean().setValue("working_memory", 12);
//      	    sav.getPropertyBean().getValue("working_memory");
      	    //sav.getGraphDescription()
            }
          }
        }

	}
	  /**
	   * Used to remove level from the input graph that have been code generated
	   *
	   * @param lastLevel
	   *          sum of contained actors timing
	   * @param upperLevel
	   *          PiGraph of the cluster
	   * @param isLastCluster
	   *          PiGraph of the cluster
	   * @param mode
	   *          PiGraph of the cluster
	   * @param value
	   *          PiGraph of the cluster
	 * @param isLOOP1 
	   */
	private void remove(PiGraph lastLevel, PiGraph copiedCluster, PiGraph upperLevel, boolean isLastCluster, boolean fullRaiserMode, Long value, boolean isLOOP1, boolean isPipLoop) {
		final String clusterPath = scenario.getCodegenDirectory() + File.separator;
		AbstractActor oEmpty = PiMMUserFactory.instance.createActor();

		oEmpty.setName(lastLevel.getName());
		upperLevel = lastLevel.getContainingPiGraph();
		List<Delay> identifiedDelays = new LinkedList<>();

		//AbstractActor a = null;

        for (AbstractActor a : upperLevel.getActors()) {
          if (a.getName().equals(lastLevel.getName())) {
        	  String listofNoDelaysArg ="";


            // Setup output of new actor + remove port of previous wrapped delay if persistence = none
            for (int i = 0; i < a.getDataInputPorts().size(); i++) {
            	boolean flag = false;
            	if(a.getDataInputPorts().get(i).getFifo().isHasADelay() && a.getDataInputPorts().get(i).getFifo().getSource()==a.getDataInputPorts().get(i).getFifo().getTarget())
            		if(a.getDataInputPorts().get(i).getFifo().getDelay().hasSetterActor()||a.getDataInputPorts().get(i).getFifo().getDelay().getLevel()!= PersistenceLevel.NONE)
            			flag = true;
            	if((isLOOP1 &&(!a.getDataInputPorts().get(i).getFifo().isHasADelay()||(flag)))||!isLOOP1) {
	              DataInputPort inputPort = PiMMUserFactory.instance.copy(a.getDataInputPorts().get(i));
	              oEmpty.getDataInputPorts().add(inputPort);
	              listofNoDelaysArg= listofNoDelaysArg+inputPort.getName();
            	}
            }
            for (int i = 0; i < a.getDataOutputPorts().size(); i++) {
            	boolean flag = false;
            	if(a.getDataOutputPorts().get(i).getFifo().isHasADelay() && a.getDataOutputPorts().get(i).getFifo().getSource()==a.getDataOutputPorts().get(i).getFifo().getTarget())
            		if(a.getDataOutputPorts().get(i).getFifo().getDelay().hasGetterActor()||a.getDataOutputPorts().get(i).getFifo().getDelay().getLevel()!= PersistenceLevel.NONE)
            			flag = true;
            	if((isLOOP1 &&(!a.getDataOutputPorts().get(i).getFifo().isHasADelay() || (flag)))||!isLOOP1) {
              DataOutputPort outputPort = PiMMUserFactory.instance.copy(a.getDataOutputPorts().get(i));
              oEmpty.getDataOutputPorts().add(outputPort);
              listofNoDelaysArg= listofNoDelaysArg+outputPort.getName();
              if(flag&&isLOOP1) {
            	  graph.removeDelay(a.getDataOutputPorts().get(i).getFifo().getDelay());
              	if(a.getDataOutputPorts().get(i).getFifo().getDelay().hasGetterActor())
              		identifiedDelays.add(a.getDataInputPorts().get(i).getFifo().getDelay());
              }
            	}
            }

            for (int i = 0; i < a.getConfigInputPorts().size(); i++) {
              ConfigInputPort cfgInputPort = PiMMUserFactory.instance.copy(a.getConfigInputPorts().get(i));
              oEmpty.getConfigInputPorts().add(cfgInputPort);

            }

            Refinement refinement = PiMMUserFactory.instance.createCHeaderRefinement();

            ((Actor) oEmpty).setRefinement(refinement);
            //((Actor) oEmpty).getRefinement().getFileName()
            // Set the refinement
            CHeaderRefinement cHeaderRefinement = (CHeaderRefinement) (((Actor) oEmpty).getRefinement());
            Prototype oEmptyPrototype = new Prototype();
            oEmptyPrototype.setIsStandardC(true);
            String fileName = lastLevel.getActorPath().replace("/", "_");
            cHeaderRefinement.setFilePath(clusterPath + "Cluster_" + fileName + ".h");
            FunctionPrototype functionPrototype = PiMMUserFactory.instance.createFunctionPrototype();
            cHeaderRefinement.setLoopPrototype(functionPrototype);
            functionPrototype.setName(fileName);
            int position = 0;
             if(isLastCluster && fullRaiserMode){//if full
	            for (int i = 0; i < this.scenario.getAlgorithm().getParameters().size(); i++) {
	                FunctionArgument functionArgument = PiMMUserFactory.instance.createFunctionArgument();
	                String str = copiedCluster.getActors().get(0).getConfigInputPorts().get(0).toString();

	                functionArgument.setType("int");
	                functionArgument.setName(this.scenario.getAlgorithm().getParameters().get(i).getName());
	                functionArgument.setDirection(Direction.IN);
	                functionArgument.setPosition(position);
	                functionArgument.setIsConfigurationParameter(true);
	                functionPrototype.getArguments().add(functionArgument);

	                position++;
	            }
              }else{
                  for (int i = 0; i < copiedCluster.getConfigInputInterfaces().size(); i++) {
                      FunctionArgument functionArgument = PiMMUserFactory.instance.createFunctionArgument();
                      // Parameter functionParameter = PiMMUserFactory.instance.createParameter();
                      //String str = copiedCluster.getActors().get(0).getConfigInputPorts().get(0).toString();

                      functionArgument.setType("int");
                      functionArgument.setName(copiedCluster.getConfigInputInterfaces().get(i).getName());
                      functionArgument.setDirection(Direction.IN);
                      functionArgument.setPosition(position);
                      functionArgument.setIsConfigurationParameter(true);
                      functionPrototype.getArguments().add(functionArgument);

                      position++;
                    }
            }
            for (int i = 0; i < copiedCluster.getDataInputInterfaces().size(); i++) {
            	//
            	if(listofNoDelaysArg.contains(copiedCluster.getDataInputInterfaces().get(i).getName())) {
              FunctionArgument functionArgument = PiMMUserFactory.instance.createFunctionArgument();
              String str = copiedCluster.getDataInputInterfaces().get(i).getDataOutputPorts().get(0).getOutgoingFifo()
                  .getType();
              functionArgument.setType(str);
              functionArgument.setName(copiedCluster.getDataInputInterfaces().get(i).getName());
              functionArgument.setDirection(Direction.IN);
              functionArgument.setPosition(position);
              functionPrototype.getArguments().add(functionArgument);
              position++;}
            }

            for (int i = 0; i < copiedCluster.getDataOutputInterfaces().size(); i++) {
            	if(listofNoDelaysArg.contains(copiedCluster.getDataOutputInterfaces().get(i).getName())) {
              FunctionArgument functionArgument = PiMMUserFactory.instance.createFunctionArgument();
              String str = copiedCluster.getDataOutputInterfaces().get(i).getDataInputPorts().get(0).getIncomingFifo()
                  .getType();
              functionArgument.setType(str);
              functionArgument.setName(copiedCluster.getDataOutputInterfaces().get(i).getName());
              functionArgument.setDirection(Direction.OUT);
              functionArgument.setPosition(position);
              functionPrototype.getArguments().add(functionArgument);
            	}
            }

            // replace
            if(upperLevel==graph)
            	clusterIndex = 0;
            else if (a.getDirectPredecessors().isEmpty())
            	clusterIndex = 0;
            else
            	clusterIndex = upperLevel.getActors().indexOf(a.getDirectPredecessors().get(0)) +1;//index
            //if local delay & no setter --> remove port
            int dataInSize = a.getDataInputPorts().size()-1;
            for(int i=0; i<=dataInSize;i++) {
            	DataInputPort din = a.getDataInputPorts().get(dataInSize-i);
            	if(din.getFifo().isHasADelay())
            		if(din.getFifo().getDelay().hasSetterActor()&&din.getFifo().getDelay().getLevel()== PersistenceLevel.NONE&&isLOOP1) {
            			//connect previous delayed port to new interface
            			Delay d = din.getFifo().getDelay();
            			if(upperLevel!=graph&&!(din.getFifo().getDelay().getSetterActor() instanceof DataInputInterface)) {
            			
            			DataInputInterface newDin = PiMMUserFactory.instance.createDataInputInterface();
            			newDin.setName(din.getName() + "_in");
            			newDin.getDataOutputPorts().get(0).setName(din.getName() + "_in");
            			newDin.getDataOutputPorts().get(0).setExpression(din.getFifo().getTargetPort().getExpression().evaluate());
            			Fifo f = PiMMUserFactory.instance.createFifo();
                        f.setType(din.getFifo().getType());
                        f.setSourcePort(newDin.getDataOutputPorts().get(0));
                        f.setTargetPort(din);
                        f.setContainingGraph(upperLevel);
                        
            			//connect interface to setter
                        newDin.setGraphPort(d.getSetterPort().getOutgoingFifo().getTargetPort());
                        
                        newDin.setContainingGraph(upperLevel);
                        newDin.getGraphPort().setExpression(din.getFifo().getTargetPort().getExpression().evaluate());
                        d.getSetterActor().setContainingGraph(upperLevel.getContainingPiGraph());
                        Fifo fp = PiMMUserFactory.instance.createFifo();
                        fp.setType(din.getFifo().getType());
                        fp.setSourcePort(d.getSetterPort());
                        fp.setTargetPort((DataInputPort) newDin.getGraphPort());
                        fp.setContainingGraph(upperLevel.getContainingPiGraph());
            			}else {
            				//connect din to setter
            				Fifo fp = PiMMUserFactory.instance.createFifo();
                            fp.setType(din.getFifo().getType());
                            fp.setSourcePort(d.getSetterPort());
                            fp.setTargetPort(din);
                            fp.setContainingGraph(upperLevel);
            			}

            		}
            		else if(din.getFifo().getDelay().getLevel()== PersistenceLevel.NONE&&isLOOP1) {
            			//Delay d = din.getFifo().getDelay();
            			a.getDataInputPorts().remove(din);
            			//upperLevel.removeDelay(d);
            		}
            		else if(din.getFifo().getDelay().getLevel()== PersistenceLevel.LOCAL&&isLOOP1)
            			din.getFifo().getDelay().setLevel(PersistenceLevel.NONE);
            }
            int dataOutSize = a.getDataOutputPorts().size()-1;
            for(int i=0; i<=dataOutSize;i++) {
            	DataOutputPort dout = a.getDataOutputPorts().get(dataOutSize-i);
            	if(dout.getFifo().isHasADelay())
            		if(dout.getFifo().getDelay().hasGetterActor()&&dout.getFifo().getDelay().getLevel()== PersistenceLevel.NONE&&isLOOP1) {
            			//connect previous delayed port to new interface
            			Delay d = dout.getFifo().getDelay();
            			if(!upperLevel.equals(graph)&&!(dout.getFifo().getDelay().getGetterActor() instanceof DataOutputInterface)) {
            			DataOutputInterface newDout = PiMMUserFactory.instance.createDataOutputInterface();
            			newDout.setName(dout.getName() + "_out");
            			newDout.getDataInputPorts().get(0).setName(dout.getName() + "_out");
            			newDout.getDataInputPorts().get(0).setExpression(dout.getFifo().getSourcePort().getExpression().evaluate());
            			Fifo f = PiMMUserFactory.instance.createFifo();
                        f.setType(dout.getFifo().getType());
                        f.setSourcePort(dout);
                        f.setTargetPort(newDout.getDataInputPorts().get(0));
                        f.setContainingGraph(upperLevel);
                        
            			//connect interface to getter
                        newDout.setGraphPort(d.getGetterPort().getIncomingFifo().getSourcePort());
                        
                        newDout.setContainingGraph(upperLevel);
                        newDout.getGraphPort().setExpression(dout.getFifo().getSourcePort().getExpression().evaluate());
                        d.getGetterActor().setContainingGraph(upperLevel.getContainingPiGraph());
                        
                        Fifo fp = PiMMUserFactory.instance.createFifo();
                        fp.setType(dout.getFifo().getType());
                        fp.setSourcePort((DataOutputPort) newDout.getGraphPort());
                        fp.setTargetPort(d.getGetterPort());
                        fp.setContainingGraph(upperLevel.getContainingPiGraph());
            			}else {
            				//connect din to setter
            				Fifo fp = PiMMUserFactory.instance.createFifo();
                            fp.setType(dout.getFifo().getType());
                            fp.setSourcePort(dout);
                            fp.setTargetPort(d.getGetterPort());
                            fp.setContainingGraph(upperLevel);
            			}
                        upperLevel.removeFifo(d.getContainingFifo());
            			upperLevel.removeDelay(d);
            		}
            		else if(dout.getFifo().getDelay().getLevel()== PersistenceLevel.NONE&&isLOOP1) {
            			Delay d = dout.getFifo().getDelay();
            			a.getDataOutputPorts().remove(dout);
            			upperLevel.removeFifo(d.getContainingFifo());
            			upperLevel.removeDelay(d);
            		}

            }

            upperLevel.replaceActor(a, oEmpty);

            int i=0;
            for(Fifo f: upperLevel.getFifos()) {
    	    	if(f.getSourcePort()==null)
    	    		upperLevel.removeFifo(f);
    	    	if(f.getTargetPort()==null)
    	    		upperLevel.removeFifo(f);
    	    }
          }

        }
        if (isLastCluster && fullRaiserMode) {
          // clear graph expect smth
          for (AbstractActor aa : upperLevel.getActors()) {
            upperLevel.removeActor(aa);
          }
          for (Dependency aa : upperLevel.getDependencies()) {
        	  if(!(aa.getTarget() instanceof Parameter))
        			  upperLevel.removeDependency(aa);
          }
          for (Fifo aa : upperLevel.getFifos()) {
            upperLevel.removeFifo(aa);
          }
          for (Parameter aa : upperLevel.getParameters()) {
            int size = aa.getOutgoingDependencies().size();

            for (int i = 1; i <= size; i++) {
              int k = size - i;
              if(!(aa.getOutgoingDependencies().get(k).getTarget() instanceof Parameter))
            	  aa.getOutgoingDependencies().remove(k);
            }

          }
//          for(int i = 0; i < oEmpty.getConfigInputPorts().size(); i++) {
//        	  oEmpty.getConfigInputPorts().remove(i);
//          }

          // create empty actor

          oEmpty.setName(upperLevel.getName());
          // connect empty actor
          for (int i = 0; i < upperLevel.getParameters().size(); i++) {
            ConfigInputPort cfgInputPort = PiMMUserFactory.instance.createConfigInputPort();
            cfgInputPort.setName(upperLevel.getParameters().get(i).getName());
            oEmpty.getConfigInputPorts().add(cfgInputPort);
            Dependency dependency = PiMMUserFactory.instance.createDependency();
            dependency.setGetter(cfgInputPort);
            dependency.setSetter(upperLevel.getParameters().get(i));
            dependency.setContainingGraph(upperLevel);

          }
          // Set the refinement
          CHeaderRefinement cHeaderRefinement = PiMMUserFactory.instance.createCHeaderRefinement();
          String fileName = lastLevel.getActorPath().replace("/", "_");
          cHeaderRefinement.setFilePath(clusterPath + "Cluster_" + fileName + ".h");
          //cHeaderRefinement.setFilePath(clusterPath + "Cluster_" + lastLevel.getName() + ".h");
          FunctionPrototype functionPrototype = PiMMUserFactory.instance.createFunctionPrototype();
          cHeaderRefinement.setLoopPrototype(functionPrototype);
          functionPrototype.setName(fileName);
          //functionPrototype.setName(lastLevel.getName());
          int position = 0;
          for (int i = 0; i < copiedCluster.getAllParameters().size(); i++) {
            FunctionArgument functionArgument = PiMMUserFactory.instance.createFunctionArgument();
            // Parameter functionParameter = PiMMUserFactory.instance.createParameter();
            String str = copiedCluster.getActors().get(0).getConfigInputPorts().get(0).toString();

            functionArgument.setType("int");
            functionArgument.setName(copiedCluster.getAllParameters().get(i).getName());
            functionArgument.setDirection(Direction.IN);
            functionArgument.setPosition(position);
            functionArgument.setIsConfigurationParameter(true);
            functionPrototype.getArguments().add(functionArgument);

            position++;
          }
          ((Actor) oEmpty).setRefinement(cHeaderRefinement);

          upperLevel.addActor(oEmpty);

        }
        brv.put(oEmpty, value);
      //duplicate instance if loop
		 if(isPipLoop) {
			 PipLoopTransfo(oEmpty, value);
		 }

	}
	  /**
	   * Used to compute timing inside the cluster
	   *
	   * @param repetitionVector
	   *          list of the cluster repetition vector
	   * @param copiedCluster
	   *          PiGraph of the cluster
	   * @param scenario2
	   *          contains list of timing
	   */
	private Long clusterTiming(Map<AbstractVertex, Long> repetitionVector, PiGraph copiedCluster, Scenario scenario2) {
	    long sumTiming = (long) 0;
	    Map<AbstractActor, Map<Component, String>> clusterTiming = new LinkedHashMap<>();
	    Component clusterComponent = null;
	    if (!scenario2.getTimings().getActorTimings().isEmpty()) {

	      for (AbstractActor a : copiedCluster.getAllActors()) {
	        if (a instanceof Actor) {

	          AbstractActor aaa = null;
	          for (AbstractActor aa : scenario.getTimings().getActorTimings().keySet()) {
	            if (a.getName().equals(aa.getName())) {
	              aaa = aa;
	            }
	          }
	          if(aaa != null) {
	        	  String time = scenario.getTimings().getActorTimings().get(aaa).get(0).getValue().get(TimingType.EXECUTION_TIME);
	        	  Long brv = repetitionVector.get(a);//aaa
	        	  if(brv == null)
	        		  brv = (long) 1;
	        	  sumTiming = sumTiming + (Long.valueOf(time)*brv);

	        	  clusterComponent = scenario.getTimings().getActorTimings().get(aaa).get(0).getKey();
		            this.clusterComponent = clusterComponent;
		          //}
	          }else {
	        	  this.clusterComponent = archi.getComponentHolder().getComponents().get(0);//temp
	        	  String time = "100";
	        	  Long brv = repetitionVector.get(a);
	        	  sumTiming = sumTiming + (Long.valueOf(time)*brv);
	          }
	        }
	        if (a instanceof SpecialActor) {
	          Long brv = repetitionVector.get(a);
	          sumTiming = sumTiming + scenario.getTimings().evaluateExecutionTimeOrDefault(a, clusterComponent) * brv;
	        }
	      }
	    }else {
	    	for (AbstractActor a : copiedCluster.getAllActors())
		        if (a instanceof Actor) {
		        	this.clusterComponent = archi.getComponentHolder().getComponents().get(0);//temp
		        	  String time = "100";
		        	  Long brv = repetitionVector.get(a);
		        	  sumTiming = sumTiming + (Long.valueOf(time)*brv);
		        }
	    }
	    return sumTiming;
	  }
	  /**
	   * Used to create a temporary scenario of the cluster
	   *
	   * @param copiedCluster
	   *          PiGraph of the cluster
	   */
	  private Scenario lastLevelScenario(PiGraph copiedCluster) {
	    Scenario clusterScenario = ScenarioUserFactory.createScenario();

	    clusterScenario.setAlgorithm(copiedCluster);
	    clusterScenario.setDesign(archi);
	    clusterScenario.setTimings(ScenarioFactory.eINSTANCE.createTimings());
	    clusterScenario.setEnergyConfig(ScenarioFactory.eINSTANCE.createEnergyConfig());

	    final List<ComponentInstance> coreIds = new ArrayList<>(archi.getOperatorComponentInstances());
	    // for all different type of cores, allow mapping on it
	    for (final ComponentInstance coreId : coreIds) {
	      for (final AbstractActor actor : copiedCluster.getAllActors()) {
	        // Add constraint
	        clusterScenario.getConstraints().addConstraint(coreId, actor);

	      }
	    }
	    clusterScenario.getSimulationInfo().getDataTypes().map().put("uchar", (long) 8);
	    clusterScenario.getSimulationInfo().getDataTypes().map().put("char", (long) 8);
	    clusterScenario.getSimulationInfo().getDataTypes().map().put("unsigned char", (long) 8);
	    clusterScenario.getSimulationInfo().getDataTypes().map().put("int", (long) 32);
	    clusterScenario.getSimulationInfo().getDataTypes().map().put("float", (long) 32);
	    clusterScenario.getSimulationInfo().getDataTypes().map().put("double", (long) 64);
	    clusterScenario.getSimulationInfo().getDataTypes().map().put("SiftKpt", (long) 4448);
	    clusterScenario.getSimulationInfo().getDataTypes().map().put("coord", (long) 64);
	    clusterScenario.getSimulationInfo().getDataTypes().map().put("coordf", (long) 64);
	    return clusterScenario;
	  }
	  /**
	   * Used to compute the schedule of the cluster with APGAN method
	   *
	   * @param graph
	   *          PiGraph of the cluster
	   * @param rv
	   *          List of repetition vector
	   */
	  private String registerClusterSchedule(PiGraph graph, Map<AbstractVertex, Long> rv) {
		  // Schedule subgraph
		    //checkSchedulability(graph);

		    String result = "";
		    List<AbstractActor> actorList = graph.getAllExecutableActors();

		    if(rv.size()==1) {
		    	result = rv.get(actorList.get(0)) +"("+ actorList.get(0).getName()+")";
		    	return result;

		    }
		    // Compute BRV

		    Map<AbstractVertex, Long> repetitionVector = rv;

		    // compute repetition count (gcd(q(a),q(b)))
		    Map<AbstractVertex, Map<AbstractVertex, Long>> repetitionCount = new LinkedHashMap<>();

		    for (AbstractActor element : graph.getAllExecutableActors()) {

		      for (DataOutputPort element2 : element.getDataOutputPorts()) {// for each fifo
		        if (!(element2.getFifo()
		            .getTarget() instanceof DataOutputInterface)&& !element2.getFifo().isHasADelay()&& repetitionVector.containsKey(element) && repetitionVector.containsKey(element2.getFifo().getTarget())) {
		          Long rep = gcd(repetitionVector.get(element), repetitionVector
		              .get(element2.getFifo().getTarget()));

		          Map<AbstractVertex, Long> map = new LinkedHashMap<>();
		          map.put(
		              (AbstractVertex) element2.getFifo().getTarget(),
		              rep);
		          if (!repetitionCount.containsKey(element)) {
		        	  if(!element2.getFifo().isHasADelay())
		        		  repetitionCount.put(element, map);
		          } else {
		        	  if(!element2.getFifo().isHasADelay())
			            repetitionCount.get(element).put((AbstractVertex) element2.getFifo().getTarget(), rep);
		          }
		        }
		      }
		    }

		  //find  actors in order of succession

		      int totalsize = graph.getAllExecutableActors().size();//5
		      List<AbstractActor> actorListOrdered = new ArrayList<>();
		      int flag = 0;
		      if(!graph.getDataInputInterfaces().isEmpty()) {
	    	  for(int i = 0; i<graph.getActors().size();i++)
	    		  if(graph.getActors().get(i) instanceof DataInputInterface) {
		    		  flag = 0;
		    		  AbstractActor curentActor = (AbstractActor) graph.getActors().get(i).getDataOutputPorts().get(0).getFifo().getTarget();
		    		  if(!actorListOrdered.contains(curentActor)) {

		    			  for(int iii=0;iii<curentActor.getDataInputPorts().size();iii++)
	    					  if(actorListOrdered.contains(curentActor.getDataInputPorts().get(iii).getFifo().getSource())||curentActor.getDataInputPorts().get(iii).getFifo().getSource() instanceof DataInputInterface ||curentActor.getDataInputPorts().get(iii).getFifo().isHasADelay()) {
	    						  flag++;
	    					  }
	    				  if(flag==curentActor.getDataInputPorts().size()) {

				    		  actorListOrdered.add(curentActor);// fill a
				    		  totalsize--;//4
	    				  }
		    		  }
		    	  }
	    	  for(AbstractActor a: actorList) {
	    		  flag = 0;
	    		  if(a.getDataInputPorts().isEmpty()) {
	    			  if(!actorListOrdered.contains(a)) {//s'il ne contient pas déja l'acteur
	    				  for(int i=0;i<a.getDataInputPorts().size();i++)
	    					  if(actorListOrdered.contains(a.getDataInputPorts().get(i).getFifo().getSource())) {
	    						  flag++;
	    					  }
	    				  if(flag==a.getDataInputPorts().size()) {
	    					  actorListOrdered.add(a);
	    						  totalsize--;
	    				  }
	    			  }
	    		  }
	    	  }
		      }else {
		    	  for(AbstractActor a: actorList) {
		    		  if(a.getDataInputPorts().isEmpty()) {
		    			  if(!actorListOrdered.contains(a)) {
		    			  actorListOrdered.add(a);
		    			  totalsize--;
		    			  }
		    		  }
		    	  }
		      }
		      int curentSize = actorListOrdered.size();//1

		      for(int i = 0; i< curentSize;i++)

		    	  for(int ii = 0; ii< actorListOrdered.get(i).getDataOutputPorts().size();ii++)
		    	  {
		    		  flag = 0;
		    		  AbstractActor precActor = (AbstractActor) actorListOrdered.get(i).getDataOutputPorts().get(ii).getFifo().getTarget();
		    		  if(!actorListOrdered.contains(precActor)) {
		    			  for(int iii=0;iii<precActor.getDataInputPorts().size();iii++)
	    					  if(actorListOrdered.contains(precActor.getDataInputPorts().get(iii).getFifo().getSource()) ||precActor.getDataInputPorts().get(iii).getFifo().getSource() instanceof DataInputInterface ||precActor.getDataInputPorts().get(iii).getFifo().isHasADelay()) {// if contains predecessors
	    						  flag ++;
	    						 }
		    			  if(flag==((AbstractActor) actorListOrdered.get(i).getDataOutputPorts().get(ii).getFifo().getTarget()).getDataInputPorts().size()) {
	    				  	actorListOrdered.add((AbstractActor) actorListOrdered.get(i).getDataOutputPorts().get(ii).getFifo().getTarget());//fill b
						    totalsize--;//3
		    			  }

		    		  }

		    	  }
		    flag = 0;
		      int precSize = curentSize;
		      curentSize = actorListOrdered.size();//2
		      while(totalsize>0) {
		    	  for(int i = precSize; i< curentSize;i++)
			    	  for(int ii = 0; ii< actorListOrdered.get(i).getDataOutputPorts().size();ii++) {
			    		  flag = 0;
			    		  AbstractActor curentActor = (AbstractActor) actorListOrdered.get(i).getDataOutputPorts().get(ii).getFifo().getTarget();
			    		  if(!actorListOrdered.contains(curentActor)) {
			    			  for(int iii=0;iii<curentActor.getDataInputPorts().size();iii++) {
			    				  AbstractActor precActor = (AbstractActor) curentActor.getDataInputPorts().get(iii).getFifo().getSource();
		    					  if(actorListOrdered.contains(precActor)||precActor instanceof DataInputInterface||curentActor.getDataInputPorts().get(iii).getFifo().isHasADelay()) {// if contains predecessors
		    						  flag++;
		    						  }
			    			  }
			    			  if(flag==curentActor.getDataInputPorts().size()) {
						    		  actorListOrdered.add((AbstractActor) actorListOrdered.get(i).getDataOutputPorts().get(ii).getFifo().getTarget());//fill c
						    		  totalsize--;//2
						    		  }

			    		  }
			    	  }
		    	  precSize = curentSize;
		    	  curentSize = actorListOrdered.size();//3++
		      }
		      actorList = actorListOrdered;
		      Long lastIndex = (long) 0;
		      if(!parallelCluster) {
		    	  AbstractActor maxLeft = null;
			      AbstractActor maxRight = null;
			      Long max = (long) 0;
			    while (repetitionVector.size() > 1) {
	              boolean removeRight = false;

			      //find pair with the biggest repetition count

			      if (result == "") {
			    	   maxLeft = null;
				       maxRight = null;
				       max = (long) 0;
			      for (int i = 0; i < actorList.size(); i++) {
			    	  int rang = actorList.size()-i-1;

			        if (repetitionCount.containsKey(actorList.get(rang))) {
			          for (AbstractVertex a : repetitionCount.get(actorList.get(rang)).keySet()) {
			            if (repetitionCount.get(actorList.get(rang)).get(a) > max) {

			              max = repetitionCount.get(actorList.get(rang)).get(a);
			              maxLeft = actorList.get(rang);
			              maxRight = (AbstractActor) a;

			            }
			          }
			        }
			      }

			      }
			      if(repetitionCount.isEmpty()) {
			    	  for (int i = 0; i < actorList.size(); i++) {
			    		  int rang = actorList.size()-i-1;
			    		  if(actorList.get(rang) instanceof Actor||actorList.get(rang) instanceof SpecialActor)
			    		  result = result+"*"+actorList.get(rang).getName();
			    	  }
			    	  return result;
			      }
			      if(repetitionVector.get(maxLeft)==null) {
			    	  boolean hasPredecessor = false;
			    	  for (int i = 0; i < actorList.size(); i++) {
				    	  int rang = actorList.size()-i-1;
				        if (repetitionCount.containsKey(actorList.get(rang))) {//if maxRight to right
				        	if (repetitionCount.get(actorList.get(rang)).containsKey(maxRight)&&!hasPredecessor) {
				              max = repetitionCount.get(actorList.get(rang)).get(maxRight);
				              maxLeft = actorList.get(rang);
				              //maxRight =  maxRight;
				              hasPredecessor = true;

				            }

				        }
				        }
			    	  if(!hasPredecessor)
						for (AbstractActor element : actorList) {
			    			  //int rang = actorList.size()-i-1;

				        if(repetitionCount.containsKey(maxRight)) {//if maxRight to left
				        	if (repetitionCount.get(maxRight).containsKey(element)&&!hasPredecessor) {
					              max = repetitionCount.get(maxRight).get(element);
					              maxLeft = maxRight;
					              maxRight = element;
					              hasPredecessor = true;
					            }
				        }
			    		  }
			      }else if(repetitionVector.get(maxRight)==null) {
			    	  boolean hasSuccessor = false;
			    	  for (int i = 0; i < actorList.size(); i++) {
				    	  int rang = actorList.size()-i-1;
				        if (repetitionCount.containsKey(actorList.get(rang))) { //if maxLeft to right
				        	if (repetitionCount.get(actorList.get(rang)).containsKey(maxLeft)&&!hasSuccessor) {
				              max = repetitionCount.get(actorList.get(rang)).get(maxLeft);
				              maxRight = maxLeft;
				              maxLeft = actorList.get(rang);

				              hasSuccessor = true;
				            }

				        }}
			    	  if(!hasSuccessor)
			    	  for (int i = 0; i < actorList.size(); i++) {
			    		  int rang = actorList.size()-i-1;
				        if (repetitionCount.containsKey(maxLeft)) { //if maxLeft to left
				        	if (repetitionCount.get(maxLeft).containsKey(actorList.get(i))&&!hasSuccessor) {
					              max = repetitionCount.get(maxLeft).get(actorList.get(i));
					              maxRight = actorList.get(i);
					              //maxLeft = maxLeft;
					              hasSuccessor = true;
					            }

					        }
				      }
			      }
			      // compute String schedule
			      if (result == "") {
			        if (max == 1) {
			          result = maxLeft.getName() + "*" + maxRight.getName();
			        } else {
			          result = String.valueOf(max) + "(" + maxLeft.getName() + "*" + maxRight.getName() + ")";
			        }
			        lastIndex = max;
			        if (maxRight.getDataInputPorts().size() > maxLeft.getDataOutputPorts().size())
			        	removeRight = false;
			        else
			        	removeRight = true;
			      } else {
			    	  if(repetitionVector.get(maxRight)==null ||repetitionVector.get(maxLeft)==null) {// si bout de queue
			    		  for(AbstractVertex a: repetitionVector.keySet())
			    			  if(!result.contains(a.getName()))
			    				  result = result +"*"+a.getName();
			    		  return result;
			    	  }
			        if (repetitionVector.get(maxRight)==1 && repetitionVector.get(maxLeft)==1 ) {
			        	//if rv = 1*
			        	if(maxRight !=null)
			        		if(!result.contains(maxRight.getName())){//si g avant d
			        			result = result  + "*" +maxRight.getName() ;
			        			removeRight = true;
			        		}else
			        			result = maxLeft.getName()  + "*" + result ;
			        		 lastIndex = (long) 1;

			        } else if (repetitionVector.get(maxRight)>1 || repetitionVector.get(maxLeft)>1){
			        	// if same loop
			        	if(!result.contains(maxRight.getName())) { //ajout de maxRight
			        		//add loop
				        	if(repetitionVector.get(maxRight)==lastIndex) {
				        		if(repetitionVector.get(maxRight)>1) {
						        	if(result.contains(repetitionVector.get(maxRight)+"(")&& result.indexOf(String.valueOf(repetitionVector.get(maxRight)))==0) {
						        		String temp = result.replace(repetitionVector.get(maxRight)+"(", "");

						        		result = temp.replaceFirst("\\)", "");
						        		result = repetitionVector.get(maxRight)+"("+result  + "*" +maxRight.getName()+")" ;
						        	}else if(result.contains(repetitionVector.get(maxRight)+"(")&& result.indexOf(String.valueOf(repetitionVector.get(maxRight)))!=0){
						        		char[] temp = result.toCharArray();
						        		String tempi = "";
						        		for(int i = 0; i<temp.length-2;i++) {
						        			tempi = tempi+temp[i];
						        		}
						        		result = tempi+ "*" +maxRight.getName()+")" ;
						        	}

					        	lastIndex =repetitionVector.get(maxRight);

				        	}else {
				        		result = result  + "*" +maxRight.getName() ;
				        		lastIndex = (long) 1;
				        	}
				        	}else {
				        		// add into prec loop
				        		if(Long.valueOf(result.charAt(result.lastIndexOf("(")-1)).equals(repetitionVector.get(maxRight))) {// si la derniere loop est egale au rv
			        				result.replace(repetitionVector.get(maxRight)+"(", "");

			        			}else {
			        				//result =  result +  ;
			        			}
			        			result = result +"*"+repetitionVector.get(maxRight)+"("+maxRight.getName()+")";
			        			lastIndex =repetitionVector.get(maxRight);
				        	}
			        		removeRight = true;
			        	}else if(!result.contains(maxLeft.getName())) { //ajout de maxLeft
				        	if(repetitionVector.get(maxLeft)==lastIndex) {
				        		if(repetitionVector.get(maxLeft)>1) {
					        	if(result.contains(repetitionVector.get(maxLeft)+"(")) {
					        		String temp = result.replace(repetitionVector.get(maxLeft)+"(", "");
					        		result = temp;//.replaceFirst("\\)", "");
					        	}
					        	result = repetitionVector.get(maxLeft)+"("+maxLeft.getName()  + "*" + result ;
					        	lastIndex =repetitionVector.get(maxLeft);
				        	}else {
				        		result = maxLeft.getName()  + "*" + result;
				        		lastIndex = (long) 1;
				        	}
				        	}else {
				        		String[] temp = result.split("\\(");
				        		if(temp[0].equals(repetitionVector.get(maxLeft))) {// in first position : result.contains(repetitionVector.get(maxLeft)+"(")
			        				String tempo = result.replace(repetitionVector.get(maxLeft)+"(", "");
			        				result = tempo;
			        				result = repetitionVector.get(maxLeft)+"("+maxLeft.getName()+ result ;
				        			lastIndex =repetitionVector.get(maxLeft);
			        			}else if(repetitionVector.get(maxLeft)>1){
			        				result = ")" + "*" + result ;
			        				result = repetitionVector.get(maxLeft)+"("+maxLeft.getName()+ result ;
				        			lastIndex =repetitionVector.get(maxLeft);
			        			}else {
			        				result = maxLeft.getName()+"*"+ result;
			        				lastIndex = (long) 1;
			        			}

				        	}

				        	}


			        }else {
			        	// if not same loop

			        	if(!result.contains(maxRight.getName())){//si g avant d
			        		if(Long.valueOf(result.charAt(result.lastIndexOf("(")-1)).equals(repetitionVector.get(maxRight))) {// si la derniere loop est egale au rv
		        				result.replace(repetitionVector.get(maxRight)+"(", "");

		        			}else {
		        				result = ")" + "*" + result ;
		        			}
		        			result = result +"*"+repetitionVector.get(maxRight)+"("+maxRight.getName()+")";
		        			removeRight = true;
			        	}else {
		        			if(result.contains(repetitionVector.get(maxLeft)+"(")) {
		        				String temp = result.replace(repetitionVector.get(maxLeft)+"(", "");
		        				result = temp;
		        			}else {
		        				result = ")" + "*" + result ;
		        			}
		        			result = repetitionVector.get(maxLeft)+"("+maxLeft.getName() +"*"+ result ;
			        	}

			        }

			      }

			      // remove/replace clustered actor
			      if (!removeRight) {
			        repetitionVector.remove(maxLeft);
			        for (AbstractActor a : graph.getAllExecutableActors()) {
			          if(repetitionCount.containsKey(a))
			            if (repetitionCount.get(a).containsKey(maxLeft)) {
			              for(AbstractVertex aa :repetitionCount.get(maxLeft).keySet())
			            	  if(!a.equals(aa))
			            	  repetitionCount.get(a).put(aa, repetitionCount.get(maxLeft).get(aa));
			              repetitionCount.get(a).remove(maxLeft);
			            }


			        }
			        repetitionCount.remove(maxLeft);
			        if (repetitionCount.containsKey(maxLeft)) {
			          repetitionCount.remove(maxLeft);
			        }


			      } else {
			        repetitionVector.remove(maxRight);
			        for (AbstractActor a : graph.getAllExecutableActors()) {
				          if(repetitionCount.containsKey(a))
				            if (repetitionCount.get(a).containsKey(maxRight)) {
				            	if(repetitionCount.containsKey(maxRight))
				            	if(!repetitionCount.get(maxRight).entrySet().isEmpty())
				              for(AbstractVertex aa :repetitionCount.get(maxRight).keySet())
				            	  if(!a.equals(aa))
				            	  repetitionCount.get(a).put(aa, repetitionCount.get(maxRight).get(aa));
				              repetitionCount.get(a).remove(maxRight);
				            }

				        }
			        repetitionCount.remove(maxRight);
//			        if (repetitionCount.containsKey(maxRight)) {
//			        	 repetitionCount.replace(maxLeft, repetitionCount.get(maxLeft), repetitionCount.get(maxRight));//
//			          repetitionCount.remove(maxRight);
//			        }
//			        if (repetitionCount.get(maxLeft).containsKey(maxRight)) {
//			        	if(repetitionCount.get(maxLeft).size()==1)
//			        		repetitionCount.remove(maxLeft);
//			        	else
//			        		repetitionCount.get(maxLeft).remove(maxRight);
//			        }
			      }
			    }
		      }else if(parallelCluster) {
		    	  PreesmLogger.getLogger().log(Level.INFO, "wait, parallel scheduling is not yet implemented");
		      }

		    return result;
	  }
	  /**
	   * Used to compute the greatest common denominator between 2 long values
	   *
	   * @param long1
	   *          long value 1
	   * @param long2
	   *          long value 2
	   */
	  private Long gcd(Long long1, Long long2) {
		  if (long2 == 0)
		      return long1;
		    return gcd(long2, long1 % long2);
	}
	// Function to find gcd of array of
	// numbers
	  private  Long gcdJustAboveCoreNumber(Long arr[], int length, PiGraph lastLevel, PiGraph upperLevel, Boolean isPipLoop)
	{
		  boolean d = false;
		  Long dValue = 0L;
		  Long rate = 0L;
		  long ratio = 0L;
		  if(!isPipLoop) {
		  for(DataInputInterface din : lastLevel.getDataInputInterfaces())
			  if(din.getGraphPort().getFifo().getSource() instanceof DataInputInterface) {
				 if(((DataInputInterface) din.getGraphPort().getFifo().getSource()).getGraphPort().getFifo().isHasADelay()) {
					 d = true;
					  if(dValue== 0L || ratio > ((DataInputInterface) din.getGraphPort().getFifo().getSource()).getGraphPort().getFifo().getDelay().getExpression().evaluate()/din.getGraphPort().getExpression().evaluate()) {
						  dValue = ((DataInputInterface) din.getGraphPort().getFifo().getSource()).getGraphPort().getFifo().getDelay().getExpression().evaluate();
						  rate = din.getGraphPort().getExpression().evaluate();
						  ratio = dValue/rate;
					  }
				 }
			  }
			  else if(din.getGraphPort().getFifo().getSource() instanceof SpecialActor) {
				  for(DataInputPort p : ((AbstractActor)din.getGraphPort().getFifo().getSource()).getDataInputPorts()) {
					  if(p.getIncomingFifo().getSource() instanceof DataInputInterface) {
						if( ((DataInputInterface) p.getIncomingFifo().getSource()).getGraphPort().getFifo().isHasADelay()) {
							d = true;
							  if(dValue== 0L || ratio > ((DataInputInterface) p.getIncomingFifo().getSource()).getGraphPort().getFifo().getDelay().getExpression().evaluate()/din.getGraphPort().getExpression().evaluate()) {
								  dValue = ((DataInputInterface) p.getIncomingFifo().getSource()).getGraphPort().getFifo().getDelay().getExpression().evaluate();
								  rate = din.getGraphPort().getExpression().evaluate();
								  ratio = dValue/rate;
							  }
						}
					  }
					  if(p.getIncomingFifo().isHasADelay()) {
						  d = true;
						  if(dValue== 0L || ratio > p.getIncomingFifo().getDelay().getExpression().evaluate()/din.getGraphPort().getExpression().evaluate()) {
							  dValue = p.getIncomingFifo().getDelay().getExpression().evaluate();
							  rate = din.getGraphPort().getExpression().evaluate();
							  ratio = dValue/rate;
						  }
					  }
				  }
			   }else {
			  if(din.getGraphPort().getFifo().isHasADelay()) {
					  d = true;
					  if(dValue== 0L || ratio > din.getGraphPort().getFifo().getDelay().getExpression().evaluate()/din.getGraphPort().getExpression().evaluate()) {
						  dValue = din.getGraphPort().getFifo().getDelay().getExpression().evaluate();
						  rate = din.getGraphPort().getExpression().evaluate();
						  ratio = dValue/rate;
					  }
				  }
			  }

		  for(DataOutputInterface dout : lastLevel.getDataOutputInterfaces())
			  if(dout.getGraphPort().getFifo().getTarget() instanceof DataOutputInterface) {
					 if(((DataOutputInterface) dout.getGraphPort().getFifo().getTarget()).getGraphPort().getFifo().isHasADelay()) {
						 d = true;
						  if(dValue== 0L || ratio > ((DataOutputInterface) dout.getGraphPort().getFifo().getTarget()).getGraphPort().getFifo().getDelay().getExpression().evaluate()/dout.getGraphPort().getExpression().evaluate()) {
							  dValue = ((DataOutputInterface) dout.getGraphPort().getFifo().getTarget()).getGraphPort().getFifo().getDelay().getExpression().evaluate();
							  rate = dout.getGraphPort().getExpression().evaluate();
							  ratio = dValue/rate;
						  }
					 }
				  }else if(dout.getGraphPort().getFifo().getTarget() instanceof SpecialActor) {
				  for(DataOutputPort p : ((AbstractActor)dout.getGraphPort().getFifo().getTarget()).getDataOutputPorts()) {
					  if(p.getOutgoingFifo().isHasADelay()) {
						  d = true;
						  if(dValue== 0L || ratio > p.getOutgoingFifo().getDelay().getExpression().evaluate()/p.getExpression().evaluate()) {
							  dValue = p.getOutgoingFifo().getDelay().getExpression().evaluate();
							  rate = p.getExpression().evaluate();
							  ratio = dValue/rate;
						  }
					  }
				  }
			   }else {
				  if(dout.getGraphPort().getFifo().isHasADelay()) {
					  d = true;
					  if(dValue== 0L || ratio > dout.getGraphPort().getFifo().getDelay().getExpression().evaluate()/dout.getGraphPort().getExpression().evaluate()) {
						  dValue = dout.getGraphPort().getFifo().getDelay().getExpression().evaluate();
						  rate = dout.getGraphPort().getExpression().evaluate();
						  ratio = dValue/rate;
					  }
				  }
			   }
	}
		  ArrayList<Long> initBRVdivisor = divisor(arr[0]) ;
		  ArrayList<Long>finalBRVdivisor = new ArrayList<>();
	  for (int i = 1; i < length; i++) {
		  ArrayList<Long> loopBRVdivisor = divisor(arr[i]);
		  finalBRVdivisor.clear();
		  for(int ii=0;ii<loopBRVdivisor.size();ii++)
			  if(initBRVdivisor.contains(loopBRVdivisor.get(ii)))
				  finalBRVdivisor.add(loopBRVdivisor.get(ii));
		  initBRVdivisor.clear();
		  for(int ii=0;ii<finalBRVdivisor.size();ii++)
			  initBRVdivisor.add(finalBRVdivisor.get(ii)) ;//oula
	  }
	  Long scaleH =0L;
	  if( brv.get(upperLevel)==null||lastLevel.getContainingPiGraph().getDelayIndex()>0)
		  scaleH=1L;
		  else
			  scaleH= brv.get(upperLevel);
	  if(!d) {//mettre / brv.get(upperLevel) if exist
	  for (Long element : initBRVdivisor) {
		  if(element>=coreNumber) {
			  float a = (float) element/(float)scaleH;
			  return (long) Math.ceil(a);
		  }
	  }
	  if(initBRVdivisor.size()>0) {
		  float a = (float) initBRVdivisor.get(initBRVdivisor.size()-1)/(float)scaleH;
		  return (long) Math.ceil(a);
	  }else
		  return 1L;
	  }else {
		  for (Long element : initBRVdivisor) {
			  if(initBRVdivisor.get(initBRVdivisor.size()-1)/element*rate<=dValue) {
				  return element;
			  }
		  }

		  return initBRVdivisor.get(initBRVdivisor.size()-1);
		  }
	//return 1L;
	}
	  private ArrayList<Long> divisor (Long n){ // les diviseurs de n seront affichés
		  ArrayList<Long> divisors = new ArrayList<>();
		      for (Long i=1L; i<=n;i++)
		          for (Long j=1L;j<=n;j++)
		              if (i*j==n)
		            	  divisors.add(i);

		  return divisors;
		  }

	/**
	   * Used to check schedulability of a graph
	   *
	   * @param graph
	   *          graph
	   */
	private void checkSchedulability(PiGraph graph) {
		 // Check if the graph is flatten
	    if (graph.getActors().stream().anyMatch(x -> x instanceof PiGraph)) {
	      throw new PreesmRuntimeException("PGANScheduler: hierarchy are not handled in [" + graph.getName() + "]");
	    }

	    // Check for incompatible delay (with getter/setter)
	    for (Fifo fifo : graph.getFifosWithDelay()) {
	      Delay delay = fifo.getDelay();
	      // If delay has getter/setter, throw an exception
	      if (delay.getActor().getDataInputPort().getIncomingFifo() != null
	          || delay.getActor().getDataOutputPort().getOutgoingFifo() != null) {
	        throw new PreesmRuntimeException(
	            "PGANScheduler: getter/setter are not handled on [" + delay.getActor().getName() + "]");
	      }
	    }

	}
	  /**
	   * Used to check that the graph matches the IBSDF semantics
	   *
	   * @param lastLevel
	   *          duplicate input token or transmit the last token to fit with the semantic
	 * @param rv
	 * @param urcOptim
	   */
	private PiGraph checkIBSDF(PiGraph lastLevel, Map<AbstractVertex, Long> rv, boolean Optim) {
	    // IBSDF check
		int indexbrd = 0;
		final List<AbstractActor>  a;
		// Compute scaleFactor for input interfaces
	    final long scaleFactorIn1 = getInputInterfacesScaleFactor(lastLevel, lastLevel.getAllActors(), 1L, brv, false);
	    // Compute scaleFactor for output interfaces
	    final long scaleFactorOut1 = getOutputInterfacesScaleFactor(lastLevel, lastLevel.getAllActors(), 1L, brv, false);

	    final long scaleFactorMax1 = Math.max(scaleFactorIn1, scaleFactorOut1);
	    if (!lastLevel.getDataInputInterfaces().isEmpty()) {

	    	long inscaleFactor = scaleFactorMax1;
	    	SortedSet<Long> scaleScaleFactors = new TreeSet<>();
	        scaleScaleFactors.add(1L);
	    		for (final DataInputInterface in : lastLevel.getDataInputInterfaces()) {
	    			long outerRate = 0L;

	    		      final DataOutputPort dataOutputPort = (DataOutputPort) in.getDataPort();
	    		      final long prod = dataOutputPort.getPortRateExpression().evaluate();

	    		      checkOppositeInterfaceRate(lastLevel,in,prod, Optim);
	    		      //checkOppositeInterfaceRate(graph, in, prod);
	    		      final Fifo fifo = dataOutputPort.getOutgoingFifo();
	    		      final DataInputPort targetPort = fifo.getTargetPort();
	    		      final AbstractActor targetActor = targetPort.getContainingActor();
	    		      if (!(targetActor instanceof InterfaceActor) && lastLevel.getAllActors().contains(targetActor)) {
	    		        final long targetRV = this.brv.get(targetActor);
	    		        final long cons = targetPort.getPortRateExpression().evaluate();
	    		        final long tmp = inscaleFactor * cons * targetRV;
	    		        if (tmp > 0) {
	    		          long scaleScaleFactor = 1L;
	    		          if (tmp < prod) {
	    		            scaleScaleFactor = (prod + tmp - 1) / tmp;
	    		            scaleScaleFactors.add(scaleScaleFactor);
	    		          }
	    		          final boolean higherCons = tmp > prod ;
	    		          final boolean lowerUndivisorCons = tmp < prod && prod % tmp != 0;
	    		          if (higherCons || lowerUndivisorCons) {

	    					        BroadcastActor brd = PiMMUserFactory.instance.createBroadcastActor();
	    					        brd.setName("BR_" + lastLevel.getActorPath().replace("/", "_") + "_" + in.getName());
	    					        DataOutputPort outputPort = PiMMUserFactory.instance.createDataOutputPort();
	    					        outputPort.setName("output_Brd");
	    					        brd.getDataOutputPorts().add(outputPort);
	    					        brd.getDataOutputPorts().get(0).setExpression(tmp);

	    					        DataInputPort inputPort = PiMMUserFactory.instance.createDataInputPort();
	    					        inputPort.setName("input_Brd");
	    					        inputPort.setExpression(in.getDataOutputPorts().get(0).getExpression().evaluate());
	    					        brd.getDataInputPorts().add(inputPort);

	    					        // connect

	    					        brd.getDataOutputPorts().get(0).setOutgoingFifo(in.getDataOutputPorts().get(0)
	    						            .getOutgoingFifo());
	    					        Fifo brdFifo = PiMMUserFactory.instance.createFifo();
	    					        brdFifo.setTargetPort(inputPort);
	    					        brdFifo.setSourcePort(in.getDataOutputPorts().get(0));
	    					        brdFifo.setContainingGraph(lastLevel);
	    					        brd.setContainingGraph(lastLevel);
	    					        indexbrd++;
	    					        this.brv.put(brd, 1L);
	    					        rv.put(brd, 1L);
	    					        //this.brv.put(brd, targetRV);
	    		          }
	    		        }
	    		      }
	    		    }

	    }
	    if (!lastLevel.getDataOutputInterfaces().isEmpty()) {
	    	long inscaleFactor = scaleFactorMax1;

	    	  SortedSet<Long> scaleScaleFactors = new TreeSet<>();
	    	    scaleScaleFactors.add(1L);
	    	    for (final DataOutputInterface out : lastLevel.getDataOutputInterfaces()) {
	    	      final DataInputPort dataInputPort = (DataInputPort) out.getDataPort();
	    	      final long cons = dataInputPort.getPortRateExpression().evaluate();
	    	      checkOppositeInterfaceRate(lastLevel, out, cons,Optim);
	    	      final Fifo fifo = dataInputPort.getIncomingFifo();
	    	      final DataOutputPort sourcePort = fifo.getSourcePort();
	    	      final AbstractActor sourceActor = sourcePort.getContainingActor();

	    	      if (!(sourceActor instanceof InterfaceActor) && lastLevel.getAllActors().contains(sourceActor)) {
	    	        final long prod = sourcePort.getPortRateExpression().evaluate();
	    	        final long sourceRV = this.brv.get(sourceActor);
	    	        final long tmp = inscaleFactor * prod * sourceRV;
	    	        if (tmp > 0) {
	    	          long scaleScaleFactor = 1L;
	    	          if (tmp < cons) {
	    	            scaleScaleFactor = (cons + tmp - 1) / tmp;
	    	            scaleScaleFactors.add(scaleScaleFactor);
	    	          }
	    	          final boolean higherProd = tmp > cons ;
	    	          final boolean lowerUndivisorProd = tmp < cons && cons % tmp != 0;
	    	          if (higherProd || lowerUndivisorProd) {

					        RoundBufferActor rnd = PiMMUserFactory.instance.createRoundBufferActor();
					        rnd.setName("RB_" + lastLevel.getActorPath().replace("/", "_") + "_" + out.getName());
					        DataOutputPort outputPort = PiMMUserFactory.instance.createDataOutputPort();
					        outputPort.setName("output_Rnd");
					        rnd.getDataOutputPorts().add(outputPort);
					        rnd.getDataOutputPorts().get(0).setExpression(
					            out.getDataInputPorts().get(0).getExpression().evaluate());


					        DataInputPort inputPort = PiMMUserFactory.instance.createDataInputPort();
					        inputPort.setName("input_Rnd");
					        inputPort.setExpression(prod);
					        rnd.getDataInputPorts().add(inputPort);


					        // connect
					        rnd.getDataInputPorts().get(0).setIncomingFifo(out.getDataInputPorts().get(0).getIncomingFifo());
					        Fifo rndFifo = PiMMUserFactory.instance.createFifo();
					        rndFifo.setTargetPort(out.getDataInputPorts().get(0));
					        rndFifo.setSourcePort(outputPort);
					        rndFifo.setContainingGraph(lastLevel);
					        rnd.setContainingGraph(lastLevel);
					        this.brv.put(rnd, 1L);
					        rv.put(rnd, 1L);
					        //this.brv.put(rnd, sourceRV);

					      }
	    		        }
	    		      }
	    	    	}
	    }

	    return lastLevel;
	  }
	  /**
	   * Used to search for the level to be clustered, the first level to clusterize is the one that contains actor the biggest brv
	   *
	   * @param brv
	   *          list of repetion vector
	   */
	  private PiGraph computeLastLevel( Map<AbstractVertex, Long> brv,int patternloop) {
	    PiGraph lastLevel = null;
	    Long brvMax = (long) 0;
	    AbstractVertex vertexMax = null;

	    // retrieve max brv

	    if(patternloop>0) {
	    	int biggestDelayIndex = 0;
	    	//check graph port
	    	for(PiGraph g :graph.getAllChildrenGraphs()) {
	    		int tempDelayIndex = 0;

	    		for(DataOutputInterface dout : g.getDataOutputInterfaces())
	  			  if(dout.getGraphPort().getFifo().isHasADelay()) {
	  				tempDelayIndex++;
	  			  }
	    		if(tempDelayIndex>biggestDelayIndex) {
	    			lastLevel=g;
	    			biggestDelayIndex = g.getFifoWithDelayIndex();
	    		}
	    	}
	    }
	    if(lastLevel !=null)
	    	return lastLevel;

	    for(AbstractVertex v : brv.keySet()) {
	    	if(graph.getActorIndex()>1) {
		    	if(brv.get(v)>brvMax) {
		    		brvMax = brv.get(v);
		    		vertexMax = v;
		    	}
	    	}else {
	    		return lastLevel = graph;
	    	}
	    }

	    //last level
	    lastLevel = vertexMax.getContainingPiGraph();
	    //first cluster its content if it exists
	    while(!lastLevel.getChildrenGraphs().isEmpty()) {
	    	lastLevel = lastLevel.getChildrenGraphs().get(0);
	    }

	    return lastLevel;

	  }
	  /**
	   * Check if both sides of an interface have the same rate, otherwise throws {@link PreesmRuntimeException}.
	   *
	   * @param graph
	   *          Current inner graph.
	   * @param ia
	   *          Interface to check (from inner graph).
	   * @param rate
	   *          Rate of the interface to check.
	 * @param urcOptim
	   */
	  private static void checkOppositeInterfaceRate(PiGraph graph, InterfaceActor ia, long rate, boolean Optim) {
	    PiGraph motherGraph = graph.getContainingPiGraph();
	    if (motherGraph != null) {
	      // otherwise it means that we compute a local BRV (from GUI)
	      DataPort opposite = ia.getGraphPort();
	      long oppositeRate = opposite.getExpression().evaluate();
	      if (/* motherGraph != graph && */oppositeRate != rate) {
	        String msg = "DataPort [" + opposite.getName() + "] of actor [" + opposite.getContainingActor().getVertexPath()
	            + "] has different rates from inner interface definition: inner " + rate + " -- outer " + oppositeRate;
	        PreesmLogger.getLogger().log(Level.INFO, msg);


	        if(Optim)
	        	opposite.setExpression(rate);//ia.getAllDataPorts().get(0).setExpression(oppositeRate);
	        else
	        	opposite.setExpression(rate);
	      }
	    }
	  }
	  /**
	   * Used to compute the number of level in the graph
	   */
	  private int computeLevel() {
	    //EList<AbstractActor> amount = graph.getAllActors();// all actor all level
	    int levelCount = 1;
	    int levelCountTemp = 1;

	    for(AbstractActor a : graph.getAllActors()) {
	    	PiGraph g = a.getContainingPiGraph();
	    	levelCountTemp = 1;
	    while(g.getContainingPiGraph()!=null) {
	    	g = g.getContainingPiGraph();
	    	levelCountTemp++;
	    }
	    levelCount = Math.max(levelCount, levelCountTemp);
	    }
	    return levelCount;
	  }


	  /**
	   * Compute the scale factor to apply to RV values based on DataOutputInterfaces. It also checks the input interfaces
	   * properties.
	   *
	   * @param graph
	   *          the graph
	   * @param subgraph
	   *          the current connected component
	   * @param scaleFactor
	   *          the current scaleFactor
	   * @param graphBRV
	   *          the graph BRV
	   * @param emitWarningsForSpecialActors
	   *          whether or not warnings about needed special actors should be emit
	   * @return new value of scale factor
	   */
	  private static long getInputInterfacesScaleFactor(final PiGraph graph, final List<AbstractActor> subgraph,
	      final long inscaleFactor, final Map<AbstractVertex, Long> graphBRV, final boolean emitWarningsForSpecialActors) {
	    SortedSet<Long> scaleScaleFactors = new TreeSet<>();
	    scaleScaleFactors.add(1L);
	    for (final DataInputInterface in : graph.getDataInputInterfaces()) {
	      final DataOutputPort dataOutputPort = (DataOutputPort) in.getDataPort();
	      final long prod = dataOutputPort.getPortRateExpression().evaluate();
	      //checkOppositeInterfaceRate(graph, in, prod);
	      final Fifo fifo = dataOutputPort.getOutgoingFifo();
	      final DataInputPort targetPort = fifo.getTargetPort();
	      final AbstractActor targetActor = targetPort.getContainingActor();
	      if (!(targetActor instanceof InterfaceActor) && subgraph.contains(targetActor)) {
	        final long targetRV = graphBRV.get(targetActor);
	        final long cons = targetPort.getPortRateExpression().evaluate();
	        final long tmp = inscaleFactor * cons * targetRV;
	        if (tmp > 0) {
	          long scaleScaleFactor = 1L;
	          if (tmp < prod) {
	            scaleScaleFactor = (prod + tmp - 1) / tmp;
	            scaleScaleFactors.add(scaleScaleFactor);
	          }
	          final boolean higherCons = tmp > prod && emitWarningsForSpecialActors;
	          final boolean lowerUndivisorCons = tmp < prod && prod % tmp != 0;
//	          if (higherCons || lowerUndivisorCons) {
//	            // we emit a warning only if consuming too much, or not enough but with a wrong multiplicity
//	            // note that it is not allowed to leave unconsumed tokens on the input interface
//	            // at the opposite, if more are consumed, a broadcast is added.
//	            String message = String.format(
//	                "The input interface [%s] does not correspond to its target total production (%d vs %d).",
//	                in.getVertexPath(), prod, tmp);
//	            if (higherCons) {
//	              message += " A Broadcast will be added in SRDAG.";
//	            } else {
//	              message += String.format(" Scaling factor (>= x%d) is needed.", scaleScaleFactor);
//	            }
//	            PreesmLogger.getLogger().log(Level.INFO, message);
//	          }
	        }
	      }
	    }
	    return inscaleFactor * scaleScaleFactors.last();
	  }
	  /**
	   * Compute the scale factor to apply to RV values based on DataInputInterfaces
	   *
	   * @param graph
	   *          the graph
	   * @param subgraph
	   *          the current connected component
	   * @param scaleFactor
	   *          the current scaleFactor
	   * @param graphBRV
	   *          the graph BRV
	   * @param emitWarningsForSpecialActors
	   *          whether or not warnings about needed special actors should be emit
	   * @return new value of scale factor
	   */
	  private static long getOutputInterfacesScaleFactor(final PiGraph graph, final List<AbstractActor> subgraph,
	      final long inscaleFactor, final Map<AbstractVertex, Long> graphBRV, final boolean emitWarningsForSpecialActors) {
	    SortedSet<Long> scaleScaleFactors = new TreeSet<>();
	    scaleScaleFactors.add(1L);
	    for (final DataOutputInterface out : graph.getDataOutputInterfaces()) {
	      final DataInputPort dataInputPort = (DataInputPort) out.getDataPort();
	      final long cons = dataInputPort.getPortRateExpression().evaluate();
	      //checkOppositeInterfaceRate(graph, out, cons);
	      final Fifo fifo = dataInputPort.getIncomingFifo();
	      final DataOutputPort sourcePort = fifo.getSourcePort();
	      final AbstractActor sourceActor = sourcePort.getContainingActor();

	      if (!(sourceActor instanceof InterfaceActor) && subgraph.contains(sourceActor)) {
	        final long prod = sourcePort.getPortRateExpression().evaluate();
	        final long sourceRV = graphBRV.get(sourceActor);
	        final long tmp = inscaleFactor * prod * sourceRV;
	        if (tmp > 0) {
	          long scaleScaleFactor = 1L;
	          if (tmp < cons) {
	            scaleScaleFactor = (cons + tmp - 1) / tmp;
	            scaleScaleFactors.add(scaleScaleFactor);
	          }
	          final boolean higherProd = tmp > cons && emitWarningsForSpecialActors;
	          final boolean lowerUndivisorProd = tmp < cons && cons % tmp != 0;
//	          if (higherProd || lowerUndivisorProd) {
//	            // we emit a warning only if producing too much, or not enough but with a wrong multiplicity
//	            // note that it is not allowed to produce less than the consumed tokens on the output interface
//	            // at the opposite, if more are produced, a roundbuffer is added.
//	            String message = String.format(
//	                "The output interface [%s] does not correspond to its source total production (%d vs %d).",
//	                out.getVertexPath(), cons, tmp);
//	            if (higherProd) {
//	              message += " A Roundbuffer will be added in SRDAG.";
//	            } else {
//	              message += String.format(" Scaling factor (>= x%d) is needed.", scaleScaleFactor);
//	            }
//	            PreesmLogger.getLogger().log(Level.INFO, message);
//	          }
	        }
	      }
	    }
	    return inscaleFactor * scaleScaleFactors.last();
	  }
}
