package org.ietr.preesm.experiment.model.transformation.test;

import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.SourceInterface;

public class MyTutorialSeccion extends GFPropertySection implements
ITabbedPropertyConstants{ 

//extends AbstractPropertySection {

	// private Text labelText;
//	 private Parameter parameterElement;
//	 private SourceInterface sourceInterfaceElement;
	 
	 private Text nameText;
	 
	    @Override
	    public void createControls(Composite parent,
	        TabbedPropertySheetPage tabbedPropertySheetPage) {
	        super.createControls(parent, tabbedPropertySheetPage);
	 
	        TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
	        Composite composite = factory.createFlatFormComposite(parent);
	        FormData data;
	 
	        nameText = factory.createText(composite, "");
	        data = new FormData();
	        data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
	        data.right = new FormAttachment(100, 0);
	        data.top = new FormAttachment(0, VSPACE);
	        nameText.setLayoutData(data);
	 
	        CLabel valueLabel = factory.createCLabel(composite, "Name:");
	        data = new FormData();
	        data.left = new FormAttachment(0, 0);
	        data.right = new FormAttachment(nameText, -HSPACE);
	        data.top = new FormAttachment(nameText, 0, SWT.CENTER);
	        valueLabel.setLayoutData(data);
	    }
	 
	    @Override
	    public void refresh() {
	        PictogramElement pe = getSelectedPictogramElement();
	        if (pe != null) {
	            Object bo = Graphiti.getLinkService()
	                 .getBusinessObjectForLinkedPictogramElement(pe);
	            // the filter assured, that it is a EClass
	            if (bo == null)
	                return;
	            String name = bo.getClass().getName();
	            		//((EClass) bo).getName();
	            nameText.setText(name == null ? "" : name);
	        }
	    }
}
