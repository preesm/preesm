/**
 * 
 */
package org.ietr.preesm.core.scenario.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Useful SWT methods
 * 
 * @author mpelcat
 */
public class EditorTools {

	/**
	 * Displays a file browser in a shell. The path is relative to the project
	 */
	static public String browseFiles(Shell shell, String title) {
		String returnVal = "";
		
		ElementTreeSelectionDialog tree = new ElementTreeSelectionDialog(shell,
				WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider(),
				new WorkbenchContentProvider());
		tree.setAllowMultiple(false);
		tree.setInput(ResourcesPlugin.getWorkspace().getRoot());
		tree.setMessage(title);
		tree.setTitle(title);
		// opens the dialog
		if (tree.open() == Window.OK) {
			IPath fileIPath = ((IFile) tree.getFirstResult()).getFullPath(); 
			returnVal = fileIPath.toString();
		}
		
		return returnVal;
	}
}
