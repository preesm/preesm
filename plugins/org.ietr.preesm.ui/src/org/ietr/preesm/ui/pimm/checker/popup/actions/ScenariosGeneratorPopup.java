/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy, Yaset Oliva Venegas
 *
 * [mpelcat,jnezan,kdesnos,jheulot,cguy,yoliva]@insa-rennes.fr
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software. You can use,
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty and the software's author, the holder of the
 * economic rights, and the successive licensors have only limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading, using, modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean that it is complicated to manipulate, and that also
 * therefore means that it is reserved for developers and experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and, more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.ui.pimm.checker.popup.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.ietr.preesm.core.scenarios.generator.ScenariosGenerator;
import org.ietr.preesm.ui.Activator;
import org.ietr.preesm.ui.wizards.PreesmProjectNature;

/**
 * Class for pop-up menu on IProjects, allowing to generate PreesmScenarios from
 * the content of the Algo and Archi folders
 * 
 * @author cguy
 *
 */
public class ScenariosGeneratorPopup implements IObjectActionDelegate {

	@Override
	public void run(IAction action) {
		ScenariosGenerator generator = new ScenariosGenerator();
		try {
			// Get the selected IProject
			IWorkbenchPage page = Activator.getDefault().getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			TreeSelection selection = (TreeSelection) page.getSelection();
			IProject project = (IProject) selection.getFirstElement();
			// If it is a Preesm project, generate the PreesmScenarios from the
			// content of the Algo and Archi folders
			if (project.hasNature(PreesmProjectNature.ID))
				generator.generateAndSaveScenarios(project);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

}
