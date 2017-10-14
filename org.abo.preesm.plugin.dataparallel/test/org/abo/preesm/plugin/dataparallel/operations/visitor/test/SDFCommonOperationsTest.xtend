package org.abo.preesm.plugin.dataparallel.operations.visitor.test

import java.util.Collection
import org.abo.preesm.plugin.dataparallel.SDF2DAG
import org.abo.preesm.plugin.dataparallel.SrSDFToSDF
import org.abo.preesm.plugin.dataparallel.operations.graph.KosarajuStrongConnectivityInspector
import org.abo.preesm.plugin.dataparallel.operations.visitor.AcyclicLikeSubgraphDetector
import org.abo.preesm.plugin.dataparallel.operations.visitor.RearrangeOperations
import org.abo.preesm.plugin.dataparallel.operations.visitor.RetimingInfo
import org.abo.preesm.plugin.dataparallel.test.Util
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.visitors.ToHSDFVisitor
import org.jgrapht.alg.CycleDetector
import org.jgrapht.graph.DirectedSubgraph
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.abo.preesm.plugin.dataparallel.fifo.FifoActorGraph
import org.abo.preesm.plugin.dataparallel.fifo.FifoActor
import org.abo.preesm.plugin.dataparallel.fifo.FifoActorBeanKey
import org.abo.preesm.plugin.dataparallel.operations.visitor.MovableInstances
import org.abo.preesm.plugin.dataparallel.NodeChainGraph

/**
 * Class to test operations on SDF graphs
 * 
 * @author Sudeep Kanur
 */
@RunWith(Parameterized)
class SDFCommonOperationsTest {
	protected val SDFGraph sdf
	
	protected val Boolean isAcyclic
	
	protected val Boolean isInstanceIndependent
	
	new(SDFGraph sdf
	   , Boolean isAcyclic
	   , Boolean isInstanceIndependent
	) {
		this.sdf = sdf
		this.isAcyclic = isAcyclic
		this.isInstanceIndependent = isInstanceIndependent		
	}
	
	@Parameterized.Parameters
	public static def Collection<Object[]> instancesToTest() {
		/*
		 * Contains following parameters
		 * 1. SDFGraph
		 * 2. Is SDFGraph acyclic?
		 * 3. Is SDFGraph instance independent?
		 */
		val parameters = newArrayList
		
		Util.provideAllGraphsContext.forEach[sdfContext |
			parameters.add(#[sdfContext.graph, sdfContext.isAcyclic, sdfContext.isInstanceIndependent])	
		]
		
		return parameters
	}
	
	/**
	 * Check whether Acyclic graphs are indeed detected as so by {@link AcyclicLikeSubgraphDetector}
	 * class
	 * 
	 * Warning! Not a generic test. Test depends on manually defined parameters
	 * 
	 * Strong test. Manually defined
	 */
	@Test
	public def void sdfIsAcyclic() {
		if(isAcyclic) {	
			val acyclicLikeVisitor = new AcyclicLikeSubgraphDetector
			sdf.accept(acyclicLikeVisitor)
			
			Assert.assertTrue(acyclicLikeVisitor.isAcyclicLike)
		}
	}
	
	/**
	 * Check correctness of re-timing transformation
	 * 
	 * Re-timing transformation is correct if:
	 * 1. The graph that is instance independent and non-acyclic like becomes acyclic like
	 * 2. Transient graphs are schedulable
	 * 3. The transient graphs have no delays
	 * 4. Transient graphs are always acyclic
	 * 5. The transient graphs have only {@link FifoActor} in their output 
	 * 6. {@link FifoActor}s of {@link FifoActorGraph} does not have duplicate {@link FifoActor}s in 
	 * SrSDF edges with delays. Meaning, SrSDF edges with delays have one unique {@link FifoActor}
	 * present either in the SrSDF graph edge or in a transient graph.
	 * 7. All movable instances are seen in the transient graph
	 * 8. All delays are positive
	 * 
	 * Strong test
	 */
	@Test
	public def void retimeTest() {
		val acyclicLikeVisitor = new AcyclicLikeSubgraphDetector
		sdf.accept(acyclicLikeVisitor)
		if(!acyclicLikeVisitor.isAcyclicLike && isInstanceIndependent) {
			
			val info = new RetimingInfo(newArrayList)
			val srsdfVisitor = new ToHSDFVisitor
			sdf.accept(srsdfVisitor)
			val srsdf = srsdfVisitor.output
			
			// For later checks of re-timing transformation
			val transform = new SrSDFToSDF(sdf, srsdf)
			
			// For checking if all movable instances are seen in the graph
			val allMovableInstances = newArrayList

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
						allMovableInstances.addAll(moveInstanceVisitor.movableInstances)
						
						val retimingVisitor = new RearrangeOperations(srsdf, info)
						subgraphDAGGen.accept(retimingVisitor)
					}
				]
			]
			
