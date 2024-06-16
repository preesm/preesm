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
package org.preesm.ui.workflow;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.ietr.dftools.graphiti.model.Graph;
import org.ietr.dftools.graphiti.model.IValidator;
import org.ietr.dftools.graphiti.model.Vertex;
import org.preesm.commons.PreesmPlugin;
import org.preesm.commons.exceptions.PreesmFrameworkException;

/**
 * This class implements a Workflow model validator. This validator checks that the workflow is executable.
 *
 * @author mpelcat
 *
 */
public class WorkflowValidator implements IValidator {

  /**
   * Validates the workflow by checking that every tasks are declared in loaded plug-ins. Each task implementation must
   * additionally accept the textual input and output edges connected to it.
   *
   * @param graph
   *          the graph
   * @param file
   *          the file
   * @return true, if successful
   */
  @Override
  public boolean validate(final Graph graph, final IFile file) {

    /**
     * Testing each task independently.
     */
    final Set<Vertex> vertices = graph.vertexSet();
    for (final Vertex vertex : vertices) {
      if ("Task".equals(vertex.getType().getName()) && !validateTaskVertex(file, vertex)) {
        return false;
      }
    }

    return true;
  }

  private boolean validateTaskVertex(final IFile file, final Vertex vertex) {
    // Getting the plugin ID and the associated class name.
    final String pluginId = (String) vertex.getValue("plugin identifier");
    final String vertexName = (String) vertex.getValue("id");

    if (pluginId == null) {
      createMarker(file, "Task '" + vertexName + "' has no id.", "Task '" + vertexName + "'", IMarker.PROBLEM,
          IMarker.SEVERITY_ERROR);
      return false;
    }

    final Class<?> preesmWorkflowTask = PreesmPlugin.getInstance().getTask(pluginId);

    if (preesmWorkflowTask == null) {
      final String location = "task='" + vertexName + "' id='" + pluginId + "'";
      final String message = "Plugin or Class with id '" + pluginId + "' associated to the workflow task '" + vertexName
          + "' could not be found.\n"
          + "Please check for typos, leading/trailing spaces, or if the id changed at https://preesm.github.io/docs/workflowtasksref/";
      createMarker(file, message, location, IMarker.PROBLEM, IMarker.SEVERITY_ERROR);
      return false;
    }

    try {
      final Object vertexTaskObj = preesmWorkflowTask.getConstructor().newInstance();
      // Adding the default parameters if necessary
      addDefaultParameters(vertex, vertexTaskObj, file);
    } catch (final InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
      createMarker(file, "Class could not be instantiated", pluginId, IMarker.PROBLEM, IMarker.SEVERITY_ERROR);
      return false;
    }

    return true;
  }

  /**
   * Getting the default parameters from the task class and adding them in the graph if they were not present. A warning
   * giving the possible parameter values is displayed
   *
   * @param vertex
   *          the vertex
   * @param object
   *          the object
   * @param graph
   *          the graph
   * @param file
   *          the file
   */
  @SuppressWarnings("unchecked")
  private void addDefaultParameters(final Vertex vertex, final Object object, final IFile file) {
    Map<String, String> parameterDefaults = null;
    try {
      final Method prototypeMethod = object.getClass().getMethod("getDefaultParameters");
      final Object obj = prototypeMethod.invoke(object);
      if (obj instanceof Map<?, ?>) {
        parameterDefaults = (Map<String, String>) obj;
      }
    } catch (final Exception e) {
      throw new PreesmFrameworkException("Could not add default parameters", e);
    }

    if (parameterDefaults != null) {

      final Object variable = vertex.getValue("variable declaration");
      final Class<?> clasz = variable.getClass();
      if (clasz == TreeMap.class) {
        final TreeMap<String, String> varMap = (TreeMap<String, String>) variable;

        for (Entry<String, String> e : parameterDefaults.entrySet()) {
          final String key = e.getKey();
          if (!varMap.containsKey(key)) {
            varMap.put(key, parameterDefaults.get(key));

            createMarker(file, "Added default parameter value: " + key + ", " + parameterDefaults.get(key),
                (String) vertex.getValue("id"), IMarker.MESSAGE, IMarker.SEVERITY_INFO);
          }
        }
      }
    }
  }

  /**
   * Displays an error.
   *
   * @param file
   *          the file
   * @param message
   *          the message
   * @param location
   *          the location
   * @param type
   *          the type
   * @param severity
   *          the severity
   */
  protected void createMarker(final IFile file, final String message, final String location, final String type,
      final int severity) {
    try {
      final IMarker marker = file.createMarker(type);
      marker.setAttribute(IMarker.LOCATION, location);
      marker.setAttribute(IMarker.SEVERITY, severity);
      marker.setAttribute(IMarker.MESSAGE, message);
    } catch (final CoreException e) {
      throw new PreesmFrameworkException("Coule not create marker", e);
    }
  }

}
