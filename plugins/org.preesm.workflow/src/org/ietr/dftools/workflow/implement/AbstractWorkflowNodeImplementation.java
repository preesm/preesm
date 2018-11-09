/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Antoine Morvan <antoine.morvan.pro@gmail.com> (2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011 - 2012)
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
/**
 *
 */
package org.ietr.dftools.workflow.implement;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.ietr.dftools.workflow.WorkflowManager;
import org.ietr.dftools.workflow.tools.WorkflowLogger;

/**
 * Node implementation is the superclass of both scenario and task implementation. Their outputs are handled the same
 * way.
 *
 * @author mpelcat
 */
public abstract class AbstractWorkflowNodeImplementation {

  /** Input/Output keys for workflow tasks. */
  protected static final String KEY_SCENARIO = "scenario"; // Should
  // give
  // a
  // PreesmScenario
  /** The Constant KEY_PI_GRAPH. */
  // object
  protected static final String KEY_PI_GRAPH = "PiMM"; // Should
  // give
  // a
  // PiGraph
  /** The Constant KEY_ARCHITECTURE. */
  // object
  protected static final String KEY_ARCHITECTURE = "architecture"; // Should
  // give
  // a
  // Design
  /** The Constant KEY_SDF_GRAPHS_SET. */
  // object
  protected static final String KEY_SDF_GRAPHS_SET = "SDFs"; // Should
  // give
  // a
  // Set<SDFGraph>
  /** The Constant KEY_SDF_GRAPH. */
  // object
  protected static final String KEY_SDF_GRAPH = "SDF"; // Should
  // give
  // an
  // SDFGraph
  /** The Constant KEY_SDF_DAG. */
  // object
  protected static final String KEY_SDF_DAG = "DAG"; // Should
  // give
  // a
  // MapperDAG
  /** The Constant KEY_SDF_DAG_SET. */
  // object
  protected static final String KEY_SDF_DAG_SET = "DAGs"; // Should
  // give
  // a
  // Set<MapperDAG>
  /** The Constant KEY_SDF_ABC. */
  // object
  protected static final String KEY_SDF_ABC = "ABC"; // Should
  // give
  // a
  // IAbc
  /** The Constant KEY_SDF_ABC_SET. */
  // object
  protected static final String KEY_SDF_ABC_SET = "ABCs"; // Should
  // give
  // a
  // Set<IAbc>
  /** The Constant KEY_MEM_EX. */
  // object
  protected static final String KEY_MEM_EX = "MemEx"; // Should
  // give
  // a
  // MemoryExclusionGraph
  /** The Constant KEY_MEM_EX_SET. */
  // object
  protected static final String KEY_MEM_EX_SET = "MemExs"; // Should
  // give
  // a
  // Set<MemoryExclusionGraph>
  /** The Constant KEY_DAG_AND_MEM_EX_MAP. */
  // object
  protected static final String KEY_DAG_AND_MEM_EX_MAP = "DAGsAndMemExs"; // Should
  // give
  // a
  // Map<DirectedAcyclicGraph,
  // MemoryExclusionGraph>
  /** The Constant KEY_BOUND_MIN. */
  // object
  protected static final String KEY_BOUND_MIN = "BoundMin"; // Should
  // give
  // an
  /** The Constant KEY_BOUND_MAX. */
  // int
  protected static final String KEY_BOUND_MAX = "BoundMax"; // Should
  // give
  // an
  /** The Constant KEY_BOUND_MIN_SET. */
  // int
  protected static final String KEY_BOUND_MIN_SET = "minBounds"; // Should
  // give
  // a
  // Set<Integer>
  /** The Constant KEY_BOUND_MAX_SET. */
  // object
  protected static final String KEY_BOUND_MAX_SET = "maxBounds"; // Should
  // give
  // a
  // Set<Integer>
  // object

  /**
   * Id and fully qualified names of node output retrieved from the extension.
   */
  private final Map<String, String> outputPrototype;

  /**
   * Instantiates a new abstract workflow node implementation.
   */
  public AbstractWorkflowNodeImplementation() {
    this.outputPrototype = new LinkedHashMap<>();
  }

  /**
   * Adds an input to the task prototype.
   *
   * @param id
   *          the id
   * @param type
   *          the type
   */
  public final void addOutput(final String id, final String type) {
    this.outputPrototype.put(id, type);
  }

  /**
   * Gets the fully qualified name of the class associated to the given output port.
   *
   * @param id
   *          the id
   * @return the output type
   */
  public final String getOutputType(final String id) {
    return this.outputPrototype.get(id);
  }

  /**
   * Compares the prototype with the output edges. Not all outputs need to be used
   *
   * @param outputPortNames
   *          the output port names
   * @return true, if successful
   */
  public final boolean acceptOutputs(final Set<String> outputPortNames) {

    for (final String outputPortName : outputPortNames) {
      if (!this.outputPrototype.keySet().contains(outputPortName)) {
        WorkflowLogger.logFromProperty(Level.SEVERE, "Workflow.FalseOutputEdge", outputPortName,
            WorkflowManager.IGNORE_PORT_NAME);
        return false;
      }
    }
    return true;
  }

  /**
   * Returns the preferred prototype for the node in a workflow. Useful to give user information in the workflow
   *
   * @return the string
   */
  public String displayPrototype() {
    return " outputs=" + this.outputPrototype.toString();
  }

  /**
   * Returns a message to display in the monitor.
   *
   * @return the string
   */
  public abstract String monitorMessage();
}
