package org.ietr.preesm.experiment.ui.pimemoc.features;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
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
 * Custom feature in charge of opening an editor for the refinement of an actor.
 * 
 * If the refinement is a graphml file, the associated diagram will be opened.
 * Otherwise, the workbench default editor will be opened.
 * 
 * @author kdesnos
 * 
 */
public class OpenRefinementFeature extends AbstractCustomFeature {

	/**
	 * Default constructor for the {@link OpenRefinementFeature}
	 * 
	 * @param fp
	 *            the feature provider
	 */
	public OpenRefinementFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		// first check, if one Actor is selected
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			if (bo instanceof Actor) {
				// Check if the actor has a valid refinement
				URI refinementFile = ((Actor) bo).getRefinement().getFileURI();
				if (refinementFile != null) {
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
				// Check if the actor has a valid refinement
				URI refinementFile = ((Actor) bo).getRefinement().getFileURI();
				if (refinementFile != null) {
					IWorkbenchWindow dw = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow();

					IResource refResource = ResourcesPlugin.getWorkspace()
							.getRoot()
							.findMember(refinementFile.toPlatformString(true));

					// If the refinement is a graphml file, open a diagram
					// instead (if it exists)
					if (refResource.getFileExtension().equals("graphml")) {
						URI diagramFile = refinementFile.trimFileExtension()
								.appendFileExtension("diagram");
						IResource diagResource = ResourcesPlugin.getWorkspace()
								.getRoot()
								.findMember(diagramFile.toPlatformString(true));

						// Check if the diaram file exists
						if (diagResource == null) {
							MessageDialog.openError(
									dw.getShell(),
									"Problem opening editor",
									"No diagram file for "
											+ refinementFile.lastSegment());
						} else {
							refResource = diagResource;
						}
					}

					// Open the editor for the refinement
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
		return "Open the refinement associated to the Actor";
	}

	@Override
	public String getName() {
		return "Open associated refinement";
	}

	@Override
	public boolean hasDoneChanges() {
		return false;
	}
}
