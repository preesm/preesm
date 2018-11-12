/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
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
package org.ietr.dftools.ui.workflow;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.ietr.dftools.graphiti.model.Graph;
import org.ietr.dftools.graphiti.model.IValidator;
import org.ietr.dftools.graphiti.model.Vertex;
import org.ietr.dftools.workflow.WorkflowException;

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
      if ("Task".equals(vertex.getType().getName())) {
        // Getting the plugin ID and the associated class name.
        final String pluginId = (String) vertex.getValue("plugin identifier");

        if (pluginId == null) {
          createMarker(file, "Enter a plugin identifier for each plugin.", "Any plugin", IMarker.PROBLEM,
              IMarker.SEVERITY_ERROR);
          return false;
        }

        final IExtensionRegistry registry = Platform.getExtensionRegistry();
        final IConfigurationElement[] elements = registry
            .getConfigurationElementsFor("org.ietr.dftools.workflow.tasks");

        boolean foundClass = false;

        // Looking for the Id of the workflow task among the registry
        // elements
        for (final IConfigurationElement element : elements) {
          final String taskId = element.getAttribute("id");
          if (pluginId.equals(taskId)) {
            try {
              final String taskType = element.getAttribute("type");
              /**
               * Getting the class corresponding to the taskType string. This is only possible because of
               * "Eclipse-BuddyPolicy: global" in the manifest: the Graphiti configuration class loader has a global
               * knowledge of classes
               */

              final Class<?> vertexTaskClass = Class.forName(taskType);

              final Object vertexTaskObj = vertexTaskClass.newInstance();

              // Adding the default parameters if necessary
              addDefaultParameters(vertex, vertexTaskObj, file);

              foundClass = true;

            } catch (final Exception e) {
              createMarker(file, "Class associated to the workflow task not found. Is the class path exported?",
                  pluginId, IMarker.PROBLEM, IMarker.SEVERITY_ERROR);
              return true;
            }
          }
        }

        if (!foundClass) {
          createMarker(file, "Plugin associated to the workflow task not found.", pluginId, IMarker.PROBLEM,
              IMarker.SEVERITY_ERROR);
          return false;
        }
      }
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
      throw new WorkflowException("Could not add default parameters", e);
    }

    if (parameterDefaults != null) {

      final Object var = vertex.getValue("variable declaration");
      final Class<?> clasz = var.getClass();
      if (clasz == TreeMap.class) {
        final TreeMap<String, String> varMap = (TreeMap<String, String>) var;

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
      throw new WorkflowException("Coule not create marker", e);
    }
  }

}
