package org.abo.preesm.plugin.dataparallel.fifo

import org.ietr.dftools.algorithm.model.sdf.SDFEdge

/**
 * {@link SDFEdge} can store {@link FifoActor} as their bean property
 * The key to get and set the actor is defined here.
 * <p>
 * FifoActor can also be set as an attribute of SDFEdge in future. Remove this 
 * class if that is the case
 * 
 * @author Sudeep Kanur
 */
class FifoActorBeanKey {
	public static String key = "fifoActor"	
}