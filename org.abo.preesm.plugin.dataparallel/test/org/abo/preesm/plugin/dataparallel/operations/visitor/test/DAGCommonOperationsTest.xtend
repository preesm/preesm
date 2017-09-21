package org.abo.preesm.plugin.dataparallel.operations.visitor.test

import java.util.ArrayList
import java.util.Collection
import org.abo.preesm.plugin.dataparallel.DAG2DAG
import org.abo.preesm.plugin.dataparallel.DAGConstructor
import org.abo.preesm.plugin.dataparallel.DAGSubset
import org.abo.preesm.plugin.dataparallel.DAGSubsetConstructor
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.iterator.DAGTopologicalIterator
import org.abo.preesm.plugin.dataparallel.iterator.DAGTopologicalIteratorInterface
import org.abo.preesm.plugin.dataparallel.iterator.SubsetTopologicalIterator
import org.abo.preesm.plugin.dataparallel.operations.graph.KosarajuStrongConnectivityInspector
import org.abo.preesm.plugin.dataparallel.operations.visitor.DAGCommonOperations
import org.abo.preesm.plugin.dataparallel.operations.visitor.DAGOperations
import org.abo.preesm.plugin.dataparallel.operations.visitor.DependencyAnalysisOperations
import org.abo.preesm.plugin.dataparallel.operations.visitor.LevelsOperations
import org.abo.preesm.plugin.dataparallel.operations.visitor.OperationsUtils
import org.abo.preesm.plugin.dataparallel.operations.visitor.RootExitOperations
import org.abo.preesm.plugin.dataparallel.test.Util
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex
import org.jgrapht.alg.CycleDetector
import org.jgrapht.graph.DirectedSubgraph
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Perform property based tests for operations that
 * derive from {@link DAGCommonOperations} on instances
 * that implement {@link DAGConstructor}
 * 
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
class DAGCommonOperationsTest {
	protected val DAGConstructor dagGen
	
	protected val DAGTopologicalIteratorInterface iterator
	
	protected val SDFAbstractVertex rootNode 
	
	protected val boolean isParallel
	
	protected val boolean isBranchSetCompatible
	
	protected val Boolean isInstanceIndependent
	
	new(DAGConstructor dagGen
		, DAGTopologicalIteratorInterface iterator
		, SDFAbstractVertex rootNode
		, Boolean isInstanceIndependent
		, boolean isParallel
		, boolean isBranchSetCompatible) {
		this.dagGen = dagGen
		this.iterator = iterator
		this.rootNode = rootNode
		this.isParallel = isParallel
		this.isInstanceIndependent = isInstanceIndependent
		this.isBranchSetCompatible = isBranchSetCompatible	
	}
	
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		/* Contains the following parameters
		 * 1. DAGConstructor
		 * 2. Iterator that completely traverses this DAG
		 * 3. Root node with which subset was created. Null if none
		 * 4. If the DAG is data parallel as well
		 * 5. If the graph is compatible with computation of branch set as outlined in DASIP 2017 paper
		 */
		val parameters = newArrayList
		
