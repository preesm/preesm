/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2021 - 2024)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2013 - 2015)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2015)
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
package org.preesm.algorithm.memory.allocation.tasks;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.memory.allocation.BasicAllocator;
import org.preesm.algorithm.memory.allocation.BestFitAllocator;
import org.preesm.algorithm.memory.allocation.DeGreefAllocator;
import org.preesm.algorithm.memory.allocation.Distributor;
import org.preesm.algorithm.memory.allocation.FirstFitAllocator;
import org.preesm.algorithm.memory.allocation.MemoryAllocator;
import org.preesm.algorithm.memory.allocation.OrderedAllocator;
import org.preesm.algorithm.memory.allocation.OrderedAllocator.Order;
import org.preesm.algorithm.memory.allocation.OrderedAllocator.Policy;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionVertex;
import org.preesm.commons.doc.annotations.DocumentedError;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * The Class MemoryAllocatorTask.
 */
@PreesmTask(id = "org.ietr.preesm.memory.allocation.MemoryAllocatorTask", name = "Memory Allocation",
    category = "Memory Optimization", shortDescription = "Perform the memory allocation for the given MEG.",

    description = "Workflow task responsible for allocating the memory objects of the given MEG.",

    inputs = { @Port(type = MemoryExclusionGraph.class, name = "MemEx", description = "Input Memory Exclusion Graph") },

    outputs = { @Port(type = Map.class, name = "MEGs",
        description = "Map associating, for each memory element in the architecture, "
            + "according to the chosen _Distribution_ parameter value, a Memory Exclusion Graph annotated with "
            + "allocation information (i.e. buffer addresses, etc.).") },

    parameters = {
        @Parameter(name = MemoryAllocatorTask.PARAM_VERBOSE, description = "Verbosity of the task.",
            values = { @Value(name = "True", effect = "Detailed statistics of the allocation process are logged"),
                @Value(name = "False", effect = "Logged information is kept to a minimum") }),
        @Parameter(name = MemoryAllocatorTask.PARAM_ALLOCATORS,
            description = "Specify which memory allocation algorithm(s) should be used. If the string value of the "
                + "parameters contains several algorithm names, all will be executed one by one.",
            values = { @Value(name = MemoryAllocatorTask.VALUE_ALLOCATORS_BASIC,
                effect = "Each memory object is allocated in a dedicated memory space. Memory allocated for a given "
                    + "object is not reused for other."),
                @Value(name = MemoryAllocatorTask.VALUE_ALLOCATORS_BEST_FIT,
                    effect = "Memory objects are allocated one by one; allocating each object to the available space "
                        + "in memory whose size is the closest to the size of the allocated object. If MEG exclusions"
                        + " permit it, memory allocated for a memory object may be reused for others."),
                @Value(name = MemoryAllocatorTask.VALUE_ALLOCATORS_FIRST_FIT,
                    effect = "Memory objects are allocated one by one; allocating each object to the first available "
                        + "space in memory whose size is the large enough to allocate the object. If MEG exclusions"
                        + " permit it, memory allocated for a memory object may be reused for others."),
                @Value(name = MemoryAllocatorTask.VALUE_ALLOCATORS_DE_GREEF,
                    effect = "Algorithm adapted from DeGreef (1997)}. If MEG exclusions permit it, memory allocated"
                        + " for a memory object may be reused for others.") }),
        @Parameter(name = MemoryAllocatorTask.PARAM_DISTRIBUTION_POLICY,
            description = "Specify which memory architecture should be used to allocate the memory.",
            values = { @Value(name = MemoryAllocatorTask.VALUE_DISTRIBUTION_SHARED_ONLY,
                effect = "(Default) All memory objects are allocated in a single memory bank accessible to all PE."),
                @Value(name = MemoryAllocatorTask.VALUE_DISTRIBUTION_DISTRIBUTED_ONLY,
                    effect = "Each PE is associated to a private memory bank that no other PE can access. "
                        + "(Currently supported only in the MPPA code generation.)"),
                @Value(name = MemoryAllocatorTask.VALUE_DISTRIBUTION_MIXED,
                    effect = "Both private memory banks and a shared memory can be used for allocating memory."),
                @Value(name = MemoryAllocatorTask.VALUE_DISTRIBUTION_MIXED_MERGED,
                    effect = "Same as mixed, but the memory allocation algorithm favors buffer merging over"
                        + " memory distribution.") }),
        @Parameter(name = MemoryAllocatorTask.PARAM_XFIT_ORDER,
            description = "When using FirstFit or BestFit memory allocators, this parameter specifies in which order"
                + " the memory objects will be fed to the allocation algorithm. If the string value associated to the "
                + "parameters contains several order names, all will be executed one by one.",
            values = {
                @Value(name = MemoryAllocatorTask.VALUE_XFIT_ORDER_APPROX_STABLE_SET,
                    effect = "Memory objects are sorted into disjoint stable sets. Stable sets are formed one after the"
                        + " other, each with the largest possible number of object. Memory objects are fed to the "
                        + "allocator set by set and in the largest first order within each stable set."),
                @Value(name = MemoryAllocatorTask.VALUE_XFIT_ORDER_EXACT_STABLE_SET,
                    effect = "Similar to '" + MemoryAllocatorTask.VALUE_XFIT_ORDER_APPROX_STABLE_SET
                        + "'. Stable set are built using an exact algorithm instead of a heuristic."),
                @Value(name = MemoryAllocatorTask.VALUE_XFIT_ORDER_LARGEST_FIRST,
                    effect = "Memory objects are allocated in decreasing order of their size."),
                @Value(name = MemoryAllocatorTask.VALUE_XFIT_ORDER_SHUFFLE,
                    effect = "Memory objects are allocated in a random order. Using the 'Nb of Shuffling Tested' "
                        + "parameter, it is possible to test several random orders and only keep the best memory"
                        + " allocation."),
            // @Value(name = MemoryAllocatorTask.VALUE_XFIT_ORDER_SCHEDULING,
            // effect = "Memory objects are allocated in scheduling order of their 'birth'. The 'birth' of a "
            // + "memory object is the instant when its memory would be allocated by a dynamic allocator. "
            // + "This option can be used to mimic the behavior of a dynamic allocator. (Only available for "
            // + "MEG updated with scheduling information).")
            }),
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
            values = { @Value(name = "$$n\\in \\mathbb{N}^*$$", effect = "Number of random order.") }) },

    documentedErrors = {
        @DocumentedError(
            message = "The obtained allocation was not valid because mutually exclusive memory objects have "
                + "overlapping address ranges. The allocator is not working.",
            explanation = "When checking the result of a memory allocation, two memory objects linked with "
                + "an exclusion in the MEG were allocated in overlapping memory spaces. The error is caused "
                + "by an invalid memory allocation algorithm and should be corrected in the source code."),
        @DocumentedError(
            message = "The obtained allocation was not valid because there were unaligned memory objects. "
                + "The allocator is not working.",
            explanation = "When checking the result of a memory allocation, some memory objects were found "
                + "not to respect the Dala alignment parameter. The error is caused by an invalid memory "
                + "allocation algorithm and should be corrected in the source code.") },

    seeAlso = {
        "**Memory Allocation Algorithms**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. Pre-and "
            + "post-scheduling memory allocation strategies on MPSoCs. In Electronic System Level Synthesis "
            + "Conference (ESLsyn), 2013.",
        "**Distributed Memory Allocation**: Karol Desnos, Maxime Pelcat, "
            + "Jean-François Nezan, and Slaheddine Aridhi. Distributed memory allocation technique for "
            + "synchronous dataflow graphs. In Signal Processing System (SiPS), Workshop on, pages 1–6. "
            + "IEEE, 2016.",
        "**Broadcast Merging**: K. Desnos, M. Pelcat, J.-F. Nezan, and S. Aridhi. "
            + "Memory analysis and optimized allocation of dataflow applications on shared-memory "
            + "MPSoCs. Journal of Signal Processing Systems, Springer, 2014." })
