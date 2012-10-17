package org.ietr.preesm.experiment.ui.pimemoc.wizards;

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

public class NewPIMeMoCWizard extends Wizard implements INewWizard {
	
	private IStructuredSelection selection;
	private IWorkbench workbench;

	public NewPIMeMoCWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("New Algorithm (PIMeMoC)");
	}
	
	@Override
	public void addPages() {
		NewPIMeMoCPage page = new NewPIMeMoCPage("saveGraph",selection);
		page.setFileName("NewGraph.diagram");
		page.setDescription("Creates a new network design.");
		page.setFileExtension("diagram");
		addPage(page);
		
		//Configuration configuration = GraphitiModelPlugin.getDefault()
		//		.getConfiguration("IP-XACT design");
		//ObjectType type = configuration.getGraphType("IP-XACT design");

		//page.setGraph(new Graph(configuration, type, true));
		//page.setDescription("Create a new architecture.");
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		this.workbench = workbench;
	}

	@Override
	public boolean performFinish() {
		final NewPIMeMoCPage page = (NewPIMeMoCPage) getPage("saveGraph");
		IFile file = page.createNewFile();
		if (file == null) {
			return false;
		}

		// Open editor on new file.
		IWorkbenchWindow dw = workbench.getActiveWorkbenchWindow();
		try {
			if (dw != null) {
				BasicNewResourceWizard.selectAndReveal(file, dw);
				IWorkbenchPage activePage = dw.getActivePage();
				if (activePage != null) {
					IDE.openEditor(activePage, file, true);
				}
			}
		} catch (PartInitException e) {
			MessageDialog.openError(dw.getShell(), "Problem opening editor",
					e.getMessage());
		}

		return true;
	}

}