		// Add all SDF2DAG instances. Additionally, none of them are parallel
		Util.provideAllGraphsContext
			.forEach[sdfContext |
				val dagGen = new SDF2DAG(sdfContext.graph)
				val iterator  = new DAGTopologicalIterator(dagGen)
				parameters.add(#[dagGen
								, iterator
								, null
								, sdfContext.isInstanceIndependent
								, false
								, sdfContext.isBranchSetCompatible
				])
			]
		
		// Create new DAG2DAG instances and add all of them. Additionally, none of them are parallel
		Util.provideAllGraphsContext
			.forEach[sdfContext |
				val dagGen = new SDF2DAG(sdfContext.graph)
				val iterator = new DAGTopologicalIterator(dagGen)
				parameters.add(#[new DAG2DAG(dagGen)
								, iterator
								, null
								, sdfContext.isInstanceIndependent
								, false
								, sdfContext.isBranchSetCompatible])
			]
			
		// Add all subsets. They are naturally parallel
		Util.provideAllGraphsContext
			.forEach[sdfContext |
				val dagGen = new SDF2DAG(sdfContext.graph)
				// Get root nodes
				var rootVisitor = new RootExitOperations
				dagGen.accept(rootVisitor)
				var rootInstances = rootVisitor.rootInstances
				
				// Add subsets created from SDF2DAG
				rootInstances.forEach[rootNode |
					val iterator = new SubsetTopologicalIterator(dagGen, rootNode)
					parameters.add(#[new DAGSubset(dagGen, rootNode)
									, iterator
									, rootNode
									, null
									, true
									, sdfContext.isBranchSetCompatible])
				]
				
				// Add subsets created from DAG2DAG
				val dag2Dag = new DAG2DAG(dagGen)
				rootVisitor = new RootExitOperations
				dag2Dag.accept(rootVisitor)
				rootInstances = rootVisitor.rootInstances
				rootInstances.forEach[rootNode |
					val iterator = new SubsetTopologicalIterator(dag2Dag, rootNode)
					parameters.add(#[new DAGSubset(dag2Dag, rootNode)
									, iterator
									, rootNode
									, null
									, true
									, sdfContext.isBranchSetCompatible])
				]
			]
			
			// Test on subgraphs
			Util.provideAllGraphsContext.forEach[sdfContext |
				val sdf = sdfContext.graph
					
				// Get strongly connected components
				val strongCompDetector = new KosarajuStrongConnectivityInspector(sdf)
				
				// Collect strongly connected component that has loops in it
				// Needed because stronglyConnectedSubgraphs also yield subgraphs with no loops
				strongCompDetector.stronglyConnectedComponents.forEach[ subgraph |
					val cycleDetector = new CycleDetector(subgraph as
						DirectedSubgraph<SDFAbstractVertex, SDFEdge>)
					val subgraphDir = subgraph as DirectedSubgraph<SDFAbstractVertex, SDFEdge>
					if(cycleDetector.detectCycles) {
						// ASSUMPTION: Strongly connected component of a directed graph contains atleast
						// one loop
						val dagGen = new SDF2DAG(subgraphDir)
						
						// Test SDF2DAG
						parameters.add(#[dagGen
										, new DAGTopologicalIterator(dagGen)
										, null
										, sdfContext.isInstanceIndependent
										, false
										, sdfContext.isBranchSetCompatible
						])
						
						// Test DAG2DAG
						parameters.add(#[new DAG2DAG(dagGen)
										, new DAGTopologicalIterator(dagGen)
										, null
										, sdfContext.isInstanceIndependent
										, false
										, sdfContext.isBranchSetCompatible
						])
					}
				]
			]
		
		return parameters
	}
	
	def dispatch void acceptVisitor(PureDAGConstructor dagGen, DAGOperations visitor) {
		dagGen.accept(visitor)
	}
	
	def dispatch void acceptVisitor(DAGSubsetConstructor dagGen, DAGCommonOperations visitor) {
		dagGen.accept(visitor)	
	}
	
	/**
	 * All source Instances are root instances, but not vice versa.
	 * 
	 * Weak Test
	 */
	@Test
	public def void sourceInstancesAreRootInstances() {
		val rootOp = new RootExitOperations
		acceptVisitor(dagGen, rootOp)
		val rootInstances = rootOp.rootInstances
		val sourceInstances = dagGen.sourceInstances.filter(source | !dagGen.explodeImplodeOrigInstances.keySet.contains(source))
		sourceInstances.forEach[source |
			Assert.assertTrue(rootInstances.contains(source))
		]
	}
	
	/**
	 * All sink instances apart from those in root, are exit instances, but not vice versa
	 * 
	 * Weak Test
	 */
	@Test
	public def void sinkInstancesAreExitInstances() {
		val rootExitOp = new RootExitOperations
		acceptVisitor(dagGen, rootExitOp)
		val rootInstances = rootExitOp.rootInstances
		val exitInstances = rootExitOp.exitInstances
		val sinkInstances = dagGen.sinkInstances
			.filter(sink | !dagGen.explodeImplodeOrigInstances.keySet.contains(sink))
			.filter(sink | !rootInstances.contains(sink))
		sinkInstances.forEach[sink |
			Assert.assertTrue(exitInstances.contains(sink))
		]
	}
	
	/**
	 * All instances of source actor should be in root
	 * This works only if DAG is not subset DAG
	 * 
	 * Strong Test
	 */
	@Test
	public def void allInstanceOfSourceAreInRoot() {
		if(dagGen instanceof SDF2DAG) {
			val rootOp = new RootExitOperations
			acceptVisitor(dagGen, rootOp)
			val rootInstances = rootOp.rootInstances
			val sourceActors = dagGen.sourceActors
			sourceActors.forEach[actor |
				val sourceInstances = dagGen.actor2Instances.get(actor).filter(instance | !dagGen.explodeImplodeOrigInstances.keySet.contains(instance))
				sourceInstances.forEach[source |
					Assert.assertTrue(rootInstances.contains(source))
				]
			]
		}
	}
	
	/** All instances of sink should be in exit. This only occurs,
	 * if the sink is not source as well. Also, this works only if
	 * DAG is not subset DAG
	 * 
	 * Strong test
	 */
	@Test
	public def void allInstancesOfSinkAreInExit() {
		if(dagGen instanceof SDF2DAG) {
			val rootExitOp = new RootExitOperations
			acceptVisitor(dagGen, rootExitOp)
			val rootInstances = rootExitOp.rootInstances
			val exitInstances = rootExitOp.exitInstances
			val sourceActors = dagGen.sourceActors
			val sinkActors = dagGen.sinkActors.filter[actor | !sourceActors.contains(actor)]
			sinkActors.forEach[actor | 
				val sinkInstances = dagGen.actor2Instances.get(actor)
						.filter(instance | !dagGen.explodeImplodeOrigInstances.keySet.contains(instance))
						.filter(instance | !rootInstances.contains(instance))
				sinkInstances.forEach[sink |
					Assert.assertTrue(exitInstances.contains(sink))
				]
			]
		}
	}
	
	/**
	 * Root instances have no implode instances and exit instances
	 * have no explode instances. Reverse is not true
	 * 
	 * Weak test
	 */
	@Test
	public def void rootNoImplodeExitNoExplode() {
		val rootExitOp = new RootExitOperations
		acceptVisitor(dagGen, rootExitOp)
		val rootInstances = rootExitOp.rootInstances
		val exitInstances = rootExitOp.exitInstances
		
		rootInstances.forEach[instance |
			val implodes = dagGen.explodeImplodeOrigInstances.filter[ impExp, origInstance |
				origInstance == instance && (impExp instanceof SDFJoinVertex)
			]
			Assert.assertTrue(implodes.empty)
		]
		
		exitInstances.forEach[instance |
			val explodes = dagGen.explodeImplodeOrigInstances.filter[impExp, origInstance |
				origInstance == instance && (impExp instanceof SDFForkVertex)
			]
			Assert.assertTrue(explodes.empty)
		]
	}

	/**
	 * Check that the levels of source instances and its associated implode
	 * explode instances have level 0
	 * 
	 * Strong test
	 */
	@Test
	public def void levelsOfRootIsZero() {
		// Get root instances and its associated implode and explode
		val rootOp = new RootExitOperations
		acceptVisitor(dagGen, rootOp)
		val allRootInstances = rootOp.rootInstances
		allRootInstances.addAll(dagGen.explodeImplodeOrigInstances.filter[explodeImplode, instance |
			allRootInstances.contains(instance)
		].keySet)
		
		// Get the levels
		val levelVisitor = new LevelsOperations
		acceptVisitor(dagGen, levelVisitor)
		val allLevels = levelVisitor.levels
		
		// Test if all the root instances belong to level 0
		allRootInstances.forEach[instance |
			Assert.assertEquals(allLevels.get(instance), 0)
		]
		
		// Conversely, test if all level 0 are root instances
		allLevels.forEach[instance, level |
			if(level == 0) Assert.assertTrue(allRootInstances.contains(instance))
		]
	}
	
	/**
	 * The test checks if the lookup table of instanceSource created by the iterator
	 * is valid. 
	 * 
	 * The test checks if the levels of the sources of a given node is less than
	 * the level of the node
	 * 
	 * Weak test
	 */
	@Test
	public def void levelsOfSourcesLessThanCurrent() {
		// Gather all relevant data-structures
		
		val forkJoinOrigInstance = dagGen.explodeImplodeOrigInstances
		// Get sources of all the instances
		val levelOp = new LevelsOperations
		acceptVisitor(dagGen, levelOp)
		val allLevels = levelOp.levels
		val instanceSources = iterator.instanceSources
		
		// Now perform the check
		instanceSources.forEach[node, sources| 
			sources.forEach[source | 
				if(forkJoinOrigInstance.keySet.contains(source))
					if(forkJoinOrigInstance.keySet.contains(node))
						if(forkJoinOrigInstance.get(source) == forkJoinOrigInstance.get(node))
							Assert.assertTrue(allLevels.get(source) == allLevels.get(node))
						else
							Assert.assertTrue(allLevels.get(source) <= allLevels.get(node) - 1)
					else 
						if(forkJoinOrigInstance.get(source) == node)
							Assert.assertTrue(allLevels.get(source) <= allLevels.get(node))
						else
							Assert.assertTrue(allLevels.get(source) <= allLevels.get(node) - 1)
				else 
					if(forkJoinOrigInstance.keySet.contains(node))
						if(source == forkJoinOrigInstance.get(node))
							Assert.assertTrue(allLevels.get(source) <= allLevels.get(node))
						else
							Assert.assertTrue(allLevels.get(source) <= allLevels.get(node) - 1)
					else 
						Assert.assertTrue(allLevels.get(source) <= allLevels.get(node) - 1)
			]
		]
	}
	
	/**
	 * Verify the calculation of level set using branch set. A branch set is the collection
	 * of all the paths from the node to all of its root node. Once we have a branch set,
	 * we can compute the level of the node by taking the maximum number of nodes seen in the
	 * path. Computing branch sets becomes intractable for large graphs, so although this was
	 * the way level sets are defined in the literature, we compute it in other way. This test
	 * verifies both way of computation gives same results.
	 * 
	 * Strong Test 
	 * 
	 * Warning! Computing branch sets can result in memory overflow for large graph (stereo vision application)
	 * 
	 * Branch sets are calculated by keeping track of all the predecessors and inserting the current node in its
	 * path (memoization). This technique is outlined in DASIP 2017 paper
	 */
	 @Test
	public def void instancesInEachPathAreInCorrectLevels() {
		if(isBranchSetCompatible) {
			val forkJoinOrigInstance = dagGen.explodeImplodeOrigInstances
		 	val instanceSources = iterator.instanceSources
		 	val levelOp = new LevelsOperations
		 	acceptVisitor(dagGen, levelOp)
		 	val allLevels = levelOp.levels
		 	val instance2Paths = newHashMap // Holds the predecessors seen for each node
		 	val newLevels = newHashMap // The new levels are stored here
		 	
		 	// Compute levels seen at each node using maximum number of instances seen in
		 	// previous paths
	 		iterator.forEach[node | 
				val sourceList = instanceSources.get(node)
				newLevels.put(node, 0)
				if(sourceList.isEmpty) {
					val paths = #[#[node]]
					instance2Paths.put(node, paths)
				} else {
					val newPaths = newArrayList
					sourceList.forEach[source |
						instance2Paths.get(source).forEach[path |
							val newPath = new ArrayList(path)
							newPath.add(node)
							newPaths.add(newPath)
						] 
					]
					instance2Paths.put(node, newPaths)
					// remember that each path contains the current node as well
					newLevels.put(node, newPaths.map[path | 
						path.filter[source |
							!forkJoinOrigInstance.keySet.contains(source)
						].size
					].max - 1) 
				}
			]
		
			// Now adjust the levels of implode and explodes
			newLevels.forEach[node, level| 
				if(forkJoinOrigInstance.keySet.contains(node))
					newLevels.put(node, newLevels.get(forkJoinOrigInstance.get(node)))
			]
		
			// Now check if calculation done in this way is same as computed levels
			newLevels.forEach[node, level|
				Assert.assertEquals(level, allLevels.get(node))
			]	
		}
	 }
	 
	/**
	 * Check if each DAG and its subsets are DAG independent.
	 * We calculate branch set as outlined in previous test. If an instance
	 * dependency is found in a set, then the actor is non parallel. Calculating
	 * branch set can becoming expensive very soon. So the actual calculation is
	 * done only using level set information. This test compares both approach. 
	 * If this test passes, then both DAGSubsetOperations as well as 
	 * DAGFromSDFOperations must be true
	 * 
	 * Strong test
	 * 
	 * Warning! Branch set calculation can blow up for complicated graphs (with
	 * too many branches per instances like broadcast). This technique is outlined in DASIP 2017 paper
	 */
	@Test
	public def void establishDagIndependenceUsingBranchSets() {
		if(isBranchSetCompatible) {
			// Populate all the necessary data-structures
			val instanceSources = iterator.instanceSources
			val forkJoinInstance = dagGen.explodeImplodeOrigInstances.keySet
			val depOp = new DependencyAnalysisOperations
			acceptVisitor(dagGen, depOp)
			val nonParallelActorsOrig = depOp.instanceDependentActors
			val isDAGInd = depOp.isIndependent
			val instance2Paths = newHashMap // Holds the predecessor levels of each node
			val nonParallelActors = newHashSet // Holds non-parallel actors
			val dagIndState = new ArrayList(#[true]) // Holds the state if DAG is instance independent
			
			iterator.forEach[node |
				val sourceList = instanceSources.get(node)
				val actor = dagGen.instance2Actor.get(node)
				if(sourceList.isEmpty) {
					val paths = #[#[node]]
					instance2Paths.put(node, paths)
				} else {
					val newPaths = newArrayList
						sourceList.forEach[source |
						instance2Paths.get(source).forEach[path |
							val actorsInPath = new ArrayList(path)
												.filter[instance | !forkJoinInstance.contains(instance)]
												.map[instance | dagGen.instance2Actor.get(instance)].toList
							if(!forkJoinInstance.contains(node) && actorsInPath.contains(actor)) {
								dagIndState.set(0, false)
								nonParallelActors.add(actor)	
							}
							val newPath = new ArrayList(path)
							newPath.add(node)
							newPaths.add(newPath)
						]
					]
					instance2Paths.put(node, newPaths)
				}
			]
			
			// Now check if DAGInd calculation is correct
			Assert.assertEquals(isDAGInd, dagIndState.get(0))
			Assert.assertEquals(nonParallelActorsOrig, nonParallelActors)	
		}
	}
	
	/**
	 * Cross checks if graphs are instance indpendent as well
	 * 
	 * Warning! The test is not generic. It depends on manually defined parameters
	 * 
	 * Strong test, Manually defined
	 */
	@org.junit.Test
	public def void checkDAGisInstanceIndependent() {
		if(isInstanceIndependent !== null && isInstanceIndependent) {
			val depOp = new DependencyAnalysisOperations
			acceptVisitor(dagGen, depOp)
			val isDAGInd = depOp.isIndependent
			Assert.assertEquals(isDAGInd, isInstanceIndependent)
		}
	}
	
	/**
	 * Cross checks if the assumption of data-parallelism due to 
	 * construction holds
	 * 
	 * Additionally make sure if a DAG is not independent, then it is not
	 * data-parallel
	 * 
	 * Warning! The test is not generic. It depends on a manually defined parameter
	 * 
	 * Strong Test, Manually defined
	 */
	@Test
	public def void checkDAGisDataParallel() {
		val depOp = new DependencyAnalysisOperations
		acceptVisitor(dagGen, depOp)
		val isDAGInd = depOp.isIndependent
		
		val levelsVisitor = new LevelsOperations
		acceptVisitor(dagGen, levelsVisitor)
		val levels = levelsVisitor.levels
		
		if(isDAGInd) {
			if(dagGen instanceof DAGSubset) {
				Assert.assertTrue(OperationsUtils.isParallel(dagGen, levels))
			} else {
				Assert.assertEquals(OperationsUtils.isParallel(dagGen, levels), isParallel)
			}
		} else {
			Assert.assertFalse(OperationsUtils.isParallel(dagGen, levels))
		}
	}
	 
}