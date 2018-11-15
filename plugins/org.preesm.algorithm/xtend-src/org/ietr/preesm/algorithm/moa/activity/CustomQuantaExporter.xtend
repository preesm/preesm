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

import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.file.Paths
import java.util.HashMap
import java.util.HashSet
import java.util.Map
import java.util.Set
import java.util.logging.Level
import jxl.CellType
import jxl.Workbook
import jxl.read.biff.BiffException
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.runtime.CoreException
import org.eclipse.core.runtime.IPath
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.core.runtime.Path
import org.ietr.dftools.algorithm.importer.InvalidModelException
import org.ietr.preesm.core.architecture.route.AbstractRouteStep
import org.ietr.preesm.core.architecture.route.MessageRouteStep
import org.ietr.preesm.core.scenario.PreesmScenario
import org.ietr.preesm.core.types.ImplementationPropertyNames
import org.ietr.preesm.mapper.abc.SpecialVertexManager
import org.ietr.preesm.mapper.abc.impl.latency.LatencyAbc
import org.ietr.preesm.mapper.model.MapperDAGEdge
import org.ietr.preesm.mapper.model.MapperDAGVertex
import org.nfunk.jep.JEP
import org.nfunk.jep.ParseException
import org.preesm.commons.files.ContainersManager
import org.preesm.commons.files.PathTools
import org.preesm.commons.logger.PreesmLogger
import org.preesm.model.pisdf.Actor
import org.preesm.model.pisdf.PiGraph
import org.preesm.model.pisdf.serialize.PiParser
import org.preesm.model.slam.ComponentInstance
import org.preesm.workflow.WorkflowException
import org.preesm.workflow.elements.Workflow
import org.preesm.workflow.implement.AbstractTaskImplementation

/**
 * Exports activity information in terms of  quanta based on
 * individual quanta information on actors set in an xls file
 *
 * @author mpelcat
 *
 */
class CustomQuantaExporter extends AbstractTaskImplementation {

	/**
	 * Tag for storing the path of the input file with individual quanta information
	 */
	val INPUT_XLS_FILE = "xls_file";

	/**
	 * Tag for storing the path of the file to store data of per core information
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

	/**
	 * Number of custom quanta per actor and operator
	 */
	CustomQuanta customQuanta = null

	new() {
		visited = new HashSet<MapperDAGVertex>()
		activity = new Activity()
		customQuanta = new CustomQuanta()
	}

	/**
	 * Exporting in a CSV file custom quanta information
	 */
	override execute(Map<String, Object> inputs, Map<String, String> parameters, IProgressMonitor monitor,
		String nodeName, Workflow workflow) throws WorkflowException {
		var logger = PreesmLogger.getLogger()

		val filePath = parameters.get(PATH)
		val human_readable = (parameters.get(HUMAN_READABLE) == "Yes")
		// The abc contains all information on the implemented system
		var abc = inputs.get(KEY_SDF_ABC) as LatencyAbc

		var inputXLSFile = parameters.get(INPUT_XLS_FILE)

		if (abc !== null) {
			// The pattern $SCENARIO$ in the input excel file name is replaced by the scenario name
			val scenario = abc.scenario
			val scenarioURL = scenario.scenarioURL
			val scenarioName = Paths.get(scenarioURL).fileName.toString.replace(".scenario", "")
			inputXLSFile = inputXLSFile.replace("$SCENARIO$", scenarioName)
			inputXLSFile = PathTools.getAbsolutePath(inputXLSFile, workflow.getProjectName())

			// parsing individual quanta values from an excel file
			parseQuantaInputFile(inputXLSFile, abc.scenario)

			writeActivity(abc, filePath, workflow, human_readable)
		} else {
			logger.log(Level.SEVERE, "Not a valid set of ABCs for ActivityExporter.")
		}

		logger.log(Level.INFO, "Activity: " + activity)

		return new HashMap<String, Object>()
	}

