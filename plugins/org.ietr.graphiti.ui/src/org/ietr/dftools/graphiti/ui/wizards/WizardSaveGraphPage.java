/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Alexandre Romana [a-romana@ti.com] (2008)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008 - 2011)
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
package org.ietr.dftools.graphiti.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.ietr.dftools.graphiti.io.GenericGraphWriter;
import org.ietr.dftools.graphiti.model.Configuration;
import org.ietr.dftools.graphiti.model.Graph;
import org.ietr.dftools.graphiti.model.ObjectType;
import org.ietr.dftools.graphiti.ui.GraphitiUiPlugin;

/**
 * This class provides a page for the save as graph wizard.
 *
 * @author Matthieu Wipliez
 */
public class WizardSaveGraphPage extends WizardNewFileCreationPage implements IGraphTypeSettable {

  /** The file name. */
  private String fileName;

  /** The graph. */
  private Graph graph;

  /**
   * Constructor for {@link WizardSaveGraphPage}.
   *
   * @param selection
   *          The current resource selection.
   */
  public WizardSaveGraphPage(final IStructuredSelection selection) {
    super("saveGraph", selection);

    // if the selection is a file, gets its file name and removes its
    // extension. Otherwise, let fileName be null.
    final Object obj = selection.getFirstElement();
    if (obj instanceof IFile) {
      final IFile file = (IFile) obj;
      final String ext = file.getFileExtension();
      this.fileName = file.getName();
      final int idx = this.fileName.indexOf(ext);
      if (idx != -1) {
        this.fileName = this.fileName.substring(0, idx - 1);
      }
    }

    setTitle("Choose file name and parent folder");
  }

  /**
   * Displays an error message with the given exception.
   *
   * @param message
   *          A description of the error.
   * @param exception
   *          An exception.
   */
  private void errorMessage(final String message, final Throwable exception) {
    final IWorkbench workbench = PlatformUI.getWorkbench();
    final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    final Shell shell = window.getShell();

    final IStatus status = new Status(IStatus.ERROR, GraphitiUiPlugin.PLUGIN_ID, message, exception);
    ErrorDialog.openError(shell, "Save error", "The file could not be saved.", status, IStatus.ERROR);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#getInitialContents()
   */
  @Override
  public InputStream getInitialContents() {
    // set graph name
    final String currentFileName = getFileName();
    if (currentFileName != null) {
      final IPath filePath = new Path(currentFileName).removeFileExtension();
      this.graph.setValue(ObjectType.PARAMETER_ID, filePath.toString());
    }

    // retrieve the IFile so we can get its location
    final IPath containerPath = getContainerFullPath();
    final IPath filePath = containerPath.append(getFileName());
    final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
    this.graph.setFileName(filePath);

    // writes graph
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final GenericGraphWriter writer = new GenericGraphWriter(this.graph);
    try {
      writer.write(file.getLocation().toString(), out);
      return new ByteArrayInputStream(out.toByteArray());
    } catch (final RuntimeException e) {
      errorMessage("Exception", e);
    }

    return null;
  }

  /**
   * Sets a new graph for this page.
   *
   * @param graph
   *          A {@link Graph}.
   */
  public void setGraph(final Graph graph) {
    this.graph = graph;
    final Configuration configuration = graph.getConfiguration();
    final ObjectType type = graph.getType();
    final String fileExt = configuration.getFileFormat().getFileExtension();
    setFileExtension(fileExt);
    if (this.fileName == null) {
      setFileName("New " + type.getName() + "." + fileExt);
    } else {
      setFileName(this.fileName + "." + fileExt);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.ietr.dftools.graphiti.ui.wizards.IGraphTypeSettable#setGraphType(org.ietr.dftools.graphiti.model.Configuration,
   * org.ietr.dftools.graphiti.model.ObjectType)
   */
  @Override
  public void setGraphType(final Configuration configuration, final ObjectType type) {
    // create an empty graph, may be overridden
    this.graph = new Graph(configuration, type, true);

    final String fileExt = configuration.getFileFormat().getFileExtension();
    if (this.fileName == null) {
      setFileName("New " + type.getName() + "." + fileExt);
    } else {
      setFileName(this.fileName + "." + fileExt);
    }
  }

}
