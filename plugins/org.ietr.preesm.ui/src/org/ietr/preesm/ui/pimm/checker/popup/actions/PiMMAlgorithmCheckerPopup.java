/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy
 * 
 * [mpelcat,jnezan,kdesnos,jheulot,cguy]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
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
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	@Override
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
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
