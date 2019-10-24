/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
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
package org.preesm.algorithm.synthesis.memalloc;

import bsh.EvalError;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.preesm.algorithm.mapping.model.Mapping;
import org.preesm.algorithm.memalloc.model.Allocation;
import org.preesm.algorithm.memory.allocation.MemoryAllocator;
import org.preesm.algorithm.memory.allocation.tasks.MemoryAllocatorTask;
import org.preesm.algorithm.memory.allocation.tasks.MemoryScriptTask;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.algorithm.schedule.model.Schedule;
import org.preesm.algorithm.synthesis.memalloc.allocation.PiBasicAllocator;
import org.preesm.algorithm.synthesis.memalloc.allocation.PiBestFitAllocator;
import org.preesm.algorithm.synthesis.memalloc.allocation.PiDistributor;
import org.preesm.algorithm.synthesis.memalloc.allocation.PiFirstFitAllocator;
import org.preesm.algorithm.synthesis.memalloc.allocation.PiMemoryAllocator;
import org.preesm.algorithm.synthesis.memalloc.allocation.PiOrderedAllocator;
import org.preesm.algorithm.synthesis.memalloc.allocation.PiOrderedAllocator.Order;
import org.preesm.algorithm.synthesis.memalloc.allocation.PiOrderedAllocator.Policy;
import org.preesm.algorithm.synthesis.memalloc.meg.MemExUpdaterEngine;
import org.preesm.algorithm.synthesis.memalloc.meg.PiMemoryExclusionGraph;
import org.preesm.algorithm.synthesis.memalloc.meg.PiMemoryExclusionVertex;
import org.preesm.algorithm.synthesis.memalloc.script.PiMemoryScriptEngine;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;

/**
 *
 *
 * @author anmorvan
 */
public class LegacyMemoryAllocation implements IMemoryAllocation {

