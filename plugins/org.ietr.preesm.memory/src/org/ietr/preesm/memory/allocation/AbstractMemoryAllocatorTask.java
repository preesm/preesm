/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2015)
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
package org.ietr.preesm.memory.allocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.memory.allocation.OrderedAllocator.Order;
import org.ietr.preesm.memory.allocation.OrderedAllocator.Policy;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractMemoryAllocatorTask.
 */
public abstract class AbstractMemoryAllocatorTask extends AbstractTaskImplementation {

  /** The Constant PARAM_VERBOSE. */
  public static final String PARAM_VERBOSE = "Verbose";

  /** The Constant VALUE_TRUE_FALSE_DEFAULT. */
  public static final String VALUE_TRUE_FALSE_DEFAULT = "? C {True, False}";

  /** The Constant VALUE_TRUE. */
  public static final String VALUE_TRUE = "True";

  /** The Constant VALUE_FALSE. */
  public static final String VALUE_FALSE = "False";

  /** The Constant PARAM_ALLOCATORS. */
  public static final String PARAM_ALLOCATORS = "Allocator(s)";

  /** The Constant VALUE_ALLOCATORS_DEFAULT. */
  public static final String VALUE_ALLOCATORS_DEFAULT = "{?,?,...} C {Basic, BestFit, FirstFit, DeGreef}";

  /** The Constant VALUE_ALLOCATORS_BASIC. */
  public static final String VALUE_ALLOCATORS_BASIC = "Basic";

  /** The Constant VALUE_ALLOCATORS_BEST_FIT. */
  public static final String VALUE_ALLOCATORS_BEST_FIT = "BestFit";

  /** The Constant VALUE_ALLOCATORS_FIRST_FIT. */
  public static final String VALUE_ALLOCATORS_FIRST_FIT = "FirstFit";

  /** The Constant VALUE_ALLOCATORS_DE_GREEF. */
  public static final String VALUE_ALLOCATORS_DE_GREEF = "DeGreef";

  /** The Constant PARAM_XFIT_ORDER. */
  public static final String PARAM_XFIT_ORDER = "Best/First Fit order";

  /** The Constant VALUE_XFIT_ORDER_DEFAULT. */
  public static final String VALUE_XFIT_ORDER_DEFAULT = "{?,?,...} C "
      + "{ApproxStableSet, ExactStableSet, LargestFirst, Shuffle, Scheduling}";

  /** The Constant VALUE_XFIT_ORDER_APPROX_STABLE_SET. */
  public static final String VALUE_XFIT_ORDER_APPROX_STABLE_SET = "ApproxStableSet";

  /** The Constant VALUE_XFIT_ORDER_LARGEST_FIRST. */
  public static final String VALUE_XFIT_ORDER_LARGEST_FIRST = "LargestFirst";

  /** The Constant VALUE_XFIT_ORDER_SHUFFLE. */
  public static final String VALUE_XFIT_ORDER_SHUFFLE = "Shuffle";

  /** The Constant VALUE_XFIT_ORDER_EXACT_STABLE_SET. */
  public static final String VALUE_XFIT_ORDER_EXACT_STABLE_SET = "ExactStableSet";

  /** The Constant VALUE_XFIT_ORDER_SCHEDULING. */
  public static final String VALUE_XFIT_ORDER_SCHEDULING = "Scheduling";

  /** The Constant PARAM_NB_SHUFFLE. */
  public static final String PARAM_NB_SHUFFLE = "Nb of Shuffling Tested";

  /** The Constant VALUE_NB_SHUFFLE_DEFAULT. */
  public static final String VALUE_NB_SHUFFLE_DEFAULT = "10";

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
  public static final String VALUE_DISTRIBUTION_DEFAULT = "? C {"
      + AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_SHARED_ONLY + ", "
      + AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_MIXED + ", "
      + AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DISTRIBUTED_ONLY + ", "
      + AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_MIXED_MERGED + "}";

  /** The logger. */
  // Rem: Logger is used to display messages in the console
  protected Logger logger = WorkflowLogger.getLogger();

  /** The value verbose. */
  // Shared attributes
  protected String valueVerbose;

  /** The value allocators. */
  protected String valueAllocators;

  /** The value X fit order. */
  protected String valueXFitOrder;

  /** The value nb shuffle. */
  protected String valueNbShuffle;

  /** The value distribution. */
  protected String valueDistribution;

  /** The verbose. */
  protected boolean verbose;

  /** The value alignment. */
  protected String valueAlignment;

  /** The alignment. */
  protected int alignment;

  /** The nb shuffle. */
  protected int nbShuffle;

  /** The ordering. */
  protected List<Order> ordering;

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
    this.valueVerbose = parameters.get(AbstractMemoryAllocatorTask.PARAM_VERBOSE);
    this.valueAllocators = parameters.get(AbstractMemoryAllocatorTask.PARAM_ALLOCATORS);
    this.valueXFitOrder = parameters.get(AbstractMemoryAllocatorTask.PARAM_XFIT_ORDER);
    this.valueNbShuffle = parameters.get(AbstractMemoryAllocatorTask.PARAM_NB_SHUFFLE);
    this.valueDistribution = parameters.get(AbstractMemoryAllocatorTask.PARAM_DISTRIBUTION_POLICY);

    this.verbose = this.valueVerbose.equals(AbstractMemoryAllocatorTask.VALUE_TRUE);

    // Correct default distribution policy
    if (this.valueDistribution.equals(AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DEFAULT)) {
      this.valueDistribution = AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_SHARED_ONLY;
    }

    // Retrieve the alignment param
    this.valueAlignment = parameters.get(AbstractMemoryAllocatorTask.PARAM_ALIGNMENT);