public class MemoryAllocatorTask extends AbstractTaskImplementation {

  public static final String PARAM_VERBOSE                      = "Verbose";
  public static final String VALUE_TRUE_FALSE_DEFAULT           = "? C {True, False}";
  public static final String VALUE_TRUE                         = "True";
  public static final String PARAM_ALLOCATORS                   = "Allocator(s)";
  public static final String VALUE_ALLOCATORS_DEFAULT           = "BestFit";
  public static final String VALUE_ALLOCATORS_BASIC             = "Basic";
  public static final String VALUE_ALLOCATORS_BEST_FIT          = "BestFit";
  public static final String VALUE_ALLOCATORS_FIRST_FIT         = "FirstFit";
  public static final String VALUE_ALLOCATORS_DE_GREEF          = "DeGreef";
  public static final String PARAM_XFIT_ORDER                   = "Best/First Fit order";
  public static final String VALUE_XFIT_ORDER_DEFAULT           = "LargestFirst";
  public static final String VALUE_XFIT_ORDER_APPROX_STABLE_SET = "ApproxStableSet";
  public static final String VALUE_XFIT_ORDER_LARGEST_FIRST     = "LargestFirst";
  public static final String VALUE_XFIT_ORDER_SHUFFLE           = "Shuffle";
  public static final String VALUE_XFIT_ORDER_EXACT_STABLE_SET  = "ExactStableSet";
  // public static final String VALUE_XFIT_ORDER_SCHEDULING = "Scheduling";
  public static final String PARAM_NB_SHUFFLE                    = "Nb of Shuffling Tested";
  public static final String VALUE_NB_SHUFFLE_DEFAULT            = "10";
  public static final String PARAM_ALIGNMENT                     = "Data alignment";
  public static final String VALUE_ALIGNEMENT_NONE               = "None";
  public static final String VALUE_ALIGNEMENT_DATA               = "Data";
  public static final String VALUE_ALIGNEMENT_FIXED              = "Fixed:=";
  public static final String VALUE_ALIGNEMENT_DEFAULT            = VALUE_ALIGNEMENT_FIXED + 8;
  public static final String PARAM_DISTRIBUTION_POLICY           = "Distribution";
  public static final String VALUE_DISTRIBUTION_SHARED_ONLY      = "SharedOnly";
  public static final String VALUE_DISTRIBUTION_DISTRIBUTED_ONLY = "DistributedOnly";
  public static final String VALUE_DISTRIBUTION_MIXED            = "Mixed";

