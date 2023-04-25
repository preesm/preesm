package scape;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.model.pisdf.AbstractActor;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
/**
 * This class cluster actor in order to match the target architecture
 * For more details, see conference paper "SCAPE: HW-Aware Clustering of Dataflow Actors for Tunable Scheduling Complexity", published at DASIP 2023
 * @author orenaud
 *
 */
@PreesmTask(id = "clustering.raiser.task.identifier", name = "Clustering Task",
    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class),
        @Port(name = "architecture", type = Design.class) },
    outputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class) },

    parameters = { @Parameter(name = "Printer",
        description = "Specify which printer should be used to generate code. Printers are defined in Preesm source"
            + " code using an extension mechanism that make it possible to define a single printer name for several "
            + "targeted architecture. Hence, depending on the type of PEs declared in the architecture model, Preesm "
            + "will automatically select the associated printer class, if it exists.",
        values = {
            @Value(name = "C",
                effect = "Print C code and shared-memory based communications. Currently compatible with x86, c6678, "
                    + "and arm architectures."),
            @Value(name = "InstrumentedC",
                effect = "Print C code instrumented with profiling code, and shared-memory based communications. "
                    + "Currently compatible with x86, c6678 architectures.."),
            @Value(name = "XML",
                effect = "Print XML code with all informations used by other printers to print code. "
                    + "Compatible with x86, c6678.") }),

        @Parameter(name = "Stack size", description = "stack size (in Byte)", values = { @Value(name = "Fixed:=n",
            effect = "the size of the stack allows to quantify the number of allocable buffer in the stack the rest in the heap") }),

        @Parameter(name = "Core number", description = "number of target cores",
            values = { @Value(name = "Fixed:=n",
                effect = "the number of cores of the target allows to quantify the number of possible clusters") }),

        @Parameter(name = "Level number", description = "number of level to cluster",
        values = { @Value(name = "Fixed:=n",
            effect = "the number of level to cluster in order to reach flattener performance and compromising analysis time") }),

        //@Parameter(name = "SrDAG max", description = "limit of the srdag number which induces a desired analysis time limit",
        //values = { @Value(name = "Fixed:=n",
            //effect = "the algorithm searches for the SrDAG with the best performance in the limit") }),

        @Parameter(name = "SCAPE mode", description = "choose the clustering mode : 1 = coarse then  id on the *level number, 2 = id on each levels, 3 = coarse then id on id levels ",
            values = { @Value(name = "Fixed:=n", effect = "switch of clustering algorithm") }),

        @Parameter(name = "Optimized Cluster", description = "compute the best cluster number at : false = level, true = APGAN(level)",
        values = { @Value(name = "true/false", effect = "switch of cluster level") }),

        //@Parameter(name = "Auto", description = "enable SCAPE on nested level",
        //values = { @Value(name = "true/false", effect = "switch of several granularity configuration or the other one") }),

        //@Parameter(name = "Parallelizable cluster", description = "allows the mapping of the cluster on : false = 1core, true = several core",
        //values = { @Value(name = "true/false", effect = "switch of cluster scheduling method") }),
        
        @Parameter(name = "Non-cluster actor", description = "does not allow to group the actors entered in parameter",
        values = { @Value(name = "String", effect = "disable cluster") }),

//        @Parameter(name = "Apollo", description = "Enable the use of Apollo for intra-actor optimization",
//            values = { @Value(name = "true/false",
//                effect = "Print C code with Apollo function calls. " + "Currently compatibe with x86") })
    })
public class ScapeTask extends AbstractTaskImplementation {
	  public static final String STACK_SIZE_DEFAULT      = "1000000";    // 1MB
	  public static final String STACK_PARAM             = "Stack size";
	  public static final String CORE_AMOUNT_DEFAULT     = "1";          // 1
	  public static final String CORE_PARAM              = "Core number";
	  public static final String CLUSTERING_MODE_DEFAULT = "1";      // SCAPE1
	  public static final String CLUSTERING_PARAM        = "SCAPE mode";
	  public static final String CLUSTER_NUMBER_DEFAULT     = "1";          // 1
	  public static final String CLUSTER_PARAM              = "Level number";
	  //public static final String SrDAG_NUMBER_DEFAULT     = "2000";          // 100--> 1s; 1000--> 1min4s; 2000--> 10min14s; 3000-->37min47s; 4000-->1h34min1s; 5000--> 3h9min14s
	  //public static final String SrDAG_PARAM              = "SrDAG max";
	  //public static final String PARALLEL_CLUSTER_DEFAULT = "false";      // no parallel
	  //public static final String PARALLEL_CLUSTER_PARAM        = "Parallelizable cluster";
	  //public static final String CLUSTER_OPTIM_DEFAULT     = "false";          // no algo
	  //public static final String CLUSTER_OPTIM_PARAM              = "Optimized Cluster";
	  //public static final String AUTO_DEFAULT     = "false";          // no algo
	  //public static final String AUTO_PARAM              = "Auto";
	  public static final String NON_CLUSTER_DEFAULT              = "";
	  public static final String NON_CLUSTER_PARAM              = "Non-cluster actor";

	  public static final String PARAM_PRINTER    = "Printer";
	  public static final String VALUE_PRINTER_IR = "IR";