	override getDefaultParameters() {
		var defaultParams = new HashMap<String, String>()
		defaultParams.put(INPUT_XLS_FILE, "stats/mat/custom_quanta_in/quanta_in_$SCENARIO$.xls")
		defaultParams.put(PATH, "stats/mat/activity")
		defaultParams.put(HUMAN_READABLE, "Yes")
		return defaultParams
	}

	override monitorMessage() {
		return "Exporting implementation activity in terms of custom quanta."
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
		writeString(activity.quantaString(human_readable), "custom_quanta", path, workflow)
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
	 * Activity is computed from time information and from custom quanta information
	 */
	def private visitVertex(MapperDAGVertex vertex) {
		val duration = vertex.getPropertyStringValue(ImplementationPropertyNames.Task_duration)
		val operator = vertex.getEffectiveComponent().component
		val actor = vertex.correspondingSDFVertex
		val cquanta = customQuanta.getQuanta(actor.id, operator.vlnv.name)

		if(!cquanta.isEmpty){
			// Resolving the value as a String expression of t
			var jep = new JEP()
			jep.addVariable("t", Double.valueOf(duration))

			try {
				val node = jep.parse(cquanta)
				val result = jep.evaluate(node) as Double

				activity.addQuantaNumber(vertex.getEffectiveComponent().instanceName,
					result.longValue)

				PreesmLogger.getLogger().log(Level.INFO,
					"Custom quanta set to " + result.longValue + " by solving " + cquanta + " with t=" + duration + " for " + vertex.name)
			} catch (ParseException exc) {
				throw new RuntimeException(exc)
			}
		} else if(SpecialVertexManager.isBroadCast(vertex)){
			// Broadcasts have a fix ponderation of their custom quanta compared to timing
			var correctedDuration = Double.valueOf(duration)
			correctedDuration = correctedDuration * 0.720;
			activity.addQuantaNumber(vertex.getEffectiveComponent().instanceName,
				correctedDuration.longValue)

			PreesmLogger.getLogger().log(Level.INFO,
				"Broadcast custom quanta set to " + correctedDuration.longValue + " by applying constant factor 0.72 to " + duration + " for " + vertex.name)

		} else {
			activity.addQuantaNumber(vertex.getEffectiveComponent().instanceName,
				Long.valueOf(duration))
		}
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
					val mstep = step
					for (ComponentInstance node : mstep.nodes){
						activity.addQuantaNumber(node.instanceName, size)
					}
				}
			}
		}

	}

	/**
	 * Writing CSV text containing the activity description in fileName located in stringPath.
	 */
	static def void writeString(String text, String fileName, String stringPath, Workflow workflow) {

		var sPath = PathTools.getAbsolutePath(stringPath, workflow.getProjectName())
		var path = new Path(sPath) as IPath
		path = path.append(fileName + ".csv")

		// Get a complete valid path with all folders existing
		try {
			if (path.getFileExtension() !== null) {
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

	/**
	 * Reading individual quanta information from an excel file.
	 */
	def void  parseQuantaInputFile(String fileName, PreesmScenario scenario) {

		val path = new Path(fileName);
		val iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path)

		if (!iFile.exists()) {
			PreesmLogger.getLogger().log(Level.SEVERE,
				"The custom quanta input file " + fileName + " does not exist.")
		} else {
			PreesmLogger.getLogger().log(Level.INFO, "Reading custom quanta from file " + fileName + ".")
			try {
				var w = Workbook.getWorkbook(iFile.getContents())

				if (scenario.isPISDFScenario()) {
					val currentGraph = PiParser.getPiGraph(scenario.getAlgorithmURL())
					val operators = scenario.getOperatorDefinitionIds()
					parseQuantaForPISDFGraph(w, currentGraph, operators)
				} else {
					PreesmLogger.getLogger().log(Level.SEVERE,
						"Only PiSDF graphs are supported for custom quanta export.")
				}

			// Warnings are displayed once for each missing operator or vertex
			// in the excel sheet
			} catch (IOException e) {
				e.printStackTrace()
			} catch (CoreException e) {
				e.printStackTrace()
			} catch (InvalidModelException e) {
				e.printStackTrace()
			} catch (BiffException e) {
				e.printStackTrace()
			}
		}

	}

	/**
	 * Reading individual quanta information from an excel file.
	 * Recursive method.
	 */
	def void parseQuantaForPISDFGraph(Workbook w, PiGraph appli, Set<String> operators) {
		// Each of the vertices of the graph is either itself a graph
		// (hierarchical vertex), in which case we call recursively this method;
		// we parse quanta for standard and special vertices
		for (vertex : appli.actors) {
			// Handle connected graphs from hierarchical vertices
			if (vertex instanceof PiGraph) {
				parseQuantaForPISDFGraph(w, vertex, operators)
			} else if (vertex instanceof Actor) {
				val actor = vertex;

				// Handle unconnected graphs from hierarchical vertices
				val refinement = actor.getRefinement();
				if (refinement !== null){
					val subgraph = refinement.abstractActor

					if (subgraph !== null && subgraph instanceof PiGraph) {
						parseQuantaForPISDFGraph(w, subgraph as PiGraph, operators);
					}
					// If the actor is not hierarchical, parse its timing
					else {
						parseQuantaForVertex(w, vertex.getName(), operators);
					}
				}
			}
			else{
				// case of special actors
				parseQuantaForVertex(w, vertex.getName(), operators);

			}
		}
	}

	/**
	 * Reading individual quanta information from an excel file.
	 * Recursive method.
	 */
	def void parseQuantaForVertex(Workbook w, String vertexName, Set<String> operators) {

		// Test excel reader, to be continued
		for(opDefId : operators){
			var vertexCell = w.getSheet(0).findCell(vertexName)
			var operatorCell = w.getSheet(0).findCell(opDefId);

			if (vertexCell !== null && operatorCell !== null) {
				// Get the cell containing the timing
				var timingCell = w.getSheet(0).getCell(operatorCell.getColumn(), vertexCell.getRow())

				if (timingCell.getType().equals(CellType.NUMBER) || timingCell.getType().equals(
					CellType.NUMBER_FORMULA)) {

					var stringQuanta = timingCell.getContents()
					// Removing useless characters (spaces...)
					stringQuanta = stringQuanta.replaceAll(" ", "")

					try {
						// Testing the validity of the value as a Long number.
						var quantaValue = Long.valueOf(timingCell.contents)

						PreesmLogger.getLogger().log(Level.INFO, "Importing custom quantum: " +
							vertexName + " on " + opDefId + ": " + quantaValue
						)
						customQuanta.addQuantaExpression(vertexName, opDefId, timingCell.contents)

					} catch (NumberFormatException e) {
						PreesmLogger.getLogger().log(Level.SEVERE,
							"Problem importing quanta of " + vertexName + " on " + opDefId +
								". Integer with no space or special character needed. Be careful on the special number formats.")
					}
				}
				else {

					try {
						// Case of a string formula
						PreesmLogger.getLogger().log(Level.INFO, "Detected formula: " + timingCell.contents +
							" for " + vertexName + " on " + opDefId)

						// Testing the validity of the value as a String expression of t
						var jep = new JEP()
						jep.addVariable("t", 1)
						val node = jep.parse(timingCell.contents)
						jep.evaluate(node)

						customQuanta.addQuantaExpression(vertexName, opDefId, timingCell.contents)
					} catch (ParseException e) {
						PreesmLogger.getLogger().log(Level.SEVERE,
							"Problem evaluating quanta expression of " + vertexName + " on " + opDefId +
								": " + timingCell.contents)
					}

				}
			} else {
				if (vertexCell === null) {
					PreesmLogger.getLogger().log(Level.WARNING,
						"No line found in custom quanta excel sheet for vertex: " + vertexName)
				} else if (operatorCell === null) {
					PreesmLogger.getLogger().log(Level.WARNING,
						"No column found in custom quanta excel sheet for operator type: " + opDefId);
				}
			}
		}
	}

}
