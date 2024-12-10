/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2010 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2014)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2010 - 2011)
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
package org.ietr.dftools.graphiti.model;

import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.NewWizardAction;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.ietr.dftools.graphiti.GraphitiModelPlugin;

/**
 * This class defines a default refinement policy.
 *
 * @author Matthieu Wipliez
 *
 */
public class DefaultRefinementPolicy implements IRefinementPolicy {

  /**
   * This class provides a listener for the {@link IResourceChangeEvent#POST_BUILD} event.
   *
   * @author Matthieu Wipliez
   *
   */
  private final class NewFileListener implements IResourceChangeListener {

    /** The refinement. */
    private IPath refinement;

    /** The vertex. */
    private final Vertex vertex;

    /**
     * Instantiates a new new file listener.
     *
     * @param vertex
     *          the vertex
     */
    public NewFileListener(final Vertex vertex) {
      this.vertex = vertex;
    }

    /**
     * Returns the first {@link IResource} added to the workspace.
     *
     * @param delta
     *          The {@link IResourceDelta} obtained from an {@link IResourceChangeEvent}.
     * @return The first {@link IResource} added to the workspace.
     */
    private IResource findAddedFile(final IResourceDelta delta) {
      IResourceDelta[] deltas = delta.getAffectedChildren(IResourceDelta.CHANGED);
      if (deltas.length == 0) {
        deltas = delta.getAffectedChildren(IResourceDelta.ADDED);
        return deltas[0].getResource();
      } else {
        return findAddedFile(deltas[0]);
      }
    }

    /**
     * Gets the refinement.
     *
     * @return the refinement
     */
    public IPath getRefinement() {
      return this.refinement;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.
     * IResourceChangeEvent)
     */
    @Override
    public void resourceChanged(final IResourceChangeEvent event) {
      final IResource resource = findAddedFile(event.getDelta());
      if (resource instanceof IFile) {
        this.refinement = getRefinementValue(this.vertex, (IFile) resource);
      }
    }
  }

  /**
   * Execute the {@link NewWizardAction}, and listens for resource change in the workspace to find out the file added
   * before calling {@link #setRefinement(IWorkbenchPage, IFile)} on it.
   *
   * @param vertex
   *          the vertex
   * @param shell
   *          The active window's {@link Shell}.
   * @return the i path
   */
  protected IPath createNewFile(final Vertex vertex, final Shell shell) {
    final IWorkbench workbench = PlatformUI.getWorkbench();
    final NewWizardAction action = new NewWizardAction(workbench.getActiveWorkbenchWindow());

    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final NewFileListener listener = new NewFileListener(vertex);
    workspace.addResourceChangeListener(listener, IResourceChangeEvent.POST_BUILD);
    action.run();
    workspace.removeResourceChangeListener(listener);

    return listener.getRefinement();
  }

