package org.preesm.algorithm.synthesis.memalloc;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.AbstractAllocation;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memalloc.model.WorkingMemory;
import org.preesm.algorithm.memory.allocation.tasks.MemoryAllocatorTask;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.memalloc.meg.allocation.PiMemoryAllocatorTask;
import org.preesm.algorithm.synthesis.memalloc.script.PiMemoryScriptTask;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.model.PreesmCopyTracker;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.statictools.PiSDFFlattener;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

@PreesmTask(id = "alloc2.hybrid", name = "Hybrid Allocation task", category = "Memory Optimization",

    inputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, type = PiGraph.class, description = ""),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SCENARIO, type = Scenario.class, description = ""),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE, type = Design.class, description = ""),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_SCHEDULE, type = Schedule.class, description = ""),
      @Port(name = AbstractWorkflowNodeImplementation.KEY_MAPPING, type = Mapping.class, description = "") },
    outputs = {
      @Port(name = AbstractWorkflowNodeImplementation.KEY_ALLOCATION, type = Allocation.class, description = "") },

    shortDescription = "Creates an Allocation for the input graph, using the MEG technique for the first hierarchical "
        + "levels, and a PiMM based technique for the last hierarchical levels.",

    description = """
        The classical allocation technique in PREESM is based on a Memory Exculsion Graph (MEG) that is itself
        based on the SrDAG of the input application. To get more information, go see the MEG Builder, MEG Updater,
        Memory Scripts, and the Memory Allocation tasks. For clusters, the allocation must be different since the MEG
        can't be built as an SrDAG is never built for clusters. This task allows to allocate the top graph with a MEG,
        and bottom levels that are clusters with a PiMM based allocation method.
        """,

    parameters = {
      @Parameter(name = HybridAllocationTask.PARAM_UPDATE,
          description = "if True, it will activate the update of the MEG after its creation."
              + " For more infos, please see the MEG Updater Task.",
          values = { @Value(name = HybridAllocationTask.VALUE_FALSE, effect = ""),
            @Value(name = HybridAllocationTask.VALUE_TRUE, effect = "") }),

      @Parameter(name = HybridAllocationTask.PARAM_SCRIPTS,
          description = "if True, it will activate the memory scripts to enhance memory reuse in the meg. "
              + "For more infos and to see the parameters to add, please see the Memroy Scripts Task.",
          values = { @Value(name = HybridAllocationTask.VALUE_FALSE, effect = ""),
            @Value(name = HybridAllocationTask.VALUE_TRUE, effect = "") }),

      @Parameter(name = PiMemoryScriptTask.PARAM_CHECK,
          description = "Verification policy used when checking the applicability of the memory scripts written"
              + " by the developer and associated to the actor.",
          values = {
            @Value(name = PiMemoryScriptTask.VALUE_CHECK_THOROUGH,
                effect = "Will generate error messages with a detailed description of the source of the error."
                    + " This policy should be used when writting memory scripts for the first time."),
            @Value(name = PiMemoryScriptTask.VALUE_CHECK_FAST,
                effect = "All errors in memory script are still detected, but error messages are less verbose. "
                    + "This verification policy is faster than the Thorough policy."),
            @Value(name = PiMemoryScriptTask.VALUE_CHECK_NONE,
                effect = "No verification is performed. Use this policy to speed up workflow execution once all"
                    + " memory scripts have been validated..") }),
      @Parameter(name = PiMemoryScriptTask.PARAM_FALSE_SHARING,
          description = "Force additional allocation before/after buffer to prevent false sharing issues.",
          values = {
            @Value(name = PiMemoryScriptTask.VALUE_FALSE,
                effect = "The false sharing prevention mecanism will not be used."),
            @Value(name = PiMemoryScriptTask.VALUE_TRUE,
                effect = "The false sharing prevention mecanism will be used."
                    + "Using the Data alignement parameter.") }),

      @Parameter(name = PiMemoryScriptTask.PARAM_LOG,
          description = "Specify whether, and where, a log of the buffer matching optimization should be "
              + "generated. Generated log are in the markdown format, and provide information "
              + "on all matches created by scripts as well as which match could be applied by the "
              + "optimization process.",
          values = {
            @Value(name = "path/file.txt",
                effect = "The path given in this property is relative to the ”Code generation "
                    + "directory” defined in the executed scenario."),
            @Value(name = "empty", effect = "No log will be generated.") }),
      @Parameter(name = PiMemoryAllocatorTask.PARAM_ALLOCATORS,
          description = "Specify which memory allocation algorithm(s) should be used. If the string value of the "
              + "parameters contains several algorithm names, all will be executed one by one.",
          values = {
            @Value(name = PiMemoryAllocatorTask.VALUE_ALLOCATORS_BASIC,
                effect = "Each memory object is allocated in a dedicated memory space. Memory allocated for a given "
                    + "object is not reused for other."),
            @Value(name = PiMemoryAllocatorTask.VALUE_ALLOCATORS_BEST_FIT,
                effect = "Memory objects are allocated one by one; allocating each object to the available space "
                    + "in memory whose size is the closest to the size of the allocated object. If MEG exclusions"
                    + " permit it, memory allocated for a memory object may be reused for others."),
            @Value(name = PiMemoryAllocatorTask.VALUE_ALLOCATORS_FIRST_FIT,
                effect = "Memory objects are allocated one by one; allocating each object to the first available "
                    + "space in memory whose size is the large enough to allocate the object. If MEG exclusions"
                    + " permit it, memory allocated for a memory object may be reused for others.") }),
      @Parameter(name = PiMemoryAllocatorTask.PARAM_DISTRIBUTION_POLICY,
          description = "Specify which memory architecture should be used to allocate the memory.",
          values = {
            @Value(name = PiMemoryAllocatorTask.VALUE_DISTRIBUTION_SHARED_ONLY,
                effect = "(Default) All memory objects are allocated in a single memory bank accessible to all PE."),
            @Value(name = PiMemoryAllocatorTask.VALUE_DISTRIBUTION_DISTRIBUTED_ONLY,
                effect = "Each PE is associated to a private memory bank that no other PE can access. "
                    + "(Currently supported only in the MPPA code generation.)"),
            @Value(name = PiMemoryAllocatorTask.VALUE_DISTRIBUTION_MIXED,
                effect = "Both private memory banks and a shared memory can be used for allocating memory."),
            @Value(name = PiMemoryAllocatorTask.VALUE_DISTRIBUTION_MIXED_MERGED,
                effect = "Same as mixed, but the memory allocation algorithm favors buffer merging over"
                    + " memory distribution.") }),
      @Parameter(name = PiMemoryAllocatorTask.PARAM_XFIT_ORDER,
          description = "When using FirstFit or BestFit memory allocators, this parameter specifies in which order"
              + " the memory objects will be fed to the allocation algorithm. If the string value associated to the "
              + "parameters contains several order names, all will be executed one by one.",
          values = {
            @Value(name = PiMemoryAllocatorTask.VALUE_XFIT_ORDER_APPROX_STABLE_SET,
                effect = "Memory objects are sorted into disjoint stable sets. Stable sets are formed one after the"
                    + " other, each with the largest possible number of object. Memory objects are fed to the "
                    + "allocator set by set and in the largest first order within each stable set."),
            @Value(name = PiMemoryAllocatorTask.VALUE_XFIT_ORDER_EXACT_STABLE_SET,
                effect = "Similar to '" + PiMemoryAllocatorTask.VALUE_XFIT_ORDER_APPROX_STABLE_SET
                    + "'. Stable set are built using an exact algorithm instead of a heuristic."),
            @Value(name = PiMemoryAllocatorTask.VALUE_XFIT_ORDER_LARGEST_FIRST,
                effect = "Memory objects are allocated in decreasing order of their size."),
            @Value(name = PiMemoryAllocatorTask.VALUE_XFIT_ORDER_SHUFFLE,
                effect = "Memory objects are allocated in a random order. Using the 'Nb of Shuffling Tested' "
                    + "parameter, it is possible to test several random orders and only keep the best memory"
                    + " allocation.") }),
      @Parameter(name = MemoryAllocatorTask.PARAM_ALIGNMENT,
          description = "Option used to force the allocation of buffers (i.e. Memory objects) with aligned addresses."
              + " The data alignment property should always have the same value as the one set in the properties of "
              + "the Memory Scripts task.",
          values = {
            @Value(name = MemoryAllocatorTask.VALUE_ALIGNEMENT_NONE,
                effect = "No special care is taken to align the buffers in memory."),
            @Value(name = MemoryAllocatorTask.VALUE_ALIGNEMENT_DATA,
                effect = "All buffers are aligned on addresses that are multiples of their size. For example, a 32 "
                    + "bites integer is aligned on 32 bits address."),
            @Value(name = MemoryAllocatorTask.VALUE_ALIGNEMENT_FIXED + "n",
                effect = "Where $$n\\in \\mathbb{N}^*$$. This forces the allocation algorithm to align all buffers"
                    + " on addresses that are multiples of n bits.") }),
      @Parameter(name = MemoryAllocatorTask.PARAM_NB_SHUFFLE,
          description = "Number of random order tested when using the Shuffle value for the Best/First Fit order"
              + " parameter.",
          values = { @Value(name = "$$n\\in \\mathbb{N}^*$$", effect = "Number of random order.") }),

      @Parameter(name = HybridAllocationTask.PARAM_TOP_THRESHOLD, values = {
        @Value(name = "0", effect = "The top scheduler will never be called, only the bottom scheduler."),
        @Value(name = "1",
            effect = "Default value. The top scheduler will be called on the first level of the graph, "
                + "and the bottom scheduler will be called on every graph under it. The value can be 2, 3, or more, "
                + "and the top scheduler will be used for graphs of depth of 2, 3 or more, respectively."),
        @Value(name = "all",
            effect = "This value garanties that the bottom scheduler will never be used, and that the top scheduler"
                + " will be called for every graphs, no matter their depth") },
          description = """
              Gives the threshold to separate the top and the bottom hierarchical levels of the graph. By default,
              it is set a 1.
              """),

      @Parameter(name = HybridAllocationTask.PARAM_BOT_ALLOCATOR,
          values = { @Value(name = HybridAllocationTask.VALUE_BOT_ALLOCATOR_SIMPLE,
              effect = "Simple and naive allocation from PiMM graph.") },
          description = """
              Gives the PiMM Based allocation method to use when allocating the bottom hierarchical levels of the graph.
              """),

      @Parameter(name = "Verbose",
          description = "How verbose will this task be during its execution. In verbose mode, the task will"
              + " log the start and completion time of the build, as well as characteristics (number of memory"
              + " objects, density of exclusions) of the produced MEG.",
          values = { @Value(name = "false", effect = "(Default) The task will not log information."),
            @Value(name = "true", effect = "The task will log build and allocation information.") }) },

    seeAlso = { "**MEG**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Memory bounds for the distributed "
        + "execution of a hierarchical synchronous data-flow graph. In Embedded Computer Systems: "
        + "Architectures, Modeling, and Simulation (SAMOS XII), 2012 International Conference on, 2012." })

