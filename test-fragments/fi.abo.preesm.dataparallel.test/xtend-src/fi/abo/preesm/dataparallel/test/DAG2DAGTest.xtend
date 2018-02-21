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
package fi.abo.preesm.dataparallel.test

import java.util.Collection
import java.util.HashMap
import fi.abo.preesm.dataparallel.DAG2DAG
import fi.abo.preesm.dataparallel.SDF2DAG
import fi.abo.preesm.dataparallel.operations.LevelsOperations
import fi.abo.preesm.dataparallel.operations.OperationsUtils
import org.jgrapht.alg.CycleDetector
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import fi.abo.preesm.dataparallel.operations.graph.KosarajuStrongConnectivityInspector
import org.jgrapht.graph.DirectedSubgraph
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import fi.abo.preesm.dataparallel.test.util.Util

/**
 * Property based test to check {@link DAG2DAG} construction
 * 
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
class DAG2DAGTest {
	
	protected val SDF2DAG dagGen
	
	/**
	 * Has the following parameters from {@link Util#provideAllGraphs}:
	 * <ol>
	 * 	<li> {@link SDF2DAG} instance derived from a {@link SDFGraph}
	 * </ol>
	 */
	new(SDF2DAG dagGen) {
		this.dagGen = dagGen
	}
	
	/**
	 * Generates following parameters from {@link Util#provideAllGraphs}:
	 * <ol>
	 * 	<li> {@link SDF2DAG} instance derived from a {@link SDFGraph}
	 * </ol>
	 */
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		val parameters = newArrayList
		
		Util.provideAllGraphs.forEach[sdf |
			val dagGen = new SDF2DAG(sdf)
			parameters.add(#[dagGen])
		]
		
		Util.provideAllGraphs.forEach[ sdf |			
			// Get strongly connected components
			val strongCompDetector = new KosarajuStrongConnectivityInspector(sdf)
					
			// Collect strongly connected component that has loops in it
			// Needed because stronglyConnectedSubgraphs also yield subgraphs with no loops
			strongCompDetector.stronglyConnectedComponents.forEach[ subgraph |
				val cycleDetector = new CycleDetector(subgraph as 
					DirectedSubgraph<SDFAbstractVertex, SDFEdge>
				) 
				if(cycleDetector.detectCycles) {
					// ASSUMPTION: Strongly connected component of a directed graph contains atleast
					// one loop
					val dagGen = new SDF2DAG(subgraph as 
						DirectedSubgraph<SDFAbstractVertex, SDFEdge>
					)
					parameters.add(#[dagGen])
				}
			]
		]
		
		return parameters
	}
	
	/**
	 * Actor of {@link DAG2DAG#instance2Actor} gives the same actor from {@link SDF2DAG#instance2Actor} map
	 * <p>
	 * <i>Strong Test</i>
	 */
	@Test
	public def void bothInstancesLeadToSameActor() {
		val newDagGen = new DAG2DAG(dagGen)
		newDagGen.instance2Actor.forEach[instance, actor |
			val newActor = newDagGen.instance2Actor.get(instance)
			// Check that the new map does return an actor and not null
			Assert.assertTrue(newActor !== null)
			
			// Now check that its same as old actor
			Assert.assertEquals(newActor, actor)
		]
	}
	
	/**
	 * Changing levels of one {@link SDF2DAG} does not modify the levels of {@link DAG2DAG}
	 * <p>
	 * <i>Strong Test</i> 
	 */
	@Test
	public def void graphsAreOperationInvariant() {
		val newDagGen = new DAG2DAG(dagGen)
		var levelOp = new LevelsOperations
		dagGen.accept(levelOp)
		var oldLevels = new HashMap(levelOp.levels)
		val maxLevel = OperationsUtils.getMaxLevel(oldLevels)
		
		// Now modify one level
		val indexInstance = oldLevels.keySet.get(0)
		oldLevels.put(indexInstance, maxLevel)
		
		// Now construct the new DAG and check if the levels are modified
		levelOp = new LevelsOperations
		newDagGen.accept(levelOp)
		val newLevels = new HashMap(levelOp.levels)
		Assert.assertEquals(oldLevels.get(indexInstance), maxLevel)
		Assert.assertTrue(newLevels.get(indexInstance) != maxLevel)
	}
}
