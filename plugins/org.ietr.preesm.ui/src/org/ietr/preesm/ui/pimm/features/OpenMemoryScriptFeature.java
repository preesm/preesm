/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
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
package org.ietr.preesm.ui.pimm.features;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.ietr.preesm.experiment.model.pimm.Actor;

/**
 * Custom feature in charge of opening an editor for the memory script of an actor.
 * 
 * the workbench default editor will be opened.
 * 
 * @author kdesnos
 * 
 */
public class OpenMemoryScriptFeature extends AbstractCustomFeature {

	/**
	 * Default constructor for the {@link OpenMemoryScriptFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public OpenMemoryScriptFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		// first check, if one Actor is selected
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof Actor) {
				// Check if the actor has a valid memory script
				IPath memoryScriptPath = ((Actor) bo).getMemoryScriptPath();
				if (memoryScriptPath != null) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void execute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		// first check, if one Actor is selected
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof Actor) {
				// Check if the actor has a valid path to memory script
				IPath memoryScriptPath = ((Actor) bo).getMemoryScriptPath();
				if (memoryScriptPath != null) {
					IWorkbenchWindow dw = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow();

					IResource refResource = ResourcesPlugin.getWorkspace()
							.getRoot().getFile(memoryScriptPath);

					// Open the editor for the memory script
					try {
						if (dw != null) {
							IWorkbenchPage activePage = dw.getActivePage();
							if (activePage != null) {
								IDE.openEditor(activePage, (IFile) refResource,
										true);
							}
						}
					} catch (PartInitException e) {
						MessageDialog.openError(dw.getShell(),
								"Problem opening editor", e.getMessage());
					}
				}
			}
		}
	}

	@Override
	public String getDescription() {
		return "Open the memory script associated to the Actor";
	}

	@Override
	public String getName() {
		return "Open associated memory script";
	}

	@Override
	public boolean hasDoneChanges() {
		return false;
	}
}
