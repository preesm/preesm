package org.abo.preesm.plugin.dataparallel.dag.operations.test

import java.util.Collection
import org.abo.preesm.plugin.dataparallel.DAGConstructor
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGFromSDFOperations
import org.abo.preesm.plugin.dataparallel.test.Util
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized)
class DAGFromSDFOperationsTest {
	
	val DAGConstructor dagGen
	val DAGFromSDFOperations dagOps
	
	new(DAGConstructor dagGen, DAGFromSDFOperations dagOps) {
		this.dagGen = dagGen
		this.dagOps = dagOps
	}
	
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList() 
		Util.provideAllGraphs
			.map[sdf | new SDF2DAG(sdf)]
			.forEach[dagGen | 
				parameters.add(#[dagGen, new DAGFromSDFOperations(dagGen)])
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
}