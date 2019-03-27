/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018 - 2019)
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
package org.preesm.workflow;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.exceptions.PreesmFrameworkException;
import org.preesm.commons.files.WorkspaceUtils;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.commons.messages.PreesmMessages;
import org.preesm.workflow.elements.AbstractWorkflowNode;
import org.preesm.workflow.elements.ScenarioNode;
import org.preesm.workflow.elements.TaskNode;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.elements.WorkflowEdge;
import org.preesm.workflow.eow.ErrorOnWarningError;
import org.preesm.workflow.eow.ErrorOnWarningLogHandler;
import org.preesm.workflow.implement.AbstractScenarioImplementation;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * This abstract class provides methods to check and execute a workflow. A workflow consists of several transformation
 * plug-ins tasks applied to a scenario.
 *
 * Log method for check and execution are to be fixed by concrete subclasses, depending on available interfaces
 *
 * @author mpelcat
 * @author cguy
 *
 */
public abstract class AbstractWorkflowExecutor {

  private Logger logger;

  private boolean debug = false;

  public final void setDebug(final boolean newDebugMode) {
    this.debug = newDebugMode;
  }

  public final void setLogger(final Logger logger) {
    this.logger = logger;
    PreesmLogger.setLogger(logger);
  }

  public final Logger getLogger() {
    return logger;
  }

  /**
   * Ports with this name are ignored when exchanging data. They just specify precedence.
   */
  public static final String IGNORE_PORT_NAME = "void";

  /**
   * Checks the existence of all task and scenario classes and sets the classes in the workflow nodess.
   *
   * @param workflow
   *          the workflow
   * @param monitor
   *          the monitor
   * @return true, if successful
   */
  public boolean check(final Workflow workflow, final IProgressMonitor monitor) {

    if (monitor != null) {
      monitor.subTask("Checking workflow...");
    }

    boolean workflowOk = true;

    return workflowOk;
  }

  /**
   * Checks that the workflow scenario node edges fit the task prototype.
   *
   * @param scenarioNode
   *          the scenario node
   * @param workflow
   *          the workflow
   * @return true, if successful
   */
  public boolean checkScenarioPrototype(final ScenarioNode scenarioNode, final Workflow workflow) {

    return true;
  }

  /**
   * Checks that the workflow task node edges fit the task prototype.
   *
   * @param taskNode
   *          the task node
   * @param workflow
   *          the workflow
   * @return true, if successful
   */
  public boolean checkTaskPrototype(final TaskNode taskNode, final Workflow workflow) {
    final AbstractTaskImplementation task = taskNode.getTask();
    final Map<String, String> inputs = new LinkedHashMap<>();
    final Set<String> outputs = new LinkedHashSet<>();

    return true;
  }

  /**
   * Executes the workflow.
   *
   * @param workflowPath
   *          the workflow path
   * @param scenarioPath
   *          the scenario path
   * @param monitor
   *          the monitor
   * @return true, if successful
   */
  public boolean execute(final String workflowPath, final String scenarioPath, final IProgressMonitor monitor) {
    final Workflow workflow = new WorkflowParser().parse(workflowPath);

    boolean result = initAndCheck(workflowPath, monitor, workflow);
    if (!result) {
      return false;
    }
    final Handler eowHandler = addEOWHandler(workflow);
    final Level oldLevel = Optional.ofNullable(this.logger.getLevel()).orElse(Level.INFO);
    try {
      // read and apply workflow parameters
      this.logger.setLevel(workflow.getOutputLevel());
      WorkspaceUtils.updateWorkspace();

      final Iterator<AbstractWorkflowNode<?>> iterator = workflow.vertexTopologicalList().iterator();

      while (result && iterator.hasNext()) {
        AbstractWorkflowNode<?> node = iterator.next();
        result = executeNode(scenarioPath, monitor, workflow, node);
      }

      if (result) {
        log(Level.INFO, "Workflow.EndInfo", workflowPath);
      }

      WorkspaceUtils.updateWorkspace();

      // set back default logger behavior

    } catch (final ErrorOnWarningError e) {
      this.logger.log(e.getRecord());
      result = false;
    } finally {
      this.logger.removeHandler(eowHandler);
      this.logger.setLevel(oldLevel);
    }
    return result;
  }