  /**
   * Returns the absolute path of the given refinement using the given parent filename.
   *
   * @param parent
   *          parent file name
   * @param refinement
   *          a refinement
   * @return the absolute path of the refinement of the given vertex
   */
  protected IPath getAbsolutePath(final IPath parent, final String refinement) {
    // get the path from the refinement
    IPath path = new Path(refinement);
    if (!path.isAbsolute()) {
      final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
      final IFile file = root.getFile(parent);
      path = file.getParent().getFullPath().append(path);
    }

    return path;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.graphiti.model.IRefinementPolicy#getNewRefinement(org.ietr.dftools.graphiti.model.Vertex)
   */
  @Override
  public IPath getNewRefinement(final Vertex vertex) {
    final IWorkbench workbench = PlatformUI.getWorkbench();
    final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    final Shell shell = window.getShell();

    // prompts the user to choose a file
    final String message = "The selected vertex can be refined by an existing "
        + "file, or by a new file you can create.";
    final MessageDialog dialog = new MessageDialog(shell, "Set/Update Refinement", null, message,
        MessageDialog.QUESTION, new String[] { "Use an existing file", "Create a new file" }, 0);
    final int index = dialog.open();
    if (index == 0) {
      return useExistingFile(vertex, shell);
    } else if (index == 1) {
      return createNewFile(vertex, shell);
    } else {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.graphiti.model.IRefinementPolicy#getRefinement(org.ietr.dftools.graphiti.model.Vertex)
   */
  @Override
  public IPath getRefinement(final Vertex vertex) {
    if (vertex != null) {
      final Object refinement = vertex.getValue(ObjectType.PARAMETER_REFINEMENT);
      if (refinement instanceof IPath) {
        return (IPath) refinement;
      }

      if (refinement instanceof String) {
        return getAbsolutePath(vertex.getParent().getFileName(), (String) refinement);
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.graphiti.model.IRefinementPolicy#getRefinementFile(org.ietr.dftools.graphiti.model.Vertex)
   */
  @Override
  public IFile getRefinementFile(final Vertex vertex) {
    final IPath refinement = getRefinement(vertex);
    if (refinement == null) {
      return null;
    }

    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    return root.getFile(refinement);
  }

  /**
   * Returns the refinement value corresponding to the given file. This method automatically uses relative or absolute
   * form depending on the location of file compared to {@link #editedFile}.
   *
   * @param vertex
   *          the vertex
   * @param file
   *          The file refinining the selected vertex.
   * @return A {@link String} with the refinement value.
   */
  protected IPath getRefinementValue(final Vertex vertex, final IFile file) {
    return file.getFullPath();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.graphiti.model.IRefinementPolicy#isRefinable(org.ietr.dftools.graphiti.model.Vertex)
   */
  @Override
  public boolean isRefinable(final Vertex vertex) {
    if (vertex != null) {
      final List<Parameter> parameters = vertex.getParameters();
      for (final Parameter parameter : parameters) {
        if (parameter.getName().equals(ObjectType.PARAMETER_REFINEMENT)) {
          return true;
        }
      }
    }

    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.graphiti.model.IRefinementPolicy#setRefinement(org.ietr.dftools.graphiti.model.Vertex,
   * org.eclipse.core.runtime.IPath)
   */
  @Override
  public IPath setRefinement(final Vertex vertex, final IPath refinement) {
    return (IPath) vertex.setValue(ObjectType.PARAMETER_REFINEMENT, refinement);
  }

  /**
   * Ask the user to choose an existing file to refine the selected vertex.
   *
   * @param vertex
   *          the vertex
   * @param shell
   *          The active window's {@link Shell}.
   * @return the i path
   */
  protected IPath useExistingFile(final Vertex vertex, final Shell shell) {
    final ElementTreeSelectionDialog tree = new ElementTreeSelectionDialog(shell,
        WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider(), new WorkbenchContentProvider());
    tree.setAllowMultiple(false);
    tree.setInput(ResourcesPlugin.getWorkspace().getRoot());
    tree.setMessage("Please select an existing file:");
    tree.setTitle("Choose an existing file");
    tree.setValidator(selection -> {
      if ((selection.length == 1) && (selection[0] instanceof IFile)) {
        final IFile file = (IFile) selection[0];
        final String message = "Vertex refinement: " + getRefinementValue(vertex, file);
        return new Status(IStatus.OK, GraphitiModelPlugin.PLUGIN_ID, message);
      }

      return new Status(IStatus.ERROR, GraphitiModelPlugin.PLUGIN_ID,
          "Only files can be selected, not folders nor projects");
    });

    // initial selection
    IResource resource = getRefinementFile(vertex);
    if (resource == null) {
      resource = vertex.getParent().getFile().getParent();
    }
    tree.setInitialSelection(resource);

    // opens the dialog
    if (tree.open() == Window.OK) {
      final IFile file = (IFile) tree.getFirstResult();
      if (file != null) {
        return getRefinementValue(vertex, file);
      }
    }

    return null;
  }

}
