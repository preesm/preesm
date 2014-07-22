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
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.runtime.FileLocator
import org.eclipse.core.runtime.Path
import org.eclipse.xtext.xbase.lib.Pair
import org.ietr.dftools.algorithm.model.dag.DAGEdge
import org.ietr.dftools.algorithm.model.dag.DAGVertex
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph
import org.ietr.dftools.algorithm.model.dag.edag.DAGBroadcastVertex
import org.ietr.dftools.algorithm.model.dag.edag.DAGForkVertex
import org.ietr.dftools.algorithm.model.dag.edag.DAGJoinVertex
import org.ietr.dftools.algorithm.model.parameters.Argument
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.SDFVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex
import org.ietr.dftools.workflow.WorkflowException
import org.ietr.dftools.workflow.tools.WorkflowLogger
import org.ietr.preesm.core.types.DataType
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex

import static extension org.ietr.preesm.experiment.memory.Buffer.*
import static extension org.ietr.preesm.experiment.memory.Range.*
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph

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
	 * A {@link Map} that associates each {@link DAGVertex} with a
	 * memory script to this memory script {@link File}.
	 */
	val scriptedVertices = new HashMap<DAGVertex, File>();

	val List<List<Buffer>> bufferGroups = newArrayList

	static public final boolean printTodo = false

	static public final boolean verbose = true

	static int nbBuffersBefore = 0
	static int nbBuffersAfter = 0
	static int sizeBefore
	static int sizeAfter

	/**
	 * This property is used to represent the alignment of buffers in memory.
	 * The same value, or a multiple should always be used in the memory 
	 * allocation. 
	 */
	@Property
	val int alignment

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
	 * <li>If there are inter-siblings matches. (i.e. inter-inputs or inter-
	 * outputs matches.)</li>
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
		if (!res1 && checkPolicy == CheckPolicy::FAST) {
			val logger = WorkflowLogger.logger
			logger.log(Level.WARNING,
				"Error in " + script + ":\nOne or more match is not reciprocal." +
					" Please set matches only by using Buffer.matchWith() methods.")
		} else if (!res1 && checkPolicy == CheckPolicy::THOROUGH) {
			allBuffers.forEach [ localBuffer |
				// for all matcheSet  
				val res = localBuffer.reciprocal
				if (!res && checkPolicy == CheckPolicy::THOROUGH) {
					val logger = WorkflowLogger.logger
					logger.log(Level.WARNING,
						"Error in " + script + ":\nBuffer " + localBuffer + " has nonreciprocal matches:\n" + localBuffer.
							matchTable.values.flatten.filter[match|
								val remoteMatches = match.remoteBuffer.matchTable.get(match.remoteIndex)
								!(remoteMatches != null && remoteMatches.contains(
									new Match(match.remoteBuffer, match.remoteIndex, localBuffer, match.localIndex,
										match.length)))] + "\nPlease set matches only by using Buffer.matchWith() methods.")
				}
			]
		}

		// Find inter-inputs and inter-outputs matches
		var res2 = result.key.forall [ buffer |
			buffer.matchTable.values.flatten.forall [ match |
				result.value.contains(match.remoteBuffer)
			]
		] && result.value.forall [ buffer |
			buffer.matchTable.values.flatten.forall [ match |
				result.key.contains(match.remoteBuffer)
			]
		]
		if (!res2 && checkPolicy == CheckPolicy::FAST) {
			val logger = WorkflowLogger.logger
			logger.log(Level.WARNING,
				"Error in " + script + ":\nOne or more match links an input (or an output) to another." +
					"\nPlease set matches only between inputs and outputs.")
		} else if (!res2 && checkPolicy == CheckPolicy::THOROUGH) {
			result.key.forEach [ buffer |
				buffer.matchTable.values.flatten.forEach [ match |
					if (!result.value.contains(match.remoteBuffer)) {
						val logger = WorkflowLogger.logger
						logger.log(Level.WARNING,
							"Error in " + script + ":\nMatch " + match + " links an input to another." +
								"\nPlease set matches only between inputs and outputs.")
					}
				]
			]
			result.value.forEach [ buffer |
				buffer.matchTable.values.flatten.forEach [ match |
					if (!result.key.contains(match.remoteBuffer)) {
						val logger = WorkflowLogger.logger
						logger.log(Level.WARNING,
							"Error in " + script + ":\nMatch " + match + " links an output to another." +
								"\nPlease set matches only between inputs and outputs.")
					}
				]
			]
		}

		// Find ranges from input and output with multiple matches
		val multipleRanges = allBuffers.map[it -> it.multipleMatchRange]

		// There can be no multiple match range in the output buffers !
		val res3 = multipleRanges.forall [
			result.key.contains(it.key) || it.value.size == 0
		]
		if (!res3 && checkPolicy == CheckPolicy::FAST) {
			val logger = WorkflowLogger.logger
			logger.log(Level.WARNING,
				"Error in " + script + ":\nMatching multiple times a range of an output buffer is not allowed.")
		} else if (!res3 && checkPolicy == CheckPolicy::THOROUGH) {
			multipleRanges.forEach [
				if (!(result.key.contains(it.key) || it.value.size == 0)) {
					val logger = WorkflowLogger.logger
					logger.log(Level.WARNING,
						"Error in " + script + ":\nMatching multiple times output buffer " + it.key + " is not allowed." +
							"\nRange matched multiple times:" + it.value)
				}
			]
		}

		res1 && res2 && res3
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

		// Identify divisible buffers
		scriptResults.forEach[vertex, result|identifyDivisibleBuffers(result)]

		// Update output buffers for alignment
		if (_alignment > 0) {
			scriptResults.forEach [ vertex, result |
				// All outputs except the mergeable one linked only to pure_in 
				// inputs within their actor must be enlarged.
				// In other terms, only buffers that will never be written by their
				// producer actor or consumer actor are not enlarged since these 
				// buffer will only be used to divide data written by other actors. 
				result.value.filter[
					!(it.originallyMergeable && it.matchTable.values.flatten.forall [
						it.remoteBuffer.originallyMergeable
					])].forEach [ buffer |
					// Enlarge the buffer 
					// New range mergeability is automatically handled by 
					// setMinIndex(int)
					enlargeForAlignment(buffer)
				]
			]
		}

		// Identify matches types
		scriptResults.forEach[vertex, result|identifyMatchesType(result)]

		// Identify match that may cause a inter-output merge (not inter-input since
		// at this point, multiple matches of output range is forbidden)
		scriptResults.forEach[vertex, result|identifyConflictingMatchCandidates(result.key, result.value)]

		// Identify groups of chained buffers from the scripts and dag
		val groups = groupVertices()

		// Update input buffers on the group border for alignment
		if (_alignment > 0) {

			// For each group
			groups.forEach [ group |
				// For each vertex of the group
				group.forEach [ dagVertex |
					// For each incoming edge of this vertex
					dagVertex.incomingEdges.forEach [ edge |
						// If the edge producer is not part of the group
						if (!group.contains(edge.source)) {

							// Retrieve the corresponding buffer.
							scriptResults.get(dagVertex).key.filter [ buffer |
								buffer.sdfEdge.source.name == edge.source.name
							].forEach [
								it.enlargeForAlignment
							]
						}
					]
				]
			]
		}

		// Process the groups one by one
		sizeBefore = sizeAfter = nbBuffersBefore = nbBuffersAfter = 0
		groups.forEach [
			it.processGroup
		]

		if (verbose)
			println(
				'''Identified «groups.size» groups. From «nbBuffersBefore» to «nbBuffersAfter» buffers.''' + "\n" + ''' Saving «sizeBefore»-«sizeAfter»=«sizeBefore -
					sizeAfter» («(sizeBefore - sizeAfter as float) / sizeBefore»%).''')
	}

	def enlargeForAlignment(Buffer buffer) {
		if (printTodo) {
			println('''Alignment minus one is probably sufficient + Only enlarge [0-Alignment,Max+alignment];''')
			// Todo description :
			// This method is called only for output buffers.
			// Since only "real" tokens of the output buffers are written back
			// from cache (in non-coherent architectures), alignment is here
			// only to ensure that these "real" tokens are not cached in the
			// same cache line as other real tokens. 
			// Consequently, enlarging buffers as follows is sufficient to 
			// prevent cache-line alignment issues:
			// minIdx = min(0 - (_alignment -1), minIdx)
			// maxIdx = max(maxIdx + (_alignment -1), maxIdx)
			//
			 
		}
		val oldMinIndex = buffer.minIndex
		if (oldMinIndex == 0 || (oldMinIndex) % alignment != 0) {
			buffer.minIndex = ((oldMinIndex / _alignment) - 1) * _alignment

			// New range is indivisible with end of buffer
			buffer.indivisibleRanges.lazyUnion(new Range(buffer.minIndex, oldMinIndex + 1))
		}

		val oldMaxIndex = buffer.maxIndex
		if (oldMaxIndex == buffer.nbTokens * buffer.tokenSize || (oldMaxIndex) % alignment != 0) {
			buffer.maxIndex = ((oldMaxIndex / _alignment) + 1) * _alignment

			// New range is indivisible with end of buffer
			buffer.indivisibleRanges.lazyUnion(new Range(oldMaxIndex - 1, buffer.maxIndex))
		}

		// Update matches of the buffer
		// Since the updateMatches update the remote buffer matchTable of a match
		// except the given match, we create a fake match with the current 
		// buffer as a remote buffer
		val fakeMatch = new Match(null, 0, buffer, 0, 0)
		updateMatches(fakeMatch)
		updateConflictingMatches(buffer.matchTable.values.flatten.toList)
	}

	/**
	 * For each {@link Buffer} passed as a parameter, this method scan the 
	 * {@link Match} in the {@link Buffer#getMatchTable() matchTable} and set.
	 * their {@link Match#getType() type}. Matches whose {@link 
	 * Match#getLocalBuffer() localBuffer} and {@link Match#getRemoteBuffer() 
	 * remoteBuffer} belong to the same {@link List} of {@link Match} will 
	 * cause the method to throw a {@link RuntimeError}. Other {@link Match}
	 * are marked as <code>FORWARD</code> or <code>BACKWARD</code>.
	 * 
	 * @param result
	 * 	{@link Pair} of {@link List} of {@link Buffer}. The {@link Pair} key 
	 * and value respectively contain input and output {@link Buffer} of an 
	 * actor. 
	 */
	def identifyMatchesType(Pair<List<Buffer>, List<Buffer>> result) {
		result.key.forEach [
			it.matchTable.values.flatten.forEach [
				if (result.key.contains(it.remoteBuffer)) {
					throw new RuntimeException("Inter-sibling matches are no longer allowed.")
				} else {
					it.type = MatchType::FORWARD
				}
			]
		]

		result.value.forEach [
			it.matchTable.values.flatten.forEach [
				if (result.value.contains(it.remoteBuffer)) {
					throw new RuntimeException("Inter-sibling matches are no longer allowed.")
				} else {
					it.type = MatchType::BACKWARD
				}
			]
		]
	}

	/** 
	 * Also fill the {@link Buffer#getDivisibilityRequiredMatches() 
	 * divisibilityRequiredMatches} {@link List}. 
	 */
	def identifyDivisibleBuffers(Pair<List<Buffer>, List<Buffer>> result) {
		val allBuffers = new ArrayList<Buffer>
		allBuffers.addAll(result.key)
		allBuffers.addAll(result.value)
		val divisibleCandidates = allBuffers.filter [ buffer |
			// A buffer is potentially divisible 	
			// If it has several matches (that were not merged by the
			//  simplifyResult). (Because if the buffer only has one
			// contiguous match, a divided buffer is not possible, cf
			// Buffer.simplifyMatches() comments.)
			buffer.matchTable.size > 1 &&
				// if it is totally matched, so that all parts of the divided 
				// buffer can still be accessed in an immediately logical way.
				// With the successive merges, unmatched ranges might become 
				// part of an indivisible range with a matched range. However
				// since this kind of behavior is not intuitive, we set not
				// completely matched buffers as indivisible from the start so 
				// that the developer knows where tokens are only by looking at
				// its actor script.
				buffer.completelyMatched
		// Note that at this point, virtual tokens are always matched
		// so this constraint ensure that future virtual tokens are 
		// always attached to real token by an overlapping 
		// indivisible range !
		]

		// All are divisible BUT it will not be possible to match divided
		// buffers together (checked later)
		divisibleCandidates.forEach [ buffer |
			val drMatches = newArrayList
			buffer.divisibilityRequiredMatches.add(drMatches)
			buffer.matchTable.values.flatten.forEach [
				val r = it.localRange
				buffer.indivisibleRanges.lazyUnion(r)
				drMatches.add(it)
			]
		]

		// All other buffers are not divisible
		allBuffers.removeAll(divisibleCandidates)
		allBuffers.forEach [
			it.indivisibleRanges.add(new Range(it.minIndex, it.maxIndex))
		]
	}

	/**
	 * This method fills the {@link Match#getConflictCandidates() 
	 * conflictCandidates} {@link Match} {@link List} of all the {@link Match 
	 * matches} contained in the {@link Buffer#getMatchTable() matchTable} of 
	 * the {@link Buffer} passed as parameter. Two {@link Match} are 
	 * potentially conflicting if:
	 * <ul><li>They have the same {@link Match#getRemoteBuffer()}</li></ul>
	 * 
	 * @param inputs
	 * 	{@link List} of input {@link Buffer} of an actor.
	 * @param outputs
	 * 	{@link List} of output {@link Buffer} of an actor.
	 */
	def identifyConflictingMatchCandidates(List<Buffer> inputs, List<Buffer> outputs) {

		// Identify potentially conflicting matches
		// For each Buffer
		for (buffer : #{inputs, outputs}.flatten) {

			// Get the matches 
			val matches = new ArrayList<Match>(buffer.matchTable.values.flatten.toList)

			// Update the potential conflict list of all matches
			matches.forEach [ match |
				match.reciprocate.conflictCandidates.addAll(matches.filter[it != match].map[it.reciprocate])
			]

		}

		// Identify the already conflicting matches
		for (buffer : #{inputs, outputs}.flatten) {

			// for Each match
			val matchList = buffer.matchTable.values.flatten
			Buffer::updateConflictingMatches(matchList)
		}
	}

	/**
	 * Process the groups generated by the groupVertices method.
	 * @return the total amount of memory saved
	 */
	def processGroup(ArrayList<DAGVertex> vertices) {

		// Get all the buffers
		val buffers = newArrayList
		vertices.forEach [
			var pair = scriptResults.get(it)
			// Buffer that were already merged are not processed
			buffers.addAll(pair.key.filter[it.appliedMatches.size == 0])
			buffers.addAll(pair.value.filter[it.appliedMatches.size == 0])
		]

		// copy the buffer list for later use in MEG update
		bufferGroups.add(new ArrayList(buffers))
		nbBuffersBefore = nbBuffersBefore + buffers.size

		var before = buffers.fold(0, [res, buf|res + buf.maxIndex - buf.minIndex])
		sizeBefore = sizeBefore + before

		// Iterate the merging algorithm until no buffers are merged
		var step = 0
		var stop = false
		do {

			// Sort the buffers in alphabetical order to enforce similarities 
			// between successive run
			buffers.sortInplace [ a, b |
				val nameRes = a.dagVertex.name.compareTo(b.dagVertex.name)
				if(nameRes != 0) nameRes else a.name.compareTo(b.name)
			]

			val matchedBuffers = switch (step) {
				// First step: Merge non-conflicting buffer with a unique match 
				case 0:
					processGroupStep0(buffers)
				// Second step: Merge divisible buffers with multiple matchs 
				// and no conflict 
				case 1:
					processGroupStep1(buffers)
				// Third step: Same as step 0, but test forward matches
				// of buffers only
				case 2:
					processGroupStep2(buffers)
				// Fourth step: Like case 1 but considering forward only
				// or backward only matches
				case 3:
					processGroupStep3(buffers)
				// Fifth step: Mergeable buffers with a unique backward match that have conflict(s) 
				case 4:
					processGroupStep4(buffers)
				case 5:
					processGroupStep5(buffers)
				case 6:
					processGroupStep6(buffers)
				case 7:
					processGroupStep7(buffers)
			}

			if (matchedBuffers.size != 0) {
				step = 0
			} else {
				step = step + 1
			}

			// Stop if only buffers with no match remains
			stop = buffers.forall[it.matchTable.empty]

		} while (step < 8 && !stop)

		var after = buffers.fold(0, [res, buf|res + buf.maxIndex - buf.minIndex])
		if (verbose) {
			println('''«before» => «after» : «((before - after) as float) / before»''')
			println("---")
		}

		nbBuffersAfter = nbBuffersAfter + buffers.size
		sizeAfter = sizeAfter + after
	}

	/**
	 * Match {@link Buffer buffers} with a unique {@link Match} in their 
	 * {@link Buffer#getMatchTable() matchTable} if:<ul>
	 * <li>The unique match covers the whole real token range of the buffer
	 * </li>
	 * <li>The match is not {@link Match#getConflictingMatches() conflicting} 
	 * with any other match</li>
	 * <li>The match and its {@link Match#getReciprocate() reciprocate} are 
	 * applicable.</li></ul>
	 * 
	 * @param buffers
	 * 		{@link List} of {@link Buffer} of the processed group. Matched 
	 * 		buffers will be removed from this list by the method.
	 * @return a {@link List} of merged {@link Buffer}.
	 */
	def processGroupStep0(List<Buffer> buffers) {
		val candidates = newArrayList

		for (candidate : buffers) {
			val entry = candidate.matchTable.entrySet.head

			// Returns true if:
			// There is a unique match
			var test = candidate.matchTable.size == 1 && entry.value.size == 1

			// that covers at index 0 (or less)
			test = test && entry.value.head.localIndivisibleRange.start <= 0

			// and ends at the end of the buffer (or more)
			test = test && entry.value.head.localIndivisibleRange.end >= candidate.nbTokens * candidate.tokenSize

			//entry.key + entry.value.head.length >= candidate.nbTokens * candidate.tokenSize
			// and is not involved in any conflicting range
			test = test && {
				val match = entry.value.head
				match.conflictingMatches.size == 0 && match.applicable && match.reciprocate.applicable
			}

			// and remote buffer is not already in the candidates list
			test = test && !candidates.contains(entry.value.head.remoteBuffer)

			if (test) {
				candidates.add(candidate)
			}
		}

		// If there are candidates, apply the matches
		if(verbose) print('''0- («candidates.size») ''')
		for (candidate : candidates) {
			if(verbose) print('''«candidate.matchTable.entrySet.head.value.head»  ''')
			candidate.applyMatches(candidate.matchTable.entrySet.head.value)
		}
		if(verbose) println("")

		buffers.removeAll(candidates)

		// Return the matched buffers
		return candidates
	}

	/**
	 * Match {@link Buffer buffers} that are divisible if:<ul>
	 * <li>The buffer is {@link Buffer#isDivisible() divisible}.</li>
	 * <li>Its matches cover the whole real token range of the buffer</li>
	 * <li>Its matches are not {@link Match#getConflictingMatches() 
	 * conflicting} with any other match.</li>
	 * <li>The buffer has no {@link Buffer#getMultipleMatchRange(Buffer) 
	 * multipleMatchRange}.</li></ul>
	 *  
	 * @param buffers
	 * 		{@link List} of {@link Buffer} of the processed group. Matched 
	 * 		buffers will be removed from this list by the method.
	 * @return a {@link List} of merged {@link Buffer}.
	 */
	def processGroupStep1(List<Buffer> buffers) {

		val candidates = newArrayList

		for (candidate : buffers) {

			// Find all divisible buffers with multiple match and no 
			// conflict that are not matched in another divisible buffer
			// (if any)
			// Has a non-empty matchTable 
			var test = candidate.matchTable.size != 0

			// is divisible
			test = test && candidate.divisible
			test = test && candidate.matchTable.values.flatten.forall [
				// Is not involved in any conflicting range
				it.conflictingMatches.size == 0 && it.applicable && it.reciprocate.applicable
			]

			// Has no multiple match Range. 
			test = test && candidate.multipleMatchRange.size == 0

			// No need to check the divisibilityRequiredMatches since
			// the only matches of the Buffer are the one
			// responsible for the division
			// and remote buffer(s) are not already in the candidates list
			test = test && candidate.matchTable.values.flatten.map[it.remoteBuffer].forall[!candidates.contains(it)]

			if (test) {
				candidates.add(candidate)
			}
		}

		// If there are candidates, apply the matches
		if(verbose) print('''1- («candidates.size») ''')
		for (candidate : candidates) {
			if(verbose) print('''«candidate»  ''')
			applyDivisionMatch(candidate, candidate.matchTable.values.flatten.toList)
		}
		if(verbose) println("")

		buffers.removeAll(candidates)

		// Return the matched buffers
		return candidates
	}

	/**
	 * Match {@link Buffer buffers} with a unique <code>FORWARD</code> {@link 
	 * Match} (or a unique <code>BACKWARD</code> {@link Match}). in their 
	 * {@link Buffer#getMatchTable() matchTable} if:<ul>
	 * <li>The unique match covers the whole real token range of the buffer
	 * </li>
	 * <li>The match is not {@link Match#getConflictingMatches() conflicting} 
	 * with any other match</li>
	 * <li>The match and its {@link Match#getReciprocate() reciprocate} are 
	 * applicable.</li></ul>
	 * 
	 * @param buffers
	 * 		{@link List} of {@link Buffer} of the processed group. Matched 
	 * 		buffers will be removed from this list by the method.
	 * @return a {@link List} of merged {@link Buffer}.
	 */
	def processGroupStep2(List<Buffer> buffers) {
		val candidates = newLinkedHashMap
		val involved =  newArrayList
		
		for (candidate : buffers) {
			val iterType = #[MatchType::FORWARD, MatchType::BACKWARD].iterator
			var test = false
			while (iterType.hasNext && !test) {
				val currentType = iterType.next
				val matches = candidate.matchTable.values.flatten.filter[it.type == currentType]

				// Returns true if:
				// There is a unique forward match
				test = matches.size == 1

				// that begins at index 0 (or less)
				test = test && matches.head.localIndivisibleRange.start <= 0

				// and ends at the end of the buffer (or more)
				test = test && matches.head.localIndivisibleRange.end >= candidate.nbTokens * candidate.tokenSize

				// and is not involved in any conflicting match
				test = test && matches.head.conflictingMatches.size == 0

				// and is both backward and forward applicable
				test = test && matches.head.applicable && matches.head.reciprocate.applicable

				// and remote buffer is not already involved in a match 
				test = test && !involved.contains(matches.head.remoteBuffer)
				test = test && !involved.contains(candidate)

				if (test) {
					candidates.put(candidate, currentType)
					involved.add(matches.head.remoteBuffer)
					involved.add(candidate)
				}
			}
		}

		// If there are candidates, apply the matches
		if(verbose) print('''2- («candidates.size») ''')
		for (candidate : candidates.entrySet) {
			if(verbose) print('''«candidate.key.matchTable.values.flatten.filter[it.type == candidate.value].head»  ''')
			candidate.key.applyMatches(
				#[candidate.key.matchTable.values.flatten.filter[it.type == candidate.value].head])
		}
		if(verbose) println("")

		buffers.removeAll(candidates.keySet)

		// Return the matched buffers
		return candidates.keySet.toList
	}

	/**
	 * Match {@link Buffer buffers} that are divisible with their <code>FORWARD
	 * </code> {@link Match matches} only (or a their <code>BACKWARD</code> 
	 * {@link Match matches} only) if:<ul>
	 * <li>The buffer is {@link Buffer#isDivisible() divisible}.</li>
	 * <li>Its matches cover the whole real token range of the buffer</li>
	 * <li>Its matches are not {@link Match#getConflictingMatches() 
	 * conflicting} with any other match.</li>
	 * <li>The buffer has no {@link Buffer#getMultipleMatchRange(Buffer) 
	 * multipleMatchRange}.</li>
	 * <li>The buffer verify the {@link 
	 * Buffer#doesCompleteRequiredMatches(Iterable)} condition.</li></ul>
	 * 
	 * @param buffers
	 * 		{@link List} of {@link Buffer} of the processed group. Matched 
	 * 		buffers will be removed from this list by the method.
	 * @return a {@link List} of merged {@link Buffer}.
	 */
	def processGroupStep3(List<Buffer> buffers) {
		val candidates = newLinkedHashMap

		for (candidate : buffers) {
			val iterType = #[MatchType::FORWARD, MatchType::BACKWARD].iterator
			var test = false
			while (iterType.hasNext && !test) {
				val currentType = iterType.next

				val matches = candidate.matchTable.values.flatten.filter[it.type == currentType]

				// Returns true if:
				// Has a several matches 
				test = matches.size != 0

				// is divisible
				test = test && candidate.divisible

				// and is not involved in any conflicting match
				test = test && matches.forall[
					it.conflictingMatches.size == 0 && it.applicable && it.reciprocate.applicable]

				// Matches have no multiple match Range. 
				test = test && matches.overlappingRanges.size == 0

				// Check divisibilityRequiredMatches
				test = test && candidate.doesCompleteRequiredMatches(matches)

				// and remote buffer(s) are not already in the candidates list
				test = test && matches.forall[!candidates.keySet.contains(it.remoteBuffer)]

				if (test) {
					candidates.put(candidate, currentType)
				}
			}
		}

		// If there are candidates, apply the matches
		if(verbose) print('''3- («candidates.size») ''')
		for (candidate : candidates.entrySet) {
			if(verbose) print('''«candidate.key»  ''')
			applyDivisionMatch(candidate.key,
				candidate.key.matchTable.values.flatten.filter[it.type == candidate.value].toList)
		}
		if(verbose) println("")

		buffers.removeAll(candidates.keySet)

		// Return the matched buffers
		return candidates.keySet.toList
	}

	/**
	 * Match {@link Buffer buffers} with a unique {@link Match} in their 
	 * {@link Buffer#getMatchTable() matchTable} if:<ul>
	 * <li>The unique match covers the whole real token range of the buffer
	 * </li>
	 * <li>The match is {@link Match#getConflictingMatches() conflicting} 
	 * with other match(es)</li>
	 * <li>The buffer is mergeable</li>
	 * <li>The match and its {@link Match#getReciprocate() reciprocate} are 
	 * applicable.</li></ul>
	 * 
	 * @param buffers
	 * 		{@link List} of {@link Buffer} of the processed group. Matched 
	 * 		buffers will be removed from this list by the method.
	 * @return a {@link List} of merged {@link Buffer}.
	 */
	def processGroupStep4(List<Buffer> buffers) {
		val candidates = newArrayList
		val involved =  newArrayList

		for (candidate : buffers) {

			val entry = candidate.matchTable.entrySet.head

			// Returns true if:
			// There is a unique match
			var test = candidate.matchTable.size == 1 && entry.value.size == 1

			// Is backward
			test = test && entry.value.head.type == MatchType::BACKWARD

			// that begins at index 0 (or less)
			test = test && entry.value.head.localIndivisibleRange.start <= 0

			// and ends at the end of the buffer (or more)
			test = test && entry.value.head.localIndivisibleRange.end >= candidate.nbTokens * candidate.tokenSize

			// and is involved in any conflicting range
			test = test && {
				val match = entry.value.head
				match.conflictingMatches.size > 0 && match.applicable && match.reciprocate.applicable
			}

			// buffer is fully mergeable
			test = test && {
				candidate.mergeableRanges.size == 1 && candidate.mergeableRanges.head.start == candidate.minIndex &&
					candidate.mergeableRanges.head.end == candidate.maxIndex
			}

			// and remote and local buffer are not already in the candidates list
			test = test && !involved.contains(entry.value.head.remoteBuffer)
			test = test && !involved.contains(candidate)

			if (test) {
				candidates.add(candidate)
				involved.add(entry.value.head.remoteBuffer)
				involved.add(candidate)
			}
		}

		// If there are candidates, apply the matches
		if(verbose) print('''4- («candidates.size») ''')
		for (candidate : candidates) {
			if(verbose) print('''«candidate.matchTable.entrySet.head.value.head»  ''')
			candidate.applyMatches(candidate.matchTable.entrySet.head.value)
		}
		if(verbose) println("")

		buffers.removeAll(candidates)

		// Return the matched buffers
		return candidates
	}

	/**
	 * Match {@link Buffer buffers} that are divisible with their <code>BACKWARD
	 * </code> {@link Match matches} only if:<ul>
	 * <li>The buffer is {@link Buffer#isDivisible() divisible}.</li>
	 * <li>Its matches cover the whole real token range of the buffer</li>
	 * <li>Its matches are {@link Match#getConflictingMatches() 
	 * conflicting} with other match(s) but are applicable.</li>
	 * <li>The buffer is fully mergeable</li>
	 * <li>The matches are not overlapping with each other.</li>
	 * <li>The buffer verify the {@link 
	 * Buffer#doesCompleteRequiredMatches(Iterable)} condition.</li></ul>
	 * 
	 * @param buffers
	 * 		{@link List} of {@link Buffer} of the processed group. Matched 
	 * 		buffers will be removed from this list by the method.
	 * @return a {@link List} of merged {@link Buffer}.
	 */
	def processGroupStep5(List<Buffer> buffers) {
		val candidates = newArrayList

		for (candidate : buffers) {
			val matches = candidate.matchTable.values.flatten.filter[it.type == MatchType::BACKWARD]

			// Returns true if:
			// Has a several matches 
			var test = matches.size != 0

			// is divisible
			test = test && candidate.divisible

			// and is involved in conflicting match(es)
			test = test && !matches.forall[it.conflictingMatches.size == 0]

			// All matches are applicable
			test = test && matches.forall[it.applicable && it.reciprocate.applicable]

			// buffer is fully mergeable
			test = test && {
				candidate.mergeableRanges.size == 1 && candidate.mergeableRanges.head.start == candidate.minIndex &&
					candidate.mergeableRanges.head.end == candidate.maxIndex
			}

			// Matches have no multiple match Range. 
			test = test && matches.overlappingRanges.size == 0

			// Check divisibilityRequiredMatches
			test = test && candidate.doesCompleteRequiredMatches(matches)

			// and remote buffer(s) are not already in the candidates list
			test = test && matches.forall[!candidates.contains(it.remoteBuffer)]

			if (test) {
				candidates.add(candidate)

			}
		}

		// If there are candidates, apply the matches
		if(verbose) print('''5- («candidates.size») ''')
		for (candidate : candidates) {
			if(verbose) print('''«candidate»  ''')
			applyDivisionMatch(candidate,
				candidate.matchTable.values.flatten.filter[it.type == MatchType::BACKWARD].toList)
		}
		if(verbose) println("")

		buffers.removeAll(candidates)

		// Return the matched buffers
		return candidates
	}

	/**
	 * Match {@link Buffer buffers} with a unique {@link Match} in their 
	 * {@link Buffer#getMatchTable() matchTable} if:<ul>
	 * <li>The unique match covers the whole real token range of the buffer
	 * </li>
	 * <li>The match is {@link Match#getConflictingMatches() conflicting} 
	 * with other match(es) but is applicable</li>
	 * <li>The buffer is partially or not mergeable</li>
	 * <li>The match and its {@link Match#getReciprocate() reciprocate} are 
	 * applicable.</li></ul>
	 * 
	 * @param buffers
	 * 		{@link List} of {@link Buffer} of the processed group. Matched 
	 * 		buffers will be removed from this list by the method.
	 * @return a {@link List} of merged {@link Buffer}.
	 */
	def processGroupStep6(List<Buffer> buffers) {

		val candidates = newLinkedHashMap
		val involved = newArrayList

		// Largest buffers first for this step.
		buffers.sortInplace [ a, b |
			// Largest buffer first
			val size = (a.minIndex - a.maxIndex) - (b.minIndex - b.maxIndex)
			// Alphabetical order for buffers of equal size
			if (size != 0)
				size
			else {
				val nameRes = a.dagVertex.name.compareTo(b.dagVertex.name)
				if(nameRes != 0) nameRes else a.name.compareTo(b.name)
			}
		]

		for (candidate : buffers) {
			val iterType = #[MatchType::FORWARD, MatchType::BACKWARD].iterator
			var test = false
			while (iterType.hasNext && !test) {
				val currentType = iterType.next
				val matches = candidate.matchTable.values.flatten.filter[it.type == currentType]

				// Returns true if:
				// There is a unique forward match
				test = matches.size == 1

				// that begins at index 0 (or less)
				test = test && matches.head.localIndivisibleRange.start <= 0

				// and ends at the end of the buffer (or more)
				test = test && matches.head.localIndivisibleRange.end >= candidate.nbTokens * candidate.tokenSize

				// and is involved in conflicting range
				test = test && {
					val match = matches.head
					match.conflictingMatches.size > 0 && match.applicable && match.reciprocate.applicable
				}

				// buffer not fully mergeable, no test needed,
				// such a buffer it would have been matched in step 4
				// Conflicting matches of the match are not already in the candidate list
				test = test && {
					val match = matches.head
					match.conflictingMatches.forall[!candidates.keySet.contains(it.localBuffer)]
				}

				// and buffers are not already in the candidates list
				test = test && !involved.contains(matches.head.remoteBuffer)
				test = test && !involved.contains(candidate)

				if (test) {
					candidates.put(candidate, currentType)
					involved.add(matches.head.remoteBuffer)
					involved.add(candidate)
				}
			}
		}

		// If there are candidates, apply the matches
		if(verbose) print('''6- («candidates.size») ''')
		for (candidate : candidates.entrySet) {
			if(verbose) print('''«candidate.key.matchTable.values.flatten.filter[it.type == candidate.value].head»  ''')
			candidate.key.applyMatches(
				#[candidate.key.matchTable.values.flatten.filter[it.type == candidate.value].head])
		}
		if(verbose) println("")

		buffers.removeAll(candidates.keySet)

		// Return the matched buffers
		return candidates.keySet.toList
	}

	/**
	 * Match {@link Buffer buffers} that are divisible with their <code>FORWARD
	 * </code> {@link Match matches} only (or a their <code>BACKWARD</code> 
	 * {@link Match matches} only) if:<ul>
	 * <li>The buffer is {@link Buffer#isDivisible() divisible}.</li>
	 * <li>Its matches cover the whole real token range of the buffer</li>
	 * <li>Its matches are {@link Match#getConflictingMatches() 
	 * conflicting} with other matches but are applicable.</li>
	 * <li>The matches are not overlapping.</li>
	 * <li>The buffer verify the {@link 
	 * Buffer#doesCompleteRequiredMatches(Iterable)} condition.</li></ul>
	 * 
	 * @param buffers
	 * 		{@link List} of {@link Buffer} of the processed group. Matched 
	 * 		buffers will be removed from this list by the method.
	 * @return a {@link List} of merged {@link Buffer}.
	 */
	def processGroupStep7(List<Buffer> buffers) {
		val candidates = newLinkedHashMap

		for (candidate : buffers) {
			val iterType = #[MatchType::FORWARD, MatchType::BACKWARD].iterator
			var test = false
			while (iterType.hasNext && !test) {
				val currentType = iterType.next

				val matches = candidate.matchTable.values.flatten.filter[it.type == currentType]

				// Returns true if:
				// Has a several matches 
				test = matches.size != 0

				// is divisible
				test = test && candidate.divisible

				// and is involved in conflicting match
				test = test && matches.forall[
					it.conflictingMatches.size != 0 && it.applicable && it.reciprocate.applicable]

				// Matches have no multiple match Range. 
				test = test && matches.overlappingRanges.size == 0

				// Check divisibilityRequiredMatches
				test = test && candidate.doesCompleteRequiredMatches(matches)

				// Conflicting matches of the matches are not already in the candidate list
				test = test && {
					matches.forall [
						it.conflictingMatches.forall[!candidates.keySet.contains(it.localBuffer)]
					]
				}

				// and remote buffer(s) are not already in the candidates list
				test = test && matches.forall[!candidates.keySet.contains(it.remoteBuffer)]

				if (test) {
					candidates.put(candidate, currentType)
				}
			}
		}

		// If there are candidates, apply the matches
		if(verbose) print('''7- («candidates.size») ''')
		for (candidate : candidates.entrySet) {
			if(verbose) print('''«candidate.key»  ''')
			applyDivisionMatch(candidate.key,
				candidate.key.matchTable.values.flatten.filter[it.type == candidate.value].toList)
		}
		if(verbose) println("")

		buffers.removeAll(candidates.keySet)

		// Return the matched buffers
		return candidates.keySet.toList
	}

	new(int alignment) {
		this._alignment = alignment
	}

	/**
	 * Called only for divisible buffers with multiple match and no 
	 * conflict that are not matched in another divisible buffer
	 */
	def applyDivisionMatch(Buffer buffer, List<Match> matches) {

		// In the current version, the buffer only contains
		// the matches necessary and sufficient for the division (i.e. no multiple matched ranges)
		// To process this special case in the future, some matches will have 
		// to be changes: e.g. siblings will become forward or things like that
		// . For a simpler version, simply remove those other matches.	
		// The match table will be modified by the applyMatch method, so we need a copy of it to iterate ! 
		// Remove the matches from each other conflict candidates
		matches.forEach [
			it.conflictCandidates.removeAll(matches)
			it.reciprocate.conflictCandidates.removeAll(matches.map[it.reciprocate])
			it.conflictingMatches.removeAll(matches)
			it.reciprocate.conflictingMatches.removeAll(matches.map[it.reciprocate])
		]

		// apply the matches of the buffer one by one
		buffer.applyMatches(matches)
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
	 * buffers of different actors that correspond to the same SDFEdges. This 
	 * method must be called after {@link
	 * ScriptRunner#identifyDivisibleBuffer()} as it set to indivisible the 
	 * buffers that are on the border of groups. 
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
			val List<Buffer> intraGroupBuffer = newArrayList
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
										validBuffers = true

										// Match them together
										val match = buffers.get(0).matchWith(0, buffers.get(1), 0,
											buffers.get(0).nbTokens)
										val forwardMatch = if (buffers.get(0).dagVertex == dagEdge.source) {
												match.type = MatchType::FORWARD
												match.reciprocate.type = MatchType::BACKWARD
												match
											} else {
												match.type = MatchType::BACKWARD
												match.reciprocate.type = MatchType::FORWARD
												match.reciprocate
											}

										// Apply the forward match immediately
										// (we always apply the forward match to enforce
										// reproducibility of the processing)
										forwardMatch.localBuffer.applyMatches(#[forwardMatch])

										// Save matched buffer
										intraGroupBuffer.add(match.localBuffer)
										intraGroupBuffer.add(match.remoteBuffer)

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

			// Set as indivisible all buffers that are on the edge of the group.
			group.forEach [
				val results = scriptResults.get(it)
				#{results.key, results.value}.flatten.filter[!intraGroupBuffer.contains(it)].forEach [
					it.indivisibleRanges.lazyUnion(new Range(it.minIndex, it.maxIndex))
				]
			]

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
			parameters.put("alignment", _alignment)

			// Create the input/output lists
			val inputs = sdfVertex.incomingEdges.map[
				// An input buffer is backward mergeable if it is Pure_in OR if it is unused
				val isMergeable = (it.targetPortModifier ?: "").toString.contains(SDFEdge::MODIFIER_PURE_IN) || ((it.
					targetPortModifier ?: "").toString.contains(SDFEdge::MODIFIER_UNUSED))
				try {
					new Buffer(it, dagVertex, it.targetLabel, it.cons.intValue, dataTypes.get(it.dataType.toString).size,
						isMergeable)
				} catch (NullPointerException exc) {
					throw new WorkflowException(
						'''SDFEdge «it.source.name»_«it.sourceLabel»->«it.target.name»_«it.targetLabel» has unknows type «it.
							dataType.toString». Add the corresponding data type to the scenario.''')
				}].toList

			val outputs = sdfVertex.outgoingEdges.map[
				// An output buffer is mergeable if it is unused or if its target port is not Pure_in 
				val isMergeable = (it.targetPortModifier ?: "").toString.contains(SDFEdge::MODIFIER_PURE_IN) || ((it.
					targetPortModifier ?: "").toString.contains(SDFEdge::MODIFIER_UNUSED))
				try {
					new Buffer(it, dagVertex, it.sourceLabel, it.prod.intValue, dataTypes.get(it.dataType.toString).size,
						isMergeable)
				} catch (NullPointerException exc) {
					throw new WorkflowException(
						'''SDFEdge «it.source.name»_«it.sourceLabel»->«it.target.name»_«it.targetLabel» has unknows type «it.
							dataType.toString». Add the corresponding data type to the scenario.''')
				}].toList

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

	def updateMEG(MemoryExclusionGraph meg) {

		// Create a new property in the MEG to store the merged memory objects
		val mergedMObjects = newHashMap
		meg.propertyBean.setValue(MemoryExclusionGraph::HOST_MEMORY_OBJECT_PROPERTY, mergedMObjects)
		
		// For each buffer, get the corresponding MObject
		val bufferAndMObjectMap = newHashMap
		for (buffers : bufferGroups) {			
			for (buffer : buffers) {
				// Get the Mobj
				val mObjCopy = new MemoryExclusionVertex(buffer.sdfEdge.source.name, buffer.sdfEdge.target.name, 0)
				val mObj = meg.getVertex(mObjCopy)
				if (mObj == null) {
					throw new WorkflowException(
						'''Cannot find «mObjCopy» in the given MEG. Contact developers for more information.''')
				}

				if (mObj.weight != buffer.nbTokens * buffer.tokenSize) {

					// Karol's Note:
					// To process the aggregated dag edges, we will need to 
					// split them in the MEG. Doing so, we still need to make 
					// sure that all related information remains correct:
					// - Exclusions
					// - Scheduling order
					// - Predecessors
					// - The two Mobj must have different source and sink names.
					// or otherwise they will be considered equals() even with 
					// different sizes. 
					//
					// Also we will need to make sure that the code generation
					// printerS are still functional 
					throw new WorkflowException(
						'''Aggregated DAG Edge «mObj» not yet supported. Contact Preesm developers for more information.''')
				}
				bufferAndMObjectMap.put(buffer, mObj)
			}
		}
		
		// Backup neighbors of each buffer before changing anything in the meg
		for (buffers : bufferGroups) {
			for (buffer : buffers) {
				val mObj = bufferAndMObjectMap.get(buffer)
				val neighbors = new ArrayList<MemoryExclusionVertex>(meg.getAdjacentVertexOf(mObj))
				mObj.setPropertyValue(MemoryExclusionVertex::ADJACENT_VERTICES_BACKUP, neighbors)
			}
		}

		// Process each group of buffers separately
		for (buffers : bufferGroups) {


			// For each unmatched buffer that received matched buffers
			for (buffer : buffers.filter[it.matched == null && it.host]) {

				// Enlarge the corresponding mObject to the required size
				val mObj = bufferAndMObjectMap.get(buffer)
				val minIndex = if (buffer.minIndex == 0 || _alignment == -1) {
						buffer.minIndex
					} else {

						// Make sure that index aligned in the buffer are in 
						// fact aligned
						((buffer.minIndex / _alignment) - 1) * _alignment
					}
				mObj.setWeight(buffer.maxIndex - minIndex)

				// Add the mobj to the meg host list
				mergedMObjects.put(mObj, newHashSet)

				// Save the real token range in the Mobj properties
				val realTokenRange = new Range(0, buffer.tokenSize * buffer.nbTokens)
				val actualRealTokenRange = new Range(-minIndex, buffer.tokenSize * buffer.nbTokens - minIndex)
				val ranges = newArrayList
				ranges.add(mObj -> (realTokenRange -> actualRealTokenRange))
				mObj.setPropertyValue(MemoryExclusionVertex::REAL_TOKEN_RANGE_PROPERTY, ranges)
			}

			// For each matched buffers
			for (buffer : buffers.filter[it.matched != null]) {

				// find the root buffer(s)
				// there might be several roots if the buffer was divided
				// the map associates:
				// a localRange of the buffer to 
				// a pair of a root buffer and its range for the buffer 
				val Map<Range, Pair<Buffer, Range>> rootBuffers = newHashMap()
				for (match : buffer.matched) {
					rootBuffers.putAll(match.root)
				}

				val mObj = bufferAndMObjectMap.get(buffer)
				
				// Enlarge the memory object (if needed)
				if(ScriptRunner::printTodo){
					println("We do not need to enlarge the buffer so much. We just need to enlarge it so that the minIndex is aligned (if alignement is needed)")
					// Change the emptyspace before to make sure that the buffer is aligned in the host buffer
					// for non-divided buffers only :
					// realTokenRange.start - EmptySpaceBefore must be modulo of alignement.
					// This condition will be sufficient to guarantee that other Mobject of the MEG
					// Will never share the same cache line as the merged Mobject. (For other buffer involved in the
					// Merge operation, this rule is guaranteed during the script execution.)
				}
				mObj.setWeight(mObj.getWeight - buffer.minIndex)
				mObj.setPropertyValue(MemoryExclusionVertex::EMPTY_SPACE_BEFORE, -buffer.minIndex)

				// For buffer receiving a part of the current buffer
				for (rootBuffer : rootBuffers.values.map[it.key]) {
					val rootMObj = bufferAndMObjectMap.get(rootBuffer)

					// Update the meg hostList property
					mergedMObjects.get(rootMObj).add(mObj)

					// Add exclusions between the rootMobj and all adjacent
					// memory objects of MObj
					for (excludingMObj : meg.getAdjacentVertexOf(mObj)) {
						if (rootMObj != excludingMObj && !meg.getAdjacentVertexOf(rootMObj).contains(excludingMObj)) {
							meg.addEdge(rootMObj, excludingMObj)
						}
					}
				}
				meg.removeVertex(mObj)

				// Fill the mobj properties (i.e. save the matched buffer info)	
				val List<Pair<MemoryExclusionVertex, Pair<Range, Range>>> mObjRoots = newArrayList
				mObj.setPropertyValue(MemoryExclusionVertex::REAL_TOKEN_RANGE_PROPERTY, mObjRoots)
				val realTokenRange = new Range(0, buffer.tokenSize * buffer.nbTokens)
				// For each subrange of real tokens, save the corresponding remote buffer
				// and range.
				rootBuffers.entrySet.forEach [ entry |
					val rootMObj = bufferAndMObjectMap.get(entry.value.key)
					val localRange = entry.key.intersection(realTokenRange)
					val translatedLocalRange = localRange.clone as Range
					translatedLocalRange.translate(entry.value.value.start - entry.key.start)
					val remoteRange = entry.value.value.intersection(translatedLocalRange)
					if(remoteRange != translatedLocalRange){
						// Should always be the case
						throw new RuntimeException("Unexpected error !")
					}
					mObjRoots.add(rootMObj -> (localRange -> remoteRange))
				]
				
				// Sort mObjRoots in order of contiguous ranges
				mObjRoots.sortInplaceBy[it.value.key.start]
			}
		}

		// List of the unused and pureout memobjects
		val unusedMObjects = newArrayList

		// Find unusedMObjects that were not processed by the scripts.
		unusedMObjects.addAll(
			meg.vertexSet.filter [ mObj |
				if (mObj.edge != null) {

					// Find unused pure_out edges
					val aggregate = mObj.edge.aggregate
					aggregate.forall [ sdfEdge |
						((sdfEdge as SDFEdge).getPropertyStringValue(SDFEdge::SOURCE_PORT_MODIFIER) ?: "").
							contains(SDFEdge::MODIFIER_PURE_OUT) && ((sdfEdge as SDFEdge).
							getPropertyStringValue(SDFEdge::TARGET_PORT_MODIFIER) ?: "").contains(SDFEdge::MODIFIER_UNUSED)
					]
				} else
					false
			].filter [ mObj |
				// keep only those that are not host. (matched ones have already been removed from the MEG)
				val correspondingBuffer = bufferGroups.flatten.findFirst[mObj.edge.aggregate.contains(it.sdfEdge)]
				if (correspondingBuffer != null) {
					!correspondingBuffer.host
				} else
					true
			].toList)

		// Remove all exclusions between unused buffers
		unusedMObjects.forEach [ mObj |
			val unusedNeighbors = meg.getAdjacentVertexOf(mObj).filter[unusedMObjects.contains(it)].toList
			unusedNeighbors.forEach[meg.removeEdge(mObj, it)]
		]
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
