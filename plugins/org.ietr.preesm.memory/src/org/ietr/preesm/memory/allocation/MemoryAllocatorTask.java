/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.memory.allocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.preesm.memory.distributed.Distributor;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;

public class MemoryAllocatorTask extends AbstractMemoryAllocatorTask {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
			IProgressMonitor monitor, String nodeName, Workflow workflow) throws WorkflowException {
		this.init(parameters);

		// Retrieve the input of the task
		MemoryExclusionGraph memEx = (MemoryExclusionGraph) inputs.get("MemEx");

		// Prepare the MEG with the alignment
		MemoryAllocator.alignSubBuffers(memEx, alignment);

		// Get vertices before distribution
		Set<MemoryExclusionVertex> verticesBeforeDistribution = memEx.getTotalSetOfVertices();

		// Create several MEGs according to the selected distribution policy
		// Each created MEG corresponds to a single memory bank
		// Log the distribution policy used
		if (verbose && !valueDistribution.equals(VALUE_DISTRIBUTION_SHARED_ONLY)) {
			logger.log(Level.INFO, "Split MEG with " + valueDistribution + " policy");
		}
		
		// Do the distribution
		Distributor.setLogger(logger);
		Map<String, MemoryExclusionGraph> megs = Distributor.distributeMeg(valueDistribution, memEx, alignment);
		
		// Log results
		if (verbose && !valueDistribution.equals(VALUE_DISTRIBUTION_SHARED_ONLY)) {
			logger.log(Level.INFO, "Created " + megs.keySet().size() + " MemExes");
			for (Entry<String, MemoryExclusionGraph> entry : megs.entrySet()) {
				double density = entry.getValue().edgeSet().size()
						/ (entry.getValue().vertexSet().size() * (entry.getValue().vertexSet().size() - 1) / 2.0);
				logger.log(Level.INFO, "Memex(" + entry.getKey() + "): " + entry.getValue().vertexSet().size()
						+ " vertices, density=" + density + ":: " + entry.getValue().getTotalSetOfVertices());
			}
		}

		// Get total set of vertices after distribution
		Set<MemoryExclusionVertex> verticesAfterDistribution = memEx.getTotalSetOfVertices();
		final Set<MemoryExclusionVertex> verticesInMegs = new HashSet<MemoryExclusionVertex>();
		megs.forEach((bank, meg) -> {
			verticesInMegs.addAll(meg.getTotalSetOfVertices());
		});

		// Check that the total number of vertices is unchanged
		if (!valueDistribution.equals(VALUE_DISTRIBUTION_SHARED_ONLY)
				&& (verticesBeforeDistribution.size() != verticesAfterDistribution.size()
						|| verticesBeforeDistribution.size() != verticesInMegs.size())) {
			// Compute the list of missing vertices
			verticesBeforeDistribution.removeAll(verticesInMegs);
			logger.log(Level.SEVERE,
					"Problem in the MEG distribution, some memory objects were lost during the distribution.\n"
					+ verticesBeforeDistribution
							+ "\nContact Preesm developers to solve this issue.");
		}

		for(Entry<String,MemoryExclusionGraph> entry : megs.entrySet()) {
			
			String memoryBank = entry.getKey();
			MemoryExclusionGraph meg = entry.getValue();
			
			createAllocators(meg);
			
			if (verbose) {
				logger.log(Level.INFO, "Heat up MemEx for " + memoryBank +" memory bank." );
			}
			for (MemoryExclusionVertex vertex : meg.vertexSet()) {
				meg.getAdjacentVertexOf(vertex);
			}

			for (MemoryAllocator allocator : allocators) {
				this.allocateWith(allocator);
			}
		}

		Map<String, Object> output = new HashMap<String, Object>();
		output.put("MEGs", megs);
		return output;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		// This useless method must be copied here because inheritance link
		// does not work when getting the parameter lists.
		return super.getDefaultParameters();
	}

	@Override
	public String monitorMessage() {
		return "Allocating MemEx";
	}

}
