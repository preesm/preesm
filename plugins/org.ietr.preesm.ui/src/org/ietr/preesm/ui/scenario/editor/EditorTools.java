/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2014)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.ietr.preesm.ui.scenario.editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.ietr.preesm.core.Activator;

// TODO: Auto-generated Javadoc
/**
 * Useful SWT methods.
 *
 * @author mpelcat
 */
public class EditorTools {

  /**
   * File tree Content provider that filters a given extension of files.
   */
  public static class FileContentProvider extends WorkbenchContentProvider {

    /** The file extensions. */
    Set<String> fileExtensions = null;

    /**
     * Instantiates a new file content provider.
     *
     * @param fileExtension
     *          the file extension
     */
    public FileContentProvider(final String fileExtension) {
      super();
      this.fileExtensions = new HashSet<>();
      this.fileExtensions.add(fileExtension);
    }

    /**
     * Instantiates a new file content provider.
     *
     * @param fileExtensions
     *          the file extensions
     */
    public FileContentProvider(final Set<String> fileExtensions) {
      super();
      this.fileExtensions = new HashSet<>(fileExtensions);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.model.BaseWorkbenchContentProvider#getChildren(java.lang.Object)
     */
    @Override
    public Object[] getChildren(final Object element) {
      final Object[] children = super.getChildren(element);
      final List<Object> list = new ArrayList<>();
      for (final Object o : children) {
        if (o instanceof IFile) {
          final IFile file = (IFile) o;
          if ((file.getFileExtension() != null) && this.fileExtensions.contains(file.getFileExtension())) {
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
   * Validates the selection based on the multi select and folder setting.
   */
  private static class SingleFileSelectionValidator implements ISelectionStatusValidator {

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.dialogs.ISelectionStatusValidator#validate(java.lang.Object[])
     */
    @Override
    public IStatus validate(final Object[] selection) {
      if ((selection.length == 1) && ((selection[0] instanceof IFile) || (selection[0] instanceof IFolder))) {
        return new Status(IStatus.OK, Activator.PLUGIN_ID, "");
      }
      return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "");
    }
  }

  /**
   * Directory tree Content provider that filters files.
   */
  public static class DirectoryContentProvider extends WorkbenchContentProvider {

    /**
     * Instantiates a new directory content provider.
     */
    public DirectoryContentProvider() {
      super();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.model.BaseWorkbenchContentProvider#getChildren(java.lang.Object)
     */
    @Override
    public Object[] getChildren(final Object element) {
      final Object[] children = super.getChildren(element);
      final List<Object> list = new ArrayList<>();
      for (final Object o : children) {
        if (o instanceof IProject) {
          list.add(o);
        } else if (o instanceof IFolder) {
          list.add(o);
        }
      }

      return list.toArray();
    }
  }

  /**
   * Displays a file browser in a shell. The path is relative to the project
   *
   * @param shell
   *          the shell
   * @param title
   *          the title
   * @param fileExtension
   *          the file extension
   * @return the string
   */
  public static String browseFiles(final Shell shell, final String title, final String fileExtension) {
    final Set<String> fileExtensions = new HashSet<>();
    fileExtensions.add(fileExtension);

    return EditorTools.browseFiles(shell, title, fileExtensions);
  }

  /**
   * Displays a file browser in a shell. The path is relative to the project.
   *
   * @param shell
   *          the shell
   * @param title
   *          the title
   * @param fileExtensions
   *          the file extensions
   * @return the string
   */
  public static String browseFiles(final Shell shell, final String title, final Set<String> fileExtensions) {
    String returnVal = "";

    ElementTreeSelectionDialog tree = null;

    if (fileExtensions == null) {
      tree = new ElementTreeSelectionDialog(shell, WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider(), new DirectoryContentProvider());
    } else {
      tree = new ElementTreeSelectionDialog(shell, WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider(), new FileContentProvider(fileExtensions));
    }
    tree.setAllowMultiple(false);
    tree.setInput(ResourcesPlugin.getWorkspace().getRoot());
    tree.setMessage(title);
    tree.setTitle(title);
    tree.setValidator(new SingleFileSelectionValidator());
    // opens the dialog
    if (tree.open() == Window.OK) {
      IPath fileIPath = null;
      if (fileExtensions == null) {
        if (tree.getFirstResult() instanceof IFolder) {
          fileIPath = ((IFolder) tree.getFirstResult()).getFullPath();
        } else {
          return returnVal;
        }
      } else {
        if (tree.getFirstResult() instanceof IFile) {
          fileIPath = ((IFile) tree.getFirstResult()).getFullPath();
        } else {
          return returnVal;
        }
      }
      returnVal = fileIPath.toString();
    }

    return returnVal;
  }
}
