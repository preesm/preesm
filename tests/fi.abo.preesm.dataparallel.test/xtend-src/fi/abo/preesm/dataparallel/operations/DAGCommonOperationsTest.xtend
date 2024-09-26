/**
 * Copyright or © or Copr. Åbo Akademi University (2017 - 2019),
 * IETR/INSA - Rennes (2017 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Sudeep Kanur [skanur@abo.fi] (2017 - 2018)
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

import fi.abo.preesm.dataparallel.DAG2DAG
import fi.abo.preesm.dataparallel.DAGConstructor
import fi.abo.preesm.dataparallel.DAGSubset
import fi.abo.preesm.dataparallel.DAGSubsetConstructor
import fi.abo.preesm.dataparallel.PureDAGConstructor
import fi.abo.preesm.dataparallel.SDF2DAG
import fi.abo.preesm.dataparallel.iterator.DAGTopologicalIterator
import fi.abo.preesm.dataparallel.iterator.DAGTopologicalIteratorInterface
import fi.abo.preesm.dataparallel.iterator.SubsetTopologicalIterator
import fi.abo.preesm.dataparallel.test.util.Util
import java.util.ArrayList
import java.util.Collection
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector
import org.jgrapht.alg.cycle.CycleDetector
import org.jgrapht.graph.AsSubgraph
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.junit.runners.Parameterized
import org.preesm.algorithm.model.sdf.SDFAbstractVertex
import org.preesm.algorithm.model.sdf.esdf.SDFForkVertex
import org.preesm.algorithm.model.sdf.esdf.SDFJoinVertex

/**
 * Perform property based tests for operations that derive from {@link DAGCommonOperations}
 * on instances that implement {@link DAGConstructor}
 *
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
@FixMethodOrder(MethodSorters.DEFAULT)
class DAGCommonOperationsTest {
	protected val DAGConstructor dagGen

	protected val DAGTopologicalIteratorInterface iterator

	protected val SDFAbstractVertex rootNode

	protected val boolean isParallel

	protected val boolean isBranchSetCompatible

	protected val Boolean isInstanceIndependent

	/**
	 * Has the following parameters from {@link Util#provideAllGraphsContext}:
	 * <ol>
	 * 	<li> {@link DAGConstructor} instance
	 * 	<li> {@link DAGTopologicalIterator} instance that completely traverses this DAG
	 * 	<li> Root node with which {@link DAGSubset} was created
	 * 	<li> <code>true</code> if DAG is data-parallel, <code>false</code> otherwise
	 * 	<li> <code>true</code> if computing branch set does not lead to impractical computation time
	 * </ol>
	 *
	 * @see Author's DASIP 2017 paper for computation of branch set
	 */
	new(DAGConstructor dagGen
		, DAGTopologicalIteratorInterface iterator
		, SDFAbstractVertex rootNode
		, Boolean isInstanceIndependent
		, boolean isParallel
		, boolean isBranchSetCompatible) {
		this.dagGen = dagGen
		this.iterator = iterator
		this.rootNode = rootNode
		this.isParallel = isParallel
		this.isInstanceIndependent = isInstanceIndependent
		this.isBranchSetCompatible = isBranchSetCompatible
	}

	/**
	 * Generates following parameters from {@link Util#provideAllGraphsContext}:
	 * <ol>
	 * 	<li> {@link DAGConstructor} instance
	 * 	<li> {@link DAGTopologicalIterator} instance that completely traverses this DAG
	 * 	<li> Root node with which {@link DAGSubset} was created
	 * 	<li> <code>true</code> if DAG is data-parallel, <code>false</code> otherwise
	 * 	<li> <code>true</code> if computing branch set does not lead to impractical computation time
	 * </ol>
	 *
	 * @see Author's DASIP 2017 paper for computation of branch set
	 */
	@Parameterized.Parameters
	static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList

		// Add all SDF2DAG instances. Additionally, none of them are parallel
		Util.provideAllGraphsContext
			.forEach[sdfContext |
				val dagGen = new SDF2DAG(sdfContext.graph)
				val iterator  = new DAGTopologicalIterator(dagGen)
				parameters.add(#[dagGen
								, iterator
								, null
								, sdfContext.isInstanceIndependent
								, false
								, sdfContext.isBranchSetCompatible
				])
			]

		// Create new DAG2DAG instances and add all of them. Additionally, none of them are parallel
		Util.provideAllGraphsContext
			.forEach[sdfContext |
				val dagGen = new SDF2DAG(sdfContext.graph)
				val iterator = new DAGTopologicalIterator(dagGen)
				parameters.add(#[new DAG2DAG(dagGen)
								, iterator
								, null
								, sdfContext.isInstanceIndependent
								, false
								, sdfContext.isBranchSetCompatible])
			]

		// Add all subsets. They are naturally parallel
		Util.provideAllGraphsContext
			.forEach[sdfContext |
				val dagGen = new SDF2DAG(sdfContext.graph)
				// Get root nodes
				var rootVisitor = new RootExitOperations
				dagGen.accept(rootVisitor)
				var rootInstances = rootVisitor.rootInstances

				// Add subsets created from SDF2DAG
				rootInstances.forEach[rootNode |
					val iterator = new SubsetTopologicalIterator(dagGen, rootNode)
					parameters.add(#[new DAGSubset(dagGen, rootNode)
									, iterator
									, rootNode
									, null
									, true
									, sdfContext.isBranchSetCompatible])
				]

				// Add subsets created from DAG2DAG
				val dag2Dag = new DAG2DAG(dagGen)
				rootVisitor = new RootExitOperations
				dag2Dag.accept(rootVisitor)
				rootInstances = rootVisitor.rootInstances
				rootInstances.forEach[rootNode |
					val iterator = new SubsetTopologicalIterator(dag2Dag, rootNode)
					parameters.add(#[new DAGSubset(dag2Dag, rootNode)
									, iterator
									, rootNode
									, null
									, true
									, sdfContext.isBranchSetCompatible])
				]
			]

		// Test on subgraphs
		Util.provideAllGraphsContext.forEach[sdfContext |
			val sdf = sdfContext.graph

			// Get strongly connected components
			val strongCompDetector = new KosarajuStrongConnectivityInspector(sdf)
			// Collect strongly connected component that has loops in it
			// Needed because stronglyConnectedSubgraphs also yield subgraphs with no loops
			strongCompDetector.stronglyConnectedSets.forEach[ subgraphset |
				val subgraphDir = new AsSubgraph(sdf.copy, subgraphset)
				val cycleDetector = new CycleDetector(subgraphDir)
				if(cycleDetector.detectCycles) {
					// ASSUMPTION: Strongly connected component of a directed graph contains atleast
					// one loop
					val dagGen = new SDF2DAG(subgraphDir)

					// Test SDF2DAG
					parameters.add(#[dagGen
									, new DAGTopologicalIterator(dagGen)
									, null
									, sdfContext.isInstanceIndependent
									, false
									, sdfContext.isBranchSetCompatible
					])

					// Test DAG2DAG
					parameters.add(#[new DAG2DAG(dagGen)
									, new DAGTopologicalIterator(dagGen)
									, null
									, sdfContext.isInstanceIndependent
									, false
									, sdfContext.isBranchSetCompatible
					])
				}
			]
		]

		return parameters
	}

	// This is a patch for an issue introduced 2.36.0 of xtend/xtext.

	def void acceptVisitor(PureDAGConstructor dagGen, DAGOperations visitor) {
		dagGen.accept(visitor)
	}

	def void acceptVisitor(DAGSubsetConstructor dagGen, DAGCommonOperations visitor) {
		dagGen.accept(visitor)
	}

	def void acceptVisitor(DAGConstructor dagGen, DAGOperations visitor) {
		if (dagGen instanceof DAGSubsetConstructor && visitor instanceof DAGCommonOperations) {
			acceptVisitor(dagGen as DAGSubsetConstructor, visitor as DAGCommonOperations);
			return
		} else if (dagGen instanceof PureDAGConstructor && visitor !== null) {
			acceptVisitor(dagGen as PureDAGConstructor, visitor);
			return
		} else {
			throw new IllegalArgumentException("Unhandled parameter types");
		}
	}

	// Original code was :
	//	def dispatch void acceptVisitor(PureDAGConstructor dagGen, DAGOperations visitor) {
	//		dagGen.accept(visitor)
	//	}
	//
	//	def dispatch void acceptVisitor(DAGSubsetConstructor dagGen, DAGCommonOperations visitor) {
	//		dagGen.accept(visitor)
	//	}


	/**
	 * All source Instances are root instances, but not vice versa.
	 * <p>
	 * <i> Weak Test </i>
	 */
	@Test
	def void sourceInstancesAreRootInstances() {
		val rootOp = new RootExitOperations
		acceptVisitor(dagGen, rootOp)
		val rootInstances = rootOp.rootInstances
		val sourceInstances = dagGen.sourceInstances.filter(source | !dagGen.explodeImplodeOrigInstances.keySet.contains(source))
		sourceInstances.forEach[source |
			Assert.assertTrue(rootInstances.contains(source))
		]
	}

	/**
	 * All sink instances apart from those in root, are exit instances, but not vice versa
	 * <p>
	 * <i> Weak Test </i>
	 */
	@Test
	def void sinkInstancesAreExitInstances() {
		val rootExitOp = new RootExitOperations
		acceptVisitor(dagGen, rootExitOp)
		val rootInstances = rootExitOp.rootInstances
		val exitInstances = rootExitOp.exitInstances
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
	 * <p>
	 * <i>Strong Test</i>
	 */
	@Test
	def void allInstanceOfSourceAreInRoot() {
		if(dagGen instanceof SDF2DAG) {
			val rootOp = new RootExitOperations
			acceptVisitor(dagGen, rootOp)
			val rootInstances = rootOp.rootInstances
			val sourceActors = dagGen.sourceActors
			sourceActors.forEach[actor |
				val sourceInstances = dagGen.actor2Instances.get(actor).filter(instance | !dagGen.explodeImplodeOrigInstances.keySet.contains(instance))
				sourceInstances.forEach[source |
					Assert.assertTrue(rootInstances.contains(source))
				]
			]
		}
	}

	/**
	 * All instances of sink should be in exit. This only occurs,
	 * if the sink is not source as well. Also, this works only if
	 * DAG is not subset DAG
	 * <p>
	 * <i>Strong test</i>
	 */
	@Test
	def void allInstancesOfSinkAreInExit() {
		if(dagGen instanceof SDF2DAG) {
			val rootExitOp = new RootExitOperations
			acceptVisitor(dagGen, rootExitOp)
			val rootInstances = rootExitOp.rootInstances
			val exitInstances = rootExitOp.exitInstances
			val sourceActors = dagGen.sourceActors
			val sinkActors = dagGen.sinkActors.filter[actor | !sourceActors.contains(actor)]
			sinkActors.forEach[actor |
				val sinkInstances = dagGen.actor2Instances.get(actor)
						.filter(instance | !dagGen.explodeImplodeOrigInstances.keySet.contains(instance))
						.filter(instance | !rootInstances.contains(instance))
				sinkInstances.forEach[sink |
					Assert.assertTrue(exitInstances.contains(sink))
				]
			]
		}
	}

	/**
	 * Root instances have no implode instances and exit instances
	 * have no explode instances. Reverse is not true
	 * <p>
	 * <i>Weak test</i>
	 */
	@Test
	def void rootNoImplodeExitNoExplode() {
		val rootExitOp = new RootExitOperations
		acceptVisitor(dagGen, rootExitOp)
		val rootInstances = rootExitOp.rootInstances
		val exitInstances = rootExitOp.exitInstances

		rootInstances.forEach[instance |
			val implodes = dagGen.explodeImplodeOrigInstances.filter[ impExp, origInstance |
				origInstance == instance && (impExp instanceof SDFJoinVertex)
			]
			Assert.assertTrue(implodes.empty)
		]

		exitInstances.forEach[instance |
			val explodes = dagGen.explodeImplodeOrigInstances.filter[impExp, origInstance |
				origInstance == instance && (impExp instanceof SDFForkVertex)
			]
			Assert.assertTrue(explodes.empty)
		]
	}

	/**
	 * Levels of source instances and its associated implode explode instances have level 0
	 * <p>
	 * <i>Strong test</i>
	 */
	@Test
	def void levelsOfRootIsZero() {
		// Get root instances and its associated implode and explode
		val rootOp = new RootExitOperations
		acceptVisitor(dagGen, rootOp)
		val allRootInstances = rootOp.rootInstances
		allRootInstances.addAll(dagGen.explodeImplodeOrigInstances.filter[explodeImplode, instance |
			allRootInstances.contains(instance)
		].keySet)

		// Get the levels
		val levelVisitor = new LevelsOperations
		acceptVisitor(dagGen, levelVisitor)
		val allLevels = levelVisitor.levels

		// Test if all the root instances belong to level 0
		allRootInstances.forEach[instance |
			Assert.assertEquals(allLevels.get(instance), 0)
		]

		// Conversely, test if all level 0 are root instances
		allLevels.forEach[instance, level |
			if(level == 0) Assert.assertTrue(allRootInstances.contains(instance))
		]
	}

	/**
	 * <ol>
	 * 	<li> Lookup table of instanceSource created by the iterator is valid.
	 * 	<li> Levels of the sources of a given node is less than the level of the node
	 * </ol>
	 *
	 * <i>Weak test</i>
	 */
	@Test
	def void levelsOfSourcesLessThanCurrent() {
		// Gather all relevant data-structures

		val forkJoinOrigInstance = dagGen.explodeImplodeOrigInstances
		// Get sources of all the instances
		val levelOp = new LevelsOperations
		acceptVisitor(dagGen, levelOp)
		val allLevels = levelOp.levels
		val instanceSources = iterator.instanceSources

		// Now perform the check
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
	 * Verify level set construction using branch set.
	 * <p>
	 * A branch set is the collection of all the paths from the node to all of its root node.
	 * Once we have a branch set, we can compute the level of the node by taking the maximum
	 * number of nodes seen in the path. Computing branch sets becomes intractable for large graphs,
	 * so although this was the way level sets are defined in the literature, we compute it in other
	 * way. This test verifies both way of computation gives same results.
	 * <p>
	 * <i>Strong Test</i>
	 * <p>
	 * <b>Warning!<b> Computing branch sets can result in memory overflow for large graph
	 * (eg. stereo vision application)
	 * <p>
	 * Branch sets are calculated by keeping track of all the predecessors and inserting the
	 * current node in its path (memoization). This technique is outlined in author's DASIP 2017 paper
	 */
	 @Test
	def void instancesInEachPathAreInCorrectLevels() {
		if(isBranchSetCompatible) {
			val forkJoinOrigInstance = dagGen.explodeImplodeOrigInstances
		 	val instanceSources = iterator.instanceSources
		 	val levelOp = new LevelsOperations
		 	acceptVisitor(dagGen, levelOp)
		 	val allLevels = levelOp.levels
		 	val instance2Paths = newLinkedHashMap // Holds the predecessors seen for each node
		 	val newLevels = newLinkedHashMap // The new levels are stored here

		 	// Compute levels seen at each node using maximum number of instances seen in
		 	// previous paths
	 		iterator.forEach[node |
				val sourceList = instanceSources.get(node)
				newLevels.put(node, 0)
				if(sourceList.isEmpty) {
					val paths = #[#[node]]
					instance2Paths.put(node, paths)
				} else {
					val newPaths = newArrayList
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
						path.filter[source |
							!forkJoinOrigInstance.keySet.contains(source)
						].size
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
				Assert.assertEquals(level, allLevels.get(node))
			]
		}
	 }

	/**
	 * Verify level-based instance independence by cross-checking with branch-set based instance
	 * independence check
	 * <p>
	 * We calculate branch set as outlined in previous test. If an instance
	 * dependency is found in a set, then the actor is non parallel. Calculating
	 * branch set can becoming expensive very soon. So the actual calculation is
	 * done only using level set information. This test compares both approach.
	 * If this test passes, then both DAGSubsetOperations as well as
	 * DAGFromSDFOperations must be true
	 * <p>
	 * <i>Strong test</i>
	 * <p>
	 * <b>Warning!</b> Branch set calculation can blow up for complicated graphs (with
	 * too many branches per instances like broadcast).
	 * <p>
	 * The technique is outlined in DASIP 2017 paper
	 */
	@Test
	def void establishDagIndependenceUsingBranchSets() {
		if(isBranchSetCompatible) {
			// Populate all the necessary data-structures
			val instanceSources = iterator.instanceSources
			val forkJoinInstance = dagGen.explodeImplodeOrigInstances.keySet
			val depOp = new DependencyAnalysisOperations
			acceptVisitor(dagGen, depOp)
			val nonParallelActorsOrig = depOp.instanceDependentActors
			val isDAGInd = depOp.isIndependent
			val instance2Paths = newLinkedHashMap // Holds the predecessor levels of each node
			val nonParallelActors = newLinkedHashSet // Holds non-parallel actors
			val dagIndState = new ArrayList(#[true]) // Holds the state if DAG is instance independent

			iterator.forEach[node |
				val sourceList = instanceSources.get(node)
				val actor = dagGen.instance2Actor.get(node)
				if(sourceList.isEmpty) {
					val paths = #[#[node]]
					instance2Paths.put(node, paths)
				} else {
					val newPaths = newArrayList
						sourceList.forEach[source |
						instance2Paths.get(source).forEach[path |
							val actorsInPath = new ArrayList(path)
												.filter[instance | !forkJoinInstance.contains(instance)]
												.map[instance | dagGen.instance2Actor.get(instance)].toList
							if(!forkJoinInstance.contains(node) && actorsInPath.contains(actor)) {
								dagIndState.set(0, false)
								nonParallelActors.add(actor)
							}
							val newPath = new ArrayList(path)
							newPath.add(node)
							newPaths.add(newPath)
						]
					]
					instance2Paths.put(node, newPaths)
				}
			]

			// Now check if DAGInd calculation is correct
			Assert.assertEquals(isDAGInd, dagIndState.get(0))
			Assert.assertEquals(nonParallelActorsOrig, nonParallelActors)
		}
	}

	/**
	 * Cross checks if graphs are instance independent against <i>manually</i> defined parameter
	 * <p>
	 * <b>Warning!</b> The test is not generic. It depends on manually defined parameters
	 * <p>
	 * <i>Strong test</i>
	 */
	@Test
	def void checkDAGisInstanceIndependent() {
		if(isInstanceIndependent !== null && isInstanceIndependent) {
			val depOp = new DependencyAnalysisOperations
			acceptVisitor(dagGen, depOp)
			val isDAGInd = depOp.isIndependent
			Assert.assertEquals(isDAGInd, isInstanceIndependent)
		}
	}

	/**
	 * Verify DAG is parallel against <i>manually</i> defined property
	 * <p>
	 * Additionally make sure if a DAG is not independent, then it is not data-parallel
	 * <p>
	 * <b>Warning!<b> The test is not generic. It depends on a manually defined parameter
	 * <p>
	 * <i>Strong Test</i>
	 */
	@Test
	def void checkDAGisDataParallel() {
		val depOp = new DependencyAnalysisOperations
		acceptVisitor(dagGen, depOp)
		val isDAGInd = depOp.isIndependent

		val levelsVisitor = new LevelsOperations
		acceptVisitor(dagGen, levelsVisitor)
		val levels = levelsVisitor.levels

		if(isDAGInd) {
			if(dagGen instanceof DAGSubset) {
				Assert.assertTrue(OperationsUtils.isParallel(dagGen, levels))
			} else {
				Assert.assertEquals(OperationsUtils.isParallel(dagGen, levels), isParallel)
			}
		} else {
			Assert.assertFalse(OperationsUtils.isParallel(dagGen, levels))
		}
	}

}
