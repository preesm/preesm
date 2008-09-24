/**
 * 
 */
package org.ietr.preesm.core.task;

import org.ietr.preesm.core.codegen.SourceFileList;

/**
 * This interface defines methods to translate generic source code to specific
 * source code.
 * 
 * @author Matthieu Wipliez
 * 
 */
public interface ICodeTranslation extends ITransformation {

	/**
	 * Translate generic source code to specific source code.
	 * 
	 * @param list
	 *            A {@link SourceFileList}.
	 */
	public TaskResult transform(SourceFileList list);

}
