/**
 * 
 */
package org.ietr.preesm.core.scenario.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
	 * File tree Content provider that filters a given extension of files
	 */
	public static class CoreContentProvider extends WorkbenchContentProvider {

		String fileExtension = null;

		public CoreContentProvider(String fileExtension) {
			super();
			this.fileExtension = fileExtension;
		}

		@Override
		public Object[] getChildren(Object element) {
			Object[] children = super.getChildren(element);
			List<Object> list = new ArrayList<Object>();
			for (Object o : children) {
				if (o instanceof IFile) {
					IFile file = (IFile) o;
					if (file.getFileExtension() != null
							&& file.getFileExtension().equals(fileExtension)) {
						list.add(o);
					}
				} else {
					list.add(o);
				}
			}

			return list.toArray();
		}
	}

	/**
	 * Displays a file browser in a shell. The path is relative to the project
	 */
	static public String browseFiles(Shell shell, String title,
			String fileExtension) {
		String returnVal = "";

		ElementTreeSelectionDialog tree = new ElementTreeSelectionDialog(shell,
				WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider(),
				new CoreContentProvider(fileExtension));
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