  private Handler addEOWHandler(final Workflow workflow) {
    final boolean errorOnWarning = workflow.isErrorOnWarning();
    Handler eowHandler = null;
    if (errorOnWarning) {
      eowHandler = new ErrorOnWarningLogHandler();
      this.logger.addHandler(eowHandler);
    }
    return eowHandler;
  }

  private boolean executeNode(final String scenarioPath, final IProgressMonitor monitor, final Workflow workflow,
      final AbstractWorkflowNode<?> node) {
    return true;
  }

  private boolean initAndCheck(final String workflowPath, final IProgressMonitor monitor, final Workflow workflow) {
    // Initializing the workflow console

    // Checking the existence of plugins and retrieving prototypess
    boolean check = check(workflow, monitor);
    return check;
  }

  /**
   * Check output type.
   *
   * @param outputs
   *          the outputs
   * @param currentTaskNode
   *          the task
   * @throws PreesmException
   *           the workflow exception
   */
  private void checkOutputType(final Map<String, Object> outputs,
      final AbstractWorkflowNodeImplementation currentTaskNode) {
    final Set<Entry<String, Object>> outputEntries = outputs.entrySet();
    // Check outputs one by one
    for (final Entry<String, Object> outputEntry : outputEntries) {
      final String outputName = outputEntry.getKey();
      final Object outputValue = outputEntry.getValue();

      final String expectedOutputType = currentTaskNode.getOutputType(outputName);
      if (expectedOutputType == null) {
        throw new PreesmFrameworkException(
            "The task node " + currentTaskNode + " has an unspecified output with name " + outputName);
      }
      try {
        if (outputValue != null) {
          final ClassLoader outputTypeClassLoader = outputValue.getClass().getClassLoader();
          final Class<?> currentOutputType = Class.forName(expectedOutputType, true, outputTypeClassLoader);
          if (!currentOutputType.isInstance(outputValue)) {
            // Type is wrong !
            final String givenType = outputValue.getClass().getName();
            throw new PreesmFrameworkException("\nOutput \"" + outputName + "\" of workflow task \""
                + currentTaskNode.getClass() + "\" is null or has an invalid type.\n(expected: \"" + expectedOutputType
                + "\" given: \"" + givenType + "\")");
          }
        }
      } catch (final Exception ex) {
        final String message = "Could not check output type for [" + outputEntry + "] of task [" + currentTaskNode
            + "] with expected type [" + expectedOutputType + "].";
        throw new PreesmFrameworkException(message, ex);
      }
    }
  }

  /**
   * Checks that all necessary parameters are provided to the workflow task.
   *
   * @param taskNode
   *          workflow task node with the provided parameters
   * @param taskImplementation
   *          the task implementation requiring specific parameters
   * @return true, if successful
   */
  private boolean checkParameters(final TaskNode taskNode, final AbstractTaskImplementation taskImplementation) {
    final Map<String, String> defaultParameters = taskImplementation.getDefaultParameters();
    final Map<String, String> parameters = taskNode.getParameters();

    if (defaultParameters != null) {
      if (parameters == null) {
        if (defaultParameters.size() > 0) {
          log(Level.SEVERE, "Workflow.MissingAllParameters", taskNode.getName(), taskNode.getID(),
              defaultParameters.keySet().toString());
          return false;
        }
      } else {
        for (final Entry<String, String> p : defaultParameters.entrySet()) {
          if (!parameters.containsKey(p.getKey())) {
            // Antoine Morvan, 29/05/2017 :
            // https://github.com/preesm/dftools/issues/2
            // Instead of failing when a parameter is not specified, simply log a warning and
            // use value from defaultParameters.
            log(Level.WARNING, "Workflow.MissingParameter", taskNode.getName(), taskNode.getID(), p.getKey());
            parameters.put(p.getKey(), p.getValue());
          }
        }
      }
    }

    return true;
  }

  /**
   * Log method for workflow executions, depending on the available UI.
   *
   * @param level
   *          the level
   * @param msgKey
   *          the msg key
   * @param variables
   *          the variables
   */
  protected void log(Level level, String msgKey, String... variables) {
    getLogger().log(level, PreesmMessages.getString(msgKey, variables));
  }

}