  /**
   * Mixed Policy, but preserving all merged operations.
   */
  public static final String VALUE_DISTRIBUTION_MIXED_MERGED = "MixedMerged";

  public static final String VALUE_DISTRIBUTION_DEFAULT = "? C {" + VALUE_DISTRIBUTION_SHARED_ONLY + ", "
      + VALUE_DISTRIBUTION_MIXED + ", " + VALUE_DISTRIBUTION_DISTRIBUTED_ONLY + ", " + VALUE_DISTRIBUTION_MIXED_MERGED
      + "}";

  protected Logger          logger = PreesmLogger.getLogger();
  private String            valueAllocator;
  protected String          valueDistribution;
  protected boolean         verbose;
  protected long            alignment;
  private int               nbShuffle;
  private Order             ordering;
  protected MemoryAllocator allocator;

  /**
   * This method retrieves the value of task parameters from the workflow and stores them in local protected attributes.
   * Some parameter {@link String} are also interpreted by this method (eg. {@link #verbose}, {@link #allocator}).
   *
   * @param parameters
   *          the parameter {@link Map} given to the {@link #execute(Map, Map, IProgressMonitor, Workflow) execute()}
   *          method.
   */
  protected void init(final Map<String, String> parameters) {
    // Retrieve parameters from workflow
    final String valueVerbose = parameters.get(PARAM_VERBOSE);
    final String valueXFitOrder = parameters.get(PARAM_XFIT_ORDER);
    final String valueNbShuffle = parameters.get(PARAM_NB_SHUFFLE);
    this.valueAllocator = parameters.get(PARAM_ALLOCATORS);
    this.valueDistribution = parameters.get(PARAM_DISTRIBUTION_POLICY);

    this.verbose = valueVerbose.equals(VALUE_TRUE);

    // Correct default distribution policy
    if (this.valueDistribution.equals(VALUE_DISTRIBUTION_DEFAULT)) {
      this.valueDistribution = VALUE_DISTRIBUTION_SHARED_ONLY;
    }

    // Retrieve the alignment param
    final String valueAlignment = parameters.get(PARAM_ALIGNMENT);

    this.alignment = switch (valueAlignment.substring(0, Math.min(valueAlignment.length(), 7))) {
      case VALUE_ALIGNEMENT_NONE -> -1;
      case VALUE_ALIGNEMENT_DATA -> 0;
      case VALUE_ALIGNEMENT_FIXED -> Long.parseLong(valueAlignment.substring(7));
      default -> -1;
    };

    if (this.verbose) {
      this.logger.log(Level.INFO, () -> "Allocation with alignment:=" + this.alignment + " bits.");
    }

    // Retrieve the ordering policies to test
    this.nbShuffle = 0;
    this.ordering = switch (valueXFitOrder) {
      case VALUE_XFIT_ORDER_SHUFFLE -> {
        this.nbShuffle = Integer.decode(valueNbShuffle);
        yield Order.SHUFFLE;
      }
      case VALUE_XFIT_ORDER_LARGEST_FIRST -> Order.LARGEST_FIRST;
      case VALUE_XFIT_ORDER_APPROX_STABLE_SET -> Order.STABLE_SET;
      case VALUE_XFIT_ORDER_EXACT_STABLE_SET -> Order.EXACT_STABLE_SET;
      // case MemoryAllocatorTask.VALUE_XFIT_ORDER_SCHEDULING -> Order.SCHEDULING; // Not supported anymore
      default -> throw new IllegalArgumentException(fitOrderNameErrorMessage());
    };
  }

