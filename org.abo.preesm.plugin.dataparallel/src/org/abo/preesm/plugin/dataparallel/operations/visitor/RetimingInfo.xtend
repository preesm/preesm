package org.abo.preesm.plugin.dataparallel.operations.visitor

import java.util.List
import org.abo.preesm.plugin.dataparallel.fifo.FifoActorGraph
import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.xtend.lib.annotations.Accessors

/**
 * Class that contains information required to perform re-timing transformation
 * 
 * Re-timing information is stored in a {@link List} of {@link FifoActorGraph}. Each
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