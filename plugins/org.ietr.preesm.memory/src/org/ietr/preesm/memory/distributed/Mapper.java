package org.ietr.preesm.memory.distributed;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
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
public class Mapper extends AbstractTaskImplementation {

	static final public String PARAM_VERBOSE = "Verbose";
	static final public String VALUE_VERBOSE_DEFAULT = "? C {True, False}";
	static final public String VALUE_VERBOSE_TRUE = "True";
	static final public String VALUE_VERBOSE_FALSE = "False";

	// With this policy, each P.E. has its own, non-shared memory.
	// As a result, inter-PE communications will result in corresponding buffers
	// being present in both sender and receiver memory
	static final public String VALUE_POLICY_PE_SPECIFIC = "PE_specific_only";

	// With this policy, each P.E. has its own, non-shared memory and a shared
	// memory is used for inter-core communications.
	static final public String VALUE_POLICY_PE_SPECIFIC_SHARED = "PE_specific_and_shared";

	static final public String PARAM_POLICY = "Policy";
	static final public String VALUE_POLICY_DEFAULT = "? C {" + VALUE_POLICY_PE_SPECIFIC + ", "
			+ VALUE_POLICY_PE_SPECIFIC_SHARED + "}";

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

		String valuePolicy = parameters.get(PARAM_POLICY);

		// Retrieve inputs
		// Design architecture = (Design) inputs.get("architecture");
		// DirectedAcyclicGraph dag = (DirectedAcyclicGraph) inputs.get("DAG");
		MemoryExclusionGraph memEx = (MemoryExclusionGraph) inputs.get("MemEx");

		// Create output
		Map<String, MemoryExclusionGraph> memExes;
		memExes = new HashMap<String, MemoryExclusionGraph>();

		// Generate output

		// Each entry of this map associate a memory to the set
		// of vertices of its MemEx. This map will be differently
		// depending on the policy chosen.
		Map<String, HashSet<MemoryExclusionVertex>> memExesVerticesSet;
		memExesVerticesSet = new HashMap<String, HashSet<MemoryExclusionVertex>>();

		// PE_Specific_and_shared
		if (valuePolicy.equals(VALUE_POLICY_PE_SPECIFIC_SHARED) || valuePolicy.equals(VALUE_POLICY_DEFAULT)) {
			if (verbose) {
				logger.log(Level.INFO,
						"Filling MemExes Vertices set with " + VALUE_POLICY_PE_SPECIFIC_SHARED + " policy");
			}
			// scan the vertices of the input MemEx
			for (MemoryExclusionVertex memExVertex : memEx.vertexSet()) {
				String memory;
				// If dag edge source and target are mapped to the same
				// component
				if (memExVertex.getEdge().getSource().getPropertyBean().getValue("Operator")
						.equals(memExVertex.getEdge().getTarget().getPropertyBean().getValue("Operator"))) {
					ComponentInstance component;
					DAGVertex dagVertex = memExVertex.getEdge().getSource();
					component = (ComponentInstance) dagVertex.getPropertyBean().getValue("Operator");
					memory = component.getInstanceName();
				} else {
					memory = "Shared";
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
		if (valuePolicy.equals(VALUE_POLICY_PE_SPECIFIC)) {
			if (verbose) {
				logger.log(Level.INFO, "Filling MemExes Vertices set with " + VALUE_POLICY_PE_SPECIFIC + " policy");
			}
			// scan the vertices of the input MemEx
			for (MemoryExclusionVertex memExVertex : memEx.vertexSet()) {

				// For source then sink of DAG edge corresponding to the memex
				// vertex
				for (int i = 0; i < 2; i++) {
					// Retrieve the component on which the DAG Vertex is mapped
					ComponentInstance component;
					DAGVertex dagVertex = (i == 0) ? memExVertex.getEdge().getSource()
							: memExVertex.getEdge().getTarget();

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

		if (verbose) {
			logger.log(Level.INFO, "Creating " + memExesVerticesSet.keySet().size() + " MemExes");
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

			if (verbose) {
				double density = copiedMemEx.edgeSet().size()
						/ (copiedMemEx.vertexSet().size() * (copiedMemEx.vertexSet().size() - 1) / 2.0);
				logger.log(Level.INFO,
						"Memex(" + memory + "): " + copiedMemEx.vertexSet().size() + " vertices, density=" + density);
			}
		}

		// Output output
		Map<String, Object> output = new HashMap<String, Object>();
		output.put(OUTPUT_KEY_MEM_EX, memExes);
		return output;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(PARAM_VERBOSE, VALUE_VERBOSE_DEFAULT);
		parameters.put(PARAM_POLICY, VALUE_POLICY_DEFAULT);
		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Generating memory specific MemEx";
	}

}