  @Override
  public Allocation allocateMemory(final PiGraph piGraph, final Design slamDesign, final Scenario scenario,
      final Schedule schedule, final Mapping mapping) {

    // *************
    // INITIAL MEG BUILD
    // *************
    final PiMemoryExclusionGraph memEx = new PiMemoryExclusionGraph(scenario, piGraph);
    PreesmLogger.getLogger().log(Level.INFO, () -> "building memex graph");
    memEx.buildGraph(piGraph);
    final int edgeCount = memEx.edgeSet().size();
    final int vertexCount = memEx.vertexSet().size();
    final double density = edgeCount / ((vertexCount * (vertexCount - 1)) / 2.0);
    PreesmLogger.getLogger().log(Level.INFO, () -> "Memory exclusion graph built with " + vertexCount
        + " vertices and density = " + density + " (" + edgeCount + " edges)");

    // *************
    // MEG UPDATE
    // *************
    final MemExUpdaterEngine memExUpdaterEngine = new MemExUpdaterEngine(piGraph, memEx, schedule, mapping, true);
    memExUpdaterEngine.update();

    // *************
    // SCRIPTS
    // *************
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(MemoryScriptTask.PARAM_VERBOSE, MemoryScriptTask.VALUE_TRUE);
    parameters.put(MemoryScriptTask.PARAM_CHECK, MemoryScriptTask.VALUE_CHECK_THOROUGH);
    parameters.put(MemoryAllocatorTask.PARAM_ALIGNMENT, MemoryAllocatorTask.VALUE_ALIGNEMENT_DEFAULT);
    parameters.put(MemoryScriptTask.PARAM_LOG, MemoryScriptTask.VALUE_LOG);
    parameters.put(MemoryAllocatorTask.PARAM_VERBOSE, MemoryAllocatorTask.VALUE_TRUE_FALSE_DEFAULT);
    parameters.put(MemoryAllocatorTask.PARAM_ALLOCATORS, MemoryAllocatorTask.VALUE_ALLOCATORS_DEFAULT);
    parameters.put(MemoryAllocatorTask.PARAM_XFIT_ORDER, MemoryAllocatorTask.VALUE_XFIT_ORDER_DEFAULT);
    parameters.put(MemoryAllocatorTask.PARAM_NB_SHUFFLE, MemoryAllocatorTask.VALUE_NB_SHUFFLE_DEFAULT);
    parameters.put(MemoryAllocatorTask.PARAM_ALIGNMENT, MemoryAllocatorTask.VALUE_ALIGNEMENT_DEFAULT);
    parameters.put(MemoryAllocatorTask.PARAM_DISTRIBUTION_POLICY, MemoryAllocatorTask.VALUE_DISTRIBUTION_DEFAULT);

    final String log = parameters.get(MemoryScriptTask.PARAM_LOG);
    final String checkString = parameters.get(MemoryScriptTask.PARAM_CHECK);
    final String valueAlignment = parameters.get(MemoryAllocatorTask.PARAM_ALIGNMENT);
    final String valueAllocators = parameters.get(MemoryAllocatorTask.PARAM_ALLOCATORS);
    final long alignment = IMemoryAllocation.extractAlignment(valueAlignment);

    final PiMemoryScriptEngine engine = new PiMemoryScriptEngine(valueAlignment, log, true);
    try {
      engine.runScripts(piGraph, scenario.getSimulationInfo().getDataTypes(), checkString);
    } catch (final EvalError e) {
      final String message = "An error occurred during memory scripts execution";
      throw new PreesmRuntimeException(message, e);
    }
    engine.updateMemEx(memEx);

    // *************
    // ALLOCATION
    // *************

    // Prepare the MEG with the alignment
    PiMemoryAllocator.alignSubBuffers(memEx, alignment);

    // Get vertices before distribution
    final Set<PiMemoryExclusionVertex> verticesBeforeDistribution = memEx.getTotalSetOfVertices();

    // Create several MEGs according to the selected distribution policy
    // Each created MEG corresponds to a single memory bank
    // Log the distribution policy used
    final String valueDistribution = parameters.get(MemoryAllocatorTask.PARAM_DISTRIBUTION_POLICY);
    if (!valueDistribution.equals(MemoryAllocatorTask.VALUE_DISTRIBUTION_SHARED_ONLY)) {
      final String msg = "Split MEG with " + valueDistribution + " policy";
      PreesmLogger.getLogger().log(Level.INFO, msg);
    }

    // Do the distribution
    final Map<String,
        PiMemoryExclusionGraph> megs = PiDistributor.distributeMeg(valueDistribution, memEx, alignment, mapping);

    // Log results
    if (!valueDistribution.equals(MemoryAllocatorTask.VALUE_DISTRIBUTION_SHARED_ONLY)) {
      final String msg = "Created " + megs.keySet().size() + " MemExes";
      PreesmLogger.getLogger().log(Level.INFO, msg);
      for (final Entry<String, PiMemoryExclusionGraph> entry : megs.entrySet()) {
        final double density2 = entry.getValue().edgeSet().size()
            / ((entry.getValue().vertexSet().size() * (entry.getValue().vertexSet().size() - 1)) / 2.0);
        final String msg2 = "Memex(" + entry.getKey() + "): " + entry.getValue().vertexSet().size()
            + " vertices, density=" + density2 + ":: " + entry.getValue().getTotalSetOfVertices();
        PreesmLogger.getLogger().log(Level.INFO, msg2);
      }
    }

    // Get total set of vertices after distribution
    final Set<PiMemoryExclusionVertex> verticesAfterDistribution = memEx.getTotalSetOfVertices();
    final Set<PiMemoryExclusionVertex> verticesInMegs = new LinkedHashSet<>();
    megs.forEach((bank, meg) -> verticesInMegs.addAll(meg.getTotalSetOfVertices()));

    // Check that the total number of vertices is unchanged
    if (!valueDistribution.equals(MemoryAllocatorTask.VALUE_DISTRIBUTION_SHARED_ONLY)
        && ((verticesBeforeDistribution.size() != verticesAfterDistribution.size())
            || (verticesBeforeDistribution.size() != verticesInMegs.size()))) {
      // Compute the list of missing vertices
      verticesBeforeDistribution.removeAll(verticesInMegs);
      final String msg = "Problem in the MEG distribution, some memory objects were lost during the distribution.\n"
          + verticesBeforeDistribution + "\nContact Preesm developers to solve this issue.";
      throw new PreesmRuntimeException(msg);
    }

    final String valueNbShuffle = parameters.get(MemoryAllocatorTask.PARAM_NB_SHUFFLE);
    final String valueXFitOrder = parameters.get(MemoryAllocatorTask.PARAM_XFIT_ORDER);

    int nbShuffle = 0;
    Order ordering = null;
    if (MemoryAllocatorTask.VALUE_XFIT_ORDER_SHUFFLE.equals(valueXFitOrder)) {
      nbShuffle = Integer.decode(valueNbShuffle);
      ordering = (Order.SHUFFLE);
    } else if (MemoryAllocatorTask.VALUE_XFIT_ORDER_LARGEST_FIRST.equals(valueXFitOrder)) {
      ordering = (Order.LARGEST_FIRST);
    } else if (MemoryAllocatorTask.VALUE_XFIT_ORDER_APPROX_STABLE_SET.equals(valueXFitOrder)) {
      ordering = (Order.STABLE_SET);
    } else if (MemoryAllocatorTask.VALUE_XFIT_ORDER_EXACT_STABLE_SET.equals(valueXFitOrder)) {
      ordering = (Order.EXACT_STABLE_SET);
    } else if (MemoryAllocatorTask.VALUE_XFIT_ORDER_SCHEDULING.equals(valueXFitOrder)) {
      ordering = (Order.SCHEDULING);
    } else {
      throw new IllegalArgumentException("unknonwn order " + valueXFitOrder);
    }

    for (final Entry<String, PiMemoryExclusionGraph> entry : megs.entrySet()) {

      final String memoryBank = entry.getKey();
      final PiMemoryExclusionGraph meg = entry.getValue();

      PiMemoryAllocator allocator = createAllocators(valueAllocators, alignment, ordering, nbShuffle, meg);

      final String msg = "Heat up MemEx for " + memoryBank + " memory bank.";
      PreesmLogger.getLogger().log(Level.INFO, msg);
      for (final PiMemoryExclusionVertex vertex : meg.vertexSet()) {
        meg.getAdjacentVertexOf(vertex);
      }

      allocateWith(allocator);
    }

    // TODO fix
    return new SimpleMemoryAllocation().allocateMemory(piGraph, slamDesign, scenario, schedule, mapping);
  }