    switch (this.valueAlignment.substring(0, Math.min(this.valueAlignment.length(), 7))) {
      case VALUE_ALIGNEMENT_NONE:
        this.alignment = -1;
        break;
      case VALUE_ALIGNEMENT_DATA:
        this.alignment = 0;
        break;
      case VALUE_ALIGNEMENT_FIXED:
        final String fixedValue = this.valueAlignment.substring(7);
        this.alignment = Integer.parseInt(fixedValue);
        break;
      default:
        this.alignment = -1;
    }
    if (this.verbose) {
      this.logger.log(Level.INFO, "Allocation with alignment:=" + this.alignment + ".");
    }

    // Retrieve the ordering policies to test
    this.nbShuffle = 0;
    this.ordering = new ArrayList<>();
    if (this.valueXFitOrder.contains(AbstractMemoryAllocatorTask.VALUE_XFIT_ORDER_SHUFFLE)) {
      this.nbShuffle = Integer.decode(this.valueNbShuffle);
      this.ordering.add(Order.SHUFFLE);
    }
    if (this.valueXFitOrder.contains(AbstractMemoryAllocatorTask.VALUE_XFIT_ORDER_LARGEST_FIRST)) {
      this.ordering.add(Order.LARGEST_FIRST);
    }
    if (this.valueXFitOrder.contains(AbstractMemoryAllocatorTask.VALUE_XFIT_ORDER_APPROX_STABLE_SET)) {
      this.ordering.add(Order.STABLE_SET);
    }
    if (this.valueXFitOrder.contains(AbstractMemoryAllocatorTask.VALUE_XFIT_ORDER_EXACT_STABLE_SET)) {
      this.ordering.add(Order.EXACT_STABLE_SET);
    }
    if (this.valueXFitOrder.contains(AbstractMemoryAllocatorTask.VALUE_XFIT_ORDER_SCHEDULING)) {
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
    if (this.valueAllocators.contains(AbstractMemoryAllocatorTask.VALUE_ALLOCATORS_BASIC)) {
      final MemoryAllocator alloc = new BasicAllocator(memEx);
      alloc.setAlignment(this.alignment);
      this.allocators.add(alloc);
    }
    if (this.valueAllocators.contains(AbstractMemoryAllocatorTask.VALUE_ALLOCATORS_FIRST_FIT)) {
      for (final Order o : this.ordering) {
        final OrderedAllocator alloc = new FirstFitAllocator(memEx);
        alloc.setNbShuffle(this.nbShuffle);
        alloc.setOrder(o);
        alloc.setAlignment(this.alignment);
        this.allocators.add(alloc);
      }
    }
    if (this.valueAllocators.contains(AbstractMemoryAllocatorTask.VALUE_ALLOCATORS_BEST_FIT)) {
      for (final Order o : this.ordering) {
        final OrderedAllocator alloc = new BestFitAllocator(memEx);
        alloc.setNbShuffle(this.nbShuffle);
        alloc.setOrder(o);
        alloc.setAlignment(this.alignment);
        this.allocators.add(alloc);
      }
    }
    if (this.valueAllocators.contains(AbstractMemoryAllocatorTask.VALUE_ALLOCATORS_DE_GREEF)) {
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
   * @throws WorkflowException
   *           the workflow exception
   */
  protected void allocateWith(final MemoryAllocator allocator) throws WorkflowException {
    long tStart;
    String sAllocator = allocator.getClass().getSimpleName();
    if (allocator instanceof OrderedAllocator) {
      sAllocator += "(" + ((OrderedAllocator) allocator).getOrder();
      if (((OrderedAllocator) allocator).getOrder() == Order.SHUFFLE) {
        sAllocator += ":" + ((OrderedAllocator) allocator).getNbShuffle();
      }
      sAllocator += ")";
    }

    if (this.verbose) {
      this.logger.log(Level.INFO, "Starting allocation with " + sAllocator);
    }

    tStart = System.currentTimeMillis();
    allocator.allocate();
    final long tFinish = System.currentTimeMillis();

    // Check the correct allocation
    try {
      if (!allocator.checkAllocation().isEmpty()) {
        throw new WorkflowException("The obtained allocation was not valid because mutually"
            + " exclusive memory objects have overlapping address ranges." + " The allocator is not working.\n"
            + allocator.checkAllocation());
      }
    } catch (final RuntimeException e) {
      throw new WorkflowException(e.getMessage());
    }

    if (!allocator.checkAlignment().isEmpty()) {
      throw new WorkflowException("The obtained allocation was not valid because there were"
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
    float size = allocator.getMemorySize();
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

    final String log = sAllocator + " allocates " + size + " " + unit + " in " + (tFinish - tStart) + " ms.";
    return log;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(AbstractMemoryAllocatorTask.PARAM_VERBOSE, AbstractMemoryAllocatorTask.VALUE_TRUE_FALSE_DEFAULT);
    parameters.put(AbstractMemoryAllocatorTask.PARAM_ALLOCATORS, AbstractMemoryAllocatorTask.VALUE_ALLOCATORS_DEFAULT);
    parameters.put(AbstractMemoryAllocatorTask.PARAM_XFIT_ORDER, AbstractMemoryAllocatorTask.VALUE_XFIT_ORDER_DEFAULT);
    parameters.put(AbstractMemoryAllocatorTask.PARAM_NB_SHUFFLE, AbstractMemoryAllocatorTask.VALUE_NB_SHUFFLE_DEFAULT);
    parameters.put(AbstractMemoryAllocatorTask.PARAM_ALIGNMENT, AbstractMemoryAllocatorTask.VALUE_ALIGNEMENT_DEFAULT);
    parameters.put(AbstractMemoryAllocatorTask.PARAM_DISTRIBUTION_POLICY,
        AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DEFAULT);
    return parameters;
  }
}
