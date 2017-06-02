/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
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
package org.ietr.preesm.memory.multiSDFTasks;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation;
import org.ietr.preesm.memory.allocation.AbstractMemoryAllocatorTask;
import org.ietr.preesm.memory.allocation.MemoryAllocator;
import org.ietr.preesm.memory.distributed.Distributor;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;

// TODO: Auto-generated Javadoc
/**
 * The Class MultiMemoryAllocator.
 */
public class MultiMemoryAllocator extends AbstractMemoryAllocatorTask {

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map, org.eclipse.core.runtime.IProgressMonitor,
   * java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters, final IProgressMonitor monitor,
      final String nodeName, final Workflow workflow) throws WorkflowException {
    init(parameters);

    // Retrieve the input of the task
    @SuppressWarnings("unchecked")
    final Map<DirectedAcyclicGraph, MemoryExclusionGraph> dagsAndMemExs = (Map<DirectedAcyclicGraph, MemoryExclusionGraph>) inputs
        .get(AbstractWorkflowNodeImplementation.KEY_DAG_AND_MEM_EX_MAP);

    for (final DirectedAcyclicGraph dag : dagsAndMemExs.keySet()) { // Prepare the MEG with the alignment
      final MemoryExclusionGraph memEx = dagsAndMemExs.get(dag);
      MemoryAllocator.alignSubBuffers(memEx, this.alignment);

      // Get total number of vertices before distribution
      final int nbVerticesBeforeDistribution = memEx.getTotalNumberOfVertices();

      // Create several MEGs according to the selected distribution policy
      // Each created MEG corresponds to a single memory bank
      // Log the distribution policy used
      if (this.verbose && !this.valueDistribution.equals(AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_SHARED_ONLY)) {
        this.logger.log(Level.INFO, "Split MEG with " + this.valueDistribution + " policy");
      }

      // Do the distribution
      final Map<String, MemoryExclusionGraph> megs = Distributor.distributeMeg(this.valueDistribution, memEx, this.alignment);

      // Log results
      if (this.verbose && !this.valueDistribution.equals(AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_SHARED_ONLY)) {
        this.logger.log(Level.INFO, "Created " + megs.keySet().size() + " MemExes");
        for (final Entry<String, MemoryExclusionGraph> entry : megs.entrySet()) {
          final double density = entry.getValue().edgeSet().size() / ((entry.getValue().vertexSet().size() * (entry.getValue().vertexSet().size() - 1)) / 2.0);
          this.logger.log(Level.INFO, "Memex(" + entry.getKey() + "): " + entry.getValue().vertexSet().size() + " vertices, density=" + density + ":: "
              + entry.getValue().getTotalSetOfVertices());
        }
      }

      // Get total number of vertices before distribution
      final int nbVerticesAfterDistribution = memEx.getTotalNumberOfVertices();
      final int[] nbVerticesInMegs = { 0 };
      megs.forEach((bank, meg) -> {
        nbVerticesInMegs[0] += meg.getTotalNumberOfVertices();
      });

      // Check that the total number of vertices is unchanged
      if (!this.valueDistribution.equals(AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_SHARED_ONLY)
          && ((nbVerticesBeforeDistribution != nbVerticesAfterDistribution) || (nbVerticesBeforeDistribution != nbVerticesInMegs[0]))) {
        this.logger.log(Level.SEVERE,
            "Problem in the MEG distribution, some memory objects were lost during the distribution.\n" + "Contact Preesm developers to solve this issue.");
      }

      for (final Entry<String, MemoryExclusionGraph> entry : megs.entrySet()) {

        final String memoryBank = entry.getKey();
        final MemoryExclusionGraph meg = entry.getValue();

        createAllocators(meg);

        if (this.verbose) {
          this.logger.log(Level.INFO, "Heat up MemEx for " + memoryBank + " memory bank.");
        }
        for (final MemoryExclusionVertex vertex : meg.vertexSet()) {
          meg.getAdjacentVertexOf(vertex);
        }

        for (final MemoryAllocator allocator : this.allocators) {
          allocateWith(allocator);
        }
      }
    }

    final Map<String, Object> output = new HashMap<>();
    output.put(AbstractWorkflowNodeImplementation.KEY_DAG_AND_MEM_EX_MAP, dagsAndMemExs);
    return output;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.memory.allocation.AbstractMemoryAllocatorTask#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    // This useless method must be copied here because inheritance link
    // does not work when getting the parameter lists.
    return super.getDefaultParameters();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Allocating MemExs";
  }

}
