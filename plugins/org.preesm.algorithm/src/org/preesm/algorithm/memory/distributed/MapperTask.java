/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
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
package org.preesm.algorithm.memory.distributed;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.memory.allocation.AbstractMemoryAllocatorTask;
import org.preesm.algorithm.memory.exclusiongraph.MemoryExclusionGraph;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.workflow.WorkflowException;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

// TODO: Auto-generated Javadoc
/**
 * Workflow element taking the architecture a Scheduled DAG and a its corresponding *updated* MemEx as inputs and
 * generates specific MemExes for each memory of the architecture.
 *
 * @author kdesnos
 *
 */
public class MapperTask extends AbstractTaskImplementation {

  /** The Constant PARAM_VERBOSE. */
  public static final String PARAM_VERBOSE = "Verbose";

  /** The Constant VALUE_VERBOSE_DEFAULT. */
  public static final String VALUE_VERBOSE_DEFAULT = "? C {True, False}";

  /** The Constant VALUE_VERBOSE_TRUE. */
  public static final String VALUE_VERBOSE_TRUE = "True";

  /** The Constant VALUE_VERBOSE_FALSE. */
  public static final String VALUE_VERBOSE_FALSE = "False";

  /** The Constant OUTPUT_KEY_MEM_EX. */
  public static final String OUTPUT_KEY_MEM_EX = "MemExes";

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map,
   * org.eclipse.core.runtime.IProgressMonitor, java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) throws WorkflowException {

    // Rem: Logger is used to display messages in the console
    final Logger logger = PreesmLogger.getLogger();

    // Check Workflow element parameters
    final String valueVerbose = parameters.get(MapperTask.PARAM_VERBOSE);
    boolean verbose;
    verbose = valueVerbose.equals(MapperTask.VALUE_VERBOSE_TRUE);

    final String valuePolicy = parameters.get(AbstractMemoryAllocatorTask.PARAM_DISTRIBUTION_POLICY);

    // Retrieve inputs
    final MemoryExclusionGraph memEx = (MemoryExclusionGraph) inputs.get("MemEx");

    // Log the distribution policy used
    if (verbose) {
      logger.log(Level.INFO, "Filling MemExes Vertices set with " + valuePolicy + " policy");
    }

    // Create output
    Map<String, MemoryExclusionGraph> memExes;
    memExes = Distributor.distributeMeg(valuePolicy, memEx, -1);

    // Log results
    if (verbose) {
      logger.log(Level.INFO, "Created " + memExes.keySet().size() + " MemExes");
      for (final Entry<String, MemoryExclusionGraph> entry : memExes.entrySet()) {
        final double density = entry.getValue().edgeSet().size()
            / ((entry.getValue().vertexSet().size() * (entry.getValue().vertexSet().size() - 1)) / 2.0);
        logger.log(Level.INFO,
            "Memex(" + entry.getKey() + "): " + entry.getValue().vertexSet().size() + " vertices, density=" + density);
      }
    }

    // Output output
    final Map<String, Object> output = new LinkedHashMap<>();
    output.put(MapperTask.OUTPUT_KEY_MEM_EX, memExes);
    return output;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    parameters.put(MapperTask.PARAM_VERBOSE, MapperTask.VALUE_VERBOSE_DEFAULT);
    parameters.put(AbstractMemoryAllocatorTask.PARAM_DISTRIBUTION_POLICY,
        AbstractMemoryAllocatorTask.VALUE_DISTRIBUTION_DEFAULT);
    return parameters;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Generating memory specific MemEx";
  }

}