  /**
   * Based on allocators specified in the task parameters, and stored in the {@link #allocators} attribute, this method
   * instantiate the {@link MemoryAllocator} that are to be executed on the given {@link MemoryExclusionGraph MEG}.
   *
   * @param memEx
   *          the {@link MemoryExclusionGraph MEG} to allocate.
   */
  protected PiMemoryAllocator createAllocators(final String value, final long alignment, final Order o,
      final int nbShuffle, final PiMemoryExclusionGraph memEx) {
    if (MemoryAllocatorTask.VALUE_ALLOCATORS_BASIC.equalsIgnoreCase(value)) {
      final PiMemoryAllocator alloc = new PiBasicAllocator(memEx);
      alloc.setAlignment(alignment);
      return alloc;
    }
    if (MemoryAllocatorTask.VALUE_ALLOCATORS_FIRST_FIT.equalsIgnoreCase(value)) {
      final PiOrderedAllocator alloc = new PiFirstFitAllocator(memEx);
      alloc.setNbShuffle(nbShuffle);
      alloc.setOrder(o);
      alloc.setAlignment(alignment);
      return alloc;
    }
    if (MemoryAllocatorTask.VALUE_ALLOCATORS_BEST_FIT.equalsIgnoreCase(value)) {
      final PiOrderedAllocator alloc = new PiBestFitAllocator(memEx);
      alloc.setNbShuffle(nbShuffle);
      alloc.setOrder(o);
      alloc.setAlignment(alignment);
      return alloc;
    }
    throw new IllegalArgumentException("unknonwn allocator " + value);
  }

  /**
   * Allocate with.
   *
   * @param allocator
   *          the allocator
   * @throws PreesmException
   *           the workflow exception
   */
  protected void allocateWith(final PiMemoryAllocator allocator) {
    long tStart;
    final StringBuilder sb = new StringBuilder(allocator.getClass().getSimpleName());

    if (allocator instanceof PiOrderedAllocator) {
      sb.append("(" + ((PiOrderedAllocator) allocator).getOrder());
      if (((PiOrderedAllocator) allocator).getOrder() == Order.SHUFFLE) {
        sb.append(":" + ((PiOrderedAllocator) allocator).getNbShuffle());
      }
      sb.append(")");
    }
    final String sAllocator = sb.toString();
    PreesmLogger.getLogger().log(Level.INFO, () -> "Starting allocation with " + sAllocator);

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

    if ((allocator instanceof PiOrderedAllocator) && (((PiOrderedAllocator) allocator).getOrder() == Order.SHUFFLE)) {
      ((PiOrderedAllocator) allocator).setPolicy(Policy.WORST);
      log += " worst: " + allocator.getMemorySize();

      ((PiOrderedAllocator) allocator).setPolicy(Policy.MEDIANE);
      log += "(med: " + allocator.getMemorySize();

      ((PiOrderedAllocator) allocator).setPolicy(Policy.AVERAGE);
      log += " avg: " + allocator.getMemorySize() + ")";

      ((PiOrderedAllocator) allocator).setPolicy(Policy.BEST);
    }

    PreesmLogger.getLogger().log(Level.INFO, log);
  }

  private String computeLog(final PiMemoryAllocator allocator, final long tStart, final String sAllocator,
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
}
