/**
 * 
 */
package org.ietr.preesm.core.codegen.printer;

/**
 * During code generation, a code element can visit himself several times 
 * Kept until I am sure we do not need it anymore
 * 
 * @author mpelcat
 */
public enum CodeZoneId {

	body, // Visit of the code element body
}