public class HybridAllocationTask extends AbstractTaskImplementation {

  public static final String PARAM_TOP_THRESHOLD     = "Top Threshold";
  public static final String VALUE_TOP_THRESHOLD_ALL = "all";

  public static final String PARAM_BOT_ALLOCATOR        = "Bot Allocator";
  public static final String VALUE_BOT_ALLOCATOR_SIMPLE = "simple";

  public static final String PARAM_UPDATE = "Update MEG";

  public static final String PARAM_SCRIPTS = "Run memory scripts";
  public static final String VALUE_TRUE    = "True";
  public static final String VALUE_FALSE   = "False";

  Map<PiGraph, AbstractAllocation> internalAllocationTracker = new HashMap<>();

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow) {

    // Getting the inputs
    final PiGraph graph = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final Design arch = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Schedule schedule = (Schedule) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCHEDULE);
    final Mapping mapping = (Mapping) inputs.get(AbstractWorkflowNodeImplementation.KEY_MAPPING);

    // Computing the threshold
    final String thresholdValue = parameters.get(PARAM_TOP_THRESHOLD);
    long depthThreshold;
    if (thresholdValue.equals(VALUE_TOP_THRESHOLD_ALL)) {
      depthThreshold = PiSDFFlattener.computeGraphMaxDepth(graph);
    } else {
      depthThreshold = Long.parseLong(thresholdValue);
    }

    // Making the allocation for every hierarchical level of the graph.
    final AbstractAllocation tmpResult = executeRecursively(graph, scenario, arch, schedule, mapping, depthThreshold, 0,
        parameters, monitor, nodeName, workflow);

    if (!(tmpResult instanceof final Allocation result)) {
      throw new PreesmRuntimeException("The chosen allocators must give back an Allocation, not just a Working Memory");
    }

    final Map<String, Object> outputs = new HashMap<>();
    outputs.put(AbstractWorkflowNodeImplementation.KEY_ALLOCATION, result);
    return outputs;
  }

  private AbstractAllocation executeRecursively(PiGraph graph, Scenario scenario, Design arch, Schedule schedule,
      Mapping mapping, long threshold, int currentDepth, Map<String, String> parameters, IProgressMonitor monitor,
      String nodeName, Workflow workflow) {

    /** Choosing witch scheduler to use */
    final String botAllocatorValue = parameters.get(PARAM_BOT_ALLOCATOR);

    final IMemoryAllocation allocator;
    if (currentDepth < threshold) {
      allocator = new LegacyMemoryAllocation(parameters);
    } else {
      allocator = switch (botAllocatorValue) {
        case VALUE_BOT_ALLOCATOR_SIMPLE:
          schedule = schedule.getInternalSchedules().get(graph);
          yield new SimplePiMMMemoryAllocation();
        default:
          throw new PreesmRuntimeException(PARAM_BOT_ALLOCATOR + " " + botAllocatorValue
              + " doesn't exists. Choose between the following ones : " + VALUE_BOT_ALLOCATOR_SIMPLE);
      };
    }

    /** Making the allocation for the current graph */
    final AbstractAllocation parentAbstractAlloc = allocator.allocateMemory(graph, arch, scenario, schedule, mapping);

    /** Making the allocation for every child graphs */
    for (final PiGraph child : graph.getChildrenGraphs()) {
      final PiGraph oriGraph = PreesmCopyTracker.getOriginalSource(child);

      if (internalAllocationTracker.containsKey(oriGraph)) {
        continue;
      }

      final AbstractAllocation childAbstractAlloc = this.executeRecursively(oriGraph, scenario, arch, schedule, mapping,
          threshold, currentDepth + 1, parameters, monitor, nodeName, workflow);
      this.internalAllocationTracker.put(oriGraph, childAbstractAlloc);

      /** Merging child allocation in parent allocation */
      switch (childAbstractAlloc) {
        case final WorkingMemory childWorkingMem -> parentAbstractAlloc.getWorkingMemories().add(childWorkingMem);

        case final Allocation childAlloc when parentAbstractAlloc instanceof final Allocation parentAlloc ->
          parentAlloc.mergeAllocations(childAlloc);

        default -> throw new PreesmRuntimeException(
            "This type of allocation merging is not supported yet. Please contact Preesm developers.");

      }

    }

    return parentAbstractAlloc;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(PiMemoryScriptTask.PARAM_VERBOSE, PiMemoryScriptTask.VALUE_TRUE);
    parameters.put(PiMemoryScriptTask.PARAM_CHECK, PiMemoryScriptTask.VALUE_CHECK_THOROUGH);
    parameters.put(PiMemoryScriptTask.PARAM_LOG, PiMemoryScriptTask.VALUE_LOG);
    parameters.put(PiMemoryScriptTask.PARAM_FALSE_SHARING, PiMemoryScriptTask.VALUE_FALSE);
    parameters.put(PiMemoryAllocatorTask.PARAM_VERBOSE, PiMemoryAllocatorTask.VALUE_TRUE_FALSE_DEFAULT);
    parameters.put(PiMemoryAllocatorTask.PARAM_ALLOCATORS, PiMemoryAllocatorTask.VALUE_ALLOCATORS_DEFAULT);
    parameters.put(PiMemoryAllocatorTask.PARAM_XFIT_ORDER, PiMemoryAllocatorTask.VALUE_XFIT_ORDER_DEFAULT);
    parameters.put(PiMemoryAllocatorTask.PARAM_NB_SHUFFLE, PiMemoryAllocatorTask.VALUE_NB_SHUFFLE_DEFAULT);
    parameters.put(PiMemoryAllocatorTask.PARAM_ALIGNMENT, PiMemoryAllocatorTask.VALUE_ALIGNEMENT_DEFAULT);
    parameters.put(PiMemoryAllocatorTask.PARAM_DISTRIBUTION_POLICY,
        PiMemoryAllocatorTask.VALUE_DISTRIBUTION_MIXED_MERGED);
    parameters.put(PARAM_BOT_ALLOCATOR, VALUE_BOT_ALLOCATOR_SIMPLE);
    parameters.put(PARAM_TOP_THRESHOLD, "1");
    parameters.put(PARAM_UPDATE, VALUE_TRUE);
    parameters.put(PARAM_SCRIPTS, VALUE_TRUE);

    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "allocating the graph and its sub graphs";
  }

}
