package org.abo.preesm.plugin.dataparallel.dag.operations

import java.util.List
import java.util.Map
import java.util.logging.Logger
import org.abo.preesm.plugin.dataparallel.DAGTopologicalIteratorInterface
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import java.util.logging.Level
import java.util.Collections
import java.util.Collection
import java.util.Set
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor

abstract class AbstractDAGCommonOperations implements DAGCommonOperationsImplAssistant {
	
	/**
	 * Holds the original DAGConstructor instance
	 */	
	protected var PureDAGConstructor dagGen
	
	/**
	 * Holds the original DAG, extracted from dagGen
	 */
	protected var SDFGraph inputGraph
	
	/**
	 * Optional logging
	 */
	protected var Logger logger
	
	/**
	 * Look up table for instances and its source instances
	 */
	protected var Map<SDFAbstractVertex, List<SDFAbstractVertex>> instanceSources
	
	/**
	 * The topological iterator used. Could be TopologicalOrderIterator or
	 * SubsetTopologicalIterator
	 */
	protected var DAGTopologicalIteratorInterface iterator
	
	/**
	 * Relevant nodes seen in the DAG or its subset
	 */
	protected var List<SDFAbstractVertex> seenNodes
	
	/**
	 * The map of explode and implode instances linked to its original instance
	 * This is extracted from dagGen
	 */
	protected var Map<SDFAbstractVertex, SDFAbstractVertex> forkJoinOrigInstance
	
	/**
	 * List of root instances
	 */
	protected var List<SDFAbstractVertex> rootInstances
	
	/**
	 * List of root actors
	 */
	protected var List<SDFAbstractVertex> rootActors
	
	/**
	 * List of exit nodes
	 */
	protected var List<SDFAbstractVertex> exitInstances
	
	/**
	 * Set of non-data parallel actors
	 */
	protected val Set<SDFAbstractVertex> nonParallelActors
	
	/**
	 * Flag to avoid recomputing dag independences. Its an
	 * expensive operation
	 */
	protected var boolean computeDAGInd
	
	/**
	 * Look up table of instances and its levels
	 */
	protected val Map<SDFAbstractVertex, Integer> levels
	
	/**
	 * Flag that checks if DAG is instance independent
	 */
	protected var boolean dagInd
	
	/**
	 * Constructor for plugin
	 * 
	 * @param dagGen A {@link PureDAGConstructor} instance
	 * @param logger Workflow Logger
	 */
	new(PureDAGConstructor dagGen, Logger logger) {
		this.dagGen = dagGen
		this.logger = logger
		this.inputGraph = dagGen.outputGraph
		this.levels = newHashMap()
		this.dagInd = false
		this.nonParallelActors = newHashSet()
	}
	
	/**
	 * Constructor used for test setup
	 * 
	 * @param dagGen A {@link PureDAGConstructor} instance
	 */
	new(PureDAGConstructor dagGen) {
		this(dagGen, null)
	}
	
	/**
	 * Initialize the appropriate data-structures
	 * by calling appropriate compute functions in the required
	 * order
	 */
	override initialize() {
		computeRootInstances
		computeRootActors
		computeExitInstances
		computeAllLevels
	}
	
	/**
	 * Optionally log message
	 * 
	 * @param message The message to log
	 */
	protected def void log(String message) {
		logger?.log(Level.INFO, message)
	}
	
	/**
	 * Compute root instances
	 */
	protected def void computeRootInstances() {
		rootInstances = inputGraph.vertexSet
			.filter[instance | getSeenNodes.contains(instance)]
			.filter[instance | inputGraph.incomingEdgesOf(instance).size == 0].toList
	}
	
	/**
	 * Get exit instances of the DAG. 
	 * Exit instances are those instances of the DAG that have no outgoing edges 
	 * and are not root instances
	 * 
	 * @return Unmodifiable List of exit instances
	 */	
	public override getRootInstances() {
		return Collections.unmodifiableList(rootInstances)
	}
	
	/**
	 * Compute root actors
	 * The computation leverages the fact that only those instances
	 * seen in a DAG is considered. Hence, the implementation is safe
	 * for a pure DAG as well as a subset of a DAG
	 */
	protected def void computeRootActors() {
		rootActors = getRootInstances().map[instance | dagGen.instance2Actor.get(instance)].toSet.toList
	}
	
	public override getRootActors() {
		return Collections.unmodifiableList(rootActors)
	}
	
