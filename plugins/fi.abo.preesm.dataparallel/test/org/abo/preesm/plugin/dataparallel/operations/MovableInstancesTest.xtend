package org.abo.preesm.plugin.dataparallel.operations

import java.util.Collection
import org.abo.preesm.plugin.dataparallel.DAG2DAG
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.operations.graph.KosarajuStrongConnectivityInspector
import org.abo.preesm.plugin.dataparallel.operations.AcyclicLikeSubgraphDetector
import org.abo.preesm.plugin.dataparallel.operations.MovableInstances
import org.abo.preesm.plugin.dataparallel.operations.OperationsUtils
import org.abo.preesm.plugin.dataparallel.operations.RootExitOperations
import org.abo.preesm.plugin.dataparallel.test.util.Util
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex
import org.jgrapht.alg.CycleDetector
import org.jgrapht.graph.DirectedSubgraph
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Property based tests for {@link MovableInstances}
 * 
 * @author Sudeep Kanur 
 */
@RunWith(Parameterized)
class MovableInstancesTest {
	
	protected val SDFGraph sdf
	
	protected val PureDAGConstructor dagGen
	
	protected val boolean isAcyclicLike
	
	/**
	 * Has the following test parameters from {@link Util#provideAllGraphsContext}:
	 * <ol>
	 * 	<li> {@link SDFGraph} that is instance independent
	 * 	<li> {@link PureDAGConstructor} DAG of the SDFGraph
	 *  <li> True if SDFGraph is acyclic-like
	 * </ol>
	 */
	new(SDFGraph sdf, PureDAGConstructor dagGen, boolean isAcyclicLike) {
		this.sdf = sdf
		this.dagGen = dagGen
		this.isAcyclicLike = isAcyclicLike
	}
	
	/**
	 * Generate following test parameters from {@link Util#provideAllGraphsContext}:
	 * <ol>
	 * 	<li> {@link SDFGraph} that is instance independent
	 * 	<li> {@link PureDAGConstructor} DAG of the SDFGraph
	 *  <li> True if SDFGraph is acyclic-like
	 * </ol>
	 */
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList
		
