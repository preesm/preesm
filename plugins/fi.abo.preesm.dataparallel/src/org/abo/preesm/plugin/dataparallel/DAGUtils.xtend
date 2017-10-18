package org.abo.preesm.plugin.dataparallel

import org.ietr.dftools.algorithm.model.AbstractGraph
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFEdge
import org.ietr.dftools.algorithm.model.visitors.SDF4JException

/**
 * Static class that has various utility functions related
 * to SDF, DAG and HSDF
 * 
 * @author Sudeep Kanur
 */
class DAGUtils {
	
	/**
	 * Find a vertex of "source" graph occurring "dest" graph
	 * 
	 * @param vertex The vertex to be found in "dest" graph
	 * @param source The vertex is initially in this graph
	 * @param dest Check the occurrence in this graph
	 * @return vertex in "dest" graph 
	 * @throws SDF4JException If the vertex is not present in source graph
	 */
	static def SDFAbstractVertex findVertex(SDFAbstractVertex vertex
											, AbstractGraph<SDFAbstractVertex, SDFEdge> source
											, AbstractGraph<SDFAbstractVertex, SDFEdge> dest) 
											throws SDF4JException {
		if(!source.vertexSet.contains(vertex)) {
			throw new SDF4JException("The given vertex is not in source graph. Check the order")
		}
		
		// We define when two normal vertices are equal to each other
		// 1. When the names are equal. As the names are generated to be unique, this test alone 
		// must stand
		
		// Due to the decision that dangling special actors won't be removed, we can't check
		// the below conditions any more!
		// 2. When they have same quantity of source and sink interface
		// 3. When the name of source and sink interfaces are same
		
		// Note that source and destination vertices need not be same. So
		// we restrict only to source and sink interfaces
		val destVertices = dest.vertexSet.filter[ destVertex |
			(destVertex.name.equals(vertex.name))
//			(destVertex.sources.size == vertex.sources.size) &&
//			(destVertex.sinks.size == vertex.sinks.size) &&
//			(destVertex.sources.forall[sourceInterface | 
//				vertex.sources.filter[vertexSource |
//					vertexSource.name.equals(sourceInterface.name)
//				].size == 1
//			]) &&
//			(destVertex.sinks.forall[sinkInterface |
//				vertex.sinks.filter[vertexSink |
//					vertexSink.name.equals(sinkInterface.name)
//				].size == 1
//			])
		]
		
		if(destVertices.size > 1) {
			throw new SDF4JException("The vertex " + vertex + " matches more than two nodes:\n" + 
				destVertices)
		}

		if(destVertices.size == 1) {
			return destVertices.toList.get(0)
		} else {
			return null
		}
	} 
}