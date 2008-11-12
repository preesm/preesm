/**
 * 
 */
package org.ietr.preesm.core.codegen.printer;

/**
 * During code generation, a code element visits himself
 * several times and
 * and 
 * @author mpelcat
 */
public enum CodeZoneId {

	
	begin, // Visit at the beginning of a code element
	begin_2, // Visit at the beginning of a code element

	body, // Visit of the code element body
	body_2, // Visit of the code element body
	
	end, // Visit at the end of a code element
	
	loopBegin, // Visit at the beginning of a loop
	loopEnd, // Visit at the end of a loop
	loop // Single visit in a loop
}
