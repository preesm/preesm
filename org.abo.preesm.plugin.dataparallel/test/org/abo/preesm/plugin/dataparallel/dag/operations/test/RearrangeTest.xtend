package org.abo.preesm.plugin.dataparallel.dag.operations.test

import java.util.Collection
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGOperations
import org.abo.preesm.plugin.dataparallel.test.ExampleGraphs
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.jgrapht.alg.CycleDetector
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.abo.preesm.plugin.dataparallel.dag.operations.DAGOperationsImpl

/**
 * Setup to test re-timing transformations SDFG
 * We need a different setup here because unlike DAGOperationsTest
 * the expected values of some graphs vary with the graph in
 * question. i.e. This class does not perform property tests
 */
@RunWith(Parameterized)
class RearrangeTest {
	
	protected val SDF2DAG dagGen
	
	protected val DAGOperations dagOps
	
	protected val SDFGraph sdf
	
	/**
	 * Flag to distinguish SDF graphs that are to be tested with
	 * rearranging acyclic graphs
	 */
	protected val boolean isRearrangeAcyclic
	
	new(SDFGraph sdf, SDF2DAG dagGen, DAGOperations dagOps, Boolean isRearrangeAcyclic) {
		this.sdf = sdf
		this.dagGen = dagGen
		this.dagOps = dagOps
		this.isRearrangeAcyclic = isRearrangeAcyclic.booleanValue
	}
	
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList()
		val parameterArray = #[
			// [DAG from SDF, test acyclic rearranging?]
			#[ExampleGraphs.acyclicTwoActors, Boolean.TRUE],
			#[ExampleGraphs.semanticallyAcyclicCycle, Boolean.TRUE]
		]
		parameterArray.forEach[
			val sdf = it.get(0) as SDFGraph
			val dagGen = new SDF2DAG(sdf)
			parameters.add(#[sdf, dagGen, new DAGOperationsImpl(dagGen), it.get(1)])
		]
		
		// Graphs that are instance independent, but are not acyclic-like
		val cyclicParamterArray = #[
			// [DAG from SDF, test acyclic rearranging? (false)]
			#[ExampleGraphs.strictlyCyclic, Boolean.FALSE],
			#[ExampleGraphs.mixedNetwork1, Boolean.FALSE]
		]
		
		// Make sure all the cyclic graphs are DAG-Ind
		cyclicParamterArray.forEach[row |
			val sdf = row.get(0) as SDFGraph
			val dagGen = new SDF2DAG(sdf)
			val dagOps = new DAGOperationsImpl(dagGen)
			if(!dagOps.DAGInd) {
				throw new AssertionError("SDF is not instance independent")
			}
			parameters.add(#[sdf, dagGen, dagOps, row.get(1)])
		]
		
		return parameters
	}
	
	/**
	 * Test checks parallel level is -1 for strictly cyclic 
	 * non-acyclic like graph
	 * 
	 * Strong Test
	 */
	@Test
	public def void negativeParallelLevelTest() {
		if(dagOps.isDAGInd) {
			
			// Graph is strictly cyclic and not acyclic-like
			if(dagGen.sourceInstances.empty && !isRearrangeAcyclic) {
				Assert.assertTrue(dagOps.parallelLevel == -1)
			}
		}
	}
	
	/**
	 * Test rearranging of acyclic-like graphs only
	 */
	@Test
	public def void rearrangedDAGisParallel() {
		if(isRearrangeAcyclic && dagOps.isDAGInd){
			dagOps.rearrange
			Assert.assertTrue(dagOps.isDAGParallel)
		}
	}
	
	/**
	 * Test detection of cycles in SDFGs that is not
	 * acyclic like. 
	 * 
	 * The cycles obtained using getCycleRoots method of DAGOperations
	 * should be a subset of the cycles found using standard
	 * cycle detection algorithms
	 * 
	 * Also, getCycleRoots method of DAGOperations should not find
	 * cycles in acyclic like graphs
	 */
	 
	@Test
	public def void getCycleRootsIsSubsetOfAllCycles() {
		// Operation only valid on main DAGs, not its subset
		if(dagOps instanceof DAGOperationsImpl) {
			val allCycles = new CycleDetector(sdf).findCycles.map[it.name].toSet
			
			// Acyclic graphs
			if(allCycles.empty) {
				// For acyclic-like graphs, cycleRoots must be empty
				Assert.assertTrue(dagOps.cycleRoots.empty)
			} else {
				// For non-acyclic-like graphs, cycleRoots can't be empty
				if(!isRearrangeAcyclic) {
					Assert.assertTrue(!dagOps.cycleRoots.empty)
				}
				// For cyclic graphs, the nodes detected should 
				// be part of main cycle
				dagOps.cycleRoots.forEach[it.forEach[node |
					val actor = dagGen.instance2Actor.get(node)
					Assert.assertTrue(allCycles.contains(actor.name))
					]
				]
			}
		}
	}
	
		/**
	 * Test to check partial rearranging of the actors
	 * If the DAG is acyclic like, then the effect of rearranging is complete. 
	 * Max parallel-level SHOULD yield 0. Further, the DAG is parallel
	 * 
	 * If the DAG is non-acyclic like, then effect of rearranging is partial
	 * Max parallel level will minimum of the maximum levels of actors in the
	 * rest of the cycle (i.e. cycle without the actor of anchoring instance)
	 * 
	 * Weak Test
	 */
	@Test
	public def void checkPartialRearranging() {
		if(dagOps.isDAGInd) {
			dagOps.rearrange
			if(isRearrangeAcyclic) {
				Assert.assertTrue(dagOps.parallelLevel == 0)
				Assert.assertTrue(dagOps.isDAGParallel)
			} else {
				val cycles = dagOps.cycleRoots
				
				cycles.forEach[cycle |
					val restOfCycle = cycle.filter[instance | instance != dagOps.pickElement(cycle)]
					val minimumParLevel = newArrayList() 
					restOfCycle.forEach[instance |
						val actor = dagGen.instance2Actor.get(instance)
						val levelsOfInstances = newArrayList()
						dagGen.actor2Instances.get(actor).forEach[node |
							levelsOfInstances.add(dagOps.allLevels.get(node))
						]
						minimumParLevel.add(levelsOfInstances.max)
					]
					if(dagOps.parallelLevel != -1) {
						Assert.assertTrue(minimumParLevel.min <= dagOps.parallelLevel)	
					}
				]
			}
		}
	}
	
	/**
	 * Make sure that all the instances of a cycle, except the picked one is
	 * in the same level
	 * 
	 * Strong Test
	 */
	@Test
	public def void restOfCyclesAreArranged() {
		dagOps.rearrange
		val cycles = dagOps.cycleRoots
		
		if(!cycles.empty) {
			cycles.forEach[cycle |
				val anchor = dagOps.pickElement(cycle)
				val restOfCycle = cycle.filter[instance | instance != anchor]
				restOfCycle.forEach[instance | 
					val actor = dagGen.instance2Actor.get(instance)
					// Check if levels of all the instance of actor are same
					val levelsOfInstances = newArrayList() 
					dagGen.actor2Instances.get(actor).forEach[node |
						levelsOfInstances.add(dagOps.allLevels.get(node))
					]
					val maxLevel = levelsOfInstances.max
					levelsOfInstances.forEach[level | Assert.assertTrue(level == maxLevel)]
				]
			]
		}
	}
}