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
 
package org.ietr.preesm.core.scenario.editor.timings;

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
import org.ietr.preesm.core.ui.Activator;

/**
 * This class provides a save as excel sheet wizard
 * 
 * @author mpelcat
 */
public class SaveAsWizard extends Wizard implements INewWizard {

	private IWorkbench workbench;
	private ExcelTimingWriter writer;


	/**
	 * Constructor for {@link SaveAsWizard}.
	 */
	public SaveAsWizard(ExcelTimingWriter writer) {
		super();
		this.writer = writer;
		setNeedsProgressMonitor(true);
		setWindowTitle("Save timings As...");
	}

	@Override
	public void addPages() {
		WizardSaveExcelPage page = new WizardSaveExcelPage();
		page.setWriter(writer);
		page.setDescription("Save timings as.");
		addPage(page);
	}

	/**
	 * Returns the workbench which was passed to <code>init</code>.
	 * 
	 * @return the workbench
	 */
	public IWorkbench getWorkbench() {
		return workbench;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
	}

	@Override
	public boolean performFinish() {
		final WizardSaveExcelPage page = (WizardSaveExcelPage) getPage("saveTimings");

		InputStream in = page.getInitialContents();
		if (in == null) {
			return false;
		}

		IFile file = page.createNewFile();
		if (file == null) {
			return false;
		}

		// Open editor on new file.
		IWorkbenchWindow dw = Activator.getDefault()
		.getWorkbench().getActiveWorkbenchWindow();
		try {
			if (dw != null) {
				BasicNewResourceWizard.selectAndReveal(file, dw);
				IWorkbenchPage activePage = dw.getActivePage();
				if (activePage != null) {
					IDE.openEditor(activePage, file, true);
				}
			}
		} catch (PartInitException e) {
			MessageDialog.openError(dw.getShell(), "Problem opening editor", e
					.getMessage());
		}

		return true;
	}
}