/*
 * Copyright (c) 2008, IETR/INSA of Rennes
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   * Neither the name of the IETR/INSA of Rennes nor the names of its
 *     contributors may be used to endorse or promote products derived from this
 *     software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package org.ietr.preesm.ui.editor.graph.validators;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.sf.graphiti.model.Edge;
import net.sf.graphiti.model.Graph;
import net.sf.graphiti.model.IValidator;
import net.sf.graphiti.model.Vertex;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * This class implements an Workflow model validator.
 * 
 * @author mpelcat
 * 
 */
public class WorkflowValidator implements IValidator {

	/**
	 * Validates the workflow by checking that every tasks are declared in
	 * loaded plug-ins. Each task implementation must additionally accept the
	 * textual input and output edges connected to it.
	 */
	@Override
	public boolean validate(Graph graph, IFile file) {

		/**
		 * Testing each task independently.
		 */
		Set<Vertex> vertices = graph.vertexSet();
		for (Vertex vertex : vertices) {
			if ("Task".equals(vertex.getType().getName())) {
				// Getting the plugin ID and the associated class name.
				String pluginId = (String) vertex.getValue("plugin identifier");
				IExtensionRegistry registry = Platform.getExtensionRegistry();
				IConfigurationElement[] elements = registry
						.getConfigurationElementsFor("net.sf.dftools.workflow.tasks");

				boolean foundClass = false;

				// Looking for the Id of the workflow task among the registry
				// elements
				for (IConfigurationElement element : elements) {
					String taskId = element.getAttribute("id");
					if (pluginId.equals(taskId)) {
						try {
							String taskType = element.getAttribute("type");
							/**
							 * Getting the class corresponding to the taskType
							 * string. This is only possible because of
							 * "Eclipse-BuddyPolicy: global" in the manifest:
							 * the Graphiti configuration class loader has a
							 * global knowledge of classes
							 */
							Class<?> vertexTaskClass = Class.forName(taskType);

							Object vertexTaskObj = vertexTaskClass
									.newInstance();

							// Adding the default parameters if necessary
							addDefaultParameters(vertex, vertexTaskObj, graph,
									file);

							foundClass = true;

						} catch (Exception e) {
							createMarker(
									file,
									"Class associated to the workflow task not found.",
									pluginId, IMarker.PROBLEM,
									IMarker.SEVERITY_ERROR);
							return true;
						}
					}
				}

				if (foundClass == false) {
					createMarker(
							file,
							"Plugin associated to the workflow task not found.",
							pluginId, IMarker.PROBLEM, IMarker.SEVERITY_ERROR);
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Getting the default parameters from the task class and adding them in the
	 * graph if they were not present. A warning giving the possible parameter
	 * values is displayed
	 */
	@SuppressWarnings("unchecked")
	private void addDefaultParameters(Vertex vertex, Object object,
			Graph graph, IFile file) {
		Map<String, String> parameterDefaults = null;
		try {
			Method prototypeMethod = object.getClass().getDeclaredMethod(
					"getDefaultParameters");
			Object obj = prototypeMethod.invoke(object);
			if (obj instanceof Map<?, ?>) {
				parameterDefaults = (Map<String, String>) obj;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (parameterDefaults != null) {

			Object var = vertex.getValue("variable declaration");
			Class<?> clasz = var.getClass();
			if (clasz == TreeMap.class) {
				TreeMap<String, String> varMap = (TreeMap<String, String>) var;

				for (String key : parameterDefaults.keySet()) {
					if (!varMap.containsKey(key)) {
						varMap.put(key, parameterDefaults.get(key));

						createMarker(file, "Added default parameter value: "
								+ key + ", " + parameterDefaults.get(key),
								(String) vertex.getValue("id"),
								IMarker.MESSAGE, IMarker.SEVERITY_INFO);
					}
				}
			}
		}
	}

	/**
	 * Displays an error
	 */
	protected void createMarker(IFile file, String message, String location,
			String type, int severity) {
		try {
			IMarker marker = file.createMarker(type);
			marker.setAttribute(IMarker.LOCATION, location);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.MESSAGE, message);
		} catch (CoreException e) {
		}
	}

}
