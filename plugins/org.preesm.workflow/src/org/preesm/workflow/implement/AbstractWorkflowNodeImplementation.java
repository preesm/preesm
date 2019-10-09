/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
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
package org.preesm.workflow.implement;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.workflow.WorkflowManager;
import org.preesm.workflow.elements.AbstractWorkflowNode;

/**
 * Node implementation is the superclass of both scenario and task implementation. Their outputs are handled the same
 * way.
 *
 * @author mpelcat
 */
public abstract class AbstractWorkflowNodeImplementation {

  public static final String KEY_SCENARIO           = "scenario";
  public static final String KEY_PI_GRAPH           = "PiMM";
  public static final String KEY_ARCHITECTURE       = "architecture";
  public static final String KEY_SDF_GRAPHS_SET     = "SDFs";
  public static final String KEY_SDF_GRAPH          = "SDF";
  public static final String KEY_SDF_DAG            = "DAG";
  public static final String KEY_SDF_DAG_SET        = "DAGs";
  public static final String KEY_SDF_ABC            = "ABC";
  public static final String KEY_SDF_ABC_SET        = "ABCs";
  public static final String KEY_MEM_EX             = "MemEx";
  public static final String KEY_MEM_EX_SET         = "MemExs";
  public static final String KEY_DAG_AND_MEM_EX_MAP = "DAGsAndMemExs";
  public static final String KEY_BOUND_MIN          = "BoundMin";
  public static final String KEY_BOUND_MAX          = "BoundMax";
  public static final String KEY_BOUND_MIN_SET      = "minBounds";
  public static final String KEY_BOUND_MAX_SET      = "maxBounds";

  /**
   * Id and fully qualified names of node output retrieved from the extension.
   */
  private final Map<String, String> outputPrototype;

  private AbstractWorkflowNode<?> workflowNode;

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
        PreesmLogger.logFromProperty(Level.SEVERE, "Workflow.FalseOutputEdge", outputPortName,
            this.getWorkflowNode().getName(), this.getWorkflowNode().getID(), WorkflowManager.IGNORE_PORT_NAME);
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

  public AbstractWorkflowNode<?> getWorkflowNode() {
    return workflowNode;
  }

  public void setWorkflowNode(final AbstractWorkflowNode<?> workflowNode) {
    this.workflowNode = workflowNode;
  }
}
