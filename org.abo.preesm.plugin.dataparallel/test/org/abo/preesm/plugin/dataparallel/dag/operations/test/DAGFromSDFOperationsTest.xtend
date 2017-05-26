package org.abo.preesm.plugin.dataparallel.dag.operations.test

import java.util.Collection
import java.util.List
import java.util.Map
import org.abo.preesm.plugin.dataparallel.DAGConstructor
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGFromSDFOperations
import org.abo.preesm.plugin.dataparallel.test.Util
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.jgrapht.traverse.BreadthFirstIterator
import org.jgrapht.traverse.GraphIterator
import org.jgrapht.traverse.TopologicalOrderIterator
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.ArrayList

@RunWith(Parameterized)
class DAGFromSDFOperationsTest {
	
	val DAGConstructor dagGen
	val DAGFromSDFOperations dagOps
	val GraphIterator<SDFAbstractVertex, SDFEdge> iterator
	val Map<SDFAbstractVertex, List<SDFAbstractVertex>> instanceSources
	
	new(DAGConstructor dagGen, DAGFromSDFOperations dagOps, GraphIterator<SDFAbstractVertex, SDFEdge> iterator, Map<SDFAbstractVertex, List<SDFAbstractVertex>> instanceSources) {
		this.dagGen = dagGen
		this.dagOps = dagOps
		this.iterator = iterator
		this.instanceSources = instanceSources
	}
	
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList() 
		Util.provideAllGraphs
			.map[sdf | new SDF2DAG(sdf)]
			.forEach[dagGen | 
				val instance2Sources = newHashMap()
				new BreadthFirstIterator<SDFAbstractVertex, SDFEdge>(dagGen.outputGraph).forEach[node |
					instance2Sources.put(node, newArrayList())	
				]
				instance2Sources.forEach[node, sources|
					sources.addAll(dagGen.outputGraph.incomingEdgesOf(node)
					.map[edge | edge.source]
					.filter[source | instance2Sources.keySet.contains(source)]
					.toList)
				]
				
				parameters.add(#[dagGen, new DAGFromSDFOperations(dagGen), new TopologicalOrderIterator<SDFAbstractVertex, SDFEdge>(dagGen.outputGraph), instance2Sources])
			]
		return parameters
	}
	
	/**
	 * Check that the levels of source instances and its associated implode
	 * explode instances have level 0
	 */
	@Test
	public def void levelsOfRootIsZero() {
		val rootInstances = dagOps.rootInstances
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
		val instanceSources = newHashMap()
		dagGen.outputGraph.vertexSet.forEach[node | 
			instanceSources.put(node, dagGen.outputGraph.incomingEdgesOf(node).map[edge | edge.source].toList)
		]
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
					path.filter[source | !forkJoinOrigInstance.keySet.contains(source)].size
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
}