  private String fitOrderNameErrorMessage() {
    final StringBuilder errorStringBuilder = new StringBuilder();
    errorStringBuilder.append("Unrecognized " + PARAM_XFIT_ORDER + " parameter. Supported parameters are:\n");
    errorStringBuilder.append(VALUE_XFIT_ORDER_SHUFFLE + "\n");
    errorStringBuilder.append(VALUE_XFIT_ORDER_LARGEST_FIRST + "\n");
    errorStringBuilder.append(VALUE_XFIT_ORDER_APPROX_STABLE_SET + "\n");
    errorStringBuilder.append(VALUE_XFIT_ORDER_EXACT_STABLE_SET + "\n");
    // errorStringBuilder.append(MemoryAllocatorTask.VALUE_XFIT_ORDER_SCHEDULING); // Not supported anymore
    return errorStringBuilder.toString();
  }

  /**
   * Based on allocators specified in the task parameters, and stored in the {@link #allocator} attribute, this method
   * instantiate the {@link MemoryAllocator} that is to be executed on the given {@link MemoryExclusionGraph MEG}.
   *
   * @param memEx
   *          the {@link MemoryExclusionGraph MEG} to allocate.
   */
  protected void createAllocator(final MemoryExclusionGraph memEx) {
    // Create all allocators

    this.allocator = switch (valueAllocator) {
      case VALUE_ALLOCATORS_BASIC -> new BasicAllocator(memEx);
      case VALUE_ALLOCATORS_FIRST_FIT -> {
        final OrderedAllocator alloc = new FirstFitAllocator(memEx);
        alloc.setNbShuffle(this.nbShuffle);
        alloc.setOrder(this.ordering);
        yield alloc;
      }
      case VALUE_ALLOCATORS_BEST_FIT -> {
        final OrderedAllocator alloc = new BestFitAllocator(memEx);
        alloc.setNbShuffle(this.nbShuffle);
        alloc.setOrder(this.ordering);
        yield alloc;
      }
      case VALUE_ALLOCATORS_DE_GREEF -> new DeGreefAllocator(memEx);
      default -> throw new IllegalArgumentException(allocatorNameErrorMessage());
    };

    this.allocator.setAlignment(this.alignment);
  }

