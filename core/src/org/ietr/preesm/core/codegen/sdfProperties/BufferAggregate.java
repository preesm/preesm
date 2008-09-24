/**
 * 
 */
package org.ietr.preesm.core.codegen.sdfProperties;

import java.util.ArrayList;

/**
 * Objects used to tag the SDF edges when they represent more
 * than one buffer each. One edge contains one aggregate in
 * its propertybean. The Aggregate is composed of BufferDefinitions
 * with details on their size, type...
 * 
 * @author mpelcat
 *
 */
public class BufferAggregate extends ArrayList<BufferProperties> {

	/**
	 * ID used to reference the element in a property bean
	 */
	public static final String propertyBeanName = "bufferAggregate";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
