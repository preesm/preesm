/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
 * Richard Thavot <Richard.Thavot@insa-rennes.fr> (2011)
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
package org.ietr.dftools.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.ietr.dftools.workflow.Activator;

// TODO: Auto-generated Javadoc
/**
 * Useful UI methods.
 *
 * @author mpelcat
 */
public class FileUtils {

  /**
   * File tree Content provider that filters a given extension of files.
   */
  public static class FileContentProvider extends WorkbenchContentProvider {

    /** The file extensions. */
    final Collection<String> fileExtensions;

    /**
     * Instantiates a new file content provider.
     *
     * @param fileExtension
     *          the file extension
     */
    public FileContentProvider(final String fileExtension) {
      this(Collections.singleton(fileExtension));
    }

    /**
     * Instantiates a new file content provider.
     *
     * @param fileExtensions
     *          the file extensions
     */
    public FileContentProvider(final Collection<String> fileExtensions) {
      super();
      this.fileExtensions = Collections.unmodifiableCollection(fileExtensions);
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

    private final boolean filterFolders;

    public SingleFileSelectionValidator(final boolean filterFolders) {
      this.filterFolders = filterFolders;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.dialogs.ISelectionStatusValidator#validate(java.lang.Object[])
     */
    @Override
    public IStatus validate(final Object[] selection) {
      final boolean hasOneElementOnly = selection.length == 1;
      if (hasOneElementOnly) {
        final Object object = selection[0];
        final boolean selectedItemIsFile = object instanceof IFile;
        final boolean selectedItemIsFolder = object instanceof IFolder;
        final boolean selectedItemIsWhatsExcepted = (selectedItemIsFile && this.filterFolders)
            || (selectedItemIsFolder && !this.filterFolders);
        if (selectedItemIsWhatsExcepted) {
          return new Status(IStatus.OK, Activator.PLUGIN_ID, "");
        }
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

  public static IPath browseFiles(final String title, final String fileExtension) {
    return FileUtils.browseFiles(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title, title,
        fileExtension);
  }

  public static IPath browseFiles(final String title, final Collection<String> fileExtensions) {
    return FileUtils.browseFiles(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title, title,
        fileExtensions);
  }

  public static IPath browseFiles(final String title, final String message, final String fileExtension) {
    return FileUtils.browseFiles(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title, message,
        fileExtension);
  }

  public static IPath browseFiles(final String title, final String message, final Collection<String> fileExtensions) {
    return FileUtils.browseFiles(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title, message,
        fileExtensions);
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
  public static IPath browseFiles(final Shell shell, final String title, final String fileExtension) {
    return FileUtils.browseFiles(shell, title, title, Collections.singleton(fileExtension));
  }

  public static IPath browseFiles(final Shell shell, final String title, final Collection<String> fileExtensions) {
    return FileUtils.browseFiles(shell, title, title, fileExtensions);
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
  public static IPath browseFiles(final Shell shell, final String title, final String message,
      final String fileExtension) {
    return FileUtils.browseFiles(shell, title, message, Collections.singleton(fileExtension));
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
  public static IPath browseFiles(final Shell shell, final String title, final String message,
      final Collection<String> fileExtensions) {
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    final ILabelProvider decoratingWorkbenchLabelProvider = WorkbenchLabelProvider
        .getDecoratingWorkbenchLabelProvider();

    ElementTreeSelectionDialog tree = null;

    if (fileExtensions == null) {
      tree = new ElementTreeSelectionDialog(shell, decoratingWorkbenchLabelProvider, new DirectoryContentProvider());
      tree.setValidator(new SingleFileSelectionValidator(false));
    } else {
      tree = new ElementTreeSelectionDialog(shell, decoratingWorkbenchLabelProvider,
          new FileContentProvider(fileExtensions));
      tree.setValidator(new SingleFileSelectionValidator(true));

      tree.addFilter(new ViewerFilter() {
        @Override
        public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
          final boolean isFile = element instanceof IFile;
          final boolean isContainer = element instanceof IContainer;
          if (isFile) {
            return true;
          } else if (isContainer) {
            final IContainer container = (IContainer) element;
            try {
              container.accept(resource -> {
                final boolean contains = fileExtensions.contains(resource.getFileExtension());
                if (contains && (resource instanceof IFile)) {
                  throw new CoreException(new Status(IStatus.OK, title, message));
                }
                return true;
              });
            } catch (final CoreException e) {
              final IStatus status = e.getStatus();
              if ((status.getSeverity() == IStatus.OK) && title.equals(status.getPlugin())) {
                return true;
              }
              return false;
            }
            return false;
          } else {
            return false;
          }
        }
      });

    }
    tree.setAllowMultiple(false);
    tree.setInput(root);
    tree.setMessage(message);
    tree.setTitle(title);

    // opens the dialog
    if (tree.open() == Window.OK) {
      IPath fileIPath = null;
      if (fileExtensions == null) {
        fileIPath = ((IFolder) tree.getFirstResult()).getFullPath();
      } else {
        fileIPath = ((IFile) tree.getFirstResult()).getFullPath();
      }
      return fileIPath;
    }

    return null;
  }
}
