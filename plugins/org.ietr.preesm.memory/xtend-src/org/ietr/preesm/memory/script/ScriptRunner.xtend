/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Alexandre Honorat <ahonorat@insa-rennes.fr> (2018)
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Julien Hascoet <jhascoet@kalray.eu> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2014 - 2015)
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
package org.ietr.preesm.memory.script

import bsh.EvalError
import bsh.Interpreter
import bsh.ParseException
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.List
import java.util.Map
import java.util.Set
import java.util.logging.Level
import java.util.logging.Logger
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.runtime.CoreException
import org.eclipse.core.runtime.IPath
import org.eclipse.core.runtime.Path
import org.eclipse.xtend.lib.annotations.Accessors
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
import org.ietr.preesm.core.scenario.PreesmScenario
import org.ietr.preesm.core.types.DataType
import org.ietr.preesm.memory.script.Buffer
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionGraph
import org.ietr.preesm.memory.exclusiongraph.MemoryExclusionVertex
import org.ietr.preesm.utils.files.ContainersManager
import org.ietr.preesm.utils.files.FilesManager

import static extension org.ietr.preesm.memory.script.Range.*

enum CheckPolicy {
	NONE,
	FAST,
	THOROUGH
}

@SuppressWarnings("rawtypes", "unchecked")
class ScriptRunner {

	public static final String SCRIPT_FOLDER = "scripts"

	public static final String JOIN_SCRIPT = "join.bsh"
	public static final String FORK_SCRIPT = "fork.bsh"
	public static final String ROUNDBUFFER_SCRIPT = "roundbuffer.bsh"
	public static final String BROADCAST_SCRIPT = "broadcast.bsh"

	// Name of bundle where to look for files (allow not to search into all projects)
	public static final String bundleId = "org.ietr.preesm.memory"

	// Name of the IProject where to extract script files
	public static final String tmpProjectName = bundleId + "." + "temporary"

	// Name of the IFolder where to extract script files
	public static final String tmpFolderPath = IPath.SEPARATOR + SCRIPT_FOLDER + IPath.SEPARATOR

	public static final String copiedScriptsPath = tmpProjectName + IPath.SEPARATOR + tmpFolderPath

	// Paths to the special scripts files
	public static final String JOIN = copiedScriptsPath + JOIN_SCRIPT
	public static final String FORK = copiedScriptsPath + FORK_SCRIPT
	public static final String ROUNDBUFFER = copiedScriptsPath + ROUNDBUFFER_SCRIPT
	public static final String BROADCAST = copiedScriptsPath + BROADCAST_SCRIPT

	/**
	 * Helper method to get the incoming {@link SDFEdge}s of an {@link
	 * SDFAbstractVertex}.
	 */
	def static Set<SDFEdge> incomingEdges(SDFAbstractVertex vertex) {
		return vertex.base.incomingEdgesOf(vertex)
	}

	/**
	 * Helper method to get the outgoing {@link SDFEdge}s of an {@link
	 * SDFAbstractVertex}.
	 */
	def static Set<SDFEdge> outgoingEdges(SDFAbstractVertex vertex) {
		return vertex.base.outgoingEdgesOf(vertex)
	}

	@Accessors CheckPolicy checkPolicy = CheckPolicy::NONE

	/**
	 * A {@link Map} that associates each {@link String} representing a type name
	 * with a corresponding {@link DataType}.
	 */
	@Accessors Map<String, DataType> dataTypes

	/**
	 * A {@link Map} that associates each {@link DAGVertex} from the
	 * {@link #scriptedVertices} map to the result of the successful
	 * execution of its script. The result is stored as a {@link Pair}
	 * of {@link List} of {@link Buffer}. The first {@link List} contains
	 * the input {@link Buffer buffers} and the second contains output
     * {@link Buffer buffers}.
	 */
	val scriptResults = new LinkedHashMap<DAGVertex, Pair<List<Buffer>, List<Buffer>>>

	/**
	 * A {@link Map} that associates each {@link DAGVertex} with a
	 * memory script to this memory script {@link File}.
	 */
	val scriptedVertices = new LinkedHashMap<DAGVertex, File>();

