package org.abo.preesm.plugin.dataparallel.test

import java.util.Collection
import org.abo.preesm.plugin.dataparallel.DAG2DAG
import org.abo.preesm.plugin.dataparallel.DAGUtils
import org.abo.preesm.plugin.dataparallel.PureDAGConstructor
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.iterator.SrSDFDAGCoIteratorBuilder
import org.abo.preesm.plugin.dataparallel.operations.visitor.DependencyAnalysisOperations
import org.abo.preesm.plugin.dataparallel.operations.visitor.MovableInstances
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.visitors.ToHSDFVisitor
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex

@RunWith(Parameterized)
class SrSDFDAGCoIteratorTest {
	protected val SDFGraph sdf
	
	protected val PureDAGConstructor dagGen
	
	new(SDFGraph sdf, PureDAGConstructor dagGen) {
		this.sdf = sdf
		this.dagGen = dagGen
	}
	
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		/*
		 * Parameters
		 * 1. Original SDF graph that is instance independent
		 * 2. PureDAGConstructor instance
		 */
		val parameters = newArrayList
		
		val parameterArray = #[
			ExampleGraphs.strictlyCyclic,
			ExampleGraphs.strictlyCyclic2,
			ExampleGraphs.mixedNetwork1
		]
		
		parameterArray.forEach[sdf |
			val dagGen = new SDF2DAG(sdf)
			
			val parallelVisitor = new DependencyAnalysisOperations
			dagGen.accept(parallelVisitor)
			
			if(!parallelVisitor.isIndependent){
				throw new AssertionError("SDF is not instance independent")
			}
			parameters.add(#[sdf, dagGen])
			parameters.add(#[sdf, new DAG2DAG(dagGen)])
		]
		
		return parameters
	}
	
	/**
	 * Test that all the visitable instance of DAG is seen when traversing
	 * its corresponding SrSDF
	 */
	@Test
	public def void traversalIsCorrect(){
		val dag = dagGen.outputGraph
		val moveInstancesVisitor = new MovableInstances
		dagGen.accept(moveInstancesVisitor)
		val movableInstances = moveInstancesVisitor.movableInstances
		
		val hsdfVisitor = new ToHSDFVisitor
		sdf.accept(hsdfVisitor)
		val srsdf = hsdfVisitor.output
		
		val srsdfInstancesSeen = newArrayList
		
		moveInstancesVisitor.movableRootInstances.forEach[root |
			val sit = (new SrSDFDAGCoIteratorBuilder)
						.addDAG(dag)
						.addSrSDF(srsdf)
						.addVisitableNodes(movableInstances)
						.addStartVertex(root)
						.build()
			while(sit.hasNext) {
				srsdfInstancesSeen.add(sit.next)
			}
		]

		movableInstances.forEach[instance |
			if( (instance instanceof SDFJoinVertex) || (instance instanceof SDFForkVertex)) {
				Assert.assertTrue(srsdfInstancesSeen.exists[node | node.name == instance.name])
			} else {
				val srsdfInstance = DAGUtils.findVertex(instance, dag, srsdf)
				Assert.assertTrue(srsdfInstance !== null)
				Assert.assertTrue(srsdfInstancesSeen.contains(srsdfInstance))	
			}
		]
	}
}