  private String allocatorNameErrorMessage() {
    final StringBuilder errorStringBuilder = new StringBuilder();
    errorStringBuilder.append("Unrecognized Allocator name. Supported parameters are:\n");
    errorStringBuilder.append(VALUE_ALLOCATORS_BASIC + "\n");
    errorStringBuilder.append(VALUE_ALLOCATORS_BEST_FIT + "\n");
    errorStringBuilder.append(VALUE_ALLOCATORS_DE_GREEF + "\n");
    errorStringBuilder.append(VALUE_ALLOCATORS_DEFAULT + "\n");
    errorStringBuilder.append(VALUE_ALLOCATORS_FIRST_FIT);
    return errorStringBuilder.toString();
  }

  /**
   * Allocate with.
   *
   * @param allocator
   *          the allocator
   * @throws PreesmException
   *           the workflow exception
   */
  protected void allocateWith(final MemoryAllocator allocator) {
    long tStart;
    final StringBuilder sb = new StringBuilder(allocator.getClass().getSimpleName());

    if (allocator instanceof final OrderedAllocator orderedAllocator) {
      sb.append("(" + orderedAllocator.getOrder());
      if (orderedAllocator.getOrder() == Order.SHUFFLE) {
        sb.append(":" + orderedAllocator.getNbShuffle());
      }
      sb.append(")");
    }
    final String sAllocator = sb.toString();
    if (this.verbose) {
      this.logger.log(Level.INFO, () -> "Starting allocation with " + sAllocator);
    }

    tStart = System.currentTimeMillis();
    allocator.allocate();
    final long tFinish = System.currentTimeMillis();

    // Check the correct allocation
    try {
      if (!allocator.checkAllocation().isEmpty()) {
        throw new PreesmRuntimeException("The obtained allocation was not valid because mutually"
            + " exclusive memory objects have overlapping address ranges." + " The allocator is not working.\n"
            + allocator.checkAllocation());
      }
    } catch (final RuntimeException e) {
      throw new PreesmRuntimeException(e.getMessage());
    }

    if (!allocator.checkAlignment().isEmpty()) {
      throw new PreesmRuntimeException("The obtained allocation was not valid because there were"
          + " unaligned memory objects. The allocator is not working.\n" + allocator.checkAlignment());
    }

    String log = computeLog(allocator, tStart, sAllocator, tFinish);

    if ((allocator instanceof final OrderedAllocator orderedAllocator)
        && (orderedAllocator.getOrder() == Order.SHUFFLE)) {
      orderedAllocator.setPolicy(Policy.WORST);
      log += " worst: " + allocator.getMemorySizeInByte();

      orderedAllocator.setPolicy(Policy.MEDIANE);
      log += "(med: " + allocator.getMemorySizeInByte();

      orderedAllocator.setPolicy(Policy.AVERAGE);
      log += " avg: " + allocator.getMemorySizeInByte() + ")";

      orderedAllocator.setPolicy(Policy.BEST);
    }

    this.logger.log(Level.INFO, log);
  }

