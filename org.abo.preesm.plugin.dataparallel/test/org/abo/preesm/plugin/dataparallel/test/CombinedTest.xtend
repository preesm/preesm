package org.abo.preesm.plugin.dataparallel.test

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGOperations
import java.util.Collection
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGFromSDFOperations
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGSubsetOperations
import org.abo.preesm.plugin.dataparallel.DAGSubset
import org.junit.Assert
import org.abo.preesm.plugin.dataparallel.DAGConstructor

/**
 * All the tests on DAGConverter that also needs other classes from
 * dag.operations for verification
 */
@RunWith(Parameterized)
class CombinedTest {
	protected val SDFGraph sdf
	
	protected val DAGConstructor dagGen
	
	protected val DAGOperations dagOps
	
	new (SDFGraph sdf, DAGConstructor dagGen, DAGOperations dagOps) {
		this.sdf = sdf
		this.dagGen = dagGen
		this.dagOps = dagOps	
	}
	
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList()
		
		// Provide pure SDF graphs
		Util.provideAllGraphs
			.forEach[sdf |
				val dagGen = new SDF2DAG(sdf)
				parameters.add(
					#[sdf, dagGen, new DAGFromSDFOperations(dagGen)]
				)
			]
			
		// Provide various subsets of pure SDF graphs
		Util.provideAllGraphs
			.forEach[sdf |
				val dagGen = new SDF2DAG(sdf)
				new DAGFromSDFOperations(dagGen).rootInstances.forEach[rootNode |
					parameters.add(
						#[sdf, new DAGSubset(dagGen, rootNode), new DAGSubsetOperations(dagGen, rootNode)]
					)
				]
			]
		
		return parameters
	}
	
	/**
	 * All source Instances are root instances, but not vice versa.
	 * 
	 * Weak Test
	 */
	@org.junit.Test
	public def void sourceInstancesAreRootInstances() {
		val rootInstances = dagOps.rootInstances
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
	@org.junit.Test
	public def void sinkInstancesAreExitInstances() {
		val exitInstances = dagOps.exitInstances
		val rootInstances = dagOps.rootInstances
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
	@org.junit.Test
	public def void allInstanceOfSourceAreInRoot() {
		if(dagGen instanceof SDF2DAG) {
			val rootInstances = dagOps.rootInstances
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
	@org.junit.Test
	public def void allInstancesOfSinkAreInExit() {
		if(dagGen instanceof SDF2DAG) {
			val sourceActors = dagGen.sourceActors
			val sinkActors = dagGen.sinkActors.filter[actor | !sourceActors.contains(actor)]
			val exitInstances = dagOps.exitInstances
			sinkActors.forEach[actor | 
				val rootInstances = dagOps.rootInstances
				val sinkInstances = dagGen.actor2Instances.get(actor)
						.filter(instance | !dagGen.explodeImplodeOrigInstances.keySet.contains(instance))
						.filter(instance | !rootInstances.contains(instance))
				sinkInstances.forEach[sink |
					Assert.assertTrue(exitInstances.contains(sink))
				]
			]
		}
	}
}