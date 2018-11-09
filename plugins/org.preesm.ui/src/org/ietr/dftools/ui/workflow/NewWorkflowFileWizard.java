/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
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
/**
 *
 */
package org.ietr.dftools.ui.workflow;

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
import org.ietr.dftools.graphiti.GraphitiModelPlugin;
import org.ietr.dftools.graphiti.model.Configuration;
import org.ietr.dftools.graphiti.model.Graph;
import org.ietr.dftools.graphiti.model.ObjectType;
import org.ietr.dftools.graphiti.ui.wizards.WizardSaveGraphPage;

// TODO: Auto-generated Javadoc
/**
 * This class provides a wizard to create a new workflow network.
 *
 * @author Matthieu Wipliez
 * @author mpelcat
 */
public class NewWorkflowFileWizard extends Wizard implements INewWizard {

  /** The selection. */
  private IStructuredSelection selection;

  /** The workbench. */
  private IWorkbench workbench;

  /**
   * Creates a new wizard.
   */
  public NewWorkflowFileWizard() {
    super();
    setNeedsProgressMonitor(true);
    setWindowTitle("New Workflow");
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.wizard.Wizard#addPages()
   */
  @Override
  public void addPages() {
    final WizardSaveGraphPage page = new WizardSaveGraphPage(this.selection);

    final Configuration configuration = GraphitiModelPlugin.getDefault().getConfiguration("Workflow");
    final ObjectType type = configuration.getGraphType("DFTools Workflow");

    page.setGraph(new Graph(configuration, type, true));
    page.setDescription("Create a new Workflow.");
    addPage(page);
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
    final WizardSaveGraphPage page = (WizardSaveGraphPage) getPage("saveGraph");
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
