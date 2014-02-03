package org.ietr.preesm.experiment.memory

import bsh.EvalError
import bsh.Interpreter
import java.io.File
import java.io.IOException
import java.net.URI
import java.util.ArrayList
import java.util.HashMap
import java.util.List
import java.util.Map
import java.util.Set
import java.util.logging.Level
import java.util.logging.Logger
import net.sf.dftools.algorithm.model.dag.DAGVertex
import net.sf.dftools.algorithm.model.dag.DirectedAcyclicGraph
import net.sf.dftools.algorithm.model.dag.edag.DAGBroadcastVertex
import net.sf.dftools.algorithm.model.dag.edag.DAGForkVertex
import net.sf.dftools.algorithm.model.dag.edag.DAGJoinVertex
import net.sf.dftools.algorithm.model.parameters.Argument
import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex
import net.sf.dftools.algorithm.model.sdf.SDFEdge
import net.sf.dftools.algorithm.model.sdf.SDFGraph
import net.sf.dftools.algorithm.model.sdf.SDFVertex
import net.sf.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex
import net.sf.dftools.workflow.tools.WorkflowLogger
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.runtime.FileLocator
import org.eclipse.core.runtime.Path
import org.eclipse.xtext.xbase.lib.Pair
import org.ietr.preesm.core.types.DataType
import bsh.ParseException

enum CheckPolicy {
	NONE,
	FAST,
	THOROUGH
}

class ScriptRunner {
	
	@Property
	var checkPolicy = CheckPolicy::NONE

	/**
	 * A {@link Map} that associates each {@link String} representing a type name 
	 * with a corresponding {@link DataType}. 
	 */
	@Property
	var Map<String, DataType> dataTypes

	/** 
	 * A {@link Map} that associates each {@link DAGVertex} with a
	 * memory script to this memory script {@link File}.
	 */
	val scriptedVertices = new HashMap<DAGVertex, File>();

	/**
	 * A {@link Map} that associates each {@link DAGVertex} from the
	 * {@link #scriptedVertices} map to the result of the successful 
	 * execution of its script. The result is stored as a {@link Pair}
	 * of {@link List} of {@link Buffer}. The first {@link List} contains
	 * the input {@link Buffer buffers} and the second contains output
     * {@link Buffer buffers}.
	 */
	val scriptResults = new HashMap<DAGVertex, Pair<List<Buffer>, List<Buffer>>>

