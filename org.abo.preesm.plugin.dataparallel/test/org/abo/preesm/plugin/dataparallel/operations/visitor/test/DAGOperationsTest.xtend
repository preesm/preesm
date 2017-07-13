package org.abo.preesm.plugin.dataparallel.operations.visitor.test

import java.util.Collection
import org.abo.preesm.plugin.dataparallel.DAG2DAG
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.operations.visitor.CyclicSDFGOperations
import org.abo.preesm.plugin.dataparallel.operations.visitor.DAGOperations
import org.abo.preesm.plugin.dataparallel.operations.visitor.DependencyAnalysisOperations
import org.abo.preesm.plugin.dataparallel.operations.visitor.OperationsUtils
import org.abo.preesm.plugin.dataparallel.operations.visitor.RearrangeOperations
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
		val rearrangeOp = new RearrangeOperations
		dagGen.accept(rearrangeOp)
		
		// First test the dagCLevels
		val dagCLevels = rearrangeOp.dagCLevels
		dagCLevels.forEach[node, level |
			if(dagGen.explodeImplodeOrigInstances.keySet.contains(node)) {
				val origNode = dagGen.explodeImplodeOrigInstances.get(node)
				Assert.assertEquals(dagCLevels.get(origNode), level)
			}
		]
		
		// Then check the same for dagTLevels
		val dagTLevels = rearrangeOp.dagTLevels
		if(dagTLevels !== null) {
			dagTLevels.forEach[node, level|
				if(dagGen.explodeImplodeOrigInstances.keySet.contains(node)) {
					val origNode = dagGen.explodeImplodeOrigInstances.get(node)
					Assert.assertEquals(dagTLevels.get(origNode), level)
				}
			]
		}
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
			val rearrangeVisitor = new RearrangeOperations
			dagGen.accept(rearrangeVisitor)
			
			// Check that dagT is not-populated
			Assert.assertEquals(rearrangeVisitor.dagT, null)
			
			// Check that datTlevels are empty
			Assert.assertTrue(rearrangeVisitor.dagTLevels.empty)
			
			// Level set should be populated with something
			Assert.assertFalse(rearrangeVisitor.dagCLevels.empty)
			
			// Now check if the newly created levels are data-parallel
			Assert.assertTrue(OperationsUtils.isParallel(rearrangeVisitor.dagC, rearrangeVisitor.dagCLevels))
			
			// Further, the parallel level SHOULD be 0
			Assert.assertEquals(OperationsUtils.getParallelLevel(dagGen, rearrangeVisitor.dagCLevels), 0)
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
		val cycleOp = new RearrangeOperations
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
	 * When the DAG is non-acyclic like, then effect of rearranging is partial
	 * Max parallel level will be minimum of the maximum levels of actors in the
	 * rest of the cycle (i.e. cycle without the actor of anchoring instance)
	 * 
	 * Weak Test
	 */
	@Test
	public def void checkPartialRearranging() {
		val rearrangeOp = new RearrangeOperations
		dagGen.accept(rearrangeOp)
		
		val rearrangedLevels = rearrangeOp.dagCLevels
		
		if(!isAcyclicLike) {
			val cycles = rearrangeOp.cycleRoots
			
			cycles.forEach[cycle |
				val restOfCycle = cycle.roots.filter[instance |
					instance != OperationsUtils.pickElement(cycle.roots)
				]
				val minimumParLevel = newArrayList
				restOfCycle.forEach[instance |
					val actor = dagGen.instance2Actor.get(instance)
					val levelsOfInstances = newArrayList
					dagGen.actor2Instances.get(actor).forEach[node |
						levelsOfInstances.add(rearrangedLevels.get(node))
					]
					minimumParLevel.add(levelsOfInstances.max)
				]
				val parallelLevel = OperationsUtils.getParallelLevel(dagGen, rearrangedLevels)
				Assert.assertTrue(parallelLevel !== null)
				Assert.assertTrue(minimumParLevel.min <= parallelLevel)
			]
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
		val rearrangeOp = new RearrangeOperations
		dagGen.accept(rearrangeOp)
		val rearrangedLevels = rearrangeOp.dagCLevels
		
		if(!isAcyclicLike) {
			val cycles = rearrangeOp.cycleRoots
			cycles.forEach[cycle |
				val anchor = OperationsUtils.pickElement(cycle.roots)
				val restOfCycle = cycle.roots.filter[instance | instance != anchor]
				restOfCycle.forEach[instance | 
					val actor = dagGen.instance2Actor.get(instance)
					// Check if levels of all the instance of actor are same
					val levelsOfInstances = newArrayList
					dagGen.actor2Instances.get(actor).forEach[node |
						levelsOfInstances.add(rearrangedLevels.get(node))
					]
					val maxLevel = levelsOfInstances.max
					levelsOfInstances.forEach[level | Assert.assertTrue(level == maxLevel)]
				]
			]
		}
	}
}