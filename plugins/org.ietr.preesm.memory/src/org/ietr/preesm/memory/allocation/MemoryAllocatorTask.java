/*********************************************************
Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
Karol Desnos, Julien Heulot

[mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/
package org.ietr.preesm.memory.allocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.elements.Workflow;
import net.sf.dftools.workflow.implement.AbstractTaskImplementation;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.preesm.memory.allocation.OrderedAllocator.Order;
import org.ietr.preesm.memory.allocation.OrderedAllocator.Policy;
import org.ietr.preesm.memory.exclusiongraph.MemExBroadcastMerger;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;

public class MemoryAllocatorTask extends AbstractTaskImplementation {

	static final public String PARAM_VERBOSE = "Verbose";
	static final public String VALUE_TRUE_FALSE_DEFAULT = "? C {True, False}";
	static final public String VALUE_TRUE = "True";
	static final public String VALUE_FALSE = "False";

	static final public String PARAM_ALLOCATORS = "Allocator(s)";
	static final public String VALUE_ALLOCATORS_DEFAULT = "{?,?,...} C {Basic, BestFit, FirstFit, DeGreef}";
	static final public String VALUE_ALLOCATORS_BASIC = "Basic";
	static final public String VALUE_ALLOCATORS_BEST_FIT = "BestFit";
	static final public String VALUE_ALLOCATORS_FIRST_FIT = "FirstFit";
	static final public String VALUE_ALLOCATORS_DE_GREEF = "DeGreef";

	static final public String PARAM_XFIT_ORDER = "Best/First Fit order";
	static final public String VALUE_XFIT_ORDER_DEFAULT = "{?,?,...} C {ApproxStableSet, ExactStableSet, LargestFirst, Shuffle, Scheduling}";
	static final public String VALUE_XFIT_ORDER_APPROX_STABLE_SET = "ApproxStableSet";
	static final public String VALUE_XFIT_ORDER_LARGEST_FIRST = "LargestFirst";
	static final public String VALUE_XFIT_ORDER_SHUFFLE = "Shuffle";
	static final public String VALUE_XFIT_ORDER_EXACT_STABLE_SET = "ExactStableSet";
	static final public String VALUE_XFIT_ORDER_SCHEDULING = "Scheduling";

	static final public String PARAM_NB_SHUFFLE = "Nb of Shuffling Tested";
	static final public String VALUE_NB_SHUFFLE_DEFAULT = "10";

	static final public String PARAM_MERGE_BROADCAST = "Merge broadcasts";
	static final public String BROADCAST_MERGED_PROPERTY = "broadcast_merged";

	static final public String PARAM_ALIGNMENT = "Data alignement";
	static final public String VALUE_ALIGNEMENT_NONE = "None";
	static final public String VALUE_ALIGNEMENT_DATA = "Data";
	static final public String VALUE_ALIGNEMENT_FIXED = "Fixed:=";
	static final public String VALUE_ALIGNEMENT_DEFAULT = "? C {None, Data, Fixed:=<nbBytes>}";

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {
		// Rem: Logger is used to display messages in the console
		Logger logger = WorkflowLogger.getLogger();

		// Retrieve parameters from workflow
		String valueVerbose = parameters.get(PARAM_VERBOSE);
		String valueAllocators = parameters.get(PARAM_ALLOCATORS);
		String valueXFitOrder = parameters.get(PARAM_XFIT_ORDER);
		String valueNbShuffle = parameters.get(PARAM_NB_SHUFFLE);
		String valueMergeBroadcast = parameters.get(PARAM_MERGE_BROADCAST);

		boolean verbose = valueVerbose.equals(VALUE_TRUE);
		boolean mergeBroadcast = valueMergeBroadcast.equals(VALUE_TRUE);

		// Retrieve the alignment param
		String valueAlignment = parameters.get(PARAM_ALIGNMENT);
		int alignment;
		switch (valueAlignment.substring(0,
				Math.min(valueAlignment.length(), 7))) {
		case VALUE_ALIGNEMENT_NONE:
			alignment = -1;
			break;
		case VALUE_ALIGNEMENT_DATA:
			alignment = 0;
			break;
		case VALUE_ALIGNEMENT_FIXED:
			String fixedValue = valueAlignment.substring(7);
			alignment = Integer.parseInt(fixedValue);
			break;
		default:
			alignment = -1;
		}
		if (verbose) {
			logger.log(Level.INFO, "Allocation with alignment:=" + alignment
					+ ".");
		}

		// Retrieve the ordering policies to test
		int nbShuffle = 0;
		List<Order> ordering = new ArrayList<Order>();
		if (valueXFitOrder.contains(VALUE_XFIT_ORDER_SHUFFLE)) {
			nbShuffle = Integer.decode(valueNbShuffle);
			ordering.add(Order.SHUFFLE);
		}
		if (valueXFitOrder.contains(VALUE_XFIT_ORDER_LARGEST_FIRST)) {
			ordering.add(Order.LARGEST_FIRST);
		}
		if (valueXFitOrder.contains(VALUE_XFIT_ORDER_APPROX_STABLE_SET)) {
			ordering.add(Order.STABLE_SET);
		}
		if (valueXFitOrder.contains(VALUE_XFIT_ORDER_EXACT_STABLE_SET)) {
			ordering.add(Order.EXACT_STABLE_SET);
		}
		if (valueXFitOrder.contains(VALUE_XFIT_ORDER_SCHEDULING)) {
			ordering.add(Order.SCHEDULING);
		}

		// Retrieve the input of the task
		MemoryExclusionGraph memEx = (MemoryExclusionGraph) inputs.get("MemEx");

		// Prepare the MEG with the alignment
		MemoryAllocator.alignSubBuffers(memEx, alignment);

		// Create all allocators
		List<MemoryAllocator> allocators = new ArrayList<MemoryAllocator>();
		if (valueAllocators.contains(VALUE_ALLOCATORS_BASIC)) {
			MemoryAllocator alloc = new BasicAllocator(memEx);
			alloc.setAlignment(alignment);
			allocators.add(alloc);
		}
		if (valueAllocators.contains(VALUE_ALLOCATORS_FIRST_FIT)) {
			for (Order o : ordering) {
				OrderedAllocator alloc = new FirstFitAllocator(memEx);
				alloc.setNbShuffle(nbShuffle);
				alloc.setOrder(o);
				alloc.setAlignment(alignment);
				allocators.add(alloc);
			}
		}
		if (valueAllocators.contains(VALUE_ALLOCATORS_BEST_FIT)) {
			for (Order o : ordering) {
				OrderedAllocator alloc = new BestFitAllocator(memEx);
				alloc.setNbShuffle(nbShuffle);
				alloc.setOrder(o);
				alloc.setAlignment(alignment);
				allocators.add(alloc);
			}
		}
		if (valueAllocators.contains(VALUE_ALLOCATORS_DE_GREEF)) {
			MemoryAllocator alloc = new DeGreefAllocator(memEx);
			alloc.setAlignment(alignment);
			allocators.add(alloc);
		}

		// Merge broadcast (if required)
		MemExBroadcastMerger broadcastMerger = null;
		if (mergeBroadcast) {
			int nbBefore = memEx.vertexSet().size();
			if (verbose) {
				logger.log(Level.INFO,
						"Merging broadcast edges (when possible).");
			}
			broadcastMerger = new MemExBroadcastMerger(memEx);
			int nbBroadcast = broadcastMerger.merge();

			if (verbose) {
				logger.log(Level.INFO, "Merging broadcast: " + nbBroadcast
						+ " were mergeable for a total of "
						+ (nbBefore - memEx.vertexSet().size())
						+ " memory objects.");
			}
		}

		// Heat up the neighborsBackup
		if (verbose) {
			logger.log(Level.INFO, "Heat up MemEx");
		}
		for (MemoryExclusionVertex vertex : memEx.vertexSet()) {
			memEx.getAdjacentVertexOf(vertex);
		}

		String csv = "";

		for (MemoryAllocator allocator : allocators) {

			long tStart, tFinish;
			String sAllocator = allocator.getClass().getSimpleName();
			if (allocator instanceof OrderedAllocator) {
				sAllocator += "(" + ((OrderedAllocator) allocator).getOrder();
				if (((OrderedAllocator) allocator).getOrder() == Order.SHUFFLE) {
					sAllocator += ":"
							+ ((OrderedAllocator) allocator).getNbShuffle();
				}
				sAllocator += ")";
			}

			if (verbose) {
				logger.log(Level.INFO, "Starting allocation with " + sAllocator);
			}

			tStart = System.currentTimeMillis();
			allocator.allocate();
			tFinish = System.currentTimeMillis();

			// Check the correct allocation
			try {
				if (!allocator.checkAllocation().isEmpty()) {
					throw new WorkflowException(
							"The obtained allocation was not valid because mutually"
									+ " exclusive memory objects have overlapping address ranges."
									+ " The allocator is not working.\n"
									+ allocator.checkAllocation());
				}
			} catch (RuntimeException e) {
				throw new WorkflowException(e.getMessage());
			}

			if (!allocator.checkAlignment().isEmpty()) {
				throw new WorkflowException(
						"The obtained allocation was not valid because there were"
								+ " unaligned memory objects. The allocator is not working.\n"
								+ allocator.checkAlignment());
			}

			csv += "" + allocator.getMemorySize() + ";" + (tFinish - tStart)
					+ ";";
			String log = sAllocator + " allocates " + allocator.getMemorySize()
					+ "mem. units in " + (tFinish - tStart) + " ms.";

			if (allocator instanceof OrderedAllocator
					&& ((OrderedAllocator) allocator).getOrder() == Order.SHUFFLE) {
				((OrderedAllocator) allocator).setPolicy(Policy.worst);
				log += " worst: " + allocator.getMemorySize();
				csv += allocator.getMemorySize() + ";";
				((OrderedAllocator) allocator).setPolicy(Policy.mediane);
				log += "(med: " + allocator.getMemorySize();
				csv += allocator.getMemorySize() + ";";
				((OrderedAllocator) allocator).setPolicy(Policy.average);
				log += " avg: " + allocator.getMemorySize() + ")";
				csv += allocator.getMemorySize() + ";";
				((OrderedAllocator) allocator).setPolicy(Policy.best);
			}

			logger.log(Level.INFO, log);
		}

		if (mergeBroadcast) {
			if (verbose) {
				logger.log(Level.INFO, "Unmerge broadcast edges.");
			}
			broadcastMerger.unmerge();
			memEx.setPropertyValue(BROADCAST_MERGED_PROPERTY, true);
		}

		System.out.println(csv);
		Map<String, Object> output = new HashMap<String, Object>();
		output.put("MemEx", memEx);
		return output;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(PARAM_VERBOSE, VALUE_TRUE_FALSE_DEFAULT);
		parameters.put(PARAM_ALLOCATORS, VALUE_ALLOCATORS_DEFAULT);
		parameters.put(PARAM_XFIT_ORDER, VALUE_XFIT_ORDER_DEFAULT);
		parameters.put(PARAM_NB_SHUFFLE, VALUE_NB_SHUFFLE_DEFAULT);
		parameters.put(PARAM_MERGE_BROADCAST, VALUE_TRUE_FALSE_DEFAULT);
		parameters.put(PARAM_ALIGNMENT, VALUE_ALIGNEMENT_DEFAULT);
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Allocating MemEx";
	}

}
