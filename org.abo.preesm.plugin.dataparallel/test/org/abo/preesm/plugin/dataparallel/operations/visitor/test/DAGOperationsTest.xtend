package org.abo.preesm.plugin.dataparallel.operations.visitor.test

import java.util.Collection
import org.abo.preesm.plugin.dataparallel.DAG2DAG
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.operations.visitor.CyclicSDFGOperations
import org.abo.preesm.plugin.dataparallel.operations.visitor.DAGOperations
import org.abo.preesm.plugin.dataparallel.operations.visitor.DependencyAnalysisOperations
import org.abo.preesm.plugin.dataparallel.operations.visitor.MovableInstances
import org.abo.preesm.plugin.dataparallel.operations.visitor.OperationsUtils
import org.abo.preesm.plugin.dataparallel.test.ExampleGraphs
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.jgrapht.alg.CycleDetector
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Property based tests for operations that implement {@link DAGOperations} on
 * instances of {@link PureDAGConstructor}
 * 
 * @author Sudeep Kanur 
 */
@RunWith(Parameterized)
class DAGOperationsTest {
	
	protected val SDFGraph sdf
	
	protected val PureDAGConstructor dagGen
	
	/**
	 * Flag to distinguish SDF graphs that are to be tested with
	 * rearranging acyclic graphs
	 */
	protected val boolean isAcyclicLike
	
	new(SDFGraph sdf, PureDAGConstructor dagGen, boolean isAcyclicLike) {
		this.sdf = sdf
		this.dagGen = dagGen
		this.isAcyclicLike = isAcyclicLike
	}
	
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		/*
		 * Parameters
		 * 1. Original SDF graph that is instance independent.
		 * 2. PureDAGConstructor instance
		 * 3. Boolean value if it is acyclic like SDF
		 */
		val parameters = newArrayList
		
