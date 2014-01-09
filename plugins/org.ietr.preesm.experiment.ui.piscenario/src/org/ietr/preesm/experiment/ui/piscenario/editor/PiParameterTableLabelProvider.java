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
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Table;
import org.ietr.preesm.experiment.core.piscenario.ActorNode;
import org.ietr.preesm.experiment.core.piscenario.ParameterValue;
import org.ietr.preesm.experiment.core.piscenario.ParameterValue.ParameterType;
import org.ietr.preesm.experiment.core.piscenario.Timing;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * The label provider displays informations to fill the multi-column tree for parameter edition.
 * @author jheulot
 */
public class PiParameterTableLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {
	private final Table table;
	private Image imageOk, imageError;
	
	PiParameterTableLabelProvider(Table _table){
		super();
		table = _table;
		
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
		ParameterValue paramValue = ((ParameterValue)element);
		if(columnIndex == 4){ // Expression Column
			if(paramValue.isValid())
				return imageOk;
			else
				return imageError;	
		} 
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		ParameterValue paramValue = ((ParameterValue)element);
		switch(columnIndex){
			case 0: // Actors Column
				return paramValue.getName();
			case 1: // Path Column
				String path = "";
				ActorNode node = paramValue.getParent();
				while(node != null){
					path = "/"+node.getName()+path;
					node = node.getParent();
				}
				return path;
			case 2: // Type Column
				return paramValue.getType().toString();
			case 3: // Variables Column
				if(paramValue.getType() == ParameterType.DEPENDENT)
					return paramValue.getInputParameters().toString();
				else 
					return null;
			case 4: // Expression Column
				if(paramValue.getType() == ParameterType.DEPENDENT)
					return paramValue.getExpression();
				else if(paramValue.getType() == ParameterType.STATIC)
					return Integer.toString(paramValue.getValue());
				else if(paramValue.getType() == ParameterType.DYNAMIC)
					return paramValue.getValues().toString();
				return null;
				
		}
		return null;
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		return table.getForeground();
	}


	@Override
	public Color getBackground(Object element, int columnIndex) {
		ParameterValue paramValue = ((ParameterValue)element);
		switch(columnIndex){
			case 0: // Actors Column
			case 1: // Path Column
			case 2: // Type Column
			case 4: // Expression Column
				return table.getBackground();
			case 3: // Variables Column
				if(paramValue.getType() == ParameterType.DEPENDENT)
					return table.getBackground();
				else 
					return new Color(table.getDisplay(), 200,200,200);
				
		}
		return table.getBackground();
	}

}
