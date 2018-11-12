/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Antoine Morvan <antoine.morvan.pro@gmail.com> (2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
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
package org.ietr.dftools.workflow.implement;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.tools.WorkflowLogger;

/**
 * This interface must be implemented by any workflow task element. The prototype of the workflow task is specified in
 * the plugin extension.
 *
 * @author mpelcat
 */
public abstract class AbstractTaskImplementation extends AbstractWorkflowNodeImplementation {

  /**
   * Id and fully qualified names of task input and output retrieved from the extension.
   */
  private final Map<String, String> inputPrototype;

  /**
   * Instantiates a new abstract task implementation.
   */
  public AbstractTaskImplementation() {
    this.inputPrototype = new LinkedHashMap<>();
  }

  /**
   * Adds an input to the task prototype.
   *
   * @param id
   *          the id
   * @param type
   *          the type
   */
  public final void addInput(final String id, final String type) {
    this.inputPrototype.put(id, type);
  }

  /**
   * Compares the prototype with the input edges id AND type. All inputs need to be initialized
   *
   * @param graphInputPorts
   *          the graph input ports
   * @return true, if successful
   */
  public final boolean acceptInputs(final Map<String, String> graphInputPorts) {

    for (final String protoInputPortName : this.inputPrototype.keySet()) {
      if (!graphInputPorts.keySet().contains(protoInputPortName)) {
        WorkflowLogger.logFromProperty(Level.SEVERE, "Workflow.FalseInputEdge", protoInputPortName);
        return false;
      } else {
        final String protoType = this.inputPrototype.get(protoInputPortName);
        final String graphType = graphInputPorts.get(protoInputPortName);
        if (!protoType.equals(graphType)) {
          WorkflowLogger.logFromProperty(Level.SEVERE, "Workflow.FalseInputType", protoInputPortName, graphType,
              protoType);
          return false;
        }
      }
    }

    if (graphInputPorts.keySet().size() > this.inputPrototype.keySet().size()) {
      WorkflowLogger.logFromProperty(Level.SEVERE, "Workflow.TooManyInputEdges",
          String.valueOf(graphInputPorts.keySet().size()), String.valueOf(this.inputPrototype.keySet().size()));
      return false;
    }

    return true;
  }

  /**
   * Returns the preferred prototype for the node in a workflow. Useful to give user information in the workflow
   *
   * @return the string
   */
  @Override
  public final String displayPrototype() {
    return " inputs=" + this.inputPrototype.toString() + super.displayPrototype();
  }

  /**
   * The workflow task element implementation must have a execute method that is called by the workflow manager.
   *
   * @param inputs
   *          a map associating input objects to their data type in the graph
   * @param parameters
   *          a map containing the vertex parameters
   * @param monitor
   *          the progress monitor that can be checked to cancel a task if requested
   * @param nodeName
   *          name of the graph node that triggered this execution
   * @param workflow
   *          the workflow that launched the task
   * @return a map associating output objects to their data type in the graph
   * @throws WorkflowException
   *           the workflow exception
   */
  public abstract Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters,
      IProgressMonitor monitor, String nodeName, Workflow workflow);

  /**
   * Returns the task parameters and their default values. These parameters are automatically added in the graph if not
   * present.
   *
   * @return the default parameters
   */
  public abstract Map<String, String> getDefaultParameters();
}
