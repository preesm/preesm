package org.ietr.preesm.experiment.model.transformation.properties;

import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.OutputPort;
import org.ietr.preesm.experiment.model.pimm.Parameter;


public class Section extends GFPropertySection implements ITabbedPropertyConstants{

	private Text txtName;
	private Text txtExpression;
	//private Button btn;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		Composite composite = factory.createFlatFormComposite(parent);
		FormData data;

		//btn = new Button(composite, SWT.PUSH);
		txtName = factory.createText(composite, "");

		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, VSPACE);
		txtName.setLayoutData(data);
		
		CLabel lblName = factory.createCLabel(composite, "Name:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(txtName, -HSPACE);
		data.top = new FormAttachment(txtName, 0, SWT.CENTER);
		lblName.setLayoutData(data);

		txtExpression = factory.createText(composite, "");
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(50, VSPACE);
		txtExpression.setLayoutData(data);

		CLabel lblExpression = factory.createCLabel(composite, "Expression:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(txtExpression, -HSPACE);
		data.top = new FormAttachment(txtExpression, 50, SWT.CENTER);
		lblExpression.setLayoutData(data);
	
		/*
		btn.setText("Validar");
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(10, 0);
		data.top = new FormAttachment(100, VSPACE);
		btn.setLayoutData(data);
		*/
		
		txtExpression.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				PictogramElement pe = getSelectedPictogramElement();
				if (pe != null) {
					Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
					if (bo == null)
						return;
					if(bo instanceof Parameter){
						((Parameter) bo).getExpression().setExpressionString(txtExpression.getText());
						txtExpression.setText(txtExpression.getText());
					}
					if(bo instanceof OutputPort){
						((OutputPort) bo).getExpression().setExpressionString(txtExpression.getText());
						txtExpression.setText(txtExpression.getText());
					}
					
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	@Override
	public void refresh() {
		PictogramElement pe = getSelectedPictogramElement();
		if (pe != null) {
			Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
			if (bo == null)
				return;
			
			if (bo instanceof Graph){
				String name = ((Graph) bo).getName();
				txtName.setText(name == null ? " " : name);
			}
		
			if (bo instanceof Parameter){
				String name = ((Parameter) bo).getName();
				txtName.setText(name == null ? " " : name);

				String expression = ((Parameter) bo).getExpression().getExpressionString();
				txtExpression.setText(expression == null ? "0" : expression);
				
				Integer i = ((Parameter) bo).getExpression().evaluate();
				System.out.println("Parameter: "+i);
			}
			
			
			if(bo instanceof OutputPort){
				String name = ((OutputPort) bo).getName();
				txtName.setText(name == null ? " " : name);
			
				String expression = ((OutputPort) bo).getExpression().getExpressionString();
				txtExpression.setText(expression == null ? "0" : expression);
				
				Integer i = ((OutputPort) bo).getExpression().evaluate();
				System.out.println("Port: "+i);
			}
		}
	}
}
