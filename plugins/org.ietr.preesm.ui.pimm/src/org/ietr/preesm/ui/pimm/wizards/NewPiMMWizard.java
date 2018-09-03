/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2013)
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
package org.ietr.preesm.ui.pimm.wizards;

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

// TODO: Auto-generated Javadoc
/**
 * The Class NewPiMMWizard.
 */
public class NewPiMMWizard extends Wizard implements INewWizard {

  /** The selection. */
  private IStructuredSelection selection;

  /** The workbench. */
  private IWorkbench workbench;

  /**
   * Instantiates a new new pi MM wizard.
   */
  public NewPiMMWizard() {
    super();
    setNeedsProgressMonitor(true);
    setWindowTitle("New Algorithm (PiMM)");
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.wizard.Wizard#addPages()
   */
  @Override
  public void addPages() {
    final NewPiMMPage page = new NewPiMMPage("saveGraph", this.selection);
    page.setFileName("NewGraph.diagram");
    page.setDescription("Creates a new network design.");
    page.setFileExtension("diagram");
    addPage(page);

    // Configuration configuration = GraphitiModelPlugin.getDefault()
    // .getConfiguration("IP-XACT design");
    // ObjectType type = configuration.getGraphType("IP-XACT design");

    // page.setGraph(new Graph(configuration, type, true));
    // page.setDescription("Create a new architecture.");
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
    final NewPiMMPage page = (NewPiMMPage) getPage("saveGraph");
    final IFile file = page.createNewFile();
    if (file == null) {
      return false;
    }

    // Open editor on new file.
    final IWorkbenchWindow dw = this.workbench.getActiveWorkbenchWindow();
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
