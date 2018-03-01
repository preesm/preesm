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
package fi.abo.preesm.dataparallel.iterator

import fi.abo.preesm.dataparallel.NodeChainGraph
import fi.abo.preesm.dataparallel.PureDAGConstructor
import java.util.List
import java.util.logging.Logger
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.SDFGraph
import org.ietr.dftools.algorithm.model.sdf.visitors.ToHSDFVisitor
import org.ietr.dftools.algorithm.model.visitors.SDF4JException

/**
 * Helper builder class for {@link SrSDFDAGCoIterator}
 * 
 * @see {@link SrSDFDAGCoIterator}
 * @author Sudeep Kanur
 */
class SrSDFDAGCoIteratorBuilder {
	/**
	 * Logger instance
	 */
	var Logger logger
	
	/**
	 * A DAG as {@link SDFGraph} instance obtained from implementations
	 * of {@link PureDAGConstructor}
	 */
	var SDFGraph dag

	/**
	 * Node chain graph of the original SrSDF
	 */
	var NodeChainGraph ncg
	
	/**
	 * List of visitable nodes that are defined in the 
	 * {@link SrSDFDAGCoIteratorBuilder#dag} 
	 */
	var List<SDFAbstractVertex> visitableNodes
	
	/**
	 * Constructor
	 * 
	 * @param logger A Workflow logger
	 */
	new(Logger logger) {
		this.logger = logger
		this.dag = null
		this.ncg = null
		this.visitableNodes = null
	}
	
	new() {
		this(null)
	}
	
	/**
	 * Add DAG graph (not generator) produced from the output of a {@link PureDAGConstructor}
	 * implementor
	 * 
	 * @param dag DAG obtained by {@link PureDAGConstructor#getOutputGraph}
	 * @return Builder instance to continue building 
	 */
	public def SrSDFDAGCoIteratorBuilder addDAG(SDFGraph dag) {
		this.dag = dag
		return this
	}
	
	/**
	 * Add {@link NodeChainGraph} of single rate graph obtained by {@link ToHSDFVisitor}
	 * 
	 * @param ncg A {@link NodeChainGraph} of Single rate graph of the SDF
	 * @return Builder instance to continue building
	 */
	public def SrSDFDAGCoIteratorBuilder addNodeChainGraph(NodeChainGraph ncg) {
		this.ncg = ncg
		return this
	}
	
	/**
	 * Add visitable nodes
	 * <p>
	 * Visitable nodes are those nodes of the DAG that <i>must</i> be seen in the SrSDF graph
	 * 
	 * @param visitableNodes List of visitable nodes of DAG
	 * @return Builder instance to continue building
	 */
	public def SrSDFDAGCoIteratorBuilder addVisitableNodes(List<SDFAbstractVertex> visitableNodes){
		this.visitableNodes = visitableNodes
		return this
	}
	
	/**
	 * Return the coiterator instance
	 * 
	 * @return {@link SrSDFDAGCoIterator} instance
	 */
	public def SrSDFDAGCoIterator build() throws SDF4JException {
		if(dag === null) {
			throw new SDF4JException("Co-iterator builder needs a DAG. Use addDAG method.")
		}
		
		if(visitableNodes === null) {
			throw new SDF4JException("Co-iterator builder needs visitable nodes. 
									Use addVisitableNodes method.")
		}
		
		if(ncg === null) {
			throw new SDF4JException("Co-iterator builder needs node chain graphs.
									Use addNodeChainGraphs method.")
		}
		
		visitableNodes.forEach[node |
			if(!(dag.vertexSet.contains(node))) {
				throw new SDF4JException("Node: " + node.name + " does not exist in source graph")
			}
		]
		
		if(logger === null) {
			return new SrSDFDAGCoIterator(dag, ncg, visitableNodes)	
		} else {
			return new SrSDFDAGCoIterator(dag, ncg, visitableNodes, logger)
		}
	}
}
