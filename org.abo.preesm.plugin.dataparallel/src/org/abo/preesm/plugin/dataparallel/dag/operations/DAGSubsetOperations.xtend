package org.abo.preesm.plugin.dataparallel.dag.operations

import java.util.logging.Logger
import org.abo.preesm.plugin.dataparallel.DAGConstructor
import org.abo.preesm.plugin.dataparallel.DAGSubset
import org.abo.preesm.plugin.dataparallel.SubsetTopologicalIterator
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex

class DAGSubsetOperations extends DAGFromSDFOperations {
	
	private val SDFAbstractVertex rootNode
	
	new(DAGConstructor dagGen, SDFAbstractVertex rootNode, Logger logger) {
		super(dagGen, logger)
		this.rootNode = rootNode
		val sit = new SubsetTopologicalIterator(dagGen, rootNode)
		iterator = sit
		instanceSources = sit.instanceSources
		seenNodes = new SubsetTopologicalIterator(dagGen, rootNode).toList
		forkJoinOrigInstance = new DAGSubset(dagGen, rootNode).explodeImplodeOrigInstances
	}
	
	new(DAGConstructor dagGen, SDFAbstractVertex rootNode) {
		this(dagGen, rootNode, null)
	}
}