	/**
	 * Compute exit instances
	 * The computation leverages the fact that only those instances
	 * seen in a DAG is considered. Hence, the implementation is safe
	 * for a pure DAG as well as a subset of a DAG
	 */
	protected def void computeExitInstances() {
		val rootInstances = getRootInstances()
		exitInstances = inputGraph.vertexSet
				.filter[instance | getSeenNodes.contains(instance)]
				.filter[instance |
			 		inputGraph.outgoingEdgesOf(instance).size == 0 && !rootInstances.contains(instance)
				].toList
	}
	
	/**
	 * Get exit instances of the DAG. 
	 * Exit instances are those instances of the DAG that have no outgoing edges 
	 * and are not root instances
	 * 
	 * @return Unmodifiable List of exit instances
	 */ 
	public override getExitInstances() {
		return Collections.unmodifiableList(exitInstances)
	}
	
	/**
	 * Compute all the levels. 
	 * The computation leverages the fact that only those instances
	 * seen in a DAG is considered. Hence, the implementation is safe
	 * for a pure DAG as well as a subset of a DAG
	 */
	protected def void computeAllLevels() {
		inputGraph.vertexSet
			.filter[instance | getSeenNodes.contains(instance)]
			.forEach[instance | levels.put(instance, new Integer(0))]
		while(getIterator.hasNext()) {
			val seenNode = getIterator.next()
			val predecessors = getInstanceSources.get(seenNode)
			if(predecessors.isEmpty) {
				levels.put(seenNode, new Integer(0))
			} else {
				val predecessorLevel = levels.filter[node, value | predecessors.contains(node)].values.max
				if(getForkJoinOrigInstance.keySet.contains(seenNode)) {
					levels.put(seenNode, new Integer(predecessorLevel))
				} else {
					levels.put(seenNode, new Integer(predecessorLevel+1))
				}
			}
		}
		
		// Properly set the implode and explode according to the level of its original
		levels.keySet.forEach[node |
			if(getForkJoinOrigInstance.keySet.contains(node)) 
				levels.put(node, levels.get(getForkJoinOrigInstance.get(node)))
		]
	}
	
	/**
	 * Get all the levels of the nodes as a lookup table. Considers implode and 
	 * explode instance of an actor to be at the same level of its respective
	 * instance 
	 * 
	 * @return Unmodifiable lookup table of levels of nodes
	 */
	public override Map<SDFAbstractVertex, Integer> getAllLevels() {
		return Collections.unmodifiableMap(levels)
	}
	
	/**
	 * Get maximum level of a given table of levels
	 * 
	 * @param ls Lookup table consisting of instance to level mapping
	 * @return Maximum of all the levels of the instances
	 */
	protected def int getMaxLevel(Map<SDFAbstractVertex, Integer> ls) {
		return (ls.values.max + 1)
	}
	
	/**
	 * Get the maximum depth/level of the graph. 
	 * Does not generic DAGs. Need a specific instance
	 * 
	 * @return Maximum level of the graph
	 */
	public override getMaxLevel() {
		return getMaxLevel(levels)
	}
	
	/**
	 * Compute level sets for a given table of levels
	 * 
	 * @param ls Lookup table consisting of instance to level mapping
	 * @return Level Set corresponding to the input levels
	 */
	protected def List<List<SDFAbstractVertex>> getLevelSets(Map<SDFAbstractVertex, Integer> ls) {
		val List<List<SDFAbstractVertex>> levelSet = newArrayList()
		(0..<getMaxLevel(ls)).forEach[levelSet.add(newArrayList)]
		ls.forEach[instance, level | 
			levelSet.get(level).add(instance)
		]
		return levelSet
	}
	
	/**
	 * Get all the level sets for a given DAG. A level set is a set of all instances
	 * seen at a particular level. Instead of set, we represent them as a list for
	 * space saving reason. The index of the level set, in this way, represents the level
	 * at which the set exists. 
	 * 
	 * @return Unmodifiable list of lists of instances seen at the level given by the index of the outer list
	 */
	public override List<List<SDFAbstractVertex>> getLevelSets() {
		return getLevelSets(getAllLevels)
	}
	
	/**
	 * Check if the DAG is data-parallel. A data-parallel DAG has a
	 * level set where all instances of an actor are contained in
	 * the same set
	 * 
	 * @return True if DAG is data-parallel as well
	 */
	override SDFAbstractVertex pickElement(Collection<SDFAbstractVertex> set) {
		val itr = set.iterator
		return itr.next
	}
}