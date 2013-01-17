package org.ietr.preesm.experiment.memory.allocation;

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
import org.ietr.preesm.experiment.memory.allocation.OrderedAllocator.Order;
import org.ietr.preesm.experiment.memory.allocation.OrderedAllocator.Policy;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;

public class MemoryAllocatorTask extends AbstractTaskImplementation {

	static final public String PARAM_VERBOSE = "Verbose";
	static final public String VALUE_VERBOSE_DEFAULT = "? C {True, False}";
	static final public String VALUE_VERBOSE_TRUE = "True";
	static final public String VALUE_VERBOSE_FALSE = "False";

	static final public String PARAM_ALLOCATORS = "Allocator(s)";
	static final public String VALUE_ALLOCATORS_DEFAULT = "{?,?,...} C {Basic, BestFit, FirstFit, DeGreef}";
	static final public String VALUE_ALLOCATORS_BASIC = "Basic";
	static final public String VALUE_ALLOCATORS_BEST_FIT = "BestFit";
	static final public String VALUE_ALLOCATORS_FIRST_FIT = "FirstFit";
	static final public String VALUE_ALLOCATORS_DE_GREEF = "DeGreef";

	static final public String PARAM_XFIT_ORDER = "Best/First Fit order";
	static final public String VALUE_XFIT_ORDER_DEFAULT = "{?,?,...} C {StableSet, LargestFirst, Shuffle}";
	static final public String VALUE_XFIT_ORDER_STABLE_SET = "StableSet";
	static final public String VALUE_XFIT_ORDER_LARGEST_FIRST = "LargestFirst";
	static final public String VALUE_XFIT_ORDER_SHUFFLE = "Shuffle";

	static final public String PARAM_NB_SHUFFLE = "Nb of Shuffling Tested";
	static final public String VALUE_NB_SHUFFLE_DEFAULT = "10";

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

		boolean verbose = valueVerbose.equals(VALUE_VERBOSE_TRUE);

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
		if (valueXFitOrder.contains(VALUE_XFIT_ORDER_STABLE_SET)) {
			ordering.add(Order.STABLE_SET);
		}

		// Retrieve the input of the task
		MemoryExclusionGraph memEx = (MemoryExclusionGraph) inputs.get("MemEx");

		// Create all allocators
		List<MemoryAllocator> allocators = new ArrayList<MemoryAllocator>();
		if (valueAllocators.contains(VALUE_ALLOCATORS_BASIC)) {
			allocators.add(new BasicAllocator(memEx));
		}
		if (valueAllocators.contains(VALUE_ALLOCATORS_FIRST_FIT)) {
			for (Order o : ordering) {
				OrderedAllocator alloc = new FirstFitAllocator(memEx);
				alloc.setNbShuffle(nbShuffle);
				alloc.setOrder(o);
				allocators.add(alloc);
			}
		}
		if (valueAllocators.contains(VALUE_ALLOCATORS_BEST_FIT)) {
			for (Order o : ordering) {
				OrderedAllocator alloc = new BestFitAllocator(memEx);
				alloc.setNbShuffle(nbShuffle);
				alloc.setOrder(o);
				allocators.add(alloc);
			}
		}
		if (valueAllocators.contains(VALUE_ALLOCATORS_DE_GREEF)) {
			allocators.add(new DeGreefAllocator(memEx));
		}
		
		// Heat up the neighborsBackup
		logger.log(Level.INFO, "Heat up MemEx");
		for(MemoryExclusionVertex vertex : memEx.vertexSet()){
			memEx.getAdjacentVertexOf(vertex);
		}

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

			logger.log(Level.INFO, "Starting allocation with " + sAllocator);

			tStart = System.currentTimeMillis();
			allocator.allocate();
			tFinish = System.currentTimeMillis();

			String log = sAllocator + " allocates " + allocator.getMemorySize()
					+ "mem. units in " + (tFinish - tStart) + " ms.";

			if (allocator instanceof OrderedAllocator
					&& ((OrderedAllocator) allocator).getOrder() == Order.SHUFFLE) {
				((OrderedAllocator) allocator).setPolicy(Policy.mediane);
				log += "(med: " + allocator.getMemorySize();
				((OrderedAllocator) allocator).setPolicy(Policy.worst);
				log += " worst: " + allocator.getMemorySize();
				((OrderedAllocator) allocator).setPolicy(Policy.average);
				log += " avg: " + allocator.getMemorySize() + ")";
			}
			logger.log(Level.INFO, log);
		}

		return null;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(PARAM_VERBOSE, VALUE_VERBOSE_DEFAULT);
		parameters.put(PARAM_ALLOCATORS, VALUE_ALLOCATORS_DEFAULT);
		parameters.put(PARAM_XFIT_ORDER, VALUE_XFIT_ORDER_DEFAULT);
		parameters.put(PARAM_NB_SHUFFLE, VALUE_NB_SHUFFLE_DEFAULT);
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Allocating MemEx";
	}

}
