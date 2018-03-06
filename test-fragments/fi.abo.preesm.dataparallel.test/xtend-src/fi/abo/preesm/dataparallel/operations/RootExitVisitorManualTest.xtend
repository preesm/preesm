/**
 * Copyright or © or Copr. Åbo Akademi University (2017 - 2018),
 * IETR/INSA - Rennes (2017 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
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

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import fi.abo.preesm.dataparallel.PureDAGConstructor
import java.util.List
import java.util.Collection
import fi.abo.preesm.dataparallel.test.util.ExampleGraphs
import fi.abo.preesm.dataparallel.SDF2DAG
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import fi.abo.preesm.dataparallel.DAG2DAG
import fi.abo.preesm.dataparallel.operations.RootExitOperations
import org.junit.Assert
import org.jgrapht.alg.CycleDetector
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.jgrapht.alg.KosarajuStrongConnectivityInspector
import org.jgrapht.graph.AsSubgraph

/**
 * <b>Manual test</b> for verifying root and exit instances and actors of {@link RootExitOperations}
 * 
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
class RootExitVisitorManualTest {
	
	protected val PureDAGConstructor dagGen
	
	protected val List<String> rootNodeNames
	
	protected val List<String> exitNodeNames
	
	protected val List<String> actorNames
	
	protected val boolean checkCounts
	
	/**
	 * Has the following <b>manually defined</b> parameters
	 * <ol>
	 * 	<li> A {@link PureDAGConstructor} instance
	 *  <li> Names of the root instances
	 *  <li> Names of the exit instances
	 *  <li> Names of the root actors
	 *  <li> <code>true</code> if the test must perform counting, <code>false</code> otherwise
	 * </ol>
	 */
	new(PureDAGConstructor dagGen, 
		List<String> rootNodeNames, 
		List<String> exitNodeNames, 
		List<String> actorNames, 
		boolean checkCounts
	) {
		this.dagGen = dagGen
		this.rootNodeNames = rootNodeNames.sort()
		this.exitNodeNames = exitNodeNames.sort()
		this.actorNames = actorNames.sort()
		this.checkCounts = checkCounts
	}
	
	/**
	 * Generates following <b>manually defined</b> parameters
	 * <ol>
	 * 	<li> A {@link PureDAGConstructor} instance
	 *  <li> Names of the root instances
	 *  <li> Names of the exit instances
	 *  <li> Names of the root actors
	 *  <li> <code>true</code> if the test must perform counting, <code>false</code> otherwise
	 * </ol>
	 */
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList
		
		val parameterArray = #[
			#[ExampleGraphs.acyclicTwoActors, #["a_0", "a_1", "a_2", "a_3", "a_4", "b_0"], #["b_1", "b_2"], #["a", "b"]],
			#[ExampleGraphs.twoActorSelfLoop, #["a_0", "b_0"], #["a_4", "b_1", "b_2"], #["a", "b"]],
			#[ExampleGraphs.twoActorLoop, #["a_0", "a_1" ], #["b_2"], #["a"]],
			#[ExampleGraphs.semanticallyAcyclicCycle, #["c_0", "c_1", "c_2", "d_0"], #["b_0", "b_1"], #["c", "d"]],
			#[ExampleGraphs.strictlyCyclic, #["a_0", "c_0"], #["a_2", "b_1", "d_1"], #["a", "c"]],
			#[ExampleGraphs.strictlyCyclicDual, #["b_0", "d_0"], #["a_1", "a_2", "c_1", "c_2"], #["b", "d"]],
			#[ExampleGraphs.strictlyCyclic2, #["a0_0", "c0_0", "f_0", "f_1", "a1_0", "c1_0"], #["a0_2", "b0_1", "d0_1", "e_0", "e_1", "e_2", "a1_2", "b1_1", "d1_1"], #["a0", "c0", "f", "a1", "c1"]],
			#[ExampleGraphs.mixedNetwork1, #["b_0", "c_0", "z_0", "z_1", "z_2", "z_3", "z_4", "z_5"], #["a_1", "a_2", "b_1", "e_0", "e_1", "e_2"], #["b", "c", "z"]],
			#[ExampleGraphs.mixedNetwork2, #["b_0", "z_0", "z_1", "z_2", "z_3", "z_4", "z_5"], #["a_1", "a_2", "e_0", "e_1", "e_2"], #["b", "z"]]
		]
		
		// Add SDF2DAG instances
		parameterArray.forEach[
			parameters.add(#[new SDF2DAG(it.get(0) as SDFGraph)
						   , it.get(1) as List<String>
						   , it.get(2) as List<String>
						   , it.get(3) as List<String>
						   , true
			])
		]
		
		// Add DAG2DAG instances
		parameterArray.forEach[
			val dagGen = new SDF2DAG(it.get(0) as SDFGraph)
			parameters.add(#[new DAG2DAG(dagGen)
						   , it.get(1) as List<String>
						   , it.get(2) as List<String>
						   , it.get(3) as List<String>
						   , true
			])
		]
		
		// Test on subgraphs
		parameterArray.forEach[ 
			val sdf = it.get(0) as SDFGraph
			// Get strongly connected components
			val strongCompDetector = new KosarajuStrongConnectivityInspector(sdf)
		
			val stronglyConnectedComponents = strongCompDetector.stronglyConnectedSets.size
			
			// Collect strongly connected component that has loops in it
			// Needed because stronglyConnectedSubgraphs also yield subgraphs with no loops
			strongCompDetector.stronglyConnectedComponents.forEach[ subgraph |
				val cycleDetector = new CycleDetector(subgraph as
					AsSubgraph<SDFAbstractVertex, SDFEdge>)
				val subgraphDir = subgraph as AsSubgraph<SDFAbstractVertex, SDFEdge>
				if(cycleDetector.detectCycles) {
					// ASSUMPTION: Strongly connected component of a directed graph contains atleast
					// one loop
					if( (stronglyConnectedComponents == 1) 
						&& 
						((it.get(0) as SDFGraph).vertexSet.size 
							== 
						strongCompDetector.stronglyConnectedSets.get(0).size)) {
						
						// Add SDF2DAG instances
						val dagGen = new SDF2DAG(subgraphDir)
						parameters.add(#[dagGen
										, it.get(1) as List<String>
										, it.get(2) as List<String>
										, it.get(3) as List<String>
										, true
						])
						
						// Add DAG2DAG instances
						parameters.add(#[new DAG2DAG(dagGen)
										, it.get(1) as List<String>
										, it.get(2) as List<String>
										, it.get(3) as List<String>
										, true
						])
					} else {
						val dagGen = new SDF2DAG(subgraphDir)
						
						// Add SDF2DAG instances
						parameters.add(#[dagGen
										, it.get(1) as List<String>
										, it.get(2) as List<String>
										, it.get(3) as List<String>
										, false
						])	
						
						// Add DAG2DAG instances
						parameters.add(#[new DAG2DAG(dagGen)
										, it.get(1) as List<String>
										, it.get(2) as List<String>
										, it.get(3) as List<String>
										, false
						])
					}
				}
			]
		]
		
		return parameters
	}
	
	/**
	 * Manually determined root instances match the computed ones
	 */
	@org.junit.Test
	public def void checkRootInstances() {
		val rootOp = new RootExitOperations
		dagGen.accept(rootOp)
		if(checkCounts) {
			Assert.assertEquals(rootNodeNames, rootOp.rootInstances.map[node | node.name].sort)	
		}
	}
	
	/**
	 * Manually determined exit instances match the computed ones
	 */
	@org.junit.Test
	public def void checkExitInstances() {
		val exitOp = new RootExitOperations
		dagGen.accept(exitOp)
		if(checkCounts) {
			Assert.assertEquals(exitNodeNames, exitOp.exitInstances.map[node | node.name].sort)
		}
	}
	
	/**
	 * Manually determined actors match the computed ones
	 */
	@org.junit.Test
	public def void checkActors() {
		val actorOp = new RootExitOperations
		dagGen.accept(actorOp)
		if(checkCounts) {
			Assert.assertEquals(actorNames, actorOp.rootActors.map[node | node.name].sort)
		}
		
		// Make sure instances are not disturbed
		if(checkCounts) {
			Assert.assertEquals(rootNodeNames, actorOp.rootInstances.map[node | node.name].sort)
		}
	}
}
