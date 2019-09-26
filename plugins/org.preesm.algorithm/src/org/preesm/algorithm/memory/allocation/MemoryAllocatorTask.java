/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2013 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Daniel Madroñal [daniel.madronal@upm.es] (2019)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
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
package org.preesm.algorithm.memory.allocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.memory.allocation.OrderedAllocator.Order;
import org.preesm.algorithm.memory.allocation.OrderedAllocator.Policy;
import org.preesm.algorithm.memory.distributed.Distributor;
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
        @Parameter(name = "Verbose", description = "Verbosity of the task.",
            values = { @Value(name = "True", effect = "Detailed statistics of the allocation process are logged"),
                @Value(name = "False", effect = "Logged information is kept to a minimum") }),
        @Parameter(name = "Allocator(s)",
            description = "Specify which memory allocation algorithm(s) should be used. If the string value of the "
                + "parameters contains several algorithm names, all will be executed one by one.",
            values = { @Value(name = "Basic",
                effect = "Each memory object is allocated in a dedicated memory space. Memory allocated for a given "
                    + "object is not reused for other."),
                @Value(name = "BestFit",
                    effect = "Memory objects are allocated one by one; allocating each object to the available space "
                        + "in memory whose size is the closest to the size of the allocated object. If MEG exclusions"
                        + " permit it, memory allocated for a memory object may be reused for others."),
                @Value(name = "FirstFit",
                    effect = "Memory objects are allocated one by one; allocating each object to the first available "
                        + "space in memory whose size is the large enough to allocate the object. If MEG exclusions"
                        + " permit it, memory allocated for a memory object may be reused for others."),
                @Value(name = "DeGreef",
                    effect = "Algorithm adapted from DeGreef (1997)}. If MEG exclusions permit it, memory allocated"
                        + " for a memory object may be reused for others.") }),
        @Parameter(name = "Distribution",
            description = "Specify which memory architecture should be used to allocate the memory.",
            values = { @Value(name = "SharedOnly",
                effect = "(Default) All memory objects are allocated in a single memory bank accessible to all PE."),
                @Value(name = "DistributedOnly",
                    effect = "Each PE is associated to a private memory bank that no other PE can access. "
                        + "(Currently supported only in the MPPA code generation.)"),
                @Value(name = "Mixed",
                    effect = "Both private memory banks and a shared memory can be used for allocating memory."),
                @Value(name = "MixedMerged",
                    effect = "Same as mixed, but the memory allocation algorithm favors buffer merging over"
                        + " memory distribution.") }),
        @Parameter(name = "Best/First Fit order",
            description = "When using FirstFit or BestFit memory allocators, this parameter specifies in which order"
                + " the memory objects will be fed to the allocation algorithm. If the string value associated to the "
                + "parameters contains several order names, all will be executed one by one.",
            values = {
                @Value(name = "ApproxStableSet",
                    effect = "Memory objects are sorted into disjoint stable sets. Stable sets are formed one after the"
                        + " other, each with the largest possible number of object. Memory objects are fed to the "
                        + "allocator set by set and in the largest first order within each stable set."),
                @Value(name = "ExactStableSet",
                    effect = "Similar to 'ApproxStableSet'. Stable set are built using an exact algorithm instead of a"
                        + " heuristic."),
                @Value(name = "LargestFirst",
                    effect = "Memory objects are allocated in decreasing order of their size."),
                @Value(name = "Shuffle",
                    effect = "Memory objects are allocated in a random order. Using the 'Nb of Shuffling Tested' "
                        + "parameter, it is possible to test several random orders and only keep the best memory"
                        + " allocation."),
                @Value(name = "Scheduling",
                    effect = "Memory objects are allocated in scheduling order of their 'birth'. The 'birth' of a "
                        + "memory object is the instant when its memory would be allocated by a dynamic allocator. "
                        + "This option can be used to mimic the behavior of a dynamic allocator. (Only available for "
                        + "MEG updated with scheduling information).") }),
        @Parameter(name = "Data alignment",
            description = "Option used to force the allocation of buffers (i.e. Memory objects) with aligned addresses."
                + " The data alignment property should always have the same value as the one set in the properties of "
                + "the Memory Scripts task.",
            values = { @Value(name = "None", effect = "No special care is taken to align the buffers in memory."),
                @Value(name = "Data",
                    effect = "All buffers are aligned on addresses that are multiples of their size. For example, a 4 "
                        + "bytes integer is aligned on 4 bytes address."),
                @Value(name = "Fixed:=n",
                    effect = "Where $$n\\in \\mathbb{N}^*$$. This forces the allocation algorithm to align all buffers"
                        + " on addresses that are multiples of n bytes.") }),
        @Parameter(name = "Nb of Shuffling Tested",
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

  /** The Constant PARAM_VERBOSE. */
  private static final String PARAM_VERBOSE = "Verbose";

  /** The Constant VALUE_TRUE_FALSE_DEFAULT. */
  private static final String VALUE_TRUE_FALSE_DEFAULT = "? C {True, False}";

  /** The Constant VALUE_TRUE. */
  private static final String VALUE_TRUE = "True";

  /** The Constant PARAM_ALLOCATORS. */
  private static final String PARAM_ALLOCATORS = "Allocator(s)";

  /** The Constant VALUE_ALLOCATORS_DEFAULT. */
  private static final String VALUE_ALLOCATORS_DEFAULT = "{?,?,...} C {Basic, BestFit, FirstFit, DeGreef}";

  /** The Constant VALUE_ALLOCATORS_BASIC. */
  private static final String VALUE_ALLOCATORS_BASIC = "Basic";

  /** The Constant VALUE_ALLOCATORS_BEST_FIT. */
  private static final String VALUE_ALLOCATORS_BEST_FIT = "BestFit";

  /** The Constant VALUE_ALLOCATORS_FIRST_FIT. */
  private static final String VALUE_ALLOCATORS_FIRST_FIT = "FirstFit";

  /** The Constant VALUE_ALLOCATORS_DE_GREEF. */
  private static final String VALUE_ALLOCATORS_DE_GREEF = "DeGreef";

  /** The Constant PARAM_XFIT_ORDER. */
  private static final String PARAM_XFIT_ORDER = "Best/First Fit order";

  /** The Constant VALUE_XFIT_ORDER_DEFAULT. */
  private static final String VALUE_XFIT_ORDER_DEFAULT = "{?,?,...} C "
      + "{ApproxStableSet, ExactStableSet, LargestFirst, Shuffle, Scheduling}";

  /** The Constant VALUE_XFIT_ORDER_APPROX_STABLE_SET. */
  private static final String VALUE_XFIT_ORDER_APPROX_STABLE_SET = "ApproxStableSet";

  /** The Constant VALUE_XFIT_ORDER_LARGEST_FIRST. */
  private static final String VALUE_XFIT_ORDER_LARGEST_FIRST = "LargestFirst";

  /** The Constant VALUE_XFIT_ORDER_SHUFFLE. */
  private static final String VALUE_XFIT_ORDER_SHUFFLE = "Shuffle";

  /** The Constant VALUE_XFIT_ORDER_EXACT_STABLE_SET. */
  private static final String VALUE_XFIT_ORDER_EXACT_STABLE_SET = "ExactStableSet";

  /** The Constant VALUE_XFIT_ORDER_SCHEDULING. */
  private static final String VALUE_XFIT_ORDER_SCHEDULING = "Scheduling";

  /** The Constant PARAM_NB_SHUFFLE. */
  private static final String PARAM_NB_SHUFFLE = "Nb of Shuffling Tested";

  /** The Constant VALUE_NB_SHUFFLE_DEFAULT. */
  private static final String VALUE_NB_SHUFFLE_DEFAULT = "10";

  /** The Constant PARAM_ALIGNMENT. */
  public static final String PARAM_ALIGNMENT = "Data alignment";

  /** The Constant VALUE_ALIGNEMENT_NONE. */
  public static final String VALUE_ALIGNEMENT_NONE = "None";

  /** The Constant VALUE_ALIGNEMENT_DATA. */
  public static final String VALUE_ALIGNEMENT_DATA = "Data";

  /** The Constant VALUE_ALIGNEMENT_FIXED. */
  public static final String VALUE_ALIGNEMENT_FIXED = "Fixed:=";

  /** The Constant VALUE_ALIGNEMENT_DEFAULT. */
  public static final String VALUE_ALIGNEMENT_DEFAULT = "? C {None, Data, Fixed:=<nbBytes>}";

  /** The Constant PARAM_DISTRIBUTION_POLICY. */
  public static final String PARAM_DISTRIBUTION_POLICY = "Distribution";

  /** The Constant VALUE_DISTRIBUTION_SHARED_ONLY. */
  public static final String VALUE_DISTRIBUTION_SHARED_ONLY = "SharedOnly";

  /** The Constant VALUE_DISTRIBUTION_DISTRIBUTED_ONLY. */
  public static final String VALUE_DISTRIBUTION_DISTRIBUTED_ONLY = "DistributedOnly";

  /** The Constant VALUE_DISTRIBUTION_MIXED. */
  public static final String VALUE_DISTRIBUTION_MIXED        = "Mixed";
  /**
   * Mixed Policy, but preserving all merged operations.
   */
  public static final String VALUE_DISTRIBUTION_MIXED_MERGED = "MixedMerged";

  /** The Constant VALUE_DISTRIBUTION_DEFAULT. */
  public static final String VALUE_DISTRIBUTION_DEFAULT = "? C {" + MemoryAllocatorTask.VALUE_DISTRIBUTION_SHARED_ONLY
      + ", " + MemoryAllocatorTask.VALUE_DISTRIBUTION_MIXED + ", "
      + MemoryAllocatorTask.VALUE_DISTRIBUTION_DISTRIBUTED_ONLY + ", "
      + MemoryAllocatorTask.VALUE_DISTRIBUTION_MIXED_MERGED + "}";

  /** The logger. */
  // Rem: Logger is used to display messages in the console
  protected Logger logger = PreesmLogger.getLogger();

  /** The value verbose. */
  // Shared attributes
  private String valueVerbose;

  /** The value allocators. */
  private String valueAllocators;

  /** The value X fit order. */
  private String valueXFitOrder;

  /** The value nb shuffle. */
  private String valueNbShuffle;

  /** The value distribution. */
  protected String valueDistribution;

  /** The verbose. */
  protected boolean verbose;

  /** The value alignment. */
  private String valueAlignment;

  /** The alignment. */
  protected long alignment;

  /** The nb shuffle. */
  private int nbShuffle;

  /** The ordering. */
  private List<Order> ordering;

  /** The allocators. */
  protected List<MemoryAllocator> allocators;

  /**
   * This method retrieves the value of task parameters from the workflow and stores them in local protected attributes.
   * Some parameter {@link String} are also interpreted by this method (eg. {@link #verbose}, {@link #allocators}).
   *
   * @param parameters
   *          the parameter {@link Map} given to the {@link #execute(Map, Map, IProgressMonitor, Workflow) execute()}
   *          method.
   */
  protected void init(final Map<String, String> parameters) {
    // Retrieve parameters from workflow
    this.valueVerbose = parameters.get(MemoryAllocatorTask.PARAM_VERBOSE);
    this.valueAllocators = parameters.get(MemoryAllocatorTask.PARAM_ALLOCATORS);
    this.valueXFitOrder = parameters.get(MemoryAllocatorTask.PARAM_XFIT_ORDER);
    this.valueNbShuffle = parameters.get(MemoryAllocatorTask.PARAM_NB_SHUFFLE);
    this.valueDistribution = parameters.get(MemoryAllocatorTask.PARAM_DISTRIBUTION_POLICY);

    this.verbose = this.valueVerbose.equals(MemoryAllocatorTask.VALUE_TRUE);

    // Correct default distribution policy
    if (this.valueDistribution.equals(MemoryAllocatorTask.VALUE_DISTRIBUTION_DEFAULT)) {
      this.valueDistribution = MemoryAllocatorTask.VALUE_DISTRIBUTION_SHARED_ONLY;
    }

    // Retrieve the alignment param
    this.valueAlignment = parameters.get(MemoryAllocatorTask.PARAM_ALIGNMENT);

    switch (this.valueAlignment.substring(0, Math.min(this.valueAlignment.length(), 7))) {
      case VALUE_ALIGNEMENT_NONE:
        this.alignment = -1;
        break;
      case VALUE_ALIGNEMENT_DATA:
        this.alignment = 0;
        break;
      case VALUE_ALIGNEMENT_FIXED:
        final String fixedValue = this.valueAlignment.substring(7);
        this.alignment = Long.parseLong(fixedValue);
        break;
      default:
        this.alignment = -1;
    }
    if (this.verbose) {
      this.logger.log(Level.INFO, () -> "Allocation with alignment:=" + this.alignment + ".");
    }

    // Retrieve the ordering policies to test
    this.nbShuffle = 0;
    this.ordering = new ArrayList<>();
    if (this.valueXFitOrder.contains(MemoryAllocatorTask.VALUE_XFIT_ORDER_SHUFFLE)) {
      this.nbShuffle = Integer.decode(this.valueNbShuffle);
      this.ordering.add(Order.SHUFFLE);
    }
    if (this.valueXFitOrder.contains(MemoryAllocatorTask.VALUE_XFIT_ORDER_LARGEST_FIRST)) {
      this.ordering.add(Order.LARGEST_FIRST);
    }
    if (this.valueXFitOrder.contains(MemoryAllocatorTask.VALUE_XFIT_ORDER_APPROX_STABLE_SET)) {
      this.ordering.add(Order.STABLE_SET);
    }
    if (this.valueXFitOrder.contains(MemoryAllocatorTask.VALUE_XFIT_ORDER_EXACT_STABLE_SET)) {
      this.ordering.add(Order.EXACT_STABLE_SET);
    }
    if (this.valueXFitOrder.contains(MemoryAllocatorTask.VALUE_XFIT_ORDER_SCHEDULING)) {
      this.ordering.add(Order.SCHEDULING);
    }

  }

  /**
   * Based on allocators specified in the task parameters, and stored in the {@link #allocators} attribute, this method
   * instantiate the {@link MemoryAllocator} that are to be executed on the given {@link MemoryExclusionGraph MEG}.
   *
   * @param memEx
   *          the {@link MemoryExclusionGraph MEG} to allocate.
   */
  protected void createAllocators(final MemoryExclusionGraph memEx) {
    // Create all allocators
    this.allocators = new ArrayList<>();
    if (this.valueAllocators.contains(MemoryAllocatorTask.VALUE_ALLOCATORS_BASIC)) {
      final MemoryAllocator alloc = new BasicAllocator(memEx);
      alloc.setAlignment(this.alignment);
      this.allocators.add(alloc);
    }
    if (this.valueAllocators.contains(MemoryAllocatorTask.VALUE_ALLOCATORS_FIRST_FIT)) {
      for (final Order o : this.ordering) {
        final OrderedAllocator alloc = new FirstFitAllocator(memEx);
        alloc.setNbShuffle(this.nbShuffle);
        alloc.setOrder(o);
        alloc.setAlignment(this.alignment);
        this.allocators.add(alloc);
      }
    }
    if (this.valueAllocators.contains(MemoryAllocatorTask.VALUE_ALLOCATORS_BEST_FIT)) {
      for (final Order o : this.ordering) {
        final OrderedAllocator alloc = new BestFitAllocator(memEx);
        alloc.setNbShuffle(this.nbShuffle);
        alloc.setOrder(o);
        alloc.setAlignment(this.alignment);
        this.allocators.add(alloc);
      }
    }
    if (this.valueAllocators.contains(MemoryAllocatorTask.VALUE_ALLOCATORS_DE_GREEF)) {
      final MemoryAllocator alloc = new DeGreefAllocator(memEx);
      alloc.setAlignment(this.alignment);
      this.allocators.add(alloc);
    }
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

    if (allocator instanceof OrderedAllocator) {
      sb.append("(" + ((OrderedAllocator) allocator).getOrder());
      if (((OrderedAllocator) allocator).getOrder() == Order.SHUFFLE) {
        sb.append(":" + ((OrderedAllocator) allocator).getNbShuffle());
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

    if ((allocator instanceof OrderedAllocator) && (((OrderedAllocator) allocator).getOrder() == Order.SHUFFLE)) {
      ((OrderedAllocator) allocator).setPolicy(Policy.worst);
      log += " worst: " + allocator.getMemorySize();

      ((OrderedAllocator) allocator).setPolicy(Policy.mediane);
      log += "(med: " + allocator.getMemorySize();

      ((OrderedAllocator) allocator).setPolicy(Policy.average);
      log += " avg: " + allocator.getMemorySize() + ")";

      ((OrderedAllocator) allocator).setPolicy(Policy.best);
    }

    this.logger.log(Level.INFO, log);
  }

  private String computeLog(final MemoryAllocator allocator, final long tStart, final String sAllocator,
      final long tFinish) {
    String unit = "bytes";
    double size = allocator.getMemorySize();
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
    parameters.put(MemoryAllocatorTask.PARAM_VERBOSE, MemoryAllocatorTask.VALUE_TRUE_FALSE_DEFAULT);
    parameters.put(MemoryAllocatorTask.PARAM_ALLOCATORS, MemoryAllocatorTask.VALUE_ALLOCATORS_DEFAULT);
    parameters.put(MemoryAllocatorTask.PARAM_XFIT_ORDER, MemoryAllocatorTask.VALUE_XFIT_ORDER_DEFAULT);
    parameters.put(MemoryAllocatorTask.PARAM_NB_SHUFFLE, MemoryAllocatorTask.VALUE_NB_SHUFFLE_DEFAULT);
    parameters.put(MemoryAllocatorTask.PARAM_ALIGNMENT, MemoryAllocatorTask.VALUE_ALIGNEMENT_DEFAULT);
    parameters.put(MemoryAllocatorTask.PARAM_DISTRIBUTION_POLICY, MemoryAllocatorTask.VALUE_DISTRIBUTION_DEFAULT);
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
    if (this.verbose && !this.valueDistribution.equals(MemoryAllocatorTask.VALUE_DISTRIBUTION_SHARED_ONLY)) {
      final String msg = "Split MEG with " + this.valueDistribution + " policy";
      this.logger.log(Level.INFO, msg);
    }

    // Do the distribution
    final Map<String,
        MemoryExclusionGraph> megs = Distributor.distributeMeg(this.valueDistribution, memEx, this.alignment);

    // Log results
    if (this.verbose && !this.valueDistribution.equals(MemoryAllocatorTask.VALUE_DISTRIBUTION_SHARED_ONLY)) {
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
    if (!this.valueDistribution.equals(MemoryAllocatorTask.VALUE_DISTRIBUTION_SHARED_ONLY)
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

      createAllocators(meg);

      if (this.verbose) {
        final String msg = "Heat up MemEx for " + memoryBank + " memory bank.";
        this.logger.log(Level.INFO, msg);
      }
      for (final MemoryExclusionVertex vertex : meg.vertexSet()) {
        meg.getAdjacentVertexOf(vertex);
      }

      for (final MemoryAllocator allocator : this.allocators) {
        allocateWith(allocator);
      }
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
