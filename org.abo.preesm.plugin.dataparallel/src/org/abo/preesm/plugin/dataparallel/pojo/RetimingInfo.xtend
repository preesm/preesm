package org.abo.preesm.plugin.dataparallel.pojo

import java.util.List
import org.abo.preesm.plugin.dataparallel.fifo.FifoActorGraph
import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.xtend.lib.annotations.Accessors

/**
 * POJO to hold transient graphs.
 * <p>
 * Re-timing information is stored in a list of {@link FifoActorGraph}. Each
 * {@link FifoActorGraph} can represent transient graph of a strongly connected component.
 * If two strongly connected components share a transient graph, then the transient
 * graphs are merged together.
 * 
 * @author Sudeep Kanur
 */
@Data class RetimingInfo {
	@Accessors(PUBLIC_GETTER, PUBLIC_SETTER)
	List<FifoActorGraph> initializationGraphs
}