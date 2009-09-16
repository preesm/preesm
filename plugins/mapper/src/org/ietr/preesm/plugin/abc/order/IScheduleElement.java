/**
 * 
 */
package org.ietr.preesm.plugin.abc.order;

import org.ietr.preesm.plugin.mapper.model.ImplementationVertexProperty;
import org.ietr.preesm.plugin.mapper.model.TimingVertexProperty;

/**
 * Element containing either a simple vertex or a group of synchronized elements that have the same total order.
 * 
 * @author mpelcat
 */
public interface IScheduleElement {

	public String getName();
	
	public TimingVertexProperty getTimingVertexProperty();
	
	public ImplementationVertexProperty getImplementationVertexProperty();
}
