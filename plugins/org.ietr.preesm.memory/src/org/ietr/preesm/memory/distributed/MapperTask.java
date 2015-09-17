/*********************************************************
Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
Karol Desnos

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
package org.ietr.preesm.memory.distributed;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.memory.allocation.AbstractMemoryAllocatorTask;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph;
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex;

/**
 * Workflow element taking the architecture a Scheduled DAG and a its
 * corresponding *updated* MemEx as inputs and generates specific MemExes for
 * each memory of the architecture.
 * 
 * @author kdesnos
 * 
 */
public class MapperTask extends AbstractTaskImplementation {

	static final public String PARAM_VERBOSE = "Verbose";
	static final public String VALUE_VERBOSE_DEFAULT = "? C {True, False}";
	static final public String VALUE_VERBOSE_TRUE = "True";
	static final public String VALUE_VERBOSE_FALSE = "False";

	static final public String OUTPUT_KEY_MEM_EX = "MemExes";

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
			IProgressMonitor monitor, String nodeName, Workflow workflow) throws WorkflowException {

		// Rem: Logger is used to display messages in the console
		Logger logger = WorkflowLogger.getLogger();

		// Check Workflow element parameters
		String valueVerbose = parameters.get(PARAM_VERBOSE);
		boolean verbose;
		verbose = valueVerbose.equals(VALUE_VERBOSE_TRUE);

		String valuePolicy = parameters.get(AbstractMemoryAllocatorTask.PARAM_DISTRIBUTION_POLICY);

		// Retrieve inputs
		MemoryExclusionGraph memEx = (MemoryExclusionGraph) inputs.get("MemEx");

		// Log the distribution policy used
		if (verbose) {
			logger.log(Level.INFO, "Filling MemExes Vertices set with " + valuePolicy + " policy");
		}

		// Create output
		Map<String, MemoryExclusionGraph> memExes;
		memExes = distributeMeg(valuePolicy, memEx);

		// Log results
		if (verbose) {
			logger.log(Level.INFO, "Created " + memExes.keySet().size() + " MemExes");
			for (Entry<String, MemoryExclusionGraph> entry : memExes.entrySet()) {
				double density = entry.getValue().edgeSet().size()
						/ (entry.getValue().vertexSet().size() * (entry.getValue().vertexSet().size() - 1) / 2.0);
				logger.log(Level.INFO, "Memex(" + entry.getKey() + "): " + entry.getValue().vertexSet().size()
						+ " vertices, density=" + density);
			}
		}

		// Output output
		Map<String, Object> output = new HashMap<String, Object>();
		output.put(OUTPUT_KEY_MEM_EX, memExes);
		return output;
	}

	/**
	 * @param valuePolicy
	 * @param memEx
	 * @return
	 * @throws RuntimeException
	 */
	public Map<String, MemoryExclusionGraph> distributeMeg(String valuePolicy, MemoryExclusionGraph memEx)
			throws RuntimeException {
		Map<String, MemoryExclusionGraph> memExes;
		memExes = new HashMap<String, MemoryExclusionGraph>();

		// Generate output

		// Each entry of this map associate a memory to the set
		// of vertices of its MemEx. This map will be differently
		// depending on the policy chosen.
		Map<String, HashSet<MemoryExclusionVertex>> memExesVerticesSet;
		memExesVerticesSet = new HashMap<String, HashSet<MemoryExclusionVertex>>();

		// PE_Specific_and_shared
		if (valuePolicy.equals(AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_MIXED)
				|| valuePolicy.equals(AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DEFAULT)) {
			// scan the vertices of the input MemEx
			for (MemoryExclusionVertex memExVertex : memEx.vertexSet()) {
				String memory = "Shared";

				// If dag edge source and target are mapped to the same
				// component
				if (memExVertex.getEdge() != null) {
					// If source and target are mapped to te same core
					if (memExVertex.getEdge().getSource().getPropertyBean().getValue("Operator")
							.equals(memExVertex.getEdge().getTarget().getPropertyBean().getValue("Operator"))) {
						ComponentInstance component;
						DAGVertex dagVertex = memExVertex.getEdge().getSource();
						component = (ComponentInstance) dagVertex.getPropertyBean().getValue("Operator");
						memory = component.getInstanceName();
					} // Else => Shared memory
				} else {
					// The MObject is not associated to a DAGEdge
					// It is either a FIFO_head/body or working memory
					// For now these mobjects are put in shared memory
				}

				HashSet<MemoryExclusionVertex> verticesSet = memExesVerticesSet.get(memory);
				if (verticesSet == null) {
					// If the component is not yet in the map, add it
					verticesSet = new HashSet<MemoryExclusionVertex>();
					memExesVerticesSet.put(memory, verticesSet);
				}

				// Add the memEx Vertex to the set of vertex of the
				// component
				verticesSet.add(memExVertex);
			}
		}

		// PE_Specific_only
		if (valuePolicy.equals(AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DISTRIBUTED_ONLY)) {
			// scan the vertices of the input MemEx
			for (MemoryExclusionVertex memExVertex : memEx.vertexSet()) {

				// For source then sink of DAG edge corresponding to the memex
				// vertex
				for (int i = 0; i < 2; i++) {
					// Retrieve the component on which the DAG Vertex is mapped
					ComponentInstance component;
					DAGEdge edge = memExVertex.getEdge();
					if (edge == null) {
						throw new RuntimeException("Feedback fifos not yet supported wit this policy.");
					}
					DAGVertex dagVertex = (i == 0) ? edge.getSource() : edge.getTarget();

					component = (ComponentInstance) dagVertex.getPropertyBean().getValue("Operator");

					HashSet<MemoryExclusionVertex> verticesSet = memExesVerticesSet.get(component.getInstanceName());
					if (verticesSet == null) {
						// If the component is not yet in the map, add it
						verticesSet = new HashSet<MemoryExclusionVertex>();
						memExesVerticesSet.put(component.getInstanceName(), verticesSet);
					}

					// Add the memEx Vertex to the set of vertex of the
					// component
					verticesSet.add(memExVertex);
				}
			}
		}

		// Create Memory Specific MemEx using their verticesSet
		for (String memory : memExesVerticesSet.keySet()) {
			// Clone the input exclusion graph
			MemoryExclusionGraph copiedMemEx = (MemoryExclusionGraph) memEx.clone();
			// Obtain the list of vertices to remove from it
			Set<MemoryExclusionVertex> verticesToRemove = new HashSet<MemoryExclusionVertex>(copiedMemEx.vertexSet());
			verticesToRemove.removeAll(memExesVerticesSet.get(memory));
			// Remove them
			copiedMemEx.removeAllVertices(verticesToRemove);
			// Save the MemEx
			memExes.put(memory, copiedMemEx);
		}
		return memExes;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(PARAM_VERBOSE, VALUE_VERBOSE_DEFAULT);
		parameters.put(AbstractMemoryAllocatorTask.PARAM_DISTRIBUTION_POLICY,
				AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DEFAULT);
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Generating memory specific MemEx";
	}

}
