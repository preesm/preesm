package org.abo.preesm.plugin.dataparallel.test

import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.dag.operations.GenericDAGOperations
import org.abo.preesm.plugin.dataparallel.DAGSubset
import org.abo.preesm.plugin.dataparallel.SubsetTopologicalIterator
import org.junit.Assert

class DAGSubsetTest {
	/**
	 * Check that the actor2Instances have actors and instances
	 * from the subset of DAG only. SubsetTopologicalIterator is 
	 * used to verify this fact
	 */
	@org.junit.Test
	public def void actorsHaveRightInstances() {
		Util.provideAllGraphs
		.map[sdf | new SDF2DAG(sdf)]
		.forEach[dagGen | 
			new GenericDAGOperations(dagGen).rootInstances.forEach[rootNode |
				val subsetActor2Instances = new DAGSubset(dagGen, rootNode).actor2Instances
				val seenNodes = new SubsetTopologicalIterator(dagGen, rootNode).instanceSources.keySet
				dagGen.actor2Instances.forEach[actor, instances|
					// Consider only those instances that are in the subset (found by iterator)
					val filteredInstances = instances.filter[instance | seenNodes.contains(instance)].toList
					if(filteredInstances.isEmpty){
						// Clearly, this actor and its instance was filtered out
						Assert.assertTrue(!subsetActor2Instances.keySet.contains(actor))
					} else {
						// Both actor should exist and their instances should be seen
						Assert.assertTrue(subsetActor2Instances.keySet.contains(actor))
						Assert.assertEquals(filteredInstances, subsetActor2Instances.get(actor))
					}
				]	
			]
		]
	}
	
	/**
	 * Check that instance2Actor has instances and actors from the subset of DAG only.
	 * SubsetTopologicalIterator is used to verify 
	 */
	@org.junit.Test
	public def void instancesHaveRightActors() {
		Util.provideAllGraphs
		.map[sdf | new SDF2DAG(sdf)]
		.forEach[dagGen |
			new GenericDAGOperations(dagGen).rootInstances.forEach[rootNode |
				val subsetInstance2Actor = new DAGSubset(dagGen, rootNode).instance2Actor
				val seenNodes = new SubsetTopologicalIterator(dagGen, rootNode).instanceSources.keySet
				dagGen.instance2Actor.forEach[instance, actor |
					// Consider only those instances that are in the subset (found by iterator)
					if(seenNodes.contains(instance)){
						// If instance is seen, then it should be in the lookup table as well
						Assert.assertTrue(subsetInstance2Actor.keySet.contains(instance))
						Assert.assertEquals(subsetInstance2Actor.get(instance), actor)
					} else {
						Assert.assertTrue(!subsetInstance2Actor.keySet.contains(instance))
					}
				]
			]
		]
	}
}