		// Add SDF2DAG and DAG2DAG
		Util.provideAllGraphsContext.forEach[sdfContext |
			// Only add instance independent graphs
			if(sdfContext.instanceIndependent) {
				val sdf = sdfContext.graph
				val dagGen = new SDF2DAG(sdf)
				parameters.add(#[sdf, dagGen, sdfContext.isAcyclic])
				parameters.add(#[sdf, new DAG2DAG(dagGen), sdfContext.isAcyclic])	
			}
		]
		
		return parameters
	}
	
	/**
	 * {@link MovableInstances} operation create partially parallel DAG for a non-acyclic, but
	 * instance independent SDFG. Following tests are carried out:
	 * <ol>
	 * 	<li> DAG without movable instances is always non-empty and parallel
	 *  <li> DAG with only movable instances is always non-empty and non-parallel
	 * </ol>
	 * <p> 
	 * <i>Weak Test</i>
	 */
	@Test
	public def void checkPartialRearranging() {
		val acyclicLikeVisitor = new AcyclicLikeSubgraphDetector
		sdf.accept(acyclicLikeVisitor)
		if(!acyclicLikeVisitor.isAcyclicLike) {
			acyclicLikeVisitor.SDFSubgraphs.forEach[sdfSubgraph |
				// Get strongly connected components
				val strongCompDetector = new KosarajuStrongConnectivityInspector(sdfSubgraph)
				
				// Collect strongly connected component that has loops in it
				// Needed because stronglyConnectedSubgraphs also yield subgraphs with no loops
				strongCompDetector.getStronglyConnectedComponents.forEach[ subgraph |
					val dirSubGraph = subgraph as DirectedSubgraph<SDFAbstractVertex, SDFEdge>
					val cycleDetector = new CycleDetector(dirSubGraph) 
					if(cycleDetector.detectCycles) {
						// ASSUMPTION: Strongly connected component of a directed graph contains atleast
						// one loop. Perform the tests now. As only instance independent graphs are
						// added, no check is made
						val subgraphDAGGen = new SDF2DAG(dirSubGraph)
						val moveInstanceVisitor = new MovableInstances
						subgraphDAGGen.accept(moveInstanceVisitor)
						
						val rearrangedLevels = moveInstanceVisitor.rearrangedLevels
						
						val moveableInstances = moveInstanceVisitor.movableInstances
						val moveableActors = newHashSet
						moveableInstances.forEach[instance |
							moveableActors.add(subgraphDAGGen.instance2Actor.get(instance))
						]
						
						// Create a new level set minus moveableInstances
						val parallelLevelSet = rearrangedLevels.filter[instance, level |
							val actor = subgraphDAGGen.instance2Actor.get(instance)
							!moveableActors.contains(actor)
						]
						// 1. DAG without movable instances is always non-empty and parallel
						Assert.assertFalse(parallelLevelSet.empty)
						Assert.assertTrue(OperationsUtils.isParallel(subgraphDAGGen, parallelLevelSet))
						
						// Create a new level set of only moveableInstances
						val nonParallelLevelSet = rearrangedLevels.filter[instance, level |
							val actor = subgraphDAGGen.instance2Actor.get(instance)
							moveableActors.contains(actor)
						]
						// 2. DAG with only movable instances is always non-empty and non-parallel
						Assert.assertFalse(nonParallelLevelSet.empty)
						Assert.assertFalse(OperationsUtils.isParallel(subgraphDAGGen, nonParallelLevelSet))
					}
				]
			]
		}
	}
	
	/**
	 * Relation between {@link MovableInstances#movableInstances}, 
	 * {@link MovableInstances#movableRootInstances} and {@link MovableInstances#movableExitInstances}.
	 * Following tests are carried out:
	 * 
	 * <ol>
	 *  <li> If movable instances are not empty, then there are root and exit nodes
	 *  <li> MovableRootInstances are also root nodes
	 * 	<li> MovableRootInstances cannot have implode nodes
	 * 	<li> If movableExitInstance is explode, then its corresponding original actor will be in 
	 * movableInstance
	 * 	<li> If movableExitInstance is not explode, then it does not have an explode instance at all
	 * 	<li> MovableExitInstance cannot be implode nodes
	 * 	<li> MovableExitInstances and movableRootInstances are subset of movableInstances
	 * </ol>
	 * <p>
	 * <i>Weak Test</i>
	 */
	@Test
	public def void movableInstancesTest() {
		val acyclicLikeVisitor = new AcyclicLikeSubgraphDetector
		sdf.accept(acyclicLikeVisitor)
		if(!acyclicLikeVisitor.isAcyclicLike) {
			acyclicLikeVisitor.SDFSubgraphs.forEach[sdfSubgraph |
				// Get strongly connected components
				val strongCompDetector = new KosarajuStrongConnectivityInspector(sdfSubgraph)
				
				// Collect strongly connected component that has loops in it
				// Needed because stronglyConnectedSubgraphs also yield subgraphs with no loops
				strongCompDetector.getStronglyConnectedComponents.forEach[ subgraph |
					val dirSubGraph = subgraph as DirectedSubgraph<SDFAbstractVertex, SDFEdge>
					val cycleDetector = new CycleDetector(dirSubGraph) 
					if(cycleDetector.detectCycles) {
						// ASSUMPTION: Strongly connected component of a directed graph contains atleast
						// one loop. Perform the tests now. As only instance independent graphs are
						// added, no check is made
						
						val subgraphDAGGen = new SDF2DAG(dirSubGraph)
						val moveInstanceVisitor = new MovableInstances
						subgraphDAGGen.accept(moveInstanceVisitor)
						
						val movableRootInstances = moveInstanceVisitor.movableRootInstances
						val movableInstances = moveInstanceVisitor.movableInstances
						val movableExitInstances = moveInstanceVisitor.movableExitInstances
						
						// 1. If movableInstances is not empty, then root and exit instances 
						// cannot be empty
						if(!movableInstances.empty) {
							Assert.assertFalse(movableRootInstances.empty)
							Assert.assertFalse(movableExitInstances.empty)
						}
						
						// Get root nodes
						val rootVisitor = new RootExitOperations
						subgraphDAGGen.accept(rootVisitor)
						val rootInstances = rootVisitor.rootInstances
						
						movableRootInstances.forEach[instance |
							// 2. MovableRootInstances are also root instances
							Assert.assertTrue(rootInstances.contains(instance))
							// 7a. MovableRootInstances are subset of all movable instances
							Assert.assertTrue(movableInstances.contains(instance))
							// 3. MovableRootInstances are never implode nodes
							Assert.assertTrue(!(instance instanceof SDFJoinVertex))
						]
						
						movableExitInstances.forEach[instance |
							// 6. MovableExitInstances cannot be implode nodes
							Assert.assertTrue(!(instance instanceof SDFJoinVertex))
							
							// 4. If movableExitInstance is explode, then its corresponding original 
							// actor will be in movableInstance
							if(instance instanceof SDFForkVertex) {
								val origInstance = subgraphDAGGen.explodeImplodeOrigInstances.get(instance)
								Assert.assertTrue(movableInstances.contains(origInstance))
							} else {
								// Get list of explode instance of this instance
								val explodeInstances = dagGen.explodeImplodeOrigInstances.filter[expImp, origInstance |
									(expImp instanceof SDFForkVertex) && (origInstance == instance)
								]
								// 5. If movableExitInstance is not explode, then it does not have 
								// an explode instance at all
								Assert.assertTrue(explodeInstances.keySet.empty)	
							}
							// 7b. MovableExitInstance are subset of all movable instances
							Assert.assertTrue(movableInstances.contains(instance))
						]
					}
				]
			]
		}
	}
}