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

import java.util.List
import java.util.Map
import fi.abo.preesm.dataparallel.DAG2DAG
import fi.abo.preesm.dataparallel.DAGComputationBug
import fi.abo.preesm.dataparallel.DAGSubset
import fi.abo.preesm.dataparallel.SDF2DAG
import fi.abo.preesm.dataparallel.iterator.DAGTopologicalIterator
import fi.abo.preesm.dataparallel.iterator.DAGTopologicalIteratorInterface
import fi.abo.preesm.dataparallel.iterator.SubsetTopologicalIterator
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFGraph

/**
 * DAG Operation to obtain the level set of a DAG.
 * Use {@link OperationsUtils} to obtain level sets and other information
 * that can be gleamed from levels.
 * 
 * @author Sudeep Kanur
 */
class LevelsOperations implements DAGCommonOperations {
	
	private var SDFGraph dag
	
	private var List<SDFAbstractVertex> seenNodes
	
	private var DAGTopologicalIteratorInterface iterator
	
	private var Map<SDFAbstractVertex, List<SDFAbstractVertex>> instanceSources
	
	private var Map<SDFAbstractVertex, SDFAbstractVertex> forkJoinOrigInstance
	
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	val Map<SDFAbstractVertex, Integer> levels
	
	new() {
		levels = newHashMap
		instanceSources = newHashMap
	}
	
	/**
	 * Helper function to compute all the levels. 
	 * The computation leverages the fact that only those instances
	 * seen in a DAG is considered. Hence, the implementation is safe
	 * for a pure DAG as well as a subset of a DAG.
	 */
	protected def void computeAllLevels() {
		dag.vertexSet
			.filter[instance | seenNodes.contains(instance)]
			.forEach[instance | levels.put(instance, new Integer(0))]
		while(iterator.hasNext()) {
			val seenNode = iterator.next()
			val predecessors = iterator.instanceSources.get(seenNode)
			if(predecessors.isEmpty) {
				levels.put(seenNode, new Integer(0))
			} else {
				val predecessorLevel = levels.filter[node, value | predecessors.contains(node)].values.max
				if(forkJoinOrigInstance.keySet.contains(seenNode)) {
					levels.put(seenNode, new Integer(predecessorLevel))
				} else {
					levels.put(seenNode, new Integer(predecessorLevel+1))
				}
			}
		}
		
		// Properly set the implode and explode according to the level of its original
		levels.keySet.forEach[node |
			if(forkJoinOrigInstance.keySet.contains(node)) 
				levels.put(node, levels.get(forkJoinOrigInstance.get(node)))
		]
	}
	
	/**
	 * Visitor method to compute levels of a {@link SDF2DAG} instance.
	 * <p>
	 * Use {@link LevelsOperations#levels} to get the levels computed.
	 * 
	 * @param dagGen A {@link SDF2DAG} instance
	 */
	override visit(SDF2DAG dagGen) {
		dag = dagGen.outputGraph
		seenNodes = dag.vertexSet.toList
		iterator = new DAGTopologicalIterator(dagGen)
		forkJoinOrigInstance = dagGen.explodeImplodeOrigInstances
		instanceSources.putAll(iterator.instanceSources)
		computeAllLevels
	}
	
	/**
	 * Visitor method to compute levels of a {@link DAGSubset} instance.
	 * <p> 
	 * Use {@link LevelsOperations#levels} to get the levels computed.
	 * <p>
	 * @param dagGen A {@link DAGSubset} instance
	 */
	override visit(DAGSubset dagGen) {
		dag = dagGen.inputGraph
		seenNodes = dagGen.seenNodes
		
		// Get the root node and subsequently the iterator
		val rootVisitor = new RootExitOperations()
		dagGen.accept(rootVisitor)
		val rootNodes = rootVisitor.rootInstances
		if(rootNodes.size !== 1) {
			throw new DAGComputationBug("Root nodes size must be 1, but is " + rootNodes.size)
		} else if (rootNodes.empty) {
			throw new DAGComputationBug("Root instances set cannot be empty")
		}
		val rootNode = rootNodes.get(0)
		
		iterator = new SubsetTopologicalIterator(dagGen.originalDAG, rootNode)
		forkJoinOrigInstance = dagGen.explodeImplodeOrigInstances
		instanceSources.putAll(new DAGTopologicalIterator(dagGen.originalDAG).instanceSources)
		computeAllLevels
	}
	
	/**
	 * Visitor method to compute levels of a {@link DAG2DAG} instance.
	 * <p>
	 * Use {@link LevelsOperations#levels} to get the levels computed.
	 * 
	 * @param dagGen A {@link DAG2DAG} instance
	 */
	override visit(DAG2DAG dagGen) {
		dag = dagGen.outputGraph
		seenNodes = dag.vertexSet.toList
		iterator = new DAGTopologicalIterator(dagGen)
		forkJoinOrigInstance = dagGen.explodeImplodeOrigInstances
		instanceSources.putAll(iterator.instanceSources)
		computeAllLevels	
	}
	
}
