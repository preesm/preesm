/**
 * 
 */
package org.ietr.preesm.codegen.communication;

import net.sf.dftools.algorithm.model.sdf.SDFAbstractVertex;

import org.ietr.preesm.codegen.model.main.SourceFileList;
import org.ietr.preesm.codegen.model.types.CodeSectionType;

/**
 * Generating communication code (initialization and calls) for a given type of
 * Route Step
 * 
 * @author mpelcat
 */
public interface IComCodeGenerator {
	/**
	 * Inserting communications for a given vertex;
	 * 
	 * @param vertex the communication vertex that is implemented
	 * @param sectionType the localization in source file
	 * @sourceFiles access point to other source files to check propeerties
	 */
	public void insertComs(SDFAbstractVertex vertex, CodeSectionType sectionType, SourceFileList sourceFiles);
}
