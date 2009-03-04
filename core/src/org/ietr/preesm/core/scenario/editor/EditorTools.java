/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.core.scenario.editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
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
	public static class FileContentProvider extends WorkbenchContentProvider {

		Set<String> fileExtensions = null;

		public FileContentProvider(String fileExtension) {
			super();
			this.fileExtensions = new HashSet<String>();
			this.fileExtensions.add(fileExtension);
		}

		public FileContentProvider(Set<String> fileExtensions) {
			super();
			this.fileExtensions = new HashSet<String>(fileExtensions);
		}

		@Override
		public Object[] getChildren(Object element) {
			Object[] children = super.getChildren(element);
			List<Object> list = new ArrayList<Object>();
			for (Object o : children) {
				if (o instanceof IFile) {
					IFile file = (IFile) o;
					if (file.getFileExtension() != null
							&& fileExtensions.contains(file.getFileExtension())) {
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
	 * Directory tree Content provider that filters files
	 */
	public static class DirectoryContentProvider extends
			WorkbenchContentProvider {

		public DirectoryContentProvider() {
			super();
		}

		@Override
		public Object[] getChildren(Object element) {
			Object[] children = super.getChildren(element);
			List<Object> list = new ArrayList<Object>();
			for (Object o : children) {
				if (o instanceof Project) {
					list.add(o);
				} else if (o instanceof Folder) {
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
		Set<String> fileExtensions = new HashSet<String>();
		fileExtensions.add(fileExtension);

		return browseFiles(shell, title, fileExtensions);
	}

	/**
	 * Displays a file browser in a shell. The path is relative to the project.
	 */
	static public String browseFiles(Shell shell, String title,
			Set<String> fileExtensions) {
		String returnVal = "";

		ElementTreeSelectionDialog tree = null;

		if (fileExtensions == null) {
			tree = new ElementTreeSelectionDialog(shell, WorkbenchLabelProvider
					.getDecoratingWorkbenchLabelProvider(),
					new DirectoryContentProvider());
		} else {
			tree = new ElementTreeSelectionDialog(shell, WorkbenchLabelProvider
					.getDecoratingWorkbenchLabelProvider(),
					new FileContentProvider(fileExtensions));
		}
		tree.setAllowMultiple(false);
		tree.setInput(ResourcesPlugin.getWorkspace().getRoot());
		tree.setMessage(title);
		tree.setTitle(title);
		// opens the dialog
		if (tree.open() == Window.OK) {
			IPath fileIPath = null;
			if (fileExtensions == null) {
				fileIPath = ((IFolder) tree.getFirstResult()).getFullPath();
			} else {
				fileIPath = ((IFile) tree.getFirstResult()).getFullPath();
			}
			returnVal = fileIPath.toString();
		}

		return returnVal;
	}
}
