package org.ietr.preesm.ui.pimm.checker.popup.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.ietr.preesm.core.scenario.serialize.ScenarioParser;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.pimm.algorithm.checker.PiMMAlgorithmChecker;
import org.ietr.preesm.ui.Activator;

/**
 * Class to launch a PiGraph check through pop-up menu
 * 
 * @author cguy
 * 
 */
public class PiMMAlgorithmCheckerPopup implements IObjectActionDelegate {

	private Shell shell;

	/**
	 * Constructor.
	 */
	public PiMMAlgorithmCheckerPopup() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		PiMMAlgorithmChecker checker = new PiMMAlgorithmChecker();
		try {

			IWorkbenchPage page = Activator.getDefault().getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			TreeSelection selection = (TreeSelection) page.getSelection();
			IFile file = (IFile) selection.getFirstElement();
			PiGraph graph = ScenarioParser.getPiGraph(file.getFullPath()
					.toString());

			StringBuffer message = new StringBuffer();
			if (checker.checkGraph(graph))
				message.append(checker.getOkMsg());
			else {
				if (checker.isErrors()) {
					message.append(checker.getErrorMsg());
					if (checker.isWarnings())
						message.append("\n");
				}
				if (checker.isWarnings())
					message.append(checker.getWarningMsg());
			}

			MessageDialog.openInformation(shell, "Checker", message.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