	/**
	 * This method finds the memory scripts associated to the {@link DAGVertex
	 * vertices} of the input {@link DirectedAcyclicGraph}. When a script path
	 * is set in the property of the {@link SDFVertex} associated to a
	 * {@link DAGVertex} of the graph, scripts are either found in a path
	 * relative to the original {@link SDFGraph} file, or in the plugin project
	 * "scripts" directory. If an invalid script path is set, a warning message
	 * will be written in the log.
	 * 
	 * @param dag
	 *            the {@link DirectedAcyclicGraph} whose vertices memory scripts
	 *            are retrieved.
	 */
	def protected findScripts(DirectedAcyclicGraph dag) {
		// Retrieve the original sdf folder
		val workspace = ResourcesPlugin.getWorkspace
		val sdfPath = dag.propertyBean.getValue(DirectedAcyclicGraph.PATH, String) as String
		var sdfFile = workspace.root.getFileForLocation(new Path(sdfPath))

		// Logger is used to display messages in the console
		val logger = WorkflowLogger.getLogger

		// Identify all actors with a memory Script
		for (dagVertex : dag.vertexSet) {
			val sdfVertex = dagVertex.propertyBean.getValue(DAGVertex.SDF_VERTEX, SDFAbstractVertex) as SDFAbstractVertex
			if (dagVertex.kind != null) {
				switch (dagVertex.kind) {
					case DAGVertex.DAG_VERTEX: {
						val pathString = sdfVertex.propertyBean.getValue(SDFVertex.MEMORY_SCRIPT, String) as String
						if (pathString != null) {
							// Retrieve the script path as a relative path to the
							// graphml
							var scriptFile = sdfFile.parent.getFile(new Path(pathString)).rawLocation.makeAbsolute.
								toFile
							if (scriptFile.exists) {
								scriptedVertices.put(dagVertex, scriptFile)
							} else {
								// If this code is reached
								// Check if the script file exists in the source code
								// /scripts directory.
								val classpathString = "/../scripts/" + pathString
								var URI sourceStream
								try {
									sourceStream = class.getResource(classpathString).toURI
									scriptFile = new File(FileLocator.resolve(sourceStream.toURL).file)
									if (scriptFile.exists) {
										scriptedVertices.put(dagVertex, scriptFile)
									}
								} catch (Exception e) {
									// Nothing to do
								}
								if (scriptFile == null || !scriptFile.exists)
									logger.log(Level.WARNING,
										"Memory script of vertex " + sdfVertex.getName() + " is invalid: \"" +
											pathString + "\". Change it in the graphml editor.")
							}
						}
					}
					case DAGForkVertex.DAG_FORK_VERTEX: {
						val classpathString = "/../scripts/fork.bsh";
						try {
							val sourceStream = class.getResource(classpathString).toURI;
							val scriptFile = new File(FileLocator.resolve(sourceStream.toURL).file)
							if (scriptFile.exists) {
								scriptedVertices.put(dagVertex, scriptFile)
							}
						} catch (Exception e) {
							logger.log(Level.SEVERE,
								"Memory script of fork vertices not found. Please contact Preesm developers.")
						}
					}
					case DAGJoinVertex.DAG_JOIN_VERTEX: {
						val classpathString = "/../scripts/join.bsh"
						try {
							val sourceStream = class.getResource(classpathString).toURI
							val scriptFile = new File(FileLocator.resolve(sourceStream.toURL).file)
							if (scriptFile.exists) {
								scriptedVertices.put(dagVertex, scriptFile)
							}
						} catch (Exception e) {
							logger.log(Level.SEVERE,
								"Memory script of join vertices not found. Please contact Preesm developers.")
						}

					}
					case DAGBroadcastVertex.DAG_BROADCAST_VERTEX: {
						var classpathString = if (sdfVertex instanceof SDFRoundBufferVertex) {
								"/../scripts/roundbuffer.bsh"
							} else {
								"/../scripts/broadcast.bsh"
							}
						try {
							val sourceStream = class.getResource(classpathString).toURI

							val scriptFile = new File(FileLocator.resolve(sourceStream.toURL).file)
							if (scriptFile.exists) {
								scriptedVertices.put(dagVertex, scriptFile)
							}
						} catch (Exception e) {
							logger.log(Level.SEVERE,
								"Memory script of broadcast/roundbuffer vertices not found. Please contact Preesm developers.")
						}
					}
				}
			}
		}
	}
	
		/**
	 * This method perform several checks on the {@link Buffer buffers}
	 * resulting from the evaluation of a script. The checked points are:
	 * <ul>
	 * <li>If all {@link Match matches} are reciprocal. A {@link Match}
	 * belonging to the {@link Buffer#getMatchTable() matchTable} of a
	 * {@link Buffer} is reciprocal if the {@link Match#getBuffer() matched
	 * buffer} has a reciprocal {@link Match} in its
	 * {@link Buffer#getMatchTable() match table}.</li>
	 * <li>If ranges matched multiple times are not matched with other ranges
	 * that are {@link #getMultipleMatchRange(Buffer) matched multiple times}.
	 * For example, with a,b and c three {@link Buffer buffers}, if a[i] is
	 * matched with b[j], b[k], and c[l] then b[j] (or b[k] or c[l]) cannot be
	 * matched with a {@link Buffer} different than a[i].</li>
	 * </ul>
	 * If one of the checks is not valid, the method will return false and a
	 * warning will be displayed in the {@link Logger log}.
	 * 
	 * @param script
	 *            the script {@link File} from which the result {@link Buffer
	 *            buffers} result.
	 * @param result
	 *            a {@link Pair} of {@link List} of {@link Buffer buffers}. The
	 *            key {@link List} contains input {@link Buffer buffers} and the
	 *            value {@link List} contain output {@link Buffer buffers}.
	 * @return <code>true</code> if all checks were valid, <code>false</code>
	 *         otherwise.
	 */
	def check(File script, Pair<List<Buffer>, List<Buffer>> result) {

		val allBuffers = new ArrayList<Buffer>
		allBuffers.addAll(result.key)
		allBuffers.addAll(result.value)

		// Check that all matches are reciprocal
		// For all buffers
		val res1 = allBuffers.forall[localBuffer | //localBuffer.matchTable.get(0).add()
				// for all matcheSet  
				localBuffer.matchTable.entrySet.forall[
					val matches = it.value
					val localIdx = it.key
					// for all matches
					matches.forall[match |
						val remoteMatches = match.buffer.matchTable.get(match.index)
						remoteMatches != null && remoteMatches.contains(new Match(localBuffer, localIdx, match.length))
				]
			]
		]	
		if (!res1) {
			val logger = WorkflowLogger.logger
			logger.log(Level.WARNING,
				"Error in " + script +
					": One of the match is not reciprocal. Please set matches only by using Buffer.setMatch() methods.")
		}	
		
					
		// Check that inputs are only merged with outputs (and vice versa)
		// Not checked => Authorized (ex. for round buffers)  
		
		
		// Find ranges from input and output with multiple matches
		// Check: If a[i] is matched with b[j], b[k], and c[l] then b[j] (or 
		// b[k] or c[l]) cannot be matched with a buffer different than a[i].
		// forall inputs -> forall elements -> forall multiple matches
		// check that other side of the match has a unique match (implicitly: 
		// with the current multiple match).
		val multipleRanges = allBuffers.map[it -> it.multipleMatchRange]
		val res2 = multipleRanges.forall [ multipleRange |
			if (multipleRange.value.size != 0) {
				// If the current buffer has multiple ranges
				multipleRange.value.entrySet.forall [
					val srcOrig = it.key
					val srcEnd = it.getValue()
					// for all match within this range
					(srcOrig .. srcEnd).forall [
						val matches = multipleRange.key.matchTable.get(it)
						if (matches != null) {
							// Test the destination is not within a multiple range
							matches.forall [
								val range = multipleRanges.get(allBuffers.indexOf(it.buffer)).value
								range.filter[destOrig, destEnd|
									srcOrig < destEnd && destOrig < srcEnd
								].size == 0
							]
						} else
							true
					]
				]
			} else
				true // No multiple range for this buffer
		]
		
		if (!res2) {
			val logger = WorkflowLogger.logger
			logger.log(Level.WARNING,
				"Error in " + script +
					": A buffer element matched multiple times cannot be matched with an element that is itself matched multiple times.")
		}		

		res1 && res2
		
	}
	