	/**
	 * Each {@link List} of {@link Buffer} stored in this {@link List}
	 * corresponds to an independent connected {@link Match} tree resulting
	 * from the execution of the memory scripts.
	 */
	package val List<List<Buffer>> bufferGroups = newArrayList

	@Accessors CharSequence log = ''''''

	@Accessors
	public final boolean printTodo

	public boolean generateLog = true

	static int nbBuffersBefore = 0
	static int nbBuffersAfter = 0
	static int sizeBefore
	static int sizeAfter

	/**
	 * This property is used to represent the alignment of buffers in memory.
	 * The same value, or a multiple should always be used in the memory
	 * allocation.
	 */
	@Accessors val int alignment

	/**
	 * Check the results obtained when running the {@link #run()} method.
	 * Checks are performed according to the current {@link #setCheckPolicy(CheckPolicy)}.
	 * The {@link #checkResult(File,Pair)} method is used to perform the checks.
	 * Vertices whose script results do not pass the checks are removed
	 * from the {@link #scriptResults} map.
	 */
	def void check() {
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
	def boolean checkResult(File script, Pair<List<Buffer>, List<Buffer>> result) {
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
								!(remoteMatches !== null && remoteMatches.contains(
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

		return res1 && res2 && res3
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
	def protected int findScripts(DirectedAcyclicGraph dag, PreesmScenario scenario) throws CoreException , IOException, URISyntaxException {

		// TODO : extract script lookup and initialization

		// Create temporary containers for special scripts files
		// and extract special script files and fill the map with it
		val project = ContainersManager.createProject(tmpProjectName)
		ContainersManager.createFolderInto(tmpFolderPath, project)
		FilesManager.extract(tmpFolderPath, tmpProjectName, bundleId)

		// Special scripts files
		val specialScriptFiles = new LinkedHashMap<String, File>()

		// Script files already found
		val scriptFiles = new LinkedHashMap<String, File>();

		putSpecialScriptFile(specialScriptFiles, JOIN, bundleId)
		putSpecialScriptFile(specialScriptFiles, FORK, bundleId)
		putSpecialScriptFile(specialScriptFiles, ROUNDBUFFER, bundleId)
		putSpecialScriptFile(specialScriptFiles, BROADCAST, bundleId)

		// Retrieve the original sdf folder
		val workspace = ResourcesPlugin.getWorkspace
		val root = workspace.getRoot

		// Logger is used to display messages in the console
		val logger = WorkflowLogger.getLogger

		// Identify all actors with a memory Script
		for (dagVertex : dag.vertexSet) {
			val sdfVertex = dagVertex.propertyBean.getValue(DAGVertex.SDF_VERTEX, SDFAbstractVertex)
			if (dagVertex.kind !== null) {
				switch (dagVertex.kind) {
					case DAGVertex.DAG_VERTEX: {
						val pathString = sdfVertex.propertyBean.getValue(SDFVertex.MEMORY_SCRIPT, String)
						if (pathString !== null) {

							// Retrieve the script path as a relative path to the
							// graphml
							var scriptFile = scriptFiles.get(pathString)
							if (scriptFile === null)
								scriptFile = root.getFile(new Path(pathString)).rawLocation.makeAbsolute.toFile
							if (scriptFile.exists) {
								scriptedVertices.put(dagVertex, scriptFile)
								scriptFiles.put(pathString, scriptFile)
							} else {
								val message = "Memory script of vertex " + sdfVertex.getName() + " is invalid: \"" +pathString + "\". Change it in the graphml editor."
								logger.log(Level.WARNING, message)
							}
						}
					}
					case DAGForkVertex.DAG_FORK_VERTEX: {
						associateScriptToSpecialVertex(dagVertex, "fork", specialScriptFiles.get(FORK))
					}
					case DAGJoinVertex.DAG_JOIN_VERTEX: {
						associateScriptToSpecialVertex(dagVertex, "join", specialScriptFiles.get(JOIN))
					}
					case DAGBroadcastVertex.DAG_BROADCAST_VERTEX: {
						if (sdfVertex instanceof SDFRoundBufferVertex) {
							associateScriptToSpecialVertex(dagVertex, "roundbuffer", specialScriptFiles.get(ROUNDBUFFER))
						} else {
							associateScriptToSpecialVertex(dagVertex, "broadcast", specialScriptFiles.get(BROADCAST))
						}
					}
				}
			}
		}

		// Remove all temporary objects (containers created, files extracted)
		ContainersManager.deleteProject(project)

		return scriptedVertices.size
	}

	/**
	 * Associate a script file to a special DAGVertex if this script file have been extracted,
	 * display an error otherwise
	 */
	private def void associateScriptToSpecialVertex(DAGVertex dagVertex, String vertexName, File scriptFile) {

		if (scriptFile === null || !scriptFile.exists) {
			val message = "Memory script [" + scriptFile + "] of [" + vertexName + "] vertices not found. Please contact Preesm developers."

			throw new IllegalStateException(message);
		} else {
			scriptedVertices.put(dagVertex, scriptFile)
		}
	}

	/**
	 * Get the special script file at the right path and put it into the map
	 */
	private def void putSpecialScriptFile(Map<String, File> specialScriptFiles, String filePath, String bundleFilter) {
		var File file = FilesManager.getFile(filePath, bundleFilter)
		if(file !== null) specialScriptFiles.put(filePath, file)
	}

	/**
	 * This method process the {@link #scriptResults} in order to simplify
	 * them with {@link #simplifyResult(List,List)}. Then, it extracts
	 * mergeable buffers.
	 * This method must be called after {@link #run()} and {@link #check()}
	 * have been successfully called.
	 */
	def void process() {

		// Simplify results
		scriptResults.forEach[vertex, result|simplifyResult(result.key, result.value)]

		// Identify divisible buffers
		scriptResults.forEach[vertex, result|identifyDivisibleBuffers(result)]

		// Update output buffers for alignment
		if (alignment > 0) {
			scriptResults.forEach [ vertex, result |
				// All outputs except the mergeable one linked only to read_only
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
					// the setMinIndex(int) function
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
		if (alignment > 0) {

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

		if (generateLog) {
			log = '''# Memory scripts summary''' + '\n' + '''- Independent match trees : *«groups.size»*''' + '\n' +
				'''- Total number of buffers in these trees: From «nbBuffersBefore» to «nbBuffersAfter» buffers.''' +
				"\n" + '''- Total size of these buffers: From «sizeBefore» to «sizeAfter» («100.0 *
					(sizeBefore - sizeAfter as float) / sizeBefore»%).''' + "\n\n" + '''# Match tree optimization log''' +
				'\n' + log
		}
	}

	def private void enlargeForAlignment(Buffer buffer) {
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
		// minIdx = min(0 - (alignment -1), minIdx)
		// maxIdx = max(maxIdx + (alignment -1), maxIdx)
		//
		}
		val oldMinIndex = buffer.minIndex
		if (oldMinIndex == 0 || (oldMinIndex) % alignment != 0) {
			buffer.minIndex = ((oldMinIndex / alignment) - 1) * alignment

			// New range is indivisible with end of buffer
			buffer.indivisibleRanges.lazyUnion(new Range(buffer.minIndex, oldMinIndex + 1))
		}

		val oldMaxIndex = buffer.maxIndex
		if (oldMaxIndex == buffer.nbTokens * buffer.tokenSize || (oldMaxIndex) % alignment != 0) {
			buffer.maxIndex = ((oldMaxIndex / alignment) + 1) * alignment

			// New range is indivisible with end of buffer
			buffer.indivisibleRanges.lazyUnion(new Range(oldMaxIndex - 1, buffer.maxIndex))
		}

		// Update matches of the buffer
		// Since the updateMatches update the remote buffer matchTable of a match
		// except the given match, we create a fake match with the current
		// buffer as a remote buffer
		val fakeMatch = new Match(null, 0, buffer, 0, 0)
		Buffer.updateMatches(fakeMatch)
		Buffer.updateConflictingMatches(buffer.matchTable.values.flatten.toList)
	}

	/**
	 * For each {@link Buffer} passed as a parameter, this method scan the
	 * {@link Match} in the {@link Buffer#getMatchTable() matchTable} and set.
	 * their {@link Match#getType() type}. Matches whose {@link
	 * Match#getLocalBuffer() localBuffer} and {@link Match#getRemoteBuffer()
	 * remoteBuffer} belong to the same {@link List} of {@link Match} will
	 * cause the method to throw a {@link RuntimeException}. Other {@link Match}
	 * are marked as <code>FORWARD</code> or <code>BACKWARD</code>.
	 *
	 * @param result
	 * 	{@link Pair} of {@link List} of {@link Buffer}. The {@link Pair} key
	 * and value respectively contain input and output {@link Buffer} of an
	 * actor.
	 */
	def private identifyMatchesType(Pair<List<Buffer>, List<Buffer>> result) {
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
	def private identifyDivisibleBuffers(Pair<List<Buffer>, List<Buffer>> result) {
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
	def private identifyConflictingMatchCandidates(List<Buffer> inputs, List<Buffer> outputs) {

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
			val matchList = buffer.matchTable.values.flatten.toList
			Buffer.updateConflictingMatches(matchList)
		}
	}

	/**
	 * Process the groups generated by the groupVertices method.
	 * @return the total amount of memory saved
	 */
	def private void processGroup(List<DAGVertex> vertices) {

		// Get all the buffers
		val buffers = newArrayList
		vertices.forEach [
			var pair = scriptResults.get(it)
			// Buffer that were already merged are not processed
			buffers.addAll(pair.key.filter[it.appliedMatches.size == 0])
			buffers.addAll(pair.value.filter[it.appliedMatches.size == 0])
		]

		// copy the buffer list for later use in MEG update
		val bufferList = new ArrayList(buffers)
		bufferGroups.add(bufferList)
		nbBuffersBefore = nbBuffersBefore + buffers.size

		var before = buffers.fold(0, [res, buf|res + buf.maxIndex - buf.minIndex])
		sizeBefore = sizeBefore + before
		if (generateLog) {
			log = log + '''## Tree of «buffers.size» buffers''' + '\n' + '''### Original buffer list:''' + '\n' +
				'''> «buffers»''' + "\n\n" + '''### Match application log: ''' + '\n'
		}

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
		if (generateLog) {
			log = log + "\n" + '''### Tree summary:''' + '\n'
			log = log + '''- From «bufferList.size» buffers to «buffers.size» buffers.''' + "\n"
			log = log + '''- From «before» bytes to «after» bytes («100.0 * ((before - after) as float) / before»%)''' +
				"\n\n"
		}

		// Log unapplied matches (if any)
		if (generateLog) {
			log = log + '''### Unapplied matches:''' + "\n>"
			val logged = newArrayList
			for (buffer : bufferList) {
				for (match : buffer.matchTable.values.flatten.filter[!it.applied]) {
					if (!logged.contains(match.reciprocate)) {
						log = log + match.originalMatch.toString + ", "
						logged.add(match)
					}
				}
			}
			log = log + "\n"
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
	def private processGroupStep0(List<Buffer> buffers) {
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
		if (generateLog && !candidates.empty)
			log = log + '''- __Step 0 - «candidates.size» matches__''' + "\n>"

		for (candidate : candidates) {
			if(generateLog) log = log + '''«candidate.matchTable.entrySet.head.value.head»  '''
			candidate.applyMatches(candidate.matchTable.entrySet.head.value)
		}
		if(generateLog && !candidates.empty) log = log + '\n'

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
	def private processGroupStep1(List<Buffer> buffers) {

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
		if (generateLog && !candidates.empty)
			log = log +
				'''- __Step 1 - «candidates.fold(0)[v, c|c.matchTable.values.flatten.size + v]» matches__ ''' + "\n>"
		for (candidate : candidates) {
			if (generateLog)
				log = log +
					'''«FOR match : candidate.matchTable.values.flatten.toList SEPARATOR ', '»«match»«ENDFOR», '''
			applyDivisionMatch(candidate, candidate.matchTable.values.flatten.toList)
		}
		if(generateLog && !candidates.empty) log = log + '\n'

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
	def private processGroupStep2(List<Buffer> buffers) {
		val candidates = newLinkedHashMap
		val involved = newArrayList

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
		if(generateLog && !candidates.empty) log = log + '''- __Step 2 - «candidates.size» matches__''' + "\n>"
		for (candidate : candidates.entrySet) {
			if (generateLog)
				log = log +
					'''«candidate.key.matchTable.values.flatten.filter[it.type == candidate.value].head»  '''
			candidate.key.applyMatches(
				#[candidate.key.matchTable.values.flatten.filter[it.type == candidate.value].head])
		}
		if(generateLog && !candidates.empty) log = log + '\n'

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
	def private processGroupStep3(List<Buffer> buffers) {
		val candidates = newLinkedHashMap

		for (candidate : buffers) {
			val iterType = #[MatchType::FORWARD, MatchType::BACKWARD].iterator
			var test = false
			while (iterType.hasNext && !test) {
				val currentType = iterType.next

				val matches = candidate.matchTable.values.flatten.filter[it.type == currentType].toList

				// Returns true if:
				// Has a several matches
				test = matches.size != 0

				// is divisible
				test = test && candidate.divisible

				// and is not involved in any conflicting match
				test = test && matches.forall[
					it.conflictingMatches.size == 0 && it.applicable && it.reciprocate.applicable]

				// Matches have no multiple match Range.
				test = test && Buffer.getOverlappingRanges(matches).size == 0

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
		if (generateLog && !candidates.empty)
			log = log + '''- __Step 3 - «candidates.entrySet.fold(0)[v, c|
				c.key.matchTable.values.flatten.filter[it.type == c.value].size + v]» matches__''' + "\n>"
		for (candidate : candidates.entrySet) {
			if (generateLog)
				log = log +
					'''«FOR match : candidate.key.matchTable.values.flatten.filter[it.type == candidate.value].toList SEPARATOR ', '»«match»«ENDFOR», '''
			applyDivisionMatch(candidate.key,
				candidate.key.matchTable.values.flatten.filter[it.type == candidate.value].toList)
		}
		if(generateLog && !candidates.empty) log = log + '\n'

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
	def private processGroupStep4(List<Buffer> buffers) {
		val candidates = newArrayList
		val involved = newArrayList

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
		if(generateLog && !candidates.empty) log = log + '''- __Step 4 - «candidates.size» matches__''' + "\n>"
		for (candidate : candidates) {
			if(generateLog) log = log + '''«candidate.matchTable.entrySet.head.value.head»  '''
			candidate.applyMatches(candidate.matchTable.entrySet.head.value)
		}
		if(generateLog && !candidates.empty) log = log + '\n'

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
	def private processGroupStep5(List<Buffer> buffers) {
		val candidates = newArrayList

		for (candidate : buffers) {
			val matches = candidate.matchTable.values.flatten.filter[it.type == MatchType::BACKWARD].toList

			// Returns true if:
			// Has a several matches
			var test = matches.size != 0

			// is divisible
			test = test && candidate.divisible

			// and is involved in conflicting match(es)
			test = test && !matches.forall[it.conflictingMatches.size == 0]

			// All matches are applicable
			test = test && matches.forall[it.applicable && it.reciprocate.applicable]

			// buffer is fully mergeable (Since buffer is fully mergeable
			// even if division matches are conflicting with each other
			// this will not be a problem since they are mergeable)
			test = test && {
				candidate.mergeableRanges.size == 1 && candidate.mergeableRanges.head.start == candidate.minIndex &&
					candidate.mergeableRanges.head.end == candidate.maxIndex
			}

			// Matches have no multiple match Range (on the local buffer side).
			test = test && Buffer.getOverlappingRanges(matches).size == 0

			// Check divisibilityRequiredMatches
			test = test && candidate.doesCompleteRequiredMatches(matches)

			// and remote buffer(s) are not already in the candidates list
			test = test && matches.forall[!candidates.contains(it.remoteBuffer)]

			if (test) {
				candidates.add(candidate)

			}
		}

		// If there are candidates, apply the matches
		if (generateLog && !candidates.empty)
			log = log + '''- __Step 5 - «candidates.fold(0)[v, c|c.matchTable.values.flatten.size + v]» matches__''' +
				"\n>"
		for (candidate : candidates) {
			if (generateLog)
				log = log +
					'''«FOR match : candidate.matchTable.values.flatten.toList SEPARATOR ', '»«match»«ENDFOR», '''
			applyDivisionMatch(candidate,
				candidate.matchTable.values.flatten.filter[it.type == MatchType::BACKWARD].toList)
		}
		if(generateLog && !candidates.empty) log = log + '\n'

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
	def private processGroupStep6(List<Buffer> buffers) {

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
		if(generateLog && !candidates.empty) log = log + '''- __Step 6 - «candidates.size» matches__''' + "\n>"
		for (candidate : candidates.entrySet) {
			if (generateLog)
				log = log +
					'''«candidate.key.matchTable.values.flatten.filter[it.type == candidate.value].head»  '''
			candidate.key.applyMatches(
				#[candidate.key.matchTable.values.flatten.filter[it.type == candidate.value].head])
		}
		if(generateLog && !candidates.empty) log = log + '\n'

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
	def private processGroupStep7(List<Buffer> buffers) {
		val candidates = newLinkedHashMap

		for (candidate : buffers) {
			val iterType = #[MatchType::FORWARD, MatchType::BACKWARD].iterator
			var test = false
			while (iterType.hasNext && !test) {
				val currentType = iterType.next

				val matches = candidate.matchTable.values.flatten.filter[it.type == currentType].toList

				// Returns true if:
				// Has a several matches
				test = matches.size != 0

				// is divisible
				test = test && candidate.divisible

				// and is involved in conflicting match
				test = test && matches.forall[
					it.conflictingMatches.size != 0 && it.applicable && it.reciprocate.applicable]

				// Unless the matches are backward AND the buffer is mergeable
				// the matches must not be conflicting with each other
				test = test && ({ // the matches are backward AND the buffer is mergeable
					currentType == MatchType::BACKWARD && candidate.mergeableRanges.size == 1 &&
						candidate.mergeableRanges.head.start == candidate.minIndex &&
						candidate.mergeableRanges.head.end == candidate.maxIndex
				} || // the matches must not be conflicting with each other
				matches.forall [
					it.conflictingMatches.forall[!matches.contains(it)]
				])

				// Matches have no multiple match Range (on the local buffer side).
				test = test && Buffer.getOverlappingRanges(matches).size == 0

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
		if (generateLog && !candidates.empty)
			log = log + '''- __Step 7 - «candidates.entrySet.fold(0)[v, c|
				c.key.matchTable.values.flatten.filter[it.type == c.value].size + v]» matches__ ''' + "\n>"
		for (candidate : candidates.entrySet) {
			if (generateLog)
				log = log +
					'''«FOR match : candidate.key.matchTable.values.flatten.filter[it.type == candidate.value].toList SEPARATOR ', '»«match»«ENDFOR», '''
			applyDivisionMatch(candidate.key,
				candidate.key.matchTable.values.flatten.filter[it.type == candidate.value].toList)
		}
		if(generateLog && !candidates.empty) log = log + '\n'

		buffers.removeAll(candidates.keySet)

		// Return the matched buffers
		return candidates.keySet.toList
	}

	new(int alignment) {
		// kdesnos: Data alignment is supposed to be equivalent
		// to no alignment from the script POV. (not 100% sure of this)
		this.alignment = if(alignment <= 0) -1 else alignment
		printTodo = false
	}

	/**
	 * Called only for divisible buffers with multiple match and no
	 * conflict that are not matched in another divisible buffer
	 */
	def void applyDivisionMatch(Buffer buffer, List<Match> matches) {

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
	 * {@link Buffer#matchWith(int,Buffer,int,int) match} is added between
	 * buffers of different actors that correspond to the same SDFEdges. This
	 * method must be called after {@link
	 * ScriptRunner#identifyDivisibleBuffer()} as it set to indivisible the
	 * buffers that are on the border of groups.
	 *
	 * @return a {@link List} of groups. Each group is itself a {@link List} of
	 *         {@link DAGVertex}.
	 */
	def List<List<DAGVertex>> groupVertices() {

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
		return groups
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
	def void run() throws EvalError {
		// For each vertex with a script
		for (e : scriptedVertices.entrySet) {
			runScript(e.key, e.value)
		}
	}

	def void runScript(DAGVertex dagVertex, File script) throws EvalError {
		val interpreter = new Interpreter();

		// TODO : isolate Interpreter initializatino
		val classManager = interpreter.getClassManager();
		classManager.cacheClassInfo("Buffer", Buffer);

		// Retrieve the corresponding sdf vertex
		val sdfVertex = dagVertex.getPropertyBean().getValue(DAGVertex.SDF_VERTEX, SDFAbstractVertex)

		// Create the vertex parameter list
		val Map<String, Integer> parameters = newLinkedHashMap
		val arguments = sdfVertex.propertyBean.getValue(SDFAbstractVertex.ARGUMENTS) as Map<String, Argument>
		if(arguments !== null) arguments.entrySet.forEach[parameters.put(it.key, it.value.intValue)]

		parameters.put("alignment", alignment)

		// Create the input/output lists
		val inputs = sdfVertex.incomingEdges.map[
			// An input buffer is backward mergeable if it is read_only OR if it is unused
			val isMergeable = (it.targetPortModifier ?: "").toString.contains(SDFEdge::MODIFIER_READ_ONLY) || ((it.
				targetPortModifier ?: "").toString.contains(SDFEdge::MODIFIER_UNUSED))
			try {
				new Buffer(it, dagVertex, it.targetLabel, it.cons.intValue, dataTypes.get(it.dataType.toString).size,
					isMergeable)
			} catch (NullPointerException exc) {
				throw new WorkflowException(
					'''SDFEdge «it.source.name»_«it.sourceLabel»->«it.target.name»_«it.targetLabel» has unknows type «it.
						dataType.toString». Add the corresponding data type to the scenario.''', exc)
			}].toList

		val outputs = sdfVertex.outgoingEdges.map[
			// An output buffer is mergeable if it is unused OR if its target port is read_only
			val isMergeable = (it.targetPortModifier ?: "").toString.contains(SDFEdge::MODIFIER_READ_ONLY) || ((it.
				targetPortModifier ?: "").toString.contains(SDFEdge::MODIFIER_UNUSED))
			try {
				new Buffer(it, dagVertex, it.sourceLabel, it.prod.intValue, dataTypes.get(it.dataType.toString).size,
					isMergeable)
			} catch (NullPointerException exc) {
				throw new WorkflowException(
					'''SDFEdge «it.source.name»_«it.sourceLabel»->«it.target.name»_«it.targetLabel» has unknows type «it.
						dataType.toString». Add the corresponding data type to the scenario.''', exc)
			}].toList

		// Import the necessary libraries
		interpreter.eval("import " + Buffer.name + ";")
		interpreter.eval("import " + List.name + ";")

		// Feed the parameters/inputs/outputs to the interpreter
		for (e : parameters.entrySet) {
			interpreter.set(e.key, e.value)
		}
		for (i : inputs) {
			interpreter.set("i_" + i.name, i)
		}
		for (o : outputs) {
			interpreter.set("o_" + o.name, o)
		}
		if (interpreter.get("parameters") === null)
			interpreter.set("parameters", parameters)
		if (interpreter.get("inputs") === null)
			interpreter.set("inputs", inputs)
		if (interpreter.get("outputs") === null)
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
			val logger = WorkflowLogger.getLogger
			logger.log(Level.WARNING,exception.message, exception)
		}
	}

	def void updateMEG(MemoryExclusionGraph meg) {

		// Create a new property in the MEG to store the merged memory objects
		val mergedMObjects = newLinkedHashMap
		meg.propertyBean.setValue(MemoryExclusionGraph::HOST_MEMORY_OBJECT_PROPERTY, mergedMObjects)

		// For each buffer, get the corresponding MObject
		val bufferAndMObjectMap = newLinkedHashMap
		for (buffers : bufferGroups) {
			for (buffer : buffers) {

				// Get the Mobj
				val mObjCopy = new MemoryExclusionVertex(buffer.sdfEdge.source.name, buffer.sdfEdge.target.name, 0)
				val mObj = meg.getVertex(mObjCopy)
				if (mObj === null) {
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
			for (buffer : buffers.filter[it.matched === null && it.host]) {

				// Enlarge the corresponding mObject to the required size
				val mObj = bufferAndMObjectMap.get(buffer)
				val minIndex = if (buffer.minIndex == 0 || alignment == -1) {
						buffer.minIndex
					} else {

						// Make sure that index aligned in the buffer are in
						// fact aligned
						// NB: at this point, the minIndex of the buffer is
						// either 0 or a negative number (if buffer were
						// matched before the range of real tokens of the
						// host). This division is here to make sure that
						// index 0 of the host buffer is still aligned !
						((buffer.minIndex / alignment) - 1) * alignment
					}
				mObj.setWeight(buffer.maxIndex - minIndex)

				// Add the mobj to the meg host list
				mergedMObjects.put(mObj, newLinkedHashSet)

				// Save the real token range in the Mobj properties
				val realTokenRange = new Range(0, buffer.tokenSize * buffer.nbTokens)
				val actualRealTokenRange = new Range(-minIndex, buffer.tokenSize * buffer.nbTokens - minIndex)
				val ranges = newArrayList
				ranges.add(mObj -> (realTokenRange -> actualRealTokenRange))
				mObj.setPropertyValue(MemoryExclusionVertex::REAL_TOKEN_RANGE_PROPERTY, ranges)
			}

			// For each matched buffers
			for (buffer : buffers.filter[it.matched !== null]) {

				// find the root buffer(s)
				// there might be several roots if the buffer was divided
				// the map associates:
				// a localRange of the buffer to
				// a pair of a root buffer and its range for the buffer
				val Map<Range, Pair<Buffer, Range>> rootBuffers = newLinkedHashMap()
				for (match : buffer.matched) {
					rootBuffers.putAll(match.root)
				}

				val mObj = bufferAndMObjectMap.get(buffer)

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
					if (remoteRange != translatedLocalRange) {

						// Should never be the case
						throw new RuntimeException("Unexpected error !")
					}
					mObjRoots.add(rootMObj -> (localRange -> remoteRange))
				]

				// If the mObj is a divided buffer
				if(rootBuffers.size > 1) {
					// Identify and all source and destination buffers in which
					// parts of the divided buffer are merged and store this
					// information in the mObject properties.
					// => This information will be used when allocating a
					// mObject in distributed memory to make sure that the
					// divided buffer remains accessible everywhere it is
					// needed, and otherwise forbid its division.
					val sourceAndDestBuffers = new ArrayList<Buffer>

					// buffers in which the divided buffer is mapped
					sourceAndDestBuffers += rootBuffers.values.map[it.key].toSet
					// buffers mapped in the divided buffer
					sourceAndDestBuffers += buffers.filter[it.appliedMatches.values.map[it.key].exists[it == buffer]]

					// Find corresponding mObjects
					var srcAndDestMObj = sourceAndDestBuffers.map[bufferAndMObjectMap.get(it)]

					// Save this list in the attributes of the divided buffer
					mObj.setPropertyValue(MemoryExclusionVertex.DIVIDED_PARTS_HOSTS,srcAndDestMObj)
				}

				// Sort mObjRoots in order of contiguous ranges
				mObjRoots.sortInplaceBy[it.value.key.start]
			}
		}

		// List of the unused and pureout memobjects
		val unusedMObjects = newArrayList

		// Find unusedMObjects that were not processed by the scripts.
		unusedMObjects.addAll(
			meg.vertexSet.filter [ mObj |
				if (mObj.edge !== null) {

					// Find unused write_only edges
					val aggregate = mObj.edge.aggregate
					aggregate.forall [ sdfEdge |
						((sdfEdge as SDFEdge).getPropertyStringValue(SDFEdge::SOURCE_PORT_MODIFIER) ?: "").
							contains(SDFEdge::MODIFIER_WRITE_ONLY) && ((sdfEdge as SDFEdge).
							getPropertyStringValue(SDFEdge::TARGET_PORT_MODIFIER) ?: "").contains(SDFEdge::MODIFIER_UNUSED)
					]
				} else
					false
			].filter [ mObj |
				// keep only those that are not host. (matched ones have already been removed from the MEG)
				val correspondingBuffer = bufferGroups.flatten.findFirst[mObj.edge.aggregate.contains(it.sdfEdge)]
				if (correspondingBuffer !== null) {
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
	def void simplifyResult(List<Buffer> inputs, List<Buffer> outputs) {
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
