package org.ietr.preesm.experiment.ui.pimm.features;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.Actor;

public class AddActorFromRefinementFeature extends AbstractAddFeature {
	
	boolean hasDoneChanges = false;

	/**
	 * When a file is drag and dropped on an graph, the feature attempts to
	 * create a new {@link Actor} and to set this file as the refinement of the
	 * actor.
	 * 
	 * Works only for IDL, H and PI files.
	 * 
	 * @author kdesnos
	 * 
	 */
	public AddActorFromRefinementFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		if (!(context.getNewObject() instanceof IFile)) {
			return false;
		} else {
			String fileExtension = ((IFile) context.getNewObject())
					.getFileExtension();
			if (fileExtension.equals("pi") || fileExtension.equals("h")
					|| fileExtension.equals("idl")) {
				return true;
			}
			return false;
		}
	}

	@Override
	public PictogramElement add(IAddContext context) {
		// 1- Create and Add Actor
		CreateActorFeature createActorFeature = new CreateActorFeature(getFeatureProvider());
		CreateContext createContext = new CreateContext();
		createContext.setLocation(context.getX(),context.getY());
		createContext.setSize(context.getWidth(), context.getWidth());
		createContext.setTargetContainer(context.getTargetContainer());
		createContext.setTargetConnection(context.getTargetConnection());
		Object[] actors = createActorFeature.create(createContext);
		
		// Stop if actor creation was cancelled
		if(actors.length == 0 ){
			return null;
		} else {
			hasDoneChanges = true;
		}
		
		// 2- Set Refinement
		Actor actor = (Actor) actors[0];
		SetActorRefinementFeature setRefinementFeature = new SetActorRefinementFeature(
				getFeatureProvider());
		IPath newFilePath = ((IFile) context.getNewObject()).getFullPath();
		
		setRefinementFeature.setActorRefinement(actor, newFilePath);
		
		// 3- Create all ports corresponding to the refinement.
		// TODO
		
		return null;
	}

	@Override
	public boolean hasDoneChanges() {
		return this.hasDoneChanges;
	}
}