	/**
	 * Identify which data ranges of a {@link Buffer} are matched multiple
	 * times. A range is matched multiple times if several matches involving
	 * this ranges are stored in the {@link Buffer#getMatchTable() match table}
	 * of the {@link Buffer}. For example, if these to calls are executed: </br>
	 * <code>a.matchWith(b,0,32)</br>a.matchWith(c,16,4)</code> <br>
	 * Then <code>a[16..19]</code> is matched multiple times.
	 * 
	 * @param buffer
	 *            the {@link Buffer} whose multiple matched ranges are
	 *            extracted.
	 * 
	 * @return a {@link Map} containing the start end end of ranges matched
	 *         multiple times.
	 */
	static def getMultipleMatchRange(Buffer buffer) {
		var multMatchRange = newHashMap
		var nbMatch = 0
		var multStart = -1
		val endMult = newArrayList
		var inZone = false
		for(idx : 0 .. buffer.nbTokens*buffer.tokenSize){						
			// Process ending matches
			var nbEnding = endMult.size
			endMult.removeAll(idx)
			nbMatch = nbMatch - (nbEnding-endMult.size)
			
			// Add new matches (if any)
			var matches = buffer.matchTable.get(idx)
			if(matches != null){
				nbMatch = nbMatch + matches.size
				// If a new "multiple match" zone begins
				if(nbMatch>1 && !inZone)
				{
					inZone = true
					multStart = idx				
				}				
				matches.forEach[endMult.add(idx + it.length)]
			}			
			
			// If this was the end of a mult zone
			if(inZone && nbMatch<=1){
				multMatchRange.put(multStart, idx-1)
				inZone = false
				multStart = -1
			}			
		}
		
		multMatchRange
	}

