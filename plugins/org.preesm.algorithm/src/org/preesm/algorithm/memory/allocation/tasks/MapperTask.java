/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2015)
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
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.memory.allocation.MemoryAllocatorTask;
import org.preesm.algorithm.memory.distributed.Distributor;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * Workflow element taking the architecture a Scheduled DAG and a its corresponding *updated* MemEx as inputs and
 * generates specific MemExes for each memory of the architecture.
 *
 * @author kdesnos
 *
 */
@PreesmTask(id = "org.ietr.preesm.memory.distributed.MapperTask", name = "Memory Exclusion Graph Mapper",

    inputs = { @Port(name = "MemEx", type = MemoryExclusionGraph.class) },

    outputs = { @Port(name = "MemExes", type = Map.class) },

    parameters = {

        @Parameter(name = "Verbose", values = { @Value(name = "? C {True, False}", effect = "") }),
        @Parameter(name = "Distribution",
            description = "Specify which memory architecture should be used to allocate the memory.",
            values = { @Value(name = "SharedOnly",
                effect = "(Default) All memory objects are allocated in a single memory bank accessible to all PE."),
                @Value(name = "DistributedOnly",
                    effect = "Each PE is associated to a private memory bank that no other PE can access. "
                        + "(Currently not supported by code generation.)"),
                @Value(name = "Mixed",
                    effect = "Both private memory banks and a shared memory can be used for allocating memory."),
                @Value(name = "MixedMerged",
                    effect = "Same as mixed, but the memory allocation algorithm favors buffer merging over"
                        + " memory distribution.") })

    }

)
public class MapperTask extends AbstractTaskImplementation {

  public static final String PARAM_VERBOSE         = "Verbose";
  public static final String VALUE_VERBOSE_DEFAULT = "? C {True, False}";
  public static final String VALUE_VERBOSE_TRUE    = "True";
  public static final String VALUE_VERBOSE_FALSE   = "False";
  public static final String OUTPUT_KEY_MEM_EX     = "MemExes";

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    // Rem: Logger is used to display messages in the console
    final Logger logger = PreesmLogger.getLogger();

    // Check Workflow element parameters
    final String valueVerbose = parameters.get(MapperTask.PARAM_VERBOSE);
    boolean verbose;
    verbose = valueVerbose.equals(MapperTask.VALUE_VERBOSE_TRUE);

    final String valuePolicy = parameters.get(MemoryAllocatorTask.PARAM_DISTRIBUTION_POLICY);

    // Retrieve inputs
    final MemoryExclusionGraph memEx = (MemoryExclusionGraph) inputs.get("MemEx");

    // Log the distribution policy used
    if (verbose) {
      logger.log(Level.INFO, () -> "Filling MemExes Vertices set with " + valuePolicy + " policy");
    }

    // Create output
    final Map<String, MemoryExclusionGraph> memExes = Distributor.distributeMeg(valuePolicy, memEx, -1);

    // Log results
    if (verbose) {
      logger.log(Level.INFO, () -> "Created " + memExes.keySet().size() + " MemExes");
      for (final Entry<String, MemoryExclusionGraph> entry : memExes.entrySet()) {
        final double density = entry.getValue().edgeSet().size()
            / ((entry.getValue().vertexSet().size() * (entry.getValue().vertexSet().size() - 1)) / 2.0);
        logger.log(Level.INFO, () -> "Memex(" + entry.getKey() + "): " + entry.getValue().vertexSet().size()
            + " vertices, density=" + density);
      }
    }

    // Output output
    final Map<String, Object> output = new LinkedHashMap<>();
    output.put(MapperTask.OUTPUT_KEY_MEM_EX, memExes);
    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(MapperTask.PARAM_VERBOSE, MapperTask.VALUE_VERBOSE_DEFAULT);
    parameters.put(MemoryAllocatorTask.PARAM_DISTRIBUTION_POLICY, MemoryAllocatorTask.VALUE_DISTRIBUTION_DEFAULT);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Generating memory specific MemEx";
  }

}
