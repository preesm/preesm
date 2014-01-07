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
package org.ietr.preesm.experiment.ui.piscenario.editor;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.ietr.preesm.experiment.core.piscenario.ActorNode;
import org.ietr.preesm.experiment.core.piscenario.Timing;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * The label provider displays informations to fill the multi-column tree for timing edition.
 * @author jheulot
 */
public class PiTimingsTreeLabelProvider extends LabelProvider implements ITableLabelProvider {
	private Image imageOk, imageError;
	private PiTimingsPage timingPage;
	
	PiTimingsTreeLabelProvider(PiTimingsPage _timingPage){
		super();
		timingPage = _timingPage;
		
		Bundle bundle = FrameworkUtil.getBundle(PiTimingsTreeLabelProvider.class);
		
	    URL url = FileLocator.find(bundle, new Path("icon/error.png"), null);
	    ImageDescriptor imageDcr = ImageDescriptor.createFromURL(url);
	    this.imageError = imageDcr.createImage();

	    url = FileLocator.find(bundle, new Path("icon/ok.png"), null);
	    imageDcr = ImageDescriptor.createFromURL(url);
	    this.imageOk = imageDcr.createImage();
	    
	}
	
	
	@Override
	public String getText(Object element) {
		return null;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (element instanceof ActorNode && !((ActorNode) element).isHierarchical()) {
			Timing timing = ((ActorNode)element).getTiming(timingPage.getSelectedOperatorType());
			if(timing != null){			
				switch(columnIndex){
				case 1: // Parsing Column
					if(timing.canParse())
						return imageOk;
					else
						return imageError;					
				case 2: // Evaluation Column
					if(timing.canEvaluate())
						return imageOk;
					else
						return imageError;	
				case 0: // Actors Column
				case 3: // Variables Column
				case 4: // Expression Column
					return null; 
				}				
			}
		} 
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Timing timing;
		if(columnIndex == 0){ // Actors Column
			return ((ActorNode)element).getName();
		}
		if (element instanceof ActorNode && !((ActorNode) element).isHierarchical()) {
			switch(columnIndex){
			case 1: // Parsing Column
			case 2: // Evaluation Column
				return null;
			case 3: // Variables Column
				timing = ((ActorNode)element).getTiming(timingPage.getSelectedOperatorType());
				if(timing != null)
					return timing.getInputParameters().toString(); 
				else 
					return "";
			case 4: // Expression Column
				timing = ((ActorNode)element).getTiming(timingPage.getSelectedOperatorType());
				if(timing != null)
					return timing.getStringValue(); 
				else 
					return "";
			}		
		} 
		return null;
	}

}
