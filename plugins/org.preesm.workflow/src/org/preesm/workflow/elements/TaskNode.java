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
package org.preesm.workflow.elements;

import java.util.LinkedHashMap;
import java.util.Map;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.workflow.WorkflowParser;
import org.preesm.workflow.implement.AbstractTaskImplementation;

/**
 * This class provides a task workflow node.
 *
 * @author Matthieu Wipliez
 * @author mpelcat
 *
 */
public class TaskNode extends AbstractWorkflowNode<AbstractTaskImplementation> {

  /**
   * Transformation Id.
   */
  private final String pluginId;

  /**
   * Instance Id.
   */
  private final String taskId;

  /**
   * Parameters used to parameterize the transformation.
   */
  private final Map<String, String> parameters;

  /**
   * Creates a new transformation node with the given transformation identifier and the given name.
   *
   * @param pluginId
   *          the plugin id
   * @param taskId
   *          The transformation identifier.
   */
  public TaskNode(final String pluginId, final String taskId) {
    this.pluginId = pluginId;
    this.taskId = taskId;

    this.parameters = new LinkedHashMap<>();
  }

  /**
   * Creates a new parameter retrieved from the xml in {@link WorkflowParser}.
   *
   * @param key
   *          the name of the variable.
   * @param value
   *          the value of the variable.
   */
  public void addParameter(final String key, final String value) {
    this.parameters.put(key, value);
  }

  /**
   * Returns the transformation associated with this {@link TaskNode}. Note that it is only valid if
   * {@link #isTransformationPossible()} returns true.
   *
   * @return The transformation associated with this transformation node, or <code>null</code> if the transformation is
   *         not valid.
   */
  public AbstractTaskImplementation getTask() {
    return this.implementation;
  }

  /**
   * Returns the variables map.
   *
   * @return the variables map.
   */
  public Map<String, String> getParameters() {
    return this.parameters;
  }

  /**
   * Returns the value of the parameter with name key.
   *
   * @param key
   *          the name of the variable.
   * @return the value of the variable.
   */
  public String getParameter(final String key) {
    return this.parameters.get(key);
  }

  @Override
  public boolean isScenarioNode() {
    return false;
  }

  @Override
  public boolean isTaskNode() {
    return true;
  }

  /**
   * Specifies the inputs and outputs types of the workflow task using information from the plugin extension.
   *
   * @param task
   *          the task
   * @param element
   *          the element
   * @return True if the prototype was correctly set.
   */
  @Override
  protected boolean initPrototype(final AbstractTaskImplementation task) {
    final PreesmTask annotation = task.getClass().getAnnotation(PreesmTask.class);
    for (final Port input : annotation.inputs()) {
      task.addInput(input.name(), input.type().getCanonicalName());
    }
    for (final Port output : annotation.outputs()) {
      task.addOutput(output.name(), output.type().getCanonicalName());
    }
    return true;
  }

  /**
   * Get task transformation Id.
   *
   * @return the plugin id
   */
  @Override
  public String getID() {
    return this.pluginId;
  }

  /**
   * Get task instance Id.
   *
   * @return the task id
   */
  @Override
  public String getName() {
    return this.taskId;
  }

}
