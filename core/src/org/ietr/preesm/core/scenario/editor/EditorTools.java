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
	static public void browseFiles(Shell shell, Text filePath) {
		ElementTreeSelectionDialog tree = new ElementTreeSelectionDialog(shell,
				WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider(),
				new WorkbenchContentProvider());
		tree.setAllowMultiple(false);
		tree.setInput(ResourcesPlugin.getWorkspace().getRoot());
		tree.setMessage("Please select an existing file:");
		tree.setTitle("Choose an existing file");
		// opens the dialog
		if (tree.open() == Window.OK) {
			IPath fileIPath = ((IFile) tree.getFirstResult()).getFullPath(); 
			filePath.setText(fileIPath.toString());
		}
	}
}