			// 1. Check re-timing creates acyclic-like graphs
			val retimedSDF = transform.getRetimedSDF(srsdf)
			val retimedAcyclicLikeVisitor = new AcyclicLikeSubgraphDetector
			retimedSDF.accept(retimedAcyclicLikeVisitor)
			Assert.assertTrue(retimedAcyclicLikeVisitor.isAcyclicLike)
			
			
			Assert.assertTrue(!info.initializationGraphs.empty)
			
			info.initializationGraphs.forEach[graph |
				// 2. Transient graphs are schedulable
				Assert.assertTrue(graph.schedulable)
				
				// 3. Check transient graph has no delays
				val edgesWithDelays = newArrayList
				graph.edgeSet.forEach[edge |
					if(edge.delay.intValue > 0) {
						edgesWithDelays.add(edge)
					}
				]
				Assert.assertTrue(edgesWithDelays.empty)
				
				// 4. Transient graphs are always acyclic
				val cycleDetector = new CycleDetector(graph)
				Assert.assertTrue(!cycleDetector.detectCycles)
				
				// 5. The transient graphs have only {@link FifoActor} in their output 
				graph.vertexSet.filter[vertex |
					vertex.sinks.empty
				].forEach[sinkVertex |
					Assert.assertTrue(sinkVertex instanceof FifoActor)
				]
			]
			
			// 6. SrSDF edges with delays have one unique {@link FifoActor} 
			// present either in the SrSDF graph edge or in a transient graph.
			val fifoActorsFromSrSDF = newArrayList
			val fifoActorsFromTransientGraph = newArrayList
			
			srsdf.edgeSet.forEach[edge |
				val fifoActor = edge.propertyBean.getValue(FifoActorBeanKey.key)
				if(fifoActor !== null) {
					fifoActorsFromSrSDF.add(fifoActor)
				}
			]
			
			info.initializationGraphs.forEach[graph |
				graph.vertexSet.forEach[vertex |
					if(vertex instanceof FifoActor) {
						// Assure that transient graphs have no duplicate FifoActors
						Assert.assertTrue(!fifoActorsFromTransientGraph.contains(vertex))
						fifoActorsFromTransientGraph.add(vertex)
					}
				]
			]
			
			fifoActorsFromTransientGraph.forEach[fifoActor |
				Assert.assertTrue(!fifoActorsFromSrSDF.contains(fifoActor))
			]
			
			// 7. All movable instances are seen in the transient graph
			val allUserAddedInstances = newArrayList // Actors added in SDF
			val nodeChainGraph = new NodeChainGraph(srsdf)
			val movableNodeNames = nodeChainGraph.nodechains.keySet.map[it.name].toList
			val allSignificantMovableInstances = allMovableInstances.filter[vertex |
				movableNodeNames.contains(vertex.name)
			]
			info.initializationGraphs.forEach[graph |
				allUserAddedInstances.addAll(graph.vertexSet.filter[vertex |
					nodeChainGraph.nodechains.keySet.contains(vertex)
				])
			]

			allSignificantMovableInstances.forEach[moveInstance |
				val srsdfMoveInstance = srsdf.getVertex(moveInstance.name)
				Assert.assertTrue(allUserAddedInstances.contains(srsdfMoveInstance))
			]
			
			allUserAddedInstances.forEach[addedInstance |
				Assert.assertEquals(1, allSignificantMovableInstances.filter[vertex |
					vertex.name == addedInstance.name].size)
			]
			
			// 8. Check all delays are positive
			srsdf.edgeSet.forEach[edge |
				if(edge.delay.intValue < 0) {
					println(edge)
				}
			]
			
			Assert.assertTrue(srsdf.edgeSet.forall[edge |
				edge.delay.intValue >= 0
			])
		}
	}
}