  private String computeLog(final MemoryAllocator allocator, final long tStart, final String sAllocator,
      final long tFinish) {
    String unit = "bytes";
    double size = allocator.getMemorySizeInByte();
    if (size > 1024) {
      size /= 1024.0;
      unit = "kBytes";
      if (size > 1024) {
        size /= 1024.0;
        unit = "MBytes";
        if (size > 1024) {
          size /= 1024.0;
          unit = "GBytes";
        }
      }
    }
    return sAllocator + " allocates " + size + " " + unit + " in " + (tFinish - tStart) + " ms.";
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(PARAM_VERBOSE, VALUE_TRUE_FALSE_DEFAULT);
    parameters.put(PARAM_ALLOCATORS, VALUE_ALLOCATORS_DEFAULT);
    parameters.put(PARAM_XFIT_ORDER, VALUE_XFIT_ORDER_DEFAULT);
    parameters.put(PARAM_NB_SHUFFLE, VALUE_NB_SHUFFLE_DEFAULT);
    parameters.put(PARAM_ALIGNMENT, VALUE_ALIGNEMENT_DEFAULT);
    parameters.put(PARAM_DISTRIBUTION_POLICY, VALUE_DISTRIBUTION_DEFAULT);
    return parameters;
  }

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {
    init(parameters);

    // Retrieve the input of the task
    final MemoryExclusionGraph memEx = (MemoryExclusionGraph) inputs.get("MemEx");

    // Prepare the MEG with the alignment
    MemoryAllocator.alignSubBuffers(memEx, this.alignment);

    // Get vertices before distribution
    final Set<MemoryExclusionVertex> verticesBeforeDistribution = memEx.getTotalSetOfVertices();

    // Create several MEGs according to the selected distribution policy
    // Each created MEG corresponds to a single memory bank
    // Log the distribution policy used
    if (this.verbose && !this.valueDistribution.equals(VALUE_DISTRIBUTION_SHARED_ONLY)) {
      final String msg = "Split MEG with " + this.valueDistribution + " policy";
      this.logger.log(Level.INFO, msg);
    }

    // Do the distribution
    final Map<String,
        MemoryExclusionGraph> megs = Distributor.distributeMeg(this.valueDistribution, memEx, this.alignment);

    // Log results
    if (this.verbose && !this.valueDistribution.equals(VALUE_DISTRIBUTION_SHARED_ONLY)) {
      final String msg = "Created " + megs.keySet().size() + " MemExes";
      this.logger.log(Level.INFO, msg);
      for (final Entry<String, MemoryExclusionGraph> entry : megs.entrySet()) {
        final double density = entry.getValue().edgeSet().size()
            / ((entry.getValue().vertexSet().size() * (entry.getValue().vertexSet().size() - 1)) / 2.0);
        final String msg2 = "Memex(" + entry.getKey() + "): " + entry.getValue().vertexSet().size()
            + " vertices, density=" + density + ":: " + entry.getValue().getTotalSetOfVertices();
        this.logger.log(Level.INFO, msg2);
      }
    }

    // Get total set of vertices after distribution
    final Set<MemoryExclusionVertex> verticesAfterDistribution = memEx.getTotalSetOfVertices();
    final Set<MemoryExclusionVertex> verticesInMegs = new LinkedHashSet<>();
    megs.forEach((bank, meg) -> verticesInMegs.addAll(meg.getTotalSetOfVertices()));

    // Check that the total number of vertices is unchanged
    if (!this.valueDistribution.equals(VALUE_DISTRIBUTION_SHARED_ONLY)
        && ((verticesBeforeDistribution.size() != verticesAfterDistribution.size())
            || (verticesBeforeDistribution.size() != verticesInMegs.size()))) {
      // Compute the list of missing vertices
      verticesBeforeDistribution.removeAll(verticesInMegs);
      final String msg = "Problem in the MEG distribution, some memory objects were lost during the distribution.\n"
          + verticesBeforeDistribution + "\nContact Preesm developers to solve this issue.";
      throw new PreesmRuntimeException(msg);
    }

    for (final Entry<String, MemoryExclusionGraph> entry : megs.entrySet()) {

      final String memoryBank = entry.getKey();
      final MemoryExclusionGraph meg = entry.getValue();

      createAllocator(meg);

      if (this.verbose) {
        final String msg = "Heat up MemEx for " + memoryBank + " memory bank.";
        this.logger.log(Level.INFO, msg);
      }

      meg.vertexSet().stream().forEach(meg::getAdjacentVertexOf);

      allocateWith(this.allocator);
    }

    final Map<String, Object> output = new LinkedHashMap<>();
    output.put("MEGs", megs);
    return output;
  }

  @Override
  public String monitorMessage() {
    return "Allocating MemEx";
  }

}
