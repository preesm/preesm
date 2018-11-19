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
package fi.abo.preesm.dataparallel

import org.preesm.algorithm.model.AbstractGraph
import org.preesm.algorithm.model.sdf.SDFAbstractVertex
import org.preesm.algorithm.model.sdf.SDFEdge
import org.preesm.algorithm.model.visitors.SDF4JException

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
