/**
 * Copyright or © or Copr. Åbo Akademi University (2017 - 2018),
 * IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Sudeep Kanur <skanur@abo.fi> (2017 - 2018)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fi.abo.preesm.dataparallel.operations

import java.util.Collection
import fi.abo.preesm.dataparallel.DAG2DAG
import fi.abo.preesm.dataparallel.PureDAGConstructor
import fi.abo.preesm.dataparallel.SDF2DAG
import fi.abo.preesm.dataparallel.operations.AcyclicLikeSubgraphDetector
import fi.abo.preesm.dataparallel.operations.MovableInstances
import fi.abo.preesm.dataparallel.operations.OperationsUtils
import fi.abo.preesm.dataparallel.operations.RootExitOperations
import fi.abo.preesm.dataparallel.test.util.Util
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex
import org.jgrapht.alg.CycleDetector
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.jgrapht.alg.KosarajuStrongConnectivityInspector
import org.jgrapht.graph.AsSubgraph

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
					val dirSubGraph = subgraph as AsSubgraph<SDFAbstractVertex, SDFEdge>
					val cycleDetector = new CycleDetector(dirSubGraph) 
					if(cycleDetector.detectCycles) {
						// ASSUMPTION: Strongly connected component of a directed graph contains atleast
						// one loop. Perform the tests now. As only instance independent graphs are
						// added, no check is made
						val subgraphDAGGen = new SDF2DAG(dirSubGraph)
						val sc = new KosarajuStrongConnectivityInspector(dirSubGraph)
						val sourceActors = sc.stronglyConnectedComponents.filter[sg |
							val cd = new CycleDetector(sg as 
								AsSubgraph<SDFAbstractVertex, SDFEdge>
							)
							!cd.detectCycles
						].map[sg |
							sg.vertexSet
						].flatten
						.toList
						val moveInstanceVisitor = new MovableInstances(sourceActors)
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
					val dirSubGraph = subgraph as AsSubgraph<SDFAbstractVertex, SDFEdge>
					val cycleDetector = new CycleDetector(dirSubGraph) 
					if(cycleDetector.detectCycles) {
						// ASSUMPTION: Strongly connected component of a directed graph contains atleast
						// one loop. Perform the tests now. As only instance independent graphs are
						// added, no check is made
						
						val subgraphDAGGen = new SDF2DAG(dirSubGraph)
						val sc = new KosarajuStrongConnectivityInspector(dirSubGraph)
						val sourceActors = sc.stronglyConnectedComponents.filter[sg |
							val cd = new CycleDetector(sg as 
								AsSubgraph<SDFAbstractVertex, SDFEdge>
							)
							!cd.detectCycles
						].map[sg |
							sg.vertexSet
						].flatten
						.toList
						val moveInstanceVisitor = new MovableInstances(sourceActors)
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
