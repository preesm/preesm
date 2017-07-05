package org.abo.preesm.plugin.dataparallel.dag.operations.test

import java.util.ArrayList
import java.util.Collection
import java.util.List
import java.util.Map
import org.abo.preesm.plugin.dataparallel.DAGConstructor
import org.abo.preesm.plugin.dataparallel.DAGSubset
import org.abo.preesm.plugin.dataparallel.DAGTopologicalIterator
import org.abo.preesm.plugin.dataparallel.DAGTopologicalIteratorInterface
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.SubsetTopologicalIterator
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGCommonOperations
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGOperationsImpl
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGSubsetOperations
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGSubsetOperationsImpl
import org.abo.preesm.plugin.dataparallel.test.Util
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Test setup for implementations of DAGCommonOperations
 * 
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
public class DAGCommonOperationsTest {
	protected val DAGConstructor dagGen
	
	protected val DAGCommonOperations dagOps
	
	protected val Map<SDFAbstractVertex, List<SDFAbstractVertex>> instanceSources
	
	protected val DAGTopologicalIteratorInterface iterator
	
	protected val SDFAbstractVertex rootNode
	
	new(DAGConstructor dagGen, DAGCommonOperations dagOps, DAGTopologicalIteratorInterface iterator, Map<SDFAbstractVertex, List<SDFAbstractVertex>> instanceSources, SDFAbstractVertex rootNode) {
		this.dagGen = dagGen
		this.dagOps = dagOps
		this.iterator = iterator
		this.instanceSources = instanceSources
		this.rootNode = rootNode
	}
	
	/**
	 * Construct parameters for testing all the implementations of DAGCommonOperations
	 */
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList() 
		Util.provideAllGraphs
			.map[sdf | new SDF2DAG(sdf)]
			.forEach[dagGen | 
				val iterator = new DAGTopologicalIterator(dagGen)
				parameters.add(#[dagGen, new DAGOperationsImpl(dagGen), iterator, iterator.instanceSources, null])
			]
			
		Util.provideAllGraphs
			.map[sdf | new SDF2DAG(sdf)]
			.forEach[dagGen |
				new DAGOperationsImpl(dagGen).getRootInstances.forEach[rootNode |
					val iterator = new SubsetTopologicalIterator(dagGen, rootNode)
					parameters.add(#[dagGen, new DAGSubsetOperationsImpl(dagGen, rootNode), iterator, iterator.instanceSources, rootNode])
				]
			]
		return parameters
	}
	
	/**
	 * Check that the levels of source instances and its associated implode
	 * explode instances have level 0
	 */
	@Test
	public def void levelsOfRootIsZero() {
		val rootInstances = new ArrayList(dagOps.rootInstances)
		rootInstances.addAll(dagGen.explodeImplodeOrigInstances.filter[explodeImplode, instance |
			rootInstances.contains(instance)
		].keySet)
		val allLevels = dagOps.allLevels
		// Test if all the root instances belong to level 0
		rootInstances.forEach[instance | 
			Assert.assertEquals(allLevels.get(instance), 0)
		]
		
		// Test if all the level 0 are root instances
		allLevels.forEach[instance, level |
			if(level == 0) Assert.assertTrue(rootInstances.contains(instance))
		]
	}
	
	/**
	 * Check that all the predecessor levels (except the source) have levels
	 * less than the current ones
	 */
	@Test
	public def void levelsOfSourcesLessThanCurrent() {
		val forkJoinOrigInstance = dagGen.explodeImplodeOrigInstances
		// Get sources of all the instances
		val allLevels = dagOps.allLevels
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
	 * Warning! Computing branch sets can result in memory overflow for large graph (stereo vision application)
	 * 
	 * Branch sets are calculated by keeping track of all the predecessors and inserting the current node in its
	 * path (memoization)
	 */
	@Test
	public def void instancesInEachPathAreInCorrectLevels() {
		val forkJoinOrigInstance = dagGen.explodeImplodeOrigInstances
		val instance2Paths = newHashMap() // holds the predecessors seen for each node
		val newLevels = newHashMap()  // The new levels are stored here
		// Compute levels seen at each node using maximum number of instances seen in
		// previous paths
		iterator.forEach[node | 
			val sourceList = instanceSources.get(node)
			newLevels.put(node, 0)
			if(sourceList.isEmpty) {
				val paths = #[#[node]]
				instance2Paths.put(node, paths)
			} else {
				val newPaths = newArrayList()
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
			Assert.assertEquals(level, dagOps.allLevels.get(node))
		]
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
	 * Warning! Branch set calculation can blow up for complicated graphs (with
	 * too many branches per instances like broadcast)
	 */
	@Test
	public def void establishDagIndependenceUsingBranchSets() {
		val instance2Paths = newHashMap() // Holds the predecessor levels of each node
		val nonParallelActors = newHashSet() // Holds non-parallel actors
		val dagIndState = new ArrayList(#[true]) // Holds the state of the dagInd
		val forkJoinInstance = dagGen.explodeImplodeOrigInstances.keySet 
		iterator.forEach[node |
			val sourceList = instanceSources.get(node)
			val actor = dagGen.instance2Actor.get(node)
			if(sourceList.isEmpty) {
				val paths = #[#[node]]
				instance2Paths.put(node, paths)
			} else {
				val newPaths = newArrayList()
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
		Assert.assertEquals(dagOps.isDAGInd, dagIndState.get(0))
		Assert.assertEquals(dagOps.getNonParallelActors, nonParallelActors)
	}
	
	/**
	 * Verifies that none of the DAGs are data-parallel
	 */
	@Test
	public def void noneOfDAGisDataParallel() {
		val seenActors = newHashSet()
		val instance2Actor = newHashMap
		if(dagOps instanceof DAGSubsetOperations) {
			instance2Actor.putAll(new DAGSubset(dagGen as SDF2DAG, rootNode).instance2Actor)
			if(dagOps.isDAGInd)
				Assert.assertTrue(dagOps.isDAGParallel)
			else
				Assert.assertFalse(dagOps.isDAGParallel)
		} else {
			instance2Actor.putAll(dagGen.instance2Actor)
			Assert.assertFalse(dagOps.isDAGParallel)
		}
		
		dagOps.levelSets.forEach[levelSet |
		val seenInLevel = newHashSet()
		levelSet.forEach[ instance |
				val actor = instance2Actor.get(instance)
				if(actor === null) {
					throw new RuntimeException("Bug!")
				}
				seenInLevel.add(actor)
				if(dagOps instanceof DAGSubsetOperations && dagOps.isDAGInd) {
					Assert.assertFalse(seenActors.contains(actor))
				}
			]
			seenActors.addAll(seenInLevel)
		]
	}
}