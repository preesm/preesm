package org.abo.preesm.plugin.dataparallel.pojo

import java.util.List
import org.eclipse.xtend.lib.annotations.Accessors
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex
import org.abo.preesm.plugin.dataparallel.NodeChainGraph

/**
 * POJO to hold optional explode and implode nodes associated with a vertex. To be used
 * in {@link NodeChainGraph}.
 */
@org.eclipse.xtend.lib.annotations.Data class NodeChain {
	/**
	 * Optional associated explode instances connected to the output of this vertex
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	List<SDFForkVertex> explode
	
	/**
	 * Optional implode instances connected to the input of this vertex
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	List<SDFJoinVertex> implode
	
	/**
	 * The vertex under consideration
	 */
	@Accessors(PUBLIC_GETTER, PRIVATE_SETTER)
	SDFAbstractVertex vertex
	
	/**
	 * Constructor
	 */
	new(List<SDFForkVertex> explode, List<SDFJoinVertex> implode, SDFAbstractVertex vertex) {
		if(explode.empty) {
			this.explode = null
		} else {
			this.explode = explode
		}
		
		if(implode.empty) {
			this.implode = null
		} else {
			this.implode = implode
		}
		this.vertex = vertex
	}	
}