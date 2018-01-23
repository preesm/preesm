/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
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

import java.io.InputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.ietr.preesm.ui.PreesmUIPlugin;

/**
 * This class provides a save as excel sheet wizard.
 *
 * @author mpelcat
 * @author kdesnos
 */
public class SaveAsWizard extends Wizard implements INewWizard {

  /** The workbench. */
  private IWorkbench workbench;

  /** The writer. */
  private final ExcelWriter writer;

  /**
   * What is saved (Timings, Variables, ...)
   */
  private final String _savedObject;

  /**
   * Constructor for {@link SaveAsWizard}.
   *
   * @param writer
   *          the writer
   * @param savedObject
   *          the saved object
   */
  public SaveAsWizard(final ExcelWriter writer, final String savedObject) {
    super();
    this.writer = writer;
    this._savedObject = savedObject;
    setNeedsProgressMonitor(true);
    setWindowTitle("Save " + this._savedObject + " As...");
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.wizard.Wizard#addPages()
   */
  @Override
  public void addPages() {
    final WizardSaveExcelPage page = new WizardSaveExcelPage(this._savedObject);
    page.setWriter(this.writer);
    page.setDescription("Save " + this._savedObject + " As...");
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
   * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
   */
  @Override
  public void init(final IWorkbench workbench, final IStructuredSelection selection) {
    this.workbench = workbench;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.wizard.Wizard#performFinish()
   */
  @Override
  public boolean performFinish() {
    final WizardSaveExcelPage page = (WizardSaveExcelPage) getPage("save" + this._savedObject);

    final InputStream in = page.getInitialContents();
    if (in == null) {
      return false;
    }

    final IFile file = page.createNewFile();
    if (file == null) {
      return false;
    }

    // Open editor on new file.
    final IWorkbenchWindow dw = PreesmUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
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
