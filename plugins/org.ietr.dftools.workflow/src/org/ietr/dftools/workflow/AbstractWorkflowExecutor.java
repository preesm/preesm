/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
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
package org.ietr.dftools.workflow;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.workflow.elements.AbstractWorkflowNode;
import org.ietr.dftools.workflow.elements.ScenarioNode;
import org.ietr.dftools.workflow.elements.TaskNode;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.elements.WorkflowEdge;
import org.ietr.dftools.workflow.implement.AbstractScenarioImplementation;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation;
import org.ietr.dftools.workflow.messages.WorkflowMessages;
import org.ietr.dftools.workflow.tools.WorkflowLogger;

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
    WorkflowLogger.setLogger(logger);
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
    for (final AbstractWorkflowNode node : workflow.vertexSet()) {
      if (node.isScenarioNode()) {
        workflowOk = ((ScenarioNode) node).getExtensionInformation();

        // The plugin declaring the scenario class was not found
        if (!workflowOk) {
          log(Level.SEVERE, "Workflow.FailedFindScenarioPlugin", ((ScenarioNode) node).getScenarioId());
          return false;
        }
      } else if (node.isTaskNode() && !workflow.edgesOf(node).isEmpty()) {
        workflowOk = ((TaskNode) node).getExtensionInformation();

        // The plugin declaring the task class was not found
        if (!workflowOk) {
          log(Level.SEVERE, "Workflow.FailedFindTaskPlugin", ((TaskNode) node).getTaskId());
          return false;
        }
      }
    }

    // Check the workflow
    if (monitor != null) {
      monitor.worked(1);
    }

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
    final AbstractScenarioImplementation scenario = scenarioNode.getScenario();
    final Set<String> outputPorts = new LinkedHashSet<>();

    // There may be several output edges with same data type (sharing same
    // port). All output names are retrieved here.
    for (final WorkflowEdge edge : workflow.outgoingEdgesOf(scenarioNode)) {
      if (!outputPorts.contains(edge.getSourcePort())
          && !edge.getSourcePort().equals(AbstractWorkflowExecutor.IGNORE_PORT_NAME)) {
        outputPorts.add(edge.getSourcePort());
      }
    }

    // The scenario node prototype is compared to the output port names
    if (!scenario.acceptOutputs(outputPorts)) {
      log(Level.SEVERE, "Workflow.WrongScenarioPrototype", scenarioNode.getScenarioId(), scenario.displayPrototype());

      return false;
    }

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

    // input ports are retrieved as well as the data type
    // of the corresponding output port in the connected node
    for (final WorkflowEdge edge : workflow.incomingEdgesOf(taskNode)) {
      final AbstractWorkflowNode predecessor = workflow.getEdgeSource(edge);
      final String type = predecessor.getImplementation().getOutputType(edge.getSourcePort());
      if (!edge.getSourcePort().equals(AbstractWorkflowExecutor.IGNORE_PORT_NAME)) {
        inputs.put(edge.getTargetPort(), type);
      }
    }

    // There may be several output edges with same data type (sharing same
    // port)
    for (final WorkflowEdge edge : workflow.outgoingEdgesOf(taskNode)) {
      if (!outputs.contains(edge.getSourcePort())
          && !edge.getSourcePort().equals(AbstractWorkflowExecutor.IGNORE_PORT_NAME)) {
        outputs.add(edge.getSourcePort());
      }
    }

    if (!task.acceptInputs(inputs)) {
      log(Level.SEVERE, "Workflow.WrongTaskPrototype", taskNode.getTaskId(), task.displayPrototype());

      return false;
    }

    // The task prototype is compared to the output port names
    if (!task.acceptOutputs(outputs)) {
      log(Level.SEVERE, "Workflow.WrongTaskPrototype", taskNode.getTaskId(), task.displayPrototype());

      return false;
    }

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
    refreshProject(workflow.getProjectName(), monitor, "Warning: Could not refresh project before workflow execution");

    // Initializing the workflow console
    log(Level.INFO, "Workflow.StartInfo", workflowPath);
    if (monitor != null) {
      monitor.beginTask("Executing workflow", workflow.vertexSet().size());
    }

    // Checking the existence of plugins and retrieving prototypess
    if (!check(workflow, monitor)) {
      if (monitor != null) {
        monitor.setCanceled(true);
      }
      return false;
    }

    if (workflow.vertexSet().isEmpty()) {
      log(Level.SEVERE, "Workflow.EmptyVertexSet");

      return false;
    }

    if (!workflow.hasScenario()) {
      log(Level.SEVERE, "Workflow.OneScenarioNeeded");

      return false;
    }

    for (final AbstractWorkflowNode node : workflow.vertexTopologicalList()) {
      if (workflow.edgesOf(node).isEmpty()) {
        log(Level.WARNING, "Workflow.IgnoredNonConnectedTask");
      } else {
        // Data outputs of the node
        Map<String, Object> outputs = null;
        String nodeId = null;

        if (node.isScenarioNode()) {
          // The scenario node is special because it gets a reference
          // path and generates the inputs of the rapid prototyping
          // process
          final ScenarioNode scenarioNode = (ScenarioNode) node;
          nodeId = scenarioNode.getScenarioId();
          final AbstractScenarioImplementation scenario = scenarioNode.getScenario();

          // Checks that the scenario node output edges fit the task
          // prototype
          if (!checkScenarioPrototype(scenarioNode, workflow)) {
            return false;
          }

          try {
            // updating monitor
            if (monitor != null) {
              monitor.subTask(scenario.monitorMessage());
            }
            outputs = scenario.extractData(scenarioPath);

            // Filter only outputs required in the workflow
            final Map<String, Object> outs = outputs; // final
            // reference
            // for
            // predicate
            final Set<WorkflowEdge> edges = new LinkedHashSet<>(workflow.outgoingEdgesOf(scenarioNode));
            edges.removeIf(edge -> !outs.containsKey(edge.getSourcePort()));

            final Map<String, Object> checkedOutputs = new LinkedHashMap<>();
            edges.forEach(edge -> checkedOutputs.put(edge.getSourcePort(), outs.get(edge.getSourcePort())));

            // Check the outputs have the right type.
            checkOutputType(checkedOutputs, scenario);

            // Each node execution is equivalent for the monitor
            if (monitor != null) {
              monitor.worked(1);
            }
          } catch (final WorkflowException e) {
            error(e, e.getMessage());
            return false;
          }

        } else if (node.isTaskNode()) {
          final TaskNode taskNode = (TaskNode) node;
          final AbstractTaskImplementation task = taskNode.getTask();
          nodeId = taskNode.getTaskId();

          // Checks that the workflow task node edges fit the task
          // prototype
          if (!checkTaskPrototype(taskNode, workflow)) {
            return false;
          }

          // Preparing the input and output maps of the execute method
          final Map<String, Object> inputs = new LinkedHashMap<>();

          // Retrieving the data from input edges
          for (final WorkflowEdge edge : workflow.incomingEdgesOf(taskNode)) {
            inputs.put(edge.getTargetPort(), edge.getData());
          }

          // Executing the workflow task
          try {
            // updating monitor and console
            final StringBuilder monitorMessage = new StringBuilder();
            if (this.debug) {
              monitorMessage.append("[" + task.getClass().getSimpleName() + "] ");
            }
            monitorMessage.append(task.monitorMessage());

            final String monitorMessageStr = monitorMessage.toString();
            if (monitor != null) {
              monitor.subTask(monitorMessageStr);
            }
            log(Level.INFO, "Workflow.Step", monitorMessageStr);

            // Workflow cancellation was requested
            if ((monitor != null) && monitor.isCanceled()) {

              log(Level.SEVERE, "Workflow.CancellationRequested");
              return false;
            }

            // Checks that the requested parameters are available
            // for the task
            final boolean checkParameters = checkParameters(taskNode, task);
            if (!checkParameters) {
              return false;
            }

            // execution
            outputs = task.execute(inputs, taskNode.getParameters(), monitor, nodeId, workflow);

            // Filter only outputs required in the workflow
            final Map<String, Object> outs = outputs; // final
            // reference
            // for
            // predicate
            final Set<WorkflowEdge> edges = new LinkedHashSet<>(workflow.outgoingEdgesOf(taskNode));
            edges.removeIf(edge -> !outs.containsKey(edge.getSourcePort()));

            final Map<String, Object> checkedOutputs = new LinkedHashMap<>();
            edges.forEach(edge -> checkedOutputs.put(edge.getSourcePort(), outs.get(edge.getSourcePort())));

            // Check the outputs have the right type.
            checkOutputType(outputs, task);

            // Each node execution is equivalent for the monitor
            if (monitor != null) {
              monitor.worked(1);
            }
          } catch (final WorkflowException e) {
            error(e, e.getMessage());
            return false;
          } catch (final Exception e) {
            final StringWriter error = new StringWriter();
            e.printStackTrace(new PrintWriter(error));
            error(e, "Unexpected Exception: " + error.toString()
                + "\n Contact Preesm developers if you cannot solve the problem.");
            return false;
          }
        }

        // If the execution incorrectly initialized the outputs
        if (outputs == null) {
          log(Level.SEVERE, "Workflow.NullNodeOutput", nodeId);
          return false;
        } else {
          // Retrieving output of the current node
          // Putting the data in output edges
          for (final WorkflowEdge edge : workflow.outgoingEdgesOf(node)) {
            final String type = edge.getSourcePort();
            // The same data may be transferred to several
            // successors
            if (type.equals(AbstractWorkflowExecutor.IGNORE_PORT_NAME)) {
              // Ignore data
            } else if (outputs.containsKey(type)) {
              edge.setData(outputs.get(type));
            } else {
              edge.setData(null);
              log(Level.SEVERE, "Workflow.IncorrectOutput", nodeId, type);
              return false;
            }
          }
        }
      }
    }

    refreshProject(workflow.getProjectName(), monitor, "Warning: Could not refresh project after workflow execution");
    log(Level.INFO, "Workflow.EndInfo", workflowPath);

    return true;
  }

  private final void refreshProject(final String projectName, final IProgressMonitor monitor,
      final String warningMessage) {
    try {
      final IWorkspace workspace = ResourcesPlugin.getWorkspace();
      final IProject project = workspace.getRoot().getProject(projectName);
      project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
    } catch (final Exception e) {
      WorkflowLogger.getLogger().log(Level.WARNING, warningMessage, e);
    }
  }

  /**
   * Check output type.
   *
   * @param outputs
   *          the outputs
   * @param currentTaskNode
   *          the task
   * @throws WorkflowException
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
        throw new WorkflowException(
            "The task node " + currentTaskNode + " has an unspecified output with name " + outputName);
      }
      try {
        if (outputValue != null) {
          final ClassLoader outputTypeClassLoader = outputValue.getClass().getClassLoader();
          final Class<?> currentOutputType = Class.forName(expectedOutputType, true, outputTypeClassLoader);
          if (!currentOutputType.isInstance(outputValue)) {
            // Type is wrong !
            final String givenType = outputValue.getClass().getName();
            throw new WorkflowException("\nOutput \"" + outputName + "\" of workflow task \""
                + currentTaskNode.getClass() + "\" is null or has an invalid type.\n(expected: \"" + expectedOutputType
                + "\" given: \"" + givenType + "\")");
          }
        }
      } catch (final Exception ex) {
        final String message = "Could not check output type for [" + outputEntry + "] of task [" + currentTaskNode
            + "] with expected type [" + expectedOutputType + "].";
        throw new WorkflowException(message, ex);
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
          log(Level.SEVERE, "Workflow.MissingAllParameters", taskNode.getTaskId(),
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
            log(Level.WARNING, "Workflow.MissingParameter", taskNode.getTaskId(), p.getKey());
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
    getLogger().log(level, WorkflowMessages.getString(msgKey, variables));
  }

  /**
   * This special logging method is used for handling severe message with associated exception or error.
   *
   * TODO: add debug parameter to the application and handle it here to show (or not) the full stack trace/
   */
  protected void error(final Throwable cause, final String msg) {
    getLogger().log(Level.SEVERE, msg, cause);
  }
}