	  protected long             stack;
	  protected long             core;
	  protected int             cluster;
	  //protected int             srdagMax;
	  protected int          mode;
	  //protected boolean          parallelCluster;
	  //protected boolean             optimizedCluster;
	  //protected boolean             auto;

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
			IProgressMonitor monitor, String nodeName, Workflow workflow) {
	    // retrieve input parameter stack size
	    String stack = parameters.get(ScapeTask.STACK_PARAM);
	    this.stack = Integer.decode(stack);
	    // retrieve input parameter
	    String core = parameters.get(ScapeTask.CORE_PARAM);
	    this.core = Integer.decode(core);
	 // retrieve input parameter
	    String cluster = parameters.get(ScapeTask.CLUSTER_PARAM);
	    this.cluster = Integer.decode(cluster);
	    // retrieve input parameter
	    String mode = parameters.get(ScapeTask.CLUSTERING_PARAM);
	    this.mode = Integer.decode(mode);
	 // retrieve input parameter
	    //String srdagMax = parameters.get(ClusteringRaiserTask.SrDAG_PARAM);
	    //this.srdagMax = Integer.decode(srdagMax);
	 // retrieve input parameter
	    //String parallelCluster = parameters.get(ClusteringRaiserTask.PARALLEL_CLUSTER_PARAM);
	    //this.parallelCluster = bool(parallelCluster);
	 // retrieve input parameter
	    //String optimizedCluster = parameters.get(ClusteringRaiserTask.CLUSTER_OPTIM_PARAM);
	    //this.optimizedCluster = bool(optimizedCluster);
	 // retrieve input parameter
	    //String auto = parameters.get(ClusteringRaiserTask.AUTO_PARAM);
	    //this.auto = bool(auto);
	 // retrieve input parameter
	    String nonClusterable = parameters.get(ScapeTask.NON_CLUSTER_PARAM);
	    String[] StrNonClusterableList = nonClusterable.split("\\*");
	    

	    // Task inputs
	    PiGraph inputGraph = (PiGraph) inputs.get("PiMM");
	    Scenario scenario = (Scenario) inputs.get("scenario");
	    Design archi = (Design) inputs.get("architecture");
	    Long stackSize = this.stack;
	    Long coreAmount = this.core;
	    int clusterNumber = this.cluster;
	    int clusteringMode = this.mode;
	    
	    List<AbstractActor> nonClusterableList = new LinkedList<>();
	    for(int i = 0; i < StrNonClusterableList.length;i++)
	    	for(AbstractActor a: inputGraph.getExecutableActors())
	    		if(a.getName().equals(StrNonClusterableList[i]) && !nonClusterableList.contains(a))
	    			nonClusterableList.add(a);
	    //...
	    //PiGraph tempGraph = null;
	    PiGraph tempGraph = new Scape(inputGraph, scenario, archi, stackSize, coreAmount, clusteringMode, clusterNumber,nonClusterableList).execute();

	    Map<String, Object> output = new HashMap<>();
	    // return topGraph
	    output.put("PiMM", tempGraph);
	    for(Entry<ComponentInstance, EList<AbstractActor>> gp: scenario.getConstraints().getGroupConstraints()) {
	    	int size = gp.getValue().size();
	    	for (int i = 1; !gp.getValue().isEmpty(); i++) {
	  	      int k = size - i;
	  	      gp.getValue().remove(k);
	  	    }
		      for (final AbstractActor actor : tempGraph.getAllActors()) {

	    		        gp.getValue().add(actor);
	    		      }
	    }


	    // return scenario updated
	    output.put("scenario", scenario);
	    return output;
	}

	private boolean bool(String mode2) {
		if(mode2.equals("true")) {
			return true;
		}
		return false;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		 final Map<String, String> parameters = new LinkedHashMap<>();
		    final StringBuilder avilableLanguages = new StringBuilder("? C {");

		    // Retrieve the languages registered with the printers
		    final Set<String> languages = new LinkedHashSet<>();
		    final IExtensionRegistry registry = Platform.getExtensionRegistry();

		    final IConfigurationElement[] elements = registry
		        .getConfigurationElementsFor("org.ietr.preesm.codegen.xtend.printers");
		    for (final IConfigurationElement element : elements) {
		      languages.add(element.getAttribute("language"));
		    }

		    for (final String lang : languages) {
		      avilableLanguages.append(lang + ", ");
		    }
		    avilableLanguages.append(ScapeTask.VALUE_PRINTER_IR + "}");

		    parameters.put(ScapeTask.PARAM_PRINTER, avilableLanguages.toString());

		    // stack default
		    parameters.put(ScapeTask.STACK_PARAM, ScapeTask.STACK_SIZE_DEFAULT);
		    // core default
		    parameters.put(ScapeTask.CORE_PARAM, ScapeTask.CORE_AMOUNT_DEFAULT);
		 // core default
		    parameters.put(ScapeTask.CLUSTER_PARAM, ScapeTask.CLUSTER_NUMBER_DEFAULT);
		    // mode default
		    parameters.put(ScapeTask.CLUSTERING_PARAM, ScapeTask.CLUSTERING_MODE_DEFAULT);
		 // mode default
		    //parameters.put(ClusteringRaiserTask.SrDAG_PARAM, ClusteringRaiserTask.SrDAG_NUMBER_DEFAULT);
		 // mode default
		    //parameters.put(ClusteringRaiserTask.PARALLEL_CLUSTER_PARAM, ClusteringRaiserTask.PARALLEL_CLUSTER_DEFAULT);
		 // mode default
		    //parameters.put(ClusteringRaiserTask.CLUSTER_OPTIM_PARAM, ClusteringRaiserTask.CLUSTER_OPTIM_DEFAULT);
		 // mode default
		    //parameters.put(ClusteringRaiserTask.AUTO_PARAM, ClusteringRaiserTask.AUTO_DEFAULT);
		 // mode default
		    parameters.put(ScapeTask.NON_CLUSTER_PARAM, ScapeTask.NON_CLUSTER_DEFAULT);

		    return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Starting Execution of clustering Task";
	}

}