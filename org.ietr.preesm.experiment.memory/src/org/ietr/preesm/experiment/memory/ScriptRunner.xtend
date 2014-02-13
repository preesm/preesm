package org.ietr.preesm.experiment.memory

import bsh.EvalError
import bsh.Interpreter
import bsh.ParseException
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
import net.sf.dftools.algorithm.model.dag.DAGEdge
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

import static extension org.ietr.preesm.experiment.memory.Buffer.*
import static extension org.ietr.preesm.experiment.memory.Range.*

enum CheckPolicy {
	NONE,
	FAST,
	THOROUGH
}

class ScriptRunner {

	/**
	 * Helper method to get the incoming {@link SDFEdge}s of an {@link 
	 * SDFAbstractVertex}.
	 */
	def static Set<SDFEdge> incomingEdges(SDFAbstractVertex vertex) {
		vertex.base.incomingEdgesOf(vertex)
	}

	/**
	 * Helper method to get the outgoing {@link SDFEdge}s of an {@link 
	 * SDFAbstractVertex}.
	 */
	def static Set<SDFEdge> outgoingEdges(SDFAbstractVertex vertex) {
		vertex.base.outgoingEdgesOf(vertex)
	}

	@Property
	var checkPolicy = CheckPolicy::NONE

	/**
	 * A {@link Map} that associates each {@link String} representing a type name 
	 * with a corresponding {@link DataType}. 
	 */
	@Property
	var Map<String, DataType> dataTypes

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
	 * A {@link Map} that associates each {@link DAGVertex} from the
	 * {@link ScriptRunner#scriptedVertices scriptedVertices} map to the 
	 * potential merge conflicts between its inputs buffers and between its 
	 * output buffers. These conflicts are stored as follow in the Map:<br>
	 * <ul><li>Map</li>
	 * <ul><li>Map.Key: {@link DAGVertex} whose conflicts are stored as value</li>
	 * <li>Map.Value: Pair to separate inter-inputs and inter-outputs conflicts 
	 * </li><ul><li>Pair.Key: Map - For inter-inputs conflicts</li>
	 * <ul><li>Map.Key: Pair identifies a conflicting range of an input buffer</li>
	 * <ul><li>Pair.Key: Buffer</li><li>Pair.Value: Range</li>
	 * </ul><li>Map.Value: List of Match that overlap the conflicting range</li></ul>
	 * <li>Pair.Value: Map - For inter-outputs conflicts</li>
	 * <ul><li>Map.Key: Pair identifies a conflicting range of an output buffer</li>
	 * <ul><li>Pair.Key: Buffer</li><li>Pair.Value: Range</li>
	 * </ul><li>Map.Value: List of Match that overlap the conflicting range</li></ul>
	 * </ul></ul><ul>
	 * This {@link Map} is filled in the {@link ScriptRunner#process() 
	 * process()} method with the result of a call to {@link
	 * ScriptRunner#identifyMergeRanges(List,List) identifyMergeRanges()} for 
	 * each {@link DAGVertex}. 
	 */
	val mergeConflicts = new HashMap<DAGVertex, Pair<Map<Pair<Buffer, Range>, List<Match>>, Map<Pair<Buffer, Range>, List<Match>>>>

	/** 
	 * A {@link Map} that associates each {@link DAGVertex} with a
	 * memory script to this memory script {@link File}.
	 */
	val scriptedVertices = new HashMap<DAGVertex, File>();

	/**
	 * Check the results obtained when running the {@link #run()} method.
	 * Checks are performed according to the current {@link #setCheckPolicy(CheckPolicy)}.
	 * The {@link #checkResult(File,Pair)} method is used to perform the checks.
	 * Vertices whose script results do not pass the checks are removed
	 * from the {@link #scriptResults} map.
	 */
	def check() {
		if (checkPolicy != CheckPolicy::NONE) {

			// Do the checks
			val invalidVertices = newArrayList
			scriptResults.forEach[vertex, result|
				if(!checkResult(scriptedVertices.get(vertex), result)) invalidVertices.add(vertex)]

			// Remove invalid results
			invalidVertices.forEach[scriptResults.remove(it)]
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
	def checkResult(File script, Pair<List<Buffer>, List<Buffer>> result) {
		val allBuffers = new ArrayList<Buffer>
		allBuffers.addAll(result.key)
		allBuffers.addAll(result.value)

		// Check that all matches are reciprocal
		// For all buffers
		val res1 = allBuffers.forall [ localBuffer |
			// for all matcheSet  
			localBuffer.reciprocal
		]
		if (!res1) {
			val logger = WorkflowLogger.logger
			logger.log(Level.WARNING,
				"Error in " + script + ":\nOne or more match is not reciprocal." +
					" Please set matches only by using Buffer.matchWith() methods.")
		}

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
				multipleRange.value.forall [ r |
					// Fill a list that contains Pair
					// Each pair is composed of a remote buffer and a remote range
					// This range is matched with a multiple matched range from
					// the current local buffer. Consequently, this remote 
					// range must not be part of a remote multiple matched range.
					val matchFromMultRange = newArrayList;
					multipleRange.key.matchTable.forEach [ localIdx, matches |
						matches.forEach [
							if (r.hasOverlap(new Range(localIdx, localIdx + it.length))) {
								val localInter = r.intersection(new Range(localIdx, localIdx + it.length))
								val remoteRangeStart = it.remoteIndex + localInter.start - localIdx
								val remoteRange = new Range(remoteRangeStart, remoteRangeStart + localInter.length)
								matchFromMultRange.add(it.remoteBuffer -> remoteRange)
							}
						]
					]
					matchFromMultRange.forall [
						val buffer = it.key
						val range = it.value
						val intersect = multipleRanges.get(allBuffers.indexOf(buffer)).value.intersection(range)
						intersect.size == 0
					]
				]
			} else
				true // No multiple range for this buffer
		]

		if (!res2) {
			val logger = WorkflowLogger.logger
			logger.log(Level.WARNING,
				"Error in " + script + ":\nA buffer element matched multiple times cannot" +
					" be matched with an element that is itself matched multiple times.")
		}
		res1 && res2
	}

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

		scriptedVertices.size
	}

	/**
	 * This method process the {@link #scriptResults} in order to simplify 
	 * them with {@link #simplifyResult(List,List)}. Then, it extracts 
	 * mergeable buffers. 
	 * This method must be called after {@link #run()} and {@link #check()} 
	 * have been successfully called.
	 */
	def process() {

		// Simplify results
		scriptResults.forEach[vertex, result|simplifyResult(result.key, result.value)]

		// Identify ranges that may cause a transversal merge
		scriptResults.forEach[vertex, result|mergeConflicts.put(vertex, identifyMergeRanges(result.key, result.value))]

		// Identify groups of chained buffers from the scripts and dag
		val groups = groupVertices()

		// Process the groups one by one
		groups.forEach[it.processGroup]
		var result = groups.fold(0, [res, gr|res + gr.size])
		println("Identified " + groups.size + " groups. " + result)
	}

	def Pair<Map<Pair<Buffer, Range>, List<Match>>, Map<Pair<Buffer, Range>, List<Match>>> identifyMergeRanges(
		List<Buffer> inputs, List<Buffer> outputs) {

		// Result directly stored in buffers:
		// Map<Pair<Buffer,Range>, List<Match>> 
		val interInputMerge = new HashMap<Pair<Buffer, Range>, List<Match>>()
		val interOutputMerge = new HashMap<Pair<Buffer, Range>, List<Match>>()

		// For each Buffer
		for (buffer : #{inputs, outputs}.flatten) {
			val isIn = inputs.contains(buffer)
			val siblings = if (isIn) {
					inputs
				} else {
					outputs
				}

			// 1- Find multi Ranges
			val multiRanges = buffer.multipleMatchRange
			multiRanges.forEach [ range |
				// find overlapping match
				val overlappingMatches = newArrayList
				buffer.matchTable.forEach [ localIdx, matchList |
					matchList.forEach [
						if (range.hasOverlap(new Range(localIdx, localIdx + it.length))  
							// match in siblings are not processed here 
						&& !siblings.contains(it.remoteBuffer)) {
							overlappingMatches.add(it)
						}
					]
				]
				val key = new Pair(buffer, range)
				(if(isIn) interOutputMerge else interInputMerge).put(key, overlappingMatches)
			]

			// 2- Inter-input / inter			
			buffer.matchTable.forEach [ localIdx, matchList |
				matchList.filter[siblings.contains(it.remoteBuffer)].forEach [
					// If this code is reached, the match is between
					// two inputs or two outputs
					val map = if(isIn) interInputMerge else interOutputMerge
					val keyClone = new Pair(buffer, new Range(localIdx, localIdx + it.length))
					val key = map.keySet.findFirst[it == keyClone] ?: keyClone
					var conflictingMatches = map.get(key)
					if (conflictingMatches === null) {
						conflictingMatches = newArrayList
						map.put(key, conflictingMatches)
					}
					conflictingMatches.add(it)
				]
			]
		}

		// Return the pair of result lists
		new Pair(interInputMerge, interOutputMerge)
	}

	/**
	 * Process the groups generated by the groupVertices method.
	 */
	def processGroup(ArrayList<DAGVertex> vertices) {

		// Get all the buffers
		val buffers = newArrayList
		vertices.forEach [
			var pair = scriptResults.get(it)
			buffers.addAll(pair.key)
			buffers.addAll(pair.value)
		]

		// Iterate the merging algorithm until no buffers are merged
		var updated = false
		var step = 0
		do {
			switch (step) {
				// First step: Merge mergeable buffer with a unique match 
				case 0: {
					// Find all mergeable buffers with a unique match (if any)
					val candidates = buffers.filter[
						val entry = it.matchTable.entrySet.head
						// Returns true if:
						// There is a unique match
						it.matchTable.size == 1 && entry.value.size == 1
						// that begins at index 0 (or less)
						entry.key <= 0 &&
						// and ends at the end of the buffer (or more)
						entry.key + entry.value.head.length >= it.nbTokens * it.tokenSize &&
						// and is not involved in any conflicting range
						{
							// Only the destination can have conflicts here since
							// the match source has a unique match in its matchTable
							val match = entry.value.head
							val destConflicts = mergeConflicts.get(match.remoteBuffer.getDagVertex)
							val dagVertexResults = scriptResults.get(match.remoteBuffer.getDagVertex)
							val isDestIn = dagVertexResults.key.contains(it)
							val map = if(isDestIn) destConflicts.key else destConflicts.value
							map.values.flatten.forall[
								it != match.reciprocate
							]
						} 						
					]
					
					if(!candidates.empty){
						// If there are candidates, merge them all and do step 0 again
						step = 1
						// Do the merge
						candidates.forEach[
							applyMatches(it, #[it.matchTable.entrySet.head.value.head])
						]
						
						
					} else {
						// If there was no candidates, go to step 1
						step = 1
					}
				}				
			} 
			 
			 updated = step != 1
		} while (updated)
	}
	
	def applyMatches(Buffer buffer, List<Match> matchs) {
		
	}

	/**
	 * The purpose of this method is to create groups of {@link DAGVertex} which
	 * satisfy the following conditions:
	 * <ul>
	 * <li> {@link DAGVertex Vertices} are associated to a memory script</li>
	 * <li> {@link DAGVertex Vertices} of the same group are strongly connected
	 * via {@link DAGEdge FIFOs}</li>
	 * </ul>
	 * The {@link #scriptResults} attribute of the calling {@link ScriptRunner}
	 * are updated by this method. In particular, a
	 * {@link Buffe#matchWith(int,Buffer,int,int) match} is added between
	 * buffers of different actors that correspond to the same SDFEdges.
	 * 
	 * @return a {@link List} of groups. Each group is itself a {@link List} of
	 *         {@link DAGVertex}.
	 */
	def groupVertices() {

		// Each dag vertex can be involved in at most one group
		val groups = newArrayList
		val dagVertices = new ArrayList(scriptResults.keySet)
		while (dagVertices.size != 0) {

			// Get the first dagVertex
			val dagSeedVertex = dagVertices.remove(0)

			// Create a new group
			val group = newArrayList(dagSeedVertex)

			// Identify other vertices that can be put into the group
			var List<DAGVertex> newVertices = newArrayList(dagSeedVertex)
			while (newVertices.size != 0) {

				// Initialize the group size
				val groupSize = group.size

				// For all vertices from the newVertices list
				// check if a successors/predecessor can be added to the group
				for (dagVertex : newVertices) {
					val candidates = newArrayList
					dagVertex.incomingEdges.forEach[candidates.add(it.source)]
					dagVertex.outgoingEdges.forEach[candidates.add(it.target)]

					var addedVertices = group.subList(groupSize, group.size)
					for (candidate : candidates) {
						if (addedVertices.contains(candidate) || newVertices.contains(candidate) ||
							dagVertices.contains(candidate)) {

							// Match the buffers corresponding to the edge 
							// between vertices "dagVertex" and "candidate"
							// Get the sdfEdges
							var dagEdge = ( dagVertex.base.getEdge(dagVertex, candidate) ?:
								dagVertex.base.getEdge(candidate, dagVertex)) as DAGEdge

							// For edges between newVertices, only process if the dagVertex
							// is the source (to avoid matching the pair of buffer twice)
							var validBuffers = false
							val isBetweenNewVertices = newVertices.contains(candidate)
							if (!isBetweenNewVertices || dagEdge.source == dagVertex) {

								// Add match between the two buffers that
								// correspond to the sdf edge(s) between vertex
								// and it 
								val bufferCandidates = newArrayList
								for (v : #{dagVertex, candidate}) {
									var pair = scriptResults.get(v)
									bufferCandidates.addAll(pair.key)
									bufferCandidates.addAll(pair.value)
								}
								for (sdfEdge : dagEdge.aggregate) {

									// Find the 2 buffers corresponding to this sdfEdge
									var buffers = bufferCandidates.filter[it.sdfEdge == sdfEdge]
									if (buffers.size == 2) {

										// Match them together
										buffers.get(0).matchWith(0, buffers.get(1), 0, buffers.get(0).nbTokens)
										validBuffers = true
									}
								}
							}

							// Add the vertex to the group (if not already in 
							// it) and if there was valid buffers)
							if (!group.contains(candidate) && validBuffers) {
								group.add(candidate)
								dagVertices.remove(candidate)
								addedVertices = group.subList(groupSize, group.size)
							}
						}
					}
				}

				// Update the newVertices list (we do not use sublists here because it causes
				// a ConcurrentModificationException
				newVertices = new ArrayList<DAGVertex>(group.subList(groupSize, group.size))
			}

			// The group is completed, save it
			groups.add(group)
		}
		groups
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
				new Buffer(it, dagVertex, it.targetLabel, it.cons.intValue, dataTypes.get(it.dataType.toString).size)].
				toList
			val outputs = sdfVertex.outgoingEdges.map[
				new Buffer(it, dagVertex, it.sourceLabel, it.prod.intValue, dataTypes.get(it.dataType.toString).size)].
				toList

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

				// Store the result if the execution was successful 
				scriptResults.put(dagVertex, inputs -> outputs)
			} catch (ParseException error) {

				// Logger is used to display messages in the console
				val logger = WorkflowLogger.getLogger
				var message = error.rawMessage + "\n" + error.cause
				logger.log(Level.WARNING, "Parse error in " + sdfVertex.name + " memory script:\n" + message)
			} catch (EvalError error) {

				// Logger is used to display messages in the console
				val logger = WorkflowLogger.getLogger
				var message = error.rawMessage + "\n" + error.cause
				logger.log(Level.WARNING,
					"Evaluation error in " + sdfVertex.name + " memory script:\n[Line " + error.errorLineNumber + "] " +
						message)
			} catch (IOException exception) {
				exception.printStackTrace
			}
		}
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
		var inputs = newArrayList(new Buffer(null, null,"input", parameters.get("Height") * parameters.get("Width"), 1))
		inputs.forEach[interpreter.set("i_" + it.name, it)]

		var outputs = newArrayList(
			new Buffer(null, null ,"output",
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

		}
	}

	/**
	 * This method calls {@link Buffer#simplifyMatches()} for each 
	 * {@link Buffer} of the {@link #scriptResults}. 
	 * If a {@link Buffer} has an empty {@link Buffer#getMatchTable() 
	 * matchTable} after the simplification process, it is removed from
	 * the  {@link #scriptResults}.
	 */
	def simplifyResult(List<Buffer> inputs, List<Buffer> outputs) {
		val allBuffers = new ArrayList<Buffer>
		allBuffers.addAll(inputs)
		allBuffers.addAll(outputs)

		// Matches whose reciprocate has been processed
		// no need to test them again
		val processedMatch = newArrayList

		// Iterate over all buffers
		allBuffers.forEach[it.simplifyMatches(processedMatch)]

		// If a buffer has an empty matchTable, remove it from its list
		val unmatchedBuffer = allBuffers.filter[it.matchTable.empty]
		inputs.removeAll(unmatchedBuffer)
		outputs.removeAll(unmatchedBuffer)
	}
}
