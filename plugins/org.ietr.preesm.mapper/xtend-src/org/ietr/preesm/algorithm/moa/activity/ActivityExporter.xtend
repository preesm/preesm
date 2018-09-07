/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
package org.ietr.preesm.algorithm.moa.activity

import org.ietr.dftools.workflow.implement.AbstractTaskImplementation
import java.util.Map
import org.eclipse.core.runtime.IProgressMonitor
import org.ietr.dftools.workflow.elements.Workflow
import org.ietr.dftools.workflow.WorkflowException
import java.util.logging.Level
import org.ietr.dftools.workflow.tools.WorkflowLogger
import org.ietr.preesm.mapper.abc.impl.latency.LatencyAbc
import java.util.HashMap
import java.util.Set
import org.ietr.preesm.mapper.model.MapperDAGVertex
import org.ietr.preesm.mapper.model.MapperDAGEdge
import java.util.HashSet
import org.ietr.preesm.core.architecture.route.AbstractRouteStep
import org.ietr.preesm.core.types.ImplementationPropertyNames
import org.ietr.preesm.utils.paths.PathTools
import org.eclipse.core.runtime.Path
import org.eclipse.core.runtime.IPath
import org.ietr.preesm.utils.files.ContainersManager
import org.eclipse.core.runtime.CoreException
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.runtime.NullProgressMonitor
import java.io.ByteArrayInputStream
import org.ietr.dftools.architecture.slam.ComponentInstance
import org.ietr.preesm.core.architecture.route.MessageRouteStep

/**
 * Exports activity information in terms of tokens and quanta based on LatencyAbc information
 * in the CSV file
 *
 * @author mpelcat
 *
 */
class ActivityExporter extends AbstractTaskImplementation {

	/**
	 * Tag for storing the path of the file to store data
	 */
	val PATH = "path";

	/**
	 * Tag for storing the information on whether to display the component names or not
	 */
	val HUMAN_READABLE = "human_readable";

	/**
	 * Storing the graph vertices already explored
	 */
	Set<MapperDAGVertex> visited = null

	/**
	 * Currently retrieved activity
	 */
	Activity activity = null

	new() {
		visited = new HashSet<MapperDAGVertex>()
		activity = new Activity()
	}

	/**
	 * Exporting in a CSV file tokens and quanta information
	 */
	override execute(Map<String, Object> inputs, Map<String, String> parameters, IProgressMonitor monitor,
		String nodeName, Workflow workflow) throws WorkflowException {
		var logger = WorkflowLogger.getLogger()

		val filePath = parameters.get(PATH)
		val human_readable = (parameters.get(HUMAN_READABLE) == "Yes")
		// The abc contains all information on the implemented system
		var abc = inputs.get(KEY_SDF_ABC) as LatencyAbc

		if (abc != null) {
			writeActivity(abc, filePath, workflow, human_readable)
		} else {
			logger.log(Level.SEVERE, "Not a valid set of ABCs for ActivityExporter.")
		}

		logger.log(Level.INFO, "Activity: " + activity)

		return new HashMap<String, Object>()
	}

	override getDefaultParameters() {
		var defaultParams = new HashMap<String, String>()
		defaultParams.put(PATH, "stats/mat/activity")
		defaultParams.put(HUMAN_READABLE, "Yes")
		return defaultParams
	}

	override monitorMessage() {
		return "Exporting implementation activity in terms of tokens and quanta."
	}

	/**
	 * Generating a class with activity information, then exporting it in a text file
	 */
	def private writeActivity(LatencyAbc abc, String path, Workflow workflow, boolean human_readable) {

		// Clearing the history
		visited.clear
		activity.clear

		// Initializing all PE and CN information to 0
		var architecture = abc.architecture
		for (ComponentInstance vertex : architecture.componentInstances) {
			activity.addTokenNumber(vertex.instanceName, 0)
			activity.addQuantaNumber(vertex.instanceName, 0)
		}

		// Generating activity information
		for (MapperDAGVertex vertex : abc.implementation.sources) {
			addVertexAndSuccessors(vertex, abc)
			vertex.getSuccessors(true)
		}

		// Writing the activity csv file
		writeString(activity.tokensString(human_readable), "tokens", path, workflow)
		writeString(activity.quantaString(human_readable), "quanta", path, workflow)
	}

	/**
	 * Recursive function to scan the whole application for extracting activity
	 */
	def private void addVertexAndSuccessors(MapperDAGVertex vertex, LatencyAbc abc) {
		// add a vertex and its successors to activity information
		visitVertex(vertex)
		visited.add(vertex); // avoiding multiple visits of a vertex
		for (MapperDAGVertex succ : vertex.getSuccessors(true).keySet()) {
			var edge = vertex.getSuccessors(true).get(succ)
			visitEdge(edge, abc); // Visiting edge even if the successor vertex was already visited
			if (!visited.contains(succ)) {
				addVertexAndSuccessors(succ, abc)
			}
		}
	}

	/**
	 * Visiting one vertex and extracting activity
	 */
	def private visitVertex(MapperDAGVertex vertex) {
		activity.addTokenNumber(vertex.getEffectiveComponent().instanceName, 1)
		activity.addQuantaNumber(vertex.getEffectiveComponent().instanceName,
			Long.valueOf(vertex.getPropertyStringValue(ImplementationPropertyNames.Task_duration)))
	}

	/**
	 * Visiting one edge and extracting activity
	 */
	def private visitEdge(MapperDAGEdge edge, LatencyAbc abc) {
		var source = edge.source as MapperDAGVertex
		var target = edge.target as MapperDAGVertex
		if (!(source.getEffectiveComponent()).equals(target.getEffectiveComponent())) {
			var size = edge.init.dataSize
			var route = abc.getComRouter().getRoute(edge)

			// Counting tokens and quanta for each elements in the route between 2 processors for an edge
			for (AbstractRouteStep step : route) {
				if(step instanceof MessageRouteStep){
					// a step is internally composed of several communication nodes
					val mstep = step as MessageRouteStep
					for (ComponentInstance node : mstep.nodes){
						activity.addTokenNumber(node.instanceName, 1)
						activity.addQuantaNumber(node.instanceName, size)
					}
				}
			}
		}

	}

	/**
	 * Writing CSV text containing the activity description in fileName located in stringPath.
	 */
	static def writeString(String text, String fileName, String stringPath,
		Workflow workflow) {

		var sPath = PathTools.getAbsolutePath(stringPath, workflow.getProjectName())
		var path = new Path(sPath) as IPath
		path = path.append(fileName + ".csv")

		// Get a complete valid path with all folders existing
		try {
			if (path.getFileExtension() != null) {
				ContainersManager.createMissingFolders(path.removeFileExtension().removeLastSegments(1))
			} else {
				ContainersManager.createMissingFolders(path)
			}
		} catch (CoreException e) {
			throw new WorkflowException("Path " + path + " is not a valid path for export.")
		}

		var iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path)
		try {
			if (!iFile.exists()) {
				iFile.create(null, false, new NullProgressMonitor())
				iFile.setContents(new ByteArrayInputStream(text.getBytes()), true, false, new NullProgressMonitor())
			} else {
				iFile.setContents(new ByteArrayInputStream(text.getBytes()), true, false, new NullProgressMonitor())
			}
		} catch (CoreException e1) {
			e1.printStackTrace()
		}
	}
}
