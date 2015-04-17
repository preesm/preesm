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

import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.runtime.IPath;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.ietr.preesm.experiment.model.pimm.Actor;

@SuppressWarnings("restriction")
/**
 * When a file is drag and dropped on an actor, the feature attempts to set 
 * this file as the refinement of the actor.
 * 
 * Works only for IDL, H and PI files.
 * @author kdesnos
 *
 */
public class AddRefinementFeature extends AbstractAddFeature {

	public AddRefinementFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		if (!(context.getNewObject() instanceof File 
				|| context.getNewObject() instanceof ITranslationUnit)) {
			return false;
		} else {
			String fileExtension;
			if(context.getNewObject() instanceof File){
				fileExtension = ((File) context.getNewObject()).getFileExtension();
			}else{
				fileExtension = ((ITranslationUnit) context.getNewObject()).getPath().getFileExtension();
			}
			
			if (fileExtension.equals("pi") || fileExtension.equals("h")
					|| fileExtension.equals("idl")) {
				return true;
			}
			return false;
		}
	}

	@Override
	public PictogramElement add(IAddContext context) {
		SetActorRefinementFeature setRefinementFeature = new SetActorRefinementFeature(
				getFeatureProvider());

		IPath newFilePath;
		if(context.getNewObject() instanceof File){
			newFilePath = ((File) context.getNewObject()).getFullPath();
		}else{
			newFilePath = ((ITranslationUnit) context.getNewObject()).getPath();
		}
	
		Actor actor = (Actor) getBusinessObjectForPictogramElement(context
				.getTargetContainer());

		setRefinementFeature.setActorRefinement(actor, newFilePath);

		return null;
	}

}
