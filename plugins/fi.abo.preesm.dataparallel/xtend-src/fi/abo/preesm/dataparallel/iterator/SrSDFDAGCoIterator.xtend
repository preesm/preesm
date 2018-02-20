/**
 * Copyright or © or Copr. Åbo Akademi University (2017), IETR/INSA - Rennes (2017):
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Sudeep Kanur <skanur@abo.fi> (2017)
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
package fi.abo.preesm.dataparallel.iterator

import fi.abo.preesm.dataparallel.NodeChainGraph
import fi.abo.preesm.dataparallel.PureDAGConstructor
import java.util.Iterator
import java.util.LinkedList
import java.util.List
import java.util.Queue
import java.util.logging.Logger
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.visitors.ToHSDFVisitor
import org.jgrapht.traverse.TopologicalOrderIterator
import java.util.NoSuchElementException
import fi.abo.preesm.dataparallel.DAGUtils

/**
 * The iterator walks one graph w.r.t to nodes present in the another graph such that atleast the 
 * nodes of a given set from the source graph is visited.
 * <p>
 * Eg. Let "source" graph be a DAG and "dest" graph be a SrSDF. 
 * Let visitable nodes be set of nodes of "source" graph that must be seen in "dest" graph
 * Let startInstance be the starting node in "dest" from which walking must be carried out.
 * Then,
 * This iterator walks the "dest" graph such that all the nodes of
 * "visitable nodes" are seen. Additional nodes of "dest" can be seen in order to reach all the nodes
 * of the "visitable nodes". 
 * <p>
 * Note that "visitable nodes" is defined w.r.t to "source" graph and we need to
 * find explicit equivalent of these in the "dest" graph.
 * <p>
 * The class is written explicitly to be used with a {@link NodeChainGraph} of Single Rate Graph or 
 * HSDF as "dest" graph
 * and the custom DAG (obtained from {@link PureDAGConstructor} implementations).
 * <p>
 * Construct the iterator through the builder {@link SrSDFDAGCoIteratorBuilder}
 * 
 * Note: The iteration is eagerly evaluated at the time of construction
 * 
 * @author Sudeep Kanur 
 */
class SrSDFDAGCoIterator implements Iterator<SDFAbstractVertex>  {
	
	/**
	 * Logging instance
	 */
	val Logger logger
	
	/**
	 * Queue to hold instances
	 */
	val Queue<SDFAbstractVertex> queue
	
	/**
	 * Constructor
	 * 
	 * @param dag DAG obtained from implementations of {@link PureDAGConstructor}
	 * @param ncg A {@link NodeChainGraph} constructed from the single rate SDF graph (SrSDF) 
	 * 	obtained from {@link ToHSDFVisitor}
	 * @param visitableNodes Traverse such that only these nodes are seen
	 * @param logger A logger instance
	 */
	protected new(SDFGraph dag, NodeChainGraph ncg, List<SDFAbstractVertex>visitableNodes, 
		Logger logger
	) {
		this.logger = logger
		this.queue = new LinkedList<SDFAbstractVertex>
		val mainNodes = ncg.nodechains.keySet
		val topo = new TopologicalOrderIterator(dag)
		while(topo.hasNext) {
			val node = topo.next
			val srsdfNode = DAGUtils.findVertex(node, dag, ncg.graph)
			if(srsdfNode !== null && visitableNodes.contains(node) && mainNodes.contains(srsdfNode)) {
				val chain = ncg.nodechains.get(srsdfNode)
				if(chain.implode !== null) {
					for(imp: chain.implode) {
						queue.offer(imp)
					}	
				}
				queue.offer(chain.vertex)
				if(chain.explode !== null) {
					for(exp: chain.explode) {
						queue.offer(exp)
					}	
				}
			}
		}
	}
	
	/**
	 * Constructor
	 * 
	 * @param dag DAG obtained from implementations of {@link PureDAGConstructor}
	 * @param ncg A {@link NodeChainGraph} constructed from the single rate SDF graph (SrSDF) 
	 * 	obtained from {@link ToHSDFVisitor}
	 * @param visitableNodes Traverse such that only these nodes are seen
	 */	
	protected new(SDFGraph dag, NodeChainGraph ncg, List<SDFAbstractVertex>visitableNodes) {
			this(dag, ncg, visitableNodes, null)
	}
	
	override hasNext() {
		return !queue.isEmpty
	}
	
	override next() {
		val node = queue.poll
		if(node === null) {
			throw new NoSuchElementException
		}
		return node
	}
	
}
