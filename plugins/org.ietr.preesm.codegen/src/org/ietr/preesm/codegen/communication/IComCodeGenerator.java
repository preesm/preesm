/**
 * 
 */
package org.ietr.preesm.codegen.communication;

import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;

import org.ietr.preesm.codegen.model.types.CodeSectionType;

/**
 * Generating communication code (initialization and calls) for a given type of
 * Route Step
 * 
 * @author mpelcat
 */
public interface IComCodeGenerator {
	public void insertComs(SDFAbstractVertex vertex, CodeSectionType sectionType);
}