	/**
	 * This method run the scripts that were found during the call to
	 * {@link #findScripts()}. As a result, the {@link #scriptResults} is
	 * filled.<br>
	 * <br>
	 * 
	 * If the execution of a script fails, the {@link Interpreter} error message
	 * will be printed in the {@link Logger log} as a warning.<br>
	 * <br>
	 * The {@link #check(List,List)} method is also used after each script
	 * execution to verify the validity of the script results. If the results
	 * are not valid, they will not be stored in the {@link #scriptResults}
	 * {@link Map}, and a warning will be printed in the {@link Logger log}.
	 */
	def run() {

		// For each vertex with a script
		for (e : scriptedVertices.entrySet) {			
			val dagVertex = e.key
			val script = e.value
			val interpreter = new Interpreter();

			// Retrieve the corresponding sdf vertex
			val sdfVertex = dagVertex.getPropertyBean().getValue(DAGVertex.SDF_VERTEX, SDFAbstractVertex) as SDFAbstractVertex

			// Create the vertex parameter list
			val Map<String, Integer> parameters = newHashMap
			{
				val arguments = sdfVertex.propertyBean.getValue(SDFAbstractVertex.ARGUMENTS) as HashMap<String, Argument>
				if(arguments != null) arguments.entrySet.forEach[parameters.put(it.key, it.value.intValue)]
			}

			// Create the input/output lists
			val inputs = sdfVertex.incomingEdges.map[
				new Buffer(it.targetLabel, it.cons.intValue, dataTypes.get(it.dataType.toString).size)].toList
			val outputs = sdfVertex.outgoingEdges.map[
				new Buffer(it.sourceLabel, it.prod.intValue, dataTypes.get(it.dataType.toString).size)].toList

			// Import the necessary libraries
			interpreter.eval("import " + Buffer.name + ";")
			interpreter.eval("import " + List.name + ";")

			// Feed the parameters/inputs/outputs to the interpreter
			parameters.forEach[name, value|interpreter.set(name, value)]
			inputs.forEach[interpreter.set("i_" + it.name, it)]
			outputs.forEach[interpreter.set("o_" + it.name, it)]
			if (interpreter.get("parameters") == null)
				interpreter.set("parameters", parameters)
			if (interpreter.get("inputs") == null)
				interpreter.set("inputs", inputs)
			if (interpreter.get("outputs") == null)
				interpreter.set("outputs", outputs)

			try {				
				// Run the script
				interpreter.source(script.absolutePath);
				
				// Check the result
				if(checkPolicy != CheckPolicy::NONE)
				{
					check(script ,inputs->outputs)
				}

				// Store the result if the execution was successful 
				scriptResults.put(dagVertex, inputs -> outputs)
			
			} catch (ParseException error) {

				// Logger is used to display messages in the console
				val logger = WorkflowLogger.getLogger
				var message = error.rawMessage + "\n" + error.cause
			
				logger.log(Level.WARNING,
					"Parse error in " + sdfVertex.name + " memory script:\n" +
						message)

			} catch (EvalError error) {

				// Logger is used to display messages in the console
				val logger = WorkflowLogger.getLogger
				var message = error.rawMessage + "\n" + error.cause
			
				logger.log(Level.WARNING,
					"Evaluation error in " + sdfVertex.name + " memory script:\n[Line " + error.errorLineNumber + "] " +
						message)

			} catch (IOException exception) {
				exception.printStackTrace
			}  catch (Exception error) {

				// Logger is used to display messages in the console
				val logger = WorkflowLogger.getLogger
				logger.log(Level.WARNING,
					"Evaluation error in " + sdfVertex.name + " memory script:\n[Line ?] " +
						error.message)

			}
		}
	}

	def static Set<SDFEdge> incomingEdges(SDFAbstractVertex vertex) {
		vertex.base.incomingEdgesOf(vertex)
	}

	def static Set<SDFEdge> outgoingEdges(SDFAbstractVertex vertex) {
		vertex.base.outgoingEdgesOf(vertex)
	}

	def runTest() {

		val interpreter = new Interpreter();

		// Import the necessary libraries
		interpreter.eval("import " + Buffer.name + ";")

		//interpreter.eval("import " + Map.name + ";")
		// Retrieve Parameters
		var parameters = newHashMap("NbSlice" -> 3, "Overlap" -> 1, "Height" -> 3, "Width" -> 2)
		parameters.forEach[name, value|interpreter.set(name, value)]

		// Retrieve buffers
		var inputs = newArrayList(new Buffer("input", parameters.get("Height") * parameters.get("Width"), 1))
		inputs.forEach[interpreter.set("i_" + it.name, it)]

		var outputs = newArrayList(
			new Buffer("output",
				parameters.get("Height") * parameters.get("Width") +
					parameters.get("NbSlice") * parameters.get("Overlap") * 2 * parameters.get("Width"), 1))
		outputs.forEach[interpreter.set("o_" + it.name, it)]

		try {
			val sourceStream = class.getResource("/../scripts/split.bsh").toURI
			val scriptFile = new File(FileLocator.resolve(sourceStream.toURL).file)
			if (scriptFile.exists) {
				interpreter.source(scriptFile.absolutePath);
			}
			
		} catch (IOException e) {
			e.printStackTrace
			//null // return null for the try catch block
		}
	}
}