		val parameterArray = #[
			#[ExampleGraphs.acyclicTwoActors, Boolean.TRUE],
			#[ExampleGraphs.semanticallyAcyclicCycle, Boolean.TRUE]
		]
		
		// Add SDF2DAG and DAG2DAG
		parameterArray.forEach[
			val sdf = it.get(0) as SDFGraph
			val dagGen = new SDF2DAG(sdf)
			parameters.add(#[sdf, dagGen, it.get(1)])
			parameters.add(#[sdf, new DAG2DAG(dagGen), it.get(1)])
		]
		
		// Graphs that are instance independent, but are not acyclic-like
		val cyclicParamterArray = #[
			// [DAG from SDF, test acyclic rearranging? (false)]
			#[ExampleGraphs.strictlyCyclic, Boolean.FALSE],
			#[ExampleGraphs.strictlyCyclic2, Boolean.FALSE],
			#[ExampleGraphs.mixedNetwork1, Boolean.FALSE]
		]
		
		// Make sure all the cyclic graphs are DAG-Ind
		cyclicParamterArray.forEach[row |
			val sdf = row.get(0) as SDFGraph
			val dagGen = new SDF2DAG(sdf)
			
			val parallelVisitor = new DependencyAnalysisOperations
			dagGen.accept(parallelVisitor)
	
			if(!parallelVisitor.isIndependent) {
				throw new AssertionError("SDF is not instance independent")
			}
			parameters.add(#[sdf, dagGen, row.get(1)])
			parameters.add(#[sdf, new DAG2DAG(dagGen), row.get(1)])
		]
		
		return parameters
	}
	
	/**
	 * Test checks parallel level is null for strictly cyclic 
	 * non-acyclic like graph
	 * 
	 * Strong Test
	 */
	@Test
	public def void negativeParallelLevelTest() {
		val depOp = new DependencyAnalysisOperations
		dagGen.accept(depOp)
		if(depOp.isIndependent) {
			
			// Graph is not acyclic-like
			if(dagGen.sourceInstances.empty && !isAcyclicLike) {
				// Perform for each cycle
				val cycleOp = new CyclicSDFGOperations
				dagGen.accept(cycleOp)
				
				cycleOp.cycleRoots.forEach[ cycle |
					Assert.assertTrue(OperationsUtils.getParallelLevel(dagGen, cycle.levels) === null)
				]
			}
		}
	}
	
	/**
	 * Test checks that explode implode instances of all instances
	 * are in the same level as its original instances post rearranging.
	 * 
	 * Strong Test
	 */
	@Test
	public def void implodeExplodeInSameLevelAsOrig() {
		val movableOp = new MovableInstances
		dagGen.accept(movableOp)
		
		val dagCLevels = movableOp.rearrangedLevels
		dagCLevels.forEach[node, level |
			if(dagGen.explodeImplodeOrigInstances.keySet.contains(node)) {
				val origNode = dagGen.explodeImplodeOrigInstances.get(node)
				Assert.assertEquals(dagCLevels.get(origNode), level)
			}
		]
	}
	
	/**
	 * Test rearranging of acyclic-like graphs only
	 * 
	 * Strong Test
	 */
	@Test
	public def void rearrangedDAGisParallel() {
		val depOp = new DependencyAnalysisOperations
		dagGen.accept(depOp)
		if(isAcyclicLike && depOp.isIndependent){
			val movableInstanceVisitor = new MovableInstances
			dagGen.accept(movableInstanceVisitor)
			
			// Level set should be populated with something
			Assert.assertFalse(movableInstanceVisitor.rearrangedLevels.empty)
			
			// Now check if the newly created levels are data-parallel
			Assert.assertTrue(OperationsUtils.isParallel(dagGen, movableInstanceVisitor.rearrangedLevels))
			
			// Further, the parallel level SHOULD be 0
			Assert.assertEquals(OperationsUtils.getParallelLevel(dagGen, movableInstanceVisitor.rearrangedLevels), 0)
		}
	}
	
	/**
	 * Test detection of cycles in SDFGs that is not
	 * acyclic like. 
	 * 
	 * The cycles obtained using RearrangeVisitor
	 * should be a subset of the cycles found using standard
	 * cycle detection algorithms
	 * 
	 * Also, it should not find cycles in acyclic like graphs
	 * 
	 * Weak Test (Tests only if it belongs, not equality)
	 */
	 
	@Test
	public def void cycleRootsIsSubsetOfAllCycles() {
		val sdfgCycles = new CycleDetector(sdf).findCycles.map[it.name].toSet
		
		// Get the cycles that have instances in root
		val cycleOp = new CyclicSDFGOperations
		dagGen.accept(cycleOp)
		val cycleRoots = cycleOp.cycleRoots
		
		// Acyclic graphs
		if(sdfgCycles.empty) {
			// For acyclic graph, there should not be any cycle
			Assert.assertFalse(cycleOp.containsCycles)
			
			Assert.assertTrue(cycleRoots.empty)
			
			Assert.assertTrue(isAcyclicLike)
		}
		
		// SDFG has cycles, but is acyclic-like
		if(!sdfgCycles.empty && isAcyclicLike) {
			// Acyclic-like graph, there shouldn't be any cycles with 
			// instances in root
			Assert.assertFalse(cycleOp.containsCycles)
			
			Assert.assertTrue(cycleRoots.empty)
			
			Assert.assertTrue(isAcyclicLike)
		}
		
		// SDFG has cycles and some of its instances are in root
		if(!sdfgCycles.empty && !isAcyclicLike) {
			Assert.assertTrue(!cycleRoots.empty)
			
			cycleRoots.forEach[it.roots.forEach [ node |
					val actor = dagGen.instance2Actor.get(node)
					Assert.assertTrue(sdfgCycles.contains(actor.name))
				]
			]
		}
	}
	
	/**
	 * Test to check partial rearranging of the actors
	 * 
	 * In a non-acyclic-like DAG, the movable instances are not at the same level as its siblings.
	 * On the other hand, rest of the instances of the DAG (that are not in movable instances) are
	 * in the same level
	 * 
	 * Weak Test
	 */
	@org.junit.Test
	public def void checkPartialRearranging() {
		val moveInstanceVisitor = new MovableInstances
		dagGen.accept(moveInstanceVisitor)
		
		val rearrangedLevels = moveInstanceVisitor.rearrangedLevels
		
		if(!isAcyclicLike) {
			val moveableInstances = moveInstanceVisitor.movableInstances
			val moveableActors = newHashSet
			moveableInstances.forEach[instance |
				moveableActors.add(dagGen.instance2Actor.get(instance))
			]
			
			// Create a new level set minus moveableInstances
			val parallelLevelSet = rearrangedLevels.filter[instance, level |
				val actor = dagGen.instance2Actor.get(instance)
				!moveableActors.contains(actor)
			]
			Assert.assertFalse(parallelLevelSet.empty)
			Assert.assertTrue(OperationsUtils.isParallel(dagGen, parallelLevelSet))
			
			// Create a new level set of only moveableInstances
			val nonParallelLevelSet = rearrangedLevels.filter[instance, level |
				val actor = dagGen.instance2Actor.get(instance)
				moveableActors.contains(actor)
			]
			Assert.assertFalse(nonParallelLevelSet.empty)
			Assert.assertFalse(OperationsUtils.isParallel(dagGen, nonParallelLevelSet))
		}
	}
}