/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
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

import java.io.IOException;
import java.io.InputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.ietr.dftools.graphiti.ui.editors.GraphEditor;

/**
 * This class provides a save as graph wizard.
 *
 * @author Matthieu Wipliez
 */
public class SaveAsWizard extends Wizard implements INewWizard {

  /** The selection. */
  private IStructuredSelection selection;

  /** The workbench. */
  private IWorkbench workbench;

  /**
   * Constructor for {@link SaveAsWizard}.
   */
  public SaveAsWizard() {
    super();
    setNeedsProgressMonitor(true);
    setWindowTitle("Save As...");
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.wizard.Wizard#addPages()
   */
  @Override
  public void addPages() {
    IWizardPage page = new WizardGraphTypePage(this.selection);
    page.setDescription("Save the graph with the chosen type.");
    addPage(page);

    addPage(new WizardConvertPage(this.selection));

    // To improve user experience, the selection is the editor's input file.
    IStructuredSelection currentSelection = this.selection;
    final Object obj = currentSelection.getFirstElement();
    if (obj instanceof GraphEditor) {
      final GraphEditor editor = (GraphEditor) obj;
      final IEditorInput input = editor.getEditorInput();
      if (input instanceof IFileEditorInput) {
        final IFile file = ((IFileEditorInput) input).getFile();
        currentSelection = new StructuredSelection(file);
      }
    }

    page = new WizardSaveGraphPage(currentSelection);
    page.setDescription("Save graph as.");
    addPage(page);
  }

  /**
   * Returns the workbench which was passed to <code>init</code>.
   *
   * @return the workbench
   */
  public IWorkbench getWorkbench() {
    return this.workbench;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
   * org.eclipse.jface.viewers.IStructuredSelection)
   */
  @Override
  public void init(final IWorkbench workbench, final IStructuredSelection selection) {
    this.selection = selection;
    this.workbench = workbench;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.wizard.Wizard#performFinish()
   */
  @Override
  public boolean performFinish() {
    final WizardConvertPage convertPage = (WizardConvertPage) getPage("convertGraph");
    final WizardSaveGraphPage page = (WizardSaveGraphPage) getPage("saveGraph");
    page.setGraph(convertPage.getGraph());

    final InputStream in = page.getInitialContents();
    if (in == null) {
      return false;
    }
    try {
      in.close();
    } catch (final IOException e) {
      // don't care because in is a byte array input stream
    }

    final IFile file = page.createNewFile();
    if (file == null) {
      return false;
    }

    // Open editor on new file.
    final IWorkbenchWindow dw = getWorkbench().getActiveWorkbenchWindow();
    try {
      if (dw != null) {
        BasicNewResourceWizard.selectAndReveal(file, dw);
        final IWorkbenchPage activePage = dw.getActivePage();
        if (activePage != null) {
          IDE.openEditor(activePage, file, true);
        }
      }
    } catch (final PartInitException e) {
      MessageDialog.openError(dw.getShell(), "Problem opening editor", e.getMessage());
    }

    return true;
  }
}
