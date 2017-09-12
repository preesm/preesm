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
import org.abo.preesm.plugin.dataparallel.operations.visitor.RootExitOperations
import org.abo.preesm.plugin.dataparallel.test.ExampleGraphs
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.jgrapht.alg.CycleDetector
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex
import org.abo.preesm.plugin.dataparallel.operations.visitor.LevelsOperations
import org.abo.preesm.plugin.dataparallel.operations.visitor.GetParallelLevelBuilder
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex
import org.abo.preesm.plugin.dataparallel.operations.visitor.RearrangeDAG

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
			#[ExampleGraphs.strictlyCyclicDual, Boolean.FALSE],
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
		val levelsOp = new LevelsOperations
		dagGen.accept(levelsOp)
		if(depOp.isIndependent) {
			
			// Graph is not acyclic-like
			if(dagGen.sourceInstances.empty && !isAcyclicLike) {
				// Perform for each cycle
				val cycleOp = new CyclicSDFGOperations
				dagGen.accept(cycleOp)
				
				cycleOp.cycleRoots.forEach[ cycle |
					val parallelLevel = (new GetParallelLevelBuilder)
											.addOrigLevels(cycle.levels)
											.addSubsetLevels(cycle.levels)
											.addDagGen(dagGen)
											.build()
					Assert.assertTrue(parallelLevel === null)
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
			val parallelLevel = (new GetParallelLevelBuilder)
									.addOrigLevels(movableInstanceVisitor.rearrangedLevels)
									.addSubsetLevels(movableInstanceVisitor.rearrangedLevels)
									.addDagGen(dagGen)
									.build()
			Assert.assertEquals(parallelLevel, 0)
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
	@org.junit.Test
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
	@Test
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
	
	/**
	 * Test relation between movableInstances, movableRootInstances, movableExitInstances
	 * 
	 * MovableRootInstances are also root nodes
	 * MovableRootInstances cannot have implode nodes
	 * If movableExitInstance is explode, then its corresponding original actor will be in 
	 * movableInstance
	 * If movableExitInstance is not explode, then it does not have an explode instance at all
	 * MovableExitInstance cannot be implode nodes
	 * MovableExitInstances and movableRootInstances are subset of movableInstances
	 * All have anchor instances
	 * 
	 * Weak Test
	 */
	@Test
	public def void movableInstancesTest() {
		val moveInstanceVisitor = new MovableInstances
		dagGen.accept(moveInstanceVisitor)
		
		if(!isAcyclicLike) {
			val movableRootInstances = moveInstanceVisitor.movableRootInstances
			val movableInstances = moveInstanceVisitor.movableInstances
			val movableExitInstances = moveInstanceVisitor.movableExitInstances
			
			// Get root nodes
			val rootVisitor = new RootExitOperations
			dagGen.accept(rootVisitor)
			val rootInstances = rootVisitor.rootInstances
			
			// Get anchor nodes
			val cycleDetectOp = new CyclicSDFGOperations
			dagGen.accept(cycleDetectOp)
			val anchorInstances = newArrayList 
			cycleDetectOp.cycleRoots.forEach[cycle |
				val cycleRoots = cycle.roots
				anchorInstances.add(OperationsUtils.pickElement(cycleRoots))
			]
			Assert.assertTrue(!anchorInstances.empty)
			
			movableRootInstances.forEach[instance |
				Assert.assertTrue(rootInstances.contains(instance))
				Assert.assertTrue(movableInstances.contains(instance))
				Assert.assertTrue(!(instance instanceof SDFJoinVertex))
			]  
			
			movableExitInstances.forEach[instance |
				Assert.assertTrue(!(instance instanceof SDFJoinVertex))
				if(instance instanceof SDFForkVertex) {
					val origInstance = dagGen.explodeImplodeOrigInstances.get(instance)
					Assert.assertTrue(movableInstances.contains(origInstance))
				} else {
					// Get list of explode instance of this instance
					val explodeInstances = dagGen.explodeImplodeOrigInstances.filter[expImp, origInstance |
						(expImp instanceof SDFForkVertex) && (origInstance == instance)
					]
					Assert.assertTrue(explodeInstances.keySet.empty)
				}
				Assert.assertTrue(movableInstances.contains(instance))
			]

			anchorInstances.forEach[instance |
				Assert.assertTrue(movableInstances.contains(instance))
				Assert.assertTrue(movableRootInstances.contains(instance))
			]
		}
	}
	
	/**
	 * Test generation of transient SrSDF graph
	 * There are not many properties to test, except check the fact that
	 * the movable instances are indeed moved in the SrSDF
	 */
	@org.junit.Test
	public def void checkRearranging() {
		val rearrangeVisitor = new RearrangeDAG(sdf)
		dagGen.accept(rearrangeVisitor)
		val cySDF = rearrangeVisitor.cyclicGraph
		
		// Convert SrSDF to DAG
		val cyDAG = (new SDF2DAG(cySDF)).outputGraph		
	}
}