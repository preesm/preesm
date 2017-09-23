package org.abo.preesm.plugin.dataparallel.test

//import java.util.Collection
//import org.abo.preesm.plugin.dataparallel.DAG2DAG
//import org.abo.preesm.plugin.dataparallel.DAGUtils
//import org.abo.preesm.plugin.dataparallel.PureDAGConstructor
//import org.abo.preesm.plugin.dataparallel.SDF2DAG
//import org.abo.preesm.plugin.dataparallel.iterator.SrSDFDAGCoIteratorBuilder
//import org.abo.preesm.plugin.dataparallel.operations.visitor.DependencyAnalysisOperations
//import org.abo.preesm.plugin.dataparallel.operations.visitor.MovableInstances
//import org.ietr.dftools.algorithm.model.sdf.SDFGraph
//import org.ietr.dftools.algorithm.model.sdf.visitors.ToHSDFVisitor
//import org.junit.Assert
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.junit.runners.Parameterized
//import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex
//import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex
//import org.abo.preesm.plugin.dataparallel.operations.visitor.AcyclicLikeSubgraphDetector
//import org.abo.preesm.plugin.dataparallel.operations.graph.KosarajuStrongConnectivityInspector
//import org.jgrapht.graph.DirectedSubgraph
//import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
//import org.ietr.dftools.algorithm.model.sdf.SDFEdge
//import org.jgrapht.alg.CycleDetector

//@RunWith(Parameterized)
//class SrSDFDAGCoIteratorTest {
//	protected val SDFGraph sdf
//	
//	protected val PureDAGConstructor dagGen
//	
//	new(SDFGraph sdf, PureDAGConstructor dagGen) {
//		this.sdf = sdf
//		this.dagGen = dagGen
//	}
//	
//	@Parameterized.Parameters
//	public static def Collection<Object[]> instancesToTest() {
//		/*
//		 * Parameters
//		 * 1. Original SDF graph that is instance independent
//		 * 2. PureDAGConstructor instance
//		 */
//		val parameters = newArrayList
//		
//		val parameterArray = #[
//			ExampleGraphs.strictlyCyclic,
//			ExampleGraphs.strictlyCyclicDual,
//			ExampleGraphs.strictlyCyclic2,
//			ExampleGraphs.mixedNetwork1
//		]
//		
//		parameterArray.forEach[sdf |
//			val dagGen = new SDF2DAG(sdf)
//			
//			val parallelVisitor = new DependencyAnalysisOperations
//			dagGen.accept(parallelVisitor)
//			
//			if(!parallelVisitor.isIndependent){
//				throw new AssertionError("SDF is not instance independent")
//			}
//			parameters.add(#[sdf, dagGen])
//			parameters.add(#[sdf, new DAG2DAG(dagGen)])
//		]
//		
//		return parameters
//	}
//	
//	/**
//	 * Test that all the visitable instance of DAG is seen when traversing
//	 * its corresponding SrSDF
//	 */
//	@Test
//	public def void traversalIsCorrect(){
//		val hsdfVisitor = new ToHSDFVisitor
//		sdf.accept(hsdfVisitor)
//		val srsdf = hsdfVisitor.output
//		
//		val acyclicLikeVisitor = new AcyclicLikeSubgraphDetector
//		sdf.accept(acyclicLikeVisitor)
//		
//
//		
//		if(!acyclicLikeVisitor.isAcyclicLike) {
//			acyclicLikeVisitor.SDFSubgraphs.forEach[sdfSubgraph |
//				// Get strongly connected components
//				val strongCompDetector = new KosarajuStrongConnectivityInspector(sdfSubgraph)
//				
//				// Collect strongly connected component that has loops in it
//				// Needed because stronglyConnectedSubgraphs also yield subgraphs with no loops
//				strongCompDetector.getStronglyConnectedComponents.forEach[ subgraph |
//					val dirSubGraph = subgraph as DirectedSubgraph<SDFAbstractVertex, SDFEdge>
//					val cycleDetector = new CycleDetector(dirSubGraph) 
//					if(cycleDetector.detectCycles) {
//						// ASSUMPTION: Strongly connected component of a directed graph contains atleast
//						// one loop. Perform the tests now. As only instance independent graphs are
//						// added, no check is made
//						val subgraphDAGGen = new SDF2DAG(dirSubGraph)
//						val dag = subgraphDAGGen.outputGraph
//						val moveInstanceVisitor = new MovableInstances
//						subgraphDAGGen.accept(moveInstanceVisitor)
//						
//						val movableInstances = moveInstanceVisitor.movableInstances
//						
//						val srsdfInstancesSeen = newArrayList
//		
//						moveInstanceVisitor.movableRootInstances.forEach[root |
//							val sit = (new SrSDFDAGCoIteratorBuilder)
//										.addDAG(dag)
//										.addSrSDF(srsdf)
//										.addVisitableNodes(movableInstances)
//										.addStartVertex(root)
//										.build()
//							while(sit.hasNext) {
//								srsdfInstancesSeen.add(sit.next)
//							}
//						]
//				
//						movableInstances.forEach[instance |
//							if( (instance instanceof SDFJoinVertex) || (instance instanceof SDFForkVertex)) {
//								Assert.assertTrue(srsdfInstancesSeen.exists[node | node.name == instance.name])
//							} else {
//								val srsdfInstance = DAGUtils.findVertex(instance, dag, srsdf)
//								Assert.assertTrue(srsdfInstance !== null)
//								Assert.assertTrue(srsdfInstancesSeen.contains(srsdfInstance))	
//							}
//						]
//					}
//				]
//			]	
//		}
//	}
//}